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

		deviceService.getCachedData().then(function(data) {
			console.log("received cache", data)
			$scope.futureData = data
		})
}])
