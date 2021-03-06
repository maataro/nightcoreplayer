package jp.ed.nnn.nightcoreplayer

import javafx.scene.media.Media

import scala.beans.BeanProperty

class Movie {

  // @BeanPropertyはアノテーション
  //  @BeanPropertyを付けることで、getter (ゲッター) と setter (セッター) を自動で追加してくれる
  @BeanProperty
  var id: Long = _

  @BeanProperty
  var fileName: String = _

  @BeanProperty
  var time: String = _

  @BeanProperty
  var filePath: String = _

  @BeanProperty
  var media: Media = _

  def canEqual(other: Any): Boolean = other.isInstanceOf[Movie]

  //  IntelliJ IDEA が id という属性を元に自動的に生成した、 equals と hashcode メソッド
  // この 2 つのメソッド は、ハッシュ関数を内部的に利用するコレクションや、 同値性を判断するのに利用される
  override def equals(other: Any): Boolean = other match {
    case that: Movie =>
      (that canEqual this) &&
        id == that.id
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(id)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object Movie {

  // コンパニオンオブジェクトを利用したファクトリメソッド
  // BeanPropertyアノテーションにより自動的に追加されたセッターメソッドを使ってフィールドに値をセットしている
  def apply(id: Long, fileName: String, time: String, filePath: String, media: Media): Movie = {
    val movie = new Movie
    movie.setId(id)
    movie.setFileName(fileName)
    movie.setTime(time)
    movie.setFilePath(filePath)
    movie.setMedia(media)
    movie
  }
}
