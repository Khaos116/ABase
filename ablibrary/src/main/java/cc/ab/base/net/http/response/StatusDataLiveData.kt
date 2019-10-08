package cc.ab.base.net.http.response

import androidx.lifecycle.MediatorLiveData

/**
 * Description:数据类型为StatusData的liveData.
 * @author: caiyoufei
 * @date: 2019/9/22 18:57
 */
class StatusDataLiveData<T> : MediatorLiveData<StatusData<T>>()