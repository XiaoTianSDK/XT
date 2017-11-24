package com.xiaotian.framework.util.view.inject;

import android.app.Activity;
import android.view.View;

import com.xiaotian.framework.common.Mylog;
import com.xiaotian.frameworkxt.android.common.MyOnClickListener;

import java.lang.reflect.Field;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @description
 * @date 2015/12/31
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2015 小天天 Studio, All Rights Reserved.
 */
public class Injector {
    //
    public static void injecting(Activity activity) {
        Class clazz = activity.getClass();
        do {
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; fields != null && i < fields.length; i++) {
                Field field = fields[i];//
                InjectId injectId = field.getAnnotation(InjectId.class);
                if (injectId != null) {
                    int id = injectId.id();
                    if (id != Integer.MIN_VALUE) {
                        View idView = activity.findViewById(id);
                        if (idView != null) {
                            // View Inject
                            if (field.getType().isInstance(idView)) {
                                try {
                                    field.setAccessible(true);
                                    field.set(activity, idView);
                                    field.setAccessible(false);
                                } catch (IllegalAccessException e) {
                                    Mylog.e(e);
                                }
                            }
                            // OnClick Inject
                            if (injectId.onClick() != InjectId.DEFAULT_ONCLICK) {
                                idView.setOnClickListener(new MyOnClickListener<Object>(activity, injectId, clazz) {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            getInitParams(2).getClass().getDeclaredMethod(((InjectId) getInitParams(1)).onClick(), View.class).invoke(getInitParams(0), v);
                                        } catch (Exception e) {
                                            Mylog.e(e);
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
    }

    //
    public static View injecting(Object target, View view) {
        Class clazz = target.getClass();
        do {
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; fields != null && i < fields.length; i++) {
                Field field = fields[i];//
                InjectId injectId = field.getAnnotation(InjectId.class);
                if (injectId != null) {
                    int id = injectId.id();
                    if (id != Integer.MIN_VALUE) {
                        View idView = view.findViewById(id);
                        if (idView != null) {
                            // View Inject
                            if (field.getType().isInstance(idView)) {
                                try {
                                    field.setAccessible(true);
                                    field.set(target, idView);
                                    field.setAccessible(false);
                                } catch (IllegalAccessException e) {
                                    Mylog.e(e);
                                }
                            }
                            // OnClick Inject
                            if (injectId.onClick() != InjectId.DEFAULT_ONCLICK) {
                                idView.setOnClickListener(new MyOnClickListener<Object>(target, injectId, clazz) {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            getInitParams(2).getClass().getDeclaredMethod(((InjectId) getInitParams(1)).onClick(), View.class).invoke(getInitParams(0), v);
                                        } catch (Exception e) {
                                            Mylog.e(e);
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        return view;
    }

}
