`
<!doctype html>
<html lang="en">
'
include(mainhead.m4)
`
<body class="main-page">
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
    <div class="boxtitle smokegrey" id="listingstitle"></div>
    <div id="companydiv"></div>
</div>
<!-- end left column -->
'
include(main-rightcol.m4)
`
</div>
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
  <script src="js/modules/mainpage.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
