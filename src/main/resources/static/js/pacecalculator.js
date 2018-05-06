var app = angular.module('PaceChartCalculator', []);


app.controller('Pacecalculator', function($scope, $http) {
    $scope.ErrorMessage = "";
    $http.get('/pacecharttemplate?distance=10').
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
                $scope.loadingmessage = "";
            }).catch(function(e){
                console.log("Error creating chart",e);
                $scope.CriticalErrorMessage = "Error connecting to server";
             })
        }

        $scope.createChartTemplate = function (distance) {
            $scope.ErrorMessage = "";
            $scope.paceChart = null;
            var sQuery = '/pacecharttemplate?distance=' + distance;
            $http.get(sQuery).then(function (response) {
                $scope.paceChartInput = response.data;
            }).catch(function(e){
                console.log("Error creating distances",e);
                $scope.CriticalErrorMessage = "Error connecting to server";
             })
        }

});