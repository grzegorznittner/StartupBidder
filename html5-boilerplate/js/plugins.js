
// usage: log('inside coolFunc', this, arguments);
// paulirish.com/2009/log-a-lightweight-wrapper-for-consolelog/
window.log = function(){
  log.history = log.history || [];   // store logs to an array for reference
  log.history.push(arguments);
  if(this.console) {
    arguments.callee = arguments.callee.caller;
    var newarr = [].slice.call(arguments);
    (typeof console.log === 'object' ? log.apply.call(console.log, console, newarr) : console.log.apply(console, newarr));
  }
};

// make it safe to use console.log always
(function(b){function c(){}for(var d="assert,count,debug,dir,dirxml,error,exception,group,groupCollapsed,groupEnd,info,log,timeStamp,profile,profileEnd,time,timeEnd,trace,warn".split(","),a;a=d.pop();){b[a]=b[a]||c}})((function(){try
{console.log();return window.console;}catch(err){return window.console={};}})());


// place any jQuery/helper plugins in here, instead of separate, slower script files.

function Plugins() {
	/*
	 * jQuery hashchange event - v1.3 - 7/21/2010
	 * http://benalman.com/projects/jquery-hashchange-plugin/
	 *
	 * Copyright (c) 2010 "Cowboy" Ben Alman
	 * Dual licensed under the MIT and GPL licenses.
	 * http://benalman.com/about/license/
	 */
	(function($, e, b) {
		var f = undefined;
		var c = "hashchange", h = document, g = $.event.special, i = h.documentMode, d = "on"
				+ c in e
				&& (i === b || i > 7);
		function a(j) {
			j = j || location.href;
			return "#" + j.replace(/^[^#]*#?(.*)$/, "$1");
		}
		$.fn[c] = function(j) {
			return j ? this.bind(c, j) : this.trigger(c);
		};
		$.fn[c].delay = 50;
		g[c] = $.extend(g[c], {
			setup : function() {
				if (d) {
					return false;
				}
				$(f.start);
			},
			teardown : function() {
				if (d) {
					return false;
				}
				$(f.stop);
			}
		});
		f = (function() {
			var j = {}, p = undefined, m = a(), k = function(q) {
				return q;
			}, l = k, o = k;
			j.start = function() {
				p || n();
			};
			j.stop = function() {
				p && clearTimeout(p);
				p = b;
			};
			function n() {
				var r = a(), q = o(m);
				if (r !== m) {
					l(m = r, q);
					$(e).trigger(c);
				} else {
					if (q !== m) {
						location.href = location.href.replace(/#.*/, "") + q;
					}
				}
				p = setTimeout(n, $.fn[c].delay);
			}
			$.browser.msie
					&& !d
					&& (function() {
						var q = undefined;
						var r = undefined;
						j.start = function() {
							if (!q) {
								r = $.fn[c].src;
								r = r && r + a();
								q = $('<iframe tabindex="-1" title="empty"/>')
										.hide().one("load", function() {
											r || l(a());
											n();
										}).attr("src", r || "javascript:0")
										.insertAfter("body")[0].contentWindow;
								h.onpropertychange = function() {
									try {
										if (event.propertyName === "title") {
											q.document.title = h.title;
										}
									} catch (s) {
									}
								};
							}
						};
						j.stop = k;
						o = function() {
							return a(q.location.href);
						};
						l = function(v, s) {
							var u = q.document, t = $.fn[c].domain;
							if (v !== s) {
								u.title = h.title;
								u.open();
								t
										&& u.write('<script>document.domain="'
												+ t + '"<\/script>');
								u.close();
								q.location.hash = v;
							}
						};
					})();
			return j;
		})();
	})(jQuery, this);

	/*
	 * This document is licensed as free software under the terms of the
	 * MIT License: <a href="http://www.opensource.org/licenses/mit-license.php" title="http://www.opensource.org/licenses/mit-license.php">http://www.opensource.org/licenses/mit-license.php</a>
	 *
	 * Adapted by Rahul Singla.
	 *
	 * Brantley Harris wrote this plugin. It is based somewhat on the JSON.org
	 * website's <a href="http://www.json.org/json2.js" title="http://www.json.org/json2.js">http://www.json.org/json2.js</a>, which proclaims:
	 * "NO WARRANTY EXPRESSED OR IMPLIED. USE AT YOUR OWN RISK.", a sentiment that
	 * I uphold.
	 *
	 * It is also influenced heavily by MochiKit's serializeJSON, which is
	 * copyrighted 2005 by Bob Ippolito.
	 */

	/**
	 * jQuery.JSON.encode( json-serializble ) Converts the given argument into a
	 * JSON respresentation.
	 *
	 * If an object has a "toJSON" function, that will be used to get the
	 * representation. Non-integer/string keys are skipped in the object, as are
	 * keys that point to a function.
	 *
	 * json-serializble: The *thing* to be converted.
         */
        jQuery.JSON = {
		encode : function(o) {
			if (typeof (JSON) == 'object' && JSON.stringify)
				return JSON.stringify(o);

			var type = typeof (o);

			if (o === null)
				return "null";

			if (type == "undefined")
				return undefined;

			if (type == "number" || type == "boolean")
				return o + "";

			if (type == "string")
				return this.quoteString(o);

			if (type == 'object') {
				if (typeof o.toJSON == "function")
					return this.encode(o.toJSON());

				if (o.constructor === Date) {
					var month = o.getUTCMonth() + 1;
					if (month < 10)
						month = '0' + month;

					var day = o.getUTCDate();
					if (day < 10)
						day = '0' + day;

					var year = o.getUTCFullYear();

					var hours = o.getUTCHours();
					if (hours < 10)
						hours = '0' + hours;

					var minutes = o.getUTCMinutes();
					if (minutes < 10)
						minutes = '0' + minutes;

					var seconds = o.getUTCSeconds();
					if (seconds < 10)
						seconds = '0' + seconds;

					var milli = o.getUTCMilliseconds();
					if (milli < 100)
						milli = '0' + milli;
					if (milli < 10)
						milli = '0' + milli;

					return '"' + year + '-' + month + '-' + day + 'T' + hours
							+ ':' + minutes + ':' + seconds + '.' + milli
							+ 'Z"';
				}

				if (o.constructor === Array) {
					var ret = [];
					for ( var i = 0; i < o.length; i++)
						ret.push(this.encode(o[i]) || "null");

					return "[" + ret.join(",") + "]";
				}

				var pairs = [];
				for ( var k in o) {
					var name;
					var type = typeof k;

					if (type == "number")
						name = '"' + k + '"';
					else if (type == "string")
						name = this.quoteString(k);
					else
						continue; // skip non-string or number keys

					if (typeof o[k] == "function")
						continue; // skip pairs where the value is a function.

					var val = this.encode(o[k]);

					pairs.push(name + ":" + val);
				}

				return "{" + pairs.join(", ") + "}";
			}
		},

		/**
		 * jQuery.JSON.decode(src) Evaluates a given piece of json source.
		 */
		decode : function(src) {
			if (typeof (JSON) == 'object' && JSON.parse)
				return JSON.parse(src);
			return eval("(" + src + ")");
		},

		/**
		 * jQuery.JSON.decodeSecure(src) Evals JSON in a way that is *more* secure.
		 */
		decodeSecure : function(src) {
			if (typeof (JSON) == 'object' && JSON.parse)
				return JSON.parse(src);

			var filtered = src;
			filtered = filtered.replace(/\\["\\\/bfnrtu]/g, '@');
			filtered = filtered
					.replace(
							/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,
							']');
			filtered = filtered.replace(/(?:^|:|,)(?:\s*\[)+/g, '');

			if (/^[\],:{}\s]*$/.test(filtered))
				return eval("(" + src + ")");
			else
				throw new SyntaxError(
						"Error parsing JSON, source is not valid.");
		},

		/**
		 * jQuery.JSON.quoteString(string) Returns a string-repr of a string, escaping
		 * quotes intelligently. Mostly a support function for JSON.encode.
		 *
		 * Examples: >>> jQuery.JSON.quoteString("apple") "apple"
		 *
		 * >>> jQuery.JSON.quoteString('"Where are we going?", she asked.') "\"Where
		 * are we going?\", she asked."
		 */
		quoteString : function(string) {
			if (string.match(this._escapeable)) {
				return '"'
						+ string.replace(this._escapeable, function(a) {
							var c = this._meta[a];
							if (typeof c === 'string')
								return c;
							c = a.charCodeAt();
							return '\\u00' + Math.floor(c / 16).toString(16)
									+ (c % 16).toString(16);
						}) + '"';
			}
			return '"' + string + '"';
		},

		_escapeable : /["\\\x00-\x1f\x7f-\x9f]/g,

		_meta : {
			'\b' : '\\b',
			'\t' : '\\t',
			'\n' : '\\n',
			'\f' : '\\f',
			'\r' : '\\r',
			'"' : '\\"',
			'\\' : '\\\\'
		}
	};

	/**
	 * jQuery.UI.iPad plugin
	 * Copyright (c) 2010 Stephen von Takach
	 * licensed under MIT.
	 * Date: 27/8/2010
	 *
	 * http://code.google.com/p/jquery-ui-for-ipad-and-iphone/
	 */

	$(function() {
		//
		// Extend jQuery feature detection
		//
		$.extend($.support, {
			touch : typeof Touch == "object"
		});

		//
		// Hook up touch events
		//
		$.fn.addTouch = function() {
			if ($.support.touch) {
				this
						.each(function(i, el) {
							el.addEventListener("touchstart", iPadTouchHandler,
									false);
							el.addEventListener("touchmove", iPadTouchHandler,
									false);
							el.addEventListener("touchend", iPadTouchHandler,
									false);
							el.addEventListener("touchcancel",
									iPadTouchHandler, false);
						});
			}

			return this;
		};
	});

	var lastTap = null; // Holds last tapped element (so we can compare for double tap)
	var tapValid = false; // Are we still in the .6 second window where a double tap can occur
	var tapTimeout = null; // The timeout reference

	function cancelTap() {
		tapValid = false;
	}

	var rightClickPending = false; // Is a right click still feasible
	var rightClickEvent = null; // the original event
	var holdTimeout = null; // timeout reference
	var cancelMouseUp = false; // prevents a click from occuring as we want the context menu

	function cancelHold() {
		if (rightClickPending) {
			window.clearTimeout(holdTimeout);
			rightClickPending = false;
			rightClickEvent = null;
		}
	}

	function startHold(event) {
		if (rightClickPending)
			return;

		rightClickPending = true; // We could be performing a right click
		rightClickEvent = (event.changedTouches)[0];
		holdTimeout = window.setTimeout("doRightClick();", 800);
	}

	function doRightClick() {
		rightClickPending = false;

		//
		// We need to mouse up (as we were down)
		//
		var first = rightClickEvent, simulatedEvent = document
				.createEvent("MouseEvent");
		simulatedEvent.initMouseEvent("mouseup", true, true, window, 1,
				first.screenX, first.screenY, first.clientX, first.clientY,
				false, false, false, false, 0, null);
		first.target.dispatchEvent(simulatedEvent);

		//
		// emulate a right click
		//
		simulatedEvent = document.createEvent("MouseEvent");
		simulatedEvent.initMouseEvent("mousedown", true, true, window, 1,
				first.screenX, first.screenY, first.clientX, first.clientY,
				false, false, false, false, 2, null);
		first.target.dispatchEvent(simulatedEvent);

		//
		// Show a context menu
		//
		simulatedEvent = document.createEvent("MouseEvent");
		simulatedEvent.initMouseEvent("contextmenu", true, true, window, 1,
				first.screenX + 50, first.screenY + 5, first.clientX + 50,
				first.clientY + 5, false, false, false, false, 2, null);
		first.target.dispatchEvent(simulatedEvent);

		//
		// Note:: I don't mouse up the right click here however feel free to add if required
		//

		cancelMouseUp = true;
		rightClickEvent = null; // Release memory
	}

	//
	// mouse over event then mouse down
	//
	function iPadTouchStart(event) {
		var touches = event.changedTouches, first = touches[0], type = "mouseover", simulatedEvent = document
				.createEvent("MouseEvent");
		//
		// Mouse over first - I have live events attached on mouse over
		//
		simulatedEvent.initMouseEvent(type, true, true, window, 1,
				first.screenX, first.screenY, first.clientX, first.clientY,
				false, false, false, false, 0, null);
		first.target.dispatchEvent(simulatedEvent);

		type = "mousedown";
		simulatedEvent = document.createEvent("MouseEvent");

		simulatedEvent.initMouseEvent(type, true, true, window, 1,
				first.screenX, first.screenY, first.clientX, first.clientY,
				false, false, false, false, 0, null);
		first.target.dispatchEvent(simulatedEvent);

		if (!tapValid) {
			lastTap = first.target;
			tapValid = true;
			tapTimeout = window.setTimeout("cancelTap();", 600);
			startHold(event);
		} else {
			window.clearTimeout(tapTimeout);

			//
			// If a double tap is still a possibility and the elements are the same
			//    Then perform a double click
			//
			if (first.target == lastTap) {
				lastTap = null;
				tapValid = false;

				type = "click";
				simulatedEvent = document.createEvent("MouseEvent");

				simulatedEvent.initMouseEvent(type, true, true, window, 1,
						first.screenX, first.screenY, first.clientX,
						first.clientY, false, false, false, false, 0/*left*/,
						null);
				first.target.dispatchEvent(simulatedEvent);

				type = "dblclick";
				simulatedEvent = document.createEvent("MouseEvent");

				simulatedEvent.initMouseEvent(type, true, true, window, 1,
						first.screenX, first.screenY, first.clientX,
						first.clientY, false, false, false, false, 0/*left*/,
						null);
				first.target.dispatchEvent(simulatedEvent);
			} else {
				lastTap = first.target;
				tapValid = true;
				tapTimeout = window.setTimeout("cancelTap();", 600);
				startHold(event);
			}
		}
	}

	function iPadTouchHandler(event) {
		var type = "", button = 0; /*left*/

		if (event.touches.length > 1)
			return;

		switch (event.type) {
		case "touchstart":
			if ($(event.changedTouches[0].target).is("select")) {
				return;
			}
			iPadTouchStart(event); /*We need to trigger two events here to support one touch drag and drop*/
			event.preventDefault();
			return false;
			break;

		case "touchmove":
			cancelHold();
			type = "mousemove";
			event.preventDefault();
			break;

		case "touchend":
			if (cancelMouseUp) {
				cancelMouseUp = false;
				event.preventDefault();
				return false;
			}
			cancelHold();
			type = "mouseup";
			break;

		default:
			return;
		}

		var touches = event.changedTouches, first = touches[0], simulatedEvent = document
				.createEvent("MouseEvent");

		simulatedEvent.initMouseEvent(type, true, true, window, 1,
				first.screenX, first.screenY, first.clientX, first.clientY,
				false, false, false, false, button, null);

		first.target.dispatchEvent(simulatedEvent);

		if (type == "mouseup" && tapValid && first.target == lastTap) { // This actually emulates the ipads default behaviour (which we prevented)
			simulatedEvent = document.createEvent("MouseEvent"); // This check avoids click being emulated on a double tap

			simulatedEvent.initMouseEvent("click", true, true, window, 1,
					first.screenX, first.screenY, first.clientX, first.clientY,
					false, false, false, false, button, null);

			first.target.dispatchEvent(simulatedEvent);
		}
	}
}
