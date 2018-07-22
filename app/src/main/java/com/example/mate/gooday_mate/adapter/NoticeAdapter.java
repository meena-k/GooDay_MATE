package com.example.mate.gooday_mate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mate.gooday_mate.R;
import com.example.mate.gooday_mate.service.Item_Notice;

import java.util.ArrayList;

public class NoticeAdapter extends BaseAdapter {
    private ArrayList<Item_Notice> item_notices = new ArrayList<Item_Notice>();

    //Adapter에 사용되는 데이터의 개수 리턴
    @Override
    public int getCount() {
        return item_notices.size();
    }

    //position에 위치한 데이터를 화면에 출력하는데 사용될 View 리턴
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_cardview_notice, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = convertView.findViewById(R.id.image);
        TextView titleTextView = convertView.findViewById(R.id.title);
        TextView descTextView = convertView.findViewById(R.id.metadata);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        Item_Notice item_notice = item_notices.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageResource(item_notice.getIcon());
        titleTextView.setText(item_notice.getTitle());
        descTextView.setText(item_notice.getMetadata());

        return convertView;
    }

    //지정한 position에 있는 데이터와 관계된 아이템의 id를 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    //지정한 position에 있는 데이터 리턴
    @Override
    public Object getItem(int position) {
        return item_notices.get(position);
    }

    //아이템 데이터 추가를 위한 함수
    public void addItem(int id, int icon, String title, String metadata, String content) {
        item_notices.add(new Item_Notice(id, icon, title, metadata, content));
    }
}