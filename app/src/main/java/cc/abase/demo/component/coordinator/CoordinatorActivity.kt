package cc.abase.demo.component.coordinator

import android.animation.ArgbEvaluator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import cc.ab.base.ext.*
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommActivity
import cc.abase.demo.component.simple.SimpleFragment
import cc.abase.demo.utils.RandomName
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_coordinator.*
import kotlinx.android.synthetic.main.layout_user_head.view.*
import kotlinx.android.synthetic.main.merge_coordinator_user.view.coordinatorUserCTL
import kotlinx.android.synthetic.main.merge_coordinator_user.view.coordinatorUserToolbar
import kotlin.math.abs
import kotlin.math.min

/**
 * Description:
 * @author: CASE
 * @date: 2019/12/6 10:16
 */
class CoordinatorActivity : CommActivity() {
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

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.activity_coordinator
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="状态栏操作">
  override fun fillStatus() = false
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  @Suppress("DEPRECATION")
  override fun initView() {
    //初始化页面
    (coordinatorBack.layoutParams as MarginLayoutParams).topMargin = mStatusBarHeight
    val type = (System.currentTimeMillis() % 2).toInt()
    titles.clear()
    fragments.clear()
    titles.add(if (type == 0) "Normal1" else "Smart1")
    titles.add(if (type == 0) "Normal2" else "Smart2")
    fragments = mutableListOf(SimpleFragment.newInstance(type), SimpleFragment.newInstance(type))
    coordinatorPager.adapter = object : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
      override fun getItem(position: Int) = fragments[position]

      override fun getCount() = fragments.size

      override fun getPageTitle(position: Int) = titles[position]
    }
    //加载页面
    coordinatorCoordinator.indicator?.let {
      it.setExpand(true) //设置tab宽度为包裹内容还是平分父控件剩余空间，默认值：false,包裹内容
          .setIndicatorWrapText(true) //设置indicator是与文字等宽还是与整个tab等宽，默认值：true,与文字等宽
          .setIndicatorHeight(4)
          .setTabRound(2) //设置固定的指示器圆角
          .setIndicatorColor(R.color.yellow_FF6100.xmlToColor()) //indicator颜色
          .setTabTextSize(15) //文字大小
          .setTabTextColor(Color.parseColor("#87827D")) //文字颜色
          .setTabTypeface(null) //字体
          .setTabTypefaceStyle(Typeface.NORMAL) //字体样式：粗体、斜体等
          .setTabBackgroundResId(0) //设置tab的背景
          .setTabPadding(12) //设置tab的左右padding
          .setParentPadding(4)
          .setSelectedTabTextSize(17) //被选中的文字大小
          .setSelectedTabTextColor(Color.parseColor("#08080D")) //被选中的文字颜色
          .setSelectedTabTypeface(null)
          .setSelectedTabTypefaceStyle(Typeface.BOLD)
          .setTabTransY(-4f) //导航器偏移量
          .setTextTransY(0f) //tab文字偏移量
          .setScrollOffset(120) //滚动偏移量
      it.setViewPager(coordinatorPager)
    }
    //滑动变化
    coordinatorCoordinator.appBar?.let { bar ->
      bar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
        val percent = min(abs(verticalOffset * 1f) / appBarLayout.totalScrollRange, 1f)
        coordinatorCoordinator.layoutUserOver.alpha = 1 - percent
        coordinatorBack.setColorFilter(mEvaluator.evaluate(percent, Color.WHITE, R.color.gray.xmlToColor()) as Int)
      })
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化Data">
  override fun initData() {
    fillTop()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="顶部数据填充">
  private fun fillTop() {
    val userHomeCoordinator = coordinatorCoordinator
    //返回
    userHomeCoordinator.layoutUserFollowState.pressEffectAlpha()
    userHomeCoordinator.layoutUserFollowState.click {
      if (userHomeCoordinator.layoutUserFollowState.text.toString() == "未关注") {
        userHomeCoordinator.layoutUserFollowState.text = "已关注"
      } else {
        userHomeCoordinator.layoutUserFollowState.text = "未关注"
      }
    }
    //标题(layoutCircleName是为了找到expandedTitle的位置使用)
    val nickname = RandomName.randomName(Math.random() > 0.5, if (Math.random() > 0.5) 4 else 3)
    userHomeCoordinator.layoutUserName.text = nickname
    when (System.currentTimeMillis() % 3) {
      0L -> ContextCompat.getDrawable(mContext, R.drawable.sex_man)
      1L -> ContextCompat.getDrawable(mContext, R.drawable.sex_woman)
      else -> null
    }.let { d ->
      userHomeCoordinator.layoutUserName.setCompoundDrawablesWithIntrinsicBounds(null, null, d, null)
    }
    userHomeCoordinator.coordinatorUserToolbar.title = nickname
    //找到位置
    userHomeCoordinator.layoutUserName.post {
      val arr1 = intArrayOf(0, 0)
      val arr2 = intArrayOf(0, 0)
      userHomeCoordinator.layoutUserName.getLocationInWindow(arr1)
      userHomeCoordinator.layoutUserBg.getLocationInWindow(arr2)
      userHomeCoordinator.coordinatorUserCTL.expandedTitleMarginTop = arr1[1] - arr2[1] - mStatusBarHeight
      userHomeCoordinator.coordinatorUserCTL.expandedTitleMarginStart = arr1[0] - arr2[0]
    }
    //背景和封面
    userHomeCoordinator.layoutUserBg.setImageResource(R.drawable.bg_user_top)
    userHomeCoordinator.layoutUserHead.setImageResource(R.drawable.head_placeholder_border)
    userHomeCoordinator.layoutUserHead.pressEffectAlpha(0.9f)
    //数量
    userHomeCoordinator.layoutUserCircleNum.setNumberNo00((Math.random() * 10000).toInt() + 30.0)
    userHomeCoordinator.layoutUserFollowNum.setNumberNo00((Math.random() * 10000).toInt() + 30.0)
    userHomeCoordinator.layoutUserFansNum.setNumberNo00((Math.random() * 10000).toInt() + 30.0)
    userHomeCoordinator.layoutUserCircleNumClick.click { "圈子".toast() }
    userHomeCoordinator.layoutUserFollowNumClick.click { "关注".toast() }
    userHomeCoordinator.layoutUserFansNumClick.click { "粉丝".toast() }
  }
  //</editor-fold>
}