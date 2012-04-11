function MapPageClass() {
    this.url = '/listings/top';
    this.data = { max_results: 20 };
};
pl.implement(MapPageClass,{
    load: function(completeFunc) {
        var title = 'NEARBY COMPANIES',
            ajax = new AjaxClass(this.url, 'listingsmsg', completeFunc);
        pl('#listingstitle').html(title);
        pl('#welcometitle').html('Find a startup near you!');
        pl('#welcometext').html('Browse the map below to see companies nearby you and around the world');
        ajax.ajaxOpts.data = this.data;
        ajax.call();
    },
    store: function(json) {
        this.listings = (json && json.listings) ? json.listings : [];
    },
    display: function() {
        var self = this,
            defaultCenter = new google.maps.LatLng(51.47777, 0),
            defaultZoom = 2;
        if (navigator && navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                function(position) {
                    console.log('noerror');
                    var coords = position.coords,
                        center = new google.maps.LatLng(coords.latitude, coords.longitude);
                    self.displayMap(center, 6);
                },
                function() {
                    console.log('error');
                    self.displayMap(defaultCenter, defaultZoom);
                },
                { maximumAge: 600000 }
            );
        }
        else {
            this.displayMap(defaultCenter, defaultZoom);
        }
    },
    displayMap: function(center, zoom) {
        console.log('display map');
        var map = new google.maps.Map(pl('#map').get(0), {
                center: center,
                zoom: zoom,
                mapTypeId: google.maps.MapTypeId.ROADMAP
            }),
            i,
            listing,
            latLng,
            marker,
            markers = [],
            markerCluster,
            genInfoDisplay = function(listing, marker) {
                var tile = new CompanyTileClass(),
                    info;
                tile.store(listing);
                info = new google.maps.InfoWindow({
                    content: pl('<div>').html(tile.makeHtml()).get(0)
                });
                return function() {
                    info.open(map, marker);
                }
            };
        for (i = 0; i < this.listings.length; i++) {
            listing = this.listings[i];
            latLng = new google.maps.LatLng(listing.latitude, listing.longitude);
            marker = new google.maps.Marker({
                cursor: 'pointer',
                position: latLng,
                raiseOnDrag: false,
                title: listing.title
            });
            google.maps.event.addListener(marker, 'click', genInfoDisplay(listing, marker));
            markers.push(marker);
        }
        markerCluster = new MarkerClusterer(map, markers, { maxZoom: 15 });
    }
});

function NearbyPageClass() {}
pl.implement(NearbyPageClass,{
    loadPage: function() {
        console.log('here');
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
