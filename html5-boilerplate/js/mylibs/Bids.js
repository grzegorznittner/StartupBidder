function Bids() {
			var self = {};
			self.saveBid = function(listing_id, profile_id, profile_username) {
				var raw_amt = $('#bid-amt').val() || '';
				var raw_pct = $('#bid-pct').val() || '';
				var raw_type = $('#bid-type').val() || '';
				var raw_rate = $('#bid-rate').val() || '';

				raw_amt = raw_amt.replace(/[^\d]+/g, '');
				raw_pct = raw_pct.replace(/[^\d]+/g, '');
				raw_rate = raw_rate.replace(/[^\d]+/g, '');

				var bid_amt = raw_amt != '' ? raw_amt : 0;
				var bid_type = ((raw_type === 'common' || raw_type === 'preferred') || raw_type === 'note') ? raw_type
						: 'common';
				var bid_rate = raw_rate != '' ? raw_rate : 0;
				var bid_pct = raw_pct != '' ? raw_pct : 0;

				var valuation = self.calculateValuation(bid_amt, bid_pct, true);

				var validationMsg = self.validateBid(bid_amt, bid_type,
						bid_rate, bid_pct, valuation);
				if (validationMsg !== '') {
					$('#bid-save-message').attr('class', 'attention').html(
							validationMsg);
					return;
				}

				var callback = function(bid) {
					var callback2 = function(results) {
						self.displayResults(results, false, 'confirmed',
								'bid saved');
					};
					var errorCallback2 = function() {
						$('#bids-message').attr('class', 'attention').html(
								'unable to get bids');
						self.imports.util.displayPage('bids');
					};
					if (bid) {
						self.imports.backend.bidsForListing(bid.listing_id, callback2,
								errorCallback2);
					} else {
						$('#bid-save-message').attr('class', 'attention').html(
								'unable to save bid');
					}
				};
				var errorCallback = function() {
					$('#bid-save-message').attr('class', 'attention').html(
							'unable to add bid');
				};
				self.imports.backend.addBid(listing_id, profile_id, profile_username,
						bid_amt, bid_pct, bid_type, bid_rate, valuation,
						callback, errorCallback);
			};
			self.validateBid = function(bid_amt, bid_type, bid_rate, bid_pct,
					valuation) {
				var msg;
				if (bid_amt < 1000) {
					msg = 'your bid must be at least $1,000';
				} else if (bid_amt > 1000000) {
					msg = 'your bid must be no more than $1,000,000';
				} else if (bid_pct < 1) {
					msg = 'your bid percentage must be at least 1%';
				} else if (bid_pct > 50) {
					msg = 'your bid percentage must be no more than 50%';
				} else if (bid_type != 'common' && bid_rate < 1) {
					msg = 'your bid preferred or note rate must be at least 1%';
				} else if (bid_type != 'common' && bid_rate > 20) {
					msg = 'your bid interest rate must be no more than 20%';
				} else if (valuation < 10000) {
					msg = 'your calculated valuation must be at least $10,000';
				} else {
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
				self.imports.util.displayPage('bids');
				$('#bids-form-loggedin').hide();
				$('#bids-form-not-loggedin').hide();
				$('#bids-listing-snippet').html('');
				$('#bids-content-results-list').html(resultsHtml);
			};
			self.displayAcceptedResults = function(results, keepBid, msgClass,
					msgText) {
				self.displayResults(results, keepBid, msgClass, msgText,
						'accepted');
			};
			self.displayPaidResults = function(results, keepBid, msgClass,
					msgText) {
				self
						.displayResults(results, keepBid, msgClass, msgText,
								'paid');
			};
			self.displayResults = function(results, keepBid, msgClass, msgText,
					bidStatus) {
				var bids_profile = results ? results.profile : null;
				var listing = results ? results.listing : null;
				var bids = results ? results.bids : null;
				var profile = self.imports.user.profile;
				var filteredBids = self.filterBidsByStatus(bids, bidStatus);
				var profileBids = self.filterBidsWithProfile(filteredBids,
						profile);
				var profileBid = profileBids && profileBids.length ? profileBids[0]
						: undefined;

				$('#bids-message').html('');
				$('#bid-save-message').attr('class', (msgClass || 'attention'))
						.html(msgText || '');
				self.recalculateValuation();
				if (!keepBid) {
					self.clearBidFields();
				}
				self.imports.util.displayPage('bids');

				self.displayBidSnippet(bids_profile);
				self.displayBidForm(listing, profile, profileBid);
				self.displayBids(filteredBids, listing, profile);
				self.displayBidStats(filteredBids);
				self.bindBidLinks(filteredBids, profile, listing, bidStatus);
			};
			self.changeBidFunc = function(index, actionType) {
				var changeBid = function() {
					var index = $(this).data('index');
					var profile_id = $(this).data('profile_id');
					var listing_id = $(this).data('listing_id');
					var errorCallback = function() {
						$('#bids-item-message-' + index).attr('class',
								'attention').html('unable to '+actionType+' bid');
					};
					var errorCallback2 = function() {
						$('#bids-message').attr('class', 'attention').html(
								'unable to get bids');
						self.imports.util.displayPage('bids');
					};
					var profile_callback = function() {
						self.imports.backend.bidsForProfile(profile_id, self.displayResults,
								errorCallback2);
					};
					var listings_callback = function() {
						self.imports.backend.bidsForListing(listing_id, self.displayResults,
								errorCallback2);
					};
					var callback = listing_id ? listings_callback
							: profile_callback;
					var actionFuncMap = {
						consider: self.imports.backend.considerBid,
						accept: self.imports.backend.acceptBid,
						reject: self.imports.backend.rejectBid,
						withdraw: self.imports.backend.withdrawBid
					};
					actionFuncMap[actionType]($(this).data('bid_id'), callback,
							errorCallback);
				};
				return changeBid;
			};
			self.displayBidSnippet = function(bids_profile) {
				if (bids_profile) {
					$('#bids-profile-snippet').html(
							self.imports.user.generateUserSnippetHtml('bids', bids_profile));
					self.imports.user.bindUserSnippet('bids', bids_profile);
					$('#bids-profile-header').show();
				} else {
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
					$('#bids-listing-snippet').html(
							self.imports.listingObj.generateListingSnippetHtml('bids',
									listing));
					self.imports.listingObj.bindListingSnippet('bids', listing);
					$('#bids-listing-header').show();
				}
				if (listing.status != 'active') {
					$('#bids-form-loggedin').hide();
					$('#bids-form-not-loggedin').show().html(
							'This bid is no longer active');
				} else if (profile && profile.profile_id == listing.profile_id) {
					$('#bids-form-loggedin').hide();
					$('#bids-form-not-loggedin').show().html(
							'You may not bid on your own listing');
				} else if (profile && profile.investor) {
					if (profileBid) { /* prefill form with previous bid */
						$('#bid-form-new').hide();
						$('#bid-form-update-date').html(
								self.imports.util.formatDate(profileBid.bid_date));
						$('#bid-form-update').show();
						$('#bid-amt').val(self.imports.util.addCommas(profileBid.amount));
						$('#bid-pct').val(profileBid.equity_pct);
						$('#bid-type').val(profileBid.bid_type);
						if ($('#bid-type').val() == 'common') {
							$('#bid-rate').attr('disabled', true);
							$('#bid-rate').val('');
						} else {
							$('#bid-rate').removeAttr('disabled');
							$('#bid-rate').val(profileBid.interest_rate);
						}
						$('#bid-calculated-valuation').html(
								self.imports.util.addCommas(profileBid.valuation));
					} else { /* new bid, default to suggested numbers */
						self.clearBidFields();
						$('#bid-amt').val(self.imports.util.addCommas(listing.suggested_amt));
						$('#bid-pct').val(listing.suggested_pct);
						$('#bid-form-update').hide();
						$('#bid-form-new').show();
					}
					$('#bids-form-loggedin').show();
					$('#bids-form-not-loggedin').hide();
					$('#bid-save-button').show().unbind().data('listing_id',
							listing.listing_id).data('profile_id',
							profile.profile_id).data('profile_username',
							profile.username).bind(
							'click',
							function(e) {
								e.preventDefault();
								self.saveBid($(this).data('listing_id'),
										$(this).data('profile_id'), $(this)
												.data('profile_username'));
							});
					$('#bid-amt,#bid-pct,#bid-type,#bid-rate').unbind().bind(
							'change', function(e) {
								self.recalculateValuation();
							});
					$('#bid-type').unbind().bind('change', function(e) {
						if ($('#bid-type').val() == 'common')
							$('#bid-rate').attr('disabled', true);
						else
							$('#bid-rate').removeAttr('disabled');
					});
				} else if (profile && !profile.investor) {
					$('#bids-form-loggedin').hide();
					$('#bids-form-not-loggedin').show().html(
							'You must be an investor to place a bid');
				} else {
					$('#bids-form-loggedin').hide();
					$('#bids-form-not-loggedin').show().html(
							'You must be a logged in investor to place a bid');
				}
			};
			self.filterBidsWithProfile = function(bids, profile) {
				var bid;
				var filteredBids = [];
				if (bids && bids.length > 0 && profile) {
					for ( var i = 0; i < bids.length; i++) { // create results
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
					for ( var i = 0; i < bids.length; i++) { // create results
						bid = bids[i];
						if (!bid) {
							continue;
						}
						if (bid.status !== bidStatus) {
							continue;
						}
						filteredBids.push(bid);
					}
				} else {
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
				} else {
					for ( var i = 0; i < bids.length; i++) { // create results
						bid = bids[i];
						if (bid) {
							resultsHtml += self.generateResultHtml(i, bid,
									listing, profile);
						}
					}
				}
				$('#bids-content-results-list').hide().html(resultsHtml).show();
			};
			self.bindBidLinks = function(bids, profile, listing) {
				if (!bids || bids.length == 0) {
					return;
				}
				for ( var i = 0; i < bids.length; i++) {
					bid = bids[i];
					$('#bids-item-profile-' + i).data({
						index : i,
						profile_id : bid.profile_id
					}).unbind().bind(
							'click',
							function() {
								self.imports.util.hashedCall('bids', 'showBidProfile', [
										$(this).data('profile_id'),
										$(this).data('index') ]);
							});
					$('#bids-item-listing-' + i).data({
						index : i,
						listing_id : bid.listing_id
					}).unbind().bind(
							'click',
							function() {
								self.imports.util.hashedCall('bids', 'showBidListing', [
										$(this).data('listing_id'),
										$(this).data('index') ]);
							});
					var boundData = {
						index: i,
						bid_id: bid.bid_id,
						profile_id: listing ? '' : bid.profile_id,
						listing_id: listing ? listing.listing_id : ''
					};
					$('#bids-item-delete-' + i).unbind().data(boundData).bind('click', self.changeBidFunc('withdraw'));
					$('#bids-item-consider-' + i).data(boundData)
							.unbind().bind('click', self.changeBidFunc(i, 'consider'));
					$('#bids-item-accept-' + i).data(boundData)
							.unbind().bind('click', self.changeBidFunc(i, 'accept'));
					$('#bids-item-reject-' + i).data(boundData)
							.unbind().bind('click', self.changeBidFunc(i, 'reject'));
				}
			};
			self.showBidProfile = function(profile_id, index) {
				var errorCallback = function() {
					$('#bids-item-message-' + index).attr('class', 'attention')
							.html('unable to get profile');
				};
				self.imports.profiles.showProfile(profile_id, errorCallback);
			};
			self.showBidListing = function(listing_id, index) {
				var errorCallback = function() {
					$('#bids-item-message-' + index).attr('class', 'attention')
							.html('unable to get listing');
				};
				self.imports.backend.getListing(listing_id, self.imports.listingObj.displayListing,
						errorCallback);
			};
			self.formatBidType = function(bid_type) {
				var visualMapping = {
					common : 'common stock',
					preferred : 'preferred stock',
					note : 'a convertible note',
					syndicate : 'common stock as a syndicate',
					sole_investor : 'common stock as a sole investor'
				};
				var bidType = bid_type in visualMapping ? visualMapping[bid_type]
						: bid_type;
				return bidType;
			};
			self.generateResultHtml = function(i, bid, listing, profile) {
				var resultHtml = '';
				var evenOdd;
				if (isFinite(i)) {
					evenOdd = (i % 2 == 0) ? 'even' : 'odd';
					resultHtml += '<div class="content-results-list-item content-results-list-item-'
							+ evenOdd + '">';
				} else {
					resultHtml += '<div class="content-results-list-item">';
				}
				resultHtml += '<div class="content-results-list-item-num">'
						+ bid.num + '</div>';
				resultHtml += '<div class="content-results-list-item-title">';
				resultHtml += 'Valued at ';
				resultHtml += '$' + self.imports.util.addCommas(bid.valuation);
				resultHtml += ' with a bid of ';
				resultHtml += '$' + self.imports.util.addCommas(bid.amount);
				resultHtml += ' for ' + bid.equity_pct + '%';
				resultHtml += ' with ' + self.formatBidType(bid.bid_type);
				resultHtml += bid.interest_rate ? ' @ ' + bid.interest_rate
						+ '%' : '';
				resultHtml += '</div>';
				if (profile && profile.profile_id === bid.profile_id
						&& (bid.status === 'posted' || bid.status === 'active')) { // user can withdraw their own non-accepted bid
					resultHtml += '<div class="content-results-list-item-profile link" id="bids-item-delete-'
							+ i + '">(withdraw bid)</div>';
				}
				// user can accept or reject a bid for their own listing
				if (listing
						&& profile
						&& (profile.profile_id === listing.profile_id || profile.profile_id === bid.listing_profile_id)
						&& (bid.status === 'posted' || bid.status === 'active')) {
					if (bid.status === 'posted') { // posted bids can be considered
						resultHtml += '<div class="content-results-list-item-profile link" id="bids-item-consider-'
							+ i + '">(consider bid)</div>';
					}
					if (bid.status === 'posted' || bid.status === 'active') { // posted and considering bids can be accepted
						resultHtml += '<div class="content-results-list-item-profile link" id="bids-item-accept-'
							+ i + '">(accept bid)</div>';
					}
					// both kinds of bids can be rejected
					resultHtml += '<div class="content-results-list-item-profile link" id="bids-item-reject-'
						+ i + '">(reject bid)</div>';
				}
				resultHtml += '<div class="content-results-list-item-profile">';
				resultHtml += ' <span id="bids-item-message-' + i
						+ '" class="attention"></span>';
				resultHtml += '</div>';
				resultHtml += '<div class="content-results-list-item-clear"></div>';
				resultHtml += '<div class="content-results-list-item-detail">';
				resultHtml += 'by ';
				resultHtml += '<span class="link" id="bids-item-profile-' + i
						+ '">';
				resultHtml += bid.profile_username;
				resultHtml += '</span>';
				resultHtml += ' on ';
				resultHtml += self.imports.util.formatDate(bid.bid_date);
				resultHtml += listing ? ''
						: ' for <span class="link" id="bids-item-listing-' + i
								+ '">' + bid.listing_title + '</span>';
				resultHtml += ' <b>' + (bid.status === 'active' ? 'considering bid' : 'bid '+bid.status) + '</b>';
				resultHtml += '</div>';
				resultHtml += '</div>';
				resultHtml += '<div class="content-results-list-item-clear"></div>';
				return resultHtml;
			};
			self.displayBidStats = function(bids) {
				var bid, i;
				var stats = {
					label : 'Bid Activity',
					xaxis : 'time',
					yaxis : 'valuation',
					prefix : '$',
					suffix : 'k',
					values : []
				};
				for (i = 0; i < bids.length; i++) {
					bid = bids[i];
					stats.values[i] = bid.valuation / 1000;
				}
				stats.values.reverse();
				self.imports.statistics.displayStats(stats);
			};
			self.recalculateValuation = function() {
				var bid_amt = $('#bid-amt').val() || '';
				var bid_pct = $('#bid-pct').val() || '';
				var bid_type = $('#bid-type').val() || '';
				var bid_rate = $('#bid-rate').val() || '';
				bid_amt = bid_amt.replace(/[^\d]/g, '');
				bid_pct = bid_pct.replace(/[^\d]/g, '');
				bid_rate = bid_rate.replace(/[^\d]/g, '');
				var valuation = self.calculateValuation(bid_amt, bid_pct, true);
				var validationMsg = self.validateBid(bid_amt, bid_type,
						bid_rate, bid_pct, valuation);
				$('#bid-save-message').attr('class', 'attention').html(
						validationMsg);
				var displayVal = valuation ? self.imports.util.addCommas(valuation) : '';
				$('#bid-calculated-valuation').html(displayVal);
			};
			self.calculateValuation = function(amount, equity_pct, thousands) {
				var valuation;
				if (amount >= 1000 && equity_pct >= 1) {
					if (thousands)
						valuation = Math.floor(1000 * Math
								.floor((100 * amount / equity_pct) / 1000));
					else
						valuation = Math.floor(100 * amount / equity_pct);
				} else {
					valuation = 0;
				}
				return valuation;
			};
			return self;
}
