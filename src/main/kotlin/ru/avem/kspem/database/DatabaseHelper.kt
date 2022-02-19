package ru.avem.kspem.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.kspem.app.Main
import ru.avem.kspem.data.motorType
import ru.avem.kspem.data.schemeType
import ru.avem.kspem.database.entities.*
import ru.avem.kspem.database.entities.Users.login
import ru.avem.kspem.utils.copyFileFromStream
import java.io.File
import java.sql.Connection
import java.text.SimpleDateFormat

fun validateDB() {

    if (!File("cfg").exists()) {
        File("cfg").mkdir()
    }

    if (!File("cfg\\log.txt").exists()) {
        File("cfg\\log.txt").createNewFile()
        File("cfg\\log.txt").setWritable(true)
    } else if (File("cfg\\log.txt").length() > 200000) {
        File("cfg\\log.txt").delete()
        File("cfg\\log.txt").createNewFile()
        File("cfg\\log.txt").setWritable(true)
    }
    if (!File("cfg\\ReadMe.txt").exists()) {
        copyFileFromStream(Main::class.java.getResource("ReadMe.txt").openStream(), File("cfg/ReadMe.txt"))
    }
    if (!File("cfg\\protocol.xlsx").exists()) {
        copyFileFromStream(Main::class.java.getResource("protocol.xlsx").openStream(), File("cfg/protocol.xlsx"))
    }

    File("cfg\\log.txt").appendText("\n-/-/-/-/  Программа запущена  ${SimpleDateFormat("dd.MM.y").format(System.currentTimeMillis())}  /-/-/-/-")

    Database.connect("jdbc:sqlite:cfg\\data.db", "org.sqlite.JDBC")
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE

    transaction {
        SchemaUtils.create(Users, ProtocolsTable, Objects)
    }

    transaction {
        val admin = User.find {
            login eq "admin"
        }

        if (admin.empty()) {
            User.new {
                login = "admin"
                password = "avem"
            }
        }

        if (TestObjects.all().count() < 1) {
            TestObjects.new {
                name = "ВЭМ3"
                type = motorType.dpt
                p2 = "100"
                uNom = "100"
                iN = "10.0"
                nAsync = "740"
                kpd = "93.0"
                cos = "0.75"
                scheme = schemeType.triangle
                uVIU = "3600"
                uMGR = "1000"
//                temp_koef = "0.00425"
//                rMGRmax = ""
//                rMGRmin = ""
//                rPhaseMax = ""
//                rPhaseMin = ""
                timeVIU = "60"
                timeHH = "60"
                timeMVZ = "60"
                timeRUNNING = "60"
//                uVIU = "0.2"
//                iMVZ = "2"
                iOV = "10"
                uOV = "100"
            }


            Protocol.new {
                objectName = "ВЭМ3"
                type = "Синхронный двигатель"
                date = SimpleDateFormat("dd.MM.y").format(System.currentTimeMillis()).toString()
                time = SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()).toString()
                operator = "Тестовый протокол"
                serial = "5АМ250М8УПУ3"
                p2 = "100"
                uN = "380"
                iN = "98.0"
                nAsync = "740"
                kpd = "93.0"
                cos = "0.75"
                scheme = schemeType.star
                //MGR//
                mgrU = "1000"
                mgrR15 = "3496"
                mgrR60 = "8796"
                mgrkABS = "2.5"
                mgrTemp = "13.6"
                mgrResult = "Успешно"
                //VIU//
                viuU = "1499"
                viuI = "12.5"
                viuTime = "10"
                viuResult = "Успешно"

                //IKAS//
                ikasR1 = "0.1565"
                ikasR2 = "0.1556"
                ikasR3 = "0.1558"
                ikasResult = "Успешно"

                //HH//
                hhUAB = "5940"
                hhUBC = "6000"
                hhUCA = "6050"
                hhIA = "0.30"
                hhIB = "0.30"
                hhIC = "0.30"
                hhTempOI = "15.4"
                hhTempAmb = "15.9"
                hhSpeed = "747"
                hhVibro1 = "0.092"
                hhVibro2 = "0.051"
                hhP1 = "1"
                hhCos = "0.16"
                hhTime = "30"
                hhResult = "Успешно"

                //RUNNING//
                runningUAB = "5960"
                runningUBC = "6000"
                runningUCA = "6040"
                runningIA = "0.30"
                runningIB = "0.30"
                runningIC = "0.30"
                runningTempOI = "14.9"
                runningTempAmb = "15.2"
                runningSpeed = "749"
                runningVibro1 = "0.11"
                runningVibro2 = "0.057"
                runningTime = "30"
                runningP1 = "1"
                runningCos = "0.16"
                runningResult = "Успешно"
                //LOAD//
                loadUOV     = "361"
                loadIOV     = "362"
                loadUOY     = "363"
                loadIOY     = "364"
                loadN       = "365"
                loadP       = "366"
                loadTempAmb = "367"
                loadTempOI  = "368"
                loadResult  = "Успешно"
                //H_HH//
//                h_hhUAB1 = "494"
//                h_hhUBC1 = "494"
//                h_hhUCA1 = "494"
//                h_hhIA1 = "60"
//                h_hhIB1 = "60"
//                h_hhIC1 = "60"
//                h_hhUAB2 = "460"
//                h_hhUBC2 = "460"
//                h_hhUCA2 = "460"
//                h_hhIA2 = "58"
//                h_hhIB2 = "58"
//                h_hhIC2 = "58"
//                h_hhUAB3 = "426"
//                h_hhUBC3 = "426"
//                h_hhUCA3 = "426"
//                h_hhIA3 = "56"
//                h_hhIB3 = "56"
//                h_hhIC3 = "56"
//                h_hhUAB4 = "380"
//                h_hhUBC4 = "380"
//                h_hhUCA4 = "380"
//                h_hhIA4 = "54"
//                h_hhIB4 = "54"
//                h_hhIC4 = "54"
//                h_hhUAB5 = "340"
//                h_hhUBC5 = "340"
//                h_hhUCA5 = "340"
//                h_hhIA5 = "52"
//                h_hhIB5 = "52"
//                h_hhIC5 = "52"
//                h_hhUAB6 = "300"
//                h_hhUBC6 = "300"
//                h_hhUCA6 = "300"
//                h_hhIA6 = "48"
//                h_hhIB6 = "48"
//                h_hhIC6 = "48"
//                h_hhUAB7 = "270"
//                h_hhUBC7 = "270"
//                h_hhUCA7 = "270"
//                h_hhIA7 = "44"
//                h_hhIB7 = "44"
//                h_hhIC7 = "44"
//                h_hhUAB8 = "240"
//                h_hhUBC8 = "240"
//                h_hhUCA8 = "240"
//                h_hhIA8 = "38"
//                h_hhIB8 = "38"
//                h_hhIC8 = "38"
//                h_hhUAB9 = "180"
//                h_hhUBC9 = "180"
//                h_hhUCA9 = "180"
//                h_hhIA9 = "35"
//                h_hhIB9 = "35"
//                h_hhIC9 = "35"
//                h_hhN1 = "750"
//                h_hhN2 = "750"
//                h_hhN3 = "750"
//                h_hhN4 = "750"
//                h_hhN5 = "750"
//                h_hhN6 = "750"
//                h_hhN7 = "748"
//                h_hhN8 = "746"
//                h_hhN9 = "740"
//                h_hhResult = "Успешно"
                        //N//
                nUAB = "5870"
                nUBC = "6010"
                nUCA = "6030"
                nIA = "0.20"
                nIB = "0.30"
                nIC = "0.30"
                nSpeed = "902"
                nF = "60"
                nResult = "Успешно"
                //KTr//
                ktrUAVG1 = "101.1"
                ktrUAVG2 = "10.9"
                ktrKTR  = "9.3"
                ktrResult = "Успешно"
                //MV//
                mvUAB1 = "5950"
                mvUBC1 = "6010"
                mvUCA1 = "6050"
                mvIA1 = "0.30"
                mvIB1 = "0.30"
                mvIC1 = "0.30"
                mvUAB2 = "6050"
                mvUBC2 = "5950"
                mvUCA2 = "6000"
                mvIA2 = "0.30"
                mvIB2 = "0.30"
                mvIC2 = "0.30"
                mvDeviation = "0"
                mvResult = "Успешно"
                //KZ//
                kzUAB = "1580"
                kzUBC = "1600"
                kzUCA = "1550"
                kzIA = "0.50"
                kzIB = "0.60"
                kzIC = "0.50"
                kzP1 = "1"
                kzResult = "Успешно"
            }
        }
    }
}
