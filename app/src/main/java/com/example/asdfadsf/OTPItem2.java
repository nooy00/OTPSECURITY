package com.example.asdfadsf;

public class OTPItem2 {
    private String name;
    private String expiry;
    private String otp;
    private String state;

    public OTPItem2(String otpName, String selectedTime, String otp, String otpState) {
        this.name = otpName;
        this.expiry = selectedTime;
        this.otp = otp;
        this.state = otpState;
    }

    // Getter 메소드
    public String getState() {
        return state;
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

    // Setter 메소드
    public void setState(String state) {
        this.state = state;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public void setNumber(String otp) {
        this.otp = otp;
    }
}
