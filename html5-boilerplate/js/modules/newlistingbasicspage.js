function NewListingBasicsClass() {
    var base = new NewListingBaseClass(),
        categoryCompleteFunc = function(json) {
            var makeOptions = function(json) {
                    var options = [],
                        sorter = function(a, b) {
                            return a[0] - b[0];
                        },
                        cat,
                        catid;
                    for (catid in json) {
                        cat = json[catid];
                        options.push([cat, catid]);
                    }
                    return options.sort(sorter);
                },
                options = makeOptions(json),
                makeHtml = function(options) {
                    var i, option, cat, catid,
                        html = '<option value="0">Select...</option>\n';
                    for (i = 0; i < options.length; i++) {
                        option = options[i];
                        cat = option[0];
                        catid = option[1];
                        html += '<option value="' + catid + '">' + cat + '</option>\n';
                    }
                    return html;
                },
                optionsHtml = makeHtml(options);
            pl('#category').html(optionsHtml);
        },
        ajax = new AjaxClass('/listing/categories', 'newlistingmsg', categoryCompleteFunc);
    base.prevPage = '',
    base.nextPage = '/new-listing-qa-page.html';
    this.base = base;
    ajax.call();
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
    bindEvents: function() {
        var textFields = ['title', 'category', 'mantra', 'address', 'website'],
            validators = {
                title: ValidatorClass.prototype.isNotEmpty,
                category: ValidatorClass.prototype.isSelected,
                mantra: ValidatorClass.prototype.makeLengthChecker(5, 140),
                website: ValidatorClass.prototype.isURL,
                address: ValidatorClass.prototype.isNotEmpty
            },
            id,
            field;
        this.base.fields = [];
        for (i = 0; i < textFields.length; i++) {
            id = textFields[i];
            field = new TextFieldClass(id, this.base.listing[id], this.base.getUpdater(id), 'newlistingmsg');
            if (id === 'address') {
                field.fieldBase.setDisplayName('LOCATION');
            }
            field.fieldBase.addValidator(validators[id]);
            field.fieldBase.validator.postValidator = this.base.genDisplayCalculatedIfValid(field);
            field.bindEvents();
            this.base.fields.push(field);
        } 
        this.addMap();
        this.base.bindNavButtons();
    },
    addMap: function() {
        var autoField = pl('#address').get(0),
            autoOptions = {
                types: [ 'geocode' ]
            },
            mapField = pl('#addressmap').get(0),
            mapOptions = {
                center: new google.maps.LatLng(-33.8688, 151.2195),
                zoom: 13,
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

