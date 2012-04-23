function NotificationPageClass() {
    this.json = {};
};
pl.implement(NotificationPageClass,{
    loadPage: function() {
        var self = this,
            completeFunc = function(json) {
                var header = new HeaderClass(),
                    profile = new ProfileClass(),
                    notifyList = new NotifyListClass();
                header.setLogin(json);
                if (!json.loggedin_profile) { // must be logged in for this page
                    window.location = '/';
                }
                profile.setProfile(json.loggedin_profile);
                notifyList.display(json);
             },
            ajax = new AjaxClass('/listings/discover_user/', 'profilemsg', completeFunc);
        ajax.call();
    }
});

(new NotificationPageClass()).loadPage();
