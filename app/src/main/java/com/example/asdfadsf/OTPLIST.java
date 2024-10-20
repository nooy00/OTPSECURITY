package com.example.asdfadsf;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class OTPLIST extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private OTPAdapter2 adapter2;
    private DatabaseReference otpRef;
    private DatabaseReference newOtpRef;
    private List<OTPItem2> otpList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otplist);

        mAuth = FirebaseAuth.getInstance();

        Button gobackButton = findViewById(R.id.gobackbutton);
        gobackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OTPLIST.this, mainpage.class);
                startActivity(intent);
            }
        });

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            otpRef = FirebaseDatabase.getInstance().getReference().child("otpList").child(userId); // DatabaseReference
            newOtpRef = FirebaseDatabase.getInstance().getReference().child("NewOtplist").child(userId);

            recyclerView = findViewById(R.id.recyclerView2);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // OTP 정보를 담을 리스트를 초기화합니다.
            otpList = new ArrayList<>();

            // RecyclerView 어댑터를 초기화하고 설정합니다.
            adapter2 = new OTPAdapter2(otpList);
            recyclerView.setAdapter(adapter2);

            // ChildEventListener를 추가하여 Firebase에서 데이터를 가져옵니다.
            otpRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                    // 중복 데이터 확인을 위해 추가
                    String newOtp = dataSnapshot.child("otp").getValue(String.class);
                    boolean isNewOtpExist = false;
                    for (OTPItem2 item : otpList) {
                        if (item.getNumber().equals(newOtp)) {
                            isNewOtpExist = true;
                            break;
                        }
                    }

                    if (!isNewOtpExist) {
                        // 중복되지 않은 데이터만 추가합니다.
                        String otp = dataSnapshot.child("otp").getValue(String.class);
                        String otpName = dataSnapshot.child("otpName").getValue(String.class);
                        String selectedTime = dataSnapshot.child("selectedTime").getValue(String.class);
                        String otpState = dataSnapshot.child("state").getValue(String.class);

                        // otpState가 null이면 '미사용'으로 설정
                        if (otpState == null) {
                            otpState = "미사용";
                        }

                        otpList.add(new OTPItem2(otpName, selectedTime, otp, otpState));
                        adapter2.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    String removedOtp = dataSnapshot.child("otp").getValue(String.class);
                    String otpName = dataSnapshot.child("otpName").getValue(String.class);
                    String selectedTime = dataSnapshot.child("selectedTime").getValue(String.class);

                    if (removedOtp != null) {
                        // 삭제된 데이터에 대한 처리
                        for (OTPItem2 item : otpList) {
                            if (item.getNumber().equals(removedOtp)) {
                                // 상태를 '삭제됨'으로 설정
                                item.setState("삭제됨");
                                adapter2.notifyDataSetChanged();

                                    DatabaseReference newItemRef = newOtpRef.child(removedOtp);
                                    newItemRef.child("otp").setValue(removedOtp);
                                    newItemRef.child("otpName").setValue(otpName);
                                    newItemRef.child("selectedTime").setValue(selectedTime);
                                    newItemRef.child("state").setValue("삭제됨");


                                break;
                            }
                        }
                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                    // 데이터가 이동되었을 때 처리 (필요에 따라 구현)
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // 오류 발생 시 처리합니다.
                }
            });

            // NewOtplist 데이터를 읽어오기 위한 ChildEventListener 추가
            newOtpRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                    // NewOtplist에서 데이터를 읽어와 리스트에 추가합니다.
                    String otp = dataSnapshot.child("otp").getValue(String.class);
                    String otpName = dataSnapshot.child("otpName").getValue(String.class);
                    String selectedTime = dataSnapshot.child("selectedTime").getValue(String.class);
                    String otpState = dataSnapshot.child("state").getValue(String.class);

                    otpList.add(new OTPItem2(otpName, selectedTime, otp, otpState));
                    adapter2.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }



                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                    // NewOtplist에서 데이터가 이동되었을 때 처리 (필요에 따라 구현)
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // 오류 발생 시 처리합니다.
                }
            });
        }
    }
}
