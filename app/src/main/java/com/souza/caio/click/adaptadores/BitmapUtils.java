package com.souza.caio.click.adaptadores;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BitmapUtils
{

    public static Bitmap getBitmapFromAssets(Context context, String nome, int largura, int altura)
    {
        AssetManager assetManager = context.getAssets();

        InputStream inputStream;
        Bitmap bitmap = null;

        try
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            inputStream = assetManager.open(nome);
            options.inSampleSize = calculateInSampleSize(options, largura, altura);
            options.inJustDecodeBounds = false;

            return BitmapFactory.decodeStream(inputStream, null, options);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap getBitmapFromGallery(Context context, Uri uri, int largura, int altura)
    {
        try{
            String[] colunaDiretorio = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, colunaDiretorio, null, null, null);
            if (cursor.moveToFirst()) {


              /*  if (Build.VERSION.SDK_INT >= 29) {
                    // You can replace '0' by 'cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)'
                    // Note that now, you read the column '_ID' and not the column 'DATA'
                    Uri imageUri= ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getInt(0));

                    // now that you have the media URI, you can decode it to a bitmap
                    try (ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(imageUri, "r")) {
                        if (pfd != null) {
                             return BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor());
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        return null;
                    }
                } else {*/
                    // Repeat the code you already are using
                    String diretorioImagem = cursor.getString(cursor.getColumnIndex(colunaDiretorio[0]));
                    cursor.close();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    return BitmapFactory.decodeFile(diretorioImagem, options);
               // }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Bitmap applyOverlay(Context context, Bitmap sourceImage, int overlayDrawableResourceId)
    {
        Bitmap bitmap = null;
        try
        {
            int width = sourceImage.getWidth();
            int height = sourceImage.getHeight();
            Resources r = context.getResources();

            Drawable imageAsDrawable = new BitmapDrawable(r, sourceImage);
            Drawable[] layers = new Drawable[2];

            layers[0] = imageAsDrawable;
            layers[1] = new BitmapDrawable(r, BitmapUtils.decodeSampledBitmapFromResource(r, overlayDrawableResourceId, width, height));
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            bitmap = BitmapUtils.drawableToBitmap(layerDrawable);
        }
        catch (Exception ex)
        {
        }
        return bitmap;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight)
    {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth)
            {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap drawableToBitmap(Drawable drawable)
    {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable)
        {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null)
            {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0)
        {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        }
        else
        {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static String inserirImagem(ContentResolver resolver, Bitmap fonte, String titulo, String descricao)
    {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, titulo);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, titulo);
        values.put(MediaStore.Images.Media.DESCRIPTION, descricao);
        values.put(MediaStore.Images.Media.MIME_TYPE, "images/jpeg");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        Uri uri = null;
        String stringUri = null;

        try
        {
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (fonte != null)
            {
                OutputStream outputStream = resolver.openOutputStream(uri);

                try
                {
                    fonte.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                }
                finally

                {
                    outputStream.close();
                }

                long id = ContentUris.parseId(uri);
                Bitmap mini_thumb = MediaStore.Images.Thumbnails.getThumbnail(resolver, id, MediaStore.Images.Thumbnails.MINI_KIND, null);

                armazenarThumb(resolver, mini_thumb, id, 50f, 50f, MediaStore.Images.Thumbnails.MICRO_KIND);
            }
            else
            {
                resolver.delete(uri, null, null);
                uri = null;
            }
        }
        catch (IOException e)
        {
            if (uri != null)
            {
                resolver.delete(uri, null, null);
                uri = null;
            }
        }

        if (uri != null)
            stringUri = uri.toString();


        return stringUri;
    }

    public static String salvarCover(String diretorio, Bitmap imagem, String nome, String descricao)
    {
        Uri uri = null;
        String stringUri = null;

        FileOutputStream fileOutputStream = null;

        File file = new File(Environment.getExternalStorageDirectory() + "/Documentos/");

        if (!file.exists())
        {
            file.mkdir();
        }
        try
        {
            fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory() + "/Documentos/" + nome);
            imagem.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                fileOutputStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        //return stringUri;
        return Environment.getExternalStorageDirectory() + "/Documentos/" + nome;
    }


    private static final Bitmap armazenarThumb(ContentResolver resolver, Bitmap mini_thumb, long id, float largura, float altura, int kind)
    {
        Matrix matrix = new Matrix();

        float escalaX = largura / mini_thumb.getWidth();

        float escalaY = altura / mini_thumb.getHeight();

        matrix.setScale(escalaX, escalaY);

        Bitmap thumb = Bitmap.createBitmap(mini_thumb, 0, 0, mini_thumb.getWidth(), mini_thumb.getHeight(), matrix, true);

        ContentValues contentValues = new ContentValues(4);
        contentValues.put(MediaStore.Images.Thumbnails.KIND, kind);
        contentValues.put(MediaStore.Images.Thumbnails.IMAGE_ID, id);
        contentValues.put(MediaStore.Images.Thumbnails.HEIGHT, altura);
        contentValues.put(MediaStore.Images.Thumbnails.WIDTH, largura);

        Uri uri = resolver.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, contentValues);

        try
        {
            OutputStream outputStream = resolver.openOutputStream(uri);
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();

            return thumb;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();

            return null;
        }
        catch (IOException e)
        {
            e.printStackTrace();

            return null;
        }


    }


}
