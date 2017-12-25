var exec = require('cordova/exec');

function promiseExec(success, error, service, action, args) {
	return new Promise(function (resolve, reject) {
		exec(function (result) {
			resolve(result);
			if (success) success(result);
		}, function (result) {
			reject(result);
			if (error) error(result);
		}, service, action, args);
	});
}

function Gpio(name) {
	this.name = name;
}
Gpio.prototype.getName = function () {
	return this.name;
};
Gpio.prototype.close = function (success, error) {
	return exports.gpio.close(this.name, success, error);
};
Gpio.prototype.setValue = function (value, success, error) {
	return exports.gpio.setValue(this.name, value, success, error);
};
Gpio.prototype.getValue = function (success, error) {
	return exports.gpio.getValue(this.name, success, error);
};
Gpio.prototype.setActiveType = function (activeType, success, error) {
	return exports.gpio.setActiveType(this.name, activeType, success, error);
};
Gpio.prototype.setDirection = function (direction, success, error) {
	return exports.gpio.setDirection(this.name, direction, success, error);
};
Gpio.prototype.setEdgeTriggerType = function (edgeTriggerType, success, error) {
	return exports.gpio.setEdgeTriggerType(this.name, edgeTriggerType, success, error);
};
Gpio.prototype.registerGpioCallback = function (callback, success, error) {
	return exports.gpio.registerGpioCallback(this.name, callback, success, error);
};
Gpio.prototype.unregisterGpioCallback = function (success, error) {
	return exports.gpio.unregisterGpioCallback(this.name, success, error);
};

function GpioPlugin() {
    this.ACTIVE_HIGH = 1;
    this.ACTIVE_LOW = 0;
    this.DIRECTION_IN = 0;
    this.DIRECTION_OUT_INITIALLY_HIGH = 1;
    this.DIRECTION_OUT_INITIALLY_LOW = 2;
    this.EDGE_BOTH = 3;
    this.EDGE_FALLING = 2;
    this.EDGE_NONE = 0;
    this.EDGE_RISING = 1;

    this.callbackMap = {};

	this.getGpioList = function (name, success, error) {
		return promiseExec(success, error, 'gpio', 'getGpioList', []);
	};

	this.openGpio = function (name, success, error) {
	    return new Promise(function (resolve, reject) {
			exec(function () {
				var gpio = new Gpio(name);
				resolve(gpio);
				if (success) success(gpio);
			}, function (result) {
				reject(result);
				if (error) error(result);
			}, 'gpio', 'openGpio', [name]);
		});
	};

	this.close = function (name, success, error) {
		var _this = this;
		return promiseExec(function() {
			delete _this.callbackMap[name];
			if (success) success();
		}, error, 'gpio', 'close', [name]);
	};

	this.setValue = function (name, value, success, error) {
		return promiseExec(success, error, 'gpio', 'setValue', [name, value]);
	};

	this.getValue = function (name, success, error) {
		return promiseExec(success, error, 'gpio', 'setValue', [name]);
	};

	this.setActiveType = function (name, activeType, success, error) {
		return promiseExec(success, error, 'gpio', 'setActiveType', [name, activeType]);
	};

	this.setDirection = function (name, direction, success, error) {
		return promiseExec(success, error, 'gpio', 'setDirection', [name, direction]);
	};

	this.setEdgeTriggerType = function (name, edgeTriggerType, success, error) {
		return promiseExec(success, error, 'gpio', 'setEdgeTriggerType', [name, edgeTriggerType]);
	};

	this.registerGpioCallback = function (name, callback, success, error) {
		var _this = this;
		return promiseExec(function() {
			_this.callbackMap[name] = callback;
			if (success) success();
		}, error, 'gpio', 'registerGpioCallback', [name]);
	};

	this.unregisterGpioCallback = function (name, success, error) {
		var _this = this;
		return promiseExec(function() {
			delete _this.callbackMap[name];
			if (success) success();
		}, error, 'gpio', 'registerGpioCallback', [name]);
	};

	this.callback = function (name) {
		if (this.callbackMap[name]) {
			this.callbackMap[name](name);
		}
	};
}

