package cc.abase.demo.utils

import com.tencent.mmkv.MMKV

/**
 * Description:
 * @author: CASE
 * @date: 2019/10/10 15:14
 */
class MMkvUtils private constructor() {
    private object SingletonHolder {
        val holder = MMkvUtils()
    }

    companion object {
        val instance = SingletonHolder.holder
    }

    private val GUIDE_SPLASH = "KKMV_KEY_GUIDE_SPLASH"
    private val USER_UID = "KKMV_KEY_USER_UID"
    private val USER_TOKEN = "KKMV_KEY_USER_TOKEN"
    private val USER_ACCOUNT = "USER_ACCOUNT"
    private val USER_PWD = "USER_PWD"
    fun setNeedGuide(need: Boolean = true) {
        MMKV.defaultMMKV().encode(GUIDE_SPLASH, need)
    }

    fun getNeedGuide(): Boolean {
        return MMKV.defaultMMKV().decodeBool(GUIDE_SPLASH, true)
    }

    fun getUid(): Long {
        return MMKV.defaultMMKV().decodeLong(USER_UID, 0L)
    }

    fun setAccount(account: String) {
        MMKV.defaultMMKV().encode(USER_ACCOUNT, account)
    }

    fun setPassword(pwd: String) {
        MMKV.defaultMMKV().encode(USER_PWD, pwd)
    }

    fun getAccount(): String {
        return MMKV.defaultMMKV().decodeString(USER_ACCOUNT, "")
    }

    fun getPassword(): String {
        return MMKV.defaultMMKV().decodeString(USER_PWD, "")
    }

    fun setUid(uid: Long) {
        MMKV.defaultMMKV().encode(USER_UID, uid)
    }

    fun getToken(): String? {
        return MMKV.defaultMMKV().decodeString(USER_TOKEN)
    }

    fun setToken(token: String) {
        MMKV.defaultMMKV().encode(USER_TOKEN, token)
    }

    fun clearUserInfo() {
        MMKV.defaultMMKV().removeValueForKey(USER_UID)
        MMKV.defaultMMKV().removeValueForKey(USER_TOKEN)
    }
}