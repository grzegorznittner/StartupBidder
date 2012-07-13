function NewListingBMCClass() {
    var base = new NewListingBaseClass();
    this.base = base;
    this.bmc = new BMCClass();
}
pl.implement(NewListingBMCClass, {
    load: function() {
        var self = this,
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
        ajax = new AjaxClass('/listings/create', 'newlistingmsg', completeFunc);
        ajax.setPost();
        ajax.call();
    },
    display: function() {
        var status = this.base.listing.status;
        if (status !== 'new' && status !== 'posted') {
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
        this.bindPreviewButton();
        this.bindInfoButtons();
    },
    bindEditButton: function() {
        pl('#bmc-edit-btn').bind('click', function() {
            pl('#bmc').hide();
            pl('#bmc-editable').show();
        }).show();
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

function NewListingPageClass() {};
pl.implement(NewListingPageClass, {
    loadPage: function() {
        var newlisting = new NewListingBMCClass();
        newlisting.load();
    }
});

(new NewListingPageClass()).loadPage();

