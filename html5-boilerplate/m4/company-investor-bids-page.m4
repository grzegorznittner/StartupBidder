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
include(company-banner.m4)
companybannermacro(`', `', `', `companynavselected', `', `')
`
<div class="container preloader">
     <div class="preloaderfloater"></div>
     <div class="preloadericon"></div>
</div>

<div class="container initialhidden wrapper">
'
include(company-order-book.m4)
`
<div class="boxtitlegap smokegrey clear">YOUR BIDS WITH THE OWNER
    <span class="newlistingtitlemsg" id="bidtitlemsg"></span>
</div>
<div class="initialhidden clear" id="bidsloggedin">
    <div class="boxpanel boxpanelfull" id="bidlistparent">
        <div id="bidlistlast"></div>
    </div>

    <div class="boxtitlegap smokegrey clear initialhidden" id="new_bid_boxtitle">MAKE A NEW BID
        <span class="newlistingtitlemsg" id="newbidtitlemsg"></span>
    </div>
    <div class="boxpanel boxpanelfull initialhidden" id="new_bid_boxparent">
        <div class="messageline new_bid_line" id="new_bid_box">
            <span class="span-4">&nbsp;</span>
            <span class="span-15">
                <div class="formitem">
                    <label class="inputlabel" for="amt">AMOUNT</label>
                    <span class="inputfield">
                        <input class="text inputwidetext" type="text" name="amt" id="new_bid_amt" value="" length="8" maxlength="8"></input>
                    </span>
                    <span class="inputicon">
                        <div id="new_bid_amticon"></div>
                    </span>
                </div>
                <div class="formitem clear">
                    <label class="inputlabel" for="pct">PERCENT</label>
                    <span class="inputfield">
                        <input class="text inputwidetext" type="text" name="pct" id="new_bid_pct" value="" length="3" maxlength="3"></input>
                    </span>
                    <span class="inputicon">
                        <div id="new_bid_pcticon"></div>
                    </span>
                </div>
                <div class="formitem clear">
                    <label class="inputlabel" for="note">NOTE</label>
                    <span class="inputfield">
                        <textarea class="textarea new_bid_textarea" name="note" id="new_bid_text" cols="20" rows="5">Put your note to the owner here...</textarea>
                    </span>
                    <span class="inputicon">
                        <div id="new_bid_texticon"></div>
                    </span>
                </div>
                <div class="formitem clear">
                    <label class="inputlabel" for="val">VALUATION</label>
                    <span class="inputfield">
                        <div class="span-3 new_bid_valtext medtext inprogress" id="new_bid_val"></div>
                    </span>
                </div>
            </span>
            <div class="newbidactionline" id="newbidbuttons">
                <span class="span-3 inputbutton bidactionbutton initialhidden" id="investor_counter_btn">COUNTER</span>
                <span class="span-3 inputbutton bidactionbutton initialhidden" id="investor_post_btn">MAKE BID</span>
                <span class="span-17 bidconfirmmessage" id="new_bid_msg"></span>
            </div>
            <div class="newbidactionline initialhidden" id="newconfirmbuttons">
                <span class="span-3 inputbutton bidactionbutton" id="investor_new_cancel_btn">CANCEL</span>
                <span class="span-3 inputbutton bidactionbutton" id="investor_new_confirm_btn">CONFIRM</span>
                <span class="span-17 bidconfirmmessage" id="investor_new_msg"></span>
            </div>
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
(new SingleInvestorBidListClass()).load();
</script>
'
include(promptie.m4)
`
</body>
</html>
'
