package cc.abase.demo.utils

import com.blankj.utilcode.util.LanguageUtils
import java.util.*

/**
 * zh-CN → zh-CHS → zh-Hans → zh → Invariant
 * zh-TW → zh-CHT → zh-Hant → zh → Invariant
 * https://www.cnblogs.com/jacksoft/p/5771130.html
 * 加载WebVie后变回了系统默认语言的坑 https://blog.csdn.net/ganduwei/article/details/88371004
 * Author:Khaos
 * Date:2021/11/19
 * Time:12:51
 */
object MyLanguageUtils {
  //判断中文
  private val chinaLanguages = mutableListOf(
    //中划线
    "zh-CN",//中国
    "zh-CHS",//单一化
    "zh-Hans",
    //下划线
    "zh_CN",
    "zh_CHS",
    "zh_Hans",
  )

  //判断繁体中文
  private val traditionalChinese = mutableListOf(
    //中划线
    "zh-HK",//香港
    "zh-MO",//澳门
    "zh-TW",//台湾
    "zh-CHT",//传统的
    "zh-SG",//新加坡
    "zh-Hant",
    //下划线
    "zh_HK",
    "zh_MO",
    "zh_TW",
    "zh_CHT",
    "zh_SG",
    "zh_Hant",
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
    return traditionalChinese.any { getCurrentLanguage().lowercase().contains(it.lowercase()) }
  }

  //当前语言是否是中文
  fun isAppChinese(): Boolean {
    return chinaLanguages.any { getCurrentLanguage().lowercase().contains(it.lowercase()) }
  }

  //是否是英文
  fun isEnglish(): Boolean {
    return !isAppTraditionalChinese() && !isAppChinese()
  }
}