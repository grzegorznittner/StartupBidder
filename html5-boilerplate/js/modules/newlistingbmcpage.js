function NewListingBMCClass() {
    var base = new NewListingBaseClass();
    base.prevPage = '/new-listing-media-page.html';
    base.nextPage = '/new-listing-qa-page.html';
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
            };
        ajax = new AjaxClass('/listings/create', 'newlistingmsg', completeFunc);
        ajax.setPost();
        ajax.call();
    },
    display: function() {
        if (this.base.listing.status !== 'new') {
            document.location = 'new-listing-submitted-page.html';
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
            field.bindEvents({noEnterKeySubmit: true});
            this.base.fields.push(field);
        } 
        this.base.bindNavButtons();
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
        pl('#bmc-preview-btn').bind('click', function() {
            pl('#bmc-editable').hide();
            pl('#bmc').show();
        });
    },
    bindInfoButtons: function() {
        pl('.bmcinfobtn').bind({
            click: function(e) {
                var evt = new EventClass(e),
                    infoel;
                infoel = evt.target().parentNode.nextSibling.nextSibling.nextSibling.nextSibling.childNodes[3];
                if (pl(infoel).hasClass('bmcinfodisplay') || pl(infoel).hasClass('bmcinfodisplayvaluepropositions')) {
                    pl('.bmcinfo').removeClass('bmcinfodisplay').removeClass('bmcinfodisplayvaluepropositions');
                }
                else {
                    pl('.bmcinfo').removeClass('bmcinfodisplay').removeClass('bmcinfodisplayvaluepropositions');
                    if (pl(infoel).hasClass('bmcinfovaluepropositions')) {
                        pl(infoel).addClass('bmcinfodisplayvaluepropositions');
                    }
                    else {
                        pl(infoel).addClass('bmcinfodisplay');
                    }
                }
            }
        });
        pl('.bmcinfo').bind('click', function() {
            pl('.bmcinfo').removeClass('bmcinfodisplay').removeClass('bmcinfodisplayvaluepropositions');
        });
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

