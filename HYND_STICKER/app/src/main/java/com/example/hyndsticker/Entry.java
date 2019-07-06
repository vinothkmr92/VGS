package com.example.hyndsticker;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Entry {

    private Integer ENTRY_ID;
    private String ENTRY_DATE;
    private String SHOP;
    private String PART_NO;
    private String PART_NAME;
    private Integer QTY;
    private String REASON_CD;
    private String REASON;
    private String DEPART_CD;
    private String APPROVED_BY;
    private String ENTERED_BY;
    private Date EntDate;

    public Date getEntDateDate() {
        try{
            Date ds = new SimpleDateFormat("dd/MM/yyyy hh:mm aa").parse(ENTRY_DATE);
            return ds;
        }
        catch(Exception ex){
            return  new Date();
        }
    }

    public void setEntDate(Date entDate) {
        EntDate = entDate;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
        ENTRY_DATE = simpleDateFormat.format(entDate);
    }

    public Integer getENTRY_ID() {
        return ENTRY_ID;
    }

    public void setENTRY_ID(Integer ENTRY_ID) {
        this.ENTRY_ID = ENTRY_ID;
    }

    public String getENTRY_DATE() {
        return ENTRY_DATE;
    }

    public void setENTRY_DATE(String ENTRY_DATE) {
        this.ENTRY_DATE = ENTRY_DATE;
    }

    public String getSHOP() {
        return SHOP;
    }

    public void setSHOP(String SHOP) {
        this.SHOP = SHOP;
    }

    public String getPART_NO() {
        return PART_NO;
    }

    public void setPART_NO(String PART_NO) {
        this.PART_NO = PART_NO;
    }

    public String getPART_NAME() {
        return PART_NAME;
    }

    public void setPART_NAME(String PART_NAME) {
        this.PART_NAME = PART_NAME;
    }

    public Integer getQTY() {
        return QTY;
    }

    public void setQTY(Integer QTY) {
        this.QTY = QTY;
    }

    public String getREASON_CD() {
        return REASON_CD;
    }

    public void setREASON_CD(String REASON_CD) {
        this.REASON_CD = REASON_CD;
    }

    public String getREASON() {
        return REASON;
    }

    public void setREASON(String REASON) {
        this.REASON = REASON;
    }

    public String getDEPART_CD() {
        return DEPART_CD;
    }

    public void setDEPART_CD(String DEPART_CD) {
        this.DEPART_CD = DEPART_CD;
    }

    public String getAPPROVED_BY() {
        return APPROVED_BY;
    }

    public void setAPPROVED_BY(String APPROVED_BY) {
        this.APPROVED_BY = APPROVED_BY;
    }

    public String getENTERED_BY() {
        return ENTERED_BY;
    }

    public void setENTERED_BY(String ENTERED_BY) {
        this.ENTERED_BY = ENTERED_BY;
    }
}
