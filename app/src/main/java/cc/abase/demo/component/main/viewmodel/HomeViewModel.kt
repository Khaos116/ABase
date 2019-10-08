package cc.abase.demo.component.main.viewmodel

import cc.ab.base.mvrx.MvRxViewModel
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import com.cc.base.repository.emum.MainType

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/9/30 16:36
 */
data class MainState(
  val types: MutableList<MainType> = mutableListOf(),
  var request: Async<Any> = Uninitialized
) : MvRxState

class HomeViewModel(
  state: MainState = MainState()
) : MvRxViewModel<MainState>(state) {
  private val list: MutableList<MainType> = mutableListOf()

  init {
    list.add(MainType.RECYCLER_DRAG)
    list.add(MainType.HTTP_REQUEST)
    list.add(MainType.CORNER_IMAGEVIEW)
    list.add(MainType.SMART_REFRESH)
    list.add(MainType.FILE_DOWNLOAD)
  }

  fun getMenuList() {
    setState { copy(types = list) }
  }
}