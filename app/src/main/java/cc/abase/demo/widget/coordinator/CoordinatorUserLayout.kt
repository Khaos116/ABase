package cc.abase.demo.widget.coordinator

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import cc.abase.demo.databinding.MergeCoordinatorUserBinding
import com.angcyo.tablayout.DslTabLayout
import com.blankj.utilcode.util.BarUtils
import com.google.android.material.appbar.AppBarLayout

/**
 * Author:Khaos
 * Date:2020-10-20
 * Time:19:34
 */
class CoordinatorUserLayout @kotlin.jvm.JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : CoordinatorLayout(context, attrs, defStyleAttr) {
  var tabLayout: DslTabLayout? = null
  var appBar: AppBarLayout? = null

  init {
    if (!isInEditMode) {
      val binding = MergeCoordinatorUserBinding.inflate(LayoutInflater.from(context), this)
      tabLayout = binding.coordinatorUserIndicator
      appBar = binding.coordinatorUserAppBar
      //适配状态栏
      (binding.coordinatorUserToolbar.layoutParams as FrameLayout.LayoutParams).topMargin = BarUtils.getStatusBarHeight()
    }
  }
}