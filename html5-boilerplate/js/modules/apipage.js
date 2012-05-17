function APIPageClass() {}
pl.implement(APIPageClass, {
    loadPage: function() {
        var completeFunc = function(json) {
                var header = new HeaderClass();
                header.setLogin(json);
                pl('#loadmsg').html('');
                pl('.apipanel dt').bind({
                    click: function() {
                        var detail = pl(this.nextSibling.nextSibling.nextSibling.nextSibling),
                            cls = 'apidetaildisplay';
                        detail.hasClass(cls) ? detail.removeClass(cls) : detail.addClass(cls);
                        return false;
                    }
                });
                pl('.apidetail form input[type=submit]').bind({
                    click: function() {
                        var iframe = this.parentNode.nextSibling.nextSibling;
                        if (!iframe) {
                            iframe = this.parentNode.parentNode.parentNode.nextSibling.nextSibling;
                        }
                        pl(iframe).addClass('apidetailframe');
                    }
                });
                DlHighlight.HELPERS.highlightByName('code', 'pre');
            },
            ajax = new AjaxClass('/listings/latest/', 'loadmsg', completeFunc);
        ajax.ajaxOpts.data = { max_results: 1 };
        ajax.call();
    }
});

(new APIPageClass()).loadPage();
