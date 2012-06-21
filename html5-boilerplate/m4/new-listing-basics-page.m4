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

<div class="initialhidden" id="newlistingbasicswrapper">
<!-- left column -->
<div class="span-16">

    <div class="boxtitle basicstitle">
        <span class="titletext">BASIC INFO</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">Instructions</label>
            <p> List your project or company here on startupbidder and submit a funding
            request to your choice of investors.  Startupbidder will review the listing and
            suggest any changes before approving.</p>
            <p>NOTE THAT YOU MUST BE THE OWNER OR AN AUTHORIZED AGENT OF THE PROJECT OR BUSINESS TO ASK FOR FUNDING ON THIS SITE.</p>
        </div>
        <span class="bmctitlemsg" id="newlistingmsg"></span>
    </div>

    <div class="boxpanel basicspanel">
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="title">NAME</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="title" id="title" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Name</label>
                <br />
                A short name for your project, product or company.  If a company, it should be the legal name of the company.
            </p>
            <span class="inputicon">
                <div id="titleicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="category">CATEGORY</label>
            <span class="inputfield">
                <select id="category" class="text inputwidetext categoryselect">
                </select>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Category</label>
                <br />
                The industry classification of your project or company, drawn from a list of common angel and venture investment categories.
            </p>
            <span class="inputicon">
                <div id="categoryicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="mantra">MANTRA</label>
            <span class="inputfield">
                <textarea class="inputtextareatwoline inputwidetext" cols="20" rows="5" name="mantra" id="mantra" maxlength="140"></textarea>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Mantra</label>
                <br />
                A tagline for your project or company, a single phrase or sentence, up to 140 characters.  Make it short but descriptive.
            </p>
            <span class="inputicon">
                <div id="mantraicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="website">WEBSITE</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="website" id="website" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Website</label>
                <br />
                The website for your project or company, if you don't have one then link to your facebook page, google profile, or blog.
            </p>
            <span class="inputicon">
                <div id="websiteicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="founders">FOUNDERS</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="founders" id="founders" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Founders</label>
                <br />
                The full legal names of the founders and any co-founders of this project or company.
            </p>
            <span class="inputicon">
                <div id="foundersicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="contact_email">EMAIL</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="contact_email" id="contact_email" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Email</label>
                <br />
                Email address where you want to be contacted regarding this listing.
            </p>
            <span class="inputicon">
                <div id="contact_emailicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="address">LOCATION</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="address" id="address" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Location</label>
                <br />
                The full physical address of your company or where you are developing this project, e.g. 1 Infinite Loop, Cupertino, CA, USA.
            </p>
            <span class="inputicon">
                <div id="locationicon"></div>
            </span>
        </div>
        <div class="formitem clean">
            <span class="inputlabel"></span>
            <span class="inputmap" id="addressmap"></span>
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
<!--
    <div class="boxtitle clear">TIPS</div>
    <div class="sidebox">
    </div>
-->
</div>
<!-- end right column -->

</div>

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
