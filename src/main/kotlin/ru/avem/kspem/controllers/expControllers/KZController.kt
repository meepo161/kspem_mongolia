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
import ru.avem.kspem.view.expViews.KZView
import ru.avem.stand.utils.autoformat
import kotlin.math.abs

class KZController : CustomController() {
    override val model: KZView by inject()
    override val name = model.name
    var deltaStatus = 0
    private var setTime = 0.0
    var fDelta = 1
    private var ktrVoltage = 1.0
    private var ktrAmperage = 1.0

    @Volatile
    var voltageDelta = 0.0
    var koefDelta = 0.0
    var ktrDelta = 1.0


    override fun start() {
        model.clearTables()
        super.start()
        // (value-4)*0.625

        fDelta = 1
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
                    model.data.n.value = value.autoformat()
                }
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация PM135...")
            pm135.checkResponsibility()
            sleep(1000)

            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.U_AB_REGISTER) { value ->
                if (fDelta > 10 && value.toDouble() > 700 && isExperimentRunning) cause = "проверьте подключение ОИ"
                model.data.uAB.value = (value.toDouble() * ktrVoltage).autoformat()
                if (!pm135.isResponding && isExperimentRunning) cause = "PM135 не отвечает"
                if (isExperimentRunning && (value.toDouble() * ktrVoltage) > objectModel!!.uN.toDouble() * 0.35) cause =
                    "превышение напряжения ОИ, проверьте схему"
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.U_BC_REGISTER) { value ->
                if (fDelta > 10 && value.toDouble() > 700 && isExperimentRunning) cause = "проверьте подключение ОИ"
                model.data.uBC.value = (value.toDouble() * ktrVoltage).autoformat()
                if (isExperimentRunning && (value.toDouble() * ktrVoltage) > objectModel!!.uN.toDouble() * 0.35) cause =
                    "превышение напряжения ОИ, проверьте схему"
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.U_CA_REGISTER) { value ->
                if (fDelta > 10 && value.toDouble() > 700 && isExperimentRunning) cause = "проверьте подключение ОИ"
                model.data.uCA.value = (value.toDouble() * ktrVoltage).autoformat()
                if (isExperimentRunning && (value.toDouble() * ktrVoltage) > objectModel!!.uN.toDouble() * 0.35) cause =
                    "превышение напряжения ОИ, проверьте схему"
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
            appendMessageToLog(LogTag.MESSAGE, "Проверка выставленного напряжения")
            var timer = 5.0
            while (isExperimentRunning && timer > 0) {
                sleep(100)
                timer -= 0.1
                model.data.timeExp.value = abs(timer).autoformat()
            }
        }

        if (isExperimentRunning) {
            calibrateVoltage()
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Выдержка 5 секунд")
            var timer = 5.0
            while (isExperimentRunning && timer > 0) {
                sleep(100)
                timer -= 0.1
                model.data.timeExp.value = abs(timer).autoformat()
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

        var timer = 100
        while (timer > 0) {
            sleep(100)
            timer--
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
        protocolModel.kzResult = model.data.result.value
        restoreData()
    }

    private fun calibrateVoltage() {
        appendMessageToLog(LogTag.DEBUG, "Проверка выставленного напряжения")
        val uAvg =
            (model.data.uAB.value.toDouble() + model.data.uBC.value.toDouble() + model.data.uCA.value.toDouble()) / 3.0
        val kCalibr = objectModel!!.uN.toDouble() / 3.8 / uAvg
        if (kCalibr < 1.6 && kCalibr > 0.9) {
            voltageDelta *= kCalibr
            delta.setObjectURun(voltageDelta)
        } else {
            appendMessageToLog(LogTag.ERROR, "Коэффициент $kCalibr")
        }
    }


    private fun startRegulation() {
        val timer = System.currentTimeMillis()
        appendMessageToLog(LogTag.DEBUG, "Разгон двигателя")
        while (isExperimentRunning && fDelta != 50 && fDelta < 50) {
            delta.setObjectF(++fDelta)
            sleep(200)
            if (System.currentTimeMillis() - timer > 30000) cause = "превышено время регулирования"
        }
    }

    private fun stopRegulation() {
        val timer = System.currentTimeMillis()
        appendMessageToLog(LogTag.DEBUG, "Замедление двигателя")
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