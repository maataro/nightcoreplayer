
// オブザーバーパターンの基本形
//  Listener というトレイトと、Observable というオブジェクト


// 変化を監視するオブジェクト: リスナーまたは、オブザーバー
trait Listener {  // ChangeListener
  // ChangeListenerのchangeメソッド 要オーバーライド
  // mediaPlayerオブジェクトのincrementメソッド内で、mediaPlayerのフィールドnumを引数に呼び出される
  def changed(newValue: Int): Unit
}

// 監視対象のオブジェクト: オブザーバブルまたは、サブジェクト
object Observable {   // mediaPlayer
  private var num = 0    // currentTimeProperty（現在時間）
  private var listeners = Seq[Listener]()  // mediaPlayerが持つリスナーのリスト

  // mediaPlayerの再生中に呼び出されるメソッド？
  // increment メソッドは、内部の数字を 1 増やすとともに、
  // Observable オブジェクトが所持している全てのリスナーに変化があったことを通知
  def increment(): Unit = {
    num = num + 1  // 現在時間を更新
    listeners.foreach(l => l.changed(num))  // すべてのリスナーはchangedメソッドを持つ？
  }

  // mediaPlayerのaddListenerメソッド
  // 引数で渡されたリスナーを Observable が持つシーケンスに追加
  def addListener(listener: Listener) = listeners = listeners :+ listener

}

// Observable オブジェクトに匿名内部クラスとしてリスナーオブジェクトを追加
//Observable.addListener(new Listener {
//  override def changed(newValue: Int): Unit = println(s"${newValue}に変わったよ")
//})
//
//Observable.increment()
//Observable.increment()
//Observable.increment()