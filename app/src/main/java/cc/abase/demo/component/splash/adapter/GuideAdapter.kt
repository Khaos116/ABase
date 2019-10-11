package cc.abase.demo.component.splash.adapter

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cc.ab.base.ext.*
import cc.abase.demo.R
import cc.abase.demo.component.login.LoginActivity
import cc.abase.demo.utils.MMkvUtils
import kotlinx.android.synthetic.main.layout_guide.view.guideGo
import kotlinx.android.synthetic.main.layout_guide.view.guideKIV
import me.panpf.sketch.SketchImageView

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/11 15:16
 */
class GuideAdapter(list: List<String>) : RecyclerView.Adapter<GuideAdapter.ViewHolder>() {
  private var mData = list
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    val view = inflater.inflate(R.layout.layout_guide, parent, false)
    return ViewHolder(view)
  }

  override fun getItemCount() = if (mData.isNullOrEmpty()) 0 else mData.size

  override fun onBindViewHolder(
    holder: ViewHolder,
    position: Int
  ) {
    holder.imageView?.load(mData[position])
    holder.textView?.let { view ->
      view.visibleGone(position == itemCount - 1)
      view.pressEffectAlpha()
      view.click {
        MMkvUtils.instance.setNeedGuide(false)
        LoginActivity.startActivity(it.context)
      }
    }
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var imageView: SketchImageView? = null
    var textView: TextView? = null

    init {
      imageView = itemView.guideKIV
      textView = itemView.guideGo
    }
  }
}