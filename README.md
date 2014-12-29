cordova-liferay-plugin
======================

#Liferay plugin for Cordova

#salvatejero.cordova.liferay

This plugin defines a global 'Liferay' object, which describes the interface to Liferay SDK.
Although the object is in the global scope, it is not available until after the 'deviceready' event.

``` 
Liferay.connect(null, null, 'http://10.0.2.2:8080', 'test@liferay.com', 'test' );
``` 
## Installation
``` 
cordova plugin add https://github.com/salvatejero/cordova-liferay-plugin.git
``` 
### Supported Platforms:

- Android
- IOS: comming soon
- WP: comming soon

##Requirements:

This plugin requires Liferay SDK.

- Add Liferay SDK to your project: https://dev.liferay.com/develop/tutorials/-/knowledge_base/6-2/mobile


### Quick Example

``` 
Liferay.execute(successCallback, errorCallback, 'com.liferay.portal.model.User', 'getUserByEmailAddress', ['10154', 'test@liferay.com']);


function successCallback(data){
	// .... json array or json object
}

```