define(`companybannermacro', `
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
            <div class="companynavcontainer initialhidden" id="companynavcontainer">
                <a class="hoverlink companynavlink" href="#" id="basicstab">
                    <div class="companynav hoverlink $1">
                        BASICS
                    </div>
                </a>
                <a class="hoverlink companynavlink" href="#" id="modeltab">
                    <div class="companynav hoverlink $2" id="modeltab">
                        MODEL
                    </div>
                </a>
                <a class="hoverlink companynavlink" href="#" id="slidestab">
                    <div class="companynav hoverlink $3" id="slidestab">
                        SLIDES
                    </div>
                </a>
                <a class="hoverlink companynavlink" href="#" id="bidstab">
                    <div class="companynav hoverlink $4" id="bidstab">
                        BIDS <span id="num_bids"></span>
                    </div>
                </a>
                <a class="hoverlink companynavlink" href="#" id="commentstab">
                    <div class="companynav hoverlink $5" id="commentstab">
                        COMMENTS <span id="num_comments"></span>
                    </div>
                </a>
                <a class="hoverlink companynavlink" href="#" id="questionstab">
                    <div class="companynav hoverlink $6" id="questionstab">
                        QUESTIONS <span id="num_qandas"></span>
                    </div>
                </a>
            </div>
        </div>
    </div>
</div>
')
