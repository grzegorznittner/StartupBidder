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

<!-- right column -->
<div class="span-8 last">
    <div class="boxtitle">ADD YOUR STARTUP</div>
    <a href="#" id="postnowbtnlink"><div class="sideboxbutton">POST NOW</div></a>

    <div id="investbox">
        <div class="boxtitle">FUND A STARTUP</div>
        <a href="main-page.html?type=closing"><div class="sideboxbutton">INVEST NOW</div></a>
    </div>

    <div class="boxtitle">INDUSTRIES</div>
    <div class="sidebox">
        <span class="attention" id="categorymsg"></span>
        <span class="sideboxcol1 clear">
            <ul class="sideboxlist" id="categorydivcol1"></ul>
        </span>
        <span class="sideboxcol2">
            <ul class="sideboxlist" id="categorydivcol2"></ul>
        </span>
    </div>
     
    <div class="boxtitle">LOCATIONS</div>
    <div class="sidebox">
        <span class="attention" id="locationmsg"></span>
        <span class="sideboxcol1 clear">
            <ul class="sideboxlist" id="locationdivcol1"></ul>
        </span>
        <span class="sideboxcol2">
            <ul class="sideboxlist" id="locationdivcol2"></ul>
        </span>
    </div>
</div> <!-- end right column -->

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
