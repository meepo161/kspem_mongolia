package ru.avem.kspem.view.expViews

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.communication.utils.schemeMessage
import tornadofx.*


class KZView : View() {
    val name = "Определение тока и потерь КЗ"
    val data = KZData()

    override fun onDock() {
        super.onDock()
        runLater {
            clearTables()
        }
    }

    override val root = vbox(16.0, Pos.CENTER) {
        label(name)
        separator()
        padding = insets(8)

        hboxConstraints {
            hGrow = Priority.ALWAYS
        }
        label("Измеренные значения")
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("Uab, В", KZData::uAB.getter).isEditable = false
            column("Ubc, В", KZData::uBC.getter).isEditable = false
            column("Uca, В", KZData::uCA.getter).isEditable = false
            column("Ia, А", KZData::iA.getter).isEditable = false
            column("Ib, А", KZData::iB.getter).isEditable = false
            column("Ic, А", KZData::iC.getter).isEditable = false
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("f, Гц", KZData::f.getter).isEditable = false
            column("P1, кВт", KZData::p.getter).isEditable = false
            column("cosφ, о.е.", KZData::cos.getter).isEditable = false
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("n, об/мин", KZData::n.getter).isEditable = false
            column("t воздуха, °C", KZData::tempAmb.getter)
            column("t ОИ, °C", KZData::tempOI.getter)
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("Время, сек", KZData::timeExp.getter)
            column("Результат", KZData::result.getter)
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
    }
    fun clearTables() {
        data.result.value = ""
        data.cos.value = ""
        data.f.value = ""
        data.iA.value = ""
        data.iB.value = ""
        data.iC.value = ""
        data.n.value = ""
        data.p.value = ""
        data.tempAmb.value = ""
        data.tempOI.value = ""
        data.uAB.value = ""
        data.uCA.value = ""
        data.uBC.value = ""
        data.timeExp.value = ""
    }
}
data class KZData(
    val tempOI: StringProperty = SimpleStringProperty(""),
    val tempAmb: StringProperty = SimpleStringProperty(""),
    val n: StringProperty = SimpleStringProperty(""),
    val uAB: StringProperty = SimpleStringProperty(""),
    val uBC: StringProperty = SimpleStringProperty(""),
    val uCA: StringProperty = SimpleStringProperty(""),
    val timeExp: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty(""),
    val iA: StringProperty = SimpleStringProperty(""),
    val iB: StringProperty = SimpleStringProperty(""),
    val iC: StringProperty = SimpleStringProperty(""),
    val f: StringProperty = SimpleStringProperty(""),
    val p: StringProperty = SimpleStringProperty(""),
    val cos: StringProperty = SimpleStringProperty("")
)