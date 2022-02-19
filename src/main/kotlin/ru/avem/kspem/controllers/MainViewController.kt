package ru.avem.kspem.controllers

import com.fazecast.jSerialComm.SerialPort
import javafx.application.Platform
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.text.Text
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.kspem.app.Main.Companion.isAppRunning
import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.delta.Delta
import ru.avem.kspem.communication.model.devices.owen.pr.OwenPr
import ru.avem.kspem.communication.model.devices.pm130.PM130
import ru.avem.kspem.data.*
import ru.avem.kspem.database.entities.Protocol
import ru.avem.kspem.utils.*
import ru.avem.kspem.view.ExpView
import ru.avem.kspem.view.MainView
import tornadofx.*
import tornadofx.controlsfx.infoNotification
import java.io.File
import java.text.SimpleDateFormat
import kotlin.concurrent.thread


class MainViewController : Controller() {
    private val pr102 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.DD2_1) as OwenPr
    val pm135 = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.PAV41) as PM130
    val delta = CommunicationModel.getDeviceById(CommunicationModel.DeviceID.UZ91) as Delta

    private val expView: ExpView by inject()
    private val mainView: MainView by inject()
    var position1 = ""
    var serialNum = ""
    var objectName = ""
    private var logBuffer: String? = null
    var expListRaw = mutableListOf<CustomController>()
    var expList = expListRaw.iterator()


    lateinit var currentExp: CustomController
    var listDPT = mutableListOf<CustomController>(mgrMPT, viu, ikasMPT, hhMPT, nMPT, loadMPT)
    var listGPT = mutableListOf<CustomController>(mgrMPT, viu, ikasMPT, nGPT)
    var listSD = mutableListOf<CustomController>(mgr, viu, ikas, nSD)
    var listSG = mutableListOf<CustomController>(mgr, viu, ikas, h_hhSG, nSG, kzSG)

    @Volatile
    var isExperimentRunning: Boolean = false

    @Volatile
    var isDevicesResponding = false

    private var cause: String = ""
        set(value) {
            if (value != "") {
                isExperimentRunning = false
//                view.buttonStart.isDisable = true
            }
            field = value
        }


    fun showAboutUs() {
        runLater {
            Toast.makeText("Версия ПО: 1.0.0\nВерсия БСУ: 1.0.0\nДата: 24.12.2021").show(Toast.ToastType.INFORMATION)
        }
    }

    private fun appendOneMessageToLog(tag: LogTag, message: String) {
        if (logBuffer == null || logBuffer != message) {
            logBuffer = message
            appendMessageToLog(tag, message)
        }
    }

    fun appendMessageToLog(tag: LogTag, _msg: String) {
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
            expView.vBoxLog.add(msg)
            File("cfg\\log.txt").appendText("\n${SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())} | $_msg")
        }
    }

//    fun start() {
//
//    }

//    fun repeat() {
//        expList = expListRaw.iterator()
//        currentExp = expList.next()
//        loadExp()
//    }

    fun next() {
        if (expList.hasNext()) {
            currentExp = expList.next()
            loadExp()
        } else {
            saveProtocol()
            clearProtocol()
            find<ExpView>().replaceWith<MainView>()
        }
    }

    fun initExp() {
        expListRaw = sortExp(expListRaw)
        expList = expListRaw.iterator()
        currentExp = expList.next()
        loadExp()
    }

    fun loadExp() {
        expView.vBoxLog.clear()
        currentExp.loadExpModel()
    }

    fun startExperiment() {
        thread(isDaemon = true) {
            currentExp.start()
        }
    }

    fun exit() {
        showTwoWayDialog(
            title = "Внимание!",
            text = "Текущий протокол будет сохранен и очищен",
            way1Title = "Подтвердить",
            way2Title = "Отменить",
            way1 = {
                saveProtocol()
                clearProtocol()
                find<ExpView>().replaceWith<MainView>()
            },
            way2 = {
            },
            currentWindow = primaryStage.scene.window
        )
    }

    fun stopExperiment() {
        currentExp.stop()
    }

