package ru.avem.kspem.communication.model.devices.delta

import ru.avem.kspem.communication.model.DeviceRegister
import ru.avem.kspem.communication.model.IDeviceModel

class DeltaModel : IDeviceModel {
    companion object {
        const val ERRORS_REGISTER = "ERRORS_REGISTER"
        const val STATUS_REGISTER = "STATUS_REGISTER"
        const val CURRENT_FREQUENCY_INPUT_REGISTER = "CURRENT_FREQUENCY_INPUT_REGISTER"
        const val CONTROL_REGISTER = "CONTROL_REGISTER"
        const val CURRENT_FREQUENCY_OUTPUT_REGISTER = "CURRENT_FREQUENCY_OUTPUT_REGISTER"
        const val MAX_FREQUENCY_REGISTER = "MAX_FREQUENCY_REGISTER"
        const val NOM_FREQUENCY_REGISTER = "NOM_FREQUENCY_REGISTER"
        const val MAX_VOLTAGE_REGISTER = "MAX_VOLTAGE_REGISTER"
        const val POINT_1_FREQUENCY_REGISTER = "POINT_1_FREQUENCY_REGISTER"
        const val POINT_1_VOLTAGE_REGISTER = "POINT_1_VOLTAGE_REGISTER"
        const val POINT_2_FREQUENCY_REGISTER = "POINT_2_FREQUENCY_REGISTER"
        const val POINT_2_VOLTAGE_REGISTER = "POINT_2_VOLTAGE_REGISTER"
        const val CURRENT_AMPER = "CURRENT_AMPER"
        const val CURRENT_FREQ = "CURRENT_FREQ"
        const val CURRENT_VOLT = "CURRENT_VOLT"
        const val CURRENT_POWER = "CURRENT_POWER"
    }

    override val registers: Map<String, DeviceRegister> = mapOf(
        CURRENT_AMPER to DeviceRegister(0x2200, DeviceRegister.RegisterValueType.SHORT),
        CURRENT_FREQ to DeviceRegister(0x2202, DeviceRegister.RegisterValueType.SHORT),
        CURRENT_VOLT to DeviceRegister(0x2204, DeviceRegister.RegisterValueType.SHORT),
        CURRENT_POWER to DeviceRegister(0x2206, DeviceRegister.RegisterValueType.SHORT),
        ERRORS_REGISTER to DeviceRegister(0x2100, DeviceRegister.RegisterValueType.SHORT),
        STATUS_REGISTER to DeviceRegister(0x2101, DeviceRegister.RegisterValueType.SHORT),
        CURRENT_FREQUENCY_INPUT_REGISTER to DeviceRegister(0x2103, DeviceRegister.RegisterValueType.SHORT),
        CONTROL_REGISTER to DeviceRegister(0x2000, DeviceRegister.RegisterValueType.SHORT),
        CURRENT_FREQUENCY_OUTPUT_REGISTER to DeviceRegister(0x2001, DeviceRegister.RegisterValueType.SHORT),
        MAX_FREQUENCY_REGISTER to DeviceRegister(0x0100, DeviceRegister.RegisterValueType.SHORT),
        NOM_FREQUENCY_REGISTER to DeviceRegister(0x0101, DeviceRegister.RegisterValueType.SHORT),
        MAX_VOLTAGE_REGISTER to DeviceRegister(0x0102, DeviceRegister.RegisterValueType.SHORT),
        POINT_1_FREQUENCY_REGISTER to DeviceRegister(0x0103, DeviceRegister.RegisterValueType.SHORT),
        POINT_1_VOLTAGE_REGISTER to DeviceRegister(0x0104, DeviceRegister.RegisterValueType.SHORT),
        POINT_2_FREQUENCY_REGISTER to DeviceRegister(0x0105, DeviceRegister.RegisterValueType.SHORT),
        POINT_2_VOLTAGE_REGISTER to DeviceRegister(0x0106, DeviceRegister.RegisterValueType.SHORT)
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")

    var outMask: Short = 0
}