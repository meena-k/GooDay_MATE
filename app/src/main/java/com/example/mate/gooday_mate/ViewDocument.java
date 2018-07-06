package com.example.mate.gooday_mate;

import android.content.Context;
import android.net.Uri;

import java.io.File;

public interface ViewDocument  {

    void uploadFile(Uri fileUri);

    void downloadFile();

    void createFile(Context context, Uri srcUri, File dstFile);


}