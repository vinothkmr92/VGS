package com.example.gatepass.gatepass;

import java.util.Hashtable;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

/**
 * Created by VINOTH on 23-Feb-18.
 */

public class GatePassResponse implements KvmSerializable {

    public String vendorName;

    public String shopName;

    public String msgFromSAP;

    public String plant;

    public String interfaceMsg;

    public String interfaceFlg;
    public boolean isResponseOK ;
    public  String exception;

    public GatePassResponse(){}

    public GatePassResponse(String VENDORNAME,String SHOPNAME,String MSG_FRM_SAP,String PLANT,String INTERFACEMSG,String INTERFACEFLAG,String EXCEPTION,boolean IS_RESPONSEOK){
        vendorName = VENDORNAME;
        shopName = SHOPNAME;
        msgFromSAP = MSG_FRM_SAP;
        plant = PLANT;
        interfaceMsg = INTERFACEMSG;
        interfaceFlg = INTERFACEFLAG;
        exception = EXCEPTION;
        isResponseOK = IS_RESPONSEOK;
    }

    public  Object getProperty(int arg0){
        switch (arg0){
            case 0:
                return vendorName;
            case 1:
                return shopName;
            case 2:
                return  msgFromSAP;
            case  3:
                return  plant;
            case 4:
                return interfaceMsg;
            case 5:
                return interfaceFlg;
            case 6:
                return  isResponseOK;
            case 7:
                return exception;
        }
        return null;
    }

    public int getPropertyCount(){
        return 8;
    }

    public void getPropertyInfo(int index,Hashtable arg1,PropertyInfo info){
        switch (index){
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "vendorName";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name ="shopName";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "msgFromSAP";
                break;
            case  3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name="plant";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name ="interfaceMsg";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "interfaceFlg";
                break;
            case 6:
                info.type = PropertyInfo.BOOLEAN_CLASS;
                info.name="isResponseOK";
                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name="exception";
                break;
            default:break;
        }
    }

    public  void setProperty(int index,Object value){
        switch (index){
            case 0:
               vendorName = value.toString();
               break;
            case 1:
                shopName= value.toString();
                break;
            case 2:
                 msgFromSAP= value.toString();
                break;
            case  3:
                  plant= value.toString();
                break;
            case 4:
                 interfaceMsg= value.toString();
                break;
            case 5:
                 interfaceFlg= value.toString();
                break;
            case 6:
                isResponseOK= Boolean.parseBoolean(value.toString());
                break;
            case 7:
                 exception= value.toString();
                break;
            default:break;
        }
    }
}
