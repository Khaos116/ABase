package cc.abase.demo.repository

import com.blankj.utilcode.util.PathUtils
import io.rx_cache2.internal.RxCache
import io.victoralbertos.jolyglot.GsonSpeaker
import java.io.File

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/9 21:47
 */
abstract class BaseRepository<T>(classProviders: Class<T>) {
  //缓存目录
  private val cacheDir = PathUtils.getExternalAppDataPath() + File.separator + ".fuel"
  //缓存api
  var cacheApi: T = RxCache.Builder()
      .persistence(
          if (File(cacheDir).exists()) {
            File(cacheDir)
          } else {
            File(cacheDir).mkdirs()
            File(cacheDir)
          }, GsonSpeaker()
      )
      .using<T>(classProviders)
}