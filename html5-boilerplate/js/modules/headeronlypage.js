function HeaderOnlyPageClass() {}
pl.implement(HeaderOnlyPageClass, {
    loadPage: function() {
        var completeFunc = function(json) {
                var header = new HeaderClass();
                header.setLogin(json);
                pl('#loadmsg').html('');
            },
            ajax = new AjaxClass('/listings/latest/', 'loadmsg', completeFunc);
        ajax.ajaxOpts.data = { max_results: 1 };
        ajax.call();
    }
});

(new HeaderOnlyPageClass()).loadPage();
