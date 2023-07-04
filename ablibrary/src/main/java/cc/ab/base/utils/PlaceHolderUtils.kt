package cc.ab.base.utils

import android.graphics.*
import android.graphics.drawable.*
import android.view.Gravity
import androidx.annotation.ColorInt
import cc.ab.base.R
import com.blankj.utilcode.util.ResourceUtils
import com.blankj.utilcode.util.ScreenUtils

/**
 * 不能使用Bitmap.createBitmap创建占位图，不然在列表中会卡顿很严重
 * 动态生成loading和error占位图,默认占位图资源需要1:1
 * Author:Khaos
 * Date:2020-12-8
 * Time:14:30
 */
object PlaceHolderUtils {
  //<editor-fold defaultstate="collapsed" desc="获取loading占位图">
  /**
   * 获取loading占位图
   * @param ratio 宽高比(不能设置为0)
   * @param width 控件宽度(默认为屏幕宽度)
   * @param bgColor 占位图背景色
   */
  fun getLoadingHolder(ratio: Float = 1f, width: Int = ScreenUtils.getScreenWidth(), @ColorInt bgColor: Int = Color.WHITE): Drawable {
    return generatePlaceholder(newRatio = ratio, newWidth = width, bgColor = bgColor, loading = true)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="获取error占位图">
  /**
   * 获取error占位图
   * @param ratio 宽高比(不能设置为0)
   * @param width 控件宽度(默认为屏幕宽度)
   * @param bgColor 占位图背景色
   */
  fun getErrorHolder(ratio: Float = 1f, width: Int = ScreenUtils.getScreenWidth(), @ColorInt bgColor: Int = Color.WHITE): Drawable {
    return generatePlaceholder(newRatio = ratio, newWidth = width, bgColor = bgColor, loading = false)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="生成占位图">
  /**
   * 生成不同宽高比的占位图
   * @param newRatio 宽高比(不能设置为0)
   * @param newWidth 控件宽度(默认为屏幕宽度)
   * @param bgColor 占位图背景色
   * @param loading 是否是加载中的占位图
   */
  private fun generatePlaceholder(newRatio: Float = 1f, newWidth: Int, bgColor: Int, loading: Boolean): Drawable {
    //计算背景图高度
    val newHeight = (newWidth * 1f / newRatio).toInt()
    //计算中间图标的尺寸
    val size = newWidth.coerceAtMost(newHeight)
    //背景图
    val d1 = ColorDrawable()
    d1.setBounds(0, 0, newWidth, newHeight)
    d1.color = bgColor
    //占位图
    val d2 = ResourceUtils.getDrawable(if (loading) R.drawable.loading_square else R.drawable.error_square)
    d2.setBounds(0, 0, size, size)
    //生成LayerDrawable
    return LayerDrawable(arrayOf(d1, d2)).also {
      it.setLayerSize(0, newWidth, newHeight)
      it.setLayerSize(1, size, size)
      it.setLayerGravity(0, Gravity.CENTER)
      it.setLayerGravity(1, Gravity.CENTER)
    }
  }
  //</editor-fold>
}