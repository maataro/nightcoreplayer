package jp.ed.nnn.nightcoreplayer

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.stage.Stage

object Main extends App {
  Application.launch(classOf[Main], args: _*)

}

class Main extends Application {

  override def start(primaryStage: Stage): Unit = {
    val baseBorderPane = new BorderPane()
    val scene = new Scene(baseBorderPane, 800, 500)
    scene.setFill(Color.BLACK)
    primaryStage.setScene(scene)
    primaryStage.show()
  }

}
