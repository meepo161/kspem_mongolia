package ru.avem.kspem.view.expViews.expViewsMPT

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import ru.avem.kspem.communication.utils.toHHmmss
import ru.avem.kspem.data.loadMPT
import ru.avem.kspem.utils.Singleton
import ru.avem.kspem.utils.Toast
import tornadofx.*
import kotlin.time.ExperimentalTime


class LoadViewMPT : View() {
    val name = "Проверка коммутации при номинальной нагрузке и кратковременной перегрузки по току"
    val data = LoadDataMPT()

    var tfSparking1: TextField by singleAssign()
    var tfSparking2: TextField by singleAssign()
    var tfSparking3: TextField by singleAssign()
    var tfSparking4: TextField by singleAssign()

    override fun onDock() {
        super.onDock()
        runLater {
            clearTables()
        }
    }

    @OptIn(ExperimentalTime::class)
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
            column("U ОВ, В", LoadDataMPT::uOV.getter).isEditable = false
            column("I ОВ, А", LoadDataMPT::iOV.getter).isEditable = false
            column("U ОЯ, В", LoadDataMPT::uOY.getter).isEditable = false
            column("I ОЯ, А", LoadDataMPT::iOY.getter).isEditable = false
            column("P1, кВт", LoadDataMPT::p.getter).isEditable = false
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("n, об/мин", LoadDataMPT::n.getter).isEditable = false
            column("t воздуха, °C", LoadDataMPT::tempAmb.getter)
            column("t ОИ, °C", LoadDataMPT::tempOI.getter)
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("Время, сек", LoadDataMPT::timeExp.getter)
            column("Результат", LoadDataMPT::result.getter)
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        hbox(16.0, Pos.CENTER) {
            vbox(4.0, Pos.CENTER) {
                label("1 узел")
                tfSparking1 = textfield {
                    alignment = Pos.CENTER
                }
            }
            vbox(4.0, Pos.CENTER) {
                label("2 узел")
                tfSparking2 = textfield {
                    alignment = Pos.CENTER
                }
            }
            vbox(4.0, Pos.CENTER) {
                label("3 узел")
                tfSparking3 = textfield {
                    alignment = Pos.CENTER
                }
            }
            vbox(4.0, Pos.CENTER) {
                label("4 узел")
                tfSparking4 = textfield {
                    alignment = Pos.CENTER
                }
            }
            vbox(4.0, Pos.CENTER) {
                label("")
                button("Сохранить") {
                    action {
                        try {
                            if (loadMPT.loadNomStarted) {
                                Singleton.sparkingTime.add(toHHmmss(((loadMPT.timerLoadNom * 1000) - (data.timeExp.value.toDouble() * 1000)).toLong()))
                            } else {
                                Singleton.sparkingTime.add(toHHmmss(((loadMPT.timerLoad * 1000) - (data.timeExp.value.toDouble() * 1000)).toLong()))
                            }
                            Singleton.sparking1.add(tfSparking1.text.replace(",", "."))
                            Singleton.sparking2.add(tfSparking2.text.replace(",", "."))
                            Singleton.sparking3.add(tfSparking3.text.replace(",", "."))
                            Singleton.sparking4.add(tfSparking4.text.replace(",", "."))
                            runLater {
                                Toast.makeText("Точка сохранена").show(Toast.ToastType.INFORMATION)
                            }
                        } catch (e: Exception) {
                            runLater {
                                Toast.makeText("Ошибка сохранения точки").show(Toast.ToastType.ERROR)
                            }
                        }
                    }
                }
            }
        }
    }

    fun clearTables() {
        data.result.value = ""
        data.uOV.value = ""
        data.iOV.value = ""
        data.uOY.value = ""
        data.iOY.value = ""
        data.n.value = ""
        data.p.value = ""
        data.tempAmb.value = ""
        data.tempOI.value = ""
        data.timeExp.value = ""
    }

}

data class LoadDataMPT(
    val tempOI: StringProperty = SimpleStringProperty(""),
    val tempAmb: StringProperty = SimpleStringProperty(""),
    val n: StringProperty = SimpleStringProperty(""),
    val uOV: StringProperty = SimpleStringProperty(""),
    val iOV: StringProperty = SimpleStringProperty(""),
    val uOY: StringProperty = SimpleStringProperty(""),
    val iOY: StringProperty = SimpleStringProperty(""),
    val timeExp: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty(""),
    val p: StringProperty = SimpleStringProperty("")
)