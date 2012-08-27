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
            <div style="position: relative; top: -12px; left: 112px; font-size:16px; color: #eed;">beta</div>
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
            <input type="image" class="searchbutton" alt="search" width="32" height="32" title="" src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAMAAABEpIrGAAACZFBMVEUUEBXi4uLj4uPj4+Pn5+fp6Ons7Ozr6+vp6eno5+jm5ubm5ebn5ufo6Ojq6urs6+zt7e3t7O3r6uvq6eru7e7u7u7v7u/v7+/g4ODd3d3b2tvZ2NnZ2dnc29zf3t/h4eHg4ODb29vW1tbe3d7h4eHe3t7U1NTZ2dnh4OHe3t7S0dLW1tbh4OHf3t/R0NHW1tbPz8/d3N3X19fIx8i/v7/d3N3f3t/Lysu7ubvHxseurK6bmZuxsLGFg4atq61pZ2qenZ4uKi+DgYQUEBWYlphRTlFaV1sUEBUUEBVWU1c9OT0UEBUUEBVRTlLg3+Dm5ebn5+fp6ekUEBVHQ0fY19jj4uPo6OgUEBUsKS0XExjf398UEBUUEBUcGB0UEBW3tre/vb/n5ucUEBUUEBUUEBUUEBUUEBUUEBUUEBUUEBWCf4LHxcfn5ucUEBVLSEzo6OgUEBVoZWnd3N3q6uoUEBXc3NwYFBnp6OkUEBUeGh+zsrOAfYAUEBUpJiovKzAnJCgUEBUUEBUUEBUUEBUUEBXY19jY2NjU1NTNzc3Ix8jEw8TDwsPDw8PGxcbKycrR0NHLy8u+vb68u7y6ubq5uLm9vL3Av8DGxsbQz9C7urvBwMHMy8zFxMXLysvHxsfOzc7FxcW/vr/CwcLJyMnGxcesqqyioKKWlZfU09TNzM2ysLLT0tPc3Nze3t6MiozPzs9gXmF0cnXR0dHS0dLZ2NkyLjNwbnGioaOtq62TkZRTUFTW1dYUEBXV1NXe3d7f39+HhYjb2tulo6bX1tff3t+XlZjV1dV2c3ba2dpWU1adnJ56eXsHcqoyAAAAhXRSTlMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAtgcbz3q5aDCGl62kDSO+4DE790Qkt+bHXWlDmyzAG/Jsv1mHrc/Fh6y/UBvySxy1I3cpLKvVRmWwGTv352TZC79j9HqLoZg/hiid7wOfPq1QJF/d4DOZRP/3zG2y8l0UDv5qiflr14SGxzIQVdLA4AwAAAAlwSFlzAAAASAAAAEgARslrPgAAAcxJREFUOMtjYAADRglJKenWNhlZOXlGJiBgQAMKiu0dnV3dPb19/UrKKpgKVNUmdE+cNHnKlMlTp02foa7BxIwqr6k1feLMyVAwadZsbR1UBbp6c6bORIBJ0+bqMyLLqxjMmzgJGUydNd8QWYHR7GlTUcHEOcbICkwWLJyIBqbNNkXIm5kvWogB5lkgFFjOnTUNA8yxQiiwXjwLE3TZIBTY9nVjgjl2CAX2c7swQa8DQoHjkjmYoNMJocDZZV4vBpjrihQObnM7McBSdyQFHssWo4O5niiR5bWoDxXMXe6NosDHdy4qmL3Czz8gEElFUPCilUhg1fzVIWvWhoYhqQiPWLd+PhRM3BC5ccamzau3RAUgqYiO2bpt+8T163fs3BUbF5/Qv379+v7daxORVLAkJaek7tmTlp6RyZqVvXkGEPTv3ZeDUMDGzsHJxc3Nw8vHz5ab1w8Gm3fvz4crEEAAQaGCwtWbQWB1a1ExFgUCAsIlB1asBoEVB0thCkSQgQBr2aEVYHD4SDlUgSgKEKioPHpsLxAcO1gFVSCGCgQy048f3g0EJ6qxKxBlYKg5OR+oYFYtVIE4GgAKZdSdWnW6PhO3AoaGxqbmFgYAo7g+62UFu08AAAAldEVYdGRhdGU6Y3JlYXRlADIwMTItMDctMTFUMDc6MDE6MDAtMDc6MDDu5vEUAAAAJXRFWHRkYXRlOm1vZGlmeQAyMDEyLTA3LTExVDA3OjAxOjAwLTA3OjAwn7tJqAAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAAASUVORK5CYII=" ></input>
        </form>
    </div>

    <!-- not logged in -->
    <div class="span-5 last loginspan headerrow initialhidden" id="headernotloggedin">
        <div>
            <a href="/login-page.html">
                <span class="headerlink headerlinkright">Sign In</span>
            </a>
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
    <div class="span-5 last loginspan headerrow initialhidden headerloggedin" id="headerloggedin">
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
                <div title="View Your Profile" class="profileavatar headeravatar" id="headeravatar"></div>
            </a>
        </div>
    </div>

  </div>
</div>
'
