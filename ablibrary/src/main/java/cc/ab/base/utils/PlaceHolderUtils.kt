package cc.ab.base.utils

import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.toDrawable
import cc.ab.base.R
import com.blankj.utilcode.util.ResourceUtils
import com.blankj.utilcode.util.ScreenUtils

/**
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
   * @param corner 圆角大小px
   */
  fun getLoadingHolder(ratio: Float = 1f, width: Int = ScreenUtils.getScreenWidth(), @ColorInt bgColor: Int = Color.WHITE, corner: Float = 0f): Drawable {
    return generatePlaceholder(newRatio = ratio, newWidth = width, bgColor = bgColor, corner = corner, loading = true)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="获取error占位图">
  /**
   * 获取error占位图
   * @param ratio 宽高比(不能设置为0)
   * @param width 控件宽度(默认为屏幕宽度)
   * @param bgColor 占位图背景色
   * @param corner 圆角大小px
   */
  fun getErrorHolder(ratio: Float = 1f, width: Int = ScreenUtils.getScreenWidth(), @ColorInt bgColor: Int = Color.WHITE, corner: Float = 0f): Drawable {
    return generatePlaceholder(newRatio = ratio, newWidth = width, bgColor = bgColor, corner = corner, loading = false)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="生成占位图">
  /**
   * 生成不同宽高比的占位图
   * @param newRatio 宽高比(不能设置为0)
   * @param newWidth 控件宽度(默认为屏幕宽度)
   * @param bgColor 占位图背景色
   * @param corner 圆角大小px
   * @param loading 是否是加载中的占位图
   */
  private fun generatePlaceholder(newRatio: Float = 1f, newWidth: Int, bgColor: Int, corner: Float, loading: Boolean): Drawable {
    //计算背景图高度
    val newHeight = (newWidth * 1f / newRatio).toInt()
    //计算中间图标的尺寸
    val size = newWidth.coerceAtMost(newHeight)
    // 计算居中偏移量
    val offsetX = (newWidth - size) / 2
    val offsetY = (newHeight - size) / 2
    // 创建缩放和居中的占位图
    val bitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.RGB_565)
    bitmap.eraseColor(bgColor)//指定背景色
    val canvas = Canvas(bitmap)
    val drawable = ResourceUtils.getDrawable(if (loading) R.drawable.loading_square else R.drawable.error_square)
    drawable.setBounds(offsetX, offsetY, offsetX + size, offsetY + size)
    drawable.draw(canvas)
    return (if (corner > 0) getRoundedCornerBitmap(bitmap, corner) else bitmap).toDrawable(Resources.getSystem())
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="图片圆角处理(但是图片会缩放，可能填充到UI上不是理想效果)">
  private fun getRoundedCornerBitmap(bitmap: Bitmap, radius: Float): Bitmap {
    val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    //output.eraseColor(Color.BLUE)
    val canvas = Canvas(output)
    val paint = Paint()

    val rect = Rect(0, 0, bitmap.width, bitmap.height)
    val rectF = RectF(rect)
    val path = Path()
    path.addRoundRect(rectF, radius, radius, Path.Direction.CW)

    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    paint.color = Color.BLACK
    canvas.drawPath(path, paint)

    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(bitmap, rect, rect, paint)

    return output
    //</editor-fold>
  }
  //</editor-fold>
}