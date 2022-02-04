package ru.avem.kspem.database.entities

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object Objects : IntIdTable() {
    val name = varchar("name", 128)
    val type = varchar("type", 128)
    val p2 = varchar("p2", 128)
    val uN = varchar("uN", 128)
    val iN = varchar("iN", 128)
    val nAsync = varchar("nAsync", 128)
    val kpd = varchar("kpd", 128)
    val cos = varchar("cos", 128)
    val scheme = varchar("scheme", 128)
    val uVIU = varchar("uVIU", 128)
    val uMGR = varchar("uMGR", 128)
//    val rMGRmax = varchar("rMGRmax", 128)
//    val rMGRmin = varchar("rMGRmin", 128)
//    val rPhaseMax = varchar("rPhaseMax", 128)
//    val rPhaseMin = varchar("rPhaseMin", 128)
    val timeVIU = varchar("timeVIU", 128)
    val timeHH = varchar("timeHH", 128)
    val timeMVZ = varchar("timeMVZ", 128)
    val timeRUNNING = varchar("timeRUNNING", 128)
//    val iVIU = varchar("iVIU", 128)
//    val iMVZ = varchar("iMVZ", 128)
    val iOV = varchar("iOV", 128)
    val uOV = varchar("uOV", 128)
}

class TestObjects(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TestObjects>(Objects)

    var name by Objects.name
    var type by Objects.type
    var p2 by Objects.p2
    var uN by Objects.uN
    var iN by Objects.iN
    var nAsync by Objects.nAsync
    var kpd by Objects.kpd
    var cos by Objects.cos
    var scheme by Objects.scheme
    var uVIU by Objects.uVIU
    var uMGR by Objects.uMGR
//    var rMGRmax by Objects.rMGRmax
//    var rMGRmin by Objects.rMGRmin
//    var rPhaseMax by Objects.rPhaseMax
//    var rPhaseMin by Objects.rPhaseMin
    var timeVIU by Objects.timeVIU
    var timeHH by Objects.timeHH
    var timeMVZ by Objects.timeMVZ
    var timeRUNNING by Objects.timeRUNNING
//    var iVIU by Objects.iVIU
//    var iMVZ by Objects.iMVZ
    var iOV by Objects.iOV
    var uOV by Objects.uOV

    override fun toString(): String {
        return name
    }
}
