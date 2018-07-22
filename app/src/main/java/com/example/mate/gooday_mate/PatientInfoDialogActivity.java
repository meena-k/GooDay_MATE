package com.example.mate.gooday_mate;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
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
import java.util.HashMap;

public class PatientInfoDialogActivity extends AppCompatActivity implements Button.OnClickListener {
    private WebView chart_temp, chart_press, chart_pulse;
    String DATA_URL = Config.URL + "search_patient.php";
    String name, birth, phone, disease, image, channel;
    TextView birthTxt, phoneTxt, diseaseTxt, nameTxt;
    String myJSON;
    JSONArray contents = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.7f;
        getWindow().setAttributes(layoutParams);


        setContentView(R.layout.activity_patient_info_dialog);
        Intent intent = getIntent();
        HashMap<String, String> patientMap;
        patientMap = (HashMap<String, String>) intent.getSerializableExtra("patientMap");
        // String channel="2";
        getDB(DATA_URL, patientMap.get("name"), patientMap.get("birth"));

    }

    public void setData(String result) {
        Log.i("eunlogset0", result);

        try {
            JSONObject jsonObj = new JSONObject(result);
            contents = jsonObj.getJSONArray("result");

            for (int i = 0; i < contents.length(); i++) {
                JSONObject c = contents.getJSONObject(i);
                Log.i("eunlogset", c.toString());
                birth = c.getString("birth");
                name = c.getString("name");
                phone = c.getString("phone");
                image = c.getString("image");
                channel = c.getString("channel");
                disease = c.getString("disease");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        initViews();
    }

    private void initViews() {
        nameTxt = findViewById(R.id.name);
        nameTxt.setText(name);
        birthTxt = findViewById(R.id.birth);
        birthTxt.setText(birth);
        phoneTxt = findViewById(R.id.phone);
        phoneTxt.setText(phone);
        diseaseTxt = findViewById(R.id.disease);
        diseaseTxt.setText(disease);
        chart_temp = findViewById(R.id.chart_temp);
        chart_temp.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        chart_temp.setWebViewClient(new WebViewClient());
        chart_temp.setInitialScale(190);
        WebSettings set_temp = chart_temp.getSettings();
        set_temp.setJavaScriptEnabled(true);
        set_temp.setBuiltInZoomControls(true);
        set_temp.setLoadWithOverviewMode(true);
        chart_temp.loadUrl("https://thingspeak.com/channels/" + channel + "/charts/1");

        chart_pulse = findViewById(R.id.chart_pulse);
        chart_pulse.setWebViewClient(new WebViewClient());
        chart_pulse.setInitialScale(190);
        WebSettings set_pulse = chart_pulse.getSettings();
        set_pulse.setJavaScriptEnabled(true);
        set_pulse.setBuiltInZoomControls(true);
        set_pulse.setLoadWithOverviewMode(true);
        chart_pulse.loadUrl("https://thingspeak.com/channels/" + channel + "/charts/3");

//        findViewById(R.id.img).setOnClickListener(this);
        findViewById(R.id.info_img).setOnClickListener(this);
        findViewById(R.id.phone_img).setOnClickListener(this);
        findViewById(R.id.message_img).setOnClickListener(this);


    }


    public void getDB(String url, String name, String birth) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String name = params[1];
                String birth = params[2];

                Log.i("eunjin_getdata", name + birth);

                BufferedReader bufferedReader = null;
                try {
                    String data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                    data += "&" + URLEncoder.encode("birth", "UTF-8") + "=" + URLEncoder.encode(birth, "UTF-8");

                    URL url = new URL(uri);
                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    //setMyJSON(sb.toString());
                    Log.i("eunjin_sb", sb.toString().trim());
                    return sb.toString().trim();
                } catch (Exception e) {
                    Log.i("err", e.getMessage());
                    return new String("Exception: " + e.getMessage());
                }
            }

            @Override
            protected void onPostExecute(String result) {
                myJSON = result;
                Log.i("eunjin_getData_json", result);
                setData(result);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(DATA_URL, name, birth);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image:
                //이미지 확대
                break;

            case R.id.info_img:
                Intent intent_info = new Intent(PatientInfoDialogActivity.this, ShowPatientActivity.class);
                intent_info.putExtra("patientJSON", myJSON);
                startActivity(intent_info);
                break;

            case R.id.phone_img:
                // 환자 추가 정보 업로드 액티비티
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
                break;

            case R.id.message_img:
                // 환자 추가 정보 업로드 액티비티
                Log.i("treatment", "enter");
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("smsto:" + phone));
                intent.putExtra("sms_body", name + "님 안녕하세요 :D");
                startActivity(intent);
                break;
        }
    }
}