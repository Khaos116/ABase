package cc.abase.demo.component.main

import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.View
import cc.ab.base.ext.*
import cc.abase.demo.R
import cc.abase.demo.component.chat.ChatActivity
import cc.abase.demo.component.comm.CommActivity
import cc.abase.demo.component.comm.CommFragment
import cc.abase.demo.component.ffmpeg.RxFFmpegActivity
import cc.abase.demo.epoxy.base.dividerItem
import cc.abase.demo.epoxy.item.simpleTextItem
import cc.abase.demo.mvrx.MvRxEpoxyController
import cc.abase.demo.repository.UserRepository
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.StringUtils
import kotlinx.android.synthetic.main.fragment_mine.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/30 18:13
 */
class MineFragment : CommFragment() {
  //菜单列表
  val menuList = mutableListOf(
      Pair(StringUtils.getString(R.string.chat_title), ChatActivity::class.java),
      Pair(StringUtils.getString(R.string.ffmpeg_title), RxFFmpegActivity::class.java)
  )
  //item文字颜色
  private var typeColor = ColorUtils.getColor(R.color.style_Primary)

  companion object {
    fun newInstance(): MineFragment {
      return MineFragment()
    }
  }

  override val contentLayout = R.layout.fragment_mine

  override fun initView(root: View?) {
    mineRecycler.setController(epoxyController)
  }

  override fun initData() {
    showLoadingView()
    mineRoot.gone()
    val dis = UserRepository.instance.myIntegral()
        .subscribe { t1, t2 ->
          dismissLoadingView()
          mineRoot.visible()
          if (t1 != null) {
            mineIntegral.text =
              String.format(StringUtils.getString(R.string.my_integral), t1.coinCount)
          } else if (t2 != null) {
            mineIntegral.text =
              String.format(StringUtils.getString(R.string.my_integral), 0)
            mContext.toast(t2.message)
          }
          epoxyController.data = menuList
        }
  }

  //epoxy
  private val epoxyController =
    MvRxEpoxyController<List<Pair<String, Class<out CommActivity>>>> { list ->
      list.forEachIndexed { index, pair ->
        //内容
        simpleTextItem {
          id("type_$index")
          msg(pair.first)
          textColor(typeColor)
          gravity(Gravity.CENTER_VERTICAL)
          onItemClick { mActivity.startActivity(Intent(mContext, pair.second)) }
        }
        //分割线
        dividerItem {
          id("line_$index")
        }
      }
    }
}