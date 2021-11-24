package cc.abase.demo.ext

import cc.ab.base.ext.toast
import cc.abase.demo.constants.MyErrorCode
import rxhttp.wrapper.exception.HttpStatusCodeException
import rxhttp.wrapper.exception.ParseException

/**
 * Author:Khaos
 * Date:2021/11/24
 * Time:15:14
 */

//吐司异常
fun Throwable?.toast2() {
  //已经全局处理的异常则不再吐司
  if (this is ParseException && this.errorCode == MyErrorCode.ALREADY_DEAL.toString()) {
    return
  } else if (this is HttpStatusCodeException) {
    this.result?.toast()
    return
  }
}