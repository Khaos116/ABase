package cc.ab.base.utils.coil

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.annotation.FloatRange
import coil.size.Size
import coil.transform.Transformation
import com.blankj.utilcode.util.ImageUtils

/**
 * 转化为高斯模糊
 * Author:Khaos116
 * Date:2023/7/3
 * Time:11:01
 */
class BlurTransformation(@FloatRange(from = 0.0, to = 25.0) private val radius: Float = 0f) : Transformation {
  override val cacheKey: String = "${BlurTransformation::class.java.name}-${radius}"

  @SuppressLint("Range")
  override suspend fun transform(input: Bitmap, size: Size): Bitmap {
    return if (radius > 0 && radius <= 25) ImageUtils.renderScriptBlur(input, radius) else input
  }
}