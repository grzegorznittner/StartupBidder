function BMCClass() {
}
pl.implement(BMCClass, {
    store: function(listing) {
        var n = 9,
            i,
            field,
            idx,
            sel,
            val,
            html;
        for (i = 0; i < n; i++) {
            idx = 1 + i;
            field = 'answer' + idx;
            sel = '#' + field + 'bmc';
            val = listing[field];
            html = HTMLMarkup.prototype.stylize(val, 'bmc');
            pl(sel).html(html);
        }
    },
    display: function(listing) {
        this.store(listing);
        pl('#bmc').show();
    },
    getUpdater: function(id) {
        return function(json) {
            var bmcsel = '#' + id + 'bmc',
                newval = json && json.listing && (json.listing[id] !== null) ? json.listing[id] : null,
                html = HTMLMarkup.prototype.stylize(newval, 'bmc');
            if (newval !== null) {
                pl(bmcsel).html(html);
            }
        };
    },
    genDisplay: function(field, displayCalc) {
        return function(result, val) {
            var html;
            displayCalc();
            if (result === 0) {
                html = HTMLMarkup.prototype.stylize(val, 'bmc');
                pl(field.fieldBase.sel + 'bmc').html(html);
            }
        }
    }
});

function ModelPageClass() {
    var queryString = new QueryStringClass();
    this.listing_id = queryString.vars.id;
};
pl.implement(ModelPageClass,{
    load: function() {
        var self = this,
            complete = function(json) {
                var header = new HeaderClass(),
                    companybanner = new CompanyBannerClass('model'),
                    bmc = new BMCClass();
                header.setLogin(json);
                companybanner.display(json);
                bmc.display(json.listing);
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            ajax = new AjaxClass('/listing/get/' + this.listing_id, 'bmcmsg', complete);
        ajax.call();
    }
});

function NewListingBMCClass() {
    var queryString = new QueryStringClass();
    this.id = queryString.vars.id;
    this.listing_id = this.id;
    this.base = new NewListingBaseClass();
    this.bmc = new BMCClass();
}
pl.implement(NewListingBMCClass, {
    load: function() {
        var self = this,
            url = this.id
                ? '/listing/get/' + this.id
                : '/listings/create',
            completeFunc = function(json) {
                var listing = json && json.listing ? json.listing : {},
                    header = new HeaderClass();
                header.setLogin(json);
                self.base.store(listing);
                self.bmc.store(listing);
                self.display();
                pl('.preloader').hide();
                pl('.wrapper').show();
            };
        ajax = new AjaxClass(url, 'newlistingmsg', completeFunc);
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
        pl('#bmc-editable').show();
    },
    bindEvents: function() {
        var textFields = [],
            m = 1,
            n = 9,
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
            field = new TextFieldClass(id, this.base.listing[id], this.base.getUpdater(id, null, this.bmc.getUpdater(id)), 'bmc-editable-msg');
            if (this.base.displayNameOverrides[id]) {
                field.fieldBase.setDisplayName(this.base.displayNameOverrides[id]);
            }
            field.fieldBase.addValidator(ValidatorClass.prototype.makeLengthChecker(16, 1000));
            field.fieldBase.validator.postValidator = this.bmc.genDisplay(field, this.base.genDisplayCalculatedIfValid(field));
            field.bindEvents({noEnterKeySubmit: true, iconid: 'bmcfieldicon'});
            this.base.fields.push(field);
        } 
        this.base.bindNavButtons();
        this.base.bindTitleInfo();
        this.bindEditButton();
        this.bindActivateDeactivateButton();
        this.bindPreviewButton();
        this.bindInfoButtons();
    },
    bindEditButton: function() {
        pl('#bmc-edit-btn').bind('click', function() {
            pl('#bmc').hide();
            pl('#bmc-editable').show();
        }).show();
    },

    bindActivateDeactivateButton: function() {
        var self = this,
            text = this.base.listing.has_bmc ? 'DEACTIVATE' : 'ACTIVATE';
        pl('#bmc-activate-deactivate-btn').text(text).bind('click', function() {
            if (self.base.listing.has_bmc) {
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
                self.base.listing.has_bmc = json.listing.has_bmc;
                pl('#bmc-editable-msg').html('<span class="successful">Business model activated</span>');
                pl('#bmc-activate-deactivate-btn').text('DEACTIVATE');
            },
            data = {
                listing: {
                    id: this.base.listing.listing_id,
                    has_bmc: true
                }
            },
            ajax = new AjaxClass('/listing/update_field', 'bmc-editable-msg', complete);
        ajax.setPostData(data);
        ajax.call();
    },

    deactivate: function() {
        var self = this,
            complete = function(json) {
                self.base.listing.has_bmc = json.listing.has_bmc;
                pl('#bmc-editable-msg').html('<span class="successful">Business model deactivated</span>');
            },
            data = {
                listing: {
                    id: this.base.listing.listing_id,
                    has_bmc: false
                }
            },
            ajax = new AjaxClass('/listing/update_field', 'bmc-editable-msg', complete);
        ajax.setPostData(data);
        ajax.call();
    },

    bindPreviewButton: function() {
        var self = this;
        pl('#bmc-preview-btn').bind('click', function() {
            pl('#bmc-editable').hide();
            pl('#bmc').show();
            self.base.hideAllInfo();
        });
    },
    bindInfoButtons: function() {
        var self = this;
        pl('.bmctextarea, .bmctextareashort, .bmctextareawide').bind({
            focus: function(e) {
                var evt = new EventClass(e),
                    infoel = evt.target().previousSibling.previousSibling.childNodes[3];
                self.base.hideAllInfo();
                pl(infoel).addClass(pl(infoel).hasClass('bmcinfovaluepropositions') ? 'bmcinfodisplayvaluepropositions' : 'bmcinfodisplay');
            },
            blur: self.base.hideAllInfo
        });
        pl('.bmcinfo').bind('click', self.base.hideAllInfo);
    }
});

function NewListingBMCPageClass() {};
pl.implement(NewListingBMCPageClass, {
    loadPage: function() {
        var newlisting = new NewListingBMCClass();
        newlisting.load();
    }
});

