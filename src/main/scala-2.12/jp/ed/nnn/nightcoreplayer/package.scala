package jp.ed.nnn

import javafx.util.Duration

package object nightcoreplayer {

  def formatTime(elapsed: Duration): String = {
    "%02d:%02d:%02d".format(
      elapsed.toHours.toInt,
      elapsed.toMinutes.toInt % 60,
      elapsed.toSeconds.toInt % 60
    )
  }

  def formatTime(elapsed: Duration, duration: Duration): String =
    s"${formatTime(elapsed)}/${formatTime(duration)}"

}
