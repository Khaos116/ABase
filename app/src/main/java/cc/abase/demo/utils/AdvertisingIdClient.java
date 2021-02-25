package cc.abase.demo.utils;

import android.content.*;
import android.content.pm.PackageManager;
import android.os.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Description Google服务通过广告id获取设备唯一id【只有手机有谷歌服务才能获取到】
 * @Author：CASE
 * @Date：2021/2/25
 * @Time：15:48
 */
public class AdvertisingIdClient {
  /**
   * 这个方法是耗时的，不能在主线程调用
   */
  public static String getGoogleAdId(Context context) throws Exception {
    if (Looper.getMainLooper() == Looper.myLooper()) {
      return "Cannot call in the main thread, You must call in the other thread";
    }
    PackageManager pm = context.getPackageManager();
    pm.getPackageInfo("com.android.vending", 0);
    AdvertisingConnection connection = new AdvertisingConnection();
    Intent intent = new Intent(
        "com.google.android.gms.ads.identifier.service.START");
    intent.setPackage("com.google.android.gms");
    if (context.bindService(intent, connection, Context.BIND_AUTO_CREATE)) {
      try {
        AdvertisingInterface adInterface = new AdvertisingInterface(
            connection.getBinder());
        return adInterface.getId();
      } finally {
        context.unbindService(connection);
      }
    }
    return "";
  }

  private static final class AdvertisingConnection implements ServiceConnection {
    boolean retrieved = false;
    private final LinkedBlockingQueue<IBinder> queue = new LinkedBlockingQueue<>(1);

    public void onServiceConnected(ComponentName name, IBinder service) {
      try {
        this.queue.put(service);
      } catch (InterruptedException localInterruptedException) {
      }
    }

    public void onServiceDisconnected(ComponentName name) {
    }

    public IBinder getBinder() throws InterruptedException {
      if (this.retrieved)
        throw new IllegalStateException();
      this.retrieved = true;
      return this.queue.take();
    }
  }

  private static final class AdvertisingInterface implements IInterface {
    private IBinder binder;

    public AdvertisingInterface(IBinder pBinder) {
      binder = pBinder;
    }

    public IBinder asBinder() {
      return binder;
    }

    public String getId() throws RemoteException {
      Parcel data = Parcel.obtain();
      Parcel reply = Parcel.obtain();
      String id;
      try {
        data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
        binder.transact(1, data, reply, 0);
        reply.readException();
        id = reply.readString();
      } finally {
        reply.recycle();
        data.recycle();
      }
      return id;
    }

    public boolean isLimitAdTrackingEnabled(boolean paramBoolean)
        throws RemoteException {
      Parcel data = Parcel.obtain();
      Parcel reply = Parcel.obtain();
      boolean limitAdTracking;
      try {
        data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
        data.writeInt(paramBoolean ? 1 : 0);
        binder.transact(2, data, reply, 0);
        reply.readException();
        limitAdTracking = 0 != reply.readInt();
      } finally {
        reply.recycle();
        data.recycle();
      }
      return limitAdTracking;
    }
  }
}
