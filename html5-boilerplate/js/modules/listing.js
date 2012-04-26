function ListingClass(id, preview) {
    var self = this;
    this.id = id;
    this.preview = preview;
    this.url = '/listings/get/' + this.id;
    this.statusId = 'listingstatus';
    this.completeFunc = function(json) {
        var header;
        if (!this.preview) {
            header = new HeaderClass();
            header.setLogin(json);
        }
        self.store(json);
        self.display();
    };
    this.bmc = new BMCClass();
    this.ip = new IPClass();
    this.ajax = new AjaxClass(this.url, this.statusId, this.completeFunc);
};
pl.implement(ListingClass, {
    store: function(json) {
        var key;
        if (json && json.listing && json.listing.listing_id) {
            for (key in json.listing) {
                this[key] = json.listing[key];
            }
        }
        if (json && json.loggedin_profile) {
            this.loggedin_profile = json.loggedin_profile;
        }
        this.dateobj = new DateClass();
        this.currency = new CurrencyClass();
        this.listing_url = window.location.protocol + '//' + window.location.hostname + ':' + window.location.port + '/company_page.html?id=' + this.id;
        this.listing_public_title = 'Startupbidder Listing: ' + this.title;
    },
    load: function() {
        this.ajax.call();
    },
    display: function() {
        this.displayTabs();
        this.displayBasics();
        this.displayFollow();
        this.bmc.display(this);
        this.ip.display(this);
        this.ip.bindButtons();
        this.displayMessage();
        this.displayMap();
        this.displayDocuments();
        this.displayFunding();
        this.displaySocial();
        this.displayWithdraw();
        this.displayApprove();
        this.displaySendback();
        this.displayFreeze();
    },
    displayBasics: function() {
        var logobg = this.logo ? 'url(' + this.logo + ') no-repeat scroll left top' : null,
            url = this.website ? new URLClass(this.website) : null,
            categoryfounderstext = (this.category ? (this.category==='Other' ? 'A' : (this.category.match(/^[AEIOU]/) ? 'An '+this.category : 'A '+this.category)) : 'A')
                + ' company' + (this.brief_address ? ' in ' + this.brief_address : '')
                + (this.founders ? ' founded by ' + this.founders : ''),
            listingdatetext = SafeStringClass.prototype.ucfirst(this.status) + ' listing' + (this.listing_date ? ' from ' + this.dateobj.format(this.listing_date) : ' not yet listed') + ' at ';
        if (logobg) {
            pl('#companylogo').removeClass('noimage').css({background: logobg});
        }
        pl('#title').text(this.title || 'Company Name Here');
        pl('title').text('Startupbidder Listing: ' + (this.title || 'Company Name Here'));
        pl('#mantra').text(this.mantra);
        pl('#companystatus').text('Listing is ' + this.status);
        if (this.status === 'withdrawn') {
            pl('#companystatus').addClass('attention');
        }
        pl('#videopresentation').attr({src: this.video});
        pl('#summary').text(this.summary || 'Listing summary goes here');
        pl('#categoryfounderstext').text(categoryfounderstext);
        pl('#listing_date_text').text(listingdatetext);
        pl('#websitelink').attr({href: this.website});
        if (url) {
            pl('#domainname').text(url.getHostname());
        }
        pl('#listingdata').show();
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
            ajax = new AjaxClass('/monitor/deactivate/' + self.listing_id, 'followmsg', completeFunc);
        ajax.setPost();
        ajax.call();
    },
    follow: function() {
        var self = this,
            completeFunc = function(json) {
                self.monitored = true;
                self.displayFollow();
            },
            ajax = new AjaxClass('/monitor/set/' + self.listing_id, 'followmsg', completeFunc);
        ajax.setPost();
        ajax.call();
    },
    displayFollow: function() {
        var self = this,
            following = self.monitored;
        if (self.loggedin_profile) {
            if (following) {
                self.displayFollowing();
            }
            else {
                self.displayNotFollowing();
            }
            self.bindFollow();
        }
    },
    displayFollowing: function() {
            pl('#followbtn').text('UNFOLLOW');
            pl('#followtext, #followbtn').show();
    },
    displayNotFollowing: function() {
            pl('#followtext').hide();
            pl('#followbtn').text('FOLLOW').show();
    },
    displayMessage: function() {
        if (this.loggedin_profile) {
            message = new MessageClass(this.id, this.loggedin_profile.profile_id);
            message.display();
        }
    },
    displayMap: function() {
        this.address = this.address || 'Unknown Address';
        //this.addressurl = this.addressurl || 'http://nominatim.openstreetmap.org/search?q=' + encodeURIComponent(this.address);
        this.addressurl = 'http://maps.google.com/maps?q='+this.latitude+','+this.longitude+'+('+encodeURIComponent(this.title)+', '+encodeURIComponent(this.address)+')';
        this.latitude = this.latitude || '51.499117116569';
        this.longitude = this.longitude || '-0.12359619140625';
        //this.mapurl = 'http://ojw.dev.openstreetmap.org/StaticMap/?lat=' + this.latitude + '&lon=' + this.longitude + '&z=5&show=1&fmt=png&w=302&h=302&att=none';
        this.mapurl = 'http://maps.googleapis.com/maps/api/staticmap?center=' + this.latitude + ',' + this.longitude + '&zoom=7&size=302x302&maptype=roadmap&markers=color:blue%7Clabel:' + encodeURI(this.title) + '%7C' + encodeURI(this.address) + '&sensor=false';
        pl('#fulladdress').html(this.address || 'Unknown Address');
        pl('#addresslink').attr({href: this.addressurl});
        pl('#mapimg').attr({src: this.mapurl});
    },
    displayDocumentLink: function(linkId, btnId, docId) {
        var url;
        if (docId) {
            url = '/file/download/' + docId;
            pl('#'+btnId).addClass('span-3 smallinputbutton').html('DOWNLOAD');
            pl('#'+linkId).attr({href: url});
            
        }
        else {
            pl('#'+btnId).addClass('span-3 doclinkmsg attention').html('NONE');
            pl('#'+linkId).attr({href: '#'}).addClass('nohover').bind({click: function() { return false; }});
        }
    },
    displayDocuments: function() {
        this.displayDocumentLink('presentationlink', 'presentationbtn', this.presentation_id);
        this.displayDocumentLink('businessplanlink', 'businessplanbtn', this.business_plan_id);
        this.displayDocumentLink('financialslink', 'financialsbtn', this.financials_id);
        pl('#documentwrapper').show();
    },
    displayFunding: function() {
        if (this.asked_fund) {
            pl('#suggested_amt').html(this.currency.format(this.suggested_amt));
            pl('#suggested_pct').html(this.suggested_pct);
            pl('#suggested_val').html(this.currency.format(this.suggested_val));
            pl('#closingmsg').html(this.closing_date && this.days_left >= 0 ? 'CLOSES ON ' + this.dateobj.format(this.closing_date) + ' (' + (this.days_left > 0 ? this.days_left + ' DAYS LEFT' : 'CLOSES TODAY!') + ')' : 'BIDDING CLOSED');
/*
            if (this.num_bids && this.num_bids > 0) {
                this.best_bid_pct = 10 + 5*Math.floor(8*Math.random()); // FIXME
                this.best_bid_amt = this.valuation ? Math.floor((this.best_bid_pct / 100) * this.valuation) : 1000 + 1000*Math.floor(99*Math.random());
                this.best_bid_val = this.valuation || (100 * this.best_bid_amt / this.best_bid_pct); // FIXME
                pl('#num_bids').html(this.num_bids);
                pl('#best_bid_amt').html(this.currency.format(this.best_bid_amt));
                pl('#best_bid_pct').html(this.best_bid_pct);
                pl('#best_bid_val').html(this.currency.format(this.best_bid_val));
                pl('#bidboxinfo').show();
            }
            else {
                pl('#bidboxstatus').html('NO BIDS').show();
            }
            pl('#bidboxtitle').show();
            pl('#bidbox').show();
*/
            pl('#suggestedinfo').show();
        }
        else {
            pl('#suggestedmsg').html('NOT SEEKING FUNDING').show();
        }
    },
    displaySocial: function() {
        if (this.preview) { // their iframe usage busts during preview
            pl('#socialsidewrapper').html('<p>Twitter, Facebook, and Google Plus buttons will be displayed here</p>');
        }
        else {
            this.displayTwitter();
            this.displayFacebook();
            this.displayGooglePlus();
        }
        pl('#socialsidewrapper').show();
    },
    displayTwitter: function() {
        pl('#twitterbanner').html('<a href="https://twitter.com/share" class="twitter-share-button" data-url="' + this.listing_url + '" data-text="' + this.listing_public_title + '" data-via="startupbidder" data-related="startupbidder" data-hashtags="startupbidder">Tweet</a>');
        !function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src="//platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");
    },
    displayFacebook: function() {
        this.addMetaTags('property', {
            'og:title': this.listing_public_title,
            'og:type': 'company',
            'og:url': this.listing_url,
            'og:image': 'https://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc4/41604_200032480112098_936268874_n.jpg', // FIXME
            'og:site_name': 'startupbidder',
            'fb:app_id': '3063944677997'
        });
        pl('#facebookbanner').html('<div class="fb-like" data-href="' + this.listing_url + '" data-send="false" data-width="290" data-show-faces="false" data-font="arial"></div>');
  (function(d, s, id) {
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) return;
  js = d.createElement(s); js.id = id;
  js.src = "//connect.facebook.net/en_US/all.js#xfbml=1";
  fjs.parentNode.insertBefore(js, fjs);
  }(document, 'script', 'facebook-jssdk'));
    },
    displayGooglePlus: function() {
        this.addMetaTags('itemprop', {
            'name': this.listing_public_title,
            'description': this.summary,
            'image': 'https://fbcdn-profile-a.akamaihd.net/hprofile-ak-snc4/41604_200032480112098_936268874_n.jpg'
        });
        pl('#gplusbanner').html('<g:plusone size="medium" annotation="inline" width="290" href="' + this.listing_url + '"></g:plusone>');
  (function() {
    var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true;
    po.src = 'https://apis.google.com/js/plusone.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(po, s);
  })();
    },
    addMetaTags: function(attributeName, metas) {
        var prop, content, metatag;
        for (prop in metas) {
            content = metas[prop];
            metatag = '<meta ' + attributeName + '="' + prop + '" content="' + content + '"/>';
            pl('head').append(metatag);
        }
    },
    displayWithdraw: function() {
        var withdrawable = this.status === 'active' && (this.loggedin_profile && this.loggedin_profile.profile_id === this.profile_id);
        if (withdrawable) {
            this.bindWithdrawButton();
        }
    },
    bindWithdrawButton: function() {
        var self = this;
        pl('#withdrawbox').show();
        pl('#withdrawbtn').bind({
            click: function() {
                var completeFunc = function() {
                        self.status = 'withdrawn';
                        pl('#withdrawmsg').addClass('successful').html('LISTING WITHDRAWN');
                        pl('#withdrawbtn, #withdrawcancelbtn').hide();
                        pl('#companystatus').html('Listing is ' + self.status).addClass('attention');
                        window.location.reload();
                    },
                    url = '/listing/withdraw/' + self.listing_id,
                    ajax = new AjaxClass(url, 'withdrawmsg', completeFunc);
                if (pl('#withdrawcancelbtn').css('display') === 'none') { // first call
                    pl('#withdrawmsg, #withdrawcancelbtn').show();
                }
                else {
                    ajax.setPost();
                    ajax.call();
                }
                return false;
            }
        });
        pl('#withdrawcancelbtn').bind({
            click: function() {
                pl('#withdrawmsg, #withdrawcancelbtn').hide();
                return false;
            }
        });
    },
    displayApprove: function() {
        var approvable = this.loggedin_profile && this.loggedin_profile.admin && (this.status === 'posted' || this.status === 'frozen');
        if (approvable) {
            this.bindApproveButton();
        }
    },
    bindApproveButton: function() {
        var self = this;
        pl('#approvebox').show();
        pl('#approvebtn').bind({
            click: function() {
                var completeFunc = function() {
                        window.location.reload();
                    },
                    url = '/listing/activate/' + self.listing_id;
                    ajax = new AjaxClass(url, 'approvemsg', completeFunc);
                if (pl('#approvecancelbtn').css('display') === 'none') { // first call
                    pl('#approvemsg, #approvecancelbtn').show();
                }
                else {
                    ajax.setPost();
                    ajax.call();
                }
                return false;
            }
        });
        pl('#approvecancelbtn').bind({
            click: function() {
                pl('#approvemsg, #approvecancelbtn').hide();
                return false;
            }
        });
    },
    displaySendback: function() {
        var sendbackable = this.loggedin_profile && this.loggedin_profile.admin && (this.status === 'posted' || this.status === 'frozen');
        if (sendbackable) {
            this.bindSendbackButton();
        }
    },
    bindSendbackButton: function() {
        var self = this;
        pl('#sendbackbox').show();
        pl('#sendbackbtn').bind({
            click: function() {
                var completeFunc = function() {
                        window.location.reload();
                    },
                    url = '/listing/send_back/' + self.listing_id;
                    ajax = new AjaxClass(url, 'sendbackmsg', completeFunc);
                if (pl('#sendbackcancelbtn').css('display') === 'none') { // first call
                    pl('#sendbackmsg, #sendbackcancelbtn').show();
                }
                else {
                    ajax.setPost();
                    ajax.call();
                }
                return false;
            }
        });
        pl('#sendbackcancelbtn').bind({
            click: function() {
                pl('#sendbackmsg, #sendbackcancelbtn').hide();
                return false;
            }
        });
    },
    displayFreeze: function() {
        var freezable = this.status === 'active' && this.loggedin_profile && this.loggedin_profile.admin;
        if (freezable) {
            this.bindFreezeButton();
        }
    },
    bindFreezeButton: function() {
        var self = this;
        pl('#freezebox').show();
        pl('#freezebtn').bind({
            click: function() {
                var completeFunc = function() {
                        window.location.reload();
                    },
                    url = '/listing/freeze/' + self.listing_id;
                    ajax = new AjaxClass(url, 'freezemsg', completeFunc);
                if (pl('#freezecancelbtn').css('display') === 'none') { // first call
                    pl('#freezemsg, #freezecancelbtn').show();
                }
                else {
                    ajax.setPost();
                    ajax.call();
                }
                return false;
            }
        });
        pl('#freezecancelbtn').bind({
            click: function() {
                pl('#freezemsg, #freezecancelbtn').hide();
                return false;
            }
        });
    },
    hideSelectedTabs: function() {
        var tabs = [],
            wrappers = [],
            tabsel = '',
            wrappersel = '';
        if (pl('#basicstab').hasClass('companynavselected')) {
            tabs.push('#basicstab');
            wrappers.push('#basicswrapper1');
            wrappers.push('#basicswrapper2');
        }
        if (pl('#bidstab').hasClass('companynavselected')) {
            tabs.push('#bidstab');
            wrappers.push('#bidswrapper');
        }
        if (pl('#commentstab').hasClass('companynavselected')) {
            tabs.push('#commentstab');
            wrappers.push('#commentswrapper');
        }
        if (pl('#messagestab').hasClass('companynavselected')) {
            tabs.push('#messagestab');
            wrappers.push('#messageswrapper');
        }
        tabsel = tabs.join(', ');
        wrappersel = wrappers.join(', ');
        pl(tabsel).removeClass('companynavselected');
        pl(wrappersel).hide();
    },
    displayTab: function(tab) {
        var self = this,
            tabid = tab + 'tab',
            tabsel = '#' + tabid,
            wrapperid = tab + 'wrapper',
            wrappersel = '#' + wrapperid;
        if (!pl(tabsel).hasClass('companynavselected')) {
            self.hideSelectedTabs();
            pl(tabsel).addClass('companynavselected');
            if (tab === 'basics') {
                pl('#basicswrapper1, #basicswrapper2').show();
            }
            else if (tab === 'comments') {
                pl(wrappersel).show();
                comments = new CommentsClass(self.id);
                comments.load();
            }
            else if (tab === 'bids') {
                pl(wrappersel).show();
                pl('#bidsmsg').text('No bids');
            }
            else if (tab === 'messages') {
                pl(wrappersel).show();
                pl('#messagesmsg').text('No messages');
            }
            else {
                pl(wrappersel).show();
            }
        }
    },
    displayTabs: function() {
        var self = this,
            qs = new QueryStringClass();
        pl('#num_comments').text(self.num_comments || 0);
        pl('#num_bids').text(self.num_bids || 0);
        pl('#num_messages').text(self.num_messages || 0);
        if (this.loggedin_profile) {
            pl('#sendmessagelink').bind({
                click: function() {
                    self.displayTab('messages');
                    return false;
                }
            }).css({display: 'inline'});
            pl('#makebidtitle,#makebidbox,#addmessagetitle,#addmessagebox,#messagestab').show();
        }
        pl('#basicstab').bind({
            click: function() {
                self.displayTab('basics');
                return false;
            }
        });
        pl('#commentstab').bind({
            click: function() {
                self.displayTab('comments');
                return false;
            }
        });
        pl('#bidstab').bind({
            click: function() {
                self.displayTab('bids');
                return false;
            }
        });
        pl('#messagestab').bind({
            click: function() {
                self.displayTab('messages');
                return false;
            }
        });
        if (qs.vars.page === 'comments') {
            self.displayTab('comments');
        }
        else if (qs.vars.page === 'bids') {
            self.displayTab('bids');
        }
        else if (qs.vars.page === 'messages') {
            self.displayTab('messages');
        }
    }
});

