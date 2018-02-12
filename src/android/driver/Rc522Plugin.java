package org.apache.cordova.android.things.driver;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.android.things.driver.wrapper.Rc522Wrapper;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shinya on 2018/01/31.
 */
public class Rc522Plugin extends CordovaPlugin {
    private Map<String, Rc522Wrapper> deviceMap = new HashMap<>();

    @Override
    public void onDestroy() {
        for (Rc522Wrapper device : deviceMap.values()) {
            try {
                device.close();
            } catch (Exception e) {
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

        String name = args.optString(0);
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
                String spiName = args.optString(1, "");
                String gpioName = args.optString(2, "");
                open(name, spiName, gpioName);
                callbackContext.success();
                return true;
            }
            if (!deviceMap.containsKey(name)) {
                callbackContext.error("not open!!");
                return false;
            }
            Rc522Wrapper device = deviceMap.get(name);
            if ("close".equals(action)) {
                close(device, name);
                callbackContext.success();
                return true;
            } else if ("setDebugging".equals(action)) {
                Boolean debugging = args.optBoolean(1, false);
                device.setDebugging(debugging);
                callbackContext.success();
                return true;
            } else if ("getUid".equals(action)) {
                byte[] result = device.getUid();
                callbackContext.success(toJsonArray(result));
                return true;
            } else if ("getUidString".equals(action)) {
                String separator = args.optString(2, null);
                String result = separator == null ? device.getUidString() : device.getUidString(separator);
                callbackContext.success(result);
                return true;
            } else if ("request".equals(action)) {
                boolean result = device.request();
                if (result) {
                    callbackContext.success();
                } else {
                    callbackContext.error("request failed.");
                }
                return true;
            } else if ("requestMode".equals(action)) {
                byte requestMode = (byte)args.optInt(1,0);
                boolean result = device.request(requestMode);
                if (result) {
                    callbackContext.success();
                } else {
                    callbackContext.error("request failed.");
                }
                return true;
            } else if ("antiCollisionDetect".equals(action)) {
                boolean result = device.antiCollisionDetect();
                if (result) {
                    callbackContext.success();
                } else {
                    callbackContext.error("collision detected.");
                }
                return true;
            } else if ("selectTag".equals(action)) {
                byte[] selectTag = toByteArray(args.optJSONArray(1));
                boolean result = device.selectTag(selectTag);
                if (result) {
                    callbackContext.success();
                } else {
                    callbackContext.error("select failed.");
                }
                return true;
            } else if ("authenticateCard".equals(action)) {
                byte authMode = (byte)args.optInt(1, 0);
                byte address = (byte)args.optInt(2, 0);
                byte[] key = toByteArray(args.optJSONArray(3));
                boolean result = device.authenticateCard(authMode, address, key);
                if (result) {
                    callbackContext.success();
                } else {
                    callbackContext.error("authenticate failed.");
                }
                return true;
            } else if ("stopCrypto".equals(action)) {
                device.stopCrypto();
                callbackContext.success();
                return true;
            } else if ("readBlock".equals(action)) {
                byte address = (byte)args.optInt(1, 0);
                byte[] buffer = new byte[16];
                boolean result = device.readBlock(address, buffer);
                if (result) {
                    callbackContext.success(toJsonArray(buffer));
                } else {
                    callbackContext.error("readBlock failed.");
                }
                return true;
            } else if ("writeBlock".equals(action)) {
                byte address = (byte)args.optInt(1, 0);
                byte[] data = toByteArray(args.optJSONArray(2));
                boolean result = device.writeBlock(address, data);
                if (result) {
                    callbackContext.success();
                } else {
                    callbackContext.error("writeBlock failed.");
                }
                return true;
            } else if ("increaseBlock".equals(action)) {
                byte address = (byte)args.optInt(1, 0);
                int operand = args.optInt(2,0);
                boolean result = device.increaseBlock(address, operand);
                if (result) {
                    callbackContext.success();
                } else {
                    callbackContext.error("increaseBlock failed.");
                }
                return true;
            } else if ("decreaseBlock".equals(action)) {
                byte address = (byte)args.optInt(1, 0);
                int operand = args.optInt(2,0);
                boolean result = device.decreaseBlock(address, operand);
                if (result) {
                    callbackContext.success();
                } else {
                    callbackContext.error("decreaseBlock failed.");
                }
                return true;
            } else if ("transferBlock".equals(action)) {
                byte address = (byte)args.optInt(1, 0);
                boolean result = device.transferBlock(address);
                if (result) {
                    callbackContext.success();
                } else {
                    callbackContext.error("transferBlock failed.");
                }
                return true;
            } else if ("restoreBlock".equals(action)) {
                byte address = (byte)args.optInt(1, 0);
                boolean result = device.restoreBlock(address);
                if (result) {
                    callbackContext.success();
                } else {
                    callbackContext.error("restoreBlock failed.");
                }
                return true;
            } else if ("writeValue".equals(action)) {
                byte address = (byte)args.optInt(1, 0);
                int value = args.optInt(2, 0);
                boolean result = device.writeValue(address, value);
                if (result) {
                    callbackContext.success();
                } else {
                    callbackContext.error("writeValue failed.");
                }
                return true;
            } else if ("readValue".equals(action)) {
                byte address = (byte)args.optInt(1, 0);
                Integer result = device.readValue(address);
                if (result != null) {
                    callbackContext.success(result);
                } else {
                    callbackContext.error("readValue failed.");
                }
                return true;
            } else if ("writeTrailer".equals(action)) {
                byte sector = (byte)args.optInt(1,0);
                byte[] keyA = toByteArray(args.optJSONArray(2));
                byte[] accessBits = toByteArray(args.optJSONArray(3));
                byte userData = (byte)args.optInt(4, 0);
                byte[] keyB = toByteArray(args.optJSONArray(5));
                boolean result = device.writeTrailer(sector, keyA, accessBits, userData, keyB);
                if (result) {
                    callbackContext.success();
                } else {
                    callbackContext.error("writeTrailer failed.");
                }
                return true;
            } else if ("dumpMifare1k".equals(action)) {
                String result = device.dumpMifare1k();
                callbackContext.success(result);
                return true;
            }
        } catch(Exception e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackContext.error("undefined function. [" + action + "]");
        return false;
    }

    public void open(String name, String spiName, String gpioName) throws IOException {
        Rc522Wrapper device = new Rc522Wrapper(spiName, gpioName);
        deviceMap.put(name, device);
    }

    private void close(Rc522Wrapper device, String name) throws Exception {
        device.close();
        deviceMap.remove(name);
    }

    private JSONArray toJsonArray(byte[] data) throws  JSONException {
        JSONArray jsonArray = new JSONArray();
        if (data == null) {
            data = new byte[0];
        }
        for (byte b : data) {
            jsonArray.put((int)b);
        }
        return jsonArray;
    }

    private byte[] toByteArray(JSONArray jsonArray) throws JSONException {
        if (jsonArray == null) {
            jsonArray = new JSONArray();
        }
        byte[] byteArray = new byte[jsonArray.length()];
        for (int index = 0; index <jsonArray.length(); index++) {
            byteArray[index] = (byte)jsonArray.getInt(index);
        }
        return byteArray;
    }
}
