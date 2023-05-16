//package cc.abase.demo.utils.sm
//
//import com.blankj.utilcode.util.EncryptUtils
//
///**
// * SM国密算法工具类
// * implementation "org.bouncycastle:bcprov-jdk15on:1.70"
// * implementation "cn.hutool:hutool-crypto:5.8.18"
// *
// * https://github.com/dromara/hutool/blob/v5-master/hutool-crypto/src/main/java/cn/hutool/crypto/SmUtil.java
// * Author:Khaos
// * Date:2023/5/16
// * Time:10:19
// */
//object SMUtils {
//  fun test() {
//    val str = "{\n" +
//        "    \"region\":\"+86\",\n" +
//        "    \"memberName\":\"khaos116\",\n" +
//        "    \"password\":\"696d21dbe226d404f168a024a43d9433\"\n" +
//        "}";
//    val key = "192.168.220.6ANDROID||CHROM"
//    enCodeSM4(str, EncryptUtils.encryptMD5ToString(key).lowercase())
//  }
//
//  fun enCodeSM4(str: String, key: String): String {
//    return try {
//      SmUtil.sm4(HexUtil.decodeHex(key)).encryptHex(str)
//    } catch (e: Exception) {
//      e.printStackTrace()
//      ""
//    }
//  }
//
//  fun deCodeSM4(str: String, key: String): String {
//    return try {
//      SmUtil.sm4(HexUtil.decodeHex(key)).decryptStr(str, CharsetUtil.CHARSET_UTF_8)
//    } catch (e: Exception) {
//      e.printStackTrace()
//      ""
//    }
//  }
//
//  fun enCodeSM2(str: String, privateKey: String, publicKey: String): String {
//    return try {
//      SmUtil.sm2(privateKey, publicKey).encryptHex(str, KeyType.PublicKey)
//    } catch (e: Exception) {
//      e.printStackTrace()
//      ""
//    }
//  }
//
//  fun deCodeSM2(str: String, privateKey: String, publicKey: String): String {
//    return try {
//      SmUtil.sm2(privateKey, publicKey).decryptStr(str, KeyType.PrivateKey)
//    } catch (e: Exception) {
//      e.printStackTrace()
//      ""
//    }
//  }
//}