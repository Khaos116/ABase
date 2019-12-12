package cc.abase.demo.component.playlist

import android.content.Context
import android.content.Intent
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommActivity
import cc.abase.demo.component.playlist.viewmoel.PlayPagerViewModel

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/12/12 11:33
 */
class PlayPagerActivity:CommActivity() {

  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, PlayPagerActivity::class.java)
      context.startActivity(intent)
    }
  }

  //数据层
  private val viewModel: PlayPagerViewModel by lazy {
    PlayPagerViewModel()
  }

  override fun layoutResId() = R.layout.activity_play_pager

  override fun initView() {
  }

  override fun initData() {
  }
}