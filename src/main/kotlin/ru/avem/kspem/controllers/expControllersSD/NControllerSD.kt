package ru.avem.kspem.controllers.expControllersSD

import ru.avem.kspem.communication.model.CommunicationModel
import ru.avem.kspem.communication.model.devices.avem.avem4.Avem4Model
import ru.avem.kspem.communication.model.devices.avem.avem7.Avem7Model
import ru.avem.kspem.communication.model.devices.delta.DeltaModel
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
    private var setTime = 0.0

    @Volatile
    var deltaStatus = 0

    @Volatile
    var fDelta = 2.0

    @Volatile
    var startDelta = 1 * 10

    private var ktrVoltage = 1.0
    private var ktrAmperage = 400 / 5

    @Volatile
    var voltageDelta = 0.0

    @Volatile
    var voltageTRN = 0.0

    @Volatile
    var voltageOV = 0.0

    @Volatile
    var amperageOV = 0.0

    @Volatile
    var amperage = 0.0

    @Volatile
    var voltageSet = 0.0

    @Volatile
    var voltageAB = 0.0

    @Volatile
    var voltageBC = 0.0

    @Volatile
    var voltageCA = 0.0

    @Volatile
    var voltageOY = 0.0

    @Volatile
    var voltageOVSet = 0.0

    @Volatile
    var rotateSpeed = 0.0

    @Volatile
    var rotateSpeedSet = 0.0

    @Volatile
    var ktrDelta = 1.0

    override fun start() {
        model.clearTables()
        super.start()
        // (value-4)*0.625

        voltageTRN = 0.0
        fDelta = 2.0
        startDelta = 1 * 10
        voltageSet = objectModel!!.uNom.toDouble()
        rotateSpeedSet = objectModel!!.nAsync.toDouble()
        voltageOVSet = objectModel!!.uOV.toDouble()
        setTime = objectModel!!.timeHH.toDouble()


        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "?????????????????????????? ????????4-03...")
            cm.startPoll(CommunicationModel.DeviceID.PV23, Avem4Model.RMS) { value ->
                voltageOV = value.toDouble()
                model.data.uOV.value = voltageOV.autoformat()
                if (!avemUov.isResponding && isExperimentRunning) cause = "????????4-03 ???? ????????????????"
            }
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "?????????????????????????? ????????4-01...")
            cm.startPoll(CommunicationModel.DeviceID.PA13, Avem7Model.AMPERAGE) { value ->
                amperageOV = value.toDouble()
                model.data.iOV.value = amperageOV.autoformat()
                if (!avemIov.isResponding && isExperimentRunning) cause = "????????4-01 ???? ????????????????"
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
            pr102.setTRN(voltageTRN)
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

            cm.startPoll(CommunicationModel.DeviceID.UZ91, DeltaModel.STATUS_REGISTER) { value ->
                deltaStatus = value.toInt()
                if (!delta.isResponding && isExperimentRunning) cause = "Delta ???? ????????????????"
            }

            cm.startPoll(CommunicationModel.DeviceID.UZ91, DeltaModel.CURRENT_VOLT) { value ->
                voltageAB = value.toInt() / 10.0
                model.data.uAB.value = voltageAB.autoformat()
            }

            cm.startPoll(CommunicationModel.DeviceID.UZ91, DeltaModel.CURRENT_AMPER) { value ->
                model.data.iA.value = (value.toInt() / 100.0).autoformat()
            }
        }

        if (isExperimentRunning) {
            pr102.arn(true)
            pr102.ov_oi(true)
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "???????????? ???????????????????? ???? ?????????????? ??????????????????????")
            voltageRegulationTRN(voltageOVSet, 1000, 1500)
            appendMessageToLog(LogTag.MESSAGE, "???????????? ???????????????????? ???? ?????????????? ?????????????????????? ????????????????")
        }

        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "?????????????????????? ???? ?????????????????????? ?????????????? ????????????????")
            delta.setObjectParamsRun(2, 20, 2)
            delta.startObject()
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
                if (rotateSpeed < 100 || rotateSpeed > rotateSpeedSet * 2) {
                    cause = "?????????????????? ???????????? ????????????????"
                }
                sleep(1000)
            }
        }

        if (isExperimentRunning) {
            for (i in 1..3) {
                regulateToRPM(rotateSpeedSet, 50, 25, 100L, 200L)
                var timer = 2.0
                if (isExperimentRunning) {
                    while (isExperimentRunning && timer > 0) {
                        timer -= 0.1
                        sleep(100)
                    }
                }
            }
        }

        var timer = 120.0 //TODO
        if (isExperimentRunning) {
            appendMessageToLog(LogTag.MESSAGE, "???????????????? 120 ????????????")
            while (isExperimentRunning && timer > 0) {
                timer -= 0.1
                if (timer >= 0) {
                    model.data.timeExp.value = timer.autoformat()
                }
                sleep(100)
            }
            model.data.timeExp.value = "0.0"
        }

        saveData()

        delta.stopObject()
        voltageTRN = 0.0
        pr102.setTRN(voltageTRN)

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
        protocolModel.nResult = model.data.result.value
        restoreData()
    }

    private fun regulateToRPM(
        speed: Double,
        coarseLimit: Int,
        fineLimit: Int,
        coarseSleep: Long,
        fineSleep: Long
    ) {
        while (isExperimentRunning && (rotateSpeed > speed + coarseLimit || rotateSpeed < speed)) {
            if (rotateSpeed < speed + coarseLimit) {
                fDelta += 0.1
                delta.setObjectF(fDelta)
                sleep(coarseSleep)
            } else if (rotateSpeed > speed) {
                fDelta -= 0.1
                delta.setObjectF(fDelta)
                sleep(coarseSleep)
            }
        }

        while (isExperimentRunning && (rotateSpeed > speed + fineLimit || rotateSpeed < speed)) {
            if (rotateSpeed < speed + fineLimit) {
                fDelta += 0.05
                delta.setObjectF(fDelta)
                sleep(fineSleep)
            } else if (rotateSpeed > speed) {
                fDelta -= 0.05
                delta.setObjectF(fDelta)
                sleep(fineSleep)
            }
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
        latr.stopLATR()
    }

    private fun regulation(
        coarseStep: Int,
        fineStep: Int,
        end: Double,
        coarseLimit: Double,
        fineLimit: Double,
        coarseSleep: Int,
        fineSleep: Int
    ): Int {
        val coarseMinLimit = 1 - coarseLimit
        val coarseMaxLimit = 1 + coarseLimit
        var timeOut = 30
        while (isExperimentRunning && (rotateSpeed < end * coarseMinLimit || rotateSpeed > end * coarseMaxLimit) && timeOut-- > 0) {
            if (rotateSpeed < end * coarseMinLimit) {
                delta.setObjectUMax(coarseStep.let { startDelta += it; startDelta })
            } else if (rotateSpeed > end * coarseMaxLimit) {
                delta.setObjectUMax(coarseStep.let { startDelta -= it; startDelta })
            }
            sleep(coarseSleep.toLong())
            appendMessageToLog(LogTag.MESSAGE, "?????????????? ???????????????????? ?????? ?????????????????? ?????????????????? ???????????????? ??????????")
        }
        timeOut = 30
        while (isExperimentRunning && (rotateSpeed < end /*- fineLimit TODO ???????? ???????????? ????????*/ || rotateSpeed > end + fineLimit) && timeOut-- > 0) {
            if (rotateSpeed < end /*- fineLimit*/) {
                delta.setObjectUMax(fineStep.let { startDelta += it; startDelta })
            } else if (rotateSpeed > end + fineLimit) {
                delta.setObjectUMax(fineStep.let { startDelta -= it; startDelta })
            }
            sleep(fineSleep.toLong())
            appendMessageToLog(LogTag.MESSAGE, "?????????????? ???????????????????? ?????? ?????????????????? ?????????????????? ???????????????? ??????????")
        }
        return startDelta
    }

    private fun stopRegulation() {
        val timer = System.currentTimeMillis()
        if (isExperimentRunning) {
            appendMessageToLog(LogTag.DEBUG, "???????????????????? ??????????????????")
        }
        while (fDelta > 0) {
            delta.setObjectF(--fDelta)
            sleep(1000)
            if (System.currentTimeMillis() - timer > 90000) cause = "?????????????????? ?????????? ??????????????????????????"
        }
    }

    override fun stop() {
        cause = "???????????????? ????????????????????"
        model.data.result.value = "????????????????"
    }

    private fun saveData() {
        protocolModel.nUBC = model.data.uOV.value
        protocolModel.nIA = model.data.iOV.value

        protocolModel.nUAB = model.data.uAB.value
        protocolModel.nIB = model.data.iA.value
        protocolModel.nSpeed = model.data.n.value
        protocolModel.nF = model.data.f.value
//        protocolModel.nResult = model.data.result.value
    }

    private fun restoreData() {
        model.data.uAB.value = protocolModel.nUAB
        model.data.uOV.value = protocolModel.nUBC
        model.data.iOV.value = protocolModel.nIA
        model.data.iA.value = protocolModel.nIB
        model.data.n.value = protocolModel.nSpeed
        model.data.f.value = protocolModel.nF
    }
}