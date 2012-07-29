function NewListingMediaClass() {
    this.base = new NewListingBaseClass();
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
            videourl = self.base.listing.video,
            postVideo = function(json) {
                var url = json && json.listing && json.listing.video ? json.listing.video : null;
                if (url) {
                    self.base.listing.video = url;
                    self.displayVideo(url);
                    self.base.displayCalculated();
                }
            },
            videoUpdater = this.base.getUpdater('video', VideoCheckClass.prototype.preformat, postVideo),
            videoURLField = new TextFieldClass('video', this.base.listing.video, videoUpdater, 'videomsg');
            websiteField = new TextFieldClass('website', this.base.listing.website, this.base.getUpdater('website'), 'newlistingmediamsg');
        this.base.fields = [];
        this.base.fieldMap = {};
        websiteField.fieldBase.setDisplayName('WEBSITE');
        websiteField.fieldBase.addValidator(ValidatorClass.prototype.isURL);
        websiteField.fieldBase.validator.postValidator = this.base.genDisplayCalculatedIfValid(websiteField);
        websiteField.bindEvents();
        this.base.fields.push(websiteField);
        this.base.fieldMap.website = websiteField;
        videoURLField.fieldBase.validator.preValidateTransform = VideoCheckClass.prototype.preformat;
        videoURLField.fieldBase.setDisplayName('VIDEO URL');
        videoURLField.fieldBase.addValidator(ValidatorClass.prototype.isVideoURL);
        videoURLField.bindEvents();
        self.displayVideo(videourl);
        this.base.displayCalculated();
        this.base.bindNavButtons(this.genNextValidator());
        this.base.bindTitleInfo();
        this.base.bindInfoButtons();
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

