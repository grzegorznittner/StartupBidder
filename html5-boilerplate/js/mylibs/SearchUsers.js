function SearchUsers() {
			var self = {};
			self.defaultSearchType = 'top';
			self.bindLinks = function() {
				$('#user-search-submit').unbind().bind('click', function() {
					self.imports.util.hashedCall('searchUsers', 'doKeywordSearch');
				});
				$('#user-search-keywords').unbind().bind('keyup', function(e) {
					if (e.keyCode == 13) { // return key
						self.imports.util.hashedCall('searchUsers', 'doKeywordSearch');
					}
				});
			};
			self.doTopSearch = function() {
				self.doSearch({
					search_type : 'top'
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
				var keywords = $('#user-search-keywords').val();
				if (keywords) {
					keywords = keywords.replace(/\btype:\w*/g, '').replace(
							/\s+/g, ' ').trim();
				}
				$('#user-search-keywords').val(keywords);
				var errorCallback = function() {
					$('#user-search-message').html(
							'unable to retrieve profiles');
					self.imports.util.displayPage('searchusers');
				};
				self.imports.backend.searchUsers(args.search_type, keywords,
						args.start_index, args.max_results,
						self.displayResultsJson, errorCallback);
			};
			self.displayResultsJson = function(results_json) {
				if (!results_json.users_props) {
					results_json.users_props = {
						start_index : 0,
						num_results : !results_json.users
								|| results_json.users.length,
						total_results : !results_json.users
								|| results_json.users.length
					};
				}
				self.displayResults(results_json.users,
						results_json.search_type, results_json.keywords,
						results_json.users_props.start_index,
						results_json.users_props.num_results,
						results_json.users_props.total_results);
			};
			self.displayResultsHtml = function(resultsHtml) {
				$('#user-search-results-list').hide().html(resultsHtml).show();
			};
			self.displayResults = function(members, search_type, keywords,
					start_index, num_results, total_results) {
				$('#user-search-message').html('');
				self.imports.util.displayPage('searchusers');
				self.displayMemberStats(members);
				self.displayMembers(members);
				self.bindMembers(members);
				//self.displayPagination(search_type, keywords, start_index); // set pagination
			};
			self.displayMemberStats = function(members) {
				var member, memberDate, i;
				var k = undefined;
				var stats = {
					label : 'Total Members',
					xaxis : 'time',
					yaxis : '#members',
					values : []
				};
				var memberMap = {};
				var memberDates = [];
				var runningTotal = 0;
				for (i = 0; i < members.length; i++) {
					member = members[i];
					memberDate = member.joined_date;
					memberMap[memberDate] = memberMap[memberDate] ? memberMap[memberDate] + 1
							: 1;
				}
				for (k in memberMap) {
					memberDates.push(k);
				}
				memberDates.sort(function(a, b) {
					return a - b;
				});
				for (i = 0; i < memberDates.length; i++) {
					memberDate = memberDates[i];
					runningTotal += memberMap[memberDate];
					stats.values[i] = runningTotal;
				}
				stats.values.reverse();
				self.imports.statistics.displayStats(stats);
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
					resultsHtml += self.imports.user.generateUserSnippetHtml(i, member);
				}
				self.displayResultsHtml(resultsHtml);
			};
			self.bindMembers = function(members) {
				if (!members || !members.length) {
					return;
				}
				var i, member;
				for (i = 0; i < members.length; i++) {
					member = members[i];
					self.imports.user.bindUserSnippet(i, member);
				}
			};
			self.displayPagination = function(search_type, keywords,
					start_index) {
				var args = {
					page : 'searchusers',
					search_type : search_type,
					keywords : keywords,
					start_index : start_index
				};
				var prevargs, nextargs;
				if (start_index > 0) {
					prevargs = $.extend(true, {}, args);
					prevargs.start_index = start_index - 20;
					$('#user-search-prev-results').show().unbind().data('args',
							prevargs).bind('click', function() {
						self.doSearch($(this).data('args'));
					});
				} else {
					$('#user-search-prev-results').hide();
				}
				if (start_index + num_results < total_results) {
					nextargs = $.extend(true, {}, args);
					nextargs.start_index = start_index + 20;
					$('#user-search-next-results').show().unbind().data('args',
							nextargs).bind('click', function() {
						self.doSearch($(this).data('args'));
					});
				} else {
					$('#user-search-next-results').hide();
				}
			};
			return self;
}
