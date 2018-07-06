package com.example.mate.gooday_mate;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RecyclerAdapter.ItemListener, PatientDialogFragment.PatientDialogFragmentListener {
    String SHOWDATA_URL = Config.URL + "show_patient.php";
    String CHECKDATA_URL = Config.URL + "patient_register.php";
    String echo = null;
    RecyclerAdapter recyclerAdapter;
    private String patient_JSON = null;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private AlertDialog alertDialog;
    private ImageView managerBtn, addpatientBtn, qrBtn, gpsBtn, camBtn;

    //qr code scanner object
    private IntentIntegrator qrScan;
    private JSONObject jsonObj;

    JSONArray contents = null;
    ArrayList<Item_Main> items = new ArrayList<>();
    String myJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        Log.i("eundddo_1ininViews", "beforegetdata");
        getData(SHOWDATA_URL);

        layoutManager = new GridLayoutManager(MainActivity.this, 3);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);

        //intializing scan object
        qrScan = new IntentIntegrator(this);

        managerBtn = findViewById(R.id.manager);
        addpatientBtn = findViewById(R.id.addpatient);
        qrBtn = findViewById(R.id.ic_qr);
        gpsBtn = findViewById(R.id.ic_map);
        camBtn = findViewById(R.id.ic_cam);
        managerBtn.setOnClickListener(this);
        addpatientBtn.setOnClickListener(this);
        qrBtn.setOnClickListener(this);
        gpsBtn.setOnClickListener(this);
        camBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.manager:
                AlertDialog.Builder managerbuilder = new AlertDialog.Builder(this);
                managerbuilder.setTitle("Smart Life Care 과정 1기 연수생");
                managerbuilder.setMessage("    개발자 :     강은진     권미나     김정은 ");
                alertDialog = managerbuilder.create();
                alertDialog.show();
                break;
            case R.id.addpatient:
                AlertDialog.Builder addbuilder = new AlertDialog.Builder(this);
                addbuilder.setTitle("Health Life Care System");
                addbuilder.setMessage("환자 추가 - QR코드를 준비해주세요");
                addbuilder.setNegativeButton("취소", null);
                addbuilder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //scan option
                        qrScan.setPrompt("Scanning...");
                        qrScan.initiateScan();
                        //qrScan.setOrientationLocked(false);
                    }
                });
                alertDialog = addbuilder.create();
                alertDialog.show();
                break;
            case R.id.ic_qr:
                qrScan.setPrompt("Scanning...");
                qrScan.initiateScan();
                break;
            case R.id.ic_map:
                //       startActivity(new Intent(MainActivity.this, MapActivity.class));
                break;
            case R.id.ic_cam:
                //      Intent intent = new Intent(MainActivity.this, CctvActivity.class);
                //      intent.putExtra("resultFromMainActivity", myJSON);
                //      Log.i("ERRR", myJSON);
                //    startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(final Item_Main item) {
      /*  FragmentManager manager = getSupportFragmentManager();
        PatientDialogFragment dialogFragment = new PatientDialogFragment();
        dialogFragment.show(manager, "fragment_dialog_test");
        onPatientDialogClick(dialogFragment,item.getName());*/

        AlertDialog.Builder showbuilder = new AlertDialog.Builder(this);
        showbuilder.setTitle("my Patient");
        showbuilder.setMessage(item.getName() + " 님" + "\n" + "생년월일    " + item.getBirth() + "  " + "입원일   " + item.getEnterdate());
        Config.PATIENT_NAME = item.getName();
        showbuilder.setNeutralButton("대화하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                Toast.makeText(getApplicationContext(), "개발중입니다", Toast.LENGTH_SHORT).show();
            }
        });
        showbuilder.setNegativeButton("보호자와 통화", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + item.getPhone())));
            }
        });

        showbuilder.setPositiveButton("현재상태 확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String json = "{'name':'" + item.getName() + "','birth':'" + item.getBirth() + "','sex':'" + item.getSex() + "','phone':'" + item.getPhone() + "','enterdate':'" + item.getEnterdate() + "','image':'" + item.getImg() + "','channel':'" + item.getChannel() + "','port':'" + item.getPort() + "'}";
                insertToDatabase(CHECKDATA_URL, json);
            }
        });
        alertDialog = showbuilder.create();
        alertDialog.show();
    }


    @Override
    public void onPatientDialogClick(DialogFragment dialogFragment, String someData) {
    }

    private void getData(String data_url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                StringBuilder sb = new StringBuilder();
                BufferedReader bufferedReader = null;

                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString();

                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                myJSON = result;
                showList();
            }

        }
        GetDataJSON g = new GetDataJSON();
        g.execute(data_url);
    }

    private void insertToDatabase(String url, String patient_JSON) {
        Log.i("eundddo_8stinsert", url);

        class insertData extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                Log.i("eundddo_11doI", params[0]);
                String uri = params[0];
                StringBuilder sb = new StringBuilder();
                BufferedReader bufferedReader = null;

                try {
                    URL url = new URL(uri);
                    Log.i("eundddo_12doI", params[1]);
                    jsonObj = new JSONObject(params[1]);
                    String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(jsonObj.getString("name"), "UTF-8");
                    data += "&" + URLEncoder.encode("birth", "UTF-8") + "=" + URLEncoder.encode(jsonObj.getString("birth"), "UTF-8");
                    data += "&" + URLEncoder.encode("sex", "UTF-8") + "=" + URLEncoder.encode(jsonObj.getString("sex"), "UTF-8");
                    data += "&" + URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(jsonObj.getString("phone"), "UTF-8");
                    data += "&" + URLEncoder.encode("enterdate", "UTF-8") + "=" + URLEncoder.encode(jsonObj.getString("enterdate"), "UTF-8");
                    data += "&" + URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(jsonObj.getString("image"), "UTF-8");
                    data += "&" + URLEncoder.encode("channel", "UTF-8") + "=" + URLEncoder.encode(jsonObj.getString("channel"), "UTF-8");
                    data += "&" + URLEncoder.encode("port", "UTF-8") + "=" + URLEncoder.encode(jsonObj.getString("port"), "UTF-8");

                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    Log.i("eundddo_13_1sb", data);

                    wr.write(data);
                    Log.i("eundddo_13_2sb", wr.toString());
                    wr.flush();
                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    Log.i("eundddo_13_3sb", bufferedReader.readLine());

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
                Log.i("eundddo_14Post", result);

                if (result.contains("already")) {
                    Intent showIntent = new Intent(MainActivity.this, ShowPatientActivity.class);
                    String json = result.substring(14);
                    showIntent.putExtra("patientJSON", json);
                    startActivity(showIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "추가완료", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                }


            }
        }
        Log.i("eundddo_9stinserturl", url);
        insertData g = new insertData();
        g.execute(url, patient_JSON);
        Log.i("eundddo_10patient_JSON", patient_JSON);
    }

    protected void showList() {
        try {
            jsonObj = new JSONObject(myJSON);
            contents = jsonObj.getJSONArray("result");

            for (int i = 0; i < contents.length(); i++) {
                JSONObject c = contents.getJSONObject(i);
                String id = c.getString("id");
                String name = c.getString("name");
                String birth = c.getString("birth");
                String sex = c.getString("sex");
                String phone = c.getString("phone");
                String enterdate = c.getString("enterdate");
                String image = c.getString("image");
                String channel = c.getString("channel");
                String port = c.getString("port");

                items.add(new Item_Main(name, birth, sex, enterdate, phone, R.mipmap.mate_logo, channel, port));
            }
            recyclerAdapter = new RecyclerAdapter(this, items, this);
            recyclerView.setAdapter(recyclerAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) { //qrcode 가 없으면
            if (result.getContents() == null) {
                Toast.makeText(MainActivity.this, "취소!", Toast.LENGTH_SHORT).show();
            } else { //qrcode 결과가 있으면
                patient_JSON = result.getContents();
                insertToDatabase(CHECKDATA_URL, patient_JSON);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}