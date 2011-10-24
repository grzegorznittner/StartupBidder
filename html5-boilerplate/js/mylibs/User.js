function User() {
			var self = {};
			self.profile = null;
			self.login_url = '/_ah/login?continue=%2F';
			self.logout_url = '/_ah/logout?continue=%2F';
			self.setProfile = function(profile) {
				self.profile = profile;
				self.imports.header.displayUser(self);
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
				var days_ago = userjson.days_ago !== undefined ? userjson.days_ago
						: 0;
				var resultHtml = '';
				var evenOdd;
				if (isFinite(i)) {
					evenOdd = (i % 2 == 0) ? 'even' : 'odd';
					resultHtml += '<div class="content-results-list-item content-results-list-item-'
							+ evenOdd + '">';
					resultHtml += '<div class="content-results-list-item-num">'
							+ userjson.num + '</div>';
				} else {
					resultHtml += '<div class="content-results-list-item">';
					resultHtml += '<div class="content-results-list-item-num"></div>';
				}
				if (self.profile
						&& (self.profile.profile_id !== userjson.profile_id)) {
					resultHtml += '<div class="content-results-list-item-upvote" id="user-search-results-list-item-upvote-'
							+ i + '"></div>';
				} else {
					resultHtml += '<div class="content-results-list-item-upvote-filler"></div>';
				}
				resultHtml += '<div class="content-results-list-item-title link" id="user-search-results-list-item-name-'
						+ i + '">';
				resultHtml += userjson.name
						+ (userjson.organization ? ' of '
								+ userjson.organization : '');
				resultHtml += '</div>';
				resultHtml += '<div class="content-results-list-item-profile link" id="user-search-results-list-item-profile-'
						+ i + '">';
				resultHtml += '(' + userjson.username + ')';
				resultHtml += userjson.status === 'active' ? ''
						: ' <span class="attention">' + userjson.status
								+ '</span>';
				resultHtml += ' <span class="attention" id="user-search-results-list-item-message-'
						+ i + '"></span>';
				resultHtml += '</div>';
				resultHtml += '<div class="content-results-list-item-clear"></div>';
				resultHtml += '<div class="content-results-list-item-detail">';
				resultHtml += '<span id="user-search-results-list-item-votes-'
						+ i
						+ '">'
						+ (userjson.num_votes !== undefined ? userjson.num_votes
								: 0) + '</span>' + ' votes | ';
				resultHtml += '<span class="link" id="user-search-results-list-item-listings-'
						+ i
						+ '">'
						+ userjson.num_listings
						+ ' listing'
						+ (userjson.num_listings !== 1 ? 's' : '')
						+ '</span> | ';
				resultHtml += '<span class="link" id="user-search-results-list-item-bids-'
						+ i
						+ '">'
						+ userjson.num_bids
						+ ' bid'
						+ (userjson.num_bids !== 1 ? 's' : '') + '</span> | ';
				resultHtml += '<span class="link" id="user-search-results-list-item-comments-'
						+ i
						+ '">'
						+ userjson.num_comments
						+ ' comment'
						+ (userjson.num_comments !== 1 ? 's' : '')
						+ '</span> | ';
				resultHtml += (days_ago == 0) ? 'active today' : 'active '
						+ days_ago + ' day' + (days_ago !== 1 ? 's' : '')
						+ ' ago';
				resultHtml += ' | ';
				resultHtml += 'joined ' + self.imports.util.formatDate(userjson.joined_date);
				resultHtml += userjson.investor ? ' | <b>investor</b>' : '';
				resultHtml += '</div>';
				resultHtml += '</div>';
				resultHtml += '<div class="content-results-list-item-clear"></div>';
				return resultHtml;
			};
			self.showUserProfile = function(profile_id, index) {
				var errorCallback = function() {
					$('#user-search-results-list-item-message-' + index).html(
							'unable to retrieve profile');
				};
				self.imports.profiles.showProfile(profile_id, errorCallback);
			};
			self.showListingsForProfile = function(profile_id, index) {
				var errorCallback = function() {
					$('#user-search-results-list-item-message-' + index).html(
							'unable to retrieve listings');
				};
				self.imports.backend.listingsForProfile(profile_id,
						self.imports.searchListings.displayResultsJson, errorCallback);
			};
			self.showCommentsForProfile = function(profile_id, index) {
				var errorCallback = function() {
					$('#user-search-results-list-item-message-' + index).html(
							'unable to retrieve comments');
				};
				self.imports.backend.commentsForProfile(profile_id, self.imports.comments.displayResults,
						errorCallback);
			};
			self.showBidsForProfile = function(profile_id, index) {
				var errorCallback = function() {
					$('#user-search-results-list-item-message-' + index).html(
							'unable to retrieve bids');
				};
				self.imports.backend.bidsForProfile(profile_id, self.imports.bids.displayResults,
						errorCallback);
			};
			self.showAcceptedBidsForProfile = function(profile_id, index) {
				var errorCallback = function() {
					$('#user-search-results-list-item-message-' + index).html(
							'unable to retrieve bids');
				};
				self.imports.backend.bidsForProfile(profile_id, self.imports.bids.displayAcceptedResults,
						errorCallback);
			};
			self.showPaidBidsForProfile = function(profile_id, index) {
				var errorCallback = function() {
					$('#user-search-results-list-item-message-' + index).html(
							'unable to retrieve bids');
				};
				self.imports.backend.bidsForProfile(profile_id, self.imports.bids.displayPaidResults,
						errorCallback);
			};
			self.bindUserSnippet = function(i, userjson) {
				var upvote_item = document
						.getElementById('user-search-results-list-item-upvote-'
								+ i);
				$('#user-search-results-list-item-name-' + i
								+ ',#user-search-results-list-item-profile-'
								+ i).data({
					index : i,
					profile_id : userjson.profile_id
				}).unbind().bind(
						'click',
						function() {
							self.imports.util.hashedCall('user', 'showUserProfile', [
									$(this).data('profile_id'),
									$(this).data('index') ]);
						});
				$('#user-search-results-list-item-listings-' + i).data({
					index : i,
					profile_id : userjson.profile_id
				}).unbind().bind(
						'click',
						function() {
							self.imports.util.hashedCall('user', 'showListingsForProfile', [
									$(this).data('profile_id'),
									$(this).data('index') ]);
						});
				$('#user-search-results-list-item-comments-' + i).data({
					index : i,
					profile_id : userjson.profile_id
				}).unbind().bind(
						'click',
						function() {
							self.imports.util.hashedCall('user', 'showCommentsForProfile', [
									$(this).data('profile_id'),
									$(this).data('index') ]);
						});
				$('#user-search-results-list-item-bids-' + i).data({
					index : i,
					profile_id : userjson.profile_id
				}).unbind().bind(
						'click',
						function() {
							self.imports.util.hashedCall('user', 'showBidsForProfile', [
									$(this).data('profile_id'),
									$(this).data('index') ]);
						});
				$(upvote_item)
						.data(
								{
									index : i,
									profile_id : userjson.profile_id,
									num_votes : (userjson.num_votes !== undefined ? userjson.num_votes
											: 0)
								});
				if (!userjson.votable) {
					$(upvote_item).css('visibility', 'hidden').unbind();
					return;
				}
				$(upvote_item)
						.css('visibility', 'visible')
						.unbind()
						.bind(
								'click',
								function() {
									var index = $(this).data('index');
									var profile_id = $(this).data('profile_id');
									var num_votes = $(this).data('num_votes');
									var list_item = this;
									var votes_item = document
											.getElementById('user-search-results-list-item-votes-'
													+ index);
									var callback = function(json) {
										if (json) {
											var new_votes = num_votes + 1;
											$(votes_item).html(new_votes);
											$(list_item).data('num_votes',
													new_votes).css(
													'visibility', 'hidden')
													.unbind(); // don't let them click twice
										}
									};
									var errorCallback = function() {
										$(
												'#user-search-results-list-item-message-'
														+ i).html(
												'unable to upvote user');
									};
									self.imports.backend.upvoteUser(profile_id, callback, errorCallback);
								});
			};
			return self;
}
