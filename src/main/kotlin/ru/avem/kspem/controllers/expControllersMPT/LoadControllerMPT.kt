package ru.avem.kspem.controllers.expControllersMPT

import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.avem.avem4.Avem4Model
import ru.avem.kspem.communication.model.devices.avem.avem7.Avem7Model
import ru.avem.kspem.communication.model.devices.delta.DeltaModel
import ru.avem.kspem.communication.model.devices.owen.pr.OwenPrModel
import ru.avem.kspem.communication.model.devices.th01.TH01Model
import ru.avem.kspem.communication.model.devices.trm202.TRM202Model
import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.data.protocolModel
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.utils.sleep
import ru.avem.kspem.view.expViews.expViewsMPT.LoadViewMPT
import ru.avem.stand.utils.autoformat
import kotlin.concurrent.thread
import kotlin.math.abs

class LoadControllerMPT : CustomController() {
    override val model: LoadViewMPT by inject()
    override val name = model.name
    private var setTime = 0.0
    private var ktrVoltage = 1.0

    private var ktrAmperageOY = 500 / 0.075
    private var ktrAmperageOV = 25 / 0.075

    @Volatile
    var voltageOV = 0.0

    @Volatile
    var amperageOV = 0.0

    @Volatile
    var voltageOY = 0.0

    @Volatile
    var amperageOY = 0.0

    @Volatile
    var voltageDelta = 0.0

    @Volatile
    var voltageOYSet = 0.0

    @Volatile
    var voltageOVSet = 0.0

    @Volatile
    var rotateSpeed = 0.0

    @Volatile
    var rotateSpeedSet = 0.0

    @Volatile
    var voltageTVN = 0.0

    @Volatile
    var deltaStatus = 0

    @Volatile
    var fDelta = 0.0

    @Volatile
    var amperageSet = 0.0

    @Volatile
    var voltageTRN = 0.0

    @Volatile
    var rotateUNM = 0.0

    override fun start() {
        model.clearTables()
        super.start()
        // (value-4)*0.625

        voltageTVN = 0.0
        voltageTRN = 0.0
        voltageOYSet = objectModel!!.uNom.toDouble()
        rotateSpeedSet = objectModel!!.nAsync.toDouble()
        voltageOVSet = objectModel!!.uOV.toDouble()
        amperageSet = objectModel!!.iN.toDouble()
        setTime = objectModel!!.timeHH.toDouble()


        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация АВЭМ4-01-ОВ...")
            cm.startPoll(CommunicationModel.DeviceID.DD2_1, OwenPrModel.ROTATE_UNM) { value ->
                rotateUNM = value.toDouble()
            }
        }
        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация АВЭМ4-01-ОВ...")
            cm.startPoll(CommunicationModel.DeviceID.PA13, Avem7Model.AMPERAGE) { value ->
                amperageOV = abs(value.toDouble() * ktrAmperageOV)
                model.data.iOV.value = amperageOV.autoformat()
                if (!avemIov.isResponding && isExperimentRunning) cause = "АВЭМ4-01 не отвечает"
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация АВЭМ4-03-ОВ...")
            cm.startPoll(CommunicationModel.DeviceID.PV23, Avem4Model.RMS) { value ->
                voltageOV = abs(value.toDouble())
                model.data.uOV.value = voltageOV.autoformat()
                if (!avemUov.isResponding && isExperimentRunning) cause = "АВЭМ4-03 не отвечает"
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация АВЭМ4-01-ОЯ...")
            cm.startPoll(CommunicationModel.DeviceID.PA15, Avem7Model.AMPERAGE) { value ->
                amperageOY = abs(value.toDouble() * ktrAmperageOY)
                model.data.iOY.value = amperageOY.autoformat()
                if (!avemIoy.isResponding && isExperimentRunning) cause = "АВЭМ4-01 не отвечает"
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация АВЭМ4-03-ОЯ...")
            cm.startPoll(CommunicationModel.DeviceID.PV25, Avem4Model.RMS) { value ->
                voltageOY = abs(value.toDouble())
                model.data.uOY.value = voltageOY.autoformat()
                model.data.p.value = ((voltageOY * amperageOY) / 1000).autoformat()
                if (!avemUoy.isResponding && isExperimentRunning) cause = "АВЭМ4-03 не отвечает"
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
            pr102.vent(true)
        }

        sleep(2000)


        thread(isDaemon = true) {
            var noRotate = 0
            while (isExperimentRunning) {
                val rotateUNMLast = rotateUNM
                sleep(1000)
                if (abs(rotateUNMLast - rotateUNM) < 10) {
                    noRotate++
                    if (noRotate > 5) {
                        cause = "Вентилятор остановился"
                    }
                } else {
                    noRotate = 0
                }
            }
        }

        while (isExperimentRunning) {
            sleep(100)
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
            cm.startPoll(CommunicationModel.DeviceID.UZ91, DeltaModel.CURRENT_FREQ) { value ->
                println("CURRENT_FREQ = " + value.toDouble() / 100 + " Гц")
            }
            cm.startPoll(CommunicationModel.DeviceID.UZ91, DeltaModel.CURRENT_AMPER) { value ->
                println("CURRENT_AMPER = " + value.toDouble() / 100 + " А")
            }
            cm.startPoll(CommunicationModel.DeviceID.UZ91, DeltaModel.CURRENT_VOLT) { value ->
                println("CURRENT_VOLT = " + value.toDouble() / 10 + " В")
            }
        }

        if (isExperimentRunning) {
            pr102.arn(true)
            pr102.ov_oi_obr(true)
            pr102.tvn(true)
            pr102.setTVN(voltageTVN)
            pr102.setTRN(voltageTRN)
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Регулировка до номинальной частоты вращения")
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Регулировка до номинальной частоты вращения завершена")
        }
        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Подъем напряжения обмотки возбуждения и обмотки якоря.")
            voltageRegulationTRN(voltageOVSet, 300, 600)
            voltageRegulationTVN(voltageOYSet, 300, 600)
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Разгон НМ...")

//            fDelta = if ((rotateSpeed / (1500 / 50)) / 2 < 50) {
//                (rotateSpeed / (1500 / 50)) / 2 //TODO проверка шкивов
//            } else {
//                50.0
//            }
            fDelta = 38.3

            var u = 2
            val maxU = 380 / 50 * fDelta

            delta.setObjectParamsRun(fDelta, u, fDelta)
            delta.startObject()

            var timer = 10.0
            while (isExperimentRunning && timer > 0) {
                timer -= 0.1
                sleep(100)
            }

            while (isExperimentRunning && u < maxU) {
                u++
                delta.setObjectUMax(u)
                Thread.sleep(50)
            }
            delta.setObjectUMax(maxU)

            appendMessageToLog(LogTag.MESSAGE, "Подключение нагрузки")

            pr102.unm(true)
            pr102.vent(true)

            thread(isDaemon = true) {
                while (isExperimentRunning) {
                    voltageRegulationTRN(voltageOVSet, 300, 600)
                }
            }
            thread(isDaemon = true) {
                while (isExperimentRunning) {
                    voltageRegulationTVN(voltageOYSet, 300, 600)
                }
            }
            regulationTo(amperageSet)

            appendMessageToLog(LogTag.MESSAGE, "Регулировка до номинальной нагрузки завершена")
        }

        var timer = 120.0
        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Выдержка 120 секунд")
            while (isExperimentRunning && timer > 0) {
                timer -= 0.1
                if (timer >= 0) {
                    model.data.timeExp.value = timer.autoformat()
                }
                sleep(100)
            }
            model.data.timeExp.value = "0.0"
        }
        saveData()

        isExperimentRunning = false
        sleep(100)
        pr102.setTVN(0.0)
        pr102.setTRN(0.0)
        sleep(2000)

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
        protocolModel.nResult = model.data.result.value
        restoreData()
    }

