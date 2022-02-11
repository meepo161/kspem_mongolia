package ru.avem.kspem.controllers.expControllers

import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.delta.DeltaModel
import ru.avem.kspem.communication.model.devices.pm130.PM130Model
import ru.avem.kspem.communication.model.devices.th01.TH01Model
import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.utils.sleep
import ru.avem.kspem.view.expViews.H_HHView
import ru.avem.stand.utils.autoformat
import tornadofx.runLater
import kotlin.math.abs

class H_HHController : CustomController() {
    override val model: H_HHView by inject()
    override val name = model.name
    var deltaStatus = 0
    private var setTime = 0.0
    var fDelta = 1
    private var ktrVoltage = 1.0
    private var ktrAmperage = 1.0

    @Volatile
    var voltageDelta = 0.0
    var koefDelta = 0.0
    var ktrDelta = 1.0


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
                model.data.uAB.value = (value.toDouble() * ktrVoltage).autoformat()
                if (!pm135.isResponding && isExperimentRunning) cause = "PM135 не отвечает"
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.U_BC_REGISTER) { value ->
                model.data.uBC.value = (value.toDouble() * ktrVoltage).autoformat()
            }
            cm.startPoll(CommunicationModel.DeviceID.PAV41, PM130Model.U_CA_REGISTER) { value ->
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
                model.data.power.value = abs(value.toDouble() * ktrAmperage * ktrVoltage).autoformat()
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
            var timeDelta = 90
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

//        if (isExperimentRunning) {
//            if (model.data.iA.value.toDouble() < 100 / ktrDelta) {
//                appendMessageToLog(LogTag.DEBUG, "Токовая ступень 160А")
//                curStep = I_MIN
//                pr102.iMin(true)
//                pr102.iMax(false)
//            } else {
//                appendMessageToLog(LogTag.DEBUG, "Токовая ступень 1000А")
//            }
//        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Выдержка 5 секунд")
            var timer = 5.0
            while (isExperimentRunning && timer > 0) {
                sleep(100)
                timer -= 0.1
            }
        }

        if (isExperimentRunning) {
//            startVoltage()
        }

        if (isExperimentRunning) {
            startMeasuring()
        }

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
//        protocolModel.h_hhResult = model.data.result.value
    }

//    private fun startScheme1() {
//        appendMessageToLog(LogTag.DEBUG, "Сборка схемы для 380/400 В")
//        ktrDelta = 400.0 / 1500.0 * 1.73
//        pr102.iMax(true)
//        pr102.tv1500(true)
//        voltageDelta = objectModel!!.uN.toDouble() * ktrDelta
//        appendMessageToLog(LogTag.DEBUG, voltageDelta.toString())
//        if (isExperimentRunning) {
//            delta.setObjectParamsRun(1, voltageDelta)
//        }
//    }

//
//    private fun startVoltage() {
//        val timer = System.currentTimeMillis()
//        appendMessageToLog(LogTag.DEBUG, "Подъем напряжения до 1.3 Uн")
//        koefDelta = 1.0
//        while (isExperimentRunning && koefDelta < 1.3) {
//            koefDelta += 0.02
//            delta.setObjectURun(voltageDelta * koefDelta)
//            sleep(500)
//            if (System.currentTimeMillis() - timer > 30000) cause = "превышено время регулирования"
//        }
//    }
//
//    private fun stopVoltage(goTo: Double) {
//        val timer = System.currentTimeMillis()
//        appendMessageToLog(LogTag.DEBUG, "Уменьшение напряжения до $goTo Uн")
//        while (isExperimentRunning && koefDelta > goTo) {
//            koefDelta -= 0.02
//            delta.setObjectURun(voltageDelta * koefDelta)
//            sleep(500)
//            if (System.currentTimeMillis() - timer > 30000) cause = "превышено время регулирования"
//        }
//
//        if (isExperimentRunning) {
//            appendMessageToLog(LogTag.MESSAGE, "Выдержка 5 секунд")
//            var timer = 5.0
//            while (isExperimentRunning && timer > 0) {
//                sleep(100)
//                timer -= 0.1
//            }
//        }
//    }

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

