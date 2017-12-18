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
import java.util.Map;

/**
 * Created by Shinya on 2017/12/18.
 */

public class SerialPlugin extends CordovaPlugin {

    private PeripheralManagerService service = new PeripheralManagerService();
    private Map<String, Gpio> gpioMap = new HashMap<String, Gpio>();
    private Map<String, GpioCallback> callbackMap = new HashMap<String, GpioCallback>();
    private Map<String, Thread> threadMap = new HashMap<String, Thread>();


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

        if ("openSerial".equals(action)) {
            String name = args.length() > 0 ? args.getString(0) : null;
            Integer direction = args.length() > 1 ? args.getInt(1) : null;
            Integer baudrate = args.length() > 2 ? args.getInt(2) : null;
            return openSerial(name, direction, baudrate, callbackContext);
        }

        return false;
    }

    private boolean openSerial(String name, Integer direction, Integer baudrate, CallbackContext callbackContext) {
        if (name == null) {
            callbackContext.error("name is null!!");
            return false;
        }
        if (gpioMap.containsKey(name)) {
            callbackContext.error("already open!!");
            return false;
        }
        if (direction == null) {
            direction = Gpio.DIRECTION_IN;
        }
        if (baudrate == null) {
            baudrate = 9600;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        try {
            Gpio gpio = service.openGpio(name);
            gpio.setDirection(direction);
            gpio.setEdgeTriggerType(Gpio.EDGE_FALLING);
            SerialThread thread = new SerialThread(gpio, baudrate, this, handler);
            gpio.registerGpioCallback(thread);
            gpioMap.put(name, gpio);
        } catch(IOException e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackContext.success();
        return true;
    }

    private class SerialThread extends GpioCallback implements Runnable {
        private Gpio gpio;
        private int baudrate;
        private SerialPlugin plugin;
        private Handler handler;
        private int nanos;
        private int value = 0;
        private int count = 0;
        private int dataSize = 8;
        private int beforeBit = 0;
        private boolean invalidFrame = false;
        public SerialThread(Gpio gpio, int baudrate, SerialPlugin plugin, Handler handler) {
            this.gpio = gpio;
            this.baudrate = baudrate;
            this.plugin = plugin;
            this.nanos = 1000 * 1000 / baudrate;
            this.handler = handler;
        }
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            gpio.unregisterGpioCallback(this);
            new Thread(this).start();
            return super.onGpioEdge(gpio);
        }
        @Override
        public void run() {
            long beforeTime = System.nanoTime();
            while(true) {
                try {
                    int bit = this.gpio.getValue() ? 1 : 0;
                    boolean update = false;
                    if (this.beforeBit != bit) {
                        // 変化した場合
                        update = true;
                        this.beforeBit = bit;
                    } else {
                        // 変化しなかった場合
                        long currentTime = System.nanoTime();
                        if (this.nanos <= currentTime - beforeTime) {
                            // 規定時間を超えている場合
                            update = true;
                        }
                    }
                    if (update) {
                        if (this.count == this.dataSize) {
                            if (bit == 0) {
                                // ストップビットだった場合
                                this.invalidFrame = false;
                            } else {
                                // ストップビットではなかった場合
                                this.invalidFrame = true;
                            }
                            break;
                        }
                        beforeTime = System.nanoTime();
                        this.value = this.value << 1 | bit;
                        this.count++;
                    }
                    Thread.sleep(0, this.nanos / 2);
                } catch (IOException e) {
                    this.invalidFrame = true;
                    break;
                } catch (Exception e) {
                    // do nothing.
                }
            }

            final SerialPlugin _plugin = this.plugin;
            final Gpio _gpio = this.gpio;
            final boolean _invalidFrame = this.invalidFrame;
            final int _value = this.value;
            handler.post(new Runnable() {
                private SerialPlugin serialPlugin = _plugin;
                private Gpio gpio = _gpio;
                private boolean invalidFrame = _invalidFrame;
                private int value = _value;
                @Override
                public void run() {
                    serialPlugin.webView.getEngine().evaluateJavascript("(function() {cordova.plugins.things.serial.callback('"+gpio.getName()+"', '"+(invalidFrame?"error":"success")+"', "+value+");})();", null);
                }
            });

            this.value = 0;
            this.count = 0;

        }
    }
}
