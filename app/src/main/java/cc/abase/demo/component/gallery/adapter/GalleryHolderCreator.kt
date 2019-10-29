package cc.abase.demo.component.gallery.adapter

import android.view.View
import cc.ab.base.ext.load
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolder
import cc.ab.base.widget.discretescrollview.holder.DiscreteHolderCreator
import cc.abase.demo.R
import kotlinx.android.synthetic.main.layout_gallery.view.itemGallery
import me.panpf.sketch.SketchImageView

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/29 17:25
 */
class GalleryHolderCreator : DiscreteHolderCreator {
  override fun createHolder(itemView: View): DiscreteHolder<String> = GalleryHolder(itemView)

  override fun getLayoutId() = R.layout.layout_gallery
}

class GalleryHolder(view: View) : DiscreteHolder<String>(view) {
  private var imageView: SketchImageView? = null
  override fun initView(itemView: View) {
    imageView = itemView.itemGallery
  }

  override fun updateUI(
    data: String?,
    position: Int,
    count: Int
  ) {
    this.imageView?.load(data)
  }
}