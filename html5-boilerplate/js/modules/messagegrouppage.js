function MessageGroupPageClass() {
    this.json = {};
};
pl.implement(MessageGroupPageClass,{
    loadPage: function() {
        var completeFunc = function(json) {
                var header = new HeaderClass(),
                    messageList = new MessageGroupListClass();
                header.setLogin(json);
                if (!json.loggedin_profile) { // must be logged in for this page
                    window.location = '/';
                }
                messageList.display(json);
                pl('.preloader').hide();
                pl('.wrapper').show();
             },
            ajax = new AjaxClass('/user/message_users', 'messagemsg', completeFunc);
        ajax.setGetData({ max_results: 20 });
        ajax.call();
    }
});

(new MessageGroupPageClass()).loadPage();
