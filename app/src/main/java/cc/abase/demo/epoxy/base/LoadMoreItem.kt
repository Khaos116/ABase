package cc.abase.demo.epoxy.base

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import cc.ab.base.ext.*
import cc.ab.base.ui.holder.BaseEpoxyHolder
import cc.abase.demo.R
import cc.abase.demo.utils.NetUtils
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.blankj.utilcode.util.StringUtils
import kotlinx.android.synthetic.main.item_comm_loadmore.view.loadMorePb
import kotlinx.android.synthetic.main.item_comm_loadmore.view.loadMoreTv

/**
 * Description:由于EpoxyRecyclerView使用SmartRefresh的加载更多会导致数据越界，所以单独写
 * @author: CASE
 * @date: 2019/10/1 18:36
 */
@EpoxyModelClass(layout = R.layout.item_comm_loadmore)
abstract class LoadMoreItem : BaseEpoxyModel<BaseEpoxyHolder>() {

  //提示的文字，没有默认提示 默认为："数据加载中..."
  @EpoxyAttribute
  var tipsText: CharSequence? = null

  //是否加载失败
  @EpoxyAttribute
  var fail: Boolean = false

  //提示TextView的样式
  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var textStyle: (TextView.() -> Unit)? = null

  //提示ProgressBar的样式
  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var progressBarStyle: (ProgressBar.() -> Unit)? = null

  //加载更多的回调
  @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
  var onLoadMore: (() -> Unit)? = null

  override fun onBind(itemView: View) {

    textStyle?.let {
      itemView.loadMoreTv.apply(it)
    }
    progressBarStyle?.let {
      itemView.loadMorePb.apply(it)
    }
    setLoadState(itemView)
    itemView.click {
      if (fail && onLoadMore != null && NetUtils.instance.checkToast()) {
        fail = false
        setLoadState(itemView)
        onLoadMore?.invoke()
      }
    }
  }

  //根据成功和失败显示不同ui
  private fun setLoadState(itemView: View) {
    itemView.loadMoreTv.text = if (!tipsText.isNullOrBlank()) tipsText
    else {
      StringUtils.getString(
          if (fail) {
                R.string.load_more_fail
              } else {
                R.string.load_more_info
              }
          )
    }
    if (fail) {
      itemView.loadMorePb.invisible()
    } else {
      itemView.loadMorePb.visible()
    }
  }

  override fun onVisibilityChanged(
    percentVisibleHeight: Float,
    percentVisibleWidth: Float,
    visibleHeight: Int,
    visibleWidth: Int,
    view: BaseEpoxyHolder
  ) {
    super.onVisibilityChanged(
      percentVisibleHeight,
      percentVisibleWidth,
      visibleHeight,
      visibleWidth,
      view
    )
    if (percentVisibleHeight == 100f) {
      //加载更多完全显示出来
      onLoadMore?.invoke()
    }
  }
}