package ru.avem.kspem.view.expViews.expViewsSG

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import ru.avem.kspem.utils.createScreenShot
import tornadofx.*


class NViewSG : View() {
    val name = "Испытание при повышенной частоте вращения"
    val data = NDataSG()

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
            column("U ОВ, В", NDataSG::uOV.getter).isEditable = false
            column("I ОВ, А", NDataSG::iOV.getter).isEditable = false
            column("f, Гц", NDataSG::f.getter).isEditable = false
            column("P1, кВт", NDataSG::p.getter).isEditable = false
            column("cosφ, о.е.", NDataSG::cos.getter).isEditable = false
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("U AB, В", NDataSG::uAB.getter).isEditable = false
            column("U BC, В", NDataSG::uBC.getter).isEditable = false
            column("U CA, В", NDataSG::uCA.getter).isEditable = false
            column("I A, А", NDataSG::iA.getter).isEditable = false
            column("I B, А", NDataSG::iB.getter).isEditable = false
            column("I C, А", NDataSG::iC.getter).isEditable = false
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("n, об/мин", NDataSG::n.getter).isEditable = false
            column("t воздуха, °C", NDataSG::tempAmb.getter)
            column("t ОИ, °C", NDataSG::tempOI.getter)
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("Время, сек", NDataSG::timeExp.getter)
            column("Результат", NDataSG::result.getter)
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
    }

    fun clearTables() {
        data.result.value = ""
        data.cos.value = ""
        data.uOV.value = ""
        data.iOV.value = ""
        data.n.value = ""
        data.p.value = ""
        data.tempAmb.value = ""
        data.tempOI.value = ""
        data.uAB.value = ""
        data.uBC.value = ""
        data.uCA.value = ""
        data.iA.value = ""
        data.iB.value = ""
        data.iC.value = ""
        data.f.value = ""
        data.timeExp.value = ""
    }

}
data class NDataSG(
    val tempOI: StringProperty = SimpleStringProperty(""),
    val tempAmb: StringProperty = SimpleStringProperty(""),
    val n: StringProperty = SimpleStringProperty(""),
    val uOV: StringProperty = SimpleStringProperty(""),
    val iOV: StringProperty = SimpleStringProperty(""),
    val uAB: StringProperty = SimpleStringProperty(""),
    val uBC: StringProperty = SimpleStringProperty(""),
    val uCA: StringProperty = SimpleStringProperty(""),
    val iA: StringProperty = SimpleStringProperty(""),
    val iB: StringProperty = SimpleStringProperty(""),
    val iC: StringProperty = SimpleStringProperty(""),
    val f: StringProperty = SimpleStringProperty(""),
    val timeExp: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty(""),
    val p: StringProperty = SimpleStringProperty(""),
    val cos: StringProperty = SimpleStringProperty("")
)