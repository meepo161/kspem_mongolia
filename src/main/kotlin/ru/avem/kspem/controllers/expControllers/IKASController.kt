package ru.avem.kspem.controllers.expControllers

import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.avem.ikas.IKAS8Model
import ru.avem.kspem.communication.model.devices.trm202.TRM202Model
import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.data.protocolModel
import ru.avem.kspem.data.schemeType
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.view.expViews.IKASView
import ru.avem.stand.modules.r.communication.model.devices.avem.ikas.IKAS8
import ru.avem.stand.utils.autoformat
import ru.avem.stand.utils.toDoubleOrDefault
import java.util.*


class IKASController : CustomController() {

    override val model: IKASView by inject()
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
                if (!isResponding)  {
                    cause = "ИКАС не отвечает"
                } else {
                    cm.startPoll(CommunicationModel.DeviceID.PR61, IKAS8Model.STATUS) {  value ->
                        status = value.toInt()
                        if (!ikas.isResponding &&isExperimentRunning) cause = "ИКАС не отвечает"
                    }
                    cm.startPoll(CommunicationModel.DeviceID.PR61, IKAS8Model.RESIST_MEAS) { value ->
                        measuredR = value.toDouble()
                    }
                }
            }
        }

        if (isExperimentRunning) {
//            initButtonPost()
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Начало измерения...")
            ikas.startMeasuringAB()
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
            ikas.startMeasuringBC()
            while (isExperimentRunning && status != 0 && status != 101) {
                Thread.sleep(100)
            }
            while (isExperimentRunning && measuredR == -1.0) {
                Thread.sleep(100)
            }
            model.data.R2.value =
                if (measuredR != 1E9) "%.4f".format(Locale.ENGLISH, measuredR) else "Обрыв"
        }

        if (isExperimentRunning) {
            ikas.startMeasuringCA()
            while (isExperimentRunning && status != 0 && status != 101) {
                Thread.sleep(100)
            }
            while (isExperimentRunning && measuredR == -1.0) {
                Thread.sleep(100)
            }
            model.data.R3.value =
                if (measuredR != 1E9) "%.4f".format(Locale.ENGLISH, measuredR) else "Обрыв"
        }

        if (isExperimentRunning) {
            calcRs()
        }

        finalizeExperiment()
        when(cause) {
            "" -> {
                if (model.data.R1.value == "Обрыв" ||
                    model.data.R2.value == "Обрыв" ||
                    model.data.R3.value == "Обрыв") {
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
        protocolModel.ikasR1 = model.data.calcR1.value
        protocolModel.ikasR2 = model.data.calcR2.value
        protocolModel.ikasR3 = model.data.calcR3.value
        protocolModel.ikasResult = model.data.result.value
    }

    override fun stop() {
        cause = "Отменено оператором"
        model.data.result.value = "Прервано"
    }

    private fun calcRs() {
        if (model.data.R1.value == "Обрыв" ||
            model.data.R2.value == "Обрыв" ||
            model.data.R3.value == "Обрыв"
        ) {
            model.data.calcR1.value = "Обрыв"
            model.data.calcR2.value = "Обрыв"
            model.data.calcR3.value = "Обрыв"

        } else {
            val r12 = model.data.R1.value.toDouble()
            val r23 = model.data.R2.value.toDouble()
            val r31 = model.data.R3.value.toDouble()

            if (objectModel!!.scheme == schemeType.star) {
                model.data.calcR1.value = "%.4f".format(Locale.ENGLISH, ((r31 + r12 - r23) / 2.0))
                model.data.calcR2.value = "%.4f".format(Locale.ENGLISH, ((r12 + r23 - r31) / 2.0))
                model.data.calcR3.value = "%.4f".format(Locale.ENGLISH, ((r23 + r31 - r12) / 2.0))
            } else if (objectModel!!.scheme == schemeType.triangle) {
                model.data.calcR1.value = "%.4f".format(Locale.ENGLISH, (2.0 * r23 * r31 / (r23 + r31 - r12) - (r23 + r31 - r12) / 2.0))
                model.data.calcR2.value = "%.4f".format(Locale.ENGLISH, (2.0 * r31 * r12 / (r31 + r12 - r23) - (r31 + r12 - r23) / 2.0))
                model.data.calcR3.value = "%.4f".format(Locale.ENGLISH, (2.0 * r12 * r23 / (r12 + r23 - r31) - (r12 + r23 - r31) / 2.0))
            }

            val rA = model.data.calcR1.value.toDouble()
            val rB = model.data.calcR2.value.toDouble()
            val rC = model.data.calcR3.value.toDouble()

            val t = model.data.tempAmb.value.toDoubleOrDefault(0.0)
            val rtK = tempKoef // при 20°C
            val rtT = 20.0

            model.data.calcR1.value = "%.4f".format(Locale.ENGLISH, (rA / (1 + rtK * (t - rtT))))
            model.data.calcR2.value = "%.4f".format(Locale.ENGLISH, (rB / (1 + rtK * (t - rtT))))
            model.data.calcR3.value = "%.4f".format(Locale.ENGLISH, (rC / (1 + rtK * (t - rtT))))

//            model.data.averR.value =
//                ((model.data.R1.value.toDouble() + model.data.R2.value.toDouble() + model.data.R3.value.toDouble()) / 3.0).autoformat()
//
//            testModel.percentData.R1.value =
//                ((model.data.R1.value.toDouble() - model.data.averR.value.toDouble()) / model.data.averR.value.toDouble() * 100).autoformat()
//            testModel.percentData.R2.value =
//                ((model.data.R2.value.toDouble() - model.data.averR.value.toDouble()) / model.data.averR.value.toDouble() * 100).autoformat()
//            testModel.percentData.R3.value =
//                ((model.data.R3.value.toDouble() - model.data.averR.value.toDouble()) / model.data.averR.value.toDouble() * 100).autoformat()
        }
    }

    private fun saveData() {
        protocolModel.ikasR1 = model.data.calcR1.value
        protocolModel.ikasR2 = model.data.calcR2.value
        protocolModel.ikasR3 = model.data.calcR3.value
        protocolModel.ikasResult = model.data.result.value
    }
}