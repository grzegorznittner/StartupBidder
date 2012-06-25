`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="new-listing-submitted-page">
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
    <div class="bannersmall">
        <span class="bannertextsmall span-16">THANK YOU!</span>
    </div>

    <div class="boxstep">
        <!-- <span class="boxstepn" id="boxstepn"></span> -->
        <span class="boxsteptitle">YOUR LISTING PROFILE HAS BEEN SUBMITTED</span>
    </div>
<!-- end banner -->

<!-- left column -->
<div class="span-16">

    <div class="boxtitle">DETAILS</div>
    <div class="boxpanel">
    <p>
Your listing has now been submitted to startupbidder.  We will review the listing,
suggest any changes needed if necessary, and then approve the listing.  Once approved,
you will be immediately notified via email.  The listing will then appear as the first
listing on the front page.  Users worldwide will be able to comment on your listing.
If you've enabled bidding, investors will then be able to submit bids, of which you will
be notified by email.
    </p>
    <span class="newlistingmsg" id="newlistingmsg">&nbsp;</span>
    </div>

</div> <!-- end left column -->

<!-- right column -->
<div class="span-8 last">

    <div class="boxtitle">SUMMARY PREVIEW</div>
    <div id="summarypreview"></div>

    <div class="boxtitle clear">WITHDRAW POST</div>
    <div class="newwithdrawmsg attention" id="newwithdrawmsg">Are you sure?<br/>This cannot be undone.</div>
    <a href="#" id="newwithdrawbtn"><div class="sideboxbutton sideboxbuttonsmall">WITHDRAW</div></a>
    <a href="#" class="newwithdrawcancelbtn" id="newwithdrawcancelbtn"><div class="sideboxbutton sideboxbuttonsmall">CANCEL</div></a>

</div>
<!-- end right column -->

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
  <script src="js/libs/prevel.min.js"></script>
  <script src="js/modules/base.js"></script>
  <script src="js/modules/companylist.js"></script>
  <script src="js/modules/forms.js"></script>
  <script src="js/modules/newlistingbase.js"></script>
  <script src="js/modules/newlistingsubmittedpage.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
