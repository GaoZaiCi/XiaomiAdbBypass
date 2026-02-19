package com.xiaomi.adb.bypass;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

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
            if (Objects.equals(lp.packageName, "com.android.settings")) {
                startHookBySettings(lp.classLoader);
            }
        } catch (Throwable e) {
            XposedBridge.log(e);
        }
    }

    private void startHookBySecurityCenter(ClassLoader classLoader) {
        try {
            Class<?> clazz = classLoader.loadClass("com.miui.common.persistence.RemoteProvider");
            Method method = clazz.getDeclaredMethod("a", String.class, boolean.class);
            XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(null));
        } catch (Throwable e) {
            XposedBridge.log(e);
        }
        try {
            Class<?> clazz = classLoader.loadClass("com.miui.permcenter.install.c");
            {
                Method method = clazz.getDeclaredMethod("a", boolean.class);
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.args[0] = true;
                    }
                });
            }
            {
                Method method = clazz.getDeclaredMethod("b", boolean.class);
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.args[0] = false;
                    }
                });
            }
            {
                Method method = clazz.getDeclaredMethod("e");
                XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(false));
            }
        } catch (Throwable e) {
            XposedBridge.log(e);
        }
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
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Activity activity = (Activity) param.thisObject;
                        var intent = activity.getIntent();
                        boolean isInput = intent.getBooleanExtra("is_input", false);
                        if (isInput) {
                            {
                                Class<?> superClass = activity.getClass().getSuperclass();
                                Method onCreate = null;
                                while (true) {
                                    if (superClass == null) {
                                        break;
                                    }
                                    onCreate = superClass.getDeclaredMethod("onCreate", Bundle.class);
                                    if (onCreate != method) {
                                        break;
                                    }
                                    superClass = superClass.getSuperclass();
                                }
                                if (onCreate != null) {
                                    onCreate.setAccessible(true);
                                    onCreate.invoke(activity, (Bundle) param.args[0]);
                                }
                            }
                            {
                                var method = activity.getClass().getDeclaredMethod("K0");
                                method.setAccessible(true);
                                method.invoke(activity);
                            }
                            activity.finish();
                            param.setResult(null);
                        }
                    }
                });
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
        try {
            Class<?> clazz = classLoader.loadClass("com.android.settings.development.EnableAdbWarningDialog");
            {
                Method method = clazz.getMethod("show", Fragment.class);
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("EnableAdbWarningDialog.show()");
                    }
                });
            }
        } catch (Throwable e) {
            XposedBridge.log(e);
        }
        try {
            Class<?> clazz = classLoader.loadClass("com.android.settingslib.development.AbstractEnableAdbPreferenceController");
            Class<?> mPreference = classLoader.loadClass("androidx.preference.Preference");
            {
                Method method = clazz.getDeclaredMethod("updateEnableAdbPreference", mPreference, boolean.class);
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("AbstractEnableAdbPreferenceController.updateEnableAdbPreference() " + param.args[1]);
                    }
                });
            }
            {
                Method method = clazz.getDeclaredMethod("onPreferenceChange", mPreference, Object.class);
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("AbstractEnableAdbPreferenceController.onPreferenceChange() " + param.args[1]);
                    }
                });
            }
        } catch (Throwable e) {
            XposedBridge.log(e);
        }
    }
}
