function NotificationPageClass() {
    this.json = {};
};
pl.implement(NotificationPageClass,{
    loadPage: function() {
        var completeFunc = function(json) {
                var header = new HeaderClass(),
                    notifyList = new NotifyListClass();
                header.setLogin(json);
                if (!json.loggedin_profile) { // must be logged in for this page
                    window.location = '/';
                }
                notifyList.display(json);
             },
            ajax = new AjaxClass('/notification/user', 'notificationsmsg', completeFunc);
            ajax.setGetData({ max_results: 10 });
            ajax.call();
    }
});

(new NotificationPageClass()).loadPage();
