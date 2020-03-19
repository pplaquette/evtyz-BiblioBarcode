package com.evanzheng.bibliobarcode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;

class BarcodeHelper {
    static int processCode(String code) {
        if (!isISBN(code)) { return 1; }
        return 0;
    }

    //Converting image to Bitmap object, courtesy of Rod_Algonquin at https://stackoverflow.com/questions/41775968/how-to-convert-android-media-image-to-bitmap-object
    static Bitmap imageToBitmap(Image image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
    }

    private static boolean isISBN(@NonNull String code) {
        if (code.length() == 13) {
            boolean weightThree = false;
            int checkSum = 0;
            for (int i = 0; i < 13; i++) {
                char c = code.charAt(i);
                if (Character.isDigit(c)) {
                    int digit = Character.getNumericValue(c);
                    if (weightThree) { digit = digit * 3; }
                    weightThree = !weightThree;
                    checkSum = checkSum + digit;
                } else { return false; }
            }
            return checkSum % 10 == 0;
        } else if (code.length() == 10) {
            int checkSum = 0;
            for (int i = 0; i < 10; i++) {
                char c = code.charAt(i);
                if (Character.isDigit(c)) {
                    int digit = Character.getNumericValue(c) * (10 - i);
                    checkSum = checkSum + digit;
                } else { return false; }
            }
            return checkSum % 11 == 0;
        } else {
            return false;
        }
    }

}
