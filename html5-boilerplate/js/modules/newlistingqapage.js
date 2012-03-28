function NewListingQAClass() {
    var base = new NewListingBaseClass();
    base.prevPage = '/new-listing-bmc-page.html';
    base.nextPage = '/new-listing-financials-page.html';
    this.base = base;
    this.page = 1;
    this.pagetotal = 20;
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
        this.displayPresentation();
    },
    genDisplayPresentation: function(field) {
        var self = this,
            f1 = self.base.genDisplayCalculatedIfValid(field);
        return function(result, val) {
            f1();
            if (result === 0) {
                pl(field.fieldBase.sel + 'ip').html(self.stylize(val));
            }
        }
    },
    displayPresentation: function() {
        var self = this,
            listing = self.base.listing,
            logo = 'url(' + (listing.logo || 'img/noimage_146x146.jpg') + ')',
            corp = listing.title || 'Company name here',
            date = listing.created_date ? DateClass.prototype.format(listing.created_date) : DateClass.prototype.today(),
            mantra = listing.mantra || 'Company mantra here',
            name = listing.founders || 'Founders here',
            contact_email = listing.contact_email || 'Email address here',
            contact_emaillink = contact_email ? 'mailto:' + contact_email :'',
            brief_address = listing.brief_address || 'Postal address here',
            website = listing.website || 'Website here',
            asking = listing.asked_funds ? 'Asking ' + CurrencyClass.prototype.format(suggested_amt) + ' for ' + PercentClass.prototype.format(suggested_pct) : 'Not asking for funds at this time.',
            m = 10,
            n = 26,
            i,
            field,
            val,
            sel;
        pl('#mantraip').text(mantra);
        pl('#nameip,#name2ip').text(name);
        pl('#contact_emailip,#contact_email2ip').text(contact_email);
        pl('#contact_emaillinkip,#contact_email2linkip').attr({href: contact_emaillink});
        pl('#brief_addressip,#brief_address2ip').text(brief_address);
        pl('#websiteip,#website2ip').text(website);
        pl('#websitelinkip,#website2linkip').attr({href: website});
        pl('#created_dateip').text(date);
        pl('#askingip').text(asking);
        pl('#ipcorp').text(corp);
        pl('#ipdate,#ipdate2').text(date);
        pl('#ippagetotal').text(self.pagetotal);
        pl('#ippage').text(self.page);
        pl('#iptitle1').text(corp);
        pl('#summaryip').html(self.stylize(listing.summary));
        for (i = m; i <= n; i++) {
            field = 'answer' + i;
            sel = '#' + field + 'ip';
            val = listing[field];
            pl(sel).html(self.stylize(val));
        }
        for (i = 1; i <= self.pagetotal; i++) {
            sel = '#iplogo' + i;
            pl(sel).css({background: logo});
        }
    },
    getUpdater: function(id) {
        var self = this,
            cleaner = null,
            updatePresentation = function(json) {
                var ipsel = '#' + id + 'ip',
                    newval = json && json.listing && (json.listing[id] !== null) ? json.listing[id] : null;
                if (newval !== null) {
                    pl(ipsel).html(self.stylize(newval));
                }
            },
            baseUpdater = this.base.getUpdater(id, cleaner, updatePresentation);
        return baseUpdater;
    },
    stylize: function(text) {
        var stylized = text ? '' + text : '';
        if (stylized) {
            stylized = stylized.replace(/^[ \t]*\n|\n[ \t]*\n/g, '\n<div class="ipspacer"></div>\n');
            stylized = stylized.replace(/^[ \t]*[*]([^\n]*)/g, '\n<ul class="iplist"><li>$1</li></ul>\n');
            stylized = stylized.replace(/\n[ \t]*[*]([^\n]*)/g, '\n<ul class="iplist"><li>$1</li></ul>\n');
        }
        return stylized;
    },
    pageRight: function() {
        var self = this,
            newpage = self.page >= self.pagetotal ? self.pagetotal : self.page + 1;
        self.setPage(newpage);
    },
    pageLeft: function() {
        var self = this,
            newpage = self.page <= 1 ? 1 : self.page - 1;
        self.setPage(newpage);
    },
    setPage: function(newpage) {
        var self = this,
            left = (960 * (1 - newpage)) + 'px';
        self.page = newpage;
        pl('#ipslideset').css({left: left})
        pl('#ippage').text(newpage);
    },
    bindPresentationButtons: function() {
        var self = this;
        pl('#ipleft').bind({
            click: function() {
                self.pageLeft();
                return false;
            }
        });
        pl('#ipright').bind({
            click: function() {
                self.pageRight();
                return false;
            }
        });
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
            field = new TextFieldClass(id, this.base.listing[id], this.getUpdater(id), msgFields[i]);
            if (this.base.displayNameOverrides[id]) {
                field.fieldBase.setDisplayName(this.base.displayNameOverrides[id]);
            }
            field.fieldBase.addValidator(ValidatorClass.prototype.makeLengthChecker(16, 1000));
            field.fieldBase.validator.postValidator = this.genDisplayPresentation(field);
            field.bindEvents({noEnterKeySubmit: true});
            this.base.fields.push(field);
        } 
        this.bindPresentationButtons();
        this.base.bindNavButtons();
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

