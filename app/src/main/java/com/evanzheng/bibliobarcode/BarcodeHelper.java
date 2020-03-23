package com.evanzheng.bibliobarcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;
import java.util.List;

class BarcodeHelper {


    //Converting image to Bitmap object, courtesy of Rod_Algonquin at https://stackoverflow.com/questions/41775968/how-to-convert-android-media-image-to-bitmap-object
    static Bitmap imageToBitmap(Image image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
    }

    //Checks if code is ISBN based on algorithm here: https://en.wikipedia.org/wiki/International_Standard_Book_Number
    private static boolean isISBN(@NonNull String code) {
        if (code.length() == 13) {
            boolean weightThree = false;
            int checkSum = 0;
            for (int i = 0; i < 13; i++) {
                char c = code.charAt(i);
                if (Character.isDigit(c)) {
                    int digit = Character.getNumericValue(c);
                    if (weightThree) {
                        digit = digit * 3;
                    }
                    weightThree = !weightThree;
                    checkSum = checkSum + digit;
                } else {
                    return false;
                }
            }
            return checkSum % 10 == 0;
        } else if (code.length() == 10) {
            int checkSum = 0;
            for (int i = 0; i < 10; i++) {
                char c = code.charAt(i);
                if (Character.isDigit(c)) {
                    int digit = Character.getNumericValue(c) * (10 - i);
                    checkSum = checkSum + digit;
                } else {
                    return false;
                }
            }
            return checkSum % 11 == 0;
        } else {
            return false;
        }
    }

    //Checks if an ISBN is already in the SQL database
    private static boolean notInDatabase(String isbn) {
        List<String> isbnList = MainActivity.database.bookDao().loadISBN();
        for (String s : isbnList) {
            if (isbn.equals(s)) {
                return false;
            }
        }
        return true;
    }

    //Checks if code is valid
    static boolean checkCode(String code, Context context) {
        if (!isISBN(code)) {
            Toast.makeText(context, "We didn't detect a valid ISBN. Please try again.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!notInDatabase(code)) {
            Toast.makeText(context, "A book with this ISBN is already cited. Please delete it, then try again.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
