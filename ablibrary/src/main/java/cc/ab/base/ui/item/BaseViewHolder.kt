package cc.ab.base.ui.item

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding

/**
 * @Description
 * @Author：CASE
 * @Date：2021/2/19
 * @Time：16:43
 */
class BaseViewHolder<T : ViewBinding>(val viewBinding: T) : ViewHolder(viewBinding.root)