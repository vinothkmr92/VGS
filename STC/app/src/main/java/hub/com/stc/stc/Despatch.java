package hub.com.stc.stc;

/**
 * Created by VINOTH on 3/12/2018.
 */

public class Despatch {
    private String AWB_NUMBER ;
    private String QUANTITY;
    private String WEIGHT;

    public String getAWB_NUMBER() {
        return AWB_NUMBER;
    }

    public void setAWB_NUMBER(String AWB_NUMBER) {
        this.AWB_NUMBER = AWB_NUMBER;
    }

    public String getQUANTITY() {
        return QUANTITY;
    }

    public void setQUANTITY(String QUANTITY) {
        this.QUANTITY = QUANTITY;
    }

    public String getWEIGHT() {
        return WEIGHT;
    }

    public void setWEIGHT(String WEIGHT) {
        this.WEIGHT = WEIGHT;
    }
}
