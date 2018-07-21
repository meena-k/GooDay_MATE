package com.example.mate.gooday_mate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mate.gooday_mate.service.Config;
import com.example.mate.gooday_mate.service.InsertInfoData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;

public class RegisterManagerActivity extends AppCompatActivity {
    EditText edit_id, edit_pw, edit_email, edit_name, edit_phone;
    HashMap<String, String> userMap;
    InsertInfoData user;
    String REGISTER_URL = Config.URL + "manager_register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_manager);
        initView();
    }

    private void initView() {
        Button btn_workerRegister = (Button) findViewById(R.id.btn_manager_register);

        edit_id = findViewById(R.id.edit_id);
        edit_pw = findViewById(R.id.edit_pw);
        edit_email = findViewById(R.id.edit_email);
        edit_name = findViewById(R.id.edit_name);
        edit_phone = findViewById(R.id.edit_phone);

        btn_workerRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*userMap = new HashMap<String, String>();
                userMap.put("manager_id", edit_id.getText().toString());
                userMap.put("manager_pw", edit_pw.getText().toString());
                userMap.put("manager_email", edit_email.getText().toString());
                userMap.put("manager_phone", edit_email.getText().toString());*/
                insertToDatabase(edit_id.getText().toString(), edit_name.getText().toString(), edit_pw.getText().toString(), edit_email.getText().toString(), edit_phone.getText().toString());
                Intent intent = new Intent(RegisterManagerActivity.this, LoginActivity.class);
                startActivity(intent);
                //Log.i("btnMap", userMap.toString());
                //user = new InsertInfoData(userMap);
            }
        });

    }

    private void insertToDatabase(String manager_id, String manager_name, String manager_pw, String manager_email, String manager_phone) {

        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(RegisterManagerActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    String manager_id = (String) params[0];
                    String manager_name = (String) params[1];
                    String manager_pw = (String) params[2];
                    String manager_email = (String) params[3];
                    String manager_phone = (String) params[4];


                    String data = URLEncoder.encode("manager_id", "UTF-8") + "=" + URLEncoder.encode(manager_id, "UTF-8");
                    data += "&" + URLEncoder.encode("manager_name", "UTF-8") + "=" + URLEncoder.encode(manager_name, "UTF-8");
                    data += "&" + URLEncoder.encode("manager_pw", "UTF-8") + "=" + URLEncoder.encode(manager_pw, "UTF-8");
                    data += "&" + URLEncoder.encode("manager_email", "UTF-8") + "=" + URLEncoder.encode(manager_email, "UTF-8");
                    data += "&" + URLEncoder.encode("manager_phone", "UTF-8") + "=" + URLEncoder.encode(manager_phone, "UTF-8");

                    URL url = new URL(REGISTER_URL);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();

                } catch (Exception e) {
                    Log.i("err", e.getMessage());
                    return new String("Exception: " + e.getMessage());

                }

            }
        }


        InsertData task = new InsertData();
        task.execute(manager_id, manager_name, manager_pw, manager_email, manager_phone);

    }

}
