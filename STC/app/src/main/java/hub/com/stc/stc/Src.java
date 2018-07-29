package hub.com.stc.stc;

/**
 * Created by VINOTH on 3/15/2018.
 */

public class Src {
    private String sc_code;
    private String sc_name;

    public String getSc_code() {
        return sc_code;
    }

    public void setSc_code(String sc_code) {
        this.sc_code = sc_code;
    }

    public String getSc_name() {
        return sc_name;
    }

    public void setSc_name(String sc_name) {
        this.sc_name = sc_name;
    }

    @Override
    public String toString(){
        return this.sc_name;
    }
}
