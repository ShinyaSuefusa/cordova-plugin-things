package org.apache.cordova.android.things.driver;

import com.google.android.things.contrib.driver.matrixkeypad.MatrixKeypadInputDriver;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shinya on 2018/01/17.
 */

public class MatrixKeypadPlugin extends CordovaPlugin {
    private Map<String, MatrixKeypadInputDriver> deviceMap = new HashMap<>();

    @Override
    public void onDestroy() {
        for (MatrixKeypadInputDriver device : deviceMap.values()) {
            try {
                device.close();
            } catch(Exception e) {
                // Do nothing.
            }
        }
        deviceMap.clear();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("closeAll".equals(action)) {
            onDestroy();
            callbackContext.success();
            return true;
        }

        String name = args.getString(0);
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        try {
            if ("open".equals(action)) {
                if (deviceMap.containsKey(name)) {
                    callbackContext.error("already open!!");
                    return false;
                }
                String[] rowPins = !args.isNull(1) ?
                        toStringArray(args.optJSONArray(1)) : new String[0];
                String[] colPins = !args.isNull(2) ?
                        toStringArray(args.optJSONArray(2)) : new String[0];
                int[] keyCodes = !args.isNull(3) ?
                        toIntArray(args.optJSONArray(3)) : new int[0];
                open(name, rowPins, colPins, keyCodes);
                callbackContext.success();
                return true;
            }
            if (!deviceMap.containsKey(name)) {
                callbackContext.error("not open!!");
                return false;
            }
            MatrixKeypadInputDriver device = deviceMap.get(name);
            if ("close".equals(action)) {
                close(device, name);
                callbackContext.success();
                return true;
            } else if ("register".equals(action)) {
                device.register();
                callbackContext.success();
                return true;
            } else if ("unregister".equals(action)) {
                device.unregister();
                callbackContext.success();
                return true;
            }
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackContext.error("undefined function. [" + action + "]");
        return false;
    }

    public void open(String name, String[] rowPins, String[] colPins, int[] keyCodes) throws IOException {
        MatrixKeypadInputDriver device = new MatrixKeypadInputDriver(rowPins, colPins, keyCodes);
        deviceMap.put(name, device);
    }

    private void close(MatrixKeypadInputDriver device, String name) throws Exception {
        device.close();
        deviceMap.remove(name);
    }

    private String[] toStringArray(JSONArray jsonArray) throws JSONException {
        String[] stringArray = new String[jsonArray.length()];
        for (int index = 0; index <jsonArray.length(); index++) {
            stringArray[index] = jsonArray.getString(index);
        }
        return stringArray;
    }

    private int[] toIntArray(JSONArray jsonArray) throws JSONException {
        int[] intArray = new int[jsonArray.length()];
        for (int index = 0; index <jsonArray.length(); index++) {
            intArray[index] = jsonArray.getInt(index);
        }
        return intArray;
    }
}
