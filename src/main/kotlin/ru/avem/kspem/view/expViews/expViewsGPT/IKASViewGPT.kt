package ru.avem.kspem.view.expViews.expViewsGPT

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import ru.avem.kspem.utils.createScreenShot
import ru.avem.kspem.utils.showTwoWayDialog
import ru.avem.kspem.view.ExpView
import ru.avem.kspem.view.MainView
import tornadofx.*


class IKASViewGPT : View() {
    val name = "Измерение сопротивления обмотки  постоянному току в практически холодном состоянии"
    val data = IKASDataGPT()

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
        label("Приведенные значения к 20°C")
            tableview(observableListOf(data)) {
                hboxConstraints {
                    useMaxWidth = true
                }
                minHeight = 120.0
                maxHeight = 120.0
                isMouseTransparent = true
                column("R ОВ, Ом", IKASDataGPT::R1.getter).isEditable = false
                column("R ОЯ, Ом", IKASDataGPT::R2.getter).isEditable = false
                column("t воздуха, °C", IKASDataGPT::tempAmb.getter).isEditable = false
                column("t ОИ, °C", IKASDataGPT::tempOI.getter).isEditable = false
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }/*
        label("Приведенные к 20°C")
            tableview(observableListOf(data)) {
                hboxConstraints {
                    useMaxWidth = true
                }
                minHeight = 120.0
                maxHeight = 120.0
                isMouseTransparent = true
                column("R ОВ, Ом", IKASDataMPT::calcR1.getter).isEditable = false
                column("R ОЯ, Ом", IKASDataMPT::calcR2.getter).isEditable = false
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }*/
            tableview(observableListOf(data)) {
                hboxConstraints {
                    useMaxWidth = true
                }
                minHeight = 120.0
                maxHeight = 120.0
                isMouseTransparent = true
                column("Результат", IKASDataGPT::result.getter).isEditable = false
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            }
    }
    fun clearTables() {
        data.R1.value = ""
        data.R2.value = ""
        data.result.value = ""
        data.tempOI.value = ""
        data.tempAmb.value = ""
    }
}

data class IKASDataGPT(
    val tempAmb: StringProperty = SimpleStringProperty(""),
    val tempOI: StringProperty = SimpleStringProperty(""),
    val R1: StringProperty = SimpleStringProperty(""),
    val R2: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty("")
)