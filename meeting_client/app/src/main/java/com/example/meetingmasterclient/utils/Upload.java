package com.example.meetingmasterclient.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Upload {
    public File getFileFromUri(Context context, Uri uri) {
        ContentResolver cr = context.getContentResolver();

        try {
            File file = new File(
                    Environment.getExternalStorageDirectory().getPath()
                            + File.separatorChar
                            + "auxpic."
                            + MimeTypeMap.getSingleton().getExtensionFromMimeType(cr.getType(uri)));

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
        } catch(Exception e) {
            return null;
        }
    }
}
