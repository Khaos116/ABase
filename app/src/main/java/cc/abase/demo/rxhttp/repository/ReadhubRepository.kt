package cc.abase.demo.rxhttp.repository

import cc.abase.demo.bean.readhub.TopicBean
import cc.abase.demo.constants.TimeConstants
import cc.abase.demo.constants.api.ReadhubUrl
import rxhttp.cc.*
import rxhttp.wrapper.cache.CacheMode
import rxhttp.wrapper.coroutines.Await

/**
 * Description:
 * @author: Khaos
 * @date: 2022年3月8日16:05:42
 */
object ReadhubRepository {
  //获得热门话题
  suspend fun getTopicList(lastOrder: Long = 0): Await<MutableList<TopicBean>> {
    return RxHttp.get("${ReadhubUrl.Home.TOPIC}?pageSize=20${if (lastOrder <= 0) "" else "&lastCursor=$lastOrder"}")
      .setCacheValidTime(TimeConstants.DYN_CACHE) //设置缓存时长
      .setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE) //先请求数据，失败再读取缓存
      .toAwaitResponseReadhub()
  }

  //获得热门话题细节
  suspend fun getTopicDetail(topicId: String): Await<TopicBean> {
    return RxHttp.get(String.format(ReadhubUrl.Home.TOPIC_DETAIL, topicId))
      .setCacheValidTime(TimeConstants.DYN_CACHE) //设置缓存时长
      .setCacheMode(CacheMode.READ_CACHE_FAILED_REQUEST_NETWORK) //先读取缓存，失败再请求数据
      .toAwaitResponseOther()
  }
}