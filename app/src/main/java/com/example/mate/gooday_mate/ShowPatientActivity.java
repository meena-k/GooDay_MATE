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

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowPatientActivity extends AppCompatActivity implements View.OnClickListener {

    private String patientJSON, patient_name, patient_img;
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
        textview = findViewById(R.id.name);
        imgPatient = findViewById(R.id.image);

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
            patient_img = jo.getString("image");
            Config.KEY_BIRTH = jo.getString("birth");
            textview.setText(patient_name);
            imgPatient.setImageResource(getResources().getIdentifier(patient_img, "mipmap", this.getPackageName()));

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

            case R.id.btn_info:
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
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


}
