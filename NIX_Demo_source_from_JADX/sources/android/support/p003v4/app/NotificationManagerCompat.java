package android.support.p003v4.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings.Secure;
import android.support.p003v4.app.INotificationSideChannel.Stub;
import android.support.p003v4.p005os.BuildCompat;
import android.util.Log;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/* renamed from: android.support.v4.app.NotificationManagerCompat */
public final class NotificationManagerCompat {
    public static final String ACTION_BIND_SIDE_CHANNEL = "android.support.BIND_NOTIFICATION_SIDE_CHANNEL";
    public static final String EXTRA_USE_SIDE_CHANNEL = "android.support.useSideChannel";
    private static final Impl IMPL;
    public static final int IMPORTANCE_DEFAULT = 3;
    public static final int IMPORTANCE_HIGH = 4;
    public static final int IMPORTANCE_LOW = 2;
    public static final int IMPORTANCE_MAX = 5;
    public static final int IMPORTANCE_MIN = 1;
    public static final int IMPORTANCE_NONE = 0;
    public static final int IMPORTANCE_UNSPECIFIED = -1000;
    static final int MAX_SIDE_CHANNEL_SDK_VERSION = 19;
    private static final String SETTING_ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    static final int SIDE_CHANNEL_BIND_FLAGS = IMPL.getSideChannelBindFlags();
    private static final int SIDE_CHANNEL_RETRY_BASE_INTERVAL_MS = 1000;
    private static final int SIDE_CHANNEL_RETRY_MAX_COUNT = 6;
    private static final String TAG = "NotifManCompat";
    private static Set<String> sEnabledNotificationListenerPackages = new HashSet();
    private static String sEnabledNotificationListeners;
    private static final Object sEnabledNotificationListenersLock = new Object();
    private static final Object sLock = new Object();
    private static SideChannelManager sSideChannelManager;
    private final Context mContext;
    private final NotificationManager mNotificationManager = ((NotificationManager) this.mContext.getSystemService("notification"));

    /* renamed from: android.support.v4.app.NotificationManagerCompat$CancelTask */
    private static class CancelTask implements Task {
        final boolean all;

        /* renamed from: id */
        final int f15id;
        final String packageName;
        final String tag;

        public CancelTask(String packageName2) {
            this.packageName = packageName2;
            this.f15id = 0;
            this.tag = null;
            this.all = true;
        }

        public CancelTask(String packageName2, int id, String tag2) {
            this.packageName = packageName2;
            this.f15id = id;
            this.tag = tag2;
            this.all = false;
        }

        public void send(INotificationSideChannel service) throws RemoteException {
            if (this.all) {
                service.cancelAll(this.packageName);
            } else {
                service.cancel(this.packageName, this.f15id, this.tag);
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("CancelTask[");
            sb.append("packageName:").append(this.packageName);
            sb.append(", id:").append(this.f15id);
            sb.append(", tag:").append(this.tag);
            sb.append(", all:").append(this.all);
            sb.append("]");
            return sb.toString();
        }
    }

    /* renamed from: android.support.v4.app.NotificationManagerCompat$Impl */
    interface Impl {
        boolean areNotificationsEnabled(Context context, NotificationManager notificationManager);

        void cancelNotification(NotificationManager notificationManager, String str, int i);

        int getImportance(NotificationManager notificationManager);

        int getSideChannelBindFlags();

        void postNotification(NotificationManager notificationManager, String str, int i, Notification notification);
    }

    /* renamed from: android.support.v4.app.NotificationManagerCompat$ImplApi24 */
    static class ImplApi24 extends ImplKitKat {
        ImplApi24() {
        }

        public boolean areNotificationsEnabled(Context context, NotificationManager notificationManager) {
            return NotificationManagerCompatApi24.areNotificationsEnabled(notificationManager);
        }

        public int getImportance(NotificationManager notificationManager) {
            return NotificationManagerCompatApi24.getImportance(notificationManager);
        }
    }

    /* renamed from: android.support.v4.app.NotificationManagerCompat$ImplBase */
    static class ImplBase implements Impl {
        ImplBase() {
        }

        public void cancelNotification(NotificationManager notificationManager, String tag, int id) {
            notificationManager.cancel(tag, id);
        }

        public void postNotification(NotificationManager notificationManager, String tag, int id, Notification notification) {
            notificationManager.notify(tag, id, notification);
        }

        public int getSideChannelBindFlags() {
            return 1;
        }

        public boolean areNotificationsEnabled(Context context, NotificationManager notificationManager) {
            return true;
        }

        public int getImportance(NotificationManager notificationManager) {
            return NotificationManagerCompat.IMPORTANCE_UNSPECIFIED;
        }
    }

