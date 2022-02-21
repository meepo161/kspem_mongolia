package ru.avem.kspem.view

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.stage.Modality
import org.jetbrains.exposed.sql.transactions.transaction
import ru.avem.kspem.controllers.MainViewController
import ru.avem.kspem.data.MainViewTable
import ru.avem.kspem.data.motorType
import ru.avem.kspem.data.objectModel
import ru.avem.kspem.data.protocolModel
import ru.avem.kspem.database.entities.TestObjects
import ru.avem.kspem.utils.*
import ru.avem.kspem.view.Styles.Companion.mainTheme
import tornadofx.*
import tornadofx.controlsfx.errorNotification
import java.nio.file.Path
import java.nio.file.Paths
import java.text.SimpleDateFormat
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime


class MainView : View("КСПЭМ") {

    private val controller: MainViewController by inject()

    private var cbObjects: ComboBox<TestObjects> by singleAssign()
    var comIndicate: Circle by singleAssign()
    var deltaIndicate: Circle by singleAssign()
    private var sliderColor: Slider by singleAssign()
    private var sliderSaturation: Slider by singleAssign()
    private var sliderBrightness: Slider by singleAssign()

    var circlePR102: Circle by singleAssign()
    var circlePM130: Circle by singleAssign()
    var circleAVEM: Circle by singleAssign()
    var circleLATR: Circle by singleAssign()
    var tfSerial: TextField by singleAssign()
    var btnAuto: Button by singleAssign()
    var cbList = mutableListOf<CheckBox>()
    var labelType: Label by singleAssign()

    val tableData = MainViewTable()

    private var vBoxExps: VBox by singleAssign()

    override val configPath: Path = Paths.get("cfg/app.properties")

    private var someThing: Double = 0.0
        set(value) {
            println("111")
            field = value
        }

    @OptIn(ExperimentalTime::class)
    override fun onDock() {
        sliderColor.value = config["COLOR"].toString().toDouble()
        sliderSaturation.value = config["SATURATION"].toString().toDouble()
        sliderBrightness.value = config["BRIGHTNESS"].toString().toDouble()

        this@MainView.root.style {
            setColorAndSave()
        }

        controller.expListRaw.clear()
        cbList.forEach { it.isSelected = false }
        getObjectItems()
        getObjectData()
        super.onDock()
    }

