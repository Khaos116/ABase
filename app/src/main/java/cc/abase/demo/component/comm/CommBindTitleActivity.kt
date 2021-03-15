package cc.abase.demo.component.comm

import androidx.viewbinding.ViewBinding
import cc.ab.base.ext.click
import cc.ab.base.ext.pressEffectAlpha
import cc.abase.demo.databinding.LayoutCommTitleBinding

/**
 * @Description
 * @Author：CASE
 * @Date：2021/3/15
 * @Time：17:59
 */
abstract class CommBindTitleActivity<T : ViewBinding> : CommBindActivity<T>() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //子类操作标题栏可能需要用到
  protected lateinit var viewBindingTitle: LayoutCommTitleBinding
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="拦截添加标题栏">
  //重新后，添加标题栏，重新调用子类方法
  override fun initView() {
    viewBindingTitle = LayoutCommTitleBinding.inflate(layoutInflater)
    baseBinding.root.addView(viewBindingTitle.root, 1)
    //处理返回
    viewBindingTitle.commTitleBack.pressEffectAlpha()
    viewBindingTitle.commTitleBack.click { finish() }
    initContentView()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="设置标题、修改返回按钮等">
  //设置标题
  fun setTitleText(title: CharSequence) {
    viewBindingTitle.commTitleText.text = title
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="子类必须重新的方法">
  //执行初始化
  protected abstract fun initContentView()
  //</editor-fold>
}