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


class H_HHView : View() {
    val data = H_HHData()
    var table: TableView<H_HHPoints> by singleAssign()

    val h_hhTablePoints = observableListOf(
        H_HHPoints(SimpleStringProperty("1.3"),SimpleStringProperty(), SimpleStringProperty(), SimpleStringProperty()),
        H_HHPoints(SimpleStringProperty("1.2"),SimpleStringProperty(), SimpleStringProperty(), SimpleStringProperty()),
        H_HHPoints(SimpleStringProperty("1.1"),SimpleStringProperty(), SimpleStringProperty(), SimpleStringProperty()),
        H_HHPoints(SimpleStringProperty("1.0"),SimpleStringProperty(), SimpleStringProperty(), SimpleStringProperty()),
        H_HHPoints(SimpleStringProperty("0.9"),SimpleStringProperty(), SimpleStringProperty(), SimpleStringProperty()),
        H_HHPoints(SimpleStringProperty("0.8"),SimpleStringProperty(), SimpleStringProperty(), SimpleStringProperty()),
        H_HHPoints(SimpleStringProperty("0.7"),SimpleStringProperty(), SimpleStringProperty(), SimpleStringProperty()),
        H_HHPoints(SimpleStringProperty("0.6"),SimpleStringProperty(), SimpleStringProperty(), SimpleStringProperty()),
        H_HHPoints(SimpleStringProperty("0.5"),SimpleStringProperty(), SimpleStringProperty(), SimpleStringProperty())
    )

    val name = "Определение характеристики холостого хода для асинхронных машин"


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
            column("U AB, В", H_HHData::uAB.getter)
            column("U BC, В", H_HHData::uBC.getter)
            column("U CA, В", H_HHData::uCA.getter)
            column("I A, А", H_HHData::iA.getter)
            column("I B, А", H_HHData::iB.getter)
            column("I C, А", H_HHData::iC.getter)
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

            column("Скорость, об/мин", H_HHData::n.getter)
            column("Мощность, кВт", H_HHData::power.getter)
//            column("Время, с", H_HHData::timeExp.getter)
            column("Результат", H_HHData::result.getter)
        }
        label("Зафиксированные значения") {
            alignment = Pos.TOP_CENTER
            textAlignment = TextAlignment.CENTER
            useMaxWidth = true
            isWrapText = true
        }
        table = tableview(h_hhTablePoints) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 230.0
            maxHeight = 230.0
            minWidth = 180.0 * 2
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY

            column("k*Uн, о.е.", H_HHPoints::point.getter)
            column("U AB, В", H_HHPoints::uAB.getter)
            column("U BC, В", H_HHPoints::uBC.getter)
            column("U CA, В", H_HHPoints::uCA.getter)
            column("I A, А", H_HHPoints::iA.getter)
            column("I B, А", H_HHPoints::iB.getter)
            column("I C, А", H_HHPoints::iC.getter)
            column("Мощность, кВт", H_HHPoints::power.getter)
        }
    }

    fun clearTables() {
        data.uAB.value = ""
        data.uBC.value = ""
        data.iA.value = ""
        data.iB.value = ""
        data.iC.value = ""
        data.uCA.value = ""
        data.n.value = ""
        data.power.value = ""
        data.timeExp.value = ""
        data.result.value = ""
        h_hhTablePoints.forEach {
            it.uAB.value = ""
            it.uBC.value = ""
            it.uCA.value = ""
            it.iA.value = ""
            it.iB.value = ""
            it.iC.value = ""
            it.power.value = ""
        }
    }
}

