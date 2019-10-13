package cc.abase.demo.repository.bean.wan

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/13 17:46
 */
data class BannerBean(
  var desc: String? = null,//"Android高级进阶直播课免费学习",
  var id: Long = 0,//23,
  var imagePath: String? = null,//"https://wanandroid.com/blogimgs/67c28e8c-2716-4b78-95d3-22cbde65d924.jpeg",
  var isVisible: Int = 1,//1,
  var order: Int = 0,//0,
  var title: String? = null,//"Android高级进阶直播课免费学习",
  var type: Int = 0,//0,
  var url: String? = null//"https://url.163.com/4bj"
)