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
//    val ad: String = "АД"
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

val mgr             = MGRController()
val mgrMPT             = MGRControllerMPT()
val viu         = VIUController()
val ikas                = IKASController()

val ikasMPT         = IKASControllerMPT()

val nSD         = NControllerSD()
val nSG         = NControllerSG()
val nMPT        = NControllerMPT()
val nGPT        = NControllerGPT()

val hh          = HHController()
val hhMPT       = HHControllerMPT()

val h_hh        = H_HHController()
val h_hhSG      = H_HHControllerSG()

val load        = LoadController()
val loadMPT         = LoadControllerMPT()

val n           = NController()
val ktr             = KTRController()
val mv          = MVController()

val kz          = KZController()
val kzSG        = KZControllerSG()

var list        = mutableListOf<CustomController>()

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
    var nAsync:String = "",
    var kpd:String = "",
    var cos:String = "",
    var scheme:String = "",
//MGR//,
    var mgrU:String = "",
    var mgrR15:String = "",
    var mgrR60:String = "",
    var mgrkABS:String = "",
    var mgrTemp:String = "",
    var mgrResult:String = "",
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
//HH//,
    var hhUAB:String = "",
    var hhUBC:String = "",
    var hhUCA:String = "",
    var hhIA:String = "",
    var hhIB:String = "",
    var hhIC:String = "",
    var hhUOV:String = "",
    var hhIOV:String = "",
    var hhTempOI:String = "",
    var hhTempAmb:String = "",
    var hhSpeed:String = "",
    var hhVibro1:String = "",
    var hhVibro2:String = "",
    var hhTime:String = "",
    var hhP1:String = "",
    var hhCos:String = "",
    var hhF:String = "",
    var hhResult:String = "",
//RUNNING//,
    var runningUAB:String = "",
    var runningUBC:String = "",
    var runningUCA:String = "",
    var runningIA:String = "",
    var runningIB:String = "",
    var runningIC:String = "",
    var runningTempOI:String = "",
    var runningTempAmb:String = "",
    var runningSpeed:String = "",
    var runningVibro1:String = "",
    var runningVibro2:String = "",
    var runningTime:String = "",
    var runningP1:String = "",
    var runningCos:String = "",
    var runningResult:String = "",
//LOADMPT//,
    var loadResult          :String = "",
    var loadUOV             :String = "",
    var loadIOV             :String = "",
    var loadUOY             :String = "",
    var loadIOY             :String = "",
    var loadN               :String = "",
    var loadP               :String = "",
    var loadTempAmb         :String = "",
    var loadTempOI          :String = "",
//H_HH//,
//    var h_hhUAB1:String = "",
//    var h_hhUBC1:String = "",
//    var h_hhUCA1:String = "",
//    var h_hhIA1:String = "",
//    var h_hhIB1:String = "",
//    var h_hhIC1:String = "",
//    var h_hhP1:String = "",
//
//    var h_hhUAB2:String = "",
//    var h_hhUBC2:String = "",
//    var h_hhUCA2:String = "",
//    var h_hhIA2:String = "",
//    var h_hhIB2:String = "",
//    var h_hhIC2:String = "",
//    var h_hhP2:String = "",
//
//    var h_hhUAB3:String = "",
//    var h_hhUBC3:String = "",
//    var h_hhUCA3:String = "",
//    var h_hhIA3:String = "",
//    var h_hhIB3:String = "",
//    var h_hhIC3:String = "",
//    var h_hhP3:String = "",
//
//    var h_hhUAB4:String = "",
//    var h_hhUBC4:String = "",
//    var h_hhUCA4:String = "",
//    var h_hhIA4:String = "",
//    var h_hhIB4:String = "",
//    var h_hhIC4:String = "",
//    var h_hhP4:String = "",
//
//    var h_hhUAB5:String = "",
//    var h_hhUBC5:String = "",
//    var h_hhUCA5:String = "",
//    var h_hhIA5:String = "",
//    var h_hhIB5:String = "",
//    var h_hhIC5:String = "",
//    var h_hhP5:String = "",
//
//    var h_hhUAB6:String = "",
//    var h_hhUBC6:String = "",
//    var h_hhUCA6:String = "",
//    var h_hhIA6:String = "",
//    var h_hhIB6:String = "",
//    var h_hhIC6:String = "",
//    var h_hhP6:String = "",
//
//    var h_hhUAB7:String = "",
//    var h_hhUBC7:String = "",
//    var h_hhUCA7:String = "",
//    var h_hhIA7:String = "",
//    var h_hhIB7:String = "",
//    var h_hhIC7:String = "",
//    var h_hhP7:String = "",
//
//    var h_hhUAB8:String = "",
//    var h_hhUBC8:String = "",
//    var h_hhUCA8:String = "",
//    var h_hhIA8:String = "",
//    var h_hhIB8:String = "",
//    var h_hhIC8:String = "",
//    var h_hhP8:String = "",
//
//    var h_hhUAB9:String = "",
//    var h_hhUBC9:String = "",
//    var h_hhUCA9:String = "",
//    var h_hhIA9:String = "",
//    var h_hhIB9:String = "",
//    var h_hhIC9:String = "",
//    var h_hhP9:String = "",
//
//    var h_hhResult:String = "",
//N//,
    var nUAB:String = "",
    var nUBC:String = "",
    var nUCA:String = "",
    var nIA:String = "",
    var nIB:String = "",
    var nIC:String = "",
    var nSpeed:String = "",
    var nF:String = "",
    var nResult:String = "",
//KTn/,
    var ktrUAVG1: String = "",
    var ktrUAVG2: String = "",
    var ktrKTR: String = "",
    var ktrResult: String = "",
//MV//,
    var mvUAB1:String = "",
    var mvUBC1:String = "",
    var mvUCA1:String = "",
    var mvIA1:String = "",
    var mvIB1:String = "",
    var mvIC1:String = "",
    var mvUAB2:String = "",
    var mvUBC2:String = "",
    var mvUCA2:String = "",
    var mvIA2:String = "",
    var mvIB2:String = "",
    var mvIC2:String = "",
    var mvDeviation:String = "",
    var mvResult:String = "",
//KZ//,
    var kzUAB:String = "",
    var kzUBC:String = "",
    var kzUCA:String = "",
    var kzIA:String = "",
    var kzIB:String = "",
    var kzIC:String = "",
    var kzP1:String = "",
    var kzResult:String = ""
)
