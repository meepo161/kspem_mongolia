package ru.avem.kspem.controllers.expControllers

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
import ru.avem.kspem.view.expViews.HHView
import ru.avem.stand.utils.autoformat
import kotlin.math.abs


class HHController : CustomController() {
    override val model: HHView by inject()
    override val name = model.name
    private var setTime = 0.0
    private var uAnc = 0.0
    private var uOV = 0.0
    private var iAnc = 0.0
    private var iOV = 0.0
    private var voltageLatr = 0.0

//    ТВН 460В = 1.00 на пр


    override fun start() {
        model.clearTables()
        super.start()


        setTime = objectModel!!.timeHH.toDouble()

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация ТРМ202...")
            trm202.checkResponsibility()
            if (!trm202.isResponding) {
                cause = "ТРМ202 не отвечает"
            } else {
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
            appendMessageToLog(LogTag.MESSAGE, "Инициализация PV23...")
            cm.startPoll(CommunicationModel.DeviceID.PV23, Avem4Model.AMP) { value ->
                if (!avemUov.isResponding && isExperimentRunning) cause = "АРН не отвечает"
                model.data.uOV.value = value.autoformat()
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация PV25...")
            cm.startPoll(CommunicationModel.DeviceID.PV25, Avem4Model.AMP) { value ->
                if (!avemUoy.isResponding && isExperimentRunning) cause = "АРН не отвечает"
                model.data.uAnc.value = value.autoformat()
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация PA13...")
            cm.startPoll(CommunicationModel.DeviceID.PA13, Avem7Model.AMPERAGE) { value ->
                if (!avemIov.isResponding && isExperimentRunning) cause = "АРН не отвечает"
                model.data.iOV.value = value.autoformat()
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация PA15...")
            cm.startPoll(CommunicationModel.DeviceID.PA15, Avem7Model.AMPERAGE) { value ->
                if (!avemIoy.isResponding && isExperimentRunning) cause = "АРН не отвечает"
                model.data.iAnc.value = value.autoformat()
            }
        }

//        if (isExperimentRunning) {
//            appendMessageToLog(LogTag.MESSAGE, "Инициализация АРН...")
//            cm.startPoll(CommunicationModel.DeviceID.GV240, LatrModel.U_RMS_REGISTER) { value ->
//                if (!latr.isResponding && isExperimentRunning) cause = "АРН не отвечает"
//                voltageLatr = value.toDouble()
//            }
//            latr.resetLATR()
//        }

        if (isExperimentRunning) {
            initButtonPost()
        }

        if (isExperimentRunning) {
            if (voltageLatr < 10) {
                pr102.arn(true)
            } else cause = "АРН не вернулся в нулевое положение"
        }

        if (isExperimentRunning) {
            pr102.tvn(true)
        }

        if (isExperimentRunning) {
//            startRegulation(setuAnc = objectModel!!.uN.toDouble(), setuOv = objectModel!!.uOV.toDouble())
        }

//        if (isExperimentRunning) {
//            var timer = 50
//            while (isExperimentRunning && timer > 0) {
//                sleep(100)
//                timer--
//            }
//        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Выдержка $setTime секунд")
            var timer = setTime
            while (isExperimentRunning && timer > 0) {
                sleep(100)
                timer -= 0.1
                model.data.timeExp.value = abs(timer).autoformat()
            }
        }

        protocolModel.hhTempOI = model.data.tempOI.value
        protocolModel.hhSpeed = model.data.n.value
        protocolModel.hhTime = objectModel!!.timeHH



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
        protocolModel.hhResult = model.data.result.value
        restoreData()
    }


    override fun stop() {
        cause = "Отменено оператором"
        model.data.result.value = "Прервано"
    }

    private fun saveData() {
        protocolModel.hhTempOI = model.data.tempOI.value
        protocolModel.hhSpeed = model.data.n.value
        protocolModel.hhTime = objectModel!!.timeHH
        protocolModel.hhResult = model.data.result.value
    }

    private fun restoreData() {
        model.data.tempOI.value = protocolModel.hhTempOI
        model.data.n.value = protocolModel.hhSpeed
    }
}