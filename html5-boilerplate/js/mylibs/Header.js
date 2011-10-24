function Header() {
			var self = {};
			self.bindLinks = function() {
				$('#header-logo,#header-title-link').unbind().bind('click',
						function() {
							self.imports.util.hashedCall('searchListings', 'doSearch');
						});
				$('#header-menu-command-login').unbind().bind('click',
						function() {
							self.imports.user.login();
						});
				$('#header-menu-command-logout').unbind().bind('click',
						function() {
							self.imports.user.logout();
						});
				$('#header-menu-bar-submit').unbind().bind('click', function() {
					self.imports.util.hashedCall('listingObj', 'newListing');
				});
				$('#header-menu-bar-setup').unbind().bind('click', function() {
					document.location = '/setup';
				});
				$('#header-menu-bar-test').unbind().bind('click', function() {
					document.location = '/hello';
				});
				$('#header-menu-bar-top').unbind().bind('click', function() {
					self.imports.util.hashedCall('searchListings', 'doTopSearch');
				});
				$('#header-menu-bar-valuation').unbind().bind(
						'click',
						function() {
							self.imports.util.hashedCall('searchListings',
									'doValuationSearch');
						});
				$('#header-menu-bar-active').unbind().bind('click', function() {
					self.imports.util.hashedCall('searchListings', 'doActiveSearch');
				});
				$('#header-menu-bar-popular').unbind().bind(
						'click',
						function() {
							self.imports.util.hashedCall('searchListings', 'doPopularSearch');
						});
				$('#header-menu-bar-discussed').unbind().bind(
						'click',
						function() {
							self.imports.util.hashedCall('searchListings',
									'doDiscussedSearch');
						});
				$('#header-menu-bar-latest').unbind().bind('click', function() {
					self.imports.util.hashedCall('searchListings', 'doLatestSearch');
				});
				$('#header-menu-bar-top-users').unbind().bind('click',
						function() {
							self.imports.util.hashedCall('searchUsers', 'doTopSearch');
						});
				$('#footer-menu-bar-faq').unbind().bind('click', function() {
					self.imports.util.hashedCall('util', 'displayFaq');
				});
				$('#footer-menu-bar-disclaimer').unbind().bind('click',
						function() {
							self.imports.util.hashedCall('util', 'displayDisclaimer');
						});
				$('#footer-menu-bar-contact').unbind().bind('click',
						function() {
							self.imports.util.hashedCall('util', 'displayContact');
						});
				$('#faq-contact').unbind().bind('click', function() {
					self.imports.util.hashedCall('util', 'displayContact');
				});
			};
			self.showHeaderProfile = function(profile_id) {
				var errorCallback = function() {
					$('#header-menu-message').html('unable to retrieve user');
				};
				self.imports.profiles.showProfile(profile_id, errorCallback);
			};
			self.displayUser = function(userobj) {
				if (userobj.profile && userobj.logout_url) {
					$('#header-menu-name-login,#header-menu-command-login')
							.hide();
					$('#header-menu-name-username').html(
							userobj.profile.username).data('profile_id',
							userobj.profile.profile_id).show().unbind().bind(
							'click',
							function() {
								self.imports.util.hashedCall('header', 'showHeaderProfile',
										[ $(this).data('profile_id') ]);
							});
					$(
							'#header-menu-command-logout,#header-menu-bar-submit-spacer,#header-menu-bar-submit')
							.show();
					$('#header-menu-command-logout').data('logout_url',
							userobj.logout_url).show().unbind().bind(
							'click',
							function() {
								var errorCallback = function() {
									$('#header-menu-message').html(
											'unable to retrieve user');
								};
								userobj.logout($(this).data('logout_url'),
										errorCallback);
							});
				} else {
					$('#header-menu-name-login,#header-menu-command-login')
							.show();
					$('#header-menu-name-username').html(null).hide();
					$(
							'#header-menu-command-logout,#header-menu-bar-submit-spacer,#header-menu-bar-submit')
							.hide();
				}
				if (userobj.profile && userobj.logout_url
						&& userobj.profile.admin) {
					$(
							'#header-menu-bar-setup-spacer,#header-menu-bar-setup,#header-menu-bar-test-spacer,#header-menu-bar-test')
							.show();
				} else {
					$(
							'#header-menu-bar-setup-spacer,#header-menu-bar-setup,#header-menu-bar-test-spacer,#header-menu-bar-test')
							.hide();
				}
			};
			self.narrowClasses = {
				'#mainbody' : 'body-narrow',
				'#header' : 'header-narrow',
				'#header-logo' : 'header-logo-narrow',
				'#header-title' : 'header-title-narrow',
				'#header-menu-bar' : 'header-menu-bar-narrow',
				'#header-menu-name' : 'header-menu-name-narrow',
				'#header-menu-command' : 'header-menu-command-narrow',
				'#searchlistingspage' : 'content-narrow',
				'#searchuserspage' : 'content-narrow',
				'#commentspage' : 'content-narrow',
				'#profilepage' : 'content-narrow',
				'#listingpage' : 'content-narrow',
				'#bidspage' : 'content-narrow',
				'#loginpage' : 'content-narrow',
				'#faqpage' : 'content-narrow',
				'#footer' : 'footer-narrow',
				'#footer-copyright' : 'footer-copyright-narrow',
				'.content-results' : 'content-results-narrow'
			};
			self.currentStyle = undefined;
			self.resizeStyles = function(windowWidth) {
				var sel = undefined;
				var width = parseInt(windowWidth);
				var newStyle = (width < 701) ? 'narrow' : 'normal';
				if (self.currentStyle === undefined) { // first time
					if (newStyle === 'narrow') {
						for (sel in self.narrowClasses) {
							$(sel).addClass(self.narrowClasses[sel]);
						}
					}
				} else if (self.currentStyle !== newStyle
						&& newStyle === 'narrow') { // style change to narrow
					for (sel in self.narrowClasses) {
						$(sel).addClass(self.narrowClasses[sel]);
					}
				} else if (self.currentStyle !== newStyle
						&& newStyle === 'normal') { // style change to normal
					for (sel in self.narrowClasses) {
						$(sel).removeClass(self.narrowClasses[sel]);
					}
				}
				self.currentStyle = newStyle; // save the new style
			};
			return self;
}
