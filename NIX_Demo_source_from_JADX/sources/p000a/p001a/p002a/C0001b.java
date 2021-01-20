package p000a.p001a.p002a;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

/* renamed from: a.a.a.b */
public abstract class C0001b extends Binder implements C0000a {
    public C0001b() {
        attachInterface(this, "woyou.aidlservice.jiuiv5.ICallback");
    }

    /* renamed from: a */
    public static C0000a m3a(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("woyou.aidlservice.jiuiv5.ICallback");
        return (queryLocalInterface == null || !(queryLocalInterface instanceof C0000a)) ? new C0002c(iBinder) : (C0000a) queryLocalInterface;
    }

    public IBinder asBinder() {
        return this;
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
        switch (i) {
            case 1:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.ICallback");
                mo3a(parcel.readInt() != 0);
                parcel2.writeNoException();
                return true;
            case 2:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.ICallback");
                mo2a(parcel.readString());
                parcel2.writeNoException();
                return true;
            case 3:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.ICallback");
                mo1a(parcel.readInt(), parcel.readString());
                parcel2.writeNoException();
                return true;
            case 1598968902:
                parcel2.writeString("woyou.aidlservice.jiuiv5.ICallback");
                return true;
            default:
                return super.onTransact(i, parcel, parcel2, i2);
        }
    }
}
