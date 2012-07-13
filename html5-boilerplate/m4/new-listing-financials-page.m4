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
<div class="container preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>
<div class="container initialhidden wrapper">

<!-- begin banner -->
    <div class="bannertextsmall">NEW LISTING WIZARD - FINANCIALS</div>

    <div class="span-16">
        <a href="#" class="backbuttonlink">
            <span class="push-1 span-3 inputbutton backbutton">
                << BACK
            </span>
        </a>
    </div>

<!-- end banner -->

<!-- left column -->
<div class="span-16 initialhidden" id="newlistingfinancialswrapper">

    <div class="boxtitle">
        <span class="titletext">ASKING FOR INVESTMENT?</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">Instructions</label>
            <p>
            Tell investors how much money you want to raise and how much of the company or product you
            want to offer for it.  Once your listing is approved, interested investors will
            contact you to make an offer.  Alternatively, at your option, you may post your
            listing without asking for any money.
            </p>
        </div>
    </div>
    <div class="boxpanel">
        <div class="formitem clear">
            <label class="inputlabel" for="asked_fund">ALLOW BIDS</label>
            <span class="inputcheckbox">
                <div id="asked_fund"></div>
            </span>
            <span class="inputhelp inputmsg">Investors can place bids <span class="newlistingaskmsg" id="newlistingaskmsg">&nbsp;</span></span>
        </div>
    </div>

    <div class="offerwrapper offerwrapperdisplay" id="offerwrapper">
    <div class="boxtitle offertitle">
        <span class="titletext">THE OFFER</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">Instructions</label>
            <p>
            Tell investors how much money you want to raise and how much of the company or product you
            want to offer for it.  The amount you want to raise is cash in US Dollars.  For a company,
            the percentage offering is the percentage of common stock, fully-diluted post-money, you
            are offering in exchange for the funding.  If you are listing a product, the percentage is
            the gross royalty in perpetuity on worldwide sales you will pay the investor.  
            The valuation is calculated by dividing the funding amount by the percentage of the
            company or product being offered.
            Once your listing is approved, interested investors will contact you to make an offer.
            </p>
        </div>
    </div>
    <div class="boxpanel offerpanel" id="offerpanel">
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="suggested_amt">ASKING</label>
            <span class="inputfield">
                <input class="text inputmedtext" type="text" name="suggested_amt" id="suggested_amt" maxlength="8"></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Asking</label>
                <br />
                The amount of money you want for investment, between $5,000 and $500,000 USD.  This is only a suggestion,
                as the investor may make their own bid, which you may accept, reject, or counter.
            </p>
            <span class="inputicon">
                <div id="suggested_amticon"></div>
            </span>
        </div>
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="suggested_pct">FOR</label>
            <span class="inputfield">
                <input class="text inputmedtext" type="text" name="suggested_pct" id="suggested_pct" maxlength="3"></input>%
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">For</label>
                <br />
                For a company, how much fully diluted post-money common equity you are offering, from 5% to 50%.
                For product investments, the royalty on gross worldwide sales for the investor.
            </p>
            <span class="inputicon">
                <div id="suggested_pcticon"></div>
            </span>
        </div>
        <div class="formitem clear">
            <span class="inputlabel">VALUATION</span>
            <span class="inputfield valuationfield">
                <div class="medtext successful" id="suggested_val"></div>
            </span>
        </div>
        <div class="formitem clear">
            <span class="newlistingmsgsmall" id="newlistingoffermsg">&nbsp;</span>
        </div>
    </div>
    </div>

    <div class="boxtitle">
        <span class="titletext">PRESENTATION</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">Instructions</label>
            <p>
            Giving investors insight into your company or product is a great way to get them over
            the hurdle to actually winning their funding.  Especially if you are asking for significant
            funding of $100,000 or more, investors will want to see a detailed business plan, an
            investor-oriented presentation, and whatever existing financial statements you have.  Although
            these documents are optional, including them will increase your chances of getting funding,
            as well as exposing you to a higher calibur of investor.
            </p>
        </div>
    </div>
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
                <p class="dummy"></p>
                <span class="uploadinputicon">
                    <div id="presentation_urlicon"></div>
                </span>
            </div>
            <div class="formitem uploaditem">
                <span class="inputfield">
                    <form id="presentationuploadform" method="post" enctype="multipart/form-data" target="presentationuploadiframe" action="#">
                        <input class="text uploadinputbutton uploadinputbuttonshort" id="presentationuploadfile" name="PRESENTATION" size="18" type="file"></input>
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

    <div class="boxtitle">
        <span class="titletext">BUSINESS PLAN</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">Instructions</label>
            <p>
            Giving investors insight into your company or product is a great way to get them over
            the hurdle to actually winning their funding.  Especially if you are asking for significant
            funding of $100,000 or more, investors will want to see a detailed business plan, an
            investor-oriented presentation, and whatever existing financial statements you have.  Although
            these documents are optional, including them will increase your chances of getting funding,
            as well as exposing you to a higher calibur of investor.
            </p>
        </div>
    </div>
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
                <p class="dummy"></p>
                <span class="uploadinputicon">
                    <div id="business_plan_urlicon"></div>
                </span>
            </div>
            <div class="formitem uploaditem">
                <span class="inputfield">
                    <form id="business_planuploadform" method="post" enctype="multipart/form-data" target="business_planuploadiframe" action="#">
                        <input class="text uploadinputbutton uploadinputbuttonshort" id="business_planuploadfile" name="BUSINESS_PLAN" size="18" type="file"></input>
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

    <div class="boxtitle">
        <span class="titletext">FINANCIAL STATEMENTS</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">Instructions</label>
            <p>
            Giving investors insight into your company or product is a great way to get them over
            the hurdle to actually winning their funding.  Especially if you are asking for significant
            funding of $100,000 or more, investors will want to see a detailed business plan, an
            investor-oriented presentation, and whatever existing financial statements you have.  Although
            these documents are optional, including them will increase your chances of getting funding,
            as well as exposing you to a higher calibur of investor.
            </p>
        </div>
    </div>
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
                <p class="dummy"></p>
                <span class="uploadinputicon">
                    <div id="financials_urlicon"></div>
                </span>
            </div>
            <div class="formitem uploaditem">
                <span class="inputfield">
                    <form id="financialsuploadform" method="post" enctype="multipart/form-data" target="financialsuploadiframe" action="#">
                        <input class="text uploadinputbutton uploadinputbuttonshort" id="financialsuploadfile" name="FINANCIALS" size="18" type="file"></input>
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
