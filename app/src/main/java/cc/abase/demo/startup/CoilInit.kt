package cc.abase.demo.startup

import android.content.Context
import android.os.Build
import androidx.startup.Initializer
import cc.ab.base.ext.logI
import cc.abase.demo.rxhttp.config.RxHttpConfig
import cc.abase.demo.rxhttp.interceptor.NetCacheInterceptor
import cc.abase.demo.rxhttp.interceptor.OfflineCacheInterceptor
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.fetch.VideoFrameFileFetcher
import coil.fetch.VideoFrameUriFetcher
import coil.util.CoilUtils

/**
 * Author:CASE
 * Date:2020-12-9
 * Time:14:19
 */
class CoilInit : Initializer<Int> {
  override fun create(context: Context): Int {
    val imageLoader = ImageLoader.Builder(context)
        .crossfade(300)
        .okHttpClient {
          RxHttpConfig.getOkHttpClient()
              .addNetworkInterceptor(NetCacheInterceptor())
              .addInterceptor(OfflineCacheInterceptor())
              .cache(CoilUtils.createDefaultCache(context))
              .build()
        }
        .componentRegistry {
          add(VideoFrameFileFetcher(context))
          add(VideoFrameUriFetcher(context))
          if (Build.VERSION.SDK_INT >= 28) {
            add(ImageDecoderDecoder())
          } else {
            add(GifDecoder())
          }
        }
        .build()
    Coil.setImageLoader(imageLoader)
    "初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(RxHttpInit::class.java)
  }
}