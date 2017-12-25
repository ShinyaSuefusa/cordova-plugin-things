package org.apache.cordova.things;

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
    private Map<String, LcdPcf8574> lcdMap = new HashMap<String, LcdPcf8574>();

    @Override
    public void onDestroy() {
        for (LcdPcf8574 lcdDevice : lcdMap.values()) {
            try {
                lcdDevice.close();
            } catch(Exception e) {
                // Do nothing.
            }
        }
        lcdMap.clear();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        String name = args.length() > 0 ? args.getString(0) : null;
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        try {
            if ("open".equals(action)) {
                if (lcdMap.containsKey(name)) {
                    callbackContext.error("already open!!");
                    return false;
                }
                int address = args.length() > 1 ? args.getInt(1) : 0;
                open(name, address);
                callbackContext.success();
                return true;
            }
            if (!lcdMap.containsKey(name)) {
                callbackContext.error("not open!!");
                return false;
            }
            LcdPcf8574 lcdDevice = lcdMap.get(name);
            if ("close".equals(action)) {
                close(lcdDevice, name);
                callbackContext.success();
                return true;
            } else if ("begin".equals(action)) {
                int cols = args.length() > 1 ? args.getInt(1) : 16;
                int rows = args.length() > 2 ? args.getInt(2) : 2;
                int charsize = args.length() > 3 ? args.getInt(3) : 0x00;
                lcdDevice.begin(cols, rows, charsize);
                callbackContext.success();
                return true;
            } else if ("clear".equals(action)) {
                lcdDevice.clear();
                callbackContext.success();
                return true;
            } else if ("home".equals(action)) {
                lcdDevice.home();
                callbackContext.success();
                return true;
            } else if ("setCursor".equals(action)) {
                int col = args.length() > 1 ? args.getInt(1) : 0;
                int row = args.length() > 2 ? args.getInt(2) : 0;
                lcdDevice.setCursor(col, row);
                callbackContext.success();
                return true;
            } else if ("noDisplay".equals(action)) {
                lcdDevice.noDisplay();
                callbackContext.success();
                return true;
            } else if ("display".equals(action)) {
                lcdDevice.display();
                callbackContext.success();
                return true;
            } else if ("noBlink".equals(action)) {
                lcdDevice.noBlink();
                callbackContext.success();
                return true;
            } else if ("blink".equals(action)) {
                lcdDevice.blink();
                callbackContext.success();
                return true;
            } else if ("noCursor".equals(action)) {
                lcdDevice.noCursor();
                callbackContext.success();
                return true;
            } else if ("cursor".equals(action)) {
                lcdDevice.cursor();
                callbackContext.success();
                return true;
            } else if ("scrollDisplayLeft".equals(action)) {
                lcdDevice.scrollDisplayLeft();
                callbackContext.success();
                return true;
            } else if ("scrollDisplayRight".equals(action)) {
                lcdDevice.scrollDisplayRight();
                callbackContext.success();
                return true;
            } else if ("leftToRight".equals(action)) {
                lcdDevice.leftToRight();
                callbackContext.success();
                return true;
            } else if ("rightToLeft".equals(action)) {
                lcdDevice.rightToLeft();
                callbackContext.success();
                return true;
            } else if ("autoscroll".equals(action)) {
                lcdDevice.autoscroll();
                callbackContext.success();
                return true;
            } else if ("noAutoscroll".equals(action)) {
                lcdDevice.noAutoscroll();
                callbackContext.success();
                return true;
            } else if ("setBacklight".equals(action)) {
                boolean enable = args.length() > 1 ? args.getBoolean(1) : true;
                lcdDevice.setBacklight(enable);
                callbackContext.success();
                return true;
            } else if ("createChar".equals(action)) {
                int location = args.length() > 1 ? args.getInt(1) : 0;
                JSONArray array = args.length() > 2 ? args.getJSONArray(2) : new JSONArray();
                int[] charmap = new int[array.length()];
                for (int index = 0; index < array.length(); index++) {
                    charmap[index] = array.getInt(index);
                }
                lcdDevice.createChar(location, charmap);
                callbackContext.success();
                return true;
            } else if ("write".equals(action)) {
                int value = args.length() > 1 ? args.getInt(1) : 0;
                lcdDevice.write(value);
                callbackContext.success();
                return true;
            } else if ("print".equals(action)) {
                String message = args.length() > 1 ? args.getString(1) : "";
                lcdDevice.print(message);
                callbackContext.success();
                return true;
            }
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        return false;
    }

    public void open(String name, int address) throws IOException {
        LcdPcf8574 lcdDevice = new LcdPcf8574(name, address);
        lcdMap.put(name, lcdDevice);
    }

    private void close(LcdPcf8574 lcdDevice, String name) throws Exception {
        lcdDevice.close();
        lcdMap.remove(name);
    }
}
