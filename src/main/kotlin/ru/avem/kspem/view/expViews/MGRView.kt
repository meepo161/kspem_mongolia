package ru.avem.kspem.view.expViews

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import ru.avem.kspem.utils.showTwoWayDialog
import ru.avem.kspem.view.ExpView
import ru.avem.kspem.view.MainView
import tornadofx.*


class MGRView : View() {
    val name = "Измерение сопротивления изоляции обмоток относительно корпуса и между обмотками"
    val data = MGRData()

    override fun onDock() {
        super.onDock()
        runLater {
            clearTables()
        }
        showTwoWayDialog(
            title = "Внимание!",
            text = "Подключить ТОЛЬКО Высоковольтный провод с зажимом типа «крокодил» (XA1) к Испытуемой обмотке/выводу ОИ"+
                    "\nПровод измерительный (ХА2) к корпусу и/или частям, относительно которых будет проходить проверка." +
            "\nСиловые провода НЕ ДОЛЖНЫ быть подключены к ОИ",
            way1Title = "Подтвердить",
            way2Title = "Отменить",
            way1 = {

            },
            way2 = {
                find<ExpView>().replaceWith<MainView>()
            },
            currentWindow = primaryStage.scene.window
        )
    }

    override val root = vbox(16.0,Pos.CENTER) {
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

            column("U, В", MGRData::U.getter)
            column("R(за 15 с.),МОм", MGRData::R15.getter)
            column("R(за 60 с.),МОм", MGRData::R60.getter)
            column("t воздуха,°C", MGRData::tempAmb.getter)
            column("t ОИ,°C", MGRData::tempOI.getter)
        }
//        label("Приведенные к 20°C") {
//            alignment = Pos.TOP_CENTER
//            textAlignment = TextAlignment.CENTER
//            useMaxWidth = true
//            isWrapText = true
//        }
//        separator()
//        tableview(observableListOf(data)) {
//            column("R(за 15 с.),МОм", MGRData::calcR15.getter)
//            column("R(за 60 с.),МОм", MGRData::calcR60.getter)
//            column("kABS,о.е.", MGRData::K_ABS.getter)
//            minHeight = 120.0
//            maxHeight = 120.0
//            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
//            isMouseTransparent = true
//        }
        tableview(observableListOf(data)) {
            minHeight = 120.0
            maxHeight = 120.0
            minWidth = 200.0
            prefWidth = 200.0
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            isMouseTransparent = true

            column("Время, с.", MGRData::time.getter)
            column("kABS, о.е.", MGRData::K_ABS.getter)
            column("Результат", MGRData::result.getter)
        }
    }
    fun clearTables() {
        runLater {
            data.tempAmb.value = ""
            data.R60.value = ""
            data.R15.value = ""
            data.tempOI.value = ""
            data.result.value = ""
            data.U.value = ""
            data.K_ABS.value = ""
        }
    }
}


data class MGRData(
    val U: StringProperty = SimpleStringProperty(""),
    val R15: StringProperty = SimpleStringProperty(""),
    val R60: StringProperty = SimpleStringProperty(""),
    val tempAmb: StringProperty = SimpleStringProperty(""),
    val tempOI: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty(""),
    val time: StringProperty = SimpleStringProperty(""),
//    val calcR15: StringProperty = SimpleStringProperty(""),
//    val calcR60: StringProperty = SimpleStringProperty(""),
    val K_ABS: StringProperty = SimpleStringProperty("")
)
