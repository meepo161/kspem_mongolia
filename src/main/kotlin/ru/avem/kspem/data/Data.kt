package ru.avem.kspem.data

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.controllers.expControllers.*
import ru.avem.kspem.controllers.expControllersGPT.NControllerGPT
import ru.avem.kspem.controllers.expControllersMPT.*
import ru.avem.kspem.controllers.expControllersSD.NControllerSD
import ru.avem.kspem.controllers.expControllersSG.H_HHControllerSG
import ru.avem.kspem.controllers.expControllersSG.KZControllerSG
import ru.avem.kspem.controllers.expControllersSG.NControllerSG
import ru.avem.kspem.database.entities.TestObjects

data class MainViewTable(
    val p2: StringProperty = SimpleStringProperty(""),
    val uN: StringProperty = SimpleStringProperty(""),
    val iN: StringProperty = SimpleStringProperty(""),
    val uOV: StringProperty = SimpleStringProperty(""),
    val iOV: StringProperty = SimpleStringProperty(""),
    val nAsync: StringProperty = SimpleStringProperty("")
)

var objectModel = listOf<TestObjects>().firstOrNull()

val motorType = MotorType()
data class MotorType(
    val sd: String = "СД",
    val sg: String = "СГ",
    val dpt: String = "ДПТ",
    val gpt: String = "ГПТ"
)

val schemeType = SchemeType()
data class SchemeType(
    val triangle: String = "△",
    val star: String = "λ"
)

val mgr = MGRController()
val mgrMPT = MGRControllerMPT()
val viu = VIUController()
val ikas = IKASController()

val ikasMPT = IKASControllerMPT()

val nSD = NControllerSD()
val nSG = NControllerSG()
val nMPT = NControllerMPT()
val nGPT = NControllerGPT()

val hh = HHController()
val hhMPT = HHControllerMPT()

val h_hh = H_HHController()
val h_hhSG = H_HHControllerSG()

//val load = LoadController()
val loadMPT = LoadControllerMPT()

val n = NController()
val ktr = KTRController()
val mv = MVController()

val kz = KZController()
val kzSG = KZControllerSG()

var list = mutableListOf<CustomController>()

