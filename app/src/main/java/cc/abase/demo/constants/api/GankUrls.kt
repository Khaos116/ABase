package cc.abase.demo.constants.api

/**
 * Description:干货集中营相关开发api地址
 * @author: Khaos
 * @date: 2019/10/8 18:48
 */
object GankUrls {
  const val HOST = "gank.io" //Gank的Host
  const val baseUrl = "https://gank.io/" //Gank的Host

  const val ANDROID = "${baseUrl}api/v2/data/category/GanHuo/type/Android/page/%s/count/%s" //{page}/{pageSize}

  //https://gank.io/api/v2/data/category/Girl/type/Girl/page/1/count/10
  const val GIRL = "${baseUrl}api/v2/data/category/Girl/type/Girl/page/%s/count/%s" //{page}/{pageSize}
}