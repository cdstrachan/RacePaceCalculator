var app = angular.module('PaceChartCalculator', [])
.filter('formatTime', function ($filter) {
	return function (time, format) {
	    var parts = time.split(':');
	    var date = new Date(0, 0, 0, parts[0], parts[1], parts[2]);
	    return $filter('date')(date, format || 'mm:ss');
	};
	})

app.controller('Pacecalculator', function($scope, $http) {
    $scope.ErrorMessage = "";
    $http.get('/pacechartbootstrap?distance=10').
    then(function(response) {
        $scope.paceChartInput = response.data;
    }).catch(function(e){
       console.log("Error creating initial config data",e);
       $scope.CriticalErrorMessage = "Error connecting to server";
    });
    
        $scope.createChart = function () {
            $scope.ErrorMessage = "";
            $scope.paceChart = null;
            $scope.loadingmessage = "Loading";
            $http.post('/pacechart', $scope.paceChartInput).then(function (response) {
                $scope.paceChart = response.data;
                $scope.loadingmessage = "Done. Scroll down to view pacecharts.";
            }).catch(function(e){
                console.log("Error creating chart",e);
                $scope.CriticalErrorMessage = "Error connecting to server";
             })
        }

        $scope.createChartTemplate = function (distance) {
            $scope.ErrorMessage = "";
            $scope.paceChart = null;
            //var sQuery = '/pacecharttemplate;//?distance=' + distance;
            //$http.post(sQuery).then(function (response) {
            $http.post('/pacecharttemplate', $scope.paceChartInput).then(function (response) {
                $scope.paceChartInput = response.data;
            }).catch(function(e){
                console.log("Error creating distances",e);
                $scope.CriticalErrorMessage = "Error connecting to server";
             })
        }
       
        
});