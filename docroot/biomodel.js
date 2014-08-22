(function() {
	var app = angular.module('vcell-directives', []);
	
	app.directive("vcellBiomodel", function() {
		return {
			restrict: "E",
			templateUrl: "/scripts/vcell-biomodel.html",
		};
	});
	
    app.directive("vcellLoginbar", function() {
        return {
          restrict: "E",
          templateUrl: "/scripts/vcell-loginbar.html",
        };
    });

    app.directive("vcellJobs", function() {
        return {
          restrict: "E",
          templateUrl: "/scripts/vcell-jobs.html",
        };
    });



})();