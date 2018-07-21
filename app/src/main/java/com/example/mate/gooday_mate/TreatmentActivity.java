package com.example.mate.gooday_mate;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mate.gooday_mate.service.Config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;


public class TreatmentActivity extends AppCompatActivity implements View.OnClickListener {
    String CHECKDATA_URL = Config.URL + "update_patient.php";
    TextView outdate;
    EditText edit_disease, edit_guardian, edit_caution;
    Button submit_btn;
    String image, disease, guardian, caution;
    Calendar dateAndTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment);
        initView();
    }

    private void initView() {
        edit_disease = findViewById(R.id.edit_disease);
        edit_guardian = findViewById(R.id.edit_guardian);
        edit_caution = findViewById(R.id.edit_caution);
        outdate = findViewById(R.id.outdate);
        outdate.setOnClickListener(this);
        findViewById(R.id.submit_btn).setOnClickListener(this);
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
                String disease = (String) params[0];
                String caution = (String) params[1];
                String guardian = (String) params[2];
                String outdate = (String) params[3];

                try {
                    Log.i("eundddo_12doI", params[1]);
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
        }
    }
}