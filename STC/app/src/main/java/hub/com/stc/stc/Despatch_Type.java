package hub.com.stc.stc;

/**
 * Created by VINOTH on 3/17/2018.
 */

public class Despatch_Type {
    private String type_Code;
    private String type_Name;

    public String getType_Code() {
        return type_Code;
    }

    public void setType_Code(String type_Code) {
        this.type_Code = type_Code;
    }

    public String getType_Name() {
        return type_Name;
    }

    public void setType_Name(String type_Name) {
        this.type_Name = type_Name;
    }

    @Override
    public String toString(){
        return this.getType_Code()+" - "+ this.getType_Name();
    }
}
