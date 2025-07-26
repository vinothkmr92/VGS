package com.example.vgposrpt;

public class Branch {
    private  int Branch_Code;
    private  String Branch_Name;

    public int getBranch_Code() {
        return Branch_Code;
    }

    public void setBranch_Code(int branch_Code) {
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

    public Branch(int bCode,String bName){
        Branch_Name = bName;
        Branch_Code = bCode;
    }
    public Branch(){

    }
}
