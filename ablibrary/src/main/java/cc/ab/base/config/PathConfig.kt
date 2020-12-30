package cc.ab.base.config

import coil.util.CoilUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.Utils
import java.io.File

/**
 * Author:CASE
 * Date:2020-10-6
 * Time:16:05
 */
class PathConfig {
  companion object {
    //网络视频封面
    val VIDEO_OVER_CACHE_DIR = PathUtils.getExternalAppPicturesPath() + File.separator + "VideoCover"

    //视频缓存地址
    val VIDEO_CACHE_DIR: String = PathUtils.getExternalAppMoviesPath()

    //图片缓存地址
    val IMG_CACHE_DIR = CoilUtils.createDefaultCache(Utils.getApp()).directory

    //接口数据缓存地址
    val API_CACHE_DIR = PathUtils.getExternalAppCachePath() + File.separator + "RxHttpCache"

    //异常数据目录
    val CRASH_CACHE_DIR = PathUtils.getExternalAppFilesPath() + File.separator + "Crash"

    //临时文件存放目录，如压缩图片等
    val TEMP_CACHE_DIR = PathUtils.getExternalAppFilesPath() + File.separator + "Temp"
  }
}