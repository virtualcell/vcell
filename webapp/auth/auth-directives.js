(function() {
	var app = angular.module('app.auth');
	
    app.directive("vcellLoginbar", function() {
        return {
          restrict: "E",
          templateUrl: "/webapp/auth/vcell-loginbar.html",
        };
    });

	app.directive('loginDialog', ["AUTH_EVENTS", function (AUTH_EVENTS) {
		return {
			restrict: 'A',
			template: '<div ng-if="visible" ng-include="\'/webapp/auth/login-form.html\'">',
			link: function (scope) {
				var showDialog = function () {
					scope.visible = true;
				};
				var hideDialog = function () {
					scope.visible = false;
				};

				scope.visible = false;
				scope.$on(AUTH_EVENTS.requestLoginForm, showDialog);
				scope.$on(AUTH_EVENTS.notAuthenticated, showDialog);
				scope.$on(AUTH_EVENTS.sessionTimeout, showDialog);
				scope.$on(AUTH_EVENTS.loginSuccess, hideDialog);
			}
		}
		
  		}]);

})();