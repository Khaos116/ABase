package cc.abase.demo.utils

import androidx.annotation.IntRange
import cc.abase.demo.repository.bean.local.VideoBean

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/12/12 11:49
 */
class VideoRandomUtils private constructor() {
  private object SingletonHolder {
    val holder = VideoRandomUtils()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //视频资源封面和播放地址
  private var resourceList = mutableListOf(
    Pair(
      "https://cms-bucket.nosdn.127.net/eb411c2810f04ffa8aaafc42052b233820180418095416.jpeg",
      "http://vfx.mtime.cn/Video/2019/02/04/mp4/190204084208765161.mp4"
    ),
    Pair(
      "https://cms-bucket.nosdn.127.net/cb37178af1584c1588f4a01e5ecf323120180418133127.jpeg",
      "http://vfx.mtime.cn/Video/2019/03/21/mp4/190321153853126488.mp4"
    ),
    Pair(
      "https://cms-bucket.nosdn.127.net/eb411c2810f04ffa8aaafc42052b233820180418095416.jpeg",
      "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319222227698228.mp4"
    ),
    Pair(
      "https://cms-bucket.nosdn.127.net/cb37178af1584c1588f4a01e5ecf323120180418133127.jpeg",
      "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4"
    ),
    Pair(
      "https://cms-bucket.nosdn.127.net/eb411c2810f04ffa8aaafc42052b233820180418095416.jpeg",
      "http://vfx.mtime.cn/Video/2019/03/18/mp4/190318231014076505.mp4"
    ),
    Pair(
      "https://cms-bucket.nosdn.127.net/cb37178af1584c1588f4a01e5ecf323120180418133127.jpeg",
      "http://vfx.mtime.cn/Video/2019/03/18/mp4/190318214226685784.mp4"
    ),
    Pair(
      "https://cms-bucket.nosdn.127.net/eb411c2810f04ffa8aaafc42052b233820180418095416.jpeg",
      "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319104618910544.mp4"
    ),
    Pair(
      "https://cms-bucket.nosdn.127.net/cb37178af1584c1588f4a01e5ecf323120180418133127.jpeg",
      "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319125415785691.mp4"
    ),
    Pair(
      "https://cms-bucket.nosdn.127.net/eb411c2810f04ffa8aaafc42052b233820180418095416.jpeg",
      "http://vfx.mtime.cn/Video/2019/03/17/mp4/190317150237409904.mp4"
    ),
    Pair(
      "https://cms-bucket.nosdn.127.net/cb37178af1584c1588f4a01e5ecf323120180418133127.jpeg",
      "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4"
    ),
    Pair(
      "https://cms-bucket.nosdn.127.net/eb411c2810f04ffa8aaafc42052b233820180418095416.jpeg",
      "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314102306987969.mp4"
    ),
    Pair(
      "https://cms-bucket.nosdn.127.net/cb37178af1584c1588f4a01e5ecf323120180418133127.jpeg",
      "http://vfx.mtime.cn/Video/2019/03/13/mp4/190313094901111138.mp4"
    ),
    Pair(
      "https://cms-bucket.nosdn.127.net/eb411c2810f04ffa8aaafc42052b233820180418095416.jpeg",
      "http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4"
    ),
    Pair(
      "https://cms-bucket.nosdn.127.net/cb37178af1584c1588f4a01e5ecf323120180418133127.jpeg",
      "http://vfx.mtime.cn/Video/2019/03/12/mp4/190312083533415853.mp4"
    )
  )

  //获取随机视频数量
  fun getVideoList(
    @IntRange(from = 0) idStart: Long = 0, @IntRange(from = 10, to = 100) count: Int = 20
  ): MutableList<VideoBean> {
    val result = mutableListOf<VideoBean>()
    for (i in 0 until count) {
      val pair = resourceList[(i + idStart).toInt() % resourceList.size]
      result.add(
        VideoBean(
          id = idStart + i,
          thumb = pair.first,
          url = pair.second,
          title = "这是第${i + idStart}个视频"
        )
      )
    }
    return result
  }
}