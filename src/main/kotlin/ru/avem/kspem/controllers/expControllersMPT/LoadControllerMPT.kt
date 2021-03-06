package ru.avem.kspem.controllers.expControllersMPT

import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.avem.avem4.Avem4Model
import ru.avem.kspem.communication.model.devices.avem.avem7.Avem7Model
import ru.avem.kspem.communication.model.devices.delta.Delta
import ru.avem.kspem.communication.model.devices.delta.DeltaModel
import ru.avem.kspem.communication.model.devices.owen.pr.OwenPrModel
import ru.avem.kspem.communication.model.devices.th01.TH01Model
import ru.avem.kspem.communication.model.devices.trm202.TRM202Model
import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.data.protocolModel
import ru.avem.kspem.utils.LogTag
import ru.avem.kspem.utils.Singleton
import ru.avem.kspem.utils.Singleton.sparking1
import ru.avem.kspem.utils.Singleton.sparking2
import ru.avem.kspem.utils.Singleton.sparking3
import ru.avem.kspem.utils.Singleton.sparking4
import ru.avem.kspem.utils.Singleton.sparkingTime
import ru.avem.kspem.utils.showTwoWayDialog
import ru.avem.kspem.utils.sleep
import ru.avem.kspem.view.expViews.expViewsMPT.LoadViewMPT
import ru.avem.stand.utils.autoformat
import kotlin.concurrent.thread
import kotlin.math.abs

class LoadControllerMPT : CustomController() {
    override val model: LoadViewMPT by inject()
    override val name = model.name
    private var ktrVoltage = 1.0

    private var ktrAmperageOY = 500 / 0.075
    private var ktrAmperageOV = 25 / 0.075

    @Volatile
    var voltageOV = 0.0

    @Volatile
    var amperageOV = 0.0

    @Volatile
    var voltageOY = 0.0

    @Volatile
    var amperageOY = 0.0

    @Volatile
    var voltageDelta = 0.0

    @Volatile
    var voltageOYSet = 0.0

    @Volatile
    var voltageOVSet = 0.0

    @Volatile
    var rotateSpeed = 0.0

    @Volatile
    var rotateSpeedSet = 0.0

    @Volatile
    var voltageTVN = 0.0

    @Volatile
    var deltaStatus = 0

    @Volatile
    var fDelta = 0.0

    @Volatile
    var amperageSet = 0.0

    @Volatile
    var voltageTRN = 0.0

    @Volatile
    var rotateUNM = 0.0

    @Volatile
    var isReverseNM = false

    @Volatile
    var isReverseOI = false

    @Volatile
    var currentFreq = 0.0

    @Volatile
    var currentFreqLast = 0.0

    @Volatile
    var loadNomStarted = false

    @Volatile
    var regulateStarted = false

    @Volatile
    var regulate12Started = false

    @Volatile
    var unmStarted = false

    var timerLoad = 0.0

    var timerLoadNom = 0.0

