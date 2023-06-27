package cc.abase.demo.bean.readhub

/**
 * Author:Khaos116
 * Date:2022/3/8
 * Time:16:19
 */
class TopicBeanParent(
  var totalItems: Int = 0,
  var startIndex: Int = 0,
  var pageIndex: Int = 0,
  var itemsPerPage: Int = 0,
  var currentItemCount: Int = 0,
  var totalPages: Int = 0,
  val items: MutableList<TopicBean>? = null
)

class TopicBean(
  val uid: String? = null,//8qX87kQF3fP
  val title: String? = null,//左旋星获数千万元PreA+轮融资
  val summary: String? = null,//细胞基因治疗和核酸药物CDMO公司左旋星生物已获得数千万元PreA+轮融资，由恒旭资本独家投资 ... 此前，公司曾获得经纬创投的Pre-A轮融资，以及济峰资本和薄荷天使基金的天使轮投资 ... 左旋星成立于2022年3月，专注于细胞及基因治疗（CGT）和核酸药物临床产品及疗法检测与生产服务，开发针对mRNA、病毒载体、CAR-T等治疗产品的临床检测服务，并开发质粒及mRNA的GMP级别生产工艺及过程产品质控方法，以加快基因细胞治疗药物和疗法的进程。
  val createdAt: String? = null,//2023-06-27T06:46:11.706Z
  val newsAggList: MutableList<TopicTitleBean>? = null,
)

class TopicTitleBean(
  val uid: String? = null,// 8qXuCMWpJcc
  val url: String? = null,// https://www.chinaventure.com.cn/news/111-20230627-375881.html
  val title: String? = null,// 左旋星获数千万元PreA+轮融资
  val siteNameDisplay: String? = null,// 投中网
  val statementType: String? = null,// 1
  val statementTypeDisplay: String? = null,// 事实
)