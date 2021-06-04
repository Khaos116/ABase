package cc.abase.demo.widget.dkplayer

import android.content.Context
import xyz.doikki.videoplayer.player.PlayerFactory

/**
 * VideoViewManager需要设置为自己的播放器工厂
 * Author:CASE
 * Date:2020/12/23
 * Time:15:29
 */
class MyExoPlayerFactory : PlayerFactory<MyExoMediaPlayer>() {
  override fun createPlayer(context: Context) = MyExoMediaPlayer(context)
}