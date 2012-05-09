function DiscoverPageClass() {}
pl.implement(DiscoverPageClass,{
    loadPage: function() {
        var completeFunc = function(json) {
                var header = new HeaderClass(),
                    searchbox = new SearchBoxClass(),
                    editedListing = new CompanyListClass({ propertykey: 'edited_listing', propertyissingle: true, companydiv: 'edited_listing', fullWidth: true }),
                    usersListings = new CompanyListClass({ propertykey: 'users_listings', companydiv: 'users_listings', seeall: '/profile-listing-page.html?type=active' }),
                    monitoredListings = new CompanyListClass({ propertykey: 'monitored_listings', companydiv: 'monitored_listings', seeall: '/profile-listing-page.html?type=monitored' }),
                    topListings = new CompanyListClass({ propertykey: 'top_listings', companydiv: 'top_listings', seeall: '/main-page.html?type=top' }),
                    closingListings = new CompanyListClass({ propertykey: 'closing_listings', companydiv: 'closing_listings', seeall: '/main-page.html?type=closing' }),
                    latestListings = new CompanyListClass({ propertykey: 'latest_listings', companydiv: 'latest_listings', seeall: '/main-page.html?type=latest' }),
                    categories = json.categories || {},
                    locations = json.top_locations || {},
                    categoryList = new BaseListClass(categories, 'category', 2, 'category'),
                    locationList = new BaseListClass(locations, 'location', 2, 'location');
        console.log('BAR');
                header.setLogin(json);
        console.log('BAZ');
                categoryList.display();
        console.log('BUZ');
                locationList.display();
        console.log('BUZ2');
                searchbox.bindEvents();
        console.log('BUZ3');
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
                closingListings.storeList(json);
                latestListings.storeList(json);
            },
            ajax = new AjaxClass('/listings/discover/', 'top_listings', completeFunc);
        ajax.call();
    }
});

(new DiscoverPageClass()).loadPage();
