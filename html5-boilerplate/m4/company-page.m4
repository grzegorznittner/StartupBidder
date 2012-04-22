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
            <div class="companybannertitle" id="title"></div>
            <div class="companybannertextgrey">
                <span id="categorytext"></span> company in <span id="address"></span> by <span id="founders"></span>
                <a class="companybannertextlink" href="#">send message</a>
            </div>
            <div class="companybannertextgrey">
                <span style="float:left;">Posted on <span id="listing_date"></span> from</span>
                <a class="companybannertextlink" href="#" target="_blank" id="websitelink"><div class="span-1 linkicon"></div><span id="domainname"></span></a>
            </div>
            <div class="companybannermantra" id="mantra"></div>
            <div class="companynavcontainer">
                <div class="companynav companynavselected" id="basicstab">
                    LISTING
                </div>
                <div class="companynav" id="bidstab">
                    BIDS
                </div>
                <div class="companynav" id="commentstab">
                    COMMENTS
                </div>
            </div>
        </div>
    </div>
</div>

<div class="container">

<!-- left column -->
<div class="basicswrapper" id="basicswrapper">
<div class="span-16">
    <div class="boxtitle">SUMMARY</div>
    <div class="boxpanel summarypanel">
        <p id="summary"></p>
	</div>
    <div class="boxtitle">VIDEO</div>
    <div class="boxpanel">
	    <div class="videocontainer">
      	    <iframe width="622" height="452" id="videopresentation" src="" frameborder="0" allowfullscreen></iframe> 
	    </div>
    </div>
</div> <!-- end left column -->

<!-- right column -->
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
                    <span id="presentationbtn"></span>
                </a>
            </span>
            <span class="doclabel">Business Plan</span>
            <span class="formlabelvalue formlink">
                <div class="span-1 smallinputicon documenticon"></div>
                <a href="#" id="businessplanlink">
                    <span id="businessplanbtn"></span>
                </a>
            </span>
            <span class="doclabel clear">Financials</span>
            <span class="formlabelvalue formlink">
                <div class="span-1 smallinputicon spreadsheeticon"></div>
                <a href="#" id="financialslink">
                    <span id="financialsbtn"></span>
                </a>
            </span>
        </div>
    </div>

    <div class="boxtitle">LOCATION</div>
    <div class="sidebox">
        <a href="#" class="formlink hoverlink" target="_blank" id="addresslink">
            <div class="inputmsg" id="fulladdress"></div>
            <div class="sideboxmap">
                <img src="#" id="mapimg"></img>
            </div>
        </a>
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
    <a href="#" id="withdrawbtn"><div class="sideboxbutton sideboxbuttonsmall">WITHDRAW</div></a>
    <a href="#" class="withdrawcancelbtn" id="withdrawcancelbtn"><div class="sideboxbutton sideboxbuttonsmall">CANCEL</div></a>
    </div>

    <div id="approvebox" class="approvebox">
    <div class="boxtitle">APPROVE POST</div>
    <div class="approvemsg attention" id="approvemsg">Are you sure?</div>
    <a href="#" id="approvebtn"><div class="sideboxbutton sideboxbuttonsmall">APPROVE</div></a>
    <a href="#" class="approvecancelbtn" id="approvecancelbtn"><div class="sideboxbutton sideboxbuttonsmall">CANCEL</div></a>
    </div>

    <div id="sendbackbox" class="sendbackbox">
    <div class="boxtitle">SEND BACK POST</div>
    <div class="sendbackmsg attention" id="sendbackmsg">Are you sure?</div>
    <a href="#" id="sendbackbtn"><div class="sideboxbutton sideboxbuttonsmall">SEND BACK</div></a>
    <a href="#" class="sendbackcancelbtn" id="sendbackcancelbtn"><div class="sideboxbutton sideboxbuttonsmall">CANCEL</div></a>
    </div>

    <div id="freezebox" class="freezebox">
    <div class="boxtitle">FREEZE POST</div>
    <div class="freezemsg attention" id="freezemsg">Are you sure?</div>
    <a href="#" id="freezebtn"><div class="sideboxbutton sideboxbuttonsmall">FREEZE</div></a>
    <a href="#" class="freezecancelbtn" id="freezecancelbtn"><div class="sideboxbutton sideboxbuttonsmall">CANCEL</div></a>
    </div>

