package ru.avem.kspem.view

import javafx.geometry.Pos
import javafx.scene.control.TextField
import ru.avem.kspem.utils.Singleton.sparking2
import ru.avem.kspem.utils.Singleton.sparking1
import tornadofx.*

class AlertView : View("Степень искрения") {

    var tfMPT: TextField by singleAssign()
    var tfSG: TextField by singleAssign()

    override val root = anchorpane {
        vbox(spacing = 32.0) {
            anchorpaneConstraints {
                leftAnchor = 16.0
                rightAnchor = 16.0
                bottomAnchor = 16.0
                topAnchor = 16.0
            }
            alignment = Pos.CENTER

            label("Степень искрения 1 узла:") {

            }
            tfMPT = textfield {

            }
            label("Степень искрения 2 узла:") {

            }

            tfSG = textfield {

            }
            button("Сохранить") {
                action {
                    sparking1.add(tfMPT.text)
                    sparking2.add(tfSG.text)
                }
            }
        }/*.addClass(Styles.expTheme)*/
    }

}