exports.gpio = new GpioPlugin();

function I2c(name) {
	this.name = name;
}
I2c.prototype.getName = function () {
	return this.name;
};
I2c.prototype.close = function (success, error) {
	return exports.i2c.close(this.name, success, error);
};
I2c.prototype.read = function (length, success, error) {
	return exports.i2c.read(this.name, length, success, error);
};
I2c.prototype.readRegBuffer = function (reg, length, success, error) {
	return exports.i2c.readRegBuffer(this.name, reg, length, success, error);
};
I2c.prototype.readRegByte = function (reg, success, error) {
	return exports.i2c.readRegByte(this.name, reg, success, error);
};
I2c.prototype.readRegWord = function (reg, success, error) {
	return exports.i2c.readRegWord(this.name, reg, success, error);
};
I2c.prototype.write = function (array, success, error) {
	return exports.i2c.write(this.name, array, success, error);
};
I2c.prototype.writeRegBuffer = function (reg, array, success, error) {
	return exports.i2c.writeRegBuffer(this.name, reg, array, success, error);
};
I2c.prototype.writeRegByte = function (reg, data, success, error) {
	return exports.i2c.writeRegByte(this.name, reg, data, success, error);
};
I2c.prototype.writeRegWord = function (reg, data, success, error) {
	return exports.i2c.writeRegWord(this.name, reg, data, success, error);
};

function I2cPlugin() {
	this.getI2cBusList = function (name, success, error) {
		return promiseExec(success, error, 'i2c', 'getI2cBusList', []);
	};

	this.openI2cDevice = function (name, success, error) {
	    return new Promise(function (resolve, reject) {
			exec(function () {
				var i2c = new I2c(name);
				resolve(i2c);
				if (success) success(i2c);
			}, function (result) {
				reject(result);
				if (error) error(result);
			}, 'i2c', 'openI2cDevice', [name]);
		});
	};

	this.close = function (name, success, error) {
		return promiseExec(success, error, 'i2c', 'close', [name]);
	};

	this.read = function (name, length, success, error) {
		return promiseExec(success, error, 'i2c', 'read', [name, length]);
	};

	this.readRegBuffer = function (name, reg, length, success, error) {
		return promiseExec(success, error, 'i2c', 'readRegBuffer', [name, reg, length]);
	};

	this.readRegByte = function (name, reg, success, error) {
		return promiseExec(success, error, 'i2c', 'readRegByte', [name, reg]);
	};

	this.readRegWord = function (name, reg, success, error) {
		return promiseExec(success, error, 'i2c', 'readRegWord', [name, reg]);
	};
	
	this.write = function (name, array, success, error) {
		return promiseExec(success, error, 'i2c', 'write', [name, array]);
	};
	
	this.writeRegBuffer = function (name, reg, array, success, error) {
		return promiseExec(success, error, 'i2c', 'writeRegBuffer', [name, reg, array]);
	};
	
	this.writeRegByte = function (name, reg, data, success, error) {
		return promiseExec(success, error, 'i2c', 'writeRegByte', [name, reg, data]);
	};
	
	this.writeRegWord = function (name, reg, data, success, error) {
		return promiseExec(success, error, 'i2c', 'writeRegWord', [name, reg, data]);
	};
}

exports.i2c = new I2cPlugin();

