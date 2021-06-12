package cc.ab.base.ui.item

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * @Description
 * @Author：Khaos
 * @Date：2021/2/19
 * @Time：16:43
 */
class BaseViewHolder<T : ViewBinding>(val viewBinding: T) : RecyclerView.ViewHolder(viewBinding.root)