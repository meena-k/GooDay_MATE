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
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.util.IOUtils;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ViewXrayActivity extends AppCompatActivity implements ViewDocument, View.OnClickListener {

    // Initialize the Amazon Cognito credentials provider
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonS3 s3;
    private TransferUtility transferUtility;
    private TransferObserver uploadObserver, downloadObserver;
    private Uri fileUri;
    private Bitmap bitmap;


    private TextView titleTxt;
    private ImageButton scanBtn;
    private ImageView listImg, docImg;

    private AlertDialog alertDialog;
    private int REQUEST_CODE;

    String patient_name = "TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_xray);
        initViews();
    }

    private void initViews() {

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-northeast-2:48a8ebb7-e536-4e52-a595-2a0d49f76395", // Identity pool ID
                Regions.AP_NORTHEAST_2 // Region
        );

        s3 = new AmazonS3Client(credentialsProvider);
        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
        s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");

        transferUtility = new TransferUtility(s3, getApplicationContext());

        titleTxt = findViewById(R.id.title);
        scanBtn = findViewById(R.id.scanBtn);
        listImg = findViewById(R.id.listImg);
        docImg = findViewById(R.id.docImg);

        scanBtn.setOnClickListener(this);
        listImg.setOnClickListener(this);
        docImg.setOnClickListener(this);

        if (bitmap != null) { // 버튼 비활성화
            scanBtn.setVisibility(View.GONE);
            docImg.setVisibility(View.VISIBLE);
            downloadFile();
        }
    }


    @Override
    public void uploadFile(Uri fileUri) {
        if (fileUri != null) {

            final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/" + patient_name + ".jpg");
            createFile(getApplicationContext(), fileUri, file);

            uploadObserver = transferUtility.upload(
                    "mate-bucket" ,     /* 업로드 할 버킷 이름 */
                    patient_name + ".jpg",  /* 버킷에 저장할 파일의 이름 */
                    file     /* 버킷에 저장할 파일  */
            );

            uploadObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED == state) {
                        Toast.makeText(getApplicationContext(), "Upload Completed!", Toast.LENGTH_SHORT).show();

                        file.delete();
                    } else if (TransferState.FAILED == state) {
                        file.delete();
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                    int percentDone = (int) percentDonef;
                    //   tvFileName.setText("ID:" + id + "|bytesCurrent: " + bytesCurrent + "|bytesTotal: " + bytesTotal + "|" + percentDone + "%");

                }

                @Override
                public void onError(int id, Exception ex) {

                }
            });
        }
    }

    @Override
    public void downloadFile() {

        try {
            final File localFile = File.createTempFile("images", "jpg");

            downloadObserver = transferUtility.download(
                    "mate-bucket",     /* 업로드 할 버킷 이름 */
                    patient_name + ".jpg",     /* 다운로드할 파일의 이름 */
                    localFile     /* 다운로드할 파일  */
            );

            downloadObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED == state) {
                        Toast.makeText(getApplicationContext(), "Download Completed!", Toast.LENGTH_SHORT).show();
                        Bitmap bmp = BitmapFactory.decodeFile(localFile.getAbsolutePath());
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
                REQUEST_CODE = 99;
                int preference = ScanConstants.OPEN_CAMERA;
                Intent intent = new Intent(this, ScanActivity.class);
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.docImg:

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            fileUri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            try {
                // upload to S3
                uploadFile(fileUri);

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                getContentResolver().delete(fileUri, null, null);
                docImg.setImageBitmap(bitmap);

                //버튼 비활성화
                scanBtn.setVisibility(View.GONE);
                docImg.setVisibility(View.VISIBLE);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
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