`
<!doctype html>
<html lang="en">
'
include(mainhead.m4)
`
<body class="nearby-page">
<div id="fb-root"></div> <!-- for fb like button -->
<div id="wrap">
<div id="main">
'
include(header.m4)
`
<div class="banner nearbybanner" id="banner">
    <div class="container">
        <span class="bannertext span-24">
            <div class="welcometitle" id="welcometitle">Find a startup near you!</div>
            <div class="welcometext" id="welcometext">...and around the world</div>
        </span>
    </div>
</div> <!-- end banner -->

<div class="container">
<div class="span-24 nearbywrapper">
    <div class="boxtitle smokegrey" id="listingstitle">NEARBY LISTINGS</div>

    <div id="mappanel" class="boxpanel nearbypanel">
        <div id="map" class="nearbymap">
            <div class="span-24 preloadermap">
                <div class="preloaderfloater"></div>
                <div class="preloadericon"></div>
            </div>
        </div>
    </div>
</div>

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="http://maps.googleapis.com/maps/api/js?libraries=maps&sensor=true&language=en-GB"></script>
<script src="http://google-maps-utility-library-v3.googlecode.com/svn/trunk/markerclusterer/src/markerclusterer.js"></script>
<script src="js/modules/base.js"></script>
<script src="js/modules/nearbypage.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
