'use strict';

describe('inversiones.version module', function() {
  beforeEach(module('inversiones.version'));

  describe('version service', function() {
    it('should return current version', inject(function(version) {
      expect(version).toEqual('0.1');
    }));
  });
});
