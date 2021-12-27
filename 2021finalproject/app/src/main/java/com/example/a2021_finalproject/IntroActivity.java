package com.example.a2021_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        
        // 작업(Runnable)을 다른 스레드에 보내기 위한 핸들러 생성 및 호출
        Handler handler = new Handler();
        
        // 지정한 시간 동안 시작 화면을 보여주고 MainActivity로 화면 전환
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }}

