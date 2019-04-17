package com.example.meetingmasterclient.utils;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.meetingmasterclient.server.Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public abstract class Upload {
    public static boolean checkFilePermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private static File getFileFromUri(Context context, Uri uri) {
        ContentResolver cr = context.getContentResolver();

        try {
            File file = File.createTempFile(
                    "med",
                    "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(cr.getType(uri)),
                    context.getCacheDir()
            );
            file.deleteOnExit();

            InputStream in =  cr.openInputStream(uri);
            OutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len;

            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            out.close();
            in.close();

            return file;
        } catch(FileNotFoundException fe) {
            Toast.makeText(context, "The file could not be found", Toast.LENGTH_SHORT).show();
            return null;
        } catch(IOException ioe) {
            Toast.makeText(context, "An error occurred while reading the file", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    // Requires authentication
    public static void uploadPictureToServer(Context context, Uri profilePictureUri) {
        File picture = getFileFromUri(context, profilePictureUri);

        if (picture == null) {
            return;
        }

        Call<ResponseBody> c = Server.getService().uploadProfilePicture(MultipartBody.Part.createFormData(
                "profile_picture",
                picture.getName(),
                RequestBody.create(MediaType.parse(context.getContentResolver().getType(profilePictureUri)), picture)
        ));

        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Profile Picture Success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,
                                "An error has occurred while saving the profile picture",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                (call, t) -> t.printStackTrace()
        ));
    }

    public static void uploadFileToServer(Context context, Uri fileUri, String id) {
        File file = getFileFromUri(context, fileUri);

        if (file == null) {
            return;
        }

        Call<ResponseBody> c = Server.getService().uploadFile(id, MultipartBody.Part.createFormData(
                "file_attachment",
                file.getName(),
                RequestBody.create(MediaType.parse(context.getContentResolver().getType(fileUri)), file)
        ));

        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "File Success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context,
                                "An error has occurred while saving the file",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                (call, t) -> t.printStackTrace()
        ));
    }
}
