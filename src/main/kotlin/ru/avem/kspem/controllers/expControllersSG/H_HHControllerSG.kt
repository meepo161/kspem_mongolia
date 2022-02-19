package ru.avem.kspem.controllers.expControllersSG

import javafx.scene.chart.XYChart
import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.avem.avem4.Avem4Model
import ru.avem.kspem.communication.model.devices.avem.avem7.Avem7Model
import ru.avem.kspem.communication.model.devices.pm130.PM130Model
import ru.avem.kspem.communication.model.devices.th01.TH01Model
import ru.avem.kspem.communication.model.devices.trm202.TRM202Model
import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.data.protocolModel
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.utils.sleep
import ru.avem.kspem.view.expViews.expViewsSG.H_HHViewSG
import ru.avem.stand.utils.autoformat
import tornadofx.runLater
import kotlin.concurrent.thread
import kotlin.math.abs

class H_HHControllerSG : CustomController() {
    override val model: H_HHViewSG by inject()
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
    var voltageOY = 0.0

    @Volatile
    var amperageOV = 0.0

    @Volatile
    var voltageLatr = 0.0

    @Volatile
    var voltageTRN = 0.0

    @Volatile
    var latrStatus = 0

    @Volatile
    var voltageOYSet = 0.0

    @Volatile
    var voltageOVSet = 0.0

    @Volatile
    var rotateSpeed = 0.0

    @Volatile
    var rotateSpeedSet = 0.0

    @Volatile
    var ktrDelta = 1.0

