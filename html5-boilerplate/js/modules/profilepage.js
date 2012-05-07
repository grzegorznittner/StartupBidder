function ProfilePageClass() {
    this.json = {};
};
pl.implement(ProfilePageClass,{
    storeListings: function(propertykey, _options) {
        var self = this,
            options = _options || {},
            wrappersel = '#' + propertykey + '_wrapper',
            listings = self.json[propertykey],
            listingfound = false,
            companylist;
        options.propertykey = propertykey;
        options.companydiv = propertykey;
        if (listings && (options.propertyissingle || listings.length > 0)) {
            pl(wrappersel).show();
            companylist = new CompanyListClass(options);
            companylist.storeList(self.json);
            listingfound = true;
        }
        return listingfound;
    },
    loadPage: function() {
        var self = this,
            completeFunc = function(json) {
                var header = new HeaderClass(),
                    profile = new ProfileClass(),
                    notifyList = new NotifyListClass(),
                    listprops = [ 'edited_listing',
                        'active_listings',
                        'admin_posted_listings',
                        'admin_frozen_listings',
                        'monitored_listings',
                        'closed_listings',
                        'withdrawn_listings',
                        'frozen_listings'
                    ],
                    options = {
                        edited_listing: { propertyissingle: true, fullWidth: true },
                        active_listings: { seeall: '/profile-listing-page.html?type=active' },
                        monitored_listings: { seeall: '/profile-listing-page.html?type=monitored' },
                        closed_listings: { seeall: '/profile-listing-page.html?type=closed' },
                        withdrawn_listings: { seeall: '/profile-listing-page.html?type=withdrawn' },
                        frozen_listings: { seeall: '/profile-listing-page.html?type=frozen' },
                        admin_posted_listings: { seeall: '/profile-listing-page.html?type=admin_posted' },
                        admin_frozen_listings: { seeall: '/profile-listing-page.html?type=admin_frozen' }
                    },
                    listingfound = false,
                    propertykey,
                    i;
                self.json = json;
                header.setLogin(json);
                if (!json.loggedin_profile) { // must be logged in for this page
                    window.location = '/';
                }
                profile.setProfile(json.loggedin_profile);
                notifyList.display(json);
                for (i = 0; i < listprops.length; i++) {
                    propertykey = listprops[i];
                    if (self.storeListings(propertykey, options[propertykey])) {
                        listingfound = true;
                    }
                }
                if (!listingfound) {
                    pl('#no_listings_wrapper').show();
                }
                pl('#editprofilebutton').show();
             },
            ajax = new AjaxClass('/listings/discover_user/', 'profilemsg', completeFunc);
        ajax.call();
    }
});

(new ProfilePageClass()).loadPage();
