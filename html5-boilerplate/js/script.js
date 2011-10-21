/* Author: johnarleyburns@gmail.com

*/

google.load("jquery", "1.6.4");
google.load("visualization", "1", {packages:["corechart"]});
google.setOnLoadCallback(loadAll);

function loadAll() {
        /*
         * jQuery hashchange event - v1.3 - 7/21/2010
         * http://benalman.com/projects/jquery-hashchange-plugin/
         * 
         * Copyright (c) 2010 "Cowboy" Ben Alman
         * Dual licensed under the MIT and GPL licenses.
         * http://benalman.com/about/license/
         */
        (function($,e,b){var c="hashchange",h=document,f,g=$.event.special,i=h.documentMode,d="on"+c in e&&(i===b||i>7);function a(j){j=j||location.href;return"#"+j.replace(/^[^#]*#?(.*)$/,"$1")}$.fn[c]=function(j){return j?this.bind(c,j):this.trigger(c)};$.fn[c].delay=50;g[c]=$.extend(g[c],{setup:function(){if(d){return false}$(f.start)},teardown:function(){if(d){return false}$(f.stop)}});f=(function(){var j={},p,m=a(),k=function(q){return q},l=k,o=k;j.start=function(){p||n()};j.stop=function(){p&&clearTimeout(p);p=b};function n(){var r=a(),q=o(m);if(r!==m){l(m=r,q);$(e).trigger(c)}else{if(q!==m){location.href=location.href.replace(/#.*/,"")+q}}p=setTimeout(n,$.fn[c].delay)}$.browser.msie&&!d&&(function(){var q,r;j.start=function(){if(!q){r=$.fn[c].src;r=r&&r+a();q=$('<iframe tabindex="-1" title="empty"/>').hide().one("load",function(){r||l(a());n()}).attr("src",r||"javascript:0").insertAfter("body")[0].contentWindow;h.onpropertychange=function(){try{if(event.propertyName==="title"){q.document.title=h.title}}catch(s){}}}};j.stop=k;o=function(){return a(q.location.href)};l=function(v,s){var u=q.document,t=$.fn[c].domain;if(v!==s){u.title=h.title;u.open();t&&u.write('<script>document.domain="'+t+'"<\/script>');u.close();q.location.hash=v}}})();return j})()})(jQuery,this);

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
encode: function(o) {
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

                                return '"' + year + '-' + month + '-' + day + 'T' + hours + ':'
                                        + minutes + ':' + seconds + '.' + milli + 'Z"';
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
decode: function(src) {
                if (typeof (JSON) == 'object' && JSON.parse)
                        return JSON.parse(src);
                return eval("(" + src + ")");
        },

        /**
         * jQuery.JSON.decodeSecure(src) Evals JSON in a way that is *more* secure.
         */
decodeSecure: function(src) {
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
                              throw new SyntaxError("Error parsing JSON, source is not valid.");
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
quoteString: function(string) {
                     if (string.match(this._escapeable)) {
                             return '"' + string.replace(this._escapeable, function(a) {
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

_escapeable: /["\\\x00-\x1f\x7f-\x9f]/g,

              _meta: {
                      '\b': '\\b',
                      '\t': '\\t',
                      '\n': '\\n',
                      '\f': '\\f',
                      '\r': '\\r',
                      '"': '\\"',
                      '\\': '\\\\'
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
touch: typeof Touch == "object"
});

                        //
                        // Hook up touch events
                        //
                        $.fn.addTouch = function()
                        {
                        if ($.support.touch) {
                        this.each(function(i,el){
                                el.addEventListener("touchstart", iPadTouchHandler, false);
                                el.addEventListener("touchmove", iPadTouchHandler, false);
                                el.addEventListener("touchend", iPadTouchHandler, false);
                                el.addEventListener("touchcancel", iPadTouchHandler, false);
                                });
                        }

                        return this;
                        }
});


var lastTap = null;            // Holds last tapped element (so we can compare for double tap)
var tapValid = false;            // Are we still in the .6 second window where a double tap can occur
var tapTimeout = null;            // The timeout reference

function cancelTap() {
        tapValid = false;
}


var rightClickPending = false;    // Is a right click still feasible
var rightClickEvent = null;        // the original event
var holdTimeout = null;            // timeout reference
var cancelMouseUp = false;        // prevents a click from occuring as we want the context menu


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
        var first = rightClickEvent,
            simulatedEvent = document.createEvent("MouseEvent");
        simulatedEvent.initMouseEvent("mouseup", true, true, window, 1, first.screenX, first.screenY, first.clientX, first.clientY,
                        false, false, false, false, 0, null);
        first.target.dispatchEvent(simulatedEvent);

        //
        // emulate a right click
        //
        simulatedEvent = document.createEvent("MouseEvent");
        simulatedEvent.initMouseEvent("mousedown", true, true, window, 1, first.screenX, first.screenY, first.clientX, first.clientY,
                        false, false, false, false, 2, null);
        first.target.dispatchEvent(simulatedEvent);

        //
        // Show a context menu
        //
        simulatedEvent = document.createEvent("MouseEvent");
        simulatedEvent.initMouseEvent("contextmenu", true, true, window, 1, first.screenX + 50, first.screenY + 5, first.clientX + 50, first.clientY + 5,
                        false, false, false, false, 2, null);
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
        var touches = event.changedTouches,
            first = touches[0],
            type = "mouseover",
            simulatedEvent = document.createEvent("MouseEvent");
        //
        // Mouse over first - I have live events attached on mouse over
        //
        simulatedEvent.initMouseEvent(type, true, true, window, 1, first.screenX, first.screenY, first.clientX, first.clientY,
                        false, false, false, false, 0, null);
        first.target.dispatchEvent(simulatedEvent);

        type = "mousedown";
        simulatedEvent = document.createEvent("MouseEvent");

        simulatedEvent.initMouseEvent(type, true, true, window, 1, first.screenX, first.screenY, first.clientX, first.clientY,
                        false, false, false, false, 0, null);
        first.target.dispatchEvent(simulatedEvent);


        if (!tapValid) {
                lastTap = first.target;
                tapValid = true;
                tapTimeout = window.setTimeout("cancelTap();", 600);
                startHold(event);
        }
        else {
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

                        simulatedEvent.initMouseEvent(type, true, true, window, 1, first.screenX, first.screenY, first.clientX, first.clientY,
                                        false, false, false, false, 0/*left*/, null);
                        first.target.dispatchEvent(simulatedEvent);

                        type = "dblclick";
                        simulatedEvent = document.createEvent("MouseEvent");

                        simulatedEvent.initMouseEvent(type, true, true, window, 1, first.screenX, first.screenY, first.clientX, first.clientY,
                                        false, false, false, false, 0/*left*/, null);
                        first.target.dispatchEvent(simulatedEvent);
                }
                else {
                        lastTap = first.target;
                        tapValid = true;
                        tapTimeout = window.setTimeout("cancelTap();", 600);
                        startHold(event);
                }
        }
}

function iPadTouchHandler(event) {
        var type = "",
            button = 0; /*left*/

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

        var touches = event.changedTouches,
            first = touches[0],
            simulatedEvent = document.createEvent("MouseEvent");

        simulatedEvent.initMouseEvent(type, true, true, window, 1, first.screenX, first.screenY, first.clientX, first.clientY,
                        false, false, false, false, button, null);

        first.target.dispatchEvent(simulatedEvent);

        if (type == "mouseup" && tapValid && first.target == lastTap) {    // This actually emulates the ipads default behaviour (which we prevented)
                simulatedEvent = document.createEvent("MouseEvent");        // This check avoids click being emulated on a double tap

                simulatedEvent.initMouseEvent("click", true, true, window, 1, first.screenX, first.screenY, first.clientX, first.clientY,
                                false, false, false, false, button, null);

                first.target.dispatchEvent(simulatedEvent);
        }
}

/* startupbidder javascript code */
$(function(){

                var $ = jQuery;

                var Util = function() {
                var self = {};
                self.keyCache = {};
                self.pages = ['loading', 'searchlistings', 'searchusers', 'comments', 'profile', 'listing', 'bids', 'login', 'faq', 'contact', 'disclaimer'];
                self.objectMapper = {};
                self.setup = function() {
                self.createObjectMapper();
                };
                self.displayPage = function(page) {
                var i;
                var sel;
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
                        }
                        else {
                                $(sel).show();
                        }
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
                        var date = '' + year + (month < 10 ? '0' : '') + month + (day < 10 ? '0' : '') + day;
                        return date;
                };
                self.numericFromDateObj = function(date) {
                        if (date)
                                return self.constructNumericDate(date.getFullYear(), date.getUTCMonth(), date.getUTCDate());
                };
                self.epochToNumericDate = function(epoch) {
                        var date;
                        if (epoch) {
                                date = new Date(epoch);
                                return util.numericFromDateObj(date);
                        }
                };
                self.today = function() {
                        if (!self.todayCache) {
                                var today = new Date();
                                var numericDate = self.constructNumericDate(today.getFullYear(), today.getMonth()+1, today.getDate());
                                self.todayCache = numericDate;
                        }
                        return self.todayCache;
                };
                self.todayPlusDays = function(days) {
                        var today = new Date();
                        today.setDate(today.getDate() + days);
                        var numericDate = self.numericFromDateObj(today);
                        return numericDate;
                }
                self.randomElement = function(array) {
                        return array[Math.floor(Math.random()*array.length)];
                };
                self.randomKey = function(name, assocArray) {
                        if (!self.keyCache[name]) { // caching
                                self.keyCache[name] = [];
                                for (var key in assocArray) {
                                        self.keyCache[name].push(key);
                                }
                        }
                        return self.randomElement(self.keyCache[name]);
                }
                self.ucFirst = function(string) {
                        return string.charAt(0).toUpperCase() + string.slice(1);
                };
                self.formatDate = function(yyyymmdd) {
                        var year  = yyyymmdd.substr(0,4);
                        var month = yyyymmdd.substr(4,2);
                        var day   = yyyymmdd.substr(6,2);
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
                        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
                                        var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
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
                        $(window).resize(function(){
                                        resizerCallback(self.guessWidth());
                                        });
                }
                self.createObjectMapper = function() {
                        self.objectMapper = { 'util': self, 'user': user, 'backend': backend, 'uploader': uploader, 'header': header,
                                'searchListings': searchListings, 'searchUsers': searchUsers,
                                'profiles': profiles, 'listingClass': listingClass, 'comments': comments, 'bids': bids };
                };
                self.setHash = function(objectName, methodName, argArray) {
                        var methodCall = objectName + '.' + methodName;
                        var argStr = argArray ? $.JSON.encode(argArray) : '';
                        var hashStr = '#' + methodCall + '|' + argStr;
                        location.hash = hashStr;
                };
                self.manualCall = function(objectName, methodName, argArray) {
                        var object, method, args;
                        if (objectName && self.objectMapper[objectName] && self.objectMapper[objectName][methodName]) {
                            object = self.objectMapper[objectName];
                            method = methodName;
                        }
                        else {
                            object = searchListings;
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
                        var methodCall = hash.replace(/^#/,'');
                        var objectStr = methodCall.replace(/\..*$/,'');
                        var methodStr = methodCall.replace(/^.*\./,'').replace(/\|.*$/,'');
                        var methodArgsStr = methodCall.replace(/^.*\|/,'');
                        var methodArgs = methodArgsStr != '' ? $.JSON.decode(methodArgsStr) : [];
                        self.manualCall(objectStr, methodStr, methodArgs);
                };
                return self;
                };

                var User = function() {
                        var self = {};
                        self.profile = null;
                        self.login_url = '/_ah/login?continue=%2F';
                        self.logout_url = '/_ah/logout?continue=%2F';
                        self.setProfile = function(profile) {
                                self.profile = profile;
                                header.displayUser(self);
                        };
                        self.hasVoted = function(listing_id) {
                                return false;
                        };
                        self.login = function() {
                                document.location = self.login_url;
                        };
                        self.logout = function() {
                                document.location = self.logout_url;
                        };
                        self.generateUserSnippetHtml = function(i, userjson) {
                                var days_ago = userjson.days_ago !== undefined ? userjson.days_ago : 0;
                                var resultHtml = '';
                                var evenOdd;
                                if (isFinite(i)) {
                                        evenOdd = (i % 2 == 0) ? 'even' : 'odd';
                                        resultHtml += '<div class="content-results-list-item content-results-list-item-' + evenOdd + '">';
                                        resultHtml += '<div class="content-results-list-item-num">' + userjson.num + '</div>';
                                }
                                else {
                                        resultHtml += '<div class="content-results-list-item">';
                                        resultHtml += '<div class="content-results-list-item-num"></div>';
                                }
                                if (user.profile && (user.profile.profile_id !== userjson.profile_id)) {
                                        resultHtml += '<div class="content-results-list-item-upvote" id="user-search-results-list-item-upvote-' + i + '"></div>';
                                }
                                else {
                                        resultHtml += '<div class="content-results-list-item-upvote-filler"></div>';
                                }
                                resultHtml += '<div class="content-results-list-item-title link" id="user-search-results-list-item-name-' + i + '">';
                                resultHtml += userjson.name + (userjson.organization ? ' of '+userjson.organization : '');
                                resultHtml += '</div>';
                                resultHtml += '<div class="content-results-list-item-profile link" id="user-search-results-list-item-profile-' + i + '">';
                                resultHtml += '(' + userjson.username + ')';
                                resultHtml += userjson.status === 'active' ? '' : ' <span class="attention">' + userjson.status + '</span>';
                                resultHtml += ' <span class="attention" id="user-search-results-list-item-message-' + i + '"></span>';
                                resultHtml += '</div>';
                                resultHtml += '<div class="content-results-list-item-clear"></div>';
                                resultHtml += '<div class="content-results-list-item-detail">';
                                resultHtml += '<span id="user-search-results-list-item-votes-' + i + '">' + (userjson.num_votes !== undefined ? userjson.num_votes : 0)  + '</span>' + ' votes | ';
                                resultHtml += '<span class="link" id="user-search-results-list-item-listings-' + i + '">' + userjson.num_listings + ' listing' + (userjson.num_listings!==1?'s':'') + '</span> | ';
                                resultHtml += '<span class="link" id="user-search-results-list-item-bids-' + i + '">' + userjson.num_bids + ' bid' + (userjson.num_bids!==1?'s':'') + '</span> | ';
                                resultHtml += '<span class="link" id="user-search-results-list-item-comments-' + i + '">' + userjson.num_comments + ' comment' + (userjson.num_comments!==1?'s':'') + '</span> | ';
                                resultHtml += (days_ago == 0) ? 'active today' : 'active ' + days_ago + ' day' + (days_ago!==1?'s':'')  + ' ago';
                                resultHtml += ' | ';
                                resultHtml += 'joined ' + util.formatDate(userjson.joined_date);
                                resultHtml += userjson.investor ? ' | <b>investor</b>' : '';
                                resultHtml += '</div>';
                                resultHtml += '</div>';
                                resultHtml += '<div class="content-results-list-item-clear"></div>';
                                return resultHtml;
                        };
                        self.showUserProfile = function(profile_id, index) {
                                var errorCallback = function () { $('#user-search-results-list-item-message-'+index).html('unable to retrieve profile'); };
                                profiles.showProfile(profile_id, errorCallback);
                        };
                        self.showListingsForProfile = function(profile_id, index) {
                                var errorCallback = function () { $('#user-search-results-list-item-message-'+index).html('unable to retrieve listings'); };
                                backend.listingsForProfile(profile_id, searchListings.displayResultsJson, errorCallback);
                        };
                        self.showCommentsForProfile = function(profile_id, index) {
                                var errorCallback = function () { $('#user-search-results-list-item-message-'+index).html('unable to retrieve comments'); };
                                backend.commentsForProfile(profile_id, comments.displayResults, errorCallback);
                        };
                        self.showBidsForProfile = function(profile_id, index) {
                                var errorCallback = function () { $('#user-search-results-list-item-message-'+index).html('unable to retrieve bids'); };
                                backend.bidsForProfile(profile_id, bids.displayResults, errorCallback);
                        };
                        self.showAcceptedBidsForProfile = function(profile_id, index) {
                                var errorCallback = function () { $('#user-search-results-list-item-message-'+index).html('unable to retrieve bids'); };
                                backend.bidsForProfile(profile_id, bids.displayAcceptedResults, errorCallback);
                        };
                        self.showPaidBidsForProfile = function(profile_id, index) {
                                var errorCallback = function () { $('#user-search-results-list-item-message-'+index).html('unable to retrieve bids'); };
                                backend.bidsForProfile(profile_id, bids.displayPaidResults, errorCallback);
                        };
                        self.bindUserSnippet = function(i, userjson) {
                                var upvote_item = document.getElementById('user-search-results-list-item-upvote-'+i);
                                var item_num = i;
                                $('#user-search-results-list-item-name-'+i+',#user-search-results-list-item-profile-'+i).data({index: i, profile_id: userjson.profile_id}).unbind().bind('click', function(){
                                                util.hashedCall('user', 'showUserProfile', [$(this).data('profile_id'), $(this).data('index')]);
                                                });           
                                $('#user-search-results-list-item-listings-'+i).data({index: i, profile_id: userjson.profile_id}).unbind().bind('click', function(){
                                                util.hashedCall('user', 'showListingsForProfile', [ $(this).data('profile_id'), $(this).data('index') ]);
                                                }); 
                                $('#user-search-results-list-item-comments-'+i).data({index: i, profile_id: userjson.profile_id}).unbind().bind('click', function(){
                                                util.hashedCall('user', 'showCommentsForProfile', [ $(this).data('profile_id'), $(this).data('index') ]);
                                                }); 
                                $('#user-search-results-list-item-bids-'+i).data({index: i, profile_id: userjson.profile_id}).unbind().bind('click', function(){
                                                util.hashedCall('user', 'showBidsForProfile', [ $(this).data('profile_id'), $(this).data('index') ]);
                                                });
                                $(upvote_item).data({index: i, profile_id: userjson.profile_id, num_votes: (userjson.num_votes !== undefined ? userjson.num_votes : 0)});
                                if (!userjson.votable) {
                                        $(upvote_item).css('visibility', 'hidden').unbind();
                                        return;
                                }
                                $(upvote_item)
                                        .css('visibility', 'visible')
                                        .unbind()
                                        .bind('click', function(){
                                                        var index = $(this).data('index');
                                                        var profile_id = $(this).data('profile_id');
                                                        var num_votes = $(this).data('num_votes');
                                                        var list_item = this;
                                                        var votes_item = document.getElementById('user-search-results-list-item-votes-'+index);
                                                        var callback = function(json) {
                                                        if (json) {
                                                        var new_votes = num_votes + 1;
                                                        $(votes_item).html(new_votes);
                                                        $(list_item)
                                                        .data('num_votes', new_votes)
                                                        .css('visibility', 'hidden')
                                                        .unbind(); // don't let them click twice
                                                        }
                                                        };
                                                        var errorCallback = function () { $('#user-search-results-list-item-message-'+i).html('unable to retrieve bids'); };
                                                        backend.upvoteUser(profile_id, callback);
                                                        });
                        };
                        return self;
                };

                var Uploader = function() {
                        var self = {};
                        self.prepareUploader = function(fieldRoot, fieldName) {
                                var uploader = function() {
                                        var callback = function(uploadjson) {
                                                var actionUrl = uploadjson[0];
                                                var submitCallback = function() {
                                                        var loadCallback = function() {
                                                                var responseEl = $('#'+fieldRoot+'-iframe').contents().find('body');
                                                                var responseHtml;
                                                                var responseId;
                                                                var downloadUrl;
                                                                if (responseEl) {
                                                                        responseHtml = responseEl.html().replace(/<[^>]*>/g,'');
                                                                        if (responseHtml) {
                                                                                responseId = responseHtml.replace(/^{ *"?id"? *: *"/,'').replace(/".*$/,'');
                                                                                }
                                                                        }
                                                                        if (responseId === undefined || responseId.length === 0) {
                                                                                $('#listing-save-message').attr('class','confirmed').html('error uploading');
                                                                        }
                                                                        else {
                                                                                downloadUrl = '/file/download/' + responseId;
                                                                                $('#listing-save-message').attr('class','confirmed').html('upload successful');
                                                                                $('#'+fieldRoot+'-link').data(fieldName, responseId).children('a').attr('href', downloadUrl);
                                                                                $('#'+fieldRoot+'-link').show();
                                                                        }
                                                                        $('#'+fieldRoot+'-file').hide().val('');
                                                                        $('#'+fieldRoot+'-submit').hide();
                                                                };
                                                                $('#listing-save-message').attr('class','confirmed').html('uploading...');
                                                                $('#'+fieldRoot+'-form').get(0).target = fieldRoot+'-iframe';
                                                                $('#'+fieldRoot+'-iframe').unbind().load(loadCallback);
                                                        };
                                                        $('#'+fieldRoot+'-form').attr('action',actionUrl).unbind().submit(submitCallback).show();
                                                        $('#'+fieldRoot+'-file').show();
                                                };
                                                var errorCallback = function() {
                                                        $('#listing-save-message').attr('class', 'attention').html('unable to obtain business plan upload data');
                                                };
                                                var fileChangeCallback = function() {
                                                        var filename = $(this).val();
                                                        if (!/\.doc|\.docx|\.pdf|\.ppt$/.test(filename)) {
                                                                alert('Only doc, docx, ppt or pdf files are allowed for upload');
                                                                $(this).val('');
                                                                $('#'+fieldRoot+'-submit').hide();
                                                        }
                                                        else {
                                                                $('#'+fieldRoot+'-submit').show();
                                                        }
                                                };
                                                $('#'+fieldRoot+'-file').hide().val('').change(fileChangeCallback);
                                                $('#'+fieldRoot+'-submit').hide();
                                                backend.getUploadUrls(1, callback, errorCallback);
                                        };
                                        return uploader;
                                };
                                return self;
                        };

                        var Header = function() {
                                var self = {};
                                self.setup = function() {
                                        $('#header-logo,#header-title-link').unbind().bind('click', function(){
                                                        util.hashedCall('searchListings', 'doSearch');
                                                        });
                                        $('#header-menu-command-login').unbind().bind('click', function(){
                                                        user.login();
                                                        });
                                        $('#header-menu-command-logout').unbind().bind('click', function(){
                                                        user.logout();
                                                        });
                                        $('#header-menu-bar-submit').unbind().bind('click', function(){
                                                        util.hashedCall('listingClass', 'newListing');
                                                        });
                                        $('#header-menu-bar-setup').unbind().bind('click', function(){
                                                        document.location = '/setup';
                                                        });
                                        $('#header-menu-bar-test').unbind().bind('click', function(){
                                                        document.location = '/hello';
                                                        });
                                        $('#header-menu-bar-top').unbind().bind('click', function(){
                                                        util.hashedCall('searchListings', 'doTopSearch');
                                                        });
                                        $('#header-menu-bar-valuation').unbind().bind('click', function(){
                                                        util.hashedCall('searchListings', 'doValuationSearch');
                                                        });
                                        $('#header-menu-bar-active').unbind().bind('click', function(){
                                                        util.hashedCall('searchListings', 'doActiveSearch');
                                                        });
                                        $('#header-menu-bar-popular').unbind().bind('click', function(){
                                                        util.hashedCall('searchListings', 'doPopularSearch');
                                                        });
                                        $('#header-menu-bar-discussed').unbind().bind('click', function(){
                                                        util.hashedCall('searchListings', 'doDiscussedSearch');
                                                        });
                                        $('#header-menu-bar-latest').unbind().bind('click', function(){
                                                        util.hashedCall('searchListings', 'doLatestSearch');
                                                        });
                                        $('#header-menu-bar-top-users').unbind().bind('click', function(){
                                                        util.hashedCall('searchUsers', 'doTopSearch');
                                                        });
                                        $('#footer-menu-bar-faq').unbind().bind('click', function(){
                                                        util.hashedCall('util', 'displayFaq');
                                                        });
                                        $('#footer-menu-bar-disclaimer').unbind().bind('click', function(){
                                                        util.hashedCall('util', 'displayDisclaimer');
                                                        });
                                        $('#footer-menu-bar-contact').unbind().bind('click', function(){
                                                        util.hashedCall('util', 'displayContact');
                                                        });
                                        $('#faq-contact').unbind().bind('click', function(){
                                                        util.hashedCall('util', 'displayContact');
                                                        });
                                };
                                self.showHeaderProfile = function(profile_id) {
                                        var errorCallback = function () { $('#header-menu-message').html('unable to retrieve user'); };
                                        profiles.showProfile(profile_id, errorCallback);
                                };
                                self.displayUser = function(userobj) {
                                        if (userobj.profile && userobj.logout_url) {
                                                $('#header-menu-name-login,#header-menu-command-login').hide();
                                                $('#header-menu-name-username').html(userobj.profile.username).data('profile_id',userobj.profile.profile_id).show().unbind().bind('click', function(){
                                                                util.hashedCall('header', 'showHeaderProfile', [$(this).data('profile_id')]);
                                                                });
                                                $('#header-menu-command-logout,#header-menu-bar-submit-spacer,#header-menu-bar-submit').show();
                                                $('#header-menu-command-logout').data('logout_url',userobj.logout_url).show().unbind().bind('click', function(){
                                                                var errorCallback = function () { $('#header-menu-message').html('unable to retrieve user'); };
                                                                userobj.logout($(this).data('logout_url'), errorCallback);
                                                                });
                                        }
                                        else {
                                                $('#header-menu-name-login,#header-menu-command-login').show();
                                                $('#header-menu-name-username').html(null).hide();
                                                $('#header-menu-command-logout,#header-menu-bar-submit-spacer,#header-menu-bar-submit').hide();
                                        }
                                        if (userobj.profile && userobj.logout_url && userobj.profile.admin) {
                                                $('#header-menu-bar-setup-spacer,#header-menu-bar-setup,#header-menu-bar-test-spacer,#header-menu-bar-test').show();
                                        }
                                        else {
                                                $('#header-menu-bar-setup-spacer,#header-menu-bar-setup,#header-menu-bar-test-spacer,#header-menu-bar-test').hide();
                                        }
                                };
                                self.narrowClasses = {
                                        '#mainbody': 'body-narrow',
                                        '#header': 'header-narrow',
                                        '#header-logo': 'header-logo-narrow',
                                        '#header-title': 'header-title-narrow',
                                        '#header-menu-bar': 'header-menu-bar-narrow',
                                        '#header-menu-name': 'header-menu-name-narrow',
                                        '#header-menu-command': 'header-menu-command-narrow',
                                        '#searchlistingspage': 'content-narrow',
                                        '#searchuserspage': 'content-narrow',
                                        '#commentspage': 'content-narrow',
                                        '#profilepage': 'content-narrow',
                                        '#listingpage': 'content-narrow',
                                        '#bidspage': 'content-narrow',
                                        '#loginpage': 'content-narrow',
                                        '#faqpage': 'content-narrow',
            '#footer' : 'footer-narrow',
            '#footer-copyright' : 'footer-copyright-narrow',
            '.content-results': 'content-results-narrow'
        };
        self.currentStyle = undefined;
        self.resizeStyles = function(windowWidth) {
            var sel;
            var width = parseInt(windowWidth);
            var newStyle = (width < 701) ? 'narrow' : 'normal';
            if (self.currentStyle === undefined) { // first time
                if (newStyle === 'narrow') {
                    for (sel in self.narrowClasses) {
                        $(sel).addClass(self.narrowClasses[sel]);
                    }
                }
            }
            else if (self.currentStyle !== newStyle && newStyle === 'narrow') { // style change to narrow
                for (sel in self.narrowClasses) {
                    $(sel).addClass(self.narrowClasses[sel]);
                }
            }
            else if (self.currentStyle !== newStyle && newStyle === 'normal') { // style change to normal
                for (sel in self.narrowClasses) {
                    $(sel).removeClass(self.narrowClasses[sel]);
                }
            }
            self.currentStyle = newStyle; // save the new style
        };
        return self;
    };

    var Userbox = function() { // module showing a summary box for the logged in user
        var self = {};
        self.setup = function() {
            $('#userbox-details').bind('click', function(e) {
                util.hashedCall('user', 'showUserProfile', [$('#userbox-profile-id').val(), 'zz']); // FIXME - handle error messages
            });
            $('#userbox-listings').bind('click', function(e) {
                util.hashedCall('user', 'showListingsForProfile', [$('#userbox-profile-id').val(), 'zz']); // FIXME - handle error messages
            });
            $('#userbox-bids').bind('click', function(e) {
                util.hashedCall('user', 'showBidsForProfile', [$('#userbox-profile-id').val(), 'zz']); // FIXME - handle error messages
            });
            $('#userbox-bids-accepted').bind('click', function(e) {
                util.hashedCall('user', 'showAcceptedBidsForProfile', [$('#userbox-profile-id').val(), 'zz']); // FIXME - handle error messages
            });
            $('#userbox-bids-paid').bind('click', function(e) {
                util.hashedCall('user', 'showPaidBidsForProfile', [$('#userbox-profile-id').val(), 'zz']); // FIXME - handle error messages
            });
            $('#userbox-comments').bind('click', function(e) {
                util.hashedCall('user', 'showCommentsForProfile', [$('#userbox-profile-id').val(), 'zz']); // FIXME - handle error messages
            });
            $('#userbox').bind('display', self.drawUserbox);
        };
        self.drawUserbox = function(e, data) {
            var profile = user ? user.profile : undefined;
            if (!profile) {
                $('#userbox').hide();
                return;
            }
            var memberType = self.getMemberType();
            var memberClass = 'userbox-' + memberType;
            $('#userbox-profile-id').val(profile.profile_id);
            $('#userbox-title').html(profile.username || profile.name || 'Profile');
            $('#userbox-score').html(self.getScore());
            $('#userbox-num-votes').html(profile.num_votes);
            $('#userbox-num-listings').html(profile.num_listings);
            $('#userbox-num-bids').html(profile.num_bids);
            $('#userbox-num-bids-accepted').html(profile.num_accepted_bids);
            $('#userbox-num-bids-paid').html(profile.num_payed_bids !== undefined ? profile.num_payed_bids : profile.num_paid_bids);
            $('#userbox-num-comments').html(profile.num_comments);
            $('#userbox-listings-plural').html(profile.num_listings !== 1 ? 's' : '');
            $('#userbox-bids-plural').html(profile.num_bids !== 1 ? 's' : '');
            $('#userbox-comments-plural').html(profile.num_comments !== 1 ? 's' : '');
            $('#userbox-member-type').html(util.ucFirst(memberType));
            $('#userbox-chart').removeClass().addClass('userbox-chart').addClass(memberClass);
            $('#userbox').show();
        };
        self.getScore = function() {
            if (!user || !user.profile) {
                return 0;
            }
            var profile = user.profile;
            var score = profile.num_votes + profile.num_listings + profile.num_bids + profile.num_comments;
            return score;
        };
        self.getMemberType = function() {
            var score = self.getScore();
            var memberType;
            if (score >= 100) {
                memberType = 'platinum';
            }
            else if (score >= 50) {
                memberType = 'gold';
            }
            else if (score >= 10) {
                memberType = 'silver';
            }
            else {
                memberType = 'bronze';
            }
            return memberType;
        };
        self.setup();
        return self;
    };

    var Statistics = function() {
        var self = {};
        self.columnChartId = 'stats-graph-column';
        self.lineChartId = 'stats-graph-line';
        self.width = 270;
        self.height = 180;
        self.lineChart = undefined;
        self.columnChart = undefined;
        self.displayStats = function(stats) {
            var googChart;
            var i;
            if (!stats || !stats.values) {
                return;
            }
            var chartType = stats.chartType || 'line';
            var label = stats.label ? $.trim(stats.label) : '';
            var numPrefix = stats.prefix || '';
            var numSuffix = stats.suffix || '';
            var labels = stats.labels;
            var values = stats.values;
            var chartOptions = {
                legend: 'none',
                width: self.width,
                height: self.height,
                title: label,
                titleTextStyle: {
                    color: 'green',
                    fontSize: '16'
                }
            };            
            var formatOptions = {
                prefix: numPrefix,
                suffix: numSuffix,
                fractionDigits: 0,
                negativeColor: 'red',
                negativeParens: true
            };
            var numFormatter = new google.visualization.NumberFormat(formatOptions);
            var chartData = new google.visualization.DataTable();
            if (chartType === 'column') {
                if (!self.columnChart) {
                    self.columnChart = new google.visualization.ColumnChart(document.getElementById(self.columnChartId));
                    google.visualization.events.addListener(self.columnChart, 'ready', self.showColumnChart);
                }
                googChart = self.columnChart;
            }
            else { // assume line
                if (!self.lineChart) {
                    self.lineChart = new google.visualization.LineChart(document.getElementById(self.lineChartId));
                    google.visualization.events.addListener(self.lineChart, 'ready', self.showLineChart);
                }
                googChart = self.lineChart;
            }
            chartData.addColumn('string', stats.xaxis);
            chartData.addColumn('number', stats.yaxis);
            chartData.addRows(values.length);
            for (i = 0; i < values.length; i++) {
                if (labels && labels[i] !== undefined) {
                    chartData.setValue(i, 0, labels[i]);
                }
                chartData.setValue(i, 1, values[i]);
            }
            numFormatter.format(chartData, 1);
            if (stats.xaxis) {
                chartOptions.hAxis = {
                    title: stats.xaxis
                };
            }
            if (stats.yaxis) {
                chartOptions.vAxis = {
                    title: stats.yaxis
                };
            }
            googChart.draw(chartData, chartOptions);
        };
        self.showColumnChart = function() {
            $('#'+self.lineChartId).hide();
            $('#statsbox,#'+self.columnChartId).show();
        };
        self.showLineChart = function() {
            $('#'+self.columnChartId).hide();
            $('#statsbox,#'+self.lineChartId).show();
        };
        self.hideCharts = function() {
            $('#statsbox').hide();
        };
        return self;
    };

    var SearchListings = function() {
        var self = {};
        self.defaultSearchType = 'top';
        self.setup = function() {
            $('#content-search-submit').unbind().bind('click', function(){
                util.hashedCall('searchListings', 'doKeywordSearch');
            });
            $('#content-search-keywords').unbind().bind('keydown', function(e){
                if (e.keyCode == 13) { // return key
                    util.hashedCall('searchListings', 'doKeywordSearch');
                }
            });
        };
        self.doTopSearch = function() {
            self.doSearch({search_type:'top'});
        };
        self.doLatestSearch = function() {
            self.doSearch({search_type:'latest'});
        };
        self.doValuationSearch = function() {
            self.doSearch({search_type:'valuation'});
        };
        self.doActiveSearch = function() {
            self.doSearch({search_type:'active'});
        };
        self.doPopularSearch = function() {
            self.doSearch({search_type:'popular'});
        };
        self.doDiscussedSearch = function() {
            self.doSearch({search_type:'discussed'});
        };
        self.doKeywordSearch = function() {
            self.doSearch({search_type:'keyword'});
        };
        self.doSearch = function(args) {
            args = args || {};
            args.search_type = args.search_type || self.defaultSearchType;
            args.start_index = args.start_index || 0;
            args.max_results = args.max_results || 20;
            var keywords = $('#content-search-keywords').val();
            if (keywords && keywords.length) {
                keywords = keywords.replace(/\btype:\w*/g, '').replace(/\s+/g, ' ').trim();
                $('#content-search-keywords').val(keywords);
            }
            if (args.profile_id) {
                statistics.hideCharts();
            }
            else {
                backend.bidStats(self.displayTopStats);
            }
            var searchErrorCallback = function () { $('#search-message').html('unable to retrieve search results'); };
            backend.searchListings(args.search_type, keywords, args.profile_id, args.start_index, args.max_results, self.displayResultsJson, searchErrorCallback);
        };
        self.displayTopStats = function(stats) {
            var i;
            if (stats && stats.values) {
                stats.prefix = '$';
                stats.suffix = 'k';
                stats.values.reverse();
                for (i = 0; i < stats.values.length; i++) {
                    stats.values[i] = stats.values[i] / 1000;
                }
            }
            statistics.displayStats(stats);
        };
        self.displayResultsJson = function(searchResults) {
            if (!searchResults || !searchResults.listings_props) {
                return;
            }
            var searchStats = searchResults.listings_props;
            var profile = searchResults.profile;
            var profileId = profile ? profile.profile_id : null;
            self.displayResults(searchResults.listings, searchResults.search_type, searchResults.keywords, profileId, searchResults.start_index, searchStats.num_results, searchStats.total_results, profile);
        };
        self.displayResultsHtml = function(resultsHtml) {
            util.displayPage('searchlistings');
            $('#search-content-results-list').hide().html(resultsHtml).show();
        };
        self.displayResults = function(listings, search_type, keywords, profile_id, start_index, num_results, total_results, profile) {
            $('#search-message').html('');
            self.displayMemberProfile(profile);
            self.displayListings(listings);
            self.bindListings(listings);
            // self.displayPagination(search_type, keywords, start_index, profile_id);
            if (profile) { // later we can make this always display these stats, and startupbidderindex in another div
                self.displayListingStats(listings);
            }
        };
        self.displayMemberProfile = function(profile) {
            if (profile) {
                $('#content-search-box').hide();
                $('#searchlistings-profile-snippet').html(user.generateUserSnippetHtml('search', profile));
                user.bindUserSnippet('search', profile);
                $('#searchlistings-profile-header').show();
            }
            else {
                $('#content-search-box').show();
                $('#searchlistings-profile-header').hide();
                $('#searchlistings-profile-snippet').html('');
            }
        };
        self.displayListings = function(listings) {
            var i, listing;
            var resultsHtml = '';
            if (listings && listings.length) {
                for (i = 0; i < listings.length; i++) {
                    listing = listings[i];
                    resultsHtml += listingClass.generateListingSnippetHtml(i, listing);
                }
            }
            else {
                resultsHtml = '<i>no listings found</i>';
            }
            self.displayResultsHtml(resultsHtml);
        };
        self.bindListings = function(listings) {
            var i, listing;
            if (listings && listings.length) {
                for (i = 0; i < listings.length; i++) {
                    listing = listings[i];
                    listingClass.bindListingSnippet(i, listing);
                }
            }
        };
        self.displayListingStats = function(listings) {
            if (!listings || !listings.length) {
                statistics.hideCharts();
                return;
            }
            var listing, i;
            var stats = {
                label: 'Listings History',
                xaxis: 'time',
                yaxis: 'valuation',
                prefix: '$',
                suffix: 'k',
                values: []
            }
            for (i = 0; i < listings.length; i++) {
                listing = listings[i];
                stats.values[i] = listing.valuation / 1000;
            }
            stats.values.reverse();
            statistics.displayStats(stats);
        };
        self.displayPagination = function(search_type, keywords, start_index, profile_id) {
            var args = {
                page: 'searchlistings',
                search_type: search_type,
                keywords: keywords,
                profile_id: profile_id,
                start_index: start_index
            }
            var prevargs, nextargs;
            if (start_index > 0) {
                prevargs = $.extend(true, {}, args);
                prevargs.start_index = start_index - 20;
                $('#search-prev-results').show().unbind()
                    .data('args', prevargs)
                    .bind('click',function(){
                        searchListings.doSearch($(this).data('args'));
                    });
            }
            else {
                $('#search-prev-results').hide();
            }
            if (start_index + num_results < total_results) {
                nextargs = $.extend(true, {}, args);
                nextargs.start_index = start_index + 20;
                $('#search-next-results').show().unbind()
                    .data('args', nextargs)
                    .bind('click',function(){
                        searchListings.doSearch($(this).data('args'));
                    });
            }
            else {
                $('#search-next-results').hide();
            }
        };
        return self;
    };

    var SearchUsers = function() {
        var self = {};
        self.defaultSearchType = 'top';
        self.setup = function() {
            $('#user-search-submit').unbind().bind('click', function(){
                util.hashedCall('searchUsers', 'doKeywordSearch');
            });
            $('#user-search-keywords').unbind().bind('keyup', function(e){
                if (e.keyCode == 13) { // return key
                    util.hashedCall('searchUsers', 'doKeywordSearch');
                }
            });
        };
        self.doTopSearch = function() {
            self.doSearch({search_type:'top'});
        };
        self.doKeywordSearch = function() {
            self.doSearch({search_type:'keyword'});
        };
        self.doSearch = function(args) {
            args = args || {};
            args.search_type = args.search_type || self.defaultSearchType;
            args.start_index = args.start_index || 0;
            args.max_results = args.max_results || 20;
            var keywords = $('#user-search-keywords').val();
            if (keywords) {
                keywords = keywords.replace(/\btype:\w*/g, '').replace(/\s+/g, ' ').trim();
            }
            $('#user-search-keywords').val(keywords);
            var errorCallback = function () { $('#user-search-message').html('unable to retrieve profiles');util.displayPage('searchusers'); };
            backend.searchUsers(args.search_type, keywords, args.start_index, args.max_results, searchUsers.displayResultsJson, errorCallback);
        };
        self.displayResultsJson = function(results_json) {
            if (!results_json.users_props) {
                results_json.users_props = {
                    start_index: 0,
                    num_results: !results_json.users || results_json.users.length,
                    total_results: !results_json.users || results_json.users.length
                };
            }
            self.displayResults(results_json.users, results_json.search_type, results_json.keywords, results_json.users_props.start_index,
                results_json.users_props.num_results, results_json.users_props.total_results);
        };
        self.displayResultsHtml = function(resultsHtml) {
            $('#user-search-results-list').hide().html(resultsHtml).show();
        };
        self.displayResults = function(members, search_type, keywords, start_index, num_results, total_results){
            $('#user-search-message').html('');
            util.displayPage('searchusers');
            self.displayMemberStats(members);
            self.displayMembers(members);
            self.bindMembers(members);
            //self.displayPagination(search_type, keywords, start_index); // set pagination
        };
        self.displayMemberStats = function(members) {
            var member, memberDate, i, k;
            var stats = {
                label: 'Total Members',
                xaxis: 'time',
                yaxis: '#members',
                values: []
            }
            var memberMap = {};
            var memberDates = [];
            var runningTotal = 0;
            for (i = 0; i < members.length; i++) {
                member = members[i];
                memberDate = member.joined_date;
                memberMap[memberDate] = memberMap[memberDate] ? memberMap[memberDate] + 1 : 1;
            }
            for (k in memberMap) {
                memberDates.push(k);
            }
            memberDates.sort(function(a,b){return a-b;});
            for (i = 0; i < memberDates.length; i++) {
                memberDate = memberDates[i];
                runningTotal += memberMap[memberDate];
                stats.values[i] = runningTotal;
            }
            stats.values.reverse();
            statistics.displayStats(stats);
        };
        self.displayMembers = function(members) {
            if (!members || !members.length) {
                self.displayResultsHtml('<i>no members found</i>');
                return;
            }
            var i, member;
            var resultsHtml = '';
            for (i = 0; i < members.length; i++) {
                member = members[i];
                resultsHtml += user.generateUserSnippetHtml(i, member);
            }
            self.displayResultsHtml(resultsHtml);
        };
        self.bindMembers = function(members) {
            if (!members || !members.length) {
                return;
            }
            var i, member;
            for (var i = 0; i < members.length; i++) {
                member = members[i];
                user.bindUserSnippet(i, member);
            }
        };
        self.displayPagination = function(search_type, keywords, start_index) {
            var args = {
                page: 'searchusers',
                search_type: search_type,
                keywords: keywords,
                start_index: start_index
            }
            var prevargs, nextargs;
            if (start_index > 0) {
                prevargs = $.extend(true, {}, args);
                prevargs.start_index = start_index - 20;
                $('#user-search-prev-results').show().unbind()
                    .data('args', prevargs)
                    .bind('click',function(){
                        searchUsers.doSearch($(this).data('args'));
                    });
            }
            else {
                $('#user-search-prev-results').hide();
            }
            if (start_index + num_results < total_results) {
                nextargs = $.extend(true, {}, args);
                nextargs.start_index = start_index + 20;
                $('#user-search-next-results').show().unbind()
                    .data('args', nextargs)
                    .bind('click',function(){
                        searchUsers.doSearch($(this).data('args'));
                    });
            }
            else {
                $('#user-search-next-results').hide();
            }
        };
        return self;
    };

    var Profiles = function() {
        var self = {};
        self.saveProfile = function(profile_id){
            var profile = {};
            profile.profile_id = profile_id;
            profile.username = $('#profile-edit-profile-username').val();
            profile.name = $('#profile-edit-name').val();
            profile.title = $('#profile-edit-title').val();
            profile.organization = $('#profile-edit-organization').val();
            profile.email = $('#profile-edit-email').val();
            profile.facebook = $('#profile-edit-facebook').val();
            profile.twitter = $('#profile-edit-twitter').val();
            profile.linkedin = $('#profile-edit-linkedin').val();
            profile.investor = $('#profile-edit-investor').attr('checked') ? 1 : 0;
            var callback = function(profile) {
                if (profile) {
                    self.displayProfile(profile, 'confirmed', 'profile saved');
                }
                else {
                    self.displayProfile(profile, 'attention', 'could not save profile');
                }
            };
            var errorCallback = function() { $('#profile-save-message').attr('class', 'attention').html('unable to save profile'); };
            backend.saveProfile(profile, callback, errorCallback);
        };
        self.showProfile = function(profile_id, errorCallback) {
            backend.getProfile(profile_id, self.displayProfile, errorCallback);
        };
        self.displayProfileChart = function(profile) {
            if (!profile) {
                statistics.hideCharts();
                return;
            }
            var username = profile.username || profile.name || 'Profile';
            var stats = {
                label: username,
                chartType: 'column',
                xaxis: '',
                yaxis: 'activity',
                labels: [ '#votes', '#listings', '#bids', '#comments' ],
                values: [ profile.num_votes || 0, profile.num_listings || 0, profile.num_bids || 0, profile.num_comments || 0]
            };
            statistics.displayStats(stats);
        };
        self.displayProfile = function(data, msgClass, msgText){
            var profile = data && data.profile ? data.profile : data;
            if (!profile) {
                $('#profile-header').hide();
                $('#profile-snippet').html('');
                $('#profile-form-editable').hide();
                $('#profile-form-readonly').hide();
                $('#profile-username').html('<em>unable to retrive user profile</em>');
                return;
            }
            $('#profile-save-message').attr('class', (msgClass || 'attention')).html(msgText || '');
            util.displayPage('profile');
            $('#profile-snippet').html(user.generateUserSnippetHtml('profile', profile));
            user.bindUserSnippet('profile', profile);
            $('#profile-header').show();
            self.displayProfileChart(profile);

            // enable editable if logged in
            if (user.profile && user.profile.profile_id == profile.profile_id) {
                $('#profile-form-readonly').hide();
                $('#profile-edit-profile-id').val(profile.profile_id);
                $('#profile-edit-profile-username').val(profile.username);
                $('#profile-edit-name').val(profile.name);
                $('#profile-edit-title').val(profile.title);
                $('#profile-edit-organization').val(profile.organization);
                $('#profile-edit-email').val(profile.email);
                $('#profile-edit-facebook').val(profile.facebook);
                $('#profile-edit-twitter').val(profile.twitter);
                $('#profile-edit-linkedin').val(profile.linkedin);
                $('#profile-edit-investor').attr('checked', profile.investor);
                $('#profile-edit-joined-date').html(util.formatDate(profile.joined_date));
                $('#profile-edit-open-id').html(profile.open_id);
                self.setProfileLinks(profile, 'editable');
                $('#profile-form-editable').show();
            }
            else {
                $('#profile-form-editable').hide();
                $('#profile-title').html(profile.title);
                $('#profile-organization').html(profile.organization);
                $('#profile-email').html(profile.email);
                $('#profile-email-href').attr('href', 'mailto:'+profile.email);
                $('#profile-facebook').html(profile.facebook);
                $('#profile-facebook-href').attr('href', 'http://www.facebook.com/'+profile.facebook);
                $('#profile-twitter').html(profile.twitter);
                $('#profile-twitter-href').attr('href', 'http://www.twitter.com/'+profile.twitter);
                $('#profile-linkedin').html(profile.linkedin);
                $('#profile-linkedin-href').attr('href', 'http://www.linkedin.com/'+profile.linkedin);
                $('#profile-investor').attr('checked', profile.investor);
                $('#profile-joined-date').html(util.formatDate(profile.joined_date));
                $('#profile-open-id').html(profile.open_id);
                self.setProfileLinks(profile, 'readonly');
                $('#profile-form-readonly').show();
            }
        };
        self.setProfileLinks = function(profile, auth) {
            if (auth !== 'editable') {
                return;
            }
            $('#profile-save-link').show().unbind().data('profile_id',profile.profile_id).bind('click',function(e){
                e.preventDefault();
                profiles.saveProfile($(this).data('profile_id'));
            });
            if (profile.status == 'active') {
                $('#profile-activate-link').hide();
                $('#profile-deactivate-link').show().unbind().data('profile_id',profile.profile_id).bind('click',function(){
                    var errorCallback = function() { $('#profile-save-message').attr('class', 'attention').html('unable to deactivate profile'); };
                    backend.deactivateProfile($(this).data('profile_id'), profiles.displayProfile, errorCallback);
                });
            }
            else {
                $('#profile-activate-link').show().unbind().data('profile_id',profile.profile_id).bind('click',function(){
                    var errorCallback = function() { $('#profile-save-message').attr('class', 'attention').html('unable to activate profile'); };
                    backend.activateProfile($(this).data('profile_id'), profiles.displayProfile, errorCallback);
                });
                $('#profile-deactivate-link').hide();
            }
        };
        return self;
    };

    var ListingClass = function() {
        var self = {};
        self.saveListing = function(listing_id){
            var listing = {};
            if (listing_id) {
                listing.listing_id = listing_id;
            }
            listing.title = $('#listing-edit-title').val();
            listing.median_valuation = $('#listing-edit-median-valuation').val();
            listing.num_votes =  $('#listing-edit-num-votes').val();
            listing.num_bids =  $('#listing-edit-num-bids').val();
            listing.num_comments =  $('#listing-edit-num-comments').val();
            listing.profile_id =  $('#listing-edit-profile-id').val();
            listing.profile_username =  $('#listing-edit-profile-username').val();
            listing.listing_date = $('#listing-edit-listing-date').val();
            listing.status = $('#listing-edit-status').val();
            var raw_amt = $('#listing-edit-suggested-amt').val().replace(/[^\d]/g,'');
            var raw_pct = $('#listing-edit-suggested-pct').val().replace(/[^\d]/g,'');
            listing.suggested_amt = raw_amt;
            listing.suggested_pct = raw_pct;
            listing.suggested_val = bids.calculateValuation(listing.suggested_amt, listing.suggested_pct);
            listing.summary = $('#listing-edit-summary').val();
            listing.business_plan_id = $('#listing-edit-business-plan-link').data('business_plan_id');
            listing.presentation_id = $('#listing-edit-presentation-link').data('presentation_id');
            listing.financials_id = $('#listing-edit-financials-link').data('financials_id');
            var callback = function(savedListing) {
                var msg = savedListing ? 'listing saved' : 'unable to save listing';
                var msg_class = savedListing ? 'confirmed' : 'attention';
                $('#listing-save-message').attr('class', msg_class).html(msg);
                if (savedListing) {
                    listingClass.displayListing(savedListing);
                }
            };
            if (listing.status === 'new') {
                var errorCallback = function() { $('#listing-save-message').attr('class', 'attention').html('unable to create listing'); };
                backend.createListing(listing, callback, errorCallback);
            }
            else {
                var errorCallback = function() { $('#listing-save-message').attr('class', 'attention').html('unable to save listing'); };
                backend.saveListing(listing, callback, errorCallback);
            }
        };
        self.newListing = function(profile) {
            var profile = user.profile;
            if (!profile) {
                self.displayListing();
                return;
            }
            var listing = {};
            listing.title = '';
            listing.median_valuation = 0;
            listing.num_votes = 0;
            listing.num_bids = 0;
            listing.num_comments = 0;
            listing.profile_id = profile.profile_id;
            listing.profile_username = profile.username;
            listing.listing_date = util.today();
            listing.status = 'new';
            listing.suggested_amt = 0;
            listing.suggested_pct = 0;
            listing.suggested_val = bids.calculateValuation(listing.suggested_amt, listing.suggested_pct);
            listing.summary = '';
            listing.business_plan_id = '';
            listing.presentation_id = '';
            listing.financials_id = '';
            self.displayListing(listing);
        };
        self.displayListingSnippetHtml = function(html) {
            $('#listing-snippet').html(html);
        };
        self.displayListing = function(data){
            $('#listing-save-message').attr('class', 'attention').html('');
            var listing = data && data.listing ? data.listing : data;
            var resultsHtml = '';
            var profile = user.profile;
            var isOwnListing = profile && profile.profile_id == listing.profile_id;
            var isListingEditable = isOwnListing && ((listing.status === 'new' || listing.status === 'created') || listing.status === 'active');

            // display listing snippet, exit if not found
            util.displayPage('listing');
            var errorMsg;
            if (!listing) {
                errorMsg = '<em>no listings found</em>';
            }
            else if (!isOwnListing && (listing.status === 'new' || listing.status === 'created')) {
                errorMsg = '<em>You must be logged to submit a new listing</em>';
            }
            if (errorMsg) {
                self.displayNullListing(errorMsg);
                statistics.hideCharts();
                return;
            }
            if (listing.listing_id) {
                $('#listing-snippet').hide().html(listingClass.generateListingSnippetHtml('listing', listing)).show();
                listingClass.bindListingSnippet('listing', listing);
            }

            // display listing message
            var msg = self.listingMessage(listing.status, isOwnListing);
            var msgClass = listing.status === 'active' ? 'confirmed' : 'attention';
            $('#listing-form-message').attr('class', msgClass).html(msg);

            if (isListingEditable) { // enable editable if logged in
                self.displayEditableListing(listing);
            }
            else {
                self.displayReadonlyListing(listing);
            }
            self.displayListingChart(listing);
        };
        self.displayListingChart = function(listing) {
            if (!listing || listing.status !== 'active') {
                statistics.hideCharts();
                return;
            }
            var stats = {
                label: listing.title,
                chartType: 'column',
                xaxis: '',
                yaxis: 'activity',
                labels: [ '#votes', '#bids', '#comments' ],
                values: [ listing.num_votes || 0, listing.num_bids || 0, listing.num_comments || 0 ]
            };
            statistics.displayStats(stats);
        };
        self.listingMessage = function(listingStatus, isOwnListing) {
            var msg = '';
            if (isOwnListing && listingStatus === 'withdrawn') {
                msg = 'You may not change your listing now that it is withdrawn.';
            }
            else if (listingStatus === 'new') {
                msg = 'This listing has not yet been saved.';
            }
            else if (listingStatus === 'created') {
                msg = 'This listing has not yet been activated, until then no bids are accepted.';
            }
            else if (listingStatus === 'active') {
                msg = 'This listing is active and accepting bids.';
            }
            else if (listingStatus === 'withdrawn') {
                msg = 'This listing is withdrawn and thus bidding is suspended.';
            }
            else {
                msg = 'The listing status is unknown.';
            }
            return msg;
        };
        self.displayNullListing = function(message) {
            $('#listing-form-editable,#listing-execsum-editable,#listing-form-readonly,#listing-execsum-readonly').hide();
            $('#listing-snippet').html(message).show();
        };
        self.displayReadonlyListing = function(listing) {
            $('#listing-form-editable,#listing-execsum-editable').hide();
            $('#listing-form-readonly,#listing-execsum-readonly').show();
            $('#listing-readonly-suggested-amt').html(util.addCommas(listing.suggested_amt));
            $('#listing-readonly-suggested-pct').html(listing.suggested_pct);
            $('#listing-readonly-suggested-val').html(util.addCommas(listing.suggested_val));
            $('#listing-readonly-summary').html(listing.summary);
            self.displayUploadField('listing-readonly-business-plan', 'business_plan_id', listing.business_plan_id);
            self.displayUploadField('listing-readonly-presentation', 'presentation_id', listing.presentation_id);
            self.displayUploadField('listing-readonly-financials', 'financials_id', listing.financials_id);
        };
        self.displayEditableListing = function(listing) {
            $('#listing-form-readonly,#listing-execsum-readonly').hide();
            $('#listing-form-editable,#listing-execsum-editable').show();
            $('#listing-edit-title').val(listing.title);
            $('#listing-edit-median-valuation').val(listing.median_valuation);
            $('#listing-edit-num-votes').val(listing.num_votes);
            $('#listing-edit-num-bids').val(listing.num_bids);
            $('#listing-edit-num-comments').val(listing.num_comments);
            $('#listing-edit-profile-id').val(listing.profile_id);
            $('#listing-edit-profile-username').val(listing.profile_username);
            $('#listing-edit-listing-date').val(listing.listing_date);
            $('#listing-edit-status').val(listing.status);
            $('#listing-edit-suggested-amt').val(util.addCommas(listing.suggested_amt));
            $('#listing-edit-suggested-pct').val(listing.suggested_pct);
            $('#listing-edit-suggested-val').html(util.addCommas(listing.suggested_val));
            $('#listing-edit-summary').val(listing.summary);
            $('#listing-edit-suggested-amt, #listing-edit-suggested-pct').unbind().bind('change',function(e){
                self.recalculateSuggestedValuation();
            });

            if (listing.status === 'new') {
                $('#listing-save-link').unbind().bind('click',function(e){
                    e.preventDefault();
                    listingClass.saveListing();
                });
            }
            else {
                $('#listing-save-link').unbind().data('listing_id',listing.listing_id).bind('click',function(e){
                    e.preventDefault();
                    listingClass.saveListing($(this).data('listing_id'));
                });
            }

            self.displayUploadField('listing-edit-business-plan', 'business_plan_id', listing.business_plan_id, true);
            self.displayUploadField('listing-edit-presentation', 'presentation_id', listing.presentation_id, true);
            self.displayUploadField('listing-edit-financials', 'financials_id', listing.financials_id, true);

            if (listing.status === 'new') {
                $('#listing-withdraw-link').hide();
                $('#listing-activate-link').hide();
            }
            else if (listing.status === 'created') {
                $('#listing-withdraw-link').hide();
                $('#listing-activate-link').show().unbind().data('listing_id',listing.listing_id).bind('click',function(){
                    var errorCallback = function() { $('#listing-save-message').attr('class', 'attention').html('unable to activate listing'); };
                    backend.activateListing($(this).data('listing_id'), self.displayListing, errorCallback);
                });
            }
            else {
                $('#listing-withdraw-link').show().unbind().data('listing_id',listing.listing_id).bind('click',function(){
                    var errorCallback = function() { $('#listing-save-message').attr('class', 'attention').html('unable to withdraw listing'); };
                    backend.withdrawListing($(this).data('listing_id'), self.displayListing, errorCallback);
                });
                $('#listing-activate-link').hide();
            }
        };
        self.displayUploadField = function(fieldRoot, fieldName, fieldValue, editable) {
            var fieldSelRoot = '#' + fieldRoot;
            var url;
            $(fieldSelRoot+'-form').hide();
            if (editable) {
                $(fieldSelRoot+'-add').unbind().bind('click', uploader.prepareUploader(fieldRoot, fieldName));
            }
            if (fieldValue) {
                url = '/file/download/' + fieldValue;
                $(fieldSelRoot+'-link a').attr('href', url);
                $(fieldSelRoot+'-link').show();
            }
            else {
                $(fieldSelRoot+'-link').hide();
                $(fieldSelRoot+'-remove').hide();
            }
            if (fieldValue && editable) {
                $(fieldSelRoot+'-link').data(fieldName, fieldValue);
                $(fieldSelRoot+'-remove').unbind().bind('click',function(){alert('not implemented yet');}).show();
            }
            else {
                $(fieldSelRoot+'-remove').hide();
            }
        };
        self.recalculateSuggestedValuation = function() {
            var suggested_amt  = $('#listing-edit-suggested-amt').val() || '';
            var suggested_pct  = $('#listing-edit-suggested-pct').val() || '';
            suggested_amt  = suggested_amt.replace(/[^\d]/g,'');
            suggested_pct  = suggested_pct.replace(/[^\d]/g,'');
            var valuation = bids.calculateValuation(suggested_amt, suggested_pct, true);
            var validationMsg = bids.validateBid(suggested_amt, 'common', 0, suggested_pct, valuation);
            $('#listing-save-message').attr('class', 'attention').html(validationMsg);
            var displayVal = valuation ? util.addCommas(valuation) : '';
            $('#listing-edit-suggested-val').html(displayVal);
        };
        self.showListingProfile = function(profile_id, index) {
            var errorCallback = function() { $('#content-results-list-item-message-'+index).attr('class', 'attention').html('unable to get profile'); };
            profiles.showProfile(profile_id, errorCallback);
        };
        self.showListing = function(listing_id, index) {
            var errorCallback = function() { $('#content-results-list-item-message-'+index).attr('class', 'attention').html('unable to get listing'); };
            backend.getListing(listing_id, self.displayListing, errorCallback);
        };
        self.showCommentsForListing = function(listing_id, index) {
            var errorCallback = function() { $('#content-results-list-item-message-'+index).attr('class', 'attention').html('unable to get comments'); };
            backend.commentsForListing(listing_id, comments.displayResults, errorCallback);
        };
        self.showBidsForListing = function(listing_id, index) {
            var errorCallback = function() { $('#content-results-list-item-message-'+index).attr('class', 'attention').html('unable to get bids'); };
            backend.bidsForListing(listing_id, bids.displayResults, errorCallback);
        };
        self.bindListingSnippet = function(i, listing) {
            var upvote_item = document.getElementById('content-results-list-item-upvote-'+i);
            $('#content-results-list-item-profile-'+i).data({index: i, profile_id: listing.profile_id}).unbind().bind('click', function(){
                util.hashedCall('listingClass', 'showListingProfile', [$(this).data('profile_id'), $(this).data('index')]);
            });           
            if (listing.listing_id) {
                $('#content-results-list-item-title-'+i).data({index: i, listing_id: listing.listing_id}).unbind().bind('click', function(){
                    util.hashedCall('listingClass', 'showListing', [$(this).data('listing_id'), $(this).data('index')]);
                });           
                $('#content-results-list-item-comments-'+i).data({index: i, listing_id: listing.listing_id}).unbind().bind('click', function(){
                    util.hashedCall('listingClass', 'showCommentsForListing', [$(this).data('listing_id'), $(this).data('index')]);
                }); 
                $('#content-results-list-item-bids-'+i).data({index: i, listing_id: listing.listing_id}).unbind().bind('click', function(){
                    util.hashedCall('listingClass', 'showBidsForListing', [$(this).data('listing_id'), $(this).data('index')]);
                });
            }
            $(upvote_item).data({index: i, listing_id: listing.listing_id, num_votes: listing.num_votes});
            if (!listing.votable) {
                $(upvote_item).css('visibility', 'hidden').unbind();
            }
            else {
                $(upvote_item)
                    .css('visibility', 'visible')
                    .unbind()
                    .bind('click', function(){
                        var index = $(this).data('index');
                        var listing_id = $(this).data('listing_id');
                        var num_votes = $(this).data('num_votes');
                        var list_item = this;
                        var votes_item = document.getElementById('content-results-list-item-votes-'+index);
                        var callback = function(json) {
                            if (json) {
                                var new_votes = num_votes + 1;
                                $(votes_item).html(new_votes);
                                $(list_item)
                                    .data('num_votes', new_votes)
                                    .css('visibility', 'hidden')
                                    .unbind(); // don't let them click twice
                            }
                        };
                        var errorCallback = function() { $('#content-results-list-item-message-'+$(this).data('index')).attr('class', 'attention').html('unable to upvote listing'); };
                        backend.upvoteListing(listing_id, callback, errorCallback);
                    });
            }
        };
        self.generateListingSnippetHtml = function(i, listing) {
            var days_ago = listing.days_ago;
            var days_left = listing.days_left;
            var resultHtml = '';
            var evenOdd;
            var fileUrl;
            if (isFinite(i)) {
                evenOdd = (i % 2 == 0) ? 'even' : 'odd';
                resultHtml += '<div class="content-results-list-item content-results-list-item-' + evenOdd + '">';
                resultHtml += '<div class="content-results-list-item-num">' + listing.num + '</div>';
            }
            else {
                resultHtml += '<div class="content-results-list-item">';
                resultHtml += '<div class="content-results-list-item-num"></div>';
            }
            if (user.profile && !(user.profile.profile_id === listing.profile_id)) {
                resultHtml += '<div class="content-results-list-item-upvote" id="content-results-list-item-upvote-' + i + '"></div>';
            }
            else {
                resultHtml += '<div class="content-results-list-item-upvote-filler"></div>';
            }
            resultHtml += '<div class="content-results-list-item-title link" id="content-results-list-item-title-' + i + '">';
            resultHtml += listing.title;
            resultHtml += '</div>';
            resultHtml += '<div class="content-results-list-item-profile link" id="content-results-list-item-profile-' + i + '">';
            resultHtml += '(' + listing.profile_username + ')';
            resultHtml += ' <span class="attention" id="content-results-list-item-message-' + i + '"></span>';
            resultHtml += '</div>';
            if (listing.business_plan_id) {
                fileUrl = "/file/download/" + listing.business_plan_id;
                resultHtml += '<a target="_blank" title="business plan" href="'
                        + fileUrl
                        + '" class="listing-icon business-plan-icon" id="content-results-list-item-business-plan-' + i + '"></a>';
            }
            if (listing.presentation_id) {
                fileUrl = "/file/download/" + listing.presentation_id;
                resultHtml += '<a target="_blank" title="presentation" href="'
                        + fileUrl
                        + '" class="listing-icon presentation-icon" id="content-results-list-item-presentation-' + i + '"></a>';
            }
            if (listing.financials_id) {
                fileUrl = "/file/download/" + listing.financials_id;
                resultHtml += '<a target="_blank" title="financials" href="'
                        + fileUrl
                        + '" class="listing-icon financials-icon" id="content-results-list-item-financials-' + i + '"></a>';
            }
            resultHtml += '<div class="content-results-list-item-clear"></div>';
            resultHtml += '<div class="content-results-list-item-detail">';
            resultHtml += '<span id="content-results-list-item-votes-' + i + '">' + listing.num_votes + '</span>' + ' votes | ';
            resultHtml += (days_ago == 0) ? 'today' : days_ago + ' day' + (days_ago!==1?'s':'')  + ' ago';
            resultHtml += ' | ' + listing.status + ' | ';
            resultHtml += '<span class="link" id="content-results-list-item-comments-' + i + '">';
            resultHtml += listing.num_comments + ' comment' + (listing.num_comments==1?'':'s');
            resultHtml += '</span> | ';
            resultHtml += '$' + util.addCommas(listing.median_valuation) + ' after ';
            resultHtml += '<span class="link" id="content-results-list-item-bids-' + i + '">';
            resultHtml += listing.num_bids + ' bid' + (listing.num_bids==1?'':'s');
            resultHtml += '</span> | ';
            resultHtml += 'listed ' + util.formatDate(listing.listing_date);
            resultHtml += '</div>';
            resultHtml += '</div>';
            resultHtml += '<div class="content-results-list-item-clear"></div>';
            return resultHtml;
       };
       return self;
    }

    var Comments = function() {
        var self = {};
        self.saveComment = function(listing_id, profile_id){
            var commentText = $('#comment-textarea').val();
            var callback = function (comment) {
                var keepComment = comment ? false : true;
                var callback2 = function(results) {
                    self.displayResults(results, keepComment, 'confirmed', 'comment saved');
                }
                var errorCallback2 = function() { $('#comments-message').attr('class', 'attention').html('unable to get comments'); util.displayPage('comments'); };
                backend.commentsForListing(listing_id, callback2, keepComment, errorCallback2);
            }
            var errorCallback = function() { $('#comment-save-message').attr('class', 'attention').html('unable to add comment'); };
            backend.addComment(listing_id, profile_id, commentText, callback, errorCallback);
        };
        self.displayNoResultsHtml = function(resultsHtml) {
            $('#comment-save-message').attr('class', 'attention').html('');
            $('#comment-textarea').val('');
            $('#comments-listing-snippet').html('Comments:');
            $('#comment-loggedin-add').hide();
            $('#comment-not-loggedin').hide();
            $('#comments-content-results-list').html(resultsHtml);
        };
        self.displayUserSnippet = function(comments_profile) {
            if (comments_profile) {
                $('#comments-profile-snippet').html(user.generateUserSnippetHtml('comments', comments_profile));
                user.bindUserSnippet('comments', comments_profile);
                $('#comments-profile-header').show();
            }
            else {
                $('#comments-profile-header').hide();
                $('#comments-profile-snippet').html('');
            }
        };
        self.displayComments = function(comments, listing) {
            var resultsHtml = '';
            var comment;
            if (comments && comments.length > 0) {
                for (var i = 0; i < comments.length; i++) { // create results
                    comment = comments[i];
                    resultsHtml += self.generateResultHtml(i, comment, listing, user.profile);
                }
            }
            else if (comments && comments.length == 0) {
                resultsHtml += '<div class="content-results-list-item">';
                resultsHtml += ' <span id="comments-item-message" class="attention item-message">no comments found</span>';
                resultsHtml += '</div>';
            }
            $('#comments-content-results-list').hide().html(resultsHtml).show(); // display html
        };
        self.displayListingSnippet = function(listing) {
            if (listing && listing.listing_id) {
                $('#comments-listing-snippet').html(listingClass.generateListingSnippetHtml('comments', listing));
                listingClass.bindListingSnippet('comments', listing);
                $('#comments-listing-header').show();
            }
        };
        self.displayCommentForm = function(listing) {
            if (!listing) {
                $('#comments-listing-snippet').html('Comments:');
                $('#comments-listing-header,#comment-loggedin-add,#comment-not-loggedin').hide();
                return;
            }
            if (!user.profile) {
                $('#comment-loggedin-add').hide();
                $('#comment-not-loggedin').show();
                return;
            }
            // enable editable if logged in
            $('#comment-loggedin-add').show();
            $('#comment-not-loggedin').hide();
            $('#comment-save-button').show().unbind().data('listing_id',listing.listing_id).data('profile_id',user.profile.profile_id).bind('click',function(e){
                e.preventDefault();
                self.saveComment($(this).data('listing_id'), $(this).data('profile_id'));
            });
        };
        self.bindCommentLinks = function(comments, listing) {
            var comment;
            if (!comments || comments.length == 0) {
                return;
            }
            var deleteComment = function() {
                var index = $(this).data('index');
                var comment_id = $(this).data('comment_id');
                var profile_id = $(this).data('profile_id');
                var listing_id = $(this).data('listing_id');
                var errorCallback = function() { $('#comments-item-message-'+index).attr('class', 'attention').html('unable to delete comment'); };
                var errorCallback2 = function() { $('#comments-message').attr('class', 'attention').html('unable to get comments'); util.displayPage('comments'); };
                var profile_callback = function() { backend.commentsForProfile(profile_id, self.displayResults, errorCallback2); };
                var listings_callback = function() { backend.commentsForListing(listing_id, self.displayResults, errorCallback2); };
                var callback = listing_id ? listings_callback : profile_callback;
                backend.deleteComment($(this).data('comment_id'), callback, errorCallback);
            };
            for (var i = 0; i < comments.length; i++) {
                comment = comments[i];
                $('#comments-item-profile-'+i).data({index:i,profile_id:comment.profile_id}).unbind().bind('click', function(){
                    util.hashedCall('comments', 'showCommentProfile', [$(this).data('profile_id'), $(this).data('index')]);
                });           
                $('#comments-item-listing-'+i).data({index:i,listing_id:comment.listing_id}).unbind().bind('click', function(){
                    util.hashedCall('comments', 'showCommentListing', [$(this).data('listing_id'), $(this).data('index')]);
                });           
                if (!user.profile || user.profile.profile_id != comment.profile_id) { // not my profile so I can't delete
                    continue;
                }
                $('#comments-item-delete-'+i)
                    .data({
                        index: i,
                        comment_id: comment.comment_id,
                        profile_id: (listing ? '' : comment.profile_id),
                        listing_id: (listing ? listing.listing_id : '')})
                    .unbind().bind('click', deleteComment);
            }
        };
        self.displayCommentStats = function(comments) {
            var comment, commentDate, i, k;
            var stats = {
                label: 'Comment Activity',
                xaxis: 'time',
                yaxis: '#comments',
                values: []
            }
            var commentMap = {};
            var commentDates = [];
            for (i = 0; i < comments.length; i++) {
                comment = comments[i];
                commentDate = comment.comment_date;
                commentMap[commentDate] = commentMap[commentDate] ? commentMap[commentDate] + 1 : 1;
            }
            for (k in commentMap) {
                commentDates.push(k);
            }
            commentDates.sort(function(a,b){return a-b;});
            for (i = 0; i < commentDates.length; i++) {
                commentDate = commentDates[i];
                stats.values[i] = commentMap[commentDate];
            }
            stats.values.reverse();
            statistics.displayStats(stats);
        };
        self.displayResults = function(results, keepComment, msgClass, msgText){
            var comments_profile = results ? results.profile : null;
            var listing = results ? results.listing : null;
            var comments = results ? results.comments : null;

            $('#comments-message').html('');
            $('#comment-save-message').attr('class', (msgClass || 'attention')).html(msgText || '');
            if (!keepComment) {
                $('#comment-textarea').val('');
            }
            util.displayPage('comments');

            self.displayUserSnippet(comments_profile);
            self.displayListingSnippet(listing);
            self.displayCommentForm(listing);
            self.displayComments(comments, listing);
            self.displayCommentStats(comments);
            self.bindCommentLinks(comments, listing);
        };
        self.showCommentProfile = function(profile_id, index) {
            var errorCallback = function() { $('#comments-item-message-'+index).attr('class', 'attention').html('unable to get profile'); };
            profiles.showProfile(profile_id, errorCallback);
        };
        self.showCommentListing = function(listing_id, index) {
            var errorCallback = function() { $('#comments-item-message-'+index).attr('class', 'attention').html('unable to get listing'); };
            backend.getListing(listing_id, listingClass.displayListing, errorCallback);
        };
        self.generateResultHtml = function(i, comment, listing, profile) {
            var resultHtml = '';
            var evenOdd;
            if (isFinite(i)) {
                evenOdd = (i % 2 == 0) ? 'even' : 'odd';
                resultHtml += '<div class="content-results-list-item content-results-list-item-' + evenOdd + '">';
            }
            else {
                resultHtml += '<div class="content-results-list-item">';
            }
            resultHtml += '<div class="content-results-list-item-num">' + comment.num + '</div>';
            resultHtml += '<div class="content-results-list-item-title">';
            resultHtml += comment.text;
            resultHtml += '</div>';
            if (profile && profile.profile_id == comment.profile_id) {
                resultHtml += '<div class="content-results-list-item-profile link" id="comments-item-delete-' + i + '">(delete comment)';
                resultHtml += ' <span id="comments-item-message-'+i+'" class="attention"></span>';
                resultHtml += '</div>';
            }
            resultHtml += '<div class="content-results-list-item-clear"></div>';
            resultHtml += '<div class="content-results-list-item-detail">';
            resultHtml += 'by ';
            resultHtml += '<span class="link" id="comments-item-profile-' + i + '">'
            resultHtml += comment.profile_username;
            resultHtml += '</span>'
            resultHtml += ' on ';
            resultHtml += util.formatDate(comment.comment_date);
            resultHtml += listing ? '' : ' for <span class="link" id="comments-item-listing-' + i + '">' + comment.listing_title + '</span>';
            resultHtml += '</div>';
            resultHtml += '</div>'
            resultHtml += '<div class="content-results-list-item-clear"></div>';
            return resultHtml;
        };
        return self;
    };

    var Bids = function() {
        var self = {};
        self.saveBid = function(listing_id, profile_id, profile_username){
            var raw_amt  = $('#bid-amt').val() || '';
            var raw_pct  = $('#bid-pct').val() || '';
            var raw_type = $('#bid-type').val() || '';
            var raw_rate = $('#bid-rate').val() || '';

            raw_amt = raw_amt.replace(/[^\d]+/g,'');
            raw_pct = raw_pct.replace(/[^\d]+/g,'');
            raw_rate = raw_rate.replace(/[^\d]+/g,'');

            var bid_amt  = raw_amt != '' ? raw_amt : 0;
            var bid_type = ((raw_type === 'common' || raw_type === 'preferred') || raw_type === 'note') ? raw_type : 'common';
            var bid_rate = raw_rate != '' ? raw_rate : 0;
            var bid_pct  = raw_pct != '' ? raw_pct : 0;

            var valuation = self.calculateValuation(bid_amt, bid_pct, true);

            var validationMsg = self.validateBid(bid_amt, bid_type, bid_rate, bid_pct, valuation);
            if (validationMsg !== '') {
                $('#bid-save-message').attr('class', 'attention').html(validationMsg);
                return;
            }

            var callback = function(bid) {
                var callback2 = function(results) {
                    self.displayResults(results, false, 'confirmed', 'bid saved');
                };
                var errorCallback2 = function() { $('#bids-message').attr('class', 'attention').html('unable to get bids'); util.displayPage('bids'); };
                if (bid) {
                    backend.bidsForListing(bid.listing_id, callback2, errorCallback2);
                }
                else {
                    $('#bid-save-message').attr('class', 'attention').html('unable to save bid');
                }
            };
            var errorCallback = function() { $('#bid-save-message').attr('class', 'attention').html('unable to add bid'); };
            backend.addBid(listing_id, profile_id, profile_username, bid_amt, bid_pct, bid_type, bid_rate, valuation, callback, errorCallback);
        };
        self.validateBid = function(bid_amt, bid_type, bid_rate, bid_pct, valuation) {
            var msg;
            if (bid_amt < 1000) {
                msg = 'your bid must be at least $1,000';
            }
            else if (bid_amt > 1000000) {
                msg = 'your bid must be no more than $1,000,000';
            }
            else if (bid_pct < 1) {
                msg = 'your bid percentage must be at least 1%';
            }
            else if (bid_pct > 50) {
                msg = 'your bid percentage must be no more than 50%';
            }
            else if (bid_type != 'common' && bid_rate < 1) {
                msg = 'your bid preferred or note rate must be at least 1%';
            }
            else if (bid_type != 'common' && bid_rate > 20) {
                msg = 'your bid interest rate must be no more than 20%';
            }
            else if (valuation < 10000) {
                msg = 'your calculated valuation must be at least $10,000';
            }
            else {
                msg = '';
            }
            return msg;
        };
        self.clearBidFields = function() {
            $('#bid-amt').val('');
            $('#bid-pct').val('');
            $('#bid-type').val('common');
            $('#bid-rate').val('');
            $('#bid-rate').attr('disabled', true);
            $('#bid-calculated-valuation').html('');
        };
        self.displayNoResultsHtml = function(resultsHtml) {
            util.displayPage('bids');
            $('#bids-form-loggedin').hide();
            $('#bids-form-not-loggedin').hide();
            $('#bids-listing-snippet').html('');
            $('#bids-content-results-list').html(resultsHtml);
        };
        self.displayAcceptedResults = function(results, keepBid, msgClass, msgText){
            self.displayResults(results, keepBid, msgClass, msgText, 'accepted');
        };
        self.displayPaidResults = function(results, keepBid, msgClass, msgText){
            self.displayResults(results, keepBid, msgClass, msgText, 'paid');
        };
        self.displayResults = function(results, keepBid, msgClass, msgText, bidStatus){
            var bids_profile = results ? results.profile : null;
            var listing = results ? results.listing : null;
            var bids = results ? results.bids : null;
            var profile = user.profile;
            var filteredBids = self.filterBidsByStatus(bids, bidStatus);
            var profileBids = self.filterBidsWithProfile(filteredBids, profile);
            var profileBid = profileBids && profileBids.length ? profileBids[0] : undefined;

            $('#bids-message').html('');
            $('#bid-save-message').attr('class', (msgClass || 'attention')).html(msgText || '');
            self.recalculateValuation();
            if (!keepBid) {
                self.clearBidFields();
            }
            util.displayPage('bids');

            self.displayBidSnippet(bids_profile);
            self.displayBidForm(listing, profile, profileBid);
            self.displayBids(filteredBids, listing, profile);
            self.displayBidStats(filteredBids);
            self.bindBidLinks(filteredBids, profile, listing, bidStatus);
        };
        self.withdrawBidFunc = function(index) {
            var withdrawBid = function() {
                var index = $(this).data('index');
                var bid_id = $(this).data('bid_id');
                var profile_id = $(this).data('profile_id');
                var listing_id = $(this).data('listing_id');
                var errorCallback = function() { $('#bids-item-message-'+index).attr('class', 'attention').html('unable to delete bid'); };
                var errorCallback2 = function() { $('#bids-message').attr('class', 'attention').html('unable to get bids'); util.displayPage('bids'); };
                var profile_callback = function() { backend.bidsForProfile(profile_id, self.displayResults, errorCallback2); };
                var listings_callback = function() { backend.bidsForListing(listing_id, self.displayResults, errorCallback2); };
                var callback = listing_id ? listings_callback : profile_callback;
                backend.withdrawBid($(this).data('bid_id'), callback, errorCallback);
            };
            return withdrawBid;
        };
        self.acceptBidFunc = function(index) {
            var acceptBid = function() {
                var index = $(this).data('index');
                var bid_id = $(this).data('bid_id');
                var profile_id = $(this).data('profile_id');
                var listing_id = $(this).data('listing_id');
                var errorCallback = function() { $('#bids-item-message-'+index).attr('class', 'attention').html('unable to accept bid'); };
                var errorCallback2 = function() { $('#bids-message').attr('class', 'attention').html('unable to get bids'); util.displayPage('bids'); };
                var profile_callback = function() { backend.bidsForProfile(profile_id, self.displayResults, errorCallback2); };
                var listings_callback = function() { backend.bidsForListing(listing_id, self.displayResults, errorCallback2); };
                var callback = listing_id ? listings_callback : profile_callback;
                backend.acceptBid($(this).data('bid_id'), callback, errorCallback);
            };
            return acceptBid;
        };
        self.displayBidSnippet = function(bids_profile) {
            if (bids_profile) {
                $('#bids-profile-snippet').html(user.generateUserSnippetHtml('bids', bids_profile));
                user.bindUserSnippet('bids', bids_profile);
                $('#bids-profile-header').show();
            }
            else {
                $('#bids-profile-header').hide();
                $('#bids-profile-snippet').html('');
            }
        };
        self.displayBidForm = function(listing, profile, profileBid) {
            if (!listing) {
                $('#bids-listing-header').hide();
                $('#bids-form-loggedin').hide();
                $('#bids-form-not-loggedin').hide();
                $('#bids-listing-snippet').html('');
                return;
            }
            if (listing.listing_id) {
                $('#bids-listing-snippet').html(listingClass.generateListingSnippetHtml('bids', listing));
                listingClass.bindListingSnippet('bids', listing);
                $('#bids-listing-header').show();
            }
            if (listing.status != 'active') {
                $('#bids-form-loggedin').hide();
                $('#bids-form-not-loggedin').show().html('This bid is no longer active');
            }
            else if (profile && profile.profile_id == listing.profile_id) {
                $('#bids-form-loggedin').hide();
                $('#bids-form-not-loggedin').show().html('You may not bid on your own listing');
            }
            else if (profile && profile.investor) {
                if (profileBid) { /* prefill form with previous bid */
                    $('#bid-form-new').hide();
                    $('#bid-form-update-date').html(util.formatDate(profileBid.bid_date));
                    $('#bid-form-update').show();
                    $('#bid-amt').val(util.addCommas(profileBid.amount));
                    $('#bid-pct').val(profileBid.equity_pct);
                    $('#bid-type').val(profileBid.bid_type);
                    if ($('#bid-type').val() == 'common') {
                        $('#bid-rate').attr('disabled', true);
                        $('#bid-rate').val('');
                    }
                    else {
                        $('#bid-rate').removeAttr('disabled');
                        $('#bid-rate').val(profileBid.interest_rate);
                    }
                    $('#bid-calculated-valuation').html(util.addCommas(profileBid.valuation));
                }
                else {
                    $('#bid-form-update').hide();
                    $('#bid-form-new').show();
                }
                $('#bids-form-loggedin').show();
                $('#bids-form-not-loggedin').hide();
                $('#bid-save-button')
                    .show()
                    .unbind()
                    .data('listing_id',listing.listing_id)
                    .data('profile_id',profile.profile_id)
                    .data('profile_username',profile.username)
                    .bind('click',function(e) {
                        e.preventDefault();
                        self.saveBid($(this).data('listing_id'), $(this).data('profile_id'), $(this).data('profile_username'));
                    });
                $('#bid-amt,#bid-pct,#bid-type,#bid-rate').unbind().bind('change',function(e){
                    self.recalculateValuation();
                });
                $('#bid-type').unbind().bind('change',function(e){
                    if ($('#bid-type').val() == 'common')
                        $('#bid-rate').attr('disabled', true);
                    else 
                        $('#bid-rate').removeAttr('disabled');
                });
            }
            else if (profile && !profile.investor) {
                $('#bids-form-loggedin').hide();
                $('#bids-form-not-loggedin').show().html('You must be an investor to place a bid');
            }
            else {
                $('#bids-form-loggedin').hide();
                $('#bids-form-not-loggedin').show().html('You must be a logged in investor to place a bid');
            }
        };
        self.filterBidsWithProfile = function(bids, profile) {
            var bid;
            var filteredBids = [];
            if (bids && bids.length > 0 && profile) {
                for (var i = 0; i < bids.length; i++) { // create results
                    bid = bids[i];
                    if (!bid) {
                        continue;
                    }
                    if (bid.profile_id !== profile.profile_id) {
                        continue;
                    }
                    filteredBids.push(bid);
                }
            }
            return filteredBids;
        };
        self.filterBidsByStatus = function(bids, bidStatus) {
            var bid;
            var filteredBids = [];
            if (bids && bids.length > 0 && bidStatus) {
                for (var i = 0; i < bids.length; i++) { // create results
                    bid = bids[i];
                    if (!bid) {
                        continue;
                    }
                    if (bid.status !== bidStatus) {
                        continue;
                    }
                    filteredBids.push(bid);
                }
            }
            else {
                filteredBids = bids;
            }
            return filteredBids;
        };
        self.displayBids = function(bids, listing, profile) {
            var bid;
            var resultsHtml = '';
            if (!bids || bids.length === 0) {
                resultsHtml += '<div class="content-results-list-item">';
                resultsHtml += ' <span id="bids-item-message" class="attention item-message">no bids found</span>';
                resultsHtml += '</div>';
            }
            else {
                for (var i = 0; i < bids.length; i++) { // create results
                    bid = bids[i];
                    if (bid) {
                        resultsHtml += self.generateResultHtml(i, bid, listing, profile);
                    }
                }
            }
            $('#bids-content-results-list').hide().html(resultsHtml).show();
        };
        self.bindBidLinks = function(bids, profile, listing) {
            if (!bids || bids.length == 0) {
                return;
            }
            for (var i = 0; i < bids.length; i++) {
                bid = bids[i];
                $('#bids-item-profile-'+i).data({index:i,profile_id:bid.profile_id}).unbind().bind('click', function(){
                    util.hashedCall('bids', 'showBidProfile', [$(this).data('profile_id'), $(this).data('index')]);
                });           
                $('#bids-item-listing-'+i).data({index:i,listing_id:bid.listing_id}).unbind().bind('click', function(){
                    util.hashedCall('bids', 'showBidListing', [$(this).data('listing_id'), $(this).data('index')]);
                });           
                if (profile && profile.profile_id === bid.profile_id && bid.status === 'active') { // user can withdraw their bid
                    $('#bids-item-delete-'+i)
                        .data('bid_id', bid.bid_id)
                        .data('profile_id', listing ? '' : bid.profile_id)
                        .data('listing_id', listing ? results.listing.listing_id : '')
                        .unbind()
                        .bind('click', self.withdrawBidFunc(i));
                }
                if (listing
                    && profile
                    && (profile.profile_id === listing.profile_id || profile.profile_id === bid.listing_profile_id)
                    && bid.status === 'active') { // user can accept a bid for their own listing
                    $('#bids-item-accept-'+i)
                        .data('bid_id', bid.bid_id)
                        .data('profile_id', listing ? '' : bid.profile_id)
                        .data('listing_id', listing ? listing.listing_id : '')
                        .unbind()
                        .bind('click', self.acceptBidFunc(i));
                }
            }
        };
        self.showBidProfile = function(profile_id, index) {
            var errorCallback = function() { $('#bids-item-message-'+index).attr('class', 'attention').html('unable to get profile'); };
            profiles.showProfile(profile_id, errorCallback);
        };
        self.showBidListing = function(listing_id, index) {
            var errorCallback = function() { $('#bids-item-message-'+index).attr('class', 'attention').html('unable to get listing'); };
            backend.getListing(listing_id, listingClass.displayListing, errorCallback);
        };
        self.formatBidType = function(bid_type) {
            var visualMapping = {
                common: 'common stock',
                preferred: 'preferred stock',
                note: 'a convertible note',
                syndicate: 'common stock as a syndicate',
                sole_investor: 'common stock as a sole investor'
            };
            var bidType = bid_type in visualMapping ? visualMapping[bid_type] : bid_type;
            return bidType;
        };
        self.generateResultHtml = function(i, bid, listing, profile) {
            var resultHtml = '';
            var evenOdd;
            if (isFinite(i)) {
                evenOdd = (i % 2 == 0) ? 'even' : 'odd';
                resultHtml += '<div class="content-results-list-item content-results-list-item-' + evenOdd + '">';
            }
            else {
                resultHtml += '<div class="content-results-list-item">';
            }
            resultHtml += '<div class="content-results-list-item-num">' + bid.num + '</div>';
            resultHtml += '<div class="content-results-list-item-title">';
            resultHtml += 'Valued at ';
            resultHtml += '$' + util.addCommas(bid.valuation);
            resultHtml += ' with a bid of ';
            resultHtml += '$' + util.addCommas(bid.amount);
            resultHtml += ' for ' + bid.equity_pct + '%';
            resultHtml += ' with ' + self.formatBidType(bid.bid_type);
            resultHtml += bid.interest_rate ? ' @ ' + bid.interest_rate + '%' : '';
            resultHtml += '</div>';
            if (profile && profile.profile_id === bid.profile_id && bid.status === 'active') { // user can withdraw their own bid
                resultHtml += '<div class="content-results-list-item-profile link" id="bids-item-delete-' + i + '">(withdraw bid)</div>';
            }
            if (listing
                && profile
                && (profile.profile_id === listing.profile_id || profile.profile_id === bid.listing_profile_id)
                && bid.status === 'active') { // user can accept a bid for their own listing
                resultHtml += '<div class="content-results-list-item-profile link" id="bids-item-accept-' + i + '">(accept bid)</div>';
            }
            resultHtml += '<div class="content-results-list-item-profile">';
            resultHtml += ' <span id="bids-item-message-'+i+'" class="attention"></span>';
            resultHtml += '</div>';
            resultHtml += '<div class="content-results-list-item-clear"></div>';
            resultHtml += '<div class="content-results-list-item-detail">';
            resultHtml += 'by ';
            resultHtml += '<span class="link" id="bids-item-profile-' + i + '">'
            resultHtml += bid.profile_username;
            resultHtml += '</span>'
            resultHtml += ' on ';
            resultHtml += util.formatDate(bid.bid_date);
            resultHtml += listing ? '' : ' for <span class="link" id="bids-item-listing-' + i + '">' + bid.listing_title + '</span>';
            resultHtml += bid.status !== 'active' ? ' <b>bid '+(bid.status?bid.status:'not active')+'</b>' : '';
            resultHtml += '</div>';
            resultHtml += '</div>'
            resultHtml += '<div class="content-results-list-item-clear"></div>';
            return resultHtml;
        };
        self.displayBidStats = function(bids) {
            var bid, i;
            var stats = {
                label: 'Bid Activity',
                xaxis: 'time',
                yaxis: 'valuation',
                prefix: '$',
                suffix: 'k',
                values: []
            }
            for (i = 0; i < bids.length; i++) {
                bid = bids[i];
                stats.values[i] = bid.valuation / 1000;
            }
            stats.values.reverse();
            statistics.displayStats(stats);
        };
        self.recalculateValuation = function(){
            var bid_amt  = $('#bid-amt').val() || '';
            var bid_pct  = $('#bid-pct').val() || '';
            var bid_type = $('#bid-type').val() || '';
            var bid_rate = $('#bid-rate').val() || '';
            bid_amt  = bid_amt.replace(/[^\d]/g,'');
            bid_pct  = bid_pct.replace(/[^\d]/g,'');
            bid_rate = bid_rate.replace(/[^\d]/g,'');
            var valuation = self.calculateValuation(bid_amt, bid_pct, true);
            var validationMsg = self.validateBid(bid_amt, bid_type, bid_rate, bid_pct, valuation);
            $('#bid-save-message').attr('class', 'attention').html(validationMsg);
            var displayVal = valuation ? util.addCommas(valuation) : '';
            $('#bid-calculated-valuation').html(displayVal);
        };
        self.calculateValuation = function(amount, equity_pct, thousands) {
            var valuation;
            if (amount >= 1000 && equity_pct >= 1) {
                if (thousands)
                    valuation = Math.floor(1000 * Math.floor((100 * amount / equity_pct) / 1000));
                else
                    valuation = Math.floor(100 * amount / equity_pct);
            }
            else {
                valuation = 0;
            }
            return valuation;
        };
        return self;
    };

    var Backend = function() {
        var self = {};
        self.userAssignCallback = function(resultsCallback) { // always check if user is still logged in
            var callback = function(results) {
                if (results) {
                    if (results.login_url)     {
                        user.login_url = results.login_url;
                    }
                    if (results.logout_url) {
                        user.logout_url = results.logout_url;
                    }
                    if (results.loggedin_profile) {
                        user.setProfile(results.loggedin_profile);
                        $('#userbox').trigger('display');
                    }
                }
                resultsCallback(results);
            };
            return callback;
        };
        // profile methods
        self.getProfile = function(profile_id, callback, errorCallback) {
            var url = '/user?id=' + profile_id;
            $.getJSON(url).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        self.profileForUsername = function(username, callback, errorCallback) {
            var url = '/user/' + username;
            $.getJSON(url).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        self.saveProfile = function(profile, callback, errorCallback) {
            var url = '/user/update/?id=' + profile.profile_id;
            $.post(url, {profile: $.JSON.encode(profile)}).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        self.activateProfile = function(profile_id, callback, errorCallback) {
            var url = '/user/activate/' + profile_id;
            $.post(url).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        self.deactivateProfile = function(profile_id, callback, errorCallback) {
            var url = '/user/deactivate/' + profile_id;
            $.post(url).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        self.searchUsers = function(searchType, keywords, start_index, max_results, callback, errorCallback) {
            searchType = 'all'; // FIXME
            var pathSuffix = searchType + '/?max_results=20'
                + (keywords && keywords.length > 0 ? '&text='+keywords : '');
            var url = '/users/' + pathSuffix;
            $.getJSON(url).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        self.upvoteUser = function(profile_id, callback, errorCallback) {
            var url = '/user/up/' + profile_id;
            $.post(url).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        // listing methods
        self.getListing = function(listing_id, callback, errorCallback) {
            var url = '/listings/get/' + listing_id;
            $.getJSON(url).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        self.listingsForProfile = function(profile_id, callback, errorCallback) {
            var url = '/listings/user/?id=' + profile_id;
            $.getJSON(url).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        self.searchListings = function(searchType, keywords, profile_id, start_index, max_results, callback, errorCallback) {
            var pathSuffix = '';
            if (profile_id !== undefined) {
                pathSuffix = 'user/?id=' + profile_id;
            }
            else {
                pathSuffix = searchType + '/?max_results=20';
            }
            pathSuffix += (keywords && keywords.length > 0 ? '&text='+keywords : '');
            var url = '/listings/' + pathSuffix;
            $.getJSON(url).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        self.createListing = function(listing, callback, errorCallback) {
            var url = '/listings/create/';
            $.post(url, {listing: $.JSON.encode(listing)}).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        self.saveListing = function(listing, callback, errorCallback) {
            var url = '/listings/update/';
            $.post(url, {listing: $.JSON.encode(listing)}).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        self.upvoteListing = function(listing_id, callback, errorCallback) {
            var url = '/listings/up/' + listing_id;
            $.post(url).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        self.activateListing = function(listing_id, callback, errorCallback) {
            var url = '/listings/activate/' + listing_id;
            $.post(url).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        self.withdrawListing = function(listing_id, callback, errorCallback) {
            var url = '/listings/withdraw/' + listing_id;
            $.post(url).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        // upload methods
        self.getUploadUrls = function(urlCount, callback, errorCallback) {
            var url = '/file/get-upload-url/' + urlCount + '/';
            $.get(url).success(callback).error(errorCallback); // note: no user assign callback or json
        };
        // comment methods
        self.commentsForListing = function(listing_id, callback, errorCallback) {
            var url = '/comments/listing/' + listing_id + '/';
            $.getJSON(url).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        self.commentsForProfile = function(profile_id, callback, errorCallback) {
            var url = '/comments/user/' + profile_id + '/';
            $.getJSON(url).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        self.addComment = function(listing_id, profile_id, commentText, callback, errorCallback) {
            var url = '/comment/create';
            var comment = {
                listing_id: listing_id,
                profile_id: profile_id,
                text: commentText
            };
            $.post(url, { comment: $.JSON.encode(comment) }).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        self.deleteComment = function(comment_id, callback, errorCallback) {
            var url = '/comment/delete/' + comment_id;
            $.post(url).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        // bid methods
        self.bidsForListing = function(listing_id, callback, errorCallback) {
            var url = '/bids/listing/' + listing_id + '/';
            $.getJSON(url).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        self.bidsForProfile = function(profile_id, callback, errorCallback) {
            var url = '/bids/user/' + profile_id + '/';
            $.getJSON(url).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        self.addBid = function(listing_id, profile_id, profile_username, bid_amt, bid_pct, bid_type, bid_rate, valuation, callback, errorCallback) {
            var url = '/bid/create/';
            var bid = {
                listing_id: listing_id,
                profile_id: profile_id,
                profile_username: profile_username,
                amount: bid_amt,
                equity_pct: bid_pct,
                bid_type: bid_type,
                interest_rate: bid_rate,
                valuation: valuation
            };
            $.post(url, { bid: $.JSON.encode(bid) }).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        self.withdrawBid = function(bid_id, callback, errorCallback) {
            var url = '/bid/withdraw/?id=' + bid_id;
            $.post(url).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        self.acceptBid = function(bid_id, callback, errorCallback) {
            var url = '/bid/accept/?id=' + bid_id;
            $.post(url).success(self.userAssignCallback(callback)).error(errorCallback);
        };
        self.bidStats = function(callback, errorCallback) {
            if (!errorCallback) {
                errorCallback = function () { $('#statsbox').hide(); };
            }
            var url = '/bids/bid-day-valuation/';
            $.get(url).success(self.userAssignCallback(callback)).error(errorCallback);
//            var stats = {
//                    label: "10 Day Bid Volume",
//                    xaxis: "days ago",
//                    yaxis: "#bids",
//                    values: [ 7, 1, 9, 7, 5, 6, 2, 6, 7, 2 ]
//                              2, 1, 8, 6, 8, 0, 6, 5, 4, 8,
//                              3, 0, 1, 0, 8, 8, 9, 9, 7, 5 ]
//            };
//            callback(stats);
         };
         return self;
    };

    var util = new Util();
    var statistics = new Statistics();
    var backend = new Backend();
    var user = new User();
    var uploader = new Uploader();
    var header = new Header();
    var searchListings = new SearchListings();
    var searchUsers = new SearchUsers();
    var userbox = new Userbox();
    var profiles = new Profiles();
    var listingClass = new ListingClass();
    var comments = new Comments();
    var bids = new Bids();

    util.setup();
    header.setup();
    searchListings.setup();
    searchUsers.setup();
    util.applyResizer(header.resizeStyles);
    $('#mainbody').addTouch(); // enable for touch devices
    $(window).hashchange(util.hashchange);
    $(window).hashchange();
});
}






