</div> <!-- end right column -->

<!-- begin bmc -->
<div class="span-24 last">
<div class="boxtitle bmctitle">BUSINESS MODEL CANVAS</div>
<div class="bmcborder bmcwidth bmcheight">

<div class="bmctallcolumnonedge bmcthreefourthsheight bmcfifthwidthminushalfbar bmccontainer">
<div class="bmccell">
<div class="bmcicon" style="background: url(img/icons/bmc_kp_48.png);"></div>
<span class="bmcsection">Key Partners</span>
<br/>
<span id="answer3bmc"></span>
</div>
</div>

<div class="bmcverticalbar bmcbarwidth bmcthreefourthsheight" style="background-color: olivedrab;"></div>

<div class="bmctallcolumn bmcthreefourthsheight bmcfifthwidthminusbar">
<div class="bmcshortcolumn bmcfifthwidthminusbar bmcthreeeighthsheightminushalfbar bmccontainer">
<div class="bmccell">
<div class="bmcicon" style="background: url(img/icons/bmc_ka_48.png);"></div>
<span class="bmcsection">Key Activities</span>
<br/>
<span id="answer1bmc"></span>
</div>
</div>

<div class="bmchorizontalbarshort bmcbarheight bmcfifthwidthminusbar" style="background-color: olivedrab;"></div>

<div class="bmcshortcolumn bmcfifthwidthminusbar bmcthreeeighthsheightminushalfbar bmccontainer">
<div class="bmccell">
<div class="bmcicon" style="background: url(img/icons/bmc_kr_48.png);"></div>
<span class="bmcsection">Key Resources</span>
<br/>
<span id="answer2bmc"></span>
</div>
</div>

</div>

<div class="bmcverticalbar bmcbarwidth bmcthreefourthsheight" style="background-color: gold;"></div>

<div class="bmctallcolumn bmcthreefourthsheight bmcfifthwidthminusbar bmccontainer">
<div class="bmccell">
<div class="bmcicon" style="background: url(img/icons/bmc_vp_48.png);"></div>
<span class="bmcsection">Value Propositions</span>
<br/>
<span id="answer4bmc"></span>
</div>
</div>

<div class="bmcverticalbar bmcbarwidth bmcthreefourthsheight" style="background-color: dodgerblue;"></div>

<div class="bmctallcolumn bmcthreefourthsheight bmcfifthwidthminusbar">
<div class="bmcshortcolumn bmcfifthwidthminusbar bmcthreeeighthsheightminushalfbar bmccontainer">
<div class="bmccell">
<div class="bmcicon" style="background: url(img/icons/bmc_cr_48.png);"></div>
<span class="bmcsection">Customer Relationships</span>
<br/>
<span id="answer7bmc"></span>
</div>
</div>

<div class="bmchorizontalbarshort bmcbarheight bmcfifthwidthminusbar" style="background-color: dodgerblue;"></div>

<div class="bmcshortcolumn bmcfifthwidthminusbar bmcthreeeighthsheightminushalfbar bmccontainer">
<div class="bmccell">
<div class="bmcicon" style="background: url(img/icons/bmc_ch_48.png);"></div>
<span class="bmcsection">Channels</span>
<br/>
<span id="answer6bmc"></span>
</div>
</div>

</div>

<div class="bmcverticalbar bmcbarwidth bmcthreefourthsheight" style="background-color: black;"></div>

