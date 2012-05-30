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
                <div class="messageline makebidline initialhidden" id="makebidbox">
                    <p class="messageuser messagereplyuser span-4">Make a bid</p>
                    <span class="span-16">
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
                            <span class="inputfield">
                                <div class="medtext successful" id="makebidval"></div>
                            </span>
                        </div>
                        <div class="formitem clear">
                            <label class="inputlabel" for="note">NOTE</label>
                            <span class="inputfield">
                                <textarea class="textarea makebidtextarea" name="note" id="makebidtext" cols="20" rows="5">Put your note concerning this bid here...</textarea>
                            </span>
                            <span class="inputicon">
                                <div id="makebidtexticon"></div>
                            </span>
                        </div>
                    </span>
                    <span class="span-3 inputbutton messagebutton messagereplybutton" id="postbidbtn">POST BID</span>
                    <p class="messagereplymsg inputmsg successful" id="makebidmsg"></p>
                </div>
            </div>
        </div>

        <div class="initialhidden clear" id="bidsowner">
            <div class="boxtitlegap smokegrey clear">BIDS</div>
            <div class="boxpanel boxpanelfull" id="bidgrouplist"><p>No bids recevied for this listing.</p></div>
        </div>

    </div>
'
