package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.os.AsyncTask;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    final String[] words = new String[] {"1분 후", "5분 후", "10분 후", "30분 후", "1시간 후", "2시간 후"};

    Switch switch1;
    Switch switch2;
    Switch switch3;
    TextView read_textView;
    TextView dialog_tv;

    LinearLayout timeCountSettingLV, timeCountLV;
    TextView hourET, minuteET, secondET;
    int hour, minute, second;

    private Intent intent; //인텐트 선언
    private  static String on_off;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<User> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private Socket client;
    private OutputStream dataOutput;
    private DataInputStream dataInput;
    //private InputStream dataInput;
    private static String SERVER_IP = "192.168.0.11";
    //private static String CONNECT_MSG = "connect";
    private static String STOP_MSG = "stop";
    private static String sendmsg;


    private static int BUF_SIZE = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        switch3 = findViewById(R.id.switch3);
        read_textView = findViewById(R.id.read_textView);
        dialog_tv = findViewById(R.id.dialog_tv);
        timeCountSettingLV = (LinearLayout)findViewById(R.id.timeCountSettingLV);

        hourET = (TextView)findViewById(R.id.hourET);
        minuteET = (TextView)findViewById(R.id.minuteET);
        secondET = (TextView)findViewById(R.id.secondET);

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sendmsg = "1";
                    Connect connect = new Connect();
                    connect.execute(sendmsg);
                }else{
                    sendmsg = "2";
                    Connect connect = new Connect();
                    connect.execute(sendmsg);
                }
            }
        });


        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //switch1.setChecked(true);
                    sendmsg = "3";
                    Connect connect = new Connect();
                    connect.execute(sendmsg);
                }else{
                    sendmsg = "4";
                    Connect connect = new Connect();
                    connect.execute(sendmsg);
                    //System.out.println("off");
                }
            }
        });

        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sendmsg = "5";
                    Connect connect = new Connect();
                    connect.execute(sendmsg);
                }else{
                    sendmsg = "6";
                    Connect connect = new Connect();
                    connect.execute(sendmsg);
                    //System.out.println("off");
                }
            }
        });
        intent = getIntent();// 인텐트 받아오기
        on_off = intent.getStringExtra("on_off");
        System.out.println("넘어왓어요:"+on_off);
        String d = "10";
        String f = "20";

        if (d.equals(on_off)) {
            System.out.println("10들어왓어요");
            switch1.setChecked(true);
        }
        if (f.equals(on_off)) {
            System.out.println("20들어왓어요");
            switch1.setChecked(false);
        }


        recyclerView = findViewById(R.id.recyclerView); // 아디 연결
        recyclerView.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); // User 객체를 담을 어레이 리스트 (어댑터쪽으로)

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동

        databaseReference = database.getReference("IoT"); // DB 테이블 연결
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                arrayList.clear(); // 기존 배열리스트가 존재하지않게 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // 반복문으로 데이터 List를 추출해냄
                    User user = snapshot.getValue(User.class); // 만들어뒀던 User 객체에 데이터를 담는다.
                    arrayList.add(user); // 담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
                }
                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 디비를 가져오던중 에러 발생 시
                Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        adapter = new CustomAdapter(arrayList, this);
        recyclerView.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결

        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

    }
    int num = 0;
    public void RadioClick(View view) {
        new AlertDialog.Builder(this).setTitle("자동 정지").setSingleChoiceItems(words, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                num = 1;
                Toast.makeText(MainActivity.this, "words : " + words[which], Toast.LENGTH_SHORT).show();
                if(which <= 3) {
                    int idx = words[which].indexOf("분");
                    dialog_tv.setText("남은 시작");
                    minuteET.setText(words[which].substring(0, idx));
                    minute = Integer.parseInt(words[which].substring(0, idx));
                    second = Integer.parseInt("0");
                    final Handler handler = new Handler(Looper.getMainLooper());
                    new Handler().postDelayed(new Runnable()  {
                        @Override
                        public void run() {
                            // 반복실행할 구문

                            // 0초 이상이면
                                if (second != 0) {
                                    //1초씩 감소
                                    second--;

                                    // 0분 이상이면
                                } else if (minute != 0) {
                                    // 1분 = 60초
                                    second = 60;
                                    second--;
                                    minute--;

                                // 0시간 이상이면
                                } else if(hour != 0) {
                                // 1시간 = 60분
                                    second = 60;
                                    minute = 60;
                                    second--;
                                    minute--;
                                    hour--;
                                }

                                //시, 분, 초가 10이하(한자리수) 라면
                                // 숫자 앞에 0을 붙인다 ( 8 -> 08 )
                                if (second <= 9) {
                                    secondET.setText("0" + second);
                                } else {
                                    secondET.setText(Integer.toString(second));
                                }

                                if (minute <= 9) {
                                    minuteET.setText("0" + minute);
                                } else {
                                    minuteET.setText(Integer.toString(minute));
                                }

                                if (hour <= 9) {
                                    hourET.setText("0" + hour);
                                } else {
                                    hourET.setText(Integer.toString(hour));
                                }

                                // 시분초가 다 0이라면 toast를 띄우고 타이머를 종료한다..
                                if (num == 1 && hour == 0 && minute == 0 && second == 0) {
                                    switch1.setChecked(false);
                                    dialog_tv.setText("사용 안 함");
                                    num = 0;
                                }
                            handler.postDelayed(this,1200);
                            }
                        }, 2000);
                }else {
                    int idx = words[which].indexOf("시");
                    dialog_tv.setText("남은 시간");
                    hourET.setText(words[which].substring(0, idx));

                    hour = Integer.parseInt(words[which].substring(0, idx));
                    minute = Integer.parseInt("0");
                    second = Integer.parseInt("0");
                    final Handler handler = new Handler(Looper.getMainLooper());
                    new Handler().postDelayed(new Runnable()  {
                        @Override
                        public void run() {
                            // 반복실행할 구문

                            // 0초 이상이면
                            if (second != 0) {
                                //1초씩 감소
                                second--;

                                // 0분 이상이면
                            } else if (minute != 0) {
                                // 1분 = 60초
                                second = 60;
                                second--;
                                minute--;

                                // 0시간 이상이면
                            } else if(hour != 0) {
                                // 1시간 = 60분
                                second = 60;
                                minute = 60;
                                second--;
                                minute--;
                                hour--;
                            }

                            //시, 분, 초가 10이하(한자리수) 라면
                            // 숫자 앞에 0을 붙인다 ( 8 -> 08 )
                            if (second <= 9) {
                                secondET.setText("0" + second);
                            } else {
                                secondET.setText(Integer.toString(second));
                            }

                            if (minute <= 9) {
                                minuteET.setText("0" + minute);
                            } else {
                                minuteET.setText(Integer.toString(minute));
                            }

                            if (hour <= 9) {
                                hourET.setText("0" + hour);
                            } else {
                                hourET.setText(Integer.toString(hour));
                            }

                            // 시분초가 다 0이라면 toast를 띄우고 타이머를 종료한다..
                            if (hour == 0 && minute == 0 && second == 0) {
                                switch1.setChecked(false);
                                dialog_tv.setText("사용 안 함");
                            }
                            handler.postDelayed(this,1200);
                        }
                    }, 2000);
                }
            }

        }).setNeutralButton("closed",null).setPositiveButton("OK",null).setNegativeButton("cancel", null).show();
    }

    private class Connect extends AsyncTask< String , String,Void > {
        private String input_message;



        @Override
        protected Void doInBackground(String... strings) {
            try {
                client = new Socket(SERVER_IP, 10000);
                dataOutput = new DataOutputStream(client.getOutputStream());
                dataInput = new DataInputStream(client.getInputStream());
                System.out.println(sendmsg);

                if (sendmsg == "1"){
                    String msg = sendmsg;
                    byte[] byteArr = null;
                    byteArr = msg.getBytes("UTF-8");
                    dataOutput.write(byteArr);
                }
                if (sendmsg == "2"){
                    String msg = sendmsg;
                    byte[] byteArr = null;
                    byteArr = msg.getBytes("UTF-8");
                    dataOutput.write(byteArr);
                }
                if (sendmsg == "3"){
                    String msg = sendmsg;
                    byte[] byteArr = null;
                    byteArr = msg.getBytes("UTF-8");
                    dataOutput.write(byteArr);
                }
                if (sendmsg == "4"){
                    String msg = sendmsg;
                    byte[] byteArr = null;
                    byteArr = msg.getBytes("UTF-8");
                    dataOutput.write(byteArr);
                }
                if (sendmsg == "5"){
                    String msg = sendmsg;
                    byte[] byteArr = null;
                    byteArr = msg.getBytes("UTF-8");
                    dataOutput.write(byteArr);
                }
                if (sendmsg == "6"){
                    String msg = sendmsg;
                    byte[] byteArr = null;
                    byteArr = msg.getBytes("UTF-8");
                    dataOutput.write(byteArr);
                }

            } catch (UnknownHostException e) {
                String str = e.getMessage().toString();
                Log.w("discnt", str + " 1");
            } catch (IOException e) {
                String str = e.getMessage().toString();
                Log.w("discnt", str + " 2");
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... params){

        }
    }
}