package com.example.mate.gooday_mate.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mate.gooday_mate.R;
import com.example.mate.gooday_mate.adapter.CustomAdapter;

public class TabbedDialog extends DialogFragment {
    // 환자 정보 tab이 있는 다이얼로그

    // 정보 탭
    TabLayout tabLayout;

    // 각각의 탭의 view
    ViewPager viewPager;
    String patientJSON;

    @Override
    public void onStart() {
        super.onStart();
        // safety check
        if (getDialog() == null)
            return;

        int dialogWidth = 900;
        int dialogHeight = 1000;

        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);

    }

    public void setPatientJSON(String patientJSON) {
        this.patientJSON = patientJSON;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.dialog_check, container, false);
        tabLayout = rootview.findViewById(R.id.tabLayout);
        viewPager = rootview.findViewById(R.id.checkViewPager);

        CustomAdapter adapter = new CustomAdapter(getChildFragmentManager());
        adapter.addFragment("개인정보", InfoFragment.createInstance(patientJSON));
        adapter.addFragment("질병정보", Info2Fragment.createInstance(patientJSON));

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return rootview;
    }

}