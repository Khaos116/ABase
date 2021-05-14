package cn.leizy.shell;


import java.io.File;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    public static final String DEFAULT_PWD = "abcdefghijklmnop";

    private static final String algorithmStr = "AES/ECB/PKCS5Padding";

    private static Cipher encryptCipher;
    private static Cipher decryptCipher;

    public static void init(String password) {
        try {
            // 生成一个实现指定转换的 Cipher 对象。
            encryptCipher = Cipher.getInstance(algorithmStr);
            decryptCipher = Cipher.getInstance(algorithmStr);// algorithmStr
            byte[] keyStr = password.getBytes();
            SecretKeySpec key = new SecretKeySpec(keyStr, "AES");
            encryptCipher.init(Cipher.ENCRYPT_MODE, key);
            decryptCipher.init(Cipher.DECRYPT_MODE, key);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public static byte[] encrypt(byte[] content) {
        try {
            byte[] result = encryptCipher.doFinal(content);
            return result;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decrypt(byte[] content) {
        try {
            byte[] result = decryptCipher.doFinal(content);
            return result;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) throws Exception {

        File zip = new File("/Users/xiang/develop/app-debug.apk");
        String absolutePath = zip.getAbsolutePath();
        File dir = new File(absolutePath.substring(0, absolutePath.lastIndexOf(".")));
        Zip.unZip(zip,dir);

        File zip2 = new File("/Users/xiang/develop/app-debug2.apk");
        Zip.zip(dir,zip2);

        String[] argv = {
                "jarsigner","-verbose", "-sigalg", "MD5withRSA",
                "-digestalg", "SHA1",
                "-keystore", "/Users/xiang/develop/debug.keystore",
                "-storepass","android",
                "-keypass", "android",
                "-signedjar", "/Users/xiang/develop/app-debug2-sign.apk",
                "/Users/xiang/develop/app-debug2.apk",
                "androiddebugkey"
        };
        Process pro = null;
        try {
            pro = Runtime.getRuntime().exec(argv);
            //destroy the stream
            try {
                pro.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } finally {
            if (pro != null) {
                pro.destroy();
            }
        }
    }


}