//    private fun calibrateVoltage() {
//        appendMessageToLog(LogTag.DEBUG, "Проверка выставленного напряжения")
//        val uAvg =
//            (model.data.uAB.value.toDouble() + model.data.uBC.value.toDouble() + model.data.uCA.value.toDouble()) / 3.0
//        val kCalibr = objectModel!!.uNom.toDouble() / uAvg
//        if (kCalibr < 1.3 && kCalibr > 0.8) {
//            voltageDelta *= kCalibr
//            delta.setObjectURun(voltageDelta)
//        } else {
//            appendMessageToLog(LogTag.DEBUG, "Коэффициент $kCalibr")
//        }
//    }

    override fun stop() {
        cause = "Отменено оператором"
        model.data.result.value = "Прервано"
    }

    fun startMeasuring() {

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
//            protocolModel.h_hhUAB1 = model.data.uAB.value
//            protocolModel.h_hhUBC1 = model.data.uBC.value
//            protocolModel.h_hhUCA1 = model.data.uCA.value
//            protocolModel.h_hhIA1 = model.data.iA.value
//            protocolModel.h_hhIB1 = model.data.iB.value
//            protocolModel.h_hhIC1 = model.data.iC.value
//            protocolModel.h_hhP1 = model.data.power.value

            runLater {
                model.table.scrollTo(0)
                model.table.selectionModel.select(0)
                model.h_hhTablePoints[0].uAB.value = model.data.uAB.value
                model.h_hhTablePoints[0].uBC.value = model.data.uBC.value
                model.h_hhTablePoints[0].uCA.value = model.data.uCA.value
                model.h_hhTablePoints[0].iA.value = model.data.iA.value
                model.h_hhTablePoints[0].iB.value = model.data.iB.value
                model.h_hhTablePoints[0].iC.value = model.data.iC.value
                model.h_hhTablePoints[0].power.value = model.data.power.value
            }
        }
//
//        if (isExperimentRunning) {
//            stopVoltage(1.2)
//        }


        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Фиксация значений")
