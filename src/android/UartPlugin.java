package org.apache.cordova.android.things;

import android.os.Handler;
import android.os.Looper;

import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Shinya on 2017/12/17.
 */
public class UartPlugin extends CordovaPlugin {

    private PeripheralManagerService service = new PeripheralManagerService();
    private Map<String, UartDevice> deviceMap = new HashMap<>();
    private Map<String, UartDeviceCallback> callbackMap = new HashMap<>();

    @Override
    public void onDestroy() {
        for (UartDevice device : deviceMap.values()) {
            try {
                device.close();
            } catch(IOException e) {
                // Do nothing.
            }
        }
        deviceMap.clear();
        callbackMap.clear();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if ("getUartDeviceList".equals(action)) {
            return getUartDeviceList(callbackContext);
        } else if ("openUartDevice".equals(action)) {
            String name = args.getString(0);
            return openUartDevice(name, callbackContext);
        } else if ("close".equals(action)) {
            String name = args.getString(0);
            return close(name, callbackContext);
        } else if ("closeAll".equals(action)) {
            onDestroy();
            callbackContext.success();
            return true;
        } else if ("flush".equals(action)) {
            String name = args.getString(0);
            int direction = args.optInt(1, UartDevice.FLUSH_IN_OUT);
            return flush(name, direction, callbackContext);
        } else if ("read".equals(action)) {
            String name = args.getString(0);
            int length = args.optInt(1, 1);
            return read(name, length, callbackContext);
        } else if ("sendBreak".equals(action)) {
            String name = args.getString(0);
            int durationMsec = args.optInt(1, 0);
            return sendBreak(name, durationMsec, callbackContext);
        } else if ("setBaudrate".equals(action)) {
            String name = args.getString(0);
            int rate = args.optInt(1, 0);
            return setBaudrate(name, rate, callbackContext);
        } else if ("setDataSize".equals(action)) {
            String name = args.getString(0);
            int size = args.optInt(1, 0);
            return setDataSize(name, size, callbackContext);
        } else if ("setHardwareFlowControl".equals(action)) {
            String name = args.getString(0);
            int mode = args.optInt(1, UartDevice.HW_FLOW_CONTROL_AUTO_RTSCTS);
            return setHardwareFlowControl(name, mode, callbackContext);
        } else if ("setModemControl".equals(action)) {
            String name = args.getString(0);
            int lines = args.optInt(1, 0);
            return setModemControl(name, lines, callbackContext);
        } else if ("setParity".equals(action)) {
            String name = args.getString(0);
            int mode = args.optInt(1, 0);
            return setParity(name, mode, callbackContext);
        } else if ("setStopBits".equals(action)) {
            String name = args.getString(0);
            int bits = args.optInt(1, 0);
            return setStopBits(name, bits, callbackContext);
        } else if ("write".equals(action)) {
            String name = args.getString(0);
            JSONArray array = !args.isNull(1) ? args.optJSONArray(1) : new JSONArray();
            return write(name, array, callbackContext);
        } else if ("registerUartDeviceCallback".equals(action)) {
            String name = args.getString(0);
            return registerUartDeviceCallback(name, callbackContext);
        } else if ("unregisterUartDeviceCallback".equals(action)) {
            String name = args.getString(0);
            return unregisterUartDeviceCallback(name, callbackContext);
        }
        callbackContext.error("undefined function. [" + action + "]");
        return false;
    }

    private boolean getUartDeviceList(CallbackContext callbackContext) {
        List<String> list = service.getUartDeviceList();
        JSONArray array = new JSONArray();
        for (String name : list) {
            array.put(name);
        }
        callbackContext.success(array);
        return true;
    }

