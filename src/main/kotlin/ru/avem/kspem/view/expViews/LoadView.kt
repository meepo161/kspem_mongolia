package ru.avem.kspem.view.expViews

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import tornadofx.*


class LoadView : View() {
    val name = "Нагрузка"
    val data = RUNNINGData()

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
            column("Uab, В", RUNNINGData::uab.getter).isEditable = false
            column("Ubc, В", RUNNINGData::ubc.getter).isEditable = false
            column("Uca, В", RUNNINGData::uca.getter).isEditable = false
            column("Ia, А", RUNNINGData::ia.getter).isEditable = false
            column("Ib, А", RUNNINGData::ib.getter).isEditable = false
            column("Ic, А", RUNNINGData::ic.getter).isEditable = false
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("f, Гц", RUNNINGData::f.getter).isEditable = false
            column("P1, кВт", RUNNINGData::p.getter).isEditable = false
            column("cosφ, о.е.", RUNNINGData::cos.getter).isEditable = false
            column("n, об/мин", RUNNINGData::n.getter).isEditable = false
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("Вибр. полевая, мм/с", RUNNINGData::vibroPol.getter)
            column("Вибр.рабочая, мм/с", RUNNINGData::vibroRab.getter)
            column("t воздуха, °C", RUNNINGData::tempAmb.getter)
            column("t ОИ, °C", RUNNINGData::tempOI.getter)
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("Время, сек", RUNNINGData::timeExp.getter)
            column("Результат", RUNNINGData::result.getter)
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
    }
    fun clearTables() {
        data.vibroRab.value = ""
        data.vibroPol.value = ""
        data.result.value = ""
        data.cos.value = ""
        data.f.value = ""
        data.ia.value = ""
        data.ib.value = ""
        data.ic.value = ""
        data.n.value = ""
        data.p.value = ""
        data.tempAmb.value = ""
        data.tempOI.value = ""
        data.uab.value = ""
        data.uca.value = ""
        data.ubc.value = ""
        data.timeExp.value = ""
    }
}
data class RUNNINGData(
    val tempOI: StringProperty = SimpleStringProperty(""),
    val tempAmb: StringProperty = SimpleStringProperty(""),
    val vibroPol: StringProperty = SimpleStringProperty(""),
    val vibroRab: StringProperty = SimpleStringProperty(""),
    val n: StringProperty = SimpleStringProperty(""),
    val uab: StringProperty = SimpleStringProperty(""),
    val ubc: StringProperty = SimpleStringProperty(""),
    val uca: StringProperty = SimpleStringProperty(""),
    val timeExp: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty(""),
    val ia: StringProperty = SimpleStringProperty(""),
    val ib: StringProperty = SimpleStringProperty(""),
    val ic: StringProperty = SimpleStringProperty(""),
    val f: StringProperty = SimpleStringProperty(""),
    val p: StringProperty = SimpleStringProperty(""),
    val cos: StringProperty = SimpleStringProperty("")
)