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
    var listDPT = mutableListOf<CustomController>(mgrMPT, viu, ikasMPT, nMPT, hhMPT, loadMPT)
    var listGPT = mutableListOf<CustomController>(mgrMPT, viu, ikasMPT, nGPT)
    var listSD = mutableListOf<CustomController>(mgr, viu, ikas, nSD)
    var listSG = mutableListOf<CustomController>(mgr, viu, ikas, nSG, h_hhSG, kzSG)

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
        transaction {
            Protocol.new {
                objectName = protocolModel.objectName
                type = protocolModel.type
                date = protocolModel.date
                time = protocolModel.time
                operator = protocolModel.operator
                serial = protocolModel.serial
                p2 = protocolModel.p2
                uN = protocolModel.uN
                iN = protocolModel.iN
                uOV = protocolModel.uOV
                iOV = protocolModel.iOV
                nAsync = protocolModel.nAsync
                kpd = protocolModel.kpd
                scheme = protocolModel.scheme


                mgrU1 = protocolModel.mgrU1
                mgrU2 = protocolModel.mgrU2
                mgrU3 = protocolModel.mgrU3
                mgrR151 = protocolModel.mgrR151
                mgrR152 = protocolModel.mgrR152
                mgrR153 = protocolModel.mgrR153
                mgrR601 = protocolModel.mgrR601
                mgrR602 = protocolModel.mgrR602
                mgrR603 = protocolModel.mgrR603
                mgrkABS1 = protocolModel.mgrkABS1
                mgrkABS2 = protocolModel.mgrkABS2
                mgrkABS3 = protocolModel.mgrkABS3
                mgrTemp = protocolModel.mgrTemp
                mgrResult1 = protocolModel.mgrResult1
                mgrResult2 = protocolModel.mgrResult2
                mgrResult3 = protocolModel.mgrResult3


                viuU = protocolModel.viuU
                viuI = protocolModel.viuI
                viuTime = protocolModel.viuTime
                viuResult = protocolModel.viuResult


                ikasR1 = protocolModel.ikasR1
                ikasR2 = protocolModel.ikasR2
                ikasR3 = protocolModel.ikasR3
                ikasResult = protocolModel.ikasResult


                dptNuOV = protocolModel.dptNuOV
                dptNiOV = protocolModel.dptNiOV
                dptNuN = protocolModel.dptNuN
                dptNiN = protocolModel.dptNiN
                dptNP1 = protocolModel.dptNP1
                dptNTOI = protocolModel.dptNTOI
                dptNTAmb = protocolModel.dptNTAmb
                dptNN = protocolModel.dptNN
                dptNResult = protocolModel.dptNResult


                dptHHuOV = protocolModel.dptHHuOV
                dptHHiOV = protocolModel.dptHHiOV
                dptHHuN = protocolModel.dptHHuN
                dptHHiN = protocolModel.dptHHiN
                dptHHP1 = protocolModel.dptHHP1
                dptHHTOI = protocolModel.dptHHTOI
                dptHHTAmb = protocolModel.dptHHTAmb
                dptHHN = protocolModel.dptHHN
                dptHHResult = protocolModel.dptHHResult
                dptHHTime = protocolModel.dptHHTime


                dptLOADuOV = protocolModel.dptLOADuOV
                dptLOADiOV = protocolModel.dptLOADiOV
                dptLOADuN = protocolModel.dptLOADuN
                dptLOADiN = protocolModel.dptLOADiN
                dptLOADP1 = protocolModel.dptLOADP1
                dptLOADTOI = protocolModel.dptLOADTOI
                dptLOADTAmb = protocolModel.dptLOADTAmb
                dptLOADN = protocolModel.dptLOADN
                dptLOADDots = protocolModel.dptLOADDots
                dptLOADResult = protocolModel.dptLOADResult


                gptNuOV = protocolModel.gptNuOV
                gptNiOV = protocolModel.gptNiOV
                gptNuN = protocolModel.gptNuN
                gptNiN = protocolModel.gptNiN
                gptNP1 = protocolModel.gptNP1
                gptNTOI = protocolModel.gptNTOI
                gptNTAmb = protocolModel.gptNTAmb
                gptNN = protocolModel.gptNN
                gptNResult = protocolModel.gptNResult


                nUAB = protocolModel.nUAB
                nUBC = protocolModel.nUBC
                nUCA = protocolModel.nUCA
                nIA = protocolModel.nIA
                nIB = protocolModel.nIB
                nIC = protocolModel.nIC
                nF = protocolModel.nF
                nTempOI = protocolModel.nTempOI
                nTempAmb = protocolModel.nTempAmb
                nSpeed = protocolModel.nSpeed
                nVibro1 = protocolModel.nVibro1
                nVibro2 = protocolModel.nVibro2
                nTime = protocolModel.nTime
                nP1 = protocolModel.nP1
                nCos = protocolModel.nCos
                nResult = protocolModel.nResult


                h_hhuAB1 = protocolModel.h_hhuAB1
                h_hhuBC1 = protocolModel.h_hhuBC1
                h_hhuCA1 = protocolModel.h_hhuCA1
                h_hhiA1 = protocolModel.h_hhiA1
                h_hhiB1 = protocolModel.h_hhiB1
                h_hhiC1 = protocolModel.h_hhiC1
                h_hhuOV1 = protocolModel.h_hhuOV1
                h_hhiOV1 = protocolModel.h_hhiOV1
                h_hhuAB2 = protocolModel.h_hhuAB2
                h_hhuBC2 = protocolModel.h_hhuBC2
                h_hhuCA2 = protocolModel.h_hhuCA2
                h_hhiA2 = protocolModel.h_hhiA2
                h_hhiB2 = protocolModel.h_hhiB2
                h_hhiC2 = protocolModel.h_hhiC2
                h_hhuOV2 = protocolModel.h_hhuOV2
                h_hhiOV2 = protocolModel.h_hhiOV2
                h_hhuAB3 = protocolModel.h_hhuAB3
                h_hhuBC3 = protocolModel.h_hhuBC3
                h_hhuCA3 = protocolModel.h_hhuCA3
                h_hhiA3 = protocolModel.h_hhiA3
                h_hhiB3 = protocolModel.h_hhiB3
                h_hhiC3 = protocolModel.h_hhiC3
                h_hhuOV3 = protocolModel.h_hhuOV3
                h_hhiOV3 = protocolModel.h_hhiOV3
                h_hhuAB4 = protocolModel.h_hhuAB4
                h_hhuBC4 = protocolModel.h_hhuBC4
                h_hhuCA4 = protocolModel.h_hhuCA4
                h_hhiA4 = protocolModel.h_hhiA4
                h_hhiB4 = protocolModel.h_hhiB4
                h_hhiC4 = protocolModel.h_hhiC4
                h_hhuOV4 = protocolModel.h_hhuOV4
                h_hhiOV4 = protocolModel.h_hhiOV4
                h_hhuAB5 = protocolModel.h_hhuAB5
                h_hhuBC5 = protocolModel.h_hhuBC5
                h_hhuCA5 = protocolModel.h_hhuCA5
                h_hhiA5 = protocolModel.h_hhiA5
                h_hhiB5 = protocolModel.h_hhiB5
                h_hhiC5 = protocolModel.h_hhiC5
                h_hhuOV5 = protocolModel.h_hhuOV5
                h_hhiOV5 = protocolModel.h_hhiOV5
                h_hhuAB6 = protocolModel.h_hhuAB6
                h_hhuBC6 = protocolModel.h_hhuBC6
                h_hhuCA6 = protocolModel.h_hhuCA6
                h_hhiA6 = protocolModel.h_hhiA6
                h_hhiB6 = protocolModel.h_hhiB6
                h_hhiC6 = protocolModel.h_hhiC6
                h_hhuOV6 = protocolModel.h_hhiOV6
                h_hhiOV6 = protocolModel.h_hhuOV6
                h_hhuAB7 = protocolModel.h_hhuAB7
                h_hhuBC7 = protocolModel.h_hhuBC7
                h_hhuCA7 = protocolModel.h_hhuCA7
                h_hhiA7 = protocolModel.h_hhiA7
                h_hhiB7 = protocolModel.h_hhiB7
                h_hhiC7 = protocolModel.h_hhiC7
                h_hhuOV7 = protocolModel.h_hhuOV7
                h_hhiOV7 = protocolModel.h_hhiOV7
                h_hhuAB8 = protocolModel.h_hhuAB8
                h_hhuBC8 = protocolModel.h_hhuBC8
                h_hhuCA8 = protocolModel.h_hhuCA8
                h_hhiA8 = protocolModel.h_hhiA8
                h_hhiB8 = protocolModel.h_hhiB8
                h_hhiC8 = protocolModel.h_hhiC8
                h_hhuOV8 = protocolModel.h_hhuOV8
                h_hhiOV8 = protocolModel.h_hhiOV8
                h_hhuAB9 = protocolModel.h_hhuAB9
                h_hhuBC9 = protocolModel.h_hhuBC9
                h_hhuCA9 = protocolModel.h_hhuCA9
                h_hhiA9 = protocolModel.h_hhiA9
                h_hhiB9 = protocolModel.h_hhiB9
                h_hhiC9 = protocolModel.h_hhiC9
                h_hhuOV9 = protocolModel.h_hhuOV9
                h_hhiOV9 = protocolModel.h_hhiOV9
                h_hhResult = protocolModel.h_hhResult


                kzN = protocolModel.kzN
                kzCos = protocolModel.kzCos
                kzUOV = protocolModel.kzUOV
                kzIOV = protocolModel.kzIOV
                kzUAB = protocolModel.kzUAB
                kzUBC = protocolModel.kzUBC
                kzUCA = protocolModel.kzUCA
                kzIA = protocolModel.kzIA
                kzIB = protocolModel.kzIB
                kzIC = protocolModel.kzIC
                kzP1 = protocolModel.kzP1
                kzResult = protocolModel.kzResult
            }
        }
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
        list.add(n)
        list.add(nMPT)
        list.add(nGPT)
        list.add(nSD)
        list.add(nSG)
