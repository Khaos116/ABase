package cc.ab.base.ext

import me.panpf.sketch.SketchImageView
import java.io.File

fun SketchImageView.load(url: String?) {
  url?.let {
    this.displayImage(url)
  }
}

fun SketchImageView.load(file: File?) {
  file?.let {
    this.displayImage(file.path)
  }
}

fun SketchImageView.getCacheFile(url: String?): File? {
  return if (url.isNullOrBlank()) {
    null
  } else {
    null
  }
}
//
///**
// * Description:
// * @author: caiyoufei
// * @date: 2019/9/24 10:52
// */
///**
// * 加载url图片,默认CenterCrop和CrossFade效果
// */
//fun ImageView.load(
//  url: String?,
//  fit: Boolean = false
//) {
//  url?.let {
//    val placeholderId = RandomPlaceholder.instance.getPlaceHolder(it)
//    val builder = ImageConfig.builder()
//    if (fit) {
//      builder.fitCenter()
//      this.scaleType = ScaleType.FIT_CENTER
//    }
//    val config: ImageConfig = builder
//      .useCrossFade(false)
//      .url(it)
//      .errorSrc(placeholderId)//设置默认的占位图
//      .placeholder(placeholderId)//设置默认的加载错误图
//      .build()
//    load(config)
//  }
//}
//
///**
// * 加载uri图片,默认CenterCrop和CrossFade效果
// */
//fun ImageView.load(
//  uri: Uri?,
//  fit: Boolean = false
//) {
//  uri?.let {
//    val placeholderId = RandomPlaceholder.instance.getPlaceHolder(it.toString())
//    val builder = ImageConfig.builder()
//    if (fit) {
//      builder.fitCenter()
//      this.scaleType = ScaleType.FIT_CENTER
//    }
//    val config: ImageConfig = builder
//      .useCrossFade(false)
//      .uri(it)
//      .errorSrc(placeholderId)//设置默认的占位图
//      .placeholder(placeholderId)//设置默认的加载错误图
//      .build()
//    load(config)
//  }
//}
//
///**
// * 加载本地资源,默认CenterCrop和CrossFade效果
// */
//fun ImageView.load(@DrawableRes resourceId: Int?, fit: Boolean = false) {
//  resourceId?.let {
//    val builder = ImageConfig.builder()
//    if (fit) {
//      builder.fitCenter()
//      this.scaleType = ScaleType.FIT_CENTER
//    }
//    val config: ImageConfig = builder
//      .useCrossFade(false)
//      .resourceId(it)
//      .build()
//    load(config)
//  }
//}
//
///**
// * 加载文件图片,默认CenterCrop和CrossFade效果
// */
//fun ImageView.load(file: File?) {
//  Glide.with(Utils.getApp())
//    .asBitmap()
//    .load(file)
//    .centerCrop()
//    .transition(BitmapTransitionOptions.withCrossFade())
//    .into(this)
//}
//
///**
// * 加载图片有占位图和加载错误图,默认CenterCrop和CrossFade效果
// */
//fun ImageView.load(
//  url: String?,
//  loadingResId: Int,
//  errorResId: Int,
//  fit: Boolean = false
//) {
//  url?.let {
//    val builder = ImageConfig.builder()
//    if (fit) {
//      builder.fitCenter()
//      this.scaleType = ScaleType.FIT_CENTER
//    }
//    val config: ImageConfig = builder
//      .imageView(this)
//      .url(it)
//      .placeholder(loadingResId)
//      .errorSrc(errorResId)
//      .build()
//    load(config)
//  }
//}
//
///**
// * 根据配置加载图片
// */
//fun ImageView.load(config: ImageConfig) {
//  config.url?.let {
//    config.context = Utils.getApp()
//    config.imageView = this
//    val loader: ImageLoader by BaseApplication.getApp().kodein.instance()
//    loader.loadImage(config)
//  }
//}
//
///**
// * 加载高斯模糊图
// * @param radius 模糊度1-25
// * @param sampling 缩放，越大缩放比例越大
// */
//fun ImageView.loadBlur(
//  url: String?,
//  radius: Int = 12,
//  sampling: Int = 7,
//  fit: Boolean = false
//) {
//  url?.let {
//    val builder = ImageConfig.builder()
//    if (fit) {
//      builder.fitCenter()
//      this.scaleType = ScaleType.FIT_CENTER
//      builder.onSuccess { }
//    }
//    val config: ImageConfig = builder
//      .useCrossFade(true)
//      .blur(radius, sampling)
//      .url(it)
//      .build()
//    load(config)
//  }
//}
//
///**
// * 加载高斯模糊图
// * @param radius 模糊度1-25
// * @param sampling 缩放，越大缩放比例越大
// */
//fun ImageView.loadBlur(
//  uri: Uri?,
//  radius: Int = 12,
//  sampling: Int = 7,
//  fit: Boolean = false
//) {
//  uri?.let {
//    val builder = ImageConfig.builder()
//    if (fit) {
//      builder.fitCenter()
//      this.scaleType = ScaleType.FIT_CENTER
//      builder.onSuccess { }
//    }
//    val config: ImageConfig = builder
//      .useCrossFade(true)
//      .blur(radius, sampling)
//      .uri(it)
//      .build()
//    load(config)
//  }
//}
//
///**
// * 加载高斯模糊图
// * @param radius 模糊度1-25
// * @param sampling 缩放，越大缩放比例越大
// */
//fun ImageView.loadBlur(
//  @DrawableRes resourceId: Int?, radius: Int = 12,
//  sampling: Int = 7,
//  fit: Boolean = false
//) {
//  resourceId?.let {
//    val builder = ImageConfig.builder()
//    if (fit) {
//      builder.fitCenter()
//      this.scaleType = ScaleType.FIT_CENTER
//      builder.onSuccess { }
//    }
//    val config: ImageConfig = builder
//      .useCrossFade(true)
//      .blur(radius, sampling)
//      .resourceId(it)
//      .build()
//    load(config)
//  }
//}