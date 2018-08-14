package com.example.mate.gooday_mate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.mate.gooday_mate.Fragment.TabbedDialog;
import com.example.mate.gooday_mate.service.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowPatientActivity extends AppCompatActivity implements View.OnClickListener {

    JSONArray contents = null;
    private JSONObject jsonObj;
    private String patientJSON, patient_name, patient_img, patient_birth, emrJSON;
    TextView textview;
    CircleImageView imgPatient;

    TabbedDialog tabbedDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_patient);
        initViews();
    }

    private void initViews() {
        //***Json 값에 따라 TextView Setting***//
        int resId;
        String key, value;

        imgPatient = findViewById(R.id.image);
        findViewById(R.id.btn_meal).setOnClickListener(this);
        findViewById(R.id.btn_treatment).setOnClickListener(this);
        findViewById(R.id.btn_document).setOnClickListener(this);

        Intent intent = getIntent();
        patientJSON = intent.getStringExtra("patientJSON");
        emrJSON = patientJSON;

        try {
            jsonObj = new JSONObject(patientJSON);//json String을 JSONObject로 변환
            contents = jsonObj.getJSONArray("result");//
            JSONObject jo = (JSONObject) contents.get(0);
            patient_name = jo.getString("name");
            patient_img = jo.getString("image");
            patient_birth = jo.getString("birth");
            Config.KEY_BIRTH = jo.getString("birth");
            Iterator key_iteraotr = jo.keys();//

            imgPatient.setImageResource(getResources().getIdentifier(patient_img, "mipmap", this.getPackageName()));

            while (key_iteraotr.hasNext()) {
                key = key_iteraotr.next().toString();
                value = jo.getString(key);
                resId = getResources().getIdentifier(key, "id", "com.example.mate.gooday_mate");

                if (resId != R.id.image && (value.trim().equals("null") || value.trim().equals(""))) {
                    textview = findViewById(resId);
                    textview.setText("진료탭을 통하여 내용을 채워주세요");
                }

                //DB 값이 있을 때
                else if (resId != R.id.image) {
                    if (resId == 0 || key.equals("image"))
                        continue;
                    textview = findViewById(resId);
                    textview.setText(value);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        tabbedDialog = new TabbedDialog();
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.image:
                break;

            case R.id.btn_meal:
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                tabbedDialog.setPatient_Key(patient_birth);
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
                        viewIntent.putExtra("birth", patient_birth);
                        startActivity(viewIntent);
                    }
                })
                        .setNegativeButton(R.string.btn_negative_txt, null)
                        .show();

                break;
        }

    }


}
