(function() {
	var app = angular.module('app.sims');
	
    app.directive("vcellJobs", function() {
        return {
          restrict: "E",
          templateUrl: "/webapp/sims/vcell-jobs.html",
        };
    });
    
 })();