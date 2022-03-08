package cc.abase.demo.constants.api

/**
 * Author:BoLuo
 * Date:2022/3/8
 * Time:15:47
 */
object ReadhubUrl {
  const val baseUrl = "https://api.readhub.cn/" //WanAndroid的Host

  object Home {
    //热门话题
    val TOPIC get() = "${baseUrl}topic"

    //热门话题细节
    val TOPIC_DETAIL get() = "${baseUrl}topic/%s"
  }
}