    override val root = borderpane {
        style {
            baseColor = Color.hsb(Singleton.color1, Singleton.color2, Singleton.color3)
        }
        top {
            menubar {
                menu("Меню") {
                    item("Экран авторизации") {
                        graphic = MaterialDesignIconView(MaterialDesignIcon.NATURE_PEOPLE).apply {
                            glyphSize = 48
                            fill = c("#FF0000")
                        }
                        action {
                            replaceWith<AuthorizationView>()
                        }
                    }
                    item("Выход") {
                        graphic = MaterialDesignIconView(MaterialDesignIcon.EXIT_TO_APP).apply {
                            glyphSize = 48
                            fill = c("#FF0000")
                        }
                        action {
                            exitProcess(0)
                        }
                    }
                }
                menu("База данных") {
                    item("Пользователи") {
                        graphic = MaterialDesignIconView(MaterialDesignIcon.ACCOUNT).apply {
                            glyphSize = 48
                        }
                        action {
                            find<UserEditorWindow>().openModal(
                                modality = Modality.APPLICATION_MODAL,
                                escapeClosesWindow = true,
                                resizable = false,
                                owner = this@MainView.currentWindow
                            )
                        }
                    }
                    item("Объект испытания") {
                        graphic = MaterialDesignIconView(MaterialDesignIcon.ETHERNET_CABLE).apply {
                            glyphSize = 48
                        }
                        action {
//                            find<ObjectEditorWindow>().openModal(
//                                modality = Modality.APPLICATION_MODAL,
//                                escapeClosesWindow = true,
//                                resizable = false,
//                                owner = this@MainView.currentWindow
//                            )
                            replaceWith(find<ObjectEditorWindow>())
                        }
                    }
                    item("Протоколы") {
                        graphic = MaterialDesignIconView(MaterialDesignIcon.FORMAT_LIST_NUMBERS).apply {
                            glyphSize = 48
                        }
                        action {
                            find<ProtocolListWindow>().openModal(
                                modality = Modality.APPLICATION_MODAL,
                                escapeClosesWindow = true,
                                resizable = false,
                                owner = this@MainView.currentWindow
                            )
                        }
                    }
                }
                menu("Выбор темы") {
                    item("Цвет") {
                        sliderColor = slider(0, 360) {
                            onMouseDragged = EventHandler {
                                this@borderpane.style {
                                    setColorAndSave()
                                }
                            }
                            onMousePressed = EventHandler {
                                this@borderpane.style {
                                    setColorAndSave()
                                }
                            }
                            onMouseReleased = EventHandler {
                                this@borderpane.style {
                                    setColorAndSave()
                                }
                            }
                        }
                    }
                    item("Насыщенность") {
                        sliderSaturation = slider(0, 1) {
                            onMouseDragged = EventHandler {
                                this@borderpane.style {
                                    setColorAndSave()
                                }
                            }
                            onMousePressed = EventHandler {
                                this@borderpane.style {
                                    setColorAndSave()
                                }
                            }
                            onMouseReleased = EventHandler {
                                this@borderpane.style {
                                    setColorAndSave()
                                }
                            }
                        }
                    }
                    item("Яркость") {
                        sliderBrightness = slider(0, 1) {
                            onMouseDragged = EventHandler {
                                this@borderpane.style {
                                    setColorAndSave()
                                }
                            }
                            onMousePressed = EventHandler {
                                this@borderpane.style {
                                    setColorAndSave()
                                }
                            }
                            onMouseReleased = EventHandler {
                                this@borderpane.style {
                                    setColorAndSave()
                                }
                            }
                        }
                    }
                }
                menu("Информация") {
//                    item("Руководство пользователя") {
//                        graphic = MaterialDesignIconView(MaterialDesignIcon.COMMENT_QUESTION_OUTLINE).apply {
//                            glyphSize = 48
//                            fill = c("#0000FF")
//                        }
//                        action {
//                            val template = File("cfg/ruk.pdf")
//                            copyFileFromStream(Main::class.java.getResource("ruk.pdf").openStream(), template)
//                            openFile(template)
//                        }
//                    }
//                    item("Расшифровка ошибок") {
//                        graphic = MaterialDesignIconView(MaterialDesignIcon.HELP).apply {
//                            glyphSize = 48
//                            fill = c("#0000FF")
//                        }
//                        action {
////                            val template = File("cfg/ruk.pdf")
////                            copyFileFromStream(Main::class.java.getResource("ruk.pdf").openStream(), template)
////                            openFile(template)
//                        }
//                    }
//                    item("Контроль защит(всех)") {
//                        graphic = MaterialDesignIconView(MaterialDesignIcon.HELP_CIRCLE).apply {
//                            glyphSize = 48
//                            fill = c("#0000FF")
//                        }
//                        action {
//                            find<Controls>().openModal(
//                                modality = Modality.WINDOW_MODAL,
//                                escapeClosesWindow = true,
//                                resizable = false,
//                                owner = this@MainView.currentWindow
//                            )
//                        }
//                    }
                    item("Версия ПО") {
                        graphic = MaterialDesignIconView(MaterialDesignIcon.INFORMATION_OUTLINE).apply {
                            glyphSize = 48
                            fill = c("#0000FF")
                        }
                        action {
                            controller.showAboutUs()
                        }
                    }
                }
            }
        }
        center = anchorpane {
            vbox(32.0, Pos.CENTER) {
                anchorpaneConstraints {
                    leftAnchor = 32.0
                    rightAnchor = 32.0
                    topAnchor = 32.0
                }
                hbox(16.0, Pos.CENTER_LEFT) {
                    label("Шифр машины:")
                    cbObjects = combobox {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                            useMaxWidth = true
                        }
                        onAction = EventHandler {
                            if (!items.isNullOrEmpty() && selectedItem != null) {
                                getObjectData()
                            }
                        }
                        items = arrayListOf<TestObjects>().asObservable()
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Тип машины: ")
                    labelType = label() {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                        useMaxWidth = true
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    label("Заводской номер:")
                    tfSerial = textfield {
                        hboxConstraints {
                            hGrow = Priority.ALWAYS
                        }
                    }
                }
                tableview(observableListOf(tableData)) {
                    hboxConstraints {
                        useMaxWidth = true
                    }
                    minHeight = 102.0
                    maxHeight = 102.0
                    isMouseTransparent = true
                    column("Мощность, кВт", MainViewTable::p2.getter).isEditable = false
                    column("Uн, В", MainViewTable::uN.getter).isEditable = false
                    column("Iн, А", MainViewTable::iN.getter).isEditable = false
                    column("U ОВ, А", MainViewTable::uOV.getter).isEditable = false
                    column("I ОА, А", MainViewTable::iOV.getter).isEditable = false
                    column("Частота вращ., об/мин", MainViewTable::nAsync.getter).isEditable = false
                    columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
                }
                vBoxExps = vbox(16.0, Pos.CENTER_LEFT) {
                    paddingLeft = 64.0
                    minHeight = 456.0
                    maxHeight = 456.0
//                    checkbox(mgr.name) {
//                        onAction = EventHandler {
//                            if (isSelected) {
//                                controller.expList.add(mgr)
//                            } else {
//                                controller.expList.remove(mgr)
//                            }
//                        }
//                    }
                }
                btnAuto = button("Перейти к испытаниям") {
                    action {
                        if (cbObjects.selectionModel.selectedItem != null) {
                            if (controller.expListRaw.size > 0) {
                                controller.clearProtocol()
                                objectModel = cbObjects.selectionModel.selectedItem
                                protocolModel.date =
                                    SimpleDateFormat("dd.MM.y").format(System.currentTimeMillis()).toString()
                                protocolModel.time =
                                    SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis()).toString()
                                protocolModel.operator = controller.position1
                                protocolModel.objectName = cbObjects.selectionModel.selectedItem.name
                                protocolModel.serial =
                                    if (tfSerial.text.isNullOrEmpty()) "Не задан" else tfSerial.text
                                protocolModel.type = objectModel!!.type
                                protocolModel.p2 = objectModel!!.p2
                                protocolModel.uN = objectModel!!.uNom
                                protocolModel.iN = objectModel!!.iN
                                protocolModel.uOV = objectModel!!.uOV
                                protocolModel.iOV = objectModel!!.iOV
                                protocolModel.nAsync = objectModel!!.nAsync
                                protocolModel.kpd = objectModel!!.kpd
                                protocolModel.scheme = objectModel!!.scheme
                                replaceWith<ExpView>()
                            } else {
                                runLater {
                                    Toast.makeText("Выберите испытания").show(Toast.ToastType.ERROR)
                                }
                            }
//                            currentWindow?.let {
//                                showTwoWayDialog(
//                                    "Внимание",
//                                    "Убедитесь в наличии перемычки на обмотках НН",
//                                    "Продолжить",
//                                    "Отменить",
//                                    {
//                                        controller.serialNum =
//                                            if (!tfSerial.text.isNullOrEmpty()) tfSerial.text else "Не задан"
//                                        controller.objectName = cbItems.selectionModel.selectedItem.toString()
//                                        replaceWith<ExpView>()
//                                    },
//                                    {
//                                    },
//                                    currentWindow = it
//                                )
//                            }
                        } else {
                            runLater {
                                errorNotification("Ошибка", "Не выбран ОИ")
                            }
                        }
                    }
                }
            }
        }

        bottom =
            hbox(spacing = 16) {
                hboxConstraints {
                    paddingBottom = 8.0
                }
                paddingLeft = 32.0
                alignment = Pos.CENTER_LEFT
                comIndicate = circle(radius = 20) {
                    fill = State.INTERMEDIATE.c
                    stroke = c("black")
                    isSmooth = true
                }
                label(" Связь БСУ") {
                    hboxConstraints {
                        hGrow = Priority.ALWAYS
                    }
                }
                separator(Orientation.VERTICAL)
                deltaIndicate = circle(radius = 20) {
                    fill = State.INTERMEDIATE.c
                    stroke = c("black")
                    isSmooth = true
                }
                label(" Связь Delta") {
                    hboxConstraints {
                        hGrow = Priority.ALWAYS
                    }
                }
                separator(Orientation.VERTICAL)
                circlePR102 = circle {
                    radius = 20.0
                    fill = State.INTERMEDIATE.c
                    stroke = c("black")
                    isSmooth = true
                }
                label("ПР102") {
                    hboxConstraints {
                        hgrow = Priority.ALWAYS
                    }
                }
//                separator(Orientation.VERTICAL)
//                circlePM130 = circle {
//                    radius = 20.0
//                    fill = State.INTERMEDIATE.c
//                    stroke = c("black")
//                    isSmooth = true
//                }
//                label("PM130") {
//                    hboxConstraints {
//                        hgrow = Priority.ALWAYS
//                    }
//                }
//                separator(Orientation.VERTICAL)
//                circleAVEM = circle {
//                    radius = 20.0
//                    fill = State.INTERMEDIATE.c
//                    stroke = c("black")
//                    isSmooth = true
//                }
//                label("АВЭМ7") {
//                    hboxConstraints {
//                        hgrow = Priority.ALWAYS
//                    }
//                }
//                separator(Orientation.VERTICAL)
//                circleLATR = circle {
//                    radius = 20.0
//                    fill = State.INTERMEDIATE.c
//                    stroke = c("black")
//                    isSmooth = true
//                }
//                label("АРН") {
//                    hboxConstraints {
//                        hgrow = Priority.ALWAYS
//                    }
//                }
            }
    }.addClass(mainTheme)


