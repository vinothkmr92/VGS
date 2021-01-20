package p000a.p001a.p002a;

import android.os.IBinder;
import android.os.Parcel;

/* renamed from: a.a.a.i */
final class C0008i implements C0006g {

    /* renamed from: a */
    private IBinder f2a;

    C0008i(IBinder iBinder) {
        this.f2a = iBinder;
    }

    /* renamed from: a */
    public final void mo30a(float f) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IYmodemSPI");
            obtain.writeFloat(f);
            this.f2a.transact(1, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    /* renamed from: a */
    public final void mo31a(int i) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IYmodemSPI");
            obtain.writeInt(i);
            this.f2a.transact(2, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    /* renamed from: a */
    public final void mo32a(boolean z, String str) {
        int i = 0;
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IYmodemSPI");
            if (z) {
                i = 1;
            }
            obtain.writeInt(i);
            obtain.writeString(str);
            this.f2a.transact(3, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    public final IBinder asBinder() {
        return this.f2a;
    }
}
