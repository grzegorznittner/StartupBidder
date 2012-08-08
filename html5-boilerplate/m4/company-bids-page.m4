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
include(company-banner.m4)
companybannermacro(`', `', `', `companynavselected', `', `')
`
<div class="container preloader">
     <div class="preloaderfloater"></div>
     <div class="preloadericon"></div>
</div>

<div class="container initialhidden wrapper">
'
include(company-order-book.m4)
`

<div class="boxtitlegap smokegrey clear">MAKE PRIVATE BID
    <span class="newlistingtitlemsg" id="bidtitlemsg"></span>
</div>
<div class="boxpanel boxpanelfull">
    <p>Sign in to place a bid.</p>
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
