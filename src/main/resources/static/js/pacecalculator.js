var app = angular.module('PaceChartCalculator', [])
    .filter('formatTime', function ($filter) {
        return function (time, format) {
            var parts = time.split(':');
            var date = new Date(0, 0, 0, parts[0], parts[1], parts[2]);
            return $filter('date')(date, format || 'mm:ss');
        };
    })

app.controller('Pacecalculator', function ($scope, $http, $window) {
    $scope.ErrorMessage = "";
    $scope.CriticalErrorMessage = "";
    $('[data-toggle="tooltip"]').tooltip();
    $http.get('/pacechartbootstrap?template=10').
        then(function (response) {
            $scope.paceChartInput = response.data;
        }).catch(function (e) {
            console.log("Error creating initial config data", e);
            $scope.CriticalErrorMessage = "Error connecting to server";
        });

    $scope.createChart = function (raceName) {
        console.log("ChartCreated,", raceName);
        $window.gtag('event', 'ChartCreated', {
            'event_label': raceName
        });   
        console.log("Done,", raceName);
        $scope.ErrorMessage = "";
        $scope.CriticalErrorMessage = "";
        $scope.paceChart = null;
        $scope.loadingmessage = "Loading";
        $http.post('/pacechart', $scope.paceChartInput).then(function (response) {
            $scope.paceChart = response.data;
            $scope.loadingmessage = "Done. Scroll down to view pacecharts.";
        }).catch(function (e) {
            console.log("Error creating chart", e);
            $scope.CriticalErrorMessage = "Error connecting to server";
        })
    }

    $scope.createChartExcel = function (raceName) {
        var xhr = new XMLHttpRequest();
        console.log("ChartCreatedInExcel,", raceName);
        $window.gtag('event', 'ChartCreatedInExcel', {
            'event_label': raceName
        });
        console.log("Done,", raceName);
        xhr.open("POST", '/pacechartexcel');
        xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
        xhr.responseType = "arraybuffer";

        xhr.onload = function () {
            if (this.status === 200) {    
                var blob = new Blob([xhr.response], { type: "application/xlsx" });
                var a = document.createElement('a');
                a.href = URL.createObjectURL(blob);
                a.download = "pacechart.xlsx";
                a.click();
            }
        };
        xhr.send(JSON.stringify($scope.paceChartInput));
        $scope.loadingmessage = "Excel chart downloaded.";
    };

    $scope.createChartPreload = function (templateName) {
        $scope.ErrorMessage = "";
        $scope.CriticalErrorMessage = "";
        $scope.paceChart = null;
        $http.post('/pacechartpreload', $scope.paceChartInput).then(function (response) {
        $scope.paceChartInput = response.data;
        }).catch(function (e) {
            console.log("Error preloading", e);
            $scope.CriticalErrorMessage = "Error connecting to server";
        })
    }

    $scope.createChartTemplate = function (distance) {
        $scope.ErrorMessage = "";
        $scope.CriticalErrorMessage = "";
        $scope.paceChart = null;
        $http.post('/pacecharttemplate', $scope.paceChartInput).then(function (response) {
            //$window.ga('send', 'event', 'distanceset', distance);
            $scope.paceChartInput = response.data;
        }).catch(function (e) {
            console.log("Error creating template", e);
            $scope.CriticalErrorMessage = "Error connecting to server";
        })
    };


});