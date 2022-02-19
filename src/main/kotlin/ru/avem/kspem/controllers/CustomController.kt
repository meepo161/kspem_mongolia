package ru.avem.kspem.controllers

import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.text.Text
import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.avem.avem4.Avem4
import ru.avem.kspem.communication.model.devices.avem.avem7.Avem7
import ru.avem.kspem.communication.model.devices.avem.latr.LatrController
import ru.avem.kspem.communication.model.devices.cs02021.CS02021
import ru.avem.kspem.communication.model.devices.delta.Delta
import ru.avem.kspem.communication.model.devices.owen.pr.OwenPr
import ru.avem.kspem.communication.model.devices.owen.pr.OwenPrModel
import ru.avem.kspem.communication.model.devices.pm130.PM130
import ru.avem.kspem.communication.model.devices.th01.TH01
import ru.avem.kspem.communication.model.devices.trm202.TRM202
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.utils.sleep
import ru.avem.kspem.view.ExpView
import ru.avem.stand.modules.r.communication.model.devices.avem.ikas.IKAS8
import tornadofx.*
import tornadofx.controlsfx.infoNotification
import java.io.File
import java.text.SimpleDateFormat
import kotlin.concurrent.thread
import kotlin.experimental.and

abstract class CustomController() : Component(), ScopedInstance {
    val pr102 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.DD2_1) as OwenPr

    val avemUoy = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.PV25) as Avem4
    val avemIoy = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.PA15) as Avem7

    val avemUov = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.PV23) as Avem4
    val avemIov = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.PA13) as Avem7

    val avemUvv = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.PV24) as Avem4

    val pm135 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.PAV41) as PM130
    val ikas = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.PR61) as IKAS8
    val th01 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.PC71) as TH01
    val trm202 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.PS81) as TRM202
    val cs02 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.PR65) as CS02021

    //    val gpt = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.PRV89) as GPT
    val delta = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.UZ91) as Delta
    val latr = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.GV240) as LatrController
    val cm = CommunicationModel

    val controller: MainViewController by inject()
    abstract val name: String
    abstract val model: View
    val view = find<ExpView>()
    var isStartPressed = false
    var isStopPressed = false
    var doorZone = false
    var doorSCO = false
    var ikzOI = false
    var iViu = false
    var ikzIN = false
    var onGround = false
    var onVV = false
    var tempUNM = false
    var speedUNM = false
//    var ikzVIU = false

    @Volatile
    var isExperimentRunning = false

    @Volatile
    protected var cause: String = ""
        set(value) {
            if (value.isNotEmpty()) {
                isExperimentRunning = false
                if (!field.contains(value)) field += "${if (field != "") "/" else ""}$value"
            } else {
                field = value
            }
        }

    open fun appendMessageToLog(tag: LogTag, _msg: String) {
        val msg = Text("${SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())} | $_msg")
        msg.style {
            fill = when (tag) {
                LogTag.MESSAGE -> tag.c
                LogTag.ERROR -> tag.c
                LogTag.DEBUG -> tag.c
            }
            stroke = Color.BLACK
            effect = DropShadow()
        }

        Platform.runLater {
            view.vBoxLog.add(msg)
            File("cfg\\log.txt").appendText("\n${SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())} | $_msg")
        }
    }

    open fun start() {
        isExperimentRunning = true
        cause = ""
        disableButtons()

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "Инициализация ПР102...")
            initPR()
        }
    }

    open fun stop() {}

    fun disableButtons() {
        runLater {
            view.btnExit.isDisable = true
            view.btnNext.isDisable = true
            view.btnStart.isDisable = true
            view.btnStop.isDisable = false
        }
    }

    fun enableButtons() {
        runLater {
            view.btnExit.isDisable = false
            view.btnNext.isDisable = false
            view.btnStart.isDisable = false
            view.btnStop.isDisable = true
        }
    }


    fun initPR() {
        isStartPressed = false
        isStopPressed = false
        if (!pr102.isResponding) {
            cause = "ПР102 не отвечает"
        } else {
            cm.addWritingRegister(
                CommunicationModel.DeviceID.DD2_1,
                OwenPrModel.RESET_DOG,
                1.toShort()
            )
            sleep(100)
            pr102.initOwenPR()
            sleep(1000)
            cm.startPoll(CommunicationModel.DeviceID.DD2_1, OwenPrModel.INPUTS_REGISTER1) { value ->
                isStopPressed = value.toShort() and 1 > 0       // 1
                isStartPressed = value.toShort() and 2 > 0      // 2
                doorZone = value.toShort() and 4 > 0            // 3
                doorSCO = value.toShort() and 8 > 0            // 4
                ikzOI = value.toShort() and 32 > 0            // 5
                ikzIN = value.toShort() and 128 > 0              // 6
//                ikzVIU = value.toShort() and 64 > 0             // 7
//                tempDros = value.toShort() and 128 > 0        // 8
                if (doorZone) {
//                    cause = "Открыты двери зоны"
                }
                if (doorSCO) {
//                    cause = "Открыты двери ШСО"
                }
                if (ikzOI) {
                    cause = "сработала токовая защита ОИ"
                }
                if (ikzIN) {
                    cause = "сработала токовая защита ВХОД"
                }
                if (isStopPressed) {
                    cause = "отменено оператором"
                }
            }
            cm.startPoll(CommunicationModel.DeviceID.DD2_1, OwenPrModel.INPUTS_REGISTER2) { value ->
                iViu = value.toShort() and 1 > 0       // 1
//                = value.toShort() and 2 > 0      // 2
                onGround = value.toShort() and 4 > 0            // 3
                onVV = value.toShort() and 8 > 0            // 4
                tempUNM = value.toShort() and 16 > 0            // 5
                speedUNM = value.toShort() and 32 > 0              // 6
//                = value.toShort() and 64 > 0             // 7
//                = value.toShort() and 128 > 0        // 8
                if (iViu) {
                    cause = "сработала токовая защита ВИУ"
                }
                if (tempUNM) {
                    cause = "сработал температурный датчик УНМ"
                }
                if (speedUNM) {
//                    cause = "сработала токовая защита ВИУ"
                }
            }
            sleep(1000)
            thread(isDaemon = true) {
                while (isExperimentRunning) {
//                    if (!pr102.isResponding) cause = "потеряна связь с ПР102"
//                    if (isStopPressed) cause = "нажата кнопка <СТОП>"
//                    if (doorZone) cause = "открыты двери зоны"
//                    if (doorSCO) cause = "открыты двери ШСО"
//                    if (ikzOI) cause = "превышение тока ОИ"
//                    if (ikzIN) cause = "превышение входного тока"
//                    if (tempDros) cause = "превышение температуры дросселей"
                    sleep(100)
                }
            }
        }
    }

    fun initButtonPost() {
        appendMessageToLog(LogTag.DEBUG, "Инициализация кнопочного поста")
        var timer = 300
        runLater {
            infoNotification("Внимание", "Нажмите <ПУСК> на кнопочном посте", Pos.CENTER)
        }
        while (!isStartPressed) {
            sleep(100)
            timer--
            if (isStartPressed || !isExperimentRunning) break
            if (timer <= 0)
                cause = "не нажата кнопка <ПУСК>"
        }
    }

    fun finalizeExperiment() {
        isExperimentRunning = false
        cm.clearPollingRegisters()
        pr102.resetKMS()
        enableButtons()
    }

    fun loadExpModel() {
        view.vboxExp.children.clear()
        view.vboxExp.children.add(model.root)
    }

}