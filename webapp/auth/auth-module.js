(function() {
	var app = angular.module('app.auth', []);
	
	app.constant('AUTH_EVENTS', {
	  loginSuccess: 'auth-login-success',
	  loginFailed: 'auth-login-failed',
	  logoutSuccess: 'auth-logout-success',
	  sessionTimeout: 'auth-session-timeout',
	  notAuthenticated: 'auth-not-authenticated',
	  notAuthorized: 'auth-not-authorized',
	  requestLoginForm: 'auth-loginform-request'
	});
	
	var USER_ROLES = app.constant('USER_ROLES', {
	  all: '*',
	  user: 'user',
	  admin: 'admin',
	  editor: 'editor',
	  guest: 'guest'
	});
	
	app.factory('AuthService', function (AUTH_EVENTS, $rootScope, $http, Session) {
	  var authService = {};
	 
		authService.login = function(credentials) {
			return $http({
				method: 'POST',
				url: '/login',
				data: 'user='+credentials.username+"&password="+credentials.password+"&redirecturl=/webapp",
				headers: {'Content-Type': "application/x-www-form-urlencoded"}
			}).then(function (res) {
				if (res.status === 200){
					Session.create(credentials.username, USER_ROLES.user);
	    			$rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
					return Session.userId;
				}
	      	});
  		};
  		
  		authService.logout = function($scope) {
			$http.get('/logout')
				.success(function(data){
					Session.destroy();
	    			$rootScope.$broadcast(AUTH_EVENTS.logoutSuccess);
	   			});
  		};
  		
  		authService.authUser = function() { 
  			return Session.userId;
  		};

  		authService.isAuthenticated = function () {
  			return !!Session.userId;
  		};
	 
  		authService.isAuthorized = function (authorizedRoles) {
  			if (!angular.isArray(authorizedRoles)) {
  				authorizedRoles = [authorizedRoles];
  			}
  			return (authService.isAuthenticated() && authorizedRoles.indexOf(Session.userRole) !== -1);
  		};
	 
  		return authService;
	});
	
	app.service('Session', function () {
		this.userId = null;
		this.userRole = null;
	  this.create = function (userId, userRole) {
	    this.userId = userId;
	    this.userRole = userRole;
	  };
	  this.destroy = function () {
	    this.userId = null;
	    this.userRole = null;
	  };
	  return this;
	});
	
	
	app.controller('LoginController', [ "$scope", "$rootScope", "AUTH_EVENTS", "AuthService", 
		function ($scope, $rootScope, AUTH_EVENTS, AuthService) {
		  $scope.credentials = {
		    username: '',
		    password: ''
		  };
		  $scope.login = function (credentials) {
		    AuthService.login(credentials).then(function (user) {
		      $rootScope.$broadcast(AUTH_EVENTS.loginSuccess);
		      $scope.currentUser(user);
		    }, function () {
		      $rootScope.$broadcast(AUTH_EVENTS.loginFailed);
		    });
		  };
		}]);
		
	app.config(function ($httpProvider) {
	  $httpProvider.interceptors.push([
	    '$injector',
	    function ($injector) {
	      return $injector.get('AuthInterceptor');
	    }
	  ]);
	});
	
	app.factory('AuthInterceptor', function ($rootScope, $q, AUTH_EVENTS) {
	  return {
	    responseError: function (response) { 
	      $rootScope.$broadcast({
	        401: AUTH_EVENTS.notAuthenticated,
	        403: AUTH_EVENTS.notAuthorized,
	        419: AUTH_EVENTS.sessionTimeout,
	        440: AUTH_EVENTS.sessionTimeout
	      }[response.status], response);
	      return $q.reject(response);
	    }
	  };
	});
	
})();