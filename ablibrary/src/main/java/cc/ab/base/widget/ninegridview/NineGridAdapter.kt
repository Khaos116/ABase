package cc.ab.base.widget.ninegridview

import android.content.Context
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.core.content.ContextCompat
import cc.ab.base.R
import cc.ab.base.net.http.response.PicBean
import cc.ab.base.utils.RandomPlaceholder
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.Utils
import me.panpf.sketch.Sketch
import me.panpf.sketch.SketchImageView
import me.panpf.sketch.display.FadeInImageDisplayer
import me.panpf.sketch.request.DisplayOptions
import me.panpf.sketch.request.ShapeSize
import me.panpf.sketch.shaper.RoundRectImageShaper

/**
 *description: 九宫格布局的Adapter.
 *@date 2018/10/24 14:20.
 *@author: YangYang.
 */
class NineGridAdapter(private val imageSize: Int = SizeUtils.dp2px(100f)) :
    NineGridViewAdapter<PicBean>() {

  private val colorStroke = ContextCompat.getColor(Utils.getApp(), R.color.white_F5F5F5)
  override fun onDisplayImage(
    context: Context?,
    imageView: ImageView?,
    pic: PicBean?
  ) {
    pic?.let {
      context?.let { c ->
        if (imageView is SketchImageView) {
          val displayOptions = DisplayOptions()
          val holder = RandomPlaceholder.instance.getPlaceHolder(it.mediaUrl)
          displayOptions.setLoadingImage(holder)
          displayOptions.setErrorImage(R.drawable.svg_placeholder_fail)
          //圆角
          val shaper = RoundRectImageShaper(SizeUtils.dp2px(5f).toFloat())
//          shaper.setStroke(colorStroke, 1)
          displayOptions.shaper = shaper
          //图片尺寸
          val shapeSize = ShapeSize(imageSize, imageSize, ScaleType.CENTER_CROP)
          displayOptions.shapeSize = shapeSize
          imageView.setOptions(displayOptions)
          imageView.options.displayer = FadeInImageDisplayer()
          // DisplayHelper
          Sketch.with(c)
              .display(it.mediaUrl, imageView)
              .commit()
        }
      }
    }
  }
}