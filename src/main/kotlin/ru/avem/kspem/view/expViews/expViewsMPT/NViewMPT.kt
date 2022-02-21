package ru.avem.kspem.view.expViews.expViewsMPT

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import ru.avem.kspem.utils.createScreenShot
import tornadofx.*


class NViewMPT : View() {
    val name = "Испытание при повышенной частоте вращения"
    val data = NDataMPT()

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
            column("U ОВ, В", NDataMPT::uOV.getter).isEditable = false
            column("I ОВ, А", NDataMPT::iOV.getter).isEditable = false
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("U ОЯ, В", NDataMPT::uOY.getter).isEditable = false
            column("I ОЯ, А", NDataMPT::iOY.getter).isEditable = false
            column("P1, кВт", NDataMPT::p.getter).isEditable = false
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("n, об/мин", NDataMPT::n.getter).isEditable = false
            column("t воздуха, °C", NDataMPT::tempAmb.getter)
            column("t ОИ, °C", NDataMPT::tempOI.getter)
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("Время, сек", NDataMPT::timeExp.getter)
            column("Результат", NDataMPT::result.getter)
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
    }

    fun clearTables() {
        data.result.value = ""
        data.uOV.value = ""
        data.iOV.value = ""
        data.uOY.value = ""
        data.iOY.value = ""
        data.n.value = ""
        data.p.value = ""
        data.tempAmb.value = ""
        data.tempOI.value = ""
        data.timeExp.value = ""
    }

}

data class NDataMPT(
    val tempOI: StringProperty = SimpleStringProperty(""),
    val tempAmb: StringProperty = SimpleStringProperty(""),
    val n: StringProperty = SimpleStringProperty(""),
    val uOV: StringProperty = SimpleStringProperty(""),
    val iOV: StringProperty = SimpleStringProperty(""),
    val uOY: StringProperty = SimpleStringProperty(""),
    val iOY: StringProperty = SimpleStringProperty(""),
    val f: StringProperty = SimpleStringProperty(""),
    val timeExp: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty(""),
    val p: StringProperty = SimpleStringProperty(""),
    val cos: StringProperty = SimpleStringProperty("")
)