package com.test.daily_condition_record;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    ArrayList<Data> dataList = new ArrayList<Data>();


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // item.xml 을 parent ViewGroup 위에 Inflate 시켜 새로운 View를 하나 만든다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);

        // 그리고, 이 view를 바탕으로 ItemViewHolder 객체 생성
        return new ItemViewHolder(view);
    }

    // ViewHolder에 각각의 항목들을 바인딩시킴.
    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ItemViewHolder itemViewHolder, int position) {
        itemViewHolder.onBind(dataList.get(position));

        itemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, PostActivity.class);

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // Data 객체(아이템) 을 하나씩 추가시킨다.
    public void addItem(Data data) {
        dataList.add(data);
    }


    // RecyclerView 의 ViewHolder 만든다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView Title;
        private TextView Today_Weather;
        private TextView Contents;
        private ImageView Image;

        public View mView; // 온클릭 이벤트에 사용

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView; // 온클릭 이벤트에 사용

            Title = itemView.findViewById(R.id.Title);
            Today_Weather = itemView.findViewById(R.id.Today_Weather);
            Contents = itemView.findViewById(R.id.Contents);
            Image = itemView.findViewById(R.id.Image);

        }

        // 실제 데이터들을 1:1 대응하여 각각의 내부뷰에 바인딩시킨다.
        void onBind(Data data) {
            Title.setText(data.getTitle());
            Today_Weather.setText(data.getToday_Weather());
            Contents.setText(data.getContents());
            //Image.setImageResource(data.getResId());

        }
    }


}
