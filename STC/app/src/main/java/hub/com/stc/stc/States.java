package hub.com.stc.stc;

/**
 * Created by VINOTH on 3/11/2018.
 */

public class States {
    private String state_code;
    private String state_name;

    public void setState_code(String stCode){
        this.state_code = stCode;
    }

    public String getState_code(){
        return  this.state_code;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    @Override
    public String toString() {
        return state_name;
    }
}
