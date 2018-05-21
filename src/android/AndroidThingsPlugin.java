package org.apache.cordova.android.things;

import android.content.Intent;

import com.google.android.things.AndroidThings;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Shinya on 2018/01/13.
 */
public class AndroidThingsPlugin extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("getVersionMajor".equals(action)) {
            //callbackContext.success(AndroidThings.getVersionMajor());
            callbackContext.error("unsupported function. [" + action + "]");
            return true;
        } else if ("getVersionMinor".equals(action)) {
            //callbackContext.success(AndroidThings.getVersionMinor());
            callbackContext.error("unsupported function. [" + action + "]");
            return true;
        } else if ("getVersionRevision".equals(action)) {
            //callbackContext.success(AndroidThings.getVersionRevision());
            callbackContext.error("unsupported function. [" + action + "]");
            return true;
        } else if ("getVersionString".equals(action)) {
            //callbackContext.success(AndroidThings.getVersionString());
            callbackContext.error("unsupported function. [" + action + "]");
            return true;
        } else if ("getVersionTag".equals(action)) {
            //callbackContext.success(AndroidThings.getVersionTag());
            callbackContext.error("unsupported function. [" + action + "]");
            return true;
        } else if ("startDefaultIoTLauncher".equals(action)) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setClassName("com.android.iotlauncher", "com.android.iotlauncher.DefaultIoTLauncher");
            ((CordovaActivity)this.cordova.getActivity()).startActivity(intent);
            callbackContext.success();
            return true;
        } else if ("startLauncherActivity".equals(action)) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            String packageName = args.getString(0);
            String className = args.getString(1);
            intent.setClassName(packageName, className);
            ((CordovaActivity)this.cordova.getActivity()).startActivity(intent);
            callbackContext.success();
            return true;
        }
        callbackContext.error("undefined function. [" + action + "]");
        return false;
    }
}
