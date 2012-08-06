function LoginClass() {
    var queryString = new QueryStringClass();
    this.url = queryString.vars.url || '/';
 }

pl.implement(LoginClass, {

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
        if (this.loggedin_profile) {
            document.location = '/';
        }
        else {
            this.displayLoggedOut();
        }
    },

    displayLoggedOut: function() {
        var nexturl = this.url,
            login_url = this.login_url,
            twitter_login_url = this.twitter_login_url,
            fb_login_url = this.fb_login_url;
        if (login_url) {
            pl('#google_login').attr({href: login_url + nexturl});
        } else {
            pl('#google_login').hide();
        }
        if (twitter_login_url) {
            pl('#twitter_login').attr({href: twitter_login_url + '?url=' + nexturl}).show();
        } else {
            pl('#twitter_login').hide();
        }
        if (fb_login_url) {
            pl('#fb_login').attr({href: fb_login_url + '?url=' + nexturl}).show();
        } else {
            pl('#fb_login').hide();
        }
    }

});

(new LoginClass()).load();

