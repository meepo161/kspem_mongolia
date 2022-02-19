package ru.avem.kspem.controllers.expControllers

import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.trm202.TRM202Model
import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.data.protocolModel
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.utils.showTwoWayDialog
import ru.avem.kspem.utils.sleep
import ru.avem.kspem.view.expViews.MGRView
import ru.avem.stand.utils.autoformat
import tornadofx.isDouble
import java.util.*


class MGRController : CustomController() {
    override val model: MGRView by inject()
    override val name = model.name

    override fun start() {
        model.clearTables()
        super.start()

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация ТРМ202...")
            with(trm202) {
                checkResponsibility()
                if (!isResponding) cause = "ТРМ202 не отвечает"
            }
            cm.startPoll(CommunicationModel.DeviceID.PS81, TRM202Model.T_1) { value ->
                model.data.tempAmb.value = value.autoformat()
            }
            cm.startPoll(CommunicationModel.DeviceID.PS81, TRM202Model.T_2) { value ->
                model.data.tempOI.value = value.autoformat()
            }
        }

        var isClicked = false

        if (isExperimentRunning) {
            showTwoWayDialog(
                title = "Внимание!",
                text = "Подключить ТОЛЬКО Высоковольтный провод с зажимом типа «крокодил» (XA1) к Испытуемой обмотке/выводу ОИ" +
                        "\nПровод измерительный (ХА2) к корпусу и/или частям, относительно которых будет проходить проверка." +
                        "\nСиловые провода НЕ ДОЛЖНЫ быть подключены к ОИ",
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
            initButtonPost()
        }

        if (isExperimentRunning) {
            pr102.shunt(true)
            pr102.ground(true)
            pr102.mgr(true)
            pr102.shunt(false)
            sleep(3000)
        }

//        if (isExperimentRunning) {
//            if (!onVV) {
//                if (onGround) {
//                    cause = "заземлитель не разомкнулся"
//                } else if (!onGround) {
//                    cause = "одновременно разомкнуты DI11 и DI12"
//                }
//            } else if (onGround && onVV) {
//                cause = "одновременно замкнуты DI11 и DI12"
//            }
//        }

        if (isExperimentRunning) {
            sleep(1000)
            appendMessageToLog(LogTag.MESSAGE, "Инициализация Мегаомметра...")
            with(cs02) {
                var timer1 = 50
                while (timer1-- > 0) {
                    sleep(100)
                }
                checkResponsibility()

                if (isResponding) {
                    appendMessageToLog(LogTag.MESSAGE, "Измерение сопротивления 90 секунд")
                    setVoltage(objectModel!!.uMGR.toInt())
                    var timer = 90.0
                    while (isExperimentRunning && timer > 0) {
                        sleep(100)
                        model.data.time.value = "%.1f".format(Locale.ENGLISH, timer)
                        timer -= 0.1
                    }
                    if (isExperimentRunning) {
                        val mgrData = readData()
                        val measuredR60 = mgrData[0].toDouble()
                        val measuredUr = mgrData[1].toDouble()
                        val measuredAbs = mgrData[2].toDouble()
                        val measuredR15 = mgrData[3].toDouble()

                        val measuredR60Mohm = (measuredR60 / 1_000_000)
                        val measuredR15Mohm = (measuredR15 / 1_000_000)
                        if (measuredR60Mohm > 200_000) {
                            model.data.U.value = measuredUr.autoformat()
                            model.data.R15.value = "обрыв"
                            model.data.R60.value = "обрыв"
                            model.data.K_ABS.value = "обрыв"
                            cause = "обрыв"
                        } else {
                            model.data.U.value = measuredUr.autoformat()
                            model.data.R15.value = measuredR15Mohm.autoformat()
                            model.data.R60.value = measuredR60Mohm.autoformat()
                            model.data.K_ABS.value = measuredAbs.autoformat()
                            appendMessageToLog(LogTag.DEBUG, "Заземление")
                            timer = 30.0
                            while (isExperimentRunning && timer > 0) {
                                sleep(100)
                                model.data.time.value = "%.1f".format(Locale.ENGLISH, timer)
                                timer -= 0.1
                            }
                        }
                    }

                } else {
                    cause = "Меггер не отвечает"
                }
            }
        }

        if (isExperimentRunning) {
            pr102.shunt(false)
            pr102.ground(false)
            pr102.mgr(false)
            sleep(3000)
        }
//
//        if (isExperimentRunning) {
//            if (!onGround) {
//                if (onVV) {
//                    cause = "заземлитель не замкнулся"
//                } else if (!onVV) {
//                    cause = "одновременно разомкнуты DI11 и DI12"
//                }
//            } else if (onGround && onVV) {
//                cause = "одновременно замкнуты DI11 и DI12"
//            }
//        }


        when (cause) {
            "" -> {
                if (model.data.K_ABS.value.isDouble()) {
                    if (model.data.K_ABS.value.toDouble() < 1.3) {
                        appendMessageToLog(LogTag.ERROR, "Измеренный kABS < 1.3")
                        model.data.result.value = "Не соответствует"
                    } else {
                        model.data.result.value = "Соответствует"
                        appendMessageToLog(LogTag.MESSAGE, "Испытание завершено успешно")
                    }
                } else {
                    model.data.result.value = "Обрыв"
                }
            }
            else -> {
                model.data.result.value = "Прервано"
                appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
            }
        }
        finalizeExperiment()


        model.data.U.value = "1"
        model.data.R15.value = "2"
        model.data.R60.value = "3"
        model.data.K_ABS.value = "4"
        model.data.tempAmb.value = "5"
        model.data.result.value = "6"
        saveData()
    }


    override fun stop() {
        cause = "Отменено оператором"
        model.data.result.value = "Прервано"
    }

    private fun saveData() {
        protocolModel.mgrTemp = model.data.tempOI.value
        protocolModel.mgrU1 = model.data.U.value
        protocolModel.mgrR151 = model.data.R15.value
        protocolModel.mgrR601 = model.data.R60.value
        protocolModel.mgrkABS1 = model.data.K_ABS.value
        protocolModel.mgrResult1 = model.data.result.value
    }
}