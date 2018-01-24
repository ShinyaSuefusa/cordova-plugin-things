package org.apache.cordova.android.things.driver;

import com.nilhcem.androidthings.driver.lcdpcf8574.LcdPcf8574;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shinya on 2017/12/25.
 */

public class LcdPcf8574Plugin extends CordovaPlugin {
    private Map<String, LcdPcf8574> deviceMap = new HashMap<>();

    @Override
    public void onDestroy() {
        for (LcdPcf8574 device : deviceMap.values()) {
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
                int address = args.optInt(1, 0);
                open(name, address);
                callbackContext.success();
                return true;
            }
            if (!deviceMap.containsKey(name)) {
                callbackContext.error("not open!!");
                return false;
            }
            LcdPcf8574 device = deviceMap.get(name);
            if ("close".equals(action)) {
                close(device, name);
                callbackContext.success();
                return true;
            } else if ("begin".equals(action)) {
                int cols = args.optInt(1, 16);
                int rows = args.optInt(2, 2);
                int charsize = args.optInt(3, 0x00);
                device.begin(cols, rows, charsize);
                callbackContext.success();
                return true;
            } else if ("clear".equals(action)) {
                device.clear();
                callbackContext.success();
                return true;
            } else if ("home".equals(action)) {
                device.home();
                callbackContext.success();
                return true;
            } else if ("setCursor".equals(action)) {
                int col = args.optInt(1, 0);
                int row = args.optInt(2, 0);
                device.setCursor(col, row);
                callbackContext.success();
                return true;
            } else if ("noDisplay".equals(action)) {
                device.noDisplay();
                callbackContext.success();
                return true;
            } else if ("display".equals(action)) {
                device.display();
                callbackContext.success();
                return true;
            } else if ("noBlink".equals(action)) {
                device.noBlink();
                callbackContext.success();
                return true;
            } else if ("blink".equals(action)) {
                device.blink();
                callbackContext.success();
                return true;
            } else if ("noCursor".equals(action)) {
                device.noCursor();
                callbackContext.success();
                return true;
            } else if ("cursor".equals(action)) {
                device.cursor();
                callbackContext.success();
                return true;
            } else if ("scrollDisplayLeft".equals(action)) {
                device.scrollDisplayLeft();
                callbackContext.success();
                return true;
            } else if ("scrollDisplayRight".equals(action)) {
                device.scrollDisplayRight();
                callbackContext.success();
                return true;
            } else if ("leftToRight".equals(action)) {
                device.leftToRight();
                callbackContext.success();
                return true;
            } else if ("rightToLeft".equals(action)) {
                device.rightToLeft();
                callbackContext.success();
                return true;
            } else if ("autoscroll".equals(action)) {
                device.autoscroll();
                callbackContext.success();
                return true;
            } else if ("noAutoscroll".equals(action)) {
                device.noAutoscroll();
                callbackContext.success();
                return true;
            } else if ("setBacklight".equals(action)) {
                boolean enable = args.optBoolean(1, true);
                device.setBacklight(enable);
                callbackContext.success();
                return true;
            } else if ("createChar".equals(action)) {
                int location = args.optInt(1, 0);
                JSONArray array = !args.isNull(2) ? args.optJSONArray(2) : new JSONArray();
                int[] charmap = new int[array.length()];
                for (int index = 0; index < array.length(); index++) {
                    charmap[index] = array.getInt(index);
                }
                device.createChar(location, charmap);
                callbackContext.success();
                return true;
            } else if ("write".equals(action)) {
                int value = args.optInt(1, 0);
                device.write(value);
                callbackContext.success();
                return true;
            } else if ("print".equals(action)) {
                String message = args.optString(1);
                device.print(message);
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

    public void open(String name, int address) throws IOException {
        LcdPcf8574 device = new LcdPcf8574(name, address);
        deviceMap.put(name, device);
    }

    private void close(LcdPcf8574 device, String name) throws Exception {
        device.close();
        deviceMap.remove(name);
    }
}
