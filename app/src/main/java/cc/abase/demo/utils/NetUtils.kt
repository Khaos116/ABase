package cc.abase.demo.utils

import cc.ab.base.ext.xmlToast
import cc.abase.demo.R
import com.blankj.utilcode.util.NetworkUtils

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/1 16:52
 */
object NetUtils {
  //无网络则吐司
  fun checkNetToast(): Boolean {
    if (!NetworkUtils.isConnected()) {
      R.string.no_network.xmlToast()
      return false
    }
    return true
  }
}