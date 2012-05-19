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
        ajax.call();
    }
});

(new MessagePageClass()).loadPage();
