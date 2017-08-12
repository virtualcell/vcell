(function() {
	var app = angular.module('app.models');
	
	app.directive("vcellBiomodel", function() {
		return {
			restrict: "E",
			templateUrl: "/webapp/models/vcell-biomodel.html",
		};
	});
    
 })();