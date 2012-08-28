`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="company-page">
<div id="fb-root"></div> <!-- for fb like button -->
<div id="wrap">
<div id="main">
'
include(header.m4)
include(company-not-found.m4)
include(company-banner.m4)
companybannermacro(`', `', `', `companynavselected', `', `')
`
<div class="container preloader">
     <div class="preloaderfloater"></div>
     <div class="preloadericon"></div>
</div>

<div class="container bidscontainer initialhidden wrapper">
'
include(company-order-book.m4)
`

<div class="boxtitle" style="">SIGN IN TO MAKE A BID</div>
<div class="boxpanel boxpanelfull">
    <div style="height: 100px;">
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
</div>

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script src="js/modules/bids.js"></script>
<script>
(new CompanyBidsPageClass()).load();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
