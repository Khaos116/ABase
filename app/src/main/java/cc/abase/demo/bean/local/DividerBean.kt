package cc.abase.demo.bean.local

import android.graphics.Color
import com.blankj.utilcode.util.SizeUtils

/**
 * Author:Khaos
 * Date:2020/8/13
 * Time:22:31
 */
data class DividerBean(
  val heightPx: Int = SizeUtils.dp2px(1f),
  val bgColor: Int = Color.parseColor("#ebebeb")
)