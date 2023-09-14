package com.souza.caio.click.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Utils {

    public static String readApiKey(Context context) {
        AssetManager assetManager = context.getAssets();
        String apiKey = null;

        try {
            InputStream inputStream = assetManager.open("api_key.txt");
            apiKey = new Scanner(inputStream).useDelimiter("\\A").next();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return apiKey;
    }

    public static int extractBitmapFileSize(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Log.i("imageSize", ("Image size: " + byteArray.length));
        return byteArray.length;
    }

    public static double bytesToMegabytes(int bytes) {
        return bytes / (1024.0 * 1024.0);
    }

    public static void showMessage(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static String findBitmapMimeType(byte[] bitmapData) {
        try {
            InputStream inputStream = new ByteArrayInputStream(bitmapData);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);

            return options.outMimeType;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // If the format cannot be determined
    }

    public static boolean isBitmapWithinResolution(Bitmap bitmap, int maxMegapixels) {
        if (bitmap == null) {
            // Handle null bitmap gracefully
            return false;
        }

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        // Calculate the total number of pixels
        long totalPixels = (long) bitmapWidth * bitmapHeight;

        // Calculate the threshold for the maximum resolution in megapixels
        long maxPixels = maxMegapixels * 1000000L; // 1 megapixel = 1 million pixels

        // Check if the total number of pixels is within the specified limit
        return totalPixels <= maxPixels;
    }

}