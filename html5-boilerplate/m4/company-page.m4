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
`
<div class="companyheader">
    <div class="container">
        <div class="companybanner span-24">
            <div class="companybannerlogo tileimg noimage" id="companylogo"></div>
            <div class="initialhidden" id="listingdata">
                <div class="companybannertitle" id="title"></div>
                <div class="companybannertextgrey">
                    <span id="categoryaddresstext"></span><span> </span><span id="founderstext"></span>
                    <a class="companybannertextlink hoverlink initialhidden" href="" id="sendmessagelink">send message</a>
                </div>
                <div class="companybannertextgrey">
                    <span id="listing_date_text" class="inputfield"></span>
                    &nbsp;<a class="companybannertextlink" href="#" target="_blank" id="websitelink">
                        <span id="domainname" class="companybannerlink"></span>
                    </a>
                    <div class="span-1 linkicon"></div>
                </div>
                <div class="companybannermantra" id="mantra"></div>
            </div>
            <div class="companybannerfollow">
                <div class="inputmsg inputfield last initialhidden" id="followtext">You are following this listing</div>
                <div class="companybannerfollowbtn smallinputbutton span-3 hoverlink initialhidden" id="followbtn"></div>
                <div class="inputmsg inputfield last" id="followmsg"></div>
            </div>
            <div class="companynavcontainer">
                <div class="companynav hoverlink companynavselected" id="basicstab">
                    BASICS
                </div>
                <div class="companynav hoverlink" id="modeltab">
                    MODEL
                </div>
                <div class="companynav hoverlink" id="slidestab">
                    SLIDES
                </div>
                <div class="companynav hoverlink" id="bidstab">
                    BIDS <span id="num_bids"></span>
                </div>
                <div class="companynav hoverlink" id="commentstab">
                    COMMENTS <span id="num_comments"></span>
                </div>
                <div class="companynav hoverlink" id="qandastab">
                    QUESTIONS <span id="num_qandas"></span>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="container">

<!-- begin basics wrapper -->
<div class="basicswrapper" id="basicswrapper">
    <div class="span-16">
        <div class="boxtitle">SUMMARY</div>
        <div class="boxpanel summarypanel">
            <p id="summary"></p>
    	</div>
        <div class="boxtitle">MEDIA</div>
        <div class="boxpanel">
    	    <div class="videocontainer">
          	    <iframe width="622" height="452" id="videopresentation" src="" frameborder="0" allowfullscreen></iframe> 
    	    </div>
        </div>
    </div>
    <div class="span-8 last">
        <div class="boxtitle">ASKING</div>
        <div class="sidebox uneditabletext askingbox">
            <div class="sideboxdesc suggestedmsg" id="suggestedmsg"></div>
            <div class="suggestedinfo" id="suggestedinfo">
                <div class="sideboxtitlecompact" id="suggested_amt"></div>
                <div class="sideboxdesc">FOR <span id="suggested_pct"></span>% EQUITY</div>
                <div class="sideboxdesc">OWNER VALUATION IS <span id="suggested_val"></span></div>
                <div class="sideboxdesc"><span id="closingmsg"></span></div>
            </div>
        </div>
     
        <div class="boxtitle">DOCUMENTS</div>
        <div class="sidebox documentbox" id="documentbox">
            <div class="documentwrapper" id="documentwrapper">
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
        </div>
    
        <div class="boxtitle">LOCATION</div>
        <div class="sidebox">
            <div class="inputmsg" id="fulladdress"></div>
            <div class="sideboxmap">
                <a href="#" class="formlink hoverlink" target="_blank" id="addresslink">
                    <img src="#" id="mapimg"></img>
                </a>
            </div>
        </div>
    
        <div class="boxtitle">SHARE</div>
        <div class="sidebox socialsidebox">
            <div class="twitterbanner" id="twitterbanner"></div>    
            <div class="facebookbanner" id="facebookbanner"></div>
            <div class="gplusbanner" id="gplusbanner"></div>
        </div>
    
        <div id="withdrawbox" class="withdrawbox">
        <div class="boxtitle">WITHDRAW POST</div>
        <div class="withdrawmsg attention" id="withdrawmsg">Are you sure?<br/>This cannot be undone.</div>
        <a href="#" id="withdrawbtn"><div class="sideboxbutton sideboxbuttonsmall hoverlink">WITHDRAW</div></a>
        <a href="#" class="withdrawcancelbtn" id="withdrawcancelbtn"><div class="sideboxbutton sideboxbuttonsmall hoverlink">CANCEL</div></a>
        </div>
    
        <div id="approvebox" class="approvebox">
        <div class="boxtitle">ACTIVATE POST</div>
        <div class="approvemsg attention" id="approvemsg">Are you sure?</div>
        <a href="#" id="approvebtn"><div class="sideboxbutton sideboxbuttonsmall hoverlink">ACTIVATE</div></a>
        <a href="#" class="approvecancelbtn" id="approvecancelbtn"><div class="sideboxbutton sideboxbuttonsmall hoverlink">CANCEL</div></a>
        </div>
    
        <div id="sendbackbox" class="sendbackbox">
        <div class="boxtitle">SEND BACK POST</div>
        <div class="sendbackmsg attention" id="sendbackmsg">Are you sure?</div>
        <a href="#" id="sendbackbtn"><div class="sideboxbutton sideboxbuttonsmall hoverlink">SEND BACK</div></a>
        <a href="#" class="sendbackcancelbtn" id="sendbackcancelbtn"><div class="sideboxbutton sideboxbuttonsmall hoverlink">CANCEL</div></a>
        </div>
    
        <div id="freezebox" class="freezebox">
        <div class="boxtitle">FREEZE POST</div>
        <div class="freezemsg attention" id="freezemsg">Are you sure?</div>
        <a href="#" id="freezebtn"><div class="sideboxbutton sideboxbuttonsmall hoverlink">FREEZE</div></a>
        <a href="#" class="freezecancelbtn" id="freezecancelbtn"><div class="sideboxbutton sideboxbuttonsmall hoverlink">CANCEL</div></a>
        </div>
    
    </div>
</div> <!-- end basics wrapper -->

    <div class="span-24 initialhidden" id="commentswrapper">
        <div class="boxtitle smokegrey clear">COMMENTS</div>
        <div class="boxpanel boxpanelfull" id="commentlistparent">
            <div class="messageline messagereplyline initialhidden" id="addcommentbox">
                <p class="messageuser messagereplyuser span-4" id="myusername"></p>
                <textarea class="textarea messagetextarea messagereplytextarea" id="addcommenttext" name="addcommenttext" cols="20" rows="5">Put your comment here...</textarea>
                <span class="span-3 inputbutton messagebutton messagereplybutton" id="addcommentbtn">SEND</span>
                <p class="messagereplymsg inputmsg successful" id="commentmsg"></p>
            </div>
        </div>

<!--
        <div class="boxtitle addcommenttitle initialhidden" id="addcommenttitle">ADD COMMENT</div>
        <div class="boxpanel boxpanelfull remarkpanel addcommentbox initialhidden" id="addcommentbox">
            <p>
                <textarea class="textarea messagetextarea" id="addcommenttext" name="addcommenttext" cols="20" rows="5">Put your comment here...</textarea>
                <span class="span-12 inputmsg successful" id="addcommentmsg">&nbsp;</span>
                <span class="span-3 inputbutton addcommentbtn" id="addcommentbtn">POST</span>
            </p>
        </div>
        <div class="boxtitle">COMMENTS</div>
        <div class="boxpanel boxpanelfull remarkpanel">
            <div id="commentmsg"></div>
            <dl id="commentlist"></dl>
    	</div>
-->
    </div>

    <div class="span-24 initialhidden" id="bidswrapper">
        <div class="boxtitle initialhidden" id="makebidtitle">MAKE BID</div>
        <div class="boxpanel boxpanelfull remarkpanel initialhidden" id="makebidbox">
            <p>
                <textarea class="textarea messagetextarea" id="makebidtext" name="makebidtext" cols="20" rows="5">Make your bid here...</textarea>
                <span class="span-12 inputmsg successful" id="makebidmsg">&nbsp;</span>
                <span class="span-3 inputbutton addcommentbtn" id="makebidbtn">POST</span>
            </p>
        </div>
        <div class="boxtitle">BIDS</div>
        <div class="boxpanel boxpanelfull remarkpanel">
            <div id="bidsmsg" class="inputmsg"></div>
            <dl id="bidslist"></dl>
    	</div>
    </div>

    <div class="span-24 initialhidden" id="qandaswrapper">
        <div class="boxtitle initialhidden" id="addqandatitle">PUBLIC QUESTION TO THE OWNER</div>
        <div class="boxpanel boxpanelfull remarkpanel initialhidden" id="addqandabox">
            <p>
                <textarea class="textarea messagetextarea" id="addqandatext" name="addqandatext" cols="20" rows="5">Put your question here...</textarea>
                <span class="span-12 inputmsg successful" id="addqandamsg">&nbsp;</span>
                <span class="span-3 inputbutton" id="addqandabtn">SEND</span>
            </p>
        </div>
        <div class="boxtitle">QUESTIONS AND ANSWERS</div>
        <div class="boxpanel boxpanelfull remarkpanel">
            <div id="qandamsg"></div>
            <dl id="qandalist"></dl>
    	</div>
    </div>

<div class="initialhidden" id="modelwrapper">
'
include(bmc.m4)
`
</div>

<div class="initialhidden" id="slideswrapper">
'
include(ip.m4)
`
</div>

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->

'
include(footer.m4)
`
  <!-- JavaScript at the bottom for fast page loading -->
  <script src="js/libs/prevel.min.js"></script>
  <script src="js/modules/base.js"></script>
  <script src="js/modules/companylist.js"></script>
  <script src="js/modules/forms.js"></script>
  <script src="js/modules/comments.js"></script>
  <script src="js/modules/remarks.js"></script>
  <script src="js/modules/bmc.js"></script>
  <script src="js/modules/ip.js"></script>
  <script src="js/modules/listing.js"></script>
  <script src="js/modules/listingpage.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
