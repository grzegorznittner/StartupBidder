function DiscoverPageClass() {}
pl.implement(DiscoverPageClass,{
    loadPage: function() {
        var completeFunc = function(json) {
                var header = new HeaderClass(),
                    searchbox = new SearchBoxClass(),
                    editedListing = new CompanyListClass({ propertykey: 'edited_listing', propertyissingle: true, listingsdiv: 'edited_listing', fullWidth: true }),
                    usersListings = new CompanyListClass({ propertykey: 'users_listings', listingsdiv: 'users_listings', showmore: '/main-page.html?type=useractive' }),
                    topListings = new CompanyListClass({ propertykey: 'top_listings', listingsdiv: 'top_listings', showmore: '/main-page.html?type=top' }),
                    closingListings = new CompanyListClass({ propertykey: 'closing_listings', listingsdiv: 'closing_listings', showmore: '/main-page.html?type=closing' }),
                    latestListings = new CompanyListClass({ propertykey: 'latest_listings', listingsdiv: 'latest_listings', showmore: '/main-page.html?type=latest' }),
                    categories = json.categories || {},
                    locations = json.top_locations || {},
                    categoryList = new BaseListClass(categories, 'category', 2, 'category'),
                    locationList = new BaseListClass(locations, 'location', 2, 'location');
                header.setLogin(json);
                categoryList.display();
                locationList.display();
                searchbox.bindEvents();
                if (json.edited_listing) {
                    pl('#edited_listing_wrapper').show();
                    editedListing.storeList(json,1);
                }
                if (json.users_listings && json.users_listings.length > 0) {
                    pl('#users_listings_wrapper').show();
                    usersListings.storeList(json);
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
