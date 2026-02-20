package com.xiaomi.adb.bypass;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedMain implements IXposedHookLoadPackage {

    /**
     * Determine whether it is a virtual method
     *
     * @param method
     * @return
     */
    public static boolean isVirtualMethod(Method method) {
        if (method == null) {
            return false;
        }
        int modifiers = method.getModifiers();

        return !Modifier.isStatic(modifiers)
                && !Modifier.isFinal(modifiers)
                && !Modifier.isPrivate(modifiers);
    }

    /**
     * Determine whether parameter types match
     *
     * @param method
     * @param index
     * @param clazz
     * @return
     */
    public static boolean isParamType(Method method, int index, Class<?> clazz) {
        if (method == null || clazz == null || index < 0) {
            return false;
        }

        Class<?>[] paramTypes = method.getParameterTypes();

        if (index >= paramTypes.length) {
            return false;
        }

        return paramTypes[index] == clazz;
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lp) {
        try {
            if (Objects.equals(lp.packageName, "com.miui.securitycenter")) {
                startHookBySecurityCenter(lp.classLoader);
            }
            // Not recommended to force enable
            if (Objects.equals(lp.packageName, "com.android.settings")) {
                startHookBySettings(lp.classLoader);
            }
        } catch (Throwable e) {
            XposedBridge.log(e);
        }
    }

    private void startHookBySecurityCenter(ClassLoader classLoader) {
        // hyper
        try {
            Class<?> clazz = classLoader.loadClass("com.miui.permcenter.privacymanager.InterceptPermissionFragment");
            {
                Method method = clazz.getDeclaredMethod("initData");
                XposedBridge.hookMethod(method, new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object o = param.thisObject;
                        for (var method : o.getClass().getDeclaredMethods()) {
                            if (isVirtualMethod(method)) {
                                if (isParamType(method, 0, boolean.class)) {
                                    XposedBridge.log("Call confirmation method " + method.getName());
                                    method.setAccessible(true);
                                    method.invoke(o, true);
                                    break;
                                }
                            }
                        }
                    }
                });
            }
        } catch (Throwable e) {
            XposedBridge.log(e);
        }
        // miui
        try {
            Class<?> clazz = classLoader.loadClass("com.miui.permcenter.privacymanager.InterceptMIUIFragment");
            {
                Method method = clazz.getDeclaredMethod("initData");
                XposedBridge.hookMethod(method, new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object o = param.thisObject;
                        for (var method : o.getClass().getDeclaredMethods()) {
                            if (isVirtualMethod(method)) {
                                if (isParamType(method, 0, boolean.class)) {
                                    XposedBridge.log("Call confirmation method " + method.getName());
                                    method.setAccessible(true);
                                    method.invoke(o, true);
                                    break;
                                }
                            }
                        }
                    }
                });
            }
        } catch (Throwable e) {
            XposedBridge.log(e);
        }
        // hyper
        try {
            Class<?> clazz = classLoader.loadClass("com.miui.permcenter.install.AdbInstallVerifyActivity");
            {
                Method method = clazz.getDeclaredMethod("onCreate", Bundle.class);
                XposedBridge.hookMethod(method, new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        var activity = (Activity) param.thisObject;
                        var intent = activity.getIntent();
                        if (intent != null) {
                            var isInput = intent.getBooleanExtra("is_input", false);
                            if (!isInput) {
                                XposedBridge.log("Disable Xiaomi Account verification for USB installation");
                            }
                        }
                        boolean success = false;
                        try {
                            var method = activity.getClass().getDeclaredMethod("K0");
                            method.setAccessible(true);
                            method.invoke(activity);
                            success = true;
                        } catch (Throwable e) {
                            XposedBridge.log(e);
                        }
                        try {
                            // Global
                            var method = activity.getClass().getDeclaredMethod("j0");
                            method.setAccessible(true);
                            method.invoke(activity);
                            success = true;
                        } catch (Throwable e) {
                            XposedBridge.log(e);
                        }
                        if (success) {
                            activity.finish();
                        } else {
                            Toast.makeText(activity, "Disable Xiaomi Account verification failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            try {
                Method method = clazz.getDeclaredMethod("J0", Bundle.class);
                XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(null));
            } catch (Throwable e) {
                XposedBridge.log(e);
            }
            try {
                Method method = clazz.getDeclaredMethod("N0");
                XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(null));
            } catch (Throwable e) {
                XposedBridge.log(e);
            }
            // Global
            try {
                Method method = clazz.getDeclaredMethod("i0", Bundle.class);
                XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(null));
            } catch (Throwable e) {
                XposedBridge.log(e);
            }
            try {
                Method method = clazz.getDeclaredMethod("m0");
                XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(null));
            } catch (Throwable e) {
                XposedBridge.log(e);
            }
        } catch (Throwable e) {
            XposedBridge.log(e);
        }
    }

    private void startHookBySettings(ClassLoader classLoader) {
        try {
            Class<?> clazz = classLoader.loadClass("com.android.security.AdbUtils");
            {
                Method method = clazz.getDeclaredMethod("isInstallEnabled", Context.class);
                XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(true));
            }
            {
                Method method = clazz.getDeclaredMethod("isInputEnabled");
                XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(true));
            }
        } catch (Throwable e) {
            XposedBridge.log(e);
        }
    }
}
