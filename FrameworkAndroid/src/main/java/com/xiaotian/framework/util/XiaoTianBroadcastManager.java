package com.xiaotian.framework.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.xiaotian.frameworkxt.android.common.Mylog;
import com.xiaotian.frameworkxt.util.UtilMD5;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @name XiaoTianBroadcastManager
 * @description Xiao Tian Broadcast ReWin
 * @date 2017/5/15
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2017 小天天 Studio, All Rights Reserved.
 */
public class XiaoTianBroadcastManager {
    private static final String TAG = "XiaoTianBroadcastManager";
    private static final int MSG_EXEC_PENDING_BROADCASTS = 1;
    private static final boolean DEBUG = false;
    private static final Object mLock = new Object();
    private static XiaoTianBroadcastManager mInstance;
    //
    private final Context mAppContext;
    private final HashMap<Receiver, ArrayList<IntentFilter>> mReceivers = new HashMap<Receiver, ArrayList<IntentFilter>>();
    private final HashMap<String, ArrayList<ReceiverRecord>> mActions = new HashMap<String, ArrayList<ReceiverRecord>>();
    private final ArrayList<BroadcastRecord> mPendingBroadcasts = new ArrayList<BroadcastRecord>();
    private final Handler mHandler;

    public static XiaoTianBroadcastManager getInstance(Context context) {
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new XiaoTianBroadcastManager(context.getApplicationContext());
            }
            return mInstance;
        }
    }

    private XiaoTianBroadcastManager(Context context) {
        mAppContext = context;
        mHandler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_EXEC_PENDING_BROADCASTS:
                        executePendingBroadcasts();
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };
    }

    public void registerReceiver(Receiver receiver) {
        if (receiver == null) return;
        registerReceiver(receiver, getReceiverFilter(receiver.getClass(), null));
    }

    public void registerReceiver(Receiver receiver, String action) {
        if (receiver == null || action == null) return;
        registerReceiver(receiver, getReceiverFilter(receiver.getClass(), action));
    }

    public void registerReceiver(Receiver receiver, IntentFilter filter) {
        synchronized (mReceivers) {
            ReceiverRecord entry = new ReceiverRecord(filter, receiver);
            ArrayList<IntentFilter> filters = mReceivers.get(receiver);
            if (filters == null) {
                filters = new ArrayList<IntentFilter>(1);
                mReceivers.put(receiver, filters);
            }
            filters.add(filter);
            for (int i = 0; i < filter.countActions(); i++) {
                String action = filter.getAction(i);
                ArrayList<ReceiverRecord> entries = mActions.get(action);
                if (entries == null) {
                    entries = new ArrayList<ReceiverRecord>(1);
                    mActions.put(action, entries);
                }
                entries.add(entry);
            }
        }
    }

    public void unregisterReceiver(Receiver receiver) {
        synchronized (mReceivers) {
            ArrayList<IntentFilter> filters = mReceivers.remove(receiver);
            if (filters == null) {
                return;
            }
            for (int i = 0; i < filters.size(); i++) {
                IntentFilter filter = filters.get(i);
                for (int j = 0; j < filter.countActions(); j++) {
                    String action = filter.getAction(j);
                    ArrayList<ReceiverRecord> receivers = mActions.get(action);
                    if (receivers != null) {
                        for (int k = 0; k < receivers.size(); k++) {
                            if (receivers.get(k).receiver == receiver) {
                                receivers.remove(k);
                                k--;
                            }
                        }
                        if (receivers.size() <= 0) {
                            mActions.remove(action);
                        }
                    }
                }
            }
        }
    }

    public <ExtraParam, R extends Receiver> boolean sendBroadcast(Class<R> receiver, ExtraParam extraParam) {
        // extra params is reference do not send when in different Classification
        return sendBroadcast(getReceiverIntent(receiver), extraParam);
    }

    public <ExtraParam> boolean sendBroadcast(String action, ExtraParam extraParam) {
        // extra params is reference do not send when in different Classification
        return sendBroadcast(getReceiverIntent(action), extraParam);
    }

    public <ExtraParam> boolean sendBroadcast(String action, Bundle extras, ExtraParam extraParam) {
        // extra params is reference do not send when in different Classification
        Intent intent = getReceiverIntent(action);
        if (extras != null) intent.putExtras(extras);
        return sendBroadcast(intent, extraParam);
    }

    public boolean sendBroadcast(String action, Bundle extras) {
        Intent intent = getReceiverIntent(action);
        if (extras != null) intent.putExtras(extras);
        return sendBroadcast(intent, null);
    }

    public boolean sendBroadcast(String action) {
        // extra params is reference do not send when in different Classification
        return sendBroadcast(getReceiverIntent(action), null);
    }

    public <ExtraParam> boolean sendBroadcast(Intent intent, ExtraParam extraParam) {
        synchronized (mReceivers) {
            final String action = intent.getAction();
            final String type = intent.resolveTypeIfNeeded(mAppContext.getContentResolver());
            final Uri data = intent.getData();
            final String scheme = intent.getScheme();
            final Set<String> categories = intent.getCategories();

            final boolean debug = DEBUG || ((intent.getFlags() & Intent.FLAG_DEBUG_LOG_RESOLUTION) != 0);
            if (debug) Mylog.info(TAG, "Resolving type " + type + " scheme " + scheme + " of intent " + intent);

            ArrayList<ReceiverRecord> entries = mActions.get(intent.getAction());
            if (entries != null) {
                if (debug) Mylog.info(TAG, "Action list: " + entries);

                ArrayList<ReceiverRecord> receivers = null;
                for (int i = 0; i < entries.size(); i++) {
                    ReceiverRecord receiver = entries.get(i);
                    if (debug) Mylog.info(TAG, "Matching against filter " + receiver.filter);

                    if (receiver.broadcasting) {
                        if (debug) {
                            Mylog.info(TAG, "  Filter's target already added");
                        }
                        continue;
                    }

                    int match = receiver.filter.match(action, type, scheme, data, categories, "LocalBroadcastManager");
                    if (match >= 0) {
                        if (debug) Mylog.info(TAG, "  Filter matched!  match=0x" + Integer.toHexString(match));
                        if (receivers == null) {
                            receivers = new ArrayList<ReceiverRecord>();
                        }
                        receivers.add(receiver);
                        receiver.broadcasting = true;
                    } else {
                        if (debug) {
                            String reason;
                            switch (match) {
                                case IntentFilter.NO_MATCH_ACTION:
                                    reason = "action";
                                    break;
                                case IntentFilter.NO_MATCH_CATEGORY:
                                    reason = "category";
                                    break;
                                case IntentFilter.NO_MATCH_DATA:
                                    reason = "data";
                                    break;
                                case IntentFilter.NO_MATCH_TYPE:
                                    reason = "type";
                                    break;
                                default:
                                    reason = "unknown reason";
                                    break;
                            }
                            Mylog.info(TAG, "  Filter did not match: " + reason);
                        }
                    }
                }

                if (receivers != null) {
                    for (int i = 0; i < receivers.size(); i++) {
                        receivers.get(i).broadcasting = false;
                    }
                    mPendingBroadcasts.add(new BroadcastRecord(intent, receivers, extraParam));
                    if (!mHandler.hasMessages(MSG_EXEC_PENDING_BROADCASTS)) {
                        mHandler.sendEmptyMessage(MSG_EXEC_PENDING_BROADCASTS);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public void sendBroadcastSync(Intent intent) {
        if (sendBroadcast(intent, null)) {
            executePendingBroadcasts();
        }
    }

    private void executePendingBroadcasts() {
        while (true) {
            BroadcastRecord[] brs = null;
            synchronized (mReceivers) {
                final int N = mPendingBroadcasts.size();
                if (N <= 0) {
                    return;
                }
                brs = new BroadcastRecord[N];
                mPendingBroadcasts.toArray(brs);
                mPendingBroadcasts.clear();
            }
            for (int i = 0; i < brs.length; i++) {
                BroadcastRecord br = brs[i];
                for (int j = 0; j < br.receivers.size(); j++) {
                    ReceiverRecord receiverRecord = (ReceiverRecord) br.receivers.get(j);
                    receiverRecord.receiver.onReceiveXiaoTianBroadcast(mAppContext, br.intent, br.extraParam);
                }
            }
        }
    }

    private <T extends Receiver> IntentFilter getReceiverFilter(Class<T> receiver, String action) {
        IntentFilter intentFilter = new IntentFilter(action == null ? getReceiverName(receiver) : action);
        intentFilter.addCategory("com.xiaotian.broadcast.receiver");
        return intentFilter;
    }

    private <T extends Receiver> Intent getReceiverIntent(Class<T> receiver) {
        Intent intent = new Intent(getReceiverName(receiver));
        intent.addCategory("com.xiaotian.broadcast.receiver");
        return intent;
    }

    private Intent getReceiverIntent(String action) {
        Intent intent = new Intent(action);
        intent.addCategory("com.xiaotian.broadcast.receiver");
        return intent;
    }

    private <T extends Receiver> String getReceiverName(Class<T> receiver) {
        return String.format("xiaotian.receiver.%1$s", UtilMD5.MD5(receiver.getName()));
    }

    public static Bundle genTypeExtras(int type) {
        Bundle extras = new Bundle();
        extras.putInt(String.format("%1$s_type", TAG), type);
        return extras;
    }

    public static int getTypeExtras(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return Integer.MIN_VALUE;
        }
        return extras.getInt(String.format("%1$s_type", TAG), -1);
    }

    // Inner Class
    private static class ReceiverRecord {
        final IntentFilter filter;
        final Receiver receiver;
        boolean broadcasting;

        ReceiverRecord(IntentFilter _filter, Receiver _receiver) {
            filter = _filter;
            receiver = _receiver;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(128);
            builder.append("Receiver{");
            builder.append(receiver);
            builder.append(" filter=");
            builder.append(filter);
            builder.append("}");
            return builder.toString();
        }
    }

    private static class BroadcastRecord<ExtraParam> {
        final Intent intent;
        final ExtraParam extraParam;
        final ArrayList<ReceiverRecord> receivers;

        BroadcastRecord(Intent _intent, ArrayList<ReceiverRecord> _receivers, ExtraParam _extraParam) {
            intent = _intent;
            receivers = _receivers;
            extraParam = _extraParam;
        }

    }

    // Receiver Interface
    public interface Receiver<ExtraParam> {
        // Main Thread Executer
        void onReceiveXiaoTianBroadcast(Context context, Intent intent, ExtraParam extraParam);
    }

    public static <ExtraParam> ExtraParam copy(ExtraParam extraParam) {
        if (extraParam == null) return null;
        Class<?> clazz = extraParam.getClass();
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            Object newParams = constructor.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            // 支持String,Integer
            for (Field field : fields) {
                if (field.getType().isAssignableFrom(String.class)) {
                    field.setAccessible(true);
                    field.set(newParams, field.get(extraParam));
                    field.setAccessible(false);
                } else if (field.getType().isAssignableFrom(Integer.TYPE)) {

                }

                /*boolean.class
                byte.class
                char.class
                short.class
                int.class
                long.class
                float.class
                double.class*/
            }
        } catch (NoSuchMethodException e) {
            Mylog.printStackTrace(e);
            Mylog.e(TAG, String.format("%1$s mush contain null params Constructor().", clazz.getName()));
        } catch (Exception e) {
            Mylog.printStackTrace(e);
        }
        return null;
    }
}
