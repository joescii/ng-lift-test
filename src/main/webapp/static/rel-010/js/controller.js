function getScope() {
	return angular.element('#controller').scope();
}

var bcController = angular.module('bc.controllers', []);

bcController.controller('DeviceCtrl', ['$scope', 'deviceService',
  function($scope, deviceService) {
		deviceService.getData().then(function(data) {
			console.log("received", data)
			$scope.data = data
		})
		
		deviceService.getWillFail().then(function(data) {
			console.log("Oops, received", data)
			$scope.oops1 = data
		}, function(err) {
			$scope.failData = err
		})

		deviceService.getCachedData().then(function(data) {
			console.log("received cache", data)
			$scope.futureData = data
		})
		
		deviceService.getFutureFail().then(function(data) {
			console.log("Oops, received future", data)
			$scope.oops2 = data
		}, function(err) {
			$scope.futureFailData = err
		})		
}])
