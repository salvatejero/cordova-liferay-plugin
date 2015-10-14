var argscheck = require('cordova/argscheck'),
    exec = require('cordova/exec'),
    channel = require('cordova/channel'),
    cordova = require('cordova');

function Liferay () { console.log('Liferay loaded');}


Liferay.prototype.connect = function(successCallback, errorCallback, ipServer, userName, password) {
    	exec(successCallback, errorCallback, "Liferay", "connect", [ipServer, userName, password]);
}

Liferay.prototype.execute = function(successCallback, errorCallback, className, method, params) {
    	exec(successCallback, errorCallback, "Liferay", "execute", [className, method, params]);
}

var Liferay = new Liferay();

module.exports = Liferay;