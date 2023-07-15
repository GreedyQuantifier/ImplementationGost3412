package com;
import com.util.ByteArrayOperation;

import java.util.Arrays;

import static com.util.ByteArrayOperation.*;

public class Gost3412 {

    byte[] key;

    private final byte[][] iterKeys = new byte[10][];


    private final byte[] sub = intToByte(new int[]{
            252, 238, 221, 17, 207, 110, 49, 22, 251, 196, 250, 218, 35, 197, 4, 77,
            233, 119, 240, 219, 147, 46, 153, 186, 23, 54, 241, 187, 20, 205, 95, 193,
            249, 24, 101, 90, 226, 92, 239, 33, 129, 28, 60, 66, 139, 1, 142, 79,
            5, 132, 2, 174, 227, 106, 143, 160, 6, 11, 237, 152, 127, 212, 211, 31,
            235, 52, 44, 81, 234, 200, 72, 171, 242, 42, 104, 162, 253, 58, 206, 204,
            181, 112, 14, 86, 8, 12, 118, 18, 191, 114, 19, 71, 156, 183, 93, 135,
            21, 161, 150, 41, 16, 123, 154, 199, 243, 145, 120, 111, 157, 158, 178, 177,
            50, 117, 25, 61, 255, 53, 138, 126, 109, 84, 198, 128, 195, 189, 13, 87,
            223, 245, 36, 169, 62, 168, 67, 201, 215, 121, 214, 246, 124, 34, 185, 3,
            224, 15, 236, 222, 122, 148, 176, 188, 220, 232, 40, 80, 78, 51, 10, 74,
            167, 151, 96, 115, 30, 0, 98, 68, 26, 184, 56, 130, 100, 159, 38, 65,
            173, 69, 70, 146, 39, 94, 85, 47, 140, 163, 165, 125, 105, 213, 149, 59,
            7, 88, 179, 64, 134, 172, 29, 247, 48, 55, 107, 228, 136, 217, 231, 137,
            225, 27, 131, 73, 76, 63, 248, 254, 141, 83, 170, 144, 202, 216, 133, 97,
            32, 113, 103, 164, 45, 43, 9, 91, 203, 155, 37, 208, 190, 229, 108, 82,
            89, 166, 116, 210, 230, 244, 180, 192, 209, 102, 175, 194, 57, 75, 99, 182
    });
    private final byte[] subRev = intToByte(new int[]{
            165, 45, 50, 143, 14, 48, 56, 192, 84, 230, 158, 57, 85, 126, 82, 145,
            100, 3, 87, 90, 28, 96, 7, 24, 33, 114, 168, 209, 41, 198, 164, 63,
            224, 39, 141, 12, 130, 234, 174, 180, 154, 99, 73, 229, 66, 228, 21, 183,
            200, 6, 112, 157, 65, 117, 25, 201, 170, 252, 77, 191, 42, 115, 132, 213,
            195, 175, 43, 134, 167, 177, 178, 91, 70, 211, 159, 253, 212, 15, 156, 47,
            155, 67, 239, 217, 121, 182, 83, 127, 193, 240, 35, 231, 37, 94, 181, 30,
            162, 223, 166, 254, 172, 34, 249, 226, 74, 188, 53, 202, 238, 120, 5, 107,
            81, 225, 89, 163, 242, 113, 86, 17, 106, 137, 148, 101, 140, 187, 119, 60,
            123, 40, 171, 210, 49, 222, 196, 95, 204, 207, 118, 44, 184, 216, 46, 54,
            219, 105, 179, 20, 149, 190, 98, 161, 59, 22, 102, 233, 92, 108, 109, 173,
            55, 97, 75, 185, 227, 186, 241, 160, 133, 131, 218, 71, 197, 176, 51, 250,
            150, 111, 110, 194, 246, 80, 255, 93, 169, 142, 23, 27, 151, 125, 236, 88,
            247, 31, 251, 124, 9, 13, 122, 103, 69, 135, 220, 232, 79, 29, 78, 4,
            235, 248, 243, 62, 61, 189, 138, 136, 221, 205, 11, 19, 152, 2, 147, 128,
            144, 208, 36, 52, 203, 237, 244, 206, 153, 16, 68, 64, 146, 58, 1, 38,
            18, 26, 72, 104, 245, 129, 139, 199, 214, 32, 10, 8, 0, 76, 215, 116
    });
    private final byte[] l = intToByte(new int[]{148, 32, 133, 16, 194, 192, 1, 251, 1,
            192, 194, 16, 133, 32, 148, 1});

