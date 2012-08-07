function DiscoverPageClass() {}
pl.implement(DiscoverPageClass,{
    loadPage: function() {
        var completeFunc = function(json) {
                var header = new HeaderClass(),
                    editedListing = new CompanyListClass({ propertykey: 'edited_listing', propertyissingle: true, companydiv: 'edited_listing', fullWidth: true }),
                    usersListings = new CompanyListClass({ propertykey: 'users_listings', companydiv: 'users_listings', seeall: '/profile-listing-page.html?type=active', fullWidth: true }),
                    monitoredListings = new CompanyListClass({ propertykey: 'monitored_listings', companydiv: 'monitored_listings', seeall: '/profile-listing-page.html?type=monitored', fullWidth: true }),
                    topListings = new CompanyListClass({ propertykey: 'top_listings', companydiv: 'top_listings', seeall: '/main-page.html?type=top', exponential: true, fullWidth: true }),
                    latestListings = new CompanyListClass({ propertykey: 'latest_listings', companydiv: 'latest_listings', seeall: '/main-page.html?type=latest', fullWidth: true }),
                    categories = json.categories || {},
                    locations = json.top_locations || {},
                    categoryList = new BaseListClass(categories, 'category', 1, 'category'),
                    locationList = new BaseListClass(locations, 'location', 1, 'location');
                header.setLogin(json);
                pl('.preloader').hide();
                pl('.wrapper').show();
                categoryList.display();
                locationList.display();
                if (json.edited_listing) {
                    pl('#edited_listing_wrapper').show();
                    editedListing.storeList(json);
                }
                if (json.users_listings && json.users_listings.length > 0) {
                    pl('#users_listings_wrapper').show();
                    usersListings.storeList(json);
                }
                if (json.monitored_listings && json.monitored_listings.length > 0) {
                   pl('#monitored_listings_wrapper').show();
                    monitoredListings.storeList(json);
                }
                topListings.storeList(json);
                latestListings.storeList(json);
            },
            ajax = new AjaxClass('/listings/discover/', 'top_listings', completeFunc);
        ajax.call();
    }
});

(new DiscoverPageClass()).loadPage();
