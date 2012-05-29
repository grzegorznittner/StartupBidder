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
                <div class="companybannertitle aquamarine" id="title"></div>
                <div class="companybannertextgrey">
                    <span id="categoryaddresstext"></span><span> </span><span id="founderstext"></span>
                    <a class="companybannertextlink hoverlink initialhidden" href="" id="sendmessagelink">send private message</a>
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
                <div class="inputmsg inputfield last followmsg initialhidden" id="followtext">You are following this listing</div>
                <div class="companybannerfollowbtn smallinputbutton span-3 darkblue hoverlink initialhidden" id="followbtn"></div>
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
        <div class="boxpanel summarypanel darkgrey">
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
            <div class="addresstext darkgrey" id="fulladdress"></div>
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
        <div class="boxtitle smokegrey clear">PUBLIC COMMENTS</div>
        <div class="boxpanel boxpanelfull" id="commentlistparent">
            <div class="messageline messagereplyline addcommentbox initialhidden" id="addcommentbox">
                <p class="messageuser messagereplyuser span-4" id="myusername"></p>
                <textarea class="textarea messagetextarea messagereplytextarea" id="addcommenttext" name="addcommenttext" cols="20" rows="5">Put your comment here...</textarea>
                <span class="span-3 inputbutton messagebutton messagereplybutton" id="addcommentbtn">SEND</span>
                <p class="messagereplymsg inputmsg successful" id="commentmsg"></p>
            </div>
        </div>
    </div>

    <div class="span-24 initialhidden" id="bidswrapper">

        <div class="initialhidden" id="bidsnotloggedin">
            <div class="boxtitlegap smokegrey clear">MAKE PRIVATE BID</div>
            <div class="boxpanel boxpanelfull">
                <p>Sign in to place a bid.</p>
            </div>
        </div>

        <div class="initialhidden" id="bidsloggedin">
            <div class="boxtitlegap smokegrey clear">ORDER BOOK 
                <span class="newlistingtitlemsg" id="orderbooktitlemsg"></span>
            </div>
            <div class="boxpanel boxpanelfull" id="orderbookparent">
            </div>

            <div class="boxtitlegap smokegrey clear">YOUR BIDS
                <span class="newlistingtitlemsg" id="bidtitlemsg"></span>
            </div>
            <div class="boxpanel boxpanelfull" id="bidlistparent">
                <div class="messageline makebidline initialhidden" id="makebidbox">
                    <p class="messageuser messagereplyuser span-4">Make a bid</p>
                    <span class="span-14">
                        <div class="formitem">
                            <label class="inputlabel" for="title">AMOUNT</label>
                            <span class="inputfield">
                                <input class="text inputwidetext" type="text" name="makebidamt" id="makebidamt" value="$200,000" length="8" maxlength="8"></input>
                            </span>
                            <span class="inputicon">
                                <div id="makebidamticon"></div>
                            </span>
                        </div>
                        <div class="formitem clear">
                            <label class="inputlabel" for="category">PERCENT</label>
                            <span class="inputfield">
                                <input class="text inputwidetext" type="text" name="makebidpct" id="makebidpct" value="5%" length="3" maxlength="3"></input>
                            </span>
                            <span class="inputicon">
                                <div id="makebidpcticon"></div>
                            </span>
                        </div>
                        <div class="formitem clear">
                            <label class="inputlabel" for="category">VALUATION</label>
                            <span class="inputfield">
                                <input class="text inputwidetext" type="text" name="val" id="makebidval" value="" disabled="disabled"></input>
                            </span>
                            <span class="inputicon">
                                <div id="categoryicon"></div>
                            </span>
                        </div>

                        <textarea class="textarea messagetextarea messagereplytextarea" id="makebidtext" name="makebidtext" cols="20" rows="5">Put your note concerning this bid here...</textarea>
                    </span>
                    <span class="span-3 inputbutton messagebutton messagereplybutton" id="makebidbtn">MAKE BID</span>
                    <p class="messagereplymsg inputmsg successful" id="bidmsg"></p>
                </div>
            </div>
        </div>

        <div class="initialhidden" id="bidsowner">
            <div class="boxtitlegap smokegrey clear">INVESTOR BIDS</div>
            <div class="boxpanel boxpanelfull" id="bidgrouplist"><p>No bids recevied for this listing.</p></div>
        </div>

    </div>

    <div class="span-24 initialhidden" id="qandaswrapper">
        <div class="boxtitlegap smokegrey clear">PUBLIC QUESTIONS AND ANSWERS
            <span class="newlistingtitlemsg" id="qandatitlemsg"></span>
        </div>
        <div class="boxpanel boxpanelfull" id="qandalistparent">
            <div class="messageline messagereplyline questionaskline initialhidden" id="addqandabox">
                <p class="messageuser messagereplyuser span-4">Ask a question</p>
                <textarea class="textarea messagetextarea messagereplytextarea" id="addqandatext" name="addqandatext" cols="20" rows="5">Put your question here...</textarea>
                <span class="span-3 inputbutton messagebutton messagereplybutton" id="addqandabtn">SEND</span>
                <p class="messagereplymsg inputmsg successful" id="qandamsg"></p>
            </div>
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
  <script src="js/modules/orderbook.js"></script>
  <script src="js/modules/singleinvestorbidlist.js"></script>
  <script src="js/modules/comments.js"></script>
  <script src="js/modules/questions.js"></script>
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
