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
            <div class="boxtitlegap smokegrey clear">YOUR BIDS
                <span class="newlistingtitlemsg" id="bidtitlemsg"></span>
            </div>
            <div class="boxpanel boxpanelfull" id="bidlistparent">
                <div id="bidlistlast"></div>
            </div>

            <div class="boxtitlegap smokegrey clear">NEW BID
                <span class="newlistingtitlemsg" id="newbidtitlemsg"></span>
            </div>
            <div class="boxpanel boxpanelfull" id="makebidboxparent">
                <div class="messageline makebidline initialhidden" id="makebidbox">
                    <span class="span-4">&nbsp;</span>
                    <span class="span-15">
                        <div class="formitem">
                            <label class="inputlabel" for="amt">AMOUNT</label>
                            <span class="inputfield">
                                <input class="text inputwidetext" type="text" name="amt" id="makebidamt" value="" length="8" maxlength="8"></input>
                            </span>
                            <span class="inputicon">
                                <div id="makebidamticon"></div>
                            </span>
                        </div>
                        <div class="formitem clear">
                            <label class="inputlabel" for="pct">PERCENT</label>
                            <span class="inputfield">
                                <input class="text inputwidetext" type="text" name="pct" id="makebidpct" value="" length="3" maxlength="3"></input>
                            </span>
                            <span class="inputicon">
                                <div id="makebidpcticon"></div>
                            </span>
                        </div>
                        <div class="formitem clear">
                            <span class="inputlabel">VALUATION</span>
                            <span class="inputfield valuationfield">
                                <div class="medtext successful" id="makebidval"></div>
                            </span>
                        </div>
                        <div class="formitem clear">
                            <label class="inputlabel" for="note">NOTE</label>
                            <span class="inputfield">
                                <textarea class="textarea makebidtextarea" name="note" id="makebidtext" cols="20" rows="5">Put your note to the owner here...</textarea>
                            </span>
                            <span class="inputicon">
                                <div id="makebidtexticon"></div>
                            </span>
                        </div>
                    </span>
                    <span class="span-4">
                        <div class="makebidbuttons"> 
                            <div><span class="span-3 inputbutton initialhidden" id="investor_post_btn">MAKE BID</span></div>
                            <div><span class="span-3 inputbutton initialhidden" id="investor_counter_btn">COUNTER</span></div>
                        </div>
                    </span>
                    <p class="messagereplymsg inputmsg successful" id="makebidmsg"></p>
                </div>
            </div>
        </div>

        <div class="initialhidden clear" id="bidsownergroup">
            <div class="boxtitlegap smokegrey clear">BIDS</div>
            <div class="boxpanel boxpanelfull" id="investorgrouplist"><p>No bids recevied for this listing.</p></div>
        </div>

    </div>
'