    override fun start() {
        model.clearTables()
        super.start()
        // (value-4)*0.625

        fDelta = 2.0
        startDelta = 1 * 10
        voltageTRN = 0.0
        voltageOYSet = objectModel!!.uNom.toDouble()
        rotateSpeedSet = objectModel!!.nAsync.toDouble()
        voltageOVSet = objectModel!!.uOV.toDouble()
        setTime = objectModel!!.timeHH.toDouble()
        runLater {
            model.series.data.clear()
        }


        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация АВЭМ4-01...")
            cm.startPoll(CommunicationModel.DeviceID.PA13, Avem7Model.AMPERAGE) { value ->
                amperageOV = abs(value.toDouble()) * ktrAmperageOV
                model.data.iOV.value = amperageOV.autoformat()
                if (!avemIov.isResponding && isExperimentRunning) cause = "АВЭМ4-01 не отвечает"
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация АВЭМ4-03...")
            cm.startPoll(CommunicationModel.DeviceID.PV23, Avem4Model.RMS) { value ->
                voltageOV = abs(value.toDouble())
                model.data.uOV.value = voltageOV.autoformat()
                if (!avemUov.isResponding && isExperimentRunning) cause = "АВЭМ4-03 не отвечает"
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
            appendMessageToLog(LogTag.MESSAGE, "Инициализация PM135...")
            pm135.checkResponsibility()
            sleep(1000)

            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.U_AB_REGISTER) { value ->
                if (fDelta > 10 && value.toDouble() > 700 && isExperimentRunning) cause = "проверьте подключение ОИ"
                voltageOYAB = (value.toDouble() * ktrVoltage)
                model.data.uAB.value = voltageOYAB.autoformat()
                if (!pm135.isResponding && isExperimentRunning) cause = "PM135 не отвечает"
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.U_BC_REGISTER) { value ->
                if (fDelta > 10 && value.toDouble() > 700 && isExperimentRunning) cause = "проверьте подключение ОИ"
                voltageOYBC = (value.toDouble() * ktrVoltage)
                model.data.uBC.value = voltageOYBC.autoformat()
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.U_CA_REGISTER) { value ->
                if (fDelta > 10 && value.toDouble() > 700 && isExperimentRunning) cause = "проверьте подключение ОИ"
                voltageOYCA = (value.toDouble() * ktrVoltage)
                model.data.uCA.value = voltageOYCA.autoformat()
                voltageOY = (voltageOYAB + voltageOYBC + voltageOYCA) / 3
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.I_A_REGISTER) { value ->
                model.data.iA.value = (value.toDouble() * ktrAmperage).autoformat()
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.I_B_REGISTER) { value ->
                model.data.iB.value = (value.toDouble() * ktrAmperage).autoformat()
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.I_C_REGISTER) { value ->
                model.data.iC.value = (value.toDouble() * ktrAmperage).autoformat()
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.P_REGISTER) { value ->
                model.data.p.value = abs(value.toDouble() * ktrVoltage * ktrAmperage).autoformat()
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.COS_REGISTER) { value ->
                model.data.cos.value = abs(value.toDouble()).autoformat()
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.F_REGISTER) { value ->
                model.data.f.value = value.autoformat()
            }
        }

        if (isExperimentRunning) {
            initButtonPost()
        }

        if (isExperimentRunning) {
            pr102.setTRN(voltageTRN)
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
            pr102.arn(true)
            pr102.ov_oi(true)
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Регулировка до номинальной частоты вращения")
            delta.setObjectParamsRun(2, 20, 2)
            delta.startObject()
        }

        thread(isDaemon = true) {
            if (isExperimentRunning) {
                var timer = 10.0
                if (isExperimentRunning) {
                    while (isExperimentRunning && timer > 0) {
                        timer -= 0.1
                        sleep(100)
                    }
                }
            }
            while (isExperimentRunning) {
                if (rotateSpeed < 100 || rotateSpeed > rotateSpeedSet * 2) {
                    cause = "Проверьте датчик оборотов"
                }
                sleep(1000)
            }
        }

        if (isExperimentRunning) {
            for (i in 1..3) {
                regulateToRPM(rotateSpeedSet, 200, 75, 100L, 200L)
                var timer = 2.0
                if (isExperimentRunning) {
                    while (isExperimentRunning && timer > 0) {
                        timer -= 0.1
                        sleep(100)
                    }
                }
            }
            appendMessageToLog(LogTag.MESSAGE, "Регулировка до номинальной частоты вращения завершена")

        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Подъем напряжения обмотки возбуждения")
            for (i in 1..3) {
                voltageRegulationTRN(voltageOYSet, 1000, 1500)
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

        var timer = 30.0
        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Выдержка 30 секунд")
            while (isExperimentRunning && timer > 0) {
                timer -= 0.1
                if (timer >= 0) {
                    model.data.timeExp.value = timer.autoformat()
                }
                sleep(100)
            }
        }

        saveData()

        var step = 1.4
        for (i in 0..8) {
            step -= 0.1
            if (isExperimentRunning) {
                appendMessageToLog(
                    LogTag.MESSAGE,
                    "Установка напряжения обмотки возбуждения завершена. Ступень: ${step.autoformat()}"
                )
                voltageRegulationTRN(voltageOYSet * step, 1000, 1500)
            }

            timer = 10.0
            if (isExperimentRunning) {
                appendMessageToLog(LogTag.MESSAGE, "Снятие характеристик. Ступень: ${step.autoformat()}")
                while (isExperimentRunning && timer > 0) {
                    timer -= 0.1
                    if (timer >= 0) {
                        model.data.timeExp.value = timer.autoformat()
                    }
                    sleep(100)
                }
            }

            model.h_hhTablePoints[i].uAB.value = model.data.uAB.value
            model.h_hhTablePoints[i].uBC.value = model.data.uBC.value
            model.h_hhTablePoints[i].uCA.value = model.data.uCA.value
            model.h_hhTablePoints[i].iA.value = model.data.iA.value
            model.h_hhTablePoints[i].iB.value = model.data.iB.value
            model.h_hhTablePoints[i].iC.value = model.data.iC.value
            model.h_hhTablePoints[i].uOV.value = model.data.uOV.value
            model.h_hhTablePoints[i].iOV.value = model.data.iOV.value
            model.h_hhTablePoints[i].p.value = model.data.p.value
            runLater {
                model.series.data.add(XYChart.Data(amperageOV, voltageOY))
            }
        }
        runLater {
            model.series.data.add(XYChart.Data(0.0, 0.0))
        }

        try {
            saveData()
        } catch (e: Exception) {
            appendMessageToLog(LogTag.ERROR, "Ошибка сохранения протокола")
        }

        delta.stopObject()

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Выключение ПЧ")
        }
//
//        var timerDelta = 300
//        while (timerDelta > 0 && rotateSpeed > 50) {
//            sleep(100)
//            timerDelta--
//        }

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
        protocolModel.h_hhResult = model.data.result.value
    }

