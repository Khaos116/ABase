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
    //热门话题 https://api.readhub.cn/topic/list?max_topic_id=8qVW9wrNq5U&size=20
    val TOPIC get() = "${baseUrl}topic/list"
  }
}