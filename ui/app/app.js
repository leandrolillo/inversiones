'use strict';

// Declare app level module which depends on views, and core components
angular.module('inversiones', [
    'ngRoute',
    'inversiones.tenencias',
    'inversiones.view1',
    'inversiones.view2',
    'inversiones.version'
]).config(['$locationProvider', '$routeProvider', function ($locationProvider, $routeProvider) {
    $locationProvider.hashPrefix('!');

    $routeProvider.otherwise({redirectTo: '/view1'});
}]);
