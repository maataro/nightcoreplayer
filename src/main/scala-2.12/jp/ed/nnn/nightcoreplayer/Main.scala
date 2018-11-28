package jp.ed.nnn.nightcoreplayer

import java.io.File

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.media.{Media, MediaPlayer, MediaView}
import javafx.scene.paint.Color
import javafx.stage.Stage

object Main extends App { //  Scala のアプリケーションとして実行するために、 App トレイトをミックスイン
  // App トレイトのフィールドであるargs という Array[String] という型の変数を
  // launch の可変長引数として渡すために、 args: _* という型を指定した形式で渡している。
  // classOf[Main] : Scala でクラスオブジェクトを得るための書き方
  Application.launch(classOf[Main], args: _*)

}
// JavaFX における最小構成のアプリケーションの構成
class Main extends Application {

  override def start(primaryStage: Stage): Unit = {
    val path = "\\Users\\Maatarou\\workspace\\download\\video.mp4"
    val media = new Media(new File(path).toURI.toString)  // Media は再生するメディアファイルを表すクラス
    val mediaPlayer = new MediaPlayer(media)    // MediaPlayer はメディアを再生するためのクラス
    mediaPlayer.setRate(1.25)  // プレイヤーでの再生速度を 1.25 倍に設定(Nightcore プレイヤーの要件)
    mediaPlayer.play()         // プレイヤーの再生
    val mediaView = new MediaView(mediaPlayer) // 実際に映像を表示するインスタンス
    val baseBorderPane = new BorderPane()  // BorderPane クラスは、レイアウトを行うことができる部品
    baseBorderPane.setStyle("-fx-background-color: Black")  // JavaFX の CSS のスタイル表記で背景色を黒にするというスタイル指定
    baseBorderPane.setCenter(mediaView)     //  BorderPane クラスの中央に mediaView をレイアウト
    // ウィンドウの大きさを指定して、その Scene に含める JavaFX の Node を指定してインスタンス化
    val scene = new Scene(baseBorderPane, 800, 500) // コンテナ：Scene クラスは、 JavaFX の全てのUIコンポーネントの入れ物
    scene.setFill(Color.BLACK)  //  Scene では、背景も設定することができる
    primaryStage.setScene(scene) // Stage クラスは、最上位の JavaFX のコンテナで、 Scene を格納することができる。
    primaryStage.show() // 見えるようにするための show メソッド
  }

}
