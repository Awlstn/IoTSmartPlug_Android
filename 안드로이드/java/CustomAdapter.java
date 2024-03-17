package com.example.firebase;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Timer;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {


    private ArrayList<User> arrayList;
    private Context context;

    public CustomAdapter(ArrayList<User> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getProfile())
                .into(holder.iv_profile);

        holder.tv_C_AC.setText("현재 전력량 : " + String.valueOf(arrayList.get(position).getC_AC()) + "kwh");
        holder.tv_sum.setText("누적 전력량 : " + String.valueOf(arrayList.get(position).getSum()) + "kwh");
        holder.tv_E_bill.setText("예상 전기세 : " + String.valueOf(arrayList.get(position).getE_bill()) + "원");


        int Second = arrayList.get(position).getTime();
        //1일을 초로
        int Sec=24*60*60;

        // 시간을 초로
        int Si = 60*60;

        // 분을 초로
        int Bun = 60;

        // 입력한 초를 일수로
        int day= Second/Sec;

        // 입력한 초를 일수를 빼고 시수를
        Second%=Sec;
        int hours=Second/Si;

        // 입력한 초를 일수, 시수 빼고 분 구하기
        Second%=Si;
        int minutes = Second/Bun;

        // 입력한 초를 일수,시수, 분을 제외한 초
        Second%=Bun;
        holder.tv_Time.setText(day+"일"+ hours+"시"+ minutes+"분"+Second+"초");
        holder.on_off.setText(String.valueOf(arrayList.get(position).getOn_off()));

        String on_off = holder.on_off.getText().toString();
        System.out.println(on_off);
        String b = "10";
        String e = "20";

            if (b.equals(on_off)) {
                System.out.println("10이에요");
                Intent intent;//인텐트 선언
                intent = new Intent(context, MainActivity.class);
                //((MainActivity) context).startActivityForResult(intent, RESULT_CODE);//look_memo.class부분에 원하는 화면 연결
                intent.putExtra("on_off", on_off); //변수값 인텐트로 넘기기
                ((MainActivity)context).startActivity(intent);
                //context.startActivity(intent);
            }
            if (e.equals(on_off)){
                System.out.println("20이에요");
                Intent intent;//인텐트 선언
                intent = new Intent(context, MainActivity.class); //look_memo.class부분에 원하는 화면 연결
                intent.putExtra("on_off", on_off); //변수값 인텐트로 넘기기
                context.startActivity(intent);
        }

    }

    @Override
    public int getItemCount() {
        // 삼항 연산자 널이 아니면 왼쪽 널이면 0
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_profile;
        TextView tv_C_AC;
        TextView tv_sum;
        TextView tv_E_bill;
        TextView tv_Time;
        TextView on_off;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_profile = itemView.findViewById(R.id.iv_profile);
            this.tv_C_AC = itemView.findViewById(R.id.tv_C_AC);
            this.tv_sum = itemView.findViewById(R.id.tv_sum);
            this.tv_E_bill = itemView.findViewById(R.id.tv_E_bill);
            this.tv_Time = itemView.findViewById(R.id.tv_Time);
            this.on_off = itemView.findViewById(R.id.on_off);
        }
    }

}
