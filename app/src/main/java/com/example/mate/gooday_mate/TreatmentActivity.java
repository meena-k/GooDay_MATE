package com.example.mate.gooday_mate;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mate.gooday_mate.service.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Iterator;


public class TreatmentActivity extends AppCompatActivity implements View.OnClickListener {
    String CHECKDATA_URL = Config.URL + "update_patient.php";
    private static final int GET_DOC_REQUEST_CODE = 1;

    EditText edit_disease, edit_guardian, edit_caution, editText;
    TextView outdate, textview;
    String image, disease, guardian, caution, folder_birth, folder_name;
    Button submit_btn;
    ImageView docImg;
    private String intentJSON;
    private JSONObject jsonObj;
    JSONArray contents = null;
    Calendar dateAndTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment);
        initView();
    }

    private void initView() {
        String key, value;
        int resId;
        intentJSON = getIntent().getStringExtra("treatmentJSON");//DB값

        edit_disease = findViewById(R.id.disease);
        edit_guardian = findViewById(R.id.guardian);
        edit_caution = findViewById(R.id.caution);
        outdate = findViewById(R.id.outdate);
        docImg = findViewById(R.id.docImg);

        outdate.setOnClickListener(this);
        docImg.setOnClickListener(this);
        findViewById(R.id.submit_btn).setOnClickListener(this);

        try {
            jsonObj = new JSONObject(intentJSON);//json String을 JSONObject로 변환

            contents = jsonObj.getJSONArray("result");//
            JSONObject jo = (JSONObject) contents.get(0);
            folder_birth = jo.getString("birth");
            folder_name = jo.getString("name");

            Iterator key_iteraotr = jo.keys();

            while (key_iteraotr.hasNext()) {
                key = key_iteraotr.next().toString();
                value = jo.getString(key);
                resId = getResources().getIdentifier(key, "id", "com.example.mate.gooday_mate");

                if (value.trim().equals("") || resId == 0 || resId == R.id.image)//null이거나 layout에 R.id 존재하지 않을 때
                {
                    continue;
                } else if (resId == R.id.disease || resId == R.id.caution || resId == R.id.guardian) {
                    editText = findViewById(resId);
                    editText.setText(value);
                } else {
                    textview = (TextView) findViewById(resId);
                    textview.setText(value);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            outdate.setText(year + "년" + monthOfYear + "월" + dayOfMonth + "일");
        }

    };

    private void insertToDatabase(String disease, String caution, String guardian, String outdate) {

        class insertData extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                String disease = params[0];
                String caution = params[1];
                String guardian = params[2];
                String outdate = params[3];

                try {
                    String data = URLEncoder.encode("birth", "UTF-8") + "=" + URLEncoder.encode(Config.KEY_BIRTH, "UTF-8");
                    data += "&" + URLEncoder.encode("disease", "UTF-8") + "=" + URLEncoder.encode(disease, "UTF-8");
                    data += "&" + URLEncoder.encode("caution", "UTF-8") + "=" + URLEncoder.encode(caution, "UTF-8");
                    data += "&" + URLEncoder.encode("guardian", "UTF-8") + "=" + URLEncoder.encode(guardian, "UTF-8");
                    data += "&" + URLEncoder.encode("outdate", "UTF-8") + "=" + URLEncoder.encode(outdate, "UTF-8");

                    URL url = new URL(CHECKDATA_URL);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    Log.i("eundddo_13_1sb", data);

                    wr.write(data);
                    Log.i("eundddo_13_2sb", wr.toString());
                    wr.flush();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    Log.i("eundddo_13_3sb", bufferedReader.readLine());

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    Log.i("eundddo_13_4sb", sb.toString());
                    return sb.toString();

                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                Intent intent = new Intent(TreatmentActivity.this, ShowPatientActivity.class);
                intent.putExtra("patientJSON", result);
                startActivity(intent);
                finish();
            }
        }
        insertData g = new insertData();
        g.execute(disease, caution, guardian, outdate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_DOC_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String path = data.getStringExtra("fileUri");
                Bitmap bmp = BitmapFactory.decodeFile(path);
                docImg.setImageBitmap(bmp);
            }
        }

    }

    public void onClick(View v) {
        switch (v.getId()) {
            //퇴원날짜
            case R.id.outdate:
                new DatePickerDialog(this, listener,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
                break;

            //환자 정보 수정
            case R.id.submit_btn:
                disease = edit_disease.getText().toString();
                caution = edit_caution.getText().toString();
                guardian = edit_guardian.getText().toString();
                String dateresult = outdate.getText().toString();
                insertToDatabase(disease, caution, guardian, dateresult);
                break;

            case R.id.docImg:
                AlertDialog.Builder builder_document = new AlertDialog.Builder(this);
                builder_document.setItems(R.array.document_array, new DialogInterface.OnClickListener() {
                    Intent viewIntent;

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewIntent = new Intent(TreatmentActivity.this, TreatmentImgDialogActivity.class);

                        switch (which) {
                            case 0:
                                viewIntent.putExtra("folder", "PRESCRIPTION");
                                break;
                            case 1:
                                viewIntent.putExtra("folder", "X-RAY");
                                break;
                            case 2:
                                viewIntent.putExtra("folder", "MRI");
                                break;
                        }
                        viewIntent.putExtra("name", folder_name);
                        viewIntent.putExtra("birth", folder_birth);
                        startActivityForResult(viewIntent, GET_DOC_REQUEST_CODE);
                    }
                })
                        .setNegativeButton(R.string.btn_negative_txt, null)
                        .show();
                ;

                break;

        }
    }
}

