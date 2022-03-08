package cc.abase.demo.bean.readhub

/**
 * Author:Khaos116
 * Date:2022/3/8
 * Time:16:19
 */
class TopicBean(
  val id: String? = null,
  val summary: String? = null,
  val updatedAt: String? = null,
  val order: Long = 0,
  val newsArray: MutableList<TopicTitleBean> = mutableListOf(),
)

class TopicTitleBean(
  val id: String? = null,// 3816550,
  val url: String? = null,// "https://www.tmtpost.com/nictation/6030778.html",
  val title: String? = null,// "比亚迪回应王传福卸任杭州比亚迪董事长 ：简化流程，业务无影响",
  val siteName: String? = null,// "钛媒体",
  val mobileUrl: String? = null,// "https://www.tmtpost.com/nictation/6030778.html",
  val autherName: String? = null,// "",
  val duplicateId: String? = null,// 2,
  val publishDate: String? = null,// "2022-03-07T23:40:12.070Z",
  val language: String? = null,// "zh-cn",
  val hasInstantView: String? = null,// true,
  val statementType: String? = null,// 1
)