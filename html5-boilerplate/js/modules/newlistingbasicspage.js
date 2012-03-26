function NewListingBasicsClass() {
    var base = new NewListingBaseClass();
    base.prevPage = '',
    base.nextPage = '/new-listing-bmc-page.html';
    this.base = base;
}
pl.implement(NewListingBasicsClass, {
    load: function() {
        var self = this,
            completeFunc = function(json) {
                var listing = json && json.listing ? json.listing : {},
                    header = new HeaderClass();
                header.setLogin(json);
                self.base.store(listing);
                self.display();
            },
        ajax = new AjaxClass('/listings/create', 'newlistingmsg', completeFunc);
        ajax.setPost();
        ajax.call();
    },
    loadCategories: function() {
        var self = this,
            categoryCompleteFunc = function(json) {
                var makeOptions = function(json) {
                        var options = [],
                            cat,
                            catid;
                        for (catid in json) {
                            cat = json[catid];
                            options.push(cat);
                        }
                        return options.sort();
                    },
                    options = makeOptions(json);
                self.base.fieldMap['category'].setOptions(options);
            },
            ajax = new AjaxClass('/listing/categories', 'newlistingmsg', categoryCompleteFunc);
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
    bindEvents: function() {
        var textFields = ['title', 'category', 'mantra', 'address', 'website', 'contact_email'],
            validators = {
                title: ValidatorClass.prototype.isNotEmpty,
                category: ValidatorClass.prototype.isSelected,
                mantra: ValidatorClass.prototype.makeLengthChecker(5, 140),
                website: ValidatorClass.prototype.isURL,
                contact_email: ValidatorClass.prototype.isEmail,
                address: ValidatorClass.prototype.isNotEmpty
            },
            classes = {
                title: TextFieldClass,
                category: SelectFieldClass,
                mantra: TextFieldClass,
                website: TextFieldClass,
                contact_email: TextFieldClass,
                address: TextFieldClass
            },
            id,
            field,
            addr;
        this.base.fields = [];
        this.base.fieldMap = {};
        for (i = 0; i < textFields.length; i++) {
            id = textFields[i];
            field = new (classes[id])(id, this.base.listing[id], this.base.getUpdater(id), 'newlistingmsg');
            if (id === 'address') {
                field.fieldBase.setDisplayName('LOCATION');
            }
            if (id === 'contact_email') {
                field.fieldBase.setDisplayName('EMAIL');
            }
            field.fieldBase.addValidator(validators[id]);
            field.fieldBase.validator.postValidator = this.base.genDisplayCalculatedIfValid(field);
            field.bindEvents(id === 'address' ? {noAutoUpdate:true} : {});
            this.base.fields.push(field);
            this.base.fieldMap[id] = field;
        } 
        this.loadCategories();
        this.addMap(this.placeUpdater);
        this.base.bindNavButtons();
    },
    placeUpdater: function(place) {
        var self = this,
            completeFunc = function(json) {
                var val = json.formatted_address;
                pl('#address').attr({value: val});
            },
            data = { listing: {
                        address: place.formatted_address,
                        city: place.vicinity,
                        state: '',
                        country: '',
                        latitude: place.geometry.location.Ua,
                        longitude: place.geometry.location.Va
                    } },
            ajax = new AjaxClass('/listing/update_field', 'newlistingmsg', completeFunc),
            addrcomp,
            type,
            i,
            j;
            for (i = 0; i < place.address_components.length; i++) {
                addrcomp = place.address_components[i];
                for (j = 0; j < addrcomp.types.length; j++) {
                    type = addrcomp.types[j];
                    if (type === 'country') {
                        data.listing.country = addrcomp.long_name;
                    }
                    else if (type === 'administrative_area_level_1') {
                        data.listing.state = addrcomp.long_name;
                    }
                }
            }
        ajax.setPostData(data);
        ajax.call();
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
                mapTypeId: google.maps.MapTypeId.ROADMAP
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

