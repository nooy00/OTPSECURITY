package com.example.asdfadsf;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class OTPAdapter extends RecyclerView.Adapter<OTPAdapter.OTPViewHolder> {
    private List<OTPItem> otpList;
    private FirebaseAuth mAuth;
    private DatabaseReference otpRef;
    private View emptyView;

    public OTPAdapter(List<OTPItem> otpList) {
        this.otpList = otpList;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user != null ? user.getUid() : null;
        if (userId != null) {
            otpRef = FirebaseDatabase.getInstance().getReference().child("otpList").child(userId);
        }
    }

    @NonNull
    @Override
    public OTPViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_otp, parent, false);
        return new OTPViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OTPViewHolder holder, int position) {
        OTPItem otpItem = otpList.get(position);
        holder.bind(otpItem);
    }

    @Override
    public int getItemCount() {
        return otpList.size();
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    public class OTPViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewExpiry;
        TextView textViewNumber;
        Button buttonCancel;

        public OTPViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNumber = itemView.findViewById(R.id.textViewnumber);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewExpiry = itemView.findViewById(R.id.textViewExpiry);
            buttonCancel = itemView.findViewById(R.id.buttonCancel);

            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        OTPItem otpItem = otpList.get(position);
                        String otp = otpItem.getNumber();
                        if (otpRef != null) {
                            otpRef.orderByChild("otp").equalTo(otp).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        snapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // 오류 발생 시 처리
                                }
                            });
                        }

                        otpList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, otpList.size());

                        Toast.makeText(v.getContext(), "OTP가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        public void bind(OTPItem otpItem) {
            textViewName.setText(otpItem.getName());
            textViewExpiry.setText(otpItem.getExpiry());
            textViewNumber.setText(otpItem.getNumber());
        }
    }
}