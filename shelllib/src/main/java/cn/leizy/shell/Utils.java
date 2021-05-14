package cn.leizy.shell;


public class Utils {


//    public static byte[] int2Bytes(int number) {
//        byte[] b = new byte[4];
//        for (int i = 3; i >= 0; i--) {
//            b[i] = (byte) (number % 256);
//            number >>= 8;
//        }
//        return b;
//    }

    public static byte[] int2Bytes(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    public static int bytes2Int(byte[] src) {
        int value;
        value = (int) ((src[0] & 0xFF)
                | ((src[1] & 0xFF)<<8)
                | ((src[2] & 0xFF)<<16)
                | ((src[3] & 0xFF)<<24));
        return value;
    }



    public static void main(String[] args) throws Exception {
//        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
//        byteBuf.writeInt(241241143);
//        byte[] a = new byte[4];
//        byteBuf.markReaderIndex();
//        byteBuf.readBytes(a);
//        byteBuf.resetReaderIndex();
//
//        System.out.println(Arrays.toString(a));
//        System.out.println(Arrays.toString(int2Bytes(241241143)));
//        System.out.println(Arrays.toString(intToBytes(241241143)));
    }

}
