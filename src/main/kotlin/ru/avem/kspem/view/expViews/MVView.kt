package ru.avem.kspem.view.expViews

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.communication.utils.schemeMessage
import tornadofx.*


class MVView : View() {
    val data = MVData()
    val name = "Испытание межвитковой изоляции обмоток на электрическую прочность"

    override fun onDock() {
        super.onDock()
        runLater {
            clearTables()
        }
    }

    override val root = vbox(spacing = 0) {
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
            column("U AB, В", MVData::uAB.getter)
            column("U BC, В", MVData::uBC.getter)
            column("U CA, В", MVData::uCA.getter)
            column("I A, А", MVData::iA.getter)
            column("I B, А", MVData::iB.getter)
            column("I C, А", MVData::iC.getter)
        }
        label("Значения «до»") {
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
            column("U AB, В", MVData::uAB1.getter)
            column("U BC, В", MVData::uBC1.getter)
            column("U CA, В", MVData::uCA1.getter)
            column("I A, А", MVData::iA1.getter)
            column("I B, А", MVData::iB1.getter)
            column("I C, А", MVData::iC1.getter)
        }
        label("Значения «после»") {
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
            column("U AB, В", MVData::uAB2.getter)
            column("U BC, В", MVData::uBC2.getter)
            column("U CA, В", MVData::uCA2.getter)
            column("I A, А", MVData::iA2.getter)
            column("I B, А", MVData::iB2.getter)
            column("I C, А", MVData::iC2.getter)
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

            column("Отклонение, %", MVData::deviation.getter)
            column("Время, с", MVData::timeExp.getter)
            column("Результат", MVData::result.getter)
        }
    }

    fun clearTables() {
        data.uAB.value = ""
        data.uBC.value = ""
        data.uCA.value = ""
        data.uAB1.value = ""
        data.uBC1.value = ""
        data.uCA1.value = ""
        data.uAB2.value = ""
        data.uBC2.value = ""
        data.uCA2.value = ""
        data.iA.value = ""
        data.iB.value = ""
        data.iC.value = ""
        data.iA1.value = ""
        data.iB1.value = ""
        data.iC1.value = ""
        data.iA2.value = ""
        data.iB2.value = ""
        data.iC2.value = ""
        data.deviation.value = ""
        data.timeExp.value = ""
        data.time.value = ""
        data.result.value = ""
    }
}

data class MVData(
    var uAB: StringProperty = SimpleStringProperty(""),
    var uBC: StringProperty = SimpleStringProperty(""),
    var uCA: StringProperty = SimpleStringProperty(""),
    var iA: StringProperty = SimpleStringProperty(""),
    var iB: StringProperty = SimpleStringProperty(""),
    var iC: StringProperty = SimpleStringProperty(""),
    var uAB1: StringProperty = SimpleStringProperty(""),
    var uBC1: StringProperty = SimpleStringProperty(""),
    var uCA1: StringProperty = SimpleStringProperty(""),
    var iA1: StringProperty = SimpleStringProperty(""),
    var iB1: StringProperty = SimpleStringProperty(""),
    var iC1: StringProperty = SimpleStringProperty(""),
    var uAB2: StringProperty = SimpleStringProperty(""),
    var uBC2: StringProperty = SimpleStringProperty(""),
    var uCA2: StringProperty = SimpleStringProperty(""),
    var iA2: StringProperty = SimpleStringProperty(""),
    var iB2: StringProperty = SimpleStringProperty(""),
    var iC2: StringProperty = SimpleStringProperty(""),
//    val F: StringProperty = SimpleStringProperty(""),

    var time: StringProperty = SimpleStringProperty(""),
    var timeExp: StringProperty = SimpleStringProperty(""),
    var deviation: StringProperty = SimpleStringProperty(""),
    var result: StringProperty = SimpleStringProperty("")
)
