package cc.abase.demo.bean.local

import cc.abase.demo.utils.RandomName

/**
 * Description:
 * @author: CASE
 * @date: 2020/4/21 14:55
 */
data class UserStickyBean(
    val name: String = RandomName.randomName(Math.random() > 0.5, if (Math.random() > 0.5) 4 else 3),
    val title: Boolean = false,
    val score: UserScoreBean? = null)