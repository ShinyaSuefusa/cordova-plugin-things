var exec = require('cordova/exec');

function GpioPlugin() {
	this.callbackMap = {};

	this.openGpio = function (name, success, error) {
	    exec(success, error, 'gpio', 'openGpio', [name]);
	};

	this.close = function (name, success, error) {
		var _this = this;
		exec(function() {
			delete _this.callbackMap[name];
			if (success) success();
		}, error, 'gpio', 'close', [name]);
	};

	this.setValue = function (name, value, success, error) {
		exec(success, error, 'gpio', 'setValue', [name, value]);
	};

	this.getValue = function (name, success, error) {
		exec(success, error, 'gpio', 'setValue', [name]);
	};

	this.setActiveType = function (name, activeType, success, error) {
		exec(success, error, 'gpio', 'setActiveType', [name, activeType]);
	};

	this.setDirection = function (name, direction, success, error) {
		exec(success, error, 'gpio', 'setDirection', [name, direction]);
	};

	this.setEdgeTriggerType = function (name, edgeTriggerType, success, error) {
		exec(success, error, 'gpio', 'setEdgeTriggerType', [name, edgeTriggerType]);
	};

	this.registerGpioCallback = function (name, callback, success, error) {
		var _this = this;
		exec(function() {
			_this.callbackMap[name] = callback;
			if (success) success();
		}, error, 'gpio', 'registerGpioCallback', [name]);
	};

	this.unregisterGpioCallback = function (name, success, error) {
		var _this = this;
		exec(function() {
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

function SerialPlugin() {
	this.callbackMap = {};

	this.openSerial = function (name, callback, success, error) {
		var _this = this;
	    exec(function() {
			_this.callbackMap[name] = callback;
			if (success) success();
		}, error, 'serial', 'openSerial', [name]);
	};

	this.close = function (name, success, error) {
		var _this = this;
		exec(function() {
			delete _this.callbackMap[name];
			if (success) success();
		}, error, 'serial', 'close', [name]);
	};

	this.callback = function (name, result, value) {
		if (this.callbackMap[name]) {
			this.callbackMap[name](name, result, value);
		}
	};
}

exports.serial = new SerialPlugin();

function UartPlugin() {
	this.callbackMap = {};

	this.openUart = function (name, success, error) {
	    exec(success, error, 'uart', 'openUart', [name]);
	};

	this.close = function (name, success, error) {
		var _this = this;
		exec(function() {
			delete _this.callbackMap[name];
			if (success) success();
		}, error, 'uart', 'close', [name]);
	};

	this.flush = function (name, direction, success, error) {
		exec(success, error, 'uart', 'flush', [name, direction]);
	};

	this.read = function (name, length, success, error) {
		exec(success, error, 'uart', 'read', [name, length]);
	};

	this.sendBreak = function (name, duration_msec, success, error) {
		exec(success, error, 'uart', 'sendBreak', [name, duration_msec]);
	};

	this.setBaudrate = function (name, rate, success, error) {
		exec(success, error, 'uart', 'setBaudrate', [name, rate]);
	};

	this.setDataSize = function (name, size, success, error) {
		exec(success, error, 'uart', 'setDataSize', [name, size]);
	};

	this.setHardwareFlowControl = function (name, mode, success, error) {
		exec(success, error, 'uart', 'setHardwareFlowControl', [name, mode]);
	};

	this.setModemControl = function (name, lines, success, error) {
		exec(success, error, 'uart', 'setModemControl', [name, lines]);
	};

	this.setParity = function (name, mode, success, error) {
		exec(success, error, 'uart', 'setParity', [name, mode]);
	};

	this.setStopBits = function (name, bits, success, error) {
		exec(success, error, 'uart', 'setStopBits', [name, bits]);
	};

	this.write = function (name, buffer, success, error) {
		exec(success, error, 'uart', 'write', [name, buffer]);
	};

	this.registerUartDeviceCallback = function (name, callback, success, error) {
		var _this = this;
		exec(function() {
			_this.callbackMap[name] = callback;
			if (success) success();
		}, error, 'uart', 'registerUartDeviceCallback', [name]);
	};

	this.unregisterUartDeviceCallback = function (name, success, error) {
		var _this = this;
		exec(function() {
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
