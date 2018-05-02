var tcpproxy = angular.module('tcpproxy', ['ngRoute','ui.materialize','pageslide-directive','chart.js'])

tcpproxy.config(function ($routeProvider, $httpProvider) {
    $routeProvider
        .when('/', {
            templateUrl: 'views/home.html',
            controller: 'home'
        }).when('/proxy/:id',{
            templateUrl: 'views/proxy.html',
            controller: 'proxy'
    })
    $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
});