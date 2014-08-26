(function() {
	var app = angular.module('app.models');
	
	app.controller('ModelController', ["$scope","$rootScope","Session","AUTH_EVENTS","USER_ROLES","AuthService","$http", function($scope,$rootScope,Session,AUTH_EVENTS,USER_ROLES,AuthService,$http) {

		$scope.biomodels = [];
 		$scope.currBiomodel = {};
 		$scope.currSimulation = {};
		
		//
		// fetch the biomodels (first 10)
		//
		$scope.refreshBiomodel = function() {
			$http.get('/biomodel')
				.success(function(data){
    				$scope.biomodels = data;
   				})
  				.error(function(msg){
  					$scope.biomodels = [ {name: "bad"} ];
  				});
  		};
  			
   		$scope.refreshBiomodel();
  		
  		$scope.$on(AUTH_EVENTS.logoutSuccess, $scope.refreshBiomodel);
  		$scope.$on(AUTH_EVENTS.loginSuccess, $scope.refreshBiomodel);

  		$scope.back = function() {
			if ($scope.currSimulation.name){
				$scope.currSimulation = {};
			}else if ($scope.currBiomodel.name){
				$scope.currBiomodel = {};
			}
  		};
  			
  		$scope.showBiomodels = function() {
  			console.log("show list of biomodels");
  			$scope.currBiomodel = {};
  			$scope.currSimulation = {};
  		};
		
		$scope.clickBiomodelRow = function(bm) {
			console.log("clicked biomodel "+bm.name);
			$scope.currBiomodel = bm;
			$scope.currSimulation = {};
		};
		
		$scope.clickSimulationRow = function(sim) {
			console.log("clicked simulation "+sim.name);
			$scope.currSimulation = sim;
		};
	}]);

})();