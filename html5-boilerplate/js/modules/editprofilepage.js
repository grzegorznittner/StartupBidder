function EditProfilePageClass() {}
pl.implement(EditProfilePageClass,{
    loadPage: function() {
        var successFunc, ajax;
        successFunc = function(json) {
            var header, profile;
            if (!json) {
                pl('#profilestatus').html('<span class="notice">Error: null response from server</span>');
                pl('#profilecolumn').hide();
                return;
            }
            header = new HeaderClass();
            editProfile = new EditProfileClass();
            header.setLogin(json);
            editProfile.setProfile(json);
        };
        ajax = new AjaxClass('/user/loggedin', 'profilestatus', null, successFunc);
        ajax.call();
    }
});

(new EditProfilePageClass()).loadPage();
