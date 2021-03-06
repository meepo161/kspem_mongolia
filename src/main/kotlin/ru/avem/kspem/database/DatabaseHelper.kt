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

    if (!File("cfg\\protocolGPT.xlsx").exists()) {
        copyFileFromStream(Main::class.java.getResource("protocolGPT.xlsx").openStream(), File("cfg/protocolGPT.xlsx"))
    }
    if (!File("cfg\\protocolDPT.xlsx").exists()) {
        copyFileFromStream(Main::class.java.getResource("protocolDPT.xlsx").openStream(), File("cfg/protocolDPT.xlsx"))
    }
    if (!File("cfg\\protocolSD.xlsx").exists()) {
        copyFileFromStream(Main::class.java.getResource("protocolSD.xlsx").openStream(), File("cfg/protocolSD.xlsx"))
    }
    if (!File("cfg\\protocolSG.xlsx").exists()) {
        copyFileFromStream(Main::class.java.getResource("protocolSG.xlsx").openStream(), File("cfg/protocolSG.xlsx"))
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
                objectName = "objectName"
                type = motorType.dpt
                date = "date"
                time = "time"
                operator = "operator"
                serial = "serial"
                p2 = "p2"
                uN = "uN"
                iN = "iN"
                uOV = "uOV"
                iOV = "iOV"
                nAsync = "nAsync"
                kpd = "kpd"
                scheme = "scheme"


                mgrU1 = "mgrU1"
                mgrU2 = "mgrU2"
                mgrU3 = "mgrU3"
                mgrR151 = "mgrR151"
                mgrR152 = "mgrR152"
                mgrR153 = "mgrR153"
                mgrR601 = "mgrR601"
                mgrR602 = "mgrR602"
                mgrR603 = "mgrR603"
                mgrkABS1 = "mgrkABS1"
                mgrkABS2 = "mgrkABS2"
                mgrkABS3 = "mgrkABS3"
                mgrTemp = "mgrTemp"
                mgrResult1 = "mgrResult1"
                mgrResult2 = "mgrResult2"
                mgrResult3 = "mgrResult3"


                viuU = "viuU"
                viuI = "viuI"
                viuTime = "viuTime"
                viuResult = "viuResult"


                ikasR1 = "ikasR1"
                ikasR2 = "ikasR2"
                ikasR3 = "ikasR3"
                ikasResult = "ikasResult"


                dptNuOV = "dptNuOV"
                dptNiOV = "dptNiOV"
                dptNuN = "dptNuN"
                dptNiN = "dptNiN"
                dptNP1 = "dptNP1"
                dptNTOI = "dptNTOI"
                dptNTAmb = "dptNTAmb"
                dptNN = "dptNN"
                dptNResult = "dptNResult"


                dptHHuOV = "dptHHuOV"
                dptHHiOV = "dptHHiOV"
                dptHHuN = "dptHHuN"
                dptHHiN = "dptHHiN"
                dptHHP1 = "dptHHP1"
                dptHHTOI = "dptHHTOI"
                dptHHTAmb = "dptHHTAmb"
                dptHHN = "dptHHN"
                dptHHResult = "dptHHResult"
                dptHHTime = "dptHHTime"


                dptLOADuOV = "dptLOADuOV"
                dptLOADiOV = "dptLOADiOV"
                dptLOADuN = "dptLOADuN"
                dptLOADiN = "dptLOADiN"
                dptLOADP1 = "dptLOADP1"
                dptLOADTOI = "dptLOADTOI"
                dptLOADTAmb = "dptLOADTAmb"
                dptLOADN = "dptLOADN"
                dptLOADDots = "00:00:05, 1, 2, 3, 4"
                dptLOADResult = "dptLOADResult"


                gptNuOV = "gptNuOV"
                gptNiOV = "gptNiOV"
                gptNuN = "gptNuN"
                gptNiN = "gptNiN"
                gptNP1 = "gptNP1"
                gptNTOI = "gptNTOI"
                gptNTAmb = "gptNTAmb"
                gptNN = "gptNN"
                gptNResult = "gptNResult"


                nUAB = "nUAB"
                nUBC = "nUBC"
                nUCA = "nUCA"
                nIA = "nIA"
                nIB = "nIB"
                nIC = "nIC"
                nF = "nF"
                nTempOI = "nTempOI"
                nTempAmb = "nTempAmb"
                nSpeed = "nSpeed"
                nVibro1 = "nVibro1"
                nVibro2 = "nVibro2"
                nTime = "nTime"
                nP1 = "nP1"
                nCos = "nCos"
                nResult = "nResult"


                h_hhuAB1 = "h_hhuAB1"
                h_hhuBC1 = "h_hhuBC1"
                h_hhuCA1 = "h_hhuCA1"
                h_hhiA1 = "h_hhiA1"
                h_hhiB1 = "h_hhiB1"
                h_hhiC1 = "h_hhiC1"
                h_hhuOV1 = "h_hhuOV1"
                h_hhiOV1 = "h_hhiOV1"
                h_hhuAB2 = "h_hhuAB2"
                h_hhuBC2 = "h_hhuBC2"
                h_hhuCA2 = "h_hhuCA2"
                h_hhiA2 = "h_hhiA2"
                h_hhiB2 = "h_hhiB2"
                h_hhiC2 = "h_hhiC2"
                h_hhuOV2 = "h_hhuOV2"
                h_hhiOV2 = "h_hhiOV2"
                h_hhuAB3 = "h_hhuAB3"
                h_hhuBC3 = "h_hhuBC3"
                h_hhuCA3 = "h_hhuCA3"
                h_hhiA3 = "h_hhiA3"
                h_hhiB3 = "h_hhiB3"
                h_hhiC3 = "h_hhiC3"
                h_hhuOV3 = "h_hhuOV3"
                h_hhiOV3 = "h_hhiOV3"
                h_hhuAB4 = "h_hhuAB4"
                h_hhuBC4 = "h_hhuBC4"
                h_hhuCA4 = "h_hhuCA4"
                h_hhiA4 = "h_hhiA4"
                h_hhiB4 = "h_hhiB4"
                h_hhiC4 = "h_hhiC4"
                h_hhuOV4 = "h_hhuOV4"
                h_hhiOV4 = "h_hhiOV4"
                h_hhuAB5 = "h_hhuAB5"
                h_hhuBC5 = "h_hhuBC5"
                h_hhuCA5 = "h_hhuCA5"
                h_hhiA5 = "h_hhiA5"
                h_hhiB5 = "h_hhiB5"
                h_hhiC5 = "h_hhiC5"
                h_hhuOV5 = "h_hhuOV5"
                h_hhiOV5 = "h_hhiOV5"
                h_hhuAB6 = "h_hhuAB6"
                h_hhuBC6 = "h_hhuBC6"
                h_hhuCA6 = "h_hhuCA6"
                h_hhiA6 = "h_hhiA6"
                h_hhiB6 = "h_hhiB6"
                h_hhiC6 = "h_hhiC6"
                h_hhuOV6 = "h_hhuOV6"
                h_hhiOV6 = "h_hhiOV6"
                h_hhuAB7 = "h_hhuAB7"
                h_hhuBC7 = "h_hhuBC7"
                h_hhuCA7 = "h_hhuCA7"
                h_hhiA7 = "h_hhiA7"
                h_hhiB7 = "h_hhiB7"
                h_hhiC7 = "h_hhiC7"
                h_hhuOV7 = "h_hhuOV7"
                h_hhiOV7 = "h_hhiOV7"
                h_hhuAB8 = "h_hhuAB8"
                h_hhuBC8 = "h_hhuBC8"
                h_hhuCA8 = "h_hhuCA8"
                h_hhiA8 = "h_hhiA8"
                h_hhiB8 = "h_hhiB8"
                h_hhiC8 = "h_hhiC8"
                h_hhuOV8 = "h_hhuOV8"
                h_hhiOV8 = "h_hhiOV8"
                h_hhuAB9 = "h_hhuAB9"
                h_hhuBC9 = "h_hhuBC9"
                h_hhuCA9 = "h_hhuCA9"
                h_hhiA9 = "h_hhiA9"
                h_hhiB9 = "h_hhiB9"
                h_hhiC9 = "h_hhiC9"
                h_hhuOV9 = "h_hhuOV9"
                h_hhiOV9 = "h_hhiOV9"
                h_hhResult = "h_hhResult"

                kzN1 = "kzN1"
                kzCos1 = "kzCos1"
                kzUOV1 = "kzUOV1"
                kzIOV1 = "kzIOV1"
                kzUAB1 = "kzUAB1"
                kzUBC1 = "kzUBC1"
                kzUCA1 = "kzUCA1"
                kzIA1 = "kzIA1"
                kzIB1 = "kzIB1"
                kzIC1 = "kzIC1"
                kzP1 = "kzP1"
                kzF1 = "kzF1"

                kzN2 = "kzN2"
                kzCos2 = "kzCos2"
                kzUOV2 = "kzUOV2"
                kzIOV2 = "kzIOV2"
                kzUAB2 = "kzUAB2"
                kzUBC2 = "kzUBC2"
                kzUCA2 = "kzUCA2"
                kzIA2 = "kzIA2"
                kzIB2 = "kzIB2"
                kzIC2 = "kzIC2"
                kzP2 = "kzP2"
                kzF2 = "kzF2"

                kzN3 = "kzN3"
                kzCos3 = "kzCos3"
                kzUOV3 = "kzUOV3"
                kzIOV3 = "kzIOV3"
                kzUAB3 = "kzUAB3"
                kzUBC3 = "kzUBC3"
                kzUCA3 = "kzUCA3"
                kzIA3 = "kzIA3"
                kzIB3 = "kzIB3"
                kzIC3 = "kzIC3"
                kzP3 = "kzP3"
                kzF3 = "kzF3"

                kzN4 = "kzN4"
                kzCos4 = "kzCos4"
                kzUOV4 = "kzUOV4"
                kzIOV4 = "kzIOV4"
                kzUAB4 = "kzUAB4"
                kzUBC4 = "kzUBC4"
                kzUCA4 = "kzUCA4"
                kzIA4 = "kzIA4"
                kzIB4 = "kzIB4"
                kzIC4 = "kzIC4"
                kzP4 = "kzP4"
                kzF4 = "kzF4"

                kzN5 = "kzN5"
                kzCos5 = "kzCos5"
                kzUOV5 = "kzUOV5"
                kzIOV5 = "kzIOV5"
                kzUAB5 = "kzUAB5"
                kzUBC5 = "kzUBC5"
                kzUCA5 = "kzUCA5"
                kzIA5 = "kzIA5"
                kzIB5 = "kzIB5"
                kzIC5 = "kzIC5"
                kzP5 = "kzP5"
                kzF5 = "kzF5"
                
                kzN6 = "kzN6"
                kzCos6 = "kzCos6"
                kzUOV6 = "kzUOV6"
                kzIOV6 = "kzIOV6"
                kzUAB6 = "kzUAB6"
                kzUBC6 = "kzUBC6"
                kzUCA6 = "kzUCA6"
                kzIA6 = "kzIA6"
                kzIB6 = "kzIB6"
                kzIC6 = "kzIC6"
                kzP6 = "kzP6"
                kzF6 = "kzF6"
                kzResult = "kzResult"
            }
        }
    }
}
