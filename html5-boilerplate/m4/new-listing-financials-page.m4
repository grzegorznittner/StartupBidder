`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="new-listing-financials-page">
<div id="wrap">
<div id="newlistingmain">
'
include(header.m4)
`
<div class="container">

<!-- begin banner -->
    <div class="bannertextsmall">NEW COMPANY WIZARD - FINANCIALS</div>

    <div class="boxstep last">
        <a href="new-listing-basics-page.html"><span class="boxstep1"><div class="boxsteptext">BASICS</div></span></a>
        <a href="new-listing-media-page.html"><span class="boxstep2"><div class="boxsteptext">MEDIA</div></span></a>
        <a href="new-listing-bmc-page.html"><span class="boxstep3"><div class="boxsteptext">BMC</div></span></a>
        <a href="new-listing-qa-page.html"><span class="boxstep4"><div class="boxsteptext">Q &amp; A</div></span></a>
        <span class="boxstep5 boxstepcomplete"><div class="boxsteptext">FINANCIALS</div></span>
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
    <p>
Tell investors how much money you want to raise and how much of the company you
want to offer for it.  Once your listing is approved, interested investors will
contact you to make an offer.  Alternatively, at your option, you may post your
listing without asking for any money.
    </p>
    </div>

    <div class="boxtitle">ASKING FOR INVESTMENT?</div>
    <div class="boxpanel">
        <div class="formitem clear">
            <span class="inputlabel">ALLOW BIDS</span>
            <span class="inputfield">
                <input class="checkbox inputcheckbox" type="checkbox" checked="checked" value="true" name="asked_fund" id="asked_fund"></input>
            </span>
            <span class="inputhelp inputmsg">Investors can place bids <span class="newlistingaskmsg" id="newlistingaskmsg">&nbsp;</span></span>
        </div>
    </div>

    <div class="offerwrapper offerwrapperdisplay" id="offerwrapper">
    <div class="boxtitle">THE OFFER</div>
    <div class="boxpanel offerbox" id="offerpanel">
        <div class="formitem">
            <span class="inputlabel">ASKING</span>
            <span class="inputfield">
                <input class="text inputmedtext" type="text" name="suggested_amt" id="suggested_amt" maxlength="8"></input>
            </span>
            <span class="inputicon">
                <div id="suggested_amticon"></div>
            </span>
        </div>
        <div class="formitem clear">
            <span class="inputlabel">FOR</span>
            <span class="inputfield">
                <input class="text inputmedtext" type="text" name="suggested_pct" id="suggested_pct" maxlength="3"></input>
            </span>
            <span class="inputicon">
                <div id="suggested_pcticon"></div>
            </span>
        </div>
        <div class="formitem clear">
            <span class="inputlabel">IMPLIED VAL</span>
            <span class="inputfield">
                <div class="medtext successful" id="suggested_val"></div>
            </span>
        </div>
        <div class="formitem clear">
            <span class="newlistingmsgsmall" id="newlistingoffermsg">&nbsp;</span>
        </div>
    </div>
    </div>

    <div class="boxtitle">OPTIONAL PRESENTATION</div>
    <div class="boxpanel">
        <div class="mediacol1">
            <a href="#" id="presentationlink">
                <div class="tileimg noimage" id="presentationimg">
                    <div class="tiletype initialhidden" id="presentationdownloadbg"></div>
                    <div class="tiletypetext initialhidden" id="presentationdownloadtext">DOWNLOAD</div>
                </div>
            </a>
        </div>
        <div class="mediacol2">
            <div class="formitem">
                <span class="uploadinfo">Enter a URL or upload from your computer.</span>
            </div>
            <div class="formitem clear">
                <span class="inputfield">
                    <input class="text mediainputlink" type="text" maxlength="255" name="presentation_url" id="presentation_url" value=""></input>
                </span>
                <span class="uploadinputicon">
                    <div id="presentation_urlicon"></div>
                </span>
            </div>
            <div class="formitem uploaditem">
                <span class="inputfield">
                    <form id="presentationuploadform" method="post" enctype="multipart/form-data" target="presentationuploadiframe" action="#">
                        <input class="text uploadinputbutton" id="PRESENTATION" name="PRESENTATION" size="18" type="file"></input>
                        <iframe id="presentationuploadiframe" name="presentationuploadiframe" src="" class="uploadiframe"></iframe>
                    </form>
                </span>
                <a href="" id="presentationdeletelink">
                    <span class="span-3 inputbutton uploaddelete">DELETE</span>
                </a>
            </div>
            <div class="formitem clear">
                <span class="uploadinfo" id="presentationmsg">Powerpoint or PDF presentation, 10-30 slides</span>
            </div>
        </div>
    </div>

    <div class="boxtitle">OPTIONAL BUSINESS PLAN</div>
    <div class="boxpanel">
        <div class="mediacol1">
            <a href="#" id="business_planlink">
                <div class="tileimg noimage" id="business_planimg">
                    <div class="tiletype initialhidden" id="business_plandownloadbg"></div>
                    <div class="tiletypetext initialhidden" id="business_plandownloadtext">DOWNLOAD</div>
                </div>
            </a>
        </div>
        <div class="mediacol2">
            <div class="formitem">
                <span class="uploadinfo">Enter a URL or upload from your computer.</span>
            </div>
            <div class="formitem clear">
                <span class="inputfield">
                    <input class="text mediainputlink" type="text" maxlength="255" name="business_plan_url" id="business_plan_url" value=""></input>
                </span>
                <span class="uploadinputicon">
                    <div id="business_plan_urlicon"></div>
                </span>
            </div>
            <div class="formitem uploaditem">
                <span class="inputfield">
                    <form id="business_planuploadform" method="post" enctype="multipart/form-data" target="business_planuploadiframe" action="#">
                        <input class="text uploadinputbutton" id="BUSINESS_PLAN" name="BUSINESS_PLAN" size="18" type="file"></input>
                        <iframe id="business_planuploadiframe" name="business_planuploadiframe" src="" class="uploadiframe"></iframe>
                    </form>
                </span>
                <a href="" id="business_plandeletelink">
                    <span class="span-3 inputbutton uploaddelete">DELETE</span>
                </a>
            </div>
            <div class="formitem clear">
                <span class="uploadinfo" id="business_planmsg">Word document or PDF of business plan</span>
            </div>
        </div>
    </div>

    <div class="boxtitle">OPTIONAL FINANCIALS</div>
    <div class="boxpanel">
        <div class="mediacol1">
            <a href="#" id="financialslink">
                <div class="tileimg noimage" id="financialsimg">
                    <div class="tiletype initialhidden" id="financialsdownloadbg"></div>
                    <div class="tiletypetext initialhidden" id="financialsdownloadtext">DOWNLOAD</div>
                </div>
            </a>
        </div>
        <div class="mediacol2">
            <div class="formitem">
                <span class="uploadinfo">Enter a URL or upload from your computer.</span>
            </div>
            <div class="formitem clear">
                <span class="inputfield">
                    <input class="text mediainputlink" type="text" maxlength="255" name="financials_url" id="financials_url" value=""></input>
                </span>
                <span class="uploadinputicon">
                    <div id="financials_urlicon"></div>
                </span>
            </div>
            <div class="formitem uploaditem">
                <span class="inputfield">
                    <form id="financialsuploadform" method="post" enctype="multipart/form-data" target="financialsuploadiframe" action="#">
                        <input class="text uploadinputbutton" id="FINANCIALS" name="FINANCIALS" size="18" type="file"></input>
                        <iframe id="financialsuploadiframe" name="financialsuploadiframe" src="" class="uploadiframe"></iframe>
                    </form>
                </span>
                <a href="" id="financialsdeletelink">
                    <span class="span-3 inputbutton uploaddelete">DELETE</span>
                </a>
            </div>
            <div class="formitem clear">
                <span class="uploadinfo" id="financialsmsg">Spreadsheet or PDF of financials, can be unaudited</span>
            </div>
        </div>
    </div>

    <div>
        <div class="formitem clear">
            <span class="newlistingmsgsmall" id="newlistingmsg">&nbsp;</span>
        </div>
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

    <div class="boxtitle clear">TIPS</div>
    <div class="sidebox">
<p>ASKING - amount of money you want for investment, between $5,000 and $500,000 USD</p>
<p>FOR - what percentage of common equity you are selling in exchange for the investment, between 5% and 50%</p>
<p>PRESENTATION - your slide deck presentation to investors, a Powerpoint or PDF document, keep it short</p>
<p>BUSINESS PLAN - your business plan, a Word or PDF document that describes your business in detail</p>
<p>FINANCIALS - your most current financial statements, if you have no operating history, then list current capital and runway</p>
    </div>

    <div class="boxtitle">WITHDRAW POST</div>
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
  <script src="js/modules/newlistingfinancialspage.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
