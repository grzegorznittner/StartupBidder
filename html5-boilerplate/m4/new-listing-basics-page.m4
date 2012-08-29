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
include(company-not-found.m4)
`
<div class="container preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>
<div class="container initialhidden wrapper">

<div class="span-16">
    <div class="formitem clear">
        <span class="inputmsg" id="newlistingmsg"></span>
    </div>
</div>

<div class="initialhidden" id="newlistingbasicswrapper">
<!-- left column -->
<div class="span-16">

    <div class="bannertextsmall" id="newlistingbanner">NEW LISTING WIZARD</div>

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
                    <option value="ios">iOS</option>
                    <option value="android">Android</option>
                    <option value="windows_phone">Windows Phone</option>
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
            <label class="inputlabel" for="stage">STAGE</label>
            <span class="inputfield">
                <select id="stage" class="text inputwidetext categoryselect">
                    <option value="concept">Concept</option>
                    <option value="startup" selected="selected">Startup</option>
                    <option value="established">Established</option>
                </select>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Stage</label>
                <br />
                What stage is your company or application at?  If you still don&rsquo;t have any revenue or product, it&rsquo;s at the "concept" stage.
                If you have a company or application that has been launched but is still quite new and may not yet be profitable, you are at the "startup" stage.
                If your company or application has been released, is earning a profit or breaking even, and has been around for awhile, you are "established".
            </p>
            <span class="inputicon">
                <div id="stageicon"></div>
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
            <label class="inputlabel" for="summary">SUMMARY</label>
            <span class="inputfield">
                <textarea class="inputtextareatwoline inputwidetext inputelevatorpitch" cols="20" rows="5" name="summary" id="summary" maxlength="2000"></textarea>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Summary</label>
                <br />

A short summary of your application or company between 30 and 60 seconds in
length.  Also known as the "elevator pitch", being between 50 and 100 words read
at presentation speed, or between 200 and 500 characters. 
Make it compelling and enthusiastic, but
without flashy openers, which turn off experienced investors.  Make each
sentence unique and engaging.  Tailor the pitch to the audience in easily
understood language without jargon, as this is a sure way to loose interest.
Stick to facts without broad exaggerations, instilling credibility into you and
your message.

            </p>
            <span class="inputicon">
                <div id="summaryicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
        </div>
    </div>

   <div class="boxpanel logopanel">
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="address">LOGO</label>
            <span class="inputfield">
                <div class="tileimg noimage" id="logoimg"></div>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Logo</label>
Your logo is often the first thing people will see about your product.
Make sure it is professional and eye-catching.  If you do not have the budget or expertise
for a professional logo, just make something simple with text.
                <br />
            </p>
            <span class="inputicon">
                <div id="locationicon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
        </div>
    </div>

'
include(images-panel-editable.m4)
`

    <div class="boxpanel newlistingpanel">
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
        <div class="formitem sideinfoitem clear">
        </div>
    </div>

    <div class="formitem clear">
        <span class="inputmsg" id="submiterrormsg"></span>
    </div>

</div> <!-- end left column -->

<!-- right column -->
<div class="span-8 last">

    <div class="sidebox inputbutton previewbutton toppreviewbutton">PREVIEW &gt;&gt;</div>

    <a href="/new-listing-financials-page.html">
        <div class="sidebox investbutton" id="askfundingbutton">ASK FOR FUNDING</div>
    </a>

    <a href="/new-listing-media-page.html">
        <div class="sidebox investbutton" id="videobutton">ADD VIDEO</div>
    </a>

    <a href="/new-listing-valuation-page.html">
        <div class="sidebox investbutton" id="valuationbutton">ADD VALUATION</div>
    </a>

    <a href="/new-listing-bmc-page.html">
        <div class="sidebox investbutton" id="modelbutton">ADD MODEL</div>
    </a>

    <a href="/new-listing-qa-page.html">
        <div class="sidebox investbutton" id="presentationbutton">PRESENTATION</div>
    </a>

    <a href="/new-listing-documents-page.html">
        <div class="sidebox investbutton" id="documentbutton">ADD DOCUMENT</div>
    </a>

    <div class="boxtitle boxtitleside clear">UPLOAD LOGO</span></div>
    <div class="uploadbox">
        <div class="formitem">
            <span class="uploadinfo">Enter an image URL and press enter or upload a local file</span>
        </div>
        <div class="formitem clear">
            <span class="inputfield">
                <input class="text picinputlink" type="text" maxlength="255" name="logo_url" id="logo_url" value=""></input>
            </span>
            <span class="uploadinputicon">
                <div id="logo_urlicon"></div>
            </span>
        </div>
        <div class="formitem">
            <span class="inputfield">
                <form id="logouploadform" method="post" enctype="multipart/form-data" target="logouploadiframe" action="#">
                    <input class="text picinputlink" id="logouploadfile" name="LOGO" size="16" type="file"></input>
                    <iframe id="logouploadiframe" name="logouploadiframe" src="" class="uploadiframe"></iframe>
                </form>
            </span>
        </div>
        <div class="formitem clear">
            <span class="uploadinfo" id="logomsg">146x146 JPG, PNG or GIF, autosized</span>
        </div>
    </div>

    <div class="boxtitle boxtitleside clear">UPLOAD IMAGE <span id="picnum">1</span></div>
    <div class="uploadbox">
        <div class="formitem">
            <span class="uploadinfo">Enter an image URL and press enter or upload a local file</span>
        </div>
        <div class="formitem clear">
            <span class="inputfield">
                <input class="text picinputlink" type="text" maxlength="255" name="pic_url" id="pic_url" value=""></input>
            </span>
            <span class="uploadinputicon">
                <div id="pic_urlicon"></div>
            </span>
        </div>
        <div class="formitem">
            <span class="inputfield">
                <form id="picuploadform" method="post" enctype="multipart/form-data" target="picuploadiframe" action="#">
                    <input class="text picinputlink" id="picuploadfile" name="PIC1" size="16" type="file"></input>
                    <iframe id="picuploadiframe" name="picuploadiframe" src="" class="uploadiframe"></iframe>
                </form>
            </span>
        </div>
        <div class="formitem clear">
            <span class="uploadinfo" id="picmsg">622x452 JPG, PNG or GIF, autosized</span>
        </div>
    </div>

    <div class="sidebox mapsidebox">
        <span class="inputmap" id="addressmap"></span>
    </div>
</div>
</div>

'
include(new-listing-bottom-buttons.m4)
`
<!-- end right column -->


</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="http://maps.googleapis.com/maps/api/js?libraries=places&sensor=true&language=en-GB"></script>
<script src="js/modules/base.js"></script>
<script src="js/modules/forms.js"></script>
<script src="js/modules/newlistingbase.js"></script>
<script src="js/modules/newlistingbasicspage.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
