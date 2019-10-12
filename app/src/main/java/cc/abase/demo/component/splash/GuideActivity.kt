package cc.abase.demo.component.splash

import android.content.Context
import android.content.Intent
import android.util.Log
import cc.ab.base.widget.discretescrollview.DSVOrientation
import cc.ab.base.widget.discretescrollview.DiscreteScrollView.OnItemChangedListener
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommActivity
import cc.abase.demo.component.splash.adapter.GuideAdapter
import cc.abase.demo.component.splash.adapter.GuideAdapter.ViewHolder
import cc.abase.demo.constants.ImageUrls
import com.gyf.immersionbar.ktx.immersionBar
import kotlinx.android.synthetic.main.activity_guide.guideDSV
import kotlinx.android.synthetic.main.activity_guide.guideIndicator

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/11 15:11
 */
class GuideActivity : CommActivity() {
  //引导页图片，如果需要，可以在启动页进行预加载
  private val mList = ImageUrls.instance.imgs.take(4)
  //InfiniteScrollAdapter为无限循环的View
  //private val mAdapter: InfiniteScrollAdapter<ViewHolder> = InfiniteScrollAdapter.wrap(GuideAdapter(mList))
  private val mAdapter = GuideAdapter(mList)

  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, GuideActivity::class.java)
      context.startActivity(intent)
    }
  }

  //不默认填充状态栏
  override fun fillStatus() = false

  //状态栏透明
  override fun initStatus() {
    immersionBar { statusBarDarkFont(false) }
  }

  override fun layoutResId() = R.layout.activity_guide

  override fun initView() {
    guideDSV.setOrientation(DSVOrientation.VERTICAL)
    guideDSV.setOffscreenItems(1)
    guideDSV.setItemTransitionTimeMillis(200)
    guideDSV.addOnItemChangedListener(changeListener)
    guideDSV.adapter = mAdapter
    guideIndicator.initDots(mAdapter.itemCount)
    guideIndicator.setDotSelection(0)
    guideIndicator.onSelectListener = { guideDSV.smoothScrollToPosition(it) }
  }

  override fun initData() {
  }

  //滑动选中
  private var changeListener = OnItemChangedListener<ViewHolder> { viewHolder, adapterPostion ->
    //    val position = mAdapter.getRealPosition(adapterPostion)
    val position = adapterPostion
    guideIndicator.setDotSelection(position)
  }
}