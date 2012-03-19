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
                var datauri = json && json.listing && json.listing.logo ? json.listing.logo : null;
                if (datauri) {
                    self.base.listing.logo = datauri;
                    self.displayLogo(datauri);    
                    self.base.displayCalculated();
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
                    uploadurlmatch = iframe.match(/upload_url.*(https?:\/\/.*\/upload\/[A-Za-z0-9]*).*upload_url/),
                    dataurimatch = iframe.match(/value.*(data:image\/[a-z]*;base64,[A-Za-z0-9+\/]*=*).*value/),
                    uploadurl = uploadurlmatch && uploadurlmatch.length === 2 ? uploadurlmatch[1] : null,
                    datauri = dataurimatch && dataurimatch.length === 2 ? dataurimatch[1] : null;
                if (uploadurl) {
                    self.base.listing.logo_upload = uploadurl;
                    pl('#logouploadform').attr({action: uploadurl});
                }
                if (datauri) {
                    self.base.listing.logo = datauri;
                    self.displayLogo(datauri);
                    self.base.displayCalculated();
                }
            }
        });
        pl('#LOGO').bind({
            change: function() {
                pl('#logouploadform').get(0).submit();
                return false;
            }
        });
        pl('#logoloadurlbutton').bind({
            click: function() {
                var msg = logoURLField.validate();
                if (msg === 0) {
                    logoURLField.update();
                }
                else {
                    pl('#logomsg').text(msg);
                }
            }
        });
        pl('#videobutton').bind({
            click: function() {
                var msg = videoURLField.validate();
                if (msg === 0) {
                    videoURLField.update();
                }
                else {
                    pl('#videomsg').text(msg);
                }
            }
        });
        pl('#logouploadform').attr({action: uploadurl});
        logoURLField.fieldBase.setDisplayName('LOGO UPLOAD URL');
        logoURLField.fieldBase.addValidator(ValidatorClass.prototype.isURL);
        logoURLField.bindEvents({noAutoUpdate: true});
        videoURLField.fieldBase.validator.preValidateTransform = VideoCheckClass.prototype.preformat;
        videoURLField.fieldBase.setDisplayName('VIDEO URL');
        videoURLField.fieldBase.addValidator(ValidatorClass.prototype.isVideoURL);
        videoURLField.bindEvents({noAutoUpdate: false});
        self.displayLogo(datauri);
        self.displayVideo(videourl);
        this.base.displayCalculated();
        this.base.bindNavButtons(this.genNextValidator());
    },
    genNextValidator: function() {
        var self = this;
        return function() {
            var msgs = [];
            if (!self.base.listing.logo) {
                msgs.push("LOGO: you must have a logo image.");
            }
            if (!self.base.listing.video) {
                msgs.push("VIDEO: you must have a video presentation.");
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

