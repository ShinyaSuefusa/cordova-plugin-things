package org.apache.cordova.android.things;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManagerService;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Shinya on 2017/12/20.
 */
public class I2cPlugin extends CordovaPlugin {

    private PeripheralManagerService service = new PeripheralManagerService();
    private Map<String, I2cDevice> deviceMap = new HashMap<>();

    @Override
    public void onDestroy() {
        for (I2cDevice device : deviceMap.values()) {
            try {
                device.close();
            } catch(IOException e) {
                // Do nothing.
            }
        }
        deviceMap.clear();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if ("getI2cBusList".equals(action)) {
            return getI2cBusList(callbackContext);
        } else if ("openI2cDevice".equals(action)) {
            String name = args.getString(0);
            int address = args.optInt(1, 0);
            return openI2cDevice(name, address, callbackContext);
        } else if ("close".equals(action)) {
            String name = args.getString(0);
            return close(name, callbackContext);
        } else if ("closeAll".equals(action)) {
            onDestroy();
            callbackContext.success();
            return true;
        } else if ("read".equals(action)) {
            String name = args.getString(0);
            int length = args.optInt(1, 1);
            return read(name, length, callbackContext);
        } else if ("readRegBuffer".equals(action)) {
            String name = args.getString(0);
            int reg = args.optInt(1, 0);
            int length = args.optInt(2, 1);
            return readRegBuffer(name, reg, length, callbackContext);
        } else if ("readRegByte".equals(action)) {
            String name = args.getString(0);
            int reg = args.optInt(1, 0);
            return readRegByte(name, reg, callbackContext);
        } else if ("readRegWord".equals(action)) {
            String name = args.getString(0);
            int reg = args.optInt(1, 0);
            return readRegWord(name, reg, callbackContext);
        } else if ("write".equals(action)) {
            String name = args.getString(0);
            JSONArray array = !args.isNull(1) ? args.optJSONArray(1) : new JSONArray();
            return write(name, array, callbackContext);
        } else if ("writeRegBuffer".equals(action)) {
            String name = args.getString(0);
            int reg = args.optInt(1, 0);
            JSONArray array = !args.isNull(2) ? args.optJSONArray(2) : new JSONArray();
            return writeRegBuffer(name, reg, array, callbackContext);
        } else if ("writeRegByte".equals(action)) {
            String name = args.getString(0);
            int reg = args.optInt(1, 0);
            int data = args.optInt(2, 0);
            return writeRegByte(name, reg, data, callbackContext);
        } else if ("writeRegWord".equals(action)) {
            String name = args.getString(0);
            int reg = args.optInt(1, 0);
            int data = args.optInt(2, 0);
            return writeRegWord(name, reg, data, callbackContext);
        }
        callbackContext.error("undefined function. [" + action + "]");
        return false;
    }

    private boolean getI2cBusList(CallbackContext callbackContext) {
        List<String> list = service.getI2cBusList();
        JSONArray array = new JSONArray();
        for (String name : list) {
            array.put(name);
        }
        callbackContext.success(array);
        return true;
    }

    private boolean openI2cDevice(String name, int address, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (deviceMap.containsKey(name)) {
            callbackContext.error("already open!!");
            return false;
        }
        try {
            I2cDevice device = service.openI2cDevice(name, address);
            deviceMap.put(name, device);
        } catch(IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackContext.success();
        return true;
    }

    private boolean close(String name, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        I2cDevice device = deviceMap.get(name);
        try {
            device.close();
        } catch(IOException e) {
            // Do nothing.
        }
        deviceMap.remove(name);
        callbackContext.success();
        return true;
    }

    private boolean read(String name, int length, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        I2cDevice device = deviceMap.get(name);
        byte[] buffer = new byte[length];
        try {
            device.read(buffer, length);
        } catch(IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        JSONArray array = new JSONArray();
        for (int data : buffer) {
            array.put(data & 0xFF);
        }
        callbackContext.success(array);
        return true;
    }

    private boolean readRegBuffer(String name, int reg, int length, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        I2cDevice device = deviceMap.get(name);
        byte[] buffer = new byte[length];
        try {
            device.readRegBuffer(reg, buffer, length);
        } catch(IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        JSONArray array = new JSONArray();
        for (int data : buffer) {
            array.put(data & 0xFF);
        }
        callbackContext.success(array);
        return true;
    }

    private boolean readRegByte(String name, int reg, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        I2cDevice device = deviceMap.get(name);
        int data;
        try {
            data = device.readRegByte(reg);
        } catch(IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackContext.success(data);
        return true;
    }

    private boolean readRegWord(String name, int reg, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        I2cDevice device = deviceMap.get(name);
        int data;
        try {
            data = device.readRegWord(reg);
        } catch(IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackContext.success(data);
        return true;
    }

    private boolean write(String name, JSONArray array, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        I2cDevice device = deviceMap.get(name);
        byte[] buffer = new byte[array.length()];
        try {
            for (int index = 0; index < array.length(); index++) {
                buffer[index] = (byte) array.getInt(index);
            }
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        try {
            device.write(buffer, buffer.length);
        } catch (IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackContext.success();
        return true;
    }

    private boolean writeRegBuffer(String name, int reg, JSONArray array, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        I2cDevice device = deviceMap.get(name);
        byte[] buffer = new byte[array.length()];
        try {
            for (int index = 0; index < array.length(); index++) {
                buffer[index] = (byte) array.getInt(index);
            }
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        try {
            device.writeRegBuffer(reg, buffer, buffer.length);
        } catch (IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackContext.success();
        return true;
    }

    private boolean writeRegByte(String name, int reg, int data, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        I2cDevice device = deviceMap.get(name);
        try {
            device.writeRegByte(reg, (byte)data);
        } catch (IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackContext.success();
        return true;
    }

    private boolean writeRegWord(String name, int reg, int data, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        I2cDevice device = deviceMap.get(name);
        try {
            device.writeRegWord(reg, (short)data);
        } catch (IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackContext.success();
        return true;
    }
}
