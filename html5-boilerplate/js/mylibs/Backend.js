function Backend() {
			var self = {};
			self.userAssignCallback = function(resultsCallback) { // always check if user is still logged in
				var callback = function(results) {
					if (results) {
						if (results.login_url) {
							self.imports.user.login_url = results.login_url;
						}
						if (results.logout_url) {
							self.imports.user.logout_url = results.logout_url;
						}
						if (results.loggedin_profile) {
							self.imports.user.setProfile(results.loggedin_profile);
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
				$.getJSON(url).success(self.userAssignCallback(callback))
						.error(errorCallback);
			};
			self.profileForUsername = function(username, callback,
					errorCallback) {
				var url = '/user/' + username;
				$.getJSON(url).success(self.userAssignCallback(callback))
						.error(errorCallback);
			};
			self.saveProfile = function(profile, callback, errorCallback) {
				var url = '/user/update/?id=' + profile.profile_id;
				$.post(url, {
					profile : $.JSON.encode(profile)
				}).success(self.userAssignCallback(callback)).error(
						errorCallback);
			};
			self.activateProfile = function(profile_id, callback, errorCallback) {
				var url = '/user/activate/' + profile_id;
				$.post(url).success(self.userAssignCallback(callback)).error(
						errorCallback);
			};
			self.deactivateProfile = function(profile_id, callback,
					errorCallback) {
				var url = '/user/deactivate/' + profile_id;
				$.post(url).success(self.userAssignCallback(callback)).error(
						errorCallback);
			};
			self.searchUsers = function(searchType, keywords, start_index,
					max_results, callback, errorCallback) {
				searchType = 'all'; // FIXME
				var pathSuffix = searchType
						+ '/?max_results=20'
						+ (keywords && keywords.length > 0 ? '&text='
								+ keywords : '');
				var url = '/users/' + pathSuffix;
				$.getJSON(url).success(self.userAssignCallback(callback))
						.error(errorCallback);
			};
			self.upvoteUser = function(profile_id, callback, errorCallback) {
				var url = '/user/up/' + profile_id;
				$.post(url).success(self.userAssignCallback(callback)).error(
						errorCallback);
			};
			// listing methods
			self.getListing = function(listing_id, callback, errorCallback) {
				var url = '/listings/get/' + listing_id;
				$.getJSON(url).success(self.userAssignCallback(callback))
						.error(errorCallback);
			};
			self.listingsForProfile = function(profile_id, callback,
					errorCallback) {
				var url = '/listings/user/?id=' + profile_id;
				$.getJSON(url).success(self.userAssignCallback(callback))
						.error(errorCallback);
			};
			self.searchListings = function(searchType, keywords, profile_id,
					start_index, max_results, callback, errorCallback) {
				var pathSuffix = '';
				if (profile_id !== undefined) {
					pathSuffix = 'user/?id=' + profile_id;
				} else {
					pathSuffix = searchType + '/?max_results=20';
				}
				pathSuffix += (keywords && keywords.length > 0 ? '&text='
						+ keywords : '');
				var url = '/listings/' + pathSuffix;
				$.getJSON(url).success(self.userAssignCallback(callback))
						.error(errorCallback);
			};
			self.createListing = function(listing, callback, errorCallback) {
				var url = '/listings/create/';
				$.post(url, {
					listing : $.JSON.encode(listing)
				}).success(self.userAssignCallback(callback)).error(
						errorCallback);
			};
			self.saveListing = function(listing, callback, errorCallback) {
				var url = '/listings/update/';
				$.post(url, {
					listing : $.JSON.encode(listing)
				}).success(self.userAssignCallback(callback)).error(
						errorCallback);
			};
			self.upvoteListing = function(listing_id, callback, errorCallback) {
				var url = '/listings/up/' + listing_id;
				$.post(url).success(self.userAssignCallback(callback)).error(
						errorCallback);
			};
			self.activateListing = function(listing_id, callback, errorCallback) {
				var url = '/listings/activate/' + listing_id;
				$.post(url).success(self.userAssignCallback(callback)).error(
						errorCallback);
			};
			self.withdrawListing = function(listing_id, callback, errorCallback) {
				var url = '/listings/withdraw/' + listing_id;
				$.post(url).success(self.userAssignCallback(callback)).error(
						errorCallback);
			};
			// upload methods
			self.getUploadUrls = function(urlCount, callback, errorCallback) {
				var url = '/file/get-upload-url/' + urlCount + '/';
				$.get(url).success(callback).error(errorCallback); // note: no user assign callback or json
			};
			// comment methods
			self.commentsForListing = function(listing_id, callback,
					errorCallback) {
				var url = '/comments/listing/' + listing_id + '/';
				$.getJSON(url).success(self.userAssignCallback(callback))
						.error(errorCallback);
			};
			self.commentsForProfile = function(profile_id, callback,
					errorCallback) {
				var url = '/comments/user/' + profile_id + '/';
				$.getJSON(url).success(self.userAssignCallback(callback))
						.error(errorCallback);
			};
			self.addComment = function(listing_id, profile_id, commentText,
					callback, errorCallback) {
				var url = '/comment/create';
				var comment = {
					listing_id : listing_id,
					profile_id : profile_id,
					text : commentText
				};
				$.post(url, {
					comment : $.JSON.encode(comment)
				}).success(self.userAssignCallback(callback)).error(
						errorCallback);
			};
			self.deleteComment = function(comment_id, callback, errorCallback) {
				var url = '/comment/delete/' + comment_id;
				$.post(url).success(self.userAssignCallback(callback)).error(
						errorCallback);
			};
			// bid methods
			self.bidsForListing = function(listing_id, callback, errorCallback) {
				var url = '/bids/listing/' + listing_id + '/';
				$.getJSON(url).success(self.userAssignCallback(callback))
						.error(errorCallback);
			};
			self.bidsForProfile = function(profile_id, callback, errorCallback) {
				var url = '/bids/user/' + profile_id + '/';
				$.getJSON(url).success(self.userAssignCallback(callback))
						.error(errorCallback);
			};
			self.addBid = function(listing_id, profile_id, profile_username,
					bid_amt, bid_pct, bid_type, bid_rate, valuation, bid_note,
					callback, errorCallback) {
				var url = '/bid/create/';
				var bid = {
					listing_id : listing_id,
					profile_id : profile_id,
					profile_username : profile_username,
					amount : bid_amt,
					equity_pct : bid_pct,
					bid_type : bid_type,
					interest_rate : bid_rate,
					valuation : valuation,
					bid_note: bid_note
				};
				$.post(url, {
					bid : $.JSON.encode(bid)
				}).success(self.userAssignCallback(callback)).error(
						errorCallback);
			};
			self.considerBid = function(bid_id, callback, errorCallback) {
				var url = '/bid/activate/?id=' + bid_id;
				$.post(url).success(self.userAssignCallback(callback)).error(
						errorCallback);
			};
			self.acceptBid = function(bid_id, callback, errorCallback) {
				var url = '/bid/accept/?id=' + bid_id;
				$.post(url).success(self.userAssignCallback(callback)).error(
						errorCallback);
			};
			self.rejectBid = function(bid_id, callback, errorCallback) {
				var url = '/bid/reject/?id=' + bid_id;
				$.post(url).success(self.userAssignCallback(callback)).error(
						errorCallback);
			};
			self.withdrawBid = function(bid_id, callback, errorCallback) {
				var url = '/bid/withdraw/?id=' + bid_id;
				$.post(url).success(self.userAssignCallback(callback)).error(
						errorCallback);
			};
			self.bidStats = function(callback, errorCallback) {
				if (!errorCallback) {
					errorCallback = function() {
						$('#statsbox').hide();
					};
				}
				var url = '/bids/bid-day-valuation/';
				$.get(url).success(self.userAssignCallback(callback)).error(
						errorCallback);
			};
			return self;
}