    override fun start() {
        model.clearTables()
        super.start()
        // (value-4)*0.625

        timerLoad = objectModel!!.timeMVZ.toDouble()
        timerLoadNom = objectModel!!.timeRUNNING.toDouble()

        isReverseNM = false
        isReverseOI = false
        voltageTVN = 0.0
        voltageTRN = 0.0
        voltageOYSet = objectModel!!.uNom.toDouble()
        rotateSpeedSet = objectModel!!.nAsync.toDouble()
        voltageOVSet = objectModel!!.uOV.toDouble()
        amperageSet = objectModel!!.iN.toDouble()
        sparkingTime.clear()
        sparking1.clear()
        sparking2.clear()
        sparking3.clear()
        sparking4.clear()


        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "?????????????????????????? ???????????????? ??????...")
            cm.startPoll(CommunicationModel.DeviceID.DD2_1, OwenPrModel.ROTATE_UNM) { value ->
                rotateUNM = value.toDouble()
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "?????????????????????????? ????????4-01-????...")
            cm.startPoll(CommunicationModel.DeviceID.PA13, Avem7Model.AMPERAGE) { value ->
                amperageOV = abs(value.toDouble() * ktrAmperageOV)
                model.data.iOV.value = amperageOV.autoformat()
                if (!avemIov.isResponding && isExperimentRunning) cause = "????????4-01 ???? ????????????????"
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "?????????????????????????? ????????4-03-????...")
            cm.startPoll(CommunicationModel.DeviceID.PV23, Avem4Model.RMS) { value ->
                voltageOV = abs(value.toDouble())
                model.data.uOV.value = voltageOV.autoformat()
                if (!avemUov.isResponding && isExperimentRunning) cause = "????????4-03 ???? ????????????????"

                if (regulateStarted && voltageOV > 50 && amperageOV < 0.1) {
                    cause = "?????? ???????? ???? ????"
                }

            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "?????????????????????????? ????????4-01-????...")
            cm.startPoll(CommunicationModel.DeviceID.PA15, Avem7Model.AMPERAGE) { value ->
                amperageOY = abs(value.toDouble() * ktrAmperageOY)
                model.data.iOY.value = amperageOY.autoformat()
                if (!avemIoy.isResponding && isExperimentRunning) cause = "????????4-01 ???? ????????????????"
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "?????????????????????????? ????????4-03-????...")
            cm.startPoll(CommunicationModel.DeviceID.PV25, Avem4Model.RMS) { value ->
                voltageOY = abs(value.toDouble())
                if (voltageOY > 80 && rotateSpeed < 100) cause = "?????????????????? ???????????? ????????????????"

                if (regulateStarted && voltageOV > 50 && voltageOV < 500 && amperageOY < 0.05) {
                    cause = "?????? ???????? ???? ????"
                }

                model.data.uOY.value = voltageOY.autoformat()
                model.data.p.value = ((voltageOY * amperageOY) / 1000).autoformat()
                if (!avemUoy.isResponding && isExperimentRunning) cause = "????????4-03 ???? ????????????????"
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "?????????????????????????? ??????202...")
            trm202.checkResponsibility()
            if (!trm202.isResponding) {
                cause = "??????202 ???? ????????????????"
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
            appendMessageToLog(LogTag.MESSAGE, "?????????????????????????? ????01...")
            th01.checkResponsibility()
            if (!th01.isResponding) {
                cause = "????01 ???? ????????????????"
            } else {
                cm.startPoll(CommunicationModel.DeviceID.PC71, TH01Model.RPM) { value ->
                    rotateSpeed = value.toDouble()
                    model.data.n.value = rotateSpeed.autoformat()
                }
            }
        }

        if (isExperimentRunning) {
            initButtonPost()
        }

        if (isExperimentRunning) {
            pr102.km1(true)
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "?????????????????????????? Delta...")
            var timeDelta = 150
            while (isExperimentRunning && timeDelta-- > 0) {
                sleep(100)
            }

