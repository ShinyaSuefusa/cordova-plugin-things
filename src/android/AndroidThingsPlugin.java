package org.apache.cordova.android.things;

import com.google.android.things.AndroidThings;

import org.apache.cordova.CallbackContext;
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
            callbackContext.success(AndroidThings.getVersionMajor());
            return true;
        } else if ("getVersionMinor".equals(action)) {
            callbackContext.success(AndroidThings.getVersionMinor());
            return true;
        } else if ("getVersionRevision".equals(action)) {
            callbackContext.success(AndroidThings.getVersionRevision());
            return true;
        } else if ("getVersionString".equals(action)) {
            callbackContext.success(AndroidThings.getVersionString());
            return true;
        } else if ("getVersionTag".equals(action)) {
            callbackContext.success(AndroidThings.getVersionTag());
            return true;
        }
        callbackContext.error("undefined function. [" + action + "]");
        return false;
    }
}
