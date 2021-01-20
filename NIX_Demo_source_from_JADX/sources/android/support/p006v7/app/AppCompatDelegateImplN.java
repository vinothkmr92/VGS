package android.support.p006v7.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.RequiresApi;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.Window;
import android.view.Window.Callback;
import java.util.List;

@TargetApi(24)
@RequiresApi(24)
/* renamed from: android.support.v7.app.AppCompatDelegateImplN */
class AppCompatDelegateImplN extends AppCompatDelegateImplV23 {

    /* renamed from: android.support.v7.app.AppCompatDelegateImplN$AppCompatWindowCallbackN */
    class AppCompatWindowCallbackN extends AppCompatWindowCallbackV23 {
        AppCompatWindowCallbackN(Callback callback) {
            super(callback);
        }

        public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, Menu menu, int deviceId) {
            PanelFeatureState panel = AppCompatDelegateImplN.this.getPanelState(0, true);
            if (panel == null || panel.menu == null) {
                super.onProvideKeyboardShortcuts(data, menu, deviceId);
            } else {
                super.onProvideKeyboardShortcuts(data, panel.menu, deviceId);
            }
        }
    }

    AppCompatDelegateImplN(Context context, Window window, AppCompatCallback callback) {
        super(context, window, callback);
    }

    /* access modifiers changed from: 0000 */
    public Callback wrapWindowCallback(Callback callback) {
        return new AppCompatWindowCallbackN(callback);
    }
}
