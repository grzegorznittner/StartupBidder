function InformationPageClass() {}
pl.implement(InformationPageClass,{
    loadPage: function() {
        var completeFunc, basePage;
        completeFunc = function(json) {
            var header, companyList;
            header = new HeaderClass();
            companyList = new CompanyListClass();
            header.setLogin(json);
            companyList.storeList(json,2);
        };
        basePage = new BaseCompanyListPageClass();
        basePage.loadPage(completeFunc);
    }
});

(new InformationPageClass()).loadPage();
