`
<!--
<div class="topheader" id="topheader">
    <div class="container topheaderline" id="topheaderline"></div>
</div>
-->

<div class="header" id="header">
  <div class="container headerrow" style="width: 960px;">
    <div class="span-4 headerrow hoverlink">
        <a href="home-page.html">
            <div style="font-size:24px; padding-top: 7px;"><span style="color:#45a9e6;">startup</span><span style="color:#eee">bidder</span></div>
        </a>
    </div>
    <div class="span-7 headerrow headerrowcenter last">
      <a href="discover-page.html"><span class="headerlink hoverlink headerlinknoborder">Find</span></a>
      <a href="nearby-page.html"><span class="headerlink"><span class="hoverlink">Nearby</span></span></a>
      <a href="add-listing-page.html"><span class="headerlink"><span class="hoverlink">Add</span></span></a>
    </div>

    <div class="span-8 headerrow">
        <form id="searchform" action="/main-page.html">
            <input type="hidden" name="type" value="keyword"></input>
            <input type="text" class="text inputtext searchtext" name="searchtext" id="searchtext" value=""></input>
            <input type="image" class="searchbutton" alt="search" src="/img/icons/search2.png" width="32" height="32"></input>
        </form>
    </div>

    <!-- not logged in -->
    <div class="span-5 last loginspan headerrow initialhidden" id="headernotloggedin">
        <div>
            <span class="headerlink headerlinkright">Sign In</span>
            <a id="loginlink" href="">
                <div class="headericon headersignin"></div>
            </a>
            <a id="twitter_loginlink" href="">
                <div class="headericon headertwittersignin"></div>
            </a>
            <a id="fb_loginlink" href="">
                <div class="headericon headerfbsignin"></div>
            </a>
        </div>
    </div>

    <!-- logged in -->
    <div class="span-5 last loginspan headerrow initialhidden" id="headerloggedin">
        <div>
            <a id="logoutlink" href="">
                <div class="headericon headerlogout" title="Logout"></div>
            </a>
            <a href="/notifications-page.html">
                <div class="headericon headernotifications" title="Notifications">
                    <div class="headernum" id="headernumnotifications"></div>
                </div>
            </a>
            <a href="/message-group-page.html">
                <div class="headericon headermessages" title="Messages">
                    <div class="headernum" id="headernummessages"></div>
                </div>
            </a>
            <a href="/profile-page.html">
                <span class="headerlink hoverlink headerlinkright" id="headerusername" title="Profile">You</span>
            </a>
        </div>
    </div>

  </div>
</div>
'
