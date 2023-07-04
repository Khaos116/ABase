package cc.ab.base.widget.engine

import android.content.Context
import android.widget.ImageView
import cc.ab.base.ext.loadCoilUrl
import coil.imageLoader
import coil.request.ImageRequest
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.utils.ActivityCompatHelper

/**
 * https://github.com/LuckSiege/PictureSelector/blob/version_component/app/src/main/java/com/luck/pictureselector/CoilEngine.kt
 * @author：luck
 * @date：2022/2/14 3:00 下午
 * @describe：CoilEngine
 */
class CoilEngine : ImageEngine {
  override fun loadImage(context: Context, url: String, imageView: ImageView) {
    if (!ActivityCompatHelper.assertValidRequest(context)) {
      return
    }
    val target = ImageRequest.Builder(context)
      .data(url)
      .target(imageView)
      .build()
    context.imageLoader.enqueue(target)
  }

  override fun loadImage(context: Context?, imageView: ImageView?, url: String?, maxWidth: Int, maxHeight: Int) {
    if (!ActivityCompatHelper.assertValidRequest(context)) {
      return
    }
    context?.let {
      val builder = ImageRequest.Builder(it)
      if (maxWidth > 0 && maxHeight > 0) {
        builder.size(maxWidth, maxHeight)
      }
      imageView?.let { v -> builder.data(url).target(v) }
      val request = builder.build()
      context.imageLoader.enqueue(request)
    }
  }

  override fun loadAlbumCover(context: Context, url: String, imageView: ImageView) {
    if (!ActivityCompatHelper.assertValidRequest(context)) {
      return
    }
    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
    imageView.loadCoilUrl(url = url, holderRatio = 1f, holderWidth = 180)
  }

  override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
    if (!ActivityCompatHelper.assertValidRequest(context)) {
      return
    }
    imageView.scaleType = ImageView.ScaleType.CENTER_CROP
    imageView.loadCoilUrl(url = url, holderRatio = 1f, holderWidth = 270)
  }

  override fun pauseRequests(context: Context?) {}

  override fun resumeRequests(context: Context?) {}
}