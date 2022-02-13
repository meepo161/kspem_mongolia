package ru.avem.kspem.communication.model.devices.owen.pr

import ru.avem.kserialpooler.communication.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.communication.adapters.utils.ModbusRegister
import ru.avem.kserialpooler.communication.utils.TransportException
import ru.avem.kspem.communication.model.DeviceRegister
import ru.avem.kspem.communication.model.IDeviceController
import ru.avem.kspem.utils.sleep
import ru.avem.stand.utils.second
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.math.pow

class OwenPr(
    override val name: String,
    override val protocolAdapter: ModbusRTUAdapter,
    override val id: Byte
) : IDeviceController {
    val model = OwenPrModel()
    override var isResponding = false
    override var requestTotalCount = 0
    override var requestSuccessCount = 0
    override val pollingRegisters = mutableListOf<DeviceRegister>()
    override val writingRegisters = mutableListOf<Pair<DeviceRegister, Number>>()

    var outMask1: Short = 0
    var outMask2: Short = 0


    companion object {
        const val TRIG_RESETER: Short = 0xFFFF.toShort()
        const val WD_RESETER: Short = 0b10
    }

//    override fun readRegister(register: DeviceRegister) {
//        isResponding = try {
//            transactionWithAttempts {
//                val modbusRegister =
//                    protocolAdapter.readHoldingRegisters(id, register.address, 1).map(ModbusRegister::toShort)
//                register.value = modbusRegister.first()
//            }
//            true
//        } catch (e: ru.avem.kserialpooler.communication.utils.TransportException) {
//            false
//        }
//    }

    override fun readRegister(register: DeviceRegister) {
        isResponding = try {
            transactionWithAttempts {
                when (register.valueType) {
                    DeviceRegister.RegisterValueType.SHORT -> {
                        val modbusRegister =
                            protocolAdapter.readHoldingRegisters(id, register.address, 1).map(ModbusRegister::toShort)
                        register.value = modbusRegister.first()
                    }
                    DeviceRegister.RegisterValueType.FLOAT -> {
                        val modbusRegister =
                            protocolAdapter.readHoldingRegisters(id, register.address, 2).map(ModbusRegister::toShort)
                        register.value =
                            ByteBuffer.allocate(4).putShort(modbusRegister.second()).putShort(modbusRegister.first())
                                .also { it.flip() }.float
                    }
                }
            }
            true
        } catch (e: TransportException) {
            false
        }
    }

    override fun readAllRegisters() {
        model.registers.values.forEach {
            readRegister(it)
        }
    }

    override fun <T : Number> writeRegister(register: DeviceRegister, value: T) {
        isResponding = try {
            when (value) {
                is Float -> {
                    val bb = ByteBuffer.allocate(4).putFloat(value)
                    val registers = listOf(ModbusRegister(bb.getShort(2)), ModbusRegister(bb.getShort(0)))
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, registers)
                    }
                }
                is Int -> {
                    val bb = ByteBuffer.allocate(4).putInt(value)
                    val registers = listOf(ModbusRegister(bb.getShort(2)), ModbusRegister(bb.getShort(0)))
                    transactionWithAttempts {
                        protocolAdapter.presetMultipleRegisters(id, register.address, registers)
                    }
                }
                is Short -> {
                    transactionWithAttempts {
                        protocolAdapter.presetSingleRegister(id, register.address, ModbusRegister(value))
                    }
                }
                else -> {
                    throw UnsupportedOperationException("Method can handle only with Float, Int and Short")
                }
            }
            true
        } catch (e: ru.avem.kserialpooler.communication.utils.TransportException) {
            false
        }
    }

    override fun writeRegisters(register: DeviceRegister, values: List<Short>) {
        val registers = values.map { ModbusRegister(it) }
        isResponding = try {
            transactionWithAttempts {
                protocolAdapter.presetMultipleRegisters(id, register.address, registers)
            }
            true
        } catch (e: ru.avem.kserialpooler.communication.utils.TransportException) {
            false
        }
    }

    override fun checkResponsibility() {
        model.registers.values.firstOrNull()?.let {
            readRegister(it)
        }
    }

    override fun getRegisterById(idRegister: String) = model.getRegisterById(idRegister)

    fun onRegister(kms: Int, bitPosition: Short) {
        if (kms == 1) {
            val nor = bitPosition - 1
            val outMask1Old = outMask1
            outMask1 = outMask1 or 2.0.pow(nor).toInt().toShort()
            if (outMask1Old != outMask1) {
                writeRegister(getRegisterById(OwenPrModel.KMS1_REGISTER), outMask1)
                sleep(1000)
            }
        } else if (kms == 2) {
            val nor = bitPosition - 1
            val outMask1Old = outMask2
            outMask2 = outMask2 or 2.0.pow(nor).toInt().toShort()
            if (outMask1Old != outMask2) {
                writeRegister(getRegisterById(OwenPrModel.KMS2_REGISTER), outMask2)
                sleep(1000)
            }
        } else throw(Exception("There are only 1 and 2 KMS"))
    }

    fun offRegister(kms: Int, bitPosition: Short) {
        if (kms == 1) {
            val nor = bitPosition - 1
            val outMask1Old = outMask1
            outMask1 = outMask1 and 2.0.pow(nor).toInt().inv().toShort()
            if (outMask1Old != outMask1) {
                writeRegister(getRegisterById(OwenPrModel.KMS1_REGISTER), outMask1)
                sleep(1000)
            }
        } else if (kms == 2) {
            val nor = bitPosition - 1
            val outMask1Old = outMask2
            outMask2 = outMask2 and 2.0.pow(nor).toInt().inv().toShort()
            if (outMask1Old != outMask2) {
                writeRegister(getRegisterById(OwenPrModel.KMS2_REGISTER), outMask2)
                sleep(1000)
            }

        } else throw(Exception("There are only 1 and 2 KMS"))
    }

    fun initOwenPR() {
        resetKMS()
        sleep(100)
        writeRegister(getRegisterById(OwenPrModel.RES), 1)
        sleep(100)
        writeRegister(getRegisterById(OwenPrModel.RES), 0)
    }

    fun resetKMS() {
        outMask1 = 0
        outMask2 = 0
        writeRegister(getRegisterById(OwenPrModel.KMS1_REGISTER), outMask1)
        writeRegister(getRegisterById(OwenPrModel.KMS2_REGISTER), outMask2)
        writeRegister(getRegisterById(OwenPrModel.TVN), 0f)
    }

    fun km1(stat: Boolean) {
        if (stat) {
            onRegister(1, 1)
        } else {
            offRegister(1, 1)
        }
    }

    fun unm(stat: Boolean) {
        if (stat) {
            onRegister(1, 3)
        } else {
            offRegister(1, 3)
        }
    }

    fun tvn(stat: Boolean) {
        if (stat) {
            onRegister(1, 4)
        } else {
            offRegister(1, 4)
        }
    }

    fun arn(stat: Boolean) {
        if (stat) {
            onRegister(1, 5)
        } else {
            offRegister(1, 5)
        }
    }

    fun vent(stat: Boolean) {
        if (stat) {
            onRegister(1, 6)
        } else {
            offRegister(1, 6)
        }
    }

    fun viu(stat: Boolean) {
        if (stat) {
            onRegister(2, 1)
        } else {
            offRegister(2, 1)
        }
    }

    fun mgr(stat: Boolean) {
        if (stat) {
            onRegister(2, 2)
        } else {
            offRegister(2, 2)
        }
    }

    fun ground(stat: Boolean) {
        if (stat) {
            onRegister(2, 3)
        } else {
            offRegister(2, 3)
        }
    }

    fun shunt(stat: Boolean) {
        if (stat) {
            onRegister(2, 4)
        } else {
            offRegister(2, 4)
        }
    }

    fun ov_oi(stat: Boolean) {
        if (stat) {
            onRegister(2, 5)
        } else {
            offRegister(2, 5)
        }
    }

    fun vv(stat: Boolean) {
        if (stat) {
            onRegister(2, 6)
        } else {
            offRegister(2, 6)
        }
    }

    fun setTVN(voltage: Double) {
        writeRegister(getRegisterById(OwenPrModel.TVN), voltage.toFloat())
    }

    fun setTRN(voltage: Double) {
        writeRegister(getRegisterById(OwenPrModel.TRN), voltage.toFloat())
    }

}
