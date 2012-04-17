function MapPageClass() {}
pl.implement(MapPageClass,{
    load: function(completeFunc) {
        var title = 'NEARBY COMPANIES',
            ajax = new AjaxClass('/listings/all-listing-locations/', 'listingsmsg', completeFunc);
        pl('#listingstitle').html(title);
        pl('#welcometitle').html('Find a startup near you!');
        pl('#welcometext').html('Browse the map below to see companies nearby you and around the world');
        ajax.call();
    },
    store: function(json) {
        this.maplistings = (json && json.map_listings) ? json.map_listings : [];
    },
    display: function() {
        var self = this,
            defaultCenter = new google.maps.LatLng(20, 0),
            defaultZoom = 2;
        this.displayMap(defaultCenter, defaultZoom);
        if (navigator && navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                function(position) {
                    var coords = position.coords,
                        center = new google.maps.LatLng(coords.latitude, coords.longitude);
                    self.map.setZoom(6);
                    self.map.panTo(center);
                },
                function() {
                },
                {
                    maximumAge: 6000000
                }
            );
        }
    },
    displayMap: function(center, zoom) {
        var map = new google.maps.Map(pl('#map').get(0), {
                center: center,
                zoom: zoom,
                mapTypeId: google.maps.MapTypeId.ROADMAP
            }),
            i,
            maplisting,
            listingid,
            latitude,
            longitude,
            latLng,
            marker,
            markers = [],
            markerCluster,
            infowindow = new google.maps.InfoWindow(),
            genInfoDisplay = function(listingid, marker, infowindow) {
                return function() {
                    var completeFunc = function(json) {
                            var listing, tile, el;
                            if (json && json.listing) {
                                listing = json.listing;
                                tile = new CompanyTileClass({json: listing}),
                                el = pl('<div>').addClass('infowindowwrapper').html(tile.makeInfoWindowHtml()).get(0);
                                infowindow.setContent(el);
                            }
                        },
                        ajax = new AjaxClass('/listings/get/' + listingid, 'infowindowmsg', completeFunc),
                        el = pl('<div>').addClass('infowindowwrapper').html('<span class="inputmsg inprogress" id="infowindowmsg">Loading...</span>').get(0);
                    infowindow.setContent(el);
                    infowindow.open(map, marker);
                    ajax.call();
                }
            };
        this.map = map;
        for (i = 0; i < this.maplistings.length; i++) {
            maplisting = this.maplistings[i];
            listingid = maplisting[0];
            latitude = maplisting[1];
            longitude = maplisting[2];
            latLng = new google.maps.LatLng(latitude, longitude);
            marker = new google.maps.Marker({
                cursor: 'pointer',
                position: latLng,
                raiseOnDrag: false
            });
            google.maps.event.addListener(marker, 'click', genInfoDisplay(listingid, marker, infowindow));
            markers.push(marker);
        }
        markerCluster = new MarkerClusterer(map, markers, { maxZoom: 15 });
    }
});

function NearbyPageClass() {}
pl.implement(NearbyPageClass,{
    loadPage: function() {
        var mapPage = new MapPageClass(),
            completeFunc = function(json) {
                var header = new HeaderClass();
                header.setLogin(json);
                mapPage.store(json);
                mapPage.display();
            };
        mapPage.load(completeFunc);
    }
});

(new NearbyPageClass()).loadPage();
