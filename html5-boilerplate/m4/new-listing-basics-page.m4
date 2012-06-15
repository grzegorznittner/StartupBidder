`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="new-listing-basics-page">
<div id="wrap">
<div id="newlistingmain">
'
include(header.m4)
`
<div class="container">

<!-- begin banner -->
    <div class="bannertextsmall">NEW LISTING WIZARD - BASICS</div>

    <div class="span-16">
        <div class="boxstep last">
            <span class="boxstep1 boxstepcomplete"><div class="boxsteptext">BASICS</div></span>
            <a href="new-listing-media-page.html"><span class="boxstep2"><div class="boxsteptext">MEDIA</div></span></a>
            <a href="new-listing-bmc-page.html"><span class="boxstep3"><div class="boxsteptext">MODEL</div></span></a>
            <a href="new-listing-qa-page.html"><span class="boxstep4"><div class="boxsteptext">SLIDES</div></span></a>
            <a href="new-listing-financials-page.html"><span class="boxstep5"><div class="boxsteptext">FINANCIALS</div></span></a>
            <a href="new-listing-submit-page.html"><span class="boxstep6"><div class="boxsteptext">SUBMIT</div></span></a>
        </div>
        <div class="boxstep last">
            <span class="boxstepn" id="boxstepn"></span>
            <span class="boxsteptitle">YOU HAVE FILLED <span id="boxsteppct">0</span>% OF YOUR LISTING PROFILE</span>
        </div>
    </div>
    <div class="span-8 last">
        <a href="#" class="nextbuttonlink">
            <span class="push-5 span-3 smallinputbutton titleprevnextbtn hoverlink">
                NEXT
            </span>
        </a>
    </div>

<!-- end banner -->

<!-- left column -->
<div class="span-16">

    <div class="boxtitle">
        <span class="titletext">BASIC INFO</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">Instructions</label>
            <p> List your project or company here on startupbidder and submit a funding
            request to your choice of investors.  Startupbidder will review the listing and
            suggest any changes before approving.</p>
            <p>NOTE THAT YOU MUST BE THE OWNER OR AN AUTHORIZED AGENT OF THE PROJECT OR BUSINESS TO ASK FOR FUNDING ON THIS SITE.</p>
        </div>
    </div>

    <div class="boxpanel">
        <div class="formitem">
            <label class="inputlabel" for="title">NAME</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="title" id="title" value=""></input>
            </span>
            <span class="inputicon">
                <div id="titleicon"></div>
            </span>
        </div>
        <div class="formitem clear">
            <label class="inputlabel" for="category">CATEGORY</label>
            <span class="inputfield">
                <select id="category" class="text inputwidetext categoryselect">
                </select>
            </span>
            <span class="inputicon">
                <div id="categoryicon"></div>
            </span>
        </div>
        <div class="formitem clear">
            <label class="inputlabel" for="mantra">MANTRA</label>
            <span class="inputfield">
                <textarea class="inputtextareatwoline inputwidetext" cols="20" rows="5" name="mantra" id="mantra" maxlength="140"></textarea>
            </span>
            <span class="inputicon">
                <div id="mantraicon"></div>
            </span>
        </div>
        <div class="formitem clear">
            <label class="inputlabel" for="website">WEBSITE</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="website" id="website" value=""></input>
            </span>
            <span class="inputicon">
                <div id="websiteicon"></div>
            </span>
        </div>
        <div class="formitem clear">
            <label class="inputlabel" for="founders">FOUNDERS</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="founders" id="founders" value=""></input>
            </span>
            <span class="inputicon">
                <div id="foundersicon"></div>
            </span>
        </div>
        <div class="formitem clear">
            <label class="inputlabel" for="contact_email">EMAIL</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="contact_email" id="contact_email" value=""></input>
            </span>
            <span class="inputicon">
                <div id="contact_emailicon"></div>
            </span>
        </div>
        <div class="formitem clear">
            <label class="inputlabel" for="address">LOCATION</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="address" id="address" value=""></input>
            </span>
            <span class="inputicon">
                <div id="locationicon"></div>
            </span>
        </div>
        <div class="formitem clean">
            <span class="inputlabel"></span>
            <span class="inputmap" id="addressmap"></span>
        </div>

        <div class="formitem clear">
            <span class="newlistingmsg" id="newlistingmsg">&nbsp;</span>
        </div>

    </div>

    <div>
        <div class="formitem clear">
            <a href="#" class="nextbuttonlink">
                <span class="push-13 span-3 inputbutton">
                    NEXT
                </span>
            </a>
        </div>
    </div>

</div> <!-- end left column -->

<!-- right column -->
<div class="span-8 last">

    <div class="boxtitle clear">TIPS</div>
    <div class="sidebox">
        <p>NAME - short name for your project, product or company</p>
        <p>CATEGORY - your project industry classification</p>
        <p>MANTRA - tagline for your project, generally a single sentence, up to 140 characters</p>
        <p>WEBSITE - your project website, if you don&rsquo;t have one then a link to your facebook page or google profile</p>
        <p>FOUNDERS - the founders and co-founders of the project </p>
        <p>EMAIL - address where you want to be contacted by customers and investors</p>
        <p>LOCATION - e.g. 1 Infinite Loop, Cupertino, CA, USA, where your business is located, or has a nexus</p>
    </div>

</div>
<!-- end right column -->

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
  <script src="http://maps.googleapis.com/maps/api/js?libraries=places&sensor=true&language=en-GB"></script>
  <script src="js/libs/prevel.min.js"></script>
  <script src="js/modules/base.js"></script>
  <script src="js/modules/companylist.js"></script>
  <script src="js/modules/forms.js"></script>
  <script src="js/modules/newlistingbase.js"></script>
  <script src="js/modules/newlistingbasicspage.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
