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

  private[this] val mediaViewFitWidth = 800
  private[this] val mediaViewFitHeight = 450
  private[this] val toolBarMinHeight = 50

  override def start(primaryStage: Stage): Unit = {
    val path = "\\Users\\Maatarou\\workspace\\download\\video.mp4"
    val media = new Media(new File(path).toURI.toString)  // Media は再生するメディアファイルを表すクラス
    val mediaPlayer = new MediaPlayer(media)    // MediaPlayer はメディアを再生するためのクラス
    mediaPlayer.setRate(1.25)  // プレイヤーでの再生速度を 1.25 倍に設定(Nightcore プレイヤーの要件)
    mediaPlayer.play()         // プレイヤーの再生
    val mediaView = new MediaView(mediaPlayer) // 実際に映像を表示するインスタンス

    val timeLabel = new Label()
    timeLabel.setTextFill(Color.WHITE)  // ラベルのテキストの色を変更
    // mediaPlayer オブジェクトに匿名内部クラスとしてリスナーオブジェクトを追加
    mediaPlayer.currentTimeProperty().addListener(new ChangeListener[Duration]{  // 変化を監視するためのオブジェクトを追加
      // ChangeListenerのchangeメソッドはmediaPlayer内の何かしらのメソッドにより、適切な引数を渡して呼び出される
      override def changed(observable: ObservableValue[_ <: Duration], oldValue: Duration, newValue: Duration): Unit =
        timeLabel.setText(formatTime(mediaPlayer.getCurrentTime, mediaPlayer.getTotalDuration))
    })
    mediaPlayer.setOnReady(new Runnable {
      override def run(): Unit =
        timeLabel.setText(formatTime(mediaPlayer.getCurrentTime, mediaPlayer.getTotalDuration))
    })
    val toolBar = new HBox(timeLabel)  // HBoxは、単一の水平行に子をレイアウトする
    toolBar.setMinHeight(toolBarMinHeight)
    toolBar.setAlignment(Pos.CENTER)    // ツールバーにおける整列を中央寄せにする
    toolBar.setStyle("-fx-background-color: Black") // ツールバーにスタイルシートを提供して背景色を黒に設定
    val baseBorderPane = new BorderPane()  // BorderPane クラスは、レイアウトを行うことができる部品
    baseBorderPane.setStyle("-fx-background-color: Black")  // JavaFX の CSS のスタイル表記で背景色を黒にするというスタイル指定
    baseBorderPane.setCenter(mediaView)     //  BorderPane クラスの中央に mediaView をレイアウト
    baseBorderPane.setBottom(toolBar)       // ツールバーがMediaViewエリアの下側にレイアウトされる

    // ウィンドウの大きさを指定して、その Scene に含める JavaFX の Node を指定してインスタンス化
    val scene = new Scene(baseBorderPane, mediaViewFitWidth, mediaViewFitHeight + toolBarMinHeight)
    scene.setFill(Color.BLACK)  //  Scene では、背景も設定することができる

    // オブザーバーパターンの仕組みを応用した仕組み
    // bind: 対象に変化があった場合、指定した値の変化に自動的に追従するリスナーを追加してくれるメソッド
    // 内部的にはリスナーオブジェクトが、 Scene の widthProperty につけられることによって実現
    mediaView.fitWidthProperty().bind(scene.widthProperty())  // //  Scene の幅に変更があった際に、自動的に MediaView の幅に追従するようにする処理
    mediaView.fitHeightProperty().bind(scene.heightProperty().subtract(toolBarMinHeight))

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