//        list.add(load)
        list.add(loadMPT)
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
        protocolModel.type = ""
        protocolModel.date = ""
        protocolModel.time = ""
        protocolModel.operator = ""
        protocolModel.serial = ""
        protocolModel.p2 = ""
        protocolModel.uN = ""
        protocolModel.iN = ""
        protocolModel.uOV = ""
        protocolModel.iOV = ""
        protocolModel.nAsync = ""
        protocolModel.kpd = ""
        protocolModel.cos = ""
        protocolModel.scheme = ""
//MGR//,
        protocolModel.mgrU1 = ""
        protocolModel.mgrU2 = ""
        protocolModel.mgrU3 = ""
        protocolModel.mgrR151 = ""
        protocolModel.mgrR152 = ""
        protocolModel.mgrR153 = ""
        protocolModel.mgrR601 = ""
        protocolModel.mgrR602 = ""
        protocolModel.mgrR603 = ""
        protocolModel.mgrkABS1 = ""
        protocolModel.mgrkABS2 = ""
        protocolModel.mgrkABS3 = ""
        protocolModel.mgrTemp = ""
        protocolModel.mgrResult1 = ""
        protocolModel.mgrResult2 = ""
        protocolModel.mgrResult3 = ""
//VIU//,
        protocolModel.viuU = ""
        protocolModel.viuI = ""
        protocolModel.viuTime = ""
        protocolModel.viuResult = ""
