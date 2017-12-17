var exec = require('cordova/exec');

function GpioPlugin() {
	this.openGpio = function (name, success, error) {
	    exec(success, error, 'gpio', 'openGpio', [name]);
	};

	this.close = function (name, success, error) {
		exec(success, error, 'gpio', 'close', [name]);
	};

	this.setValue = function (name, value, success, error) {
		exec(success, error, 'gpio', 'setValue', [name, value]);
	};

	this.getValue = function (name, success, error) {
		exec(success, error, 'gpio', 'setValue', [name]);
	};
}

exports.gpio = new GpioPlugin();

function UartPlugin() {
	this.callbackMap = {};

	this.openUart = function (name, success, error) {
	    exec(success, error, 'uart', 'openUart', [name]);
	};

	this.close = function (name, success, error) {
		var _this = this;
		exec(function() {
			delete _this.callbackMap[name];
			success();
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
			success();
		}, error, 'uart', 'registerUartDeviceCallback', [name]);
	};

	this.unregisterUartDeviceCallback = function (name, success, error) {
		var _this = this;
		exec(function() {
			delete _this.callbackMap[name];
			success();
		}, error, 'uart', 'unregisterUartDeviceCallback', [name]);
	};

	this.callback = function (name) {
		if (this.callbackMap[name]) {
			this.callbackMap[name](name);
		}
	};
}

exports.uart = new UartPlugin();
