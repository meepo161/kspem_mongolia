package ru.avem.kspem.communication.model.devices.avem.avem4

import ru.avem.kspem.communication.model.DeviceRegister
import ru.avem.kspem.communication.model.IDeviceModel

class Avem4Model : IDeviceModel {
    companion object {
        const val AMP = "AMP"
        const val AVG = "AVG"
        const val RMS = "RMS"
        const val FREQ = "FREQ"
        const val SERIAL_NUMBER = "SERIAL_NUMBER"
    }

    override val registers = mapOf(
        AMP to DeviceRegister(0x1000, DeviceRegister.RegisterValueType.FLOAT),
        AVG to DeviceRegister(0x1002, DeviceRegister.RegisterValueType.FLOAT),
        RMS to DeviceRegister(0x1004, DeviceRegister.RegisterValueType.FLOAT),
        FREQ to DeviceRegister(0x1006, DeviceRegister.RegisterValueType.FLOAT),
        SERIAL_NUMBER to DeviceRegister(0x1108, DeviceRegister.RegisterValueType.SHORT)
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")
}
