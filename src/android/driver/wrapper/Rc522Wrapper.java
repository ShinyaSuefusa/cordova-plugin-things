package org.apache.cordova.android.things.driver.wrapper;

import android.support.annotation.Nullable;

import com.galarzaa.androidthings.Rc522;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.android.things.pio.SpiDevice;

import java.io.IOException;

/**
 * Created by Shinya on 2018/01/31.
 */
public class Rc522Wrapper {
    private SpiDevice spi;
    private Gpio gpio;
    private Rc522 rc522;

    private PeripheralManagerService service = new PeripheralManagerService();

    public Rc522Wrapper(String spiName, String gpioName) throws IOException {
        this.spi = service.openSpiDevice(spiName);
        this.gpio = service.openGpio(gpioName);
        this.rc522 = new Rc522(this.spi, this.gpio);
    }

    public void close() throws IOException {
        this.spi.close();
        this.gpio.close();
    }

    /**
     * @see Rc522#setDebugging(boolean)
     */
    public void setDebugging(boolean debugging) {
        this.rc522.setDebugging(debugging);
    }

    /**
     * @see Rc522#getUid()
     */
    public byte[] getUid() {
        return this.rc522.getUid();
    }

    /**
     * @see Rc522#getUidString(String)
     */
    public String getUidString(String separator) {
        return this.rc522.getUidString(separator);
    }

    /**
     * @see Rc522#getUidString()
     */
    public String getUidString() {
        return this.rc522.getUidString();
    }

    /**
     * @see Rc522#request()
     */
    public boolean request() {
        return this.rc522.request();
    }

    /**
     * @see Rc522#request(byte)
     */
    public boolean request(byte requestMode) {
        return this.rc522.request(requestMode);
    }

    /**
     * @see Rc522#antiCollisionDetect()
     */
    public boolean antiCollisionDetect() {
        return this.rc522.antiCollisionDetect();
    }

    /**
     * @see Rc522#selectTag(byte[])
     */
    public boolean selectTag(byte[] uid) {
        return this.rc522.selectTag(uid);
    }

    /**
     * @see Rc522#authenticateCard(byte, byte, byte[])
     */
    public boolean authenticateCard(byte authMode, byte address, byte[] key) {
        return this.rc522.authenticateCard(authMode, address, key);
    }

    /**
     * @see Rc522#stopCrypto()
     */
    public void stopCrypto() {
        this.rc522.stopCrypto();
    }

    /**
     * @see Rc522#readBlock(byte, byte[])
     */
    public boolean readBlock(byte address, byte[] buffer) {
        return this.rc522.readBlock(address, buffer);
    }

    /**
     * @see Rc522#writeBlock(byte, byte[])
     */
    public boolean writeBlock(byte address, byte[] data) {
        return this.rc522.writeBlock(address, data);
    }

    /**
     * @see Rc522#increaseBlock(byte, int)
     */
    public boolean increaseBlock(byte address, int operand) {
        return this.rc522.increaseBlock(address, operand);
    }

    /**
     * @see Rc522#decreaseBlock(byte, int)
     */
    public boolean decreaseBlock(byte address, int operand) {
        return this.rc522.decreaseBlock(address, operand);
    }

    /**
     * @see Rc522#transferBlock(byte)
     */
    public boolean transferBlock(byte address) {
        return this.rc522.transferBlock(address);
    }

    /**
     * @see Rc522#restoreBlock(byte)
     */
    public boolean restoreBlock(byte address) {
        return this.rc522.restoreBlock(address);
    }

    /**
     * @see Rc522#writeValue(byte, int)
     */
    public boolean writeValue(byte address, int value) {
        return this.rc522.writeValue(address, value);
    }

    /**
     * @see Rc522#readValue(byte)
     */
    @Nullable
    public Integer readValue(byte address) {
        return this.rc522.readValue(address);
    }

    /**
     * @see Rc522#writeTrailer(byte, byte[], byte[], byte, byte[])
     */
    public boolean writeTrailer(byte sector, byte[] keyA, byte[] accessBits, byte userData, byte[] keyB) {
        return this.rc522.writeTrailer(sector, keyA, accessBits, userData, keyB);
    }

    /**
     * @see Rc522#dumpMifare1k()
     */
    public String dumpMifare1k() {
        return this.rc522.dumpMifare1k();
    }
}
