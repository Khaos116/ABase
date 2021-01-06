package cc.abase.demo.widget.coordinator

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import cc.abase.demo.R
import cc.abase.demo.widget.SimpleViewpagerIndicator2
import com.blankj.utilcode.util.BarUtils
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.merge_coordinator_user.view.coordinatorUserToolbar

/**
 * Author:CASE
 * Date:2020-10-20
 * Time:19:34
 */
class CoordinatorUserLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CoordinatorLayout(context, attrs, defStyleAttr) {
  var indicator: SimpleViewpagerIndicator2? = null
  var appBar: AppBarLayout? = null

  init {
    if (!isInEditMode) {
      LayoutInflater.from(context).inflate(R.layout.merge_coordinator_user, this, true)
      indicator = findViewById(R.id.coordinatorUserIndicator)
      //适配状态栏
      (coordinatorUserToolbar.layoutParams as FrameLayout.LayoutParams).topMargin = BarUtils.getStatusBarHeight()
      appBar = findViewById(R.id.coordinatorUserAppBar)
    }
  }
}