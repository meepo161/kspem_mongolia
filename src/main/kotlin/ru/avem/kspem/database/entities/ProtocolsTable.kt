package ru.avem.kspem.database.entities

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object ProtocolsTable : IntIdTable() {
    var objectName = varchar("objectName", 64)
    var type = varchar("type", 64)
    var date = varchar("date", 64)
    var operator = varchar("operator", 64)
    var time = varchar("time", 64)
    var serial = varchar("serial", 64)

    var p2 = varchar("p2", 64)
    var uN = varchar("uN", 64)
    var iN = varchar("iN", 64)
    var nAsync = varchar("nAsync", 64)
    var kpd = varchar("kpd", 64)
    var cos = varchar("cos", 64)
    var scheme = varchar("scheme", 64)
    //MGR//
    var mgrU = varchar("mrgU", 64)
    var mgrR15 = varchar("mgrR15", 64)
    var mgrR30 = varchar("mgrR30", 64)
    var mgrkABS = varchar("mgrkABS", 64)
    var mgrTemp = varchar("mgrTempAmb", 64)
    var mgrResult = varchar("mgrResult", 64)

    //VIU//
    var viuU = varchar("viuU", 64)
    var viuI = varchar("viuI", 64)
    var viuTime = varchar("viuTime", 64)
    var viuResult = varchar("viuResult", 64)

    //IKAS//
    var ikasR1 = varchar("ikasR1", 64)
    var ikasR2 = varchar("ikasR2", 64)
    var ikasR3 = varchar("ikasR3", 64)
    var ikasResult = varchar("ikasResult", 64)

    //HH//
    var hhUAB = varchar("hhUAB", 64)
    var hhUBC = varchar("hhUBC", 64)
    var hhUCA = varchar("hhUCA", 64)
    var hhIA = varchar("hhIA", 64)
    var hhIB = varchar("hhIB", 64)
    var hhIC = varchar("hhIC", 64)
    var hhTempOI = varchar("hhTempOI", 64)
    var hhTempAmb = varchar("hhTempAmb", 64)
    var hhSpeed = varchar("hhSpeed", 64)
    var hhVibro1 = varchar("hhVibro1", 64)
    var hhVibro2 = varchar("hhVibro2", 64)
    var hhP1 = varchar("hhP1", 64)
    var hhCos = varchar("hhCos", 64)
    var hhTime = varchar("hhTime", 64)
    var hhResult = varchar("hhResult", 64)

    //RUNNING//
    var runningUAB = varchar("runningUAB", 64)
    var runningUBC = varchar("runningUBC", 64)
    var runningUCA = varchar("runningUCA", 64)
    var runningIA = varchar("runningIA", 64)
    var runningIB = varchar("runningIB", 64)
    var runningIC = varchar("runningIC", 64)
    var runningTempOI = varchar("runningTempOI", 64)
    var runningTempAmb = varchar("runningTempAmb", 64)
    var runningSpeed = varchar("runningSpeed", 64)
    var runningVibro1 = varchar("runningVibro1", 64)
    var runningVibro2 = varchar("runningVibro2", 64)
    var runningP1 = varchar("runningP1", 64)
    var runningCos = varchar("runningCos", 64)
    var runningTime = varchar("runningTime", 64)
    var runningResult = varchar("runningResult", 64)
    //H_HH//
//    var h_hhUAB1 = varchar("h_hhUAB1", 64)
//    var h_hhUBC1 = varchar("h_hhUBC1", 64)
//    var h_hhUCA1 = varchar("h_hhUCA1", 64)
//    var h_hhIA1 = varchar("h_hhIA1", 64)
//    var h_hhIB1 = varchar("h_hhIB1", 64)
//    var h_hhIC1 = varchar("h_hhIC1", 64)
//    var h_hhUAB2 = varchar("h_hhUAB2", 64)
//    var h_hhUBC2 = varchar("h_hhUBC2", 64)
//    var h_hhUCA2 = varchar("h_hhUCA2", 64)
//    var h_hhIA2 = varchar("h_hhIA2", 64)
//    var h_hhIB2 = varchar("h_hhIB2", 64)
//    var h_hhIC2 = varchar("h_hhIC2", 64)
//    var h_hhUAB3 = varchar("h_hhUAB3", 64)
//    var h_hhUBC3 = varchar("h_hhUBC3", 64)
//    var h_hhUCA3 = varchar("h_hhUCA3", 64)
//    var h_hhIA3 = varchar("h_hhIA3", 64)
//    var h_hhIB3 = varchar("h_hhIB3", 64)
//    var h_hhIC3 = varchar("h_hhIC3", 64)
//    var h_hhUAB4 = varchar("h_hhUAB4", 64)
//    var h_hhUBC4 = varchar("h_hhUBC4", 64)
//    var h_hhUCA4 = varchar("h_hhUCA4", 64)
//    var h_hhIA4 = varchar("h_hhIA4", 64)
//    var h_hhIB4 = varchar("h_hhIB4", 64)
//    var h_hhIC4 = varchar("h_hhIC4", 64)
//    var h_hhUAB5 = varchar("h_hhUAB5", 64)
//    var h_hhUBC5 = varchar("h_hhUBC5", 64)
//    var h_hhUCA5 = varchar("h_hhUCA5", 64)
//    var h_hhIA5 = varchar("h_hhIA5", 64)
//    var h_hhIB5 = varchar("h_hhIB5", 64)
//    var h_hhIC5 = varchar("h_hhIC5", 64)
//    var h_hhUAB6 = varchar("h_hhUAB6", 64)
//    var h_hhUBC6 = varchar("h_hhUBC6", 64)
//    var h_hhUCA6 = varchar("h_hhUCA6", 64)
//    var h_hhIA6 = varchar("h_hhIA6", 64)
//    var h_hhIB6 = varchar("h_hhIB6", 64)
//    var h_hhIC6 = varchar("h_hhIC6", 64)
//    var h_hhUAB7 = varchar("h_hhUAB7", 64)
//    var h_hhUBC7 = varchar("h_hhUBC7", 64)
//    var h_hhUCA7 = varchar("h_hhUCA7", 64)
//    var h_hhIA7 = varchar("h_hhIA7", 64)
//    var h_hhIB7 = varchar("h_hhIB7", 64)
//    var h_hhIC7 = varchar("h_hhIC7", 64)
//    var h_hhUAB8 = varchar("h_hhUAB8", 64)
//    var h_hhUBC8 = varchar("h_hhUBC8", 64)
//    var h_hhUCA8 = varchar("h_hhUCA8", 64)
//    var h_hhIA8 = varchar("h_hhIA8", 64)
//    var h_hhIB8 = varchar("h_hhIB8", 64)
//    var h_hhIC8 = varchar("h_hhIC8", 64)
//    var h_hhUAB9 = varchar("h_hhUAB9", 64)
//    var h_hhUBC9 = varchar("h_hhUBC9", 64)
//    var h_hhUCA9 = varchar("h_hhUCA9", 64)
//    var h_hhIA9 = varchar("h_hhIA9", 64)
//    var h_hhIB9 = varchar("h_hhIB9", 64)
//    var h_hhIC9 = varchar("h_hhIC9", 64)
//    var h_hhN1 = varchar("h_hhN1", 64)
//    var h_hhN2 = varchar("h_hhN2", 64)
//    var h_hhN3 = varchar("h_hhN3", 64)
//    var h_hhN4 = varchar("h_hhN4", 64)
//    var h_hhN5 = varchar("h_hhN5", 64)
//    var h_hhN6 = varchar("h_hhN6", 64)
//    var h_hhN7 = varchar("h_hhN7", 64)
//    var h_hhN8 = varchar("h_hhN8", 64)
//    var h_hhN9 = varchar("h_hhN9", 64)
//    var h_hhResult = varchar("h_hhResult", 64)
    //KTr//
    var ktrUAVG1 = varchar("ktrUAVG1", 64)
    var ktrUAVG2 = varchar("ktrUAVG2", 64)
    var ktrKTR = varchar("ktrKTR", 64)
    var ktrResult = varchar("ktrResult", 64)
    //N//
    var nUAB = varchar("nUAB", 64)
    var nUBC = varchar("nUBC", 64)
    var nUCA = varchar("nUCA", 64)
    var nIA = varchar("nIA", 64)
    var nIB = varchar("nIB", 64)
    var nIC = varchar("nIC", 64)
    var nSpeed = varchar("nSpeed", 64)
    var nF = varchar("nF", 64)
    var nResult = varchar("nResult", 64)
    //MV//
    var mvUAB1 = varchar("mvUAB1", 64)
    var mvUBC1 = varchar("mvUBC1", 64)
    var mvUCA1 = varchar("mvUCA1", 64)
    var mvIA1 = varchar("mvIA1", 64)
    var mvIB1 = varchar("mvIB1", 64)
    var mvIC1 = varchar("mvIC1", 64)
    var mvUAB2  = varchar("mvUAB2", 64)
    var mvUBC2 = varchar("mvUBC2", 64)
    var mvUCA2 = varchar("mvUCA2", 64)
    var mvIA2 = varchar("mvIA2", 64)
    var mvIB2 = varchar("mvIB2", 64)
    var mvIC2 = varchar("mvIC2", 64)
    var mvDeviation = varchar("mvDeviation", 64)
    var mvResult = varchar("mvResult", 64)

    //KZ//
    var kzUAB = varchar("kzUAB", 64)
    var kzUBC = varchar("kzUBC", 64)
    var kzUCA = varchar("kzUCA", 64)
    var kzIA = varchar("kzIA", 64)
    var kzIB = varchar("kzIB", 64)
    var kzIC = varchar("kzIC", 64)
    var kzP1 = varchar("kzP1", 64)
    var kzResult = varchar("kzResult", 64)
}

