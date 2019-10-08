package cc.abase.demo.component.main

import android.util.Log
import android.view.View
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommFragment
import cc.abase.demo.component.main.viewmodel.HomeViewModel

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/30 18:02
 */
class HomeFragment : CommFragment() {
  companion object {
    fun newInstance(): HomeFragment {
      return HomeFragment()
    }
  }

  //数据层
  private val viewModel: HomeViewModel by lazy {
    HomeViewModel()
  }

  override val contentLayout = R.layout.fragment_home

  override fun initView(root: View?) {
  }

  override fun initData() {
    Log.e("CASE", "HomeFragment-开始加载数据")
    viewModel.subscribe { postInvalidate() }
    viewModel.getMenuList()
  }
}