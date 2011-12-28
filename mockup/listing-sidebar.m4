`
<!-- right column -->
<div class="span-8 last">
    <div class="boxtitle">ABOUT COMPANY</div>
    <div class="sidebox">
        <span class="formlabel">CATEGORY</span>
        <span class="formlabelvalue">Software</span>
        <span class="formlabel clear">POSTED</span>
        <span class="formlabelvalue">2011-09-16</span>
        <span class="formlabel clear">WEBSITE</span>
        <span class="formlabelvalue formlink">
            <a href="http://wave.google.com" target="_blank">
                <div class="span-1 linkicon"></div>weliketokillemail.com
            </a>
        </span>
    </div>

    <div class="boxtitle">COMPANY LOCATION</div>
    <div class="sidebox">
            <div>
        <a href="http://nominatim.openstreetmap.org/search?q=221b baker st,london,uk" class="formlink" target="_blank">
                <div class="span-1 mapicon"></div>221B Baker St, London, UK
        </a>
        
            </div>
            <div class="sideboxmap">
                <img src="http://ojw.dev.openstreetmap.org/StaticMap/?lat=51.499117116569&lon=-0.12359619140625&z=5&show=1&fmt=png&w=302&h=302&att=none"></img>
            </div>
    </div>

    <div class="boxtitle">DOCUMENTS</div>
    <div class="sidebox">
        <span class="smallformlabel clear">Slide Deck</span>
        <span class="formlabelvalue formlink">
            <div class="span-1 smallinputicon presentationicon"></div>
            <a href="http://oamp.od.nih.gov/contracttoolbox/Roadmap_StartBusiness/RoadmapPowerpointSlides%5CTomSidesFinalRoadmapPresentation.ppt">
                <span class="span-3 smallinputbutton" id="downloadpresentation">DOWNLOAD</span>
            </a>
        </span>
        <span class="smallformlabel">Business Plan</span>
        <span class="formlabelvalue formlink">
            <div class="span-1 smallinputicon documenticon"></div>
            <a href="www.freep.com/assets/PDF/1202gmplan.pdf">
                <span class="span-3 smallinputbutton" id="downloadbusinessplan">DOWNLOAD</span>
            </a>
        </span>
        <span class="smallformlabel clear">Financials</span>
        <span class="formlabelvalue formlink">
            <div class="span-1 smallinputicon spreadsheeticon"></div>
            <a href="http://andrewchenblog.com/wp-content/uploads/2009/01/virtual-items-ltv-model.xls">
                <span class="span-3 smallinputbutton" id="downloadfinancials">DOWNLOAD</span>
            </a>
        </span>
    </div>

    <div class="boxtitle">ASKING FUNDING</div>
    <div class="sidebox uneditabletext">
        <div class="sideboxtitlecompact">$25,000</div>
        <div class="sideboxdesc">FOR 10% COMMON EQUITY</div>
        <div class="sideboxdesc">OWNER VALUATION IS $250,000</div>
        <div class="sideboxdesc">CLOSES ON 24 DEC 2011 (45 DAYS LEFT)</div>
    </div>

    <div class="boxtitle">CURRENT BIDS</div>
    <div class="sidebox uneditabletext">
        <div class="sideboxtitlecompact">2 BIDS</div>
        <div class="sideboxdesc">BEST BID IS</div>
        <div class="sideboxtitlecompact">$16,000</div>
        <div class="sideboxdesc">FOR 25% COMMON EQUITY</div>
        <div class="sideboxdesc">BID VALUATION IS $64,000</div>
    </div>

    <div class="boxtitle">YOUR BID</div>
    <div class="sidebox uneditabletext">
        <div>
            <input class="title sideinputtitle" type="text" value="$25,000" maxlength="8" size="8" id="bidamount" name="bidamonut"></input>
        </div>
        <div class="sideboxdesc">
            <span class="sideboxlabelsmall">FOR</span>
            <input class="text inputtextpct" type="text" value="10%" maxlength="3" size="3" id="bidequity" name="bidequity"></input>
            <select class="inputselect" id="bidtype" name="bidtype">
                <option value="common">Common Stock</option>
                <option value="convertible">Convertible Note</option>
                <option value="preferred">Preferred Stock</option>
            </select>
        </div>
        <div class="sideboxdesc">
            <span class="sideboxlabelsmall">AT</span>
            <input class="text inputtextpct" type="text" value="12%" maxlength="3" size="3" id="bidrate" name="bidrate" disabled="true"></input>
            INTEREST
        </div>
        <div class="sideboxdesc">YOUR VALUATION IS $250,000</div>
        <div class="sideboxdesc">
            <textarea class="sideinputtextarea" id="bidnote" name="bidnote" rows="20" cols="5">Your note to the bidder here...</textarea>
        </div>
        <a href="listing-page.html">
            <div class="sideboxbuttoninterior">BID NOW</div>
        </a>
    </div>

'
include(companies-sidebar.m4)
companiessidebardiv(`RELATED COMPANIES')
`    
</div> <!-- end right column -->
'
