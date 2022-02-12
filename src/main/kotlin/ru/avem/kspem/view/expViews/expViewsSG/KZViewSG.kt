package ru.avem.kspem.view.expViews.expViewsSG

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Pos
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import tornadofx.*


class KZViewSG : View() {
    val name = "Определение тока и потерь КЗ"
    val data = KZDataSG()

    var lineChart: LineChart<Number, Number> by singleAssign()
    var series = XYChart.Series<Number, Number>()

    override fun onDock() {
        super.onDock()
        runLater {
            clearTables()
            series.data.clear()
//            var step = 1.4
//            series.data.add(XYChart.Data(0.0, 0.0))
//            while (380 * step > 1) {
//                step -= 0.1
//                series.data.add(XYChart.Data(step * step, 380 * step))
//            }
        }
    }

    override val root = scrollpane {
        vbox(16.0, Pos.CENTER) {
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
                column("U ОВ, В", KZDataSG::uOV.getter).isEditable = false
                column("I ОВ, А", KZDataSG::iOV.getter).isEditable = false
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }
            tableview(observableListOf(data)) {
                hboxConstraints {
                    useMaxWidth = true
                }
                minHeight = 120.0
                maxHeight = 120.0
                isMouseTransparent = true
                column("Uab, В", KZDataSG::uAB.getter).isEditable = false
                column("Ubc, В", KZDataSG::uBC.getter).isEditable = false
                column("Uca, В", KZDataSG::uCA.getter).isEditable = false
                column("Ia, А", KZDataSG::iA.getter).isEditable = false
                column("Ib, А", KZDataSG::iB.getter).isEditable = false
                column("Ic, А", KZDataSG::iC.getter).isEditable = false
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }
            tableview(observableListOf(data)) {
                hboxConstraints {
                    useMaxWidth = true
                }
                minHeight = 120.0
                maxHeight = 120.0
                isMouseTransparent = true
                column("n, об/мин", KZDataSG::n.getter).isEditable = false
                column("t воздуха, °C", KZDataSG::tempAmb.getter)
                column("t ОИ, °C", KZDataSG::tempOI.getter)
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }
            tableview(observableListOf(data)) {
                hboxConstraints {
                    useMaxWidth = true
                }
                minHeight = 120.0
                maxHeight = 120.0
                isMouseTransparent = true
                column("Время, сек", KZDataSG::timeExp.getter)
                column("Результат", KZDataSG::result.getter)
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }
            lineChart = linechart("", NumberAxis(), NumberAxis()) {
                prefHeight = 460.0
                prefWidth = 1860.0
                data.add(series)
                animated = false
//                createSymbols = false
                isLegendVisible = false
            }
        }
    }

    fun clearTables() {
        data.result.value = ""
        data.cos.value = ""
        data.f.value = ""
        data.iA.value = ""
        data.iB.value = ""
        data.iC.value = ""
        data.n.value = ""
        data.p.value = ""
        data.tempAmb.value = ""
        data.tempOI.value = ""
        data.uAB.value = ""
        data.uCA.value = ""
        data.uBC.value = ""
        data.uOV.value = ""
        data.iOV.value = ""
        data.timeExp.value = ""
    }
}

data class KZDataSG(
    val tempOI: StringProperty = SimpleStringProperty(""),
    val tempAmb: StringProperty = SimpleStringProperty(""),
    val uOV: StringProperty = SimpleStringProperty(""),
    val iOV: StringProperty = SimpleStringProperty(""),
    val n: StringProperty = SimpleStringProperty(""),
    val uAB: StringProperty = SimpleStringProperty(""),
    val uBC: StringProperty = SimpleStringProperty(""),
    val uCA: StringProperty = SimpleStringProperty(""),
    val timeExp: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty(""),
    val iA: StringProperty = SimpleStringProperty(""),
    val iB: StringProperty = SimpleStringProperty(""),
    val iC: StringProperty = SimpleStringProperty(""),
    val f: StringProperty = SimpleStringProperty(""),
    val p: StringProperty = SimpleStringProperty(""),
    val cos: StringProperty = SimpleStringProperty("")
)