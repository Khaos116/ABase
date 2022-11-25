package cc.ab.base.utils

import kotlinx.coroutines.*
import kotlin.math.pow

/**
 * @Description 少量数组的组合计算工具
 * @Author：Khaos
 * @Date：2021-04-16
 * @Time：21:26
 */
object SimpleZuheUtils {
  /**
   * 简单的组合计算(由于循环量太大，所以不适合数据量太大的计算)
   * https://zhenbianshu.github.io/2019/01/charming_alg_permutation_and_combination.html
   */
  inline fun <reified T> combinationAll(list: List<T>, crossinline callBack: (List<List<T>>) -> Unit) {
    CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).launch(context = Dispatchers.Main) {
      withContext(Dispatchers.IO) {
        val result: MutableList<List<T>> = mutableListOf()
        var i = 1
        while (i < 2.0.pow(list.size.toDouble())) {
          val temp: MutableList<T> = mutableListOf()
          for (j in list.indices) if ((i and 2.0.pow(j.toDouble()).toInt()).toDouble() == 2.0.pow(j.toDouble())) temp.add(list[j])
          result.add(temp)
          i++
        }
        result.sortedBy { r -> r.size }
      }.let { r ->
        callBack.invoke(r)
      }
    }
  }

  fun combinationOuZ(list: List<Double>, price: Double = 1.0, callBack: (List<Pair<String, Double>>) -> Unit) {
    combinationAll(list) { lParent ->
      CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).launch(context = Dispatchers.Main) {
        withContext(Dispatchers.IO) {
          val size = list.size
          val result = mutableListOf<Pair<String, Double>>()
          if (size > 1) for (i in 2..size) {
            val lChild = lParent.filter { f -> f.size == i }
            result.add(Pair("${i}C1", (lChild.sumOf { ll -> ll.reduce { acc, d -> acc * d } * price } - lChild.size * price)))
          }
          result.also { r -> r.add(Pair("${size}C${(2.0.pow(size) - size - 1).toInt()}", result.sumOf { m -> m.second })) }
        }.let { r ->
          callBack.invoke(r)
        }
      }
    }
  }

  fun combinationXiangG(list: List<Double>, price: Double = 1.0, callBack: (List<Pair<String, Double>>) -> Unit) {
    combinationAll(list) { lParent ->
      CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).launch(context = Dispatchers.Main) {
        withContext(Dispatchers.IO) {
          val size = list.size
          val result = mutableListOf<Pair<String, Double>>()
          if (size > 1) for (i in 2..size) {
            val lChild = lParent.filter { f -> f.size == i }
            result.add(Pair("${i}C1", (lChild.sumOf { ll -> ll.reduce { acc, d -> acc * d } * price })))
          }
          result.also { r -> r.add(Pair("${size}C${(2.0.pow(size) - size - 1).toInt()}", result.sumOf { m -> m.second })) }
        }.let { r ->
          callBack.invoke(r)
        }
      }
    }
  }
}