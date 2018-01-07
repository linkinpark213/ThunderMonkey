package com.linkinpark213.phone.common;

public class ByteArrayUtil {
    public static byte[] cat(byte[] array1, byte[] array2) {
        byte[] result = new byte[array1.length + array2.length];
        int length1 = array1.length;
        int length2 = array2.length;
        for (int i = 0; i < length1; i++) {
            result[i] = array1[i];
        }
        for (int i = 0; i < length2; i++) {
            result[i + length1] = array2[i];
        }
        return result;
    }
}
