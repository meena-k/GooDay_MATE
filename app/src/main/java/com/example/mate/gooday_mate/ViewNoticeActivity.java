package com.example.mate.gooday_mate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mate.gooday_mate.adapter.NoticeAdapter;

public class ViewNoticeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notice);
        initViews();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setNavigationIcon(R.mipmap.mate_logo);
        setSupportActionBar(toolbar);

        NoticeAdapter adapter = new NoticeAdapter();

        Intent getIntent = getIntent();
        Bundle noticeData = getIntent.getBundleExtra("noticeData");
        noticeData.getInt("id");


        ImageView image = findViewById(R.id.content_img);
        TextView title = findViewById(R.id.title);
        TextView metadata = findViewById(R.id.metadata);
        TextView content_txt = findViewById(R.id.content_txt);

        findViewById(R.id.image).setVisibility(View.GONE);
        title.setText(noticeData.getString("title"));
        metadata.setText(noticeData.getString("metadata"));
        image.setImageResource(noticeData.getInt("img"));
        content_txt.setText(noticeData.getString("content"));
    }
}
