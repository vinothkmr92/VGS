package hub.com.stc.stc;

/**
 * Created by VINOTH on 3/12/2018.
 */

public class Hubs {
    private String hub_code;
    private String hub_name;
    private String state_code;

    public String getHub_code() {
        return hub_code;
    }

    public void setHub_code(String hub_code) {
        this.hub_code = hub_code;
    }

    public String getHub_name() {
        return hub_name;
    }

    public String getState_code() {
        return state_code;
    }

    public void setState_code(String state_code) {
        this.state_code = state_code;
    }

    public void setHub_name(String hub_name) {
        this.hub_name = hub_name;
    }
    @Override
    public String toString() {
        return hub_name;
    }
}
