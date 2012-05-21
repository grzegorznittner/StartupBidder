function MessagePageClass() {
    this.json = {};
};
pl.implement(MessagePageClass,{
    loadPage: function() {
        var completeFunc = function(json) {
                var header = new HeaderClass(),
                    messageList = new MessageListClass();
                header.setLogin(json);
                if (!json.loggedin_profile) { // must be logged in for this page
                    window.location = '/';
                }
                messageList.display(json);
             },
            ajax = new AjaxClass('/user/get_message_users', 'messagemsg', completeFunc);
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
    "users": [
        {
            from_user_id: 'abc',
            from_user_nickname: 'Dead',
            last_text: "I'm not sure if this is the right investment for me, but I'll be in town next week, if we could meet that would be great.",
            last_date: "20120428121845",
            read: false
        },
        {
            from_user_id: 'def',
            from_user_nickname: 'fowler',
            last_text: "Do you have a method for increasing efficiency of TPS reports?",
            last_date: "20120324092322",
            read: true
        },
        {
            from_user_id: ':uid',
            from_user_nickname: 'MadMax',
            last_text: "This idea you've got about bacon and martinis is so crazy that it just might work!",
            last_date: "20120322072212",
            read: true
        },
        {
            from_user_id: ':uid',
            from_user_nickname: 'jenny',
            last_text: "Would you like to discuss this further over dinner for two down at the shore?",
            last_date: "20120318172238",
            read: true
        },
        {
            from_user_id: ':uid',
            from_user_nickname: 'arley',
            last_text: "Mr. Madison, what you've just said is one of the most insanely idiotic things I have ever heard. At no point in your rambling, incoherent response were you even close to anything that could be considered a rational thought. Everyone in this room is now dumber for having listened to it. I award you no points, and may God have mercy on your soul.",
            last_date: "20120318164617",
            read: true
        }
    ],
    "user_props": {
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
    }}); // FIXME
        ajax.call();
    }
});

(new MessagePageClass()).loadPage();
