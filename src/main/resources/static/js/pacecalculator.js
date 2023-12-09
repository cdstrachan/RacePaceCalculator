var app = angular.module('PaceChartCalculator', [])
    .filter('formatTime', function ($filter) {
        return function (time, format) {
            var parts = time.split(':');
            var date = new Date(0, 0, 0, parts[0], parts[1], parts[2]);
            return $filter('date')(date, format || 'mm:ss');
        };
    })

app.controller('Pacecalculator', function ($scope, $http, $window) {
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
        $scope.CriticalErrorMessage = "";
        $scope.paceChart = null;
        $scope.loadingmessage = "Loading";
        $http.post('/pacechart', $scope.paceChartInput).then(function (response) {
            $scope.paceChart = response.data;
            console.log(response.data);
            // if validation messages are returned
            if ($scope.paceChart.validationErrorMessages) {
                $scope.loadingmessage = "Error creating online chart";
                console.log("Error creating chart", $scope.paceChart.validationErrorMessages);
            }
            else {
                $scope.loadingmessage = "Done. Scroll down to view pacecharts.";
                console.log("Done,", raceName);
            }
        }).catch(function (e) {
            console.log("Error creating chart", e);
            $scope.CriticalErrorMessage = "Error connecting to server";
        })
    }

    $scope.createChartExcel = function (raceName) {
        console.log("ChartCreatedExcel,", raceName);
        $window.gtag('event', 'ChartCreatedExcel', {
            'event_label': raceName
        });   
        $scope.CriticalErrorMessage = "";
        $scope.paceChart = null;
        $scope.loadingmessage = "Loading";
        $http.responseType = 'blob';
        
        $http({
            method: 'POST',
            url: '/pacechartexcel',
            data: $scope.paceChartInput,
            responseType: 'blob'
        }).then(async function (response) {
            // print response headers for status OK or invalid
            console.log(response.headers('result'));
            if (response.headers('result') === 'OK') {   
                // ok returned
                console.log(response.status) 
                var binaryData = [];
                binaryData.push(response.data);
                var blob = new Blob((binaryData), { type: "'application/vnd.openxmlformat-officedocument.spreadsheetml.sheet;" });
                var a = document.createElement('a');
                a.href = URL.createObjectURL(blob);
                a.download = "pacechart.xlsx";
                a.click();
                console.log("Done,", raceName);
                $scope.loadingmessage = "Excel chart downloaded.";
            }
            if (response.headers('result') === 'Invalid') {  
                console.log(response.status) 
                $scope.paceChart = JSON.parse(await response.data.text());
                $scope.loadingmessage = "Error creating excel chart....";
            }
        }).then(function() {
            // do nothing
        });
    }

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