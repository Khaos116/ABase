package cc.abase.demo.widget.dkplayer

import android.annotation.SuppressLint
import android.content.Context
import xyz.doikki.videoplayer.exo.ExoMediaPlayer

/**
 * 重写缓存配置，以适配http开始地址的适配缓存(只简单判断了http，如果是直播地址可能存在问题，没有测试，不清楚是否存在问题)
 * 注：1.可以缓存m3u8的在线适配  2.如果有http开始的直播地址需要修改缓存策略
 * Author:Khaos
 * Date:2020/12/23
 * Time:12:40
 */
class MyExoMediaPlayer(context: Context) : ExoMediaPlayer(context) {
  //<editor-fold defaultstate="collapsed" desc="重写是为了视频的边播边存">
  @SuppressLint("DefaultLocale")
  override fun setDataSource(path: String?, headers: MutableMap<String, String>?) {
    mMediaSource = mMediaSourceHelper.getMediaSource(path, headers, path?.toLowerCase()?.startsWith("http") == true)
  }
  //</editor-fold>
}