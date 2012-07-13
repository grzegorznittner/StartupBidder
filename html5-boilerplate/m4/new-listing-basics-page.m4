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
<div class="container preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>
<div class="container initialhidden wrapper">

<!-- begin banner -->
    <div class="bannertextsmall">NEW LISTING WIZARD</div>

<div class="span-16">
    <div class="formitem clear">
        <span class="inputmsg" id="newlistingmsg"></span>
    </div>
</div>

<!-- end banner -->

<div class="initialhidden" id="newlistingbasicswrapper">
<!-- left column -->
<div class="span-16">

    <div class="boxtitle basicstitle">
        <span class="titletext">MANDATORY INFO</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">Instructions</label>
            <p> List your product or company here on startupbidder and submit a funding
            request to your choice of investors.  Startupbidder will review the listing and
            suggest any changes before approving.</p>
            <p>NOTE THAT YOU MUST BE THE OWNER OR AN AUTHORIZED AGENT OF THE PRODUCT OR BUSINESS TO ASK FOR FUNDING ON THIS SITE.</p>
        </div>
        <span class="bmctitlemsg" id="newlistingbasicsmsg"></span>
    </div>

    <div class="boxpanel newlistingpanel">
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="title">NAME</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="title" id="title" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Name</label>
                <br />
                A short name for your product or company.  If a company, it should be the legal name of the company.
            </p>
            <span class="inputicon">
                <div id="titleicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="type">TYPE</label>
            <span class="inputfield">
                <select id="type" class="text inputwidetext categoryselect">
                    <option value="application">Application</option>
                    <option value="company" selected="selected">Company</option>
                </select>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Type</label>
                <br />
                The type of listing, either "Application" for mobile, tablet, or desktop software applications, or "Company" for startup companies.
            </p>
            <span class="inputicon">
                <div id="typeicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="platform">PLATFORM</label>
            <span class="inputfield">
                <select id="platform" class="text inputwidetext categoryselect">
                    <option value="ios">iPhone / iPad</option>
                    <option value="android">Android Mobile / Tablet</option>
                    <option value="windows_phone">Windows Phone / Tablet</option>
                    <option value="desktop">Desktop</option>
                    <option value="website">Website</option>
                    <option value="other" selected="selected">Other</option>
                </select>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Platform</label>
                <br />
                The platform your application supports.  If you support multiple platforms, list your primary platform, and create another listing for your additional platform.
                If you are not listing an application company, leave this as Other.
            </p>
            <span class="inputicon">
                <div id="platformicon"></div>
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
                The industry classification of your product or company, drawn from a list of common angel and venture investment categories.
            </p>
            <span class="inputicon">
                <div id="categoryicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="website">LINK</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="website" id="website" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Link</label>
                <br />
                A link to the website for your application or company, or if you don't have one, then a link to your facebook page, twitter page, google profile, or blog.
            </p>
            <span class="inputicon">
                <div id="websiteicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="founders">OWNERS</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="founders" id="founders" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Owners</label>
                <br />
                The full legal names of all owners of this application or company.
            </p>
            <span class="inputicon">
                <div id="foundersicon"></div>
            </span>
        </div>
<!--
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
-->
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="address">LOCATION</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="address" id="address" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Location</label>
                <br />
                The full physical address of your company or where you are developing this product, e.g. 1 Infinite Loop, Cupertino, CA, USA.
            </p>
            <span class="inputicon">
                <div id="locationicon"></div>
            </span>
        </div>
        <div class="formitem clear">
            <span class="inputlabel"></span>
            <span class="inputmap" id="addressmap"></span>
        </div>
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="mantra">MANTRA</label>
            <span class="inputfield">
                <textarea class="inputtextareatwoline inputwidetext" cols="20" rows="5" name="mantra" id="mantra" maxlength="140"></textarea>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Mantra</label>
                <br />
                A tagline for your product or company, a single phrase or sentence, up to 140 characters.  Make it short but descriptive.
            </p>
            <span class="inputicon">
                <div id="mantraicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="summary">ELEVATOR PITCH</label>
            <span class="inputfield">
                <textarea class="inputtextareatwoline inputwidetext inputelevatorpitch" cols="20" rows="5" name="summary" id="summary" maxlength="2000"></textarea>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Elevator Pitch</label>
                <br />
                A one paragraph summary description of your application or company.  Should be short and descriptive.  Make it your 30-second elevator pitch.
            </p>
            <span class="inputicon">
                <div id="summaryicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
        </div>
    </div>

    <div class="formitem clear">
        <span class="inputmsg" id="submiterrormsg"></span>
    </div>

    <div>
        <div class="formitem clear">
            <span class="span-3 inputbutton submitbutton" id="submitbutton">
                SUBMIT
            </span>
        </div>
    </div>

</div> <!-- end left column -->

<!-- right column -->
<div class="span-8 last">

        <a href="/new-listing-financials-page.html">
            <div class="sidebox investbutton askfundingbutton">ASK FOR FUNDING</div>
        </a>

        <a href="/new-listing-media-page.html">
            <div class="sidebox investbutton">ADD LOGO</div>
        </a>

        <a href="/new-listing-media-page.html">
            <div class="sidebox investbutton">ADD IMAGES</div>
        </a>

        <a href="/new-listing-media-page.html">
            <div class="sidebox investbutton">ADD VIDEO</div>
        </a>

        <a href="/new-listing-bmc-page.html">
            <div class="sidebox investbutton">ADD MODEL</div>
        </a>

        <a href="/new-listing-qa-page.html">
            <div class="sidebox investbutton">ADD SLIDES</div>
        </a>

        <a href="/new-listing-financials-page.html">
            <div class="sidebox investbutton">ADD DOCUMENT</div>
        </a>

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
