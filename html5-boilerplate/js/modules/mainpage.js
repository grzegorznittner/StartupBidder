function ListClass() {}
pl.implement(ListClass, {
    ucFirst: function(str) {
        return str.length > 1 ? str.substr(0,1).toUpperCase() + str.substr(1) : str.toUpperCase();
    },
    spreadOverTwoCols: function(list, divcol1, divcol2) {
        var htmlCol1, htmlCol2, mid, i, item;
        mid = Math.floor(list.length / 2);
        htmlCol1 = '';
        for (i = 0; i < mid; i++) {
            item = this.ucFirst(list[i]);
            htmlCol1 += '<li>'+item+'</li>';
        }
        pl(divcol1).html(htmlCol1);
        htmlCol2 = '';
        for (i = mid; i < list.length; i++) {
            item = this.ucFirst(list[i]);
            htmlCol2 += '<li>'+item+'</li>';
        }
        pl(divcol2).html(htmlCol2);
    }
});

function CategoryListClass() {}
pl.implement(CategoryListClass, {
    storeList: function(json) {
        var categories, lc;
        categories = json && json.categories && json.categories.length > 0
            ? json.categories
            : ['biotech','chemical','electronics','energy','environmental','financial','hardware','healthcare','industrial',
                'internet','manufacturing','media','medical','pharma','retail','software','telecom','other'];
        if (!categories) {
            pl('#categorydivcol1').html('<li class="notice">No categories found</li>');
            pl('#categorydivcol2').html('');
            return;
        }
        categories.sort();
        lc = new ListClass();
        lc.spreadOverTwoCols(categories, '#categorydivcol1', '#categorydivcol2');
    }
});

function LocationListClass() {}
pl.implement(LocationListClass, {
    storeList: function(json) {
        var locations, lc;
        locations = json && json.locations && json.locations.length > 0
            ? json.locations
            : ['Austin, USA', 'Bangalore, India', 'Beijing, China', 'Berlin, Germany', 'Cambridge, USA', 'Dusseldorf, Germany', 'Herzliya, Israel',
                'Katowice, Poland', 'London, UK', 'Menlo Park, USA', 'Mountain View, CA', 'New York City, USA', 'Seattle, WA', 'Cambridge, UK'];
        if (!locations) {
            pl('#locationdivcol1').html('<li class="notice">No locations found</li>');
            pl('#locationdivcol2').html('');
            return;
        }
        locations.sort();
        lc = new ListClass();
        lc.spreadOverTwoCols(locations, '#locationdivcol1', '#locationdivcol2');
    }
});

function MainPageClass() {};
pl.implement(MainPageClass,{
    loadPage: function() {
        var completeFunc, basePage;
        completeFunc = function(json) {
            var header, companyList, categoryList, locationList;
            header = new HeaderClass();
            companyList = new CompanyListClass();
            categoryList = new CategoryListClass();
            locationList = new LocationListClass();
            header.setLogin(json);
            companyList.storeList(json,4);
            categoryList.storeList(json);
            locationList.storeList(json);
        };
        basePage = new BaseCompanyListPageClass();
        basePage.loadPage(completeFunc);
    }
});

(new MainPageClass()).loadPage();
