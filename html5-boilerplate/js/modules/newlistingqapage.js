function NewListingQAClass() {
    var queryString = new QueryStringClass();
    this.id = queryString.vars.id;
    this.listing_id = this.id;
    this.base = new NewListingBaseClass();
    this.ip = new IPClass();
}
pl.implement(NewListingQAClass, {
    load: function() {
        var self = this,
            url = this.id
                ? '/listing/get/' + this.id
                : '/listings/create',
            complete = function(json) {
                var listing = json && json.listing ? json.listing : {},
                    header = new HeaderClass();
                header.setLogin(json);
                self.base.store(listing);
                self.display();
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            error = function(errornum, json) {
                (new HeaderClass()).setLogin(json);
                pl('.preloader').hide();
                pl('.errorwrapper').show();
            },
            ajax = new AjaxClass(url, 'newlistingmsg', complete, null, null, error);
        if (url === '/listings/create') {
            ajax.setPost();
        }
        ajax.call();
    },
    display: function() {
        var status = this.base.listing.status;
        if (status !== 'new' && status !== 'posted' && status !== 'active') {
            document.location = '/company-page.html?id=' + this.base.listing.listing_id;
        }
        if (!this.bound) {
            this.bindEvents();
            this.bound = true;
        }
        this.ip.display(this.base.listing);
        pl('#ip-editable').show();
    },
    bindEvents: function() {
        var self = this,
            textFields = ['summary'],
            m = 10,
            n = 26,
            i,
            id,
            field;
        this.base.fields = [];
        for (i = m; i <= n; i++) {
            id = 'answer' + i;
            textFields.push(id);
        }
        for (i = 0; i < textFields.length; i++) {
            id = textFields[i];
            field = new TextFieldClass(id, this.base.listing[id], this.base.getUpdater(id, null, this.ip.getUpdater(id)), 'ip-editable-msg');
    
            if (this.base.displayNameOverrides[id]) {
                field.fieldBase.setDisplayName(this.base.displayNameOverrides[id]);
            }
            field.fieldBase.addValidator(ValidatorClass.prototype.makeLengthChecker(16, 1000));
            field.fieldBase.validator.postValidator = this.ip.genDisplay(field, this.base.genDisplayCalculatedIfValid(field));
            field.bindEvents({noEnterKeySubmit: true, iconid: 'ipfieldicon', customTabKey: function(e) { // tab has problems with auto-scroll
                /*
                var id = e && e.target && e.target.id || '',
                    num = id === 'summary' ? 2 : 1 * (id.replace(/answer/,'') || 10) - 7;
                setTimeout(function() {
                    self.ip.setPage(num);
                }, 500);
                */
                return false;
            }});
            this.base.fields.push(field);
        } 
        this.ip.bindButtons();
        this.base.bindNavButtons();
        this.base.bindTitleInfo();
        this.bindEditButton();
        this.bindActivateDeactivateButton();
        this.bindPreviewButton();
        this.bindInfoButtons();
    },

    bindEditButton: function() {
        pl('#ip-edit-btn').bind('click', function() {
            pl('#ip').hide();
            pl('#ip-editable').show();
        }).show();
    },

    bindActivateDeactivateButton: function() {
        var self = this,
            text = this.base.listing.has_ip ? 'DEACTIVATE' : 'ACTIVATE';
        pl('#ip-activate-deactivate-btn').text(text).bind('click', function() {
            if (self.base.listing.has_ip) {
                self.deactivate();
            }
            else {
                self.activate();
            }
        });
    },

    activate: function() {
        var self = this,
            complete = function(json) {
                self.base.listing.has_ip = json.listing.has_ip;
                pl('#ip-editable-msg').html('<span class="successful">Presentation activated</span>');
                pl('#ip-activate-deactivate-btn').text('DEACTIVATE');
            },
            data = {
                listing: {
                    id: this.base.listing.listing_id,
                    has_ip: true
                }
            },
            ajax = new AjaxClass('/listing/update_field', 'ip-editable-msg', complete);
        ajax.setPostData(data);
        ajax.call();
    },

    deactivate: function() {
        var self = this,
            complete = function(json) {
                self.base.listing.has_ip = json.listing.has_ip;
                pl('#ip-editable-msg').html('<span class="successful">Presentation deactivated</span>');
            },
            data = {
                listing: {
                    id: this.base.listing.listing_id,
                    has_ip: false
                }
            },
            ajax = new AjaxClass('/listing/update_field', 'ip-editable-msg', complete);
        ajax.setPostData(data);
        ajax.call();
    },

    bindPreviewButton: function() {
        var self = this;
        pl('#ip-preview-btn').bind('click', function() {
            pl('#ip-editable').hide();
            pl('#ip').show();
            self.base.hideAllInfo();
        });
    },
    bindInfoButtons: function() {
        var self = this;
        pl('.qatextarea').bind({
            focus: function(e) {
                var evt = new EventClass(e),
                    infoel = evt.target().parentNode.previousSibling.previousSibling.previousSibling.previousSibling.childNodes[1];
                self.base.hideAllInfo();
                pl(infoel).addClass('ipinfodisplay');
            },
            blur: self.base.hideAllInfo
        });
        pl('.ipinfo, .ipleft, .ipright, .ipfirst').bind('click', self.base.hideAllInfo);
    }
});

function NewListingPageClass() {};
pl.implement(NewListingPageClass, {
    loadPage: function() {
        var newlisting = new NewListingQAClass();
        newlisting.load();
    }
});

(new NewListingPageClass()).loadPage();

