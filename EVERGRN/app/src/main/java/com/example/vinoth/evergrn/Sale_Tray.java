package com.example.vinoth.evergrn;

public class Sale_Tray {
    private int Customer_ID;
    private int Tray_ID;
    private int Packing_ID;
    private String weigth;

    public int getCustomer_ID() {
        return Customer_ID;
    }

    public void setCustomer_ID(int customer_ID) {
        Customer_ID = customer_ID;
    }

    public int getTray_ID() {
        return Tray_ID;
    }

    public void setTray_ID(int tray_ID) {
        Tray_ID = tray_ID;
    }

    public int getPacking_ID() {
        return Packing_ID;
    }

    public void setPacking_ID(int packing_ID) {
        Packing_ID = packing_ID;
    }

    public String getWeigth() {
        return weigth;
    }

    public void setWeigth(String weigth) {
        this.weigth = weigth;
    }
}
