function CompanyBannerClass(tab) {
    this.tab = tab || 'basics';
};
pl.implement(CompanyBannerClass, {
    store: function(json) {
        var key;
        if (json && json.listing && json.listing.listing_id) {
            for (key in json.listing) {
                this[key] = json.listing[key];
            }
        }
        this.loggedin_profile = json && json.loggedin_profile;
        this.loggedin_profile_id = this.loggedin_profile && this.loggedin_profile.profile_id;
    },

    displayMinimal: function(json) {
        if (json) {
            this.store(json);
        }
        this.displayBanner();
        pl('.preloadercompanybanner').hide();
        pl('.companybannerwrapper').show();
    },

    display: function(json) {
        if (json) {
            this.store(json);
        }
        this.displayBanner();
        if (this.status !== 'new' && this.status !== 'posted') {
            this.displayFollow();
        }
        this.displayTabs();
        pl('.preloadercompanybanner').hide();
        pl('.companybannerwrapper').show();
    },

    displayBanner: function() {
        var logobg = this.logo ? 'url(' + this.logo + ') no-repeat scroll left top' : null,
            url = this.website ? new URLClass(this.website) : null,
            cat = this.category || '',
            addr = this.brief_address,
            catprefix = !cat || (cat !== 'Other' && !cat.match(/^[AEIOU]/)) ? 'A' : 'An',
            catlink = cat && cat !== 'Other' ? '<a href="/main-page.html?type=category&val=' + encodeURIComponent(cat) + '">' + cat + '</a>' : '',
            catlinked = catprefix + ' ' + catlink + ' listing',
            addrlinked = !addr ? '' : ' in <a href="/main-page.html?type=location&val=' + encodeURIComponent(addr) + '">' + addr + '</a>',
            categoryaddresstext = catlinked + addrlinked,
            founderstext = (this.founders ? ' founded by ' + this.founders : ''),
            status = this.status || 'new',
            website = this.website || '/company-page.html?id=' + this.listing_id,
            listingdatetext = '<span class="inputmsg">' + SafeStringClass.prototype.ucfirst(status) + '</span> listing'
                + (this.listing_date ? ' from ' + DateClass.prototype.format(this.listing_date) : (this.status === 'new' ? ' not yet listed' : ''))
                + (this.website ? ' at ' : '');
        if (logobg) {
            pl('#companylogo').removeClass('noimage').css({background: logobg});
        }
        pl('#title').text(this.title || 'Listing Name Here');
        pl('title').text('Startupbidder Listing: ' + (this.title || 'Listing Name Here'));
        pl('#mantra').text(this.mantra || 'Mantra here');
        pl('#companystatus').text('Listing is ' + status);
        if (status === 'withdrawn') {
            pl('#companystatus').addClass('attention');
        }
        pl('#categoryaddresstext').html(categoryaddresstext);
        pl('#founderstext').text(founderstext);
        pl('#listing_date_text').html(listingdatetext);
        pl('#websitelink').attr({href: website});
        if (url) {
            pl('#domainname').text(url.getHostname());
        }
        else {
            pl('#domainname').text('visit listing page');
        }
    },

    bindFollow: function() {
        var self = this;
        pl('#followbtn').bind({
            click: function() {
            var following = self.monitored;
            if (following) {
                self.unfollow();
                }
                else {
                    self.follow();
                }
            }
        });
    },

    unfollow: function() {
        var self = this,
            completeFunc = function(json) {
                self.monitored = false;
                self.displayFollow();
            },

            ajax = new AjaxClass('/monitor/deactivate/' + this.listing_id, 'followmsg', completeFunc);
        ajax.setPost();
        ajax.call();
    },

    follow: function() {
        var self = this,
            completeFunc = function(json) {
                self.monitored = true;
                self.displayFollow();
            },

            ajax = new AjaxClass('/monitor/set/' + this.listing_id, 'followmsg', completeFunc);
        ajax.setPost();
        ajax.call();
    },

    displayFollow: function() {
        var following = this.monitored;
        if (this.loggedin_profile && this.loggedin_profile.profile_id !== this.profile_id) {
            if (following) {
                this.displayFollowing();
            }
            else {
                this.displayNotFollowing();
            }
            this.bindFollow();
        }
    },

    displayFollowing: function() {
            pl('#followbtn').text('UNFOLLOW');
            pl('#followbtn').show();
    },

    displayNotFollowing: function() {
            pl('#followbtn').text('FOLLOW').show();
    },

    displayTabs: function() {
        var self = this;
        pl('.companynavlink').each(function() {
            var tabid = pl(this).attr('id'),
                tab = tabid.replace(/tab$/,''),
                page,
                url;
            if (tab === 'basics') {
                page = 'company-page.html';
            }
            else if (tab === 'bids' && self.loggedin_profile_id) {
                if (self.loggedin_profile_id === self.profile_id) {
                    page = 'company-owner-bids-page.html';
                }
                else {
                    page = 'company-investor-bids-page.html';
                }
            }
            else {
                page = 'company-' + tab + '-page.html';
            }
            url = '/' + page + '?id=' + self.listing_id;
            pl(this).attr({href: url});
        });
        /*
        pl('#num_comments').text(this.num_comments || 0);
        pl('#num_qandas').text(this.num_qandas || 0);
        if (this.loggedin_profile_id && this.loggedin_profile_id === this.profile_id) {
            pl('#num_bids').text(this.num_bids || 0);
        }
        else {
            pl('#num_bids').text('');
        }
        */
        if (!this.asked_fund) {
            pl('#bidstab').hide();
        }
        if (this.loggedin_profile && this.loggedin_profile_id !== this.profile_id) {
            pl('#sendmessagelink').attr({href: '/messages-page.html?to_user_id=' + (this.profile_id || '') }).css({display: 'inline'});
        }
        pl('#companynavcontainer').show();
    }

});

