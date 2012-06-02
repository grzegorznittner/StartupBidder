`
    <div class="span-24 initialhidden" id="bidswrapper">

        <div class="boxtitlegap smokegrey clear">ORDER BOOK
            <span class="newlistingtitlemsg" id="orderbooktitlemsg"></span>
        </div>
        <div class="sidebox orderbook initialhidden" id="orderbook_investor_bids"></div>
        <div class="sidebox orderbook initialhidden" id="orderbook_owner_bids"></div>
        <div class="sidebox orderbooklast initialhidden" id="orderbook_accepted_bids"></div>

        <div class="initialhidden clear" id="bidsnotloggedin">
            <div class="boxtitlegap smokegrey clear">MAKE PRIVATE BID</div>
            <div class="boxpanel boxpanelfull">
                <p>Sign in to place a bid.</p>
            </div>
        </div>

        <div class="initialhidden clear" id="bidsloggedin">
            <div class="boxtitlegap smokegrey clear">YOUR EXISTING BIDS WITH THE OWNER
                <span class="newlistingtitlemsg" id="bidtitlemsg"></span>
            </div>
            <div class="boxpanel boxpanelfull" id="bidlistparent">
                <div id="bidlistlast"></div>
            </div>

            <div class="boxtitlegap smokegrey clear">MAKE A NEW BID
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

        <div class="initialhidden clear" id="bidsownergroup">
            <div class="boxtitlegap smokegrey clear">BIDS</div>
            <div class="boxpanel boxpanelfull" id="investorgrouplist"><p>No bids recevied for this listing.</p></div>
        </div>

    </div>
'
