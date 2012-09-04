define(`companybannermacro', `
<div class="companyheader">
    <div class="container">
        <div class="companybanner span-24">

            <div class="span-24 preloader preloadercompanybanner">
                <div class="preloaderfloater"></div>
                <div class="preloadericon"></div>
            </div>

            <div class="initialhidden companybannerwrapper">

                <div class="companybannerlogo tileimg noimage" id="companylogo"></div>
    
                <p class="companybannertitle" id="title"></p>
                <div class="companybannertextgrey">
                    <span id="categoryaddresstext"></span><span> </span><span id="admintext"></span>
                    <a class="companybannertextlink hoverlink initialhidden" href="" id="sendmessagelink"><span style="color:#666;">&middot;</span> send message</a>
                </div>
                <div class="companybannertextgrey">
                    <span id="listing_date_text" class="inputfield" style="margin-right: 1px;"></span>
                    &nbsp;<a class="companybannertextlink" href="#" target="_blank" id="websitelink">
                        <span id="domainname" class="companybannertextlink" style="float:left;"></span>
                        <div class="span-1 linkicon" id="websitelinkicon"></div>
                    </a>
                </div>
                <div class="companybannertextgrey companybannermantra2" id="mantra"></div>
    
                <div class="companybannerfollow">
                    <div class="companybannerfollowbtn smallinputbutton span-3 darkblue hoverlink initialhidden" id="followbtn"></div>
                    <div class="inputmsg inputfield last companybannerfollowmsg" id="followmsg"></div>
                </div>

                <div class="companybannerfollow">
                    <div class="companybannersubmitbtn span-4 smallinputbutton initialhidden" id="submitbutton">SUBMIT &gt;&gt;</div>
                    <div class="inputmsg last companybannerfollowmsg clear" id="submiterrormsg"></div>
                </div>
    
            </div>

            <div class="companynavcontainer initialhidden" id="companynavcontainer">
                <a class="hoverlink companynavlink" href="#" id="basicstab">
                    <div class="companynav hoverlink $1">
                        BASICS
                    </div>
                </a>
                <a class="hoverlink companynavlink" href="#" id="valuationtab">
                    <div class="companynav hoverlink $2" id="valuationtab">
                        VALUATION
                    </div>
                </a>
                <a class="hoverlink companynavlink" href="#" id="modeltab">
                    <div class="companynav hoverlink $3" id="modeltab">
                        MODEL
                    </div>
                </a>
                <a class="hoverlink companynavlink" href="#" id="presentationtab">
                    <div class="companynav hoverlink companynavpresentation $4" id="presentationtab">
                        PRESENTATION
                    </div>
                </a>
                <a class="hoverlink companynavlink" href="#" id="bidstab">
                    <div class="companynav hoverlink companynavinvestments $5" id="bidstab">
                        INVESTMENTS <!-- <span id="num_bids"></span> -->
                    </div>
                </a>
                <a class="hoverlink companynavlink" href="#" id="commentstab">
                    <div class="companynav hoverlink $6" id="commentstab">
                        COMMENTS <!-- <span id="num_comments"></span> -->
                    </div>
                </a>
                <a class="hoverlink companynavlink" href="#" id="questionstab">
                    <div class="companynav hoverlink $7" id="questionstab">
                        QUESTIONS <!-- <span id="num_qandas"></span> -->
                    </div>
                </a>
            </div>
        </div>
    </div>
</div>
')
