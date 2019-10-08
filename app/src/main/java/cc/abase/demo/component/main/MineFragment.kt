package cc.abase.demo.component.main

import android.util.Log
import android.view.View
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommFragment

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
  }

  override fun initData() {
    Log.e("CASE", "MineFragment-开始加载数据")
  }

}