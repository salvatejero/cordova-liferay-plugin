cordova.define("salvatejero.cordova.liferay.Liferay", function(require, exports, module) {

var argscheck = require('cordova/argscheck'),
    exec = require('cordova/exec'),
    channel = require('cordova/channel'),
    cordova = require('cordova');

var Liferay = {
    connect: function(successCallback, errorCallback, ipServer, userName, password) {
    	exec(successCallback, errorCallback, "Liferay", "connect", [ipServer, userName, password]);
    },
    
    execute: function(successCallback, errorCallback, className, method, params) {
    	exec(successCallback, errorCallback, "Liferay", "execute", [className, method, params]);
    }
};

channel.onCordovaReady.subscribe(function() {
	Liferay.connect(null, null, 'http://10.0.2.2:8080', 'test@liferay.com', 'test' );
	
});

module.exports = Liferay;

});