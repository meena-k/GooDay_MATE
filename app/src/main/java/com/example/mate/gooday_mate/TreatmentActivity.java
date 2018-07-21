package com.example.mate.gooday_mate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.amazonaws.util.IOUtils;
import com.example.mate.gooday_mate.service.Config;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import de.hdodenhof.circleimageview.CircleImageView;


public class TreatmentActivity extends AppCompatActivity {
    String CHECKDATA_URL = Config.URL + "update_patient.php";
    private static final int UPLOAD_REQUEST_CODE = 1;
    EditText edit_disease, edit_guardian, edit_caution;
    DatePicker datePicker;
    Button submit_btn;
    String image, disease, guardian, caution;
    CircleImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment);
        initView();
    }

    private void initView() {
        imageView = findViewById(R.id.image);
        edit_disease = findViewById(R.id.edit_disease);
        edit_guardian = findViewById(R.id.edit_guardian);
        edit_caution = findViewById(R.id.edit_caution);
        datePicker = findViewById(R.id.datePicker);
        submit_btn = findViewById(R.id.submit_btn);

        //DatePicker 오늘날짜로 맞추기
        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                    }
                });

        // Change Image Button Listener
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int preference = ScanConstants.OPEN_CAMERA;
                Intent intent = new Intent(TreatmentActivity.this, ScanActivity.class);
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
                startActivityForResult(intent, UPLOAD_REQUEST_CODE);
            }
        });

        // Submit Button Listener
        submit_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                disease = edit_disease.getText().toString();
                caution = edit_caution.getText().toString();
                guardian = edit_guardian.getText().toString();
                String dateresult = String.format("%d년 %d월 %d일", datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
                Log.i("eunjin_datepicker", dateresult);
                insertToDatabase(image, disease, caution, guardian, dateresult);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UPLOAD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + Config.KEY_BIRTH + "/image.jpg");
                createFile(getApplicationContext(), uri, file);
                image = file.getAbsolutePath();
                Log.i("LOG_IMA", image);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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

    private void insertToDatabase(String image, String disease, String caution, String guardian, String outdate) {

        class insertData extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String image = params[0];
                String disease = params[1];
                String caution = params[2];
                String guardian = params[3];
                String outdate = params[4];

                try {
                    Log.i("LOG_", params[0]);
                    String data = URLEncoder.encode("birth", "UTF-8") + "=" + URLEncoder.encode(Config.KEY_BIRTH, "UTF-8");
                    data += "&" + URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(image, "UTF-8");
                    data += "&" + URLEncoder.encode("disease", "UTF-8") + "=" + URLEncoder.encode(disease, "UTF-8");
                    data += "&" + URLEncoder.encode("caution", "UTF-8") + "=" + URLEncoder.encode(caution, "UTF-8");
                    data += "&" + URLEncoder.encode("guardian", "UTF-8") + "=" + URLEncoder.encode(guardian, "UTF-8");
                    data += "&" + URLEncoder.encode("outdate", "UTF-8") + "=" + URLEncoder.encode(outdate, "UTF-8");

                    URL url = new URL(CHECKDATA_URL);
                    URLConnection conn = url.openConnection();
                    Log.i("LOG_URLConnection", "1");

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                    StringBuilder sb = new StringBuilder();
                    Log.i("LOG_StringBuilder", "2");

                    String line = null;

                    // Read Server Response
                    while ((line = bufferedReader.readLine()) != null) {
                        Log.i("LOG_line", line);
                        sb.append(line);
                        break;
                    }
                    Log.i("LOG_Background", sb.toString());
                    return sb.toString();

                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                Intent intent = new Intent(TreatmentActivity.this, ShowPatientActivity.class);
                intent.putExtra("patientJSON", result);
                Log.i("LOG_Background", result);
                startActivity(intent);
            }
        }
        insertData g = new insertData();
        g.execute(image, disease, caution, guardian, outdate);
    }


}