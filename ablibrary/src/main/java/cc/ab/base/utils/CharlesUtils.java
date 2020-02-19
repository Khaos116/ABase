package cc.ab.base.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.Nullable;
import com.blankj.utilcode.util.Utils;
import java.io.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.*;
import java.util.Arrays;
import java.util.Collection;
import javax.net.ssl.*;
import okhttp3.OkHttpClient;

/**
 * 参考：https://www.jianshu.com/p/cc7ae2f96b64
 * app抓包辅助，抓包条件：
 * 1.需要在sd卡下面保存charles的pem文件，文件名称为charles.pem
 * 2.需要SD卡读取权限
 * 3.尽量只对测试渠道的设置抓包
 * 4.如果OkHttpClient是单利，启动APP后才设置代理，则需要杀掉APP后重新打开
 * 5.如果打开了代理，又不能正确读取设置代理，则会出现无法访问的情况
 * Author:caiyoufei
 * Date:19-7-16
 * Time:下午4:49
 */
public class CharlesUtils {
  private static class SingleTonHolder {
    private static final CharlesUtils INSTANCE = new CharlesUtils();
  }

  public static CharlesUtils getInstance() {
    return SingleTonHolder.INSTANCE;
  }

  private CharlesUtils() {
  }

  /**
   * 获取chales的pem文件流
   *
   * @param sdRootPemName sd卡下面的pem文件名称
   * @return pem文件流
   */
  public FileInputStream getCharlesInputStream(String sdRootPemName) {
    if (!isWifiProxy()) {
      Log.e("CharlesUtils", "没有使用代理，不用抓包");
      return null;
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
        Utils.getApp().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
    ) {
      try {
        File pemFile = new File(Environment.getExternalStorageDirectory(), sdRootPemName);
        if (pemFile.exists()) {
          return new FileInputStream(pemFile);
        } else {
          Log.e("CharlesUtils", "SD卡没有pem文件");
          return null;
        }
      } catch (FileNotFoundException e) {
        Log.e("CharlesUtils", "读取SD卡pem文件失败");
        e.printStackTrace();
        return null;
      }
    } else {
      Log.e("CharlesUtils", "没有SD卡权限读取pem文件");
      return null;
    }
  }

  /**
   * 是否使用了代理
   */
  private boolean isWifiProxy() {
    String proxyAddress;
    int proxyPort;
    proxyAddress = System.getProperty("http.proxyHost");
    String portStr = System.getProperty("http.proxyPort");
    proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
    return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
  }

  /**
   * 为okhttp客户端设置抓包验证
   *
   * @param builder okhttp客户端builder
   * @param certificate 自签名证书的输入流
   */
  public void setOkHttpCharlesSSL(OkHttpClient.Builder builder, InputStream certificate) {
    try {
      if (builder == null || certificate == null) return;
      X509TrustManager trustManager = trustManagerForCertificates(certificate);
      if (trustManager != null) {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        //使用构建出的trustManger初始化SSLContext对象
        sslContext.init(null, new TrustManager[] { trustManager }, null);
        //获得sslSocketFactory对象
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        builder.sslSocketFactory(sslSocketFactory, trustManager);
        Log.e("CharlesUtils", "开启抓包");
      } else {
        Log.e("CharlesUtils", "验证pem文件失败");
      }
    } catch (Exception e) {
      e.printStackTrace();
      Log.e("CharlesUtils", "验证pem文件失败:" + e.getMessage());
    }
  }

  /**
   * 获取Fuel请求需抓包需要的SSLSocketFactory
   * @param certificate 自签名证书的输入流
   */
  @Nullable
  public SSLSocketFactory getFuelCharlesSSL(InputStream certificate) {
    try {
      if (certificate == null) return null;
      X509TrustManager trustManager = trustManagerForCertificates(certificate);
      if (trustManager != null) {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        //使用构建出的trustManger初始化SSLContext对象
        sslContext.init(null, new TrustManager[] { new ChainTrust(), trustManager },
            new SecureRandom());
        //获得sslSocketFactory对象
        return sslContext.getSocketFactory();
      }
      return null;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 获去信任自签证书的trustManager
   *
   * @param input 自签证书输入流
   * @return 信任自签证书的trustManager
   */
  private X509TrustManager trustManagerForCertificates(InputStream input) {
    try {
      CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
      //通过证书工厂得到自签证书对象集合
      Collection<? extends Certificate> certificates =
          certificateFactory.generateCertificates(input);
      if (certificates.isEmpty()) {
        throw new IllegalArgumentException("expected non-empty set of trusted certificates");
      }
      //为证书设置一个keyStore
      char[] password = "password".toCharArray(); // Any password will work.
      KeyStore keyStore = newEmptyKeyStore(password);
      if (keyStore == null) return null;
      int index = 0;
      //将证书放入keystore中
      for (Certificate certificate : certificates) {
        String certificateAlias = Integer.toString(index++);
        keyStore.setCertificateEntry(certificateAlias, certificate);
      }
      // Use it to build an X509 trust manager.
      //使用包含自签证书信息的keyStore去构建一个X509TrustManager
      KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
          KeyManagerFactory.getDefaultAlgorithm());
      keyManagerFactory.init(keyStore, password);
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
          TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(keyStore);
      TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
      if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
        throw new IllegalStateException("Unexpected default trust managers:"
            + Arrays.toString(trustManagers));
      }
      return (X509TrustManager) trustManagers[0];
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private KeyStore newEmptyKeyStore(char[] password) {
    try {
      KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
      keyStore.load(null, password);
      return keyStore;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  //解决接口时间不对的bug
  private class ChainTrust implements X509TrustManager {
    @SuppressLint("TrustAllX509TrustManager")
    @Override public void checkClientTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {

    }

    @SuppressLint("TrustAllX509TrustManager")
    @Override public void checkServerTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {

    }

    @Override public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[0];
    }
  }
}