    public Gost3412(String key) {
        this.key = ByteArrayOperation.decodeHexString(key);
        genKeyVector();
    }

    byte[] substitution(byte[] input) {
        byte[] out = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            out[i] = sub[input[i] >= 0 ? input[i] : input[i] + 256];
        }
        return out;
    }

    byte[] substitutionReverse(byte[] input) {
        byte[] out = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            out[i] = subRev[input[i] >= 0 ? input[i] : input[i] + 256];
        }
        return out;
    }

    public byte[] encrypt(byte[] bytes) {
        int countBlock = bytes.length / 16 + (bytes.length % 16 != 0 ? 1 : 0);
        byte[][] block = bytePadding(bytes, countBlock);
        for (int i = 0; i < countBlock; i++) {
            byte[] temp = block[i];
            for (int j = 0; j < 9; j++) {
                temp = linear(substitution(xor(temp, iterKeys[j])));
            }
            block[i] = xor(temp, iterKeys[9]);
        }
        return boxed(block);
    }

    private byte[] boxed(byte[][] block) {
        byte[] bytes = new byte[block.length * 16];
        int counter = 0;
        for (byte[] arr : block) {
            for (byte b : arr) {
                bytes[counter++] = b;
            }
        }
        return bytes;
    }

    private byte[][] bytePadding(byte[] bytes, int countBlock) {
        byte[][] out = new byte[countBlock][];
        int numberBlock = -1, counter = 0;
        for (int i = 0; i < bytes.length; i++, counter++) {
            if (counter == 16 || numberBlock == -1) {
                out[++numberBlock] = new byte[16];
                counter = 0;
            }
            out[numberBlock][counter] = bytes[i];
        }

        if (counter < 16)
            out[numberBlock][counter] = (byte) 0x80;

        return out;
    }


    private byte[] linearReverseStep(byte[] input) {
        byte check = input[0];
        byte[] out = new byte[16];
        for (int i = 0; i < 15; i++) {
            out[i] = input[i + 1];
            check ^= multiple(out[i], l[i]);
        }
        out[15] = (byte) (check ^ multiple(out[15], l[15]));
        return out;
    }

    private byte[] linearReverse(byte[] input) {
        byte[] out = input;
        for (int i = 0; i < 16; i++) {
            out = linearReverseStep(out);
        }
        return out;
    }

    public byte[] decrypt(byte[] bytes) {
        int countBlock = bytes.length / 16;
        byte[][] block = new byte[countBlock][];

        for (int i = 0; i < countBlock; i++) {
            byte[] temp = Arrays.copyOfRange(bytes, i * 16, (i + 1) * 16);
            temp = xor(temp, iterKeys[9]);
            for (int j = 8; j >= 0; j--) {
                temp = xor(substitutionReverse(linearReverse(temp)), iterKeys[j]);
            }
            block[i] = temp;
        }

        return boxed(block);
    }

    byte[][] feistelStep(byte[] left, byte[] right, byte[] key) {
        return new byte[][]{
                xor(linear(substitution(xor(left, key))), right),
                left
        };
    }

    void genKeyVector() {

        byte[][] iterNum = new byte[32][];
        for (int i = 0; i < 32; i++) {
            iterNum[i] = linear(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, (byte) (i + 1)});
        }

        iterKeys[0] = Arrays.copyOfRange(key, 0, 16);
        iterKeys[1] = Arrays.copyOfRange(key, 16, 32);
        byte[][] tmp = {iterKeys[0], iterKeys[1]};
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                tmp = feistelStep(tmp[0], tmp[1], iterNum[8 * i + j]);
            }
            iterKeys[2 * i + 2] = tmp[0];
            iterKeys[2 * i + 3] = tmp[1];
        }
    }

    byte[] linear(byte[] input) {
        byte[] out = input;
        for (int i = 0; i < 16; i++) {
            out = linearStep(out);
        }
        return out;
    }

    byte[] linearStep(byte[] input) {

        byte check = 0;
        byte[] out = new byte[16];
        for (int i = 0; i < 15; i++) {
            out[i + 1] = input[i];
            check ^= multiple(input[i], l[i]);
        }
        out[0] = (byte) (check ^ multiple(input[15], l[15]));
        return out;
    }

    public byte[][] getIterKeys() {
        return iterKeys;
    }
}
