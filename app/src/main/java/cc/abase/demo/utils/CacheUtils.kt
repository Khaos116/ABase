package cc.abase.demo.utils

import cc.ab.base.config.PathConfig
import cc.ab.base.utils.CCSizeUtils
import com.blankj.utilcode.util.FileUtils

/**
 * Description:
 * @author: CASE
 * @date: 2020/3/30 10:25
 */
object CacheUtils {
  //获取缓存大小，需要在异步线程执行
  fun getCacheSize(): String {
    val size1 = FileUtils.getLength(PathConfig.VIDEO_OVER_CACHE_DIR)
    val size2 = FileUtils.getLength(PathConfig.VIDEO_CACHE_DIR)
    val size3 = FileUtils.getLength(PathConfig.IMG_CACHE_DIR)
    val size4 = FileUtils.getLength(PathConfig.API_CACHE_DIR)
    val size5 = FileUtils.getLength(PathConfig.CRASH_CACHE_DIR)
    val size6 = FileUtils.getLength(PathConfig.DOWNLOAD_DIR)
    val size7 = FileUtils.getLength(PathConfig.TEMP_IMG_DIR)
    val size8 = FileUtils.getLength(PathConfig.TEMP_VIDEO_DIR)
    return CCSizeUtils.getPrintSize(size1 + size2 + size3 + size4 + size5 + size6 + size7 + size8)
  }

  //清理缓存，清理后返回清理后的大小，需要在异步线程执行
  fun clearCache(): String {
    FileUtils.deleteAllInDir(PathConfig.VIDEO_OVER_CACHE_DIR)
    FileUtils.deleteAllInDir(PathConfig.VIDEO_CACHE_DIR)
    FileUtils.deleteAllInDir(PathConfig.IMG_CACHE_DIR)
    FileUtils.deleteAllInDir(PathConfig.API_CACHE_DIR)
    FileUtils.deleteAllInDir(PathConfig.CRASH_CACHE_DIR)
    FileUtils.deleteAllInDir(PathConfig.DOWNLOAD_DIR)
    FileUtils.deleteAllInDir(PathConfig.TEMP_IMG_DIR)
    FileUtils.deleteAllInDir(PathConfig.TEMP_VIDEO_DIR)
    return getCacheSize()
  }
}