<div class="bmctallcolumnonedge bmcthreefourthsheight bmcfifthwidthminushalfbar bmccontainer">
<div class="bmccell">
<div class="bmcicon" style="background: url(img/icons/bmc_cs_48.png);"></div>
<span class="bmcsection">Customer Segments</span>
<br/>
<span id="answer5bmc"></span>
</div>
</div>

<div class="bmchorizontalbar bmcbarheight bmchalfwidthminushalfbar" style="background-color: gold;"></div>
<div class="bmchorizontalbarextra bmcbarheight bmchalfwidthplushalfbar" style="background-color: crimson;"></div>

<div class="bmcwidecolumn bmcthreefourthsheightminushalfbar bmchalfwidthminushalfbar bmccontainer" style="clear: both;">
<div class="bmccell">
<div class="bmcicon" style="background: url(img/icons/bmc_co_48.png);"></div>
<span class="bmcsection">Cost Structure</span>
<br/>
<span id="answer8bmc"></span>
</div>
</div>

<div class="bmcverticalbarshort bmcbarwidth bmconefourthheightminusbar" style="background-color: crimson;"></div>

<div class="bmcwidecolumn bmcthreefourthsheightminushalfbar bmchalfwidthminushalfbar bmccontainer">
<div class="bmccell">
<div class="bmcicon" style="background: url(img/icons/bmc_rs_48.png);"></div>
<span class="bmcsection">Revenue Streams</span>
<br/>
<span id="answer9bmc"></span>
</div>
</div>

</div>
</div>
<!-- end bmc -->

