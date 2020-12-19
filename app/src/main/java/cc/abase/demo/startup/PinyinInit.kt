package cc.abase.demo.startup

import android.content.Context
import androidx.startup.Initializer
import cc.ab.base.ext.logI
import com.github.promeg.pinyinhelper.Pinyin
import com.github.promeg.tinypinyin.lexicons.android.cncity.CnCityDict

/**
 * Author:CASE
 * Date:2020/12/19
 * Time:15:55
 */
class PinyinInit : Initializer<Int> {
  override fun create(context: Context): Int {
    // 添加中文城市词典
    Pinyin.init(Pinyin.newConfig().with(CnCityDict.getInstance(context)))
    "初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(BuglyInit::class.java)
  }
}