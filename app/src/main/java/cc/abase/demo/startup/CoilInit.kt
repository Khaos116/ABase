package cc.abase.demo.startup

import android.content.Context
import android.os.Build
import cc.ab.base.ext.logI
import cc.abase.demo.rxhttp.config.RxHttpConfig
import cc.abase.demo.rxhttp.interceptor.NetCacheInterceptor
import cc.abase.demo.rxhttp.interceptor.OfflineCacheInterceptor
import coil.Coil
import coil.ImageLoader
import coil.decode.*
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.rousetime.android_startup.AndroidStartup

/**
 * Author:Khaos
 * Date:2020-12-9
 * Time:14:19
 */
class CoilInit : AndroidStartup<Int>() {
  //<editor-fold defaultstate="collapsed" desc="初始化线程问题">
  //create()方法调时所在的线程：如果callCreateOnMainThread返回true，则表示在主线程中初始化，会导致waitOnMainThread返回值失效；当返回false时，才会判断waitOnMainThread的返回值
  override fun callCreateOnMainThread(): Boolean = false

  //是否需要在主线程进行等待其完成:如果返回true，将在主线程等待，并且阻塞主线程
  override fun waitOnMainThread(): Boolean = false
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  override fun create(context: Context): Int {
    val imageLoader = ImageLoader.Builder(context)
      .crossfade(300)
      .okHttpClient {//https://coil-kt.github.io/coil/recipes/#using-a-custom-okhttpclient
        RxHttpConfig.getOkHttpClient()
          .addNetworkInterceptor(NetCacheInterceptor())
          .addInterceptor(OfflineCacheInterceptor())
          .build()
      }
      .components {
        add(VideoFrameDecoder.Factory())//https://coil-kt.github.io/coil/videos/
        add(SvgDecoder.Factory())//https://coil-kt.github.io/coil/svgs/
        //animated WebP (Android 9.0+), animated HEIF (Android 11.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//https://coil-kt.github.io/coil/gifs/
          add(ImageDecoderDecoder.Factory())
        } else {
          add(GifDecoder.Factory())
        }
      }
      .memoryCache {//https://coil-kt.github.io/coil/image_loaders/
        MemoryCache.Builder(context)
          .maxSizePercent(0.25)
          .build()
      }
      .diskCache {//https://coil-kt.github.io/coil/image_loaders/
        DiskCache.Builder()
          .directory(context.cacheDir.resolve("image_cache"))
          .maxSizePercent(0.1)
          .build()
      }
      .build()
    Coil.setImageLoader(imageLoader)
    "初始化完成".logI()
    return 0
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="依赖">
  override fun dependenciesByName(): List<String> {
    return mutableListOf(RxHttpInit::class.java.name)
  }
  //</editor-fold>
}