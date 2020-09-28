package cc.ab.base.ext

import java.util.Locale
import java.util.regex.Pattern

/**
 * Author:CASE
 * Date:2020-9-28
 * Time:19:01
 */

inline fun String?.isVideoUrl(): Boolean {
  return if (this.isNullOrEmpty()) {
    false
  } else if (!this.toLowerCase(Locale.getDefault()).startsWith("http", true)) {
    false
  } else {
    Pattern.compile(".*?(avi|rmvb|rm|asf|divx|mpg|mpeg|mpe|wmv|mp4|mkv|vob)")
        .matcher(this.toLowerCase(Locale.getDefault())).matches()
  }
}

inline fun String?.isLiveUrl(): Boolean {
  return if (this.isNullOrEmpty()) {
    false
  } else {
    this.toLowerCase(Locale.getDefault()).run {
      startsWith("rtmp") || startsWith("rtsp")
    }
  }
}