function Uart(name) {
	this.name = name;
}
Uart.prototype.getName = function () {
	return this.name;
};
Uart.prototype.close = function (success, error) {
	return exports.uart.close(this.name, success, error);
};
Uart.prototype.flush = function (direction, success, error) {
	return exports.uart.flush(this.name, direction, success, error);
};
Uart.prototype.read = function (length, success, error) {
	return exports.uart.read(this.name, length, success, error);
};
Uart.prototype.sendBreak = function (duration_msec, success, error) {
	return exports.uart.sendBreak(this.name, duration_msec, success, error);
};
Uart.prototype.setBaudrate = function (rate, success, error) {
	return exports.uart.setBaudrate(this.name, rate, success, error);
};
Uart.prototype.setDataSize = function (size, success, error) {
	return exports.uart.setDataSize(this.name, size, success, error);
};
Uart.prototype.setHardwareFlowControl = function (mode, success, error) {
	return exports.uart.setHardwareFlowControl(this.name, mode, success, error);
};
Uart.prototype.setModemControl = function (lines, success, error) {
	return exports.uart.setModemControl(this.name, lines, success, error);
};
Uart.prototype.setParity = function (mode, success, error) {
	return exports.uart.setParity(this.name, mode, success, error);
};
Uart.prototype.setStopBits = function (bits, success, error) {
	return exports.uart.setStopBits(this.name, bits, success, error);
};
Uart.prototype.write = function (buffer, success, error) {
	return exports.uart.write(this.name, buffer, success, error);
};
Uart.prototype.registerUartDeviceCallback = function (callback, success, error) {
	return exports.uart.registerUartDeviceCallback(this.name, callback, success, error);
};
Uart.prototype.unregisterUartDeviceCallback = function (callback, success, error) {
	return exports.uart.unregisterUartDeviceCallback(this.name, success, error);
};

function UartPlugin() {
    this.FLUSH_IN = 0;
    this.FLUSH_IN_OUT = 2;
    this.FLUSH_OUT = 1;
    this.HW_FLOW_CONTROL_AUTO_RTSCTS = 1;
    this.HW_FLOW_CONTROL_NONE = 0;
    this.MODEM_CONTROL_CD = 64;
    this.MODEM_CONTROL_CTS = 32;
    this.MODEM_CONTROL_DSR = 256;
    this.MODEM_CONTROL_DTR = 2;
    this.MODEM_CONTROL_LE = 1;
    this.MODEM_CONTROL_RI = 128;
    this.MODEM_CONTROL_RTS = 4;
    this.MODEM_CONTROL_SR = 16;
    this.MODEM_CONTROL_ST = 8;
    this.PARITY_EVEN = 1;
    this.PARITY_MARK = 3;
    this.PARITY_NONE = 0;
    this.PARITY_ODD = 2;
    this.PARITY_SPACE = 4;

   	this.callbackMap = {};

	this.getUartDeviceList = function (name, success, error) {
		return promiseExec(success, error, 'uart', 'getUartDeviceList', []);
	};

	this.openUartDevice = function (name, success, error) {
	    return new Promise(function (resolve, reject) {
			exec(function () {
				var uart = new Uart(name);
				resolve(uart);
				if (success) success(uart);
			}, function (result) {
				reject(result);
				if (error) error(result);
			}, 'uart', 'openUartDevice', [name]);
		});
	};

	this.close = function (name, success, error) {
		var _this = this;
		return promiseExec(function() {
			delete _this.callbackMap[name];
			if (success) success();
		}, error, 'uart', 'close', [name]);
	};

	this.flush = function (name, direction, success, error) {
		return promiseExec(success, error, 'uart', 'flush', [name, direction]);
	};

	this.read = function (name, length, success, error) {
		return promiseExec(success, error, 'uart', 'read', [name, length]);
	};

	this.sendBreak = function (name, duration_msec, success, error) {
		return promiseExec(success, error, 'uart', 'sendBreak', [name, duration_msec]);
	};

	this.setBaudrate = function (name, rate, success, error) {
		return promiseExec(success, error, 'uart', 'setBaudrate', [name, rate]);
	};

	this.setDataSize = function (name, size, success, error) {
		return promiseExec(success, error, 'uart', 'setDataSize', [name, size]);
	};

	this.setHardwareFlowControl = function (name, mode, success, error) {
		return promiseExec(success, error, 'uart', 'setHardwareFlowControl', [name, mode]);
	};

	this.setModemControl = function (name, lines, success, error) {
		return promiseExec(success, error, 'uart', 'setModemControl', [name, lines]);
	};

	this.setParity = function (name, mode, success, error) {
		return promiseExec(success, error, 'uart', 'setParity', [name, mode]);
	};

	this.setStopBits = function (name, bits, success, error) {
		return promiseExec(success, error, 'uart', 'setStopBits', [name, bits]);
	};

	this.write = function (name, buffer, success, error) {
		return promiseExec(success, error, 'uart', 'write', [name, buffer]);
	};

	this.registerUartDeviceCallback = function (name, callback, success, error) {
		var _this = this;
		return promiseExec(function() {
			_this.callbackMap[name] = callback;
			if (success) success();
		}, error, 'uart', 'registerUartDeviceCallback', [name]);
	};

	this.unregisterUartDeviceCallback = function (name, success, error) {
		var _this = this;
		return promiseExec(function() {
			delete _this.callbackMap[name];
			if (success) success();
		}, error, 'uart', 'unregisterUartDeviceCallback', [name]);
	};

	this.callback = function (name) {
		if (this.callbackMap[name]) {
			this.callbackMap[name](name);
		}
	};
}