<!-- begin presentation -->
<div class="span-24 last">
    <div class="boxtitle">INVESTOR PRESENTATION</div>

    <div class="bmcborder ipmain">

        <div class="ipslideset" id="ipslideset">

            <div class="ipslide ipslide1">
                <div class="ipheader">
                    <div class="iplogo" id="iplogo1"></div>
                    <div class="iptitle" id="iptitle1"></div>
                </div>
                <div class="ipline"></div>
                <div class="ipcontent">
                    <div class="ipcentered" id="mantraip"></div>
                    <div class="ipspacer"></div>
                    <div class="ipcentered">by <span id="nameip"></div>
                    <!-- anti-spam
                    <div class="ipcentered"><a href="" id="contact_emaillinkip"><span id="contact_emailip"></span></a></div>
                    -->
                    <div class="ipcentered"><a href="" id="websitelinkip"><span id="websiteip"></span></a></div>
                    <div class="ipcentered" id="brief_addressip"></div>
                    <div class="ipspacer"></div>
                    <div class="ipcentered" id="askingip"></div>
                    <div class="ipspacer"></div>
                    <div class="ipcentered">Created on <span id="ipdate2"></span></div>
                </div>
            </div>

            <div class="ipslide ipslide2">
                <div class="ipheader">
                    <div class="iplogo" id="iplogo2"></div>
                    <div class="iptitle">Elevator Pitch</div>
                </div>
                <div class="ipline"></div>
                <div class="ipcontent" id="summaryip"></div>
            </div>

            <div class="ipslide ipslide3">
                <div class="ipheader">
                    <div class="iplogo" id="iplogo3"></div>
                    <div class="iptitle">The Problem</div>
                </div>
                <div class="ipline"></div>
                <div class="ipcontent" id="answer10ip"></div>
            </div>

            <div class="ipslide ipslide4">
                <div class="ipheader">
                    <div class="iplogo" id="iplogo4"></div>
                    <div class="iptitle">The Solution</div>
                </div>
                <div class="ipline"></div>
                <div class="ipcontent" id="answer11ip"></div>
            </div>

            <div class="ipslide ipslide5">
                <div class="ipheader">
                    <div class="iplogo" id="iplogo5"></div>
                    <div class="iptitle">Features and Benefits</div>
                </div>
                <div class="ipline"></div>
                <div class="ipcontent" id="answer12ip"></div>
            </div>

            <div class="ipslide ipslide6">
                <div class="ipheader">
                    <div class="iplogo" id="iplogo6"></div>
                    <div class="iptitle">Company Status</div>
                </div>
                <div class="ipline"></div>
                <div class="ipcontent" id="answer13ip"></div>
            </div>

            <div class="ipslide ipslide7">
                <div class="ipheader">
                    <div class="iplogo" id="iplogo7"></div>
                    <div class="iptitle">The Market</div>
                </div>
                <div class="ipline"></div>
                <div class="ipcontent" id="answer14ip"></div>
            </div>

            <div class="ipslide ipslide8">
                <div class="ipheader">
                    <div class="iplogo" id="iplogo8"></div>
                    <div class="iptitle">Typical Customer</div>
                </div>
                <div class="ipline"></div>
                <div class="ipcontent" id="answer15ip"></div>
            </div>

            <div class="ipslide ipslide9">
                <div class="ipheader">
                    <div class="iplogo" id="iplogo9"></div>
                    <div class="iptitle">Competitors</div>
                </div>
                <div class="ipline"></div>
                <div class="ipcontent" id="answer16ip"></div>
            </div>

            <div class="ipslide ipslide10">
                <div class="ipheader">
                    <div class="iplogo" id="iplogo10"></div>
                    <div class="iptitle">Competitive Comparison</div>
                </div>
                <div class="ipline"></div>
                <div class="ipcontent" id="answer17ip"></div>
            </div>

            <div class="ipslide ipslide11">
                <div class="ipheader">
                    <div class="iplogo" id="iplogo11"></div>
                    <div class="iptitle">Business Model</div>
                </div>
                <div class="ipline"></div>
                <div class="ipcontent" id="answer18ip"></div>
            </div>

            <div class="ipslide ipslide12">
                <div class="ipheader">
                    <div class="iplogo" id="iplogo12"></div>
                    <div class="iptitle">Marketing Plan</div>
                </div>
                <div class="ipline"></div>
                <div class="ipcontent" id="answer19ip"></div>
            </div>

            <div class="ipslide ipslide13">
                <div class="ipheader">
                    <div class="iplogo" id="iplogo13"></div>
                    <div class="iptitle">Team</div>
                </div>
                <div class="ipline"></div>
                <div class="ipcontent" id="answer20ip"></div>
            </div>

            <div class="ipslide ipslide14">
                <div class="ipheader">
                    <div class="iplogo" id="iplogo14"></div>
                    <div class="iptitle">Team Values</div>
                </div>
                <div class="ipline"></div>
                <div class="ipcontent" id="answer21ip"></div>
            </div>

            <div class="ipslide ipslide15">
                <div class="ipheader">
                    <div class="iplogo" id="iplogo15"></div>
                    <div class="iptitle">Current Financials</div>
                </div>
                <div class="ipline"></div>
                <div class="ipcontent" id="answer22ip"></div>
            </div>

            <div class="ipslide ipslide16">
                <div class="ipheader">
                    <div class="iplogo" id="iplogo16"></div>
                    <div class="iptitle">Financial Projections</div>
                </div>
                <div class="ipline"></div>
                <div class="ipcontent" id="answer23ip"></div>
            </div>

            <div class="ipslide ipslide17">
                <div class="ipheader">
                    <div class="iplogo" id="iplogo17"></div>
                    <div class="iptitle">Owners and Cap Table</div>
                </div>
                <div class="ipline"></div>
                <div class="ipcontent" id="answer24ip"></div>
            </div>

            <div class="ipslide ipslide18">
                <div class="ipheader">
                    <div class="iplogo" id="iplogo18"></div>
                    <div class="iptitle">Investment Plan</div>
                </div>
                <div class="ipline"></div>
                <div class="ipcontent" id="answer25ip"></div>
            </div>

            <div class="ipslide ipslide19">
                <div class="ipheader">
                    <div class="iplogo" id="iplogo19"></div>
                    <div class="iptitle">Timeline and Wrapup</div>
                </div>
                <div class="ipline"></div>
                <div class="ipcontent" id="answer26ip"></div>
            </div>

            <div class="ipslide ipslide20">
                <div class="ipheader">
                    <div class="iplogo" id="iplogo20"></div>
                    <div class="iptitle">Thank You</div>
                </div>
                <div class="ipline"></div>
                <div class="ipcontent">
                    <div class="ipcentered">Please contact for further information:</div>
                    <div class="ipspacer"></div>
                    <div class="ipcentered" id="name2ip"></div>
                    <div class="ipcentered"><a href="" id="contact_email2linkip"><span id="contact_email2ip"></span></a></div>
                    <div class="ipcentered"><a href="" id="website2linkip"><span id="website2ip"></span></a></div>
                    <div class="ipcentered" id="brief_address2ip"></div>
                </div>
             </div>

        </div>

        <div class="ipnav">
            <div class="iparrow ipleft" id="ipleft"></div>
            <div class="iparrow ipright" id="ipright"></div>
            <div class="iparrow ipfirst" id="ipfirst"></div>
            <div class="ipfooter ipdata"><span class="ipcorp" id="ipcorp"></span><span class="ipdate" id="ipdate"></span></div>
            <div class="ipfooter ippage"><span id="ippage"></span> of <span id="ippagetotal"></span></div>
        </div>
    </div>
