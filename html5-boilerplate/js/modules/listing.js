function ListingClass() {
    var queryString = new QueryStringClass();
    this.id = queryString.vars.id;
    this.listing_id = this.id;
    this.preview = queryString.vars.preview;
    this.imagepanel = new ImagePanelClass();
};
pl.implement(ListingClass, {
    store: function(json) {
        var key;
        if (json && json.listing && json.listing.listing_id) {
            for (key in json.listing) {
                this[key] = json.listing[key];
            }
        }
        this.loggedin_profile = json && json.loggedin_profile;
        this.loggedin_profile_id = this.loggedin_profile && this.loggedin_profile.profile_id;
        this.listing_url = 'http://startupbidder.com/company-page.html?id=' + this.listing_id;
        this.listing_public_title = 'Startupbidder Listing: ' + this.title;
        if (this.preview) {
            pl('#header').hide();
            pl('#footer').hide();
        }
     },

    load: function() {
        var self = this,
            complete = function(json) {
                var header = new HeaderClass(),
                    companybanner = new CompanyBannerClass('basics');
                if (self.preview) {
                    pl('#header').hide();
                    pl('#footer').hide();
                }
                else {
                    header.setLogin(json);
                }
                companybanner.display(json);
                self.display(json);
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            ajax = new AjaxClass('/listings/get/' + this.listing_id, 'listingstatus', complete);
        ajax.call();
    },

    display: function(json) {
        if (json) {
            this.store(json);
        }
        this.displayBasics();
        this.displayEdit();
        this.displayInvest();
        this.displayGoto();
        this.displayMap();
        this.displayDocuments();
        this.displayFunding();
        this.displaySocial();
        this.displayWithdraw();
        this.displayApprove();
        this.displaySendback();
        this.displayFreeze();
        if (this.preview) {
            pl('#previewoverlay').show();
        }
        pl('#basicswrapper').show();
    },

    displayBasics: function() {
        this.displayPics();
        this.displayVideo();
        pl('#summary').text(this.summary || 'Listing summary goes here');
    },

    displayPics: function() {
        this.imagepanel.setListing(this).display();
    },

    displayVideo: function() {
        if (this.video) {
            pl('#videopresentation').attr({src: this.video});
            pl('#videowrapper').show();
        }
    },

    displayEdit: function() {
        var self = this;
        if (self.loggedin_profile && self.loggedin_profile.profile_id === self.profile_id && (self.status === 'new' || self.status === 'posted')) { // owner
            pl('#editbutton').show();
        }
    },

    displayInvest: function() {
        var self = this;
        if (self.status === 'new' || self.status === 'posted') {
            pl('#investbutton').hide();
            return;
        }
        if (self.loggedin_profile && self.loggedin_profile.profile_id === self.profile_id) { // owner
            pl('#investbutton').text('INVESTMENTS');
        }
        pl('#investbutton').bind('click', function() {
            var page, url;
            if (self.loggedin_profile) {
                if (self.loggedin_profile.profile_id === self.profile_id) { // owner
                    pl('#investbutton').text('INVESTMENTS');
                    page = 'company-owner-bids-page.html';
                }
                else { // logged in user
                    page = 'company-investor-bids-page.html';
                }
            }
            else {
                page = 'company-bids-page.html';
            }
            url = '/' + page + '?id=' + self.listing_id;
            document.location = url;
        });
    },

    displayGoto: function() {
        var bmcurl = '/company-model-page.html?id=' + this.listing_id,
            presurl = '/company-slides-page.html?id=' + this.listing_id;
        pl('#gotobusinessmodellink').attr({href: bmcurl});
        pl('#gotopresentationlink').attr({href: presurl});
    },

    displayMap: function() {
        this.address = this.address || 'Unknown Address';
        //this.addressurl = this.addressurl || 'http://nominatim.openstreetmap.org/search?q=' + encodeURIComponent(this.address);
        this.addressurl = 'http://maps.google.com/maps?q='+this.latitude+','+this.longitude+'+('+encodeURIComponent(this.title)+', '+encodeURIComponent(this.address)+')';
        this.latitude = this.latitude || '51.499117116569';
        this.longitude = this.longitude || '-0.12359619140625';
        //this.mapurl = 'http://ojw.dev.openstreetmap.org/StaticMap/?lat=' + this.latitude + '&lon=' + this.longitude + '&z=5&show=1&fmt=png&w=302&h=302&att=none';
        this.mapurl = 'http://maps.googleapis.com/maps/api/staticmap?center=' + this.latitude + ',' + this.longitude + '&zoom=7&size=320x319&maptype=roadmap&markers=color:blue%7Clabel:' + encodeURI(this.title) + '%7C' + encodeURI(this.address) + '&sensor=false';
        pl('#fulladdress').html(this.address || 'Unknown Address');
        pl('#addresslink').attr({href: this.addressurl});
        pl('#mapimg').attr({src: this.mapurl});
    },

    displayDocumentLink: function(type, docId) {
        var linkId = type + 'link',
            wrapperId = type + 'wrapper',
            url;
        if (docId) {
            url = '/file/download/' + docId;
            pl('#'+linkId).attr({href: url});
            pl('#'+wrapperId).show();
        }
    },

    displayDocuments: function() {
        if (this.presentation_id || this.business_plan_id || this.financials_id) {
            this.displayDocumentLink('presentation', this.presentation_id);
            this.displayDocumentLink('businessplan', this.business_plan_id);
            this.displayDocumentLink('financials', this.financials_id);
            pl('#documentboxwrapper').show();
        }
    },

    displayFunding: function() {
//        var total_raised = this.total_raised && this.total_raised > 0 ? CurrencyClass.prototype.format(this.total_raised) : '$0';
        if (this.asked_fund) {
            pl('#suggested_amt').text('$ ' + CurrencyClass.prototype.formatNoSymbol(this.suggested_amt));
            pl('#suggested_pct').text(this.suggested_pct);
            pl('#suggested_val').text(CurrencyClass.prototype.format(this.suggested_val));
//            pl('#total_raised').text(total_raised);
/*
            if (this.num_bids && this.num_bids > 0) {
                this.best_bid_pct = 10 + 5*Math.floor(8*Math.random()); // FIXME
                this.best_bid_amt = this.valuation ? Math.floor((this.best_bid_pct / 100) * this.valuation) : 1000 + 1000*Math.floor(99*Math.random());
                this.best_bid_val = this.valuation || (100 * this.best_bid_amt / this.best_bid_pct); // FIXME
                pl('#num_bids').html(this.num_bids);
                pl('#best_bid_amt').html(CurrencyClass.prototype.format(this.best_bid_amt));
                pl('#best_bid_pct').html(this.best_bid_pct);
                pl('#best_bid_val').html(CurrencyClass.prototype.format(this.best_bid_val));
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
            pl('#socialsidebox').html('<p>Twitter, Facebook, and Google Plus buttons will be displayed here</p>');
        }
        else {
            this.displayTwitter();
            this.displayFacebook();
            this.displayGooglePlus();
        }
    },

    displayTwitter: function() {
        var twitterurl = 'https://twitter.com/share?url=' + encodeURIComponent(this.listing_url) + '&text=' + encodeURIComponent(this.listing_public_title);
        pl('#twitterbanner a').attr({
            href: twitterurl,
            'data-url': this.listing_url,
            'data-text': this.listing_public_title
        });
        !function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src="//platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");
    },

    displayFacebook: function() {
        this.addMetaTags('property', {
            'og:title': this.listing_public_title,
            'og:type': 'company',
            'og:url': this.listing_url,
            'og:image': 'http://startupbidder.com/listing/logo/' + this.listing_id,
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
            'og:image': 'http://startupbidder.com/listing/logo/' + this.listing_id
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
                var complete = function() {
                        pl('#withdrawbtn, #withdrawcancelbtn').hide();
                        pl('#withdrawmsg').text('Listing withdrawn, reloading...').show();
                        setTimeout(function() {
                            window.location.reload();
                        }, 3000);
                    },

                    url = '/listing/withdraw/' + self.listing_id,
                    ajax = new AjaxClass(url, 'withdrawmsg', complete);
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
                var complete = function() {
                        pl('#approvebtn, #approvecancelbtn').hide();
                        pl('#approvemsg').text('Listing approved, reloading...').show();
                        setTimeout(function() {
                            window.location.reload();
                        }, 3000);
                    },

                    url = '/listing/activate/' + self.listing_id;
                    ajax = new AjaxClass(url, 'approvemsg', complete);
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
                var complete = function() {
                        pl('#sendbackbtn, #sendbackcancelbtn').hide();
                        pl('#sendbackmsg').text('Listing sent back, reloading...').show();
                        setTimeout(function() {
                            window.location.reload();
                        }, 3000);
                    },
                    data = {
                        listing: {
                            id: self.listing_id,
                            message: pl('#sendbacktext').attr('value')
                        }
                    },
                    url = '/listing/send_back/' + self.listing_id,
                    ajax = new AjaxClass(url, 'sendbackmsg', complete);
                if (pl('#sendbackmsg').text() && pl('#sendbackmsg').text().indexOf('Error') !== -1) {
                    pl('#sendbackbtn').hide();
                }
                else if (pl('#sendbackcancelbtn').css('display') === 'none') { // first call
                    pl('#sendbackmsg').text('Are you sure?').show();
                    pl('#sendbackcancelbtn').show();
                    pl('#sendbacktext').attr({disabled: 'disabled'});
                }
                else {
                    ajax.setPostData(data);
                    ajax.call();
                }
                return false;
            }
        });
        pl('#sendbackcancelbtn').bind({
            click: function() {
                pl('#sendbackcancelbtn').hide();
                pl('#sendbackmsg').text('').show();
                pl('#sendbackbtn').show();
                pl('#sendbacktext').removeAttr('disabled');
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
                var complete = function() {
                        pl('#freezebtn, #freezecancelbtn').hide();
                        pl('#freezemsg').text('Listing frozen, reloading...').show();
                        setTimeout(function() {
                            window.location.reload();
                        }, 3000);
                    },
                    data = {
                        listing: {
                            id: self.listing_id,
                            message: pl('#freezetext').attr('value')
                        }
                    },
                    url = '/listing/freeze/' + self.listing_id,
                    ajax = new AjaxClass(url, 'freezemsg', complete);
                if (pl('#freezemsg').text() && pl('#freezemsg').text().indexOf('Error') !== -1) {
                    pl('#freezebtn').hide();
                }
                else if (pl('#freezecancelbtn').css('display') === 'none') { // first call
                    pl('#freezemsg').text('Are you sure?').show();
                    pl('#freezecancelbtn').show();
                    pl('#freezetext').attr({disabled: 'disabled'});
                }
                else {
                    ajax.setPostData(data);
                    ajax.call();
                }
                return false;
            }
        });
        pl('#freezecancelbtn').bind({
            click: function() {
                pl('#freezecancelbtn').hide();
                pl('#freezemsg').text('').show();
                pl('#freezebtn').show();
                pl('#freezetext').removeAttr('disabled');
                return false;
            }
        });
    }

});
(new ListingClass()).load();

