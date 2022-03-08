package cc.abase.demo.constants

import com.blankj.utilcode.constant.TimeConstants

/**
 * Description:
 * @author: Khaos
 * @date: 2020/3/6 10:09
 */
class TimeConstants {
  companion object {
    //缓存时间为30分钟
    const val HOME_CACHE = 30L * TimeConstants.MIN

    //缓存时间为3天分钟
    const val DYN_CACHE = 3L * TimeConstants.DAY
  }
}