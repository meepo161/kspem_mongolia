package ru.avem.kspem.controllers.expControllers

import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.avem.avem4.Avem4Model
import ru.avem.kspem.communication.model.devices.avem.latr.LatrModel
import ru.avem.kspem.communication.model.devices.pm130.PM130Model
import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.data.protocolModel
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.utils.sleep
import ru.avem.kspem.view.expViews.VIUView
import ru.avem.stand.utils.autoformat
import kotlin.math.abs

class VIUController : CustomController() {
    override val model: VIUView by inject()
    override val name = model.name

    var voltageSet = 0.0
    var setI = 100.0
    var setTime = 0.0
    var voltage = 0.0
    var amperage = 0.0
    var voltageLatr = 0.0

    override fun start() {
        model.clearTables()
        super.start()

        voltageSet = objectModel!!.uVIU.toDouble()
        setTime = objectModel!!.timeVIU.toDouble()
        model.data.setU.value = objectModel!!.uVIU
        model.data.setI.value = setI.autoformat()
        model.data.time.value = objectModel!!.timeVIU


        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация ТРМ202...")
            with(trm202) {
                checkResponsibility()
                if (!isResponding) cause = "ТРМ202 не отвечает"
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
            appendMessageToLog(LogTag.MESSAGE, "Инициализация PM135...")
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.I_A_REGISTER) { value ->
                amperage = value.toDouble() * 1 / 5 * 1000.0
                model.data.I.value = amperage.autoformat()
                if (!pm135.isResponding && isExperimentRunning) cause = "PM135 не отвечает"
                if (amperage > setI && isExperimentRunning) cause = "превышение тока ОИ"
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация АВЭМ3...")
            cm.startPoll(CommunicationModel.DeviceID.PV24, Avem4Model.RMS) { value ->
                voltage = value.toDouble()
                model.data.U.value = voltage.autoformat()
                if (!avemUvv.isResponding && isExperimentRunning) cause = "АВЭМ3 не отвечает"
            }
        }

        if (isExperimentRunning) {
//            initButtonPost()
        }

        if (isExperimentRunning) {
            pr102.shunt(true)
        }

        if (isExperimentRunning) {
            pr102.ground(true)
            pr102.viu(true)
            pr102.shunt(false)
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
            if (voltageLatr < 5) {
                pr102.km1(true)
                pr102.arn(true)
                pr102.vv(true)
            } else cause = "АРН не вышел в нулевое положение"
        }

        if (isExperimentRunning) {
            if (objectModel!!.uVIU.toDoubleOrNull() != null) {
                for (i in 1..3) {
                    voltageRegulation(voltageSet)
                }
                appendMessageToLog(LogTag.MESSAGE, "Регулировка завершена")
            } else cause = "ошибка задания напряжения"
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Выдержка напряжения")
        }

        var timer = setTime
        while (isExperimentRunning && timer > 0) {
            sleep(100)
            timer -= 0.1
            model.data.timeExp.value = abs(timer).autoformat()
        }

        saveData()

        latr.resetLATR()

        val timerLatr = System.currentTimeMillis()
        while (voltage > 200) {
            sleep(100)
            if (System.currentTimeMillis() - timerLatr > 30000) {
                cause = "АРН не вернулся в начальное положение"
                break
            }
        }

        pr102.arn(false)

        if (isExperimentRunning) {
            pr102.ground(false)
            pr102.mgr(false)
            sleep(3000)
        }

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
                model.data.result.value = "Успешно"
                appendMessageToLog(LogTag.MESSAGE, "Испытание завершено успешно")
            }
            "превышение тока ОИ" -> {
                model.data.result.value = "Пробой"
                appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
            }
            else -> {
                model.data.result.value = "Прервано"
                appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
            }
        }
        finalizeExperiment()
        protocolModel.viuResult = model.data.result.value
        restoreData()
    }

    private fun voltageRegulation(volt: Double) {
        var timer = 0L
        val slow = 200.0
        val fast = 1000.0
        var speedPerc = 35f
        var timePulsePerc = 20f
        val up = 220f
        val down = 1f
        var direction: Float
        timer = System.currentTimeMillis()
        appendMessageToLog(LogTag.DEBUG, "Быстрая регулировка")
        while (abs(voltage - volt) > fast && isExperimentRunning) {
            if (voltage < volt) {
                direction = up
                speedPerc = 100f
            } else {
                direction = down
                speedPerc = 100f
            }
            if (System.currentTimeMillis() - timer > 90000) cause = "Превышено время регулирования"
            latr.startUpLATRUp(direction, false, speedPerc)
        }
        latr.stopLATR()
        timer = System.currentTimeMillis()
        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Грубая регулировка")
        }
        while (abs(voltage - volt) > slow && isExperimentRunning) {
            if (voltage < volt) {
                direction = up
                timePulsePerc = 95f
            } else {
                direction = down
                timePulsePerc = 95f
            }
            if (System.currentTimeMillis() - timer > 60000) cause = "Превышено время регулирования"
            latr.startUpLATRPulse(direction, false, timePulsePerc)
        }
        latr.stopLATR()
        timer = System.currentTimeMillis()
        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Точная регулировка")
        }
        while (abs(voltage - volt) > volt / 100 * 1.5 && isExperimentRunning) {
            if (voltage < volt) {
                direction = up
                timePulsePerc = 90f
            } else {
                direction = down
                timePulsePerc = 90f
            }
            latr.startUpLATRPulse(direction, false, timePulsePerc)
            sleep(500)
            latr.stopLATR()
            sleep(500)
            if (System.currentTimeMillis() - timer > 60000) cause = "Превышено время регулирования"
        }
        latr.stopLATR()
    }

    override fun stop() {
        cause = "Отменено оператором"
        model.data.result.value = "Прервано"
    }

    private fun saveData() {
//        protocolModel.viuU      = "model.data.U.value"
//        protocolModel.viuI      = "model.data.I.value"
//        protocolModel.viuTime   = "objectModel!!.timeVIU"

        protocolModel.viuU = model.data.U.value
        protocolModel.viuI = model.data.I.value
        protocolModel.viuTime = objectModel!!.timeVIU
    }

    private fun restoreData() {
        model.data.U.value = protocolModel.viuU
        model.data.I.value = protocolModel.viuI
    }
}