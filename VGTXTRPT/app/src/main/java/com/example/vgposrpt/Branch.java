package com.example.vgposrpt;

public class Branch {
    private  String Branch_Code;
    private  String Branch_Name;

    public String getBranch_Code() {
        return Branch_Code;
    }

    public void setBranch_Code(String branch_Code) {
        Branch_Code = branch_Code;
    }

    public String getBranch_Name() {
        return Branch_Name;
    }

    public void setBranch_Name(String branch_Name) {
        Branch_Name = branch_Name;
    }

    @Override
    public String toString() {
        return Branch_Name;
    }

    public Branch(String bCode,String bName){
        Branch_Name = bName;
        Branch_Code = bCode;
    }
    public Branch(){

    }
}
