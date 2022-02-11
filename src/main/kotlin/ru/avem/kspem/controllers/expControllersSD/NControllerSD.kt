package ru.avem.kspem.controllers.expControllersSD

import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.avem.avem4.Avem4Model
import ru.avem.kspem.communication.model.devices.avem.latr.LatrModel
import ru.avem.kspem.communication.model.devices.delta.DeltaModel
import ru.avem.kspem.communication.model.devices.pm130.PM130Model
import ru.avem.kspem.communication.model.devices.th01.TH01Model
import ru.avem.kspem.communication.model.devices.trm202.TRM202Model
import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.data.protocolModel
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.utils.sleep
import ru.avem.kspem.view.expViews.expViewsSD.NViewSD
import ru.avem.stand.utils.autoformat
import kotlin.concurrent.thread
import kotlin.math.abs

class NControllerSD : CustomController() {
    override val model: NViewSD by inject()
    override val name = model.name
    var deltaStatus = 0
    private var setTime = 0.0
    var fDelta = 1
    private var ktrVoltage = 1.0
    private var ktrAmperage = 400 / 5
    var voltageDelta = 0.0
    var voltage = 0.0
    var amperage = 0.0
    var voltageLatr = 0.0
    var voltageSet = 0.0
    var voltageOVSet = 0.0

    @Volatile
    var ktrDelta = 1.0

    override fun start() {
        model.clearTables()
        super.start()
        // (value-4)*0.625

        fDelta = 1
        voltageSet = objectModel!!.uNom.toDouble()
        voltageOVSet = objectModel!!.uOV.toDouble()
        setTime = objectModel!!.timeHH.toDouble()


        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация АВЭМ4-03...")
            cm.startPoll(CommunicationModel.DeviceID.PV23, Avem4Model.RMS) { value ->
                voltage = value.toDouble()
                model.data.uOV.value = voltage.autoformat()
                if (!avemUov.isResponding && isExperimentRunning) cause = "АВЭМ4-03 не отвечает"
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация АВЭМ4-01...")
            cm.startPoll(CommunicationModel.DeviceID.PA13, Avem4Model.RMS) { value ->
                amperage = value.toDouble()
                model.data.iOV.value = amperage.autoformat()
                if (!avemIov.isResponding && isExperimentRunning) cause = "АВЭМ4-01 не отвечает"
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
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.U_BC_REGISTER) { value ->
                if (fDelta > 10 && value.toDouble() > 700 && isExperimentRunning) cause = "проверьте подключение ОИ"
                model.data.uBC.value = (value.toDouble() * ktrVoltage).autoformat()
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.U_CA_REGISTER) { value ->
                if (fDelta > 10 && value.toDouble() > 700 && isExperimentRunning) cause = "проверьте подключение ОИ"
                model.data.uCA.value = (value.toDouble() * ktrVoltage).autoformat()
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
                model.data.p.value = abs(value.toDouble() * ktrVoltage * ktrAmperage).autoformat()
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.COS_REGISTER) { value ->
                model.data.cos.value = abs(value.toDouble()).autoformat()
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.F_REGISTER) { value ->
                model.data.f.value = value.autoformat()
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
            if (voltageLatr < 5) {
                pr102.arn(true)
                pr102.ov_oi(true)
            } else {
                cause = "АРН не вышел в нулевое положение"
            }
        }

        thread(isDaemon = true) {
            if (isExperimentRunning) {
                if (objectModel!!.uVIU.toDoubleOrNull() != null) {
                    voltageRegulation(voltageOVSet)
                    appendMessageToLog(LogTag.MESSAGE, "Регулировка завершена")
                } else cause = "ошибка задания напряжения"
            }
        }

        if (isExperimentRunning) {
            delta.setObjectParamsRun()
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

        var timer = 5.0
        while (isExperimentRunning && timer > 0) {
            sleep(100)
            timer -= 0.1
            model.data.timeExp.value = abs(timer).autoformat()
        }

        if (isExperimentRunning) {
            voltageRegulation(voltageOVSet * 1.2)
        }

        timer = 120.0
        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Выдержка 120 секунд")
            while (isExperimentRunning && timer > 0) {
                timer -= 0.1
                if (timer >= 0) {
                    model.data.timeExp.value = timer.autoformat()
                }
                sleep(100)
            }
        }

        protocolModel.nUAB = model.data.uAB.value
        protocolModel.nUBC = model.data.uBC.value
        protocolModel.nUCA = model.data.uCA.value
        protocolModel.nIA = model.data.iA.value
        protocolModel.nIB = model.data.iB.value
        protocolModel.nIC = model.data.iC.value
        protocolModel.nSpeed = model.data.n.value
        protocolModel.nF = model.data.f.value

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
        protocolModel.nResult = model.data.result.value
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
        while (abs(voltage - volt) > 20 && isExperimentRunning) {
            if (voltage < volt) {
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

    private fun startRegulation() {
        val timer = System.currentTimeMillis()
        appendMessageToLog(LogTag.MESSAGE, "Разгон двигателя")
        while (isExperimentRunning && fDelta < 50) {
            delta.setObjectF(++fDelta)
            sleep(1000)
            if (System.currentTimeMillis() - timer > 60000) cause = "превышено время регулирования"
        }
    }

    private fun startRegulationN() {
        val timer = System.currentTimeMillis()
        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Подъем 60 Гц")
        }
        while (isExperimentRunning && fDelta < 60) {
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
            if (System.currentTimeMillis() - timer > 90000) cause = "превышено время регулирования"
        }
    }

    override fun stop() {
        cause = "Отменено оператором"
        model.data.result.value = "Прервано"
    }

    private fun saveData() {
        protocolModel.nUAB = model.data.uAB.value
        protocolModel.nUBC = model.data.uOV.value
        protocolModel.nUCA = model.data.uCA.value
        protocolModel.nIA = model.data.iOV.value
        protocolModel.nIB = model.data.iA.value
        protocolModel.nIC = model.data.iC.value
        protocolModel.nSpeed = model.data.n.value
        protocolModel.nF = model.data.f.value
        protocolModel.nResult = model.data.result.value
    }

    private fun restoreData() {
        model.data.uAB.value = protocolModel.nUAB
        model.data.uOV.value = protocolModel.nUBC
        model.data.uCA.value = protocolModel.nUCA
        model.data.iOV.value = protocolModel.nIA
        model.data.iA.value = protocolModel.nIB
        model.data.iC.value = protocolModel.nIC
        model.data.n.value = protocolModel.nSpeed
        model.data.f.value = protocolModel.nF
    }
}