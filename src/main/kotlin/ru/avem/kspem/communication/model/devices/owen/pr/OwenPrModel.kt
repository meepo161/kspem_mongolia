package ru.avem.kspem.communication.model.devices.owen.pr

import ru.avem.kspem.communication.model.DeviceRegister
import ru.avem.kspem.communication.model.IDeviceModel

class OwenPrModel : IDeviceModel {
    companion object {
        const val RESET_DOG = "RESET_DOG"
        const val INPUTS_REGISTER1 = "INPUTS_REGISTER"
        const val INPUTS_REGISTER2 = "INPUTS_REGISTER2"
        const val KMS1_REGISTER = "KMS1_REGISTER"
        const val KMS2_REGISTER = "KMS2_REGISTER"
        const val TVN = "TVN"
        const val TRN = "TRN"
        const val ROTATE_UNM = "ROTATE_UNM"
        const val RES = "RES"
    }

    override val registers: Map<String, DeviceRegister> = mapOf(
        RESET_DOG to DeviceRegister(512, DeviceRegister.RegisterValueType.SHORT),
        INPUTS_REGISTER1 to DeviceRegister(513, DeviceRegister.RegisterValueType.SHORT),
        INPUTS_REGISTER2 to DeviceRegister(514, DeviceRegister.RegisterValueType.SHORT),
        KMS1_REGISTER to DeviceRegister(515, DeviceRegister.RegisterValueType.SHORT),
        KMS2_REGISTER to DeviceRegister(516, DeviceRegister.RegisterValueType.SHORT),
        TVN to DeviceRegister(520, DeviceRegister.RegisterValueType.FLOAT),
        TRN to DeviceRegister(522, DeviceRegister.RegisterValueType.FLOAT),
        RES to DeviceRegister(518, DeviceRegister.RegisterValueType.SHORT),
        ROTATE_UNM to DeviceRegister(517, DeviceRegister.RegisterValueType.SHORT)
    )

    override fun getRegisterById(idRegister: String) =
        registers[idRegister] ?: error("Такого регистра нет в описанной карте $idRegister")

    var outMask: Short = 0
}