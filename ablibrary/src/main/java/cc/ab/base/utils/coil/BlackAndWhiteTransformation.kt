package cc.ab.base.utils.coil

import android.annotation.SuppressLint
import android.graphics.*
import coil.size.Size
import coil.transform.Transformation

/**
 * 转化为黑白图
 * Author:Khaos116
 * Date:2023/7/3
 * Time:11:05
 */
class BlackAndWhiteTransformation : Transformation {
  override val cacheKey: String = "${BlackAndWhiteTransformation::class.java.name}-BlackAndWhite"

  @SuppressLint("Range")
  override suspend fun transform(input: Bitmap, size: Size): Bitmap {//不能使用Bitmap.createBitmap，否则会导致图片显示不全
    val output = input.copy(input.config, true)
    val canvas = Canvas(output)
    val paint = Paint().apply {
      val colorMatrix = ColorMatrix()
      colorMatrix.setSaturation(0f) // 设置饱和度为0，即将图像转为灰度图
      colorFilter = ColorMatrixColorFilter(colorMatrix)
    }
    canvas.drawBitmap(input, 0f, 0f, paint)
    return output
  }
}