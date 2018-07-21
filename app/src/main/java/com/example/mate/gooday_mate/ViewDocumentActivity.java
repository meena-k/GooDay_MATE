package com.example.mate.gooday_mate;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import com.example.mate.gooday_mate.service.Config;
import com.example.mate.gooday_mate.service.Util;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ViewDocumentActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int UPLOAD_REQUEST_CODE = 1;
    private static final int DOWNLOAD_SELECTION_REQUEST_CODE = 2;

    // The TransferUtility is the primary class for managing transfer to S3
    private static AmazonS3Client s3;
    private TransferUtility transferUtility;
    // Reference to the utility class
    private Util util;

    private File file;
    private Bitmap bitmap;

    private TextView nameTxt, docTxt;
    private ImageButton scanBtn;
    private ImageView listImg, docImg;

    private AlertDialog alertDialog;
    public static boolean isDocument = false;
    private String patient_name, folder_birth, folder_document;
    private String lastfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_document);
        // Initializes TransferUtility, always do this before using it.
        util = new Util();
        transferUtility = util.getTransferUtility(this);
        s3 = util.getS3Client(this);
        initViews();
    }

    private void initViews() {
        nameTxt = findViewById(R.id.name);
        docTxt = findViewById(R.id.document);
        scanBtn = findViewById(R.id.scanBtn);
        listImg = findViewById(R.id.listImg);
        docImg = findViewById(R.id.docImg);

        Intent getIntent = this.getIntent();
        patient_name = getIntent.getStringExtra("name");
        folder_birth = getIntent.getStringExtra("birth");
        folder_document = getIntent.getStringExtra("folder");

        nameTxt.setText(patient_name);
        docTxt.setText(folder_document);

        scanBtn.setOnClickListener(this);
        listImg.setOnClickListener(this);
        docImg.setOnClickListener(this);

        if (getDocumentList()) { // 버튼 비활성화
            downloadFile(folder_birth + "/" + folder_document + "/" + lastfile + ".jpg");
        } else {
            Toast.makeText(getApplicationContext(), folder_document + "을 추가해주세요", Toast.LENGTH_SHORT).show();
        }
    }


    public void uploadFile(Uri fileUri) {
        if (fileUri == null) {
            Toast.makeText(this, "Could not find the filepath of the selected file", Toast.LENGTH_LONG).show();
            return;
        }
        String filename = new SimpleDateFormat("yyyy.MM.dd_hh:mm").format(new Date(System.currentTimeMillis()));
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/" + filename + ".jpg");
        createFile(getApplicationContext(), fileUri, file);
        Log.i("LOG_", "filename:" + filename + "--- getName(): " + file.getName() + "---- Path: " + file.getAbsolutePath());
        transferUtility.upload(Config.BUCKET_NAME + "/" + folder_birth + "/" + folder_document, file.getName(), file);
        if (folder_document.equals("PRESCRIPTION")) {
            Config.PATIENT_PRES = file.getAbsolutePath();
        } else if (folder_document.equals("X-RAY")) {
            Config.PATIENT_XRAY = file.getAbsolutePath();
        } else if (folder_document.equals("MRI")) {
            Config.PATIENT_MRI = file.getAbsolutePath();
        }
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
            getContentResolver().delete(fileUri, null, null);
            docImg.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void downloadFile(String key) {

        try {
            final File file = File.createTempFile("images", "jpg");

            TransferObserver downloadObserver = transferUtility.download(Config.BUCKET_NAME, key, file);
            String temp[] = key.split("/");
            final String file_time[] = temp[2].split("_");
            downloadObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED == state) {
                        Toast.makeText(getApplicationContext(), patient_name + "님 " + file_time[0] + " " + folder_document, Toast.LENGTH_SHORT).show();
                        Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
                        docImg.setImageBitmap(bmp);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                    int percentDone = (int) percentDonef;
                }

                @Override
                public void onError(int id, Exception ex) {

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.listImg:
                AlertDialog.Builder listbuilder = new AlertDialog.Builder(this);
                listbuilder.setTitle(null)
                        .setItems(R.array.dialog_array, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                            }
                        });
                alertDialog = listbuilder.create();
                alertDialog.show();
                break;
            case R.id.scanBtn:
                int preference = ScanConstants.OPEN_CAMERA;
                Intent intent = new Intent(this, ScanActivity.class);
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
                startActivityForResult(intent, UPLOAD_REQUEST_CODE);
                break;
            case R.id.docImg:
                Intent DownIntent = new Intent(this, DownloadSelectionActivity.class);
                DownIntent.putExtra("birth", folder_birth);
                DownIntent.putExtra("folder", folder_document);
                startActivityForResult(DownIntent, DOWNLOAD_SELECTION_REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UPLOAD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                uploadFile(uri);
            }
        } else if (requestCode == DOWNLOAD_SELECTION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String key = data.getStringExtra("key");
                downloadFile(key);
            }

        }
    }

    public boolean getDocumentList() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // The list of objects we find in the S3 bucket
        List<S3ObjectSummary> s3ObjList;

        s3ObjList = s3.listObjects(Config.BUCKET_NAME, folder_birth + "/" + folder_document + "/").getObjectSummaries();


        for (S3ObjectSummary summary : s3ObjList) {
            lastfile = new SimpleDateFormat("yyyy.MM.dd_hh:mm").format(summary.getLastModified());
        }

        if (!s3ObjList.isEmpty())
            isDocument = true;

        return isDocument;
    }


    public void createFile(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}