package cc.abase.demo.widget.coordinator

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import cc.ab.base.ext.click
import cc.ab.base.ext.pressEffectAlpha
import cc.abase.demo.R
import cc.abase.demo.widget.SimpleViewpagerIndicator
import com.blankj.utilcode.util.BarUtils
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.merge_coordinator_user.view.coordinatorUserTitleBack
import kotlinx.android.synthetic.main.merge_coordinator_user.view.coordinatorUserToolbar

/**
 * Description:用户个人主页的CoordinatorLayout
 * @author: CASE
 * @date: 2019/11/18 11:34
 */
class CoordinatorUserLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : CoordinatorLayout(context, attrs, defStyleAttr) {

  var indicator: SimpleViewpagerIndicator? = null
  var appBar: AppBarLayout? = null
  var titleText: TextView? = null

  init {
    if (!isInEditMode) {
      LayoutInflater.from(context)
        .inflate(R.layout.merge_coordinator_user, this, true)
      indicator = findViewById(R.id.coordinatorUserIndicator)
      appBar = findViewById(R.id.coordinatorUserAppBar)
      titleText = findViewById(R.id.coordinatorUserTitleName)
      //适配状态栏
      (coordinatorUserToolbar.layoutParams as FrameLayout.LayoutParams)
        .topMargin = BarUtils.getStatusBarHeight()
      //返回按钮
      coordinatorUserTitleBack.pressEffectAlpha()
      coordinatorUserTitleBack.click { (context as Activity).finish() }
    }
  }
}