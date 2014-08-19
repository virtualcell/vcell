(function() {
	var app = angular.module('vcell', ['vcell-directives']);
	
	app.controller('VcellController', ["$http", function($http) {
		var vcell = this;
		vcell.biomodels = [];
 		vcell.tab = 1;
 		vcell.currBiomodel = {};
 		vcell.currSimulation = {};
 		vcell.user = {};
 		
		this.isSet = function(checkTab) {
			return vcell.tab === checkTab;
		};

		this.setTab = function(activeTab) {
			vcell.tab = activeTab;
		};

		$http.get('/biomodel')
			.success(function(data){
    			vcell.biomodels = data;
   			})
  			.error(function(msg){
  				vcell.biomodels = [ {name: "bad"} ];
  			});
  			
  		vcell.login = function() {
  			vcell.user = {name: "schaff"}
  		};

  		vcell.logout = function() {
  			vcell.user = {};
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