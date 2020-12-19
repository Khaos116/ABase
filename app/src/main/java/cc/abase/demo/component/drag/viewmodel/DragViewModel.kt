package cc.abase.demo.component.drag.viewmodel

import cc.ab.base.mvrx.MvRxViewModel
import com.airbnb.mvrx.*
import com.luck.picture.lib.entity.LocalMedia

/**
 * Description:
 * @author: CASE
 * @date: 2019/11/30 20:59
 */
data class DragState(
  val medias: MutableList<LocalMedia> = mutableListOf(),
  val request: Async<Any> = Uninitialized
) : MvRxState

class DragViewModel(
  state: DragState = DragState()
) : MvRxViewModel<DragState>(state) {
  //当前选中的图片
  private var selMediaList = mutableListOf<LocalMedia>()

  fun getSelMediaList(): MutableList<LocalMedia> {
    return selMediaList
  }

  //设置选中的图片
  fun setSelectMedias(list: MutableList<LocalMedia>?) {
    withState { state ->
      setState {
        copy(
            medias = if (list.isNullOrEmpty()) {
              mutableListOf()
            } else {
              selMediaList = list
              list
            }
        )
      }
    }
  }

  //删除选中
  fun removeSelect(media: LocalMedia) {
    withState { state ->
      selMediaList = mutableListOf()
      selMediaList.addAll(state.medias)
      selMediaList.remove(media)
      setState { copy(medias = selMediaList) }
    }
  }

  /**
   * 重新排序
   */
  fun moveBean(
    bean: LocalMedia,
    fromPosition: Int,
    toPosition: Int
  ) {
    withState { state ->
      val index = state.medias.indexOf(bean)
      //这里需要注意，一定要重新定义一个list，只是单纯的调整list的顺序不会刷新，因为数据还是原内存没有变化
      selMediaList = mutableListOf()
      selMediaList.addAll(state.medias)
      selMediaList.add(index + (toPosition - fromPosition), selMediaList.removeAt(index))
      setState { copy(medias = selMediaList) }
    }
  }
}