    private fun voltageRegulationTRN(volt: Double, coarseSleep: Long, fineSleep: Long) {
        val fast = volt / 100 * 20
        val slow = volt / 100 * 10
        val accurate = volt / 100 * 3

        var timer = System.currentTimeMillis()
        while (abs(voltageOY - volt) > fast && isExperimentRunning) {
            if (voltageOY < volt) {
                voltageTRN += 0.005
                pr102.setTRN(voltageTRN)
            } else {
                voltageTRN -= 0.005
                pr102.setTRN(voltageTRN)
            }
            if (System.currentTimeMillis() - timer > 90000) cause = "Превышено время регулирования"
            sleep(coarseSleep)
        }

        timer = System.currentTimeMillis()
        while (abs(voltageOY - volt) > slow && isExperimentRunning) {
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

        timer = System.currentTimeMillis()
        while (abs(voltageOY - volt) > accurate && isExperimentRunning) {
            if (voltageOY < volt) {
                voltageTRN += 0.001
                pr102.setTRN(voltageTRN)
            } else {
                voltageTRN -= 0.001
                pr102.setTRN(voltageTRN)
            }
            if (System.currentTimeMillis() - timer > 90000) cause = "Превышено время регулирования"
            sleep(fineSleep)
        }
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

    private fun regulation(
        coarseStep: Int,
        fineStep: Int,
        end: Double,
        coarseLimit: Double,
        fineLimit: Double,
        coarseSleep: Int,
        fineSleep: Int
    ): Int {
        val coarseMinLimit = 1 - coarseLimit
        val coarseMaxLimit = 1 + coarseLimit
        var timeOut = 30
        while (isExperimentRunning && (rotateSpeed < end * coarseMinLimit || rotateSpeed > end * coarseMaxLimit) && timeOut-- > 0) {
            if (rotateSpeed < end * coarseMinLimit) {
                delta.setObjectUMax(coarseStep.let { startDelta += it; startDelta })
            } else if (rotateSpeed > end * coarseMaxLimit) {
                delta.setObjectUMax(coarseStep.let { startDelta -= it; startDelta })
            }
            sleep(coarseSleep.toLong())
            appendMessageToLog(LogTag.MESSAGE, "Выводим напряжение для получения заданного значения грубо")
        }
        timeOut = 30
        while (isExperimentRunning && (rotateSpeed < end /*- fineLimit TODO чтоб больше было*/ || rotateSpeed > end + fineLimit) && timeOut-- > 0) {
            if (rotateSpeed < end /*- fineLimit*/) {
                delta.setObjectUMax(fineStep.let { startDelta += it; startDelta })
            } else if (rotateSpeed > end + fineLimit) {
                delta.setObjectUMax(fineStep.let { startDelta -= it; startDelta })
            }
            sleep(fineSleep.toLong())
            appendMessageToLog(LogTag.MESSAGE, "Выводим напряжение для получения заданного значения точно")
        }
        return startDelta
    }

    private fun stopRegulation() {
        val timer = System.currentTimeMillis()
        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Замедление двигателя")
        }
        while (fDelta > 0) {
            delta.setObjectF(--fDelta)
            sleep(1000)
            if (System.currentTimeMillis() - timer > 90000) cause = "превышено время регулирования"
        }
    }

    override fun stop() {
        cause = "Отменено оператором"
        model.data.result.value = "Прервано"
    }

    private fun saveData() {
//        protocolModel.h_hhuAB1 = model.h_hhTablePoints[0].uAB.value
//        protocolModel.h_hhuBC1 = model.h_hhTablePoints[0].uBC.value
//        protocolModel.h_hhuCA1 = model.h_hhTablePoints[0].uCA.value
//        protocolModel.h_hhiA1  = model.h_hhTablePoints[0].iA.value
//        protocolModel.h_hhiB1  = model.h_hhTablePoints[0].iB.value
//        protocolModel.h_hhiC1  = model.h_hhTablePoints[0].iC.value
//        protocolModel.h_hhuOV1 = model.h_hhTablePoints[0].uOV.value
//        protocolModel.h_hhiOV1 = model.h_hhTablePoints[0].iOV.value
//
//        protocolModel.h_hhuAB2 = model.h_hhTablePoints[1].uAB.value
//        protocolModel.h_hhuBC2 = model.h_hhTablePoints[1].uBC.value
//        protocolModel.h_hhuCA2 = model.h_hhTablePoints[1].uCA.value
//        protocolModel.h_hhiA2  = model.h_hhTablePoints[1].iA.value
//        protocolModel.h_hhiB2  = model.h_hhTablePoints[1].iB.value
//        protocolModel.h_hhiC2  = model.h_hhTablePoints[1].iC.value
//        protocolModel.h_hhuOV2 = model.h_hhTablePoints[1].uOV.value
//        protocolModel.h_hhiOV2 = model.h_hhTablePoints[1].iOV.value
//        protocolModel.h_hhuAB3 = model.h_hhTablePoints[2].uAB.value
//        protocolModel.h_hhuBC3 = model.h_hhTablePoints[2].uBC.value
//        protocolModel.h_hhuCA3 = model.h_hhTablePoints[2].uCA.value
//        protocolModel.h_hhiA3  = model.h_hhTablePoints[2].iA.value
//        protocolModel.h_hhiB3  = model.h_hhTablePoints[2].iB.value
//        protocolModel.h_hhiC3  = model.h_hhTablePoints[2].iC.value
//        protocolModel.h_hhuOV3 = model.h_hhTablePoints[2].uOV.value
//        protocolModel.h_hhiOV3 = model.h_hhTablePoints[2].iOV.value
//        protocolModel.h_hhuAB4 = model.h_hhTablePoints[3].uAB.value
//        protocolModel.h_hhuBC4 = model.h_hhTablePoints[3].uBC.value
//        protocolModel.h_hhuCA4 = model.h_hhTablePoints[3].uCA.value
//        protocolModel.h_hhiA4  = model.h_hhTablePoints[3].iA.value
//        protocolModel.h_hhiB4  = model.h_hhTablePoints[3].iB.value
//        protocolModel.h_hhiC4  = model.h_hhTablePoints[3].iC.value
//        protocolModel.h_hhuOV4 = model.h_hhTablePoints[3].uOV.value
//        protocolModel.h_hhiOV4 = model.h_hhTablePoints[3].iOV.value
//        protocolModel.h_hhuAB5 = model.h_hhTablePoints[4].uAB.value
//        protocolModel.h_hhuBC5 = model.h_hhTablePoints[4].uBC.value
//        protocolModel.h_hhuCA5 = model.h_hhTablePoints[4].uCA.value
//        protocolModel.h_hhiA5  = model.h_hhTablePoints[4].iA.value
//        protocolModel.h_hhiB5  = model.h_hhTablePoints[4].iB.value
//        protocolModel.h_hhiC5  = model.h_hhTablePoints[4].iC.value
//        protocolModel.h_hhuOV5 = model.h_hhTablePoints[4].uOV.value
//        protocolModel.h_hhiOV5 = model.h_hhTablePoints[4].iOV.value
//        protocolModel.h_hhuAB6 = model.h_hhTablePoints[5].uAB.value
//        protocolModel.h_hhuBC6 = model.h_hhTablePoints[5].uBC.value
//        protocolModel.h_hhuCA6 = model.h_hhTablePoints[5].uCA.value
//        protocolModel.h_hhiA6  = model.h_hhTablePoints[5].iA.value
//        protocolModel.h_hhiB6  = model.h_hhTablePoints[5].iB.value
//        protocolModel.h_hhiC6  = model.h_hhTablePoints[5].iC.value
//        protocolModel.h_hhuOV6 = model.h_hhTablePoints[5].uOV.value
//        protocolModel.h_hhiOV6 = model.h_hhTablePoints[5].iOV.value
//        protocolModel.h_hhuAB7 = model.h_hhTablePoints[6].uAB.value
//        protocolModel.h_hhuBC7 = model.h_hhTablePoints[6].uBC.value
//        protocolModel.h_hhuCA7 = model.h_hhTablePoints[6].uCA.value
//        protocolModel.h_hhiA7  = model.h_hhTablePoints[6].iA.value
//        protocolModel.h_hhiB7  = model.h_hhTablePoints[6].iB.value
//        protocolModel.h_hhiC7  = model.h_hhTablePoints[6].iC.value
//        protocolModel.h_hhuOV7 = model.h_hhTablePoints[6].uOV.value
//        protocolModel.h_hhiOV7 = model.h_hhTablePoints[7].iOV.value
//        protocolModel.h_hhuAB8 = model.h_hhTablePoints[7].uAB.value
//        protocolModel.h_hhuBC8 = model.h_hhTablePoints[7].uBC.value
//        protocolModel.h_hhuCA8 = model.h_hhTablePoints[7].uCA.value
//        protocolModel.h_hhiA8  = model.h_hhTablePoints[7].iA.value
//        protocolModel.h_hhiB8  = model.h_hhTablePoints[7].iB.value
//        protocolModel.h_hhiC8  = model.h_hhTablePoints[7].iC.value
//        protocolModel.h_hhuOV8 = model.h_hhTablePoints[7].uOV.value
//        protocolModel.h_hhiOV8 = model.h_hhTablePoints[7].iOV.value
//        protocolModel.h_hhuAB9 = model.h_hhTablePoints[8].uAB.value
//        protocolModel.h_hhuBC9 = model.h_hhTablePoints[8].uBC.value
//        protocolModel.h_hhuCA9 = model.h_hhTablePoints[8].uCA.value
//        protocolModel.h_hhiA9  = model.h_hhTablePoints[8].iA.value
//        protocolModel.h_hhiB9  = model.h_hhTablePoints[8].iB.value
//        protocolModel.h_hhiC9  = model.h_hhTablePoints[8].iC.value
//        protocolModel.h_hhuOV9 = model.h_hhTablePoints[8].uOV.value
//        protocolModel.h_hhiOV9 = model.h_hhTablePoints[8].iOV.value
//
        protocolModel.h_hhuAB1 = "model.h_hhTablePoints[0].uAB.value"
        protocolModel.h_hhuBC1 = "model.h_hhTablePoints[0].uBC.value"
        protocolModel.h_hhuCA1 = "model.h_hhTablePoints[0].uCA.value"
        protocolModel.h_hhiA1  = "model.h_hhTablePoints[0].iA.value"
        protocolModel.h_hhiB1  = "model.h_hhTablePoints[0].iB.value"
        protocolModel.h_hhiC1  = "model.h_hhTablePoints[0].iC.value"
        protocolModel.h_hhuOV1 = "model.h_hhTablePoints[0].uOV.value"
        protocolModel.h_hhiOV1 = "model.h_hhTablePoints[0].iOV.value"

        protocolModel.h_hhuAB2 = "model.h_hhTablePoints[1].uAB.value"
        protocolModel.h_hhuBC2 = "model.h_hhTablePoints[1].uBC.value"
        protocolModel.h_hhuCA2 = "model.h_hhTablePoints[1].uCA.value"
        protocolModel.h_hhiA2  = "model.h_hhTablePoints[1].iA.value"
        protocolModel.h_hhiB2  = "model.h_hhTablePoints[1].iB.value"
        protocolModel.h_hhiC2  = "model.h_hhTablePoints[1].iC.value"
        protocolModel.h_hhuOV2 = "model.h_hhTablePoints[1].uOV.value"
        protocolModel.h_hhiOV2 = "model.h_hhTablePoints[1].iOV.value"
        protocolModel.h_hhuAB3 = "model.h_hhTablePoints[2].uAB.value"
        protocolModel.h_hhuBC3 = "model.h_hhTablePoints[2].uBC.value"
        protocolModel.h_hhuCA3 = "model.h_hhTablePoints[2].uCA.value"
        protocolModel.h_hhiA3  = "model.h_hhTablePoints[2].iA.value"
        protocolModel.h_hhiB3  = "model.h_hhTablePoints[2].iB.value"
        protocolModel.h_hhiC3  = "model.h_hhTablePoints[2].iC.value"
        protocolModel.h_hhuOV3 = "model.h_hhTablePoints[2].uOV.value"
        protocolModel.h_hhiOV3 = "model.h_hhTablePoints[2].iOV.value"
        protocolModel.h_hhuAB4 = "model.h_hhTablePoints[3].uAB.value"
        protocolModel.h_hhuBC4 = "model.h_hhTablePoints[3].uBC.value"
        protocolModel.h_hhuCA4 = "model.h_hhTablePoints[3].uCA.value"
        protocolModel.h_hhiA4  = "model.h_hhTablePoints[3].iA.value"
        protocolModel.h_hhiB4  = "model.h_hhTablePoints[3].iB.value"
        protocolModel.h_hhiC4  = "model.h_hhTablePoints[3].iC.value"
        protocolModel.h_hhuOV4 = "model.h_hhTablePoints[3].uOV.value"
        protocolModel.h_hhiOV4 = "model.h_hhTablePoints[3].iOV.value"
        protocolModel.h_hhuAB5 = "model.h_hhTablePoints[4].uAB.value"
        protocolModel.h_hhuBC5 = "model.h_hhTablePoints[4].uBC.value"
        protocolModel.h_hhuCA5 = "model.h_hhTablePoints[4].uCA.value"
        protocolModel.h_hhiA5  = "model.h_hhTablePoints[4].iA.value"
        protocolModel.h_hhiB5  = "model.h_hhTablePoints[4].iB.value"
        protocolModel.h_hhiC5  = "model.h_hhTablePoints[4].iC.value"
        protocolModel.h_hhuOV5 = "model.h_hhTablePoints[4].uOV.value"
        protocolModel.h_hhiOV5 = "model.h_hhTablePoints[4].iOV.value"
        protocolModel.h_hhuAB6 = "model.h_hhTablePoints[5].uAB.value"
        protocolModel.h_hhuBC6 = "model.h_hhTablePoints[5].uBC.value"
        protocolModel.h_hhuCA6 = "model.h_hhTablePoints[5].uCA.value"
        protocolModel.h_hhiA6  = "model.h_hhTablePoints[5].iA.value"
        protocolModel.h_hhiB6  = "model.h_hhTablePoints[5].iB.value"
        protocolModel.h_hhiC6  = "model.h_hhTablePoints[5].iC.value"
        protocolModel.h_hhuOV6 = "model.h_hhTablePoints[5].uOV.value"
        protocolModel.h_hhiOV6 = "model.h_hhTablePoints[5].iOV.value"
        protocolModel.h_hhuAB7 = "model.h_hhTablePoints[6].uAB.value"
        protocolModel.h_hhuBC7 = "model.h_hhTablePoints[6].uBC.value"
        protocolModel.h_hhuCA7 = "model.h_hhTablePoints[6].uCA.value"
        protocolModel.h_hhiA7  = "model.h_hhTablePoints[6].iA.value"
        protocolModel.h_hhiB7  = "model.h_hhTablePoints[6].iB.value"
        protocolModel.h_hhiC7  = "model.h_hhTablePoints[6].iC.value"
        protocolModel.h_hhuOV7 = "model.h_hhTablePoints[6].uOV.value"
        protocolModel.h_hhiOV7 = "model.h_hhTablePoints[7].iOV.value"
        protocolModel.h_hhuAB8 = "model.h_hhTablePoints[7].uAB.value"
        protocolModel.h_hhuBC8 = "model.h_hhTablePoints[7].uBC.value"
        protocolModel.h_hhuCA8 = "model.h_hhTablePoints[7].uCA.value"
        protocolModel.h_hhiA8  = "model.h_hhTablePoints[7].iA.value"
        protocolModel.h_hhiB8  = "model.h_hhTablePoints[7].iB.value"
        protocolModel.h_hhiC8  = "model.h_hhTablePoints[7].iC.value"
        protocolModel.h_hhuOV8 = "model.h_hhTablePoints[7].uOV.value"
        protocolModel.h_hhiOV8 = "model.h_hhTablePoints[7].iOV.value"
        protocolModel.h_hhuAB9 = "model.h_hhTablePoints[8].uAB.value"
        protocolModel.h_hhuBC9 = "model.h_hhTablePoints[8].uBC.value"
        protocolModel.h_hhuCA9 = "model.h_hhTablePoints[8].uCA.value"
        protocolModel.h_hhiA9  = "model.h_hhTablePoints[8].iA.value"
        protocolModel.h_hhiB9  = "model.h_hhTablePoints[8].iB.value"
        protocolModel.h_hhiC9  = "model.h_hhTablePoints[8].iC.value"
        protocolModel.h_hhuOV9 = "model.h_hhTablePoints[8].uOV.value"
        protocolModel.h_hhiOV9 = "model.h_hhTablePoints[8].iOV.value"
//        protocolModel.h_hhResult = model.data.result.value
    }

    private fun restoreData() {

    }
}