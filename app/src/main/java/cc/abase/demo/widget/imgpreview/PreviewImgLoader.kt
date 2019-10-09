package cc.abase.demo.widget.imgpreview

import android.content.Context
import android.widget.ImageView
import androidx.fragment.app.Fragment
import cc.ab.base.ext.getDrawableRes
import cc.ab.base.utils.RandomPlaceholder
import com.blankj.utilcode.util.Utils
import com.previewlibrary.loader.IZoomMediaLoader
import com.previewlibrary.loader.MySimpleTarget
import me.panpf.sketch.Sketch
import me.panpf.sketch.request.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/5 11:35
 */
class PreviewImgLoader : IZoomMediaLoader {
  override fun displayGifImage(p0: Fragment, url: String, iv: ImageView?, p3: MySimpleTarget) {
    val placeholderId = RandomPlaceholder.instance.getPlaceHolder(url)
    Sketch.with(Utils.getApp()).load(url, object : LoadListener {
      override fun onCompleted(result: LoadResult) {
        iv?.setImageBitmap(result.bitmap)
        p3.onResourceReady()
      }

      override fun onStarted() {
      }

      override fun onCanceled(cause: CancelCause) {
      }

      override fun onError(cause: ErrorCause) {
        p3.onLoadFailed(Utils.getApp().getDrawableRes(placeholderId))
      }

    }).commit()
  }

  override fun displayImage(p0: Fragment, url: String, iv: ImageView?, p3: MySimpleTarget) {
    val placeholderId = RandomPlaceholder.instance.getPlaceHolder(url)
    Sketch.with(Utils.getApp()).load(url, object : LoadListener {
      override fun onCompleted(result: LoadResult) {
        iv?.setImageBitmap(result.bitmap)
        p3.onResourceReady()
      }

      override fun onStarted() {
      }

      override fun onCanceled(cause: CancelCause) {
      }

      override fun onError(cause: ErrorCause) {
        p3.onLoadFailed(Utils.getApp().getDrawableRes(placeholderId))
      }

    }).commit()
  }

  override fun onStop(p0: Fragment) {
  }

  override fun clearMemory(c: Context) {
  }
}