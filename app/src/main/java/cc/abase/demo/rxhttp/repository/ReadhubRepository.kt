package cc.abase.demo.rxhttp.repository

import cc.abase.demo.bean.readhub.TopicBean
import cc.abase.demo.constants.TimeConstants
import cc.abase.demo.constants.api.ReadhubUrl
import rxhttp.cc.RxHttp
import rxhttp.cc.toAwaitResponseReadhub
import rxhttp.wrapper.cache.CacheMode
import rxhttp.wrapper.coroutines.Await

/**
 * Description:
 * @author: Khaos
 * @date: 2022年3月8日16:05:42
 */
object ReadhubRepository : BaseRepository() {
  //获得热门话题
  suspend fun getTopicList(lastTopicId: String = "", readCache: Boolean = true): Await<MutableList<TopicBean>> {
    return RxHttp.get(ReadhubUrl.Home.TOPIC)
      .addAll(getBaseParams().apply {
        put("size", "20")
        if (lastTopicId.isNotBlank()) put("max_topic_id", lastTopicId)
      })
      .setCacheValidTime(TimeConstants.DYN_CACHE) //设置缓存时长
      .setCacheMode(if (readCache && lastTopicId.isBlank()) CacheMode.READ_CACHE_FAILED_REQUEST_NETWORK else CacheMode.ONLY_NETWORK) //先请求数据，失败再读取缓存
      .toAwaitResponseReadhub()
  }
}