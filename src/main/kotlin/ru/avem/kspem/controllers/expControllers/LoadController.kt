package ru.avem.kspem.controllers.expControllers

import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.delta.DeltaModel
import ru.avem.kspem.communication.model.devices.pm130.PM130Model
import ru.avem.kspem.communication.model.devices.th01.TH01Model
import ru.avem.kspem.communication.model.devices.trm202.TRM202Model
import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.data.protocolModel
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.utils.sleep
import ru.avem.kspem.view.expViews.LoadView
import ru.avem.stand.utils.autoformat
import kotlin.math.abs


class LoadController : CustomController() {
    override val model: LoadView by inject()
    override val name = model.name
    var deltaStatus = 0
    private var setTime = 0.0
    private var ktrVoltage = 1.0
    private var ktrAmperage = 1.0
    var fDelta = 1
    var voltageDelta = 0.0

    @Volatile
    var ktrDelta = 1.0

    override fun start() {
        model.clearTables()
        super.start()

        ktrDelta = 1.0
        fDelta = 1
        setTime = objectModel!!.timeRUNNING.toDouble()

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
                    model.data.n.value = value.autoformat()
                }
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация PM135...")
            pm135.checkResponsibility()
            sleep(1000)

            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.U_AB_REGISTER) { value ->
                if ( fDelta > 10 && value.toDouble() > 710 && isExperimentRunning) cause = "проверьте подключение ОИ"
                model.data.uab.value = (value.toDouble() * ktrVoltage).autoformat()
                if (!pm135.isResponding && isExperimentRunning) cause = "PM135 не отвечает"
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.U_BC_REGISTER) { value ->
                if ( fDelta > 10 && value.toDouble() > 710  && isExperimentRunning) cause = "проверьте подключение ОИ"
                model.data.ubc.value = (value.toDouble() * ktrVoltage).autoformat()
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.U_CA_REGISTER) { value ->
                if ( fDelta > 10 && value.toDouble() > 710  && isExperimentRunning) cause = "проверьте подключение ОИ"
                model.data.uca.value = (value.toDouble() * ktrVoltage).autoformat()
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.I_A_REGISTER) { value ->
                model.data.ia.value = (value.toDouble() * ktrAmperage).autoformat()
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.I_B_REGISTER) { value ->
                model.data.ib.value = (value.toDouble() * ktrAmperage).autoformat()
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.I_C_REGISTER) { value ->
                model.data.ic.value = (value.toDouble() * ktrAmperage).autoformat()
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.P_REGISTER) { value ->
                model.data.p.value = abs(value.toDouble() * ktrAmperage * ktrVoltage).autoformat()
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
        }


        if (isExperimentRunning) {
            delta.startObject()
        }

        if (isExperimentRunning) {
            startRegulation()
        }

        if (isExperimentRunning) {
            var timer = 5.0
            while (isExperimentRunning && timer > 0) {
                sleep(100)
                timer -= 0.1
            }
        }


        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Выдержка $setTime секунд")
            var timer = setTime
            while (isExperimentRunning && timer > 0) {
                sleep(100)
                timer -= 0.1
                model.data.timeExp.value = abs(timer).autoformat()
            }
        }

//        protocolModel.runningUAB = model.data.uab.value
//        protocolModel.runningUBC = model.data.ubc.value
//        protocolModel.runningUCA = model.data.uca.value
//        protocolModel.runningIA = model.data.ia.value
//        protocolModel.runningIB = model.data.ib.value
//        protocolModel.runningIC = model.data.ic.value
//        protocolModel.runningTempOI = model.data.tempOI.value
//        protocolModel.runningTempAmb = model.data.tempAmb.value
//        protocolModel.runningSpeed = model.data.n.value
//        protocolModel.runningVibro1 = model.data.vibroPol.value
//        protocolModel.runningVibro2 = model.data.vibroRab.value
//        protocolModel.runningTime = setTime.toString()
//        protocolModel.runningP1 = model.data.p.value
//        protocolModel.runningCos = model.data.cos.value

        stopRegulation()

        delta.stopObject()

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Выключение ПЧ")
        }
        var timerDelta = 10
        while (timerDelta > 0) {
            sleep(100)
            timerDelta--
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
//        protocolModel.runningResult = model.data.result.value
        restoreData()
    }

//    private fun calibrateVoltage() {
//        appendMessageToLog(LogTag.DEBUG, "Проверка выставленного напряжения")
//        val uAvg =
//            (model.data.uab.value.toDouble() + model.data.ubc.value.toDouble() + model.data.uca.value.toDouble()) / 3.0
//        val kCalibr = objectModel!!.uNom.toDouble() / uAvg
//        if (kCalibr < 1.2 && kCalibr > 0.9) {
//            voltageDelta *= kCalibr
//            delta.setObjectURun(voltageDelta)
//        } else {
//            appendMessageToLog(LogTag.ERROR, "Коэффициент $kCalibr")
//        }
//    }

    private fun startRegulation() {
        val timer = System.currentTimeMillis()
        appendMessageToLog(LogTag.DEBUG, "Разгон двигателя")
        while (isExperimentRunning && fDelta != 50 && fDelta < 50) {
            delta.setObjectF(++fDelta)
            sleep(1000)
            if (System.currentTimeMillis() - timer > 60000) cause = "превышено время регулирования"
        }
    }

    private fun stopRegulation() {
        val timer = System.currentTimeMillis()
        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Замедление двигателя")
        }
        while (fDelta > 0) {
            delta.setObjectF(--fDelta)
            sleep(1000)
            if (System.currentTimeMillis() - timer > 60000) cause = "превышено время регулирования"
        }
    }

    override fun stop() {
        cause = "Отменено оператором"
        model.data.result.value = "Прервано"
    }

    private fun saveData() {
//        protocolModel.runningUAB = model.data.uab.value
//        protocolModel.runningUBC = model.data.ubc.value
//        protocolModel.runningUCA = model.data.uca.value
//        protocolModel.runningIA = model.data.ia.value
//        protocolModel.runningIB = model.data.ib.value
//        protocolModel.runningIC = model.data.ic.value
//        protocolModel.runningTempOI = model.data.tempOI.value
//        protocolModel.runningTempAmb = model.data.tempAmb.value
//        protocolModel.runningSpeed = model.data.n.value
//        protocolModel.runningVibro1 = model.data.vibroPol.value
//        protocolModel.runningVibro2 = model.data.vibroRab.value
//        protocolModel.runningTime = setTime.toString()
//        protocolModel.runningP1 = model.data.p.value
//        protocolModel.runningCos = model.data.cos.value
//        protocolModel.runningResult = model.data.result.value
    }

    private fun restoreData() {
//        model.data.uab.value = protocolModel.runningUAB
//        model.data.ubc.value = protocolModel.runningUBC
//        model.data.uca.value = protocolModel.runningUCA
//        model.data.ia.value = protocolModel.runningIA
//        model.data.ib.value = protocolModel.runningIB
//        model.data.ic.value = protocolModel.runningIC
//        model.data.tempOI.value = protocolModel.runningTempOI
//        model.data.tempAmb.value = protocolModel.runningTempAmb
//        model.data.n.value = protocolModel.runningSpeed
//        model.data.vibroPol.value = protocolModel.runningVibro1
//        model.data.vibroRab.value = protocolModel.runningVibro2
//        model.data.p.value = protocolModel.runningP1
//        model.data.cos.value = protocolModel.runningCos
    }
}