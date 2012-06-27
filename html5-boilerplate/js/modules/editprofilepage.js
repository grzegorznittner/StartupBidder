function EditProfilePageClass() {}
pl.implement(EditProfilePageClass,{
    loadPage: function() {
        var completeFunc = function(json) {
                var header = new HeaderClass(),
                    editProfile = new EditProfileClass();
                header.setLogin(json);
                editProfile.setProfile(json);
                pl('#personalinfomsg').text('');
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            ajax = new AjaxClass('/listings/discover_user', 'personalinfomsg', completeFunc);
        ajax.call();
    }
});

(new EditProfilePageClass()).loadPage();
