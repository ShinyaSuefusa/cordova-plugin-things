package org.apache.cordova.android.things;

import android.os.Handler;
import android.os.Looper;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
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
 * Created by Shinya Suefusa on 2017/12/10.
 */
public class GpioPlugin extends CordovaPlugin {

    private PeripheralManagerService service = new PeripheralManagerService();
    private Map<String, Gpio> deviceMap = new HashMap<>();
    private Map<String, GpioCallback> callbackMap = new HashMap<>();

    @Override
    public void onDestroy() {
        for (Gpio device : deviceMap.values()) {
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

        if ("getGpioList".equals(action)) {
            return getGpioList(callbackContext);
        } else if ("openGpio".equals(action)) {
            String name = args.getString(0);
            int direction = args.optInt(1, Gpio.DIRECTION_OUT_INITIALLY_LOW);
            return openGpio(name, direction, callbackContext);
        } else if ("close".equals(action)) {
            String name = args.getString(0);
            return close(name, callbackContext);
        } else if ("closeAll".equals(action)) {
            onDestroy();
            callbackContext.success();
            return true;
        } else if ("setValue".equals(action)) {
            String name = args.getString(0);
            int value = args.optInt(1, 1);
            return setValue(name, value, callbackContext);
        } else if ("getValue".equals(action)) {
            String name = args.getString(0);
            return getValue(name, callbackContext);
        } else if ("setActiveType".equals(action)) {
            String name = args.getString(0);
            int activeType = args.optInt(1, Gpio.ACTIVE_LOW);
            return setActiveType(name, activeType, callbackContext);
        } else if ("setDirection".equals(action)) {
            String name = args.getString(0);
            int direction = args.optInt(1, Gpio.DIRECTION_OUT_INITIALLY_LOW);
            return setDirection(name, direction, callbackContext);
        } else if ("setEdgeTriggerType".equals(action)) {
            String name = args.getString(0);
            int edgeTriggerType = args.optInt(1, Gpio.EDGE_NONE);
            return setEdgeTriggerType(name, edgeTriggerType, callbackContext);
        } else if ("registerGpioCallback".equals(action)) {
            String name = args.getString(0);
            return registerGpioCallback(name, callbackContext);
        } else if ("unregisterGpioCallback".equals(action)) {
            String name = args.getString(0);
            return unregisterGpioCallback(name, callbackContext);
        }
        callbackContext.error("undefined function. [" + action + "]");
        return false;
    }

    private boolean getGpioList(CallbackContext callbackContext) {
        List<String> list = service.getGpioList();
        JSONArray array = new JSONArray();
        for (String name : list) {
            array.put(name);
        }
        callbackContext.success(array);
        return true;
    }

    private boolean openGpio(String name, Integer direction, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (deviceMap.containsKey(name)) {
            callbackContext.error("already open!!");
            return false;
        }
        try {
            Gpio device = service.openGpio(name);
            device.setDirection(direction);
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
        Gpio device = deviceMap.get(name);
        try {
            device.close();
        } catch(IOException e) {
            // Do nothing.
        }
        deviceMap.remove(name);
        callbackMap.remove(name);
        callbackContext.success();
        return true;
    }

    private boolean setValue(String name, Integer value, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        Gpio device = deviceMap.get(name);
        try {
            device.setValue(value != 0);
        } catch(IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackContext.success();
        return true;
    }

    private boolean getValue(String name, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        Gpio device = deviceMap.get(name);
        try {
            int value = device.getValue() ? 1 : 0;
            callbackContext.success(value);
        } catch(IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }

        return true;
    }

    private boolean setActiveType(String name, int activeType, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        Gpio device = deviceMap.get(name);
        try {
            device.setActiveType(activeType);
        } catch(IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }

        callbackContext.success();
        return true;
    }

    private boolean setDirection(String name, int direction, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        Gpio device = deviceMap.get(name);
        try {
            device.setDirection(direction);
        } catch(IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }

        callbackContext.success();
        return true;
    }

    private boolean setEdgeTriggerType(String name, int edgeTriggerType, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!deviceMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        Gpio device = deviceMap.get(name);
        try {
            device.setEdgeTriggerType(edgeTriggerType);
        } catch(IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }

        callbackContext.success();
        return true;
    }

    private boolean registerGpioCallback(String name, CallbackContext callbackContext) {
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
        Gpio device = deviceMap.get(name);
        final GpioPlugin plugin = this;
        final Handler handler = new Handler(Looper.getMainLooper());
        GpioCallback callback = new GpioCallback() {
            GpioPlugin gpioPlugin = plugin;
            @Override
            public boolean onGpioEdge(Gpio gpio) {
                gpioPlugin.webView.getEngine().evaluateJavascript("(function() {cordova.plugins.things.gpio.callback('"+gpio.getName()+"');})();", null);
                return super.onGpioEdge(gpio);
            }
        };
        try {
            device.registerGpioCallback(callback, handler);
        } catch(IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }

        callbackMap.put(name, callback);
        callbackContext.success();
        return true;
    }

    private boolean unregisterGpioCallback(String name, CallbackContext callbackContext) {
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
        Gpio device = deviceMap.get(name);
        GpioCallback callback = callbackMap.get(name);
        device.unregisterGpioCallback(callback);
        callbackMap.remove(name);
        callbackContext.success();
        return true;
    }
}