    fun addCheckBox(vBox: VBox) {
        cbList.clear()
        vBox.clear()
        if (cbObjects.selectionModel.selectedItem.type == motorType.sd) {
            controller.listSD.forEach { contr ->
                vBox.addChildIfPossible(checkbox(contr.name) {
                    cbList.add(this)
                    onAction = EventHandler {
                        if (isSelected) {
                            controller.expListRaw.add(contr)
                        } else {
                            controller.expListRaw.remove(contr)
                        }
                    }
                })
            }
        } else if (cbObjects.selectionModel.selectedItem.type == motorType.sg) {
            controller.listSG.forEach { contr ->
                vBox.addChildIfPossible(checkbox(contr.name) {
                    cbList.add(this)
                    onAction = EventHandler {
                        if (isSelected) {
                            controller.expListRaw.add(contr)
                        } else {
                            controller.expListRaw.remove(contr)
                        }
                    }
                })
            }
        } else if (cbObjects.selectionModel.selectedItem.type == motorType.dpt) {
            controller.listDPT.forEach { contr ->
                vBox.addChildIfPossible(checkbox(contr.name) {
                    cbList.add(this)
                    onAction = EventHandler {
                        if (isSelected) {
                            controller.expListRaw.add(contr)
                        } else {
                            controller.expListRaw.remove(contr)
                        }
                    }
                })
            }
        } else if (cbObjects.selectionModel.selectedItem.type == motorType.gpt) {
            controller.listGPT.forEach { contr ->
                vBox.addChildIfPossible(checkbox(contr.name) {
                    cbList.add(this)
                    onAction = EventHandler {
                        if (isSelected) {
                            controller.expListRaw.add(contr)
                        } else {
                            controller.expListRaw.remove(contr)
                        }
                    }
                })
            }
        }
    }

