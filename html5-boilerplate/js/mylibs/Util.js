function Util() {
			var self = {};
			self.keyCache = {};
			self.pages = [ 'loading', 'searchlistings', 'searchusers',
					'comments', 'profile', 'listing', 'bids', 'login', 'faq',
					'contact', 'disclaimer' ];
			self.displayPage = function(page) {
				var i;
				var sel;
				var foundPage = false;
				for (i = 0; i < self.pages.length; i++) {
				    if (page === self.pages[i]) {
				        foundPage = true;
				    }
				}
				if (!foundPage) {
				    page = 'searchlistings';
				}
				for (i = 0; i < self.pages.length; i++) { // hide all
					sel = '#' + self.pages[i] + 'page';
					if (self.pages[i] !== page && $(sel).is(':visible')) {
						$(sel).hide();
					}
				}
				sel = '#' + page + 'page';
				if (!$(sel).is(':visible')) {
					if ($('#mainbody').hasClass('body-narrow')) {
						$(sel).show();
					} else {
						$(sel).show();
					}
				}
				self.imports.header.bindLinks();
				if (page === 'searchlistings') {
				    self.imports.searchListings.bindLinks();
				}
				else if (page === 'searchusers') {
				    self.imports.searchUsers.bindLinks();
				}
			};
			self.displayFaq = function() {
				self.displayPage('faq');
			};
			self.displayDisclaimer = function() {
				self.displayPage('disclaimer');
			};
			self.displayContact = function() {
				self.displayPage('contact');
			};
			self.constructNumericDate = function(year, month, day) {
				var date = '' + year + (month < 10 ? '0' : '') + month
						+ (day < 10 ? '0' : '') + day;
				return date;
			};
			self.numericFromDateObj = function(date) {
				if (date)
					return self.constructNumericDate(date.getFullYear(), date
							.getUTCMonth(), date.getUTCDate());
			};
			self.epochToNumericDate = function(epoch) {
				var date;
				if (epoch) {
					date = new Date(epoch);
					return self.numericFromDateObj(date);
				}
			};
			self.today = function() {
				if (!self.todayCache) {
					var today = new Date();
					var numericDate = self.constructNumericDate(today
							.getFullYear(), today.getMonth() + 1, today
							.getDate());
					self.todayCache = numericDate;
				}
				return self.todayCache;
			};
			self.todayPlusDays = function(days) {
				var today = new Date();
				today.setDate(today.getDate() + days);
				var numericDate = self.numericFromDateObj(today);
				return numericDate;
			};
			self.randomElement = function(array) {
				return array[Math.floor(Math.random() * array.length)];
			};
			self.randomKey = function(name, assocArray) {
				if (!self.keyCache[name]) { // caching
					self.keyCache[name] = [];
					for ( var key in assocArray) {
						self.keyCache[name].push(key);
					}
				}
				return self.randomElement(self.keyCache[name]);
			};
			self.ucFirst = function(string) {
				return string.charAt(0).toUpperCase() + string.slice(1);
			};
			self.formatDate = function(yyyymmdd) {
				var year = yyyymmdd.substr(0, 4);
				var month = yyyymmdd.substr(4, 2);
				var day = yyyymmdd.substr(6, 2);
				return year + '-' + month + '-' + day;
			};
			self.addCommas = function(num) {
				var nStr = num + '';
				var x = nStr.split('.');
				var x1 = x[0];
				var x2 = x.length > 1 ? '.' + x[1] : '';
				var rgx = /(\d+)(\d{3})/;
				while (rgx.test(x1)) {
					x1 = x1.replace(rgx, '$1' + ',' + '$2');
				}
				return x1 + x2;
			};
			self.generateGuid = function() { // rfc4122 compliant
				return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g,
						function(c) {
							var r = Math.random() * 16 | 0, v = c == 'x' ? r
									: (r & 0x3 | 0x8);
							return v.toString(16);
						});
			};
			self.guessWidth = function() {
				var width1 = document.documentElement.clientWidth || 1280;
				var width2 = window.innerWidth || 1280;
				var width3 = screen.availWidth || 1280;
				return Math.min(width1, width2, width3);
			};
			self.applyResizer = function(resizerCallback) {
				resizerCallback(self.guessWidth());
				$(window).resize(function() {
					resizerCallback(self.guessWidth());
				});
			};
			self.setHash = function(objectName, methodName, argArray) {
				var methodCall = objectName + '.' + methodName;
				var argStr = argArray ? $.JSON.encode(argArray) : '';
				var hashStr = '#' + methodCall + '|' + argStr;
				location.hash = hashStr;
			};
			self.manualCall = function(objectName, methodName, argArray) {
				var object, method, args;
				if (objectName && self.imports[objectName]
						&& self.imports[objectName][methodName]) {
					object = self.imports[objectName];
					method = methodName;
				} else {
					object = self.imports.searchListings;
					method = 'doTopSearch';
				}
				args = argArray || [];
				(object[method]).apply(object, args);
			};
			self.hashedCall = function(objectName, methodName, argArray) {
				self.setHash(objectName, methodName, argArray);
				self.manualCall(objectName, methodName, argArray);
			};
			self.hashchange = function() { // use java eval-type statement to respond to history events
				var hash = location.hash;
				var methodCall = hash.replace(/^#/, '');
				var objectStr = methodCall.replace(/\..*$/, '');
				var methodStr = methodCall.replace(/^.*\./, '').replace(
						/\|.*$/, '');
				var methodArgsStr = methodCall.replace(/^.*\|/, '');
				var methodArgs = methodArgsStr != '' ? $.JSON
						.decode(methodArgsStr) : [];
				self.manualCall(objectStr, methodStr, methodArgs);
			};
			return self;
}
