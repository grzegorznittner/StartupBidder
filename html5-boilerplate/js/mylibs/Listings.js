function Listings() {
			var self = {};
			self.saveListing = function(listing_id) {
				var listing = {};
				if (listing_id) {
					listing.listing_id = listing_id;
				}
				listing.title = $('#listing-edit-title').val();
				listing.median_valuation = $('#listing-edit-median-valuation')
						.val();
				listing.num_votes = $('#listing-edit-num-votes').val();
				listing.num_bids = $('#listing-edit-num-bids').val();
				listing.num_comments = $('#listing-edit-num-comments').val();
				listing.profile_id = $('#listing-edit-profile-id').val();
				listing.profile_username = $('#listing-edit-profile-username')
						.val();
				listing.listing_date = $('#listing-edit-listing-date').val();
				listing.status = $('#listing-edit-status').val();
				var raw_amt = $('#listing-edit-suggested-amt').val().replace(
						/[^\d]/g, '');
				var raw_pct = $('#listing-edit-suggested-pct').val().replace(
						/[^\d]/g, '');
				listing.suggested_amt = raw_amt;
				listing.suggested_pct = raw_pct;
				listing.suggested_val = self.imports.bids.calculateValuation(
						listing.suggested_amt, listing.suggested_pct);
				listing.summary = $('#listing-edit-summary').val();
				listing.business_plan_id = $('#listing-edit-business-plan-link')
						.data('business_plan_id');
				listing.presentation_id = $('#listing-edit-presentation-link')
						.data('presentation_id');
				listing.financials_id = $('#listing-edit-financials-link')
						.data('financials_id');
				var callback = function(savedListing) {
					var msg = savedListing ? 'listing saved'
							: 'unable to save listing';
					var msg_class = savedListing ? 'confirmed' : 'attention';
					$('#listing-save-message').attr('class', msg_class).html(
							msg);
					if (savedListing) {
						self.displayListing(savedListing);
					}
				};
				if (listing.status === 'new') {
					var errorCallback = function() {
						$('#listing-save-message').attr('class', 'attention')
								.html('unable to create listing');
					};
					self.imports.backend.createListing(listing, callback, errorCallback);
				} else {
					var errorCallback = function() {
						$('#listing-save-message').attr('class', 'attention')
								.html('unable to save listing');
					};
					self.imports.backend.saveListing(listing, callback, errorCallback);
				}
			};
			self.newListing = function(profile) {
				var profile = self.imports.user.profile;
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
				listing.listing_date = self.imports.util.today();
				listing.status = 'new';
				listing.suggested_amt = 0;
				listing.suggested_pct = 0;
				listing.suggested_val = self.imports.bids.calculateValuation(
						listing.suggested_amt, listing.suggested_pct);
				listing.summary = '';
				listing.business_plan_id = '';
				listing.presentation_id = '';
				listing.financials_id = '';
				self.displayListing(listing);
			};
			self.displayListingSnippetHtml = function(html) {
				$('#listing-snippet').html(html);
			};
			self.displayListing = function(data) {
				$('#listing-save-message').attr('class', 'attention').html('');
				var listing = data && data.listing ? data.listing : data;
				var profile = self.imports.user.profile;
				var isOwnListing = profile
						&& profile.profile_id == listing.profile_id;
				var isListingEditable = isOwnListing
						&& ((listing.status === 'new' || listing.status === 'created') || listing.status === 'active');

				// display listing snippet, exit if not found
				self.imports.util.displayPage('listing');
				var errorMsg = '';
				if (!listing) {
					errorMsg = '<em>no listings found</em>';
				} else if (!isOwnListing
						&& (listing.status === 'new' || listing.status === 'created')) {
					errorMsg = '<em>You must be logged to submit a new listing</em>';
				}
				if (errorMsg) {
					self.displayNullListing(errorMsg);
					self.imports.statistics.hideCharts();
					return;
				}
				if (listing.listing_id) {
					$('#listing-snippet').hide().html(
							self.generateListingSnippetHtml('listing',
									listing)).show();
					self.bindListingSnippet('listing', listing);
				}

				// display listing message
				var msg = self.listingMessage(listing.status, isOwnListing);
				var msgClass = listing.status === 'active' ? 'confirmed'
						: 'attention';
				$('#listing-form-message').attr('class', msgClass).html(msg);

				if (isListingEditable) { // enable editable if logged in
					self.displayEditableListing(listing);
				} else {
					self.displayReadonlyListing(listing);
				}
				self.displayListingChart(listing);
			};
			self.displayListingChart = function(listing) {
				if (!listing || listing.status !== 'active') {
					self.imports.statistics.hideCharts();
					return;
				}
				var stats = {
					label : listing.title,
					chartType : 'column',
					xaxis : '',
					yaxis : 'activity',
					labels : [ '#votes', '#bids', '#comments' ],
					values : [ listing.num_votes || 0, listing.num_bids || 0,
							listing.num_comments || 0 ]
				};
				self.imports.statistics.displayStats(stats);
			};
			self.listingMessage = function(listingStatus, isOwnListing) {
				var msg = '';
				if (isOwnListing && listingStatus === 'withdrawn') {
					msg = 'You may not change your listing now that it is withdrawn.';
				} else if (listingStatus === 'new') {
					msg = 'This listing has not yet been saved.';
				} else if (listingStatus === 'created') {
					msg = 'This listing has not yet been activated, until then no bids are accepted.';
				} else if (listingStatus === 'active') {
					msg = 'This listing is active and accepting bids.';
				} else if (listingStatus === 'withdrawn') {
					msg = 'This listing is withdrawn and thus bidding is suspended.';
				} else {
					msg = 'The listing status is unknown.';
				}
				return msg;
			};
			self.displayNullListing = function(message) {
				$(
						'#listing-form-editable,#listing-execsum-editable,#listing-form-readonly,#listing-execsum-readonly')
						.hide();
				$('#listing-snippet').html(message).show();
			};
			self.displayReadonlyListing = function(listing) {
				$('#listing-form-editable,#listing-execsum-editable').hide();
				$('#listing-form-readonly,#listing-execsum-readonly').show();
				$('#listing-readonly-suggested-amt').html(
						self.imports.util.addCommas(listing.suggested_amt));
				$('#listing-readonly-suggested-pct')
						.html(listing.suggested_pct);
				$('#listing-readonly-suggested-val').html(
						self.imports.util.addCommas(listing.suggested_val));
				$('#listing-readonly-summary').html(listing.summary);
				self.displayUploadField('listing-readonly-business-plan',
						'business_plan_id', listing.business_plan_id);
				self.displayUploadField('listing-readonly-presentation',
						'presentation_id', listing.presentation_id);
				self.displayUploadField('listing-readonly-financials',
						'financials_id', listing.financials_id);
			};
			self.displayEditableListing = function(listing) {
				$('#listing-form-readonly,#listing-execsum-readonly').hide();
				$('#listing-form-editable,#listing-execsum-editable').show();
				$('#listing-edit-title').val(listing.title);
				$('#listing-edit-median-valuation').val(
						listing.median_valuation);
				$('#listing-edit-num-votes').val(listing.num_votes);
				$('#listing-edit-num-bids').val(listing.num_bids);
				$('#listing-edit-num-comments').val(listing.num_comments);
				$('#listing-edit-profile-id').val(listing.profile_id);
				$('#listing-edit-profile-username').val(
						listing.profile_username);
				$('#listing-edit-listing-date').val(listing.listing_date);
				$('#listing-edit-status').val(listing.status);
				$('#listing-edit-suggested-amt').val(
						self.imports.util.addCommas(listing.suggested_amt));
				$('#listing-edit-suggested-pct').val(listing.suggested_pct);
				$('#listing-edit-suggested-val').html(
						self.imports.util.addCommas(listing.suggested_val));
				$('#listing-edit-summary').val(listing.summary);
				$('#listing-edit-suggested-amt, #listing-edit-suggested-pct')
						.unbind().bind('change', function(e) {
							self.recalculateSuggestedValuation();
						});

				if (listing.status === 'new') {
					$('#listing-save-link').unbind().bind('click', function(e) {
						e.preventDefault();
						self.saveListing();
					});
				} else {
					$('#listing-save-link').unbind().data('listing_id',
							listing.listing_id).bind('click', function(e) {
						e.preventDefault();
						self.saveListing($(this).data('listing_id'));
					});
				}

				self.displayUploadField('listing-edit-business-plan',
						'business_plan_id', listing.business_plan_id, true);
				self.displayUploadField('listing-edit-presentation',
						'presentation_id', listing.presentation_id, true);
				self.displayUploadField('listing-edit-financials',
						'financials_id', listing.financials_id, true);

				if (listing.status === 'new') {
					$('#listing-withdraw-link').hide();
					$('#listing-activate-link').hide();
				} else if (listing.status === 'created') {
					$('#listing-withdraw-link').hide();
					$('#listing-activate-link').show().unbind().data(
							'listing_id', listing.listing_id).bind(
							'click',
							function() {
								var errorCallback = function() {
									$('#listing-save-message').attr('class',
											'attention').html(
											'unable to activate listing');
								};
								self.imports.backend.activateListing($(this).data(
										'listing_id'), self.displayListing,
										errorCallback);
							});
				} else {
					$('#listing-withdraw-link').show().unbind().data(
							'listing_id', listing.listing_id).bind(
							'click',
							function() {
								var errorCallback = function() {
									$('#listing-save-message').attr('class',
											'attention').html(
											'unable to withdraw listing');
								};
								self.imports.backend.withdrawListing($(this).data(
										'listing_id'), self.displayListing,
										errorCallback);
							});
					$('#listing-activate-link').hide();
				}
			};
			self.displayUploadField = function(fieldRoot, fieldName,
					fieldValue, editable) {
				var fieldSelRoot = '#' + fieldRoot;
				var url;
				$(fieldSelRoot + '-form').hide();
				if (editable) {
					$(fieldSelRoot + '-add').unbind().bind('click',
							self.imports.uploader.prepareUploader(fieldRoot, fieldName));
				}
				if (fieldValue) {
					url = '/file/download/' + fieldValue;
					$(fieldSelRoot + '-link a').attr('href', url);
					$(fieldSelRoot + '-link').show();
				} else {
					$(fieldSelRoot + '-link').hide();
					$(fieldSelRoot + '-remove').hide();
				}
				if (fieldValue && editable) {
					$(fieldSelRoot + '-link').data(fieldName, fieldValue);
					$(fieldSelRoot + '-remove').unbind().bind('click',
							function() {
								alert('not implemented yet');
							}).show();
				} else {
					$(fieldSelRoot + '-remove').hide();
				}
			};
			self.recalculateSuggestedValuation = function() {
				var suggested_amt = $('#listing-edit-suggested-amt').val()
						|| '';
				var suggested_pct = $('#listing-edit-suggested-pct').val()
						|| '';
				suggested_amt = suggested_amt.replace(/[^\d]/g, '');
				suggested_pct = suggested_pct.replace(/[^\d]/g, '');
				var valuation = self.imports.bids.calculateValuation(suggested_amt,
						suggested_pct, true);
				var validationMsg = self.imports.bids.validateBid(suggested_amt, 'common',
						0, suggested_pct, valuation);
				$('#listing-save-message').attr('class', 'attention').html(
						validationMsg);
				var displayVal = valuation ? self.imports.util.addCommas(valuation) : '';
				$('#listing-edit-suggested-val').html(displayVal);
			};
			self.showListingProfile = function(profile_id, index) {
				var errorCallback = function() {
					$('#content-results-list-item-message-' + index).attr(
							'class', 'attention').html('unable to get profile');
				};
				self.imports.profiles.showProfile(profile_id, errorCallback);
			};
			self.showListing = function(listing_id, index) {
				var errorCallback = function() {
					$('#content-results-list-item-message-' + index).attr(
							'class', 'attention').html('unable to get listing');
				};
				self.imports.backend.getListing(listing_id, self.displayListing,
						errorCallback);
			};
			self.showCommentsForListing = function(listing_id, index) {
				var errorCallback = function() {
					$('#content-results-list-item-message-' + index).attr(
							'class', 'attention')
							.html('unable to get comments');
				};
				self.imports.backend.commentsForListing(listing_id, self.imports.comments.displayResults,
						errorCallback);
			};
			self.showBidsForListing = function(listing_id, index) {
				var errorCallback = function() {
					$('#content-results-list-item-message-' + index).attr(
							'class', 'attention').html('unable to get bids');
				};
				self.imports.backend.bidsForListing(listing_id, self.imports.bids.displayResults,
						errorCallback);
			};
			self.bindListingSnippet = function(i, listing) {
				var upvote_item = document
						.getElementById('content-results-list-item-upvote-' + i);
				$('#content-results-list-item-profile-' + i).data({
					index : i,
					profile_id : listing.profile_id
				}).unbind().bind(
						'click',
						function() {
							self.imports.util.hashedCall('listingObj',
									'showListingProfile', [
											$(this).data('profile_id'),
											$(this).data('index') ]);
						});
				if (listing.listing_id) {
					$('#content-results-list-item-title-' + i).data({
						index : i,
						listing_id : listing.listing_id
					}).unbind().bind(
							'click',
							function() {
								self.imports.util.hashedCall('listingObj', 'showListing',
										[ $(this).data('listing_id'),
												$(this).data('index') ]);
							});
					$('#content-results-list-item-comments-' + i).data({
						index : i,
						listing_id : listing.listing_id
					}).unbind().bind(
							'click',
							function() {
								self.imports.util.hashedCall('listingObj',
										'showCommentsForListing', [
												$(this).data('listing_id'),
												$(this).data('index') ]);
							});
					$('#content-results-list-item-bids-' + i).data({
						index : i,
						listing_id : listing.listing_id
					}).unbind().bind(
							'click',
							function() {
								self.imports.util.hashedCall('listingObj',
										'showBidsForListing', [
												$(this).data('listing_id'),
												$(this).data('index') ]);
							});
				}
				$(upvote_item).data({
					index : i,
					listing_id : listing.listing_id,
					num_votes : listing.num_votes
				});
				if (!listing.votable) {
					$(upvote_item).css('visibility', 'hidden').unbind();
				} else {
					$(upvote_item)
							.css('visibility', 'visible')
							.unbind()
							.bind(
									'click',
									function() {
										var index = $(this).data('index');
										var listing_id = $(this).data(
												'listing_id');
										var num_votes = $(this).data(
												'num_votes');
										var list_item = this;
										var votes_item = document
												.getElementById('content-results-list-item-votes-'
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
													'#content-results-list-item-message-'
															+ $(this).data(
																	'index'))
													.attr('class', 'attention')
													.html(
															'unable to upvote listing');
										};
										self.imports.backend.upvoteListing(listing_id,
												callback, errorCallback);
									});
				}
			};
			self.generateListingSnippetHtml = function(i, listing) {
				var days_ago = listing.days_ago;
				var resultHtml = '';
				var evenOdd;
				var fileUrl;
				if (isFinite(i)) {
					evenOdd = (i % 2 == 0) ? 'even' : 'odd';
					resultHtml += '<div class="content-results-list-item content-results-list-item-'
							+ evenOdd + '">';
					resultHtml += '<div class="content-results-list-item-num">'
							+ listing.num + '</div>';
				} else {
					resultHtml += '<div class="content-results-list-item">';
					resultHtml += '<div class="content-results-list-item-num"></div>';
				}
				if (self.imports.user.profile
						&& !(self.imports.user.profile.profile_id === listing.profile_id)) {
					resultHtml += '<div class="content-results-list-item-upvote" id="content-results-list-item-upvote-'
							+ i + '"></div>';
				} else {
					resultHtml += '<div class="content-results-list-item-upvote-filler"></div>';
				}
				resultHtml += '<div class="content-results-list-item-title link" id="content-results-list-item-title-'
						+ i + '">';
				resultHtml += listing.title;
				resultHtml += '</div>';
				resultHtml += '<div class="content-results-list-item-profile link" id="content-results-list-item-profile-'
						+ i + '">';
				resultHtml += '(' + listing.profile_username + ')';
				resultHtml += ' <span class="attention" id="content-results-list-item-message-'
						+ i + '"></span>';
				resultHtml += '</div>';
				if (listing.business_plan_id) {
					fileUrl = "/file/download/" + listing.business_plan_id;
					resultHtml += '<a target="_blank" title="business plan" href="'
							+ fileUrl
							+ '" class="listing-icon business-plan-icon" id="content-results-list-item-business-plan-'
							+ i + '"></a>';
				}
				if (listing.presentation_id) {
					fileUrl = "/file/download/" + listing.presentation_id;
					resultHtml += '<a target="_blank" title="presentation" href="'
							+ fileUrl
							+ '" class="listing-icon presentation-icon" id="content-results-list-item-presentation-'
							+ i + '"></a>';
				}
				if (listing.financials_id) {
					fileUrl = "/file/download/" + listing.financials_id;
					resultHtml += '<a target="_blank" title="financials" href="'
							+ fileUrl
							+ '" class="listing-icon financials-icon" id="content-results-list-item-financials-'
							+ i + '"></a>';
				}
				resultHtml += '<div class="content-results-list-item-clear"></div>';
				resultHtml += '<div class="content-results-list-item-detail">';
				resultHtml += '<span id="content-results-list-item-votes-' + i
						+ '">' + listing.num_votes + '</span>' + ' votes | ';
				resultHtml += (days_ago == 0) ? 'today' : days_ago + ' day'
						+ (days_ago !== 1 ? 's' : '') + ' ago';
				resultHtml += ' | ' + listing.status + ' | ';
				resultHtml += '<span class="link" id="content-results-list-item-comments-'
						+ i + '">';
				resultHtml += listing.num_comments + ' comment'
						+ (listing.num_comments == 1 ? '' : 's');
				resultHtml += '</span> | ';
				resultHtml += '$' + self.imports.util.addCommas(listing.median_valuation)
						+ ' after ';
				resultHtml += '<span class="link" id="content-results-list-item-bids-'
						+ i + '">';
				resultHtml += listing.num_bids + ' bid'
						+ (listing.num_bids == 1 ? '' : 's');
				resultHtml += '</span> | ';
				resultHtml += 'listed ' + self.imports.util.formatDate(listing.listing_date);
				resultHtml += '</div>';
				resultHtml += '</div>';
				resultHtml += '<div class="content-results-list-item-clear"></div>';
				return resultHtml;
			};
			return self;
}
