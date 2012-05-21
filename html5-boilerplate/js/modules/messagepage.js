function MessagePageClass() {
    this.json = {};
    this.queryString = new QueryStringClass();
    this.from_user_id = this.queryString.vars.from_user_id || '';
    this.from_user_nickname = decodeURIComponent(this.queryString.vars.from_user_nickname || '');
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
            ajax = new AjaxClass('/user/get_messages', 'messagemsg', completeFunc);
        pl('#from_user_nickname_upper').text(this.from_user_nickname.toUpperCase());
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
    "messages": [
        {
            direction: 'received',
            text: 'Hey I really like your idea but I need to know more, what are you doing for marketing?',
            create_date: "20120318164617"
        },
        {
            direction: 'received',
            text: 'Oh I see your marketing plan, but it needs more advertising, can you bump it up a little?',
            create_date: "20120318164818"
        },
        {
            direction: 'sent',
            text: 'Okay what if I add in $500 a month in advertising the first 6 months, how does that sound?',
            create_date: "20120318173212"
        },
        {
            direction: 'received',
            text: "Sounds good, let's talk more tomorrow.",
            create_date: "20120318181243"
        }
    ],
    "messages_props": {
        "start_index": 1,
        "max_results": 20,
        "num_results": 5,
        "more_results_url": null
    },
    "other_user_profile": {
        "profile_id": "agd789601otherguy",
        "username": "theotherguy",
        "name": null,
        "email": "theotherguy@example.com",
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