    private fun regulationTo(
        amperageSet: Double,
        coarseLimit: Double = 10.0,
        fineLimit: Double = 2.0,
        coarseSleep: Long = 500,
        fineSleep: Long = 750
    ) {
        while (abs(amperageOY - amperageSet) > coarseLimit && isExperimentRunning) {
            if (amperageOY < amperageSet) {
                fDelta -= 0.1
            } else {
                fDelta += 0.1
            }
            delta.setObjectF(fDelta)
            sleep(coarseSleep)
        }
        while (abs(amperageOY - amperageSet) > fineLimit && isExperimentRunning) {
            if (amperageOY < amperageSet) {
                fDelta -= 0.05
            } else {
                fDelta += 0.05
            }
            delta.setObjectF(fDelta)
            sleep(fineSleep)
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

    private fun voltageRegulationTVN(volt: Double, coarseSleep: Long, fineSleep: Long) {
        val slow = 100.0
        val fast = 20.0
        val accurate = 2.0

        var timer = System.currentTimeMillis()
        while (abs(voltageOY - volt) > slow && isExperimentRunning) {
            if (voltageOY < volt) {
                voltageTVN += 0.01
                pr102.setTVN(voltageTVN)
            } else {
                voltageTVN -= 0.01
                pr102.setTVN(voltageTVN)
            }
            if (System.currentTimeMillis() - timer > 90000) cause = "Превышено время регулирования"
            sleep(coarseSleep)
        }

        timer = System.currentTimeMillis()
        while (abs(voltageOY - volt) > fast && isExperimentRunning) {
            if (voltageOY < volt) {
                voltageTVN += 0.005
                pr102.setTVN(voltageTVN)
            } else {
                voltageTVN -= 0.005
                pr102.setTVN(voltageTVN)
            }
            if (System.currentTimeMillis() - timer > 90000) cause = "Превышено время регулирования"
            sleep(fineSleep)
        }

        timer = System.currentTimeMillis()
        while (abs(voltageOY - volt) > accurate && isExperimentRunning) {
            if (voltageOY < volt) {
                voltageTVN += 0.003
                pr102.setTVN(voltageTVN)
            } else {
                voltageTVN -= 0.003
                pr102.setTVN(voltageTVN)
            }
            if (System.currentTimeMillis() - timer > 90000) cause = "Превышено время регулирования"
            sleep(fineSleep)
        }
    }

    private fun voltageRegulationTRN(volt: Double, coarseSleep: Long, fineSleep: Long) {
        val slow = 100.0
        val fast = 20.0
        val accurate = 2.0

        var timer = System.currentTimeMillis()
        while (abs(voltageOV - volt) > slow && isExperimentRunning) {
            if (voltageOV < volt) {
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
        while (abs(voltageOV - volt) > fast && isExperimentRunning) {
            if (voltageOV < volt) {
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
        while (abs(voltageOV - volt) > accurate && isExperimentRunning) {
            if (voltageOV < volt) {
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
        protocolModel.nSpeed = model.data.n.value
        protocolModel.nResult = model.data.result.value
    }

    private fun restoreData() {
        model.data.n.value = protocolModel.nSpeed
    }
}