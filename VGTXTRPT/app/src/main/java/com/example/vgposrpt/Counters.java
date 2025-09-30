package com.example.vgposrpt;

import androidx.annotation.NonNull;

public class Counters {
    public String CounterID;
    public String CounterName;
    public Integer branchCode;
    public Counters(String bCode,String bName){
        CounterID = bCode;
        CounterName = bName;
    }
    public Counters(){
    }
    @NonNull
    @Override
    public String toString() {
        return CounterName;
    }
}
