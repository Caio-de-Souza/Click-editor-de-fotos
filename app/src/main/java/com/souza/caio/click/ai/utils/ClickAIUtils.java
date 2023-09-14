package com.souza.caio.click.ai.utils;

import android.graphics.Bitmap;

import static com.souza.caio.click.utils.Utils.bytesToMegabytes;
import static com.souza.caio.click.utils.Utils.extractBitmapFileSize;
import static com.souza.caio.click.utils.Utils.findBitmapMimeType;
import static com.souza.caio.click.utils.Utils.isBitmapWithinResolution;

import com.souza.caio.click.exceptions.ai.AIException;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClickAIUtils {

    public static void validateImageReimaginate(Bitmap image) {
        if (image.getWidth() > 1024) {
            throw new AIException("A imagem deve ter largura máxima de 1024 pixels.", HttpURLConnection.HTTP_BAD_REQUEST);
        } else {
            int bytesImage = extractBitmapFileSize(image);
            double mBytesImage = bytesToMegabytes(bytesImage);

            if(mBytesImage > 10.0){
                throw new AIException("A imagem deve ter tamanho máximo de 10MB.", HttpURLConnection.HTTP_BAD_REQUEST);
            }
        }
    }

    public static void validateImageReplaceBackground(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bitmapData = byteArrayOutputStream.toByteArray();

        List<String> allowedMimeTypes = new ArrayList<>(
                Arrays.asList("image/png", "image/webp", "image/jpeg")
        );
        String mimeTypeImage = findBitmapMimeType(bitmapData);
        if(!allowedMimeTypes.contains(mimeTypeImage)){
            throw new AIException("Tipo de arquivo não suportado.", HttpURLConnection.HTTP_BAD_REQUEST);
        }
        if (image.getWidth() > 2048) {
            throw new AIException("A imagem deve ter largura máxima de 2048 pixels.", HttpURLConnection.HTTP_BAD_REQUEST);
        } else {
            int bytesImage = extractBitmapFileSize(image);
            double mBytesImage = bytesToMegabytes(bytesImage);

            if(mBytesImage > 20.0){
                throw new AIException("A imagem deve ter tamanho máximo de 1=20MB.", HttpURLConnection.HTTP_BAD_REQUEST);
            }
        }
    }

    public static void validateImageRemoveBackground(Bitmap image){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bitmapData = byteArrayOutputStream.toByteArray();

        List<String> allowedMimeTypes = new ArrayList<>(
                Arrays.asList("image/png", "image/webp", "image/jpeg")
        );
        String mimeTypeImage = findBitmapMimeType(bitmapData);
        if(!allowedMimeTypes.contains(mimeTypeImage)){
            throw new AIException("Tipo de arquivo não suportado.", HttpURLConnection.HTTP_BAD_REQUEST);
        }

        if(!isBitmapWithinResolution(image, 25)){
            throw new AIException("Resolução máxima do arquivo não suportada.", HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    public static void validateImageRemoveText(Bitmap image){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bitmapData = byteArrayOutputStream.toByteArray();

        List<String> allowedMimeTypes = new ArrayList<>(
                Arrays.asList("image/png", "image/jpeg")
        );
        String mimeTypeImage = findBitmapMimeType(bitmapData);
        if(!allowedMimeTypes.contains(mimeTypeImage)){
            throw new AIException("Tipo de arquivo não suportado.", HttpURLConnection.HTTP_BAD_REQUEST);
        }

        if(!isBitmapWithinResolution(image, 30)){
            throw new AIException("Resolução máxima do arquivo não suportada.", HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

}
