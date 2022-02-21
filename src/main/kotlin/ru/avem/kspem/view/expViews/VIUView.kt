package ru.avem.kspem.view.expViews

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.utils.createScreenShot
import ru.avem.kspem.utils.showTwoWayDialog
import ru.avem.kspem.view.ExpView
import ru.avem.kspem.view.MainView
import tornadofx.*


class VIUView : View() {
    val name = "Испытание изоляции обмоток относительно корпуса и между обмотками  на электрическую прочность"
    val data = VIUData()

    override fun onDock() {
        super.onDock()
        runLater {
            clearTables()
        }
    }

    override val root = vbox(spacing = 16) {
        label(name)
        separator()
        padding = insets(8)
        hboxConstraints {
            hGrow = Priority.ALWAYS
        }
        label("Заданные значения") {
            alignment = Pos.TOP_CENTER
            textAlignment = TextAlignment.CENTER
            useMaxWidth = true
            isWrapText = true
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
                hGrow = Priority.ALWAYS
            }
            minHeight = 120.0
            maxHeight = 120.0
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            isMouseTransparent = true

            alignment = Pos.CENTER
            column("U, В", VIUData::setU.getter)
            column("I, мА", VIUData::setI.getter)
            column("T, с", VIUData::time.getter)
        }
        label("Измеренные значения") {
            alignment = Pos.TOP_CENTER
            textAlignment = TextAlignment.CENTER
            useMaxWidth = true
            isWrapText = true
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
                hGrow = Priority.ALWAYS
            }
            minHeight = 120.0
            maxHeight = 120.0
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            isMouseTransparent = true

            alignment = Pos.CENTER

            column("U, В", VIUData::U.getter)
            column("I, мА", VIUData::I.getter)
        }
        alignment = Pos.BOTTOM_CENTER
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            minWidth = 200.0 * 2
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            isMouseTransparent = true


            column("Время, с", VIUData::timeExp.getter)
            column("Результат", VIUData::result.getter)
        }
    }

    fun clearTables() {
        data.I.value = ""
        data.timeExp.value = ""
        data.time.value = ""
        data.result.value = ""
        data.U.value = ""
        data.setI.value = ""
        data.setU.value = ""
    }
}

data class VIUData(
    var setU: StringProperty = SimpleStringProperty(""),
    var setI: StringProperty = SimpleStringProperty(""),
    var U: StringProperty = SimpleStringProperty(""),
    var I: StringProperty = SimpleStringProperty(""),
//    val F: StringProperty = SimpleStringProperty(""),

    var time: StringProperty = SimpleStringProperty(""),
    var timeExp: StringProperty = SimpleStringProperty(""),
    var result: StringProperty = SimpleStringProperty("")
)
