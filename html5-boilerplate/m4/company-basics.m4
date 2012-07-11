`
<!-- begin basics wrapper -->
<div class="basicswrapper" id="basicswrapper">
    <!-- preview overlay click disabler -->
    <div class="previewoverlay initialhidden" id="previewoverlay">
        <div class="previewtext">PREVIEW</div>
        <div class="previewtext previewtext2">PREVIEW</div>
    </div>

    <div class="span-16">
        <div class="boxtitle">SUMMARY</div>
        <div class="boxpanel summarypanel darkgrey">
            <p id="summary"></p>
    	</div>

'
include(images-panel.m4)
`
        <div class="initialhidden" id="videowrapper">
            <div class="boxtitle">VIDEO</div>
            <div class="boxpanel">
        	    <div class="videocontainer">
              	    <iframe width="622" height="350" id="videopresentation" src="" frameborder="0" allowfullscreen></iframe> 
        	    </div>
            </div>
        </div>

    </div>

    <div class="span-8 last">
        <div class="sidebox investbutton">INVEST</div>

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
     
        <div class="boxtitle boxtitleside">DOCUMENTS</div>
        <div class="sidebox documentbox" id="documentbox">
                <span class="doclabel clear">Presentation</span>
                <span class="formlabelvalue formlink">
                    <div class="span-1 smallinputicon presentationicon"></div>
                    <a href="#" id="presentationlink">
                        <span class="hoverlink" id="presentationbtn"></span>
                    </a>
                </span>
                <span class="doclabel">Business Plan</span>
                <span class="formlabelvalue formlink">
                    <div class="span-1 smallinputicon documenticon"></div>
                    <a href="#" id="businessplanlink">
                        <span class="hoverlink" id="businessplanbtn"></span>
                    </a>
                </span>
                <span class="doclabel clear">Financials</span>
                <span class="formlabelvalue formlink">
                    <div class="span-1 smallinputicon spreadsheeticon"></div>
                    <a href="#" id="financialslink">
                        <span class="hoverlink" id="financialsbtn"></span>
                    </a>
                </span>
        </div>
    
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
    
        <div id="withdrawbox" class="withdrawbox">
        <div class="boxtitle boxtitleside">WITHDRAW POST</div>
        <div class="withdrawmsg attention" id="withdrawmsg">Are you sure?<br/>This cannot be undone.</div>
        <a href="#" id="withdrawbtn"><div class="sideboxbutton sideboxbuttonsmall hoverlink">WITHDRAW</div></a>
        <a href="#" class="withdrawcancelbtn" id="withdrawcancelbtn"><div class="sideboxbutton sideboxbuttonsmall hoverlink">CANCEL</div></a>
        </div>
    
        <div id="approvebox" class="approvebox">
        <div class="boxtitle boxtitleside">ACTIVATE POST</div>
        <div class="approvemsg attention" id="approvemsg">Are you sure?</div>
        <a href="#" id="approvebtn"><div class="sideboxbutton sideboxbuttonsmall hoverlink">ACTIVATE</div></a>
        <a href="#" class="approvecancelbtn" id="approvecancelbtn"><div class="sideboxbutton sideboxbuttonsmall hoverlink">CANCEL</div></a>
        </div>
    
        <div id="sendbackbox" class="sendbackbox">
        <div class="boxtitle boxtitleside">SEND BACK POST</div>
        <input class="text sideinputtext" type="text" name="sendbacktext" id="sendbacktext" value="" length="35" maxlength="100"></input>
        <div class="sendbackmsg attention" id="sendbackmsg">Are you sure?</div>
        <a href="#" id="sendbackbtn"><div class="sideboxbutton sideboxbuttonsmall hoverlink">SEND BACK</div></a>
        <a href="#" class="sendbackcancelbtn" id="sendbackcancelbtn"><div class="sideboxbutton sideboxbuttonsmall hoverlink">CANCEL</div></a>
        </div>
    
        <div id="freezebox" class="freezebox">
        <div class="boxtitle boxtitleside">FREEZE POST</div>
        <input class="text sideinputtext" type="text" name="freezetext" id="freezetext" value="" length="35" maxlength="100"></input>
        <div class="freezemsg attention" id="freezemsg">Are you sure?</div>
        <a href="#" id="freezebtn"><div class="sideboxbutton sideboxbuttonsmall hoverlink">FREEZE</div></a>
        <a href="#" class="freezecancelbtn" id="freezecancelbtn"><div class="sideboxbutton sideboxbuttonsmall hoverlink">CANCEL</div></a>
        </div>
    
    </div>
</div> <!-- end basics wrapper -->
'
