package cc.abase.demo.config

import androidx.lifecycle.MutableLiveData

/**
 * Author:Khaos116
 * Date:2022/12/24
 * Time:18:50
 */
object AppLiveData {
  //左右滑动监听
  val frameLayoutScrollLiveData = MutableLiveData<Pair<Int, Boolean>>()
}