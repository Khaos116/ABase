package cc.ab.base.ext

import android.graphics.Color
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import cc.ab.base.R
import cc.ab.base.utils.PlaceHolderUtils
import cc.ab.base.utils.coil.BlackAndWhiteTransformation
import cc.ab.base.utils.coil.BlurTransformation
import coil.load
import coil.request.ImageRequest
import coil.size.Scale
import coil.transform.Transformation
import coil.util.CoilUtils
import com.blankj.utilcode.util.ScreenUtils

/**
 * Coil2.4.0已经支持网络视频封面加载了，所以不再使用MediaMetadataRetriever去获取网络视频封面
 * 预加载图片https://coil-kt.github.io/coil/getting_started/#preloading
 * 如果需要对加载的图片进行截屏，可能需要设置非硬件加载：allowHardware(false) https://coil-kt.github.io/coil/recipes/
 * diskCachePolicy(CachePolicy.DISABLED)
 * memoryCachePolicy(CachePolicy.DISABLED)
 * networkCachePolicy(CachePolicy.DISABLED)
 * Author:Khaos
 * Date:2020/8/12
 * Time:18:28
 */
private const val duration = 300

//清除上次的加载状态，保证重新加载
fun ImageView.clearLoad() {
  CoilUtils.dispose(this)
  setTag(R.id.suc_img, null)
}

fun ImageView.loadCoilSimpleUrl(url: String?, holderRatio: Float, hasHolder: Boolean = true) {
  loadCoilUrl(url = url, holderRatio = holderRatio, hasHolder = hasHolder)
}

fun ImageView.loadCoilSimpleRes(@DrawableRes resId: Int, holderRatio: Float, hasHolder: Boolean = true) {
  loadCoilRes(resId = resId, holderRatio = holderRatio, hasHolder = hasHolder)
}

//加载网络图片或者URI图片
fun ImageView.loadCoilUrl(
  url: String?,//图片地址或者文件地址
  holderRatio: Float = 1f,//占位图宽高比
  holderWidth: Int = ScreenUtils.getScreenWidth(),//占位图宽度
  hasHolder: Boolean = true,//是否需要占位图
  holderBgColor: Int = Color.WHITE,//占位图默认背景色
  blackWhite: Boolean = false,//是否需要黑白效果
  @FloatRange(from = 0.0, to = 25.0) blurRadius: Float = 0f,//如果需要高斯模糊效果则输入范围(0,25]
) {
  if (url.isNullOrBlank()) {
    this.clearLoad()
    if (hasHolder) this.load(PlaceHolderUtils.getErrorHolder(ratio = holderRatio, width = holderWidth, bgColor = holderBgColor))
  } else {
    val myTag = "${url}_${blackWhite}_${blurRadius}"
    if (getTag(R.id.suc_img) == myTag) return
    val myIv = this
    myIv.clearLoad()//清理之前设置的tag，取消之前的请求
    val transList = mutableListOf<Transformation>()//图片特殊效果列表
    if (blackWhite) transList.add(BlackAndWhiteTransformation())//黑白化
    if (blurRadius > 0 && blurRadius <= 25) transList.add(BlurTransformation(blurRadius))//高斯模糊
    val build = fun ImageRequest.Builder.() {
      crossfade(duration)//过度效果
      if (hasHolder) placeholder(PlaceHolderUtils.getLoadingHolder(ratio = holderRatio, width = holderWidth, bgColor = holderBgColor))//加载中占位图
      if (hasHolder) error(PlaceHolderUtils.getErrorHolder(ratio = holderRatio, width = holderWidth, bgColor = holderBgColor))//加载失败占位图
      if (transList.isNotEmpty()) transformations(transList)//图片特殊效果
      listener(
        onError = { r, e -> "Coil图片加载失败:${r.data}},e=${e.throwable.message ?: "null"}".logE() },//失败打印图片地址和原因
        onSuccess = { _, _ -> setTag(R.id.suc_img, myTag) },//成功设置TAG，防止复用重新加载
      )
    }
    myIv.load(data = url, builder = build)//如果是文件就加载文件，否则就加载图片地址
  }
}

//加载宽度固定，高度自适应的图片(viewWidth一定要和控件宽度一致，否则会填充不满)
fun ImageView.loadCoilWrapHeight(
  url: String?,//图片地址或者文件地址
  holderRatio: Float = 1f,//占位图宽高比
  hasHolder: Boolean = true,//是否需要占位图
  viewWidth: Int = ScreenUtils.getScreenWidth(),//控件宽度
  holderBgColor: Int = Color.WHITE,//占位图默认背景色
  blackWhite: Boolean = false,//是否需要黑白效果
  @FloatRange(from = 0.0, to = 25.0) blurRadius: Float = 0f,//如果需要高斯模糊效果则输入范围(0,25]
) {
  if (url.isNullOrBlank()) {
    this.clearLoad()
    if (hasHolder) this.load(PlaceHolderUtils.getErrorHolder(ratio = holderRatio, width = viewWidth, bgColor = holderBgColor))
  } else {
    val myTag = "${url}_${blackWhite}_${blurRadius}"
    if (getTag(R.id.suc_img) == myTag) return
    val myIv = this
    myIv.clearLoad()//清理之前设置的tag，取消之前的请求
    val transList = mutableListOf<Transformation>()//图片特殊效果列表
    if (blackWhite) transList.add(BlackAndWhiteTransformation())//黑白化
    if (blurRadius > 0 && blurRadius <= 25) transList.add(BlurTransformation(blurRadius))//高斯模糊
    val build = fun ImageRequest.Builder.() {
      size(viewWidth, Int.MAX_VALUE)//宽度固定，高度自适应
      scale(Scale.FIT)//这个模式将图片缩放以适应ImageView的尺寸，同时保持图片的宽高比例不变
      //scale(Scale.FILL)//这个模式将图片缩放以填充满整个ImageView，无论图片的宽高比例如何
      crossfade(duration)//过度效果
      if (hasHolder) placeholder(PlaceHolderUtils.getLoadingHolder(ratio = holderRatio, width = viewWidth, bgColor = holderBgColor))//加载中占位图
      if (hasHolder) error(PlaceHolderUtils.getErrorHolder(ratio = holderRatio, width = viewWidth, bgColor = holderBgColor))//加载失败占位图
      if (transList.isNotEmpty()) transformations(transList)//图片特殊效果
      listener(
        onError = { r, e -> "Coil图片加载失败:${r.data}},e=${e.throwable.message ?: "null"}".logE() },//失败打印图片地址和原因
        onSuccess = { _, _ -> setTag(R.id.suc_img, myTag) },//成功设置TAG，防止复用重新加载
      )
    }
    myIv.load(data = url, builder = build)//如果是文件就加载文件，否则就加载图片地址
  }
}

