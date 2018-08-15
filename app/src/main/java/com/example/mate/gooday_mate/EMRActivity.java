package com.example.mate.gooday_mate;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.mate.gooday_mate.service.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EMRActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    String UPDATE_URL = Config.URL + "update_emr.php";
    String getDATA_URL = Config.URL + "getdata_emr.php";

    private String emrJsonString;
    int treat, residence, meal, independent;
    TextView date, writer, treatTime, visitTime;
    EditText hospitalNm, visitorNm, reasonMeal;
    RadioGroup treatBtns, residenceBtns, mealBtns_1, mealBtns_2, independentBtns;
    Switch isBedsore;
    final Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emr);
        initViews();

        UpdateData checkDB = new UpdateData();
        checkDB.execute(getDATA_URL);

    }

    private void initViews() {

        date = findViewById(R.id.date);
        writer = findViewById(R.id.writer);

        treatBtns = findViewById(R.id.treatBtns);
        hospitalNm = findViewById(R.id.hospitalNm);
        treatTime = findViewById(R.id.treatTime);

        residenceBtns = findViewById(R.id.residenceBtns);
        visitorNm = findViewById(R.id.visitorNm);
        visitTime = findViewById(R.id.visitTime);

        mealBtns_1 = findViewById(R.id.mealBtns_1);
        mealBtns_2 = findViewById(R.id.mealBtns_2);
        reasonMeal = findViewById(R.id.reasonMeal);
        isBedsore = findViewById(R.id.isBedsore);

        date.setText(new SimpleDateFormat("yyyy.MM.dd").format(new Date(System.currentTimeMillis())));

        independentBtns = findViewById(R.id.independentBtns);

        treatBtns.setOnCheckedChangeListener(this);
        treatTime.setOnClickListener(this);
        residenceBtns.setOnCheckedChangeListener(this);
        visitTime.setOnClickListener(this);
        mealBtns_1.setOnCheckedChangeListener(this);
        mealBtns_2.setOnCheckedChangeListener(this);
        independentBtns.setOnCheckedChangeListener(this);

        isBedsore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    findViewById(R.id.bedsoreLayout).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.bedsoreLayout).setVisibility(View.GONE);
                }
            }
        });
        findViewById(R.id.cancel).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);
    }


    private void initDatas(String JsonString) {
        Log.i("EMRActivity", "initDatas " + JsonString);
        try {
            JSONObject jsonObject = new JSONObject(JsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("result");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                writer.setText(item.getString("writer"));
                treatBtns.check(Integer.parseInt(item.getString("treat")));
                hospitalNm.setText(item.getString("hospital"));
                treatTime.setText(item.getString("treatTime"));
                residenceBtns.check(Integer.parseInt(item.getString("residence")));
                visitorNm.setText(item.getString("visitor"));
                visitTime.setText(item.getString("visitTime"));
            }
        } catch (JSONException e) {
            Log.i("EMRActivity", "initDatas :catch " + e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        TimePickerDialog TimePickerDialog;

        switch (view.getId()) {
            case R.id.treatTime:
                TimePickerDialog = new TimePickerDialog(EMRActivity.this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        treatTime.setText(hour + ":" + minute);
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
                TimePickerDialog.show();
                break;

            case R.id.visitTime:
                TimePickerDialog = new TimePickerDialog(EMRActivity.this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        visitTime.setText(hour + ":" + minute);
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
                TimePickerDialog.show();
                break;
            case R.id.cancel:
                break;
            case R.id.save:
                UpdateData insertToDB = new UpdateData();
                insertToDB.execute(UPDATE_URL, String.valueOf(treat), String.valueOf(residence), String.valueOf(meal), String.valueOf(independent));
                break;
        }
    }


    class UpdateData extends AsyncTask<String, Void, String> {
        String errStr = null;
        String data = null;

        @Override
        protected void onPostExecute(String result) {

            if (result.contains("success")) {
                finish();
            } else if (result.contains("failure")) {
                Log.i("EMRActivity", "Sync Error " + errStr);
            } else {
                emrJsonString = result;
                initDatas(emrJsonString);
            }

        }

        @Override
        protected String doInBackground(String... params) {

            if (params[0].contains("get")) {
                data = "birth=" + Config.KEY_BIRTH + "&date=" + new SimpleDateFormat("yyyy.MM.dd").format(new Date(System.currentTimeMillis()));
            } else {
                String treat = params[1];
                String residence = params[2];
                String meal = params[3];
                String independent = params[4];

                data = "birth=" + Config.KEY_BIRTH + "&date=" + date.getText().toString() + "&writer=" + writer.getText().toString() + "&treat=" + treat
                        + "&hospital=" + hospitalNm.getText().toString() + "&treatTime=" + treatTime.getText().toString() + "&residence=" + residence
                        + "&visitor=" + visitorNm.getText().toString() + "&visitTime=" + visitTime.getText().toString() + "&meal=" + meal + "&independent=" + independent;
            }
            Log.i("EMRActivity", data);
            try {
                URL url = new URL(params[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(data.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("EMRActivity", "BACKGROUND response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();

            } catch (Exception e) {
                errStr = e.toString();
                return null;
            }
        }


    }


    @SuppressLint("ResourceType")
    private int getRadioInt(RadioGroup group) {
        int selectedId = 0;
        if (group.getCheckedRadioButtonId() > 0) {
            int radioId = group.indexOfChild(group.findViewById(group.getCheckedRadioButtonId()));
            RadioButton btn = (RadioButton) group.getChildAt(radioId);
            selectedId = btn.getId();
        }
        return selectedId;
    }

    /*  Radio Button CheckListener */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group == treatBtns) {
            if (checkedId != -1) {
                findViewById(R.id.treatLayout).setVisibility(View.VISIBLE);

                treat = getRadioInt(group);
            }
        } else if (group == residenceBtns) {
            if (checkedId != -1) {
                findViewById(R.id.residenceLayout).setVisibility(View.VISIBLE);

                residence = getRadioInt(group);

            }
        } else if (group == mealBtns_1) {
            if (checkedId != -1) {
                mealBtns_2.setOnCheckedChangeListener(null);
                mealBtns_2.clearCheck();
                mealBtns_2.setOnCheckedChangeListener(this);

                findViewById(R.id.mealLayout).setVisibility(View.VISIBLE);

                meal = getRadioInt(group);
            }
        } else if (group == mealBtns_2) {
            if (checkedId != -1) {
                mealBtns_1.setOnCheckedChangeListener(null);
                mealBtns_1.clearCheck();
                mealBtns_1.setOnCheckedChangeListener(this);

                findViewById(R.id.mealLayout).setVisibility(View.VISIBLE);

                meal = getRadioInt(group);
            }
        } else if (group == independentBtns) {
            if (checkedId != -1) {
                independent = getRadioInt(group);
            }
        }

    }

}

