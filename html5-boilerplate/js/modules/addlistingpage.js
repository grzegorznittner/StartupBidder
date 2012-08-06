function AddListingClass() { }

pl.implement(AddListingClass, {

    load: function() {
        var self = this,
            completeFunc = function(json) {
                var header = new HeaderClass();
                header.setLogin(json);
                self.display(json);
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            // ajax = new AjaxClass('/user/loggedin', 'addlistingmsg', completeFunc); // once greg fixes
            ajax = new AjaxClass('/listing/discover', 'addlistingmsg', completeFunc);
        ajax.call();
    },

    store: function(json) {
        if (json) {
            CollectionsClass.prototype.merge(this, json);
        }
    },
   
    display: function(json) {
        if (json) {
            this.store(json);
        }
        if (!this.loggedin_profile) {
            this.displayLoggedOut();
        }
        else if (this.loggedin_profile && this.loggedin_profile.edited_listing) {
            this.displayExistingListing();
        }
        else {
            this.displayNewListing();
        }
    },

    displayLoggedOut: function() {
        var nexturl = '/add-listing-page.html',
            login_url = this.login_url,
            twitter_login_url = this.twitter_login_url,
            fb_login_url = this.fb_login_url;
        if (login_url) {
            pl('#google_login').attr({href: login_url + encodeURIComponent(nexturl)});
        } else {
            pl('#google_login').hide();
        }
        if (twitter_login_url) {
            pl('#twitter_login').attr({href: twitter_login_url + '?url=' + encodeURIComponent(nexturl)}).show();
        } else {
            pl('#twitter_login').hide();
        }
        if (fb_login_url) {
            pl('#fb_login').attr({href: fb_login_url + '?url=' + encodeURIComponent(nexturl)}).show();
        } else {
            pl('#fb_login').hide();
        }
        pl('#notloggedin').show();
    },

    displayExistingListing: function() {
        var self = this;
        pl('#editlisting').bind('click', function() {
            var url = this.loggedin_profile && this.loggedin_profile.edited_status === 'new'
                ? '/new-listing-basics-page.html'
                : '/company-page.html?id=' + self.loggedin_profile.edited_listing;
            document.location = url;
        });
        pl('#deletebtn').bind('click', function() {
            var complete = function() {
                    pl('#deletebtn, #deletecancelbtn').hide();
                    pl('#deletemsg').text('Listing deleted, reloading...').show();
                    setTimeout(function() {
                        window.location = '/add-listing-page.html';
                    }, 2000);
                },
                url = '/listing/delete',
                ajax = new AjaxClass(url, 'deletemsg', complete);
            if (pl('#deletecancelbtn').css('display') === 'none') { // first call
                pl('#deletemsg, #deletecancelbtn').show();
            }
            else {
                ajax.setPost();
                ajax.call();
            }
            return false;
        });
        pl('#deletecancelbtn').bind('click', function() {
            pl('#deletemsg, #deletecancelbtn').hide();
            return false;
        });
        pl('#existinglisting').show();
    },

    displayNewListing: function() {
        pl('#newlisting').show();
    }

});

(new AddListingClass()).load();