//IKAS//,
        protocolModel.ikasR1 = ""
        protocolModel.ikasR2 = ""
        protocolModel.ikasR3 = ""
        protocolModel.ikasResult = ""
//N_DPT//,
        protocolModel.dptNuOV = ""
        protocolModel.dptNiOV = ""
        protocolModel.dptNuN = ""
        protocolModel.dptNiN = ""
        protocolModel.dptNP1 = ""
        protocolModel.dptNTOI = ""
        protocolModel.dptNTAmb = ""
        protocolModel.dptNN = ""
        protocolModel.dptNResult = ""
//HH_DPT//,
        protocolModel.dptHHuOV = ""
        protocolModel.dptHHiOV = ""
        protocolModel.dptHHuN = ""
        protocolModel.dptHHiN = ""
        protocolModel.dptHHP1 = ""
        protocolModel.dptHHTOI = ""
        protocolModel.dptHHTAmb = ""
        protocolModel.dptHHN = ""
        protocolModel.dptHHResult = ""
        protocolModel.dptHHTime = ""
//LOAD_DPT//,
        protocolModel.dptLOADuOV = ""
        protocolModel.dptLOADiOV = ""
        protocolModel.dptLOADuN = ""
        protocolModel.dptLOADiN = ""
        protocolModel.dptLOADP1 = ""
        protocolModel.dptLOADTOI = ""
        protocolModel.dptLOADTAmb = ""
        protocolModel.dptLOADN = ""
        protocolModel.dptLOADResult = ""
//N_GPT//,
        protocolModel.gptNuOV = ""
        protocolModel.gptNiOV = ""
        protocolModel.gptNuN = ""
        protocolModel.gptNiN = ""
        protocolModel.gptNP1 = ""
        protocolModel.gptNTOI = ""
        protocolModel.gptNTAmb = ""
        protocolModel.gptNN = ""
        protocolModel.gptNResult = ""
