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
include(banner.m4)
`
<div class="container">

<!-- left column -->
<div class="span-16">
    <div class="span-16 preloader">
        <div class="preloaderfloater"></div>
        <div class="preloadericon"></div>
    </div>

    <div class="initialhidden wrapper">

        <div class="boxtitle smokegrey">TOP LISTINGS</div>
        <div id="top_listings"></div>
    <!--
        <div class="boxtitle smokegrey">MOST FUNDED</div>
        <div id="valuation_listings"></div>
    -->
        <div class="boxtitle smokegrey">JUST LISTED</div>
        <div id="latest_listings"></div>

    </div> <!-- end wrapper -->
    
</div> <!-- end left column -->

<div class="initialhidden wrapper">

    <div class="span-8 preloader">
        <div class="preloaderfloater"></div>
        <div class="preloadericon"></div>
    </div>

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
