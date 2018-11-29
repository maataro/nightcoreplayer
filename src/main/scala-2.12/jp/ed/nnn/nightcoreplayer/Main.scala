package jp.ed.nnn.nightcoreplayer

import java.io.File

import javafx.application.Application
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.{Label, TableColumn, TableView}
import javafx.scene.input.{DragEvent, TransferMode}
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
  private[this] val tableMinWidth = 300   // テーブルの最小の横幅を定数として宣言

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

    val tableView = new TableView[Movie]()  // UIコンポーネントTableView のインスタンスを作成
    tableView.setMinWidth(tableMinWidth)    // 最小の横幅を設定
    // FXCollections.observableArrayList メソッドにより オブザーバブルな配列が取得される
    //この ObservableList は、リスナーをつけることができる、コレクションのインタフェース
    val movies = FXCollections.observableArrayList[Movie]()
    tableView.setItems(movies)  // オブザーバブルな空配列のインスタンスを取得し、TableView のインスタンスに設定

    // この ObservableList は内容が変化すると、オブザーバーパターンの仕組みを利用して、
    // 自動的に TableView に変更を通知して GUI とモデルが同期するようになっています。

    // テーブルのカラムを作成
    val fileNameColumn = new TableColumn[Movie, String]("ファイル名")
    // JavaBeans 形式でモデルを用意した場合、属性名を指定するだけで、 setCellValueFactory メソッドを使うことで、
    // 自動的にそのモデルの値を取得できるようにすることができる
    fileNameColumn.setCellValueFactory(new PropertyValueFactory("fileName"))
    fileNameColumn.setPrefWidth(160)
    val timeColumn = new TableColumn[Movie, String]("時間")
    // Movieからtimeプロパティを取得するコールバック関数をセット
    timeColumn.setCellValueFactory(new PropertyValueFactory("time"))
    timeColumn.setPrefWidth(80)

    tableView.getColumns.setAll(fileNameColumn, timeColumn)



    val baseBorderPane = new BorderPane()  // BorderPane クラスは、レイアウトを行うことができる部品
    baseBorderPane.setStyle("-fx-background-color: Black")  // JavaFX の CSS のスタイル表記で背景色を黒にするというスタイル指定
    baseBorderPane.setCenter(mediaView)     //  BorderPane クラスの中央に mediaView をレイアウト
    baseBorderPane.setBottom(toolBar)       // ツールバーがMediaViewエリアの下側にレイアウトされる
    baseBorderPane.setRight(tableView)      // baseBorderPane の右側に TableView のインスタンスをレイアウト

    // ウィンドウの大きさを指定して、その Scene に含める JavaFX の Node を指定してインスタンス化
    val scene = new Scene(baseBorderPane, mediaViewFitWidth + tableMinWidth, mediaViewFitHeight + toolBarMinHeight)
    scene.setFill(Color.BLACK)  //  Scene では、背景も設定することができる

    // オブザーバーパターンの仕組みを応用した仕組み
    // bind: 対象に変化があった場合、指定した値の変化に自動的に追従するリスナーを追加してくれるメソッド
    // 内部的にはリスナーオブジェクトが、 Scene の widthProperty につけられることによって実現
    mediaView.fitWidthProperty().bind(scene.widthProperty().subtract(tableMinWidth))  // //  Scene の幅に変更があった際に、自動的に MediaView の幅に追従するようにする処理
    mediaView.fitHeightProperty().bind(scene.heightProperty().subtract(toolBarMinHeight))

    scene.setOnDragOver(new EventHandler[DragEvent] {
      override def handle(event: DragEvent): Unit = {
        if (event.getGestureSource != scene &&
          event.getDragboard.hasFiles) {
          event.acceptTransferModes(TransferMode.COPY_OR_MOVE: _*)
        }
        event.consume()
      }
    })

    scene.setOnDragDropped(new EventHandler[DragEvent] {
      override def handle(event: DragEvent): Unit = {
        val db = event.getDragboard
        if (db.hasFiles) {
          db.getFiles.toArray(Array[File]()).toSeq.foreach { f =>
            val filePath = f.getAbsolutePath
            val fileName = f.getName
            val media = new Media(f.toURI.toString)
            val time = formatTime(media.getDuration)
            val movie = Movie(System.currentTimeMillis(), fileName, time, filePath, media)
            while (movies.contains(movie)) {  // movie同士のオブジェクトの同値性比較はidフィールドを使う
              movie.setId(movie.getId + 1L)
            }
            movies.add(movie) // movies は ObservableList であるため、これによって自動的に TableView が変化する
          }
        }
        event.consume()
      }
    })

    primaryStage.setTitle("mp4ファイルをドラッグ&ドロップしてください")

    primaryStage.setScene(scene) // Stage クラスは、最上位の JavaFX のコンテナで、 Scene を格納することができる。
    primaryStage.show() // 見えるようにするための show メソッド
  }

  private[this] def formatTime(elapsed: Duration): String = {
    // Durationはミリセカンド秒、${再生時間}:${再生分}:${再生秒}/${全体時間}:${全体分}:${全体秒}
    "%02d:%02d:%02d".format(
      elapsed.toHours.toInt,
      elapsed.toMinutes.toInt % 60,
      elapsed.toSeconds.toInt % 60
    )
  }

  private[this] def formatTime(elapsed: Duration, duration: Duration): String =
    s"${formatTime(elapsed)}/${formatTime(duration)}"
}
