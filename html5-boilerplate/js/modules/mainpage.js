function MainPageClass() {}
pl.implement(MainPageClass,{
    loadPage: function() {
        var completeFunc = function(json) {
                var header = new HeaderClass(),
                    searchbox = new SearchBoxClass(),
                    companyList = new CompanyListClass(),
                    categories = json.categories || {},
                    locations = json.top_locations || {},
                    categoryList = new BaseListClass(categories, 'category', 2, 'category'),
                    locationList = new BaseListClass(locations, 'location', 1, 'location');
                header.setLogin(json);
                categoryList.display();
                locationList.display();
                searchbox.bindEvents();
                companyList.storeList(json);
            },
            basePage = new BaseCompanyListPageClass();
        basePage.loadPage(completeFunc);
    }
});

(new MainPageClass()).loadPage();
