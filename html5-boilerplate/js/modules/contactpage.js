function ContactPageClass() {}
pl.implement(ContactPageClass,{
    checkRed: function(sel) {
        pl(sel).removeClass('checkboxgreenicon').addClass('checkboxredicon');
    },
    checkGreen: function(sel) {
        pl(sel).removeClass('checkboxredicon').addClass('checkboxgreenicon');
    },
    bindMailto: function() {
        var self = this;
        pl('#question').bind('blur', function() {
            var question = pl('#question').attr('value'),
                msg = '';
            if (!question) {
                msg += 'Please fill in QUESTION. ';
                self.checkRed('#questioncheckboxicon');
            }
            else {
                self.checkGreen('#questioncheckboxicon');
            }
            pl('#submitmsg').addClass('errorcolor').html(msg || '&nbsp;');
        });
        pl('#details').bind('blur', function() {
            var details = pl('#details').attr('value'),
                msg = '';
            if (!details) {
                msg += 'Please fill in DETAILS. ';
                self.checkRed('#detailscheckboxicon');
            }
            else {
                self.checkGreen('#detailscheckboxicon');
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
                self.checkRed('#questioncheckboxicon');
            }
            else {
                self.checkGreen('#questioncheckboxicon');
            }
            if (!details) {
                msg += 'Please fill in DETAILS. ';
                self.checkRed('#detailscheckboxicon');
            }
            else {
                self.checkGreen('#detailscheckboxicon');
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
        var self = this,
            completeFunc = function(json) {
            var header = new HeaderClass(),
                companyList = new CompanyListClass({ colsPerRow: 2});
                header.setLogin(json);
                companyList.storeList(json);
                self.bindMailto();
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            basePage = new BaseCompanyListPageClass({ max_results: 2 });
        basePage.loadPage(completeFunc);
    }
});

(new ContactPageClass()).loadPage();
