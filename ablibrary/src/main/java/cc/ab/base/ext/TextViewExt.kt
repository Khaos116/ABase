package cc.ab.base.ext

import android.annotation.SuppressLint
import android.widget.TextView
import cc.ab.base.R
import java.text.DecimalFormat
import kotlin.math.max

/**
 * @Description
 * @Author：Khaos
 * @Date：2021/1/6
 * @Time：12:36
 */
//不保留末尾为0的数据
fun TextView.setNumberNo00(num: Double) {
    val number = max(0, num.toLong())
    text = if (number > 9999f) {
        val tenThousand = number / 10000 //万
        val thousand = (number % 10000) / 1000 //千
        if (thousand > 0) {
            String.format("%s.%sw", tenThousand, thousand)
        } else {
            String.format("%dw", tenThousand)
        }
    } else {
        val decimalFormat = DecimalFormat("#########0.##########")
        decimalFormat.format(num)
    }
}

//不保留末尾为0的数据
fun TextView.getNumberNo00ZH(num: Double): String {
    val number = max(0, num.toLong())
    return if (number > 9999f) {
        val tenThousand = number / 10000 //万
        val thousand = (number % 10000) / 1000 //千
        if (thousand > 0) {
            String.format("%s.%${R.string.万.xmlToString()}", tenThousand, thousand)
        } else {
            String.format("%d${R.string.万.xmlToString()}", tenThousand)
        }
    } else {
        val decimalFormat = DecimalFormat("#########0.##########")
        decimalFormat.format(num)
    }
}

//保留2位小数
@SuppressLint("SetTextI18n")
fun TextView.setNumber2Point(num: String, prefix: String = "", suffix: String = "") {
    val decimalFormat = DecimalFormat("#0.00")
    text = prefix + try {
        decimalFormat.format(num.toDoubleMy())
    } catch (e: Exception) {
        num
    } + suffix
}

//设置2位数显示
fun TextView.setNumberStart0(number: Int) {
    text = if (number < 10) String.format("0%s", number) else number.toString()
}