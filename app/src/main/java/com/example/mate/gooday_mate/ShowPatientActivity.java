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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowPatientActivity extends AppCompatActivity implements View.OnClickListener {

    JSONArray contents = null;
    private JSONObject jsonObj;
    private String patientJSON;

    CircleImageView imgPatient;
    private String patient_name, patient_birth, patient_dateIn, patient_dateOut, patient_guardian, channel, port;
    TextView name, txtBirth, txtDisease, txtDateIn, txtDateOut, txtGuardian;
    TabbedDialog tabbedDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_patient);
        initViews();
    }

    private void initViews() {
        Intent intent = getIntent();
        patientJSON = intent.getStringExtra("patientJSON");

        try {
            jsonObj = new JSONObject(patientJSON);
            contents = jsonObj.getJSONArray("result");
            patient_name = contents.getJSONObject(0).getString("name");
            patient_birth = contents.getJSONObject(0).getString("birth");
            patient_dateIn = contents.getJSONObject(0).getString("enterdate");
            patient_dateOut = contents.getJSONObject(0).getString("outdate");
            patient_guardian = contents.getJSONObject(0).getString("guardian");
            channel = contents.getJSONObject(0).getString("channel");
            port = contents.getJSONObject(0).getString("port");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        name = findViewById(R.id.name);
        imgPatient = findViewById(R.id.image);
        txtBirth = findViewById(R.id.txt_birth);
        txtDisease = findViewById(R.id.txt_disease);
        txtDateIn = findViewById(R.id.txt_date_in);
        txtDateOut = findViewById(R.id.txt_date_out);
        txtGuardian = findViewById(R.id.txt_guardian);

        name.setText(patient_name);
        txtBirth.setText(patient_birth);
        txtDateIn.setText(patient_dateIn);
        txtDateOut.setText(patient_dateOut);
        txtGuardian.setText(patient_guardian);

        imgPatient.setOnClickListener(this);
        findViewById(R.id.btn_meal).setOnClickListener(this);
        findViewById(R.id.btn_treatment).setOnClickListener(this);
        findViewById(R.id.btn_document).setOnClickListener(this);
        tabbedDialog = new TabbedDialog();
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.image:
                //이미지 확대
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
                        startActivity(viewIntent);
                    }
                })
                        .setNegativeButton(R.string.btn_negative_txt, null)
                        .show();

                break;
        }
    }


}
