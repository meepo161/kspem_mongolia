package ru.avem.kspem.view

import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.kspem.data.motorType
import ru.avem.kspem.data.schemeType
import ru.avem.kspem.database.entities.Objects
import ru.avem.kspem.database.entities.TestObjects
import ru.avem.kspem.utils.Singleton
import ru.avem.kspem.utils.createScreenShot
import ru.avem.kspem.utils.sleep
import tornadofx.*
import tornadofx.controlsfx.errorNotification
import tornadofx.controlsfx.infoNotification
import kotlin.concurrent.thread

class ObjectEditorWindow : View("Редактор объектов испытания") {

    val view: MainView by inject()
//    var toInsert: VBox by singleAssign()

    var tfp2: TextField by singleAssign()
    var tfuN: TextField by singleAssign()
    var tfiN: TextField by singleAssign()
    var tfnAsync: TextField by singleAssign()
    var tfkpd: TextField by singleAssign()
    var tfscheme: ComboBox<String> by singleAssign()
    var tfuVIU: TextField by singleAssign()
    var tfuMGR: TextField by singleAssign()
    var tftimeVIU: TextField by singleAssign()
    var tftimeHH: TextField by singleAssign()
    var tftimeMVZ: TextField by singleAssign()
    var tftimeRUNNING: TextField by singleAssign()
    var tfiOV: TextField by singleAssign()
    var tfuOV: TextField by singleAssign()
//    var tfiVIU:      TextField by singleAssign()
//    var tfiMVZ:      TextField by singleAssign()
    //    var tfrMGRmax:   TextField by singleAssign()
//    var tfrMGRmin:   TextField by singleAssign()
//    var tfrPhaseMax: TextField by singleAssign()
//    var tfrPhaseMin: TextField by singleAssign()

    var obj: VBox by singleAssign()
    var cbObjects: ComboBox<TestObjects> by singleAssign()
    var cbObjectType: ComboBox<String> by singleAssign()
    var tfObjectName: TextField by singleAssign()
    val validator = ValidationContext()
    var newCheck = false
    var toInsert: VBox by singleAssign()


    override fun onDock() {
        root.style {
            baseColor = Color.hsb(Singleton.color1, Singleton.color2, Singleton.color3)
        }
        getObjects()
        super.onDock()
    }

    override fun onUndock() {
        view.getObjectItems()
        super.onUndock()
    }


