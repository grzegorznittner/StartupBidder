`
    <div class="initialhidden" id="askingpricewrapper">
        <div class="boxtitle boxtitleside">ASKING</div>
        <div class="sidebox uneditabletext askingbox">
                <div class="sideboxtitlecompact sideboxnum" id="askingamt"></div>
                <div class="sideboxdesc">For <span class="sideboxnum" id="askingpct"></span><span class="sideboxnum">%</span> equity</div>
                <div class="sideboxdesc">Owner valuation is <span class="sideboxnum" id="askingval"></span></div>
        </div>
    </div>

    <div class="initialhidden" id="orderbookwrapper">
        <div class="boxtitlegap smokegrey clear">LATEST BIDS
            <span class="newlistingtitlemsg" id="orderbooktitlemsg"></span>
        </div>

        <div class="sidebox orderbook lastorder">
            <div>Last Bid</div>
            <div class="lastorderamt sideboxnum" id="last_investor_bids_amt"></div>
            <div id="last_investor_bids_details"></div>
            <div class="lastdate" id="last_investor_bids_date"></div>
        </div>

        <div class="sidebox orderbook lastorder">
            <div>Last Ask</div>
            <div class="lastorderamt sideboxnum" id="last_owner_bids_amt"></div>
            <div id="last_owner_bids_details"></div>
            <div class="lastdate" id="last_owner_bids_date"></div>
        </div>

        <div class="sidebox orderbooklast lastorder">
            <div>Last Sale</div>
            <div class="lastorderamt sideboxnum" id="last_accepted_bids_amt"></div>
            <div id="last_accepted_bids_details"></div>
            <div class="lastdate" id="last_accepted_bids_date"></div>
        </div>

        <div class="boxtitlegap smokegrey clear">ORDER BOOK</div>

        <div class="sidebox orderbook" id="orderbook_investor_bids"></div>
        <div class="sidebox orderbook" id="orderbook_owner_bids"></div>
        <div class="sidebox orderbooklast" id="orderbook_accepted_bids"></div>

    </div>
'
