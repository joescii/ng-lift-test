'use strict';

var myApp = angular.module('myApp', [ 'lift-ng', 
                                      'ngResource', 'ngAnimate', 
                                      'bc.controllers', 'bc.services']);

myApp.config(function($locationProvider, $httpProvider, $compileProvider) {
//	$compileProvider.debugInfoEnabled(false);
	$locationProvider.html5Mode({
		enabled: true,
		requireBase: false
		});
	
	$compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|ssh|chrome-extension):/)
//$compileProvider.debugInfoEnabled(false); ensure dependencies don't utilize element.scope calls before disabling
});