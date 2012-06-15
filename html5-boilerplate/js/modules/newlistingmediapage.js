function NewListingMediaClass() {
    var base = new NewListingBaseClass();
    base.prevPage = '/new-listing-basics-page.html';
    base.nextPage = '/new-listing-bmc-page.html';
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
        if (this.base.listing.status !== 'new') {
            document.location = 'new-listing-submitted-page.html';
        }
        if (!this.bound) {
            this.bindEvents();
            this.bound = true;
        }
    },
    displayLogo: function(dataurl) {
        var self = this,
            url = dataurl && dataurl.indexOf('data:') === 0 ? dataurl : null,
            uploadurl = self.base.listing.logo_upload;
        if (url) {
            pl('#logoimg').attr({src: url});
        }
        else {
            pl('#logoimgwrapper').addClass('noimage');
        }
        if (uploadurl) {
            pl('#logouploadform').attr({action: uploadurl});
        }
    },
    displayVideo: function(url) {
        if (url) {
            pl('#videoiframe').attr({src: url});
        }
        else {
            pl('#videoiframe').addClass('noimage');
        }
    },
    bindEvents: function() {
        var self = this,
            uploadurl = self.base.listing.logo_upload,
            datauri = self.base.listing.logo,
            videourl = self.base.listing.video,
            postLogo = function(json) {
                var datauri = json && json.listing && json.listing.logo ? json.listing.logo : null,
                    uploadurl = json && json.listing && json.listing.logo_upload ? json.listing.logo_upload : null;
                if (uploadurl) {
                    self.base.listing.logo_upload = uploadurl;
                }
                if (datauri) {
                    self.base.listing.logo = datauri;
                    self.displayLogo(datauri);    
                    self.base.displayCalculated();
                    pl('#logomsg').removeClass('successful').addClass('successful').text('Logo uploaded');
                }
            },
            postVideo = function(json) {
                var url = json && json.listing && json.listing.video ? json.listing.video : null;
                if (url) {
                    self.base.listing.video = url;
                    self.displayVideo(url);
                    self.base.displayCalculated();
                }
            },
            logoUpdater = this.base.getUpdater('logo_url', null, postLogo),
            videoUpdater = this.base.getUpdater('video', VideoCheckClass.prototype.preformat, postVideo),
            logoURLField = new TextFieldClass('logo_url', null, logoUpdater, 'logomsg'),
            videoURLField = new TextFieldClass('video', this.base.listing.video, videoUpdater, 'videomsg');
        pl('#logouploadiframe').bind({
            load: function() {
                var iframe = pl('#logouploadiframe').get(0).contentDocument.body.innerHTML,
                    uploadurlmatch = iframe.match(/upload_url&gt;(.*)&lt;\/upload_url/),
                    uploadurl = uploadurlmatch && uploadurlmatch.length === 2 ? uploadurlmatch[1] : null,
                    dataurimatch = iframe.match(/value&gt;(.*)&lt;\/value/),
                    datauri = dataurimatch && dataurimatch.length === 2 ? dataurimatch[1] : null;
                if (uploadurl && uploadurl !== 'null') {
                    self.base.listing.logo_upload = uploadurl;
                }
                if (datauri && datauri !== 'null') {
                    self.base.listing.logo = datauri;
                    self.displayLogo(datauri);
                    self.base.displayCalculated();
                    pl('#logomsg').removeClass('successful').addClass('successful').text('Logo uploaded');
                }
                else {
                    //self.base.listing.logo = null;
                    pl('#logomsg').removeClass('errorcolor').addClass('errorcolor').text('Unable to upload logo');
                }
            }
        });
        pl('#LOGO').bind({
            change: function() {
                pl('#logomsg').removeClass('inprogress').addClass('inprogress').text('Uploading...');
                pl('#logouploadform').get(0).submit();
                return false;
            }
        });
        logoURLField.fieldBase.setDisplayName('LOGO URL');
        logoURLField.fieldBase.addValidator(ValidatorClass.prototype.isURLEmptyOk);
        logoURLField.fieldBase.isEmptyNoUpdate = true;
        logoURLField.bindEvents();
        videoURLField.fieldBase.validator.preValidateTransform = VideoCheckClass.prototype.preformat;
        videoURLField.fieldBase.setDisplayName('VIDEO URL');
        videoURLField.fieldBase.addValidator(ValidatorClass.prototype.isVideoURL);
        videoURLField.bindEvents();
        self.displayLogo(datauri);
        self.displayVideo(videourl);
        this.base.displayCalculated();
        this.base.bindNavButtons(this.genNextValidator());
        this.base.bindTitleInfo();
    },
    genNextValidator: function() {
        var self = this;
        return function() {
            var msgs = [];
            if (!self.base.listing.logo) {
                msgs.push("LOGO: you must upload a logo.");
            }
            if (!self.base.listing.pic1) {
                msgs.push("IMAGES: you must upload at least one image.");
            }
            return msgs;
        };
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

