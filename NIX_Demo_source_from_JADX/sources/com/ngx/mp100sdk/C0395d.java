package com.ngx.mp100sdk;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import p000a.p001a.p002a.C0004e;

/* renamed from: com.ngx.mp100sdk.d */
final class C0395d implements ServiceConnection {

    /* renamed from: a */
    private /* synthetic */ C0392a f87a;

    C0395d(C0392a aVar) {
        this.f87a = aVar;
    }

    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        this.f87a.f82a.f77i = C0004e.m28a(iBinder);
        NGXPrinter.m91b(this.f87a.f82a);
        Log.i("NGX", "Service Connected");
        if (this.f87a.f82a.f77i == null) {
            Log.i("NGX", "PrinterService is null");
        }
    }

    public final void onServiceDisconnected(ComponentName componentName) {
        this.f87a.f82a.f77i = null;
    }
}
