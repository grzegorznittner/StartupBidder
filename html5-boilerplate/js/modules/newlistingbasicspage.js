function NewListingBasicsClass() {
    var qs = new QueryStringClass();
    this.importtype = qs.vars.importtype;
    this.importid = qs.vars.importid;
    this.base = new NewListingBaseClass();
    this.imagepanel = new ImagePanelClass({ editmode: true });
    this.displayImportType = {
        'AppStore': 'App Store',
        'GooglePlay': 'Google Play',
        'WindowsMarketplace': 'Windows Marketplace',
        'ChromeWebStore': 'Chrome Web Store'
    };
}

pl.implement(NewListingBasicsClass, {

    load: function() {
        var self = this,
            url = this.importtype && this.importid ? '/listing/import' : '/listing/create',
            data = this.importtype && this.importid ? { type: this.importtype, id: this.importid } : null,
            displayImportType = this.displayImportType[this.importtype] || this.importtype,
            complete = function(json) {
                var listing = json && json.listing ? json.listing : {},
                    categories = json && json.categories ? json.categories : {},
                    header = new HeaderClass();
                header.setLogin(json);
                self.base.store(listing);
                self.storeCategories(categories);
                self.display();
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
            error = function(errornum, json) {
                (new HeaderClass()).setLogin(json);
                pl('.preloader').hide();
                pl('.errorwrapper').show();
            },
            ajax = new AjaxClass(url, 'newlistingmsg', complete, null, null, error);
        if (data) {
            pl('#newlistingbanner').text('IMPORTED FROM ' + displayImportType.toUpperCase());
            ajax.ajaxOpts.data = data;
        }
        ajax.setPost();
        ajax.call();
    },

    storeCategories: function(categories) {
        this.categories = categories;
    },

    displayCategories: function() {
        var self = this,
            options = [],
            cat,
            catid;
        for (catid in self.categories) {
            cat = self.categories[catid];
            options.push(cat);
        }
        options.sort();
        self.base.fieldMap['category'].setOptions(options);
    },

    display: function() {
        var status = this.base.listing.status;
        if (status !== 'new') {
            document.location = '/company-page.html?id=' + this.base.listing.listing_id;
        }
        if (!this.bound) {
            this.displayButtons();
            this.bindEvents();
            this.bound = true;
        }
    },

    displayButtons: function() {
        this.displayAskFundingButton();
        this.displayVideoButton();
        this.displayValuationButton();
        this.displayModelButton();
        this.displayPresentationButton();
        this.displayDocumentButton();
    },

    displayAskFundingButton: function() {
        if (this.base.listing.asked_fund) {
            pl('#askfundingbutton').text('EDIT FUNDING');
        }
    },

    displayVideoButton: function() {
        if (this.base.listing.video) {
            pl('#videobutton').text('EDIT VIDEO');
        }
    },

    displayValuationButton: function() {
        if (MicroListingClass.prototype.getHasValuation(this.base.listing)) {
            pl('#vaulationbutton').text('EDIT VALUATION');
        }
    },

    displayModelButton: function() {
        if (MicroListingClass.prototype.getHasBmc(this.base.listing)) {
            pl('#modelbutton').text('EDIT MODEL');
        }
    },

    displayPresentationButton: function() {
        if (MicroListingClass.prototype.getHasIp(this.base.listing)) {
            pl('#presentationbutton').text('PRESENTATION');
        }
    },

    displayDocumentButton: function() {
        if (MicroListingClass.prototype.getHasDoc(this.base.listing)) {
            pl('#documentbutton').text('EDIT DOCUMENTS');
        }
    },

    bindEvents: function() {
        var textFields = ['title', 'type', 'platform', 'category', 'stage', 'address', 'mantra', 'summary'],
            validators = {
                title: ValidatorClass.prototype.isNotEmpty,
                type: ValidatorClass.prototype.isSelected,
                platform: ValidatorClass.prototype.isSelected,
                category: ValidatorClass.prototype.isSelected,
                stage: ValidatorClass.prototype.isSelected,
                mantra: ValidatorClass.prototype.makeLengthChecker(5, 140),
                summary: ValidatorClass.prototype.makeLengthChecker(50, 2000),
                address: ValidatorClass.prototype.isNotEmpty
            },
            classes = {
                title: TextFieldClass,
                type: SelectFieldClass,
                platform: SelectFieldClass,
                category: SelectFieldClass,
                stage: SelectFieldClass,
                mantra: TextFieldClass,
                summary: TextFieldClass,
                address: TextFieldClass
            },
            typeOptions = [ ['application', 'Application'], ['company', 'Company'] ],
            stageOptions = [ ['concept', 'Concept'], ['startup', 'Startup'], ['established', 'Established' ] ],
            platforms = [ 'ios', 'android', 'windows_phone', 'website', 'desktop', 'other' ],
            platformOptions = [],
            platform,
            i,
            id,
            field,
            addr,
            updater;
        this.base.fields = [];
        this.base.fieldMap = {};
        for (i = 0; i < platforms.length; i++) {
            platform = platforms[i];
            platformOptions.push([ platform, PlatformClass.prototype.displayName(platform) ]);
        }
        for (i = 0; i < textFields.length; i++) {
            id = textFields[i];
            updater = this.base.getUpdater(id);
            field = new (classes[id])(id, this.base.listing[id], updater, 'newlistingbasicsmsg');
            if (this.base.displayNameOverrides[id]) {
                field.fieldBase.setDisplayName(this.base.displayNameOverrides[id]);
            }
            field.fieldBase.addValidator(validators[id]);
            field.fieldBase.validator.postValidator = this.base.genDisplayCalculatedIfValid(field);
            if (id !== 'address') {
                field.bindEvents();
            }
            this.base.fields.push(field);
            this.base.fieldMap[id] = field;
        } 
        this.base.fieldMap['type'].setOptionsWithValues(typeOptions);
        this.base.fieldMap['platform'].setOptionsWithValues(platformOptions);
        this.base.fieldMap['stage'].setOptionsWithValues(stageOptions);
        this.bindLogo();
        this.bindImages();
        this.setUploadUrls();
        this.displayCategories();
        this.addMap(this.genPlaceUpdater());
        this.bindAddressEnterSubmit();
        this.base.bindNavButtons();
        this.base.bindTitleInfo();
        this.base.bindInfoButtons();
        if (this.base.listing.status === 'new') {
            pl('.bottombackbutton').hide(); // no back for basic page
        }
        pl('#newlistingbasicswrapper').show();
    },

    bindLogo: function() {
        var self = this,
            datauri = this.base.listing.logo,
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
            logoUpdater = this.base.getUpdater('logo_url', null, postLogo),
            logoURLField = new TextFieldClass('logo_url', null, logoUpdater, 'logomsg');
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
        pl('#logouploadfile').bind({
            change: function() {
                pl('#logomsg').removeClass('errorcolor').addClass('inprogress').text('Uploading...');
                pl('#logouploadform').get(0).submit();
                return false;
            }
        });
        logoURLField.fieldBase.setDisplayName('LOGO URL');
        logoURLField.fieldBase.addValidator(ValidatorClass.prototype.isURLEmptyOk);
        logoURLField.fieldBase.isEmptyNoUpdate = true;
        logoURLField.bindEvents();
        self.displayLogo(datauri);
    },

    bindImages: function() {
        var self = this,
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
            picUpdater = this.base.getUpdater('pic_url', null, postPic, null, function() { return 'pic' + pl('#picnum').text() + '_url' }),
            picURLField = new TextFieldClass('pic_url', null, picUpdater, 'picmsg');
        pl('#pic_url, #picuploadfile').bind('click', function() { self.imagepanel.runningSlideshow = false; });
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
        pl('#picuploadfile').bind({
            change: function() {
                pl('#picmsg').removeClass('errorcolor').addClass('inprogress').text('Uploading...');
                pl('#picuploadform').get(0).submit();
                return false;
            }
        });
        picURLField.fieldBase.setDisplayName('IMAGE URL');
        picURLField.fieldBase.addValidator(ValidatorClass.prototype.isURLEmptyOk);
        picURLField.fieldBase.isEmptyNoUpdate = true;
        picURLField.bindEvents();
        this.displayImages();
    },

    displayLogo: function(dataurl) {
        var url = dataurl && dataurl.indexOf('data:') === 0 ? dataurl : null,
            logobg = url ? 'url(' + url + ') no-repeat scroll center center transparent' : null;
        if (url) {
            pl('#logoimg').css({ background: logobg });
        }
/*
        else {
            pl('#logoimg');
        }
*/
    },

    setUploadUrls: function() {
        pl('#logouploadform, #picuploadform').attr({action: this.base.listing.upload_url});
        pl('#logo_url, #pic_url, #logouploadfile, #picuploadfile').attr({value: ''});
    },

    displayImages: function() {
        this.imagepanel.setListing(this.base.listing).display();
    },

    bindAddressEnterSubmit: function() {
        var self = this;
        pl('#address').bind({
            keyup: function(e) {
                var evt = new EventClass(e),
                    input,
                    geocoder;
                if (evt.keyCode() === 13) {
                    address = pl('#address').attr('value'),
                    geocoder = new google.maps.Geocoder();
                    geocoder.geocode({address: address}, function(results, status) {
                        if (status == google.maps.GeocoderStatus.OK) {
                            self.genPlaceUpdater()(results[0]);
                        }
                        else {
                            pl('#newlistingbasicsmsg').html('<span class="attention">Could not geocode results: ' + status + '</span>');
                        }
                    });
                    return false;
                }
                else {
                    return true;
                }
            }
         });
    },

    genPlaceUpdater: function() {
        var self = this;
        return function(place) {
            var completeFunc = function(json) {
                    if (!json.listing) {
                        return;
                    }
                    self.base.listing.address = json.listing.address;
                    self.base.listing.brief_address = json.listing.brief_address;
                    self.base.listing.latitude = json.listing.latitude;
                    self.base.listing.longitude = json.listing.longitude;
                    pl('#address').attr({value: json.listing.address});
                    self.base.displayCalculated();
                },
                data = { listing: {
                        update_address: place
                       } },
                ajax = new AjaxClass('/listing/update_address', 'newlistingbasicsmsg', completeFunc);
            ajax.setPostData(data);
            ajax.call();
        };
    },

    addMap: function(placeUpdater) {
        var self = this,
            lat = this.base.listing.latitude !== null ? this.base.listing.latitude : 51.4791,
            lng = this.base.listing.longitude !== null ? this.base.listing.longitude : 0,
            autoField = pl('#address').get(0),
            autoOptions = {},
            mapField = pl('#addressmap').get(0),
            mapOptions = {
                center: new google.maps.LatLng(lat, lng),
                zoom: this.base.listing.address ? 13 : 7,
                mapTypeId: google.maps.MapTypeId.ROADMAP,
                draggable: false,
                scrollwheel: false,
                disableDoubleClickZoom: true
            }, 
            addressAuto = new google.maps.places.Autocomplete(autoField, autoOptions),
            addressMap = new google.maps.Map(mapField, mapOptions),
            marker = new google.maps.Marker({map: this.addressMap});
        addressAuto.bindTo('bounds', addressMap);
        google.maps.event.addListener(addressAuto, 'place_changed', function() {
            var place = addressAuto.getPlace(),
                image;
            if (place && place.geometry && place.geometry.location) {
                placeUpdater(place);
                if (place.geometry.viewport) {
                    addressMap.fitBounds(place.geometry.viewport);
                    image = new google.maps.MarkerImage(
                        place.icon,
                        new google.maps.Size(71, 71),
                        new google.maps.Point(0, 0),
                        new google.maps.Point(17, 34),
                        new google.maps.Size(35, 35));
                    marker.setIcon(image);
                    marker.setPosition(place.geometry.location);
                }
                else {
                    addressMap.setCenter(place.geometry.location);
                    addressMap.setZoom(17);
                }
            }
        });
    }

});

function NewListingPageClass() {};

pl.implement(NewListingPageClass, {

    loadPage: function() {
        var newlisting = new NewListingBasicsClass();
        newlisting.load();
    }

});

(new NewListingPageClass()).loadPage();

