function InformationPageClass() {}
pl.implement(InformationPageClass,{
    loadPage: function() {
        var completeFunc, basePage;
        completeFunc = function(json) {
            var header = new HeaderClass(),
                companyList = new CompanyListClass({ colsPerRow: 2});
            header.setLogin(json);
            companyList.storeList(json);
        };
        basePage = new BaseCompanyListPageClass();
        basePage.loadPage(completeFunc);
    }
});

(new InformationPageClass()).loadPage();
