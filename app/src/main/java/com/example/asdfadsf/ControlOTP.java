package com.example.asdfadsf;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ControlOTP extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private OTPAdapter adapter;
    private DatabaseReference otpRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_otp);

        mAuth = FirebaseAuth.getInstance();

        Button gobackButton = findViewById(R.id.gobackbutton);
        gobackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ControlOTP.this, mainpage.class);
                startActivity(intent);
            }
        });

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            otpRef = FirebaseDatabase.getInstance().getReference().child("otpList").child(userId); // Databasereference

            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // OTP 정보를 담을 리스트를 초기화합니다.
            List<OTPItem> otpList = new ArrayList<>();

            // ValueEventListener를 추가하여 Firebase에서 데이터를 가져옵니다.
            otpRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // 가져온 데이터를 리스트에 추가합니다.
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String otp = snapshot.child("otp").getValue(String.class);
                        String otpName = snapshot.child("otpName").getValue(String.class);
                        String selectedTime = snapshot.child("selectedTime").getValue(String.class);
                        otpList.add(new OTPItem(otpName, selectedTime, otp));


                        Log.d("ControlOTP", "otp: " + otp + ", otpName: " + otpName + ", selectedTime: " + selectedTime);
                    }

                    // RecyclerView 어댑터를 생성하고 설정합니다.
                    adapter = new OTPAdapter(otpList);
                    recyclerView.setAdapter(adapter);
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // 오류 발생 시 처리합니다.
                }
            });
        }
    }
}