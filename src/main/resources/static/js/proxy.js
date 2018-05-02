var tcpproxy = angular.module('tcpproxy');
tcpproxy.controller('proxy', function($scope, $routeParams, $http, $interval){

    $scope.promise = {}
    $scope.stats = {}
    $scope.last = {}
    $scope.console = true;
    $scope.received =[[]];
    $scope.sent = [[]];
    $scope.connectionCount = [[]];
    $scope.labels= [];
    $scope.series = ['Bytes Received'];
    $scope.connColors = [{ // dark grey
        fillColor: 'rgba(12,208,9,0.2)',
        strokeColor: 'rgba(12,208,9,1)',
        pointColor: 'rgba(12,208,9,1)',
        pointStrokeColor: '#fff',
        pointHighlightFill: '#fff',
        pointHighlightStroke: 'rgba(12,208,9,1)'
    }]
    $scope.sentColors = [{ // dark grey
        fillColor: 'rgba(208,12,9,0.2)',
        strokeColor: 'rgba(208,12,9,1)',
        pointColor: 'rgba(208,12,9,1)',
        pointStrokeColor: '#fff',
        pointHighlightFill: '#fff',
        pointHighlightStroke: 'rgba(208,12,9,1)'
    }]
    $scope.maxPoints = 100;
    $scope.options = {
        animation: false,
        showScale: true,
        showTooltips: false,
        pointDot: false,
        datasetStrokeWidth: 0.5
    };

    $http.get('/servers/'+$routeParams.id).then(function success(response){
        $scope.proxy = response.data;

    }, function error(response){
        console.error(response);
    });

    $scope.updateStats = function(){
        $http.get('/servers/'+$scope.proxy.id+'/stats').then(function success(response){

            if($scope.received.length && $scope.received[0].length >= $scope.maxPoints){
                $scope.labels = $scope.labels.slice(1);
                $scope.sent[0] = $scope.sent[0].slice(1);
                $scope.received[0] = $scope.received[0].slice(1);
                $scope.connectionCount[0] = $scope.connectionCount[0].slice(1);
            }

            if(typeof $scope.last.bytesSent != 'undefined'){
                stats = response.data;
                deltaReceived = stats.bytesReceived - $scope.last.bytesReceived;
                deltaSent = stats.bytesSent - $scope.last.bytesSent;
                $scope.labels.push('');
                $scope.received[0].push(stats.bytesReceived/1024);
                $scope.sent[0].push(stats.bytesSent/1024);
                $scope.connectionCount[0].push(stats.connectionCount);

            }

            $scope.last = response.data;

        }, function error(response){
            console.error(response);
            $interval.cancel($scope.promise);
        });
    }

    $scope.toggleStatus = function($event){
        $http.post('/servers/'+$scope.proxy.id+'/status',{active: $event}).then(function success(response){

        }, function error(response){
            console.error(response);
        });
    }

    $scope.applyChanges = function(){
        $http.post('/servers/'+$scope.proxy.id+'/traffic', $scope.proxy.qos.trafficShaping).then(function success(response){

        },function error(response){

        });
    }

    $scope.promise = $interval(function(){$scope.updateStats()},1000);






});