    fun getObjectItems() {
        clearTable()
        cbObjects.items = arrayListOf<TestObjects>().asObservable()
        transaction {
            TestObjects.all().forEach {
                cbObjects.items.add(it)
            }
        }
        cbObjects.items = cbObjects.items.reversed().asObservable()
        cbObjects.selectionModel.selectFirst()
    }

    private fun getObjectData() {
        labelType.text = getFullType(cbObjects.selectionModel.selectedItem.type)
        with(cbObjects.selectionModel.selectedItem) {
            tableData.p2.value = p2
            tableData.uN.value = uNom
            tableData.iN.value = iN
            tableData.uOV.value = uOV
            tableData.iOV.value = iOV
            tableData.nAsync.value = nAsync
        }
        addCheckBox(vBoxExps)
    }

    private fun clearTable() {
    }

    private fun InlineCss.setColorAndSave() {
        baseColor = Color.hsb(sliderColor.value, sliderSaturation.value, sliderBrightness.value)
        with(config) {
            set("COLOR" to sliderColor.value)
            set("SATURATION" to sliderSaturation.value)
            set("BRIGHTNESS" to sliderBrightness.value)
            Singleton.color1 = sliderColor.value
            Singleton.color2 = sliderSaturation.value
            Singleton.color3 = sliderBrightness.value
            save()
        }
    }

    fun setToDefault() {
        runLater {
        }
    }
}

