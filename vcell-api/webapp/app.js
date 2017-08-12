(function() {
	var app = angular.module('app', ['app.models','app.sims','app.auth']);
	
	app.controller('VcellController', ["$scope","$rootScope","Session","AUTH_EVENTS","USER_ROLES","AuthService","$http", function($scope,$rootScope,Session,AUTH_EVENTS,USER_ROLES,AuthService,$http) {

		//-----------------------------------------------------
		// authentication
		//-----------------------------------------------------
		$scope.currUser = null;
		$scope.userRoles = USER_ROLES;
		$scope.authUser = AuthService.authUser;
		$scope.isAuthorized = AuthService.isAuthorized;
		$scope.logout = function() {
			AuthService.logout($scope);
		}
		$scope.session = Session;
		
		// must call setter to circumvent shadow copy in other scopes
		$scope.currentUser = function (newName) {
			if (angular.isDefined(newName)) {
				$scope.currUser = newName;
			}
			return $scope.currUser;
		};
		$scope.showLoginDialog = function() {
			$rootScope.$broadcast(AUTH_EVENTS.notAuthenticated);
		};
		
		
		
		//-----------------------------------------------------
		// controlling the tab - gross navigation
		//-----------------------------------------------------		
		$scope.tab = 1;

		$scope.isSet = function(checkTab) {
			return $scope.tab === checkTab;
		};

		$scope.setTab = function(activeTab) {
			$scope.tab = activeTab;
		};
   			
	}]);

})();