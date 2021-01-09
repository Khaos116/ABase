package cc.abase.demo.utils

import androidx.annotation.IntRange
import cc.abase.demo.bean.local.VideoBean

/**
 * Description:
 * @author: CASE
 * @date: 2019/12/12 11:49
 */
object VideoRandomUtils {
  //视频资源封面和播放地址
  private var resourceList = mutableListOf(
      Pair(
          "温暖的抱抱 2020-12-31",
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/aa5308fc5285890804986750388/v.f42906.mp4"
      ),
      Pair(
          "赤狐书生 2020-11-01",
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/f904d50d5285890806304637095/v.f42906.mp4"
      ),
      Pair(
          "新神榜：哪吒重生  2020",
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/e1b5eeea5285890806379037311/v.f42906.mp4"
      ),
      Pair(
          "未知嫌疑人 2020-09-10",
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/c3f671d05285890807168094119/v.f42906.mp4"
      ),
      Pair(
          "八佰 2020-08-21",
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/83cc73365285890806554816089/v.f42906.mp4"
      ),
      Pair(
          "花木兰 2020-09-11",
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/2cb008ef5285890807135914942/v.f42906.mp4"
      ),
      Pair(
          "木兰：横空出世 2020-10-03",
          "https://vod.pipi.cn/43903a81vodtransgzp1251246104/2d1fc7685285890807909124510/v.f42906.mp4"
      ),
      Pair(
          "一点就到家 2020-10-04",
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/67c6e6575285890807968082814/v.f42906.mp4"
      ),
      Pair(
          "送你一朵小红花 2020-12-31",
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/720db5425285890808000731940/v.f42906.mp4"
      ),
      Pair(
          "我和我的家乡 2020-10-01",
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/8c2eb53e5285890807999880271/v.f42906.mp4"
      ),
      Pair(
          "假面饭店 2020-09-04",
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/87d0caf85285890807055577675/v.f42906.mp4"
      ),
      Pair(
          "飞奔去月球 2020",
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/bb68c7515285890807928280731/v.f42906.mp4"
      ),
      Pair(
          "夺冠 2020-09-25",
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/84ec486e5285890807863862400/v.f42906.mp4"
      ),
      Pair(
          "姜子牙 2020-10-01",
          "https://vod.pipi.cn/43903a81vodtransgzp1251246104/bbd4f07a5285890808066187974/v.f42906.mp4"
      ),
      Pair(
          "拆弹专家2 2020",
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/6715a2145285890808041382798/v.f42906.mp4"
      ),
      Pair(
          "急先锋 2020-09-30",
          "https://vod.pipi.cn/fe5b84bcvodcq1251246104/658e4b085285890797861659749/f0.mp4"
      ),
      Pair(
          "82号古宅 2020-05-15",
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/ccff07ce5285890807898977876/v.f42906.mp4"
      )
  )

  //获取随机视频数量
  fun getVideoList(@IntRange(from = 0) idStart: Long = 0, @IntRange(from = 1, to = 100) count: Int = 10): MutableList<VideoBean> {
    val result = mutableListOf<VideoBean>()
    for (i in 0 until count) {
      val pair = resourceList[(i + idStart).toInt() % resourceList.size]
      result.add(
          VideoBean(
              id = idStart + i,
              thumb = pair.second,
              url = pair.second,
              title = pair.first
          )
      )
    }
    return result
  }

  //获取视频信息
  fun getVideoPair(index: Int): Pair<String, String> {
    return resourceList[index % resourceList.size]
  }
}