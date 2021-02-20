package cc.abase.demo.component.drag

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import cc.ab.base.ext.*
import cc.ab.base.widget.engine.ImageEngine
import cc.abase.demo.R
import cc.abase.demo.component.comm.CommTitleActivity
import cc.abase.demo.drag.GridItemTouchHelperCallback
import cc.abase.demo.item.NineImgItem
import cc.abase.demo.widget.decoration.GridSpaceItemDecoration
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.StringUtils
import com.drakeet.multitype.MultiTypeAdapter
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_drag.dragRecycler
import kotlinx.android.synthetic.main.activity_drag.dragRoot

/**
 * Description:item拖拽效果
 * @author: CASE
 * @date: 2019/11/30 20:48
 */
class DragActivity : CommTitleActivity() {
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

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResContentId() = R.layout.activity_drag
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initContentView() {
    setTitleText(StringUtils.getString(R.string.title_drag))
    dragRecycler.layoutManager = GridLayoutManager(mContext, 3)
    dragRecycler.setPadding(spaceItem, dragRecycler.paddingTop, spaceItem, dragRecycler.paddingBottom)
    if (dragRecycler.itemDecorationCount > 0) dragRecycler.removeItemDecorationAt(0)
    dragRecycler.addItemDecoration(GridSpaceItemDecoration(spaceItem).setDragGridEdge(false))
    //适配器注册
    multiTypeAdapter.register(NineImgItem(
        onDelClick = { url, p, v -> removeSelect(url) },
        onItemChildClick = { url, p, v -> if (url.isBlank()) go2ImgSel() else showPic(url) }, //为空代表点击选择添加
    ))
    //默认+
    val items = mutableListOf<Any>()
    items.add("")
    multiTypeAdapter.items = items
    dragRecycler.adapter = multiTypeAdapter
    val mParent = dragRoot
    //拖拽
    ItemTouchHelper(GridItemTouchHelperCallback(
        mAdapter = multiTypeAdapter,
        dragBgColor = Color.parseColor("#f7f7f7"),
        dragStart = { mParent.bringChildToFront(dragRecycler) },
        dragEnd = {
          dragRecycler.removeParent()
          mParent.addView(dragRecycler, 0)
        },
        canMove = { p ->
          val hasAdd = multiTypeAdapter.items.toMutableList().any { a -> a is String && a.isBlank() }
          if (!hasAdd) true else p < multiTypeAdapter.items.size - 1
        })).attachToRecyclerView(dragRecycler)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化Data">
  override fun initData() {
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="去选图">
  private fun go2ImgSel() {
    val datas = multiTypeAdapter.items.toMutableList().filter { f -> f is String && f.isNotBlank() }
    val list = mutableListOf<LocalMedia>()
    datas.forEach { d -> list.add(LocalMedia().also { lm -> lm.path = d as String }) }
    //https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-Api%E8%AF%B4%E6%98%8E
    PictureSelector.create(this)
        .openGallery(PictureMimeType.ofImage())
        .imageEngine(ImageEngine())
        .isGif(false)
        .isPageStrategy(true, PictureConfig.MAX_PAGE_SIZE, true) //过滤掉已损坏的图片
        .selectionData(list) //过滤掉添加操作的图片
        .maxSelectNum(MAX_IMG_SIZE)
        .queryMaxFileSize(5f)
        .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        .forResult(PictureConfig.CHOOSE_REQUEST)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据处理">
  //设置选中的图片
  private fun setSelectMedias(list: MutableList<LocalMedia>?) {
    list?.let { l ->
      val items = mutableListOf<Any>()
      items.addAll(l.flatMap { f -> listOf(f.path) })
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

  //<editor-fold defaultstate="collapsed" desc="选择图片的回调">
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    data?.let {
      if (requestCode == PictureConfig.CHOOSE_REQUEST && resultCode == Activity.RESULT_OK) {
        // 图片、视频、音频选择结果回调
        PictureSelector.obtainMultipleResult(data)?.let { medias ->
          if (medias.isNotEmpty()) setSelectMedias(medias)
        }
      } else "onActivityResult:other".logE()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="预览图片">
  //预览图片
  private fun showPic(url: String) {
    val datas = multiTypeAdapter.items.toMutableList().filter { f -> f is String && f.isNotBlank() }
    val index = datas.indexOfFirst { f -> f == url }
    val list = mutableListOf<LocalMedia>()
    datas.forEach { d -> list.add(LocalMedia().also { lm -> lm.path = d as String }) }
    //https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-Api%E8%AF%B4%E6%98%8E
    PictureSelector.create(this)
        .themeStyle(R.style.picture_default_style)
        .isNotPreviewDownload(true)
        .imageEngine(ImageEngine())
        .openExternalPreview2(0.coerceAtLeast(index), list)
  }
  //</editor-fold>
}