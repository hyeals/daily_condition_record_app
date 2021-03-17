package com.test.daily_condition_record;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.test.daily_condition_record.Room.AppDatabase;
import com.test.daily_condition_record.Room.User;

import java.net.URI;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    private ArrayList<User> userData = new ArrayList<>();


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
        itemViewHolder.onBind(userData.get(position), position);

        itemViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, PostActivity.class);

                intent.putExtra("already_exist", "already_exist"); // 이미 저장한 적 있으면,
                System.out.println("리사이클러뷰 아이템 클릭");

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() { // 현재 몇개의 데이터가 있는지 반환.
        return userData.size();
    }

    // User 객체(아이템)를 하나씩 추가시킨다.
    public void addItem(User user) { // addItem()은 리사이클러뷰에 뿌릴 때 사용되는 함수
        userData.add(user);
        notifyDataSetChanged(); // 담아주고나서 변경 사항을 알려주기 위해 사용.
    }

    public void addItems(ArrayList<User> users) { // addItems()는 리사이클러뷰에 뿌리기전에, 먼저 로컬 DB에 저장되어 있는 내용을 RecyclerAdapter.java에 만들어진 userData에 값을 담아 갱신해 주는 작업
        userData = users;
        notifyDataSetChanged(); // 담아주고나서 변경 사항을 알려주기 위해 사용.
    }


    // RecyclerView 의 ViewHolder 만든다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

//        private TextView Title;
        private TextView description;
        private ImageView Image;

//        private TextView key;
        private TextView Date_Weather;

        public View mView; // 온클릭 이벤트에 사용

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView; // 온클릭 이벤트에 사용

//            Title = itemView.findViewById(R.id.Title);
            description = itemView.findViewById(R.id.Description);
            Image = itemView.findViewById(R.id.Image);

//            key = itemView.findViewById(R.id.key);
            Date_Weather = itemView.findViewById(R.id.Date_Weather);

        }

        // 실제 데이터들을 1:1 대응하여 각각의 내부뷰에 바인딩시킨다.
        void onBind(User user, int position) {
//            String s = "" + (position + 1); // key값은 위에서 매개변수로 받아온 position 값을 이용, 1번 부터 번호를 시작하기 위해서 +1을 해주고 string형으로 바꿔서 textView에 넣어줌.
//            key.setText(s);
//            Title.setText(user.getTitle());
            Date_Weather.setText( user.getDate() +"("+user.getWeather()+")"); // Room DB로부터 date와 weather를 가져와 보여줌.
            description.setText(user.getDes()); // Room DB로부터 메모 내용을 가져와 보여줌.

            if(user.getImg() == null) { // 만약 사용자가 PostActivity.java에서 아무 이미지도 선택하지 않고 저장을 눌렀다면(이렇게 null처리 안해주면 오류 발생),
                Image.setImageResource(R.drawable.ic_android_black_24dp); // MainActivity.java의 리사이클러뷰 화면에 기본안드로이드이미지(검정)로 보여지게 변경.
            }
            else { // 만약 사용자가 PostActivity.java에서 이미지를 선택했다면,
                Image.setImageURI(Uri.parse(user.getImg())); // Room DB로부터 이미지(Uri(String) → Uri(Uri)로 변경)를 가져옴.
            }

            itemView.setOnLongClickListener(v -> { // 리사이클러뷰의 아이템을 길게 누를 시 메모 삭제. (삭제할껀지 확인하는 절차 추가 필요함)
                userData.remove(user);
                AppDatabase.getInstance(itemView.getContext()).userDao().delete(user);

                notifyDataSetChanged();
                return false;
            });

        }
    }


}
