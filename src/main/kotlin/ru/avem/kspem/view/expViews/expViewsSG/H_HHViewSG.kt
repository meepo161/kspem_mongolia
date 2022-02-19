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


class H_HHViewSG : View() {
    val data = H_HHDataSG()
    val name = "Определение характеристики холостого хода и испытание межвитковой изоляции на электрическую прочность"

    val h_hhTablePoints = observableListOf(
        H_HHPointsSG(
            SimpleStringProperty("1.3"),
            SimpleStringProperty(),
            SimpleStringProperty(),
            SimpleStringProperty()
        ),
        H_HHPointsSG(
            SimpleStringProperty("1.2"),
            SimpleStringProperty(),
            SimpleStringProperty(),
            SimpleStringProperty()
        ),
        H_HHPointsSG(
            SimpleStringProperty("1.1"),
            SimpleStringProperty(),
            SimpleStringProperty(),
            SimpleStringProperty()
        ),
        H_HHPointsSG(
            SimpleStringProperty("1.0"),
            SimpleStringProperty(),
            SimpleStringProperty(),
            SimpleStringProperty()
        ),
        H_HHPointsSG(
            SimpleStringProperty("0.9"),
            SimpleStringProperty(),
            SimpleStringProperty(),
            SimpleStringProperty()
        ),
        H_HHPointsSG(
            SimpleStringProperty("0.8"),
            SimpleStringProperty(),
            SimpleStringProperty(),
            SimpleStringProperty()
        ),
        H_HHPointsSG(
            SimpleStringProperty("0.7"),
            SimpleStringProperty(),
            SimpleStringProperty(),
            SimpleStringProperty()
        ),
        H_HHPointsSG(
            SimpleStringProperty("0.6"),
            SimpleStringProperty(),
            SimpleStringProperty(),
            SimpleStringProperty()
        ),
        H_HHPointsSG(
            SimpleStringProperty("0.5"),
            SimpleStringProperty(),
            SimpleStringProperty(),
            SimpleStringProperty()
        )
    )

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
                column("U ОВ, В", H_HHDataSG::uOV.getter).isEditable = false
                column("I ОВ, А", H_HHDataSG::iOV.getter).isEditable = false
                column("f, Гц", H_HHDataSG::f.getter).isEditable = false
                column("P1, кВт", H_HHDataSG::p.getter).isEditable = false
                column("cosφ, о.е.", H_HHDataSG::cos.getter).isEditable = false
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }
            tableview(observableListOf(data)) {
                hboxConstraints {
                    useMaxWidth = true
                }
                minHeight = 120.0
                maxHeight = 120.0
                isMouseTransparent = true
                column("U AB, В", H_HHDataSG::uAB.getter).isEditable = false
                column("U BC, В", H_HHDataSG::uBC.getter).isEditable = false
                column("U CA, В", H_HHDataSG::uCA.getter).isEditable = false
                column("I A, А", H_HHDataSG::iA.getter).isEditable = false
                column("I B, А", H_HHDataSG::iB.getter).isEditable = false
                column("I C, А", H_HHDataSG::iC.getter).isEditable = false
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }
            tableview(observableListOf(data)) {
                hboxConstraints {
                    useMaxWidth = true
                }
                minHeight = 120.0
                maxHeight = 120.0
                isMouseTransparent = true
                column("n, об/мин", H_HHDataSG::n.getter).isEditable = false
                column("t воздуха, °C", H_HHDataSG::tempAmb.getter)
                column("t ОИ, °C", H_HHDataSG::tempOI.getter)
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }
            tableview(observableListOf(data)) {
                hboxConstraints {
                    useMaxWidth = true
                }
                minHeight = 120.0
                maxHeight = 120.0
                isMouseTransparent = true
                column("Время, сек", H_HHDataSG::timeExp.getter)
                column("Результат", H_HHDataSG::result.getter)
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
        data.uOV.value = ""
        data.iOV.value = ""
        data.n.value = ""
        data.p.value = ""
        data.tempAmb.value = ""
        data.tempOI.value = ""
        data.uAB.value = ""
        data.uBC.value = ""
        data.uCA.value = ""
        data.iA.value = ""
        data.iB.value = ""
        data.iC.value = ""
        data.f.value = ""
        data.timeExp.value = ""
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

data class H_HHDataSG(
    val tempOI: StringProperty = SimpleStringProperty(""),
    val tempAmb: StringProperty = SimpleStringProperty(""),
    val n: StringProperty = SimpleStringProperty(""),
    val uOV: StringProperty = SimpleStringProperty(""),
    val iOV: StringProperty = SimpleStringProperty(""),
    val uAB: StringProperty = SimpleStringProperty(""),
    val uBC: StringProperty = SimpleStringProperty(""),
    val uCA: StringProperty = SimpleStringProperty(""),
    val iA: StringProperty = SimpleStringProperty(""),
    val iB: StringProperty = SimpleStringProperty(""),
    val iC: StringProperty = SimpleStringProperty(""),
    val f: StringProperty = SimpleStringProperty(""),
    val timeExp: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty(""),
    val p: StringProperty = SimpleStringProperty(""),
    val cos: StringProperty = SimpleStringProperty("")
)

data class H_HHPointsSG(
    var point: StringProperty = SimpleStringProperty(""),
    var uAB: StringProperty = SimpleStringProperty(""),
    var uBC: StringProperty = SimpleStringProperty(""),
    var uCA: StringProperty = SimpleStringProperty(""),
    var iA: StringProperty = SimpleStringProperty(""),
    var iB: StringProperty = SimpleStringProperty(""),
    var iC: StringProperty = SimpleStringProperty(""),
    val uOV: StringProperty = SimpleStringProperty(""),
    val iOV: StringProperty = SimpleStringProperty(""),
    var power: StringProperty = SimpleStringProperty("")
)