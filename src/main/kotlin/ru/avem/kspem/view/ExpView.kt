package ru.avem.kspem.view

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import ru.avem.kspem.controllers.CustomController
import ru.avem.kspem.controllers.MainViewController
import ru.avem.kspem.utils.Singleton
import ru.avem.kspem.utils.State
import tornadofx.*

class ExpView : View("Окно испытания") {
    var vBoxLog: VBox by singleAssign()

//    var circlePR200: Circle by singleAssign()
//    var circlePM135: Circle by singleAssign()
//    var circleDelta: Circle by singleAssign()
//    var circleLATR: Circle by singleAssign()

    val controller: MainViewController by inject()
    val customController: CustomController by inject()
    var btnStart: Button by singleAssign()
    var btnExit: Button by singleAssign()
    var btnHideFirst: Button by singleAssign()
    var btnHideSecond: Button by singleAssign()
    var btnNext: Button by singleAssign()
    var btnStop: Button by singleAssign()
    var vboxExp: VBox by singleAssign()
    var hboxLog: HBox by singleAssign()

    override fun onDock() {
        super.onDock()
        root.style {
            baseColor = Color.hsb(Singleton.color1, Singleton.color2, Singleton.color3)
        }
        controller.initExp()
    }

    override val root =
        anchorpane {
            style {
                baseColor = Color.hsb(Singleton.color1, Singleton.color2, Singleton.color3)
            }
            vbox(10.0, Pos.CENTER) {
                anchorpaneConstraints {
                    leftAnchor = 4.0
                    rightAnchor = 4.0
                    topAnchor = 4.0
                    bottomAnchor = 4.0
                }
                vbox(16.0, Pos.CENTER) {
                    vboxExp = vbox {}
                }
                hboxLog = hbox(16.0, Pos.CENTER) {
                    scrollpane {
                        hboxConstraints {
                            hgrow = Priority.ALWAYS
                        }
                        isMouseTransparent = false
                        minHeight = 300.0
                        maxHeight = 300.0
                        useMaxWidth = true
                        style {
//                            baseColor = Color.TRANSPARENT
//                        backgroundImage += URI("/logImg.png")
//                            backgroundSize += BackgroundSize(1200.0, 200.0, false, false, false, false)
                        }
                        vBoxLog = vbox {
                        }.addClass(Styles.maxTemp)
                        vvalueProperty().bind(vBoxLog.heightProperty())
                    }
                    vbox(8.0, Pos.CENTER_RIGHT) {
                        minWidth = 167.0
                        btnHideFirst = button("") {
                            graphic = MaterialDesignIconView(MaterialDesignIcon.ARROW_DOWN).apply {
                                glyphSize = 48
                                fill = c("#FFFFFF")
                            }
                            action {
                                hboxLog.hide()
                                btnHideSecond.show()
                                show()
                                try {
                                    val scrollPane = vboxExp.children[0] as ScrollPane
                                    scrollPane.vvalue = scrollPane.vmax
                                } catch (ignored: Exception) {

                                }
                            }
                        }
//                        label("Состояние приборов")
//                        hbox(16.0) {
//                            label("ПР102") {
//                                hboxConstraints {
//                                    hgrow = Priority.ALWAYS
//                                }
//                                useMaxWidth = true
//                            }
//                            circlePR200 = circle {
//                                radius = 20.0
//                                fill = State.INTERMEDIATE.c
//                                stroke = c("black")
//                                isSmooth = true
//                            }
//                        }
//                        hbox(16.0) {
//                            label("PM135") {
//                                hboxConstraints {
//                                    hgrow = Priority.ALWAYS
//                                }
//                                useMaxWidth = true
//                            }
//                            circlePM135 = circle {
//                                radius = 20.0
//                                fill = State.INTERMEDIATE.c
//                                stroke = c("black")
//                                isSmooth = true
//                            }
//                        }
//                        hbox(16.0) {
//                            label("Delta") {
//                                hboxConstraints {
//                                    hgrow = Priority.ALWAYS
//                                }
//                                useMaxWidth = true
//                            }
//                            circleDelta = circle {
//                                radius = 20.0
//                                fill = State.INTERMEDIATE.c
//                                stroke = c("black")
//                                isSmooth = true
//                            }
//                        }
                    }
                }
                hbox(16.0, Pos.CENTER) {
                    hbox(16.0, Pos.CENTER) {
                        minWidth = 1800.0
                        btnExit = button("Выход") {
                            action {
                                controller.exit()
                            }
                        }
                        btnStart = button("Старт") {
                            action {
                                controller.startExperiment()
                            }
                        }
                        btnStop = button("Стоп") {
                            action {
                                controller.stopExperiment()
                            }
                        }
//                    label("            ")
                        btnNext = button("Вперед") {
                            action {
                                controller.next()
                            }
                        }
                    }
                    hbox(alignment = Pos.CENTER_RIGHT) {
                        btnHideSecond = button("") {

                            graphic = MaterialDesignIconView(MaterialDesignIcon.ARROW_UP).apply {
                                glyphSize = 48
                                fill = c("#FFFFFF")
                            }
                            hide()
                            action {
                                hboxLog.show()
                                btnHideFirst.show()
                                hide()
                            }
                        }
                    }
                }
            }
        }.addClass(Styles.expTheme)
}
