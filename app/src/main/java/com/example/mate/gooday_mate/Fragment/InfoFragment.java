package com.example.mate.gooday_mate.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mate.gooday_mate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


public class InfoFragment extends Fragment implements View.OnClickListener {
    private String patientJSON;
    TextView textview;

    public static InfoFragment createInstance(String patientJSON) {
        InfoFragment fragment = new InfoFragment();
        fragment.patientJSON = patientJSON;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_info_patient, container, false);
        initDatas(v);
        v.findViewById(R.id.btn_cancel).setOnClickListener(this);
        v.findViewById(R.id.btn_success).setOnClickListener(this);

        return v;
    }

    private void initDatas(View v) {
        int resId;
        String key, value;
        try {
            JSONArray jsonArray = new JSONObject(patientJSON).getJSONArray("result");
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            Iterator key_iterator = jsonObject.keys();
            while (key_iterator.hasNext()) {
                key = key_iterator.next().toString();
                value = jsonObject.getString(key);
                resId = getResources().getIdentifier(key, "id", "com.example.mate.gooday_mate");
                textview = v.findViewById(resId);

                if (!key.equals("id") && !key.equals("name") && !key.equals("image")) {
                    if ((value.trim().equals("null") || value.trim().equals(""))) {
                        textview.setText("진료탭을 통하여 내용을 채워주세요");
                    } else if (resId == R.id.birth || resId == R.id.sex || resId == R.id.phone || resId == R.id.guardian) {
                        textview.setText(value);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                getActivity().onBackPressed();
                break;
            case R.id.btn_success:
                getActivity().onBackPressed();
                break;
        }
    }


}