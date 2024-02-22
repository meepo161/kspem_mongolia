package ru.avem.kspem.view.expViews.expViewsGPT

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import ru.avem.kspem.utils.createScreenShot
import ru.avem.kspem.utils.showTwoWayDialog
import ru.avem.kspem.view.ExpView
import ru.avem.kspem.view.MainView
import tornadofx.*


class MGRViewGPT : View() {
    val name = "Измерение сопротивления изоляции обмоток относительно корпуса и между обмотками"
    val data = MGRDataGPT()

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

        label("Измеренные значения") {
            alignment = Pos.TOP_CENTER
            textAlignment = TextAlignment.CENTER
            useMaxWidth = true
            isWrapText = true
        }
        separator()
        tableview(observableListOf(data)) {
            minHeight = 120.0
            maxHeight = 120.0
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            isMouseTransparent = true

            column("", MGRDataGPT::regardingOV.getter)
            column("U, В", MGRDataGPT::UOV.getter)
            column("R(за 15 с.),МОм", MGRDataGPT::R15OV.getter)
            column("R(за 60 с.),МОм", MGRDataGPT::R60OV.getter)
            column("kABS, о.е.", MGRDataGPT::K_ABSOV.getter)
            column("Результат", MGRDataGPT::resultOV.getter)
        }
        tableview(observableListOf(data)) {
            minHeight = 120.0
            maxHeight = 120.0
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            isMouseTransparent = true

            column("", MGRDataGPT::regardingOY.getter)
            column("U, В", MGRDataGPT::UOY.getter)
            column("R(за 15 с.),МОм", MGRDataGPT::R15OY.getter)
            column("R(за 60 с.),МОм", MGRDataGPT::R60OY.getter)
            column("kABS, о.е.", MGRDataGPT::K_ABSOY.getter)
            column("Результат", MGRDataGPT::resultOY.getter)
        }
        tableview(observableListOf(data)) {
            minHeight = 120.0
            maxHeight = 120.0
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            isMouseTransparent = true

            column("", MGRDataGPT::regardingOVOY.getter)
            column("U, В", MGRDataGPT::UOVOY.getter)
            column("R(за 15 с.),МОм", MGRDataGPT::R15OVOY.getter)
            column("R(за 60 с.),МОм", MGRDataGPT::R60OVOY.getter)
            column("kABS, о.е.", MGRDataGPT::K_ABSOVOY.getter)
            column("Результат", MGRDataGPT::resultOVOY.getter)
        }
        tableview(observableListOf(data)) {
            minHeight = 120.0
            maxHeight = 120.0
            minWidth = 200.0
            prefWidth = 200.0
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            isMouseTransparent = true

            column("Время, с.", MGRDataGPT::time.getter)
            column("t воздуха,°C", MGRDataGPT::tempAmb.getter)
            column("t ОИ,°C", MGRDataGPT::tempOI.getter)
        }
    }

    fun clearTables() {
        runLater {
            data.tempAmb.value = ""
            data.tempOI.value = ""
            data.time.value = ""

            data.regardingOV.value = "ОВ отн К."
            data.UOV.value = ""
            data.R60OV.value = ""
            data.R15OV.value = ""
            data.K_ABSOV.value = ""
            data.resultOV.value = ""

            data.regardingOY.value = "ОЯ отн К."
            data.UOY.value = ""
            data.R60OY.value = ""
            data.R15OY.value = ""
            data.K_ABSOY.value = ""
            data.resultOY.value = ""

            data.regardingOVOY.value = "ОВ отн ОЯ"
            data.UOVOY.value = ""
            data.R60OVOY.value = ""
            data.R15OVOY.value = ""
            data.K_ABSOVOY.value = ""
            data.resultOVOY.value = ""


        }
    }
}


data class MGRDataGPT(
    val tempAmb: StringProperty = SimpleStringProperty(""),
    val tempOI: StringProperty = SimpleStringProperty(""),
    val time: StringProperty = SimpleStringProperty(""),

    val regardingOV: StringProperty = SimpleStringProperty(""),
    val UOV: StringProperty = SimpleStringProperty(""),
    val R15OV: StringProperty = SimpleStringProperty(""),
    val R60OV: StringProperty = SimpleStringProperty(""),
    val K_ABSOV: StringProperty = SimpleStringProperty(""),
    val resultOV: StringProperty = SimpleStringProperty(""),

    val regardingOY: StringProperty = SimpleStringProperty(""),
    val UOY: StringProperty = SimpleStringProperty(""),
    val R15OY: StringProperty = SimpleStringProperty(""),
    val R60OY: StringProperty = SimpleStringProperty(""),
    val K_ABSOY: StringProperty = SimpleStringProperty(""),
    val resultOY: StringProperty = SimpleStringProperty(""),

    val regardingOVOY: StringProperty = SimpleStringProperty(""),
    val UOVOY: StringProperty = SimpleStringProperty(""),
    val R15OVOY: StringProperty = SimpleStringProperty(""),
    val R60OVOY: StringProperty = SimpleStringProperty(""),
    val K_ABSOVOY: StringProperty = SimpleStringProperty(""),
    val resultOVOY: StringProperty = SimpleStringProperty("")
)
