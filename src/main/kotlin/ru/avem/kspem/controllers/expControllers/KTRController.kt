package ru.avem.kspem.controllers.expControllers

import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.delta.DeltaModel
import ru.avem.kspem.communication.model.devices.pm130.PM130Model
import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.data.protocolModel
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.utils.sleep
import ru.avem.kspem.view.expViews.KTRView
import ru.avem.stand.utils.autoformat

class KTRController : CustomController() {
    override val model: KTRView by inject()
    override val name = model.name

    var deltaStatus = 0
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


        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация PM135...")
            pm135.checkResponsibility()
            sleep(1000)

            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.U_AB_REGISTER) { value ->
                if (fDelta > 10 && value.toDouble() > 710 && isExperimentRunning) cause = "проверьте подключение ОИ"
                model.data.uAB.value = (value.toDouble()).autoformat()
                if (fDelta < 50.1 && fDelta > 10 && (value.toDouble() * ktrVoltage * 1.15 > voltageDelta/ ktrDelta * fDelta / 50)) cause = "проверьте подключение ОИ"
                if (!pm135.isResponding && isExperimentRunning) cause = "PM135 не отвечает"
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.U_BC_REGISTER) { value ->
                if (fDelta > 10 && value.toDouble() > 710 && isExperimentRunning) cause = "проверьте подключение ОИ"
                model.data.uBC.value = (value.toDouble()).autoformat()
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.U_CA_REGISTER) { value ->
                if (fDelta > 10 && value.toDouble() > 710 && isExperimentRunning) cause = "проверьте подключение ОИ"
                model.data.uCA.value = (value.toDouble()).autoformat()
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
            ktrVoltage = 1.0
//            pr102.tv1500(true)

            delta.setObjectParamsVIU(
                fOut = 50,
                voltageP1 = 40,
                fP1 = 50
            )
        }

        if (isExperimentRunning) {
//            pr102.do4(true)
        }

        if (isExperimentRunning) {
            delta.startObject()
            appendMessageToLog(LogTag.DEBUG, "Разгон ПЧ")
        }

        if (isExperimentRunning) {
            var timer = 20.0
            while (isExperimentRunning && timer > 0) {
                sleep(100)
                timer -= 0.1
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Фиксация значений")
            model.data.uAvg1.value = ((model.data.uAB.value.toDouble() + model.data.uBC.value.toDouble() + model.data.uCA.value.toDouble()) / 3.0).autoformat()
        }

        if (isExperimentRunning) {
//            pr102.do4(false)
//            pr102.km13(true)
        }

        if (isExperimentRunning) {
            var timer = 10.0
            while (isExperimentRunning && timer > 0) {
                sleep(100)
                timer -= 0.1
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Фиксация значений")
            model.data.uAvg2.value = ((model.data.uAB.value.toDouble() + model.data.uBC.value.toDouble() + model.data.uCA.value.toDouble()) / 3.0).autoformat()
            model.data.kTR.value = (model.data.uAvg1.value.toDouble() / model.data.uAvg2.value.toDouble()).autoformat()
        }

        delta.stopObject()

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Остановка ПЧ")
            var timer = 10.0
            while (isExperimentRunning && timer > 0) {
                sleep(100)
                timer -= 0.1
            }
        } else {
            sleep(2000)

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
        saveData()
    }

    private fun startRegulation() {
        val timer = System.currentTimeMillis()
        appendMessageToLog(LogTag.DEBUG, "Разгон двигателя")
        while (isExperimentRunning && fDelta != 50 && fDelta < 51) {
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
//        protocolModel.ktrUAVG1 = model.data.uAvg1.value
//        protocolModel.ktrUAVG2 = model.data.uAvg2.value
//        protocolModel.ktrKTR = model.data.kTR.value
//        protocolModel.ktrResult = model.data.result.value
    }

    private fun restoreData() {
        // no need
    }
}