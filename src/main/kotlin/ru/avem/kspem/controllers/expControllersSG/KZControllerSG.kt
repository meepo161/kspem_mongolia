package ru.avem.kspem.controllers.expControllersSG

import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.avem.latr.LatrModel
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
    var amperageOYSet = 0.0

    @Volatile
    var amperageOYA = 0.0

    @Volatile
    var amperageOYB = 0.0

    @Volatile
    var amperageOYC = 0.0

    @Volatile
    var voltageLatr = 0.0

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
                if (amperageOYA > amperageOYSet) {
                    cause = "Ток фазы A дошел до номинальных значений"
                }
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.I_B_REGISTER) { value ->
                amperageOYB = (value.toDouble() * ktrAmperage)
                model.data.iB.value = amperageOYB.autoformat()
                if (amperageOYB > amperageOYSet) {
                    cause = "Ток фазы B дошел до номинальных значений"
                }
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.I_C_REGISTER) { value ->
                amperageOYC = (value.toDouble() * ktrAmperage)
                model.data.iC.value = amperageOYC.autoformat()
                if (amperageOYC > amperageOYSet) {
                    cause = "Ток фазы C дошел до номинальных значений"
                }
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
            appendMessageToLog(LogTag.MESSAGE, "Инициализация АРН...")
            latr.resetLATR()
            cm.startPoll(CommunicationModel.DeviceID.GV240, LatrModel.U_RMS_REGISTER) { value ->
                voltageLatr = value.toDouble()
                if (!latr.isResponding && isExperimentRunning) cause = "АРН не отвечает"
            }
            cm.startPoll(CommunicationModel.DeviceID.GV240, LatrModel.ENDS_STATUS_REGISTER) { value ->
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

            cm.startPoll(CommunicationModel.DeviceID.UZ91, DeltaModel.STATUS_REGISTER) { value ->
                deltaStatus = value.toInt()
                if (!delta.isResponding && isExperimentRunning) cause = "Delta не отвечает"
            }
            cm.startPoll(CommunicationModel.DeviceID.UZ91, DeltaModel.POINT_1_VOLTAGE_REGISTER) { value ->
                voltageDelta = value.toInt() / 10.0
            }
        }

        if (isExperimentRunning) {
            if (voltageLatr < 5) {
                pr102.arn(true)
                pr102.ov_oi(true)
            } else {
                cause = "АРН не вышел в нулевое положение"
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Регулировка до номинальной частоты вращения")
            delta.setObjectParamsRun(2, 20, 2)
            delta.startObject()
        }

        if (isExperimentRunning) {
            for (i in 1..3) {
                regulateToRPM(rotateSpeedSet, 50, 25, 100L, 200L)
                var timer = 2.0
                if (isExperimentRunning) {
                    while (isExperimentRunning && timer > 0) {
                        sleep(100)
                        timer -= 0.1
                    }
                }
            }
            appendMessageToLog(LogTag.MESSAGE, "Регулировка до номинальной частоты вращения завершена")

        }

        if (isExperimentRunning) {
            if (objectModel!!.uVIU.toDoubleOrNull() != null) {
                appendMessageToLog(LogTag.DEBUG, "Подъем напряжения обмотки возбуждения")
                for (i in 1..3) {
                    voltageRegulation(voltageOYSet, 100, 50, 10)
                    var timer = 2.0
                    if (isExperimentRunning) {
                        while (isExperimentRunning && timer > 0) {
                            sleep(100)
                            timer -= 0.1
                        }
                    }
                }
            } else cause = "ошибка задания напряжения"
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Подъем напряжения обмотки возбуждения завершен")
        }

        var timer = 5.0
        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Выдержка 5 секунд")
            while (isExperimentRunning && timer > 0) {
                timer -= 0.1
                if (timer >= 0) {
                    model.data.timeExp.value = timer.autoformat()
                }
                sleep(100)
            }
        }

        protocolModel.kzUAB = model.data.uAB.value
        protocolModel.kzUBC = model.data.uBC.value
        protocolModel.kzUCA = model.data.uCA.value
        protocolModel.kzIA = model.data.iA.value
        protocolModel.kzIB = model.data.iB.value
        protocolModel.kzIC = model.data.iC.value
        protocolModel.kzP1 = model.data.p.value

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
        restoreData()
    }

    private fun regulateToRPM(
        speed: Double,
        coarseLimit: Int,
        fineLimit: Int,
        coarseSleep: Long,
        fineSleep: Long
    ) {
//        while (isExperimentRunning && (rotateSpeed > speed + coarseLimit || rotateSpeed < speed)) {
//            if (rotateSpeed < speed + coarseLimit) {
//                fDelta += 0.1
//                delta.setObjectF(fDelta)
//                sleep(coarseSleep)
//            } else if (rotateSpeed > speed) {
//                fDelta -= 0.1
//                delta.setObjectF(fDelta)
//                sleep(coarseSleep)
//            }
//        }

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
        var speedPerc = 35f
        var timePulsePerc = 20f
        val up = 220f
        val down = 1f
        var direction: Float
//        timer = System.currentTimeMillis()
//        appendMessageToLog(LogTag.DEBUG, "Быстрая регулировка")
//        while (abs(voltageOY - volt) > fine && isExperimentRunning) {
//            if (voltageOY < volt) {
//                direction = up
//                speedPerc = 100f
//            } else {
//                direction = down
//                speedPerc = 100f
//            }
//            if (System.currentTimeMillis() - timer > 90000) cause = "Превышено время регулирования"
//            latr.startUpLATRUp(direction, false, speedPerc)
//        }
//        latr.stopLATR()
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
        while (abs(voltageOY - volt) > fine && isExperimentRunning) {
            if (voltageOY < volt) {
                direction = up
                timePulsePerc = 70f
            } else {
                direction = down
                timePulsePerc = 100f
            }
            if (System.currentTimeMillis() - timer > 60000) cause = "Превышено время регулирования"
            latr.startUpLATRPulse(direction, false, timePulsePerc)
            sleep(1000)
            latr.stopLATR()
            sleep(1000)
        }
        timer = System.currentTimeMillis()
//        if (isExperimentRunning) {
//            appendMessageToLog(LogTag.DEBUG, "Точная регулировка")
//        }
        while (abs(voltageOY - volt) > accurate && isExperimentRunning) {
            if (voltageOY < volt) {
                direction = up
                timePulsePerc = 70f
            } else {
                direction = down
                timePulsePerc = 100f
            }
            if (System.currentTimeMillis() - timer > 60000) cause = "Превышено время регулирования"
            latr.startUpLATRPulse(direction, false, timePulsePerc)
            sleep(750)
            latr.stopLATR()
            sleep(3000)
        }
        latr.stopLATR()
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
        protocolModel.kzUAB = model.data.uAB.value
        protocolModel.kzUBC = model.data.uBC.value
        protocolModel.kzUCA = model.data.uCA.value
        protocolModel.kzIA = model.data.iA.value
        protocolModel.kzIB = model.data.iB.value
        protocolModel.kzIC = model.data.iC.value
        protocolModel.kzP1 = model.data.p.value
        protocolModel.kzResult = model.data.result.value
    }

    private fun restoreData() {
        model.data.uAB.value = protocolModel.kzUAB
        model.data.uBC.value = protocolModel.kzUBC
        model.data.uCA.value = protocolModel.kzUCA
        model.data.iA.value = protocolModel.kzIA
        model.data.iB.value = protocolModel.kzIB
        model.data.iC.value = protocolModel.kzIC
        model.data.p.value = protocolModel.kzP1
        model.data.result.value = protocolModel.kzResult
    }
}