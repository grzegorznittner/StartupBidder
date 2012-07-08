`
<!doctype html>
<html lang="en">
'
include(mainhead.m4)
`
<body class="discover-page">
<div id="fb-root"></div> <!-- for fb like button -->
<div id="wrap">
<div id="main">
'
include(header.m4)
`
<div class="container">
'
include(banner.m4)
`
<div class="span-24 initialhidden welcomevideo" id="welcomevideo">
    <div class="welcomevideoclosex closexicon" id="closexicon"></div>
    <div class="boxtitle">WELCOME VIDEO <a href="http://youtu.be/hq5gaQ1FtAU">(VIEW DIRECT ON YOUTUBE)</a></div>
    <div class="welcomevideocontainer">
       <iframe width="950" height="570" src="http://www.youtube.com/embed/hq5gaQ1FtAU" frameborder="0" allowfullscreen></iframe>
    </div>
</div>

<div class="span-24 preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>

<div class="initialhidden wrapper">
'
include(searchbox.m4)
`
<!-- left column -->
<div class="span-16">

    <div id="edited_listing_wrapper" class="initialhidden">
        <div class="boxtitle smokegrey">YOUR IN-PROGRESS LISTING</div>
        <div id="edited_listing"></div>
    </div>

    <div id="users_listings_wrapper" class="initialhidden">
        <div class="boxtitle smokegrey">YOUR ACTIVE LISTINGS</div>
        <div id="users_listings"></div>
    </div>

    <div id="monitored_listings_wrapper" class="initialhidden">
        <div class="boxtitle smokegrey">YOUR WATCHED LISTINGS</div>
        <div id="monitored_listings"></div>
    </div>

    <div class="boxtitle smokegrey">TOP LISTINGS</div>
    <div id="top_listings"></div>
<!--
    <div class="boxtitle smokegrey">MOST FUNDED</div>
    <div id="valuation_listings"></div>
-->
    <div class="boxtitle smokegrey">JUST LISTED</div>
    <div id="latest_listings"></div>

</div>
<!-- end left column -->
'
include(main-rightcol.m4)
`
</div> <!-- end wrapper -->

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
  <!-- JavaScript at the bottom for fast page loading -->
  <script src="js/libs/prevel.min.js"></script>
  <script src="js/modules/base.js"></script>
  <script src="js/modules/companylist.js"></script>
  <script src="js/modules/discoverpage.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
