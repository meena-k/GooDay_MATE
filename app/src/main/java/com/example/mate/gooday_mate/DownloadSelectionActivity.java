package com.example.mate.gooday_mate;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.mate.gooday_mate.service.Config;
import com.example.mate.gooday_mate.service.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * DownloadSelectionActivity displays a list of files in the bucket. Users can
 * <p>
 * select a file to download.
 */

public class DownloadSelectionActivity extends ListActivity {


    // The S3 client used for getting the list of objects in the bucket
    private AmazonS3Client s3;

    // An adapter to show the objects
    private SimpleAdapter simpleAdapter;
    private ArrayList<HashMap<String, Object>> transferRecordMaps;
    private Util util;
    private String folder_birth, folder_document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_selection);
        util = new Util();
        initData();
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the file list.
        new GetFileListTask().execute();
    }

    private void initData() {

        // Gets the default S3 client.
        s3 = util.getS3Client(DownloadSelectionActivity.this);
        transferRecordMaps = new ArrayList<HashMap<String, Object>>();

        Intent getIntent = this.getIntent();
        folder_birth = getIntent.getStringExtra("birth");
        folder_document = getIntent.getStringExtra("folder");
    }


    private void initUI() {
        simpleAdapter = new SimpleAdapter(this, transferRecordMaps, R.layout.bucket_item, new String[]{"key"}, new int[]{R.id.key});

        simpleAdapter.setViewBinder(new ViewBinder() {

            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                switch (view.getId()) {
                    case R.id.key:
                        TextView fileName = (TextView) view;
                        fileName.setText((String) data);
                        return true;
                }
                return false;
            }
        });
        setListAdapter(simpleAdapter);

        // When an item is selected, finish the activity and pass back the S3
        // key associated with the object selected
        getListView().setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                setResult(RESULT_OK, new Intent().putExtra("key", (String) transferRecordMaps.get(pos).get("key")));
                finish();
            }

        });

    }

    private class GetFileListTask extends AsyncTask<Void, Void, Void> {

        // The list of objects we find in the S3 bucket
        private List<S3ObjectSummary> s3ObjList;

        // A dialog to let the user know we are retrieving the files
        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(DownloadSelectionActivity.this,
                    getString(R.string.refreshing),
                    getString(R.string.please_wait));
        }

        @Override
        protected Void doInBackground(Void... inputs) {

            // Queries files in the bucket from S3.
            s3ObjList = s3.listObjects(Config.BUCKET_NAME, folder_birth + "/" + folder_document + "/").getObjectSummaries();
            transferRecordMaps.clear();

            for (S3ObjectSummary summary : s3ObjList) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("key", summary.getKey());

                transferRecordMaps.add(map);
            }
            // foler name 빼기
            transferRecordMaps.remove(0);
            return null;

        }

        @Override

        protected void onPostExecute(Void result) {
            dialog.dismiss();
            simpleAdapter.notifyDataSetChanged();
        }
    }

}