val protocolModel = ProtocolModel()
data class ProtocolModel(
    var objectName:String = "",
    var type:String = "",
    var date:String = "",
    var time:String = "",
    var operator:String = "",
    var serial:String = "",
    var p2:String = "",
    var uN:String = "",
    var iN:String = "",
    var uOV:String = "",
    var iOV:String = "",
    var nAsync:String = "",
    var kpd:String = "",
    var cos:String = "",
    var scheme:String = "",
//MGR//,
    var mgrU1:String = "",
    var mgrU2:String = "",
    var mgrU3:String = "",
    var mgrR151:String = "",
    var mgrR152:String = "",
    var mgrR153:String = "",
    var mgrR601:String = "",
    var mgrR602:String = "",
    var mgrR603:String = "",
    var mgrkABS1:String = "",
    var mgrkABS2:String = "",
    var mgrkABS3:String = "",
    var mgrTemp:String = "",
    var mgrResult1:String = "",
    var mgrResult2:String = "",
    var mgrResult3:String = "",
//VIU//,
    var viuU:String = "",
    var viuI:String = "",
    var viuTime:String = "",
    var viuResult:String = "",
//IKAS//,
    var ikasR1:String = "",
    var ikasR2:String = "",
    var ikasR3:String = "",
    var ikasResult:String = "",
//N_DPT//,
    var dptNuOV:String = "",
    var dptNiOV:String = "",
    var dptNuN:String = "",
    var dptNiN:String = "",
    var dptNP1:String = "",
    var dptNTOI:String = "",
    var dptNTAmb:String = "",
    var dptNN:String = "",
    var dptNResult:String = "",
//HH_DPT//,
    var dptHHuOV:String = "",
    var dptHHiOV:String = "",
    var dptHHuN:String = "",
    var dptHHiN:String = "",
    var dptHHP1:String = "",
    var dptHHTOI:String = "",
    var dptHHTAmb:String = "",
    var dptHHN:String = "",
    var dptHHResult:String = "",
    var dptHHTime:String = "",
//LOAD_DPT//,
    var dptLOADuOV:String = "",
    var dptLOADiOV:String = "",
    var dptLOADuN:String = "",
    var dptLOADiN:String = "",
    var dptLOADP1:String = "",
    var dptLOADTOI:String = "",
    var dptLOADTAmb:String = "",
    var dptLOADN:String = "",
    var dptLOADDots:String = "",
    var dptLOADResult:String = "",
//N_GPT//,
    var gptNuOV:String = "",
    var gptNiOV:String = "",
    var gptNuN:String = "",
    var gptNiN:String = "",
    var gptNP1:String = "",
    var gptNTOI:String = "",
    var gptNTAmb:String = "",
    var gptNN:String = "",
    var gptNResult:String = "",
///////////
//N//,
    var nUAB:String = "",
    var nUBC:String = "",
    var nUCA:String = "",
    var nIA:String = "",
    var nIB:String = "",
    var nIC:String = "",
    var nF:String = "",
    var nTempOI:String = "",
    var nTempAmb:String = "",
    var nSpeed:String = "",
    var nVibro1:String = "",
    var nVibro2:String = "",
    var nTime:String = "",
    var nP1:String = "",
    var nCos:String = "",
    var nResult:String = "",
//HH//,
//    var hhUAB:String = "",
//    var hhUBC:String = "",
//    var hhUCA:String = "",
//    var hhIA:String = "",
//    var hhIB:String = "",
//    var hhIC:String = "",
//    var hhUOV:String = "",
//    var hhIOV:String = "",
//    var hhTempOI:String = "",
//    var hhTempAmb:String = "",
//    var hhSpeed:String = "",
//    var hhVibro1:String = "",
//    var hhVibro2:String = "",
//    var hhTime:String = "",
//    var hhP1:String = "",
//    var hhCos:String = "",
//    var hhF:String = "",
//    var hhResult:String = "",
//H_HH//,
    var h_hhuAB1:String = "",
    var h_hhuBC1:String = "",
    var h_hhuCA1:String = "",
    var h_hhiA1:String = "",
    var h_hhiB1:String = "",
    var h_hhiC1:String = "",
    var h_hhuOV1:String = "",
    var h_hhiOV1:String = "",

    var h_hhuAB2:String = "",
    var h_hhuBC2:String = "",
    var h_hhuCA2:String = "",
    var h_hhiA2:String = "",
    var h_hhiB2:String = "",
    var h_hhiC2:String = "",
    var h_hhuOV2:String = "",
    var h_hhiOV2:String = "",

    var h_hhuAB3:String = "",
    var h_hhuBC3:String = "",
    var h_hhuCA3:String = "",
    var h_hhiA3:String = "",
    var h_hhiB3:String = "",
    var h_hhiC3:String = "",
    var h_hhuOV3:String = "",
    var h_hhiOV3:String = "",

    var h_hhuAB4:String = "",
    var h_hhuBC4:String = "",
    var h_hhuCA4:String = "",
    var h_hhiA4:String = "",
    var h_hhiB4:String = "",
    var h_hhiC4:String = "",
    var h_hhuOV4:String = "",
    var h_hhiOV4:String = "",

    var h_hhuAB5:String = "",
    var h_hhuBC5:String = "",
    var h_hhuCA5:String = "",
    var h_hhiA5:String = "",
    var h_hhiB5:String = "",
    var h_hhiC5:String = "",
    var h_hhuOV5:String = "",
    var h_hhiOV5:String = "",

    var h_hhuAB6:String = "",
    var h_hhuBC6:String = "",
    var h_hhuCA6:String = "",
    var h_hhiA6:String = "",
    var h_hhiB6:String = "",
    var h_hhiC6:String = "",
    var h_hhuOV6:String = "",
    var h_hhiOV6:String = "",

    var h_hhuAB7:String = "",
    var h_hhuBC7:String = "",
    var h_hhuCA7:String = "",
    var h_hhiA7:String = "",
    var h_hhiB7:String = "",
    var h_hhiC7:String = "",
    var h_hhuOV7:String = "",
    var h_hhiOV7:String = "",

    var h_hhuAB8:String = "",
    var h_hhuBC8:String = "",
    var h_hhuCA8:String = "",
    var h_hhiA8:String = "",
    var h_hhiB8:String = "",
    var h_hhiC8:String = "",
    var h_hhuOV8:String = "",
    var h_hhiOV8:String = "",

    var h_hhuAB9:String = "",
    var h_hhuBC9:String = "",
    var h_hhuCA9:String = "",
    var h_hhiA9:String = "",
    var h_hhiB9:String = "",
    var h_hhiC9:String = "",
    var h_hhuOV9:String = "",
    var h_hhiOV9:String = "",

    var h_hhResult:String = "",
//MV//,
//    var mvUAB1:String = "",
//    var mvUBC1:String = "",
//    var mvUCA1:String = "",
//    var mvIA1:String = "",
//    var mvIB1:String = "",
//    var mvIC1:String = "",
//    var mvUAB2:String = "",
//    var mvUBC2:String = "",
//    var mvUCA2:String = "",
//    var mvIA2:String = "",
//    var mvIB2:String = "",
//    var mvIC2:String = "",
//    var mvDeviation:String = "",
//    var mvResult:String = "",
//KZ//,
    var kzN1:String = "",
    var kzCos1:String = "",
    var kzUOV1:String = "",
    var kzIOV1:String = "",
    var kzUAB1:String = "",
    var kzUBC1:String = "",
    var kzUCA1:String = "",
    var kzIA1:String = "",
    var kzIB1:String = "",
    var kzIC1:String = "",
    var kzP1:String = "",
    var kzF1:String = "",

    var kzN2:String = "",
    var kzCos2:String = "",
    var kzUOV2:String = "",
    var kzIOV2:String = "",
    var kzUAB2:String = "",
    var kzUBC2:String = "",
    var kzUCA2:String = "",
    var kzIA2:String = "",
    var kzIB2:String = "",
    var kzIC2:String = "",
    var kzP2:String = "",
    var kzF2:String = "",

    var kzN3:String = "",
    var kzCos3:String = "",
    var kzUOV3:String = "",
    var kzIOV3:String = "",
    var kzUAB3:String = "",
    var kzUBC3:String = "",
    var kzUCA3:String = "",
    var kzIA3:String = "",
    var kzIB3:String = "",
    var kzIC3:String = "",
    var kzP3:String = "",
    var kzF3:String = "",

    var kzN4:String = "",
    var kzCos4:String = "",
    var kzUOV4:String = "",
    var kzIOV4:String = "",
    var kzUAB4:String = "",
    var kzUBC4:String = "",
    var kzUCA4:String = "",
    var kzIA4:String = "",
    var kzIB4:String = "",
    var kzIC4:String = "",
    var kzP4:String = "",
    var kzF4:String = "",

    var kzN5:String = "",
    var kzCos5:String = "",
    var kzUOV5:String = "",
    var kzIOV5:String = "",
    var kzUAB5:String = "",
    var kzUBC5:String = "",
    var kzUCA5:String = "",
    var kzIA5:String = "",
    var kzIB5:String = "",
    var kzIC5:String = "",
    var kzP5:String = "",
    var kzF5:String = "",

    var kzN6:String = "",
    var kzCos6:String = "",
    var kzUOV6:String = "",
    var kzIOV6:String = "",
    var kzUAB6:String = "",
    var kzUBC6:String = "",
    var kzUCA6:String = "",
    var kzIA6:String = "",
    var kzIB6:String = "",
    var kzIC6:String = "",
    var kzP6:String = "",
    var kzF6:String = "",

    var kzResult:String = ""
)
