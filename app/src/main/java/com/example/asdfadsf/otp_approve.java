package com.example.asdfadsf;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class otp_approve extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Calendar calendar;
    private TextView selectedDateTime;
    private Button approveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_approve);

        mAuth = FirebaseAuth.getInstance();
        calendar = Calendar.getInstance();

        Button gobackButton = findViewById(R.id.gobackbutton);
        gobackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        EditText otpname = findViewById(R.id.edt_otpname);
        approveButton = findViewById(R.id.btn_approve);
        Button pickDateTimeButton = findViewById(R.id.pick_date_time_button);
        selectedDateTime = findViewById(R.id.selected_date_time);

        pickDateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otpName = otpname.getText().toString().trim();
                if (otpName.isEmpty() || selectedDateTime.getText().toString().equals("선택된 날짜와 시간: ")) {
                    Toast.makeText(otp_approve.this, "기간과 이름을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                long currentTimeMillis = System.currentTimeMillis();
                long selectedTimeMillis = calendar.getTimeInMillis();

                if (selectedTimeMillis <= currentTimeMillis) {
                    Toast.makeText(otp_approve.this, "이미 지난 시간입니다. OTP를 발급할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String otp = generateOTP();
                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();

                DatabaseReference otpRef = FirebaseDatabase.getInstance().getReference().child("otpList").child(userId);

                // 중복 확인 로직 추가
                otpRef.orderByChild("otp").equalTo(otp).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // 중복된 OTP가 존재하는 경우
                            Toast.makeText(otp_approve.this, "중복된 OTP가 존재합니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        } else {
                            // 중복된 OTP가 없는 경우 저장
                            saveOtpData(userId, otpRef, otp, otpName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(otp_approve.this, "OTP 중복 확인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        new DatePickerDialog(otp_approve.this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            new TimePickerDialog(otp_approve.this, (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                updateDateTimeText();
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateTimeText() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        selectedDateTime.setText("선택된 날짜와 시간: " + format.format(calendar.getTime()));
    }

    private String generateOTP() {
        SecureRandom secureRandom = new SecureRandom();
        int otp = secureRandom.nextInt(10000);
        return String.format("%04d", otp);
    }

    private void saveOtpData(String userId, DatabaseReference otpRef, String otp, String otpName) {
        DatabaseReference authRef = FirebaseDatabase.getInstance().getReference().child("authentic_list").child(userId);

        HashMap<String, Object> otpData = new HashMap<>();
        otpData.put("otp", otp);
        otpData.put("selectedTime", formatDateTime(calendar.getTime()));
        otpData.put("otpName", otpName);

        authRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean isAuthentic = dataSnapshot.getValue(Boolean.class);
                if (isAuthentic != null && isAuthentic) {
                    String timestamp = String.valueOf(System.currentTimeMillis());
                    DatabaseReference newOtpRef = otpRef.child(timestamp);
                    newOtpRef.setValue(otpData);

                    long selectedTimeMillis = calendar.getTimeInMillis() - System.currentTimeMillis();
                    deleteDataAfterDelay(userId, newOtpRef.getKey(), selectedTimeMillis);

                    Toast.makeText(otp_approve.this, "OTP가 생성되었습니다.\nOTP: " + otp, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(otp_approve.this, "사용자가 인증되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(otp_approve.this, "인증 여부 확인에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteDataAfterDelay(String userId, String otpKey, long selectedTimeMillis) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                deleteData(userId, otpKey);
            }
        }, selectedTimeMillis);
    }

    private void deleteData(String userId, String otpKey) {
        DatabaseReference otpRef = FirebaseDatabase.getInstance().getReference().child("otpList").child(userId).child(otpKey);
        otpRef.removeValue();
    }

    private void displayOtpData() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        DatabaseReference otpRef = FirebaseDatabase.getInstance().getReference().child("otpList").child(userId);

        otpRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String selectedTime = snapshot.child("selectedTime").getValue(String.class);
                    String otpName = snapshot.child("otpName").getValue(String.class);

                    TextView otpDataTextView = findViewById(R.id.selected_date_time);
                    otpDataTextView.setText("이름: " + otpName + "\n만료 시간: " + selectedTime);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private String formatDateTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return format.format(date);
    }
}
