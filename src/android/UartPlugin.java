package org.apache.cordova.things;

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
    private Map<String, UartDevice> uartMap = new HashMap<String, UartDevice>();
    private Map<String, UartDeviceCallback> callbackMap = new HashMap<String, UartDeviceCallback>();

    @Override
    public void onDestroy() {
        for (String key : uartMap.keySet()) {
            try {
                uartMap.get(key).close();
            } catch(IOException e) {
                // Do nothing.
            }
        }
        uartMap.clear();
        callbackMap.clear();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if ("getUartDeviceList".equals(action)) {
            return getUartDeviceList(callbackContext);
        } else if ("openUartDevice".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            Integer rate = args.length() > 1 ? args.getInt(1) : null;
            return openUartDevice(name, rate, callbackContext);
        } else if ("close".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            return close(name, callbackContext);
        } else if ("flush".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            int direction = args.length() > 1 ? args.getInt(1) : UartDevice.FLUSH_IN_OUT;
            return flush(name, direction, callbackContext);
        } else if ("read".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            int length = args.length() > 1 ? args.getInt(1) : 1;
            return read(name, length, callbackContext);
        } else if ("sendBreak".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            int duration_msec = args.length() > 1 ? args.getInt(1) : 0;
            return sendBreak(name, duration_msec, callbackContext);
        } else if ("setBaudrate".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            int rate = args.length() > 1 ? args.getInt(1) : 0;
            return setBaudrate(name, rate, callbackContext);
        } else if ("setDataSize".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            int size = args.length() > 1 ? args.getInt(1) : 0;
            return setDataSize(name, size, callbackContext);
        } else if ("setHardwareFlowControl".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            int mode = args.length() > 1 ? args.getInt(1) : UartDevice.HW_FLOW_CONTROL_AUTO_RTSCTS;
            return setHardwareFlowControl(name, mode, callbackContext);
        } else if ("setModemControl".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            int lines = args.length() > 1 ? args.getInt(1) : 0;
            return setModemControl(name, lines, callbackContext);
        } else if ("setParity".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            int mode = args.length() > 1 ? args.getInt(1) : 0;
            return setParity(name, mode, callbackContext);
        } else if ("setStopBits".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            int bits = args.length() > 1 ? args.getInt(1) : 0;
            return setStopBits(name, bits, callbackContext);
        } else if ("write".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            JSONArray array = args.length() > 1 ? args.getJSONArray(1) : new JSONArray();
            return write(name, array, callbackContext);
        } else if ("registerUartDeviceCallback".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            return registerUartDeviceCallback(name, callbackContext);
        } else if ("unregisterUartDeviceCallback".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            return unregisterUartDeviceCallback(name, callbackContext);
        }

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

    private boolean openUartDevice(String name, Integer rate, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (uartMap.containsKey(name)) {
            callbackContext.error("already open!!");
            return false;
        }
        try {
            UartDevice uart = service.openUartDevice(name);
            if (rate != null) {
                uart.setBaudrate(rate);
            }
            uartMap.put(name, uart);
        } catch (IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackContext.success();
        return true;
    }

    private boolean close(String name, CallbackContext callbackContext) {
        if (!uartMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        UartDevice uart = uartMap.get(name);
        try {
            uart.close();
        } catch (IOException e) {
            // Do nothing.
        }
        uartMap.remove(name);
        callbackMap.remove(name);
        callbackContext.success();
        return true;
    }

    private boolean flush(String name, int direction, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!uartMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        UartDevice uart = uartMap.get(name);
        try {
            uart.flush(direction);
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
        if (!uartMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        UartDevice uart = uartMap.get(name);
        byte[] buffer = new byte[length];
        try {
            length = uart.read(buffer, length);
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

    private boolean sendBreak(String name, int duration_msec, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!uartMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        UartDevice uart = uartMap.get(name);
        try {
            uart.sendBreak(duration_msec);
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
        if (!uartMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        UartDevice uart = uartMap.get(name);
        try {
            uart.setBaudrate(rate);
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
        if (!uartMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        UartDevice uart = uartMap.get(name);
        try {
            uart.setDataSize(size);
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
        if (!uartMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        UartDevice uart = uartMap.get(name);
        try {
            uart.setHardwareFlowControl(mode);
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
        if (!uartMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        UartDevice uart = uartMap.get(name);
        try {
            uart.setModemControl(lines);
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
        if (!uartMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        UartDevice uart = uartMap.get(name);
        try {
            uart.setParity(mode);
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
        if (!uartMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        UartDevice uart = uartMap.get(name);
        try {
            uart.setStopBits(bits);
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
        if (!uartMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        UartDevice uart = uartMap.get(name);
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
            result = uart.write(buffer, buffer.length);
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
        if (!uartMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        if (callbackMap.containsKey(name)) {
            callbackContext.error("already registered!!");
            return false;
        }
        UartDevice uart = uartMap.get(name);
        final UartPlugin plugin = this;
        final Handler handler = new Handler(Looper.getMainLooper());
        UartDeviceCallback deviceCallback = new UartDeviceCallback() {
            UartPlugin uartPlugin = plugin;
            @Override
            public boolean onUartDeviceDataAvailable(UartDevice uart) {
                uartPlugin.webView.getEngine().evaluateJavascript("(function() {cordova.plugins.things.uart.callback('"+uart.getName()+"');})();", null);
                return super.onUartDeviceDataAvailable(uart);
            }
        };
        try {
            uart.registerUartDeviceCallback(deviceCallback, handler);
        } catch (IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackMap.put(name, deviceCallback);
        callbackContext.success();
        return true;
    }

    private boolean unregisterUartDeviceCallback(String name, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!uartMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        if (!callbackMap.containsKey(name)) {
            callbackContext.error("not registered!!");
            return false;
        }
        UartDevice uart = uartMap.get(name);
        UartDeviceCallback deviceCallback = callbackMap.get(name);
        uart.unregisterUartDeviceCallback(deviceCallback);
        callbackMap.remove(name);
        callbackContext.success();
        return true;
    }
}
