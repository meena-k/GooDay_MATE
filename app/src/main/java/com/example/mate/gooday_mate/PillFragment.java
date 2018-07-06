package com.example.mate.gooday_mate;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class PillFragment extends Fragment implements View.OnClickListener {
    final String SETDATA_URL = Config.URL + "set_checkdata.php";
    final String GETPILL_URL = Config.URL + "getcheck_pill.php";

    private CheckBox check_breakfast, check_lunch, check_dinner;

    JSONArray contents = null;
    private String myJSON, patient_birth;

    public static PillFragment createInstance(String patient_birth) {
        PillFragment fragment = new PillFragment();
        fragment.patient_birth = patient_birth;
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getData(GETPILL_URL);

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
                insertToDatabase(patient_birth, "pill_check", getCheck_pill());
                getActivity().onBackPressed();
                break;
        }
    }


    private boolean[] getCheck_pill() {

        return new boolean[]{check_breakfast.isChecked(), check_lunch.isChecked(), check_dinner.isChecked()};
    }

    private void setCheck_pill(boolean[] check) {
        check_breakfast.setChecked(check[0]);
        check_lunch.setChecked(check[1]);
        check_dinner.setChecked(check[2]);
    }

    private void getData(final String data_url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString();

                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                myJSON = result;
                showList(data_url);
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(data_url);
    }

    private void showList(String data_url) {

        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            contents = jsonObj.getJSONArray("result");

            for (int i = 0; i < contents.length(); i++) {
                JSONObject c = contents.getJSONObject(i);
                String birth = c.getString("birth");

                if (patient_birth.equals(birth)) {

                    Boolean breakfast = c.getBoolean("breakfast");
                    Boolean lunch = c.getBoolean("lunch");
                    Boolean dinner = c.getBoolean("dinner");

                    if (data_url.contains("pill")) {
                        Log.i("_check","d");
                        setCheck_pill(new boolean[]{breakfast, lunch, dinner});
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void insertToDatabase(String birth, String table, final boolean[] mSelect) {

        class InsertData extends AsyncTask<String, Void, String> {

            ProgressDialog loading;

            @Override
            protected String doInBackground(String... params) {
                try {

                    String data = URLEncoder.encode("birth", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");
                    data += "&" + URLEncoder.encode("table", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");
                    data += "&" + URLEncoder.encode("breakfast", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(mSelect[0]), "UTF-8");
                    data += "&" + URLEncoder.encode("lunch", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(mSelect[1]), "UTF-8");
                    data += "&" + URLEncoder.encode("dinner", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(mSelect[2]), "UTF-8");

                    URL url = new URL(SETDATA_URL);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();

                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());

                }

            }
        }
        InsertData task = new InsertData();
        task.execute(birth, table, mSelect.toString());
    }


}