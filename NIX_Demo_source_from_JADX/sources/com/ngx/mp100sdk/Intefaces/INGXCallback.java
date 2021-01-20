package com.ngx.mp100sdk.Intefaces;

public interface INGXCallback {
    void onRaiseException(int i, String str);

    void onReturnString(String str);

    void onRunResult(boolean z);
}
