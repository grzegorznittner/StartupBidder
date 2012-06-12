function NewListingQAClass() {
    var base = new NewListingBaseClass();
    base.prevPage = '/new-listing-bmc-page.html';
    base.nextPage = '/new-listing-financials-page.html';
    this.base = base;
    this.ip = new IPClass();
}
pl.implement(NewListingQAClass, {
    load: function() {
        var self = this,
            completeFunc = function(json) {
                var listing = json && json.listing ? json.listing : {},
                    header = new HeaderClass();
                header.setLogin(json);
                self.base.store(listing);
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
        this.ip.display(this.base.listing);
        pl('#ip-editable').show();
    },
    bindEvents: function() {
        var textFields = ['summary'],
            msgFields = [
                'introductionmsg',
                'problemmsg',
                'problemmsg',
                'problemmsg',
                'problemmsg',
                'marketmsg',
                'marketmsg',
                'competitionmsg',
                'competitionmsg',
                'businessmodelmsg',
                'businessmodelmsg',
                'teammsg',
                'teammsg',
                'financialmsg',
                'financialmsg',
                'financialmsg',
                'financialmsg',
                'timelinemsg'
            ],
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
            field = new TextFieldClass(id, this.base.listing[id], this.base.getUpdater(id, null, this.ip.getUpdater(id)), msgFields[i]);
    
            if (this.base.displayNameOverrides[id]) {
                field.fieldBase.setDisplayName(this.base.displayNameOverrides[id]);
            }
            field.fieldBase.addValidator(ValidatorClass.prototype.makeLengthChecker(16, 1000));
            field.fieldBase.validator.postValidator = this.ip.genDisplay(field, this.base.genDisplayCalculatedIfValid(field));
            field.bindEvents({noEnterKeySubmit: true});
            this.base.fields.push(field);
        } 
        this.ip.bindButtons();
        this.base.bindNavButtons();
        this.base.bindTitleInfo();
        this.bindEditButton();
        this.bindPreviewButton();
        this.bindInfoButtons();
    },
    bindEditButton: function() {
        pl('#ip-edit-btn').bind('click', function() {
            pl('#ip').hide();
            pl('#ip-editable').show();
        }).show();
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
        pl('.qainfobtn').bind({
            click: function(e) {
                var evt = new EventClass(e),
                    tgt = evt.target(),
                    nextsib = tgt.parentNode.nextSibling,
                    infoel,
                    classes;
                if (nextsib.nodeType !== 1) {
                    nextsib = nextsib.nextSibling;
                }
                infoel = nextsib.firstChild;
                if (infoel.nodeType !== 1) {
                    infoel = infoel.nextSibling;
                }
                classes = infoel.getAttribute('class');
                if (classes.match('qainfodisplay')) {
                    pl(infoel).removeClass('qainfodisplay');
                }
                else {
                    infoel.setAttribute('class', classes + ' qainfodisplay');
                }
            }
        });
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

