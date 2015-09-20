package com.pyler.nodevicecheck;

import org.json.JSONObject;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class NoDeviceCheck implements IXposedHookLoadPackage {
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if ("android".equals(lpparam.packageName)) {
            		XposedHelpers.findAndHookMethod(File.class, "exists",
                    		new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param)
                                throws Throwable {
                            File file = (File) param.thisObject;
                            if (new File("/sys/fs/selinux/enforce").equals(file)) {
                                param.setResult(true);
                                return;
                            }

                            if (new File("/system/bin/su").equals(file) || new File("/system/xbin/su").equals(file)) {
                                param.setResult(false);
                                return;
                            }
                        }
                    });
		}
		
		XposedHelpers.findAndHookMethod(JSONObject.class, "getBoolean",
				String.class, new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param)
							throws Throwable {
						String name = (String) param.args[0];
						if ("ctsProfileMatch".equals(name)
								|| "isValidSignature".equals(name)) {
							param.setResult(true);
							return;
						}
					}
				});
	}
}
