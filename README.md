# BiblioBarcode

Built and designed by Evan Zheng.

BiblioBarcode is an Android app that scans ISBN barcodes on books and formats them into a bibliography in MLA, APA, Chicago, or Harvard style.

This app was created as part of a CS50x final project, and was written using Java and SQL on Android Studio.

## Preview

![Screenshot](bibliobarcodepreview.gif)

## Features and Implementation

- Utilizes Google's Mobile Vision Machine Learning API to scan for valid barcodes in photos
- Supports manual ISBN entry or a blank entry as well
- Queries the Google Books API using Android Volley for a book with a matching ISBN
- Parses the resulting JSON information into title, author, publisher, and date fields
- Allows users to edit and update the book's information after parsing to clean up errors and add additional info
- Stores books on a SQLite database supported by Android Room
- Formats the book's information into a bibliography in the user's choice of MLA, APA, Chicago, or Harvard style
- Exports the bibliography to the user's choice of either a clipboard or a formatted HTML file

## Libraries and APIs Used

- [Android Jetpack CameraX API](https://developer.android.com/jetpack/androidx/releases/camera)
- [Android Room Persistence Library](https://developer.android.com/topic/libraries/architecture/room)
- [Android Volley HTTP Library](https://developer.android.com/training/volley)
- [Application Crash Reports for Android](https://github.com/ACRA/acra)
- [Google Books API](https://developers.google.com/books/docs/overview)
- [Google Mobile Vision Barcode API](https://developers.google.com/vision/android/barcodes-overview)
- [Material Showcase View](https://github.com/deano2390/MaterialShowcaseView/tree/master/library/src/main/java/uk/co/deanwild/materialshowcaseview)

# Download Links

[Google Drive Download Link](https://drive.google.com/file/d/1enPOnjjho_Nqh5FSjLK7dxPgWcaLpsSx/view?usp=sharing)

Or, download from app/release/app-release.apk

# License

See LICENSE file.


