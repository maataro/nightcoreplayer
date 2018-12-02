package jp.ed.nnn.nightcoreplayer

import java.io.File

import javafx.application.Application
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.FXCollections
import javafx.event.{ActionEvent, EventHandler}
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control._
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.{DragEvent, MouseEvent, TransferMode}
import javafx.scene.layout.{BorderPane, HBox}
import javafx.scene.media.{Media, MediaPlayer, MediaView}
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.{Callback, Duration}

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

    val mediaView = new MediaView()

    val timeLabel = new Label()
    timeLabel.setText("00:00:00/00:00:00")
    timeLabel.setTextFill(Color.WHITE)  // ラベルのテキストの色を変更


    val toolBar = new HBox()  // HBoxは、単一の水平行に子をレイアウトする
    toolBar.setMinHeight(toolBarMinHeight)
    toolBar.setAlignment(Pos.CENTER)    // ツールバーにおける整列を中央寄せにする
    toolBar.setStyle("-fx-background-color: Black") // ツールバーにスタイルシートを提供して背景色を黒に設定

    val tableView = new TableView[Movie]()  // UIコンポーネントTableView のインスタンスを作成
    tableView.setMinWidth(tableMinWidth)    // 最小の横幅を設定
    // FXCollections.observableArrayList メソッドにより オブザーバブルな配列が取得される
    //この ObservableList は、リスナーをつけることができる、コレクションのインタフェース
    val movies = FXCollections.observableArrayList[Movie]()
    tableView.setItems(movies)  // オブザーバブルな空配列のインスタンスを取得し、TableView のインスタンスに設定

    // setRowFactoryメソッドで、テーブルの行をUIとして生成
    tableView.setRowFactory(new Callback[TableView[Movie], TableRow[Movie]]() {
      override def call(param: TableView[Movie]): TableRow[Movie] = {
        val row = new TableRow[Movie]()
        row.setOnMouseClicked(new EventHandler[MouseEvent] {
          override def handle(event: MouseEvent): Unit = {
            if (event.getClickCount >= 1 && !row.isEmpty) {
              playMovie(row.getItem, tableView, mediaView, timeLabel)
            }
          }
        })
        row
      }
    })

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

    val deleteActionColumn = new TableColumn[Movie, Long]("削除")
    deleteActionColumn.setCellValueFactory(new PropertyValueFactory("id"))
    deleteActionColumn.setPrefWidth(60)
    deleteActionColumn.setCellFactory(new Callback[TableColumn[Movie, Long], TableCell[Movie, Long]]() {
      override def call(param: TableColumn[Movie, Long]): TableCell[Movie, Long] = {
        new DeleteCell(movies, mediaView, tableView)
      }
    })

    tableView.getColumns.setAll(fileNameColumn, timeColumn, deleteActionColumn)

    // first button
    val firstButton = createButton("first.png", new EventHandler[ActionEvent]() {
      override def handle(event: ActionEvent): Unit =
        if (mediaView.getMediaPlayer != null) {
          playPre(tableView, mediaView, timeLabel)
        }
    })


    // back button
    val backButton = createButton("back.png", new EventHandler[ActionEvent]() {
      override def handle(event: ActionEvent): Unit =
        if (mediaView.getMediaPlayer != null) {
          mediaView.getMediaPlayer.seek(
            mediaView.getMediaPlayer.getCurrentTime.subtract(new Duration(10000)))
        }
    })

    // play button
    val playButton = createButton("play.png", new EventHandler[ActionEvent]() {
      // 再生ボタンが押された時の処理
      override def handle(event: ActionEvent): Unit = {
        val selectionModel = tableView.getSelectionModel  // TableVeiw から SelectionModel という選択を管理するモデルを取得
        if (mediaView.getMediaPlayer != null && !selectionModel.isEmpty) {
          mediaView.getMediaPlayer.play()
        }
      }
    })


    // pause button
    val pauseButton = createButton("pause.png", new EventHandler[ActionEvent]() {
      override def handle(event: ActionEvent): Unit = {
        if (mediaView.getMediaPlayer != null) mediaView.getMediaPlayer.pause()
      }
    })

    // forward button
    val forwardButton = createButton("forward.png", new EventHandler[ActionEvent]() {
      override def handle(event: ActionEvent): Unit =
        if (mediaView.getMediaPlayer != null) {
          mediaView.getMediaPlayer.seek(
            mediaView.getMediaPlayer.getCurrentTime.add(new Duration(10000)))
        }
    })

    // last button
    val lastButton = createButton("last.png", new EventHandler[ActionEvent]() {
      override def handle(event: ActionEvent): Unit =
        if (mediaView.getMediaPlayer != null) {
          playNext(tableView, mediaView, timeLabel)
        }
    })

    // fullscreen button
    val fullscreenButton = createButton("fullscreen.png", new EventHandler[ActionEvent]() {
      override def handle(event: ActionEvent): Unit =
        primaryStage.setFullScreen(true)
    })

    toolBar.getChildren.addAll(
      firstButton, backButton, playButton, pauseButton, forwardButton, lastButton, fullscreenButton, timeLabel)

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

    // リファクタリング：匿名内部クラスをクラスにした
    scene.setOnDragOver(new MovieFileDragOverEventHandler(scene))
    scene.setOnDragDropped(new MovieFileDragDroppedEventHandler(movies))


    primaryStage.setTitle("mp4ファイルをドラッグ&ドロップしてください")

    primaryStage.setScene(scene) // Stage クラスは、最上位の JavaFX のコンテナで、 Scene を格納することができる。
    primaryStage.show() // 見えるようにするための show メソッド
  }

  // createButton メソッドを新設して処理を共通化
  private[this] def createButton(imagePath: String, eventHandler: EventHandler[ActionEvent]): Button = {
    val buttonImage = new Image(getClass.getResourceAsStream(imagePath))
    val button = new Button()
    button.setGraphic(new ImageView(buttonImage))
    button.setStyle("-fx-background-color: Black")
    button.setOnAction(eventHandler)
    button.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler[MouseEvent]() {
      override def handle(event: MouseEvent): Unit = {
        button.setStyle("-fx-body-color: Black")
      }
    })
    button.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler[MouseEvent]() {
      override def handle(event: MouseEvent): Unit = {
        button.setStyle("-fx-background-color: Black")
      }
    })
    button
  }

  private[this] def playMovie(movie: Movie, tableView: TableView[Movie], mediaView: MediaView, timeLabel: Label): Unit = {
    if (mediaView.getMediaPlayer != null) {
      val oldPlayer = mediaView.getMediaPlayer
      oldPlayer.stop()
      oldPlayer.dispose()
    }

    val mediaPlayer = new MediaPlayer(movie.media)
    mediaPlayer.currentTimeProperty().addListener(new ChangeListener[Duration] {
      override def changed(observable: ObservableValue[_ <: Duration], oldValue: Duration, newValue: Duration): Unit =
        timeLabel.setText(formatTime(mediaPlayer.getCurrentTime, mediaPlayer.getTotalDuration))
    })
    mediaPlayer.setOnReady(new Runnable {
      override def run(): Unit =
        timeLabel.setText(formatTime(mediaPlayer.getCurrentTime, mediaPlayer.getTotalDuration))
    })
    // 現在のファイルの再生が終了したら、次のファイルの再生
    mediaPlayer.setOnEndOfMedia(new Runnable {
      override def run(): Unit = playNext(tableView, mediaView, timeLabel)
    })

    mediaView.setMediaPlayer(mediaPlayer)
    mediaPlayer.setRate(1.25)
    //mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE)  // 同じ曲を無限にループ再生する
    mediaPlayer.play()
  }

  // Track という sealed トレイトを用意して、 Pre と Next という 2 つの列挙型を用意
  sealed trait Track
  object Pre extends Track
  object Next extends Track



  // TableView から取得してた選択された Movie の処理などを、このメソッドに集約
  private[this] def playAt(track: Track, tableView: TableView[Movie], mediaView: MediaView, timeLabel: Label): Unit = {
    val selectionModel = tableView.getSelectionModel
    if (selectionModel.isEmpty) return
    val index = selectionModel.getSelectedIndex
    // Pre と Next をパターンマッチによって処理を分岐
    val changedIndex = track match {
      case Pre => (tableView.getItems.size() + index - 1) % tableView.getItems.size()
      case Next => (index + 1) % tableView.getItems.size()
    }
    selectionModel.select(changedIndex)
    val movie = selectionModel.getSelectedItem
    playMovie(movie, tableView, mediaView, timeLabel)
  }

  private[this] def playPre(tableView: TableView[Movie], mediaView: MediaView, timeLabel: Label): Unit = {
    playAt(Pre, tableView, mediaView, timeLabel)
  }

  private[this] def playNext(tableView: TableView[Movie], mediaView: MediaView, timeLabel: Label): Unit = {
    playAt(Next, tableView, mediaView, timeLabel)
  }

//  private[this] def onTableButtonClick(selected: Movie): Unit = {
//    println("削除されました")
//  }

}


