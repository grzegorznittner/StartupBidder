function SearchListings() {
			var self = {};
			self.defaultSearchType = 'top';
			self.bindLinks = function() {
				$('#content-search-submit').unbind().bind('click', function() {
					self.imports.util.hashedCall('searchListings', 'doKeywordSearch');
				});
				$('#content-search-keywords').unbind().bind(
						'keydown',
						function(e) {
							if (e.keyCode == 13) { // return key
								self.imports.util.hashedCall('searchListings',
										'doKeywordSearch');
							}
						});
			};
			self.doTopSearch = function() {
				self.doSearch({
					search_type : 'top'
				});
			};
			self.doLatestSearch = function() {
				self.doSearch({
					search_type : 'latest'
				});
			};
			self.doValuationSearch = function() {
				self.doSearch({
					search_type : 'valuation'
				});
			};
			self.doActiveSearch = function() {
				self.doSearch({
					search_type : 'active'
				});
			};
			self.doPopularSearch = function() {
				self.doSearch({
					search_type : 'popular'
				});
			};
			self.doDiscussedSearch = function() {
				self.doSearch({
					search_type : 'discussed'
				});
			};
			self.doKeywordSearch = function() {
				self.doSearch({
					search_type : 'keyword'
				});
			};
			self.doSearch = function(args) {
				args = args || {};
				args.search_type = args.search_type || self.defaultSearchType;
				args.start_index = args.start_index || 0;
				args.max_results = args.max_results || 20;
				var keywords = $('#content-search-keywords').val();
				if (args.search_type === 'keyword' && keywords && keywords.length) {
					keywords = keywords.replace(/\btype:\w*/g, '').replace(
							/\s+/g, ' ').trim();
				}
				else { // null out value so it's clear we're not doing a keyword search
					keywords = null;
				}
				$('#content-search-keywords').val(keywords);
				if (args.profile_id) {
					self.imports.statistics.hideCharts();
				} else {
					self.imports.backend.bidStats(self.displayTopStats);
				}
				var searchErrorCallback = function() {
					$('#search-message').html(
							'unable to retrieve search results');
				};
				self.imports.backend.searchListings(args.search_type, keywords,
						args.profile_id, args.start_index, args.max_results,
						self.displayResultsJson, searchErrorCallback);
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
				self.imports.statistics.displayStats(stats);
			};
			self.displayResultsJson = function(searchResults) {
				if (!searchResults || !searchResults.listings_props) {
					return;
				}
				var searchStats = searchResults.listings_props;
				var profile = searchResults.profile;
				var profileId = profile ? profile.profile_id : null;
				self.displayResults(searchResults.listings,
						searchResults.search_type, searchResults.keywords,
						profileId, searchResults.start_index,
						searchStats.num_results, searchStats.total_results,
						profile);
			};
			self.displayResultsHtml = function(resultsHtml) {
				self.imports.util.displayPage('searchlistings');
				$('#search-content-results-list').hide().html(resultsHtml)
						.show();
			};
			self.displayResults = function(listings, search_type, keywords,
					profile_id, start_index, num_results, total_results,
					profile) {
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
					$('#searchlistings-profile-snippet').html(
							self.imports.user.generateUserSnippetHtml('search', profile));
					self.imports.user.bindUserSnippet('search', profile);
					$('#searchlistings-profile-header').show();
				} else {
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
						resultsHtml += self.imports.listingObj.generateListingSnippetHtml(
								i, listing);
					}
				} else {
					resultsHtml = '<i>no listings found</i>';
				}
				self.displayResultsHtml(resultsHtml);
			};
			self.bindListings = function(listings) {
				var i, listing;
				if (listings && listings.length) {
					for (i = 0; i < listings.length; i++) {
						listing = listings[i];
						self.imports.listingObj.bindListingSnippet(i, listing);
					}
				}
			};
			self.displayListingStats = function(listings) {
				if (!listings || !listings.length) {
					self.imports.statistics.hideCharts();
					return;
				}
				var listing, i;
				var stats = {
					label : 'Listings History',
					xaxis : 'time',
					yaxis : 'valuation',
					prefix : '$',
					suffix : 'k',
					values : []
				};
				for (i = 0; i < listings.length; i++) {
					listing = listings[i];
					stats.values[i] = listing.valuation / 1000;
				}
				stats.values.reverse();
				self.imports.statistics.displayStats(stats);
			};
			self.displayPagination = function(search_type, keywords,
					start_index, profile_id) {
				var args = {
					page : 'searchlistings',
					search_type : search_type,
					keywords : keywords,
					profile_id : profile_id,
					start_index : start_index
				};
				var prevargs, nextargs;
				if (start_index > 0) {
					prevargs = $.extend(true, {}, args);
					prevargs.start_index = start_index - 20;
					$('#search-prev-results').show().unbind().data('args',
							prevargs).bind('click', function() {
						searchListings.doSearch($(this).data('args'));
					});
				} else {
					$('#search-prev-results').hide();
				}
				if (start_index + num_results < total_results) {
					nextargs = $.extend(true, {}, args);
					nextargs.start_index = start_index + 20;
					$('#search-next-results').show().unbind().data('args',
							nextargs).bind('click', function() {
						searchListings.doSearch($(this).data('args'));
					});
				} else {
					$('#search-next-results').hide();
				}
			};
			return self;
}
