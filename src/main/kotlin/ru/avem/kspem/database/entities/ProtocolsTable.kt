package ru.avem.kspem.database.entities

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object ProtocolsTable : IntIdTable() {
    var objectName = varchar("objectName", 128)
    var type = varchar("type", 128)
    var date = varchar("date", 128)
    var time = varchar("time", 128)
    var operator = varchar("operator", 128)
    var serial = varchar("serial", 128)
    var p2 = varchar("p2", 128)
    var uN = varchar("uN", 128)
    var iN = varchar("iN", 128)
    var uOV = varchar("uOV", 128)
    var iOV = varchar("iOV", 128)
    var nAsync = varchar("nAsync", 128)
    var kpd = varchar("kpd", 128)
    var scheme = varchar("scheme", 128)

    //MGR//
    var mgrU1 = varchar("mgrU1", 128)
    var mgrU2 = varchar("mgrU2", 128)
    var mgrU3 = varchar("mgrU3", 128)
    var mgrR151 = varchar("mgrR151", 128)
    var mgrR152 = varchar("mgrR152", 128)
    var mgrR153 = varchar("mgrR153", 128)
    var mgrR601 = varchar("mgrR601", 128)
    var mgrR602 = varchar("mgrR602", 128)
    var mgrR603 = varchar("mgrR603", 128)
    var mgrkABS1 = varchar("mgrkABS1", 128)
    var mgrkABS2 = varchar("mgrkABS2", 128)
    var mgrkABS3 = varchar("mgrkABS3", 128)
    var mgrTemp = varchar("mgrTemp", 128)
    var mgrResult1 = varchar("mgrResult1", 128)
    var mgrResult2 = varchar("mgrResult2", 128)
    var mgrResult3 = varchar("mgrResult3", 128)

    //VIU//
    var viuU = varchar("viuU", 128)
    var viuI = varchar("viuI", 128)
    var viuTime = varchar("viuTime", 128)
    var viuResult = varchar("viuResult", 128)

    //IKAS//
    var ikasR1 = varchar("ikasR1", 128)
    var ikasR2 = varchar("ikasR2", 128)
    var ikasR3 = varchar("ikasR3", 128)
    var ikasResult = varchar("ikasResult", 128)

    //N_DPT//
    var dptNuOV = varchar("dptNuOV", 128)
    var dptNiOV = varchar("dptNiOV", 128)
    var dptNuN = varchar("dptNuN", 128)
    var dptNiN = varchar("dptNiN", 128)
    var dptNP1 = varchar("dptNP1", 128)
    var dptNTOI = varchar("dptNTOI", 128)
    var dptNTAmb = varchar("dptNTAmb", 128)
    var dptNN = varchar("dptNN", 128)
    var dptNResult = varchar("dptNResult", 128)

    //HH_DPT//
    var dptHHuOV = varchar("dptHHuOV", 128)
    var dptHHiOV = varchar("dptHHiOV", 128)
    var dptHHuN = varchar("dptHHuN", 128)
    var dptHHiN = varchar("dptHHiN", 128)
    var dptHHP1 = varchar("dptHHP1", 128)
    var dptHHTOI = varchar("dptHHTOI", 128)
    var dptHHTAmb = varchar("dptHHTAmb", 128)
    var dptHHN = varchar("dptHHN", 128)
    var dptHHResult = varchar("dptHHResult", 128)
    var dptHHTime = varchar("dptHHTime", 128)

    //LOAD_DPT//
    var dptLOADuOV = varchar("dptLOADuOV", 128)
    var dptLOADiOV = varchar("dptLOADiOV", 128)
    var dptLOADuN = varchar("dptLOADuN", 128)
    var dptLOADiN = varchar("dptLOADiN", 128)
    var dptLOADP1 = varchar("dptLOADP1", 128)
    var dptLOADTOI = varchar("dptLOADTOI", 128)
    var dptLOADTAmb = varchar("dptLOADTAmb", 128)
    var dptLOADN = varchar("dptLOADN", 128)
    var dptLOADDots = varchar("dptLOADDots", 8128)
    var dptLOADResult = varchar("dptLOADResult", 128)

    //N_GPT//
    var gptNuOV = varchar("gptNuOV", 128)
    var gptNiOV = varchar("gptNiOV", 128)
    var gptNuN = varchar("gptNuN", 128)
    var gptNiN = varchar("gptNiN", 128)
    var gptNP1 = varchar("gptNP1", 128)
    var gptNTOI = varchar("gptNTOI", 128)
    var gptNTAmb = varchar("gptNTAmb", 128)
    var gptNN = varchar("gptNN", 128)
    var gptNResult = varchar("gptNResult", 128)

    ///////////
    //N//,
    var nUAB = varchar("nUAB", 128)
    var nUBC = varchar("nUBC", 128)
    var nUCA = varchar("nUCA", 128)
    var nIA = varchar("nIA", 128)
    var nIB = varchar("nIB", 128)
    var nIC = varchar("nIC", 128)
    var nF = varchar("nF", 128)
    var nTempOI = varchar("nTempOI", 128)
    var nTempAmb = varchar("nTempAmb", 128)
    var nSpeed = varchar("nSpeed", 128)
    var nVibro1 = varchar("nVibro1", 128)
    var nVibro2 = varchar("nVibro2", 128)
    var nTime = varchar("nTime", 128)
    var nP1 = varchar("nP1", 128)
    var nCos = varchar("nCos", 128)
    var nResult = varchar("nResult", 128)

    //HH//
//    var hhUAB = varchar("hhUAB", 128)
//    var hhUBC = varchar("hhUBC", 128)
//    var hhUCA = varchar("hhUCA", 128)
//    var hhIA = varchar("hhIA", 128)
//    var hhIB = varchar("hhIB", 128)
//    var hhIC = varchar("hhIC", 128)
//    var hhUOV = varchar("hhUOV", 128)
//    var hhIOV = varchar("hhIOV", 128)
//    var hhTempOI = varchar("hhTempOI", 128)
//    var hhTempAmb = varchar("hhTempAmb", 128)
//    var hhSpeed = varchar("hhSpeed", 128)
//    var hhVibro1 = varchar("hhVibro1", 128)
//    var hhVibro2 = varchar("hhVibro2", 128)
//    var hhTime = varchar("hhTime", 128)
//    var hhP1 = varchar("hhP1", 128)
//    var hhF = varchar("hhF", 128)
//    var hhResult = varchar("hhResult", 128)
    //H_HH//
    var h_hhuAB1 = varchar("h_hhuAB1", 128)
    var h_hhuBC1 = varchar("h_hhuBC1", 128)
    var h_hhuCA1 = varchar("h_hhuCA1", 128)
    var h_hhiA1 = varchar("h_hhiA1", 128)
    var h_hhiB1 = varchar("h_hhiB1", 128)
    var h_hhiC1 = varchar("h_hhiC1", 128)
    var h_hhuOV1 = varchar("h_hhuOV1", 128)
    var h_hhiOV1 = varchar("h_hhiOV1", 128)

    var h_hhuAB2 = varchar("h_hhuAB2", 128)
    var h_hhuBC2 = varchar("h_hhuBC2", 128)
    var h_hhuCA2 = varchar("h_hhuCA2", 128)
    var h_hhiA2 = varchar("h_hhiA2", 128)
    var h_hhiB2 = varchar("h_hhiB2", 128)
    var h_hhiC2 = varchar("h_hhiC2", 128)
    var h_hhuOV2 = varchar("h_hhuOV2", 128)
    var h_hhiOV2 = varchar("h_hhiOV2", 128)

    var h_hhuAB3 = varchar("h_hhuAB3", 128)
    var h_hhuBC3 = varchar("h_hhuBC3", 128)
    var h_hhuCA3 = varchar("h_hhuCA3", 128)
    var h_hhiA3 = varchar("h_hhiA3", 128)
    var h_hhiB3 = varchar("h_hhiB3", 128)
    var h_hhiC3 = varchar("h_hhiC3", 128)
    var h_hhuOV3 = varchar("h_hhuOV3", 128)
    var h_hhiOV3 = varchar("h_hhiOV3", 128)

    var h_hhuAB4 = varchar("h_hhuAB4", 128)
    var h_hhuBC4 = varchar("h_hhuBC4", 128)
    var h_hhuCA4 = varchar("h_hhuCA4", 128)
    var h_hhiA4 = varchar("h_hhiA4", 128)
    var h_hhiB4 = varchar("h_hhiB4", 128)
    var h_hhiC4 = varchar("h_hhiC4", 128)
    var h_hhuOV4 = varchar("h_hhuOV4", 128)
    var h_hhiOV4 = varchar("h_hhiOV4", 128)

    var h_hhuAB5 = varchar("h_hhuAB5", 128)
    var h_hhuBC5 = varchar("h_hhuBC5", 128)
    var h_hhuCA5 = varchar("h_hhuCA5", 128)
    var h_hhiA5 = varchar("h_hhiA5", 128)
    var h_hhiB5 = varchar("h_hhiB5", 128)
    var h_hhiC5 = varchar("h_hhiC5", 128)
    var h_hhuOV5 = varchar("h_hhuOV5", 128)
    var h_hhiOV5 = varchar("h_hhiOV5", 128)

    var h_hhuAB6 = varchar("h_hhuAB6", 128)
    var h_hhuBC6 = varchar("h_hhuBC6", 128)
    var h_hhuCA6 = varchar("h_hhuCA6", 128)
    var h_hhiA6 = varchar("h_hhiA6", 128)
    var h_hhiB6 = varchar("h_hhiB6", 128)
    var h_hhiC6 = varchar("h_hhiC6", 128)
    var h_hhuOV6 = varchar("h_hhuOV6", 128)
    var h_hhiOV6 = varchar("h_hhiOV6", 128)

    var h_hhuAB7 = varchar("h_hhuAB7", 128)
    var h_hhuBC7 = varchar("h_hhuBC7", 128)
    var h_hhuCA7 = varchar("h_hhuCA7", 128)
    var h_hhiA7 = varchar("h_hhiA7", 128)
    var h_hhiB7 = varchar("h_hhiB7", 128)
    var h_hhiC7 = varchar("h_hhiC7", 128)
    var h_hhuOV7 = varchar("h_hhuOV7", 128)
    var h_hhiOV7 = varchar("h_hhiOV7", 128)

    var h_hhuAB8 = varchar("h_hhuAB8", 128)
    var h_hhuBC8 = varchar("h_hhuBC8", 128)
    var h_hhuCA8 = varchar("h_hhuCA8", 128)
    var h_hhiA8 = varchar("h_hhiA8", 128)
    var h_hhiB8 = varchar("h_hhiB8", 128)
    var h_hhiC8 = varchar("h_hhiC8", 128)
    var h_hhuOV8 = varchar("h_hhuOV8", 128)
    var h_hhiOV8 = varchar("h_hhiOV8", 128)

    var h_hhuAB9 = varchar("h_hhuAB9", 128)
    var h_hhuBC9 = varchar("h_hhuBC9", 128)
    var h_hhuCA9 = varchar("h_hhuCA9", 128)
    var h_hhiA9 = varchar("h_hhiA9", 128)
    var h_hhiB9 = varchar("h_hhiB9", 128)
    var h_hhiC9 = varchar("h_hhiC9", 128)
    var h_hhuOV9 = varchar("h_hhuOV9", 128)
    var h_hhiOV9 = varchar("h_hhiOV9", 128)

    var h_hhResult = varchar("h_hhResult", 128)

    //KZ//,`
    var kzN1 = varchar("kzN1", 128)
    var kzCos1 = varchar("kzCos1", 128)
    var kzUOV1 = varchar("kzUOV1", 128)
    var kzIOV1 = varchar("kzIOV1", 128)
    var kzUAB1 = varchar("kzUAB1", 128)
    var kzUBC1 = varchar("kzUBC1", 128)
    var kzUCA1 = varchar("kzUCA1", 128)
    var kzIA1 = varchar("kzIA1", 128)
    var kzIB1 = varchar("kzIB1", 128)
    var kzIC1 = varchar("kzIC1", 128)
    var kzP1 = varchar("kzP1", 128)
    var kzF1 = varchar("kzF1", 128)
    var kzN2 = varchar("kzN2", 128)
    var kzCos2 = varchar("kzCos2", 128)
    var kzUOV2 = varchar("kzUOV2", 128)
    var kzIOV2 = varchar("kzIOV2", 128)
    var kzUAB2 = varchar("kzUAB2", 128)
    var kzUBC2 = varchar("kzUBC2", 128)
    var kzUCA2 = varchar("kzUCA2", 128)
    var kzIA2 = varchar("kzIA2", 128)
    var kzIB2 = varchar("kzIB2", 128)
    var kzIC2 = varchar("kzIC2", 128)
    var kzP2 = varchar("kzP2", 128)
    var kzF2 = varchar("kzF2", 128)
    var kzN3 = varchar("kzN3", 128)
    var kzCos3 = varchar("kzCos3", 128)
    var kzUOV3 = varchar("kzUOV3", 128)
    var kzIOV3 = varchar("kzIOV3", 128)
    var kzUAB3 = varchar("kzUAB3", 128)
    var kzUBC3 = varchar("kzUBC3", 128)
    var kzUCA3 = varchar("kzUCA3", 128)
    var kzIA3 = varchar("kzIA3", 128)
    var kzIB3 = varchar("kzIB3", 128)
    var kzIC3 = varchar("kzIC3", 128)
    var kzP3 = varchar("kzP3", 128)
    var kzF3 = varchar("kzF3", 128)
    var kzN4 = varchar("kzN4", 128)
    var kzCos4 = varchar("kzCos4", 128)
    var kzUOV4 = varchar("kzUOV4", 128)
    var kzIOV4 = varchar("kzIOV4", 128)
    var kzUAB4 = varchar("kzUAB4", 128)
    var kzUBC4 = varchar("kzUBC4", 128)
    var kzUCA4 = varchar("kzUCA4", 128)
    var kzIA4 = varchar("kzIA4", 128)
    var kzIB4 = varchar("kzIB4", 128)
    var kzIC4 = varchar("kzIC4", 128)
    var kzP4 = varchar("kzP4", 128)
    var kzF4 = varchar("kzF4", 128)
    var kzN5 = varchar("kzN5", 128)
    var kzCos5 = varchar("kzCos5", 128)
    var kzUOV5 = varchar("kzUOV5", 128)
    var kzIOV5 = varchar("kzIOV5", 128)
    var kzUAB5 = varchar("kzUAB5", 128)
    var kzUBC5 = varchar("kzUBC5", 128)
    var kzUCA5 = varchar("kzUCA5", 128)
    var kzIA5 = varchar("kzIA5", 128)
    var kzIB5 = varchar("kzIB5", 128)
    var kzIC5 = varchar("kzIC5", 128)
    var kzP5 = varchar("kzP5", 128)
    var kzF5 = varchar("kzF5", 128)
    var kzN6 = varchar("kzN6", 128)
    var kzCos6 = varchar("kzCos6", 128)
    var kzUOV6 = varchar("kzUOV6", 128)
    var kzIOV6 = varchar("kzIOV6", 128)
    var kzUAB6 = varchar("kzUAB6", 128)
    var kzUBC6 = varchar("kzUBC6", 128)
    var kzUCA6 = varchar("kzUCA6", 128)
    var kzIA6 = varchar("kzIA6", 128)
    var kzIB6 = varchar("kzIB6", 128)
    var kzIC6 = varchar("kzIC6", 128)
    var kzP6 = varchar("kzP6", 128)
    var kzF6 = varchar("kzF6", 128)
    var kzResult = varchar("kzResult", 128)
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
    var uOV by ProtocolsTable.uOV
    var iOV by ProtocolsTable.iOV
    var nAsync by ProtocolsTable.nAsync
    var kpd by ProtocolsTable.kpd
    var scheme by ProtocolsTable.scheme

    //MGR//
    var mgrU1 by ProtocolsTable.mgrU1
    var mgrU2 by ProtocolsTable.mgrU2
    var mgrU3 by ProtocolsTable.mgrU3
    var mgrR151 by ProtocolsTable.mgrR151
    var mgrR152 by ProtocolsTable.mgrR152
    var mgrR153 by ProtocolsTable.mgrR153
    var mgrR601 by ProtocolsTable.mgrR601
    var mgrR602 by ProtocolsTable.mgrR602
    var mgrR603 by ProtocolsTable.mgrR603
    var mgrkABS1 by ProtocolsTable.mgrkABS1
    var mgrkABS2 by ProtocolsTable.mgrkABS2
    var mgrkABS3 by ProtocolsTable.mgrkABS3
    var mgrTemp by ProtocolsTable.mgrTemp
    var mgrResult1 by ProtocolsTable.mgrResult1
    var mgrResult2 by ProtocolsTable.mgrResult2
    var mgrResult3 by ProtocolsTable.mgrResult3

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

    //DPT//
    var dptNuOV by ProtocolsTable.dptNuOV
    var dptNiOV by ProtocolsTable.dptNiOV
    var dptNuN by ProtocolsTable.dptNuN
    var dptNiN by ProtocolsTable.dptNiN
    var dptNP1 by ProtocolsTable.dptNP1
    var dptNTOI by ProtocolsTable.dptNTOI
    var dptNTAmb by ProtocolsTable.dptNTAmb
    var dptNN by ProtocolsTable.dptNN
    var dptNResult by ProtocolsTable.dptNResult

    //HHDPT//
    var dptHHuOV by ProtocolsTable.dptHHuOV
    var dptHHiOV by ProtocolsTable.dptHHiOV
    var dptHHuN by ProtocolsTable.dptHHuN
    var dptHHiN by ProtocolsTable.dptHHiN
    var dptHHP1 by ProtocolsTable.dptHHP1
    var dptHHTOI by ProtocolsTable.dptHHTOI
    var dptHHTAmb by ProtocolsTable.dptHHTAmb
    var dptHHN by ProtocolsTable.dptHHN
    var dptHHResult by ProtocolsTable.dptHHResult
    var dptHHTime by ProtocolsTable.dptHHTime

    //LOAD_DPT//
    var dptLOADuOV by ProtocolsTable.dptLOADuOV
    var dptLOADiOV by ProtocolsTable.dptLOADiOV
    var dptLOADuN by ProtocolsTable.dptLOADuN
    var dptLOADiN by ProtocolsTable.dptLOADiN
    var dptLOADP1 by ProtocolsTable.dptLOADP1
    var dptLOADTOI by ProtocolsTable.dptLOADTOI
    var dptLOADTAmb by ProtocolsTable.dptLOADTAmb
    var dptLOADN by ProtocolsTable.dptLOADN
    var dptLOADDots by ProtocolsTable.dptLOADDots
    var dptLOADResult by ProtocolsTable.dptLOADResult

    //N_GPT//
    var gptNuOV by ProtocolsTable.gptNuOV
    var gptNiOV by ProtocolsTable.gptNiOV
    var gptNuN by ProtocolsTable.gptNuN
    var gptNiN by ProtocolsTable.gptNiN
    var gptNP1 by ProtocolsTable.gptNP1
    var gptNTOI by ProtocolsTable.gptNTOI
    var gptNTAmb by ProtocolsTable.gptNTAmb
    var gptNN by ProtocolsTable.gptNN
    var gptNResult by ProtocolsTable.gptNResult

    /////////
    var nUAB by ProtocolsTable.nUAB
    var nUBC by ProtocolsTable.nUBC
    var nUCA by ProtocolsTable.nUCA
    var nIA by ProtocolsTable.nIA
    var nIB by ProtocolsTable.nIB
    var nIC by ProtocolsTable.nIC
    var nF by ProtocolsTable.nF
    var nTempOI by ProtocolsTable.nTempOI
    var nTempAmb by ProtocolsTable.nTempAmb
    var nSpeed by ProtocolsTable.nSpeed
    var nVibro1 by ProtocolsTable.nVibro1
    var nVibro2 by ProtocolsTable.nVibro2
    var nTime by ProtocolsTable.nTime
    var nP1 by ProtocolsTable.nP1
    var nCos by ProtocolsTable.nCos
    var nResult by ProtocolsTable.nResult

    ////
//    var hhUAB by ProtocolsTable.hhUAB
//    var hhUBC by ProtocolsTable.hhUBC
//    var hhUCA by ProtocolsTable.hhUCA
//    var hhIA by ProtocolsTable.hhIA
//    var hhIB by ProtocolsTable.hhIB
//    var hhIC by ProtocolsTable.hhIC
//    var hhUOV by ProtocolsTable.hhUOV
//    var hhIOV by ProtocolsTable.hhIOV
//    var hhTempOI by ProtocolsTable.hhTempOI
//    var hhTempAmb by ProtocolsTable.hhTempAmb
//    var hhSpeed by ProtocolsTable.hhSpeed
//    var hhVibro1 by ProtocolsTable.hhVibro1
//    var hhVibro2 by ProtocolsTable.hhVibro2
//    var hhTime by ProtocolsTable.hhTime
//    var hhP1 by ProtocolsTable.hhP1
//    var hhF by ProtocolsTable.hhF
//    var hhResult by ProtocolsTable.hhResult
    //H_HH//
    var h_hhuAB1 by ProtocolsTable.h_hhuAB1
    var h_hhuBC1 by ProtocolsTable.h_hhuBC1
    var h_hhuCA1 by ProtocolsTable.h_hhuCA1
    var h_hhiA1 by ProtocolsTable.h_hhiA1
    var h_hhiB1 by ProtocolsTable.h_hhiB1
    var h_hhiC1 by ProtocolsTable.h_hhiC1
    var h_hhuOV1 by ProtocolsTable.h_hhuOV1
    var h_hhiOV1 by ProtocolsTable.h_hhiOV1
    var h_hhuAB2 by ProtocolsTable.h_hhuAB2
    var h_hhuBC2 by ProtocolsTable.h_hhuBC2
    var h_hhuCA2 by ProtocolsTable.h_hhuCA2
    var h_hhiA2 by ProtocolsTable.h_hhiA2
    var h_hhiB2 by ProtocolsTable.h_hhiB2
    var h_hhiC2 by ProtocolsTable.h_hhiC2
    var h_hhuOV2 by ProtocolsTable.h_hhuOV2
    var h_hhiOV2 by ProtocolsTable.h_hhiOV2
    var h_hhuAB3 by ProtocolsTable.h_hhuAB3
    var h_hhuBC3 by ProtocolsTable.h_hhuBC3
    var h_hhuCA3 by ProtocolsTable.h_hhuCA3
    var h_hhiA3 by ProtocolsTable.h_hhiA3
    var h_hhiB3 by ProtocolsTable.h_hhiB3
    var h_hhiC3 by ProtocolsTable.h_hhiC3
    var h_hhuOV3 by ProtocolsTable.h_hhuOV3
    var h_hhiOV3 by ProtocolsTable.h_hhiOV3
    var h_hhuAB4 by ProtocolsTable.h_hhuAB4
    var h_hhuBC4 by ProtocolsTable.h_hhuBC4
    var h_hhuCA4 by ProtocolsTable.h_hhuCA4
    var h_hhiA4 by ProtocolsTable.h_hhiA4
    var h_hhiB4 by ProtocolsTable.h_hhiB4
    var h_hhiC4 by ProtocolsTable.h_hhiC4
    var h_hhuOV4 by ProtocolsTable.h_hhuOV4
    var h_hhiOV4 by ProtocolsTable.h_hhiOV4
    var h_hhuAB5 by ProtocolsTable.h_hhuAB5
    var h_hhuBC5 by ProtocolsTable.h_hhuBC5
    var h_hhuCA5 by ProtocolsTable.h_hhuCA5
    var h_hhiA5 by ProtocolsTable.h_hhiA5
    var h_hhiB5 by ProtocolsTable.h_hhiB5
    var h_hhiC5 by ProtocolsTable.h_hhiC5
    var h_hhuOV5 by ProtocolsTable.h_hhuOV5
    var h_hhiOV5 by ProtocolsTable.h_hhiOV5
    var h_hhuAB6 by ProtocolsTable.h_hhuAB6
    var h_hhuBC6 by ProtocolsTable.h_hhuBC6
    var h_hhuCA6 by ProtocolsTable.h_hhuCA6
    var h_hhiA6 by ProtocolsTable.h_hhiA6
    var h_hhiB6 by ProtocolsTable.h_hhiB6
    var h_hhiC6 by ProtocolsTable.h_hhiC6
    var h_hhuOV6 by ProtocolsTable.h_hhuOV6
    var h_hhiOV6 by ProtocolsTable.h_hhiOV6
    var h_hhuAB7 by ProtocolsTable.h_hhuAB7
    var h_hhuBC7 by ProtocolsTable.h_hhuBC7
    var h_hhuCA7 by ProtocolsTable.h_hhuCA7
    var h_hhiA7 by ProtocolsTable.h_hhiA7
    var h_hhiB7 by ProtocolsTable.h_hhiB7
    var h_hhiC7 by ProtocolsTable.h_hhiC7
    var h_hhuOV7 by ProtocolsTable.h_hhuOV7
    var h_hhiOV7 by ProtocolsTable.h_hhiOV7
    var h_hhuAB8 by ProtocolsTable.h_hhuAB8
    var h_hhuBC8 by ProtocolsTable.h_hhuBC8
    var h_hhuCA8 by ProtocolsTable.h_hhuCA8
    var h_hhiA8 by ProtocolsTable.h_hhiA8
    var h_hhiB8 by ProtocolsTable.h_hhiB8
    var h_hhiC8 by ProtocolsTable.h_hhiC8
    var h_hhuOV8 by ProtocolsTable.h_hhuOV8
    var h_hhiOV8 by ProtocolsTable.h_hhiOV8
    var h_hhuAB9 by ProtocolsTable.h_hhuAB9
    var h_hhuBC9 by ProtocolsTable.h_hhuBC9
    var h_hhuCA9 by ProtocolsTable.h_hhuCA9
    var h_hhiA9 by ProtocolsTable.h_hhiA9
    var h_hhiB9 by ProtocolsTable.h_hhiB9
    var h_hhiC9 by ProtocolsTable.h_hhiC9
    var h_hhuOV9 by ProtocolsTable.h_hhuOV9
    var h_hhiOV9 by ProtocolsTable.h_hhiOV9
    var h_hhResult by ProtocolsTable.h_hhResult

    //KZ//
    var kzN1 by ProtocolsTable.kzN1
    var kzCos1 by ProtocolsTable.kzCos1
    var kzUOV1 by ProtocolsTable.kzUOV1
    var kzIOV1 by ProtocolsTable.kzIOV1
    var kzUAB1 by ProtocolsTable.kzUAB1
    var kzUBC1 by ProtocolsTable.kzUBC1
    var kzUCA1 by ProtocolsTable.kzUCA1
    var kzIA1 by ProtocolsTable.kzIA1
    var kzIB1 by ProtocolsTable.kzIB1
    var kzIC1 by ProtocolsTable.kzIC1
    var kzP1 by ProtocolsTable.kzP1
    var kzF1 by ProtocolsTable.kzF1
    var kzN2 by ProtocolsTable.kzN2
    var kzCos2 by ProtocolsTable.kzCos2
    var kzUOV2 by ProtocolsTable.kzUOV2
    var kzIOV2 by ProtocolsTable.kzIOV2
    var kzUAB2 by ProtocolsTable.kzUAB2
    var kzUBC2 by ProtocolsTable.kzUBC2
    var kzUCA2 by ProtocolsTable.kzUCA2
    var kzIA2 by ProtocolsTable.kzIA2
    var kzIB2 by ProtocolsTable.kzIB2
    var kzIC2 by ProtocolsTable.kzIC2
    var kzP2 by ProtocolsTable.kzP2
    var kzF2 by ProtocolsTable.kzF2
    var kzN3 by ProtocolsTable.kzN3
    var kzCos3 by ProtocolsTable.kzCos3
    var kzUOV3 by ProtocolsTable.kzUOV3
    var kzIOV3 by ProtocolsTable.kzIOV3
    var kzUAB3 by ProtocolsTable.kzUAB3
    var kzUBC3 by ProtocolsTable.kzUBC3
    var kzUCA3 by ProtocolsTable.kzUCA3
    var kzIA3 by ProtocolsTable.kzIA3
    var kzIB3 by ProtocolsTable.kzIB3
    var kzIC3 by ProtocolsTable.kzIC3
    var kzP3 by ProtocolsTable.kzP3
    var kzF3 by ProtocolsTable.kzF3
    var kzN4 by ProtocolsTable.kzN4
    var kzCos4 by ProtocolsTable.kzCos4
    var kzUOV4 by ProtocolsTable.kzUOV4
    var kzIOV4 by ProtocolsTable.kzIOV4
    var kzUAB4 by ProtocolsTable.kzUAB4
    var kzUBC4 by ProtocolsTable.kzUBC4
    var kzUCA4 by ProtocolsTable.kzUCA4
    var kzIA4 by ProtocolsTable.kzIA4
    var kzIB4 by ProtocolsTable.kzIB4
    var kzIC4 by ProtocolsTable.kzIC4
    var kzP4 by ProtocolsTable.kzP4
    var kzF4 by ProtocolsTable.kzF4
    var kzN5 by ProtocolsTable.kzN5
    var kzCos5 by ProtocolsTable.kzCos5
    var kzUOV5 by ProtocolsTable.kzUOV5
    var kzIOV5 by ProtocolsTable.kzIOV5
    var kzUAB5 by ProtocolsTable.kzUAB5
    var kzUBC5 by ProtocolsTable.kzUBC5
    var kzUCA5 by ProtocolsTable.kzUCA5
    var kzIA5 by ProtocolsTable.kzIA5
    var kzIB5 by ProtocolsTable.kzIB5
    var kzIC5 by ProtocolsTable.kzIC5
    var kzP5 by ProtocolsTable.kzP5
    var kzF5 by ProtocolsTable.kzF5
    var kzN6 by ProtocolsTable.kzN6
    var kzCos6 by ProtocolsTable.kzCos6
    var kzUOV6 by ProtocolsTable.kzUOV6
    var kzIOV6 by ProtocolsTable.kzIOV6
    var kzUAB6 by ProtocolsTable.kzUAB6
    var kzUBC6 by ProtocolsTable.kzUBC6
    var kzUCA6 by ProtocolsTable.kzUCA6
    var kzIA6 by ProtocolsTable.kzIA6
    var kzIB6 by ProtocolsTable.kzIB6
    var kzIC6 by ProtocolsTable.kzIC6
    var kzP6 by ProtocolsTable.kzP6
    var kzF6 by ProtocolsTable.kzF6
    var kzResult by ProtocolsTable.kzResult


    override fun toString(): String {
        return "$id"
    }
}
