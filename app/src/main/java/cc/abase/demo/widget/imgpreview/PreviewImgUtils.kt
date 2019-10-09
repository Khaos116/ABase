package cc.abase.demo.widget.imgpreview

import android.app.Activity
import android.graphics.Rect
import android.widget.ImageView
import cc.ab.base.net.http.response.PicBean
import com.previewlibrary.GPreviewBuilder
import kotlin.math.min

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/5 11:36
 */
class PreviewImgUtils private constructor() {
  private object SingletonHolder {
    val holder = PreviewImgUtils()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  fun startPreview(
    activity: Activity,
    picList: MutableList<PicBean>,
    views: MutableList<ImageView>,
    position: Int
  ) {
    for (i in 0 until min(picList.size, views.size)) {
      val bounds = Rect()
      views[i].getGlobalVisibleRect(bounds)
      picList[i].mBounds = bounds
    }
    GPreviewBuilder.from(activity)//activity实例必须
//      .to(CustomActivity::class.java!!)//自定义Activity 使用默认的预览不需要
      .setData(picList)//集合
//      .setUserFragment(UserFragment::class.java!!)//自定义Fragment 使用默认的预览不需要
      .setCurrentIndex(position)
      .setSingleFling(false)//是否在黑屏区域点击返回
      .setDrag(false)//是否禁用图片拖拽返回
      .setType(GPreviewBuilder.IndicatorType.Number)//指示器类型
      .start()//启动
  }
}