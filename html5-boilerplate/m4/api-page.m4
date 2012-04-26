`
<!doctype html>
<html lang="en">
'
include(head.m4)
`
<body class="api-page">
<div id="fb-root"></div> <!-- for fb like button -->
<div id="wrap">
<div id="main">
'
include(header.m4)
`
<div class="container">
'
include(api-banner.m4)
`
<!-- left column -->
<div class="span-16">

    <div class="boxtitle">SEARCH API</div>
    <div class="boxpanel apipanel">
        <dt>GET /listings/discover/
            <form method="GET" action="/listings/discover"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Return the front page listings, which includes logged in user active listings, investor bid listings, top listings, and other major category listings.  However, only four
            listings max are returned for each type.  To get the remaining listings of this type, call the appropriate type-based listing API method.
            </p>
        </dd>

        <dt>GET /listings/discover_user/
            <form method="GET" action="/listings/discover_user"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Return the logged in user&rsquo;s listings.
            </p>
        </dd>

        <dt>GET /listings/monitored/ <i>OPTIONAL max_results=&lt;n&gt;</i>
            <form method="GET" action="/listings/monitored/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Returns listings the user is watching, ordered by posting date descending.
            The max_results parameter allows for limiting response size.
            </p>
        </dd>

        <dt>GET /listings/top/ <i>OPTIONAL max_results=&lt;n&gt;</i>
            <form method="GET" action="/listings/top/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Returns the top ranked listings on startupbidder.  The algorithm used is fully explained in the FAQ, it functions as an exponentially time-decayed score similar to Hacker News.
            The max_results parameter allows for limiting response size.
            </p>
        </dd>

        <dt>GET /listings/valuation/ <i>OPTIONAL max_results=&lt;n&gt;</i>
            <form method="GET" action="/listings/valuation/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Returns the most valued listings on startupbidder, ordered by median bid valuation descending.  We use median instead of max in order to avoid outliers distoring the value.
            The max_results parameter allows for limiting response size.
            </p>
        </dd>

        <dt>GET /listings/latest/ <i>OPTIONAL max_results=&lt;n&gt;</i>
            <form method="GET" action="/listings/latest/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Returns the latest listings on startupbidder, ordered by posting date descending.  Thus, the newest listings are returned first.
            The max_results parameter allows for limiting response size.
            </p>
        </dd>

        <dt>GET /listings/closing/ <i>OPTIONAL max_results=&lt;n&gt;</i>
            <form method="GET" action="/listings/closing/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Returns listings on startupbidder open for bidding, ordered by closing date ascending.  Thus, the listing whose bidding is closing soonest is returned first.
            The max_results parameter allows for limiting response size.
            </p>
        </dd>

        <dt>GET /listings/monitored/ <i>OPTIONAL max_results=&lt;n&gt;</i>
            <form method="GET" action="/listings/monitored/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Returns listings on startupbidder monitored by logged in user.
            The max_results parameter allows for limiting response size.
            </p>
        </dd>

        <dt>GET /listings/keyword?text=&lt;searchtext&gt; <i>OPTIONAL max_results=&lt;n&gt;</i>
            <form method="GET" action="/listings/keyword"><input type="text" name="text" value="&lt;searchtext&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Returns listings on startupbidder matching the given keywords.  Relevancy ranking is applied, with the most relevant listings returned first.  Multiple keywords, including
            special keywords, are combined with an implicit AND operator.  The following special search keywords are supported:
            </p>
            <p>
            <code>location: &lt;location_name&gt;</code> - returns listings matching the location name as returned by method /listings/locations
            </p>
            <p>
            <code>category: &lt;category_name&gt;</code> - returns listings matching the category name as returned by method /listings/categories
            </p>
            <p>
            The max_results parameter allows for limiting response size.
            </p>
        </dd>

        <dt>GET /listing/messages/&lt;id&gt;
            <form method="GET" action="/listings/messages/"><input type="text" name="id" value="&lt;listing_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Returns notifications and private messages for given listing. Only listing owner sees all private messages. Other users see only their messages.
            </p>
        </dd>

        <dt>GET /listings/posted/ <i>ADMIN ONLY</i>
            <form method="GET" action="/listings/posted/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Returns posted status listings, that is, those which have been submitted by a user and are awaiting approval by a startupbidder admin.  Can only be called by a startupbidder admin.
            </p>
        </dd>

        <dt>GET /listings/frozen/ <i>ADMIN ONLY</i>
            <form method="GET" action="/listings/frozen/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Returns frozen status listings, that is, those which have been frozen by a startupbidder admin.  Can only be called by a startupbidder admin.
            </p>
        </dd>
    </div>

    <div class="boxtitle">LISTINGS API</div>
    <div class="boxpanel apipanel">
        <dt>GET /listings/get/&lt;id&gt;
            <form method="GET" action="/listings/get"><input type="text" name="id" value="&lt;listing_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Return listing data for a given listing id.  Note the special field "logo" which actually returns the data uri of the logo image.
            </p>
        </dd>

        <dt>GET /listings/logo/&lt;id&gt;
            <form method="GET" action="/listings/logo"><input type="text" name="id" value="&lt;listing_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Return listing logo for a given listing id. Should be used as source for image tags if data uri cannot be used.
            </p>
        </dd>

        <dt>POST /listing/create/
            <form method="POST" action="/listings/create/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Creates a new listing for the currently logged in user.  Only works for logged in users.  If the user already has a new listing which has not yet been approved, this existing
            listing is returned.  The happens because the user may only have one new listing at a time in the startupbidder system.
            Thus you can also call this method if you want the current in-edit listing.
            </p>
        </dd>

        <dt>POST /listing/update_field/
            <form method="POST" action="/listing/update_field"><input type="text" name="listing" value="{ title: &rsquo;Foo, Inc.&rsquo; }"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Updates a field for the currently logged in user&rsquo;s listing.  Value is a json string.  Multiple fields can be passed at once for efficient update.  Updatable fields keys are:
            </p>
            <p>
            <code>
                title, mantra, summary, contact_email, founders, website, category, asked_fund, suggested_amt, suggested_pct, video, answer1, answer2, answer3, answer4, answer5, answer6, answer7, answer8, answer9, answer10, answer11, answer12, answer13, answer14, answer15, answer16, answer17, answer18, answer19, answer20, answer21, answer22, answer23, answer24, answer25, answer26, logo_url, business_plan_url, presentation_url, financials_url
            </code>
            </p>
            <p>
            Note that by calling the *_url methods, the system will download the file at that url and store it in the backend.
            </p>
        </dd>

        <dt>POST /listing/post/
            <form method="POST" action="/listing/post/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Submits the user&rsquo;s new listing for approval by a startupbidder admin.  After approval, the current listing will become active and the user may then create a new additional listing.
            </p>
        </dd>

        <dt>POST /listing/delete/
            <form method="POST" action="/listing/delete/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Deletes the user&rsquo;s new listing.  This only works for a listing which has not yet been approved.  To delete a listing which has already been approved, call the withdraw method.
            </p>
        </dd>

        <dt>POST /listing/withdraw/&lt;id&gt;
            <form method="POST" action="/listing/withdraw"><input type="text" name="id" value="&lt;listing_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Withdraws the listing with the given id.  You must either be the logged in owner of this listing or an admin in order to call this method.  Note that for regulatory reasons,
            listings can never be deleted once approved, they must instead be withdrawn.  Withdrawn listings are kept in the system as per the startupbidder data retention policy.
            </p>
        </dd>

        <dt>POST /listing/freeze/&lt;id&gt; <i>ADMIN ONLY</i>
            <form method="POST" action="/listing/freeze"><input type="text" name="id" value="&lt;listing_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Freezes the listing with the given id.  This suspends the listing pending further review, which may be done if a listing is found to be inaccurate or in violation of any law or regulation.
            This method can only be called by admins.
            </p>
        </dd>

        <dt>POST /listing/activate/&lt;id&gt; <i>ADMIN ONLY</i>
            <form method="POST" action="/listing/activate"><input type="text" name="id" value="&lt;listing_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Activates the listing with the given id.  This makes the listing live and active on the site.  If asking for funds, bidding is enabled at this point.
            Commenting also becomes enabled for the listing.  This method can only be called if the listing is in posted or frozen state.
            This method can only be called by admins.
            </p>
        </dd>

        <dt>POST /listing/send_back/&lt;id&gt; <i>ADMIN ONLY</i>
            <form method="POST" action="/listing/send_back"><input type="text" name="id" value="&lt;listing_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Sends the listing with the given id back to the user for further modification.  This suspends the listing pending further review,
            which may be done if a listing is found to be inaccurate or in violation of any law or regulation.
            This method can only be called by admins.
            </p>
        </dd>
    </div>

    <div class="boxtitle">LOCATIONS API</div>
    <div class="boxpanel apipanel">
        <dt>GET /listings/locations/
            <form method="GET" action="/listings/locations/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Return the most active locations for startupbidder.  The returned location names can be used as a location value for the search API.
            </p>
        </dd>
        <dt>GET /listings/all-listing-locations/
            <form method="GET" action="/listings/all-listing-locations/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Return all locations for all listings in startupbidder in an abbreviated format.  Each returned item has the listing_id, it&rsquo;s latitude, and it&rsquo;s longitude.  Suitable for efficient display of all listings on a map.
            </p>
        </dd>
    </div>

    <div class="boxtitle">CATEGORIES API</div>
    <div class="boxpanel apipanel">
        <dt>GET /listings/categories/
            <form method="GET" action="/listings/categories/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Return all company categories for listings.  This is a list of the possible different categories of a company within the startupbidder system.
            </p>
        </dd>
        <dt>GET /listings/used_categories/
            <form method="GET" action="/listings/used_categories/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Return all company categories for listings which are actually used.  This only returns categories which have one or more listings active.
            </p>
        </dd>
    </div>

    <div class="boxtitle">COMMENTS API</div>
    <div class="boxpanel apipanel">
        <dt>GET /comments/listing/&lt;id&gt;
            <form method="GET" action="/comments/listing/"><input type="text" name="id" value="&lt;listing_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Return all comments for the given listing id.
            </p>
        </dd>
        <dt>GET /comments/user/&lt;id&gt;
            <form method="GET" action="/comments/user/"><input type="text" name="id" value="&lt;user_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Return all comments for the given user id.
            </p>
        </dd>
        <dt>GET /comments/get/&lt;id&gt;
            <form method="GET" action="/comments/get/"><input type="text" name="id" value="&lt;comment_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Return an individual comment for the given comment id.
            </p>
        </dd>
        <dt>POST /comment/create?comment=&lt;comment_json&gt;
            <form method="GET" action="/comment/create/">
                <input type="text" name="comment" value="{ listing_id: &lt;listing_id&gt;, text: &lt;comment_text&gt;}"></input>
                <input type="submit" value="TEST"></input>
            </form>
        </dt>
        <dd>
            <p>
            Create a comment by the currently logged in user for the given listing with the supplied text.
            </p>
        </dd>
        <dt>POST /comment/delete/&lt;id&gt;
            <form method="POST" action="/comment/delete/"><input type="text" name="id" value="&lt;comment_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Deletes the comment with the give comment id.  The comment must have been created by the currently logged in user.
            </p>
        </dd>
    </div>

    <div class="boxtitle">MONITOR API</div>
    <div class="boxpanel apipanel">
        <dt>GET /monitors/active-for-user/&lt;id&gt;
            <form method="GET" action="/monitors/active-for-user/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Returns list of active monitors for logged in user.
            </p>
        </dd>

        <dt>GET /monitors/active-for-listing/&lt;id&gt;
            <form method="GET" action="/monitors/active-for-listing/"><input type="text" name="id" value="&lt;listing_id&gt;"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Returns list of active monitors for given listing.
            </p>
        </dd>

        <dt>POST /monitor/set/&lt;id&gt;
            <form method="POST" action="/monitor/set/"><input type="text" name="id" value="&lt;listing_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Activates monitoring of the provided listing by the currently logged in user.  This allows the user to keep track of the listing.
            <br/>
            Monitor status is provided with listing objects.
            </p>
        </dd>
        <dt>POST /monitor/deactivate/&lt;id&gt;
            <form method="POST" action="/monitor/deactivate/"><input type="text" name="id" value="&lt;listing_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Deactivates monitoring of the provided listing by the currently logged in user.  This means the user no longer keeps of the listing.
            </p>
        </dd>
    </div>

    <div class="boxtitle">NOTIFICATION API</div>
    <div class="boxpanel apipanel">
        <dt>GET /notifications/get/&lt;id&gt;
            <form method="GET" action="/notifications/get/"><input type="text" name="id" value="&lt;notification_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Returns an individual notification for the given comment id.
            Calling this method automatically marks particular notification as read.
            </p>
        </dd>

        <dt>GET /notifications/user/&lt;id&gt;
            <form method="GET" action="/notifications/user/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Returns all notifications for logged in user.
            </p>
        </dd>

        <dt>GET /notifications/unread_user/&lt;id&gt;
            <form method="GET" action="/notifications/unread_user/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Returns unread notifications for logged in user.
            </p>
        </dd>
    </div>

    <div class="boxtitle">FILE API</div>
    <div class="boxpanel apipanel">
        <dt>POST &lt;upload_url&gt;
            <form method="POST" action="&lt;upload_url&gt;"><input type="file" name="BUSINESS_PLAN" value="Add BUSINESS_PLAN"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Uploads a file for the currently logged in user&rsquo;s listing.  The actual upload url must be obtained from the /listing/create call and is the value of one of the following fields:
            </p>
            <p>
            <code>
                business_plan_upload, presentation_upload, financials_upload, logo_upload
            </code>
            </p>
            <p>
            Set the form action attribute to the value of the upload url field, set the name of the input type="file" to one of the following, all upper case:
            </p>
            <p>
            <code>
                BUSINESS_PLAN, PRESENTATION, FINANCIALS, LOGO
            </code>
            </p>
            <p>
            Then when the form is submitted, the file will be uploaded.  The file can then be retrieved via the listing file download method.
            </p>
            <p>
            Also see the /listing/update_field method for an alternative way to get files into the system via url passing.
            </p>
        </dd>

        <dt>GET /file/download/&lt;id&gt;
            <form method="GET" action="/file/download"><input type="text" name="id" value="&lt;file_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Downloads a listing file.  The file id is the value of one of the following supported listing fields:
            </p>
            <p>
            <code>
                business_plan_id, presentation_id, financials_id
            </code>
            </p>
            <p>
            Note for logo, instead of a file, the data uri of the logo image is returned directly in the listing API call.  This avoids a separate API call for each listing in order to obtain the logo.
            </p>
        </dd>
    </div>

    <div class="boxtitle">USER API</div>
    <div class="boxpanel apipanel">
        <dt>GET /user/loggedin/
            <form method="GET" action="/user/loggedin"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Return logged in user profile data.
            </p>
        </dd>

        <dt>GET /user/get/&lt;id&gt;
            <form method="GET" action="/user/get"><input type="text" name="id" value="&lt;user_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Return user data for a given user id.
            </p>
        </dd>

        <dt>POST /user/deactivate/&lt;id&gt;
            <form method="POST" action="/user/deactivate"><input type="text" name="id" value="&lt;user_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Deactivates a given user.  Note that you must be logged in as this user or an admin in order to call this method.
            </p>
        </dd>
    </div>

