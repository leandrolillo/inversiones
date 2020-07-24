'use strict';
angular.module('inversiones.tenencias', []).component('tenencias',
    {
        templateUrl: 'components/tenencias/tenencias.template.html',
        controller: function TenenciasController($http) {
            var self = this;

            $http.get("http://localhost:8080/agregaciones/inversiones").then(
                function(response) {
                    self.inversiones = response.data
                    console.log(self.inversiones)
                }
            )
        }
    });