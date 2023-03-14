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

  //滑动状态记录,多组滑动存在时使用
  val transStateMaps = hashMapOf<Int, Boolean>()
}