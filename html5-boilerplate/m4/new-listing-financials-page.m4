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
            <label class="titleinfoheader">The Offer</label>
            <p>
            Tell investors how much money you want to raise and how much of the company or product you
            want to offer for it.  The amount you want to raise is cash in US Dollars.  For a company,
            the percentage offering is the percentage of common stock, fully-diluted post-money, you
            are offering in exchange for the funding.  If you are listing a product, the percentage is
            the tenancy in common of the application, including but not limited to all copyrights,
            patents, trademarks, and trade secrets.
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
                The amount of money you want for investment, between $100 and $500,000 USD.  This is only a suggestion,
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
                For a company, how much fully diluted post-money common equity you are offering, from 1% to 100%.
                For product investments, the tenancy in common ownership interest in the application.
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

    <div class="boxtitle offertitle">
        <span class="titletext">OWNERSHIP</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">Ownership</label>
            <p>
            Who founded your company?  Who owns it now?  List all applicable owners including employees, corporations and investors, if any.
            </p>
        </div>
    </div>
    <div class="boxpanel newlistingpanel">
        <div class="formitem sideinfoitem clear">
            <label class="inputlabel" for="founders">FOUNDERS</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="founders" id="founders" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Founders</label>
                <br />
                The full legal names of all individual authors of this application or founders of the company.
            </p>
            <span class="inputicon">
                <div id="foundersicon"></div>
            </span>
        </div>
        <div class="formitem clear">
            <span class="newlistingmsgsmall" id="newlistingfoundersmsg">&nbsp;</span>
        </div>
    </div>

    <div>
        <div class="formitem clear">
            <span class="newlistingmsgsmall" id="newlistingmsg">&nbsp;</span>
        </div>
    </div>

</div> <!-- end left column -->

<!-- right column -->
<div class="span-8 last">
</div>
<!-- end right column -->

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
<script src="js/modules/base.js"></script>
<script src="js/modules/forms.js"></script>
<script src="js/modules/newlistingbase.js"></script>
<script src="js/modules/newlistingfinancialspage.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
