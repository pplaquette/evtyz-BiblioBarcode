package com.evanzheng.bibliobarcode

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.widget.Toast

internal object BarcodeHelper {
    //Converting image to Bitmap object, courtesy of Rod_Algonquin at https://stackoverflow.com/questions/41775968/how-to-convert-android-media-image-to-bitmap-object
    @JvmStatic
    fun imageToBitmap(image: Image): Bitmap {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())
        buffer[bytes]
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
    }

    //Checks if code is ISBN based on algorithm here: https://en.wikipedia.org/wiki/International_Standard_Book_Number
    private fun isISBN(code: String): Boolean {
        return if (code.length == 13) {
            var weightThree = false
            var checkSum = 0
            for (i in 0..12) {
                val c = code[i]
                if (Character.isDigit(c)) {
                    var digit = Character.getNumericValue(c)
                    if (weightThree) {
                        digit = digit * 3
                    }
                    weightThree = !weightThree
                    checkSum = checkSum + digit
                } else {
                    return false
                }
            }
            checkSum % 10 == 0
        } else if (code.length == 10) {
            var checkSum = 0
            for (i in 0..9) {
                val c = code[i]
                checkSum = if (Character.isDigit(c)) {
                    val digit = Character.getNumericValue(c) * (10 - i)
                    checkSum + digit
                } else {
                    return false
                }
            }
            checkSum % 11 == 0
        } else {
            false
        }
    }

    //Checks if an ISBN is already in the SQL database
    private fun notInDatabase(isbn: String): Boolean {
        val isbnList = MainActivity.database?.bookDao()?.loadISBN()
        for (s in isbnList!!) {
            if (isbn == s) {
                return false
            }
        }
        return true
    }

    //Checks if code is valid
    @JvmStatic
    fun checkCode(code: String, context: Context?): Boolean {
        if (!isISBN(code)) {
            Toast.makeText(
                context,
                "We didn't detect a valid ISBN. Please try again.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        if (!notInDatabase(code)) {
            Toast.makeText(
                context,
                "A book with this ISBN is already cited. Please delete it, then try again.",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        return true
    }
}