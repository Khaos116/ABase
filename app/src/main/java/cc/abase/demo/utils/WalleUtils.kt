package cc.abase.demo.utils

import cc.abase.demo.BuildConfig
import com.blankj.utilcode.util.Utils
import com.meituan.android.walle.WalleChannelReader

/**
 * @Description
 * @Author：CASE
 * @Date：2021/1/30
 * @Time：15:50
 */
object WalleUtils {
  //当前APP渠道号
  private var mChannel: String = ""

  //读取渠道号(测试版本默认官方，官方默认-1)
  fun getChannel(real: Boolean = false): String {
    if (real) return WalleChannelReader.getChannel(Utils.getApp()) ?: ""
    if (mChannel.isBlank()) {
      mChannel = if (BuildConfig.APP_IS_RELEASE) WalleChannelReader.getChannel(Utils.getApp()) ?: "" else "-1"
    }
    return mChannel
  }
}