exports.uart = new UartPlugin();

function LcdPcf8574(name) {
	this.name = name;
}

LcdPcf8574.prototype.getName = function () {
	return this.name;
};
LcdPcf8574.prototype.close = function (success, error) {
	return exports.lcdpcf8574.close(this.name, success, error);
};
LcdPcf8574.prototype.begin = function (cols, rows, charsize, success, error) {
	return exports.lcdpcf8574.begin(this.name, cols, rows, charsize, success, error);
};
LcdPcf8574.prototype.clear = function (success, error) {
	return exports.lcdpcf8574.clear(this.name, success, error);
};
LcdPcf8574.prototype.home = function (success, error) {
	return exports.lcdpcf8574.home(this.name, success, error);
};
LcdPcf8574.prototype.setCursor = function (col, row, success, error) {
	return exports.lcdpcf8574.setCursor(this.name, col, row, success, error);
};
LcdPcf8574.prototype.noDisplay = function (success, error) {
	return exports.lcdpcf8574.noDisplay(this.name, success, error);
};
LcdPcf8574.prototype.display = function (success, error) {
	return exports.lcdpcf8574.display(this.name, success, error);
};
LcdPcf8574.prototype.noBlink = function (success, error) {
	return exports.lcdpcf8574.noBlink(this.name, success, error);
};
LcdPcf8574.prototype.blink = function (success, error) {
	return exports.lcdpcf8574.blink(this.name, success, error);
};
LcdPcf8574.prototype.noCursor = function (success, error) {
	return exports.lcdpcf8574.noCursor(this.name, success, error);
};
LcdPcf8574.prototype.cursor = function (success, error) {
	return exports.lcdpcf8574.cursor(this.name, success, error);
};
LcdPcf8574.prototype.scrollDisplayLeft = function (success, error) {
	return exports.lcdpcf8574.scrollDisplayLeft(this.name, success, error);
};
LcdPcf8574.prototype.scrollDisplayRight = function (success, error) {
	return exports.lcdpcf8574.scrollDisplayRight(this.name, success, error);
};
LcdPcf8574.prototype.leftToRight = function (success, error) {
	return exports.lcdpcf8574.leftToRight(this.name, success, error);
};
LcdPcf8574.prototype.rightToLeft = function (success, error) {
	return exports.lcdpcf8574.rightToLeft(this.name, success, error);
};
LcdPcf8574.prototype.autoscroll = function (success, error) {
	return exports.lcdpcf8574.autoscroll(this.name, success, error);
};
LcdPcf8574.prototype.noAutoscroll = function (success, error) {
	return exports.lcdpcf8574.noAutoscroll(this.name, success, error);
};
LcdPcf8574.prototype.setBacklight = function (enable, success, error) {
	return exports.lcdpcf8574.setBacklight(this.name, enable, success, error);
};
LcdPcf8574.prototype.createChar = function (location, charmap, success, error) {
	return exports.lcdpcf8574.createChar(this.name, location, charmap, success, error);
};
LcdPcf8574.prototype.write = function (value, success, error) {
	return exports.lcdpcf8574.write(this.name, value, success, error);
};
LcdPcf8574.prototype.print = function (message, success, error) {
	return exports.lcdpcf8574.print(this.name, message, success, error);
};

