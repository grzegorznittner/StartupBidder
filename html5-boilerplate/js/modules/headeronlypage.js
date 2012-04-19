function HeaderOnlyPageClass() {}
pl.implement(HeaderOnlyPageClass, {
    loadPage: function() {
        var completeFunc = function(json) {
                var header = new HeaderClass();
                header.setLogin(json);
            },
            ajax = new AjaxClass('/user/loggedin', 'loadmsg', completeFunc);
        ajax.call();
    }
});

(new HeaderOnlyPageClass()).loadPage();
