function InformationPageClass() {}
pl.implement(InformationPageClass,{
    loadPage: function() {
        var completeFunc = function(json) {
                var header = new HeaderClass(),
                    
                    companyList = new CompanyListClass({ colsPerRow: 2});
                header.setLogin(json);
                companyList.storeList(json);
            },
            maxResults = pl('body').hasClass('about-page') ? 12 : ( pl('body').hasClass('help-page') ? 8 : 20),
            basePage = new BaseCompanyListPageClass({ max_results: maxResults });
        basePage.loadPage(completeFunc);
    }
});

(new InformationPageClass()).loadPage();
