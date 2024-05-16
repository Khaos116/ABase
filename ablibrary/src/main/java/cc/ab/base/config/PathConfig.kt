package cc.ab.base.config

import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import java.io.File

/**
 * Author:Khaos
 * Date:2020-10-6
 * Time:16:05
 */
object PathConfig {
  //网络视频封面
  val VIDEO_OVER_CACHE_DIR = PathUtils.getExternalAppPicturesPath() + File.separator + "VideoCover"

  //视频缓存地址
  val VIDEO_CACHE_DIR: String = PathUtils.getExternalAppMoviesPath() + File.separator + "VideoExo"

  //图片缓存地址
  val IMG_CACHE_DIR = PathUtils.getExternalAppCachePath() + File.separator + "CoilImgCache"

  //接口数据缓存地址
  val API_CACHE_DIR = PathUtils.getInternalAppFilesPath() + File.separator + "RxHttpCache"

  //异常数据目录
  val CRASH_CACHE_DIR = PathUtils.getExternalAppFilesPath() + File.separator + "Crash"

  //文件下载目录
  val DOWNLOAD_DIR = PathUtils.getExternalAppFilesPath() + File.separator + "Download"

  //临时文件存放目录，如压缩图片、压缩视频等
  val TEMP_IMG_DIR = PathUtils.getExternalAppCachePath() + File.separator + "TempImg"
  val TEMP_VIDEO_DIR = PathUtils.getExternalAppCachePath() + File.separator + "TempVideo"

  fun initCacheDir() {
    FileUtils.createOrExistsDir(VIDEO_OVER_CACHE_DIR)
    FileUtils.createOrExistsDir(VIDEO_CACHE_DIR)
    FileUtils.createOrExistsDir(API_CACHE_DIR)
    FileUtils.createOrExistsDir(CRASH_CACHE_DIR)
    FileUtils.createOrExistsDir(DOWNLOAD_DIR)
    FileUtils.createOrExistsDir(TEMP_IMG_DIR)
    FileUtils.createOrExistsDir(TEMP_VIDEO_DIR)
  }
}