    private boolean openUartDevice(String name, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (deviceMap.containsKey(name)) {
            callbackContext.error("already open!!");
            return false;
        }
        try {
            UartDevice device = service.openUartDevice(name);
            deviceMap.put(name, device);
        } catch (IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackContext.success();
        return true;
    }

    private boolean close(String name, CallbackContext callbackContext) {
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        UartDevice device = deviceMap.get(name);
        try {
            device.close();
        } catch (IOException e) {
            // Do nothing.
        }
        deviceMap.remove(name);
        callbackMap.remove(name);
        callbackContext.success();
        return true;
    }

    private boolean flush(String name, int direction, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        UartDevice device = deviceMap.get(name);
        try {
            device.flush(direction);
        } catch (IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
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
        UartDevice device = deviceMap.get(name);
        byte[] buffer = new byte[length];
        try {
            length = device.read(buffer, length);
        } catch(IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        JSONArray array = new JSONArray();
        for (int index = 0; index < length; index++) {
            array.put(buffer[index] & 0xFF);
        }
        callbackContext.success(array);
        return true;
    }

    private boolean sendBreak(String name, int durationMsec, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        UartDevice device = deviceMap.get(name);
        try {
            device.sendBreak(durationMsec);
        } catch (IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackContext.success();
        return true;
    }

    private boolean setBaudrate(String name, int rate, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        UartDevice device = deviceMap.get(name);
        try {
            device.setBaudrate(rate);
        } catch (IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackContext.success();
        return true;
    }

    private boolean setDataSize(String name, int size, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        UartDevice device = deviceMap.get(name);
        try {
            device.setDataSize(size);
        } catch (IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackContext.success();
        return true;
    }

    private boolean setHardwareFlowControl(String name, int mode, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        UartDevice device = deviceMap.get(name);
        try {
            device.setHardwareFlowControl(mode);
        } catch (IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackContext.success();
        return true;
    }

    private boolean setModemControl(String name, int lines, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        UartDevice device = deviceMap.get(name);
        try {
            device.setModemControl(lines);
        } catch (IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackContext.success();
        return true;
    }

    private boolean setParity(String name, int mode, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        UartDevice device = deviceMap.get(name);
        try {
            device.setParity(mode);
        } catch (IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackContext.success();
        return true;
    }

    private boolean setStopBits(String name, int bits, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        UartDevice device = deviceMap.get(name);
        try {
            device.setStopBits(bits);
        } catch (IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackContext.success();
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
        UartDevice device = deviceMap.get(name);
        byte[] buffer = new byte[array.length()];
        try {
            for (int index = 0; index < array.length(); index++) {
                buffer[index] = (byte) array.getInt(index);
            }
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        int result;
        try {
            result = device.write(buffer, buffer.length);
        } catch (IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackContext.success(result);
        return true;
    }

    private boolean registerUartDeviceCallback(String name, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        if (callbackMap.containsKey(name)) {
            callbackContext.error("already registered!!");
            return false;
        }
        UartDevice device = deviceMap.get(name);
        final UartPlugin plugin = this;
        final Handler handler = new Handler(Looper.getMainLooper());
        UartDeviceCallback callback = new UartDeviceCallback() {
            UartPlugin uartPlugin = plugin;
            @Override
            public boolean onUartDeviceDataAvailable(UartDevice uart) {
                uartPlugin.webView.getEngine().evaluateJavascript("(function() {cordova.plugins.things.uart.callback('"+uart.getName()+"');})();", null);
                return super.onUartDeviceDataAvailable(uart);
            }
        };
        try {
            device.registerUartDeviceCallback(callback, handler);
        } catch (IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackMap.put(name, callback);
        callbackContext.success();
        return true;
    }

    private boolean unregisterUartDeviceCallback(String name, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        if (!callbackMap.containsKey(name)) {
            callbackContext.error("not registered!!");
            return false;
        }
        UartDevice device = deviceMap.get(name);
        UartDeviceCallback callback = callbackMap.get(name);
        device.unregisterUartDeviceCallback(callback);
        callbackMap.remove(name);
        callbackContext.success();
        return true;
    }
}
