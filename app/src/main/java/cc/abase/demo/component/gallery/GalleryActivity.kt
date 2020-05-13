package cc.abase.demo.component.gallery

import android.content.Context
import android.content.Intent
import cc.ab.base.ext.click
import cc.ab.base.ext.pressEffectAlpha
import cc.ab.base.widget.discretescrollview.adapter.DiscretePageAdapter
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommActivity
import cc.abase.demo.component.gallery.adapter.GalleryHolderCreator
import com.gyf.immersionbar.ktx.immersionBar
import kotlinx.android.synthetic.main.activity_gallery.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/29 17:07
 */
class GalleryActivity : CommActivity() {

  companion object {
    private const val INTENT_KEY_LIST = "INTENT_KEY_LIST"
    private const val INTENT_KEY_POSITION = "INTENT_KEY_POSITION"
    fun startActivity(
      context: Context,
      list: ArrayList<String>,
      position: Int
    ) {
      val intent = Intent(context, GalleryActivity::class.java)
      intent.putStringArrayListExtra(INTENT_KEY_LIST, list)
      intent.putExtra(INTENT_KEY_POSITION, position)
      context.startActivity(intent)
    }
  }

  //图片列表
  private var mDataList = mutableListOf<String>()
  //当前位置
  private var currentPosition = 1

  override fun fillStatus() = false

  override fun layoutResId() = R.layout.activity_gallery

  override fun onCreateBefore() {
    mDataList = intent.getStringArrayListExtra(INTENT_KEY_LIST)?: mutableListOf<String>()
        .toMutableList()
    currentPosition = intent.getIntExtra(INTENT_KEY_POSITION, 1)
  }

  override fun initStatus() {
    immersionBar {
      fullScreen(true)
      transparentNavigationBar()
      statusBarDarkFont(true)
      navigationBarDarkIcon(true)
    }
  }

  override fun initView() {
    galleryBack.pressEffectAlpha()
    galleryBack.click { finish() }
    galleryRecycler.addOnItemChangedListener { viewHolder, adapterPosition, end ->
      if (!end) galleryNumber.text = String.format("%1\$s/%2\$s", adapterPosition + 1, mDataList.size)
    }
  }

  override fun initData() {
    galleryNumber.text = String.format("%1\$s/%2\$s", currentPosition + 1, mDataList.size)
    galleryRecycler.adapter = DiscretePageAdapter<String>(GalleryHolderCreator(), mDataList)
    galleryRecycler.scrollToPosition(currentPosition)
  }
}