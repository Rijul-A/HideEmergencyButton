package me.rijul.hideemergencybutton;

import android.os.Build;
import android.view.View;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by rijul on 14/2/16.
 */
public class XposedMod implements IXposedHookLoadPackage {
    XC_MethodHook updateEmergencyCallButtonHook = new XC_MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
            XposedHelpers.callMethod(param.thisObject, "setVisibility", View.GONE);
            param.setResult(null);
        }
    };

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return;
        if (lpparam.packageName.equals("com.htc.lockscreen")) {
           hookMethod("com.htc.lockscreen.keyguard", lpparam);
        }
        else if ((lpparam.packageName.contains("android.keyguard")) || (lpparam.packageName.contains("com.android.systemui"))) {
            hookMethod("com.android.keyguard", lpparam);
        }
    }

    private void hookMethod(String keyguardPackageName, XC_LoadPackage.LoadPackageParam lpparam) {
        Class<?> emergencyButton = XposedHelpers.findClass(keyguardPackageName + ".EmergencyButton", lpparam.classLoader);
        XposedBridge.hookAllMethods(emergencyButton, "updateEmergencyCallButton", updateEmergencyCallButtonHook);
    }
}
