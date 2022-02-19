package ru.avem.kspem.view.expViews.expViewsSD

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import tornadofx.*


class NViewSD : View() {
    val name = "Определение частоты вращения синхронного двигателя на холостом ходу"
    val data = NDataSD()

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
            column("U ОВ, В", NDataSD::uOV.getter).isEditable = false
            column("I ОВ, А", NDataSD::iOV.getter).isEditable = false
            column("U ОЯ, В", NDataSD::uAB.getter).isEditable = false
            column("I ОЯ, А", NDataSD::iA.getter).isEditable = false
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("n, об/мин", NDataSD::n.getter).isEditable = false
            column("t воздуха, °C", NDataSD::tempAmb.getter)
            column("t ОИ, °C", NDataSD::tempOI.getter)
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("Время, сек", NDataSD::timeExp.getter)
            column("Результат", NDataSD::result.getter)
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
        data.iA.value = ""
        data.f.value = ""
        data.timeExp.value = ""
    }

}
data class NDataSD(
    val tempOI: StringProperty = SimpleStringProperty(""),
    val tempAmb: StringProperty = SimpleStringProperty(""),
    val n: StringProperty = SimpleStringProperty(""),
    val uOV: StringProperty = SimpleStringProperty(""),
    val iOV: StringProperty = SimpleStringProperty(""),
    val uAB: StringProperty = SimpleStringProperty(""),
    val iA: StringProperty = SimpleStringProperty(""),
    val f: StringProperty = SimpleStringProperty(""),
    val timeExp: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty(""),
    val p: StringProperty = SimpleStringProperty(""),
    val cos: StringProperty = SimpleStringProperty("")
)