function NewListingBasicsClass() {
    this.base = new NewListingBaseClass();
}
pl.implement(NewListingBasicsClass, {
    load: function() {
        var self = this,
            completeFunc = function(json) {
                var listing = json && json.listing ? json.listing : {},
                    categories = json && json.categories ? json.categories : {},
                    header = new HeaderClass();
                header.setLogin(json);
                console.log(listing);
                self.base.store(listing);
                self.storeCategories(categories);
                self.display();
                pl('.preloader').hide();
                pl('.wrapper').show();
            },
        ajax = new AjaxClass('/listings/create', 'newlistingmsg', completeFunc);
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
            this.bindEvents();
            this.bound = true;
        }
    },
    bindEvents: function() {
        var textFields = ['title', 'type', 'platform', 'category', 'website', 'founders', 'address', 'mantra', 'summary'],
//        var textFields = ['title', 'type', 'platform', 'category', 'mantra', 'summary', 'website', 'founders', 'contact_email', 'address'],
            validators = {
                title: ValidatorClass.prototype.isNotEmpty,
                type: ValidatorClass.prototype.isSelected,
                platform: ValidatorClass.prototype.isSelected,
                category: ValidatorClass.prototype.isSelected,
                mantra: ValidatorClass.prototype.makeLengthChecker(5, 140),
                summary: ValidatorClass.prototype.makeLengthChecker(15, 2000),
                website: ValidatorClass.prototype.isURL,
                founders: ValidatorClass.prototype.makeLengthChecker(5, 256),
//                contact_email: ValidatorClass.prototype.isEmail,
                address: ValidatorClass.prototype.isNotEmpty
            },
            classes = {
                title: TextFieldClass,
                type: SelectFieldClass,
                platform: SelectFieldClass,
                category: SelectFieldClass,
                mantra: TextFieldClass,
                summary: TextFieldClass,
                website: TextFieldClass,
                founders: TextFieldClass,
//                contact_email: TextFieldClass,
                address: TextFieldClass
            },
            typeOptions = [ ['application', 'Application'], ['company', 'Company'] ],
            platformOptions = [
                ['ios', 'iPhone / iPad'],
                ['android', 'Android Phone / Tablet'],
                ['windows_phone', 'Windows Phone / Tablet'],
                ['desktop', 'Desktop'],
                ['website', 'Website'],
                ['other', 'Other']
            ],
            i,
            id,
            field,
            addr,
            updater;
        this.base.fields = [];
        this.base.fieldMap = {};
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
        console.log(this.base.fieldMap);
        this.base.fieldMap['type'].setOptionsWithValues(typeOptions);
        this.base.fieldMap['platform'].setOptionsWithValues(platformOptions);
        this.displayCategories();
        this.addMap(this.genPlaceUpdater());
        this.bindPreview();
        this.bindAddressEnterSubmit();
        this.base.bindTitleInfo();
        this.base.bindInfoButtons();
        pl('#newlistingbasicswrapper').show();
    },

    bindPreview: function() {
        var self = this;
        pl('#previewbutton').bind('click', function() {
            document.location = '/company-page.html?id=' + self.base.listing.listing_id;
        });
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