class Protocol(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Protocol>(ProtocolsTable)

    var objectName by ProtocolsTable.objectName
    var type by ProtocolsTable.type
    var date by ProtocolsTable.date
    var time by ProtocolsTable.time
    var operator by ProtocolsTable.operator
    var serial by ProtocolsTable.serial

    var p2 by ProtocolsTable.p2
    var uN by ProtocolsTable.uN
    var iN by ProtocolsTable.iN
    var nAsync by ProtocolsTable.nAsync
    var kpd by ProtocolsTable.kpd
    var cos by ProtocolsTable.cos
    var scheme by ProtocolsTable.scheme
    //MGR//
    var mgrU by ProtocolsTable.mgrU
    var mgrR15 by ProtocolsTable.mgrR15
    var mgrR60 by ProtocolsTable.mgrR30
    var mgrkABS by ProtocolsTable.mgrkABS
    var mgrTemp by ProtocolsTable.mgrTemp
    var mgrResult by ProtocolsTable.mgrResult

    //VIU//
    var viuU by ProtocolsTable.viuU
    var viuI by ProtocolsTable.viuI
    var viuTime by ProtocolsTable.viuTime
    var viuResult by ProtocolsTable.viuResult

    //IKAS//
    var ikasR1 by ProtocolsTable.ikasR1
    var ikasR2 by ProtocolsTable.ikasR2
    var ikasR3 by ProtocolsTable.ikasR3
    var ikasResult by ProtocolsTable.ikasResult

    //HH//
    var hhUAB by ProtocolsTable.hhUAB
    var hhUBC by ProtocolsTable.hhUBC
    var hhUCA by ProtocolsTable.hhUCA
    var hhIA by ProtocolsTable.hhIA
    var hhIB by ProtocolsTable.hhIB
    var hhIC by ProtocolsTable.hhIC
    var hhTempOI by ProtocolsTable.hhTempOI
    var hhTempAmb by ProtocolsTable.hhTempAmb
    var hhSpeed by ProtocolsTable.hhSpeed
    var hhVibro1 by ProtocolsTable.hhVibro1
    var hhVibro2 by ProtocolsTable.hhVibro2
    var hhP1 by ProtocolsTable.hhP1
    var hhCos by ProtocolsTable.hhCos
    var hhTime by ProtocolsTable.hhTime
    var hhResult by ProtocolsTable.hhResult

    //RUNNING//
    var runningUAB by ProtocolsTable.runningUAB
    var runningUBC by ProtocolsTable.runningUBC
    var runningUCA by ProtocolsTable.runningUCA
    var runningIA by ProtocolsTable.runningIA
    var runningIB by ProtocolsTable.runningIB
    var runningIC by ProtocolsTable.runningIC
    var runningTempOI by ProtocolsTable.runningTempOI
    var runningTempAmb by ProtocolsTable.runningTempAmb
    var runningSpeed by ProtocolsTable.runningSpeed
    var runningVibro1 by ProtocolsTable.runningVibro1
    var runningVibro2 by ProtocolsTable.runningVibro2
    var runningTime by ProtocolsTable.runningTime
    var runningP1 by ProtocolsTable.runningP1
    var runningCos by ProtocolsTable.runningCos
    var runningResult by ProtocolsTable.runningResult
    //H_HH//
//    var h_hhUAB1 by ProtocolsTable.h_hhUAB1
//    var h_hhUBC1 by ProtocolsTable.h_hhUBC1
//    var h_hhUCA1 by ProtocolsTable.h_hhUCA1
//    var h_hhIA1 by ProtocolsTable.h_hhIA1
//    var h_hhIB1 by ProtocolsTable.h_hhIB1
//    var h_hhIC1 by ProtocolsTable.h_hhIC1
//    var h_hhUAB2 by ProtocolsTable.h_hhUAB2
//    var h_hhUBC2 by ProtocolsTable.h_hhUBC2
//    var h_hhUCA2 by ProtocolsTable.h_hhUCA2
//    var h_hhIA2 by ProtocolsTable.h_hhIA2
//    var h_hhIB2 by ProtocolsTable.h_hhIB2
//    var h_hhIC2 by ProtocolsTable.h_hhIC2
//    var h_hhUAB3 by ProtocolsTable.h_hhUAB3
//    var h_hhUBC3 by ProtocolsTable.h_hhUBC3
//    var h_hhUCA3 by ProtocolsTable.h_hhUCA3
//    var h_hhIA3 by ProtocolsTable.h_hhIA3
//    var h_hhIB3 by ProtocolsTable.h_hhIB3
//    var h_hhIC3 by ProtocolsTable.h_hhIC3
//    var h_hhUAB4 by ProtocolsTable.h_hhUAB4
//    var h_hhUBC4 by ProtocolsTable.h_hhUBC4
//    var h_hhUCA4 by ProtocolsTable.h_hhUCA4
//    var h_hhIA4 by ProtocolsTable.h_hhIA4
//    var h_hhIB4 by ProtocolsTable.h_hhIB4
//    var h_hhIC4 by ProtocolsTable.h_hhIC4
//    var h_hhUAB5 by ProtocolsTable.h_hhUAB5
//    var h_hhUBC5 by ProtocolsTable.h_hhUBC5
//    var h_hhUCA5 by ProtocolsTable.h_hhUCA5
//    var h_hhIA5 by ProtocolsTable.h_hhIA5
//    var h_hhIB5 by ProtocolsTable.h_hhIB5
//    var h_hhIC5 by ProtocolsTable.h_hhIC5
//    var h_hhUAB6 by ProtocolsTable.h_hhUAB6
//    var h_hhUBC6 by ProtocolsTable.h_hhUBC6
//    var h_hhUCA6 by ProtocolsTable.h_hhUCA6
//    var h_hhIA6 by ProtocolsTable.h_hhIA6
//    var h_hhIB6 by ProtocolsTable.h_hhIB6
//    var h_hhIC6 by ProtocolsTable.h_hhIC6
//    var h_hhUAB7 by ProtocolsTable.h_hhUAB7
//    var h_hhUBC7 by ProtocolsTable.h_hhUBC7
//    var h_hhUCA7 by ProtocolsTable.h_hhUCA7
//    var h_hhIA7 by ProtocolsTable.h_hhIA7
//    var h_hhIB7 by ProtocolsTable.h_hhIB7
//    var h_hhIC7 by ProtocolsTable.h_hhIC7
//    var h_hhUAB8 by ProtocolsTable.h_hhUAB8
//    var h_hhUBC8 by ProtocolsTable.h_hhUBC8
//    var h_hhUCA8 by ProtocolsTable.h_hhUCA8
//    var h_hhIA8 by ProtocolsTable.h_hhIA8
//    var h_hhIB8 by ProtocolsTable.h_hhIB8
//    var h_hhIC8 by ProtocolsTable.h_hhIC8
//    var h_hhUAB9 by ProtocolsTable.h_hhUAB9
//    var h_hhUBC9 by ProtocolsTable.h_hhUBC9
//    var h_hhUCA9 by ProtocolsTable.h_hhUCA9
//    var h_hhIA9 by ProtocolsTable.h_hhIA9
//    var h_hhIB9 by ProtocolsTable.h_hhIB9
//    var h_hhIC9 by ProtocolsTable.h_hhIC9
//    var h_hhN1 by ProtocolsTable.h_hhN1
//    var h_hhN2 by ProtocolsTable.h_hhN2
//    var h_hhN3 by ProtocolsTable.h_hhN3
//    var h_hhN4 by ProtocolsTable.h_hhN4
//    var h_hhN5 by ProtocolsTable.h_hhN5
//    var h_hhN6 by ProtocolsTable.h_hhN6
//    var h_hhN7 by ProtocolsTable.h_hhN7
//    var h_hhN8 by ProtocolsTable.h_hhN8
//    var h_hhN9 by ProtocolsTable.h_hhN9
//    var h_hhResult by ProtocolsTable.h_hhResult
    //N//
    var nUAB by ProtocolsTable.nUAB
    var nUBC by ProtocolsTable.nUBC
    var nUCA by ProtocolsTable.nUCA
    var nIA by ProtocolsTable.nIA
    var nIB by ProtocolsTable.nIB
    var nIC by ProtocolsTable.nIC
    var nSpeed by ProtocolsTable.nSpeed
    var nF by ProtocolsTable.nF
    var nResult by ProtocolsTable.nResult
    //KTn/
    var ktrUAVG1 by ProtocolsTable.ktrUAVG1
    var ktrUAVG2 by ProtocolsTable.ktrUAVG2
    var ktrKTR by ProtocolsTable.ktrKTR
    var ktrResult by ProtocolsTable.ktrResult
    //MV//
    var mvUAB1 by ProtocolsTable.mvUAB1
    var mvUBC1 by ProtocolsTable.mvUBC1
    var mvUCA1 by ProtocolsTable.mvUCA1
    var mvIA1 by ProtocolsTable.mvIA1
    var mvIB1 by ProtocolsTable.mvIB1
    var mvIC1 by ProtocolsTable.mvIC1
    var mvUAB2  by ProtocolsTable.mvUAB2
    var mvUBC2 by ProtocolsTable.mvUBC2
    var mvUCA2 by ProtocolsTable.mvUCA2
    var mvIA2 by ProtocolsTable.mvIA2
    var mvIB2 by ProtocolsTable.mvIB2
    var mvIC2 by ProtocolsTable.mvIC2
    var mvDeviation by ProtocolsTable.mvDeviation
    var mvResult by ProtocolsTable.mvResult
    //KZ//
    var kzUAB by ProtocolsTable.kzUAB
    var kzUBC by ProtocolsTable.kzUBC
    var kzUCA by ProtocolsTable.kzUCA
    var kzIA by ProtocolsTable.kzIA
    var kzIB by ProtocolsTable.kzIB
    var kzIC by ProtocolsTable.kzIC
    var kzP1 by ProtocolsTable.kzP1
    var kzResult by ProtocolsTable.kzResult

    override fun toString(): String {
        return "$id"
    }
}
