package com.example.mate.gooday_mate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabbedDialog extends DialogFragment {
    // 식사 / 약 tab이 있는 다이얼로그

    // 식사/약 탭
    TabLayout tabLayout;

    // 각각의 탭의 view
    ViewPager viewPager;

    @Override
    public void onStart() {
        super.onStart();
        // safety check
        if (getDialog() == null)
            return;

        int dialogWidth = 800;
        int dialogHeight = 900;

        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.dialog_check, container, false);
        tabLayout = rootview.findViewById(R.id.tabLayout);
        viewPager = rootview.findViewById(R.id.checkViewPager);

        CustomAdapter adapter = new CustomAdapter(getChildFragmentManager());
        adapter.addFragment("식사", MealFragment.createInstance());
        adapter.addFragment("약", PillFragment.createInstance());

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return rootview;
    }

}