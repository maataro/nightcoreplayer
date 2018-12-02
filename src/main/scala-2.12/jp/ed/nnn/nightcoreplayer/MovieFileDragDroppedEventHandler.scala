package jp.ed.nnn.nightcoreplayer

import java.io.File

import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.input.DragEvent
import javafx.scene.media.{Media, MediaPlayer}

class MovieFileDragDroppedEventHandler(movies: ObservableList[Movie]) extends EventHandler[DragEvent] {
  override def handle(event: DragEvent): Unit = {
    val db = event.getDragboard
    if (db.hasFiles) {
      db.getFiles.toArray(Array[File]()).toSeq.foreach { f =>
        val filePath = f.getAbsolutePath
        val fileName = f.getName
        val media = new Media(f.toURI.toString)
        val player = new MediaPlayer(media)

        // テーブルビューのファイルの時間カラムに正しい合計時間が表示されるようにする実装

        // 一度 Media のインスタンスを MediaPlayer に読み込ませることで、
        // Media の getDuration で正しい Duration のインスタンスを取得できるようになる
        player.setOnReady(new Runnable {
          override def run(): Unit = {
            val time = formatTime(media.getDuration)
            val movie = Movie(System.currentTimeMillis(), fileName, time, filePath, media)
            while (movies.contains(movie)) {   // movie同士のオブジェクトの同値性比較はidフィールドを使う
              movie.setId(movie.getId + 1L)
            }
            movies.add(movie)   // movies は ObservableList であるため、これによって自動的に TableView が変化する
            player.dispose()    // テーブルビューにファイルを登録後、メディアプレイヤーは廃棄する
          }
        })
      }
    }
    event.consume()
  }
}




