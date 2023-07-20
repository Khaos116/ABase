package cc.abase.demo.utils

import com.blankj.utilcode.util.LanguageUtils
import java.util.*

/**
 * zh-CN → zh-CHS → zh-Hans → zh → Invariant
 * zh-TW → zh-CHT → zh-Hant → zh → Invariant
 * https://www.cnblogs.com/jacksoft/p/5771130.html
 * 加载WebVie后变回了系统默认语言的坑 https://blog.csdn.net/ganduwei/article/details/88371004
 * 2023语言切换：https://it.cha138.com/android/show-4125028.html
 * Author:Khaos
 * Date:2021/11/19
 * Time:12:51
 */
object MyLanguageUtils {
  //判断中文(Hans简体)
  private val chinaLanguages = mutableListOf(
    "zh-",
    "zh_",
  )

  //判断繁体(Hant繁体)
  private val traditionalChinese = mutableListOf(
    //中划线
    "-HK",//香港
    "-MO",//澳门
    "-TW",//台湾
    "-CHT",//传统的
    "-SG",//新加坡
    "-Hant",
    //下划线
    "_HK",
    "_MO",
    "_TW",
    "_CHT",
    "_SG",
    "_Hant",
    "#Hant",
  )

  //获取APP当前语言
  private fun getCurrentLanguage(): String {
    return MMkvUtils.getCurrentLanguage()
  }

  //设置语言
  fun setCurrentLanguage(locale: Locale) {
    MMkvUtils.setCurrentLanguage(locale.toString().lowercase())
    LanguageUtils.updateAppContextLanguage(locale, null)
    LanguageUtils.applyLanguage(locale)
  }

  //是否是繁体中文
  fun isAppTraditionalChinese(): Boolean {
    return isAppChinese() && traditionalChinese.any { getCurrentLanguage().lowercase().contains(it.lowercase()) }
  }

  //当前语言是否是简体中文
  fun isAppChinese(): Boolean {
    return chinaLanguages.any { getCurrentLanguage().lowercase().contains(it.lowercase()) }
  }

  //是否是英文
  fun isEnglish(): Boolean {
    return !isAppChinese()
  }
}