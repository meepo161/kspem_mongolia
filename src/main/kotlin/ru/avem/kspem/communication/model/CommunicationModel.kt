package ru.avem.kspem.communication.model

import ru.avem.kserialpooler.communication.Connection
import ru.avem.kserialpooler.communication.adapters.modbusrtu.ModbusRTUAdapter
import ru.avem.kserialpooler.communication.utils.SerialParameters
import ru.avem.kspem.app.Main.Companion.isAppRunning
import ru.avem.kspem.communication.adapters.serial.SerialAdapter
import ru.avem.kspem.communication.model.devices.avem.avem4.Avem4
import ru.avem.kspem.communication.model.devices.avem.avem7.Avem7
import ru.avem.kspem.communication.model.devices.cs02021.CS02021
import ru.avem.kspem.communication.model.devices.delta.Delta
import ru.avem.kspem.communication.model.devices.latr.Latr
import ru.avem.kspem.communication.model.devices.owen.pr.OwenPr
import ru.avem.kspem.communication.model.devices.pm130.PM130
import ru.avem.kspem.communication.model.devices.th01.TH01
import ru.avem.kspem.communication.model.devices.trm202.TRM202
import ru.avem.stand.modules.r.communication.model.devices.avem.ikas.IKAS8
import java.lang.Thread.sleep
import kotlin.concurrent.thread

object CommunicationModel {
    @Suppress("UNUSED_PARAMETER")
    enum class DeviceID(description: String) {
        DD2_1("ПР102-21.2416.06.2"),
        PA13("АВЭМ-3 (Iов)"),
        PA15("АВЭМ-3 (Iя)"),
        PV23("АВЭМ-4 (Uов)"),
        PV24("АВЭМ-3-04 ВВ"),
        PV25("АВЭМ-4 (Uя)"),
        PAV41("PM135"),
        PR61("ИКАС-8"),
        PC71("ТХ01-224.Щ2.Р.RS"),
        PS81("ТРМ202 - Термометр - (Вход 1 - окр. воздух | Вход 2 - ОИ)"),
        PR65("ЦС0202"),
        UZ91("ЧП Delta ОИ"),
        UZ96("ТВН"),
        GV240("АРН-5-220")
    }
    var isConnected = false

    var connection = Connection(
        adapterName = "CP2103 USB to RS-485",
        serialParameters = SerialParameters(8, 0, 1, 38400),
        timeoutRead = 100,
        timeoutWrite = 100
    ).apply {
        connect()
        isConnected = true
    }

    var isConnectedDelta = false
    private val connectionDelta = Connection(
        adapterName = "CP2103 USB to Delta",
        serialParameters = SerialParameters(8, 0, 1, 38400),
        timeoutRead = 100,
        timeoutWrite = 100
    ).apply {
        connect()
        isConnectedDelta = true
    }

    private val modbusAdapter = ModbusRTUAdapter(connection)
    private val csAdapter = SerialAdapter(connection)

    private val deltaAdapter = ModbusRTUAdapter(connectionDelta)


    private val devices: Map<DeviceID, IDeviceController> = mapOf(
        DeviceID.DD2_1 to OwenPr(DeviceID.DD2_1.toString(), modbusAdapter, 2),
        DeviceID.PA13 to Avem7(DeviceID.PA13.toString(), modbusAdapter, 13),
        DeviceID.PA15 to Avem7(DeviceID.PA15.toString(), modbusAdapter, 15),
        DeviceID.PV23 to Avem4(DeviceID.PV23.toString(), modbusAdapter, 23),
        DeviceID.PV24 to Avem4(DeviceID.PV24.toString(), modbusAdapter, 24),
        DeviceID.PV25 to Avem4(DeviceID.PV25.toString(), modbusAdapter, 25),
        DeviceID.PAV41 to PM130(DeviceID.PAV41.toString(), modbusAdapter, 41),
        DeviceID.PR61 to IKAS8(DeviceID.PR61.toString(), modbusAdapter, 61),
        DeviceID.PR65 to CS02021(DeviceID.PR65.toString(), csAdapter, 6),
        DeviceID.PC71 to TH01(DeviceID.PC71.toString(), modbusAdapter, 71),
        DeviceID.PS81 to TRM202(DeviceID.PS81.toString(), modbusAdapter, 81),
        DeviceID.UZ91 to Delta(DeviceID.UZ91.toString(), deltaAdapter, 91),
        DeviceID.GV240 to Latr(DeviceID.GV240.toString(), modbusAdapter, 240.toByte())
//        DeviceID.UZ96 на AO пр102
    )

    init {
        thread(isDaemon = true) {
            while (isAppRunning) {
                if (isConnected) {
                    devices.values.forEach {
                        it.readPollingRegisters()
                    }
                }
                sleep(1)
            }
        }
        thread(isDaemon = true) {
            while (isAppRunning) {
                if (isConnected) {
                    devices.values.forEach {
                        it.writeWritingRegisters()
                    }
                }
                sleep(1)
            }
        }
    }

    fun getDeviceById(deviceID: DeviceID) = devices[deviceID] ?: error("Не определено $deviceID")

    fun startPoll(deviceID: DeviceID, registerID: String, block: (Number) -> Unit) {
        val device = getDeviceById(deviceID)
        val register = device.getRegisterById(registerID)
        register.addObserver { _, arg ->
            block(arg as Number)
        }
        device.addPollingRegister(register)
    }

    fun <T : IDeviceController> device(deviceID: DeviceID): T {
        return devices[deviceID] as T
    }

    fun clearPollingRegisters() {
        devices.values.forEach(IDeviceController::removeAllPollingRegisters)
        devices.values.forEach(IDeviceController::removeAllWritingRegisters)
    }

    fun clearReadingRegisters() {
        devices.values.forEach(IDeviceController::removeAllPollingRegisters)
    }

    fun checkDevices(): List<DeviceID> {
        devices.values.forEach(IDeviceController::checkResponsibility)
        return devices.filter { !it.value.isResponding }.keys.toList()
    }

    fun addWritingRegister(deviceID: DeviceID, registerID: String, value: Number) {
        val device = getDeviceById(deviceID)
        val register = device.getRegisterById(registerID)
        device.addWritingRegister(register to value)
    }
}
