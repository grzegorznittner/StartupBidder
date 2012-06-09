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
/*
        ajax.mock({ 
    "login_url": null,
    "logout_url": "/_ah/logout?continue=http%3A%2F%2Flocalhost%3A7777",
    "loggedin_profile": {
        "profile_id": "ag1zdGFydHVwYmlkZGVycg4LEgZTQlVzZXIY3pIBDA",
        "username": "test",
        "name": null,
        "email": "test@example.com",
        "investor": false,
        "edited_listing": "ag1zdGFydHVwYmlkZGVycg8LEgdMaXN0aW5nGLqUAQw",
        "edited_status": null,
        "num_notifications": 0,
        "votable": false,
        "mockData": false,
        "admin": false
    },
    "error_code": 0,
    "error_msg": null,
    "notifications": [
        {
            notify_type: "ask_listing_owner",
            title: "Question concerning your listing by google",
            text_1: "Notification text",
            create_date: "20120428121845",
            read: false,
            link: "http://www.google.com"
        },
        {
            notify_type: "private_message",
            title: "Private message for you from Kabbage1977",
            text_1: "Mystic connections",
            create_date: "20120324092322",
            read: true,
            link: "/message-group-page.html"
        },
        {
            notify_type: "notification",
            title: "Notification concerning your account",
            text_1: "Mystic connections",
            create_date: "20120322072212",
            read: true,
            link: "/profile-page.html"
        },
        {
            notify_type: "bid",
            title: "You received a bid on listing Foobar, Inc.",
            text_1: "Bid received $100,000 for 12%",
            create_date: "20120318172238",
            read: true,
            link: "/company-page.html"
        },
        {
            notify_type: "comment",
            title: "You received a comment on listing Foobar, Inc.",
            text_1: "Great idea, I love it",
            create_date: "20120318164617",
            read: true,
            link: "/company-page.html"
        }
    ],
    "notifications_props": {
        "start_index": 1,
        "max_results": 20,
        "num_results": 5,
        "more_results_url": null
    },
    "profile": {
        "profile_id": "ag1zdGFydHVwYmlkZGVycg4LEgZTQlVzZXIY3pIBDA",
        "username": "test",
        "name": null,
        "email": "test@example.com",
        "investor": false,
        "edited_listing": "ag1zdGFydHVwYmlkZGVycg8LEgdMaXN0aW5nGLqUAQw",
        "edited_status": null,
        "num_notifications": 0,
        "votable": false,
        "mockData": false,
        "admin": false
    }});
*/
        ajax.call();
    }
});

(new NotificationPageClass()).loadPage();
