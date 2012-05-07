function ProfileListingPageClass() {
    this.json = {};
    this.queryString = new QueryStringClass();
    this.type = this.queryString.vars.type || 'active';
    this.data = { max_results: 20 };
    this.urlmap = {
        active: '/listings/user/active',
        monitored: '/listings/user/monitored',
        closed: '/listings/user/closed',
        withdrawn: '/listings/user/withdrawn',
        frozen: '/listings/user/frozen',
        admin_posted: '/listings/posted',
        admin_frozen: '/listings/frozen'
    };
    this.url = this.urlmap[this.type] || this.urlmap['active'];
    this.isadmin = this.type === 'admin_posted' || this.type === 'admin_frozen';
    this.title = (this.isadmin ? 'YOUR ' : '') + this.type.toUpperCase() + ' LISTINGS';
};
pl.implement(ProfileListingPageClass,{
    loadPage: function() {
        var self = this,
            completeFunc = function(json) {
                var header = new HeaderClass(),
                    profile = new ProfileClass(),
                    notifyList = new NotifyListClass(),
                    companyList = new CompanyListClass();
                self.json = json;
                header.setLogin(json);
                if (!json.loggedin_profile) { // must be logged in for this page
                    window.location = '/';
                }
                profile.setProfile(json.loggedin_profile);
                pl('#listingstitle').text(self.title);
                notifyList.display(json);
                companyList.storeList(json);
                pl('#editprofilebutton').show();
             },
             ajax = new AjaxClass(self.url, 'companydiv', completeFunc);
        ajax.setGetData(this.data);
        ajax.call();
    }
});

(new ProfileListingPageClass()).loadPage();
