function ProfileClass() {}
pl.implement(ProfileClass, {
    display: function(json) {
        if (json) {
            this.store(json);
        }
        this.displayFields();
        this.displayPromote();
    },

    store: function(json) {
        if (json) {
            CollectionsClass.prototype.merge(this, json);
        }
    },
 
    displayFields: function() {
        var profile = this.profile || this.loggedin_profile || {};
 /*        var investor = json.investor ? 'Accredited Investor' : 'Entrepreneur';
            date = new DateClass(),
            joindate = json.joined_date ? date.format(json.joined_date) : 'unknown';
        pl('#profilestatus').html('');
        pl('#title').html(json.title);
        pl('#organization').html(json.organization);
        pl('#phone').html(json.phone || '');
        pl('#address').html(json.address || '');
        pl('#joineddate').html(joindate);
        pl('#investor').html(investor);
        pl('#notifyenabled').html(json.notifyenabled ? 'enabled' : 'disabled');
        pl('#mylistingscount').html(json.posted ? json.posted.length : 0);
        pl('#biddedoncount').html(json.bidon ? json.bidon.length : 0);
*/
        pl('#username').text(profile.username || 'anonymous');
        pl('#email').text(profile.email || 'No email address');
        pl('#name').text(profile.name || 'Mr. Anonymous');
        if (profile.avatar) {
            pl('#avatar').css('background-image', 'url(' + profile.avatar + ')');
        }
        if (profile.joined_date) {
            pl('#membersince').text('Joined ' + DateClass.prototype.formatDateStr(profile.joined_date));
        }
        if (profile.user_class) {
            pl('#user_class').text(profile.user_class.replace(/[_-]/g, ' ').toUpperCase());
        }
        if (this.loggedin_profile
            && !(this.loggedin_profile.user_class === 'dragon' || this.loggedin_profile.user_class === 'requested_dragon')
            && (!this.profile
                || (this.loggedin_profile && this.loggedin_profile.admin && this.profile.profile_id === this.loggedin_profile.profile_id))) {
            pl('#applydragonwrapper').show();
            this.bindApplyDragon();
        }
        if (this.loggedin_profile
            && this.loggedin_profile.user_class === 'requested_dragon'
            && (!this.profile
                || (this.loggedin_profile && this.loggedin_profile.admin && this.profile.profile_id === this.loggedin_profile.profile_id))) {
            pl('#pendingdragonwrapper').show();
            this.bindApplyDragon();
        }
    },

    bindApplyDragon: function() {
        pl('#applydragonbutton').unbind().bind('click', function() {
            var complete = function(json) {
                    pl('#applydragonwrapper').hide();
                    pl('#pendingdragonwrapper').show();
                },
                ajax = new AjaxClass('/user/request_dragon', 'applydragonmessage', complete);
            pl('#applydragonbutton').hide();
            pl('#applydragonspinner').show();
            ajax.setPost();
            ajax.call();
        });
     },

    displayPromote: function() {
        var profile = this.profile || this.loggedin_profile,
            is_loggedin_admin = this.loggedin_profile && this.loggedin_profile.admin,
            not_yet_dragon = !profile.user_class || profile.user_class !== 'dragon',
            promotable = is_loggedin_admin && not_yet_dragon;
        if (promotable) {
            this.bindPromoteButton();
        }
    },

    bindPromoteButton: function() {
        var self = this;
        pl('#promotebox').show();
        pl('#promotebtn').bind({
            click: function() {
                var profile = self.profile || self.loggedin_profile,
                    complete = function() {
                        pl('#promotebtn, #promotecancelbtn').hide();
                        pl('#promotemsg').text('Promoted to dragon, reloading...').show();
                        setTimeout(function() {
                            window.location.reload();
                        }, 3000);
                    },
                    url = '/user/promote_to_dragon/' + profile.profile_id,
                    ajax = new AjaxClass(url, 'promotemsg', complete);
                if (pl('#promotemsg').text() && pl('#promotemsg').text().indexOf('Error') !== -1) {
                    pl('#promotebtn').hide();
                }
                else if (pl('#promotecancelbtn').css('display') === 'none') { // first call
                    pl('#promotemsg').text('Promote this user to dragon status?').show();
                    pl('#promotecancelbtn').show();
                }
                else {
                    ajax.setPostData();
                    ajax.call();
                }
                return false;
            }
        });
        pl('#promotecancelbtn').bind({
            click: function() {
                pl('#promotecancelbtn').hide();
                pl('#promotemsg').text('').show();
                pl('#promotebtn').show();
                return false;
            }
        });
    }
});

