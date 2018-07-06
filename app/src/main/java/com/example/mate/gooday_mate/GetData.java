package com.example.mate.gooday_mate;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by MINA on 2018-03-01.
 */

public class GetData {
    private static final String TAG = "GetData";
    String DATA_URL = Config.URL + "show_patient.php";
    ArrayList<Item_Main> items = new ArrayList<>();
    JSONArray contents = null;
    String myJSON;

    public GetData() {
        GetDataJSON g = new GetDataJSON();
        g.execute(DATA_URL);
    }

    public void setItems(ArrayList<Item_Main> items) {
        this.items = items;
        Log.i(TAG, "setItems" + this.items);
    }

    public ArrayList<Item_Main> getItems() {
        if (items != null) {
            Log.i(TAG, "getItems1" + items);
            return items;
        } else {
            return null;
        }
    }

    public void showList() {
        try {

            JSONObject jsonObj = new JSONObject(myJSON);
            contents = jsonObj.getJSONArray("result");

            for (int i = 0; i < contents.length(); i++) {
                JSONObject c = contents.getJSONObject(i);
                String id = c.getString("id");
                String name = c.getString("name");
                String birth = c.getString("birth");
                String sex = c.getString("sex");
                String phone = c.getString("phone");
                String enterdate = c.getString("enterdate");
                String image = c.getString("image");
                String channel = c.getString("channel");
                String port = c.getString("port");

                items.add(new Item_Main(name, birth, sex, enterdate, phone, R.mipmap.mate_logo, channel, port));
            }
            setItems(items);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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

                return sb.toString().trim();

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            myJSON = result;
            showList();
        }
    }
}
