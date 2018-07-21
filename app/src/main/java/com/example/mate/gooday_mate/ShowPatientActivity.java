package com.example.mate.gooday_mate;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.example.mate.gooday_mate.Fragment.TabbedDialog;
import com.example.mate.gooday_mate.service.Config;
import com.example.mate.gooday_mate.service.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowPatientActivity extends AppCompatActivity implements View.OnClickListener {

    /* Upload/Download to S3 */
    private TransferUtility transferUtility;
    private Util util;
    private File file;
    private Bitmap bitmap;

    JSONArray contents = null;
    private JSONObject jsonObj;
    private String patientJSON, patient_name, patient_img, patient_birth;
    TextView textview;
    CircleImageView imgPatient;

    TabbedDialog tabbedDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_patient);
        util = new Util();
        transferUtility = util.getTransferUtility(this);
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
        imgPatient.setOnClickListener(this);

        Intent intent = getIntent();
        patientJSON = intent.getStringExtra("patientJSON");

        try {
            jsonObj = new JSONObject(patientJSON);//json String을 JSONObject로 변환
            contents = jsonObj.getJSONArray("result");//
            JSONObject jo = (JSONObject) contents.get(0);
            patient_name = jo.getString("name");
            patient_img = jo.getString("image");
            patient_birth = jo.getString("birth");
            Log.i("LOG_", patient_name + ":" + patient_birth);
            Log.i("LOG_", patient_img);
            Config.KEY_BIRTH = jo.getString("birth");
            Iterator key_iteraotr = jo.keys();//


            if (!patient_img.contains(patient_birth)) {
                imgPatient.setImageResource(R.mipmap.ic_patient);
            } else {
                imgPatient.setImageBitmap(BitmapFactory.decodeFile(patient_img));
            }

            while (key_iteraotr.hasNext()) {
                key = key_iteraotr.next().toString();
                value = jo.getString(key);
                resId = getResources().getIdentifier(key, "id", "com.example.mate.gooday_mate");

                //DB 값이 null 일때
                if (value.trim().equals("null") || value.trim().equals("")) {
                    textview = findViewById(resId);
                    textview.setText("진료탭을 통하여 내용을 채워주세요");
                }
                //DB 값이 있을 때
                else {
                    if (resId == 0 || key.equals("image"))
                        continue;
                    textview = findViewById(resId);
                    textview.setText(value);
                }
            }

        } catch (JSONException e) {
            Log.i("LOG_", e.getMessage());
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
                startActivity(new Intent(this, TreatmentActivity.class));
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

    public void uploadFile(Uri fileUri) {
        if (fileUri == null) {
            Toast.makeText(this, "Could not find the filepath of the selected file", Toast.LENGTH_LONG).show();
            return;
        }
        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/image.jpg");
        transferUtility.upload(Config.BUCKET_NAME + "/" + patient_birth, file.getName(), file);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
            getContentResolver().delete(fileUri, null, null);
            imgPatient.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
