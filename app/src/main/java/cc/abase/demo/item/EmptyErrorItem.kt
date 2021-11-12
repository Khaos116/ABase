package cc.abase.demo.item

import cc.ab.base.ext.*
import cc.ab.base.ui.item.BaseBindItemView
import cc.ab.base.ui.item.BaseViewHolder
import cc.abase.demo.R
import cc.abase.demo.bean.local.EmptyErrorBean
import cc.abase.demo.databinding.ItemEmptyErrorBinding
import cc.abase.demo.utils.NetUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.StringUtils

/**
 * Author:Khaos
 * Date:2020-11-25
 * Time:15:37
 */
class EmptyErrorItem(
  private val width: Int = 0,
  private val height: Int = 0,
  private val callRetry: (() -> Unit)? = null
) : BaseBindItemView<EmptyErrorBean, ItemEmptyErrorBinding>() {
  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: BaseViewHolder<ItemEmptyErrorBinding>, item: EmptyErrorBean) {
    val viewBinding = holder.viewBinding
    viewBinding.root.layoutParams.let { lp ->
      if (width > 0) lp.width = width
      if (height > 0) lp.height = height
    }
    viewBinding.itemEmptyErrorTv.text = if (!item.msg.isNullOrBlank()) {
      item.msg
    } else if (!item.isError) {
      StringUtils.getString(R.string.no_data)
    } else {
      if (NetworkUtils.isConnected()) {
        StringUtils.getString(R.string.net_fail_retry)
      } else {
        StringUtils.getString(R.string.net_error_retry)
      }
    }
    if (callRetry != null && item.isError) {
      viewBinding.itemEmptyErrorTv.pressEffectAlpha(0.9f)
      viewBinding.itemEmptyErrorTv.click {
        if (NetUtils.checkNetToast()) callRetry.invoke()
      }
    } else {
      viewBinding.itemEmptyErrorTv.pressEffectDisable()
      viewBinding.itemEmptyErrorTv.setOnClickListener(null)
    }
  }
  //</editor-fold>
}