function HeaderOnlyPageClass() {}
pl.implement(HeaderOnlyPageClass, {
    loadPage: function() {
        var completeFunc = function(json) {
                var header = new HeaderClass();
                console.log('bar');
                header.setLogin(json);
                pl('#loadmsg').html('');
            },
            ajax = new AjaxClass('/listings/latest/', 'loadmsg', completeFunc);
        console.log('foo');
        ajax.ajaxOpts.data = { max_results: 1 };
        ajax.call();
    }
});

(new HeaderOnlyPageClass()).loadPage();
