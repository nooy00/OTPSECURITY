package com.example.asdfadsf;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class mainpage extends AppCompatActivity {


    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);


        mAuth = FirebaseAuth.getInstance();


        // 환영 메시지 텍스트뷰 초기화
        TextView welcomeMessageTextView = findViewById(R.id.mainpagetitle);

        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();

        DatabaseReference userNameRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);


        // ValueEventListener를 사용하여 사용자 이름을 가져옵니다.  회원 이름을 db에서 참조해서 표현해줌
        userNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 사용자 이름 가져오기
                String userName = dataSnapshot.child("name").getValue(String.class);

                // 사용자 이름이 null이 아니면 환영 메시지 설정
                if (userName != null) {
                    welcomeMessageTextView.setText("안녕하세요, " + userName + " 님");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 오류 발생 시 처리
            }
        });

        Button otplistButton = findViewById(R.id.OTPlistbutton);
        otplistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainpage.this,OTPLIST.class);
                startActivity(intent);
            }
        });


        // 각 버튼에 대한 클릭 리스너 설정
        Button gotoOTPcontrolButton = findViewById(R.id.contorOTPbutton);
        gotoOTPcontrolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainpage.this,ControlOTP.class);
                startActivity(intent);
            }
        });

        Button gotoOTPapproveButton  = findViewById(R.id.gotoOTPapprove);
        gotoOTPapproveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainpage.this,otp_approve.class);
                startActivity(intent);
            }
        });


        Button gotoAuthenticButton = findViewById(R.id.gotoauthenticbtn);
        gotoAuthenticButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainpage.this,authenticpage.class);
                startActivity(intent);
            }
        });


        Button gotoSettingButton = findViewById(R.id.STBUTTON); // 설정 페이지로 이동
        gotoSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent (mainpage.this, settingpage.class);
                startActivity(intent);
            }
        });

        Button LogoutButton = findViewById(R.id.logoutbutton);// 방금 추가함
        LogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                Toast.makeText(mainpage.this,"로그아웃 되었습니다.",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mainpage.this,loginpage.class);
                startActivity(intent);
                finish();
            }
        });


        Button adminSettingButton = findViewById(R.id.admin_settingbutton);
        adminSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsTab(); // 관리자 탭에서 설정 탭으로 이동
            }
        });


        Button adminControlButton = findViewById(R.id.admin_controlerbutton);
        adminControlButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showAdminTab(); // 관리자 탭에서 관리자 탭을 다시 눌렀을 때 그대로 둠 / 오류 처리
            }
        });

        Button settingsControlButton = findViewById(R.id.setting_controlerbutton);
        settingsControlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdminTab(); // 설정 탭에서 관리자 탭으로 이동
            }
        });

        Button settingsSettingButton = findViewById(R.id.setting_settingbutton);
        settingsSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsTab(); // 설정 탭에서 설정 탭 다시 눌렀을 때 그대로 둠 / 오류 처리
            }
        });
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
    }


    // 관리자 탭을 보여주는 메서드
    private void showAdminTab() {
        findViewById(R.id.tab_admin).setVisibility(View.VISIBLE);
        findViewById(R.id.tab_settings).setVisibility(View.GONE);
    }


    // 설정 탭을 보여주는 메서드
    private void showSettingsTab() {
        findViewById(R.id.tab_admin).setVisibility(View.GONE);
        findViewById(R.id.tab_settings).setVisibility(View.VISIBLE);
    }
}