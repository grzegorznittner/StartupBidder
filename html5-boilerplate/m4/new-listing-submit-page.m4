`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="new-listing-submit-page">
<div id="wrap">
<div id="newlistingsubmitmain">
'
include(header.m4)
`
<div class="container">

<!-- begin banner -->
    <div class="bannertextsmall">NEW LISTING WIZARD - SUBMIT</div>

    <div class="span-16">
        <div class="boxstep last">
            <a href="new-listing-basics-page.html"><span class="boxstep1" id="basicsboxstep"><div class="boxsteptext">BASICS</div></span></a>
            <a href="new-listing-media-page.html"><span class="boxstep2" id="mediaboxstep"><div class="boxsteptext">MEDIA</div></span></a>
            <a href="new-listing-bmc-page.html"><span class="boxstep3" id="bmcboxstep"><div class="boxsteptext">MODEL</div></span></a>
            <a href="new-listing-qa-page.html"><span class="boxstep4" id="qaboxstep"><div class="boxsteptext">SLIDES</div></span></a>
            <a href="new-listing-financials-page.html"><span class="boxstep5" id="financialsboxstep"><div class="boxsteptext">FINANCIALS</div></span></a>
            <span class="boxstep6 boxstepcomplete"><div class="boxsteptext">SUBMIT</div></span>
        </div>
        <div class="boxstep last">
            <span class="boxstepn" id="boxstepn"></span>
            <span class="boxsteptitle">YOU HAVE FILLED <span id="boxsteppct">0</span>% OF YOUR LISTING PROFILE</span>
        </div>
    </div>
    <div class="span-8 last">
        <a href="#" class="prevbuttonlink">
            <span class="push-1 span-3 smallinputbutton titleprevnextbtn">
                PREV
            </span>
        </a>
        <a href="#" class="nextbuttonlink">
            <span class="push-2 span-3 smallinputbutton titleprevnextbtn">
                SUBMIT
            </span>
        </a>
    </div>

<!-- end banner -->

<!-- left column -->
<div class="span-16">

    <div class="boxtitle">SUBMIT YOUR LISTING</div>
    <div class="boxpanel">
    <p>
You are about to submit your listing in startupbidder.  Please review and check that
all information is accurate as you will not be able to change it once submitted.  The summary preview,
which is displayed in search results, is shown on the right.  The full listing page, which users see
after clicking on your summary, is displayed below.  Submit your listing by clicking <b>SUBMIT</b> below.
After submitting, startupbidder will review your listing, request any needed changes, and then activate it
on the site where it will appear as the latest listing.
    </p>
    </div>

    <div class="formitem clear">
        <span class="inputmsg" id="newlistingmsg"></span>
    </div>

    <div>
        <div class="formitem clear">
            <a href="#" class="nextbuttonlink">
                <span class="push-13 span-3 inputbutton">
                    SUBMIT
                </span>
            </a>
        </div>
    </div>

</div>
<!-- end left column -->

<!-- right column -->
<div class="span-8 last">

    <div class="boxtitle clear">SUMMARY PREVIEW</div>
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

<!-- preview overlay text -->
<div class="previewtext">PREVIEW</div>
<div class="previewtext previewtext2">PREVIEW</div>

<!-- preview overlay click disabler -->
<div class="previewoverlay"></div>

<!-- iframe preview -->
<iframe class="previewiframe" id="previewiframe" allowtransparency="true" frameborder="0" sandbox="allow-same-origin allow-forms allow-scripts" scrolling="auto" src=""> 
</iframe> 
'
include(footer.m4)
`
  <script src="js/libs/prevel.min.js"></script>
  <script src="js/modules/base.js"></script>
  <script src="js/modules/companylist.js"></script>
  <script src="js/modules/forms.js"></script>
  <script src="js/modules/newlistingbase.js"></script>
  <script src="js/modules/newlistingsubmitpage.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
