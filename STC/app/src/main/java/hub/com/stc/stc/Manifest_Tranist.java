package hub.com.stc.stc;

/**
 * Created by VINOTH on 3/17/2018.
 */

public class Manifest_Tranist {
    private String mt_Code;
    public String mt_Name;

    public String getMt_Code() {
        return mt_Code;
    }

    public void setMt_Code(String mt_Code) {
        this.mt_Code = mt_Code;
    }

    public String getMt_Name() {
        return mt_Name;
    }

    public void setMt_Name(String mt_Name) {
        this.mt_Name = mt_Name;
    }

    @Override
    public String toString(){
        return this.getMt_Code()+" - "+this.getMt_Name();
    }
}
