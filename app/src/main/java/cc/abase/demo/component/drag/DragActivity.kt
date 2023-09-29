package cc.abase.demo.component.drag

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import cc.ab.base.ext.mContext
import cc.ab.base.ext.removeParent
import cc.ab.base.widget.engine.CoilEngine
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommBindTitleActivity
import cc.abase.demo.databinding.ActivityDragBinding
import cc.abase.demo.drag.GridItemTouchHelperCallback
import cc.abase.demo.item.NineImgItem
import cc.abase.demo.widget.decoration.GridItemDecoration
import com.blankj.utilcode.constant.MemoryConstants
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.StringUtils
import com.drakeet.multitype.MultiTypeAdapter
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener


/**
 * Description:item拖拽效果
 * @author: Khaos
 * @date: 2019/11/30 20:48
 */
class DragActivity : CommBindTitleActivity<ActivityDragBinding>() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, DragActivity::class.java)
      context.startActivity(intent)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">

  //最大图片数量
  private val MAX_IMG_SIZE = 9

  //Item间距
  private val spaceItem = SizeUtils.dp2px(6f)

  //适配器
  private val multiTypeAdapter = MultiTypeAdapter()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.九宫格拖拽效果))
    viewBinding.dragRecycler.layoutManager = GridLayoutManager(mContext, 3)
    viewBinding.dragRecycler.setPadding(0, viewBinding.dragRecycler.paddingTop, 0, viewBinding.dragRecycler.paddingBottom)
    if (viewBinding.dragRecycler.itemDecorationCount > 0) viewBinding.dragRecycler.removeItemDecorationAt(0)
    viewBinding.dragRecycler.addItemDecoration(GridItemDecoration(spaceItem, canDrag = true))
    //适配器注册
    multiTypeAdapter.register(
      NineImgItem(
        onDelClick = { url, p, v -> removeSelect(url) },
        onItemChildClick = { url, p, v -> if (url.isBlank()) go2ImgSel() else showPic(url) }, //为空代表点击选择添加
      )
    )
    //默认+
    val items = mutableListOf<Any>()
    items.add("")
    multiTypeAdapter.items = items
    viewBinding.dragRecycler.adapter = multiTypeAdapter
    val mParent = viewBinding.root
    //拖拽
    ItemTouchHelper(
      GridItemTouchHelperCallback(
        mAdapter = multiTypeAdapter,
        dragBgColor = Color.parseColor("#f7f7f7"),
        dragStart = { mParent.bringChildToFront(viewBinding.dragRecycler) },
        dragEnd = {
          viewBinding.dragRecycler.removeParent()
          mParent.addView(viewBinding.dragRecycler, 0)
        },
        canMove = { p ->
          val hasAdd = multiTypeAdapter.items.toMutableList().any { a -> a is String && a.isBlank() }
          if (!hasAdd) true else p < multiTypeAdapter.items.size - 1
        })
    ).attachToRecyclerView(viewBinding.dragRecycler)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="去选图">
  @SuppressLint("SourceLockedOrientationActivity")
  private fun go2ImgSel() {
    val datas = multiTypeAdapter.items.toMutableList().filter { f -> f is String && f.isNotBlank() }
    val list = mutableListOf<LocalMedia>()
    datas.forEach { d -> list.add(LocalMedia().also { lm -> lm.path = d as String }) }
    //https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-3.0-%E5%8A%9F%E8%83%BDapi%E8%AF%B4%E6%98%8E
    PictureSelector.create(this)
      .openGallery(SelectMimeType.ofImage())
      .setImageEngine(CoilEngine())
      .isGif(false)
      .isPageStrategy(true, PictureConfig.MAX_PAGE_SIZE, true) //过滤掉已损坏的图片
      .setSelectedData(list) //过滤掉添加操作的图片
      .setMaxSelectNum(MAX_IMG_SIZE)
      .setFilterMaxFileSize(5L * MemoryConstants.MB)
      .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
      .forResult(object : OnResultCallbackListener<LocalMedia> {
        override fun onResult(result: ArrayList<LocalMedia>?) {
          if (!result.isNullOrEmpty()) setSelectMedias(result)
        }

        override fun onCancel() {
        }
      })
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据处理">
  //设置选中的图片
  private fun setSelectMedias(list: MutableList<LocalMedia>?) {
    list?.let { l ->
      val items = mutableListOf<Any>()
      items.addAll(l.flatMap { f -> listOf(f.availablePath) })
      if (items.size < MAX_IMG_SIZE) items.add("")
      multiTypeAdapter.items = items
      multiTypeAdapter.notifyDataSetChanged()
    }
  }

  //删除选中
  private fun removeSelect(url: String) {
    val list = multiTypeAdapter.items.toMutableList()
    val index = list.indexOfFirst { l -> l == url }
    if (index >= 0) {
      list.removeAt(index)
      var hasAdd = false
      if (list.isEmpty()) {
        list.add("")
        hasAdd = true
      } else if ((list[list.size - 1] as? String)?.isBlank() == false) {
        list.add("")
        hasAdd = true
      }
      multiTypeAdapter.items = list
      multiTypeAdapter.notifyItemRemoved(index)
      if (hasAdd) multiTypeAdapter.notifyItemInserted(list.size - 1)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="预览图片">
  //预览图片
  private fun showPic(url: String) {
    val datas = multiTypeAdapter.items.toMutableList().filter { f -> f is String && f.isNotBlank() }
    val index = datas.indexOfFirst { f -> f == url }
    val list = arrayListOf<LocalMedia>()
    datas.forEach { d -> list.add(LocalMedia().also { lm -> lm.path = d as String }) }
    //https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-3.0-%E5%8A%9F%E8%83%BDapi%E8%AF%B4%E6%98%8E
    PictureSelector.create(this)
      .openPreview()
      .isHidePreviewDownload(true)
      .setImageEngine(CoilEngine())
      .startActivityPreview(0.coerceAtLeast(index), false, list)
  }
  //</editor-fold>
}