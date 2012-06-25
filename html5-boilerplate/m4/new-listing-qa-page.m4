`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="new-listing-qa-page">
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

    <div class="bannertextsmall">NEW LISTING WIZARD - SLIDES</div>

    <div class="span-16">
        <div class="boxstep last">
            <a href="new-listing-basics-page.html"><span class="boxstep1"><div class="boxsteptext">BASICS</div></span></a>
            <a href="new-listing-media-page.html"><span class="boxstep2"><div class="boxsteptext">MEDIA</div></span></a>
            <a href="new-listing-bmc-page.html"><span class="boxstep3"><div class="boxsteptext">MODEL</div></span></a>
            <span class="boxstep4 boxstepcomplete"><div class="boxsteptext">SLIDES</div></span>
            <a href="new-listing-financials-page.html"><span class="boxstep5"><div class="boxsteptext">FINANCIALS</div></span></a>
            <a href="new-listing-submit-page.html"><span class="boxstep6"><div class="boxsteptext">SUBMIT</div></span></a>
        </div>
        <div class="boxstep last">
            <span class="boxstepn" id="boxstepn"></span>
            <span class="boxsteptitle">YOU HAVE FILLED <span id="boxsteppct">0</span>% OF YOUR LISTING PROFILE</span>
        </div>
    </div>
    <div class="span-8 last">
        <a href="#" class="prevbuttonlink">
            <span class="push-1 span-3 smallinputbutton titleprevnextbtn hoverlink">
                PREV
            </span>
        </a>
        <a href="#" class="nextbuttonlink">
            <span class="push-2 span-3 smallinputbutton titleprevnextbtn hoverlink">
                NEXT
            </span>
        </a>
    </div>
<div class="span-16">
    <div class="formitem clear">
        <span class="inputmsg" id="newlistingmsg"></span>
    </div>
</div>

'
include(ip.m4)
include(ip-editable.m4)
`

    <div>
        <div class="formitem clear">
            <a href="#" class="nextbuttonlink">
                <span class="push-21 span-3 inputbutton">
                    NEXT
                </span>
            </a>
        </div>
    </div>

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
  <script src="js/modules/ip.js"></script>
  <script src="js/modules/newlistingbase.js"></script>
  <script src="js/modules/newlistingqapage.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
