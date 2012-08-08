`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="login-page">
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
            <span class="inputmsg" id="addlistingmsg"></span>
        </div>
    </div>
<!-- end banner -->

<div class="span-24">

    <div class="addlistingtitle">Sign in to startupbidder to proceed</div>

    <div class="addlistingloginrow">
        <a id="google_login" href="">
            <div class="addlistinglogin headericon headersignin"></div>
        </a>
        <a id="twitter_login" href="">
            <div class="addlistinglogin headericon headertwittersignin"></div>
        </a>
        <a id="fb_login" href="">
            <div class="addlistinglogin headericon headerfbsignin"></div>
        </a>
    </div>

</div> <!-- end span-24 -->
</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script src="js/modules/loginpage.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
