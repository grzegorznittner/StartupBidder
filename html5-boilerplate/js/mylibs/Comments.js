function Comments() {
			var self = {};
			self.saveComment = function(listing_id, profile_id) {
				var commentText = $('#comment-textarea').val();
				var callback = function(comment) {
					var keepComment = comment ? false : true;
					var callback2 = function(results) {
						self.displayResults(results, keepComment, 'confirmed',
								'comment saved');
					};
					var errorCallback2 = function() {
						$('#comments-message').attr('class', 'attention').html(
								'unable to get comments');
						self.imports.util.displayPage('comments');
					};
					self.imports.backend.commentsForListing(listing_id, callback2,
							keepComment, errorCallback2);
				};
				var errorCallback = function() {
					$('#comment-save-message').attr('class', 'attention').html(
							'unable to add comment');
				};
				self.imports.backend.addComment(listing_id, profile_id, commentText,
						callback, errorCallback);
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
					$('#comments-profile-snippet').html(
							self.imports.user.generateUserSnippetHtml('comments',
									comments_profile));
					self.imports.user.bindUserSnippet('comments', comments_profile);
					$('#comments-profile-header').show();
				} else {
					$('#comments-profile-header').hide();
					$('#comments-profile-snippet').html('');
				}
			};
			self.displayComments = function(comments, listing) {
				var resultsHtml = '';
				var comment;
				if (comments && comments.length > 0) {
					for ( var i = 0; i < comments.length; i++) { // create results
						comment = comments[i];
						resultsHtml += self.generateResultHtml(i, comment,
								listing, self.imports.user.profile);
					}
				} else if (comments && comments.length == 0) {
					resultsHtml += '<div class="content-results-list-item">';
					resultsHtml += ' <span id="comments-item-message" class="attention item-message">no comments found</span>';
					resultsHtml += '</div>';
				}
				$('#comments-content-results-list').hide().html(resultsHtml)
						.show(); // display html
			};
			self.displayListingSnippet = function(listing) {
				if (listing && listing.listing_id) {
					$('#comments-listing-snippet').html(
							self.imports.listingObj.generateListingSnippetHtml('comments',
									listing));
					self.imports.listingObj.bindListingSnippet('comments', listing);
					$('#comments-listing-header').show();
				}
			};
			self.displayCommentForm = function(listing) {
				if (!listing) {
					$('#comments-listing-snippet').html('Comments:');
					$(
							'#comments-listing-header,#comment-loggedin-add,#comment-not-loggedin')
							.hide();
					return;
				}
				if (!self.imports.user.profile) {
					$('#comment-loggedin-add').hide();
					$('#comment-not-loggedin').show();
					return;
				}
				// enable editable if logged in
				$('#comment-loggedin-add').show();
				$('#comment-not-loggedin').hide();
				$('#comment-save-button').show().unbind().data('listing_id',
						listing.listing_id).data('profile_id',
						self.imports.user.profile.profile_id).bind(
						'click',
						function(e) {
							e.preventDefault();
							self.saveComment($(this).data('listing_id'),
									$(this).data('profile_id'));
						});
			};
			self.bindCommentLinks = function(comments, listing) {
				var comment;
				if (!comments || comments.length == 0) {
					return;
				}
				var deleteComment = function() {
					var index = $(this).data('index');
					var profile_id = $(this).data('profile_id');
					var listing_id = $(this).data('listing_id');
					var errorCallback = function() {
						$('#comments-item-message-' + index).attr('class',
								'attention').html('unable to delete comment');
					};
					var errorCallback2 = function() {
						$('#comments-message').attr('class', 'attention').html(
								'unable to get comments');
						self.imports.util.displayPage('comments');
					};
					var profile_callback = function() {
						self.imports.backend.commentsForProfile(profile_id,
								self.displayResults, errorCallback2);
					};
					var listings_callback = function() {
						self.imports.backend.commentsForListing(listing_id,
								self.displayResults, errorCallback2);
					};
					var callback = listing_id ? listings_callback
							: profile_callback;
					self.imports.backend.deleteComment($(this).data('comment_id'), callback,
							errorCallback);
				};
				for ( var i = 0; i < comments.length; i++) {
					comment = comments[i];
					$('#comments-item-profile-' + i).data({
						index : i,
						profile_id : comment.profile_id
					}).unbind().bind(
							'click',
							function() {
								self.imports.util.hashedCall('comments',
										'showCommentProfile', [
												$(this).data('profile_id'),
												$(this).data('index') ]);
							});
					$('#comments-item-listing-' + i).data({
						index : i,
						listing_id : comment.listing_id
					}).unbind().bind(
							'click',
							function() {
								self.imports.util.hashedCall('comments',
										'showCommentListing', [
												$(this).data('listing_id'),
												$(this).data('index') ]);
							});
					if (!self.imports.user.profile
							|| self.imports.user.profile.profile_id != comment.profile_id) { // not my profile so I can't delete
						continue;
					}
					$('#comments-item-delete-' + i).data({
						index : i,
						comment_id : comment.comment_id,
						profile_id : (listing ? '' : comment.profile_id),
						listing_id : (listing ? listing.listing_id : '')
					}).unbind().bind('click', deleteComment);
				}
			};
			self.displayCommentStats = function(comments) {
				var comment, commentDate, i;
				var k = undefined;
				var stats = {
					label : 'Comment Activity',
					xaxis : 'time',
					yaxis : '#comments',
					values : []
				};
				var commentMap = {};
				var commentDates = [];
				for (i = 0; i < comments.length; i++) {
					comment = comments[i];
					commentDate = comment.comment_date;
					commentMap[commentDate] = commentMap[commentDate] ? commentMap[commentDate] + 1
							: 1;
				}
				for (k in commentMap) {
					commentDates.push(k);
				}
				commentDates.sort(function(a, b) {
					return a - b;
				});
				for (i = 0; i < commentDates.length; i++) {
					commentDate = commentDates[i];
					stats.values[i] = commentMap[commentDate];
				}
				stats.values.reverse();
				self.imports.statistics.displayStats(stats);
			};
			self.displayResults = function(results, keepComment, msgClass,
					msgText) {
				var comments_profile = results ? results.profile : null;
				var listing = results ? results.listing : null;
				var comments = results ? results.comments : null;

				$('#comments-message').html('');
				$('#comment-save-message').attr('class',
						(msgClass || 'attention')).html(msgText || '');
				if (!keepComment) {
					$('#comment-textarea').val('');
				}
				self.imports.util.displayPage('comments');

				self.displayUserSnippet(comments_profile);
				self.displayListingSnippet(listing);
				self.displayCommentForm(listing);
				self.displayComments(comments, listing);
				self.displayCommentStats(comments);
				self.bindCommentLinks(comments, listing);
			};
			self.showCommentProfile = function(profile_id, index) {
				var errorCallback = function() {
					$('#comments-item-message-' + index).attr('class',
							'attention').html('unable to get profile');
				};
				self.imports.profiles.showProfile(profile_id, errorCallback);
			};
			self.showCommentListing = function(listing_id, index) {
				var errorCallback = function() {
					$('#comments-item-message-' + index).attr('class',
							'attention').html('unable to get listing');
				};
				self.imports.backend.getListing(listing_id, self.imports.listingObj.displayListing,
						errorCallback);
			};
			self.generateResultHtml = function(i, comment, listing, profile) {
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
						+ comment.num + '</div>';
				resultHtml += '<div class="content-results-list-item-title">';
				resultHtml += comment.text;
				resultHtml += '</div>';
				if (profile && profile.profile_id == comment.profile_id) {
					resultHtml += '<div class="content-results-list-item-profile link" id="comments-item-delete-'
							+ i + '">(delete comment)';
					resultHtml += ' <span id="comments-item-message-' + i
							+ '" class="attention"></span>';
					resultHtml += '</div>';
				}
				resultHtml += '<div class="content-results-list-item-clear"></div>';
				resultHtml += '<div class="content-results-list-item-detail">';
				resultHtml += 'by ';
				resultHtml += '<span class="link" id="comments-item-profile-'
						+ i + '">';
				resultHtml += comment.profile_username;
				resultHtml += '</span>';
				resultHtml += ' on ';
				resultHtml += self.imports.util.formatDate(comment.comment_date);
				resultHtml += listing ? ''
						: ' for <span class="link" id="comments-item-listing-'
								+ i + '">' + comment.listing_title + '</span>';
				resultHtml += '</div>';
				resultHtml += '</div>';
				resultHtml += '<div class="content-results-list-item-clear"></div>';
				return resultHtml;
			};
			return self;
}
