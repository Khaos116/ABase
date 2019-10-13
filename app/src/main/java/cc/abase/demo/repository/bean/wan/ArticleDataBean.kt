package cc.abase.demo.repository.bean.wan

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/13 18:08
 */
data class ArticleDataBean(
  var curPage: Int = 0,
  var offset: Int = 0,// 0,
  var over: Boolean = false,//false,
  var pageCount: Int = 0,//362,
  var size: Int = 20,//20,
  var total: Int = 0,//7240
  val datas: MutableList<ArticleBean>? = null
) {
  fun hasMore(): Boolean {
    return curPage == pageCount
  }
}