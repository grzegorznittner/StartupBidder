function Profiles() {
			var self = {};
			self.saveProfile = function(profile_id) {
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
				profile.investor = $('#profile-edit-investor').attr('checked') ? 1
						: 0;
				var callback = function(profile) {
					if (profile) {
						self.displayProfile(profile, 'confirmed',
								'profile saved');
					} else {
						self.displayProfile(profile, 'attention',
								'could not save profile');
					}
				};
				var errorCallback = function() {
					$('#profile-save-message').attr('class', 'attention').html(
							'unable to save profile');
				};
				self.imports.backend.saveProfile(profile, callback, errorCallback);
			};
			self.showProfile = function(profile_id, errorCallback) {
				self.imports.backend.getProfile(profile_id, self.displayProfile,
						errorCallback);
			};
			self.displayProfileChart = function(profile) {
				if (!profile) {
					self.imports.statistics.hideCharts();
					return;
				}
				var username = profile.username || profile.name || 'Profile';
				var stats = {
					label : username,
					chartType : 'column',
					xaxis : '',
					yaxis : 'activity',
					labels : [ '#votes', '#listings', '#bids', '#comments' ],
					values : [ profile.num_votes || 0,
							profile.num_listings || 0, profile.num_bids || 0,
							profile.num_comments || 0 ]
				};
				self.imports.statistics.displayStats(stats);
			};
			self.displayProfile = function(data, msgClass, msgText) {
				var profile = data && data.profile ? data.profile : data;
				if (!profile) {
					$('#profile-header').hide();
					$('#profile-snippet').html('');
					$('#profile-form-editable').hide();
					$('#profile-form-readonly').hide();
					$('#profile-username').html(
							'<em>unable to retrive user profile</em>');
					return;
				}
				$('#profile-save-message').attr('class',
						(msgClass || 'attention')).html(msgText || '');
				self.imports.util.displayPage('profile');
				$('#profile-snippet').html(
						self.imports.user.generateUserSnippetHtml('profile', profile));
				self.imports.user.bindUserSnippet('profile', profile);
				$('#profile-header').show();
				self.displayProfileChart(profile);

				// enable editable if logged in
				if (self.imports.user.profile
						&& self.imports.user.profile.profile_id == profile.profile_id) {
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
					$('#profile-edit-investor').attr('checked',
							profile.investor);
					$('#profile-edit-joined-date').html(
							self.imports.util.formatDate(profile.joined_date));
					$('#profile-edit-open-id').html(profile.open_id);
					self.setProfileLinks(profile, 'editable');
					$('#profile-form-editable').show();
				} else {
					$('#profile-form-editable').hide();
					$('#profile-title').html(profile.title);
					$('#profile-organization').html(profile.organization);
					$('#profile-email').html(profile.email);
					$('#profile-email-href').attr('href',
							'mailto:' + profile.email);
					$('#profile-facebook').html(profile.facebook);
					$('#profile-facebook-href').attr('href',
							'http://www.facebook.com/' + profile.facebook);
					$('#profile-twitter').html(profile.twitter);
					$('#profile-twitter-href').attr('href',
							'http://www.twitter.com/' + profile.twitter);
					$('#profile-linkedin').html(profile.linkedin);
					$('#profile-linkedin-href').attr('href',
							'http://www.linkedin.com/' + profile.linkedin);
					$('#profile-investor').attr('checked', profile.investor);
					$('#profile-joined-date').html(
							self.imports.util.formatDate(profile.joined_date));
					$('#profile-open-id').html(profile.open_id);
					self.setProfileLinks(profile, 'readonly');
					$('#profile-form-readonly').show();
				}
			};
			self.setProfileLinks = function(profile, auth) {
				if (auth !== 'editable') {
					return;
				}
				$('#profile-save-link').show().unbind().data('profile_id',
						profile.profile_id).bind('click', function(e) {
					e.preventDefault();
					self.saveProfile($(this).data('profile_id'));
				});
				if (profile.status == 'active') {
					$('#profile-activate-link').hide();
					$('#profile-deactivate-link').show().unbind().data(
							'profile_id', profile.profile_id).bind(
							'click',
							function() {
								var errorCallback = function() {
									$('#profile-save-message').attr('class',
											'attention').html(
											'unable to deactivate profile');
								};
								self.imports.backend.deactivateProfile($(this).data(
										'profile_id'), self.displayProfile,
										errorCallback);
							});
				} else {
					$('#profile-activate-link').show().unbind().data(
							'profile_id', profile.profile_id).bind(
							'click',
							function() {
								var errorCallback = function() {
									$('#profile-save-message').attr('class',
											'attention').html(
											'unable to activate profile');
								};
								self.imports.backend.activateProfile($(this).data(
										'profile_id'), self.displayProfile,
										errorCallback);
							});
					$('#profile-deactivate-link').hide();
				}
			};
			return self;
}
