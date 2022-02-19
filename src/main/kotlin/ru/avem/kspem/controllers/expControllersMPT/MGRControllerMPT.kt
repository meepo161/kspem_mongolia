package ru.avem.kspem.controllers.expControllersMPT

import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.trm202.TRM202Model
import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.data.protocolModel
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.utils.showTwoWayDialog
import ru.avem.kspem.utils.sleep
import ru.avem.kspem.view.expViews.expViewsMPT.MGRViewMPT
import ru.avem.stand.utils.autoformat
import tornadofx.isDouble
import java.util.*


class MGRControllerMPT : CustomController() {
    override val model: MGRViewMPT by inject()
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

        if (isExperimentRunning) {
            initButtonPost()
        }

        var isClicked = false

        if (isExperimentRunning) {
            showTwoWayDialog(
                title = "Внимание!",
                text = "Подключить ТОЛЬКО Высоковольтный провод с зажимом типа «крокодил» (XA1) к обмотке возбуждения ОИ" +
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
            pr102.shunt(true)
            pr102.ground(true)
            pr102.mgr(true)
            pr102.shunt(false)
            sleep(3000)
        }

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
                        model.data.UOV.value = measuredUr.autoformat()
                        model.data.R15OV.value = measuredR15Mohm.autoformat()
                        model.data.R60OV.value = measuredR60Mohm.autoformat()
                        model.data.K_ABSOV.value = measuredAbs.autoformat()
                        appendMessageToLog(LogTag.DEBUG, "Заземление")
                        timer = 30.0
                        while (isExperimentRunning && timer > 0) {
                            sleep(100)
                            model.data.time.value = "%.1f".format(Locale.ENGLISH, timer)
                            timer -= 0.1
                        }
                    }

                } else {
                    cause = "Меггер не отвечает"
                }
            }
        }

        if (isExperimentRunning) {
            pr102.ground(false)
            pr102.mgr(false)
            sleep(3000)
        }

        isClicked = false

        if (isExperimentRunning) {
            showTwoWayDialog(
                title = "Внимание!",
                text = "Подключить ТОЛЬКО Высоковольтный провод с зажимом типа «крокодил» (XA1) к обмотке якоря ОИ" +
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
            pr102.shunt(true)
            pr102.ground(true)
            pr102.mgr(true)
            pr102.shunt(false)
            sleep(3000)
        }

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
                        model.data.UOY.value = measuredUr.autoformat()
                        model.data.R15OY.value = measuredR15Mohm.autoformat()
                        model.data.R60OY.value = measuredR60Mohm.autoformat()
                        model.data.K_ABSOY.value = measuredAbs.autoformat()
                        appendMessageToLog(LogTag.DEBUG, "Заземление")
                        timer = 30.0
                        while (isExperimentRunning && timer > 0) {
                            sleep(100)
                            model.data.time.value = "%.1f".format(Locale.ENGLISH, timer)
                            timer -= 0.1
                        }
                    }

                } else {
                    cause = "Меггер не отвечает"
                }
            }
        }

        if (isExperimentRunning) {
            pr102.ground(false)
            pr102.mgr(false)
            sleep(3000)
        }

        isClicked = false

        if (isExperimentRunning) {
            showTwoWayDialog(
                title = "Внимание!",
                text = "Подключить ТОЛЬКО Высоковольтный провод с зажимом типа «крокодил» (XA1) к обмотке возбуждения ОИ" +
                        "\nПровод измерительный (ХА2) к обмотке якоря." +
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
            pr102.shunt(true)
            pr102.ground(true)
            pr102.mgr(true)
            pr102.shunt(false)
            sleep(3000)
        }

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
                        model.data.UOVOY.value = measuredUr.autoformat()
                        model.data.R15OVOY.value = measuredR15Mohm.autoformat()
                        model.data.R60OVOY.value = measuredR60Mohm.autoformat()
                        model.data.K_ABSOVOY.value = measuredAbs.autoformat()
                        appendMessageToLog(LogTag.DEBUG, "Заземление")
                        timer = 30.0
                        while (isExperimentRunning && timer > 0) {
                            sleep(100)
                            model.data.time.value = "%.1f".format(Locale.ENGLISH, timer)
                            timer -= 0.1
                        }
                    }

                } else {
                    cause = "Меггер не отвечает"
                }
            }
        }

        if (isExperimentRunning) {
            pr102.ground(false)
            pr102.mgr(false)
            sleep(3000)
        }

        when (cause) {
            "" -> {
                if (model.data.K_ABSOV.value.isDouble()) {
                    if (model.data.K_ABSOV.value.toDouble() < 1.3) {
                        appendMessageToLog(LogTag.ERROR, "Измеренный kABS < 1.3")
                        model.data.resultOV.value = "Не соответствует"
                    } else {
                        model.data.resultOV.value = "Соответствует"
                        appendMessageToLog(LogTag.MESSAGE, "Испытание завершено успешно")
                    }
                } else {
                    model.data.resultOV.value = "Обрыв"
                }
            }
            else -> {
                model.data.resultOV.value = "Прервано"
                appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
            }
        }

        when (cause) {
            "" -> {
                if (model.data.K_ABSOY.value.isDouble()) {
                    if (model.data.K_ABSOY.value.toDouble() < 1.3) {
                        appendMessageToLog(LogTag.ERROR, "Измеренный kABS < 1.3")
                        model.data.resultOY.value = "Не соответствует"
                    } else {
                        model.data.resultOY.value = "Соответствует"
                        appendMessageToLog(LogTag.MESSAGE, "Испытание завершено успешно")
                    }
                } else {
                    model.data.resultOY.value = "Обрыв"
                }
            }
            else -> {
                model.data.resultOY.value = "Прервано"
                appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
            }
        }

        when (cause) {
            "" -> {
                if (model.data.K_ABSOVOY.value.isDouble()) {
                    if (model.data.K_ABSOVOY.value.toDouble() < 1.3) {
                        appendMessageToLog(LogTag.ERROR, "Измеренный kABS < 1.3")
                        model.data.resultOVOY.value = "Не соответствует"
                    } else {
                        model.data.resultOVOY.value = "Соответствует"
                        appendMessageToLog(LogTag.MESSAGE, "Испытание завершено успешно")
                    }
                } else {
                    model.data.resultOVOY.value = "Обрыв"
                }
            }
            else -> {
                model.data.resultOVOY.value = "Прервано"
                appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
            }
        }
        finalizeExperiment()
        saveData()
    }


    override fun stop() {
        cause = "Отменено оператором"
        model.data.resultOV.value = "Прервано"
        model.data.resultOY.value = "Прервано"
        model.data.resultOVOY.value = "Прервано"
    }

    private fun saveData() {
        protocolModel.mgrTemp = model.data.tempOI.value

        protocolModel.mgrU1 = model.data.UOV.value
        protocolModel.mgrR151 = model.data.R15OV.value
        protocolModel.mgrR601 = model.data.R60OV.value
        protocolModel.mgrkABS1 = model.data.K_ABSOV.value
        protocolModel.mgrResult1 = model.data.resultOV.value

        protocolModel.mgrU2 = model.data.UOY.value
        protocolModel.mgrR152 = model.data.R15OY.value
        protocolModel.mgrR602 = model.data.R60OY.value
        protocolModel.mgrkABS2 = model.data.K_ABSOY.value
        protocolModel.mgrResult2 = model.data.resultOY.value

        protocolModel.mgrU3 = model.data.UOVOY.value
        protocolModel.mgrR153 = model.data.R15OVOY.value
        protocolModel.mgrR603 = model.data.R60OVOY.value
        protocolModel.mgrkABS3 = model.data.K_ABSOVOY.value
        protocolModel.mgrResult3 = model.data.resultOVOY.value
    }
}