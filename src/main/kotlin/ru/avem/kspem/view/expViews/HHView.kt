package ru.avem.kspem.view.expViews

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import tornadofx.*


class HHView : View() {
    val data = HHData()
    val name = "Определение частоты вращения двигателя постоянного тока на холостом ходу"

    override fun onDock() {
        super.onDock()
        clearTables()
    }

    override val root = vbox(16.0, Pos.CENTER) {
        padding = insets(8)
        label(name)
        separator()

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
            column("U Я, В", HHData::uAnc.getter).isEditable = false
            column("I Я, А", HHData::iAnc.getter).isEditable = false
            column("U ОВ, В", HHData::uOV.getter).isEditable = false
            column("I ОВ, А", HHData::iOV.getter).isEditable = false
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("t ОИ, °C", HHData::tempOI.getter)
            column("n, об/мин", HHData::n.getter).isEditable = false
            column("Время, сек", HHData::timeExp.getter)
            column("Результат", HHData::result.getter)
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
    }

    fun clearTables() {
        data.uAnc.value = ""
        data.uOV.value = ""
        data.iAnc.value = ""
        data.iOV.value = ""
        data.tempOI.value = ""
        data.timeExp.value = ""
        data.result.value = ""
        data.n.value = ""
    }
}
data class HHData(
    val tempOI: StringProperty = SimpleStringProperty(""),
    val n: StringProperty = SimpleStringProperty(""),
    val uAnc: StringProperty = SimpleStringProperty(""),
    val uOV: StringProperty = SimpleStringProperty(""),
    val iAnc: StringProperty = SimpleStringProperty(""),
    val iOV: StringProperty = SimpleStringProperty(""),
    val timeExp: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty("")
)