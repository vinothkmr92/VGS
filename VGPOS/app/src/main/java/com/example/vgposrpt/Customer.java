package com.example.vgposrpt;

import androidx.annotation.NonNull;

public class Customer {
    public int MemberID;
    public String MemberName;
    public String MobileNumber;
    @NonNull
    @Override
    public String toString() {
        return MemberName+"-"+MobileNumber;
    }
}
