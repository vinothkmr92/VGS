package com.example.vgposrpt;

import androidx.annotation.NonNull;

public class Tables {
    public Integer TableID;
    public String TableName;
    public Integer BranchCode;
    @NonNull
    @Override
    public String toString() {
        return TableName;
    }
}
