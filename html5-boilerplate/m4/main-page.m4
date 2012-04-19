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
include(searchbox.m4)
`
<!-- left column -->
<div class="span-16">
    <div class="boxtitle smokegrey" id="listingstitle"></div>

    <!-- companydiv -->
    <div id="companydiv">
        loading...
    </div>
    <!-- end companydiv -->

</div>
<!-- end left column -->
'
include(main-rightcol.m4)
`
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
  <script src="js/modules/socialplugins.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
