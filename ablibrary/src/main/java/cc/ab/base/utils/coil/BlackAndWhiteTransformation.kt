package cc.ab.base.utils.coil

import android.annotation.SuppressLint
import android.graphics.Bitmap
import coil.size.Size
import coil.transform.Transformation
import com.blankj.utilcode.util.ImageUtils

/**
 * 转化为黑白图
 * Author:Khaos116
 * Date:2023/7/3
 * Time:11:05
 */
class BlackAndWhiteTransformation : Transformation {
  override val cacheKey: String = "${BlackAndWhiteTransformation::class.java.name}-BlackAndWhite"

  @SuppressLint("Range")
  override suspend fun transform(input: Bitmap, size: Size): Bitmap {
    return ImageUtils.toGray(input)
  }
}