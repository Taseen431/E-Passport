package com.e_passport.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.e_passport.R;
import com.e_passport.utilities.interfaces.ImagePathReceiver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AppUtilities {
    private static final int CAMERA_PERMISSION_REQUEST = 1;
    private static final int GALLERY_PERMISSION_REQUEST = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int PICK_IMAGE_REQUEST = 4;
    private static String currentPhotoPath;
    private static ImagePathReceiver imagePathReceiver;
    private static ProgressDialog dialog;
    public static void showLoadingDialog(Context context, String msg){
        dialog = new ProgressDialog(context);
        dialog.setMessage(msg);
        dialog.setCancelable(false);
        dialog.show();
    }
    public static void updateLoadingDialog(String msg){
        if(dialog != null && msg != null) dialog.setMessage(msg);
    }
    public static void hideLoadingDialog(){
        if(dialog != null) dialog.dismiss();
    }
    public static void hideLoadingDialog(int time){
        if(dialog != null) {
            dialog.setIndeterminate(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppUtilities.hideLoadingDialog();
                }
            }, time);
        }
    }
    public static void getImageFromCamera(Activity activity){
        AppUtilities.imagePathReceiver = imagePathReceiver;
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile(activity);
                } catch (IOException ex) {
                    imagePathReceiver.onImagePathError();
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(activity, "com.e_passport.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    imagePathReceiver.onImagePathError();
                }
            }
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        }
    }
    public static void getImageFromGallery(Activity activity){
        AppUtilities.imagePathReceiver = imagePathReceiver;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activity.startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES}, GALLERY_PERMISSION_REQUEST);
        }
    }
    private static File createImageFile(Activity activity) throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    public static String getPathFromUri(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            return null;
        }
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }
    public static void openImageChooser(Activity activity, ImagePathReceiver imagePathReceiver){
        AppUtilities.imagePathReceiver = imagePathReceiver;
        final String[] options = {activity.getResources().getString(R.string.image_chooser_dialog_camera), activity.getResources().getString(R.string.image_chooser_dialog_gallery)};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.image_chooser_dialog_title));
        builder.setAdapter(new ArrayAdapter(activity, android.R.layout.simple_list_item_1, options),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                AppUtilities.getImageFromCamera(activity);
                                break;
                            case 1:
                                AppUtilities.getImageFromGallery(activity);
                                break;
                        }
                    }
                });
        builder.setPositiveButton(R.string.close_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    public static void showNoInternetDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.no_internet_dialog_title));
        builder.setMessage(activity.getResources().getString(R.string.no_internet_dialog_msg));
        builder.setCancelable(false);

        builder.setPositiveButton(activity.getResources().getString(R.string.exit_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                activity.finish();
            }
        });

        builder.setNegativeButton(activity.getResources().getString(R.string.settings_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        });

        builder.create().show();
    }

    public static void openYoutube(Context context, String videoUrl){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
            intent.setPackage("com.google.android.youtube");
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                context.startActivity(browserIntent);
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public static void openDefaultBrowser(Context context, String link) {
        try{
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            context.startActivity(browserIntent);
        } catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    public static Bitmap base64ToBitmap(String base64String) {
        if (base64String == null) {
            return null;
        }
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    public static String bitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) {
            return "";
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    public static void resizeAndCompressImage(String imagePath, int targetWidth, int targetHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap originalBitmap = BitmapFactory.decodeFile(imagePath, options);
        float scaleX = (float) targetWidth / originalBitmap.getWidth();
        float scaleY = (float) targetHeight / originalBitmap.getHeight();
        float scale = Math.min(scaleX, scaleY);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap scaledBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
        File compressedFile = new File(imagePath);
        try (FileOutputStream out = new FileOutputStream(compressedFile)) {
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (IOException e) {}
    }

    public static String preZeroFormat(int n){
        return n < 10 ? "0" + n : String.valueOf(n);
    }

    public static void onActivityResult(Context context, int requestCode, int resultCode, Intent data){
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    imagePathReceiver.onImagePathReceived(getPathFromUri(context, selectedImageUri));
                } else {
                    imagePathReceiver.onImagePathError();
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (currentPhotoPath != null) {
                    imagePathReceiver.onImagePathReceived(currentPhotoPath);
                } else {
                    imagePathReceiver.onImagePathError();
                }
            }
        } else {
            imagePathReceiver.onImagePathError();
        }
        imagePathReceiver = null;
    }

    public static void onRequestPermissionsResult(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImageFromCamera(activity);
            } else {
                imagePathReceiver.onImagePathError();
            }
        } else if (requestCode == GALLERY_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getImageFromGallery(activity);
            } else {
                imagePathReceiver.onImagePathError();
            }
        }
    }
}