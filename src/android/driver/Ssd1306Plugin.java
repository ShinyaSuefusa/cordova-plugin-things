package org.apache.cordova.android.things.driver;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.android.things.contrib.driver.ssd1306.BitmapHelper;
import com.google.android.things.contrib.driver.ssd1306.Ssd1306;

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

public class Ssd1306Plugin extends CordovaPlugin {
    private Map<String, Ssd1306> deviceMap = new HashMap<>();

    @Override
    public void onDestroy() {
        for (Ssd1306 device : deviceMap.values()) {
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
                int i2cAddress = args.optInt(1, Ssd1306.I2C_ADDRESS);
                int width = args.optInt(2, 128);
                int height = args.optInt(3, 64);
                open(name, i2cAddress, width, height);
                callbackContext.success();
                return true;
            }
            if (!deviceMap.containsKey(name)) {
                callbackContext.error("not open!!");
                return false;
            }
            Ssd1306 device = deviceMap.get(name);
            if ("close".equals(action)) {
                close(device, name);
                callbackContext.success();
                return true;
            } else if ("getLcdWidth".equals(action)) {
                callbackContext.success(device.getLcdWidth());
                return true;
            } else if ("getLcdHeight".equals(action)) {
                callbackContext.success(device.getLcdHeight());
                return true;
            } else if ("clearPixels".equals(action)) {
                device.clearPixels();
                callbackContext.success();
                return true;
            } else if ("setPixel".equals(action)) {
                int x = args.optInt(1, 0);
                int y = args.optInt(2, 0);
                boolean on = args.optBoolean(3);
                device.setPixel(x, y, on);
                callbackContext.success();
                return true;
            } else if ("setContrast".equals(action)) {
                int level = args.optInt(1, 0);
                device.setContrast(level);
                callbackContext.success();
                return true;
            } else if ("setDisplayOn".equals(action)) {
                boolean on = args.optBoolean(1);
                device.setDisplayOn(on);
                callbackContext.success();
                return true;
            } else if ("show".equals(action)) {
                device.show();
                callbackContext.success();
                return true;
            } else if ("startScroll".equals(action)) {
                int startY = args.optInt(1, 0);
                int finishY = args.optInt(2, 0);
                Ssd1306.ScrollMode scrollMode = toScrollMode(args.optInt(3, 0));
                device.startScroll(startY, finishY, scrollMode);
                callbackContext.success();
                return true;
            } else if ("stopScroll".equals(action)) {
                device.stopScroll();
                callbackContext.success();
                return true;
            } else if ("setPixels".equals(action)) {
                JSONArray pixels = !args.isNull(1) ? args.optJSONArray(1) : new JSONArray();
                int index = 0;
                for (int y = 0; y < device.getLcdHeight() && index < pixels.length(); y++) {
                    for (int x = 0; x < device.getLcdWidth() && index < pixels.length(); x++) {
                        device.setPixel(x, y, pixels.getInt(index) != 0);
                        index++;
                    }
                }
                callbackContext.success();
                return true;
            } else if ("drawBitmap".equals(action)) {
                String base64Bitmap = args.getString(1);
                byte[] byteBitmap = Base64.decode(base64Bitmap, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteBitmap, 0, byteBitmap.length);
                int xOffset = args.optInt(2, 0);
                int yOffset = args.optInt(3, 0);
                boolean drawWhite = args.optBoolean(4);
                BitmapHelper.setBmpData(device, xOffset, yOffset, bitmap, drawWhite);
                bitmap.recycle();
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

    public void open(String name, int i2cAddress, int width, int height) throws IOException {
        Ssd1306 device = new Ssd1306(name, i2cAddress, width, height);
        deviceMap.put(name, device);
    }

    private void close(Ssd1306 device, String name) throws Exception {
        device.close();
        deviceMap.remove(name);
    }

    private Ssd1306.ScrollMode toScrollMode(int scrollMode) {
        switch (scrollMode) {
            case 0:
                return Ssd1306.ScrollMode.RightHorizontal;
            case 1:
                return Ssd1306.ScrollMode.LeftHorizontal;
            case 2:
                return Ssd1306.ScrollMode.VerticalRightHorizontal;
            case 3:
                return Ssd1306.ScrollMode.VerticalLeftHorizontal;
            default:
                throw new IllegalArgumentException("unknown scroll mode. [" + scrollMode + "]");
        }
    }
}
