package org.apache.cordova.things;

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
    private Map<String, Gpio> gpioMap = new HashMap<String, Gpio>();
    private Map<String, GpioCallback> callbackMap = new HashMap<String, GpioCallback>();

    @Override
    public void onDestroy() {
        for (String key : gpioMap.keySet()) {
            try {
                gpioMap.get(key).close();
            } catch(IOException e) {
                // Do nothing.
            }
        }
        gpioMap.clear();
        callbackMap.clear();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if ("getGpioList".equals(action)) {
            return getGpioList(callbackContext);
        } else if ("openGpio".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            Integer direction = args.length() > 1 ? args.getInt(1) : null;
            return openGpio(name, direction, callbackContext);
        } else if ("close".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            return close(name, callbackContext);
        } else if ("setValue".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            Integer value = args.length() > 1 ? args.getInt(1) : null;
            return setValue(name, value, callbackContext);
        } else if ("getValue".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            return getValue(name, callbackContext);
        } else if ("setActiveType".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            int activeType = args.length() > 1 ? args.getInt(1) : Gpio.ACTIVE_LOW;
            return setActiveType(name, activeType, callbackContext);
        } else if ("setDirection".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            int direction = args.length() > 1 ? args.getInt(1) : Gpio.DIRECTION_OUT_INITIALLY_LOW;
            return setDirection(name, direction, callbackContext);
        } else if ("setEdgeTriggerType".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            int edgeTriggerType = args.length() > 1 ? args.getInt(1) : Gpio.EDGE_NONE;
            return setEdgeTriggerType(name, edgeTriggerType, callbackContext);
        } else if ("registerGpioCallback".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            return registerGpioCallback(name, callbackContext);
        } else if ("unregisterGpioCallback".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            return unregisterGpioCallback(name, callbackContext);
        }

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
        if (gpioMap.containsKey(name)) {
            callbackContext.error("already open!!");
            return false;
        }
        if (direction == null) {
            direction = Gpio.DIRECTION_OUT_INITIALLY_LOW;
        }
        try {
            Gpio gpio = service.openGpio(name);
            gpio.setDirection(direction);
            gpioMap.put(name, gpio);
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
        if (!gpioMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        Gpio gpio = gpioMap.get(name);
        try {
            gpio.close();
        } catch(IOException e) {
            // Do nothing.
        }
        gpioMap.remove(name);
        callbackMap.remove(name);
        callbackContext.success();
        return true;
    }

    private boolean setValue(String name, Integer value, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!gpioMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        Gpio gpio = gpioMap.get(name);
        if (value == null) {
            value = 1;
        }
        try {
            gpio.setValue(value != 0);
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
        if (!gpioMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        Gpio gpio = gpioMap.get(name);
        try {
            int value = gpio.getValue() ? 1 : 0;
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
        if (!gpioMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        Gpio gpio = gpioMap.get(name);
        try {
            gpio.setActiveType(activeType);
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
        if (!gpioMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        Gpio gpio = gpioMap.get(name);
        try {
            gpio.setDirection(direction);
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
        if (!gpioMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        Gpio gpio = gpioMap.get(name);
        try {
            gpio.setEdgeTriggerType(edgeTriggerType);
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
        if (!gpioMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        if (callbackMap.containsKey(name)) {
            callbackContext.error("already registered!!");
            return false;
        }
        Gpio gpio = gpioMap.get(name);
        final GpioPlugin plugin = this;
        final Handler handler = new Handler(Looper.getMainLooper());
        GpioCallback gpioCallback = new GpioCallback() {
            GpioPlugin gpioPlugin = plugin;
            @Override
            public boolean onGpioEdge(Gpio gpio) {
                gpioPlugin.webView.getEngine().evaluateJavascript("(function() {cordova.plugins.things.gpio.callback('"+gpio.getName()+"');})();", null);
                return super.onGpioEdge(gpio);
            }
        };
        try {
            gpio.registerGpioCallback(gpioCallback, handler);
        } catch(IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }

        callbackMap.put(name, gpioCallback);
        callbackContext.success();
        return true;
    }

    private boolean unregisterGpioCallback(String name, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (!gpioMap.containsKey(name)) {
            callbackContext.error("not open!!");
            return false;
        }
        if (!callbackMap.containsKey(name)) {
            callbackContext.error("not registered!!");
            return false;
        }
        Gpio gpio = gpioMap.get(name);
        GpioCallback gpioCallback = callbackMap.get(name);
        gpio.unregisterGpioCallback(gpioCallback);
        callbackMap.remove(name);
        callbackContext.success();
        return true;
    }
}
