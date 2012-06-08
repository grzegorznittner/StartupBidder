`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="new-listing-bmc-page">
<div id="wrap">
<div id="newlistingmain">
'
include(header.m4)
`
<div class="container">

<!-- begin banner -->
    <div class="bannertextsmall">NEW COMPANY WIZARD - BUSINESS MODEL CANVAS</div>

    <div class="boxstep last">
        <a href="new-listing-basics-page.html"><span class="boxstep1"><div class="boxsteptext">BASICS</div></span></a>
        <a href="new-listing-media-page.html"><span class="boxstep2"><div class="boxsteptext">MEDIA</div></span></a>
        <span class="boxstep3 boxstepcomplete"><div class="boxsteptext">BMC</div></span>
        <a href="new-listing-qa-page.html"><span class="boxstep4"><div class="boxsteptext">Q &amp; A</div></span></a>
        <a href="new-listing-financials-page.html"><span class="boxstep5"><div class="boxsteptext">FINANCIALS</div></span></a>
        <a href="new-listing-submit-page.html"><span class="boxstep6"><div class="boxsteptext">SUBMIT</div></span></a>
    </div>

    <div class="boxstep last">
        <span class="boxstepn" id="boxstepn"></span>
        <span class="boxsteptitle">YOU HAVE FILLED <span id="boxsteppct">0</span>% OF YOUR COMPANY PROFILE</span>
    </div>
<!-- end banner -->

<!-- left column -->
<div class="span-16">

    <div class="boxtitle">INSTRUCTIONS</div>

    <div class="boxpanel">
        <p>Building a Business Model Canvas gives a one-page snapshot view of
        your business model.  Answer the questions below and the overview is
        automatically constructed at the bottom of the page.  This is the exact canvas
        which will be shown on the listing page.</p>

        <p>Enter the text for each section as you would like it to appear on
        the presentation.  Use lines starting with "*" for bullet points and a blank
        line for extra space.  Enter a tab or click out of the field to update the
        presentation at the bottom of the page.</p>

        <p>Your goal here is to present specific relevant detail that can help someone
        to evaluate your idea.  Picture the life of a typical junior analyst at a
        venture firm.  They receive dozens, if not hundreds of these startup
        applications each week.  They have only a short time to glance over each one
        and see if it merits further investigation.  You want to show that you have a
        vision, with a definite plan, and the right team to execute it.  Avoid
        platitudes.  Avoid generalizations.  Talk about your market, your product, your
        price, your strategy, and just for kicks, how you&rsquo;re going to change the world.
        </p>
    </div>

    <div class="formitem clear">
        <span class="inputmsg" id="newlistingmsg"></span>
    </div>

    <div>
        <div class="formitem clear">
            <a href="#" id="prevbuttonlink">
                <span class="span-3 inputbutton" id="prevbutton">
                    PREV
                </span>
            </a>
            <a href="#" id="nextbuttonlink">
                <span class="push-10 span-3 inputbutton" id="nextbutton">
                    NEXT
                </span>
            </a>
        </div>
    </div>

</div> <!-- end left column -->

<!-- right column -->
<div class="span-8 last">

    <div class="boxtitle">SUMMARY PREVIEW</div>
    <div id="summarypreview"></div>
<!--
    <div class="boxtitle clear">TIPS</div>
    <div class="sidebox">
<p>
Your goal here is to present specific relevant detail that can help someone to evaluate your idea.  Picture the life of a typical junior analyst at a venture firm.  They receive dozens, if not hundreds of these startup applications each week.  They have only a short time to glance over each one and see if it merits further investigation.  You want to show that you have a vision, with a definite plan, and the right team to execute it.  Avoid platitudes.  Avoid generalizations.  Talk about your market, your product, your price, your strategy, and just for kicks, how you&rsquo;re going to change the world.
</p>
    </div>
-->
</div>
<!-- end right column -->

'
include(bmc.m4)
include(bmc-editable.m4)
`

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
  <script src="js/modules/bmc.js"></script>
  <script src="js/modules/newlistingbase.js"></script>
  <script src="js/modules/newlistingbmcpage.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
