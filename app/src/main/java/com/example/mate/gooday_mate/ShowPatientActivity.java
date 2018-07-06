package com.example.mate.gooday_mate;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowPatientActivity extends AppCompatActivity implements ViewDocument, View.OnClickListener {
    // Initialize the Amazon Cognito credentials provider
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonS3 s3;
    private TransferUtility transferUtility;
    private TransferObserver uploadObserver, downloadObserver;
    private Uri fileUri;
    private Bitmap bitmap;
    private int REQUEST_CODE;
    Item_HealthData itemHealthData;

    /*DB*/
    String SETDATA_URL = Config.URL + "set_checkdata.php";
    String GETMEAL_URL = Config.URL + "getcheck_meal.php";
    String GETEXEC_URL = Config.URL + "getcheck_exec.php";
    String GETPILL_URL = Config.URL + "getcheck_pill.php";

    JSONArray contents = null;
    private JSONObject jsonObj;
    private String patientJSON;

    CircleImageView imgPatient;
    private String myJSON, patient_name, patient_birth, channel, port, push_str;
    TextView name, txtBirth, txtDisease, txtDateIn, txtDateOut, txtGuardian;
    TabbedDialog tabbedDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_patient);
        initViews();
    }

    private void initViews() {
        Intent intent = getIntent();
        patientJSON = intent.getStringExtra("patientJSON");

        try {
            jsonObj = new JSONObject(patientJSON);
            contents = jsonObj.getJSONArray("result");
            patient_name = contents.getJSONObject(0).getString("name");
            patient_birth = contents.getJSONObject(0).getString("birth");
            channel = contents.getJSONObject(0).getString("channel");
            port = contents.getJSONObject(0).getString("port");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        getData(GETMEAL_URL);
        getData(GETPILL_URL);

        itemHealthData = new Item_HealthData();

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-northeast-2:48a8ebb7-e536-4e52-a595-2a0d49f76395", // Identity pool ID
                Regions.AP_NORTHEAST_2 // Region
        );

        s3 = new AmazonS3Client(credentialsProvider);
        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
        s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");

        transferUtility = new TransferUtility(s3, getApplicationContext());

        name = findViewById(R.id.name);
        name.setText(patient_name);
        imgPatient = findViewById(R.id.image);
        txtBirth = findViewById(R.id.txt_birth);
        txtBirth.setText(patient_birth);
        txtDisease = findViewById(R.id.txt_disease);
        txtDateIn = findViewById(R.id.txt_date_in);
        txtDateOut = findViewById(R.id.txt_date_out);
        txtGuardian = findViewById(R.id.txt_guardian);

        imgPatient.setOnClickListener(this);
        findViewById(R.id.btn_meal).setOnClickListener(this);
        findViewById(R.id.btn_treatment).setOnClickListener(this);
        findViewById(R.id.btn_document).setOnClickListener(this);
        tabbedDialog = new TabbedDialog();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image:
                REQUEST_CODE = 99;
                int preference = ScanConstants.OPEN_CAMERA;
                Intent intent = new Intent(this, ScanActivity.class);
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
                startActivityForResult(intent, REQUEST_CODE);
                break;

            case R.id.btn_meal:

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                tabbedDialog.show(ft, "dialog");
                break;

            case R.id.btn_treatment:
                break;

            case R.id.btn_document:
                AlertDialog.Builder builder_document = new AlertDialog.Builder(this);
                builder_document.setItems(R.array.document_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                startActivity(new Intent(ShowPatientActivity.this, ViewPrescriptionActivity.class));
                                break;
                            case 1:
                                startActivity(new Intent(ShowPatientActivity.this, ViewXrayActivity.class));
                                break;
                            case 2:
                                startActivity(new Intent(ShowPatientActivity.this, ViewMriActivity.class));
                                break;
                        }
                    }
                })
                        .setNegativeButton(R.string.btn_negative_txt, null)
                        .show();

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
                imgPatient.setImageBitmap(bitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void uploadFile(Uri fileUri) {
        if (fileUri != null) {

            // cash file 저장

            final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/" + patient_name + ".jpg");
            createFile(getApplicationContext(), fileUri, file);

            uploadObserver = transferUtility.upload(
                    "mate-bucket/" + patient_name,     /* 업로드 할 버킷 이름 */
                    patient_name + ".jpg",    /* 버킷에 저장할 파일의 이름 */
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
                    "mate-bucket/" + patient_name,     /* 업로드 할 버킷 이름 */
                    patient_name + ".jpg",    /* 다운로드할 파일의 이름 */
                    localFile     /* 다운로드할 파일  */
            );

            downloadObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED == state) {
                        Toast.makeText(getApplicationContext(), "Download Completed!", Toast.LENGTH_SHORT).show();
                        Bitmap bmp = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        imgPatient.setImageBitmap(bmp);
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

    private void getData(final String data_url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                myJSON = result;
                showList(data_url);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(data_url);
    }

    protected void showList(String data_url) {
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            contents = jsonObj.getJSONArray("result");

            for (int i = 0; i < contents.length(); i++) {
                JSONObject c = contents.getJSONObject(i);
                String birth = c.getString("birth");
                if (patient_birth.equals(birth)) {
                    Boolean breakfast = c.getBoolean("breakfast");
                    Boolean lunch = c.getBoolean("lunch");
                    Boolean dinner = c.getBoolean("dinner");

                    if (data_url.contains("meal")) {
                        itemHealthData.setmSelect_meal(new boolean[]{breakfast, lunch, dinner});
                    } else if (data_url.contains("pill")) {
                        itemHealthData.setmSelect_pill(new boolean[]{breakfast, lunch, dinner});
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
