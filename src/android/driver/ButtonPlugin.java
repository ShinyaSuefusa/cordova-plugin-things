package org.apache.cordova.android.things.driver;

import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.contrib.driver.button.ButtonInputDriver;

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
public class ButtonPlugin extends CordovaPlugin {
    private Map<String, ButtonInputDriver> deviceMap = new HashMap<>();

    @Override
    public void onDestroy() {
        for (ButtonInputDriver device : deviceMap.values()) {
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
                Button.LogicState logicLevel = args.optInt(1, 0) == 0 ?
                        Button.LogicState.PRESSED_WHEN_LOW : Button.LogicState.PRESSED_WHEN_HIGH;
                int keycode = args.optInt(2, 0);
                open(name, logicLevel, keycode);
                callbackContext.success();
                return true;
            }
            if (!deviceMap.containsKey(name)) {
                callbackContext.error("not open!!");
                return false;
            }
            ButtonInputDriver device = deviceMap.get(name);
            if ("close".equals(action)) {
                close(device, name);
                callbackContext.success();
                return true;
            } else if ("setDebounceDelay".equals(action)) {
                long delay = args.optLong(1, 100);
                device.setDebounceDelay(delay);
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

    public void open(String name, Button.LogicState logicLevel, int keycode) throws IOException {
        ButtonInputDriver device = new ButtonInputDriver(name, logicLevel, keycode);
        deviceMap.put(name, device);
    }

    private void close(ButtonInputDriver device, String name) throws Exception {
        device.close();
        deviceMap.remove(name);
    }
}
