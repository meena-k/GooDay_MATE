package com.example.mate.gooday_mate.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mate.gooday_mate.R;
import com.example.mate.gooday_mate.service.Item_Main;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Item_Main> itemList;
    protected ItemListener mListener;

    public RecyclerAdapter(Context context, ArrayList<Item_Main> itemList, ItemListener mListener) {
        this.context = context;
        this.itemList = itemList;
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview_main, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(itemList.get(position));
        /*final Item_Main content = itemList.get(position);
        holder.patient_img.setImageResource(content.getImg());
        holder.user_name.setText(content.getName());*/
    }


    public interface ItemListener {
        void onItemClick(Item_Main item);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView patient_img;
        private TextView user_name;
        Item_Main item;

        public ViewHolder(View view) {
            super(view);

            patient_img = view.findViewById(R.id.img);
            user_name = view.findViewById(R.id.name);

            view.setOnClickListener(this);

        }

        public void setData(Item_Main item) {
            this.item = item;
            if (!item.getImg().contains(item.getBirth())) {
                patient_img.setImageResource(R.mipmap.ic_patient);
            } else {
                //    patient_img.setImageBitmap(BitmapFactory.decodeFile("/storage/emulated/0/1972.03.01/image.jpg"));
                patient_img.setImageBitmap(BitmapFactory.decodeFile(item.getImg()));
            }
            user_name.setText(item.getName());
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClick(item);
            }
        }
    }

}