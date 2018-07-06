package com.example.mate.gooday_mate;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;


public class PillFragment extends Fragment implements View.OnClickListener {

    final private String setType = "Pill";
    CheckBox check_breakfast, check_lunch, check_dinner;
    boolean[] mCheck_pill = {false, false, false};

    public static PillFragment createInstance() {
        PillFragment fragment = new PillFragment();
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_check, container, false);
        check_breakfast = v.findViewById(R.id.check_breakfast);
        check_lunch = v.findViewById(R.id.check_lunch);
        check_dinner = v.findViewById(R.id.check_dinner);

        v.findViewById(R.id.btn_cancel).setOnClickListener(this);
        v.findViewById(R.id.btn_success).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                Log.i("CustomFragment", "btn_cancel");
                getActivity().onBackPressed();
                break;
            case R.id.btn_success:
                mCheck_pill[0] = check_breakfast.isChecked();
                mCheck_pill[1] = check_lunch.isChecked();
                mCheck_pill[2] = check_dinner.isChecked();

                for (int i = 0; i < mCheck_pill.length; i++) {
                    Log.i("CustomFragment", String.valueOf(mCheck_pill[i]));
                }
                getActivity().onBackPressed();
                break;
        }
    }
}