function HomePageClass() { }

pl.implement(HomePageClass, {

    load: function() {
        var self = this,
            completeFunc = function(json) {
                (new HeaderClass()).setLogin(json);
                self.display(json);
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            ajax = new AjaxClass('/listing/discover', 'homepagemsg', completeFunc);
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
        if (this.hasListings()) {
            this.displayListings();
            if (this.loggedin_profile && this.loggedin_profile.edited_listing) {
                this.displayExistingListing();
            }
            pl('#haslistings').show();
        }
        else {
            pl('#nolistings').show();
        }
    },
   
    hasListings: function() {
        var has = false;
        if (this.loggedin_profile) {
            if (this.loggedin_profile.edited_listing) {
                has = true;
            }
            else if (this.users_listings && this.users_listings.length > 0) {
                has = true;
            }
            else if (this.monitored_listings && this.monitored_listings.length > 0) {
                has = true;
            }
        }
        return has;
    }, 

    displayListings: function() {
        var usersListings = new CompanyListClass({ propertykey: 'users_listings', companydiv: 'users_listings', seeall: '/profile-listing-page.html?type=active', fullWidth: true }),
            monitoredListings = new CompanyListClass({ propertykey: 'monitored_listings', companydiv: 'monitored_listings', seeall: '/profile-listing-page.html?type=monitored', fullWidth: true });
        if (this.users_listings && this.users_listings.length > 0) {
            pl('#users_listings_wrapper').show();
            usersListings.storeList(this);
        }
        if (this.monitored_listings && this.monitored_listings.length > 0) {
           pl('#monitored_listings_wrapper').show();
           monitoredListings.storeList(this);
        }
    },

    displayExistingListing: function() {
        var self = this;
        pl('#editlisting').bind('click', function() {
            var url = this.loggedin_profile && this.loggedin_profile.edited_status === 'new'
                ? '/new-listing-basics-page.html'
                : '/company-page.html?id=' + self.loggedin_profile.edited_listing;
            document.location = url;
        });
        pl('#existinglisting').show();
    }

});

(new HomePageClass()).load();
