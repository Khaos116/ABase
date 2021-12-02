package cc.ab.base.ext

import android.content.res.Resources
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.min

//<editor-fold defaultstate="collapsed" desc="尺寸转换">
//dp->px
fun Number.dp2px(): Int {
  val width = min(Resources.getSystem().displayMetrics.widthPixels, Resources.getSystem().displayMetrics.heightPixels)
  return (this.toFloat() * (width / 360f)).toInt()
}

//sp->px
fun Number.sp2px(): Int {
  val width = min(Resources.getSystem().displayMetrics.widthPixels, Resources.getSystem().displayMetrics.heightPixels)
  return (this.toFloat() * (width / 360f)).toInt()
}

//px->dp
fun Number.px2dp(): Int {
  val width = min(Resources.getSystem().displayMetrics.widthPixels, Resources.getSystem().displayMetrics.heightPixels)
  return (this.toFloat() * (width / 360f)).toInt()
}

//px->sp
fun Number.px2sp(): Int {
  val width = min(Resources.getSystem().displayMetrics.widthPixels, Resources.getSystem().displayMetrics.heightPixels)
  return (this.toFloat() * (width / 360f)).toInt()
}
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="保留小数">
//保留2位小数(根据第三位四舍五入)
fun Number?.to2pointUp(): String {
  return if (this == null) "0.00" else DecimalFormat("#0.00").also { it.roundingMode = RoundingMode.HALF_UP }.format(this.toDouble())
}

//保留2位小数(去掉后面的)
fun Number?.to2pointDown(): String {
  return if (this == null) "0.00" else DecimalFormat("#0.00").also { it.roundingMode = RoundingMode.DOWN }.format(this.toDouble())
}

//保留2位小数+逗号分隔
fun Number?.to2point2(): String {
  return if (this == null) "0.00" else DecimalFormat("###,###,###,###,##0.00").format(this.toDouble())
}

//保留2位小数
fun String?.to2point(): String {
  return if (this.isNullOrBlank()) "0.00" else try {
    DecimalFormat("#0.00").format(this.toDouble())
  } catch (e: Exception) {
    this
  }
}

//保留2位小数+逗号分隔
fun String?.to2point2(): String {
  return if (this.isNullOrBlank()) "0.00" else try {
    DecimalFormat("###,###,###,###,##0.00").format(this.toDouble())
  } catch (e: Exception) {
    this
  }
}

//小于10显示0开头
fun Number?.toStart0(): String {
  return when {
    this == null -> "00"
    this.toDouble() < 10.0 -> String.format("0%s", this)
    else -> this.toString()
  }
}

//删除默尾0
fun Number?.delEnd0(): String {
  return if (this == null) "0" else DecimalFormat("#########0.##########").format(this.toDouble())
}
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="转为银行卡格式">
//转为银行卡格式,4位一个空格
fun String?.toBankNumber(): String {
  return if (this.isNullOrBlank()) "" else this.replace(" ", "").replace("(.{4})".toRegex(), "$1 ").trim()
}
//</editor-fold>