</div>
<!-- end presentation -->
</div>

<!-- begin second row left column -->
    <div class="span-16 sendmsgwrapper" id="sendmsgwrapper">
        <div class="boxtitle addmessagetitle" id="addmessagetitle">PRIVATE MESSAGE TO THE OWNER</div>
        <div class="boxpanel messagepanel addmessagebox" id="addmessagebox">
            <p>
                <textarea class="textarea messagetextarea" id="addmessagetext" name="addmessagetext" cols="20" rows="5">Put your private message here...</textarea>
                <span class="span-12 inputmsg successful" id="addmessagemsg">&nbsp;</span>
                <span class="span-3 inputbutton" id="addmessagebtn">SEND</span>
            </p>
        </div>
    </div>

    <div class="span-16 commentswrapper" id="commentswrapper">
        <div class="boxtitle addcommenttitle" id="addcommenttitle">ADD COMMENT</div>
        <div class="boxpanel commentpanel addcommentbox" id="addcommentbox">
            <p>
                <textarea class="textarea messagetextarea" id="addcommenttext" name="addcommenttext" cols="20" rows="5">Put your comment here...</textarea>
                <span class="span-12 inputmsg successful" id="addcommentmsg">&nbsp;</span>
                <span class="span-3 inputbutton addcommentbtn" id="addcommentbtn">POST</span>
            </p>
        </div>
        <div class="boxtitle">COMMENTS</div>
        <div class="boxpanel commentpanel">
            <div id="commentsmsg"></div>
            <dl id="commentlist"></dl>
    	</div>
    </div>

    <div class="span-16 bidswrapper" id="bidswrapper">
        <div class="boxtitle addcommenttitle" id="makebidtitle">MAKE BID</div>
        <div class="boxpanel commentpanel addcommentbox" id="makebidbox">
            <p>
                <textarea class="textarea messagetextarea" id="makebidtext" name="makebidtext" cols="20" rows="5">Make your bid here...</textarea>
                <span class="span-12 inputmsg successful" id="makebidmsg">&nbsp;</span>
                <span class="span-3 inputbutton addcommentbtn" id="makebidbtn">POST</span>
            </p>
        </div>
        <div class="boxtitle">BIDS</div>
        <div class="boxpanel commentpanel">
            <div id="bidsmsg"></div>
            <dl id="bidslist"></dl>
    	</div>
    </div>

<!-- end second row left column -->

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
  <script src="js/modules/message.js"></script>
  <script src="js/modules/comments.js"></script>
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
