function MessageClass(listing_id, loggedin_profile_id) {
    var safeStr = new SafeStringClass(),
        completeFunc = function() {
            pl('#addmessagetext').removeClass('edited').get(0).blur();
            pl('#addmessagemsg').html('Message sent');
        },
        ajax = new AjaxClass('/listing/sendmessage', 'addmessagemsg', completeFunc);
    pl('#addmessagetext').bind({
        focus: function() {
            if (!pl('#addmessagetext').hasClass('edited')) {
                pl('#addmessagetext').attr({value: ''});
                pl('#addmessagemsg').html('&nbsp;');
            }
        },
        change: function() {
            if (!pl('#addmessagetext').hasClass('edited')) {
                pl('#addmessagetext').addClass('edited');
                pl('#addmessagemsg').html('&nbsp;');
            }
            return false;
        },
        blur: function() {
            if (!pl('#addmessagetext').hasClass('edited')) {
                pl('#addmessagetext').attr({value: 'Put your private message here...'});
            }
        }
    });
    pl('#addmessagebtn').bind({
        click: function(event) {
            var messageText = safeStr.clean(pl('#addmessagetext').attr('value'));
            ajax.setPostData({
                message: {
                    listing_id: listing_id,
                    profile_id: loggedin_profile_id,
                    subject: 'You have received a private message concerning your listing',
                    text: messageText
                }
            });
            ajax.call();
            return false;
        }
    });
}
pl.implement(MessageClass, {
    display: function() {
        pl('#addmessagetitle').show();
        pl('#addmessagebox').show();            
    }
});


