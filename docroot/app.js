(function() {
	var app = angular.module('vcell', ['vcell-directives']);
	
	app.controller('VcellController', ["$http", function($http) {
		var vcell = this;

 		vcell.tab = 1;

 		vcell.credentials = {};
 		vcell.user = {};

		vcell.biomodels = [];
 		vcell.currBiomodel = {};
 		vcell.currSimulation = {};

 		vcell.simStatuses = [];
 		vcell.currSimStatus = {};
 		
		this.isSet = function(checkTab) {
			return vcell.tab === checkTab;
		};

		this.setTab = function(activeTab) {
			vcell.tab = activeTab;
		};
		
		//
		// fetch the biomodels (first 10)
		//
		$http.get('/biomodel')
			.success(function(data){
    			vcell.biomodels = data;
   			})
  			.error(function(msg){
  				vcell.biomodels = [ {name: "bad"} ];
  			});
  			
  			
  		//
  		// fetch the simulation status (first ?)
  		//
		$http.get('/simstatus?maxRows=200&hasData=all&active=on&completed=on&failed=on&stopped=on')
			.success(function(data){
    			vcell.simStatuses = data;
   			})
  			.error(function(msg){
  				vcell.simStatuses = [ {name: "bad"} ];
  			});


  		vcell.login = function(credentials) {
			$http({
				method: 'POST',
				url: '/login',
				data: 'user='+vcell.credentials.username+"&password="+vcell.credentials.password+"&redirecturl=/webapp",
				headers: {'Content-Type': "application/x-www-form-urlencoded"}
			}).success(function(data){
	    		vcell.user = { name: vcell.credentials.username };
	    		console.log("success "+data);
	   		}).error(function(msg){
	   			vcell.user = {};
	   		});
  		};

  		vcell.logout = function() {
			$http.get('/logout')
				.success(function(data){
	    			vcell.user = {};
	   			});
  		};
  		
  		vcell.back = function() {
  			if (vcell.tab === 1){
  				if (vcell.currSimulation.name){
  					vcell.currSimulation = {};
  				}else if (vcell.currBiomodel.name){
  					vcell.currBiomodel = {};
  				}
  			}
  		};
  			
  		vcell.showBiomodels = function() {
  			console.log("show list of biomodels");
  			vcell.currBiomodel = {};
  			vcell.currSimulation = {};
  		};
		
		vcell.clickBiomodelRow = function(bm) {
			console.log("clicked biomodel "+bm.name);
			vcell.currBiomodel = bm;
			vcell.currSimulation = {};
		};
		vcell.clickSimulationRow = function(sim) {
			console.log("clicked simulation "+sim.name);
			vcell.currSimulation = sim;
		};
	}]);

})();