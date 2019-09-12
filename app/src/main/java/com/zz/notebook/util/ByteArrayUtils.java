package com.zz.notebook.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.InvalidParameterException;

public class ByteArrayUtils {

    public static byte[] int2byte(int res) {//小端序
        byte[] targets = new byte[4];
        targets[0] = (byte) (res & 0xff);// 最低位
        targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
        targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
        targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
        return targets;
    }
    public static byte[] long2byte(long res){//小端序
        byte[] targets = new byte[8];
        targets[0] = (byte) (res & 0xff);
        targets[1] = (byte) ((res >> 8) & 0xff);
        targets[2] = (byte) ((res >> 16) & 0xff);
        targets[3] = (byte) ((res >> 24) & 0xff);
        targets[4] = (byte) ((res >> 32) & 0xff);
        targets[5] = (byte) ((res >> 40) & 0xff);
        targets[6] = (byte) ((res >> 48) & 0xff);
        targets[7] = (byte) ((res >> 56) & 0xff);
        return targets;

    }
    public static int byte2int(byte[] res) {//小端序

        if(res==null)throw new InvalidParameterException("参数不能为空。");
        int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) // '|' 表示按位或
                | (res[2] <<16) | (res[3] << 24);
        return targets;
    }
    public static long byte2long(byte[] res){
        if(res==null)throw new InvalidParameterException("参数不能为空。");
        long target=(res[0]&0xffL)|((res[1]<<8)&0xff00L)
                |((res[2]<<16)&0xff0000L)
                |((res[3]<<24)&0xff000000L)
                |((res[4]<<32)&0xff00000000L)
                |((res[5]<<40)&0xff0000000000L)
                |((res[6]<<48)&0xff000000000000L)
                |((res[7]<<56)&0xff00000000000000L)
                ;
        return target;
    }
    public static byte[] concat(byte[] a, byte[] b) {//数组合并
        if(a==null||b==null)throw new InvalidParameterException("参数不能为空。");

        final int alen = a.length;
        final int blen = b.length;
        if (alen == 0) {
            return b;
        }
        if (blen == 0) {
            return a;
        }
        final byte[] result = new byte[alen + blen];
        System.arraycopy(a, 0, result, 0, alen);
        System.arraycopy(b, 0, result, alen, blen);
        return result;
    }
    public static byte[] xor(byte[]a,byte[]b){
        if(a==null||b==null)throw new InvalidParameterException("参数不能为空。");
        if(a.length==0||b.length==0)throw new InvalidParameterException("两个数组不能是空数组。");
        if(a.length!=b.length) throw new InvalidParameterException("参与计算必须是两个等长数组。");

        byte[] result=new byte[a.length];
        for(int i=0;i<a.length;i++){
            result[i]= (byte) (a[i]^b[i]);
        }
        return result;
    }

    /**
     *
     * @param out 要输出到的流
     * @param res 要输出的数据
     * @param with_name 输出时显示的数据集名称
     * @param group_distance 数据分组的距离，为0表示不分组
     * @throws IOException
     */
    public static void outputByte(OutputStream out,byte[] res,String with_name,int group_distance) throws IOException {
        if(out==null||res==null)throw new InvalidParameterException("参数不能为空。");
        PrintStream o=new PrintStream(out);
        o.print(with_name+"{ ");
        for(int i=0;i<res.length;i++){
            o.print(String.format("%02x",res[i])+" ");
            if(group_distance!=0&&i!=0)
                if((i+1)%group_distance==0&& i+1<res.length)o.print("| ");
        }
        o.println("}");
    }
    public static void outputByte(OutputStream out,byte[] res,String with_name) throws IOException {
        outputByte(out,res,with_name,0);
    }
    public static void outputByte(OutputStream out,byte[] res) throws IOException {
        outputByte(out,res,"byte[]");
    }

    public static boolean isEqual(byte[] a,byte[] b){
        if(a==null||b==null)throw new InvalidParameterException("参数不能为空。");
        if(a.length!=b.length)return false;//如果长度不等，整体就不相等。
        for(int i=0;i<a.length;i++){
            if(a[i]!=b[i])return false;//只要有一个不相等，整体就不相等。
        }
        return true;
    }
    private static String[] bytecode={"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
    public static String bytesToString(byte[] in){
        if(in==null||in.length==0)return "";
        StringBuilder builder=new StringBuilder();
        for(byte b:in){
            int high=(b>>4)&0x0f;
            int low=b&0x0f;
            builder.append(bytecode[high]);
            builder.append(bytecode[low]);
        }
        return builder.toString();
    }
}
