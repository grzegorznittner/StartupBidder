function MessagePageClass() {
    this.json = {};
    this.queryString = new QueryStringClass();
    this.from_user_id = this.queryString.vars.from_user_id || this.queryString.vars.to_user_id || '';
    this.from_user_nickname = decodeURIComponent(this.queryString.vars.from_user_nickname || this.queryString.vars.to_user_nickname || '');
    console.log('this.from_user_id: ' + this.from_user_id);
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
             ajax = new AjaxClass('/user/messages/' + this.from_user_id, 'messagemsg', completeFunc);
        pl('#from_user_nickname_upper').text(this.from_user_nickname.toUpperCase());
        ajax.setGetData({ max_results: 20 });
        ajax.call();
    }
});

(new MessagePageClass()).loadPage();
