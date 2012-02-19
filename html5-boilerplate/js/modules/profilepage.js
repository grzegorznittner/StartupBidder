function ProfilePageClass() {};
pl.implement(ProfilePageClass,{
    loadPage: function() {
        var completeFunc, ajax;
        completeFunc = function(json) {
            var header, profile, notifyList, companyList, testCompanies;
            header = new HeaderClass();
            profile = new ProfileClass();
            notifyList = new NotifyListClass();
            companyList = new CompanyListClass();
            testCompanies = new TestCompaniesClass(); // FIXME
            if (!json.posted) { // FIXME
                json.posted = testCompanies.testJson()
            }
            if (!json.bidon) { // FIXME
                json.bidon = testCompanies.testJson()
            }
            if (!json.upvoted) { // FIXME
                json.upvoted = testCompanies.testJson()
            }
            if (!json.notifications || !json.notifications.length > 0) {
                json.notifications = testCompanies.testNotifications();
            }
            header.setLogin(json);
            profile.setProfile(json);
            notifyList.storeList(json);
            companyList.storeList(json, 4, 'posteddiv', 'posted');
            companyList.storeList(json, 4, 'bidondiv', 'bidon');
            companyList.storeList(json, 4, 'upvoteddiv', 'upvoted');
        };
        ajax = new AjaxClass('/user/loggedin', 'profilestatus', completeFunc);
        ajax.call();
    }
});

(new ProfilePageClass()).loadPage();
