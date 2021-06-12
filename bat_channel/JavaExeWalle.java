package x.x.x;

import java.io.File;
import java.io.IOException;

/**
 * Main方法无法运行的解决方案：添加delegatedBuild
 * project模式->.idea文件夹->gradle.xml
 * <GradleProjectSettings>
 *    <option name="delegatedBuild" value="false"/>
 * </GradleProjectSettings>
 *
 * @Description JAVA实现美团多渠道打包(供服务器参考)
 * @Author：Khaos
 * @Date：2021/3/9
 * @Time：18:12
 */
public class JavaExeWalle {
  //服务器上最新APK的版本号【需要为最新的版本】
  private static int serviceApkVersion = 101;
  //walle-cli-all.jar的存放目录
  private static String pathJar = "C:\\Users\\Administrator\\Desktop\\APK\\walle-cli-all.jar";
  //项目最新版本的APK存放地址，每次发版后更换为最新的APK地址【需要为最新的APK】
  private static String pathOriginApk = "C:\\Users\\Administrator\\Desktop\\APK\\release\\Khaos.apk";
  //每个包含渠道信息的APK文件目录
  private static String pathDestDirApk = "C:\\Users\\Administrator\\Desktop\\APK" + File.separator + serviceApkVersion;

  public static void main(String[] args) {
    //H5调用分享，先本地生成最后的下载链接，再异步生成对应的渠道APK即可
    boolean suiji = System.currentTimeMillis() % 2 == 0L;
    String h5ShowApkDownPath = shareChannel(suiji ? "Khaos" : "YiHui");
    //将生成预生成的地址返回给H5显示为二维码
    System.out.println("H5页面显示的二维码下载链接:" + h5ShowApkDownPath);
  }

  //H5调用分享接口，通过对渠道MD获取到APK的文件名称，
  private static String shareChannel(String channel) {
    //对应渠道的APK名称【最好使用channel进行MD5加密后得到】
    String apkName = channel.hashCode() + ".apk";
    //最后需要生成的APK
    File destFile = new File(pathDestDirApk, apkName);
    //如果文件不存在，则需要生成对应的最新渠道包【异步生成，不要影响接口调用】
    if (!destFile.exists()) {
      createChannelApk(channel, destFile.getPath());
    }
    //最后把需要下载的文件路径返回给H5调用接口
    return destFile.getPath();
  }

  //异步调用命令生成渠道APK
  private synchronized static void createChannelApk(String channel, String apkPath) {
    //异步执行，服务器的方式可能不是这样，另外还需要注意的是并发问题
    new Thread() {
      @Override
      public void run() {
        super.run();
        //原始命令(第一个参数是jar目录，第二个参数是渠道信息，第三个参数是原APK地址，第四个参数是生成后的APK地址)
        String cmdOrigin = "java -jar %s put -c %s %s %s";
        //将变量写入CMD命令
        String cmdExe = String.format(cmdOrigin, pathJar, channel, pathOriginApk, apkPath);
        try {
          Runtime.getRuntime().exec(cmdExe);
        } catch (IOException mE) {
          mE.printStackTrace();
        }
      }
    }.start();
  }
}