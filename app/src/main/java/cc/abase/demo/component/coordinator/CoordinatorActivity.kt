package cc.abase.demo.component.coordinator

import android.animation.ArgbEvaluator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import cc.ab.base.ext.*
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommBindActivity
import cc.abase.demo.component.simple.SimpleFragment
import cc.abase.demo.databinding.*
import cc.abase.demo.utils.RandomName
import cc.abase.demo.widget.indictor.ViewPager1Delegate
import com.angcyo.tablayout.DslTabLayout
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs
import kotlin.math.min

/**
 * Description:
 * @author: Khaos
 * @date: 2019/12/6 10:16
 */
class CoordinatorActivity : CommBindActivity<ActivityCoordinatorBinding>() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, CoordinatorActivity::class.java)
      context.startActivity(intent)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  private val mEvaluator = ArgbEvaluator()
  private var fragments: MutableList<Fragment> = mutableListOf()
  private var titles: MutableList<String> = mutableListOf()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="是否显示默认状态栏占位">
  override fun showHolderStatusView() = false
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  @Suppress("DEPRECATION")
  override fun initView() {
    //初始化页面
    (viewBinding.coordinatorBack.layoutParams as MarginLayoutParams).topMargin = mStatusBarHeight
    viewBinding.coordinatorBack.pressEffectAlpha()
    viewBinding.coordinatorBack.click { onBackPressed() }
    val type = (System.currentTimeMillis() % 2).toInt()
    titles.clear()
    fragments.clear()
    val size = 3 + (Math.random() * 4).toInt()
    for (i in 1..size) {
      titles.add((if (type == 0) "Normal" else "Smart") + i)
      fragments.add(SimpleFragment.newInstance(type))
    }
    viewBinding.coordinatorPager.adapter = object : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
      override fun getItem(position: Int) = fragments[position]

      override fun getCount() = fragments.size

      override fun getPageTitle(position: Int) = titles[position]
    }
    viewBinding.coordinatorPager.offscreenPageLimit = fragments.size
    //配置属性 https://github.com/angcyo/DslTabLayout/wiki/%E5%B1%9E%E6%80%A7%E5%A4%A7%E5%85%A8
    viewBinding.coordinatorCoordinator.tabLayout?.configTabLayoutConfig {
      tabDeselectColor = cc.ab.base.R.color.gray_808A87.xmlToColor()//未选中文字颜色
      tabSelectColor = cc.ab.base.R.color.yellow_FF6100.xmlToColor()//已选中文字颜色
      tabTextMinSize = 15.dp2px() * 1f//未选中文字大小
      tabTextMaxSize = 17.dp2px() * 1f//已选中文字大小
      tabEnableGradientColor = true
      tabEnableTextBold = true
    }
    //添加Tab
    viewBinding.coordinatorCoordinator.tabLayout?.let { tab ->
      tab.itemAutoEquWidth = true//智能判断Item是否等宽
      val pad = 8.dp2px()
      for (s in titles) {
        val tv = TextView(mContext)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        tv.setTextColor(cc.ab.base.R.color.gray_808A87.xmlToColor())
        tv.gravity = Gravity.CENTER
        tv.includeFontPadding = true
        tv.text = s
        tv.setPadding(pad, 0, pad, 0)
        tab.addView(tv, DslTabLayout.LayoutParams(-2, -1))
      }
      ViewPager1Delegate.install(viewBinding.coordinatorPager, tab)
    }
    val bindingCoordinator = MergeCoordinatorUserBinding.bind(viewBinding.coordinatorCoordinator)
    val bindingUser = LayoutUserHeadBinding.bind(bindingCoordinator.coordinatorUserHead)
    //滑动变化
    viewBinding.coordinatorCoordinator.appBar?.let { bar ->
      bar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
        val percent = min(abs(verticalOffset * 1f) / appBarLayout.totalScrollRange, 1f)
        bindingUser.layoutUserOver.alpha = 1 - percent
        viewBinding.coordinatorBack.setColorFilter(mEvaluator.evaluate(percent, Color.WHITE, cc.ab.base.R.color.gray.xmlToColor()) as Int)
      })
    }
    fillTop(bindingCoordinator, bindingUser)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="顶部数据填充">
  private fun fillTop(bindingCoordinator: MergeCoordinatorUserBinding, bindingUser: LayoutUserHeadBinding) {
    //返回
    bindingUser.layoutUserFollowState.pressEffectAlpha()
    bindingUser.layoutUserFollowState.click {
      if (bindingUser.layoutUserFollowState.text.toString() == R.string.未关注.xmlToString()) {
        bindingUser.layoutUserFollowState.text = R.string.已关注.xmlToString()
      } else {
        bindingUser.layoutUserFollowState.text = R.string.未关注.xmlToString()
      }
      bindingUser.layoutUserFollowState.text.toast()
    }
    //标题(layoutCircleName是为了找到expandedTitle的位置使用)
    val nickname = RandomName.randomName(Math.random() > 0.5, if (Math.random() > 0.5) 4 else 3)
    bindingUser.layoutUserName.text = nickname
    when (System.currentTimeMillis() % 3) {
      0L -> ContextCompat.getDrawable(mContext, R.drawable.sex_man)
      1L -> ContextCompat.getDrawable(mContext, R.drawable.sex_woman)
      else -> null
    }.let { d ->
      bindingUser.layoutUserName.setCompoundDrawablesWithIntrinsicBounds(null, null, d, null)
    }
    bindingCoordinator.coordinatorUserToolbar.title = nickname
    //找到位置
    bindingUser.layoutUserName.post {
      val arr1 = intArrayOf(0, 0)
      val arr2 = intArrayOf(0, 0)
      bindingUser.layoutUserName.getLocationInWindow(arr1)
      bindingUser.layoutUserBg.getLocationInWindow(arr2)
      bindingCoordinator.coordinatorUserCTL.expandedTitleMarginTop = arr1[1] - arr2[1] - mStatusBarHeight
      bindingCoordinator.coordinatorUserCTL.expandedTitleMarginStart = arr1[0] - arr2[0]
    }
    //背景和封面
    bindingUser.layoutUserBg.setImageResource(R.drawable.bg_user_top)
    bindingUser.layoutUserHead.setImageResource(R.drawable.head_placeholder_border)
    bindingUser.layoutUserHead.pressEffectAlpha(0.9f)
    //数量
    bindingUser.layoutUserCircleNum.setNumberNo00((Math.random() * 10000).toInt() + 30.0)
    bindingUser.layoutUserFollowNum.setNumberNo00((Math.random() * 10000).toInt() + 30.0)
    bindingUser.layoutUserFansNum.setNumberNo00((Math.random() * 10000).toInt() + 30.0)
    bindingUser.layoutUserCircleNumClick.click { R.string.圈子.xmlToast() }
    bindingUser.layoutUserFollowNumClick.click { R.string.关注.xmlToast() }
    bindingUser.layoutUserFansNumClick.click { R.string.粉丝.xmlToast() }
    //是否不允许上滑展开
    val noScroll = System.currentTimeMillis() < 0
    (bindingCoordinator.coordinatorUserCTL.layoutParams as? AppBarLayout.LayoutParams)?.let { p ->
      if (noScroll) {
        bindingCoordinator.coordinatorUserAppBar.setExpanded(true)
        p.scrollFlags = 0
      } else {
        p.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
            AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP or
            AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
      }
      bindingCoordinator.coordinatorUserCTL.layoutParams = p
    }
  }
  //</editor-fold>
}