function LcdPcf8574Plugin() {
	this.open = function (name, address, success, error) {
	    return new Promise(function (resolve, reject) {
			exec(function () {
				var lcdpcf8574 = new LcdPcf8574(name);
				resolve(lcdpcf8574);
				if (success) success(lcdpcf8574);
			}, function (result) {
				reject(result);
				if (error) error(result);
			}, 'lcdpcf8574', 'open', [name, address]);
		});
	};

	this.close = function (name, success, error) {
		return promiseExec(success, error, 'lcdpcf8574', 'close', [name]);
	};

	this.begin = function (name, cols, rows, charsize, success, error) {
		return promiseExec(success, error, 'lcdpcf8574', 'begin', [name, cols, rows, charsize]);
	};

	this.clear = function (name, success, error) {
		return promiseExec(success, error, 'lcdpcf8574', 'clear', [name]);
	};

	this.home = function (name, success, error) {
		return promiseExec(success, error, 'lcdpcf8574', 'home', [name]);
	};

	this.setCursor = function (name, col, row, success, error) {
		return promiseExec(success, error, 'lcdpcf8574', 'setCursor', [name, col, row]);
	};

	this.noDisplay = function (name, success, error) {
		return promiseExec(success, error, 'lcdpcf8574', 'noDisplay', [name]);
	};

	this.display = function (name, success, error) {
		return promiseExec(success, error, 'lcdpcf8574', 'display', [name]);
	};

	this.noBlink = function (name, success, error) {
		return promiseExec(success, error, 'lcdpcf8574', 'noBlink', [name]);
	};

	this.blink = function (name, success, error) {
		return promiseExec(success, error, 'lcdpcf8574', 'blink', [name]);
	};

	this.noCursor = function (name, success, error) {
		return promiseExec(success, error, 'lcdpcf8574', 'noCursor', [name]);
	};

	this.cursor = function (name, success, error) {
		return promiseExec(success, error, 'lcdpcf8574', 'cursor', [name]);
	};

	this.scrollDisplayLeft = function (name, success, error) {
		return promiseExec(success, error, 'lcdpcf8574', 'scrollDisplayLeft', [name]);
	};

	this.scrollDisplayRight = function (name, success, error) {
		return promiseExec(success, error, 'lcdpcf8574', 'scrollDisplayRight', [name]);
	};

	this.leftToRight = function (name, success, error) {
		return promiseExec(success, error, 'lcdpcf8574', 'leftToRight', [name]);
	};

	this.rightToLeft = function (name, success, error) {
		return promiseExec(success, error, 'lcdpcf8574', 'rightToLeft', [name]);
	};

	this.autoscroll = function (name, success, error) {
		return promiseExec(success, error, 'lcdpcf8574', 'autoscroll', [name]);
	};

	this.noAutoscroll = function (name, success, error) {
		return promiseExec(success, error, 'lcdpcf8574', 'noAutoscroll', [name]);
	};

	this.setBacklight = function (name, enable, success, error) {
		return promiseExec(success, error, 'lcdpcf8574', 'setBacklight', [name, enable]);
	};

	this.createChar = function (name, location, charmap, success, error) {
		return promiseExec(success, error, 'lcdpcf8574', 'createChar', [name, location, charmap]);
	};

	this.write = function (name, value, success, error) {
		return promiseExec(success, error, 'lcdpcf8574', 'write', [name, value]);
	};

	this.print = function (name, message, success, error) {
		return promiseExec(success, error, 'lcdpcf8574', 'print', [name, message]);
	};
}

exports.lcdpcf8574 = new LcdPcf8574Plugin();

