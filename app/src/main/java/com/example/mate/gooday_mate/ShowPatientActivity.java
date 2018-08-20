package com.example.mate.gooday_mate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.example.mate.gooday_mate.Fragment.TabbedDialog;
import com.example.mate.gooday_mate.service.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowPatientActivity extends AppCompatActivity implements View.OnClickListener {

    private String patientJSON, patient_name, patient_img;
    TextView textview;
    CircleImageView imgPatient;

    TabbedDialog tabbedDialog;
    private WebView temp_chartWebView, pulse_chartWebView;
    private WebSettings set_temp, set_pulse;
    final Handler handler = new Handler();
    int handlerTest;

    /*Activity focus 잃었을 때 핸들러 종료*/
    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.removeMessages(0);
        }
    }

    //Activity focus 돌아왔을 때 핸들러 재시작
    @Override
    protected void onResume() {
        super.onResume();
        handlerTest = 0;
        reloadView();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_patient);
        initViews();
    }

    private void initViews() {
        textview = findViewById(R.id.name);
        imgPatient = findViewById(R.id.image);

        findViewById(R.id.show_btn).setOnClickListener(this);
        findViewById(R.id.btn_info).setOnClickListener(this);
        findViewById(R.id.btn_treatment).setOnClickListener(this);
        findViewById(R.id.btn_document).setOnClickListener(this);

        Intent intent = getIntent();
        patientJSON = intent.getStringExtra("patientJSON");

        try {
            JSONObject jsonObject = new JSONObject(patientJSON);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            JSONObject jo = (JSONObject) jsonArray.get(0);
            patient_name = jo.getString("name");
            Config.KEY_BIRTH = jo.getString("birth");
            patient_img = jo.getString("image");
            textview.setText(patient_name);
            imgPatient.setImageResource(getResources().getIdentifier(patient_img, "mipmap", this.getPackageName()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*온도 구글차트*/
        temp_chartWebView = findViewById(R.id.google_chart_temp);
        temp_chartWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        temp_chartWebView.setWebViewClient(new WebViewClient());
        temp_chartWebView.setInitialScale(190);
        set_temp = temp_chartWebView.getSettings();
        set_temp.setJavaScriptEnabled(true);
        set_temp.setBuiltInZoomControls(true);
        //set_temp.setLoadWithOverviewMode(true);

        /*심박센서 구글차트*/
        pulse_chartWebView = findViewById(R.id.google_chart_pulse);
        pulse_chartWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        pulse_chartWebView.setWebViewClient(new WebViewClient());
        pulse_chartWebView.setInitialScale(190);
        set_pulse = pulse_chartWebView.getSettings();
        set_pulse.setJavaScriptEnabled(true);
        set_pulse.setBuiltInZoomControls(true);
        //set_temp.setLoadWithOverviewMode(true);

        temp_chartWebView.loadUrl(Config.URL + "/google_temp_chart.php");
        pulse_chartWebView.loadUrl(Config.URL + "/google_pulse_chart.php");

        //핸들러로 webView Reload
        reloadView();

        tabbedDialog = new TabbedDialog();
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.image:
                break;
            case R.id.show_btn:
                startActivity(new Intent(ShowPatientActivity.this, GoogleChartTextActivity.class));
                break;
            case R.id.btn_info:
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                Log.i("ShowPatientLOG", patientJSON);
                tabbedDialog.setPatientJSON(patientJSON);
                tabbedDialog.show(ft, "dialog");
                break;

            case R.id.btn_treatment:
                // 환자 추가 정보 업로드 액티비티
                startActivity(new Intent(this, EMRActivity.class));
                break;

            case R.id.btn_document:
                AlertDialog.Builder builder_document = new AlertDialog.Builder(this);
                builder_document.setItems(R.array.document_array, new DialogInterface.OnClickListener() {
                    Intent viewIntent;

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewIntent = new Intent(ShowPatientActivity.this, ViewDocumentActivity.class);

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
                        viewIntent.putExtra("name", patient_name);
                        viewIntent.putExtra("birth", Config.KEY_BIRTH);
                        startActivity(viewIntent);
                    }
                })
                        .setNegativeButton(R.string.btn_negative_txt, null)
                        .show();

                break;
        }

    }

    private void reloadView() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                //Toast.makeText(LogThirdPager.this, "Hello", Toast.LENGTH_SHORT).show();
                // mWebview.loadUrl("http://www.google.com");
                handlerTest++;
                temp_chartWebView.reload();
                pulse_chartWebView.reload();
                reloadView();
            }
        }, 5000);

    }

}