    /* renamed from: android.support.v4.app.NotificationManagerCompat$ImplIceCreamSandwich */
    static class ImplIceCreamSandwich extends ImplBase {
        ImplIceCreamSandwich() {
        }

        public int getSideChannelBindFlags() {
            return 33;
        }
    }

    /* renamed from: android.support.v4.app.NotificationManagerCompat$ImplKitKat */
    static class ImplKitKat extends ImplIceCreamSandwich {
        ImplKitKat() {
        }

        public boolean areNotificationsEnabled(Context context, NotificationManager notificationManager) {
            return NotificationManagerCompatKitKat.areNotificationsEnabled(context);
        }
    }

    /* renamed from: android.support.v4.app.NotificationManagerCompat$NotifyTask */
    private static class NotifyTask implements Task {

        /* renamed from: id */
        final int f16id;
        final Notification notif;
        final String packageName;
        final String tag;

        public NotifyTask(String packageName2, int id, String tag2, Notification notif2) {
            this.packageName = packageName2;
            this.f16id = id;
            this.tag = tag2;
            this.notif = notif2;
        }

        public void send(INotificationSideChannel service) throws RemoteException {
            service.notify(this.packageName, this.f16id, this.tag, this.notif);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("NotifyTask[");
            sb.append("packageName:").append(this.packageName);
            sb.append(", id:").append(this.f16id);
            sb.append(", tag:").append(this.tag);
            sb.append("]");
            return sb.toString();
        }
    }

    /* renamed from: android.support.v4.app.NotificationManagerCompat$ServiceConnectedEvent */
    private static class ServiceConnectedEvent {
        final ComponentName componentName;
        final IBinder iBinder;

        public ServiceConnectedEvent(ComponentName componentName2, IBinder iBinder2) {
            this.componentName = componentName2;
            this.iBinder = iBinder2;
        }
    }

    /* renamed from: android.support.v4.app.NotificationManagerCompat$SideChannelManager */
    private static class SideChannelManager implements Callback, ServiceConnection {
        private static final String KEY_BINDER = "binder";
        private static final int MSG_QUEUE_TASK = 0;
        private static final int MSG_RETRY_LISTENER_QUEUE = 3;
        private static final int MSG_SERVICE_CONNECTED = 1;
        private static final int MSG_SERVICE_DISCONNECTED = 2;
        private Set<String> mCachedEnabledPackages = new HashSet();
        private final Context mContext;
        private final Handler mHandler;
        private final HandlerThread mHandlerThread;
        private final Map<ComponentName, ListenerRecord> mRecordMap = new HashMap();

        /* renamed from: android.support.v4.app.NotificationManagerCompat$SideChannelManager$ListenerRecord */
        private static class ListenerRecord {
            public boolean bound = false;
            public final ComponentName componentName;
            public int retryCount = 0;
            public INotificationSideChannel service;
            public LinkedList<Task> taskQueue = new LinkedList<>();

            public ListenerRecord(ComponentName componentName2) {
                this.componentName = componentName2;
            }
        }

        public SideChannelManager(Context context) {
            this.mContext = context;
            this.mHandlerThread = new HandlerThread("NotificationManagerCompat");
            this.mHandlerThread.start();
            this.mHandler = new Handler(this.mHandlerThread.getLooper(), this);
        }

        public void queueTask(Task task) {
            this.mHandler.obtainMessage(0, task).sendToTarget();
        }

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    handleQueueTask((Task) msg.obj);
                    return true;
                case 1:
                    ServiceConnectedEvent event = (ServiceConnectedEvent) msg.obj;
                    handleServiceConnected(event.componentName, event.iBinder);
                    return true;
                case 2:
                    handleServiceDisconnected((ComponentName) msg.obj);
                    return true;
                case 3:
                    handleRetryListenerQueue((ComponentName) msg.obj);
                    return true;
                default:
                    return false;
            }
        }

        private void handleQueueTask(Task task) {
            updateListenerMap();
            for (ListenerRecord record : this.mRecordMap.values()) {
                record.taskQueue.add(task);
                processListenerQueue(record);
            }
        }

        private void handleServiceConnected(ComponentName componentName, IBinder iBinder) {
            ListenerRecord record = (ListenerRecord) this.mRecordMap.get(componentName);
            if (record != null) {
                record.service = Stub.asInterface(iBinder);
                record.retryCount = 0;
                processListenerQueue(record);
            }
        }

        private void handleServiceDisconnected(ComponentName componentName) {
            ListenerRecord record = (ListenerRecord) this.mRecordMap.get(componentName);
            if (record != null) {
                ensureServiceUnbound(record);
            }
        }

