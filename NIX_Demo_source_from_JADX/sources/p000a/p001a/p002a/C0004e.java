package p000a.p001a.p002a;

import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

/* renamed from: a.a.a.e */
public abstract class C0004e extends Binder implements C0003d {
    /* renamed from: a */
    public static C0003d m28a(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface queryLocalInterface = iBinder.queryLocalInterface("woyou.aidlservice.jiuiv5.IWoyouService");
        return (queryLocalInterface == null || !(queryLocalInterface instanceof C0003d)) ? new C0005f(iBinder) : (C0003d) queryLocalInterface;
    }

    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
        C0006g gVar = null;
        switch (i) {
            case 1:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IWoyouService");
                byte[] createByteArray = parcel.createByteArray();
                long readLong = parcel.readLong();
                String readString = parcel.readString();
                IBinder readStrongBinder = parcel.readStrongBinder();
                if (readStrongBinder != null) {
                    IInterface queryLocalInterface = readStrongBinder.queryLocalInterface("woyou.aidlservice.jiuiv5.IYmodemSPI");
                    gVar = (queryLocalInterface == null || !(queryLocalInterface instanceof C0006g)) ? new C0008i(readStrongBinder) : (C0006g) queryLocalInterface;
                }
                mo16a(createByteArray, readLong, readString, gVar);
                parcel2.writeNoException();
                return true;
            case 2:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IWoyouService");
                int a = mo7a();
                parcel2.writeNoException();
                parcel2.writeInt(a);
                return true;
            case 3:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IWoyouService");
                String b = mo19b();
                parcel2.writeNoException();
                parcel2.writeString(b);
                return true;
            case 4:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IWoyouService");
                mo10a(C0001b.m3a(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            case 5:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IWoyouService");
                mo21b(C0001b.m3a(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            case 6:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IWoyouService");
                String c = mo23c();
                parcel2.writeNoException();
                parcel2.writeString(c);
                return true;
            case 7:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IWoyouService");
                String d = mo26d();
                parcel2.writeNoException();
                parcel2.writeString(d);
                return true;
            case 8:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IWoyouService");
                String e = mo27e();
                parcel2.writeNoException();
                parcel2.writeString(e);
                return true;
            case 9:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IWoyouService");
                mo24c(C0001b.m3a(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            case 10:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IWoyouService");
                mo9a(parcel.readInt(), C0001b.m3a(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            case 11:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IWoyouService");
                mo17a(parcel.createByteArray(), C0001b.m3a(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            case 12:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IWoyouService");
                mo20b(parcel.readInt(), C0001b.m3a(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            case 13:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IWoyouService");
                mo14a(parcel.readString(), C0001b.m3a(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            case 14:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IWoyouService");
                mo8a(parcel.readFloat(), C0001b.m3a(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            case 15:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IWoyouService");
                mo22b(parcel.readString(), C0001b.m3a(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            case 16:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IWoyouService");
                mo15a(parcel.readString(), parcel.readString(), parcel.readFloat(), C0001b.m3a(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            case 17:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IWoyouService");
                mo18a(parcel.createStringArray(), parcel.createIntArray(), parcel.createIntArray(), C0001b.m3a(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            case 18:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IWoyouService");
                mo11a(parcel.readInt() != 0 ? (Bitmap) Bitmap.CREATOR.createFromParcel(parcel) : null, C0001b.m3a(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            case 19:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IWoyouService");
                mo12a(parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt(), C0001b.m3a(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            case 20:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IWoyouService");
                mo13a(parcel.readString(), parcel.readInt(), parcel.readInt(), C0001b.m3a(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            case 21:
                parcel.enforceInterface("woyou.aidlservice.jiuiv5.IWoyouService");
                mo25c(parcel.readString(), C0001b.m3a(parcel.readStrongBinder()));
                parcel2.writeNoException();
                return true;
            case 1598968902:
                parcel2.writeString("woyou.aidlservice.jiuiv5.IWoyouService");
                return true;
            default:
                return super.onTransact(i, parcel, parcel2, i2);
        }
    }
}