function ProfilePageClass() {
    var queryString = new QueryStringClass();
    this.passed_id = queryString.vars.id;
    this.json = {};
};
pl.implement(ProfilePageClass,{
    storeListings: function(propertykey, _options) {
        var self = this,
            options = _options || {},
            wrappersel = '#' + propertykey + '_wrapper',
            listings = self.json[propertykey],
            listingfound = false,
            companylist;
        options.propertykey = propertykey;
        options.companydiv = propertykey;
        if (listings && (options.propertyissingle || listings.length > 0)) {
            pl(wrappersel).show();
            companylist = new CompanyListClass(options);
            companylist.storeList(self.json);
            listingfound = true;
        }
        return listingfound;
    },
    loadPage: function() {
        var self = this,
            completeFunc = function(json) {
                var header = new HeaderClass(),
                    profile = new ProfileClass(),
                    //notifyList = new NotifyListClass(),
                    listprops = [ 'edited_listing',
                        'active_listings',
                        'admin_posted_listings',
                        'admin_frozen_listings',
                        'monitored_listings',
                        'withdrawn_listings',
                        'frozen_listings'
                    ],
                    seeallurl = '/profile-listing-page.html?' + (self.passed_id ? 'id=' + self.passed_id + '&' : '') + 'type=',
                    options = {
                        edited_listing: { propertyissingle: true, fullWidth: true },
                        active_listings: { seeall: seeallurl + 'active', fullWidth: true },
                        monitored_listings: { seeall: seeallurl + 'monitored', fullWidth: true },
                        withdrawn_listings: { seeall: seeallurl + 'withdrawn', fullWidth: true },
                        frozen_listings: { seeall: seeallurl + 'frozen', fullWidth: true },
                        admin_posted_listings: { seeall: seeallurl + 'admin_posted', fullWidth: true },
                        admin_frozen_listings: { seeall: seeallurl + 'admin_frozen', fullWidth: true }
                    },
                    listingfound = false,
                    propertykey,
                    i;
                self.json = json;
                header.setLogin(json);
                if (!json.loggedin_profile) { // must be logged in for this page
                    window.location = '/';
                }
                profile.display(json);
                //notifyList.display(json);
                for (i = 0; i < listprops.length; i++) {
                    propertykey = listprops[i];
                    if (self.storeListings(propertykey, options[propertykey])) {
                        listingfound = true;
                    }
                }
                if (!listingfound) {
                    pl('#no_listings_wrapper').show();
                }
                if (json.loggedin_profile) {
                    if (!json.loggedin_profile.admin) {
                        pl('#editprofilebutton').show();
                    }
                    else if (json.loggedin_profile.admin && !json.profile) {
                        pl('#editprofilebutton').show();
                    }
                    else if (json.loggedin_profile.admin && json.profile && json.loggedin_profile.profile_id === json.profile.profile_id) {
                        pl('#editprofilebutton').show();
                    }
                    else {
                        pl('.titleperson').text('USER');
                    }
                }
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            url = this.passed_id ? '/user/get/' + this.passed_id : '/listings/discover_user',
            ajax = new AjaxClass(url, 'profilemsg', completeFunc);
        ajax.call();
    }
});

function ProfileListingPageClass() {
    this.json = {};
    this.queryString = new QueryStringClass();
    this.passed_id = queryString.vars.id;
    this.type = this.queryString.vars.type || 'active';
    this.data = { max_results: 20 };
    this.urlmap = {
        active: '/listings/user/active',
        monitored: '/listings/monitored',
        withdrawn: '/listings/user/withdrawn',
        frozen: '/listings/user/frozen',
        admin_posted: '/listings/posted',
        admin_frozen: '/listings/frozen'
    };
    this.url = this.urlmap[this.type] || this.urlmap['active'];
    this.isadmin = this.type === 'admin_posted' || this.type === 'admin_frozen';
    this.title = (this.isadmin ? 'YOUR ' : '') + this.type.toUpperCase() + ' LISTINGS';
};
pl.implement(ProfileListingPageClass,{
    loadPage: function() {
        var self = this,
            completeFunc = function(json) {
                var header = new HeaderClass(),
                    profile = new ProfileClass(),
                    companyList = new CompanyListClass({ fullWidth: true });
                self.json = json;
                header.setLogin(json);
                if (!json.loggedin_profile) { // must be logged in for this page
                    window.location = '/';
                }
                profile.display(json);
                pl('#listingstitle').text(self.title);
                companyList.storeList(json);
                pl('#editprofilebutton').show();
                pl('.preloader').hide();
                pl('.wrapper').show();
             },
             ajax = new AjaxClass(self.url, 'companydiv', completeFunc);
        ajax.setGetData(this.data);
        ajax.call();
    }
});

function EditProfileClass() {}
pl.implement(EditProfileClass, {
    getUpdater: function() {
        var self = this;
        return function(newdata, loadFunc, errorFunc, successFunc) {
            var data, field, ajax;
            data = { profile: {
                name: pl('#name').attr('value'),
                nickname: pl('#username').attr('value')
                /*
                profile_id: self.profile_id,
                status: self.status,
                open_id: self.open_id,
                investor: pl('#investor').attr('value') ? 'true' : 'false'
                title: pl('#title').attr('value'),
                organization: pl('#organization').attr('value'),
                facebook:'',
                twitter:'',
                linkedin:''
                */
            } };
            for (field in newdata) {
                data.profile[field] = newdata[field];
            }
            ajax = new AjaxClass('/user/autosave', '', null, successFunc, loadFunc, errorFunc);
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
                        pl('#deactivatemsg').addClass('successful').html('DEACTIVATED, LOGGING OUT...');
                        pl('#deactivatebtn, #deactivatecancelbtn').hide();
                        setTimeout(function() {
                            window.location = pl('#logoutlink').attr('href');
                        }, 3000);
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
    display: function(discoverjson) {
        this.store(discoverjson);
    },
    store: function(discoverjson) {
        //properties = ['profile_id', 'status', 'name', 'username', 'open_id', 'profilestatus', 'title', 'organization', 'email', 'phone', 'address'];
        var self = this,
            json = discoverjson && discoverjson.loggedin_profile || {},
            properties = ['profile_id', 'username', 'email', 'name'],
            textFields = ['username', 'name'],
            i, property, textFields, textFieldId, textFieldObj, investorCheckbox; 
        self.profile_id = json.profile_id;
        self.admin = json.admin;
        self.updateUrl = '/user/update?id=' + self.profile_id;
        for (i = 0; i < properties.length; i++) {
            property = properties[i];
            self[property] = json[property];
        }
        for (i = 0; i < textFields.length; i++) {
            textFieldId = textFields[i];
            textFieldObj = new TextFieldClass(textFieldId, json[textFieldId], self.getUpdater(), 'personalinfomsg');
            textFieldObj.fieldBase.addValidator(textFieldObj.fieldBase.validator.isNotEmpty);
/*
            if (textFieldId === 'email') {
                textFieldObj.fieldBase.addValidator(textFieldObj.fieldBase.validator.isEmail);
            }
*/
            if (textFieldId === 'username') {
                textFieldObj.fieldBase.addValidator(function(username) {
                    var successFunc = function(json) {
                            var icon = new ValidIconClass('usernameicon');
                            if (json) {
                                icon.showValid();
                                pl('#personalinfomsg').text('');
                            }
                            else {
                                icon.showInvalid();
                                pl('#personalinfomsg').html('<span class="attention">Nickname already taken, please choose another</span>');
                            } 
                        },
                        icon = new ValidIconClass('usernameicon'),
                        ajax;
                    if (!username || !username.length) {
                        return 'Nickname must not be empty';
                    }
                    else if (username.length < 3) {
                        return 'Nickname must be at least three characters';
                    }
                    else if (username.length > 30) {
                        return 'Nickname must be no more than 30 characters';
                    }
                    else {
                        ajax = new AjaxClass('/user/check_user_name', 'personalinfomsg', null, successFunc);
                        ajax.setGetData({ name: username });
                        ajax.call();
                    }
                    return 0;
                });
                textFieldObj.fieldBase.postSuccessFunc = function(newval) {
                    pl('#headerusername').text(newval);
                };
            }
            if (textFieldId === 'name') {
                textFieldObj.fieldBase.addValidator(textFieldObj.fieldBase.validator.makeLengthChecker(3, 100));
            }
            textFieldObj.bindEvents();
        }
/*
        investorCheckbox = new CheckboxFieldClass('investor', json.investor, self.getUpdater(), 'personalinfomsg');
        investorCheckbox.bindEvents();

        notifyCheckbox = new CheckboxFieldClass('notifyenabled', json.notifyenabled, self.getUpdater(), 'settingsmsg');
        notifyCheckbox.bindEvents();
        newPassword = new TextFieldClass('newpassword', '', function(){}, 'passwordmsg');
        passwordOptions = {
            length: [8, 32],
            badWords: ['password', self.name, self.username, self.email, (self.email&&self.email.indexOf('@')>0?self.email.split('@')[0]:'')],
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
        confirmPassword = new TextFieldClass('confirmpassword', '', self.getUpdater(), 'passwordmsg');
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
        pl('#email').text(json.email || 'No email address');
        this.bindInfoButtons();
        self.displayDeactivate();
        pl('#personalcolumn').show();
    },
    hideAllInfo: function() {
        pl('.sideinfo').removeClass('sideinfodisplay');
    },
    bindInfoButtons: function() {
        var self = this;
        pl('input.text, select.text, textarea.inputwidetext').bind({
            focus: function(e) {
                var evt = new EventClass(e),
                    infoel = evt.target().parentNode.nextSibling.nextSibling;
                self.hideAllInfo();
                pl(infoel).addClass('sideinfodisplay');
            },
            blur: self.hideAllInfo
        });
        pl('.sideinfo').bind('click', self.hideAllInfo);
    }
});

function EditProfilePageClass() {}
pl.implement(EditProfilePageClass,{
    loadPage: function() {
        var completeFunc = function(json) {
                var header = new HeaderClass(),
                    editProfile = new EditProfileClass();
                header.setLogin(json);
                editProfile.display(json);
                pl('#personalinfomsg').text('');
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            ajax = new AjaxClass('/listings/discover_user', 'personalinfomsg', completeFunc);
        ajax.call();
    }
});

function ProfileListClass() {}
pl.implement(ProfileListClass, {
    store: function(json) {
        if (json) {
            CollectionsClass.prototype.merge(this, json);
        }
    },
   
    display: function(json) {
        if (json) {
            this.store(json);
        }
        if (!this.loggedin_profile) {
            document.location = '/';
        }
        this.displayList(json.users);
    },

    displayEmptyList: function() {
        var listhtml = '\
            <div class="messageline">\
                <p class="messagetext"><i>No results found</i></p>\
            </div>\
            ';
        pl('#profilelistcontainer').removeClass('addlistingcontainerfilled');
        pl('#profilelist').html(listhtml);
    },

    displayList: function(results) {
        var list = results || [],
            listhtml = '',
            listitem,
            i;
        for (i = 0; i < list.length; i++) {
            listitem = list[i];
            listhtml += this.makeListItem(listitem);
        }
        if (listhtml) {
            pl('#profilelistcontainer').addClass('addlistingcontainerfilled');
            pl('#profilelist').html(listhtml);
        }
        else {
            this.displayEmptyList();
        }
    },

    makeListItem: function(listitem) {
        var url = '/profile-page.html?id=' + listitem.profile_id,
            avatarstyle = listitem.avatar
                ? ' style="background-image: url(' + listitem.avatar + ')"'
                : '',
           	statustext =  listitem.status !== 'active'
                ? '<span class="profileliststatus">' + listitem.status + '</span>'
                : '',
			admintext =  listitem.admin ? '<span class="profilelistadmin">ADMIN</span>' : '',
			userclasstext =  listitem.user_class
                ? '<span class="profilelistuserclass">' + listitem.user_class.toUpperCase() + '</span>'
                : '',
            nametext = listitem.name
                ? 'Name:<span class="profilelistlastlogin">'
                    + SafeStringClass.prototype.htmlEntities(listitem.name) + '</span></br>'
                : '',
            locationtext = listitem.location
                ? 'Name:<span class="profilelistlastlogin">'
                    + SafeStringClass.prototype.htmlEntities(listitem.location) + '</span></br>'
                : '',
            lastlogintext = listitem.last_login
                ? 'Last Login:<span class="profilelistlastlogin">'
                    + DateClass.prototype.format(listitem.last_login) + '</span></br>'
                : '',
			pendingtext =  listitem.edited_listing
                ? '<a href="/company-page.html?id=' + listitem.edited_listing + '">'
                    + '<span class="profilelistpending">pending listing</span>'
                    + '</a><br/>'
                : '',
            html = '\
            <div class="messageline">\
                <div class="profilelistavatar"' + avatarstyle + '></div>\
                <p class="messagetext profilelistheader">\
                    <a href="' + url + '">\
			            <span class="profilelistusername">' + listitem.username + '</span>\
                    </a>\
			        <span class="profilelistemail">' + listitem.email+ '</span>\
                    ' + statustext + '\
                    ' + userclasstext + '\
                    ' + admintext + '\
                </p>\
                <p class="messagetext profilelistdetails">\
                    ' + nametext + '\
                    ' + locationtext + '\
                    ' + lastlogintext + '\
                    ' + pendingtext + '\
                </p>\
            </div>\
            ';
        return html;
    },

    loadPage: function() {
        var self = this,
            complete = function(json) {
                (new HeaderClass()).setLogin(json);
                self.display(json);
                pl('#profilelistmsg').text('');
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            ajax = new AjaxClass('/user/all', 'profilelistmsg', complete);
        ajax.call();
    }
});

