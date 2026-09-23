package com.irisaco.iotequipment.data.repository

import android.util.Log
import com.irisaco.iotequipment.data.model.Document
import com.irisaco.iotequipment.data.model.EquipmentConfiguration
import com.irisaco.iotequipment.data.model.EquipmentLOM
import com.irisaco.iotequipment.data.model.EquipmentType
import com.irisaco.iotequipment.data.model.Items
import com.irisaco.iotequipment.data.model.Records
import com.irisaco.iotequipment.data.model.User
import com.irisaco.iotequipment.data.model.Version
import com.irisaco.iotequipment.data.remote.EquipmentApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EquipmentRepository @Inject constructor(
    private val apiService: EquipmentApiService,
) {
    private val mockEquipments: MutableList<Items> = createInitialMockData()

    suspend fun getEquipments(): List<Items> = withContext(Dispatchers.IO) {
        mockEquipments.toList()
    }

    suspend fun getEquipmentById(id: Int): Items? = withContext(Dispatchers.Default) {
        mockEquipments.find { it.id == id }
    }

    suspend fun getRecordsForEquipment(id: Int): List<Records> =
        withContext(Dispatchers.IO) {
            val equipment = getEquipmentById(id)
            equipment?.records?.toList() ?: emptyList()
        }

    suspend fun addRecord(record: Records): Records? = withContext(Dispatchers.IO) {
        val equipment = getEquipmentById(record.equipmentId ?: return@withContext null)
        val allRecordIds = mockEquipments.flatMap { it.records }.mapNotNull { it.id }
        val newId = (allRecordIds.maxOrNull() ?: 1000) + 1

        val newRecord = record.copy(
            id = newId,
            creationTime = record.creationTime ?: Instant.now().toString(),
            lastModifyTime = Instant.now().toString(),
            user = User(fullName = "Saleh Hajkarami", roleName = "Admin")
        )
        equipment?.records?.add(0, newRecord)
        newRecord
    }

    suspend fun updateRecord(record: Records): Records = withContext(Dispatchers.IO) {
        val equipment = getEquipmentById(record.equipmentId ?: 0)
        val index = equipment?.records?.indexOfFirst { it.id == record.id } ?: -1
        if (index != -1 && equipment != null) {
            val updated = record.copy(lastModifyTime = Instant.now().toString())
            equipment.records[index] = updated
            return@withContext updated
        }
        record
    }

    suspend fun deleteRecord(equipmentId: Int, recordId: Int) = withContext(Dispatchers.IO) {
        val equipment = getEquipmentById(equipmentId)
        equipment?.records?.removeAll { it.id == recordId }
    }

    suspend fun getDocumentForEquipment(id: Int): List<Document> =
        withContext(Dispatchers.IO) {
            val equipment = getEquipmentById(id)
            equipment?.documents?.toList() ?: emptyList()
        }

    suspend fun search(query: String, onlyById: Boolean): List<Items> = withContext(Dispatchers.Default) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) return@withContext mockEquipments.toList()

        return@withContext mockEquipments.filter { item ->
            val matchesId = item.equipmentUniqueId?.contains(trimmedQuery, ignoreCase = true) == true
            val matchesTitle = item.title?.contains(trimmedQuery, ignoreCase = true) == true
            val matchesType = item.equipmentType?.name?.contains(trimmedQuery, ignoreCase = true) == true
            val matchesDesc = item.description?.contains(trimmedQuery, ignoreCase = true) == true
            matchesId || matchesTitle || matchesType || matchesDesc
        }
    }

    suspend fun uploadRecordDocument(
        equipmentId: Int,
        recordId: Int?,
        file: File
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val ext = if (file.extension.isNotEmpty()) ".${file.extension}" else ".pdf"
            val newDoc = Document(
                id = (1000..9999).random(),
                fileName = file.name,
                fileUrl = "file://${file.absolutePath}",
                type = ext,
                creationTime = Instant.now().toString(),
                lastModifyTime = Instant.now().toString(),
                user = User(fullName = "Saleh Hajkarami")
            )
            val equipment = getEquipmentById(equipmentId)
            equipment?.documents?.add(0, newDoc)

            if (recordId != null && recordId != -1) {
                val record = equipment?.records?.find { it.id == recordId }
                record?.documents?.add(0, newDoc)
            }
            true
        } catch (e: Exception) {
            Log.e("UPLOAD", "Error attaching document: ${e.message}")
            false
        }
    }

    private fun createInitialMockData(): MutableList<Items> {
        return mutableListOf(
            Items(
                id = 1,
                title = "PLC Siemens S7-1500 Controller",
                equipmentUniqueId = "EQ-PLC-101",
                equipmentVersionId = 1,
                productionDateTime = "2023-05-12T08:00:00.000Z",
                guid = "9a7d3b24-11e5-4f32-bb44-c7823f9821a1",
                description = "High-performance modular programmable logic controller for industrial automation, telemetry monitoring, and SCADA integration.",
                creationTime = "2023-06-01T10:00:00.000Z",
                lastModifyTime = "2024-08-18T14:15:00.000Z",
                version = Version(
                    id = 1,
                    equipmentTypeId = 1,
                    version = "v2.4.1",
                    description = "Firmware revision 2.4.1 with Modbus TCP and MQTT protocol support"
                ),
                equipmentType = EquipmentType(
                    id = 1,
                    name = "PLC Controller",
                    description = "Programmable Logic Controller",
                    abberation = "PLC"
                ),
                equipmentLOM = arrayListOf(
                    EquipmentLOM(id = 1, name = "CPU 1515-2 PN", quantity = 1, supplier = "Siemens AG", equipmentId = 1),
                    EquipmentLOM(id = 2, name = "DI 16x24VDC Module", quantity = 2, supplier = "Siemens AG", equipmentId = 1),
                    EquipmentLOM(id = 3, name = "DQ 16x24VDC Module", quantity = 2, supplier = "Siemens AG", equipmentId = 1),
                    EquipmentLOM(id = 4, name = "Power Supply 24V 10A", quantity = 1, supplier = "Phoenix Contact", equipmentId = 1),
                    EquipmentLOM(id = 5, name = "CP 1543-1 Security Module", quantity = 1, supplier = "Siemens AG", equipmentId = 1)
                ),
                documents = arrayListOf(
                    Document(id = 101, fileName = "User_Manual_S71500.pdf", fileUrl = "https://example.com/docs/manual.pdf", type = ".pdf", creationTime = "2023-06-05T09:00:00.000Z"),
                    Document(id = 102, fileName = "Cabinet_Wiring_Diagram.pdf", fileUrl = "https://example.com/docs/wiring.pdf", type = ".pdf", creationTime = "2023-06-05T09:10:00.000Z"),
                    Document(id = 103, fileName = "Firmware_Changelog_v2.4.docx", fileUrl = "https://example.com/docs/changelog.docx", type = ".docx", creationTime = "2024-04-10T11:00:00.000Z"),
                    Document(id = 104, fileName = "Maintenance_Checklist.xlsx", fileUrl = "https://example.com/docs/checklist.xlsx", type = ".xlsx", creationTime = "2024-04-10T11:15:00.000Z")
                ),
                records = arrayListOf(
                    Records(id = 1001, equipmentId = 1, description = "Preventive maintenance: Cleaned chassis, inspected bus connectors, updated firmware to v2.4.1.", creationTime = "2024-04-10T09:30:00.000Z", lastModifyTime = "2024-04-10T09:30:00.000Z", user = User(fullName = "Saleh Hajkarami", roleName = "Admin")),
                    Records(id = 1002, equipmentId = 1, description = "Routine diagnostic inspection: Temperature and power rail voltages within normal tolerance. All Modbus nodes responding.", creationTime = "2024-08-18T14:15:00.000Z", lastModifyTime = "2024-08-18T14:15:00.000Z", user = User(fullName = "Saleh Hajkarami", roleName = "Admin")),
                    Records(id = 1003, equipmentId = 1, description = "Replaced RTC backup battery and tested redundant power input failover.", creationTime = "2025-01-20T11:00:00.000Z", lastModifyTime = "2025-01-20T11:00:00.000Z", user = User(fullName = "Saleh Hajkarami", roleName = "Admin"))
                )
            ),
            Items(
                id = 2,
                title = "IoT Vibration Sensor VS-300",
                equipmentUniqueId = "EQ-VS-202",
                equipmentVersionId = 2,
                productionDateTime = "2023-09-20T10:00:00.000Z",
                guid = "b23c4d5e-22f6-5a43-cc55-d8934e0932b2",
                description = "Wireless tri-axial vibration transmitter for predictive maintenance and real-time bearing condition monitoring.",
                creationTime = "2023-10-01T08:00:00.000Z",
                lastModifyTime = "2024-11-04T13:45:00.000Z",
                version = Version(
                    id = 2,
                    equipmentTypeId = 2,
                    version = "v1.8.0",
                    description = "Tri-axial accelerometer firmware with FFT spectrum calculation"
                ),
                equipmentType = EquipmentType(
                    id = 2,
                    name = "Vibration Sensor",
                    description = "Condition Monitoring Sensor",
                    abberation = "VS"
                ),
                equipmentLOM = arrayListOf(
                    EquipmentLOM(id = 6, name = "Piezoelectric Accelerometer", quantity = 1, supplier = "Murata", equipmentId = 2),
                    EquipmentLOM(id = 7, name = "LoRaWAN 868MHz Transceiver", quantity = 1, supplier = "Semtech", equipmentId = 2),
                    EquipmentLOM(id = 8, name = "LiSOCl2 Battery 3.6V", quantity = 2, supplier = "Tadiran", equipmentId = 2)
                ),
                documents = arrayListOf(
                    Document(id = 201, fileName = "Datasheet_VS300.pdf", fileUrl = "https://example.com/docs/vs300.pdf", type = ".pdf", creationTime = "2023-10-01T08:00:00.000Z"),
                    Document(id = 202, fileName = "Calibration_Certificate.pdf", fileUrl = "https://example.com/docs/calib.pdf", type = ".pdf", creationTime = "2024-06-12T08:30:00.000Z")
                ),
                records = arrayListOf(
                    Records(id = 2001, equipmentId = 2, description = "Calibration verified against reference shaker table. Sensitivity adjusted on Z-axis.", creationTime = "2024-06-12T08:00:00.000Z", lastModifyTime = "2024-06-12T08:00:00.000Z", user = User(fullName = "Saleh Hajkarami", roleName = "Admin")),
                    Records(id = 2002, equipmentId = 2, description = "Replaced primary battery cell. LoRaWAN signal RSSI -72dBm (excellent).", creationTime = "2024-11-04T13:45:00.000Z", lastModifyTime = "2024-11-04T13:45:00.000Z", user = User(fullName = "Saleh Hajkarami", roleName = "Admin"))
                )
            ),
            Items(
                id = 3,
                title = "Industrial Edge Gateway EG-500",
                equipmentUniqueId = "EQ-GW-303",
                equipmentVersionId = 3,
                productionDateTime = "2023-11-15T09:00:00.000Z",
                guid = "c34d5e6f-33a7-6b54-dd66-e9045f1043c3",
                description = "Ruggedized DIN-rail IoT gateway collecting field sensor telemetry over RS-485 / Modbus RTU and publishing via MQTT over LTE.",
                creationTime = "2023-11-20T10:00:00.000Z",
                lastModifyTime = "2024-10-15T16:20:00.000Z",
                version = Version(
                    id = 3,
                    equipmentTypeId = 3,
                    version = "v3.1.2",
                    description = "Edge computing Linux build with Docker support and local buffering"
                ),
                equipmentType = EquipmentType(
                    id = 3,
                    name = "Edge Gateway",
                    description = "IoT Communications Gateway",
                    abberation = "GW"
                ),
                equipmentLOM = arrayListOf(
                    EquipmentLOM(id = 9, name = "Quad-Core ARM SoC Board", quantity = 1, supplier = "Advantech", equipmentId = 3),
                    EquipmentLOM(id = 10, name = "LTE Cat-4 Modem Module", quantity = 1, supplier = "Quectel", equipmentId = 3),
                    EquipmentLOM(id = 11, name = "Isolated RS-485 Transceiver", quantity = 2, supplier = "Analog Devices", equipmentId = 3)
                ),
                documents = arrayListOf(
                    Document(id = 301, fileName = "Gateway_QuickStart_Guide.pdf", fileUrl = "https://example.com/docs/eg500_quickstart.pdf", type = ".pdf", creationTime = "2023-11-20T10:00:00.000Z"),
                    Document(id = 302, fileName = "Network_Topology_Diagram.pdf", fileUrl = "https://example.com/docs/topology.pdf", type = ".pdf", creationTime = "2024-03-01T10:30:00.000Z")
                ),
                records = arrayListOf(
                    Records(id = 3001, equipmentId = 3, description = "Updated SSL/TLS certificates and verified secure MQTT broker uplink.", creationTime = "2024-03-01T10:20:00.000Z", lastModifyTime = "2024-03-01T10:20:00.000Z", user = User(fullName = "Saleh Hajkarami", roleName = "Admin")),
                    Records(id = 3002, equipmentId = 3, description = "SIM card data plan renewed. Network latency tested: 38ms average.", creationTime = "2024-10-15T16:20:00.000Z", lastModifyTime = "2024-10-15T16:20:00.000Z", user = User(fullName = "Saleh Hajkarami", roleName = "Admin"))
                )
            ),
            Items(
                id = 4,
                title = "Smart Temperature Transmitter TT-100",
                equipmentUniqueId = "EQ-TT-404",
                equipmentVersionId = 4,
                productionDateTime = "2024-01-10T12:00:00.000Z",
                guid = "d45e6f7a-44b8-7c65-ee77-fa156a2154d4",
                description = "High-precision dual-channel temperature transmitter supporting RTD Pt100/Pt1000 and thermocouple inputs with 4-20mA HART.",
                creationTime = "2024-01-15T11:00:00.000Z",
                lastModifyTime = "2024-07-15T15:00:00.000Z",
                version = Version(
                    id = 4,
                    equipmentTypeId = 4,
                    version = "v2.0.0",
                    description = "HART 7 compatible firmware with self-diagnostics"
                ),
                equipmentType = EquipmentType(
                    id = 4,
                    name = "Temperature Sensor",
                    description = "Process Temperature Transmitter",
                    abberation = "TT"
                ),
                equipmentLOM = arrayListOf(
                    EquipmentLOM(id = 12, name = "Class A PT100 RTD Probe", quantity = 2, supplier = "WIKA", equipmentId = 4),
                    EquipmentLOM(id = 13, name = "HART Modem Module", quantity = 1, supplier = "Analog Devices", equipmentId = 4)
                ),
                documents = arrayListOf(
                    Document(id = 401, fileName = "Calibration_Protocol_TT100.pdf", fileUrl = "https://example.com/docs/tt100_calib.pdf", type = ".pdf", creationTime = "2024-01-15T11:00:00.000Z")
                ),
                records = arrayListOf(
                    Records(id = 4001, equipmentId = 4, description = "Zero and span point calibration at 0°C and 100°C. Drift < 0.02%. Passed inspection.", creationTime = "2024-07-15T15:00:00.000Z", lastModifyTime = "2024-07-15T15:00:00.000Z", user = User(fullName = "Saleh Hajkarami", roleName = "Admin"))
                )
            ),
            Items(
                id = 5,
                title = "Pressure Transmitter PT-400",
                equipmentUniqueId = "EQ-PT-505",
                equipmentVersionId = 5,
                productionDateTime = "2024-02-18T14:30:00.000Z",
                guid = "e56f7a8b-55c9-8d76-ff88-0b267b3265e5",
                description = "Piezoresistive industrial pressure transmitter for pipeline pressure monitoring with surge protection and IP67 enclosure.",
                creationTime = "2024-02-20T09:00:00.000Z",
                lastModifyTime = "2024-09-02T10:15:00.000Z",
                version = Version(
                    id = 5,
                    equipmentTypeId = 5,
                    version = "v1.2.0",
                    description = "0-100 bar pressure range calibration profile"
                ),
                equipmentType = EquipmentType(
                    id = 5,
                    name = "Pressure Transmitter",
                    description = "Gauge and Absolute Pressure Sensor",
                    abberation = "PT"
                ),
                equipmentLOM = arrayListOf(
                    EquipmentLOM(id = 14, name = "Ceramic Piezoresistive Diaphragm", quantity = 1, supplier = "Endress+Hauser", equipmentId = 5),
                    EquipmentLOM(id = 15, name = "4-20mA Current Loop Board", quantity = 1, supplier = "Texas Instruments", equipmentId = 5)
                ),
                documents = arrayListOf(
                    Document(id = 501, fileName = "PT400_Technical_Specs.pdf", fileUrl = "https://example.com/docs/pt400_specs.pdf", type = ".pdf", creationTime = "2024-02-20T09:00:00.000Z")
                ),
                records = arrayListOf(
                    Records(id = 5001, equipmentId = 5, description = "Hydrostatic pressure testing completed up to 150 bar. Diaphragm seal verified.", creationTime = "2024-09-02T10:15:00.000Z", lastModifyTime = "2024-09-02T10:15:00.000Z", user = User(fullName = "Saleh Hajkarami", roleName = "Admin"))
                )
            ),
            Items(
                id = 6,
                title = "Electromagnetic Flow Meter FM-800",
                equipmentUniqueId = "EQ-FM-606",
                equipmentVersionId = 6,
                productionDateTime = "2024-04-05T11:00:00.000Z",
                guid = "f67a8b9c-66da-9e87-0099-1c378c4376f6",
                description = "Flanged electromagnetic flow meter for accurate flow measurement of conductive liquids in cooling water lines.",
                creationTime = "2024-04-10T14:00:00.000Z",
                lastModifyTime = "2024-10-18T09:00:00.000Z",
                version = Version(
                    id = 6,
                    equipmentTypeId = 6,
                    version = "v2.2.0",
                    description = "Digital flow signal processor with totalizer functionality"
                ),
                equipmentType = EquipmentType(
                    id = 6,
                    name = "Flow Meter",
                    description = "Industrial Electromagnetic Flowmeter",
                    abberation = "FM"
                ),
                equipmentLOM = arrayListOf(
                    EquipmentLOM(id = 16, name = "Hastelloy C Electrode Pair", quantity = 2, supplier = "KROHNE", equipmentId = 6),
                    EquipmentLOM(id = 17, name = "Digital Signal Converter IFC 100", quantity = 1, supplier = "KROHNE", equipmentId = 6)
                ),
                documents = arrayListOf(
                    Document(id = 601, fileName = "FM800_Installation_Manual.pdf", fileUrl = "https://example.com/docs/fm800_manual.pdf", type = ".pdf", creationTime = "2024-04-10T14:00:00.000Z"),
                    Document(id = 602, fileName = "Factory_Calibration_Sheet.xlsx", fileUrl = "https://example.com/docs/calib_sheet.xlsx", type = ".xlsx", creationTime = "2024-04-10T14:15:00.000Z")
                ),
                records = arrayListOf(
                    Records(id = 6001, equipmentId = 6, description = "Electrode cleaning and grounding resistance test. Grounding resistance 3.2 Ohms (Pass).", creationTime = "2024-10-18T09:00:00.000Z", lastModifyTime = "2024-10-18T09:00:00.000Z", user = User(fullName = "Saleh Hajkarami", roleName = "Admin"))
                )
            )
        )
    }
}