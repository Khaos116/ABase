package cc.abase.demo.utils

import android.app.Activity
import android.view.View
import android.widget.ImageView
import cc.ab.base.widget.nineimageview.ImageData
import cc.abase.demo.widget.imgpreview.PreviewImgUtils

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/28 12:06
 */
class BrowserUtils private constructor() {
  fun show(
    position: Int,
    view: View,
    dataList: MutableList<ImageData>,
    viewList: MutableList<ImageView>
  ) {
    //多图预览
    PreviewImgUtils.instance.startPreview2(
        view.context as Activity, dataList, viewList, position
    )
  }

  private object SingletonHolder {
    val holder = BrowserUtils()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

}