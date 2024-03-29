package ru.avem.kspem.protocol

import org.apache.logging.log4j.message.StringFormattedMessage
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ru.avem.kspem.app.Main
import ru.avem.kspem.data.motorType
import ru.avem.kspem.database.entities.Protocol
import ru.avem.kspem.utils.Toast
import ru.avem.kspem.utils.copyFileFromStream
import tornadofx.controlsfx.errorNotification
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.util.*


fun saveProtocolAsWorkbook(protocol: Protocol, path: String = "cfg/lastOpened.xlsx") {
    var protocolName = ""
    when (protocol.type) {
        motorType.dpt -> protocolName = "protocolDPT.xlsx"
        motorType.gpt -> protocolName = "protocolGPT.xlsx"
        motorType.sd -> protocolName = "protocolSD.xlsx"
        motorType.sg -> protocolName = "protocolSG.xlsx"
        else -> errorNotification("Ошибка", "Не указан тип ОИ")
    }
    val template = File(path)
//    copyFileFromStream(Main::class.java.getResource("protocol.xlsx").openStream(), template)
    if (File("cfg\\$protocolName").exists()) {
        copyFileFromStream(File("cfg/$protocolName").inputStream(), template)
    } else {
        copyFileFromStream(Main::class.java.getResource(protocolName).openStream(), File("cfg/$protocolName"))
        copyFileFromStream(File("cfg/$protocolName").inputStream(), template)
    }

    try {
        XSSFWorkbook(template).use { wb ->
            val sheet = wb.getSheetAt(0)
            for (iRow in 0 until 150) {
                val row = sheet.getRow(iRow)
                if (row != null) {
                    for (iCell in 0 until 150) {
                        val cell = row.getCell(iCell)
                        if (cell != null && (cell.cellType == CellType.STRING)) {
                            when (cell.stringCellValue) {
                                "#objectName#" -> cell.setCellValue(protocol.objectName)
                                "#type#" -> cell.setCellValue(protocol.type)
                                "#date#" -> cell.setCellValue(protocol.date)
                                "#time#" -> cell.setCellValue(protocol.time)
                                "#operator#" -> cell.setCellValue(protocol.operator)
                                "#serial#" -> cell.setCellValue(protocol.serial)
                                "#p2#" -> cell.setCellValue(protocol.p2)
                                "#uN#" -> cell.setCellValue(protocol.uN)
                                "#iN#" -> cell.setCellValue(protocol.iN)
                                "#uOV#" -> cell.setCellValue(protocol.uOV)
                                "#iOV#" -> cell.setCellValue(protocol.iOV)
                                "#nAsync#" -> cell.setCellValue(protocol.nAsync)
                                "#kpd#" -> cell.setCellValue(protocol.kpd)
                                "#scheme#" -> cell.setCellValue(protocol.scheme)
                                "#mgrU1#" -> cell.setCellValue(protocol.mgrU1)
                                "#mgrU2#" -> cell.setCellValue(protocol.mgrU2)
                                "#mgrU3#" -> cell.setCellValue(protocol.mgrU3)
                                "#mgrR151#" -> cell.setCellValue(protocol.mgrR151)
                                "#mgrR152#" -> cell.setCellValue(protocol.mgrR152)
                                "#mgrR153#" -> cell.setCellValue(protocol.mgrR153)
                                "#mgrR601#" -> cell.setCellValue(protocol.mgrR601)
                                "#mgrR602#" -> cell.setCellValue(protocol.mgrR602)
                                "#mgrR603#" -> cell.setCellValue(protocol.mgrR603)
                                "#mgrkABS1#" -> cell.setCellValue(protocol.mgrkABS1)
                                "#mgrkABS2#" -> cell.setCellValue(protocol.mgrkABS2)
                                "#mgrkABS3#" -> cell.setCellValue(protocol.mgrkABS3)
                                "#mgrTemp#" -> cell.setCellValue(protocol.mgrTemp)
                                "#mgrResult1#" -> cell.setCellValue(protocol.mgrResult1)
                                "#mgrResult2#" -> cell.setCellValue(protocol.mgrResult2)
                                "#mgrResult3#" -> cell.setCellValue(protocol.mgrResult3)
                                "#viuU#" -> cell.setCellValue(protocol.viuU)
                                "#viuI#" -> cell.setCellValue(protocol.viuI)
                                "#viuTime#" -> cell.setCellValue(protocol.viuTime)
                                "#viuResult#" -> cell.setCellValue(protocol.viuResult)
                                "#ikasR1#" -> cell.setCellValue(protocol.ikasR1)
                                "#ikasR2#" -> cell.setCellValue(protocol.ikasR2)
                                "#ikasR3#" -> cell.setCellValue(protocol.ikasR3)
                                "#ikasResult#" -> cell.setCellValue(protocol.ikasResult)
                                "#dptNuOV#" -> cell.setCellValue(protocol.dptNuOV)
                                "#dptNiOV#" -> cell.setCellValue(protocol.dptNiOV)
                                "#dptNuN#" -> cell.setCellValue(protocol.dptNuN)
                                "#dptNiN#" -> cell.setCellValue(protocol.dptNiN)
                                "#dptNP1#" -> cell.setCellValue(protocol.dptNP1)
                                "#dptNTOI#" -> cell.setCellValue(protocol.dptNTOI)
                                "#dptNTAmb#" -> cell.setCellValue(protocol.dptNTAmb)
                                "#dptNN#" -> cell.setCellValue(protocol.dptNN)
                                "#dptNTime#" -> cell.setCellValue("120")
                                "#dptNResult#" -> cell.setCellValue(protocol.dptNResult)
                                "#dptHHuOV#" -> cell.setCellValue(protocol.dptHHuOV)
                                "#dptHHiOV#" -> cell.setCellValue(protocol.dptHHiOV)
                                "#dptHHuN#" -> cell.setCellValue(protocol.dptHHuN)
                                "#dptHHiN#" -> cell.setCellValue(protocol.dptHHiN)
                                "#dptHHP1#" -> cell.setCellValue(protocol.dptHHP1)
                                "#dptHHTOI#" -> cell.setCellValue(protocol.dptHHTOI)
                                "#dptHHTAmb#" -> cell.setCellValue(protocol.dptHHTAmb)
                                "#dptHHN#" -> cell.setCellValue(protocol.dptHHN)
                                "#dptHHResult#" -> cell.setCellValue(protocol.dptHHResult)
                                "#dptHHTime#" -> cell.setCellValue(protocol.dptHHTime)
                                "#dptLOADuOV#" -> cell.setCellValue(protocol.dptLOADuOV)
                                "#dptLOADiOV#" -> cell.setCellValue(protocol.dptLOADiOV)
                                "#dptLOADuN#" -> cell.setCellValue(protocol.dptLOADuN)
                                "#dptLOADiN#" -> cell.setCellValue(protocol.dptLOADiN)
                                "#dptLOADP1#" -> cell.setCellValue(protocol.dptLOADP1)
                                "#dptLOADTOI#" -> cell.setCellValue(protocol.dptLOADTOI)
                                "#dptLOADTAmb#" -> cell.setCellValue(protocol.dptLOADTAmb)
                                "#dptLOADN#" -> cell.setCellValue(protocol.dptLOADN)
                                "#dptLOADTime#" -> cell.setCellValue("0")
                                "#dptLOADResult#" -> cell.setCellValue(protocol.dptLOADResult)
                                "#gptNuOV#" -> cell.setCellValue(protocol.gptNuOV)
                                "#gptNiOV#" -> cell.setCellValue(protocol.gptNiOV)
                                "#gptNuN#" -> cell.setCellValue(protocol.gptNuN)
                                "#gptNiN#" -> cell.setCellValue(protocol.gptNiN)
                                "#gptNP1#" -> cell.setCellValue(protocol.gptNP1)
                                "#gptNTOI#" -> cell.setCellValue(protocol.gptNTOI)
                                "#gptNTAmb#" -> cell.setCellValue(protocol.gptNTAmb)
                                "#gptNN#" -> cell.setCellValue(protocol.gptNN)
                                "#gptNTime#" -> cell.setCellValue("120")
                                "#gptNResult#" -> cell.setCellValue(protocol.gptNResult)
                                "#nUAB#" -> cell.setCellValue(protocol.nUAB)
                                "#nUBC#" -> cell.setCellValue(protocol.nUBC)
                                "#nUCA#" -> cell.setCellValue(protocol.nUCA)
                                "#nIA#" -> cell.setCellValue(protocol.nIA)
                                "#nIB#" -> cell.setCellValue(protocol.nIB)
                                "#nIC#" -> cell.setCellValue(protocol.nIC)
                                "#nF#" -> cell.setCellValue(protocol.nF)
                                "#nTempOI#" -> cell.setCellValue(protocol.nTempOI)
                                "#nTempAmb#" -> cell.setCellValue(protocol.nTempAmb)
                                "#nSpeed#" -> cell.setCellValue(protocol.nSpeed)
                                "#nVibro1#" -> cell.setCellValue(protocol.nVibro1)
                                "#nVibro2#" -> cell.setCellValue(protocol.nVibro2)
                                "#nTime#" -> cell.setCellValue(protocol.nTime)
                                "#nP1#" -> cell.setCellValue(protocol.nP1)
                                "#nCos#" -> cell.setCellValue(protocol.nCos)
                                "#nResult#" -> cell.setCellValue(protocol.nResult)
                                "#h_hhuAB1#" -> cell.setCellValue(protocol.h_hhuAB1)
                                "#h_hhuBC1#" -> cell.setCellValue(protocol.h_hhuBC1)
                                "#h_hhuCA1#" -> cell.setCellValue(protocol.h_hhuCA1)
                                "#h_hhiA1#" -> cell.setCellValue(protocol.h_hhiA1)
                                "#h_hhiB1#" -> cell.setCellValue(protocol.h_hhiB1)
                                "#h_hhiC1#" -> cell.setCellValue(protocol.h_hhiC1)
                                "#h_hhuOV1#" -> cell.setCellValue(protocol.h_hhuOV1)
                                "#h_hhiOV1#" -> cell.setCellValue(protocol.h_hhiOV1)
                                "#h_hhuAB2#" -> cell.setCellValue(protocol.h_hhuAB2)
                                "#h_hhuBC2#" -> cell.setCellValue(protocol.h_hhuBC2)
                                "#h_hhuCA2#" -> cell.setCellValue(protocol.h_hhuCA2)
                                "#h_hhiA2#" -> cell.setCellValue(protocol.h_hhiA2)
                                "#h_hhiB2#" -> cell.setCellValue(protocol.h_hhiB2)
                                "#h_hhiC2#" -> cell.setCellValue(protocol.h_hhiC2)
                                "#h_hhuOV2#" -> cell.setCellValue(protocol.h_hhuOV2)
                                "#h_hhiOV2#" -> cell.setCellValue(protocol.h_hhiOV2)
                                "#h_hhuAB3#" -> cell.setCellValue(protocol.h_hhuAB3)
                                "#h_hhuBC3#" -> cell.setCellValue(protocol.h_hhuBC3)
                                "#h_hhuCA3#" -> cell.setCellValue(protocol.h_hhuCA3)
                                "#h_hhiA3#" -> cell.setCellValue(protocol.h_hhiA3)
                                "#h_hhiB3#" -> cell.setCellValue(protocol.h_hhiB3)
                                "#h_hhiC3#" -> cell.setCellValue(protocol.h_hhiC3)
                                "#h_hhuOV3#" -> cell.setCellValue(protocol.h_hhuOV3)
                                "#h_hhiOV3#" -> cell.setCellValue(protocol.h_hhiOV3)
                                "#h_hhuAB4#" -> cell.setCellValue(protocol.h_hhuAB4)
                                "#h_hhuBC4#" -> cell.setCellValue(protocol.h_hhuBC4)
                                "#h_hhuCA4#" -> cell.setCellValue(protocol.h_hhuCA4)
                                "#h_hhiA4#" -> cell.setCellValue(protocol.h_hhiA4)
                                "#h_hhiB4#" -> cell.setCellValue(protocol.h_hhiB4)
                                "#h_hhiC4#" -> cell.setCellValue(protocol.h_hhiC4)
                                "#h_hhuOV4#" -> cell.setCellValue(protocol.h_hhuOV4)
                                "#h_hhiOV4#" -> cell.setCellValue(protocol.h_hhiOV4)
                                "#h_hhuAB5#" -> cell.setCellValue(protocol.h_hhuAB5)
                                "#h_hhuBC5#" -> cell.setCellValue(protocol.h_hhuBC5)
                                "#h_hhuCA5#" -> cell.setCellValue(protocol.h_hhuCA5)
                                "#h_hhiA5#" -> cell.setCellValue(protocol.h_hhiA5)
                                "#h_hhiB5#" -> cell.setCellValue(protocol.h_hhiB5)
                                "#h_hhiC5#" -> cell.setCellValue(protocol.h_hhiC5)
                                "#h_hhuOV5#" -> cell.setCellValue(protocol.h_hhuOV5)
                                "#h_hhiOV5#" -> cell.setCellValue(protocol.h_hhiOV5)
                                "#h_hhuAB6#" -> cell.setCellValue(protocol.h_hhuAB6)
                                "#h_hhuBC6#" -> cell.setCellValue(protocol.h_hhuBC6)
                                "#h_hhuCA6#" -> cell.setCellValue(protocol.h_hhuCA6)
                                "#h_hhiA6#" -> cell.setCellValue(protocol.h_hhiA6)
                                "#h_hhiB6#" -> cell.setCellValue(protocol.h_hhiB6)
                                "#h_hhiC6#" -> cell.setCellValue(protocol.h_hhiC6)
                                "#h_hhuOV6#" -> cell.setCellValue(protocol.h_hhuOV6)
                                "#h_hhiOV6#" -> cell.setCellValue(protocol.h_hhiOV6)
                                "#h_hhuAB7#" -> cell.setCellValue(protocol.h_hhuAB7)
                                "#h_hhuBC7#" -> cell.setCellValue(protocol.h_hhuBC7)
                                "#h_hhuCA7#" -> cell.setCellValue(protocol.h_hhuCA7)
                                "#h_hhiA7#" -> cell.setCellValue(protocol.h_hhiA7)
                                "#h_hhiB7#" -> cell.setCellValue(protocol.h_hhiB7)
                                "#h_hhiC7#" -> cell.setCellValue(protocol.h_hhiC7)
                                "#h_hhuOV7#" -> cell.setCellValue(protocol.h_hhuOV7)
                                "#h_hhiOV7#" -> cell.setCellValue(protocol.h_hhiOV7)
                                "#h_hhuAB8#" -> cell.setCellValue(protocol.h_hhuAB8)
                                "#h_hhuBC8#" -> cell.setCellValue(protocol.h_hhuBC8)
                                "#h_hhuCA8#" -> cell.setCellValue(protocol.h_hhuCA8)
                                "#h_hhiA8#" -> cell.setCellValue(protocol.h_hhiA8)
                                "#h_hhiB8#" -> cell.setCellValue(protocol.h_hhiB8)
                                "#h_hhiC8#" -> cell.setCellValue(protocol.h_hhiC8)
                                "#h_hhuOV8#" -> cell.setCellValue(protocol.h_hhuOV8)
                                "#h_hhiOV8#" -> cell.setCellValue(protocol.h_hhiOV8)
                                "#h_hhuAB9#" -> cell.setCellValue(protocol.h_hhuAB9)
                                "#h_hhuBC9#" -> cell.setCellValue(protocol.h_hhuBC9)
                                "#h_hhuCA9#" -> cell.setCellValue(protocol.h_hhuCA9)
                                "#h_hhiA9#" -> cell.setCellValue(protocol.h_hhiA9)
                                "#h_hhiB9#" -> cell.setCellValue(protocol.h_hhiB9)
                                "#h_hhiC9#" -> cell.setCellValue(protocol.h_hhiC9)
                                "#h_hhuOV9#" -> cell.setCellValue(protocol.h_hhuOV9)
                                "#h_hhiOV9#" -> cell.setCellValue(protocol.h_hhiOV9)
                                "#h_hhResult#" -> cell.setCellValue(protocol.h_hhResult)

                                "#kzN1#" -> cell.setCellValue(protocol.kzN1)
                                "#kzCos1#" -> cell.setCellValue(protocol.kzCos1)
                                "#kzUOV1#" -> cell.setCellValue(protocol.kzUOV1)
                                "#kzIOV1#" -> cell.setCellValue(protocol.kzIOV1)
                                "#kzUAB1#" -> cell.setCellValue(protocol.kzUAB1)
                                "#kzUBC1#" -> cell.setCellValue(protocol.kzUBC1)
                                "#kzUCA1#" -> cell.setCellValue(protocol.kzUCA1)
                                "#kzIA1#" -> cell.setCellValue(protocol.kzIA1)
                                "#kzIB1#" -> cell.setCellValue(protocol.kzIB1)
                                "#kzIC1#" -> cell.setCellValue(protocol.kzIC1)
                                "#kzP1#" -> cell.setCellValue(protocol.kzP1)
                                "#kzF1#" -> cell.setCellValue(protocol.kzF1)
                                "#kzN2#" -> cell.setCellValue(protocol.kzN2)
                                "#kzCos2#" -> cell.setCellValue(protocol.kzCos2)
                                "#kzUOV2#" -> cell.setCellValue(protocol.kzUOV2)
                                "#kzIOV2#" -> cell.setCellValue(protocol.kzIOV2)
                                "#kzUAB2#" -> cell.setCellValue(protocol.kzUAB2)
                                "#kzUBC2#" -> cell.setCellValue(protocol.kzUBC2)
                                "#kzUCA2#" -> cell.setCellValue(protocol.kzUCA2)
                                "#kzIA2#" -> cell.setCellValue(protocol.kzIA2)
                                "#kzIB2#" -> cell.setCellValue(protocol.kzIB2)
                                "#kzIC2#" -> cell.setCellValue(protocol.kzIC2)
                                "#kzP2#" -> cell.setCellValue(protocol.kzP2)
                                "#kzF2#" -> cell.setCellValue(protocol.kzF2)
                                "#kzN3#" -> cell.setCellValue(protocol.kzN3)
                                "#kzCos3#" -> cell.setCellValue(protocol.kzCos3)
                                "#kzUOV3#" -> cell.setCellValue(protocol.kzUOV3)
                                "#kzIOV3#" -> cell.setCellValue(protocol.kzIOV3)
                                "#kzUAB3#" -> cell.setCellValue(protocol.kzUAB3)
                                "#kzUBC3#" -> cell.setCellValue(protocol.kzUBC3)
                                "#kzUCA3#" -> cell.setCellValue(protocol.kzUCA3)
                                "#kzIA3#" -> cell.setCellValue(protocol.kzIA3)
                                "#kzIB3#" -> cell.setCellValue(protocol.kzIB3)
                                "#kzIC3#" -> cell.setCellValue(protocol.kzIC3)
                                "#kzP3#" -> cell.setCellValue(protocol.kzP3)
                                "#kzF3#" -> cell.setCellValue(protocol.kzF3)
                                "#kzN4#" -> cell.setCellValue(protocol.kzN4)
                                "#kzCos4#" -> cell.setCellValue(protocol.kzCos4)
                                "#kzUOV4#" -> cell.setCellValue(protocol.kzUOV4)
                                "#kzIOV4#" -> cell.setCellValue(protocol.kzIOV4)
                                "#kzUAB4#" -> cell.setCellValue(protocol.kzUAB4)
                                "#kzUBC4#" -> cell.setCellValue(protocol.kzUBC4)
                                "#kzUCA4#" -> cell.setCellValue(protocol.kzUCA4)
                                "#kzIA4#" -> cell.setCellValue(protocol.kzIA4)
                                "#kzIB4#" -> cell.setCellValue(protocol.kzIB4)
                                "#kzIC4#" -> cell.setCellValue(protocol.kzIC4)
                                "#kzP4#" -> cell.setCellValue(protocol.kzP4)
                                "#kzF4#" -> cell.setCellValue(protocol.kzF4)
                                "#kzN5#" -> cell.setCellValue(protocol.kzN5)
                                "#kzCos5#" -> cell.setCellValue(protocol.kzCos5)
                                "#kzUOV5#" -> cell.setCellValue(protocol.kzUOV5)
                                "#kzIOV5#" -> cell.setCellValue(protocol.kzIOV5)
                                "#kzUAB5#" -> cell.setCellValue(protocol.kzUAB5)
                                "#kzUBC5#" -> cell.setCellValue(protocol.kzUBC5)
                                "#kzUCA5#" -> cell.setCellValue(protocol.kzUCA5)
                                "#kzIA5#" -> cell.setCellValue(protocol.kzIA5)
                                "#kzIB5#" -> cell.setCellValue(protocol.kzIB5)
                                "#kzIC5#" -> cell.setCellValue(protocol.kzIC5)
                                "#kzP5#" -> cell.setCellValue(protocol.kzP5)
                                "#kzF5#" -> cell.setCellValue(protocol.kzF5)
                                "#kzN6#" -> cell.setCellValue(protocol.kzN6)
                                "#kzCos6#" -> cell.setCellValue(protocol.kzCos6)
                                "#kzUOV6#" -> cell.setCellValue(protocol.kzUOV6)
                                "#kzIOV6#" -> cell.setCellValue(protocol.kzIOV6)
                                "#kzUAB6#" -> cell.setCellValue(protocol.kzUAB6)
                                "#kzUBC6#" -> cell.setCellValue(protocol.kzUBC6)
                                "#kzUCA6#" -> cell.setCellValue(protocol.kzUCA6)
                                "#kzIA6#" -> cell.setCellValue(protocol.kzIA6)
                                "#kzIB6#" -> cell.setCellValue(protocol.kzIB6)
                                "#kzIC6#" -> cell.setCellValue(protocol.kzIC6)
                                "#kzP6#" -> cell.setCellValue(protocol.kzP6)
                                "#kzF6#" -> cell.setCellValue(protocol.kzF6)

                                "#kzResult#" -> cell.setCellValue(protocol.kzResult)

                                else -> {
                                    if (cell.stringCellValue.contains("#")) {
                                        cell.setCellValue("")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            fillDotsInProtocol(
                wb,
                protocol.dptLOADDots,
                1,
                78
            )

            val outStream = ByteArrayOutputStream()
            wb.write(outStream)
            outStream.close()
        }
    } catch (e: FileNotFoundException) {
        Toast.makeText("Не удалось сохранить протокол на диск")
    }
}

fun fillDotsInProtocol(
    wb: XSSFWorkbook,
    dots1: String,
    columnNumber: Int,
    rawNumber: Int
) {
    val values1 = dots1.removePrefix("[").removePrefix("'").removeSuffix("]").split(", ")

    val valuesTime = arrayListOf<String>()
    val valuesDot1 = arrayListOf<String>()
    val valuesDot2 = arrayListOf<String>()
    val valuesDot3 = arrayListOf<String>()
    val valuesDot4 = arrayListOf<String>()
    for (i in 0 until values1.size / 5) {
        valuesTime.add(values1[i])
    }
    for (i in 0 until values1.size / 5) {
        valuesDot1.add(values1[i + values1.size / 5 * 1])
    }
    for (i in 0 until values1.size / 5) {
        valuesDot2.add(values1[i + values1.size / 5 * 2])
    }
    for (i in 0 until values1.size / 5) {
        valuesDot3.add(values1[i + values1.size / 5 * 3])
    }
    for (i in 0 until values1.size / 5) {
        valuesDot4.add(values1[i + values1.size / 5 * 4])
    }
    println(valuesTime)
    println(valuesDot1)
    println(valuesDot2)
    println(valuesDot3)
    println(valuesDot4)
    val sheet = wb.getSheetAt(0)
    var row: Row
    val cellStyle: XSSFCellStyle = generateStyles(wb) as XSSFCellStyle
    var rowNum = rawNumber
    row = sheet.createRow(rowNum)
    for (i in 0 until valuesTime.size) {
        fillOneCell(row, columnNumber + 1, cellStyle, valuesTime[i])
        fillOneCell(row, columnNumber + 2, cellStyle, valuesDot1[i])
        fillOneCell(row, columnNumber + 3, cellStyle, valuesDot2[i])
        fillOneCell(row, columnNumber + 4, cellStyle, valuesDot3[i])
        fillOneCell(row, columnNumber + 5, cellStyle, valuesDot4[i])
        row = sheet.createRow(++rowNum)
    }
}

private fun generateStyles(wb: XSSFWorkbook): CellStyle {
    val headStyle: CellStyle = wb.createCellStyle()
    headStyle.wrapText = true
    headStyle.borderBottom = BorderStyle.THIN
    headStyle.borderTop = BorderStyle.THIN
    headStyle.borderLeft = BorderStyle.THIN
    headStyle.borderRight = BorderStyle.THIN
    headStyle.alignment = HorizontalAlignment.CENTER
    headStyle.verticalAlignment = VerticalAlignment.CENTER
    return headStyle
}

private fun fillOneCell(row: Row, columnNum: Int, cellStyle: XSSFCellStyle, points: String): Int {
    val cell: Cell = row.createCell(columnNum)
    cell.cellStyle = cellStyle
    cell.setCellValue(points)
    return columnNum + 1
}


