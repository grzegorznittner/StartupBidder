function NewListingQAClass() {
    var base = new NewListingBaseClass();
    base.prevPage = '/new-listing-bmc-page.html';
    base.nextPage = '/new-listing-financials-page.html';
    this.base = base;
    this.page = 1;
    this.pagetotal = 12;
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
            logo = 'url(' + (self.base.listing.logo || 'img/noimage_146x146.jpg') + ')',
            corp = self.base.listing.title || 'Company Name Here',
            date = self.base.listing.created_date ? DateClass.prototype.format(self.base.listing.created_date) : DateClass.prototype.today(),
            m = 10,
            n = 26,
            i,
            field,
            val,
            sel;
        for (i = m; i <= n; i++) {
            field = 'answer' + i;
            sel = '#' + field + 'ip';
            val = self.base.listing[field];
            pl(sel).html(self.stylize(val));
        }
        pl('#iplogo').css({background: logo});
        pl('#ipcorp').text(corp);
        pl('#ipdate').text(date);
        pl('#ippagetotal').text(self.pagetotal);
        pl('#ippage').text(self.page);
        if (self.page === 1) {
            pl('#iptitle').text(corp);
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
        self.page = newpage;
        pl('#ippage').text(self.page);
    },
    pageLeft: function() {
        var self = this,
            newpage = self.page <= 1 ? 1 : self.page - 1;
        self.page = newpage;
        pl('#ippage').text(self.page);
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
            displayName = { summary: 'ELEVATOR PITCH' },
            answerFields = [
                'PROBLEM',
                'SOLUTION',
                'MARKETING',
                'ADVERTISING',
                'COMPETITORS',
                'COMPETITIVE COMPARISON',
                'STATUS',
                'PLAN OF ATTACK',
                'GOVERNMENT',
                'TEAM',
                'TEAM RELOCATION',
                'TEAM VALUES',
                'FINANCIAL PROJECTIONS',
                'CURRENT FINANCIALS',
                'OWNERS',
                'INVESTMENT',
                'TIMELINE AND WRAPUP'
            ],
            msgFields = [
                'introductionmsg',
                'problemmsg',
                'problemmsg',
                'marketmsg',
                'marketmsg',
                'competitionmsg',
                'competitionmsg',
                'businessmodelmsg',
                'businessmodelmsg',
                'businessmodelmsg',
                'teammsg',
                'teammsg',
                'teammsg',
                'financialmsg',
                'financialmsg',
                'financialmsg',
                'financialmsg',
                'timelinemsg'
            ],
            id,
            i,
            idx,
            field;
        this.base.fields = [];
        for (i = 0; i < answerFields.length; i++) {
            idx = 10 + i;
            id = 'answer' + idx;
            textFields.push(id);
            displayName[id] = answerFields[i];
        }
        for (i = 0; i < textFields.length; i++) {
            id = textFields[i];
            field = new TextFieldClass(id, this.base.listing[id], this.getUpdater(id), msgFields[i]);
            field.fieldBase.setDisplayName(displayName[id].toUpperCase());
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

