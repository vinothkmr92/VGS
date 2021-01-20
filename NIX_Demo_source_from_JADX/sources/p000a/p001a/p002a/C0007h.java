package p000a.p001a.p002a;

import android.os.Binder;
import android.os.Parcel;

/* renamed from: a.a.a.h */
public abstract class C0007h extends Binder implements C0006g {
    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
        switch (i) {
            case 1:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IYmodemSPI");
                mo30a(parcel.readFloat());
                parcel2.writeNoException();
                return true;
            case 2:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IYmodemSPI");
                mo31a(parcel.readInt());
                parcel2.writeNoException();
                return true;
            case 3:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IYmodemSPI");
                mo32a(parcel.readInt() != 0, parcel.readString());
                parcel2.writeNoException();
                return true;
            case 1598968902:
                parcel2.writeString("woyou.aidlservice.jiuiv5.IYmodemSPI");
                return true;
            default:
                return super.onTransact(i, parcel, parcel2, i2);
        }
    }
}