data class H_HHData(
    var uAB: StringProperty = SimpleStringProperty(""),
    var uBC: StringProperty = SimpleStringProperty(""),
    var uCA: StringProperty = SimpleStringProperty(""),
    var iA: StringProperty = SimpleStringProperty(""),
    var iB: StringProperty = SimpleStringProperty(""),
    var iC: StringProperty = SimpleStringProperty(""),
    var n:StringProperty = SimpleStringProperty(""),
//    val F: StringProperty = SimpleStringProperty(""),

    var timeExp: StringProperty = SimpleStringProperty(""),
    var power: StringProperty = SimpleStringProperty(""),
    var result: StringProperty = SimpleStringProperty("")

//    var uAB1: StringProperty = SimpleStringProperty(""),
//    var uBC1: StringProperty = SimpleStringProperty(""),
//    var uCA1: StringProperty = SimpleStringProperty(""),
//    var iA1: StringProperty = SimpleStringProperty(""),
//    var iB1: StringProperty = SimpleStringProperty(""),
//    var iC1: StringProperty = SimpleStringProperty(""),
//
//    var uAB2: StringProperty = SimpleStringProperty(""),
//    var uBC2: StringProperty = SimpleStringProperty(""),
//    var uCA2: StringProperty = SimpleStringProperty(""),
//    var iA2: StringProperty = SimpleStringProperty(""),
//    var iB2: StringProperty = SimpleStringProperty(""),
//    var iC2: StringProperty = SimpleStringProperty(""),
//
//    var uAB3: StringProperty = SimpleStringProperty(""),
//    var uBC3: StringProperty = SimpleStringProperty(""),
//    var uCA3: StringProperty = SimpleStringProperty(""),
//    var iA3: StringProperty = SimpleStringProperty(""),
//    var iB3: StringProperty = SimpleStringProperty(""),
//    var iC3: StringProperty = SimpleStringProperty(""),
//
//    var uAB4: StringProperty = SimpleStringProperty(""),
//    var uBC4: StringProperty = SimpleStringProperty(""),
//    var uCA4: StringProperty = SimpleStringProperty(""),
//    var iA4: StringProperty = SimpleStringProperty(""),
//    var iB4: StringProperty = SimpleStringProperty(""),
//    var iC4: StringProperty = SimpleStringProperty(""),
//
//    var uAB5: StringProperty = SimpleStringProperty(""),
//    var uBC5: StringProperty = SimpleStringProperty(""),
//    var uCA5: StringProperty = SimpleStringProperty(""),
//    var iA5: StringProperty = SimpleStringProperty(""),
//    var iB5: StringProperty = SimpleStringProperty(""),
//    var iC5: StringProperty = SimpleStringProperty(""),
//
//    var uAB6: StringProperty = SimpleStringProperty(""),
//    var uBC6: StringProperty = SimpleStringProperty(""),
//    var uCA6: StringProperty = SimpleStringProperty(""),
//    var iA6: StringProperty = SimpleStringProperty(""),
//    var iB6: StringProperty = SimpleStringProperty(""),
//    var iC6: StringProperty = SimpleStringProperty(""),
//
//    var uAB7: StringProperty = SimpleStringProperty(""),
//    var uBC7: StringProperty = SimpleStringProperty(""),
//    var uCA7: StringProperty = SimpleStringProperty(""),
//    var iA7: StringProperty = SimpleStringProperty(""),
//    var iB7: StringProperty = SimpleStringProperty(""),
//    var iC7: StringProperty = SimpleStringProperty(""),
//
//    var uAB8: StringProperty = SimpleStringProperty(""),
//    var uBC8: StringProperty = SimpleStringProperty(""),
//    var uCA8: StringProperty = SimpleStringProperty(""),
//    var iA8: StringProperty = SimpleStringProperty(""),
//    var iB8: StringProperty = SimpleStringProperty(""),
//    var iC8: StringProperty = SimpleStringProperty(""),
//
//    var uAB9: StringProperty = SimpleStringProperty(""),
//    var uBC9: StringProperty = SimpleStringProperty(""),
//    var uCA9: StringProperty = SimpleStringProperty(""),
//    var iA9: StringProperty = SimpleStringProperty(""),
//    var iB9: StringProperty = SimpleStringProperty(""),
//    var iC9: StringProperty = SimpleStringProperty("")
)

data class H_HHPoints(
    var point: StringProperty = SimpleStringProperty(""),
    var uAB: StringProperty = SimpleStringProperty(""),
    var uBC: StringProperty = SimpleStringProperty(""),
    var uCA: StringProperty = SimpleStringProperty(""),
    var iA: StringProperty = SimpleStringProperty(""),
    var iB: StringProperty = SimpleStringProperty(""),
    var iC: StringProperty = SimpleStringProperty(""),
    var power: StringProperty = SimpleStringProperty("")
)