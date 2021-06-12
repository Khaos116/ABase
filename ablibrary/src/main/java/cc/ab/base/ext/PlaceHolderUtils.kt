package cc.ab.base.ext

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.view.Gravity
import androidx.annotation.ColorInt
import cc.ab.base.R
import com.blankj.utilcode.util.ResourceUtils
import com.blankj.utilcode.util.ScreenUtils

/**
 * 动态生成loading和error占位图
 * Author:Khaos
 * Date:2020-12-8
 * Time:14:30
 */
object PlaceHolderUtils {
  //防止每次都要生成占位图
  private var loadingMaps = hashMapOf<Triple<Float, Int, Int>, LayerDrawable>()
  private var errorMaps = hashMapOf<Triple<Float, Int, Int>, LayerDrawable>()

  /**
   * 获取loading占位图
   * @param ratio 宽高比(不能设置为0)
   * @param width 控件宽度(默认为屏幕宽度)
   * @param bgColor 占位图背景色
   */
  fun getLoadingHolder(ratio: Float = 1f, width: Int = ScreenUtils.getScreenWidth(), @ColorInt bgColor: Int = Color.WHITE): LayerDrawable {
    //loadingMaps.toList().firstOrNull { it.first.first == ratio && it.first.second == width && it.first.third == bgColor }?.let {
    //  it.second.getDrawable(0).alpha = 255
    //  it.second.getDrawable(1).alpha = 255
    //  it.second.alpha = 255
    //  return it.second
    //}
    //计算背景图高度
    val height = (width * 1f / ratio).toInt()
    //计算中间图标的尺寸
    val size = width.coerceAtMost(height)
    //背景图
    val d1 = ColorDrawable()
    d1.setBounds(0, 0, width, height)
    d1.color = bgColor
    //占位图
    val d2 = ResourceUtils.getDrawable(R.drawable.loading_square)
    d2.setBounds(0, 0, size, size)
    //生成LayerDrawable
    return LayerDrawable(arrayOf(d1, d2)).also {
      it.setLayerSize(0, width, height)
      it.setLayerSize(1, size, size)
      it.setLayerGravity(0, Gravity.CENTER)
      it.setLayerGravity(1, Gravity.CENTER)
      //loadingMaps[Triple(ratio, width, bgColor)] = it
    }
  }

  /**
   * 获取error占位图
   * @param ratio 宽高比(不能设置为0)
   * @param width 控件宽度(默认为屏幕宽度)
   * @param bgColor 占位图背景色
   */
  fun getErrorHolder(ratio: Float = 1f, width: Int = ScreenUtils.getScreenWidth(), @ColorInt bgColor: Int = Color.WHITE): LayerDrawable {
    //errorMaps.toList().firstOrNull { it.first.first == ratio && it.first.second == width && it.first.third == bgColor }?.let {
    //  it.second.getDrawable(0).alpha = 255
    //  it.second.getDrawable(1).alpha = 255
    //  it.second.alpha = 255
    //  return it.second
    //}
    //计算背景图高度
    val height = (width * 1f / ratio).toInt()
    //计算中间图标的尺寸
    val size = width.coerceAtMost(height)
    //背景图
    val d1 = ColorDrawable()
    d1.setBounds(0, 0, width, height)
    d1.color = bgColor
    //占位图
    val d2 = ResourceUtils.getDrawable(R.drawable.error_square)
    d2.setBounds(0, 0, size, size)
    //生成LayerDrawable
    return LayerDrawable(arrayOf(d1, d2)).also {
      it.setLayerSize(0, width, height)
      it.setLayerSize(1, size, size)
      it.setLayerGravity(0, Gravity.CENTER)
      it.setLayerGravity(1, Gravity.CENTER)
      //errorMaps[Triple(ratio, width, bgColor)] = it
    }
  }
}