    override val root = anchorpane {
        hbox(16.0, Pos.CENTER_LEFT) {
            anchorpaneConstraints {
                leftAnchor = 16.0
                rightAnchor = 16.0
                topAnchor = 16.0
            }
            vbox(16.0, Pos.BASELINE_LEFT) {
                minWidth = 700.0
                maxWidth = 700.0
                paddingTop = 300.0
                paddingLeft = 300.0
                checkbox("Создать новый") {
                    onAction = EventHandler {
                        if (isSelected) {
                            newCheck = true
                            cbObjects.isVisible = false
                            cbObjectType.isDisable = false
                            tfObjectName.show()
                        } else {
                            cbObjects.isVisible = true
                            newCheck = false
                            tfObjectName.text = ""
                            cbObjectType.isDisable = true
                            tfObjectName.hide()
                        }
                    }
                }
                label("Шифр машины")
                tfObjectName = textfield {
                    validator.addValidator(this) {
                        if (it == null && newCheck) {
                            error("Обязательное поле")
                        } else null
                    }
                    hide()
                }
                cbObjects = combobox() {
                    useMaxWidth = true
                    onAction = EventHandler {
                        if (cbObjects.selectionModel.selectedItem != null) {
                            cbObjectType.selectionModel.select(this.selectionModel.selectedItem.type)
                            getObjectData()
                        }
                    }
                }
                label()
                separator()
                label("Тип машины")
                cbObjectType = combobox() {
                    items = observableListOf(motorType.sd, motorType.sg, motorType.dpt, motorType.gpt)
                    useMaxWidth = true
                    isDisable = true
                    onAction = EventHandler {
                        if (cbObjectType.selectionModel.selectedItem == motorType.gpt || cbObjectType.selectionModel.selectedItem == motorType.dpt) {
                            toInsert.hide()
                        } else {
                            toInsert.show()
                        }
                    }
                }
                label()
                separator()
                hbox(16.0, Pos.CENTER) {
                    useMaxWidth = true
                    button("Сохранить") {
                        useMaxWidth = true
                        action {
                            saveItem()
                        }
                    }
                    button("Удалить") {
                        useMaxWidth = true
                        action {
                            removeItem()
                        }
                    }
                }
                separator()
                label()
                hbox(16.0, Pos.CENTER) {
                    button("Выход") {
                        action {
                            replaceWith(find<MainView>())
                        }
                    }
                }
            }
            label()
            separator(Orientation.VERTICAL)
            label()
            obj = vbox(16.0, Pos.CENTER) {
                paddingTop = 50.0
                hbox(16.0, Pos.CENTER) {
                    label("Мощность, кВт") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfp2 = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 0 || (it.toDouble()) > 1000) {
                                error("Значение не в диапазоне 0 — 1000")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Напряжение номинальное, В") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfuN = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 0 || (it.toDouble()) > 750) {
                                error("Значение не в диапазоне 0 — 750")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Ток номинальный, А") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfiN = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 0 || (it.toDouble()) > 500) {
                                error("Значение не в диапазоне 0 — 500")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Напряжение номинальное ОВ, В") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfuOV = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 0 || (it.toDouble()) > 400) {
                                error("Значение не в диапазоне 0 — 400")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Ток ОВ, А") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfiOV = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 0 || (it.toDouble()) > 25) {
                                error("Значение не в диапазоне 0 — 25")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Частота вращения, об/мин") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfnAsync = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 0 || (it.toDouble()) > 3000) {
                                error("Значение не в диапазоне 0 — 3000")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("КПД, %") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfkpd = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 0 || (it.toDouble()) > 100) {
                                error("Значение не в диапазоне 0 — 100")
                            } else null
                        }
                    }
                }
                toInsert = vbox(16.0, Pos.CENTER) {
                    hbox(16.0, Pos.CENTER) {
                        label("Схема соединения обмоток") {
                            hboxConstraints {
                                hGrow = Priority.ALWAYS
                            }
                            useMaxWidth = true
                        }
                        tfscheme = combobox<String>() {
                            minWidth = 266.0
                            items = observableListOf(schemeType.triangle, schemeType.star)
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Напряжение ВИУ, В") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfuVIU = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 0 || (it.toDouble()) > 3200) {
                                error("Значение не в диапазоне 0 — 3200")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Напряжение испытания мегаомметром, В") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tfuMGR = textfield {
                        validator.addValidator(this) {
                            if (it?.toIntOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toInt()) < 100 || (it.toInt()) > 2500) {
                                error("Значение не целое число в диапазоне 100 — 2500")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Время испытания ВИУ, с") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tftimeVIU = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 10 || (it.toDouble()) > 600) {
                                error("Значение не в диапазоне 10 — 600")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Время испытания ХХ, с") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tftimeHH = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 10 || (it.toDouble()) > 600) {
                                error("Значение не в диапазоне 10 — 600")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Время испытания номинальной нагрузкой, с") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tftimeMVZ = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 10 || (it.toDouble()) > 180) {
                                error("Значение не в диапазоне 10 — 180")
                            } else null
                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Время испытания повышенной нагрузкой, с") {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                    tftimeRUNNING = textfield {
                        validator.addValidator(this) {
                            if (it?.toDoubleOrNull() == null) {
                                error("Обязательное поле")
                            } else if ((it.toDouble()) < 10) {
                                error("Значение меньше 10")
                            } else null
                        }
                    }
                }
            }
        }
    }.addClass(Styles.mainTheme)

    fun getObjects() {
        cbObjects.items = arrayListOf<TestObjects>().asObservable()
        transaction {
            TestObjects.all().forEach {
                cbObjects.items.add(it)
            }
        }
        cbObjects.items = cbObjects.items.reversed().asObservable()
        cbObjects.selectionModel.selectFirst()
        getObjectData()
    }

    fun getObjectData() {
        with(cbObjects.selectionModel.selectedItem) {
            runLater {
                tfp2.text = p2
                tfuN.text = uNom
                tfiN.text = iN
                tfnAsync.text = nAsync
                tfkpd.text = kpd
                tfscheme.selectionModel.select(scheme)
                tfuVIU.text = uVIU
                tfuMGR.text = uMGR
                tftimeVIU.text = timeVIU
                tftimeHH.text = timeHH
                tftimeMVZ.text = timeMVZ
                tftimeRUNNING.text = timeRUNNING
                tfiOV.text = iOV
                tfuOV.text = uOV
                cbObjectType.selectionModel.select(type)
            }
        }
        if (cbObjectType.selectionModel.selectedItem == motorType.gpt || cbObjectType.selectionModel.selectedItem == motorType.dpt) {
            toInsert.hide()
        } else {
            toInsert.show()
        }
    }

    fun saveItem() {
        if (validator.isValid) {
            var tempName = ""
            if (tfObjectName.text.isNullOrEmpty()) {
                tempName = cbObjects.selectionModel.selectedItem.name
            } else {
                tempName = tfObjectName.text
            }
            transaction {
                if (!TestObjects.find { Objects.name eq tempName }.empty())
                    Objects.deleteWhere {
                        Objects.name eq tempName
                    }
                TestObjects.new {
                    name = tempName
                    type = cbObjectType.selectedItem.toString()
                    p2 = tfp2.text
                    uNom = tfuN.text
                    iN = tfiN.text
                    nAsync = tfnAsync.text
                    kpd = tfkpd.text
                    scheme = tfscheme.selectionModel.selectedItem
                    uVIU = tfuVIU.text
                    uMGR = tfuMGR.text
                    timeVIU = tftimeVIU.text
                    timeHH = tftimeHH.text
                    timeMVZ = tftimeMVZ.text
                    timeRUNNING = tftimeRUNNING.text
                    iOV = tfiOV.text
                    uOV = tfuOV.text
                }
            }
            getObjects()
            runLater {
                infoNotification("Сохранение", "Успешно сохранено")
            }
        } else {
            errorNotification("Ошибка", "Проверьте введенные данные")
        }
    }

    fun removeItem() {
        val tempName = cbObjects.selectionModel.selectedItem.name
        if (cbObjects.items.size > 1) {
            transaction {
                if (!TestObjects.find { Objects.name eq tempName }.empty())
                    Objects.deleteWhere {
                        Objects.name eq tempName
                    }
            }
            getObjects()
        } else {
            errorNotification("Ошибка", "Нельзя удалить последний ОИ")
        }
    }
}
