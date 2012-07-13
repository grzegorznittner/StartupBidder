function NewListingMediaClass() {
    this.base = new NewListingBaseClass();
    this.imagepanel = new ImagePanelClass({ editmode: true });
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
    },
    setUploadUrls: function() {
        pl('#logouploadform, #picuploadform').attr({action: this.base.listing.upload_url});
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
    displayImages: function() {
        this.imagepanel.setListing(this.base.listing).display();
    },
    displayVideo: function(url) {
        if (url) {
            pl('#videoiframe').attr({src: url});
        }
        else {
            pl('#videoiframe').addClass('novideo');
        }
    },
    bindEvents: function() {
        var self = this,
            datauri = self.base.listing.logo,
            videourl = self.base.listing.video,
            postLogo = function(json) {
                var success = false;
                if (json && json.listing) {
                    self.base.listing = json.listing;
                    self.setUploadUrls();
                    if (self.base.listing.logo) {
                        self.displayLogo(self.base.listing.logo);    
                        self.base.displayCalculated();
                        success = true;
                    }
                }
                pl('#logo_url, #logouploadfile').attr({value: ''});
                if (success) {
                    pl('#logomsg').removeClass('errorcolor').removeClass('inprogress').addClass('successful').text('Logo uploaded');
                }
                else {
                    pl('#logomsg').addClass('errorcolor').text('Unable to upload logo');
                }
            },
            postPic = function(json) { // FIXME
                var success = false,
                    picnum = pl('#picnum').text();
                if (json && json.listing) {
                    self.base.listing = json.listing;
                    self.setUploadUrls();
                    if (self.base.listing['pic' + picnum]) {
                        self.imagepanel.enableImage(picnum);
                        success = true;
                    }
                }
                pl('#pic_url, #picuploadfile').attr({value: ''});
                if (success) {
                    pl('#picmsg').removeClass('errorcolor').removeClass('inprogress').addClass('successful').text('Image uploaded');
                }
                else {
                    pl('#picmsg').addClass('errorcolor').text('Could not upload image');
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
            picUpdater = this.base.getUpdater('pic_url', null, postPic, null, function() { return 'pic' + pl('#picnum').text() + '_url' }),
            logoURLField = new TextFieldClass('logo_url', null, logoUpdater, 'logomsg'),
            videoURLField = new TextFieldClass('video', this.base.listing.video, videoUpdater, 'videomsg');
            picURLField = new TextFieldClass('pic_url', null, picUpdater, 'picmsg'),
        pl('#pic_url, #picuploadfile').bind('click', function() { self.imagepanel.runningSlideshow = false; });
        pl('#logouploadiframe').bind({
            load: function() {
                var iframe = pl('#logouploadiframe').get(0),
                    iframehtml = iframe && iframe.contentDocument && iframe.contentDocument.body ? iframe.contentDocument.body.innerHTML : '',
                    uploadurlmatch = iframehtml.match(/upload_url&gt;(.*)&lt;\/upload_url/),
                    uploadurl = uploadurlmatch && uploadurlmatch.length === 2 ? uploadurlmatch[1] : null,
                    dataurimatch = iframehtml.match(/value&gt;(.*)&lt;\/value/),
                    datauri = dataurimatch && dataurimatch.length === 2 ? dataurimatch[1] : null,
                    iframeloc = iframe.contentWindow.location,
                    errorMsg = iframeloc && iframeloc.search && iframeloc.search ? decodeURIComponent(iframeloc.search.replace(/^[?]errorMsg=/, '')) : null;
                if (uploadurl && uploadurl !== 'null') {
                    self.base.listing.upload_url = uploadurl;
                    self.setUploadUrls();
                }
                if (uploadurl && datauri && !errorMsg) {
                    self.base.listing.logo = datauri;
                    self.displayLogo(datauri);
                    self.base.displayCalculated();
                    pl('#logomsg').removeClass('errorcolor').removeClass('inprogress').addClass('successful').text('Logo uploaded');
                }
                else {
                    pl('#logomsg').addClass('errorcolor').text(errorMsg || 'Unable to upload logo');
                }
                pl('#logo_url, #logouploadfile').attr({value: ''});
            }
        });
        pl('#picuploadiframe').bind({
            load: function() {
                var iframe = pl('#picuploadiframe').get(0),
                    iframehtml = iframe && iframe.contentDocument && iframe.contentDocument.body ? iframe.contentDocument.body.innerHTML : '',
                    uploadurlmatch = iframehtml.match(/upload_url&gt;(.*)&lt;\/upload_url/),
                    uploadurl = uploadurlmatch && uploadurlmatch.length === 2 ? uploadurlmatch[1] : null,
                    picnum = pl('#picnum').text(),
                    picurl = '/listing/picture/' + self.base.listing.listing_id + '/' + picnum,
                    iframeloc = iframe.contentWindow.location,
                    errorMsg = iframeloc && iframeloc.search && iframeloc.search ? decodeURIComponent(iframeloc.search.replace(/^[?]errorMsg=/, '')) : null;
                if (uploadurl && uploadurl !== 'null') {
                    self.base.listing.upload_url = uploadurl;
                    self.setUploadUrls();
                }
                if (uploadurl && picurl && !errorMsg) {
                    self.base.listing['pic' + picnum] = true;
                    self.imagepanel.enableImage(picnum);
                    pl('#picmsg').removeClass('errorcolor').removeClass('inprogress').addClass('successful').text('Image uploaded');
                }
                else {
                    pl('#picmsg').addClass('errorcolor').text(errorMsg || 'Unable to upload image');
                }
                pl('#pic_url, #picuploadfile').attr({value: ''});
            }
        });
        pl('#logouploadfile').bind({
            change: function() {
                pl('#logomsg').removeClass('errorcolor').addClass('inprogress').text('Uploading...');
                pl('#logouploadform').get(0).submit();
                return false;
            }
        });
        pl('#picuploadfile').bind({
            change: function() {
                pl('#picmsg').removeClass('errorcolor').addClass('inprogress').text('Uploading...');
                pl('#picuploadform').get(0).submit();
                return false;
            }
        });
        logoURLField.fieldBase.setDisplayName('LOGO URL');
        logoURLField.fieldBase.addValidator(ValidatorClass.prototype.isURLEmptyOk);
        logoURLField.fieldBase.isEmptyNoUpdate = true;
        logoURLField.bindEvents();
        picURLField.fieldBase.setDisplayName('IMAGE URL');
        picURLField.fieldBase.addValidator(ValidatorClass.prototype.isURLEmptyOk);
        picURLField.fieldBase.isEmptyNoUpdate = true;
        picURLField.bindEvents();
        videoURLField.fieldBase.validator.preValidateTransform = VideoCheckClass.prototype.preformat;
        videoURLField.fieldBase.setDisplayName('VIDEO URL');
        videoURLField.fieldBase.addValidator(ValidatorClass.prototype.isVideoURL);
        videoURLField.bindEvents();
        self.displayLogo(datauri);
        self.displayImages();
        self.displayVideo(videourl);
        self.setUploadUrls();
        pl('#logo_url, #pic_url, #logouploadfile, #picuploadfile').attr({value: ''});
        this.base.displayCalculated();
        this.base.bindNavButtons(this.genNextValidator());
        this.base.bindTitleInfo();
        pl('#newlistingmediawrapper').show();
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

