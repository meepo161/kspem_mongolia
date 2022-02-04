package ru.avem.kspem.controllers.expControllers

import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.delta.DeltaModel
import ru.avem.kspem.communication.model.devices.pm130.PM130Model
import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.data.protocolModel
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.utils.sleep
import ru.avem.kspem.view.expViews.MVView
import ru.avem.stand.utils.autoformat
import kotlin.math.abs

class MVController : CustomController() {
    override val model: MVView by inject()
    override val name = model.name
    var deltaStatus = 0
    private var setTime = 0.0
    var fDelta = 1

    @Volatile
    var ktrDelta = 1.0


    @Volatile
    var voltageDelta = 0.0

    var koefDelta = 0.0
    private var ktrVoltage = 1.0
    private var ktrAmperage = 1.0

    override fun start() {
        model.clearTables()
        super.start()
        // (value-4)*0.625

        voltageDelta = 1.0
        fDelta = 1
        ktrDelta = 1.0
        koefDelta = 1.0
        setTime = objectModel!!.timeMVZ.toDouble()

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация PM135...")
            pm135.checkResponsibility()
            sleep(1000)

            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.U_AB_REGISTER) { value ->
                if ( fDelta > 10 && value.toDouble() > 710 && isExperimentRunning) cause = "проверьте подключение ОИ"
                model.data.uAB.value = (value.toDouble() * ktrVoltage).autoformat()
                if (!pm135.isResponding && isExperimentRunning) cause = "PM135 не отвечает"
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.U_BC_REGISTER) { value ->
                if ( fDelta > 10 && value.toDouble() > 710 && isExperimentRunning) cause = "проверьте подключение ОИ"
                model.data.uBC.value = (value.toDouble() * ktrVoltage).autoformat()
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.U_CA_REGISTER) { value ->
                if ( fDelta > 10 && value.toDouble() > 710 && isExperimentRunning) cause = "проверьте подключение ОИ"
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
//            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.COS_REGISTER) { value ->
//                model.data.cos.value = abs(value.toDouble()).autoformat()
//            }
//            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.F_REGISTER) { value ->
//                model.data.f.value = value.autoformat()
//            }
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
            var timer = 5.0
            while (isExperimentRunning && timer > 0) {
                sleep(100)
                timer -= 0.1
            }
        }


        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Выдержка 5 секунд")
            var timer = 5.0
            while (isExperimentRunning && timer > 0) {
                sleep(100)
                timer -= 0.1
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Фиксация значений")
            model.data.uAB1.value = model.data.uAB.value
            model.data.uBC1.value = model.data.uBC.value
            model.data.uCA1.value = model.data.uCA.value
            model.data.iA1.value = model.data.iA.value
            model.data.iB1.value = model.data.iB.value
            model.data.iC1.value = model.data.iC.value
        }

            if (isExperimentRunning) {
                startVoltage()
            }

            if (isExperimentRunning) {
                appendMessageToLog(LogTag.MESSAGE, "Выдержка $setTime секунд")
                var timer = setTime
                while (isExperimentRunning && timer > 0) {
                    sleep(100)
                    timer -= 0.1
                    model.data.timeExp.value = abs(timer).autoformat()
                }
            }

            if (isExperimentRunning) {
                stopVoltage()
            }

            if (isExperimentRunning) {
                appendMessageToLog(LogTag.MESSAGE, "Выдержка 5 секунд")
                var timer = 5.0
                while (isExperimentRunning && timer > 0) {
                    sleep(100)
                    timer -= 0.1
                }
            }

            if (isExperimentRunning) {
                appendMessageToLog(LogTag.DEBUG, "Фиксация значений")
                model.data.uAB2.value = model.data.uAB.value
                model.data.uBC2.value = model.data.uBC.value
                model.data.uCA2.value = model.data.uCA.value
                model.data.iA2.value = model.data.iA.value
                model.data.iB2.value = model.data.iB.value
                model.data.iC2.value = model.data.iC.value
            }

        stopRegulation()