//    private fun getNotRespondingDevicesString(): String {
//        return String.format(
//            "%s %s%s%s%s",
//            "Испытание прервано по причине: \nпотеряна связь с устройствами:",
//            if (pr102.isResponding) "" else "\nОвен ПР102 ",
//            if (pm130.isResponding) "" else "\nPM130 ",
//            if (latr.isResponding) "" else "\nАРН ",
//            if (avem.isResponding) "" else "\nАВЭМ7 "
//        )
//    }


//    fun causeSet(cause: String) {
//        this.cause = cause
//        if (cause.isNotEmpty()) {
//            isExperimentRunning = false
//            appendOneMessageToLog(LogTag.ERROR, "Отмена испытания")
//        }
//    }

    //    private fun finalizeExperiment() {
//        CommunicationModel.clearPollingRegisters()
////        pr102.resetKMS()
//        isExperimentRunning = false
//        runLater {
//            expView.btnStart.isDisable = false
//            expView.btnExit.isDisable = false
//        }
//    }
    private fun saveProtocol() {
//        transaction {
//            Protocol.new {
//                objectName = protocolModel.objectName
//                type = protocolModel.type
//                date = protocolModel.date
//                time = protocolModel.time
//                operator = protocolModel.operator
//                serial = protocolModel.serial
//                p2 = protocolModel.p2
//                uN = protocolModel.uN
//                iN = protocolModel.iN
//                nAsync = protocolModel.nAsync
//                kpd = protocolModel.kpd
//                cos = protocolModel.cos
//                scheme = protocolModel.scheme
//                //MGR//
//                mgrU = protocolModel.mgrU
//                mgrR15 = protocolModel.mgrR15
//                mgrR60 = protocolModel.mgrR60
//                mgrkABS = protocolModel.mgrkABS
//                mgrTemp = protocolModel.mgrTemp
//                mgrResult = protocolModel.mgrResult
//                //VIU//
//                viuU = protocolModel.viuU
//                viuI = protocolModel.viuI
//                viuTime = protocolModel.viuTime
//                viuResult = protocolModel.viuResult
//
//                //IKAS//
//                ikasR1 = protocolModel.ikasR1
//                ikasR2 = protocolModel.ikasR2
//                ikasR3 = protocolModel.ikasR3
//                ikasResult = protocolModel.ikasResult
//
//                //HH//
//                hhUAB = protocolModel.hhUAB
//                hhUBC = protocolModel.hhUBC
//                hhUCA = protocolModel.hhUCA
//                hhIA = protocolModel.hhIA
//                hhIB = protocolModel.hhIB
//                hhIC = protocolModel.hhIC
//                hhTempOI = protocolModel.hhTempOI
//                hhTempAmb = protocolModel.hhTempAmb
//                hhSpeed = protocolModel.hhSpeed
//                hhVibro1 = protocolModel.hhVibro1
//                hhVibro2 = protocolModel.hhVibro2
//                hhP1 = protocolModel.hhP1
//                hhCos = protocolModel.hhCos
//                hhTime = protocolModel.hhTime
//                hhResult = protocolModel.hhResult
//
//                //RUNNING//
//                runningUAB = protocolModel.runningUAB
//                runningUBC = protocolModel.runningUBC
//                runningUCA = protocolModel.runningUCA
//                runningIA = protocolModel.runningIA
//                runningIB = protocolModel.runningIB
//                runningIC = protocolModel.runningIC
//                runningTempOI = protocolModel.runningTempOI
//                runningTempAmb = protocolModel.runningTempAmb
//                runningSpeed = protocolModel.runningSpeed
//                runningVibro1 = protocolModel.runningVibro1
//                runningVibro2 = protocolModel.runningVibro2
//                runningTime = protocolModel.runningTime
//                runningP1 = protocolModel.runningP1
//                runningCos = protocolModel.runningCos
//                runningResult = protocolModel.runningResult
//
//                //RUNNING//
//                runningUAB = protocolModel.runningUAB
//                runningUBC = protocolModel.runningUBC
//                runningUCA = protocolModel.runningUCA
//                runningIA = protocolModel.runningIA
//                runningIB = protocolModel.runningIB
//                runningIC = protocolModel.runningIC
//                runningTempOI = protocolModel.runningTempOI
//                runningTempAmb = protocolModel.runningTempAmb
//                runningSpeed = protocolModel.runningSpeed
//                runningVibro1 = protocolModel.runningVibro1
//                runningVibro2 = protocolModel.runningVibro2
//                runningTime = protocolModel.runningTime
//                runningP1 = protocolModel.runningP1
//                runningCos = protocolModel.runningCos
//                runningResult = protocolModel.runningResult
//                //H_HH//
////                h_hhUAB1 = protocolModel.h_hhUAB1
////                h_hhUBC1 = protocolModel.h_hhUBC1
////                h_hhUCA1 = protocolModel.h_hhUCA1
////                h_hhIA1 = protocolModel.h_hhIA1
////                h_hhIB1 = protocolModel.h_hhIB1
////                h_hhIC1 = protocolModel.h_hhIC1
////                h_hhUAB2 = protocolModel.h_hhUAB2
////                h_hhUBC2 = protocolModel.h_hhUBC2
////                h_hhUCA2 = protocolModel.h_hhUCA2
////                h_hhIA2 = protocolModel.h_hhIA2
////                h_hhIB2 = protocolModel.h_hhIB2
////                h_hhIC2 = protocolModel.h_hhIC2
////                h_hhUAB3 = protocolModel.h_hhUAB3
////                h_hhUBC3 = protocolModel.h_hhUBC3
////                h_hhUCA3 = protocolModel.h_hhUCA3
////                h_hhIA3 = protocolModel.h_hhIA3
////                h_hhIB3 = protocolModel.h_hhIB3
////                h_hhIC3 = protocolModel.h_hhIC3
////                h_hhUAB4 = protocolModel.h_hhUAB4
////                h_hhUBC4 = protocolModel.h_hhUBC4
////                h_hhUCA4 = protocolModel.h_hhUCA4
////                h_hhIA4 = protocolModel.h_hhIA4
////                h_hhIB4 = protocolModel.h_hhIB4
////                h_hhIC4 = protocolModel.h_hhIC4
////                h_hhUAB5 = protocolModel.h_hhUAB5
////                h_hhUBC5 = protocolModel.h_hhUBC5
////                h_hhUCA5 = protocolModel.h_hhUCA5
////                h_hhIA5 = protocolModel.h_hhIA5
////                h_hhIB5 = protocolModel.h_hhIB5
////                h_hhIC5 = protocolModel.h_hhIC5
////                h_hhUAB6 = protocolModel.h_hhUAB6
////                h_hhUBC6 = protocolModel.h_hhUBC6
////                h_hhUCA6 = protocolModel.h_hhUCA6
////                h_hhIA6 = protocolModel.h_hhIA6
////                h_hhIB6 = protocolModel.h_hhIB6
////                h_hhIC6 = protocolModel.h_hhIC6
////                h_hhUAB7 = protocolModel.h_hhUAB7
////                h_hhUBC7 = protocolModel.h_hhUBC7
////                h_hhUCA7 = protocolModel.h_hhUCA7
////                h_hhIA7 = protocolModel.h_hhIA7
////                h_hhIB7 = protocolModel.h_hhIB7
////                h_hhIC7 = protocolModel.h_hhIC7
////                h_hhUAB8 = protocolModel.h_hhUAB8
////                h_hhUBC8 = protocolModel.h_hhUBC8
////                h_hhUCA8 = protocolModel.h_hhUCA8
////                h_hhIA8 = protocolModel.h_hhIA8
////                h_hhIB8 = protocolModel.h_hhIB8
////                h_hhIC8 = protocolModel.h_hhIC8
////                h_hhUAB9 = protocolModel.h_hhUAB9
////                h_hhUBC9 = protocolModel.h_hhUBC9
////                h_hhUCA9 = protocolModel.h_hhUCA9
////                h_hhIA9 = protocolModel.h_hhIA9
////                h_hhIB9 = protocolModel.h_hhIB9
////                h_hhIC9 = protocolModel.h_hhIC9
////                h_hhN1 = protocolModel.h_hhP1
////                h_hhN2 = protocolModel.h_hhP2
////                h_hhN3 = protocolModel.h_hhP3
////                h_hhN4 = protocolModel.h_hhP4
////                h_hhN5 = protocolModel.h_hhP5
////                h_hhN6 = protocolModel.h_hhP6
////                h_hhN7 = protocolModel.h_hhP7
////                h_hhN8 = protocolModel.h_hhP8
////                h_hhN9 = protocolModel.h_hhP9
////                h_hhResult = protocolModel.h_hhResult
//                //N//
//                nUAB = protocolModel.nUAB
//                nUBC = protocolModel.nUBC
//                nUCA = protocolModel.nUCA
//                nIA = protocolModel.nIA
//                nIB = protocolModel.nIB
//                nIC = protocolModel.nIC
//                nSpeed = protocolModel.nSpeed
//                nF = protocolModel.nF
//                nResult = protocolModel.nResult
//                //KTr//
//                ktrUAVG1 = protocolModel.ktrUAVG1
//                ktrUAVG2 = protocolModel.ktrUAVG2
//                ktrKTR = protocolModel.ktrKTR
//                ktrResult = protocolModel.ktrResult
//                //MV//
//                mvUAB1 = protocolModel.mvUAB1
//                mvUBC1 = protocolModel.mvUBC1
//                mvUCA1 = protocolModel.mvUCA1
//                mvIA1 = protocolModel.mvIA1
//                mvIB1 = protocolModel.mvIB1
//                mvIC1 = protocolModel.mvIC1
//                mvUAB2 = protocolModel.mvUAB2
//                mvUBC2 = protocolModel.mvUBC2
//                mvUCA2 = protocolModel.mvUCA2
//                mvIA2 = protocolModel.mvIA2
//                mvIB2 = protocolModel.mvIB2
//                mvIC2 = protocolModel.mvIC2
//                mvDeviation = protocolModel.mvDeviation
//                mvResult = protocolModel.mvResult
//                //KZ//
//                kzUAB = protocolModel.kzUAB
//                kzUBC = protocolModel.kzUBC
//                kzUCA = protocolModel.kzUCA
//                kzIA = protocolModel.kzIA
//                kzIB = protocolModel.kzIB
//                kzIC = protocolModel.kzIC
//                kzP1 = protocolModel.kzP1
//                kzResult = protocolModel.kzResult
//            }
//        }
        runLater {
            infoNotification("Сохранение протокола", "Протокол сохранен")
        }
    }

    private fun sortExp(expListRaw: MutableList<CustomController>): MutableList<CustomController> {
        val newList = mutableListOf<CustomController>()
        list.add(mgr)
        list.add(mgrMPT)
        list.add(viu)
        list.add(ikas)
        list.add(ikasMPT)
        list.add(hh)
        list.add(hhMPT)
        list.add(h_hh)
        list.add(h_hhSG)
        list.add(nSD)
        list.add(nSG)
        list.add(nMPT)
        list.add(nGPT)
        list.add(load)
        list.add(loadMPT)
        list.add(n)
        list.add(ktr)
        list.add(mv)
        list.add(kz)
        list.add(kzSG)
        list.forEach {
            if (expListRaw.contains(it)) {
                newList.add(it)
                expListRaw.remove(it)
            }
        }
//        repeat(expListRaw.size) {
//            when {
//                expListRaw.contains(mgr) -> {
//                    newList.add(mgr)
//                    expListRaw.remove(mgr)
//                }
//                expListRaw.contains(viu) -> {
//                    newList.add(viu)
//                    expListRaw.remove(viu)
//                }
//                expListRaw.contains(ikas) -> {
//                    newList.add(ikas)
//                    expListRaw.remove(ikas)
//                }
//                expListRaw.contains(ikasMPT) -> {
//                    newList.add(ikasMPT)
//                    expListRaw.remove(ikasMPT)
//                }
//                expListRaw.contains(hh) -> {
//                    newList.add(hh)
//                    expListRaw.remove(hh)
//                }
//                expListRaw.contains(hhMPT) -> {
//                    newList.add(hhMPT)
//                    expListRaw.remove(hhMPT)
//                }
//                expListRaw.contains(h_hhSG) -> {
//                    newList.add(h_hhSG)
//                    expListRaw.remove(h_hhSG)
//                }
//                expListRaw.contains(load) -> {
//                    newList.add(load)
//                    expListRaw.remove(load)
//                }
//                expListRaw.contains(loadMPT) -> {
//                    newList.add(loadMPT)
//                    expListRaw.remove(loadMPT)
//                }
////                expListRaw.contains(h_hh) -> {
////                    newList.add(h_hh)
////                    expListRaw.remove(h_hh)
////                }
//                expListRaw.contains(n) -> {
//                    newList.add(n)
//                    expListRaw.remove(n)
//                }
//                expListRaw.contains(nSD) -> {
//                    newList.add(nSD)
//                    expListRaw.remove(nSD)
//                }
//                expListRaw.contains(nSG) -> {
//                    newList.add(nSG)
//                    expListRaw.remove(nSG)
//                }
//                expListRaw.contains(nMPT) -> {
//                    newList.add(nMPT)
//                    expListRaw.remove(nMPT)
//                }
//                expListRaw.contains(nGPT) -> {
//                    newList.add(nGPT)
//                    expListRaw.remove(nGPT)
//                }
//                expListRaw.contains(ktr) -> {
//                    newList.add(ktr)
//                    expListRaw.remove(ktr)
//                }
//                expListRaw.contains(mv) -> {
//                    newList.add(mv)
//                    expListRaw.remove(mv)
//                }
//                expListRaw.contains(kz) -> {
//                    newList.add(kz)
//                    expListRaw.remove(kz)
//                }
//                expListRaw.contains(kzSG) -> {
//                    newList.add(kzSG)
//                    expListRaw.remove(kzSG)
//                }
//            }
//        }
        return newList
    }

    init {
        thread(isDaemon = true) {
            while (isAppRunning) {
                sleep(1500)
                val serialPortBSY = SerialPort.getCommPorts().filter {
                    it.toString() == "CP2103 USB to RS-485"
                }
                if (serialPortBSY.isEmpty()) {
                    runLater {
                        mainView.comIndicate.fill = State.BAD.c
                        mainView.circlePR102.fill = State.INTERMEDIATE.c
//                        expView.circlePM135.fill = State.INTERMEDIATE.c
//                        expView.circlePR200.fill = State.INTERMEDIATE.c
                    }
                } else {
                    runLater {
                        mainView.comIndicate.fill = State.OK.c
                    }
                    pr102.checkResponsibility()
                    if (pr102.isResponding) {
                        runLater {
                            mainView.circlePR102.fill = State.OK.c
//                            expView.circlePR200.fill = State.OK.c
                        }
                    } else {
                        pr102.resetKMS()
                        runLater {
                            mainView.circlePR102.fill = State.BAD.c
//                            expView.circlePR200.fill = State.BAD.c
                        }
                    }
                    pm135.checkResponsibility()
                    if (pm135.isResponding) {
                        runLater {
//                            expView.circlePM135.fill = State.OK.c
                        }
                    } else {
                        runLater {
//                            expView.circlePM135.fill = State.BAD.c
                        }
                    }
                }
                val serialPortDelta = SerialPort.getCommPorts().filter {
                    it.toString() == "CP2103 USB to Delta"
                }
                if (serialPortDelta.isEmpty()) {
                    runLater {
                        mainView.deltaIndicate.fill = State.BAD.c
//                        expView.circleDelta.fill = State.INTERMEDIATE.c
                    }
                } else {
                    runLater {
                        mainView.deltaIndicate.fill = State.OK.c
                    }
                    delta.checkResponsibility()
                    if (delta.isResponding) {
                        runLater {
//                            expView.circleDelta.fill = State.OK.c
                        }
                    } else {
                        runLater {
//                            expView.circleDelta.fill = State.BAD.c
                        }
                    }
                }
            }
        }
    }

    fun clearProtocol() {

        protocolModel.objectName = ""
        protocolModel.date = ""
        protocolModel.time = ""
        protocolModel.operator = ""
        protocolModel.serial = ""
        protocolModel.p2 = ""
        protocolModel.uN = ""
        protocolModel.iN = ""
        protocolModel.nAsync = ""
        protocolModel.kpd = ""
        protocolModel.cos = ""
        protocolModel.scheme = ""
        //MGR//
        protocolModel.mgrU = ""
        protocolModel.mgrR15 = ""
        protocolModel.mgrR60 = ""
        protocolModel.mgrkABS = ""
        protocolModel.mgrTemp = ""
        protocolModel.mgrResult = ""
        //VIU//
        protocolModel.viuU = ""
        protocolModel.viuI = ""
        protocolModel.viuTime = ""
        protocolModel.viuResult = ""
//IKAS//
        protocolModel.ikasR1 = ""
        protocolModel.ikasR2 = ""
        protocolModel.ikasR3 = ""
        protocolModel.ikasResult = ""
        //HH//
        protocolModel.hhUAB = ""
        protocolModel.hhUBC = ""
        protocolModel.hhUCA = ""
        protocolModel.hhIA = ""
        protocolModel.hhIB = ""
        protocolModel.hhIC = ""
        protocolModel.hhTempOI = ""
        protocolModel.hhTempAmb = ""
        protocolModel.hhSpeed = ""
        protocolModel.hhVibro1 = ""
        protocolModel.hhVibro2 = ""
        protocolModel.hhTime = ""
        protocolModel.hhP1 = ""
        protocolModel.hhCos = ""
        protocolModel.hhResult = ""
//RUNNING//
        protocolModel.runningUAB = ""
        protocolModel.runningUBC = ""
        protocolModel.runningUCA = ""
        protocolModel.runningIA = ""
        protocolModel.runningIB = ""
        protocolModel.runningIC = ""
        protocolModel.runningTempOI = ""
        protocolModel.runningTempAmb = ""
        protocolModel.runningSpeed = ""
        protocolModel.runningVibro1 = ""
        protocolModel.runningVibro2 = ""
        protocolModel.runningTime = ""
        protocolModel.runningP1 = ""
        protocolModel.runningCos = ""
        protocolModel.runningResult = ""
//LOADMPT//
        protocolModel.loadResult = ""
        protocolModel.loadUOV = ""
        protocolModel.loadIOV = ""
        protocolModel.loadUOY = ""
        protocolModel.loadIOY = ""
        protocolModel.loadN = ""
        protocolModel.loadP = ""
        protocolModel.loadTempAmb = ""
        protocolModel.loadTempOI = ""
//H_HH//
//        protocolModel.h_hhUAB1 = ""
//        protocolModel.h_hhUBC1 = ""
//        protocolModel.h_hhUCA1 = ""
//        protocolModel.h_hhIA1 = ""
//        protocolModel.h_hhIB1 = ""
//        protocolModel.h_hhIC1 = ""
//        protocolModel.h_hhP1 = ""
//        protocolModel.h_hhUAB2 = ""
//        protocolModel.h_hhUBC2 = ""
//        protocolModel.h_hhUCA2 = ""
//        protocolModel.h_hhIA2 = ""
//        protocolModel.h_hhIB2 = ""
//        protocolModel.h_hhIC2 = ""
//        protocolModel.h_hhP2 = ""
//        protocolModel.h_hhUAB3 = ""
//        protocolModel.h_hhUBC3 = ""
//        protocolModel.h_hhUCA3 = ""
//        protocolModel.h_hhIA3 = ""
//        protocolModel.h_hhIB3 = ""
//        protocolModel.h_hhIC3 = ""
//        protocolModel.h_hhP3 = ""
//        protocolModel.h_hhUAB4 = ""
//        protocolModel.h_hhUBC4 = ""
//        protocolModel.h_hhUCA4 = ""
//        protocolModel.h_hhIA4 = ""
//        protocolModel.h_hhIB4 = ""
//        protocolModel.h_hhIC4 = ""
//        protocolModel.h_hhP4 = ""
//        protocolModel.h_hhUAB5 = ""
//        protocolModel.h_hhUBC5 = ""
//        protocolModel.h_hhUCA5 = ""
//        protocolModel.h_hhIA5 = ""
//        protocolModel.h_hhIB5 = ""
//        protocolModel.h_hhIC5 = ""
//        protocolModel.h_hhP5 = ""
//        protocolModel.h_hhUAB6 = ""
//        protocolModel.h_hhUBC6 = ""
//        protocolModel.h_hhUCA6 = ""
//        protocolModel.h_hhIA6 = ""
//        protocolModel.h_hhIB6 = ""
//        protocolModel.h_hhIC6 = ""
//        protocolModel.h_hhP6 = ""
//        protocolModel.h_hhUAB7 = ""
//        protocolModel.h_hhUBC7 = ""
//        protocolModel.h_hhUCA7 = ""
//        protocolModel.h_hhIA7 = ""
//        protocolModel.h_hhIB7 = ""
//        protocolModel.h_hhIC7 = ""
//        protocolModel.h_hhP7 = ""
//        protocolModel.h_hhUAB8 = ""
//        protocolModel.h_hhUBC8 = ""
//        protocolModel.h_hhUCA8 = ""
//        protocolModel.h_hhIA8 = ""
//        protocolModel.h_hhIB8 = ""
//        protocolModel.h_hhIC8 = ""
//        protocolModel.h_hhP8 = ""
//        protocolModel.h_hhUAB9 = ""
//        protocolModel.h_hhUBC9 = ""
//        protocolModel.h_hhUCA9 = ""
//        protocolModel.h_hhIA9 = ""
//        protocolModel.h_hhIB9 = ""
//        protocolModel.h_hhIC9 = ""
//        protocolModel.h_hhP9 = ""
//        protocolModel.h_hhResult  = ""
        //KTR//
        protocolModel.ktrUAVG1 = ""
        protocolModel.ktrUAVG2 = ""
        protocolModel.ktrKTR = ""
        protocolModel.ktrResult = ""
        //N//
        protocolModel.nUAB = ""
        protocolModel.nUBC = ""
        protocolModel.nUCA = ""
        protocolModel.nIA = ""
        protocolModel.nIB = ""
        protocolModel.nIC = ""
        protocolModel.nSpeed = ""
        protocolModel.nF = ""
        protocolModel.nResult = ""
        //MV//
        protocolModel.mvUAB1 = ""
        protocolModel.mvUBC1 = ""
        protocolModel.mvUCA1 = ""
        protocolModel.mvIA1 = ""
        protocolModel.mvIB1 = ""
        protocolModel.mvIC1 = ""
        protocolModel.mvUAB2 = ""
        protocolModel.mvUBC2 = ""
        protocolModel.mvUCA2 = ""
        protocolModel.mvIA2 = ""
        protocolModel.mvIB2 = ""
        protocolModel.mvIC2 = ""
        protocolModel.mvDeviation = ""
        protocolModel.mvResult = ""
        //KZ//
        protocolModel.kzUAB = ""
        protocolModel.kzUBC = ""
        protocolModel.kzUCA = ""
        protocolModel.kzIA = ""
        protocolModel.kzIB = ""
        protocolModel.kzIC = ""
        protocolModel.kzP1 = ""
        protocolModel.kzResult = ""
    }
}
