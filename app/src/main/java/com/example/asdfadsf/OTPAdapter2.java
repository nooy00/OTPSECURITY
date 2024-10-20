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

public class OTPAdapter2 extends RecyclerView.Adapter<OTPAdapter2.OTPViewHolder> {
    private List<OTPItem2> otpList;
    private FirebaseAuth mAuth;
    private DatabaseReference otpRef;
    private View emptyView;

    public OTPAdapter2(List<OTPItem2> otpList) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_otp2, parent, false);
        return new OTPViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OTPViewHolder holder, int position) {
        OTPItem2 otpItem = otpList.get(position);
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
       TextView textViewOtpstate;

        public OTPViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNumber = itemView.findViewById(R.id.textViewnumber);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewExpiry = itemView.findViewById(R.id.textViewExpiry);
            textViewOtpstate = itemView.findViewById(R.id.OTPstate);

        }

        public void bind(OTPItem2 otpItem) {
            textViewName.setText(otpItem.getName());
            textViewExpiry.setText(otpItem.getExpiry());
            textViewNumber.setText(otpItem.getNumber());
            textViewOtpstate.setText(otpItem.getState());
        }
    }
}