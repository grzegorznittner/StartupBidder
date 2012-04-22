function EditProfilePageClass() {}
pl.implement(EditProfilePageClass,{
    loadPage: function() {
        var completeFunc = function(json) {
                var header = new HeaderClass(),
                    editProfile = new EditProfileClass();
                pl('#personalinfomsg').text('');
                header.setLogin(json);
                editProfile.setProfile(json);
            },
            ajax = new AjaxClass('/user/loggedin', 'personalinfomsg', completeFunc);
        ajax.call();
    }
});

(new EditProfilePageClass()).loadPage();
