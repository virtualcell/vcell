(function() {
	var app = angular.module('app.sims');
	
	app.controller('SimsController', ["$scope","$rootScope","Session","AUTH_EVENTS","USER_ROLES","AuthService","$http", function($scope,$rootScope,Session,AUTH_EVENTS,USER_ROLES,AuthService,$http) {

 		$scope.simStatuses = [];
 		$scope.currSimStatus = null;
 		
 		//
  		// fetch the simulation status (first ?)
  		//
		$scope.refreshSimStatus = function() {
			$http.get('/simstatus?maxRows=200&hasData=all&active=on&completed=on&failed=on&stopped=on')
				.success(function(data){
    				$scope.simStatuses = data;
   				})
  				.error(function(msg){
  					$scope.simStatuses = [ {name: "bad"} ];
  				});
  		};
  		
  		$scope.refreshSimStatus();
  		
  		$scope.$on(AUTH_EVENTS.logoutSuccess, $scope.refreshSimStatus);
  		$scope.$on(AUTH_EVENTS.loginSuccess, $scope.refreshSimStatus);

  		$scope.back = function() {
//			if ($scope.currSimulation.name){
//				$scope.currSimulation = {};
//			}else if ($scope.currBiomodel.name){
//				$scope.currBiomodel = {};
//			}
  		};
  			
/*
   		$scope.showBiomodels = function() {
  			console.log("show list of biomodels");
  			$scope.currBiomodel = {};
  			$scope.currSimulation = {};
  		};
*/
  		
/*
		$scope.clickBiomodelRow = function(bm) {
			console.log("clicked biomodel "+bm.name);
			$scope.currBiomodel = bm;
			$scope.currSimulation = {};
		};
*/

		$scope.clickSimStatusRow = function(sim) {
			console.log("clicked simulation "+sim.simRep.name);
			$scope.currSimStatus = sim;
		};

  		
	}]);

})();