            val timer = System.currentTimeMillis()
            while (isExperimentRunning && !delta.isResponding) {
                delta.checkResponsibility()
                sleep(100)
                if ((System.currentTimeMillis() - timer) > 30000) cause = "Delta ???? ????????????????"
            }
            cm.startPoll(CommunicationModel.DeviceID.UZ91, DeltaModel.CURRENT_FREQ) { value ->
                currentFreq = value.toDouble() / 100
            }
        }

        if (isExperimentRunning) {
            pr102.arn(true)
            pr102.ov_oi(true)
            pr102.tvn(true)
            pr102.setTVN(voltageTVN)
            pr102.setTRN(voltageTRN)
        }

        if (isExperimentRunning) {
            while (isExperimentRunning && rotateSpeed < 100) {
                voltageTRN += 0.03
                voltageTVN += 0.03
                pr102.setTRN(voltageTRN)
                pr102.setTVN(voltageTVN)
                sleep(1000)
                if (voltageTVN > 0.5) {
                    cause = "?????????????????? ???????????? ????????????????"
                }
            }
            voltageTRN = 0.0
            voltageTVN = 0.0
            pr102.setTRN(voltageTRN)
            pr102.setTVN(voltageTVN)
            while (isExperimentRunning && rotateSpeed > 100) {
                sleep(100)
            }
        }

        var isClicked = false

        if (isExperimentRunning) {
            showTwoWayDialog(
                title = "????????????????!",
                text = "???? ?????????????????? ?? ???????????????????? ???????????????????????",
                way1Title = "????",
                way2Title = "??????",
                way1 = {
                    appendMessageToLog(LogTag.MESSAGE, "?????????????????????? ???????????????? ???? ???????????????? ??????????????")
                    isClicked = true
                },
                way2 = {
                    appendMessageToLog(LogTag.DEBUG, "?????????????????????? ???????????????? ???? ????????????????")
                    isReverseOI = true
                    isClicked = true
                },
                currentWindow = primaryStage.scene.window
            )
        }

        while (!isClicked && isExperimentRunning) {
            sleep(100)
        }

        if (isReverseOI) {
            pr102.ov_oi(false)
            pr102.ov_oi_obr(true)
        }

        if (isExperimentRunning) {
            while (isExperimentRunning && rotateSpeed < 100) {
                voltageTRN += 0.03
                voltageTVN += 0.03
                pr102.setTRN(voltageTRN)
                pr102.setTVN(voltageTVN)
                sleep(1000)
                if (voltageTVN > 0.5) {
                    cause = "?????????????????? ???????????? ????????????????"
                }
            }
            voltageTRN = 0.0
            voltageTVN = 0.0
            pr102.setTRN(voltageTRN)
            pr102.setTVN(voltageTVN)
            while (isExperimentRunning && rotateSpeed > 100) {
                sleep(100)
            }
            delta.setObjectParamsRun(2, 20, 2)
            delta.startObject()
            var timer = 5.0
            while (isExperimentRunning && timer > 0) {
                timer -= 0.1
                sleep(100)
            }
            delta.stopObject()
            var i = 0
            while (isExperimentRunning && rotateSpeed > 100) {
                sleep(1000)
                i += 1
                if (i > 30) {
                    cause = "???? ???? ????????????????????????"
                }
            }
        }

        isClicked = false

        if (isExperimentRunning) {
            showTwoWayDialog(
                title = "????????????????!",
                text = "?????????????????? ?????????????????????? ???????????????? ???? ?? ?????",
                way1Title = "????",
                way2Title = "??????",
                way1 = {
                    appendMessageToLog(LogTag.MESSAGE, "?????????????????????? ???????????????? ???? ???????????????? ??????????????")
                    isClicked = true
                },
                way2 = {
                    appendMessageToLog(LogTag.DEBUG, "?????????????????????? ???????????????? ???? ????????????????")
                    isReverseNM = true
                    isClicked = true
                },
                currentWindow = primaryStage.scene.window
            )
        }

        while (!isClicked && isExperimentRunning) {
            sleep(100)
        }

        if (isExperimentRunning) {
            pr102.vent(true)
        }
        if (isExperimentRunning) {
            sleep(2000)
        }

        if (isExperimentRunning) {
            thread(isDaemon = true) {
                var noRotate = 0
                while (isExperimentRunning) {
                    val rotateUNMLast = rotateUNM
                    sleep(1000)
                    if (abs(rotateUNMLast - rotateUNM) < 10) {
                        noRotate++
                        if (noRotate > 5) {
                            cause = "???????????????????? ??????????????????????"
                        }
                    } else {
                        noRotate = 0
                    }
                }
            }
        }

        if (isExperimentRunning) {
            var timer = 5.0
            while (isExperimentRunning && timer > 0) {
                timer -= 0.1
                sleep(100)
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "?????????????????????? ???? ?????????????????????? ?????????????? ????????????????")
        }

        regulateStarted = true

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "???????????? ???????????????????? ?????????????? ??????????????????????.")
            voltageRegulationTRN(voltageOVSet, 300, 600)
        }

        thread(isDaemon = true) {
            if (isExperimentRunning) {
                var timer = 10.0
                if (isExperimentRunning) {
                    while (isExperimentRunning && timer > 0) {
                        timer -= 0.1
                        sleep(100)
                    }
                }
            }
            while (isExperimentRunning) {
                if (rotateSpeed < 100 && !unmStarted) {
                    cause = "?????????????????? ???????????? ????????????????"
                }
                sleep(1000)
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "???????????? ???????????????????? ?????????????? ??????????.")
            voltageRegulationTVN(voltageOYSet, 300, 600)
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "?????????????????????? ???? ?????????????????????? ?????????????? ???????????????? ??????????????????")
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "???????????? ????...")

            fDelta = if ((rotateSpeed / (1500 / 50)) / 2 < 50) {
                (rotateSpeed / (1500 / 50)) / 2 //TODO ???????????????? ????????????
            } else {
                50.0
            }

            var u = 3
            val maxU = 380 / 50 * fDelta

            delta.setObjectParamsRun(fDelta, u, fDelta)
            if (isReverseNM) {
                delta.startObject(Delta.Direction.REVERSE)
            } else {
                delta.startObject()
            }

            var timer = 10.0
            while (isExperimentRunning && timer > 0) {
                timer -= 0.1
                sleep(100)
            }

            while (isExperimentRunning && u < maxU) {
                u++
                delta.setObjectUMax(u)
                Thread.sleep(50)
            }
            delta.setObjectUMax(maxU)

            appendMessageToLog(LogTag.MESSAGE, "?????????????????????? ????????????????")

            pr102.unm(true)


            thread(isDaemon = true) {
                while (isExperimentRunning && regulateStarted) {
                    voltageRegulationTRN(voltageOVSet, 150, 300)
                }
            }
            thread(isDaemon = true) {
                while (isExperimentRunning && regulateStarted) {
                    voltageRegulationTVN(voltageOYSet, 150, 300)
                }
            }

            regulationTo(amperageSet)

            appendMessageToLog(LogTag.MESSAGE, "?????????????????????? ???? ?????????????????????? ???????????????? ??????????????????")
        }

        regulateStarted = false
        loadNomStarted = true

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "???????????????? ${timerLoadNom.autoformat()} ????????????")
            while (isExperimentRunning && timerLoadNom > 0) {
                timerLoadNom -= 0.1
                if (timerLoadNom >= 0) {
                    model.data.timeExp.value = timerLoadNom.autoformat()
                }
                sleep(100)
            }
            model.data.timeExp.value = ""
        }

        loadNomStarted = false
        regulate12Started = true

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "?????????????????????? ???? ?????????????????????? ???????????????? * 1.2")
            thread(isDaemon = true) {
                while (isExperimentRunning && regulate12Started) {
                    voltageRegulationTRN(voltageOVSet, 150, 300)
                }
            }
            thread(isDaemon = true) {
                while (isExperimentRunning && regulate12Started) {
                    voltageRegulationTVN(voltageOYSet, 150, 300)
                }
            }
            regulationTo(amperageSet * 1.2)
            appendMessageToLog(LogTag.MESSAGE, "?????????????????????? ???? ?????????????????????? ???????????????? * 1.2 ??????????????????")
        }

        regulate12Started = false

        unmStarted = true

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "???????????????? ${timerLoad.autoformat()} ????????????")
            while (isExperimentRunning && timerLoad > 0) {
                timerLoad -= 0.1
                if (timerLoad >= 0) {
                    model.data.timeExp.value = timerLoad.autoformat()
                }
                sleep(100)
            }
            model.data.timeExp.value = ""
        }

        unmStarted = false

        if (Singleton.sparking1.isNotEmpty()) {
            for (i in 0 until Singleton.sparking1.size) {
                appendMessageToLog(LogTag.MESSAGE, "?????????? ??????????????????. ?????????? $i = ${Singleton.sparkingTime[i]}")
                appendMessageToLog(LogTag.MESSAGE, "?????????????? ???????????????? 1 ????????. ?????????? $i = ${Singleton.sparking1[i]}")
                appendMessageToLog(LogTag.MESSAGE, "?????????????? ???????????????? 2 ????????. ?????????? $i = ${Singleton.sparking2[i]}")
                appendMessageToLog(LogTag.MESSAGE, "?????????????? ???????????????? 3 ????????. ?????????? $i = ${Singleton.sparking3[i]}")
                appendMessageToLog(LogTag.MESSAGE, "?????????????? ???????????????? 4 ????????. ?????????? $i = ${Singleton.sparking4[i]}")
            }
        }

        saveData()

        voltageTVN = 0.0
        voltageTRN = 0.0
        pr102.setTVN(voltageTVN)
        pr102.setTRN(voltageTRN)
        delta.stopObject()
        pr102.ov_oi_obr(false)
        pr102.ov_oi(false)
        pr102.unm(false)
        pr102.arn(false)
        pr102.tvn(false)

        var timer = 30.0
        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "?????????? ?????? 30 ????????????")
            while (isExperimentRunning && timer > 0) {
                timer -= 0.1
                if (timer >= 0) {
                    model.data.timeExp.value = timer.autoformat()
                }
                sleep(100)
            }
            model.data.timeExp.value = "0.0"
        }

        isExperimentRunning = false
        finalizeExperiment()

        when (cause) {
            "" -> {
                model.data.result.value = "??????????????"
                appendMessageToLog(LogTag.MESSAGE, "?????????????????? ?????????????????? ??????????????")
            }
            else -> {
                model.data.result.value = "????????????????"
                appendMessageToLog(LogTag.ERROR, "?????????????????? ???????????????? ???? ??????????????: $cause")
            }
        }
        protocolModel.dptLOADResult = model.data.result.value
        restoreData()
    }

    private fun regulationTo(
        amperageSet: Double,
        coarseLimit: Double = 10.0,
        fineLimit: Double = 2.0,
        coarseSleep: Long = 500,
        fineSleep: Long = 750
    ) {
        while (abs(amperageOY - amperageSet) > coarseLimit && isExperimentRunning) {
            if (amperageOY < amperageSet) {
                fDelta -= 0.1
                currentFreqLast = currentFreq
                sleep(coarseSleep)
                if (currentFreqLast < currentFreq) {
                    cause = "???? ?????????????? ?????????????????? ??????, ?????????????????? ???????????????????? ?? ??????"
                }
            } else {
                fDelta += 0.1
                sleep(coarseSleep)
            }
            delta.setObjectF(fDelta)
        }
        while (abs(amperageOY - amperageSet) > fineLimit && isExperimentRunning) {
            if (amperageOY < amperageSet) {
                fDelta -= 0.05
                currentFreqLast = currentFreq
                sleep(fineSleep)
                if (currentFreqLast < currentFreq) {
                    cause = "???? ?????????????? ?????????????????? ??????, ?????????????????? ???????????????????? ?? ??????"
                }
            } else {
                fDelta += 0.05
                sleep(fineSleep)
            }
            delta.setObjectF(fDelta)
        }
    }

    private fun regulationToSpeed(
        rpmSet: Double,
        coarseLimit: Double = 10.0,
        fineLimit: Double = 2.0,
        coarseSleep: Long = 500,
        fineSleep: Long = 750
    ) {
        while (abs(rotateSpeed - rpmSet) > coarseLimit && isExperimentRunning) {
            if (rotateSpeed > rpmSet) {
                fDelta -= 0.1
            } else {
                fDelta += 0.1
            }
            delta.setObjectF(fDelta)
            sleep(coarseSleep)
        }
        while (abs(rotateSpeed - rpmSet) > fineLimit && isExperimentRunning) {
            if (rotateSpeed > rpmSet) {
                fDelta -= 0.05
            } else {
                fDelta += 0.05
            }
            delta.setObjectF(fDelta)
            sleep(fineSleep)
        }
    }

    private fun voltageRegulation(volt: Double, coarse: Int = 10, fine: Int = 5, accurate: Int = 2) {
        var timer = 0L
        var speedPerc = 100f
        var timePulsePerc = 20f
        val up = 220f
        val down = 1f
        var direction: Float
        timer = System.currentTimeMillis()
//        appendMessageToLog(LogTag.DEBUG, "?????????????? ??????????????????????")
        while (abs(voltageOV - volt) > fine && isExperimentRunning) {
            if (voltageOV < volt) {
                direction = up
                speedPerc = 100f
            } else {
                direction = down
                speedPerc = 100f
            }
            if (System.currentTimeMillis() - timer > 180000) cause = "?????????????????? ?????????? ??????????????????????????"
            latr.startUpLATRPulse(direction, false, speedPerc)
        }
        latr.stopLATR()
//        timer = System.currentTimeMillis()
//        if (isExperimentRunning) {
//            appendMessageToLog(LogTag.DEBUG, "???????????? ??????????????????????")
//        }
//        while (abs(voltageOY - volt) > coarse && isExperimentRunning) {
//            if (voltageOY < volt) {
//                direction = up
//                timePulsePerc = 85f
//            } else {
//                direction = down
//                timePulsePerc = 100f
//            }
//            if (System.currentTimeMillis() - timer > 60000) cause = "?????????????????? ?????????? ??????????????????????????"
//            latr.startUpLATRPulse(direction, false, timePulsePerc)
//        }
//        latr.stopLATR()
        timer = System.currentTimeMillis()
//        if (isExperimentRunning) {
//            appendMessageToLog(LogTag.DEBUG, "?????????????? ??????????????????????")
//        }
        while (abs(voltageOV - volt) > accurate && isExperimentRunning) {
            if (voltageOV < volt) {
                direction = up
                timePulsePerc = 70f
            } else {
                direction = down
                timePulsePerc = 100f
            }
            if (System.currentTimeMillis() - timer > 180000) cause = "?????????????????? ?????????? ??????????????????????????"
            latr.startUpLATRPulse(direction, false, timePulsePerc)
            sleep(500)
            latr.stopLATR()
            sleep(500)
        }
        latr.stopLATR()
    }

    private fun voltageRegulationTVN(volt: Double, coarseSleep: Long, fineSleep: Long) {
        val slow = 100.0
        val fast = 20.0
        val accurate = 2.0

        var timer = System.currentTimeMillis()
        while (abs(voltageOY - volt) > slow && isExperimentRunning) {
            if (voltageOY < volt) {
                voltageTVN += 0.01
                pr102.setTVN(voltageTVN)
            } else {
                voltageTVN -= 0.01
                pr102.setTVN(voltageTVN)
            }
            if (System.currentTimeMillis() - timer > 90000) cause = "?????????????????? ?????????? ??????????????????????????"
            sleep(coarseSleep)
        }

        timer = System.currentTimeMillis()
        while (abs(voltageOY - volt) > fast && isExperimentRunning) {
            if (voltageOY < volt) {
                voltageTVN += 0.005
                pr102.setTVN(voltageTVN)
            } else {
                voltageTVN -= 0.005
                pr102.setTVN(voltageTVN)
            }
            if (System.currentTimeMillis() - timer > 90000) cause = "?????????????????? ?????????? ??????????????????????????"
            sleep(fineSleep)
        }

        timer = System.currentTimeMillis()
        while (abs(voltageOY - volt) > accurate && isExperimentRunning) {
            if (voltageOY < volt) {
                voltageTVN += 0.003
                pr102.setTVN(voltageTVN)
            } else {
                voltageTVN -= 0.003
                pr102.setTVN(voltageTVN)
            }
            if (System.currentTimeMillis() - timer > 90000) cause = "?????????????????? ?????????? ??????????????????????????"
            sleep(fineSleep)
        }
    }

    private fun voltageRegulationTRN(volt: Double, coarseSleep: Long, fineSleep: Long) {
        val slow = 100.0
        val fast = 20.0
        val accurate = 2.0

        var timer = System.currentTimeMillis()
        while (abs(voltageOV - volt) > slow && isExperimentRunning) {
            if (voltageOV < volt) {
                voltageTRN += 0.01
                pr102.setTRN(voltageTRN)
            } else {
                voltageTRN -= 0.01
                pr102.setTRN(voltageTRN)
            }
            if (System.currentTimeMillis() - timer > 90000) cause = "?????????????????? ?????????? ??????????????????????????"
            sleep(coarseSleep)
        }

        timer = System.currentTimeMillis()
        while (abs(voltageOV - volt) > fast && isExperimentRunning) {
            if (voltageOV < volt) {
                voltageTRN += 0.005
                pr102.setTRN(voltageTRN)
            } else {
                voltageTRN -= 0.005
                pr102.setTRN(voltageTRN)
            }
            if (System.currentTimeMillis() - timer > 90000) cause = "?????????????????? ?????????? ??????????????????????????"
            sleep(fineSleep)
        }

        timer = System.currentTimeMillis()
        while (abs(voltageOV - volt) > accurate && isExperimentRunning) {
            if (voltageOV < volt) {
                voltageTRN += 0.003
                pr102.setTRN(voltageTRN)
            } else {
                voltageTRN -= 0.003
                pr102.setTRN(voltageTRN)
            }
            if (System.currentTimeMillis() - timer > 90000) cause = "?????????????????? ?????????? ??????????????????????????"
            sleep(fineSleep)
        }

    }

    override fun stop() {
        cause = "???????????????? ????????????????????"
        model.data.result.value = "????????????????"
    }

    private fun saveData() {
//        protocolModel.dptLOADN = model.data.n.value
//        protocolModel.dptLOADP1 = model.data.p.value
//        protocolModel.dptLOADTOI = model.data.tempOI.value
//        protocolModel.dptLOADTAmb = model.data.tempAmb.value
//        protocolModel.dptLOADiOV = model.data.iOV.value
//        protocolModel.dptLOADuOV = model.data.uOV.value
//        protocolModel.dptLOADuN = model.data.uOY.value
//        val listDots = mutableListOf<String>()
//        if (sparkingTime.isNotEmpty()) {
//            for (i in 0 until sparkingTime.size) {
//                listDots.add(sparkingTime[i])
//            }
//            for (i in 0 until sparkingTime.size) {
//                listDots.add(sparking1[i])
//            }
//            for (i in 0 until sparkingTime.size) {
//                listDots.add(sparking2[i])
//            }
//            for (i in 0 until sparkingTime.size) {
//                listDots.add(sparking3[i])
//            }
//            for (i in 0 until sparkingTime.size) {
//                listDots.add(sparking4[i])
//            }
//        }
//        protocolModel.dptLOADDots = listDots.toString()
//        protocolModel.dptLOADiN = model.data.iOY.value


        protocolModel.dptLOADN = "model.data.n.value"
        protocolModel.dptLOADP1 = "model.data.p.value"
        protocolModel.dptLOADTOI = "model.data.tempOI.value"
        protocolModel.dptLOADTAmb = "model.data.tempAmb.value"
        protocolModel.dptLOADiOV = "model.data.iOV.value"
        protocolModel.dptLOADuOV = "model.data.uOV.value"
        protocolModel.dptLOADuN = "model.data.uOY.value"
        val listDots = mutableListOf<String>()
        for (i in 0..9) {
            listDots.add("00:00:0${i}")
        }
        for (i in 0..9) {
            listDots.add("${i + 0}")
        }
        for (i in 0..9) {
            listDots.add("${i + 1}")
        }
        for (i in 0..9) {
            listDots.add("${i + 2}")
        }
        for (i in 0..9) {
            listDots.add("${i + 3}")
        }
        if (sparkingTime.isNotEmpty()) {
            for (i in 0 until sparkingTime.size) {
                listDots.add(sparkingTime[i])
            }
            for (i in 0 until sparkingTime.size) {
                listDots.add(sparking1[i])
            }
            for (i in 0 until sparkingTime.size) {
                listDots.add(sparking2[i])
            }
            for (i in 0 until sparkingTime.size) {
                listDots.add(sparking3[i])
            }
            for (i in 0 until sparkingTime.size) {
                listDots.add(sparking4[i])
            }
        }
        protocolModel.dptLOADDots = listDots.toString()
        protocolModel.dptLOADiN = "model.data.iOY.value"
    }

    private fun restoreData() {
        model.data.n.value = protocolModel.dptLOADN
        model.data.p.value = protocolModel.dptLOADP1
        // model.data.result.value = protocolModel.dptLOADResult
        model.data.tempOI.value = protocolModel.dptLOADTOI
        model.data.tempAmb.value = protocolModel.dptLOADTAmb
        model.data.iOV.value = protocolModel.dptLOADiOV
        model.data.uOV.value = protocolModel.dptLOADuOV
        model.data.uOY.value = protocolModel.dptLOADuN
        model.data.iOY.value = protocolModel.dptLOADiN
    }
}