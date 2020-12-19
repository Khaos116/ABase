package cc.abase.demo.startup

import android.content.Context
import androidx.startup.Initializer
import cc.ab.base.ext.logI
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.ios.IosEmojiProvider

/**
 * Author:CASE
 * Date:2020/12/19
 * Time:15:51
 */
class EmojiInit : Initializer<Int> {
  override fun create(context: Context): Int {
    //表情
    EmojiManager.install(IosEmojiProvider())
    "初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(SmartInit::class.java)
  }
}