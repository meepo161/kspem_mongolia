package ru.avem.kspem.controllers.expControllersGPT

import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.avem.avem4.Avem4Model
import ru.avem.kspem.communication.model.devices.avem.avem7.Avem7Model
import ru.avem.kspem.communication.model.devices.th01.TH01Model
import ru.avem.kspem.communication.model.devices.trm202.TRM202Model
import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.data.protocolModel
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.utils.sleep
import ru.avem.kspem.view.expViews.expViewsGPT.NViewGPT
import ru.avem.stand.utils.autoformat
import kotlin.math.abs

class NControllerGPT : CustomController() {
    override val model: NViewGPT by inject()
    override val name = model.name
    private var setTime = 0.0

    @Volatile
    var deltaStatus = 0

    @Volatile
    var fDelta = 2.0

    @Volatile
    var startDelta = 1 * 10

    private var ktrVoltage = 1.0
    private var ktrAmperage = 1000 / 5
    private var ktrAmperageOY = 500 / 0.075
    private var ktrAmperageOV = 25 / 0.075

    @Volatile
    var voltageDelta = 0.0

    @Volatile
    var voltageOV = 0.0

    @Volatile
    var voltageOYAB = 0.0

    @Volatile
    var voltageOYBC = 0.0

    @Volatile
    var voltageOYCA = 0.0

    @Volatile
    var amperageOV = 0.0

    @Volatile
    var voltageOY = 0.0

    @Volatile
    var amperageOY = 0.0

    @Volatile
    var voltageOYSet = 0.0

    @Volatile
    var voltageOVSet = 0.0

    @Volatile
    var rotateSpeed = 0.0

    @Volatile
    var rotateSpeedSet = 0.0

    @Volatile
    var voltageTRN = 0.0

    @Volatile
    var ktrDelta = 1.0

    @Volatile
    var nominalRPM = false

