package cc.abase.demo.component.main

import android.util.Log
import android.view.View
import cc.ab.base.ext.click
import cc.ab.base.ext.toast
import cc.ab.base.utils.PressEffectHelper
import cc.abase.demo.R
import cc.abase.demo.component.chat.ChatActivity
import cc.abase.demo.component.comm.CommFragment
import cc.abase.demo.component.ffmpeg.RxFFmpegActivity
import cc.abase.demo.repository.UserRepository
import kotlinx.android.synthetic.main.fragment_mine.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/30 18:13
 */
class MineFragment : CommFragment() {
  companion object {
    fun newInstance(): MineFragment {
      return MineFragment()
    }
  }

  override val contentLayout = R.layout.fragment_mine

  override fun initView(root: View?) {
    PressEffectHelper.bgColorEffect(myChatEmoji)
    myChatEmoji.click { ChatActivity.startActivity(mContext) }
    myFFmpeg.click { RxFFmpegActivity.startActivity(mContext) }
  }

  override fun initData() {
    showLoadingView()
    val dis = UserRepository.instance.myIntegral()
        .subscribe { t1, t2 ->
          dismissLoadingView()
          if (t1 != null) {
            myIntegral.text = String.format(mActivity.getString(R.string.my_integral), t1.coinCount)
          } else if (t2 != null) {
            mContext.toast(t2.message)
          }
        }
    Log.e("CASE", "MineFragment-开始加载数据")
  }

}