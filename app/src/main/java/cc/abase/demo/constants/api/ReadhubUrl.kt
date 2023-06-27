package cc.abase.demo.constants.api

/**
 * https://readhub.cn/
 * Author:Khaos116
 * Date:2022/3/8
 * Time:15:47
 */
object ReadhubUrl {
  const val baseUrl = "https://api.readhub.cn/" //Readhub的Host

  object Home {
    //热门话题
    val TOPIC get() = "${baseUrl}topic"

    //热门话题细节
    val TOPIC_DETAIL get() = "${baseUrl}topic/%s"
  }
}