        private void handleRetryListenerQueue(ComponentName componentName) {
            ListenerRecord record = (ListenerRecord) this.mRecordMap.get(componentName);
            if (record != null) {
                processListenerQueue(record);
            }
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (Log.isLoggable(NotificationManagerCompat.TAG, 3)) {
                Log.d(NotificationManagerCompat.TAG, "Connected to service " + componentName);
            }
            this.mHandler.obtainMessage(1, new ServiceConnectedEvent(componentName, iBinder)).sendToTarget();
        }

        public void onServiceDisconnected(ComponentName componentName) {
            if (Log.isLoggable(NotificationManagerCompat.TAG, 3)) {
                Log.d(NotificationManagerCompat.TAG, "Disconnected from service " + componentName);
            }
            this.mHandler.obtainMessage(2, componentName).sendToTarget();
        }

        private void updateListenerMap() {
            Set<String> enabledPackages = NotificationManagerCompat.getEnabledListenerPackages(this.mContext);
            if (!enabledPackages.equals(this.mCachedEnabledPackages)) {
                this.mCachedEnabledPackages = enabledPackages;
                List<ResolveInfo> resolveInfos = this.mContext.getPackageManager().queryIntentServices(new Intent().setAction(NotificationManagerCompat.ACTION_BIND_SIDE_CHANNEL), 4);
                Set<ComponentName> enabledComponents = new HashSet<>();
                for (ResolveInfo resolveInfo : resolveInfos) {
                    if (enabledPackages.contains(resolveInfo.serviceInfo.packageName)) {
                        ComponentName componentName = new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);
                        if (resolveInfo.serviceInfo.permission != null) {
                            Log.w(NotificationManagerCompat.TAG, "Permission present on component " + componentName + ", not adding listener record.");
                        } else {
                            enabledComponents.add(componentName);
                        }
                    }
                }
                for (ComponentName componentName2 : enabledComponents) {
                    if (!this.mRecordMap.containsKey(componentName2)) {
                        if (Log.isLoggable(NotificationManagerCompat.TAG, 3)) {
                            Log.d(NotificationManagerCompat.TAG, "Adding listener record for " + componentName2);
                        }
                        this.mRecordMap.put(componentName2, new ListenerRecord(componentName2));
                    }
                }
                Iterator<Entry<ComponentName, ListenerRecord>> it = this.mRecordMap.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<ComponentName, ListenerRecord> entry = (Entry) it.next();
                    if (!enabledComponents.contains(entry.getKey())) {
                        if (Log.isLoggable(NotificationManagerCompat.TAG, 3)) {
                            Log.d(NotificationManagerCompat.TAG, "Removing listener record for " + entry.getKey());
                        }
                        ensureServiceUnbound((ListenerRecord) entry.getValue());
                        it.remove();
                    }
                }
            }
        }

        private boolean ensureServiceBound(ListenerRecord record) {
            if (record.bound) {
                return true;
            }
            record.bound = this.mContext.bindService(new Intent(NotificationManagerCompat.ACTION_BIND_SIDE_CHANNEL).setComponent(record.componentName), this, NotificationManagerCompat.SIDE_CHANNEL_BIND_FLAGS);
            if (record.bound) {
                record.retryCount = 0;
            } else {
                Log.w(NotificationManagerCompat.TAG, "Unable to bind to listener " + record.componentName);
                this.mContext.unbindService(this);
            }
            return record.bound;
        }

        private void ensureServiceUnbound(ListenerRecord record) {
            if (record.bound) {
                this.mContext.unbindService(this);
                record.bound = false;
            }
            record.service = null;
        }

        private void scheduleListenerRetry(ListenerRecord record) {
            if (!this.mHandler.hasMessages(3, record.componentName)) {
                record.retryCount++;
                if (record.retryCount > 6) {
                    Log.w(NotificationManagerCompat.TAG, "Giving up on delivering " + record.taskQueue.size() + " tasks to " + record.componentName + " after " + record.retryCount + " retries");
                    record.taskQueue.clear();
                    return;
                }
                int delayMs = (1 << (record.retryCount - 1)) * 1000;
                if (Log.isLoggable(NotificationManagerCompat.TAG, 3)) {
                    Log.d(NotificationManagerCompat.TAG, "Scheduling retry for " + delayMs + " ms");
                }
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(3, record.componentName), (long) delayMs);
            }
        }

        private void processListenerQueue(ListenerRecord record) {
            if (Log.isLoggable(NotificationManagerCompat.TAG, 3)) {
                Log.d(NotificationManagerCompat.TAG, "Processing component " + record.componentName + ", " + record.taskQueue.size() + " queued tasks");
            }
            if (!record.taskQueue.isEmpty()) {
                if (!ensureServiceBound(record) || record.service == null) {
                    scheduleListenerRetry(record);
                    return;
                }
                while (true) {
                    Task task = (Task) record.taskQueue.peek();
                    if (task == null) {
                        break;
                    }
                    try {
                        if (Log.isLoggable(NotificationManagerCompat.TAG, 3)) {
                            Log.d(NotificationManagerCompat.TAG, "Sending task " + task);
                        }
                        task.send(record.service);
                        record.taskQueue.remove();
                    } catch (DeadObjectException e) {
                        if (Log.isLoggable(NotificationManagerCompat.TAG, 3)) {
                            Log.d(NotificationManagerCompat.TAG, "Remote service has died: " + record.componentName);
                        }
                    } catch (RemoteException e2) {
                        Log.w(NotificationManagerCompat.TAG, "RemoteException communicating with " + record.componentName, e2);
                    }
                }
                if (!record.taskQueue.isEmpty()) {
                    scheduleListenerRetry(record);
                }
            }
        }
    }

    /* renamed from: android.support.v4.app.NotificationManagerCompat$Task */
    private interface Task {
        void send(INotificationSideChannel iNotificationSideChannel) throws RemoteException;
    }

    static {
        if (BuildCompat.isAtLeastN()) {
            IMPL = new ImplApi24();
        } else if (VERSION.SDK_INT >= 19) {
            IMPL = new ImplKitKat();
        } else if (VERSION.SDK_INT >= 14) {
            IMPL = new ImplIceCreamSandwich();
        } else {
            IMPL = new ImplBase();
        }
    }

    public static NotificationManagerCompat from(Context context) {
        return new NotificationManagerCompat(context);
    }

    private NotificationManagerCompat(Context context) {
        this.mContext = context;
    }

    public void cancel(int id) {
        cancel(null, id);
    }

    public void cancel(String tag, int id) {
        IMPL.cancelNotification(this.mNotificationManager, tag, id);
        if (VERSION.SDK_INT <= 19) {
            pushSideChannelQueue(new CancelTask(this.mContext.getPackageName(), id, tag));
        }
    }

    public void cancelAll() {
        this.mNotificationManager.cancelAll();
        if (VERSION.SDK_INT <= 19) {
            pushSideChannelQueue(new CancelTask(this.mContext.getPackageName()));
        }
    }

    public void notify(int id, Notification notification) {
        notify(null, id, notification);
    }

    public void notify(String tag, int id, Notification notification) {
        if (useSideChannelForNotification(notification)) {
            pushSideChannelQueue(new NotifyTask(this.mContext.getPackageName(), id, tag, notification));
            IMPL.cancelNotification(this.mNotificationManager, tag, id);
            return;
        }
        IMPL.postNotification(this.mNotificationManager, tag, id, notification);
    }

    public boolean areNotificationsEnabled() {
        return IMPL.areNotificationsEnabled(this.mContext, this.mNotificationManager);
    }

    public int getImportance() {
        return IMPL.getImportance(this.mNotificationManager);
    }

    public static Set<String> getEnabledListenerPackages(Context context) {
        Set<String> set;
        String enabledNotificationListeners = Secure.getString(context.getContentResolver(), SETTING_ENABLED_NOTIFICATION_LISTENERS);
        synchronized (sEnabledNotificationListenersLock) {
            if (enabledNotificationListeners != null) {
                if (!enabledNotificationListeners.equals(sEnabledNotificationListeners)) {
                    String[] components = enabledNotificationListeners.split(":");
                    Set<String> packageNames = new HashSet<>(components.length);
                    for (String component : components) {
                        ComponentName componentName = ComponentName.unflattenFromString(component);
                        if (componentName != null) {
                            packageNames.add(componentName.getPackageName());
                        }
                    }
                    sEnabledNotificationListenerPackages = packageNames;
                    sEnabledNotificationListeners = enabledNotificationListeners;
                }
            }
            set = sEnabledNotificationListenerPackages;
        }
        return set;
    }

    private static boolean useSideChannelForNotification(Notification notification) {
        Bundle extras = NotificationCompat.getExtras(notification);
        return extras != null && extras.getBoolean(EXTRA_USE_SIDE_CHANNEL);
    }

    private void pushSideChannelQueue(Task task) {
        synchronized (sLock) {
            if (sSideChannelManager == null) {
                sSideChannelManager = new SideChannelManager(this.mContext.getApplicationContext());
            }
            sSideChannelManager.queueTask(task);
        }
    }
}
