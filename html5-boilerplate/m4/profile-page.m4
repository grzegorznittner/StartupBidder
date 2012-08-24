`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="profile-page">
<div id="wrap">
<div id="main">
'
include(header.m4)
include(profile-not-found.m4)
`
<div class="container preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>
<div class="container initialhidden wrapper">

<!-- left column -->
<div class="span-16">
    <div id="profilemsg"></div>

    <div id="no_listings_wrapper" class="initialhidden">
        <div class="boxtitle smokegrey"><span class="titleperson">YOUR</span> LISTINGS</div>
        <div class="boxpanel">
            Currently there are no listings added or invested in by <span class="titleperson">you</span>.
            <div id="encourageuser">
                <a href="/add-listing-page.html" class="inputmsg hoverlink profilelink">Add</a> a listing or
                <a href="/main-page.html?type=top" class="inputmsg hoverlink profilelink">invest</a> in one today!
            </div>
        </div>
    </div>

    <div id="edited_listing_wrapper" class="initialhidden">
        <div class="boxtitle smokegrey"><span class="titleperson">YOUR</span> PENDING LISTING</div>
        <div id="edited_listing"></div>
    </div>

    <div id="active_listings_wrapper" class="initialhidden">
        <div class="boxtitle smokegrey"><span class="titleperson">YOUR</span> ACTIVE LISTINGS</div>
        <div id="active_listings"></div>
    </div>

    <div id="admin_posted_listings_wrapper" class="initialhidden">
        <div class="boxtitle smokegrey">LISTINGS AWAITING REVIEW BY ADMIN</div>
        <div id="admin_posted_listings"></div>
    </div>

    <div id="admin_frozen_listings_wrapper" class="initialhidden">
        <div class="boxtitle smokegrey">LISTINGS FROZEN BY ADMIN</div>
        <div id="admin_frozen_listings"></div>
    </div>

    <div id="monitored_listings_wrapper" class="initialhidden">
        <div class="boxtitle smokegrey"><span class="titleperson">YOUR</span> WATCHED LISTINGS</div>
        <div id="monitored_listings"></div>
    </div>

    <div id="withdrawn_listings_wrapper" class="initialhidden">
        <div class="boxtitle smokegrey"><span class="titleperson">YOUR</span> WITHDRAWN LISTINGS</div>
        <div id="withdrawn_listings"></div>
    </div>

    <div id="frozen_listings_wrapper" class="initialhidden">
        <div class="boxtitle smokegrey"><span class="titleperson">YOUR</span> FROZEN LISTINGS</div>
        <div id="frozen_listings"></div>
    </div>

</div> <!-- end left column -->
'
include(profilerightbar.m4)
`
</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script src="js/modules/profile.js"></script>
<script>
(new ProfilePageClass()).loadPage();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
