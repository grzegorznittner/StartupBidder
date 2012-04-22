function NotifyListClass() {}
pl.implement(NotifyListClass, {
    storeList: function(json) {
        var notifications, html, i, notification;
        notifications = json && json.notifications && json.notifications.length > 0
            ? json.notifications
            : [];
        if (notifications.length === 0) {
            notification = {};
            notification.url = null;
            notification.type = 'comment';
            notification.text = 'You currently have no notifications';
            notification.date = '';
            notifications[0] = notification;
        }
        html = '';
        for (i = 0; i < notifications.length; i++) {
            notification = notifications[i];
            html += '\
    '+(notification.url ? '<a href="' + notification.url + '">' : '') + '\
        <div class="sideboxnotify sideboxlink">\
            <span class="sideboxicon">\
                <div class="'+notification.type+'icon"></div>\
            </span>\
            <span class="sideboxnotifytext">\
                '+notification.text+'\
                <br/>\
                <span class="sideboxdate">'+notification.date+'</span>\
            </span>\
        </div>\
    '+(notification.url ? '</a>' : '');
        }
        pl('#notifylist').html(html);
    }
});

function ProfileClass() {}
pl.implement(ProfileClass, {
    setProfile: function(json) {
        var investor = json.investor ? 'Accredited Investor' : 'Entrepreneur';
/*
            date = new DateClass(),
            joindate = json.joined_date ? date.format(json.joined_date) : 'unknown';
        pl('#profilestatus').html('');
*/
        pl('#username').html(json.username || 'anonymous');
        pl('#email').html(json.email || 'no email address');
        pl('#name').html(json.name || '');
/*
        pl('#title').html(json.title);
        pl('#organization').html(json.organization);
        pl('#phone').html(json.phone || '');
        pl('#address').html(json.address || '');
        pl('#joineddate').html(joindate);
*/
        pl('#investor').html(investor);
/*
        pl('#notifyenabled').html(json.notifyenabled ? 'enabled' : 'disabled');
        pl('#mylistingscount').html(json.posted ? json.posted.length : 0);
        pl('#biddedoncount').html(json.bidon ? json.bidon.length : 0);
*/
    }
});

function EditProfileClass() {}
pl.implement(EditProfileClass, {
    getUpdater: function() {
        var self = this;
        return function(newdata, loadFunc, errorFunc, successFunc) {
            var data, field, ajax;
            data = { profile: {
                profile_id: self.profile_id,
                /*
                status: self.status,
                open_id: self.open_id,
                */
                username: pl('#username').attr('value'),
                email: pl('#email').attr('value'),
                name: pl('#name').attr('value')
                /*
                title: pl('#title').attr('value'),
                organization: pl('#organization').attr('value'),
                investor: pl('#investor').attr('value') ? 'true' : 'false',
                facebook:'',
                twitter:'',
                linkedin:''
                */
            } };
            for (field in newdata) {
                data.profile[field] = newdata[field];
            }
            ajax = new AjaxClass(self.updateUrl, '', null, successFunc, loadFunc, errorFunc);
            ajax.setPostData(data);
            ajax.call();
        };
    },
    displayDeactivate: function() {
        var deactivateable = true; // always returned by user/loggedin
        if (deactivateable) {
            this.bindDeactivateButton();
        }
    },
    bindDeactivateButton: function() {
        var self = this;
        pl('#deactivatebox').show();
        pl('#deactivatebtn').bind({
            click: function() {
                var completeFunc = function() {
                        pl('#deactivatemsg').addClass('successful').html('DEACTIVATED, GOING HOME...');
                        pl('#deactivatebtn, #deactivatecancelbtn').hide();
                        setTimeout(function(){window.location='/';}, 4000);
                    },
                    url = '/user/deactivate/' + self.profile_id,
                    ajax = new AjaxClass(url, 'deactivatemsg', completeFunc);
                if (pl('#deactivatecancelbtn').css('display') === 'none') { // first call
                    pl('#deactivatemsg, #deactivatecancelbtn').show();
                }
                else {
                    ajax.setPost();
                    ajax.call();
                }
                return false;
            }
        });
        pl('#deactivatecancelbtn').bind({
            click: function() {
                pl('#deactivatemsg, #deactivatecancelbtn').hide();
                return false;
            }
        });
    },
    setProfile: function(json) {
        var self, properties, updateUrl, i, property, textFields, textFieldId, textFieldObj,
            investorCheckbox, notifyCheckbox, newPassword, passwordOptions, confirmPassword;
        self = this;
        //properties = ['profile_id', 'status', 'name', 'username', 'open_id', 'profilestatus', 'title', 'organization', 'email', 'phone', 'address'];
        properties = ['profile_id', 'username', 'email', 'name'];
        textFields = ['username', 'email', 'name'];
        this.profile_id = json.profile_id;
        this.admin = json.admin;
        this.updateUrl = '/user/update?id=' + this.profile_id;
        for (i = 0; i < properties.length; i++) {
            property = properties[i];
            this[property] = json[property];
        }
        for (i = 0; i < textFields.length; i++) {
            textFieldId = textFields[i];
            textFieldObj = new TextFieldClass(textFieldId, json[textFieldId], this.getUpdater(), 'personalinfomsg');
            textFieldObj.fieldBase.addValidator(textFieldObj.fieldBase.validator.isNotEmpty);
            if (textFieldId === 'email') {
                textFieldObj.fieldBase.addValidator(textFieldObj.fieldBase.validator.isEmail);
            }
            textFieldObj.bindEvents();
        }
        investorCheckbox = new CheckboxFieldClass('investor', json.investor, this.getUpdater(), 'settingsmsg');
        investorCheckbox.bindEvents();
/*
        notifyCheckbox = new CheckboxFieldClass('notifyenabled', json.notifyenabled, this.getUpdater(), 'settingsmsg');
        notifyCheckbox.bindEvents();
        newPassword = new TextFieldClass('newpassword', '', function(){}, 'passwordmsg');
        passwordOptions = {
            length: [8, 32],
            badWords: ['password', this.name, this.username, this.email, (this.email&&this.email.indexOf('@')>0?this.email.split('@')[0]:'')],
            badSequenceLength: 3
        };
        newPassword.fieldBase.addValidator(newPassword.fieldBase.validator.makePasswordChecker(passwordOptions));
        newPassword.fieldBase.validator.postValidator = function(result) {
            if (result === 0) {
                pl('#confirmpassword').removeAttr('disabled');
            }
            else {
                pl('#confirmpassword').attr({disabled: 'disabled'});
            }
        };
        newPassword.bindEvents();
        confirmPassword = new TextFieldClass('confirmpassword', '', this.getUpdater(), 'passwordmsg');
        confirmPassword.fieldBase.addValidator(function(val) {
            if (pl('#newpassword').attr('value') === val) {
                return 0;
            }
            else {
                return "confirm must match new password";
            }
        });
        confirmPassword.bindEvents();
 */
        self.displayDeactivate();
    }
});

