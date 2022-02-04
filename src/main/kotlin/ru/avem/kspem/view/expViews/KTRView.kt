package ru.avem.kspem.view.expViews

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import ru.avem.kspem.utils.showTwoWayDialog
import ru.avem.kspem.view.ExpView
import ru.avem.kspem.view.MainView
import tornadofx.*


class KTRView : View() {
    val data = KTRData()
    val name = "Определение коэффициента трансформации(для машин с фазным ротором)"

    override fun onDock() {
        super.onDock()
        clearTables()
        showTwoWayDialog(
            title = "Внимание!",
            text = "Максимально допустимое напряжение 690В!\n" +
                    "Подключите ОИ к трансформатору 1500В!\n" +
                    "Подключите измерительные крокодилы к роторной обмотке!",
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

    override val root = vbox(16.0, Pos.CENTER) {
        padding = insets(8)
        label(name)
        separator()

        hboxConstraints {
            hGrow = Priority.ALWAYS
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("Uab, В", KTRData::uAB.getter).isEditable = false
            column("Ubc, В", KTRData::uBC.getter).isEditable = false
            column("Uca, В", KTRData::uCA.getter).isEditable = false
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("U статор сред., В", KTRData::uAvg1.getter).isEditable = false
            column("U ротор сред., В", KTRData::uAvg2.getter).isEditable = false
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
        tableview(observableListOf(data)) {
            hboxConstraints {
                useMaxWidth = true
            }
            minHeight = 120.0
            maxHeight = 120.0
            isMouseTransparent = true
            column("Ктр, о.е.", KTRData::kTR.getter)
            column("Результат", KTRData::result.getter)
            columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
        }
    }
    fun clearTables() {
        data.uAB.value = ""
        data.uBC.value = ""
        data.uCA.value = ""
        data.uAvg1.value = ""
        data.uAvg2.value = ""
        data.kTR.value = ""
        data.result.value = ""
    }
}
data class KTRData(
    val uAB: StringProperty = SimpleStringProperty(""),
    val uBC: StringProperty = SimpleStringProperty(""),
    val uCA: StringProperty = SimpleStringProperty(""),
    val uAvg1: StringProperty = SimpleStringProperty(""),
    val uAvg2: StringProperty = SimpleStringProperty(""),
    val kTR: StringProperty = SimpleStringProperty(""),
    val result: StringProperty = SimpleStringProperty("")
)