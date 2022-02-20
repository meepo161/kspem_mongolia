package ru.avem.kspem.controllers.expControllersSG

import javafx.scene.chart.XYChart
import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.avem.avem4.Avem4Model
import ru.avem.kspem.communication.model.devices.avem.avem7.Avem7Model
import ru.avem.kspem.communication.model.devices.delta.DeltaModel
import ru.avem.kspem.communication.model.devices.pm130.PM130Model
import ru.avem.kspem.communication.model.devices.th01.TH01Model
import ru.avem.kspem.communication.model.devices.trm202.TRM202Model
import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.data.protocolModel
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.utils.sleep
import ru.avem.kspem.view.expViews.expViewsSG.KZViewSG
import ru.avem.stand.utils.autoformat
import tornadofx.runLater
import kotlin.concurrent.thread
import kotlin.math.abs

class KZControllerSG : CustomController() {
    override val model: KZViewSG by inject()
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
    var voltageTRN = 0.0

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
    var amperageOY = 0.0

    @Volatile
    var amperageOV = 0.0

    @Volatile
    var amperageOYSet = 0.0

    @Volatile
    var amperageOYA = 0.0

    @Volatile
    var amperageOYB = 0.0

    @Volatile
    var amperageOYC = 0.0

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
        voltageOYSet = objectModel!!.uNom.toDouble()
        amperageOYSet = objectModel!!.iN.toDouble()
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
                amperageOYA = (value.toDouble() * ktrAmperage)
                model.data.iA.value = amperageOYA.autoformat()
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.I_B_REGISTER) { value ->
                amperageOYB = (value.toDouble() * ktrAmperage)
                model.data.iB.value = amperageOYB.autoformat()
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.I_C_REGISTER) { value ->
                amperageOYC = (value.toDouble() * ktrAmperage)
                model.data.iC.value = amperageOYC.autoformat()
                amperageOY = (amperageOYA + amperageOYB + amperageOYC) / 3
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

            cm.startPoll(CommunicationModel.DeviceID.UZ91, DeltaModel.STATUS_REGISTER) { value ->
                deltaStatus = value.toInt()
                if (!delta.isResponding && isExperimentRunning) cause = "Delta не отвечает"
            }
            cm.startPoll(CommunicationModel.DeviceID.UZ91, DeltaModel.POINT_1_VOLTAGE_REGISTER) { value ->
                voltageDelta = value.toInt() / 10.0
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
                regulateToRPM(rotateSpeedSet, 300, 75, 100L, 200L) //TODO убрать 0.5
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
                voltageRegulationTRN(amperageOYSet, 1000, 1500)
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


        var step = 1.1
        for (i in 0..5) {  // TODO сделать с нуля
            step -= 0.1
            if (isExperimentRunning) {
                appendMessageToLog(
                    LogTag.DEBUG,
                    "Установка напряжения обмотки возбуждения. Ступень: ${step.autoformat()}"
                )
                voltageRegulationTRN(amperageOYSet * step, 1000, 1500)
            }

            var timer = 5.0
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

            model.kzTablePoints[i].uOV.value = model.data.uOV.value
            model.kzTablePoints[i].iOV.value = model.data.iOV.value
            model.kzTablePoints[i].n.value = model.data.n.value
            model.kzTablePoints[i].uAB.value = model.data.uAB.value
            model.kzTablePoints[i].uBC.value = model.data.uBC.value
            model.kzTablePoints[i].uCA.value = model.data.uCA.value
            model.kzTablePoints[i].iA.value = model.data.iA.value
            model.kzTablePoints[i].iB.value = model.data.iB.value
            model.kzTablePoints[i].iC.value = model.data.iC.value
            model.kzTablePoints[i].f.value = model.data.f.value
            model.kzTablePoints[i].p.value = model.data.p.value
            model.kzTablePoints[i].cos.value = model.data.cos.value

            runLater {
                model.series.data.add(XYChart.Data(amperageOV, amperageOY))
            }
        }

        runLater {
            model.series.data.add(XYChart.Data(0.0, 0.0))
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
        protocolModel.kzResult = model.data.result.value
        saveData()
        restoreData()
    }

    private fun voltageRegulationTRN(amperage: Double, coarseSleep: Long, fineSleep: Long) {
        val fast = amperage / 100 * 20
        val slow = amperage / 100 * 10
        val accurate = amperage / 100 * 3

        var timer = System.currentTimeMillis()
        while (abs(amperageOY - amperage) > fast && isExperimentRunning) {
            if (amperageOY < amperage) {
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
        while (abs(amperageOY - amperage) > slow && isExperimentRunning) {
            if (amperageOY < amperage) {
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
        while (abs(amperageOY - amperage) > accurate && isExperimentRunning) {
            if (amperageOY < amperage) {
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
//        protocolModel.kzUOV = model.data.uOV.value
//        protocolModel.kzIOV = model.data.iOV.value
//        protocolModel.kzUAB = model.data.uAB.value
//        protocolModel.kzUBC = model.data.uBC.value
//        protocolModel.kzUCA = model.data.uCA.value
//        protocolModel.kzIA = model.data.iA.value
//        protocolModel.kzIB = model.data.iB.value
//        protocolModel.kzIC = model.data.iC.value
//        protocolModel.kzP1 = model.data.p.value
//        protocolModel.kzN = model.data.n.value
//        protocolModel.kzCos = model.data.cos.value
//        protocolModel.kzF = model.data.f.value

        protocolModel.kzN1   = model.kzTablePoints[0].n.value
        protocolModel.kzCos1 = model.kzTablePoints[0].cos.value
        protocolModel.kzUOV1 = model.kzTablePoints[0].uOV.value
        protocolModel.kzIOV1 = model.kzTablePoints[0].iOV.value
        protocolModel.kzUAB1 = model.kzTablePoints[0].uAB.value
        protocolModel.kzUBC1 = model.kzTablePoints[0].uBC.value
        protocolModel.kzUCA1 = model.kzTablePoints[0].uCA.value
        protocolModel.kzIA1  = model.kzTablePoints[0].iA.value
        protocolModel.kzIB1  = model.kzTablePoints[0].iB.value
        protocolModel.kzIC1  = model.kzTablePoints[0].iC.value
        protocolModel.kzP1   = model.kzTablePoints[0].p.value
        protocolModel.kzF1   = model.kzTablePoints[0].f.value
        protocolModel.kzN2 = model.kzTablePoints[1].n.value
        protocolModel.kzCos2 = model.kzTablePoints[1].cos.value
        protocolModel.kzUOV2 = model.kzTablePoints[1].uOV.value
        protocolModel.kzIOV2 = model.kzTablePoints[1].iOV.value
        protocolModel.kzUAB2 = model.kzTablePoints[1].uAB.value
        protocolModel.kzUBC2 = model.kzTablePoints[1].uBC.value
        protocolModel.kzUCA2 = model.kzTablePoints[1].uCA.value
        protocolModel.kzIA2 = model.kzTablePoints[1].iA.value
        protocolModel.kzIB2 = model.kzTablePoints[1].iB.value
        protocolModel.kzIC2 = model.kzTablePoints[1].iC.value
        protocolModel.kzP2 = model.kzTablePoints[1].p.value
        protocolModel.kzF2 = model.kzTablePoints[1].f.value
        protocolModel.kzN3 = model.kzTablePoints[2].n.value
        protocolModel.kzCos3 = model.kzTablePoints[2].cos.value
        protocolModel.kzUOV3 = model.kzTablePoints[2].uOV.value
        protocolModel.kzIOV3 = model.kzTablePoints[2].iOV.value
        protocolModel.kzUAB3 = model.kzTablePoints[2].uAB.value
        protocolModel.kzUBC3 = model.kzTablePoints[2].uBC.value
        protocolModel.kzUCA3 = model.kzTablePoints[2].uCA.value
        protocolModel.kzIA3 = model.kzTablePoints[2].iA.value
        protocolModel.kzIB3 = model.kzTablePoints[2].iB.value
        protocolModel.kzIC3 = model.kzTablePoints[2].iC.value
        protocolModel.kzP3 = model.kzTablePoints[2].p.value
        protocolModel.kzF3 = model.kzTablePoints[2].f.value
        protocolModel.kzN4 = model.kzTablePoints[3].n.value
        protocolModel.kzCos4 = model.kzTablePoints[3].cos.value
        protocolModel.kzUOV4 = model.kzTablePoints[3].uOV.value
        protocolModel.kzIOV4 = model.kzTablePoints[3].iOV.value
        protocolModel.kzUAB4 = model.kzTablePoints[3].uAB.value
        protocolModel.kzUBC4 = model.kzTablePoints[3].uBC.value
        protocolModel.kzUCA4 = model.kzTablePoints[3].uCA.value
        protocolModel.kzIA4 = model.kzTablePoints[3].iA.value
        protocolModel.kzIB4 = model.kzTablePoints[3].iB.value
        protocolModel.kzIC4 = model.kzTablePoints[3].iC.value
        protocolModel.kzP4 = model.kzTablePoints[3].p.value
        protocolModel.kzF4 = model.kzTablePoints[3].f.value
        protocolModel.kzN5 = model.kzTablePoints[4].n.value
        protocolModel.kzCos5 = model.kzTablePoints[4].cos.value
        protocolModel.kzUOV5 = model.kzTablePoints[4].uOV.value
        protocolModel.kzIOV5 = model.kzTablePoints[4].iOV.value
        protocolModel.kzUAB5 = model.kzTablePoints[4].uAB.value
        protocolModel.kzUBC5 = model.kzTablePoints[4].uBC.value
        protocolModel.kzUCA5 = model.kzTablePoints[4].uCA.value
        protocolModel.kzIA5 = model.kzTablePoints[4].iA.value
        protocolModel.kzIB5 = model.kzTablePoints[4].iB.value
        protocolModel.kzIC5 = model.kzTablePoints[4].iC.value
        protocolModel.kzP5 = model.kzTablePoints[4].p.value
        protocolModel.kzF5 = model.kzTablePoints[4].f.value
        protocolModel.kzN6 = model.kzTablePoints[5].n.value
        protocolModel.kzCos6 = model.kzTablePoints[5].cos.value
        protocolModel.kzUOV6 = model.kzTablePoints[5].uOV.value
        protocolModel.kzIOV6 = model.kzTablePoints[5].iOV.value
        protocolModel.kzUAB6 = model.kzTablePoints[5].uAB.value
        protocolModel.kzUBC6 = model.kzTablePoints[5].uBC.value
        protocolModel.kzUCA6 = model.kzTablePoints[5].uCA.value
        protocolModel.kzIA6 = model.kzTablePoints[5].iA.value
        protocolModel.kzIB6 = model.kzTablePoints[5].iB.value
        protocolModel.kzIC6 = model.kzTablePoints[5].iC.value
        protocolModel.kzP6 = model.kzTablePoints[5].p.value
        protocolModel.kzF6 = model.kzTablePoints[5].f.value

        protocolModel.kzResult = model.data.result.value
    }

    private fun restoreData() {
        // TODO
        model.data.n.value = protocolModel.kzN1
        model.data.cos.value = protocolModel.kzCos1
        model.data.uOV.value = protocolModel.kzUOV1
        model.data.iOV.value = protocolModel.kzIOV1
        model.data.uAB.value = protocolModel.kzUAB1
        model.data.uBC.value = protocolModel.kzUBC1
        model.data.uCA.value = protocolModel.kzUCA1
        model.data.iA.value = protocolModel.kzIA1
        model.data.iB.value = protocolModel.kzIB1
        model.data.iC.value = protocolModel.kzIC1
        model.data.p.value = protocolModel.kzP1
        model.data.f.value = protocolModel.kzF1
//        model.data.cos.value = protocolModel.kzCos
//        model.data.n.value = protocolModel.kzN
//        model.data.uOV.value = protocolModel.kzUOV
//        model.data.iOV.value = protocolModel.kzIOV
//        model.data.uAB.value = protocolModel.kzUAB
//        model.data.uBC.value = protocolModel.kzUBC
//        model.data.uCA.value = protocolModel.kzUCA
//        model.data.iA.value = protocolModel.kzIA
//        model.data.iB.value = protocolModel.kzIB
//        model.data.iC.value = protocolModel.kzIC
//        model.data.p.value = protocolModel.kzP1
//        model.data.result.value = protocolModel.kzResult
    }
}