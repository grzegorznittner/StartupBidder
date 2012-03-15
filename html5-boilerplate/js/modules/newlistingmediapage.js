function NewListingMediaClass() {
    var base = new NewListingBaseClass();
    base.prevPage = '/new-listing-financials-page.html';
    base.nextPage = '/new-listing-submit-page.html';
    this.base = base;
}
pl.implement(NewListingMediaClass, {
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
        if (!this.bound) {
            this.bindEvents();
            this.bound = true;
        }
    },
    displayLogo: function(dataurl) {
        var url = dataurl && dataurl.indexOf('data:') === 0 ? dataurl : null;
        if (url) {
            pl('#logoimg').attr({src: url});
        }
        else {
            pl('#logoimgwrapper').addClass('noimage');
        }
    },
    setUploadUrl: function(uploadurl) {
        pl('#logouploadform').attr({action: uploadurl});
    },
    bindEvents: function() {
        var self = this,
            uploadurl = self.base.listing.logo_upload,
            datauri = self.base.listing.logo,
            postLogo = function(json) {
                var datauri = json && json.listing && json.listing.logo ? json.listing.logo : null;
                if (datauri) {
                    self.base.listing.logo = datauri;
                    self.displayLogo(datauri);    
                }
            },
            logoUpdater = this.base.getUpdater('logo_url', null, postLogo),
            logoURLField = new TextFieldClass('logo_url', this.base.listing.logo_url, logoUpdater, 'logomsg');
        self.setUploadUrl(uploadurl);
        self.displayLogo(datauri);
        pl('#logouploadiframe').bind({
            load: function() {
                var iframe = pl('#logouploadiframe').get(0).contentDocument.body.innerHTML,
                    uploadurlmatch = iframe.match(/upload_url.*(https?:\/\/.*\/upload\/[A-Za-z0-9]*).*upload_url/),
                    dataurimatch = iframe.match(/value.*(data:image\/[a-z]*;base64,[A-Za-z0-9+\/]*=*).*value/),
                    uploadurl = uploadurlmatch && uploadurlmatch.length === 2 ? uploadurlmatch[1] : null,
                    datauri = dataurimatch && dataurimatch.length === 2 ? dataurimatch[1] : null;
                if (uploadurl) {
                    self.setUploadUrl(uploadurl);
                }
                if (datauri) {
                    self.displayLogo(datauri);
                }
            }
        });
        pl('#LOGO').bind({
            change: function() {
                pl('#logouploadform').get(0).submit();
            }
        });
        pl('#logoloadurlbutton').bind({
            click: function() {
                logoURLField.update();
            }
        });
        logoURLField.fieldBase.setDisplayName('LOGO UPLOAD URL');
        logoURLField.fieldBase.addValidator(ValidatorClass.prototype.isURL);
        logoURLField.bindEvents({noAutoUpdate: true});
        this.base.bindNavButtons();
    }
});

function NewListingPageClass() {};
pl.implement(NewListingPageClass, {
    loadPage: function() {
        var newlisting = new NewListingMediaClass();
        newlisting.load();
    }
});

(new NewListingPageClass()).loadPage();

