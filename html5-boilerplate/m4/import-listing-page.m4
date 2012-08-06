`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="import-listing-page">
<div id="wrap">
<div id="newlistingmain">
'
include(header.m4)
`
<div class="container preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>
<div class="container initialhidden wrapper">

<!-- begin banner -->
    <div class="span-24">
        <div class="formitem clear">
            <span class="inputmsg" id="importlistingmsg"></span>
        </div>
    </div>
<!-- end banner -->

<div class="span-24">

    <div class="addlistingtitle">Search for your <span id="importcorporapp"></span> on <span id="importtype"></span></div>

    <div class="addlistinginput">
        <div class="addlistingsearchcontainer">
            <input class="text addlistingsearch" type="text" name="query" id="importquery" value="Search"></input>
            <input class="addlistingsearchbutton" width="32px" type="image" height="32px" src="/img/icons/search2.png" alt="search" id="importbutton">
        </div>
    </div>

    <div class="addlistingcontainer initialhidden" id="importcontainer">
        <div class="addlistinglist" id="importlist"></div> 
    </div>

    <div class="addlistingtitle">or</div>
    
    <a href="/new-listing-basics-page.html">
        <div class="addlistingbutton investbutton">CREATE</div>
    </a>

    <div class="addlistingtitle addlistingbottom">a new listing</div>

</div> <!-- end span-24 -->
</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
  <script src="http://maps.googleapis.com/maps/api/js?libraries=places&sensor=true&language=en-GB"></script>
  <script src="js/libs/prevel.min.js"></script>
  <script src="js/modules/base.js"></script>
  <script src="js/modules/importlistingpage.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
