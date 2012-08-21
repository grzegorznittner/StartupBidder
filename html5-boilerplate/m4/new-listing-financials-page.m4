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
include(company-not-found.m4)
`
<div class="container preloader">
    <div class="preloaderfloater"></div>
    <div class="preloadericon"></div>
</div>
<div class="container initialhidden wrapper">

<!-- begin banner -->
    <div class="bannertextsmall">CHOOSE THE FUNDING THAT&rsquo;S RIGHT FOR YOU</div>

    <div class="span-16">
        <a href="#" class="backbuttonlink">
            <span class="push-1 span-3 inputbutton backbutton">
                &lt;&lt; BACK
            </span>
        </a>
    </div>

<!-- end banner -->

<div class="span-16 initialhidden" id="newlistingfinancialswrapper">

    <div class="boxtitle">
        <span class="titletext">ASKING FOR FUNDING?</span>
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
        <div class="formitem clear firstinputitem">
            <label class="inputlabel" for="asked_fund">ALLOW BIDS</label>
            <span class="inputcheckbox">
                <div id="asked_fund"></div>
            </span>
            <span class="inputhelp inputmsg"><span id="askfundstatus"></span><span class="newlistingaskmsg" id="newlistingaskmsg">&nbsp;</span></span>
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

    <div class="boxpanelfull offerpanel" id="offerpanel">

        <div class="formitem clear firstinputitem">
            <label class="inputlabel" for="suggested_amt">ASKING</label>
            <span class="inputfield">
                <div class="span-4 investbutton askingamtbtn">$1,000</div>
                <div class="span-4 investbutton askingamtbtn">$5,000</div>
                <div class="span-4 investbutton askingamtbtn">$10,000</div>
                <div class="span-4 investbutton askingamtbtn">$25,000</div>
                <div class="span-4 investbutton askingamtbtn">$50,000</div>
            </span>
        </div>

        <div class="formitem sideinfoitem clear">
            <span class="inputlabel">&nbsp;</span>
            <span class="inputfield">
                <input class="text askinginputtext" type="text" name="suggested_amt" id="suggested_amt" maxlength="8"></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Asking</label>
                <br/>
                The amount of money you want for investment, between $100 and $500,000 USD.  This is only a suggestion,
                as the investor may make their own bid, which you may accept, reject, or counter.
            </p>
            <span class="inputicon">
                <div id="suggested_amticon"></div>
            </span>
        </div>

        <div class="formitem clear">
            <label class="inputlabel" for="suggested_pct">FOR</label>
            <span class="inputfield">
                <div class="span-4 investbutton askingpctbtn">5%</div>
                <div class="span-4 investbutton askingpctbtn">10%</div>
                <div class="span-4 investbutton askingpctbtn">25%</div>
                <div class="span-4 investbutton askingpctbtn">50%</div>
                <div class="span-4 investbutton askingpctbtn">100%</div>
            </span>
        </div>

        <div class="formitem sideinfoitem clear">
            <span class="inputlabel">&nbsp;</span>
            <span class="inputfield">
                <input class="text askinginputtext" type="text" name="suggested_pct" id="suggested_pct" maxlength="3"></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">For</label>
                <br/>
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
                <div class="successful valuationtext" id="suggested_val"></div>
            </span>
        </div>

        <div class="formitem clear">
            <span class="newlistingmsgsmall newlistingoffermsg" id="newlistingoffermsg">&nbsp;</span>
        </div>

    </div>

    </div>
<!--
    <div class="boxtitle offertitle">
        <span class="titletext">OWNERSHIP</span>
        <div class="titleinfobtn"></div>
        <div class="titleinfo">
            <label class="titleinfoheader">Ownership</label>
            <p>
            Who owns your company or application?  List all applicable owners including founders, employees, corporations and investors, if any.
            </p>
        </div>
    </div>
    <div class="boxpanel newlistingpanel">
        <div class="formitem sideinfoitem clear firstinputitem">
            <label class="inputlabel" for="founders">OWNERS</label>
            <span class="inputfield">
                <input class="text inputwidetext" type="text" name="founders" id="founders" value=""></input>
            </span>
            <p class="sideinfo">
                <label class="sideinfoheader">Owners</label>
                <br/>
                The full legal names of all individual authors of this application or owners of the company.
            </p>
            <span class="inputicon">
                <div id="foundersicon"></div>
            </span>
        </div>
        <div class="formitem clear">
            <span class="newlistingmsgsmall" id="newlistingfoundersmsg">&nbsp;</span>
        </div>
    </div>
-->
    <div>
        <div class="formitem clear">
            <span class="newlistingmsgsmall" id="newlistingmsg">&nbsp;</span>
        </div>
    </div>

</div>

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
