package ru.avem.kspem.controllers.expControllersMPT

import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.avem.ikas.IKAS8Model
import ru.avem.kspem.communication.model.devices.trm202.TRM202Model
import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.data.protocolModel
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.utils.showTwoWayDialog
import ru.avem.kspem.utils.sleep
import ru.avem.kspem.view.expViews.expViewsMPT.IKASViewMPT
import ru.avem.stand.utils.autoformat
import java.util.*


class IKASControllerMPT : CustomController() {

    override val model: IKASViewMPT by inject()
    override val name = model.name
    var status = 0
    var measuredR = 0.0
    val tempKoef = 0.00425

    override fun start() {
        super.start()
        model.clearTables()

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
            appendMessageToLog(LogTag.MESSAGE, "Инициализация ИКАС...")
            with(ikas) {
                checkResponsibility()
                if (!isResponding) {
                    cause = "ИКАС не отвечает"
                } else {
                    cm.startPoll(CommunicationModel.DeviceID.PR61, IKAS8Model.STATUS) { value ->
                        status = value.toInt()
                        if (!ikas.isResponding && isExperimentRunning) cause = "ИКАС не отвечает"
                    }
                    cm.startPoll(CommunicationModel.DeviceID.PR61, IKAS8Model.RESIST_MEAS) { value ->
                        measuredR = value.toDouble()
                    }
                }
            }
        }

        if (isExperimentRunning) {
            initButtonPost()
        }

        var isClicked = false
        if (isExperimentRunning) {
            showTwoWayDialog(
                title = "Внимание!",
                text = "Подключите измерительные провода ИКАС <A> и <N> к обмотке возбуждения",
                way1Title = "Подтвердить",
                way2Title = "Отменить",
                way1 = {
                    isClicked = true
                },
                way2 = {
                    isClicked = true
                    cause = "Отменено оператором"
                },
                currentWindow = primaryStage.scene.window
            )
        }

        while (isExperimentRunning && !isClicked) {
            sleep(100)
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Начало измерения...")
            ikas.startMeasuringAA()
            while (isExperimentRunning && status != 0 && status != 101) {
                Thread.sleep(100)
            }
            while (isExperimentRunning && measuredR == -1.0) {
                Thread.sleep(100)
            }
            model.data.R1.value =
                if (measuredR != 1E9) "%.4f".format(Locale.ENGLISH, measuredR) else "Обрыв"
        }

        if (isExperimentRunning) {

            isClicked = false

            showTwoWayDialog(
                title = "Внимание!",
                text = "Подключите измерительные провода ИКАС <A> и <N> к обмотке якоря",
                way1Title = "Подтвердить",
                way2Title = "Отменить",
                way1 = {
                    isClicked = true
                },
                way2 = {
                    isClicked = true
                    cause = "Отменено оператором"
                },
                currentWindow = primaryStage.scene.window
            )
        }

        while (isExperimentRunning && !isClicked) {
            sleep(100)
        }

        if (isExperimentRunning) {
            ikas.startMeasuringAA()
            while (isExperimentRunning && status != 0 && status != 101) {
                Thread.sleep(100)
            }
            while (isExperimentRunning && measuredR == -1.0) {
                Thread.sleep(100)
            }
            model.data.R2.value =
                if (measuredR != 1E9) "%.4f".format(Locale.ENGLISH, measuredR) else "Обрыв"
        }

        ikas.stopMeasuring()
        finalizeExperiment()
        when (cause) {
            "" -> {
                if (model.data.R1.value == "Обрыв" ||
                    model.data.R2.value == "Обрыв"
                ) {
                    appendMessageToLog(LogTag.ERROR, "Обрыв")
                    model.data.result.value = "Обрыв"
                } else {
                    appendMessageToLog(LogTag.MESSAGE, "Испытание завершено успешно")
                    model.data.result.value = "Успешно"
                }
            }
//            testModel.percentData.R1.value.toDouble() > 2.0 ||
//                    testModel.percentData.R2.value.toDouble() > 2.0 ||
//                    testModel.percentData.R3.value.toDouble() > 2.0 -> {
//                appendMessageToLog(LogTag.ERROR, "Не соответствует. Отклонение превышает 2%")
//                model.data.result.value = "Не соответствует"
//            }
            else -> {
                appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
                model.data.result.value = "Прервано"
            }
        }
        saveData()
    }

    override fun stop() {
        cause = "Отменено оператором"
        model.data.result.value = "Прервано"
    }

    private fun saveData() {
//        protocolModel.ikasR1        = "model.data.R1.value"
//        protocolModel.ikasR2        = "model.data.R2.value"
//        protocolModel.ikasResult    = "model.data.result.value"
        protocolModel.ikasR1 = model.data.R1.value
        protocolModel.ikasR2 = model.data.R2.value
        protocolModel.ikasResult = model.data.result.value
    }
}