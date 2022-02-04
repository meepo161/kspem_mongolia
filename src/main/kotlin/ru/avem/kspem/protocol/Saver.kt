package ru.avem.kspem.protocol

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ru.avem.kspem.app.Main
import ru.avem.kspem.database.entities.Protocol
import ru.avem.kspem.utils.Toast
import ru.avem.kspem.utils.copyFileFromStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException


fun saveProtocolAsWorkbook(protocol: Protocol, path: String = "cfg/lastOpened.xlsx") {
    val template = File(path)
//    copyFileFromStream(Main::class.java.getResource("protocol.xlsx").openStream(), template)
    if (File("cfg\\protocol.xlsx").exists()) {
        copyFileFromStream(File("cfg/protocol.xlsx").inputStream(), template)
    } else {
        copyFileFromStream(Main::class.java.getResource("protocol.xlsx").openStream(), File("cfg/protocol.xlsx"))
        copyFileFromStream(File("cfg/protocol.xlsx").inputStream(), template)
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
                                "#OBJECTNAME#" -> cell.setCellValue(protocol.objectName)
                                "#PROTOCOL_NUMBER#" -> cell.setCellValue(protocol.id.toString())
                                "#DATE#" -> cell.setCellValue(protocol.date)
                                "#TIME#" -> cell.setCellValue(protocol.time)
                                "#OPERATOR#" -> cell.setCellValue(protocol.operator)
                                "#SERIAL#" -> cell.setCellValue(protocol.serial)
                                "#P2#" -> cell.setCellValue(protocol.p2)
                                "#UN#" -> cell.setCellValue(protocol.uN)
                                "#IN#" -> cell.setCellValue(protocol.iN)
                                "#NASYNC#" -> cell.setCellValue(protocol.nAsync)
                                "#KPD#" -> cell.setCellValue(protocol.kpd)
                                "#COS#" -> cell.setCellValue(protocol.cos)
                                "#SCHEME#" -> cell.setCellValue(protocol.scheme)
                                //MGR
                                "#MGRU#" -> cell.setCellValue(protocol.mgrU)
                                "#MGRR15#" -> cell.setCellValue(protocol.mgrR15)
                                "#MGRR60#" -> cell.setCellValue(protocol.mgrR60)
                                "#MGRKABS#" -> cell.setCellValue(protocol.mgrkABS)
                                "#MGRTEMP#" -> cell.setCellValue(protocol.mgrTemp)
                                "#MGRRESULT#" -> cell.setCellValue(protocol.mgrResult)
                                //VIU
                                "#VIUU#" -> cell.setCellValue(protocol.viuU)
                                "#VIUI#" -> cell.setCellValue(protocol.viuI)
                                "#VIUTIME#" -> cell.setCellValue(protocol.viuTime)
                                "#VIURESULT#" -> cell.setCellValue(protocol.viuResult)
                                //IKAS
                                "#IKASR1#" -> cell.setCellValue(protocol.ikasR1)
                                "#IKASR2#" -> cell.setCellValue(protocol.ikasR2)
                                "#IKASR3#" -> cell.setCellValue(protocol.ikasR3)
                                "#IKASRESULT#" -> cell.setCellValue(protocol.ikasResult)
                                //HH
                                "#HHUAB#" -> cell.setCellValue(protocol.hhUAB)
                                "#HHUBC#" -> cell.setCellValue(protocol.hhUBC)
                                "#HHUCA#" -> cell.setCellValue(protocol.hhUCA)
                                "#HHIA#" -> cell.setCellValue(protocol.hhIA)
                                "#HHIB#" -> cell.setCellValue(protocol.hhIB)
                                "#HHIC#" -> cell.setCellValue(protocol.hhIC)
                                "#HHTEMPOI#" -> cell.setCellValue(protocol.hhTempOI)
                                "#HHTEMPAMB#" -> cell.setCellValue(protocol.hhTempAmb)
                                "#HHSPEED#" -> cell.setCellValue(protocol.hhSpeed)
                                "#HHVIBRO1#" -> cell.setCellValue(protocol.hhVibro1)
                                "#HHVIBRO2#" -> cell.setCellValue(protocol.hhVibro2)
                                "#HHTIME#" -> cell.setCellValue(protocol.hhTime)
                                "#HHP1#" -> cell.setCellValue(protocol.hhP1)
                                "#HHCOS#" -> cell.setCellValue(protocol.hhCos)
                                "#HHRESULT#" -> cell.setCellValue(protocol.hhResult)
                                //RUNNING
                                "#RUNNINGUAB#" -> cell.setCellValue(protocol.runningUAB)
                                "#RUNNINGUBC#" -> cell.setCellValue(protocol.runningUBC)
                                "#RUNNINGUCA#" -> cell.setCellValue(protocol.runningUCA)
                                "#RUNNINGIA#" -> cell.setCellValue(protocol.runningIA)
                                "#RUNNINGIB#" -> cell.setCellValue(protocol.runningIB)
                                "#RUNNINGIC#" -> cell.setCellValue(protocol.runningIC)
                                "#RUNNINGTEMPOI#" -> cell.setCellValue(protocol.runningTempOI)
                                "#RUNNINGTEMPAMB#" -> cell.setCellValue(protocol.runningTempAmb)
                                "#RUNNINGSPEED#" -> cell.setCellValue(protocol.runningSpeed)
                                "#RUNNINGVIBRO1#" -> cell.setCellValue(protocol.runningVibro1)
                                "#RUNNINGVIBRO2#" -> cell.setCellValue(protocol.runningVibro2)
                                "#RUNNINGTIME#" -> cell.setCellValue(protocol.runningTime)
                                "#RUNNINGP1#" -> cell.setCellValue(protocol.runningP1)
                                "#RUNNINGCOS#" -> cell.setCellValue(protocol.runningCos)
                                "#RUNNINGRESULT#" -> cell.setCellValue(protocol.runningResult)
                                //H_HH//
//                                "#H_HHUAB1#" -> cell.setCellValue(protocol.h_hhUAB1)
//                                "#H_HHUBC1#" -> cell.setCellValue(protocol.h_hhUBC1)
//                                "#H_HHUCA1#" -> cell.setCellValue(protocol.h_hhUCA1)
//                                "#H_HHIA1#" -> cell.setCellValue(protocol.h_hhIA1)
//                                "#H_HHIB1#" -> cell.setCellValue(protocol.h_hhIB1)
//                                "#H_HHIC1#" -> cell.setCellValue(protocol.h_hhIC1)
//                                "#H_HHUAB2#" -> cell.setCellValue(protocol.h_hhUAB2)
//                                "#H_HHUBC2#" -> cell.setCellValue(protocol.h_hhUBC2)
//                                "#H_HHUCA2#" -> cell.setCellValue(protocol.h_hhUCA2)
//                                "#H_HHIA2#" -> cell.setCellValue(protocol.h_hhIA2)
//                                "#H_HHIB2#" -> cell.setCellValue(protocol.h_hhIB2)
//                                "#H_HHIC2#" -> cell.setCellValue(protocol.h_hhIC2)
//                                "#H_HHUAB3#" -> cell.setCellValue(protocol.h_hhUAB3)
//                                "#H_HHUBC3#" -> cell.setCellValue(protocol.h_hhUBC3)
//                                "#H_HHUCA3#" -> cell.setCellValue(protocol.h_hhUCA3)
//                                "#H_HHIA3#" -> cell.setCellValue(protocol.h_hhIA3)
//                                "#H_HHIB3#" -> cell.setCellValue(protocol.h_hhIB3)
//                                "#H_HHIC3#" -> cell.setCellValue(protocol.h_hhIC3)
//                                "#H_HHUAB4#" -> cell.setCellValue(protocol.h_hhUAB4)
//                                "#H_HHUBC4#" -> cell.setCellValue(protocol.h_hhUBC4)
//                                "#H_HHUCA4#" -> cell.setCellValue(protocol.h_hhUCA4)
//                                "#H_HHIA4#" -> cell.setCellValue(protocol.h_hhIA4)
//                                "#H_HHIB4#" -> cell.setCellValue(protocol.h_hhIB4)
//                                "#H_HHIC4#" -> cell.setCellValue(protocol.h_hhIC4)
//                                "#H_HHUAB5#" -> cell.setCellValue(protocol.h_hhUAB5)
//                                "#H_HHUBC5#" -> cell.setCellValue(protocol.h_hhUBC5)
//                                "#H_HHUCA5#" -> cell.setCellValue(protocol.h_hhUCA5)
//                                "#H_HHIA5#" -> cell.setCellValue(protocol.h_hhIA5)
//                                "#H_HHIB5#" -> cell.setCellValue(protocol.h_hhIB5)
//                                "#H_HHIC5#" -> cell.setCellValue(protocol.h_hhIC5)
//                                "#H_HHUAB6#" -> cell.setCellValue(protocol.h_hhUAB6)
//                                "#H_HHUBC6#" -> cell.setCellValue(protocol.h_hhUBC6)
//                                "#H_HHUCA6#" -> cell.setCellValue(protocol.h_hhUCA6)
//                                "#H_HHIA6#" -> cell.setCellValue(protocol.h_hhIA6)
//                                "#H_HHIB6#" -> cell.setCellValue(protocol.h_hhIB6)
//                                "#H_HHIC6#" -> cell.setCellValue(protocol.h_hhIC6)
//                                "#H_HHUAB7#" -> cell.setCellValue(protocol.h_hhUAB7)
//                                "#H_HHUBC7#" -> cell.setCellValue(protocol.h_hhUBC7)
//                                "#H_HHUCA7#" -> cell.setCellValue(protocol.h_hhUCA7)
//                                "#H_HHIA7#" -> cell.setCellValue(protocol.h_hhIA7)
//                                "#H_HHIB7#" -> cell.setCellValue(protocol.h_hhIB7)
//                                "#H_HHIC7#" -> cell.setCellValue(protocol.h_hhIC7)
//                                "#H_HHUAB8#" -> cell.setCellValue(protocol.h_hhUAB8)
//                                "#H_HHUBC8#" -> cell.setCellValue(protocol.h_hhUBC8)
//                                "#H_HHUCA8#" -> cell.setCellValue(protocol.h_hhUCA8)
//                                "#H_HHIA8#" -> cell.setCellValue(protocol.h_hhIA8)
//                                "#H_HHIB8#" -> cell.setCellValue(protocol.h_hhIB8)
//                                "#H_HHIC8#" -> cell.setCellValue(protocol.h_hhIC8)
//                                "#H_HHUAB9#" -> cell.setCellValue(protocol.h_hhUAB9)
//                                "#H_HHUBC9#" -> cell.setCellValue(protocol.h_hhUBC9)
//                                "#H_HHUCA9#" -> cell.setCellValue(protocol.h_hhUCA9)
//                                "#H_HHIA9#" -> cell.setCellValue(protocol.h_hhIA9)
//                                "#H_HHIB9#" -> cell.setCellValue(protocol.h_hhIB9)
//                                "#H_HHIC9#" -> cell.setCellValue(protocol.h_hhIC9)
//                                "#H_HHN1#" -> cell.setCellValue(protocol.h_hhN1)
//                                "#H_HHN2#" -> cell.setCellValue(protocol.h_hhN2)
//                                "#H_HHN3#" -> cell.setCellValue(protocol.h_hhN3)
//                                "#H_HHN4#" -> cell.setCellValue(protocol.h_hhN4)
//                                "#H_HHN5#" -> cell.setCellValue(protocol.h_hhN5)
//                                "#H_HHN6#" -> cell.setCellValue(protocol.h_hhN6)
//                                "#H_HHN7#" -> cell.setCellValue(protocol.h_hhN7)
//                                "#H_HHN8#" -> cell.setCellValue(protocol.h_hhN8)
//                                "#H_HHN9#" -> cell.setCellValue(protocol.h_hhN9)
//                                "#H_HHRESULT#"-> cell.setCellValue(protocol.h_hhResult)
                                //N
                                "#NUAB#" -> cell.setCellValue(protocol.nUAB)
                                "#NUBC#" -> cell.setCellValue(protocol.nUBC)
                                "#NUCA#" -> cell.setCellValue(protocol.nUCA)
                                "#NIA#" -> cell.setCellValue(protocol.nIA)
                                "#NIB#" -> cell.setCellValue(protocol.nIB)
                                "#NIC#" -> cell.setCellValue(protocol.nIC)
                                "#NSPEED#" -> cell.setCellValue(protocol.nSpeed)
                                "#NF#" -> cell.setCellValue(protocol.nF)
                                "#NRESULT#" -> cell.setCellValue(protocol.nResult)
                                //KTR
                                "#KTR_UAVG1#" -> cell.setCellValue(protocol.ktrUAVG1)
                                "#KTR_UAVG2#" -> cell.setCellValue(protocol.ktrUAVG2)
                                "#KTR_KTR#" -> cell.setCellValue(protocol.ktrKTR)
                                "#KTR_RESULT#" -> cell.setCellValue(protocol.ktrResult)
                                //MV
                                "#MVUAB1#" -> cell.setCellValue(protocol.mvUAB1)
                                "#MVUBC1#" -> cell.setCellValue(protocol.mvUBC1)
                                "#MVUCA1#" -> cell.setCellValue(protocol.mvUCA1)
                                "#MVIA1#" -> cell.setCellValue(protocol.mvIA1)
                                "#MVIB1#" -> cell.setCellValue(protocol.mvIB1)
                                "#MVIC1#" -> cell.setCellValue(protocol.mvIC1)
                                "#MVUAB2#" -> cell.setCellValue(protocol.mvUAB2)
                                "#MVUBC2#" -> cell.setCellValue(protocol.mvUBC2)
                                "#MVUCA2#" -> cell.setCellValue(protocol.mvUCA2)
                                "#MVIA2#" -> cell.setCellValue(protocol.mvIA2)
                                "#MVIB2#" -> cell.setCellValue(protocol.mvIB2)
                                "#MVIC2#" -> cell.setCellValue(protocol.mvIC2)
                                "#MVDEVIATION#" -> cell.setCellValue(protocol.mvDeviation)
                                "#MVRESULT#" -> cell.setCellValue(protocol.mvResult)
                                //KZ
                                "#KZUAB#" -> cell.setCellValue(protocol.kzUAB)
                                "#KZUBC#" -> cell.setCellValue(protocol.kzUBC)
                                "#KZUCA#" -> cell.setCellValue(protocol.kzUCA)
                                "#KZIA#" -> cell.setCellValue(protocol.kzIA)
                                "#KZIB#" -> cell.setCellValue(protocol.kzIB)
                                "#KZIC#" -> cell.setCellValue(protocol.kzIC)
                                "#KZP1#" -> cell.setCellValue(protocol.kzP1)
                                "#KZRESULT#" -> cell.setCellValue(protocol.kzResult)

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
            val outStream = ByteArrayOutputStream()
            wb.write(outStream)
            outStream.close()
        }
    } catch (e: FileNotFoundException) {
        Toast.makeText("Не удалось сохранить протокол на диск")
    }
}