    override fun start() {
        model.clearTables()
        super.start()

        nominalRPM = false
        voltageTRN = 0.0
        fDelta = 2.0
        startDelta = 1 * 10
        voltageOYSet = objectModel!!.uNom.toDouble()
        rotateSpeedSet = objectModel!!.nAsync.toDouble()
        voltageOVSet = objectModel!!.uOV.toDouble()
        setTime = objectModel!!.timeHH.toDouble()


        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация АВЭМ4-01-ОВ...")
            cm.startPoll(CommunicationModel.DeviceID.PA13, Avem7Model.AMPERAGE) { value ->
                amperageOV = abs(value.toDouble() * ktrAmperageOV)
                model.data.iOV.value = amperageOV.autoformat()
                if (!avemIov.isResponding && isExperimentRunning) cause = "АВЭМ4-01-ОВ не отвечает"
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация АВЭМ4-03-ОВ...")
            cm.startPoll(CommunicationModel.DeviceID.PV23, Avem4Model.RMS) { value ->
                voltageOV = abs(value.toDouble())
                model.data.uOV.value = voltageOV.autoformat()
                if (!avemUov.isResponding && isExperimentRunning) cause = "АВЭМ4-03-ОВ не отвечает"
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация АВЭМ4-01-ОЯ...")
            cm.startPoll(CommunicationModel.DeviceID.PA15, Avem7Model.AMPERAGE) { value ->
                amperageOY = abs(value.toDouble() * ktrAmperageOY)
                model.data.iOY.value = amperageOY.autoformat()
                if (!avemIoy.isResponding && isExperimentRunning) cause = "АВЭМ4-01-ОЯ не отвечает"
                if (nominalRPM && isExperimentRunning && amperageOY > 1) {
                    cause = "Ток холостого хода превысил 1А"
                }
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация АВЭМ4-03-ОЯ...")
            cm.startPoll(CommunicationModel.DeviceID.PV25, Avem4Model.RMS) { value ->
                voltageOY = abs(value.toDouble())
                model.data.uOY.value = voltageOY.autoformat()
                model.data.p.value = ((voltageOY * amperageOY) / 1000).autoformat()
                if (!avemUoy.isResponding && isExperimentRunning) cause = "АВЭМ4-03-ОЯ не отвечает"
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация ТРМ202...")
            trm202.checkResponsibility()
            if (!trm202.isResponding) {
                cause = "ТРМ202 не отвечает"
            } else {
                cm.startPoll(CommunicationModel.DeviceID.PS81, TRM202Model.T_1) { value ->
                    model.data.tempAmb.value = value.autoformat()
                }
                cm.startPoll(CommunicationModel.DeviceID.PS81, TRM202Model.T_2) { value ->
                    model.data.tempOI.value = value.autoformat()
                }
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация ТХ01...")
            th01.checkResponsibility()
            if (!th01.isResponding) {
                cause = "ТХ01 не отвечает"
            } else {
                cm.startPoll(CommunicationModel.DeviceID.PC71, TH01Model.RPM) { value ->
                    rotateSpeed = value.toDouble()
                    model.data.n.value = rotateSpeed.autoformat()
                }
            }
        }

        if (isExperimentRunning) {
//            initButtonPost()
        }

        if (isExperimentRunning) {
            pr102.km1(true)
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация Delta...")
            var timeDelta = 150
            while (isExperimentRunning && timeDelta-- > 0) {
                sleep(100)
            }

            val timer = System.currentTimeMillis()
            while (isExperimentRunning && !delta.isResponding) {
                delta.checkResponsibility()
                sleep(100)
                if ((System.currentTimeMillis() - timer) > 30000) cause = "Delta не отвечает"
            }
        }

        if (isExperimentRunning) {
            pr102.setTRN(voltageTRN)
            pr102.ov_oi(true)
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Регулировка до номинальной частоты вращения")
            delta.setObjectParamsRun(2, 20, 2)
            delta.startObject()
            var timer = 10.0
            if (isExperimentRunning) {
                while (isExperimentRunning && timer > 0) {
                    timer -= 0.1
                    sleep(100)
                }
            }
        }

        if (isExperimentRunning) {
            regulateToRPM(rotateSpeedSet * 0.5, 100, 50, 100L, 200L)
        }

        var isNeedBreak = false
        if (isExperimentRunning) {
            while (abs(voltageOY - 30) > 10 && isExperimentRunning) {
                if (voltageOY < 30) {
                    voltageTRN += 0.003
                    pr102.setTRN(voltageTRN)
                } else {
                    voltageTRN -= 0.003
                    pr102.setTRN(voltageTRN)
                }
                var timer = 1.0
                if (isExperimentRunning) {
                    while (isExperimentRunning && timer > 0) {
                        timer -= 0.1
                        sleep(100)
                        if (amperageOY > 2) {
                            voltageTRN = 0.0
                            pr102.setTRN(voltageTRN)
                            pr102.ov_oi(false)
                            pr102.ov_oi_obr(true)
                            isNeedBreak = true
                            break
                        }
                    }
                }
                if (isNeedBreak) {
                    break
                }
            }
        }

        nominalRPM = true
        if (isExperimentRunning) {
            for (i in 1..3) {
                regulateToRPM(rotateSpeedSet, 50, 25, 100L, 200L)
                var timer = 2.0
                if (isExperimentRunning) {
                    while (isExperimentRunning && timer > 0) {
                        timer -= 0.1
                        sleep(100)
                    }
                }
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Регулировка до номинальной частоты вращения")
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Подъем напряжения обмотки возбуждения")
            for (i in 1..3) {
                voltageRegulationTRN(voltageOYSet, 300, 600)
                var timer = 2.0
                if (isExperimentRunning) {
                    while (isExperimentRunning && timer > 0) {
                        timer -= 0.1
                        sleep(100)
                    }
                }
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Подъем напряжения обмотки возбуждения завершен")
        }

        var timer = 5.0
        if (isExperimentRunning) {
            while (isExperimentRunning && timer > 0) {
                sleep(100)
                timer -= 0.1
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Регулировка до номинальной частоты вращения * 1.2")
            for (i in 1..3) {
                regulateToRPM(rotateSpeedSet * 1.2, 50, 25, 100L, 200L)
                timer = 2.0
                if (isExperimentRunning) {
                    while (isExperimentRunning && timer > 0) {
                        timer -= 0.1
                        sleep(100)
                    }
                }
            }
            appendMessageToLog(LogTag.MESSAGE, "Регулировка до номинальной частоты вращения * 1.2 завершена")
        }

        timer = 120.0
        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Выдержка 120 секунд")
            while (isExperimentRunning && timer > 0) {
                timer -= 0.1
                if (timer >= 0) {
                    model.data.timeExp.value = timer.autoformat()
                }
                sleep(100)
            }
        }

        saveData()

        nominalRPM = false
        pr102.setTRN(0.0)
        delta.stopObject()

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Выключение ПЧ")
        }
        finalizeExperiment()

        when (cause) {
            "" -> {
                model.data.result.value = "Успешно"
                appendMessageToLog(LogTag.MESSAGE, "Испытание завершено успешно")
            }
            else -> {
                model.data.result.value = "Прервано"
                appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
            }
        }
        protocolModel.gptNResult = model.data.result.value
        restoreData()
    }

    private fun regulateToRPM(
        speed: Double,
        coarseLimit: Int,
        fineLimit: Int,
        coarseSleep: Long,
        fineSleep: Long
    ) {
        while (isExperimentRunning && (rotateSpeed > speed + coarseLimit || rotateSpeed < speed)) {
            if (rotateSpeed < speed + coarseLimit) {
                fDelta += 0.1
                delta.setObjectF(fDelta)
                sleep(coarseSleep)
            } else if (rotateSpeed > speed) {
                fDelta -= 0.1
                delta.setObjectF(fDelta)
                sleep(coarseSleep)
            }
        }

        while (isExperimentRunning && (rotateSpeed > speed + fineLimit || rotateSpeed < speed)) {
            if (rotateSpeed < speed + fineLimit) {
                fDelta += 0.05
                delta.setObjectF(fDelta)
                sleep(fineSleep)
            } else if (rotateSpeed > speed) {
                fDelta -= 0.05
                delta.setObjectF(fDelta)
                sleep(fineSleep)
            }
        }
    }

    private fun voltageRegulation(volt: Double, coarse: Int = 10, fine: Int = 5, accurate: Int = 2) {
        var timer = 0L
        var speedPerc = 100f
        var timePulsePerc = 20f
        val up = 220f
        val down = 1f
        var direction: Float
        timer = System.currentTimeMillis()
//        appendMessageToLog(LogTag.DEBUG, "Быстрая регулировка")
        while (abs(voltageOV - volt) > fine && isExperimentRunning) {
            if (voltageOV < volt) {
                direction = up
                speedPerc = 100f
            } else {
                direction = down
                speedPerc = 100f
            }
            if (System.currentTimeMillis() - timer > 180000) cause = "Превышено время регулирования"
            latr.startUpLATRPulse(direction, false, speedPerc)
        }
        latr.stopLATR()
//        timer = System.currentTimeMillis()
//        if (isExperimentRunning) {
//            appendMessageToLog(LogTag.DEBUG, "Грубая регулировка")
//        }
//        while (abs(voltageOY - volt) > coarse && isExperimentRunning) {
//            if (voltageOY < volt) {
//                direction = up
//                timePulsePerc = 85f
//            } else {
//                direction = down
//                timePulsePerc = 100f
//            }
//            if (System.currentTimeMillis() - timer > 60000) cause = "Превышено время регулирования"
//            latr.startUpLATRPulse(direction, false, timePulsePerc)
//        }
//        latr.stopLATR()
        timer = System.currentTimeMillis()
//        if (isExperimentRunning) {
//            appendMessageToLog(LogTag.DEBUG, "Быстрая регулировка")
//        }
        while (abs(voltageOV - volt) > accurate && isExperimentRunning) {
            if (voltageOV < volt) {
                direction = up
                timePulsePerc = 70f
            } else {
                direction = down
                timePulsePerc = 100f
            }
            if (System.currentTimeMillis() - timer > 180000) cause = "Превышено время регулирования"
            latr.startUpLATRPulse(direction, false, timePulsePerc)
            sleep(500)
            latr.stopLATR()
            sleep(500)
        }
        latr.stopLATR()
    }

    private fun voltageRegulationForSpeed(speed: Double, coarse: Int = 10, fine: Int = 5, accurate: Int = 2) {
        var timer = 0L
        var speedPerc = 100f
        var timePulsePerc = 20f
        val up = 220f
        val down = 1f
        var direction: Float
        timer = System.currentTimeMillis()
//        appendMessageToLog(LogTag.DEBUG, "Быстрая регулировка")
        while (abs(rotateSpeed - speed) > coarse && isExperimentRunning) {
            if (rotateSpeed > speed) {
                direction = up
                speedPerc = 100f
            } else {
                direction = down
                speedPerc = 100f
            }
            if (System.currentTimeMillis() - timer > 180000) cause = "Превышено время регулирования"
            latr.startUpLATRPulse(direction, false, speedPerc)
        }
        latr.stopLATR()
        var timerWait = 5.0
        if (isExperimentRunning) {
            while (isExperimentRunning && timerWait > 0) {
                timerWait -= 0.1
                sleep(100)
            }
        }
        while (abs(rotateSpeed - speed) > fine && isExperimentRunning) {
            if (rotateSpeed > speed) {
                direction = up
                speedPerc = 100f
            } else {
                direction = down
                speedPerc = 100f
            }
            if (System.currentTimeMillis() - timer > 180000) cause = "Превышено время регулирования"
            latr.startUpLATRPulse(direction, false, speedPerc)
        }
        latr.stopLATR()
        timerWait = 5.0
        if (isExperimentRunning) {
            while (isExperimentRunning && timerWait > 0) {
                timerWait -= 0.1
                sleep(100)
            }
        }
//        timer = System.currentTimeMillis()
//        if (isExperimentRunning) {
//            appendMessageToLog(LogTag.DEBUG, "Грубая регулировка")
//        }
//        while (abs(voltageOY - volt) > coarse && isExperimentRunning) {
//            if (voltageOY < volt) {
//                direction = up
//                timePulsePerc = 85f
//            } else {
//                direction = down
//                timePulsePerc = 100f
//            }
//            if (System.currentTimeMillis() - timer > 60000) cause = "Превышено время регулирования"
//            latr.startUpLATRPulse(direction, false, timePulsePerc)
//        }
//        latr.stopLATR()
        timer = System.currentTimeMillis()
//        if (isExperimentRunning) {
//            appendMessageToLog(LogTag.DEBUG, "Быстрая регулировка")
//        }
        while (abs(rotateSpeed - speed) > accurate && isExperimentRunning) {
            if (rotateSpeed > speed) {
                direction = up
                timePulsePerc = 70f
            } else {
                direction = down
                timePulsePerc = 100f
            }
            if (System.currentTimeMillis() - timer > 180000) cause = "Превышено время регулирования"
            latr.startUpLATRPulse(direction, false, timePulsePerc)
            sleep(500)
            latr.stopLATR()
            sleep(500)
        }
        latr.stopLATR()
    }

    private fun voltageRegulationTRN(
        volt: Double, coarseSleep: Long, fineSleep: Long, slow: Double = 100.0, fast: Double = 20.0,
        accurate: Double = 2.0
    ) {

        var timer = System.currentTimeMillis()
        while (abs(voltageOY - volt) > slow && isExperimentRunning) {
            if (voltageOY < volt) {
                voltageTRN += 0.01
                pr102.setTRN(voltageTRN)
            } else {
                voltageTRN -= 0.01
                pr102.setTRN(voltageTRN)
            }
            if (System.currentTimeMillis() - timer > 90000) cause = "Превышено время регулирования"
            sleep(coarseSleep)
        }

        timer = System.currentTimeMillis()
        while (abs(voltageOY - volt) > fast && isExperimentRunning) {
            if (voltageOY < volt) {
                voltageTRN += 0.005
                pr102.setTRN(voltageTRN)
            } else {
                voltageTRN -= 0.005
                pr102.setTRN(voltageTRN)
            }
            if (System.currentTimeMillis() - timer > 90000) cause = "Превышено время регулирования"
            sleep(fineSleep)
        }

        timer = System.currentTimeMillis()
        while (abs(voltageOY - volt) > accurate && isExperimentRunning) {
            if (voltageOY < volt) {
                voltageTRN += 0.003
                pr102.setTRN(voltageTRN)
            } else {
                voltageTRN -= 0.003
                pr102.setTRN(voltageTRN)
            }
            if (System.currentTimeMillis() - timer > 90000) cause = "Превышено время регулирования"
            sleep(fineSleep)
        }
    }

    private fun voltageRegulationTRNForSpeed(speed: Double, coarseSleep: Long, fineSleep: Long) {
        val slow = 500.0
        val fast = 250.0
        val accurate = 50.0

        var timer = System.currentTimeMillis()
        while (abs(rotateSpeed - speed) > slow && isExperimentRunning) {
            if (rotateSpeed > speed) {
                voltageTRN += 0.01
                pr102.setTRN(voltageTRN)
            } else {
                voltageTRN -= 0.01
                pr102.setTRN(voltageTRN)
            }
            if (System.currentTimeMillis() - timer > 90000) cause = "Превышено время регулирования"
            sleep(coarseSleep)
        }
        var timerWait = 10.0
        if (isExperimentRunning) {
            while (isExperimentRunning && timerWait > 0) {
                timerWait -= 0.1
                sleep(100)
            }
        }

        timer = System.currentTimeMillis()
        while (abs(rotateSpeed - speed) > fast && isExperimentRunning) {
            if (rotateSpeed > speed) {
                voltageTRN += 0.005
                pr102.setTRN(voltageTRN)
            } else {
                voltageTRN -= 0.005
                pr102.setTRN(voltageTRN)
            }
            if (System.currentTimeMillis() - timer > 90000) cause = "Превышено время регулирования"
            sleep(fineSleep)
        }

        timerWait = 10.0
        if (isExperimentRunning) {
            while (isExperimentRunning && timerWait > 0) {
                timerWait -= 0.1
                sleep(100)
            }
        }

        timer = System.currentTimeMillis()
        while (abs(rotateSpeed - speed) > accurate && isExperimentRunning) {
            if (rotateSpeed > speed) {
                voltageTRN += 0.003
                pr102.setTRN(voltageTRN)
            } else {
                voltageTRN -= 0.003
                pr102.setTRN(voltageTRN)
            }
            if (System.currentTimeMillis() - timer > 90000) cause = "Превышено время регулирования"
            sleep(fineSleep)
        }

    }

    override fun stop() {
        cause = "Отменено оператором"
        model.data.result.value = "Прервано"
    }

    private fun saveData() {
        protocolModel.gptNN = model.data.n.value
        protocolModel.gptNP1 = model.data.p.value
        protocolModel.gptNTOI = model.data.tempOI.value
        protocolModel.gptNTAmb = model.data.tempAmb.value
        protocolModel.gptNiOV = model.data.iOV.value
        protocolModel.gptNuOV = model.data.uOV.value
        protocolModel.gptNuN = model.data.uOY.value
        protocolModel.gptNiN = model.data.iOY.value
        protocolModel.gptNResult = model.data.result.value
    }

    private fun restoreData() {
        model.data.n.value =  protocolModel.gptNN
        model.data.p.value =  protocolModel.gptNP1
        model.data.tempOI.value =  protocolModel.gptNTOI
        model.data.tempAmb.value =  protocolModel.gptNTAmb
        model.data.iOV.value =  protocolModel.gptNiOV
        model.data.uOV.value =  protocolModel.gptNuOV
        model.data.uOY.value =  protocolModel.gptNuN
        model.data.iOY.value =  protocolModel.gptNiN
        model.data.result.value =  protocolModel.gptNResult
    }
}