package cc.abase.demo.component.decoration

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommTitleActivity
import com.blankj.utilcode.util.StringUtils
import kotlinx.android.synthetic.main.activity_decoration.decorationIndicator
import kotlinx.android.synthetic.main.activity_decoration.decorationPager

/**
 * Description:分割线展示
 * @author: CASE
 * @date: 2020/2/19 14:20
 */
class DecorationActivity : CommTitleActivity() {

  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, DecorationActivity::class.java)
      context.startActivity(intent)
    }
  }

  private var fragments: MutableList<DecorationFragment> = mutableListOf()

  private var titles: MutableList<String> =
    mutableListOf("TAB1", "TAB2", "TAB3", "TAB4", "TAB5", "TAB6", "TAB7", "TAB8", "TAB9")

  override fun layoutResContentId() = R.layout.activity_decoration

  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.title_decoration))
    fragments.clear()
    fragments.add(DecorationFragment.newInstance(0))
    fragments.add(DecorationFragment.newInstance(1))
    fragments.add(DecorationFragment.newInstance(2))
    fragments.add(DecorationFragment.newInstance(3))
    fragments.add(DecorationFragment.newInstance(4))
    fragments.add(DecorationFragment.newInstance(5))
    fragments.add(DecorationFragment.newInstance(6))
    fragments.add(DecorationFragment.newInstance(7))
    fragments.add(DecorationFragment.newInstance(8))
    decorationPager.adapter = object : FragmentPagerAdapter(supportFragmentManager, 0) {
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
    decorationPager.offscreenPageLimit = fragments.size
    decorationIndicator.setExpand(false)//设置tab宽度为包裹内容还是平分父控件剩余空间，默认值：false,包裹内容
//        .setIndicatorWrapText(true)//设置indicator是与文字等宽还是与整个tab等宽，默认值：true,与文字等宽
      .setTabWidth(60, 12)//设置固定的指示器宽度和圆角
      .setIndicatorColor(Color.parseColor("#333333"))//indicator颜色
      .setIndicatorHeight(24)//indicator高度
//        .setShowUnderline(false, getResColor(R.color.c_a28dff), 2)//设置是否展示underline，默认不展示
//        .setShowDivider(false, getResColor(R.color.c_a28dff), 10, 1)//设置是否展示分隔线，默认不展示
      .setTabTextSize(14)//文字大小
      .setTabTextColor(Color.parseColor("#999999"))//文字颜色
      .setTabTypeface(null)//字体
      .setTabTypefaceStyle(Typeface.NORMAL)//字体样式：粗体、斜体等
      .setTabBackgroundResId(0)//设置tab的背景
      .setTabPadding(16)//设置tab的左右padding
      .setSelectedTabTextSize(14)//被选中的文字大小
      .setSelectedTabTextColor(Color.parseColor("#FFFFFF"))//被选中的文字颜色
      .setSelectedTabTypeface(null)
      .setSelectedTabTypefaceStyle(Typeface.NORMAL)
      .setTabTransY(-13f)//导航器偏移量  总高度为46,导航器高度20，默认在底部，所以偏移量为13
      .setTextTransY(-1f)//tab文字偏移量
      .setScrollOffset(120)//滚动偏移量
    decorationIndicator.setViewPager(decorationPager)
  }

  override fun initData() {

  }

}