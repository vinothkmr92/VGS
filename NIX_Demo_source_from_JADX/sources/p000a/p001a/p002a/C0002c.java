package p000a.p001a.p002a;

import android.os.IBinder;
import android.os.Parcel;

/* renamed from: a.a.a.c */
final class C0002c implements C0000a {

    /* renamed from: a */
    private IBinder f0a;

    C0002c(IBinder iBinder) {
        this.f0a = iBinder;
    }

    /* renamed from: a */
    public final void mo1a(int i, String str) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.ICallback");
            obtain.writeInt(i);
            obtain.writeString(str);
            this.f0a.transact(3, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    /* renamed from: a */
    public final void mo2a(String str) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.ICallback");
            obtain.writeString(str);
            this.f0a.transact(2, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    /* renamed from: a */
    public final void mo3a(boolean z) {
        int i = 1;
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.ICallback");
            if (!z) {
                i = 0;
            }
            obtain.writeInt(i);
            this.f0a.transact(1, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    public final IBinder asBinder() {
        return this.f0a;
    }
}
