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
        this.testcompanies = new TestCompaniesClass();
        this.testcompany = this.testcompanies.allCompanies[0]; // FIXME
    },
    load: function() {
        this.ajax.call();
    },
    display: function() {
        this.displayBasics();
        this.displayQA();
        this.displayMessage();
        this.displayInfobox();
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
        var logobg = this.logo ? 'url(' + this.logo + ') no-repeat scroll left top' : null;
        if (logobg) {
            pl('#companylogo').removeClass('noimage').css({background: logobg});
        }
        pl('#title').html(this.title);
        pl('title').html('Startupbidder Listing: ' + this.title);
        pl('#profile_username').html(this.profile_username || (this.loggedin_profile ? 'You' : 'Anonymous'));
        pl('#mantra').html(this.mantra);
        pl('#companystatus').html('Listing is ' + this.status);
        if (this.status === 'withdrawn') {
            pl('#companystatus').addClass('attention');
        }
        pl('#num_votes').html(this.num_votes);
        pl('#num_comments').html(this.num_comments);
        pl('#videopresentation').attr({src: this.video});
        pl('#summary').html(this.summary);
        pl('#listingdata').show();
    },
    displayQA: function() { // FIXME
    },
    displayMessage: function() {
        if (this.loggedin_profile) {
            message = new MessageClass(this.id, this.loggedin_profile.profile_id);
            message.display();
        }
    },
    displayInfobox: function() {
        var url = this.website ? new URLClass(this.website) : null;
        pl('#category').html(this.category || 'Other');
        pl('#listing_date').html(this.listing_date ? this.dateobj.format(this.listing_date) : 'Not yet listed');
        pl('#websitelink').attr({href: this.website});
        if (url) {
            pl('#domainname').html(url.getHostname());
        }
    },
    displayMap: function() {
        this.address = this.brief_address || 'Unknown Address';
        //this.addressurl = this.addressurl || 'http://nominatim.openstreetmap.org/search?q=' + encodeURIComponent(this.address);
        this.addressurl = 'http://maps.google.com/maps?output=embed&q=' + encodeURI(this.title) + ',' + encodeURI(this.address);
        this.latitude = this.latitude || '51.499117116569';
        this.longitude = this.longitude || '-0.12359619140625';
        //this.mapurl = 'http://ojw.dev.openstreetmap.org/StaticMap/?lat=' + this.latitude + '&lon=' + this.longitude + '&z=5&show=1&fmt=png&w=302&h=302&att=none';
        this.mapurl = 'http://maps.googleapis.com/maps/api/staticmap?center=' + this.latitude + ',' + this.longitude + '&zoom=7&size=302x298&maptype=roadmap&markers=color:blue%7Clabel:' + encodeURI(this.title) + '%7C' + encodeURI(this.address) + '&sensor=false';
        pl('#address').html(this.address);
        pl('#addresslink').attr({href: this.addressurl});
        pl('#mapimg').attr({src: this.mapurl});
    },
    displayDocumentLink: function(linkId, btnId, docId) {
        var url;
        if (!docId && Math.random() > 0.5) { // FIXME: simulation
            docId = 'ag1zdGFydHVwYmlkZGVych4LEgpMaXN0aW5nRG9jIg5Eb2MtMjExMzY3MzkxOAw';
        }
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
        pl('#documentbox').show();
    },
    displayFunding: function() {
        this.askingFunding = this.askingFunding || (this.suggested_amt > 0 ? true : false);
        if (this.askingFunding) {
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
        this.displayFacebook();
        this.displayGooglePlus();
        this.displayTwitter();
    },
    displayFacebook: function() {
        this.addFacebookMetaTags();
        (function(d, s, id) {
        var js, fjs = d.getElementsByTagName(s)[0];
        if (d.getElementById(id)) return;
        js = d.createElement(s); js.id = id;
        js.src = "//connect.facebook.net/en_US/all.js#xfbml=1";
        fjs.parentNode.insertBefore(js, fjs);
        }(document, 'script', 'facebook-jssdk'));
    },
    addFacebookMetaTags: function() {
        var metas, prop, content, metatag;
        metas = {
            'og:title': this.title,
            'og:type': 'company',
            'og:url': 'http://starutpbidder.com/company_page.html?id=' + this.id,
            //'og:image': 'http://startupbidder.com' + this.logourl, // FIXME
            'og:site_name': 'startupbidder',
            'fb:app_id': '3063944677997'
        };
        for (prop in metas) {
            content = metas[prop];
            metatag = '<meta property="' + prop + '" content="' + content + '"/>';
            pl('head').append(metatag);
        }
    },
    displayGooglePlus: function() {
        (function() {
          var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true;
          po.src = 'https://apis.google.com/js/plusone.js';
          var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(po, s);
        })();
    } ,
    displayTwitter: function() {
        !function(d,s,id){
            var js,fjs=d.getElementsByTagName(s)[0];
            if(!d.getElementById(id)){
                js=d.createElement(s);
                js.id=id;
                js.src="//platform.twitter.com/widgets.js";
                fjs.parentNode.insertBefore(js,fjs);
            }
        }(document,"script","twitter-wjs");
    },
    displayWithdraw: function() {
        var withdrawable = (this.status === 'active' && (this.loggedin_profile && this.loggedin_profile.profile_id === this.profile_id));
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
        var approvable = (this.status === 'posted' && this.loggedin_profile.admin);
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
        var sendbackable = ((this.status === 'posted' || this.status === 'frozen') && this.loggedin_profile.admin);
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
        var freezable = (this.status === 'active' && this.loggedin_profile.admin);
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
    }
});