</div> <!-- end left column -->

<!-- right column -->
<div class="span-8 last">
    <div class="boxtitle">CONNECT TO STARTUPBIDDER</div>
    <div class="sidebox">
        <p>
        Startupbidder provides a public API for providing summary and detailed information
        on our listings.  This is the same API that we use internally.  We eat our own cooking,
        so to speak.  Some functions are callable only by startupbidder admins for security,
        and all APIs may be rate-limited to prevent DoS and spam attacks.
        </p>
    </div>

    <div class="boxtitle">TIPS</div>
    <div class="sidebox">
        <p>
        All API calls return a standard json response, with the exception of the listing file download API which returns a file.
        All POST methods supporting multiple values use standard json as well.  This aids in javascript integration.
        </p>
        <p>
        The standard json response consists of the logged in user profile, the login url, the logout url, and one or more data fields.
        With this approach, we attempt to aggregate all typically needed return information so that only a single json call is
        needed for most pages and use cases.  Our extensive caching strategy makes this computationally fast and inexpensive.
        </p>
    </div>

    <div class="boxtitle">API TERMS OF USE</div>
    <div class="sidebox">
        <p>
        You may use the public API royalty-free, subject to the limitation that you must
        provide a link back to startupbidder on your site or application and clearly state
        that the data is coming from startupbidder.  We reserve the right to block access
        which is found to have violated our terms of service.  The complete terms of use
        are available on the terms and conditions page.
        </p>
    </div>

</div> <!-- end right column -->

<div id="loadmsg"></div>

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
  <!-- JavaScript at the bottom for fast page loading -->
  <script src="js/libs/prevel.min.js"></script>
  <script src="js/modules/base.js"></script>
  <script src="js/modules/headeronlypage.js"></script>
  <script src="js/modules/tracker.js"></script>
  <script src="js/modules/socialplugins.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
