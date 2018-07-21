package com.example.mate.gooday_mate.Fragment;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mate.gooday_mate.R;

public class PatientDialogFragment extends DialogFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PatientDialogFragment() {
        // Required empty public constructor
    }

    public static PatientDialogFragment newInstance(String param1, String param2) {
        PatientDialogFragment fragment = new PatientDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    //Listener interface 선언
    public interface PatientDialogFragmentListener {
        void onPatientDialogClick(DialogFragment dialogFragment, String someData);
    }

    //Listener interface의 빈 객체 선언
    PatientDialogFragmentListener patientDialogFragmentListener;

    //Listener interface와 Activity 연결
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            patientDialogFragmentListener = (PatientDialogFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement PatientDialogFragmentListener");
        }
    }

    public void someAction() {
        patientDialogFragmentListener.onPatientDialogClick(
                PatientDialogFragment.this, "Some Data");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setTitle(" 님");
        //  getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        return inflater.inflate(R.layout.fragment_dialog, container);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