        delta.stopObject()

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Выключение ПЧ")
            try {
                val iBefore = (model.data.iA1.value.toString().toDouble() +
                        model.data.iB1.value.toString().toDouble() +
                        model.data.iC1.value.toString().toDouble())
                val iAfter = (model.data.iA2.value.toString().toDouble() +
                        model.data.iB2.value.toString().toDouble() +
                        model.data.iC2.value.toString().toDouble())
                model.data.deviation.value = abs(((iAfter - iBefore) / iBefore * 100)).autoformat()
            } catch (e: Exception) {
                println(e)
            }
        }

        var timerDelta = 10
        while (timerDelta > 0) {
            sleep(100)
            timerDelta--
        }

        finalizeExperiment()
        when (cause) {
            "" -> {
                if (!model.data.deviation.value.isNullOrEmpty()) {
                    if (model.data.deviation.value.toString().toDouble() > 3) {
                        model.data.result.value = "Неуспешно"
                        appendMessageToLog(LogTag.ERROR, "Отклонение более 3%")
                    } else {
                        model.data.result.value = "Успешно"
                        appendMessageToLog(LogTag.MESSAGE, "Испытание завершено успешно")
                    }
                } else {
                    model.data.result.value = "Неуспешно"
                }
            }
            else -> {
                model.data.result.value = "Прервано"
                appendMessageToLog(LogTag.ERROR, "Испытание прервано по причине: $cause")
            }
        }
        saveData()
        restoreData()
    }

    private fun startVoltage() {
        val timer = System.currentTimeMillis()
        appendMessageToLog(LogTag.MESSAGE, "Подъем напряжения")
        while (isExperimentRunning && koefDelta < 1.3) {
            koefDelta += 0.02
            delta.setObjectURun(voltageDelta * koefDelta)
            sleep(500)
            if (System.currentTimeMillis() - timer > 30000) cause = "превышено время регулирования"
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Проверка выставленного напряжения")
            if (isExperimentRunning) {
                var timer2 = 5.0
                while (isExperimentRunning && timer2 > 0) {
                    sleep(100)
                    timer2 -= 0.1
                }
            }
            val uAvg =
                (model.data.uAB.value.toDouble() + model.data.uBC.value.toDouble() + model.data.uCA.value.toDouble()) / 3.0
            val kCalibr = objectModel!!.uN.toDouble() * 1.3 / uAvg
            if (isExperimentRunning) {
                if (kCalibr < 1.2 && kCalibr > 0.9) {
                    koefDelta *= kCalibr
                    delta.setObjectURun(voltageDelta * koefDelta)
                } else {
                    appendMessageToLog(LogTag.DEBUG, "Коэффициент $kCalibr")
                }
            }
        }
    }

    private fun stopVoltage() {
        val timer = System.currentTimeMillis()
        appendMessageToLog(LogTag.MESSAGE, "Уменьшение напряжения")
        while (isExperimentRunning && koefDelta > 1.0) {
            koefDelta -= 0.02
            delta.setObjectURun(voltageDelta * koefDelta)
            sleep(500)
            if (System.currentTimeMillis() - timer > 30000) cause = "превышено время регулирования"
        }
        if (isExperimentRunning) {
            koefDelta = 1.0
            delta.setObjectURun(voltageDelta)
        }
    }

    private fun calibrateVoltage() {
        appendMessageToLog(LogTag.DEBUG, "Проверка выставленного напряжения")
        val uAvg =
            (model.data.uAB.value.toDouble() + model.data.uBC.value.toDouble() + model.data.uCA.value.toDouble()) / 3.0
        val kCalibr = objectModel!!.uN.toDouble() / uAvg
        if (kCalibr < 1.3 && kCalibr > 0.9) {
            voltageDelta *= kCalibr
            delta.setObjectURun(voltageDelta)
        } else {
            appendMessageToLog(LogTag.DEBUG, "Коэффициент $kCalibr")
        }
    }

    private fun startRegulation() {
        val timer = System.currentTimeMillis()
        appendMessageToLog(LogTag.DEBUG, "Разгон двигателя")
        while (isExperimentRunning && fDelta < 50) {
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

    fun saveData() {
        protocolModel.mvUAB1 = model.data.uAB1.value
        protocolModel.mvUBC1 = model.data.uBC1.value
        protocolModel.mvUCA1 = model.data.uCA1.value
        protocolModel.mvIA1 = model.data.iA1.value
        protocolModel.mvIB1 = model.data.iB1.value
        protocolModel.mvIC1 = model.data.iC1.value
        protocolModel.mvUAB2 = model.data.uAB2.value
        protocolModel.mvUBC2 = model.data.uBC2.value
        protocolModel.mvUCA2 = model.data.uCA2.value
        protocolModel.mvIA2 = model.data.iA2.value
        protocolModel.mvIB2 = model.data.iB2.value
        protocolModel.mvIC2 = model.data.iC2.value
        protocolModel.mvDeviation = model.data.deviation.value
        protocolModel.mvResult = model.data.result.value
    }

    fun restoreData() {
        model.data.uAB1.value = protocolModel.mvUAB1
        model.data.uBC1.value = protocolModel.mvUBC1
        model.data.uCA1.value = protocolModel.mvUCA1
        model.data.iA1.value = protocolModel.mvIA1
        model.data.iB1.value = protocolModel.mvIB1
        model.data.iC1.value = protocolModel.mvIC1
        model.data.uAB2.value = protocolModel.mvUAB2
        model.data.uBC2.value = protocolModel.mvUBC2
        model.data.uCA2.value = protocolModel.mvUCA2
        model.data.iA2.value = protocolModel.mvIA2
        model.data.iB2.value = protocolModel.mvIB2
        model.data.iC2.value = protocolModel.mvIC2
    }
}