package cc.abase.demo.bean.local

/**
 * Description:
 * @author: CASE
 * @date: 2020/4/21 14:54
 */
data class UserScoreBean(
    val scores: MutableList<Int> = mutableListOf(
        (Math.random() * 150).toInt() + 1,
        (Math.random() * 150).toInt() + 1,
        (Math.random() * 150).toInt() + 1,
        (Math.random() * 100).toInt() + 1,
        (Math.random() * 100).toInt() + 1,
        (Math.random() * 100).toInt() + 1,
        (Math.random() * 100).toInt() + 1,
        (Math.random() * 100).toInt() + 1,
        (Math.random() * 100).toInt() + 1))