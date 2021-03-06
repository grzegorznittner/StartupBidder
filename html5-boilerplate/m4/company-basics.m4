`
<!-- begin basics wrapper -->
<div class="basicswrapper" id="basicswrapper">
    <!-- preview overlay click disabler -->
    <div class="previewoverlay initialhidden" id="previewoverlay">
        <div class="previewtext">PREVIEW</div>
        <div class="previewtext previewtext2">PREVIEW</div>
    </div>

    <div class="span-16 basicsleft">
        <div class="boxtitle">SUMMARY</div>
        <div class="boxpanel summarypanel darkgrey">
            <p class="indentedtext" id="summary"></p>
    	</div>

'
include(images-panel.m4)
`
        <div class="initialhidden" id="videowrapper">
            <div class="boxtitle">VIDEO <a href="#" id="videolink">(VIEW ON YOUTUBE)</a></div>
            <div class="boxpanel">
        	    <div class="videocontainer">
              	    <iframe width="622" height="350" id="videopresentation" src="" frameborder="0" allowfullscreen></iframe> 
        	    </div>
            </div>
        </div>

    </div>

    <div class="span-8 last">
        <a href="#" id="investlink">
            <div class="sidebox investbutton initialhidden" id="investbutton">INVEST</div>
        </a>

        <a href="/new-listing-basics-page.html">
            <div class="sidebox investbutton gotobutton initialhidden" id="editbutton">&lt;&lt; Revise Info</div>
        </a>

        <div class="sidebox investbutton gotobutton" id="valuationbutton">Go To Valuation</div>

        <div class="sidebox investbutton gotobutton" id="modelbutton">Go To Business Model</div>

        <div class="sidebox investbutton gotobutton" id="presentationbutton">Go To Presentation</div>

        <div class="boxtitle boxtitleside">ASKING</div>
        <div class="sidebox uneditabletext askingbox">
            <div class="sideboxdesc suggestedmsg" id="suggestedmsg"></div>
            <div class="suggestedinfo" id="suggestedinfo">
                <div class="sideboxtitlecompact sideboxnum" id="suggested_amt"></div>
                <div class="sideboxdesc">For <span class="sideboxnum" id="suggested_pct"></span><span class="sideboxnum">%</span> equity</div>
                <div class="sideboxdesc">Owner valuation is <span class="sideboxnum" id="suggested_val"></span></div>
<!--                <div class="sideboxdesc">Total raised is <span class="sideboxnum" id="total_raised">$0</span></div> -->
            </div>
        </div>

        <a href="/new-listing-financials-page.html">
            <div class="sidebox investbutton gotobutton initialhidden" id="fundingbutton">Ask for Funding</div>
        </a>
    
        <div class="initialhidden" id="documentboxwrapper"> 
        <div class="boxtitle boxtitleside">DOCUMENTS</div>
        <div class="sidebox documentbox" id="documentbox">
                <div class="downloadline hoverlink initialhidden" id="presentationwrapper">
                    <a href="#" id="presentationlink">
                        <div class="downloadicon"></div>
                        <div class="downloadtext">Download Presentation</div>
                    </a>
                </div>
                <div class="downloadline hoverlink initialhidden" id="businessplanwrapper">
                    <a href="#" id="businessplanlink">
                        <div class="downloadicon"></div>
                        <div class="downloadtext">Download Business Plan</div>
                    </a>
                </div>
                <div class="downloadline hoverlink initialhidden" id="financialswrapper">
                    <a href="#" id="financialslink">
                        <div class="downloadicon"></div>
                        <div class="downloadtext">Download Financials</div>
                    </a>
                </div>
        </div>
        </div>

        <div class="sidebox investbutton gotobutton initialhidden" id="adddocumentbutton">Add Document</div>
        <div class="sidebox investbutton gotobutton initialhidden" id="requestpresentationbutton">Request Powerpoint/PDF Presentation</div>
        <div class="sidebox investbutton gotobutton initialhidden" id="requestbusinessplanbutton">Request Business Plan</div>
        <div class="sidebox investbutton gotobutton initialhidden" id="requestfinancialsbutton">Request Financial Statements</div>
   
        <div class="boxtitle boxtitleside">LOCATION</div>
        <div class="sidebox">
            <div class="addresstext darkgrey" id="fulladdress"></div>
            <div class="sideboxmap">
                <a href="#" class="formlink hoverlink" target="_blank" id="addresslink">
                    <img src="#" id="mapimg"></img>
                </a>
            </div>
        </div>
   
        <div class="boxtitle boxtitleside">SHARE</div>
        <div class="sidebox socialsidebox" id="socialsidebox">
           
            <div class="twitterbanner" id="twitterbanner">
                <a href="https://twitter.com/share" class="twitter-share-button" data-via="startupbidder">Tweet</a>
            </div>
            <div class="facebookbanner" id="facebookbanner"></div>
            <div class="gplusbanner" id="gplusbanner"></div>
        </div>
    
        <div id="deletebox" class="deletebox">
        <div class="deletemsg attention" id="deletemsg">Are you sure?<br/>This cannot be undone.</div>
        <a href="#" id="deletebtn"><div class="sideboxbutton hoverlink">DELETE</div></a>
        <a href="#" class="deletecancelbtn" id="deletecancelbtn"><div class="sideboxbutton hoverlink">CANCEL</div></a>
        </div>

        <div id="withdrawbox" class="withdrawbox">
        <div class="withdrawmsg attention" id="withdrawmsg">Are you sure?<br/>This cannot be undone.</div>
        <a href="#" id="withdrawbtn"><div class="sideboxbutton hoverlink">WITHDRAW</div></a>
        <a href="#" class="withdrawcancelbtn" id="withdrawcancelbtn"><div class="sideboxbutton hoverlink">CANCEL</div></a>
        </div>
    
        <div id="approvebox" class="approvebox">
        <div class="approvemsg attention" id="approvemsg">Are you sure?</div>
        <a href="#" id="approvebtn"><div class="sideboxbutton hoverlink">ACTIVATE</div></a>
        <a href="#" class="approvecancelbtn" id="approvecancelbtn"><div class="sideboxbutton hoverlink">CANCEL</div></a>
        </div>
    
        <div id="sendbackbox" class="sendbackbox">
        <input class="text sideinputtext" type="text" name="sendbacktext" id="sendbacktext" value="" length="35" maxlength="100"></input>
        <div class="sendbackmsg attention" id="sendbackmsg">Are you sure?</div>
        <a href="#" id="sendbackbtn"><div class="sideboxbutton hoverlink">SEND BACK</div></a>
        <a href="#" class="sendbackcancelbtn" id="sendbackcancelbtn"><div class="sideboxbutton hoverlink">CANCEL</div></a>
        </div>
    
        <div id="freezebox" class="freezebox">
        <input class="text sideinputtext" type="text" name="freezetext" id="freezetext" value="" length="35" maxlength="100"></input>
        <div class="freezemsg attention" id="freezemsg">Are you sure?</div>
        <a href="#" id="freezebtn"><div class="sideboxbutton hoverlink">FREEZE</div></a>
        <a href="#" class="freezecancelbtn" id="freezecancelbtn"><div class="sideboxbutton hoverlink">CANCEL</div></a>
        </div>
    
    </div>
</div> <!-- end basics wrapper -->
'
