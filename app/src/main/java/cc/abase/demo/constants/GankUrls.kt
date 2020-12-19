package cc.abase.demo.constants

/**
 * Description:干货集中营相关开发api地址
 * @author: CASE
 * @date: 2019/10/8 18:48
 */
interface GankUrls {
  companion object {
    const val ANDROID = "v2/data/category/GanHuo/type/Android/page/%s/count/%s"//{page}/{pageSize}
    const val GIRL = "v2/data/category/Girl/type/Girl/page/%s/count/%s"//{page}/{pageSize}
  }
}