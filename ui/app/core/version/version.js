'use strict';

angular.module('inversiones.version', [
  'inversiones.version.interpolate-filter',
  'inversiones.version.version-directive'
])

.value('version', '0.1');
