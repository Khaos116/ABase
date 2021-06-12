package cc.abase.demo.bean.local

/**
 * Author:Khaos
 * Date:2020/8/21
 * Time:19:49
 */
data class EmptyErrorBean(
  val isError: Boolean = true,
  val isEmpty: Boolean = false,
  val msg: String? = ""
)