package cc.abase.demo.rxhttp.repository

/**
 * Author:Khaos
 * Date:2023/6/27
 * Time:14:21
 */
abstract class BaseRepository {
  //<editor-fold defaultstate="collapsed" desc="基础参数封装">
  fun getBaseParams(): LinkedHashMap<String, Any> {
    val maps = linkedMapOf<String, Any>().apply {
      //put("os", "Android")
    }
    return maps
  }
  //</editor-fold>
}