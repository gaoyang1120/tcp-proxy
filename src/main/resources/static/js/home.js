var tcpproxy = angular.module('tcpproxy');

tcpproxy.controller('home',function($scope, $http){
    $scope.proxies = [];
    $scope.addProxy = function(){
        $http.post('/servers',$scope.proxy).then(function success(response){
            $scope.proxies.push(response.data);
            $scope.proxy = {};
            $scope.proxyForm.$setPristine();
            $scope.$apply();
        },
        function error(response){
            console.error(response);
        });
    }
    $http.get("/servers").then(function success(response){
        angular.forEach(response.data,function(p,key){
            $scope.proxies.push(p);
        }, function error(response){

        })
    });

    $scope.toggleStatus = function(id,$event){
        $http.post('/servers/'+id+'/status',{active: $event}).then(function success(response){

        }, function error(response){
            console.error(response);
        });
    }
    $scope.toggleDebug = function(id,$event){
        $http.post('/servers/'+id+'/debug',{debug: $event}).then(function success(response){

        }, function error(response){
            console.error(response);
        });
    }

    $scope.deleteServer = function(id){
        $http.delete('/servers/'+id).then(function success(response){
            $scope.proxies.forEach(function(p){
                if(p.id==id)
                    p.delete=true;
            });
        }, function error(response){
            console.error(response);
        });
    }

});