//            protocolModel.h_hhUAB2 = model.data.uAB.value
//            protocolModel.h_hhUBC2 = model.data.uBC.value
//            protocolModel.h_hhUCA2 = model.data.uCA.value
//            protocolModel.h_hhIA2 = model.data.iA.value
//            protocolModel.h_hhIB2 = model.data.iB.value
//            protocolModel.h_hhIC2 = model.data.iC.value
//            protocolModel.h_hhP2 = model.data.power.value

            runLater {
                model.table.scrollTo(1)
                model.table.selectionModel.select(1)
                model.h_hhTablePoints[1].uAB.value = model.data.uAB.value
                model.h_hhTablePoints[1].uBC.value = model.data.uBC.value
                model.h_hhTablePoints[1].uCA.value = model.data.uCA.value
                model.h_hhTablePoints[1].iA.value = model.data.iA.value
                model.h_hhTablePoints[1].iB.value = model.data.iB.value
                model.h_hhTablePoints[1].iC.value = model.data.iC.value
                model.h_hhTablePoints[1].power.value = model.data.power.value
            }
        }

        if (isExperimentRunning) {
//            stopVoltage(1.1)
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Фиксация значений")
//            protocolModel.h_hhUAB3 = model.data.uAB.value
//            protocolModel.h_hhUBC3 = model.data.uBC.value
//            protocolModel.h_hhUCA3 = model.data.uCA.value
//            protocolModel.h_hhIA3 = model.data.iA.value
//            protocolModel.h_hhIB3 = model.data.iB.value
//            protocolModel.h_hhIC3 = model.data.iC.value
//            protocolModel.h_hhP3 = model.data.power.value

            runLater {
                model.table.scrollTo(2)
                model.table.selectionModel.select(2)
                model.h_hhTablePoints[2].uAB.value = model.data.uAB.value
                model.h_hhTablePoints[2].uBC.value = model.data.uBC.value
                model.h_hhTablePoints[2].uCA.value = model.data.uCA.value
                model.h_hhTablePoints[2].iA.value = model.data.iA.value
                model.h_hhTablePoints[2].iB.value = model.data.iB.value
                model.h_hhTablePoints[2].iC.value = model.data.iC.value
                model.h_hhTablePoints[2].power.value = model.data.power.value
            }
        }

        if (isExperimentRunning) {
//            stopVoltage(1.0)
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Фиксация значений")
//            protocolModel.h_hhUAB4 = model.data.uAB.value
//            protocolModel.h_hhUBC4 = model.data.uBC.value
//            protocolModel.h_hhUCA4 = model.data.uCA.value
//            protocolModel.h_hhIA4 = model.data.iA.value
//            protocolModel.h_hhIB4 = model.data.iB.value
//            protocolModel.h_hhIC4 = model.data.iC.value
//            protocolModel.h_hhP4 = model.data.power.value

            runLater {
                model.table.scrollTo(3)
                model.table.selectionModel.select(3)
                model.h_hhTablePoints[3].uAB.value = model.data.uAB.value
                model.h_hhTablePoints[3].uBC.value = model.data.uBC.value
                model.h_hhTablePoints[3].uCA.value = model.data.uCA.value
                model.h_hhTablePoints[3].iA.value = model.data.iA.value
                model.h_hhTablePoints[3].iB.value = model.data.iB.value
                model.h_hhTablePoints[3].iC.value = model.data.iC.value
                model.h_hhTablePoints[3].power.value = model.data.power.value
            }
        }

        if (isExperimentRunning) {
//            stopVoltage(0.9)
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Фиксация значений")
//            protocolModel.h_hhUAB5 = model.data.uAB.value
//            protocolModel.h_hhUBC5 = model.data.uBC.value
//            protocolModel.h_hhUCA5 = model.data.uCA.value
//            protocolModel.h_hhIA5 = model.data.iA.value
//            protocolModel.h_hhIB5 = model.data.iB.value
//            protocolModel.h_hhIC5 = model.data.iC.value
//            protocolModel.h_hhP5 = model.data.power.value

            runLater {
                model.table.scrollTo(4)
                model.table.selectionModel.select(4)
                model.h_hhTablePoints[4].uAB.value = model.data.uAB.value
                model.h_hhTablePoints[4].uBC.value = model.data.uBC.value
                model.h_hhTablePoints[4].uCA.value = model.data.uCA.value
                model.h_hhTablePoints[4].iA.value = model.data.iA.value
                model.h_hhTablePoints[4].iB.value = model.data.iB.value
                model.h_hhTablePoints[4].iC.value = model.data.iC.value
                model.h_hhTablePoints[4].power.value = model.data.power.value
            }
        }

        if (isExperimentRunning) {
//            stopVoltage(0.8)
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Фиксация значений")
//            protocolModel.h_hhUAB6 = model.data.uAB.value
//            protocolModel.h_hhUBC6 = model.data.uBC.value
//            protocolModel.h_hhUCA6 = model.data.uCA.value
//            protocolModel.h_hhIA6 = model.data.iA.value
//            protocolModel.h_hhIB6 = model.data.iB.value
//            protocolModel.h_hhIC6 = model.data.iC.value
//            protocolModel.h_hhP6 = model.data.power.value

            runLater {
                model.table.scrollTo(5)
                model.table.selectionModel.select(5)
                model.h_hhTablePoints[5].uAB.value = model.data.uAB.value
                model.h_hhTablePoints[5].uBC.value = model.data.uBC.value
                model.h_hhTablePoints[5].uCA.value = model.data.uCA.value
                model.h_hhTablePoints[5].iA.value = model.data.iA.value
                model.h_hhTablePoints[5].iB.value = model.data.iB.value
                model.h_hhTablePoints[5].iC.value = model.data.iC.value
                model.h_hhTablePoints[5].power.value = model.data.power.value
            }
        }

        if (isExperimentRunning) {
//            stopVoltage(0.7)
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Фиксация значений")
//            protocolModel.h_hhUAB7 = model.data.uAB.value
//            protocolModel.h_hhUBC7 = model.data.uBC.value
//            protocolModel.h_hhUCA7 = model.data.uCA.value
//            protocolModel.h_hhIA7 = model.data.iA.value
//            protocolModel.h_hhIB7 = model.data.iB.value
//            protocolModel.h_hhIC7 = model.data.iC.value
//            protocolModel.h_hhP7 = model.data.power.value

            runLater {
                model.table.scrollTo(6)
                model.table.selectionModel.select(6)
                model.h_hhTablePoints[6].uAB.value = model.data.uAB.value
                model.h_hhTablePoints[6].uBC.value = model.data.uBC.value
                model.h_hhTablePoints[6].uCA.value = model.data.uCA.value
                model.h_hhTablePoints[6].iA.value = model.data.iA.value
                model.h_hhTablePoints[6].iB.value = model.data.iB.value
                model.h_hhTablePoints[6].iC.value = model.data.iC.value
                model.h_hhTablePoints[6].power.value = model.data.power.value
            }
        }

        if (isExperimentRunning) {
//            stopVoltage(0.6)
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Фиксация значений")
//            protocolModel.h_hhUAB8 = model.data.uAB.value
//            protocolModel.h_hhUBC8 = model.data.uBC.value
//            protocolModel.h_hhUCA8 = model.data.uCA.value
//            protocolModel.h_hhIA8 = model.data.iA.value
//            protocolModel.h_hhIB8 = model.data.iB.value
//            protocolModel.h_hhIC8 = model.data.iC.value
//            protocolModel.h_hhP8 = model.data.power.value

            runLater {
                model.table.scrollTo(7)
                model.table.selectionModel.select(7)
                model.h_hhTablePoints[7].uAB.value = model.data.uAB.value
                model.h_hhTablePoints[7].uBC.value = model.data.uBC.value
                model.h_hhTablePoints[7].uCA.value = model.data.uCA.value
                model.h_hhTablePoints[7].iA.value = model.data.iA.value
                model.h_hhTablePoints[7].iB.value = model.data.iB.value
                model.h_hhTablePoints[7].iC.value = model.data.iC.value
                model.h_hhTablePoints[7].power.value = model.data.power.value
            }
        }

        if (isExperimentRunning) {
//            stopVoltage(0.5)
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "Фиксация значений")
//            protocolModel.h_hhUAB9 = model.data.uAB.value
//            protocolModel.h_hhUBC9 = model.data.uBC.value
//            protocolModel.h_hhUCA9 = model.data.uCA.value
//            protocolModel.h_hhIA9 = model.data.iA.value
//            protocolModel.h_hhIB9 = model.data.iB.value
//            protocolModel.h_hhIC9 = model.data.iC.value
//            protocolModel.h_hhP9 = model.data.power.value

            runLater {
                model.table.scrollTo(8)
                model.table.selectionModel.select(8)
                model.h_hhTablePoints[8].uAB.value = model.data.uAB.value
                model.h_hhTablePoints[8].uBC.value = model.data.uBC.value
                model.h_hhTablePoints[8].uCA.value = model.data.uCA.value
                model.h_hhTablePoints[8].iA.value = model.data.iA.value
                model.h_hhTablePoints[8].iB.value = model.data.iB.value
                model.h_hhTablePoints[8].iC.value = model.data.iC.value
                model.h_hhTablePoints[8].power.value = model.data.power.value
            }
        }
    }
}