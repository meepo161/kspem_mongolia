package ru.avem.kspem.controllers.expControllersMPT

import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.avem.avem4.Avem4Model
import ru.avem.kspem.communication.model.devices.avem.avem7.Avem7Model
import ru.avem.kspem.communication.model.devices.avem.latr.LatrModel
import ru.avem.kspem.communication.model.devices.th01.TH01Model
import ru.avem.kspem.communication.model.devices.trm202.TRM202Model
import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.data.protocolModel
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.utils.sleep
import ru.avem.kspem.view.expViews.expViewsMPT.HHViewMPT
import ru.avem.kspem.view.expViews.expViewsMPT.NViewMPT
import ru.avem.stand.utils.autoformat
import kotlin.math.abs

class HHControllerMPT : CustomController() {
    override val model: HHViewMPT by inject()
    override val name = model.name
    private var setTime = 0.0
    private var ktrVoltage = 1.0

    private var ktrAmperageOY = 500 / 0.075
    private var ktrAmperageOV = 25 / 0.075

    var voltageOV = 0.0
    var amperageOV = 0.0
    var voltageOY = 0.0
    var amperageOY = 0.0

    var voltageLatr = 0.0
    var voltageOYSet = 0.0
    var voltageOVSet = 0.0
    var rotateSpeed = 0.0
    var rotateSpeedSet = 0.0

