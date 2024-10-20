package com.example.asdfadsf;

public class OTPItem {
    private String name;
    private String expiry;
    private String otp;

    public OTPItem(String otpName, String selectedTime,String otp) {
        this.name = otpName;
        this.expiry = selectedTime;
        this.otp = otp;
    }

    public String getName() {
        return name;
    }

    public String getExpiry() {
        return expiry;
    }

    public String getNumber() {
        return otp;
    }

}