//加载高度固定，宽度自适应的图片(viewHeight一定要和控件高度一致，否则会填充不满)
fun ImageView.loadCoilWrapWidth(
  url: String?,//图片地址或者文件地址
  holderRatio: Float = 1f,//占位图宽高比
  hasHolder: Boolean = true,//是否需要占位图
  viewHeight: Int = ScreenUtils.getScreenWidth() / 2,//控件高度
  holderBgColor: Int = Color.WHITE,//占位图默认背景色
  blackWhite: Boolean = false,//是否需要黑白效果
  @FloatRange(from = 0.0, to = 25.0) blurRadius: Float = 0f,//如果需要高斯模糊效果则输入范围(0,25]
) {
  val holderWidth = (viewHeight * holderRatio).toInt()
  if (url.isNullOrBlank()) {
    this.clearLoad()
    if (hasHolder) this.load(PlaceHolderUtils.getErrorHolder(ratio = holderRatio, width = holderWidth, bgColor = holderBgColor))
  } else {
    val myTag = "${url}_${blackWhite}_${blurRadius}"
    if (getTag(R.id.suc_img) == myTag) return
    val myIv = this
    myIv.clearLoad()//清理之前设置的tag，取消之前的请求
    val transList = mutableListOf<Transformation>()//图片特殊效果列表
    if (blackWhite) transList.add(BlackAndWhiteTransformation())//黑白化
    if (blurRadius > 0 && blurRadius <= 25) transList.add(BlurTransformation(blurRadius))//高斯模糊
    val build = fun ImageRequest.Builder.() {
      size(Int.MAX_VALUE, viewHeight)//高度固定，宽度自适应
      scale(Scale.FIT)//这个模式将图片缩放以适应ImageView的尺寸，同时保持图片的宽高比例不变
      //scale(Scale.FILL)//这个模式将图片缩放以填充满整个ImageView，无论图片的宽高比例如何
      crossfade(duration)//过度效果
      if (hasHolder) placeholder(PlaceHolderUtils.getLoadingHolder(ratio = holderRatio, width = holderWidth, bgColor = holderBgColor))//加载中占位图
      if (hasHolder) error(PlaceHolderUtils.getErrorHolder(ratio = holderRatio, width = holderWidth, bgColor = holderBgColor))//加载失败占位图
      if (transList.isNotEmpty()) transformations(transList)//图片特殊效果
      listener(
        onError = { r, e -> "Coil图片加载失败:${r.data}},e=${e.throwable.message ?: "null"}".logE() },//失败打印图片地址和原因
        onSuccess = { _, _ -> setTag(R.id.suc_img, myTag) },//成功设置TAG，防止复用重新加载
      )
    }
    myIv.load(data = url, builder = build)//如果是文件就加载文件，否则就加载图片地址
  }
}

//加载资源图片id
fun ImageView.loadCoilRes(
  @DrawableRes resId: Int,//资源图片id
  holderRatio: Float = 1f,//图片宽高比
  hasHolder: Boolean = true,//是否需要占位图
  holderWidth: Int = ScreenUtils.getScreenWidth(),//占位图宽度
  holderBgColor: Int = Color.WHITE,//占位图默认背景色
  blackWhite: Boolean = false,//是否需要黑白效果
  @FloatRange(from = 0.0, to = 25.0) blurRadius: Float = 0f,//如果需要高斯模糊效果则输入范围(0,25]
) {
  this.clearLoad()
  val myTag = "${resId}_${blackWhite}_${blurRadius}"
  if (getTag(R.id.suc_img) == myTag) return
  val myIv = this
  val transList = mutableListOf<Transformation>()//图片特殊效果列表
  if (blackWhite) transList.add(BlackAndWhiteTransformation())//黑白化
  if (blurRadius > 0 && blurRadius <= 25) transList.add(BlurTransformation(blurRadius))//高斯模糊
  val build = fun ImageRequest.Builder.() {
    scale(if (myIv.scaleType == ImageView.ScaleType.CENTER_CROP) Scale.FILL else Scale.FIT)//填充方式
    crossfade(duration)//过度效果
    if (hasHolder) error(PlaceHolderUtils.getErrorHolder(ratio = holderRatio, width = holderWidth, bgColor = holderBgColor))//加载失败占位图
    if (transList.isNotEmpty()) transformations(transList)//图片特殊效果
    listener(onSuccess = { _, _ -> setTag(R.id.suc_img, myTag) })//成功设置TAG，防止复用重新加载
  }
  myIv.load(data = resId, builder = build)//加载资源图片
}