    override fun start() {
        model.clearTables()
        super.start()
        // (value-4)*0.625

        voltageOYSet = objectModel!!.uNom.toDouble()
        rotateSpeedSet = objectModel!!.nAsync.toDouble()
        voltageOVSet = objectModel!!.uOV.toDouble()
        setTime = objectModel!!.timeHH.toDouble()


        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация АВЭМ4-01-ОВ...")
            cm.startPoll(CommunicationModel.DeviceID.PA13, Avem7Model.AMPERAGE) { value ->
                amperageOV = abs(value.toDouble() * ktrAmperageOV)
                model.data.iOV.value = amperageOV.autoformat()
                if (!avemIov.isResponding && isExperimentRunning) cause = "АВЭМ4-01 не отвечает"
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация АВЭМ4-03-ОВ...")
            cm.startPoll(CommunicationModel.DeviceID.PV23, Avem4Model.RMS) { value ->
                voltageOV = abs(value.toDouble())
                model.data.uOV.value = voltageOV.autoformat()
                if (!avemUov.isResponding && isExperimentRunning) cause = "АВЭМ4-03 не отвечает"
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация АВЭМ4-01-ОЯ...")
            cm.startPoll(CommunicationModel.DeviceID.PA15, Avem7Model.AMPERAGE) { value ->
                amperageOY = abs(value.toDouble() * ktrAmperageOY)
                model.data.iOY.value = amperageOY.autoformat()
                if (!avemIoy.isResponding && isExperimentRunning) cause = "АВЭМ4-01 не отвечает"
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация АВЭМ4-03-ОЯ...")
            cm.startPoll(CommunicationModel.DeviceID.PV25, Avem4Model.RMS) { value ->
                voltageOY = abs(value.toDouble())
                model.data.uOY.value = voltageOY.autoformat()
                model.data.p.value = (voltageOY * amperageOY).autoformat()
                if (!avemUoy.isResponding && isExperimentRunning) cause = "АВЭМ4-03 не отвечает"
            }
        }

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
            if (voltageLatr < 5) {
                pr102.arn(true)
                pr102.ov_oi(true)
            } else {
                cause = "АРН не вышел в нулевое положение"
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Регулировка до номинальной частоты вращения")
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Регулировка до номинальной частоты вращения завершена")
        }

        if (isExperimentRunning) {
            if (objectModel!!.uVIU.toDoubleOrNull() != null) {
                appendMessageToLog(LogTag.DEBUG, "Подъем напряжения обмотки возбуждения и обмотки якоря")
                var step = 0.0
                for (i in 0..9) {
                    step += 0.1
                    appendMessageToLog(LogTag.ERROR, "Ступень ${step * 10}% от номинала")
                    voltageRegulation(voltageOVSet * step)
                    voltageRegulationTVN(voltageOYSet * step, 750, 1000)
                }
                appendMessageToLog(LogTag.MESSAGE, "Подъем напряжения обмотки возбуждения и обмотки якоря завершен")
            } else cause = "ошибка задания напряжения"
        }

        var timer = setTime
        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Выдержка $setTime секунд")
            while (isExperimentRunning && timer > 0) {
                timer -= 0.1
                if (timer >= 0) {
                    model.data.timeExp.value = timer.autoformat()
                }
                sleep(100)
            }
        }

        saveData()

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
        protocolModel.nResult = model.data.result.value
        restoreData()
    }

    private fun voltageRegulation(volt: Double) {
        var timer = 0L
        val slow = 50.0
        val fast = 250.0
        var speedPerc = 35f
        var timePulsePerc = 20f
        val up = 220f
        val down = 1f
        var direction: Float
        timer = System.currentTimeMillis()
        while (abs(voltageOV - volt) > fast && isExperimentRunning) {
            if (voltageOV < volt) {
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
        while (abs(voltageOV - volt) > slow && isExperimentRunning) {
            if (voltageOV < volt) {
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
        while (abs(voltageOV - volt) > 5 && isExperimentRunning) {
            if (voltageOV < volt) {
                direction = up
                timePulsePerc = 85f
            } else {
                direction = down
                timePulsePerc = 85f
            }
            if (System.currentTimeMillis() - timer > 60000) cause = "Превышено время регулирования"
            latr.startUpLATRPulse(direction, false, timePulsePerc)
        }
        latr.stopLATR()
    }

    private fun voltageRegulationForSpeed(speed: Double) {
        var timer = 0L
        val slow = 50.0
        val fast = 250.0
        var speedPerc = 35f
        var timePulsePerc = 20f
        val up = 220f
        val down = 1f
        var direction: Float
        timer = System.currentTimeMillis()
        while (abs(rotateSpeed - speed) > fast && isExperimentRunning) {
            if (rotateSpeed < speed) {
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
        while (abs(rotateSpeed - speed) > slow && isExperimentRunning) {
            if (rotateSpeed < speed) {
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
        while (abs(rotateSpeed - speed) > 10 && isExperimentRunning) {
            if (rotateSpeed < speed) {
                direction = up
                timePulsePerc = 85f
            } else {
                direction = down
                timePulsePerc = 85f
            }
            if (System.currentTimeMillis() - timer > 60000) cause = "Превышено время регулирования"
            latr.startUpLATRPulse(direction, false, timePulsePerc)
        }
        latr.stopLATR()
    }

    private fun voltageRegulationTVN(volt: Double, coarseSleep: Long, fineSleep: Long) {
        var start = 0.0
        val slow = 20.0
        val fast = 100.0

        var timer = System.currentTimeMillis()
        while (abs(voltageOY - volt) > fast && isExperimentRunning) {
            if (voltageOY < volt) {
                start += 0.05
                pr102.setTVN(start)
            } else {
                start -= 0.05
                pr102.setTVN(start)
            }
            if (System.currentTimeMillis() - timer > 90000) cause = "Превышено время регулирования"
            sleep(coarseSleep)
        }

        timer = System.currentTimeMillis()
        while (abs(voltageOY - volt) > slow && isExperimentRunning) {
            if (voltageOY < volt) {
                start += 0.03
                pr102.setTVN(start)
            } else {
                start -= 0.03
                pr102.setTVN(start)
            }
            if (System.currentTimeMillis() - timer > 90000) cause = "Превышено время регулирования"
            sleep(fineSleep)
        }

        timer = System.currentTimeMillis()
        while (abs(voltageOY - volt) > 5 && isExperimentRunning) {
            if (voltageOY < volt) {
                start += 0.01
                pr102.setTVN(start)
            } else {
                start -= 0.01
                pr102.setTVN(start)
            }
            if (System.currentTimeMillis() - timer > 90000) cause = "Превышено время регулирования"
            sleep(200)
        }

    }

    override fun stop() {
        cause = "Отменено оператором"
        model.data.result.value = "Прервано"
    }

    private fun saveData() {
        protocolModel.nSpeed = model.data.n.value
        protocolModel.nF = model.data.f.value
        protocolModel.nResult = model.data.result.value
    }

    private fun restoreData() {
        model.data.n.value = protocolModel.nSpeed
        model.data.f.value = protocolModel.nF
    }
}