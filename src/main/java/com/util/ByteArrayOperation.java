package com.util;

public class ByteArrayOperation {

    public static String encodeHexString(byte[] byteArray) {
        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i]));
        }
        return hexStringBuffer.toString();
    }

    public static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    public static byte[] decodeHexString(String hex) {

        int len = hex.length();
        byte[] ans = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            ans[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }

        return ans;
    }

    public static byte[] xor(byte[] op, byte[] op1) {
        byte[] temp = new byte[op.length];
        for (int i = 0; i < op.length; i++) {
            temp[i] = (byte) (op[i] ^ op1[i]);
        }
        return temp;
    }



    public static byte multiple(byte a, byte b) {
        byte p = 0;
        byte counter;
        byte hi_bit_set;
        for (counter = 0; counter < 8 && a != 0 && b != 0; counter++) {
            if ((b & 1) != 0) {
                p ^= a;
            }
            hi_bit_set = (byte) (a & 0x80);
            a <<= 1;
            if (hi_bit_set != 0) {
                a ^= 0xc3;
            }
            b >>= 1;
        }
        return p;
    }

    static byte[] shift(byte[] bytes, int offset) {
        byte[] out = new byte[bytes.length];
        offset %= bytes.length;
        if (bytes.length - offset >= 0) System.arraycopy(bytes, offset, out, 0, bytes.length - offset);
        if (offset >= 0) System.arraycopy(bytes, 0, out, bytes.length - offset, offset);
        return out;
    }

    public static byte[] intToByte(int[] res) {
        byte[] ut = new byte[res.length];
        for (int i = 0; i < res.length; i++) {
            ut[i] = (byte) res[i];
        }
        return ut;
    }
}
