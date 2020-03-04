package cc.abase.demo.bean.gank

import cc.abase.demo.bean.gank.GankAndroidBean
import cc.abase.demo.bean.gank.GankIosBean

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/30 20:32
 */
data class GankTodayBean(
  var Android: List<GankAndroidBean>?,
  var iOS: List<GankIosBean>?
)