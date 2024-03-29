var app = angular.module('PaceChartCalculator', [])
    .filter('formatTime', function ($filter) {
        return function (time, format) {
            var parts = time.split(':');
            var date = new Date(0, 0, 0, parts[0], parts[1], parts[2]);
            return $filter('date')(date, format || 'mm:ss');
        };
    })

app.controller('Pacecalculator', function ($scope, $http, $window) {
    console.log("Bootstrapping starting");
    $scope.CriticalErrorMessage = "";
    $window.CriticalErrorMessage.style.display = "none";
    $('[data-toggle="tooltip"]').tooltip();
    $http.get('/pacechartbootstrap?template=10').
        then(function (response) {
            $scope.paceChartInput = response.data;
            console.log("Bootstrapping complete");
        }).catch(function (e) {
            console.log("Error creating initial config data");
            $scope.CriticalErrorMessage = "Error connecting to server";
            $window.CriticalErrorMessage.style.display = "block";
        });

    $scope.createChart = function (raceName) {
        console.log("Enter ChartCreated,", raceName);
        $window.gtag('event', 'ChartCreated', {
            'event_label': raceName
        });   
        $window.LoadingDiv.style.display = "block";
        $window.CriticalErrorMessage.style.display = "none";
        $scope.CriticalErrorMessage = "";
        $scope.paceChart = null;
        $scope.loadingmessage = "Loading";
        $window.LoadingMessage.style.display = "block";
        $http.post('/pacechart', $scope.paceChartInput).then(function (response) {
            $scope.paceChart = response.data;
            console.log(response.data);
            // if validation messages are returned
            if ($scope.paceChart.validationErrorMessages) {
                $scope.loadingmessage = "Error creating online chart";
                $window.LoadingDiv.style.display = "none";
                console.log("Error creating chart", $scope.paceChart.validationErrorMessages);
            }
            else {
                $scope.loadingmessage = "Done. Scroll down to view pacecharts.";
                $window.LoadingDiv.style.display = "none";
                console.log("Exit creating chart,", raceName);
            }
        }).catch(function (e) {
            $scope.CriticalErrorMessage = "Error connecting to server";
            $window.CriticalErrorMessage.style.display = "block";
            $window.LoadingDiv.style.display = "none";
            console.log("Error creating chart", e);
        })
    }

    $scope.createChartExcel = function (raceName) {
        console.log("Enter ChartCreatedExcel,", raceName);
        $window.gtag('event', 'ChartCreatedExcel', {
            'event_label': raceName
        });   
        // show divLoading
        $window.LoadingDiv.style.display = "block";
        $scope.CriticalErrorMessage = "";
        $window.CriticalErrorMessage.style.display = "none";
        $scope.paceChart = null;
        $scope.loadingmessage = "Loading";
        $window.LoadingMessage.style.display = "block";
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
                var binaryData = [];
                binaryData.push(response.data);
                var blob = new Blob((binaryData), { type: "'application/vnd.openxmlformat-officedocument.spreadsheetml.sheet;" });
                var a = document.createElement('a');
                a.href = URL.createObjectURL(blob);
                a.download = "pacechart.xlsx";
                a.click();
                $scope.loadingmessage = "Excel chart downloaded.";
                console.log("Exit ChartCreatedExcel,", raceName);
                $window.LoadingDiv.style.display = "none";
            }
            if (response.headers('result') === 'Invalid') {  
                $scope.paceChart = JSON.parse(await response.data.text());
                $scope.loadingmessage = "Error creating excel chart.";
                console.log("Error creating excel chart");
                $window.LoadingDiv.style.display = "none";
            }
        }).catch(function (e) {
            $scope.CriticalErrorMessage = "Error connecting to server";
            $window.CriticalErrorMessage.style.display = "block";
            $window.LoadingDiv.style.display = "none";
            console.log("Error creating chart", e)
        });
    }

    $scope.createChartPreload = function () {
        console.log("Enter preload,", $scope.paceChartInput.raceTemplateName);
        $scope.CriticalErrorMessage = "";
        $window.CriticalErrorMessage.style.display = "none";
        $scope.paceChart = null;
        $http.post('/pacechartpreload', $scope.paceChartInput).then(function (response) {
        $scope.paceChartInput = response.data;
        console.log("Exit preload,", $scope.paceChartInput.raceTemplateName);
        }).catch(function (e) {
            console.log("Error preloading", e);
            $window.CriticalErrorMessage.style.display = "block";
            $scope.CriticalErrorMessage = "Error connecting to server";
        })
    }

    $scope.createChartTemplate = function (distance) {
        console.log("Enter createChartTemplate", distance);
        $scope.CriticalErrorMessage = "";
        $scope.paceChart = null;
        $http.post('/pacecharttemplate', $scope.paceChartInput).then(function (response) {
            //$window.ga('send', 'event', 'distanceset', distance);
            $scope.paceChartInput = response.data;
            console.log("Exit createChartTemplate", distance);
        }).catch(function (e) {
            console.log("Error creating template", e);
            $window.CriticalErrorMessage.style.display = "block";
            $scope.CriticalErrorMessage = "Error connecting to server";
        })
    };


});