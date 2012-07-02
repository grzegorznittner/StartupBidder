function ContactPageClass() {}
pl.implement(ContactPageClass,{
    bindMailto: function() {
        pl('#question').bind('blur', function() {
            var question = pl('#question').attr('value'),
                msg = '';
            if (!question) {
                msg += 'Please fill in QUESTION. ';
                pl('#questioncheckboxicon').attr({class: 'checkboxredicon'});
            }
            else {
                pl('#questioncheckboxicon').attr({class: 'checkboxgreenicon'});
            }
            pl('#submitmsg').addClass('errorcolor').html(msg || '&nbsp;');
        });
        pl('#details').bind('blur', function() {
            var details = pl('#details').attr('value'),
                msg = '';
            if (!details) {
                msg += 'Please fill in DETAILS. ';
                pl('#detailscheckboxicon').attr({class: 'checkboxredicon'});
            }
            else {
                pl('#detailscheckboxicon').attr({class: 'checkboxgreenicon'});
            }
            pl('#submitmsg').addClass('errorcolor').html(msg || '&nbsp;');
        });
        pl('#submitbutton').bind('click', function() {
            var question = pl('#question').attr('value'),
                details = pl('#details').attr('value'),
                adminemail = 'admin@startupbidder.com',
                mailto = 'mailto:' + adminemail + '?subject=' + encodeURIComponent(question) + '&body=' + encodeURIComponent(details),
                msg = '';
            if (!question) {
                msg += 'Please fill in QUESTION. ';
                pl('#questioncheckboxicon').attr({class: 'checkboxredicon'});
            }
            else {
                pl('#questioncheckboxicon').attr({class: 'checkboxgreenicon'});
            }
            if (!details) {
                msg += 'Please fill in DETAILS. ';
                pl('#detailscheckboxicon').attr({class: 'checkboxredicon'});
            }
            else {
                pl('#detailscheckboxicon').attr({class: 'checkboxgreenicon'});
            }
            if (msg) {
                pl('#submitmsg').addClass('errorcolor').text(msg);
            }
            else {
                pl('#question, #details').attr({disabled: 'disabled'});
                pl('#sendbuttonlink').attr({href: mailto});
                pl('#submitmsg, #submitbutton').hide();
                pl('#confirmmsg, #sendbutton, #cancelbutton').show();
            }
        });
        pl('#cancelbutton').bind('click', function() {
            pl('#questioncheckboxicon, #detailscheckboxicon').removeAttr('class');
            pl('#question, #details').removeAttr('disabled');
            pl('#submitmsg').removeClass('errorcolor').addClass('successful').text('Message canceled');
            pl('#confirmmsg, #sendbutton, #cancelbutton').hide();
            pl('#submitmsg, #submitbutton').show();
        });
        pl('#sendbutton').bind('click', function() {
            pl('#questioncheckboxicon, #detailscheckboxicon').removeAttr('class');
            pl('#question, #details').attr({value: ''}).removeAttr('disabled');
            pl('#submitmsg').html('&nbsp;');
            pl('#confirmmsg, #sendbutton, #cancelbutton').hide();
            pl('#submitmsg, #submitbutton').show();
            document.location = pl('#sendbuttonlink').attr('href');
            return true;
        });
    },
    loadPage: function() {
        var completeFunc, basePage;
        completeFunc = function(json) {
            var header = new HeaderClass(),
                companyList = new CompanyListClass({ colsPerRow: 2});
            header.setLogin(json);
            companyList.storeList(json);
        };
        this.bindMailto();
        basePage = new BaseCompanyListPageClass();
        basePage.loadPage(completeFunc);
    }
});

(new ContactPageClass()).loadPage();
