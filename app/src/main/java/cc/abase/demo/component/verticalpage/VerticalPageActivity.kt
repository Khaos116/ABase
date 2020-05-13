package cc.abase.demo.component.verticalpage

import android.content.Context
import android.content.Intent
import cc.ab.base.utils.RxUtils
import cc.ab.base.widget.discretescrollview.DSVOrientation
import cc.ab.base.widget.discretescrollview.adapter.DiscretePageAdapter
import cc.abase.demo.R
import cc.abase.demo.bean.local.VerticalPageBean
import cc.abase.demo.component.comm.CommTitleActivity
import com.blankj.utilcode.util.StringUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activty_verticalpage.vvpDSV
import java.util.concurrent.TimeUnit

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/5/13 9:23
 */
class VerticalPageActivity : CommTitleActivity() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, VerticalPageActivity::class.java)
      context.startActivity(intent)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量区">
  //数据
  private var mDatas = mutableListOf<VerticalPageBean>()

  //适配器
  private var pageAdapter: DiscretePageAdapter<VerticalPageBean> = DiscretePageAdapter(VerticalPageHolderCreator(), mDatas)

  //请求
  private var disposableRequest: Disposable? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResContentId() = R.layout.activty_verticalpage
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="View初始化">
  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.title_vertical_page))
    vvpDSV.setOrientation(DSVOrientation.VERTICAL)
    vvpDSV.addOnItemChangedListener { viewHolder, adapterPosition ->

    }
    vvpDSV.adapter = pageAdapter
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据初始化">
  override fun initData() {
    loadData {
      mDatas.addAll(it)
      pageAdapter.notifyDataSetChanged()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="模拟数据获取">
  private fun loadData(lastId: Long = 0, call: ((list: MutableList<VerticalPageBean>) -> Unit)? = null) {
    if (disposableRequest != null && disposableRequest?.isDisposed == false) return
    disposableRequest = Observable.timer(1500, TimeUnit.MILLISECONDS).flatMap {
      val list = mutableListOf<VerticalPageBean>()
      for (i in lastId until lastId + 20) list.add(VerticalPageBean(id = i + 1, description = "测试数据${i + 1}"))
      Observable.just(list)
    }.compose(RxUtils.instance.rx2SchedulerHelperO(lifecycleProvider)).subscribe { call?.invoke(it) }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="释放">
  override fun finish() {
    super.finish()
    disposableRequest?.dispose()
  }
  //</editor-fold>
}