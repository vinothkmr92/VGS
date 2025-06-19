package com.example.vgposrpt;

import androidx.annotation.NonNull;

public class Counters {
    public String CounterID;
    public String CounterName;
    public Integer branchCode;
    @NonNull
    @Override
    public String toString() {
        return CounterName;
    }
}
