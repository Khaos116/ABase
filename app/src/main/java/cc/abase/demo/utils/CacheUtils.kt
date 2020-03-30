package cc.abase.demo.utils

import cc.ab.base.utils.CCSizeUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/3/30 10:25
 */
class CacheUtils private constructor() {
  private object SingletonHolder {
    val holder = CacheUtils()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //获取缓存大小，需要在异步线程执行
  fun getCacheSize(): String {
    //APP下载目录
    val size1 = FileUtils.getLength(PathUtils.getExternalAppFilesPath())
    //APP缓存目录
    val size2 = FileUtils.getLength(PathUtils.getExternalAppCachePath())
    //总目录  PathUtils.getExternalAppDataPath()
    return CCSizeUtils.getPrintSize(size1 + size2)
  }

  //清理缓存，清理后返回清理后的大小，需要在异步线程执行
  fun clearCache(): String {
    //APP下载目录
    FileUtils.deleteAllInDir(PathUtils.getExternalAppFilesPath())
    //APP缓存目录
    FileUtils.deleteAllInDir(PathUtils.getExternalAppCachePath())
    return getCacheSize()
  }
}