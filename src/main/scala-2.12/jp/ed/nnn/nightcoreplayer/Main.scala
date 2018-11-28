package jp.ed.nnn.nightcoreplayer

import java.io.File

import javafx.application.Application
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.{BorderPane, HBox}
import javafx.scene.media.{Media, MediaPlayer, MediaView}
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.Duration

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
    mediaView.setFitWidth(800)
    mediaView.setFitHeight(450)
    val timeLabel = new Label()
    timeLabel.setTextFill(Color.WHITE)  // ラベルのテキストの色を変更
    mediaPlayer.currentTimeProperty().addListener(new ChangeListener[Duration]{
      override def changed(observable: ObservableValue[_ <: Duration], oldValue: Duration, newValue: Duration): Unit =
        timeLabel.setText(formatTime(mediaPlayer.getCurrentTime, mediaPlayer.getTotalDuration))
    })
    mediaPlayer.setOnReady(new Runnable {
      override def run(): Unit =
        timeLabel.setText(formatTime(mediaPlayer.getCurrentTime, mediaPlayer.getTotalDuration))
    })
    val toolBar = new HBox(timeLabel)  // HBoxは、単一の水平行に子をレイアウトする
    toolBar.setAlignment(Pos.CENTER)    // ツールバーにおける整列を中央寄せにする
    toolBar.setStyle("-fx-background-color: Black") // ツールバーにスタイルシートを提供して背景色を黒に設定
    val baseBorderPane = new BorderPane()  // BorderPane クラスは、レイアウトを行うことができる部品
    baseBorderPane.setStyle("-fx-background-color: Black")  // JavaFX の CSS のスタイル表記で背景色を黒にするというスタイル指定
    baseBorderPane.setCenter(mediaView)     //  BorderPane クラスの中央に mediaView をレイアウト
    baseBorderPane.setBottom(toolBar)       // ツールバーがMediaViewエリアの下側にレイアウトされる
    // ウィンドウの大きさを指定して、その Scene に含める JavaFX の Node を指定してインスタンス化
    val scene = new Scene(baseBorderPane, 800, 500) // コンテナであるSceneに含めるNodeを指定、ウィンドウの大きさを指定
    scene.setFill(Color.BLACK)  //  Scene では、背景も設定することができる
    primaryStage.setScene(scene) // Stage クラスは、最上位の JavaFX のコンテナで、 Scene を格納することができる。
    primaryStage.show() // 見えるようにするための show メソッド
  }

  private[this] def formatTime(elapsed: Duration, duration: Duration): String = {
    // Durationはミリセカンド秒、${再生時間}:${再生分}:${再生秒}/${全体時間}:${全体分}:${全体秒}
    "%02d:%02d:%02d/%02d:%02d:%02d".format(
      elapsed.toHours.toInt,
      elapsed.toMinutes.toInt % 60,
      elapsed.toSeconds.toInt % 60,
      duration.toHours.toInt,
      duration.toHours.toInt % 60,
      duration.toSeconds.toInt % 60)
  }

}
