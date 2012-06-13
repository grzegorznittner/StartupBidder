function MessagePageClass() {
    this.json = {};
    this.queryString = new QueryStringClass();
    this.to_user = this.queryString.vars.to_user || '';
    this.to_user_nickname = decodeURIComponent(this.queryString.vars.to_user_nickname || '');
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
             ajax = new AjaxClass('/user/messages/' + this.to_user, 'messagemsg', completeFunc);
        pl('#from_user_nickname_upper').text(this.to_user_nickname.toUpperCase());
        ajax.call();
    }
});

(new MessagePageClass()).loadPage();
