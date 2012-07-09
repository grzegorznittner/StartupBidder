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
                pl('.preloader').hide();
                pl('.wrapper').show();
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
                console.log('here');
                console.log(id);
                console.log(num);
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

