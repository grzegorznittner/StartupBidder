function CompanyBannerClass(tab) {
    this.tab = tab || 'basics';
};
pl.implement(CompanyBannerClass, {
    store: function(json, base) {
        var key;
        if (!json) {
            return;
        }
        if (base) {
            this.base = base;
        }
        if (json.listing && json.listing.listing_id) {
            for (key in json.listing) {
                this[key] = json.listing[key];
            }
        }
        this.loggedin_profile = json.loggedin_profile;
        this.loggedin_profile_id = this.loggedin_profile && this.loggedin_profile.profile_id;
    },

    displayMinimal: function(json, base) {
        this.store(json, base);
        this.displayBanner();
        pl('.preloadercompanybanner').hide();
        pl('.companybannerwrapper').show();
    },

    display: function(json, base) {
        this.store(json, base);
        this.displayBanner();
        this.displayStatusNotification();
        this.displaySubmit();
        this.displayFollow();
        this.displayTabs();
        pl('.preloadercompanybanner').hide();
        pl('.companybannerwrapper').show();
    },

    displayBanner: function() {
        var logobg = this.logo ? 'url(' + this.logo + ') no-repeat scroll left top' : null,
            url = this.website ? new URLClass(this.website) : null,
            cat = this.category || '',
            addr = this.brief_address,
            catprefix = !cat || (cat !== 'Other' && !cat.match(/^[aeiou]/i)) ? 'A' : 'An',
            catlink = cat && cat !== 'Other' ? '<a href="/main-page.html?type=category&val=' + encodeURIComponent(cat) + '">' + cat + '</a>' : '',
            type = this.type || 'venture',
            platform = this.platform && this.platform !== 'other' ? PlatformClass.prototype.displayName(this.platform) + ' ' : '',
            categorytext = this.platform && this.platform !== 'other' && this.category === 'Software' ? '' : catprefix + ' ' + catlink + ' ',
            platformprefix = categorytext ? '' : (platform.match(/^[aeiou]/i) ? 'An ' : 'A '),
            catlinked = categorytext + platformprefix + platform + type,

            locprefix  = type === 'company' ? 'in' : 'from',
            addrlinked = !addr ? '' : ' ' + locprefix + ' <a href="/main-page.html?type=location&val=' + encodeURIComponent(addr) + '">' + addr + '</a>',
            categoryaddresstext = catlinked + addrlinked,
            // founderstext = this.founders ? ' founded by ' + this.founders : '', // too long
            founderstext = '',
            status = this.status || 'new',
            website = this.website || '/company-page.html?id=' + this.listing_id,
            listingdatetext = 
                (this.listing_date
                    ? 'Listed on ' + DateClass.prototype.format(this.listing_date)
                    : (this.status === 'new' || this.status === 'posted' ? 'Not yet listed' : '')
                )
                + (this.website ? ' from ' : '');
        if (logobg) {
            pl('#companylogo').removeClass('noimage').css({background: logobg});
        }
        pl('#title').text(this.title || 'Company or App Name Here');
        pl('title').text('Startupbidder Listing: ' + (this.title || 'Company or App Name Here'));
        pl('#mantra').text(this.mantra || 'Mantra here');
        pl('#categoryaddresstext').html(categoryaddresstext);
        pl('#founderstext').text(founderstext);
        pl('#listing_date_text').html(listingdatetext);
        pl('#websitelink').attr({href: website});
        if (url) {
            pl('#domainname').text(url.getHostname());
            pl('#websitelinkicon').bind('click', function() {
                window.open(website);
            });
        }
        else {
            pl('#domainname').text('');
            pl('#websitelinkicon').hide();
        }
    },

    displayStatusNotification: function() {
        var statusmsg = '';
        if (this.loggedin_profile && this.loggedin_profile_id === this.profile_id) {
            if (this.status === 'new') {
                statusmsg = '<span class="normal">An admin will review your listing after submit</span>';
            }
            else if (this.status === 'posted') {
                statusmsg = '<span class="inprogress">An admin is reviewing your listing for activation</span>';
            }
            else if (this.status === 'withdrawn') {
                statusmsg = '<span class="errorcolor">Your listing is withdrawn and no longer active</span>';
            }
            else if (this.status === 'frozen') {
                statusmsg = '<span class="errorcolor">An admin has frozen your listing pending review</span>';
            }
            /*
            else if (this.status === 'active') {
                statusmsg = '<span class="normal">Your listing is active</span>';
            }
            */
        }
        else {
            if (this.status === 'new') {
                statusmsg = '<span class="normal">An admin will review this listing after submission</span>';
            }
            else if (this.status === 'posted') {
                statusmsg = '<span class="inprogress">An admin is reviewing this listing for activation</span>';
            }
            else if (this.status === 'withdrawn') {
                statusmsg = '<span class="errorcolor">This listing is withdrawn and no longer active</span>';
            }
            else if (this.status === 'frozen') {
                statusmsg = '<span class="errorcolor">An admin has frozen this listing pending review</span>';
            }
            /*
            else if (this.status === 'active') {
                statusmsg = '<span class="normal">This listing is active</span>';
            }
            */
        }
        pl('#submiterrormsg').html(statusmsg);
    },
            
    postListing: function() {
        var self = this,
            completeFunc = function(json) {
                document.location = '/company-page.html?id=' + self.base.listing.listing_id;
            },
            ajax = new AjaxClass('/listing/post', 'newlistingmsg', completeFunc);
        ajax.setPost();
        ajax.call();
    },

    highlightMissing: function() {
        var self = this,
            msg = '',
            msgs = [],
            errorpages = {},
            missing,
            displayName,
            page,
            i;
        for (i = 0; i < self.base.missingprops.length; i++) {
            missing = self.base.missingprops[i];
            page = self.base.proppage[missing];
            if (!errorpages[page]) {
                errorpages[page] = [];
            }
            displayName = self.base.displayNameOverrides[missing] || missing.toUpperCase();
            errorpages[page].push(displayName);
        }
        for (i = 0; i < self.base.pages.length; i++ ) {
            page = self.base.pages[i];
            //self.highlightPage(page, errorpages[page]);
        }
        for (page in errorpages) {
            //msg = page.toUpperCase() + ' page: ' + errorpages[page].join(', ');
            msg = errorpages[page].join(', ');
            msgs.push(msg);
        }
        return msgs.join('; ');
    },

    bindSubmitButton: function() {
        var self = this,
            submitValidator = function() {
                var msg,
                    msgs = [],
                    pctcomplete = self.base.pctComplete();
                if (pctcomplete !== 100) {
                    msg = self.highlightMissing();
                    msgs.push('Missing info: ' + msg);
                }
                return msgs;
            };
        pl('#submitbutton').bind({
            click: function() {
                var validmsgs = submitValidator();
                if (validmsgs.length > 0) {
                    pl('#submiterrormsg').addClass('errorcolor');
                    pl('#submiterrormsg').html('Please correct: ' + validmsgs.join(' '));
                }
                else {
                    pl('#submiterrormsg').removeClass('errorcolor').addClass('inprogress').text('Submitting listing...');
                    self.postListing();
                }
                return false;
            }
        }).show();
    },

    displaySubmit: function() {
        var self = this;
        if (self.loggedin_profile && self.loggedin_profile.profile_id === self.profile_id && self.status === 'new') { // owner
            this.bindSubmitButton();
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
        if (this.loggedin_profile && this.loggedin_profile.profile_id !== this.profile_id && this.status === 'active') {
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
            else if (tab === 'presentation') {
                page = 'company-slides-page.html';
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
        if (!MicroListingClass.prototype.getHasBmc(this)) {
            pl('#modeltab').hide();
        }
        if (!MicroListingClass.prototype.getHasIp(this)) {
            pl('#presentationtab').hide();
        }
        if (this.loggedin_profile && this.loggedin_profile_id !== this.profile_id) {
            pl('#sendmessagelink').attr({href: '/messages-page.html?to_user_id=' + (this.profile_id || '') }).css({display: 'inline'});
        }
        pl('#companynavcontainer').show();
    }

});

