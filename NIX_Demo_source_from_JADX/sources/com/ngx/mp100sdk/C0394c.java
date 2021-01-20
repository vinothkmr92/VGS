package com.ngx.mp100sdk;

import p000a.p001a.p002a.C0001b;

/* renamed from: com.ngx.mp100sdk.c */
final class C0394c extends C0001b {

    /* renamed from: a */
    private /* synthetic */ NGXPrinter f86a;

    C0394c(NGXPrinter nGXPrinter) {
        this.f86a = nGXPrinter;
    }

    /* renamed from: a */
    public final void mo1a(int i, String str) {
        this.f86a.f79k.onRaiseException(i, str);
    }

    /* renamed from: a */
    public final void mo2a(String str) {
        this.f86a.f79k.onReturnString(str);
    }

    /* renamed from: a */
    public final void mo3a(boolean z) {
        this.f86a.f79k.onRunResult(z);
    }
}
