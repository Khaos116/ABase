package cc.ab.base.utils

import kotlin.math.pow

/**
 * @Description 少量数组的组合计算工具
 * @Author：CASE
 * @Date：2021-04-16
 * @Time：21:26
 */
object SimpleZuheUtils {
  /**
   * 简单的组合计算(由于循环量太大，所以不适合数据量太大的计算)
   * https://zhenbianshu.github.io/2019/01/charming_alg_permutation_and_combination.html
   */
  fun combination(list: List<Any>): List<List<Any>> {
    val result: MutableList<List<Any>> = mutableListOf()
    var i = 1
    while (i < 2.0.pow(list.size.toDouble())) {
      val temp: MutableList<Any> = mutableListOf()
      for (j in list.indices) if ((i and 2.0.pow(j.toDouble()).toInt()).toDouble() == 2.0.pow(j.toDouble())) temp.add(list[j])
      result.add(temp)
      i++
    }
    return result
  }
}