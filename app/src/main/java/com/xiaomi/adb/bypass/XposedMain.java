package com.xiaomi.adb.bypass;

import android.app.Fragment;
import android.content.Context;

import androidx.annotation.Keep;


import java.lang.reflect.Method;
import java.util.Objects;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/** @noinspection UnreachableCode*/
@Keep
public class XposedMain implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lp) throws Throwable {
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

    private void startHookBySecurityCenter(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        {
            Class<?> clazz = classLoader.loadClass("com.miui.common.persistence.RemoteProvider");
            Method method = clazz.getDeclaredMethod("a", String.class, boolean.class);
            XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(null));
        }
        {
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
        }
    }

    private void startHookBySettings(ClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException {
        {
            Class<?> clazz = classLoader.loadClass("com.android.security.AdbUtils");
            {
                Method method = clazz.getDeclaredMethod("isInstallEnabled", Context.class);
                XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(true));
            }
            {
                Method method = clazz.getDeclaredMethod("isInputEnabled");
                XposedBridge.hookMethod(method, XC_MethodReplacement.returnConstant(true));
            }
        }
        {
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
        }
        {
            Class<?> clazz = classLoader.loadClass("com.android.settingslib.development.AbstractEnableAdbPreferenceController");
            Class<?> mPreference = classLoader.loadClass("androidx.preference.Preference");
            {
                Method method = clazz.getDeclaredMethod("updateEnableAdbPreference", mPreference,boolean.class);
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("AbstractEnableAdbPreferenceController.updateEnableAdbPreference() " + param.args[1]);
                    }
                });
            }
            {
                Method method = clazz.getDeclaredMethod("onPreferenceChange", mPreference,Object.class);
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("AbstractEnableAdbPreferenceController.onPreferenceChange() " + param.args[1]);
                    }
                });
            }
        }
    }
}
