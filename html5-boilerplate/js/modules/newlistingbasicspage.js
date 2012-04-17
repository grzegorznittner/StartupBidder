function NewListingBasicsClass() {
    var base = new NewListingBaseClass();
    base.prevPage = '',
    base.nextPage = '/new-listing-media-page.html';
    this.base = base;
}
pl.implement(NewListingBasicsClass, {
    load: function() {
        var self = this,
            completeFunc = function(json) {
                var listing = json && json.listing ? json.listing : {},
                    categories = json && json.categories ? json.categories : {},
                    header = new HeaderClass();
                header.setLogin(json);
                self.base.store(listing);
                self.storeCategories(categories);
                self.display();
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
        if (this.base.listing.status !== 'new') {
            document.location = 'new-listing-submitted-page.html';
        }
        if (!this.bound) {
            this.bindEvents();
            this.bound = true;
        }
    },
    bindEvents: function() {
        var textFields = ['title', 'category', 'mantra', 'website', 'founders', 'contact_email', 'address'],
            validators = {
                title: ValidatorClass.prototype.isNotEmpty,
                category: ValidatorClass.prototype.isSelected,
                mantra: ValidatorClass.prototype.makeLengthChecker(5, 140),
                website: ValidatorClass.prototype.isURL,
                founders: ValidatorClass.prototype.makeLengthChecker(5, 256),
                contact_email: ValidatorClass.prototype.isEmail,
                address: ValidatorClass.prototype.isNotEmpty
            },
            classes = {
                title: TextFieldClass,
                category: SelectFieldClass,
                mantra: TextFieldClass,
                website: TextFieldClass,
                founders: TextFieldClass,
                contact_email: TextFieldClass,
                address: TextFieldClass
            },
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
            field = new (classes[id])(id, this.base.listing[id], updater, 'newlistingmsg');
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
        this.displayCategories();
        this.addMap(this.genPlaceUpdater());
        this.bindAddressEnterSubmit();
        this.base.bindNavButtons();
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
                            pl('#newlistingmsg').html('<span class="attention">Could not geocode results: ' + status + '</span>');
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
                ajax = new AjaxClass('/listing/update_address', 'newlistingmsg', completeFunc);
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