///////////
//N//,
        protocolModel.nUAB = ""
        protocolModel.nUBC = ""
        protocolModel.nUCA = ""
        protocolModel.nIA = ""
        protocolModel.nIB = ""
        protocolModel.nIC = ""
        protocolModel.nF = ""
        protocolModel.nTempOI = ""
        protocolModel.nTempAmb = ""
        protocolModel.nSpeed = ""
        protocolModel.nVibro1 = ""
        protocolModel.nVibro2 = ""
        protocolModel.nTime = ""
        protocolModel.nP1 = ""
        protocolModel.nCos = ""
        protocolModel.nResult = ""
//H_HH//,
        protocolModel.h_hhuAB1 = ""
        protocolModel.h_hhuBC1 = ""
        protocolModel.h_hhuCA1 = ""
        protocolModel.h_hhiA1 = ""
        protocolModel.h_hhiB1 = ""
        protocolModel.h_hhiC1 = ""
        protocolModel.h_hhuOV1 = ""
        protocolModel.h_hhiOV1 = ""

        protocolModel.h_hhuAB2 = ""
        protocolModel.h_hhuBC2 = ""
        protocolModel.h_hhuCA2 = ""
        protocolModel.h_hhiA2 = ""
        protocolModel.h_hhiB2 = ""
        protocolModel.h_hhiC2 = ""
        protocolModel.h_hhuOV2 = ""
        protocolModel.h_hhiOV2 = ""

        protocolModel.h_hhuAB3 = ""
        protocolModel.h_hhuBC3 = ""
        protocolModel.h_hhuCA3 = ""
        protocolModel.h_hhiA3 = ""
        protocolModel.h_hhiB3 = ""
        protocolModel.h_hhiC3 = ""
        protocolModel.h_hhuOV3 = ""
        protocolModel.h_hhiOV3 = ""

        protocolModel.h_hhuAB4 = ""
        protocolModel.h_hhuBC4 = ""
        protocolModel.h_hhuCA4 = ""
        protocolModel.h_hhiA4 = ""
        protocolModel.h_hhiB4 = ""
        protocolModel.h_hhiC4 = ""
        protocolModel.h_hhuOV4 = ""
        protocolModel.h_hhiOV4 = ""

        protocolModel.h_hhuAB5 = ""
        protocolModel.h_hhuBC5 = ""
        protocolModel.h_hhuCA5 = ""
        protocolModel.h_hhiA5 = ""
        protocolModel.h_hhiB5 = ""
        protocolModel.h_hhiC5 = ""
        protocolModel.h_hhuOV5 = ""
        protocolModel.h_hhiOV5 = ""

        protocolModel.h_hhuAB6 = ""
        protocolModel.h_hhuBC6 = ""
        protocolModel.h_hhuCA6 = ""
        protocolModel.h_hhiA6 = ""
        protocolModel.h_hhiB6 = ""
        protocolModel.h_hhiC6 = ""
        protocolModel.h_hhuOV6 = ""
        protocolModel.h_hhiOV6 = ""

        protocolModel.h_hhuAB7 = ""
        protocolModel.h_hhuBC7 = ""
        protocolModel.h_hhuCA7 = ""
        protocolModel.h_hhiA7 = ""
        protocolModel.h_hhiB7 = ""
        protocolModel.h_hhiC7 = ""
        protocolModel.h_hhuOV7 = ""
        protocolModel.h_hhiOV7 = ""

        protocolModel.h_hhuAB8 = ""
        protocolModel.h_hhuBC8 = ""
        protocolModel.h_hhuCA8 = ""
        protocolModel.h_hhiA8 = ""
        protocolModel.h_hhiB8 = ""
        protocolModel.h_hhiC8 = ""
        protocolModel.h_hhuOV8 = ""
        protocolModel.h_hhiOV8 = ""

        protocolModel.h_hhuAB9 = ""
        protocolModel.h_hhuBC9 = ""
        protocolModel.h_hhuCA9 = ""
        protocolModel.h_hhiA9 = ""
        protocolModel.h_hhiB9 = ""
        protocolModel.h_hhiC9 = ""
        protocolModel.h_hhuOV9 = ""
        protocolModel.h_hhiOV9 = ""

        protocolModel.h_hhResult = ""
//KZ//,
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
