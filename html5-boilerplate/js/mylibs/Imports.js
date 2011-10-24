function Imports() {
    var self = {};
	self.bindImports = function(obj, referenceMap) {
	    obj.imports = {};
	    var objName = undefined;
	    var objVal = undefined;
	    for (objName in referenceMap) {
	        objVal = referenceMap[objName];
	        if (obj === objVal) { // bind to self directly
	            obj.imports[objName] = obj;
	        }
	        else { // bind to imported obj
	            obj.imports[objName] = objVal;
	        }
	    }
	};
	self.crossImport = function(referenceMap) {
	    var objName = undefined;
	    var objVal = undefined;
	    for (objName in referenceMap) {
	        objVal = referenceMap[objName];
	        self.bindImports(objVal, referenceMap);
	    }
	};
    return self;
}
