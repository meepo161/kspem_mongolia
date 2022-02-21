package ru.avem.kspem.view.expViews.expViewsGPT

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import ru.avem.kspem.utils.createScreenShot
import tornadofx.*


class NViewGPT : View() {
    val name = "Испытание при повышенной частоте вращения"
    val data = NDataGPT()

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
            column("U ОВ, В", NDataGPT::uOV.getter).isEditable = false
            column("I ОВ, А", NDataGPT::iOV.getter).isEditable = false
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("U ОЯ, В", NDataGPT::uOY.getter).isEditable = false
            column("I ОЯ, А", NDataGPT::iOY.getter).isEditable = false
            column("P1, кВт", NDataGPT::p.getter).isEditable = false
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("n, об/мин", NDataGPT::n.getter).isEditable = false
            column("t воздуха, °C", NDataGPT::tempAmb.getter)
            column("t ОИ, °C", NDataGPT::tempOI.getter)
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("Время, сек", NDataGPT::timeExp.getter)
            column("Результат", NDataGPT::result.getter)
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

data class NDataGPT(
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