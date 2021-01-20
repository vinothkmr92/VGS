package p000a.p001a.p002a;

import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.Parcel;

/* renamed from: a.a.a.f */
final class C0005f implements C0003d {

    /* renamed from: a */
    private IBinder f1a;

    C0005f(IBinder iBinder) {
        this.f1a = iBinder;
    }

    /* renamed from: a */
    public final int mo7a() {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IWoyouService");
            this.f1a.transact(2, obtain, obtain2, 0);
            obtain2.readException();
            return obtain2.readInt();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    /* renamed from: a */
    public final void mo8a(float f, C0000a aVar) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IWoyouService");
            obtain.writeFloat(f);
            obtain.writeStrongBinder(aVar != null ? aVar.asBinder() : null);
            this.f1a.transact(14, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    /* renamed from: a */
    public final void mo9a(int i, C0000a aVar) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IWoyouService");
            obtain.writeInt(i);
            obtain.writeStrongBinder(aVar != null ? aVar.asBinder() : null);
            this.f1a.transact(10, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    /* renamed from: a */
    public final void mo10a(C0000a aVar) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IWoyouService");
            obtain.writeStrongBinder(aVar != null ? aVar.asBinder() : null);
            this.f1a.transact(4, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    /* renamed from: a */
    public final void mo11a(Bitmap bitmap, C0000a aVar) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IWoyouService");
            if (bitmap != null) {
                obtain.writeInt(1);
                bitmap.writeToParcel(obtain, 0);
            } else {
                obtain.writeInt(0);
            }
            obtain.writeStrongBinder(aVar != null ? aVar.asBinder() : null);
            this.f1a.transact(18, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    /* renamed from: a */
    public final void mo12a(String str, int i, int i2, int i3, int i4, C0000a aVar) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IWoyouService");
            obtain.writeString(str);
            obtain.writeInt(i);
            obtain.writeInt(i2);
            obtain.writeInt(i3);
            obtain.writeInt(i4);
            obtain.writeStrongBinder(aVar != null ? aVar.asBinder() : null);
            this.f1a.transact(19, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    /* renamed from: a */
    public final void mo13a(String str, int i, int i2, C0000a aVar) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IWoyouService");
            obtain.writeString(str);
            obtain.writeInt(i);
            obtain.writeInt(i2);
            obtain.writeStrongBinder(aVar != null ? aVar.asBinder() : null);
            this.f1a.transact(20, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    /* renamed from: a */
    public final void mo14a(String str, C0000a aVar) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IWoyouService");
            obtain.writeString(str);
            obtain.writeStrongBinder(aVar != null ? aVar.asBinder() : null);
            this.f1a.transact(13, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    /* renamed from: a */
    public final void mo15a(String str, String str2, float f, C0000a aVar) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IWoyouService");
            obtain.writeString(str);
            obtain.writeString(str2);
            obtain.writeFloat(f);
            obtain.writeStrongBinder(aVar != null ? aVar.asBinder() : null);
            this.f1a.transact(16, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    /* renamed from: a */
    public final void mo16a(byte[] bArr, long j, String str, C0006g gVar) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IWoyouService");
            obtain.writeByteArray(bArr);
            obtain.writeLong(j);
            obtain.writeString(str);
            obtain.writeStrongBinder(gVar != null ? gVar.asBinder() : null);
            this.f1a.transact(1, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    /* renamed from: a */
    public final void mo17a(byte[] bArr, C0000a aVar) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IWoyouService");
            obtain.writeByteArray(bArr);
            obtain.writeStrongBinder(aVar != null ? aVar.asBinder() : null);
            this.f1a.transact(11, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    /* renamed from: a */
    public final void mo18a(String[] strArr, int[] iArr, int[] iArr2, C0000a aVar) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IWoyouService");
            obtain.writeStringArray(strArr);
            obtain.writeIntArray(iArr);
            obtain.writeIntArray(iArr2);
            obtain.writeStrongBinder(aVar != null ? aVar.asBinder() : null);
            this.f1a.transact(17, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    public final IBinder asBinder() {
        return this.f1a;
    }

    /* renamed from: b */
    public final String mo19b() {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IWoyouService");
            this.f1a.transact(3, obtain, obtain2, 0);
            obtain2.readException();
            return obtain2.readString();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    /* renamed from: b */
    public final void mo20b(int i, C0000a aVar) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IWoyouService");
            obtain.writeInt(i);
            obtain.writeStrongBinder(aVar != null ? aVar.asBinder() : null);
            this.f1a.transact(12, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    /* renamed from: b */
    public final void mo21b(C0000a aVar) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IWoyouService");
            obtain.writeStrongBinder(aVar != null ? aVar.asBinder() : null);
            this.f1a.transact(5, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    /* renamed from: b */
    public final void mo22b(String str, C0000a aVar) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IWoyouService");
            obtain.writeString(str);
            obtain.writeStrongBinder(aVar != null ? aVar.asBinder() : null);
            this.f1a.transact(15, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    /* renamed from: c */
    public final String mo23c() {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IWoyouService");
            this.f1a.transact(6, obtain, obtain2, 0);
            obtain2.readException();
            return obtain2.readString();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    /* renamed from: c */
    public final void mo24c(C0000a aVar) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IWoyouService");
            obtain.writeStrongBinder(aVar != null ? aVar.asBinder() : null);
            this.f1a.transact(9, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    /* renamed from: c */
    public final void mo25c(String str, C0000a aVar) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IWoyouService");
            obtain.writeString(str);
            obtain.writeStrongBinder(aVar != null ? aVar.asBinder() : null);
            this.f1a.transact(21, obtain, obtain2, 0);
            obtain2.readException();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    /* renamed from: d */
    public final String mo26d() {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IWoyouService");
            this.f1a.transact(7, obtain, obtain2, 0);
            obtain2.readException();
            return obtain2.readString();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    /* renamed from: e */
    public final String mo27e() {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("woyou.aidlservice.jiuiv5.IWoyouService");
            this.f1a.transact(8, obtain, obtain2, 0);
            obtain2.readException();
            return obtain2.readString();
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }
}
