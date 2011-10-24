function Userbox() { // module showing a summary box for the logged in user
			var self = {};
			self.setup = function() {
				$('#userbox-details').bind(
						'click',
						function(e) {
							self.imports.util.hashedCall('user', 'showUserProfile', [
									$('#userbox-profile-id').val(), 'zz' ]); // FIXME - handle error messages
						});
				$('#userbox-listings').bind(
						'click',
						function(e) {
							self.imports.util.hashedCall('user', 'showListingsForProfile', [
									$('#userbox-profile-id').val(), 'zz' ]); // FIXME - handle error messages
						});
				$('#userbox-bids').bind(
						'click',
						function(e) {
							self.imports.util.hashedCall('user', 'showBidsForProfile', [
									$('#userbox-profile-id').val(), 'zz' ]); // FIXME - handle error messages
						});
				$('#userbox-bids-accepted').bind(
						'click',
						function(e) {
							self.imports.util.hashedCall('user',
									'showAcceptedBidsForProfile', [
											$('#userbox-profile-id').val(),
											'zz' ]); // FIXME - handle error messages
						});
				$('#userbox-bids-paid').bind(
						'click',
						function(e) {
							self.imports.util.hashedCall('user', 'showPaidBidsForProfile', [
									$('#userbox-profile-id').val(), 'zz' ]); // FIXME - handle error messages
						});
				$('#userbox-comments').bind(
						'click',
						function(e) {
							self.imports.util.hashedCall('user', 'showCommentsForProfile', [
									$('#userbox-profile-id').val(), 'zz' ]); // FIXME - handle error messages
						});
				$('#userbox').bind('display', self.drawUserbox);
			};
			self.drawUserbox = function(e, data) {
				var profile = self.imports.user ? self.imports.user.profile : undefined;
				if (!profile) {
					$('#userbox').hide();
					return;
				}
				var memberType = self.getMemberType();
				var memberClass = 'userbox-' + memberType;
				$('#userbox-profile-id').val(profile.profile_id);
				$('#userbox-title').html(
						profile.username || profile.name || 'Profile');
				$('#userbox-score').html(self.getScore());
				$('#userbox-num-votes').html(profile.num_votes);
				$('#userbox-num-listings').html(profile.num_listings);
				$('#userbox-num-bids').html(profile.num_bids);
				$('#userbox-num-bids-accepted').html(profile.num_accepted_bids);
				$('#userbox-num-bids-paid')
						.html(
								profile.num_payed_bids !== undefined ? profile.num_payed_bids
										: profile.num_paid_bids);
				$('#userbox-num-comments').html(profile.num_comments);
				$('#userbox-listings-plural').html(
						profile.num_listings !== 1 ? 's' : '');
				$('#userbox-bids-plural').html(
						profile.num_bids !== 1 ? 's' : '');
				$('#userbox-comments-plural').html(
						profile.num_comments !== 1 ? 's' : '');
				$('#userbox-member-type').html(self.imports.util.ucFirst(memberType));
				$('#userbox-chart').removeClass().addClass('userbox-chart')
						.addClass(memberClass);
				$('#userbox').show();
			};
			self.getScore = function() {
				if (!self.imports.user || !self.imports.user.profile) {
					return 0;
				}
				var profile = self.imports.user.profile;
				var score = profile.num_votes + profile.num_listings
						+ profile.num_bids + profile.num_comments;
				return score;
			};
			self.getMemberType = function() {
				var score = self.getScore();
				var memberType;
				if (score >= 100) {
					memberType = 'platinum';
				} else if (score >= 50) {
					memberType = 'gold';
				} else if (score >= 10) {
					memberType = 'silver';
				} else {
					memberType = 'bronze';
				}
				return memberType;
			};
			self.setup();
			return self;
}
