`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="company-page">
<div id="fb-root"></div> <!-- for fb like button -->
<div id="wrap">
<div id="main">
'
include(header.m4)
include(company-not-found.m4)
include(company-banner.m4)
companybannermacro(`', `', `', `companynavselected', `', `')
`
<div class="container preloader">
     <div class="preloaderfloater"></div>
     <div class="preloadericon"></div>
</div>

<div class="container bidscontainer initialhidden wrapper">

    <div class="span-16">
        <a href="#" class="backbuttonlink">
            <span class="push-1 span-6 inputbutton backbutton investorbackbutton">
                &lt;&lt; BACK TO INVESTOR LIST
            </span>
        </a>
    </div>

'
include(company-order-book.m4)
`
<div class="boxtitlegap smokegrey clear">BIDS FROM INVESTOR <a id="investor_nickname" href="#"></a>
    <span class="newlistingtitlemsg" id="bidtitlemsg"></span>
</div>
<div class="initialhidden clear" id="bidsloggedin">
    <div class="boxpanel boxpanelfull" id="bidlistparent">
        <div id="bidlistlast"></div>
    </div>

    <div class="boxtitlegap smokegrey clear initialhidden" id="new_bid_boxtitle">MAKE A COUNTER OFFER
        <span class="newlistingtitlemsg" id="newbidtitlemsg"></span>
    </div>

    <div class="boxpanel boxpanelfull initialhidden" id="new_bid_boxparent">

        <div class="messageline new_bid_line" id="new_bid_box">

            <div class="formitem clear firstinputitem">
                <label class="inputlabel" for="new_bid_amt">&nbsp;</label>
                <span class="inputfield">
                    <div class="span-4 investbutton askingamtbtn">$5,000</div>
                    <div class="span-4 investbutton askingamtbtn">$25,000</div>
                    <div class="span-4 investbutton askingamtbtn">$50,000</div>
                    <div class="span-4 last investbutton askingamtbtn">$100,000</div>
                </span>
            </div>
    
            <div class="formitem sideinfoitem clear">
                <span class="inputlabel biglabel">AMOUNT</span>
                <span class="inputfield">
                    <input class="text askinginputtext" type="text" name="new_bid_amt" id="new_bid_amt" maxlength="8"></input>
                </span>
                <span class="inputicon newbidicon amountbidicon">
                    <div id="new_bid_amticon"></div>
                </span>
                <span class="newbiddesc">
                    <p>
                    The amount of money you are providing for investment, between $100 and $500,000 USD.  This bid will be sent to the listing owner
                    who may accept, reject or counter the offer.  You may withdraw this offer at any time prior to action by the listing owner.
                    </p>
                </span>
            </div>

            <div class="formitem clear">
                <label class="inputlabel" for="new_bid_pct">&nbsp;</label>
                <span class="inputfield">
                    <div class="span-4 investbutton askingpctbtn">5%</div>
                    <div class="span-4 investbutton askingpctbtn">25%</div>
                    <div class="span-4 investbutton askingpctbtn">50%</div>
                    <div class="span-4 last investbutton askingpctbtn">100%</div>
                </span>
            </div>
    
            <div class="formitem sideinfoitem clear">
                <span class="inputlabel biglabel">FOR</span>
                <span class="inputfield inputpctfield">
                    <input class="text askinginputtext" type="text" name="new_bid_pct" id="new_bid_pct" maxlength="8"></input>
                </span>
                <span class="inputpcttext">%
                </span>
                <span class="inputicon newbidicon">
                    <div id="new_bid_pcticon"></div>
                </span>
                <span class="newbiddesc">
                    <p>
                    For a company, how much fully diluted post-money common equity you are requiring from the owner for
                    your investment, from 1% to 100%.
                    For product investments, the tenancy in common ownership interest in the application including all
                    copyrights, patents, trademarks, and trade secrets developed as part of the application.
                    </p>
                </span>
            </div>
    
            <div class="formitem clear">
                <span class="inputlabel newbidvallabel">VALUATION</span>
                <span class="inputfield newbidvaluationfield">
                    <div class="financialsvaluationtext" id="new_bid_val"></div>
                </span>
                <span class="newbiddesc">
                    <p>
                    This is the valuation implied by your offer.  This is the asking price divided by the percentage being offered.
                    </p>
                </span>
            </div>
    
            <div class="formitem clear">
                <label class="inputlabel newbidnotelabel" for="note">NOTE</label>
                <span class="inputfield">
                    <textarea class="textarea new_bid_textarea" name="note" id="new_bid_text" cols="20" rows="5">Put your note to the owner here...</textarea>
                </span>
                <span class="inputicon">
                    <div id="new_bid_texticon"></div>
                </span>
            </div>
    
            <div class="newbidactionline" id="newbidbuttons">
                <span class="span-3 inputbutton bidactionbutton initialhidden" id="owner_counter_btn">COUNTER</span>
                <span class="span-17 bidconfirmmessage" id="new_bid_msg"></span>
            </div>
            <div class="newbidactionline initialhidden" id="newconfirmbuttons">
                <span class="span-3 inputbutton bidactionbutton" id="owner_new_cancel_btn">CANCEL</span>
                <span class="span-3 inputbutton bidactionbutton" id="owner_new_confirm_btn">CONFIRM</span>
                <span class="span-17 bidconfirmmessage" id="owner_new_msg"></span>
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
<script src="js/modules/bids.js"></script>
<script>
(new OwnerSingleInvestorBidListClass()).load();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
