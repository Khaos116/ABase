package cc.abase.demo.component.coordinator

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import cc.ab.base.ext.invisible
import cc.ab.base.ext.visible
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommActivity
import cc.abase.demo.component.simple.SimpleFragment
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_coordinator.coordinatorPager
import kotlinx.android.synthetic.main.activity_coordinator.coordinatorUserLayout

/**
 * Description:
 * @author: CASE
 * @date: 2019/12/6 10:16
 */
class CoordinatorActivity : CommActivity() {
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, CoordinatorActivity::class.java)
      context.startActivity(intent)
    }
  }

  private var fragments: MutableList<Fragment> = mutableListOf()
  private var titles: MutableList<String> = mutableListOf()

  override fun fillStatus() = false

  override fun layoutResId() = R.layout.activity_coordinator

  override fun initView() {
    //初始化页面
    val type = (System.currentTimeMillis() % 2).toInt()
    titles.clear()
    fragments.clear()
    titles.add(if (type == 0) "Normal1" else "Smart1")
    titles.add(if (type == 0) "Normal2" else "Smart2")
    fragments = mutableListOf(
      SimpleFragment.newInstance(type),
      SimpleFragment.newInstance(type)
    )
    coordinatorPager.adapter = object : FragmentPagerAdapter(supportFragmentManager, 0) {
      override fun getItem(position: Int): Fragment {
        return fragments[position]
      }

      override fun getCount(): Int {
        return fragments.size
      }

      override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
      }
    }
    //加载页面
    coordinatorUserLayout.indicator?.let {
      it.setExpand(true)//设置tab宽度为包裹内容还是平分父控件剩余空间，默认值：false,包裹内容
        .setIndicatorWrapText(false)//设置indicator是与文字等宽还是与整个tab等宽，默认值：true,与文字等宽
//        .setTabWidth(40, 10)//设置固定的指示器宽度和圆角
        .setIndicatorColor(Color.parseColor("#AF2121"))//indicator颜色
        .setIndicatorHeight(2)//indicator高度
        .setShowUnderline(
          true, Color.parseColor("#eeeeee"), 1f
        )//设置是否展示underline，默认不展示
//        .setShowDivider(false, getResColor(R.color.c_a28dff), 10, 1)//设置是否展示分隔线，默认不展示
        .setTabTextSize(15)//文字大小
        .setTabTextColor(Color.parseColor("#999999"))//文字颜色
        .setTabTypeface(null)//字体
        .setTabTypefaceStyle(Typeface.BOLD)//字体样式：粗体、斜体等
        .setTabBackgroundResId(0)//设置tab的背景
        .setTabPadding(16)//设置tab的左右padding
        .setSelectedTabTextSize(15)//被选中的文字大小
        .setSelectedTabTextColor(Color.parseColor("#AF2121"))//被选中的文字颜色
        .setSelectedTabTypeface(null)
        .setSelectedTabTypefaceStyle(Typeface.BOLD)
        .setTabTransY(0f)//导航器偏移量
        .setTextTransY(3f)//tab文字偏移量
        .setScrollOffset(120)//滚动偏移量
      it.setViewPager(coordinatorPager)
    }
    coordinatorUserLayout.appBar?.let { bar ->
      bar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
        if (verticalOffset == 0) {
          //展开状态
          coordinatorUserLayout.titleText?.invisible()
        } else if (Math.abs(verticalOffset) >= appBarLayout.totalScrollRange) {
          //折叠状态
          coordinatorUserLayout.titleText?.visible()
        } else {
          //中间状态
          if (Math.abs(verticalOffset) < bar.totalScrollRange / 2f) {
            coordinatorUserLayout.titleText?.invisible()
          } else {
            coordinatorUserLayout.titleText?.visible()
          }
        }
      })
    }
  }

  override fun initData() {
  }
}