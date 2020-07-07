package cc.abase.demo.bean.wan

/**
 * Description:积分
 * @author: caiyoufei
 * @date: 2019/10/12 20:06
 */
data class IntegralBean(
    var coinCount: Int = 0,
    var rank: Int = 0,
    var userId: Long = 0,
    var username: String?,
    var level: String?
)