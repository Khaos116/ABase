package com.iceteck.silicompressorr

/**
 * Description:
 * @author: CASE
 * @date: 2020/5/5 16:56
 */
open class CompressCall private constructor() {
  private object SingletonHolder {
    val holder = CompressCall()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //外部回调
  var progressCall: ((filPath: String, progress: Float) -> Unit)? = null

  //防止回调太频繁
  private var lastProgress = 0f
  //临时变量
  private var tempProgress = 0f

  //更新压缩进度
  fun updateCompressProgress(originFilePath: String, progress: Float) {
    tempProgress = ((progress * 10).toInt()) / 10f//保留一位小数
    if (lastProgress != tempProgress) {
      lastProgress = tempProgress
      // StringExtKt.logE("压缩进度=$lastProgress")
      progressCall?.invoke(originFilePath, Math.min(lastProgress, 99.9f))
    }
  }

  //释放
  fun release() {
    progressCall = null
    lastProgress = 0f
    tempProgress = 0f
  }
}