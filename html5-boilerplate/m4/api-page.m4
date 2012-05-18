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
<div class="boxtitle">CONNECT TO STARTUPBIDDER</div>
<div class="boxpanel apipanel">
        <p>
        Startupbidder provides a public API for providing summary and detailed information
        on our listings.  This is the same API that we use internally.  We eat our own cooking,
        so to speak.  Some functions are callable only by startupbidder admins for security,
        and all APIs may be rate-limited to prevent DoS and spam attacks.
        </p>
        <p>
        All API calls return a standard json response, with the exception of the listing file download API which returns a file.
        All POST methods supporting multiple values use standard json as well.  This aids in javascript integration.
        The standard json response consists of the logged in user profile, the login url, the logout url, and one or more data fields.
        With this approach, we attempt to aggregate all typically needed return information so that only a single json call is
        needed for most pages and use cases.  Our extensive caching strategy makes this computationally fast and inexpensive.
        </p>
</div>

<div class="boxtitle">API TERMS OF USE</div>
<div class="boxpanel apipanel">
        <p>
        You may use the public API royalty-free, subject to the limitation that you must
        provide a link back to startupbidder on your site or application and clearly state
        that the data is coming from startupbidder.  We reserve the right to block access
        which is found to have violated our terms of service.  The complete terms of use
        are available on the terms and conditions page.
        </p>
</div>

    <span class="boxtitle">COMPANY LIST API</span>
    <div class="boxpanel apipanel">
    <p>Get a list of companies depending on the category, ranking, or user criteria.</p>

        <dt>GET /listing/discover</dt>
        <dd>
            <p>
            Return the front page listings, which includes logged in user active listings, investor bid listings, top listings, and other major category listings.  However, only four
            listings max are returned for each type.  To get the remaining listings of this type, call the appropriate type-based listing API method.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li>None.</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>login_url</code> URL to use for site login action</li>
                <li><code>logout_url</code> URL to use for site logout action</li>
                <li><code>loggedin_profile</code> private user profile object, null if not logged in, see User API for profile object details</li>
                <li><code>error_code</code> error status for this call, 0 on success</li>
                <li><code>error_msg</code> error message for this call, null on success</li>
                <li><code>top_listings</code> list of the four currently highest rated listings, see Listing API for listing object details</li>
                <li><code>closing_listings</code> list of the four listings whose bidding is closing the soonest</li>
                <li><code>latest_listings</code> list of the four listings most recently activated on the site</li>
                <li><code>monitored_listings</code> list of the four most recently watched listings for the logged in user, null if not logged in</li>
                <li><code>users_listings</code> list of the four listings most recently posted by the currently logged in user, null otherwise</li>
                <li><code>edited_listing</code> the logged in user&rsquo;s listing currently being edited but not yet approved, null otherwise</li>
                <li><code>categories</code> the map of all venture capital industry categories on the site with at least one active listing.  Structure is:
<pre name="code" class="js">
{
    :category_name: :active_listing_count,
    ...
}
</pre>
                </li>
                <li><code>top_locations</code> the map of the twenty locations with the most active listings on the site.  Locations are grouped by
                    brief address, which corresponds roughly to the Metropolitan Stastical Area, which is the city and country in most locations and
                    the city, state and country in the USA.  Structure is:
<pre name="code" class="js">
{
    :brief_address: :active_listing_count,
    ...
}
</pre>
                </li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/discover" target="listings-discover">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listings-discover"></iframe>
        </div>

        <dt>GET /listing/discover_user</dt>
        <dd>
            <p>
            Return the logged in user profile front page listings, however, only four listings max are returned for each type.
            To get the remaining listings of this type, call the appropriate type-based listing API method.
            </p>
            <p>
            NOTE: all fields except <code>login_url</code> are null unless the user is logged in.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li>None.</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>login_url</code> URL to use for site login action</li>
                <li><code>logout_url</code> URL to use for site logout action</li>
                <li><code>loggedin_profile</code> private user profile object, see User API for profile object details</li>
                <li><code>error_code</code> error status for this call, 0 on success</li>
                <li><code>error_msg</code> error message for this call, null on success</li>
                <li><code>active_listings</code> list of the four user listings most recently posted</li>
                <li><code>withdrawn_listings</code> list of the four user listings most recently withdrawn</li>
                <li><code>frozen_listings</code> list of the four user listings most recently frozen by an administrator</li>
                <li><code>closed_listings</code> list of the four user listings most recently closed for bidding</li>
                <li><code>monitored_listings</code> list of the four listings most recently watched by the user</li>
                <li><code>edited_listing</code> the logged in user&rsquo;s listing currently being edited but not yet approved, null otherwise</li>
                <li><code>notifications</code> list of the user notifications, unread first then by date order, see Notification API for details</li>
                <li><code>admin_posted_listings</code> list of all listings submitted for review but not yet approved, null except for logged in admins</li>
                <li><code>admin_frozen_listings</code> list of all administratively frozen listings, null except for logged in admins</li>
                <li><code>categories</code> map of listing categories, same as for <var>/listing/discover</var></li>
                <li><code>top_locations</code> map of top locations, same as for <var>/listing/discover</var></li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/discover_user" target="listings-discover_user">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listings-discover_user"></iframe>
        </div>

        <dt>GET /listing/monitored</dt>
        <dd>
            <p>
            Returns listings the user is watching, ordered by posting date descending, by default limited to five results.
            If more listings are available, they can be obtained by the <code>more_results_url</code> property of the <code>listings_props</code> in the response.
            </p>
            <p>
            NOTE: all fields except <code>login_url</code> are null unless the user is logged in.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>max_results</code> OPTIONAL for the max number of results to return, default 5, up to 20</i>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>login_url</code> URL to use for site login action</li>
                <li><code>logout_url</code> URL to use for site logout action</li>
                <li><code>loggedin_profile</code> private user profile object, see User API for profile object details</li>
                <li><code>error_code</code> error status for this call, 0 on success</li>
                <li><code>error_msg</code> error message for this call, null on success</li>
                <li><code>listings</code> list of listings matching this request, up to the <var>max_results</var></li>
                <li><code>notifications</code> list of the user notifications, unread first then by date order, see Notification API for details</li>
                <li><code>listings_props</code> list properties for this query.  You can call <var>more_results_url</var> in an AJAX request for additional listings.
                    Structure:
<pre name="code" class="js">
{
    start_index: :index,
    max_results: :max,
    num_results: :n,
    more_results_url: :url
}
</pre>
                </li>
                <li><code>categories</code> map of listing categories, same as for <var>/listing/discover</var></li>
                <li><code>top_locations</code> map of top locations, same as for <var>/listing/discover</var></li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/monitored" target="listings-monitored">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listings-monitored"></iframe>
        </div>

        <dt>GET /listing/top</dt>
        <dd>
            <p>
            Returns the top ranked listings on startupbidder.  The algorithm used is fully explained in the FAQ, it functions as an exponentially time-decayed
            score similar to Hacker News.
            If more listings are available, they can be obtained by the <code>more_results_url</code> property of the <code>listings_props</code> in the response.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>max_results</code> OPTIONAL for the max number of results to return, default 5, up to 20</i>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>login_url</code> URL to use for site login action</li>
                <li><code>logout_url</code> URL to use for site logout action</li>
                <li><code>loggedin_profile</code> private user profile object, see User API for profile object details</li>
                <li><code>error_code</code> error status for this call, 0 on success</li>
                <li><code>error_msg</code> error message for this call, null on success</li>
                <li><code>listings</code> list of listings matching this request, up to the <var>max_results</var></li>
                <li><code>listings_props</code> list properties for this query.  You can call <var>more_results_url</var> in an AJAX request for additional listings.
                    Structure:
<pre name="code" class="js">
{
    start_index: :index,
    max_results: :max,
    num_results: :n,
    more_results_url: :url
}
</pre>
                </li>
                <li><code>categories</code> map of listing categories, same as for <var>/listing/discover</var></li>
                <li><code>top_locations</code> map of top locations, same as for <var>/listing/discover</var></li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/top" target="listings-top">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listings-top"></iframe>
        </div>
<!--
        <dt>GET /listing/valuation</dt>
        <dd>
            <p>
            Returns the most valued active listings on startupbidder, ordered by median bid valuation descending.
            We use median instead of max in order to avoid outliers distoring the value.
            If more listings are available, they can be obtained by the <code>more_results_url</code> property of the <code>listings_props</code> in the response.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>max_results</code> OPTIONAL for the max number of results to return, default 5, up to 20</i>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>login_url</code> URL to use for site login action</li>
                <li><code>logout_url</code> URL to use for site logout action</li>
                <li><code>loggedin_profile</code> private user profile object, see User API for profile object details</li>
                <li><code>error_code</code> error status for this call, 0 on success</li>
                <li><code>error_msg</code> error message for this call, null on success</li>
                <li><code>listings</code> list of listings matching this request, up to the <var>max_results</var></li>
                <li><code>listings_props</code> list properties for this query.  You can call <var>more_results_url</var> in an AJAX request for additional listings.
                    Structure:
<pre name="code" class="js">
{
    start_index: :index,
    max_results: :max,
    num_results: :n,
    more_results_url: :url
}
</pre>
                </li>
                <li><code>categories</code> map of listing categories, same as for <var>/listing/discover</var></li>
                <li><code>top_locations</code> map of top locations, same as for <var>/listing/discover</var></li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/valuation" target="listings-valuation">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listings-valuation"></iframe>
        </div>
-->
        <dt>GET /listing/closing</dt>
        <dd>
            <p>
            Returns the active listings in order of closing date, thus the listings closing soonest are first.
            If more listings are available, they can be obtained by the <code>more_results_url</code> property of the <code>listings_props</code> in the response.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>max_results</code> OPTIONAL for the max number of results to return, default 5, up to 20</i>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>login_url</code> URL to use for site login action</li>
                <li><code>logout_url</code> URL to use for site logout action</li>
                <li><code>loggedin_profile</code> private user profile object, see User API for profile object details</li>
                <li><code>error_code</code> error status for this call, 0 on success</li>
                <li><code>error_msg</code> error message for this call, null on success</li>
                <li><code>listings</code> list of listings matching this request, up to the <var>max_results</var></li>
                <li><code>listings_props</code> list properties for this query.  You can call <var>more_results_url</var> in an AJAX request for additional listings.
                    Structure:
<pre name="code" class="js">
{
    start_index: :index,
    max_results: :max,
    num_results: :n,
    more_results_url: :url
}
</pre>
                </li>
                <li><code>categories</code> map of listing categories, same as for <var>/listing/discover</var></li>
                <li><code>top_locations</code> map of top locations, same as for <var>/listing/discover</var></li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/closing" target="listings-closing">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listings-closing"></iframe>
        </div>

        <dt>GET /listing/latest</dt>
        <dd>
            <p>
            Returns the latest listings on startupbidder, ordered by posting date descending.  Thus, the newest listings are returned first.
            If more listings are available, they can be obtained by the <code>more_results_url</code> property of the <code>listings_props</code> in the response.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>max_results</code> OPTIONAL for the max number of results to return, default 5, up to 20</i>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>login_url</code> URL to use for site login action</li>
                <li><code>logout_url</code> URL to use for site logout action</li>
                <li><code>loggedin_profile</code> private user profile object, see User API for profile object details</li>
                <li><code>error_code</code> error status for this call, 0 on success</li>
                <li><code>error_msg</code> error message for this call, null on success</li>
                <li><code>listings</code> list of listings matching this request, up to the <var>max_results</var></li>
                <li><code>listings_props</code> list properties for this query.  You can call <var>more_results_url</var> in an AJAX request for additional listings.
                    Structure:
<pre name="code" class="js">
{
    start_index: :index,
    max_results: :max,
    num_results: :n,
    more_results_url: :url
}
</pre>
                </li>
                <li><code>categories</code> map of listing categories, same as for <var>/listing/discover</var></li>
                <li><code>top_locations</code> map of top locations, same as for <var>/listing/discover</var></li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/latest" target="listings-latest">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listings-latest"></iframe>
        </div>
    </div>

    <div class="boxtitle">SEARCH API</div>
    <div class="boxpanel apipanel">
    <p>Search for a set of listings using keywords, location, and category matches</p>
    
        <dt>GET /listing/keyword</dt>
        <dd>
            <p>
            Returns listings on startupbidder matching a given set of keywords.  Revelancy ranking is applied, with the most relevant listings returned first.
            If more listings are available, they can be obtained by the <code>more_results_url</code> property of the <code>listings_props</code> in the response.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li>
                    <code>text</code>
                    <div class="span-15">
                    <p>
                    The text field is used to supply the set of basic and special keywords to seacrh by.  Multiple keywords, including
                    special keywords, are combined with an implicit AND operator.  The following special search keywords are supported:
                    </p>
                    <p>
                    <var>location:name</var> - match by location <var>name</var> as per <var>/listing/locations</var>
                    </p>
                    <p>
                    <var>category:name</var> - match by category <var>name</var> as per <var>/listing/categories</var>
                    </p>
                    <p>
                    The max_results parameter allows for limiting response size.
                    </p>
                    </div>
                </li>
                <li style="clear:both;"><code>max_results</code> OPTIONAL for the max number of results to return, default 5, up to 20</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>login_url</code> URL to use for site login action</li>
                <li><code>logout_url</code> URL to use for site logout action</li>
                <li><code>loggedin_profile</code> private user profile object, see User API for profile object details</li>
                <li><code>error_code</code> error status for this call, 0 on success</li>
                <li><code>error_msg</code> error message for this call, null on success</li>
                <li><code>listings</code> list of listings matching this request, up to the <var>max_results</var>, or an empty list if nothing is found</li>
                <li><code>listings_props</code> list properties for this query.  You can call <var>more_results_url</var> in an AJAX request for additional listings.
                    Structure:
<pre name="code" class="js">
{
    start_index: :index,
    max_results: :max,
    num_results: :n,
    more_results_url: :url
}
</pre>
                </li>
                <li><code>categories</code> map of listing categories, same as for <var>/listing/discover</var></li>
                <li><code>top_locations</code> map of top locations, same as for <var>/listing/discover</var></li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/keyword" target="listings-keyword">

                <div class="formitem">
                    <label class="inputlabel" for="title">TEXT</label>
                    <span class="inputfield">
                        <input class="text inputwidetext" type="text" name="text" value="keywords"></input>
                    </span>
                </div>
        
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>

            </form>
            <iframe name="listings-keyword"></iframe>
        </div>
    </div>

    <div class="boxtitle">LISTING API</div>
    <div class="boxpanel apipanel">
    <p>Get and update information on an individual company listing</p>

        <dt>GET /listing/get/:id</dt>
        <dd>
            <p>
            Return listing data for a given listing id.  Note the special field "logo" which actually returns the data uri of the logo image.
            All listings are private until activated, and then public thereafter.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>id</code> alphanumeric listing id as returned by the listing list and search API</li>
            </ul>
            <h4>Response</h4>
                <p>NOTE: fields answer1 through answer10 are used for the Business Model Canvas construction, fields
                answer 11 through answer 26 are used for the Investor Presentation construction</p>
            <ul>
                <li><code>login_url</code> URL to use for site login action</li>
                <li><code>logout_url</code> URL to use for site logout action</li>
                <li><code>loggedin_profile</code> private user profile object, see User API for profile object details</li>
                <li><code>error_code</code> error status for this call, 0 on success</li>
                <li><code>error_msg</code> error message for this call, null on success</li>
                <li><code>listing</code> data object for this listing, properties detailed below:
                <li><code class="apiprop">.listing_id</code> alphanumeric listing id</li>
                <li><code class="apiprop">.title</code> name of the company, partnership, or other business entity</li>
                <li><code class="apiprop">.asked_fund</code> <var>true</var> if the listing is asking for funding, <var>false</var> otherwise</li>
                <li><code class="apiprop">.suggested_amt</code> raw USD amount being asked in funding, between 20000 and 400000</li>
                <li><code class="apiprop">.suggested_pct</code> raw percentage of business offered for funding, between 5 and 50</li>
                <li><code class="apiprop">.suggested_val</code> calculated implied valuation of business, between 40000 and 8000000</li>
                <li><code class="apiprop">.modified_date</code> date the listing was last changed in YYYYMMDD format</li>
                <li><code class="apiprop">.created_date</code> date user created the listing on startupbidder</li>
                <li><code class="apiprop">.posted_date</code> date user submitted listing to an admin</li>
                <li><code class="apiprop">.listing_date</code> date admin approved the listing</li>
                <li><code class="apiprop">.closing_date</code> date bidding closes for this listing</li>
                <li><code class="apiprop">.status</code> progresses from <var>new</var> -&gt; <var>posted</var> -&gt; <var>active</var>, can also be <var>withdrawn</var> or <var>frozen</var></li>
                <li><code class="apiprop">.mantra</code> one sentance summary of the business</li>
                <li><code class="apiprop">.summary</code> one paragraph summary of the company, the "elevator pitch"</li>
                <li><code class="apiprop">.website</code> external website URL of the company</li>
                <li><code class="apiprop">.category</code> industry category as per <var>/listing/categories</var></li>
                <li><code class="apiprop">.profile_id</code> user ID of the user who posted the listing</li>
                <li><code class="apiprop">.profile_username</code> username of the user who posted the listing</li>
                <li><code class="apiprop">.brief_address</code> listing city, country or city, state country for USA</li>
                <li><code class="apiprop">.latitude</code> decimal latitude of the company business address</li>
                <li><code class="apiprop">.longitude</code> decimal longitude of the company business address</li>
                <li><code class="apiprop">.logo</code> data URI for the 146px by 146px listing company logo as per RFC 2397</li>
                <li><code class="apiprop">.previous_val</code> last valuation immediately before the current valuation was set</li>
                <li><code class="apiprop">.valuation</code> current valuation, same as median_valuation but suggested_val if no bids exist</li>
                <li><code class="apiprop">.median_valuation</code> the median valuation implied by the median bid for this listing</li>
                <li><code class="apiprop">.score</code> score for this listing computed as according to the FAQ</li>
                <li><code class="apiprop">.founders</code> comma-separated string of the company founders</li>
                <li><code class="apiprop">.contact_email</code> public contact email for the company</li>
                <li><code class="apiprop">.address</code> full google maps readable business address for this listing</li>
                <li><code class="apiprop">.num_comments</code> number of comments for this listing</li>
                <li><code class="apiprop">.num_bids</code> number of bids made on this listing</li>
                <li><code class="apiprop">.num_qandas</code> number of questions asked about this listing, only counts answered questions</li>
                <li><code class="apiprop">.days_ago</code> number of days that have elapsed since this listing was posted, rounded down</li>
                <li><code class="apiprop">.days_left</code> number of days until this listing closes, rounded down</li>
                <li><code class="apiprop">.monitored</code> <var>true</var> if logged in user is watching this listing, <var>false</var> otherwise</li>
                <li><code class="apiprop">.business_plan_id</code> business plan download ID for this listing via the File API</li>
                <li><code class="apiprop">.presentation_id</code> presentation download ID for this listing via the File API</li>
                <li><code class="apiprop">.financials_id</code> financial statement download ID for this listing via the File API</li>
                <li><code class="apiprop">.video</code> embed url for a video about this listing from youtube, dailymotion, or vidmeo</li>
                <li><code class="apiprop">.business_plan_upload</code> one-time URL for uploading a business plan document via the File API</li>
                <li><code class="apiprop">.presentation_upload</code> one-time URL for uploading a presentation document via the File API</li>
                <li><code class="apiprop">.financials_upload</code> one-time URL for uploading a financial statement via the File API</li>
                <li><code class="apiprop">.logo_upload</code> one-time URL for uploading a company logo via the File API</li>
                <li><code class="apiprop">answer1</code> KEY ACTIVITIES</li>
                <li><code class="apiprop">answer2</code> KEY RESOURCES</li>
                <li><code class="apiprop">answer3</code> KEY PARTNERS</li>
                <li><code class="apiprop">answer4</code> VALUE PROPOSITIONS</li>
                <li><code class="apiprop">answer5</code> CUSTOMER SEGMENTS</li>
                <li><code class="apiprop">answer6</code> CHANNELS</li>
                <li><code class="apiprop">answer7</code> CUSTOMER RELATIONSHIPS</li>
                <li><code class="apiprop">answer8</code> COST STRUCTURE</li>
                <li><code class="apiprop">answer9</code> REVENUE STREAMS</li>
                <li><code class="apiprop">answer10</code> PROBLEM</li>
                <li><code class="apiprop">answer11</code> SOLUTION</li>
                <li><code class="apiprop">answer12</code> FEATURES AND BENEFITS</li>
                <li><code class="apiprop">answer13</code> COMPANY STATUS</li>
                <li><code class="apiprop">answer14</code> MARKET</li>
                <li><code class="apiprop">answer15</code> CUSTOMER</li>
                <li><code class="apiprop">answer16</code> COMPETITORS</li>
                <li><code class="apiprop">answer17</code> COMPETITIVE COMPARISON</li>
                <li><code class="apiprop">answer18</code> BUSINESS MODEL</li>
                <li><code class="apiprop">answer19</code> MARKETING PLAN</li>
                <li><code class="apiprop">answer20</code> TEAM</li>
                <li><code class="apiprop">answer21</code> TEAM VALUES</li>
                <li><code class="apiprop">answer22</code> CURRENT FINANCIALS</li>
                <li><code class="apiprop">answer23</code> FINANCIAL PROJECTIONS</li>
                <li><code class="apiprop">answer24</code> OWNERS</li>
                <li><code class="apiprop">answer25</code> INVESTMENT</li>
                <li><code class="apiprop">answer26</code> TIMELINE AND WRAPUP</li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/get" target="listings-get">

                <div class="formitem">
                    <label class="inputlabel" for="title">ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext" type="text" name="id" id="listing_id" value="0"></input>
                    </span>
                </div>
        
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>

            </form>
            <iframe name="listings-get"></iframe>
        </div>

        <dt>GET /listing/logo/:id</dt>
        <dd>
            <p>
            Return listing logo for a given listing id. Should be used as source for image tags if data uri cannot be used.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>id</code> alphanumeric listing id as returned by the listing list and search API</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li>returns an image binary</li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/logo" target="listings-logo">

                <div class="formitem">
                    <label class="inputlabel" for="title">ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext" type="text" name="id" id="listing_id" value="0"></input>
                    </span>
                </div>
        
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>

            </form>
            <iframe name="listings-logo"></iframe>
        </div>


        <dt>POST /listing/create</dt>
        <dd>
            <p>
            Creates a new listing for the currently logged in user.  Only works for logged in users.  If the user already has a new
            listing which has not yet been approved, this existing
            listing is returned.  The happens because the user may only have one new listing at a time in the startupbidder system.
            Thus you can also call this method if you want the current in-edit listing.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><var>none</var></li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>login_url</code> URL to use for site login action</li>
                <li><code>logout_url</code> URL to use for site logout action</li>
                <li><code>loggedin_profile</code> private user profile object, see User API for profile object details</li>
                <li><code>error_code</code> error status for this call, 0 on success</li>
                <li><code>error_msg</code> error message for this call, null on success</li>
                <li><code>listing</code> the standard listing object as returned by the <var>/listing/get</var> method</li>
                <li><code>categories</code> list of all available industry categories the listing <var>category</var> can be set to</li>
            </ul>
            <h4>Test</h4>
            <form method="POST" action="/listing/create" target="listing-create">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listing-create"></iframe>
        </div>

        <dt>POST /listing/update_field</dt>
        <dd>
            <p>
            Updates a field for the currently logged in user&rsquo;s listing.  Value is a json string.  Multiple fields can be passed at once for efficient update.
            Updatable fields keys are detailed in the parameter section.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li>
                    <code>listing</code>
                    The listing property json contains one or more field-value pairs which you wish to update.  Value meanings as are per the <var>/listing/get</var> method.
                    Note that by passing the URL valued properties
                    <var>business_plan_url</var>, <var>presentation_url</var> or <var>financials_url</var>, the system will download the file at that url
                    and store it in the backend.  Listing property structure:
<pre name="code" class="js">
{
    title: :title,
    mantra: :mantra,
    summary: :summary,
    contact_email: :contact_email,
    founders: :founders,
    website: :website,
    category: :category,
    asked_fund: :asked_fund,
    suggested_amt: :suggested_amt,
    suggested_pct: :suggested_pct,
    video: :video,
    answer1: :answer1,
    answer2: :answer2,
    answer3: :answer3,
    answer4: :answer4,
    answer5: :answer5,
    answer6: :answer6,
    answer7: :answer7,
    answer8: :answer8,
    answer9: :answer9,
    answer10: :answer10,
    answer11: :answer11,
    answer12: :answer12,
    answer13: :answer13,
    answer14: :answer14,
    answer15: :answer15,
    answer16: :answer16,
    answer17: :answer17,
    answer18: :answer18,
    answer19: :answer19,
    answer20: :answer20,
    answer21: :answer21,
    answer22: :answer22,
    answer23: :answer23,
    answer24: :answer24,
    answer25: :answer25,
    answer26: :answer26,
    logo_url: :logo_url,
    business_plan_url: :business_plan_url,
    presentation_url: :presentation_url,
    financials_url: :financials_url
}
</pre>
                </li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>login_url</code> URL to use for site login action</li>
                <li><code>logout_url</code> URL to use for site logout action</li>
                <li><code>loggedin_profile</code> private user profile object, see User API for profile object details</li>
                <li><code>error_code</code> error status for this call, 0 on success</li>
                <li><code>error_msg</code> error message for this call, null on success</li>
                <li><code>listing</code> listing object as per <var>/listing/get</var> method</li>
            </ul>
            <h4>Test</h4>
            <form method="POST" action="/listing/update_field" target="listing-update_field">

                <div class="formitem">
                    <label class="inputlabel" for="title">LISTING</label>
                    <span class="inputfield">
                        <input class="text inputwidetext" type="text" name="listing" value="{ title: &rsquo;Foo, Inc.&rsquo; }"></input>
                    </span>
                </div>
        
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>

            </form>
            <iframe name="listing-update_field"></iframe>
        </div>

        <dt>POST /listing/post</dt>
        <dd>
            <p>
            Submits the user&rsquo;s new listing for approval by a startupbidder admin.  No ID is passed since the user may only have one pending listing at a time.
            After approval, the current listing will become active and the user may then create a new additional listing.
            You must be the logged in listing owner in order to post the listing.  Will return 200 on success, 500 on error if field validation fails.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><var>none</var></li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>login_url</code> URL to use for site login action</li>
                <li><code>logout_url</code> URL to use for site logout action</li>
                <li><code>loggedin_profile</code> private user profile object, see User API for profile object details</li>
                <li><code>error_code</code> error status for this call, 0 on success</li>
                <li><code>error_msg</code> error message for this call, null on success</li>
                <li><code>listing</code> listing object as per <var>/listing/get</var> method</li>
            </ul>
            <h4>Test</h4>
            <form method="POST" action="/listing/post" target="listing-post">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listing-post"></iframe>
        </div>

        <dt>POST /listing/delete</dt>
        <dd>
            <p>
            Deletes the user&rsquo;s new listing.  This only works for a listing which has not yet been approved.  To delete a listing which has already been approved, call the withdraw method.
            You must be the logged in listing owner in order to delete the listing.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><var>none</var></li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>login_url</code> URL to use for site login action</li>
                <li><code>logout_url</code> URL to use for site logout action</li>
                <li><code>loggedin_profile</code> private user profile object, see User API for profile object details</li>
                <li><code>error_code</code> error status for this call, 0 on success</li>
                <li><code>error_msg</code> error message for this call, null on success</li>
                <li><code>listing</code> listing object as per <var>/listing/get</var> method</li>
            </ul>
            <h4>Test</h4>
            <form method="POST" action="/listing/delete" target="listing-delete">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listing-delete"></iframe>
        </div>

        <dt>POST /listing/withdraw/:id</dt>
        <dd>
            <p>
            Withdraws the user&rsquo;s listing with the given id.  You must either be the logged in owner of this listing or an admin in order to call this method.
            Note that for legal reasons, listings can never be deleted once approved, they must instead be withdrawn.  Withdrawn listings are kept in the system
            as per the startupbidder data retention policy.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>id</code> alphanumeric listing id as returned by the listing list and search API</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>login_url</code> URL to use for site login action</li>
                <li><code>logout_url</code> URL to use for site logout action</li>
                <li><code>loggedin_profile</code> private user profile object, see User API for profile object details</li>
                <li><code>error_code</code> error status for this call, 0 on success</li>
                <li><code>error_msg</code> error message for this call, null on success</li>
                <li><code>listing</code> listing object as per <var>/listing/get</var> method</li>
            </ul>
            <h4>Test</h4>
            <form method="POST" action="/listing/withdraw" target="listings-withdraw">
                <div class="formitem">
                    <label class="inputlabel" for="title">ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext" type="text" name="id" id="listing_id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="listings-withdraw"></iframe>
        </div>
    </div>

    <div class="boxtitle">LOCATIONS API</div>
    <div class="boxpanel apipanel">
    <p>Find the set of location groupings, roughly city/state/country metropolitan areas, containing startups on this site</p>

        <dt>GET /listing/locations</dt>
        <dd>
            <p>
            Return the most active locations for startupbidder.  The returned location names can be used as a location value for the search API.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><var>none</var></li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li>Returns the map of the twenty locations with the most active listings on the site.  Locations are grouped by
                    brief address, which corresponds roughly to the Metropolitan Stastical Area, which is the city and country in most locations and
                    the city, state and country in the USA.  Structure is:
<pre name="code" class="js">
{
    :brief_address: :active_listing_count,
    ...
}
</pre>
                </li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/locations" target="listings-locations">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listings-locations"></iframe>
        </div>

        <dt>GET /listing/all-listing-locations</dt>
        <dd>
            <p>
            Return all locations for all listings in startupbidder in an abbreviated format.  Suitable for efficient display of all
            listings on a map.  Be aware this can return thousands of listings.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><var>none</var></li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>login_url</code> URL to use for site login action</li>
                <li><code>logout_url</code> URL to use for site logout action</li>
                <li><code>loggedin_profile</code> private user profile object, see User API for profile object details</li>
                <li><code>error_code</code> error status for this call, 0 on success</li>
                <li><code>error_msg</code> error message for this call, null on success</li>
                <li><code>listing</code> listing object as per <var>/listing/get</var> method</li>
                <li><code>map_listings</code> list of all active listing locations with their listing ID, decimal latitude, and decimal longitude.  Structure is:
<pre name="code" class="js">
[
    [ :listing_id, :latitude, :longitude ],
    ...
]
</pre>
                </li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/all-listing-locations" target="listings-all-listing-locations">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listings-all-listing-locations"></iframe>
        </div>
    </div>

    <div class="boxtitle">CATEGORIES API</div>
    <div class="boxpanel apipanel">
    <p>Find information on the various venture-capital categories supported by startupbidder including a list of startups for each category</p>

        <dt>GET /listing/categories</dt>
        <dd>
            <p>
            Return all company categories for listings.  This is a list of the possible different categories of a company within the startupbidder system.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><var>none</var></li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li>Returns a map of all company venture capital categories with the structure:
<pre name="code" class="js">
{
    :category_name: :display_name,
    ...
}
</pre>
                </li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/categories" target="listings-categories">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listings-categories"></iframe>
        </div>

        <dt>GET /listing/used_categories</dt>
        <dd>
            <p>
            Return all company categories for listings which are actually used along with the usage count.  This only returns categories which have one or more listings active.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><var>none</var></li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li>Returns a map of all company venture capital categories with at least one active listing with the structure:
<pre name="code" class="js">
{
    :category_name: :active_listing_count,
    ...
}
</pre>
                </li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/used_categories" target="listings-used_categories">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listings-used_categories"></iframe>
        </div>
    </div>

    <div class="boxtitle">COMMENTS API</div>
    <div class="boxpanel apipanel">
    <p>Find and create comments on a particular listing; comments are always public to all.</p>

        <dt>GET /listing/comments/:id</dt>
        <dd>
            <p>
            Return all comments for the given listing id.  Comments are always public.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>id</code> alphanumeric listing id as returned by the listing list and search API</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>login_url</code> URL to use for site login action</li>
                <li><code>logout_url</code> URL to use for site logout action</li>
                <li><code>loggedin_profile</code> private user profile object, see User API for profile object details</li>
                <li><code>error_code</code> error status for this call, 0 on success</li>
                <li><code>error_msg</code> error message for this call, null on success</li>
                <li><code>listing</code> listing object as per <var>/listing/get</var> method</li>
                <li><code>comments</code> list of comments ordered in date ascending order with structure:
<pre name="code" class="js">
[
    {
        comment_id: :comment_id,
        listing_id: :listing_id,
        listing_title: :title,
        profile_id: :comment_poster_id,
        profile_username: :comment_poster_username,
        text: :text,
        comment_date: :comment_date_yyyymmdd
    },
    ...
]
</pre>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/comments" target="listings-comments">
                <div class="formitem">
                    <label class="inputlabel" for="title">ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext" type="text" name="id" id="listing_id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="listings-comments"></iframe>
        </div>

        <dt>GET /comments/get/:id</dt>
        <dd>
            <p>
            Gets a comment.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>id</code> alphanumeric comment id as returned by the Comments API</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>comment_id</code> id for the comment</li>
                <li><code>listing_id</code> id for the listing this comment corresponds to</li>
                <li><code>listing_title</code> title for the comment listing</li>
                <li><code>profile_id</code> id for the user who posted this comment</li>
                <li><code>profile_username</code> username for the user who posted this comment</li>
                <li><code>text</code> text of the comment</li>
                <li><code>comment_date</code> date in YYYYMMDD format when the comment was posted</li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/comments/get" target="comments-get">
                <div class="formitem">
                    <label class="inputlabel" for="title">ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext" type="text" name="id" id="comment_id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="comments-get"></iframe>
        </div>

        <dt>POST /listing/post_comment</dt>
        <dd>
            <p>
            Post a comment from the currently logged in user concerning a particular listing.  Only listings may be commented on.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>comment</code> json of comment structure which can be posted, all fields are mandatory:
<pre name="code" class="js">
{
    listing_id: :listing_id,
    text: :text
}
</pre>
                </li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>comment_id</code> id for the comment</li>
                <li><code>listing_id</code> id for the listing this comment corresponds to</li>
                <li><code>listing_title</code> title for the comment listing</li>
                <li><code>profile_id</code> id for the user who posted this comment</li>
                <li><code>profile_username</code> username for the user who posted this comment</li>
                <li><code>text</code> text of the comment</li>
                <li><code>comment_date</code> date in YYYYMMDD format when the comment was posted</li>
            </ul>
            <h4>Test</h4>
            <form method="POST" action="/listing/post_comment" target="listings-post-comment">
                <div class="formitem">
                    <label class="inputlabel" for="title">COMMENT</label>
                    <span class="inputfield">
                        <input class="text inputwidetext" type="text" name="comment" value="{ listing_id: &rsquo;0&rsquo;, text: &rsquo;this is my comment&rsquo;}"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="listings-post-comment"></iframe>
        </div>

        <dt>POST /listing/delete_comment</dt>
        <dd>
            <p>
            Deletes the comment with the give comment id.  Deletion only works if the user is logged in and the comment was created by the same user.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>id</code> alphanumeric comment id as returned by the Comments API</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>login_url</code> URL to use for site login action</li>
                <li><code>logout_url</code> URL to use for site logout action</li>
                <li><code>loggedin_profile</code> private user profile object, see User API for profile object details</li>
                <li><code>error_code</code> error status for this call, 0 on success</li>
                <li><code>error_msg</code> error message for this call, null on success</li>
                <li><code>listing</code> listing object as per <var>/listing/get</var> method</li>
                <li><code>comments</code> list of comments ordered in date ascending order as per the <var>/listing/comments</var> method</li>
            </ul>
            <h4>Test</h4>
            <form method="POST" action="/listing/delete_comment" target="listings-delete-comment">
                <div class="formitem">
                    <label class="inputlabel" for="title">ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext" type="text" name="id" id="comment_id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="listings-delete-comment"></iframe>
        </div>

    </div>

    <div class="boxtitle">MONITOR API</div>
    <div class="boxpanel apipanel">
    <p>Watch listings and be automatically notified of bids, comments, and other changes to the listing</p>

        <dt>GET /monitors/active-for-user/:id
            <form method="GET" action="/monitors/active-for-user/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Returns list of active monitors for logged in user.
            </p>
        </dd>

        <dt>GET /monitors/active-for-listing/:id
            <form method="GET" action="/monitors/active-for-listing/"><input type="text" name="id" value="&lt;listing_id&gt;"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Returns list of active monitors for given listing.
            </p>
        </dd>

        <dt>POST /monitor/set
            <form method="POST" action="/monitor/set/"><input type="text" name="id" value="&lt;listing_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Activates monitoring of the provided listing by the currently logged in user.  This allows the user to keep track of the listing.
            <br/>
            Monitor status is provided with listing objects.
            </p>
        </dd>
        <dt>POST /monitor/deactivate
            <form method="POST" action="/monitor/deactivate/"><input type="text" name="id" value="&lt;listing_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Deactivates monitoring of the provided listing by the currently logged in user.  This means the user no longer keeps of the listing.
            </p>
        </dd>
    </div>

    <div class="boxtitle">QUESTION AND ANSWER API</div>
    <div class="boxpanel apipanel">
    <p>Find questions and answers concerning a listing and ask new ones for the owner to answer.</p>

    <dt>GET /listing/questions_answers/:id
    <dd>
        <p>Get the set of questions and answers for this listing.</p>
    </dd>

    <dt>POST /listing/ask_owner
        <form method="POST" action="/listing/ask_owner/"><input type="text" name="id" value="&lt;listing_id&gt;"></input><input type="text" name="message" value="&lt;text&gt"></input><input type="submit" value="TEST"></input></form>
    </dt>
    <dd>
        <p>Ask owner a question concerning the listing.  You must be a logged in user in order to ask a question.</p>
    </dd>

    <dt>POST /listing/answer_question
        <form method="POST" action="/listing/answer_question/"><input type="text" name="id" value="&lt;question_id&gt;"></input><input type="text" name="message" value="&lt;text&gt"></input><input type="submit" value="TEST"></input></form>
    </dt>
    <dd>
        <p>Answer a question.  Only the listing owner is allowed to answer questions.</p>
    </dd>
    </div>

    <div class="boxtitle">PRIVATE MESSAGE API</div>
    <div class="boxpanel apipanel">
    <p>Send and receive private messages between users on the site.  You must be logged in to use these methods.</p>

    <dt>GET /user/get_message_users</dt>
    <dd>
        <p>Get the list of users with which the currently logged in user has had a conversation.</p>
    </dd>

    <dt>GET /user/get_messages/:id</dt>
    <dd>
        <p>Get the list of messages between the logged in user and the user with the given <code>user_id</code>.</p>
    </dd>

    <dt>POST /user/send_message
        <form method="POST" action="/user/send_message/"><input type="text" name="id" value="&lt;user_id&gt;"></input><input type="text" name="message" value="&lt;text&gt"></input><input type="submit" value="TEST"></input></form>
    </dt>
    <dd>
        <p>Post a message from the logged in user to the user with the given <code>user_id</code>.</p>
    </dd>
    </div>

    <div class="boxtitle">NOTIFICATION API</div>
    <div class="boxpanel apipanel">
    <p>Get notifications for the currently logged in user, which includes listing notifications such as bids and questions and also private messages.</p>

        <dt>GET /notifications/user
           <form method="GET" action="/notifications/user/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>Returns the list of notifications for the logged in user.  Making this call automatically marks all notifications as read.</p>
        </dd>
    </div>

    <div class="boxtitle">FILE API</div>
    <div class="boxpanel apipanel">
    <p>Upload and download documents associated with a listing, downloads are public but uploads are only for the listing owner.</p>

        <dt>POST :upload_url
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

        <dt>GET /file/download/:id
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
    <p>Get information on individual users, full information is only available for the currently logged in user.</p>

        <dt>GET /user/loggedin
            <form method="GET" action="/user/loggedin"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Return logged in user profile data.
            </p>
        </dd>

        <dt>GET /user/get/:id
            <form method="GET" action="/user/get"><input type="text" name="id" value="&lt;user_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Return user data for a given user id.
            </p>
        </dd>

        <dt>POST /user/deactivate
            <form method="POST" action="/user/deactivate"><input type="text" name="id" value="&lt;user_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Deactivates a given user.  Note that you must be logged in as this user or an admin in order to call this method.
            </p>
        </dd>

        <dt>POST /user/check-user-name
            <form method="POST" action="/user/check-user-name/"><input type="text" name="name" value="&lt;user name&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Checks if the given username is currently taken.  If it is available, returns "true".  If the name is already taken, returns "false".  Used to check if a username exists before
            the user is allowed to update their username, as all usernames (nicknames) must be unique for the site.
            </p>
        </dd>
    </div>

    <div class="boxtitle">ADMIN API</div>
    <div class="boxpanel apipanel">
    <p>Perform administrative tasks on startupbidder; you must have administrative rights as a logged in user in order for these calls to work</p>

        <dt>GET /listing/posted
            <form method="GET" action="/listing/posted/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Returns posted status listings, that is, those which have been submitted by a user and are awaiting approval by a startupbidder admin.  Can only be called by a startupbidder admin.
            </p>
        </dd>

        <dt>GET /listing/frozen
            <form method="GET" action="/listing/frozen/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Returns frozen status listings, that is, those which have been frozen by a startupbidder admin.  Can only be called by a startupbidder admin.
            </p>
        </dd>

        <dt>POST /listing/freeze
            <form method="POST" action="/listing/freeze"><input type="text" name="id" value="&lt;listing_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Freezes the listing with the given id.  This suspends the listing pending further review, which may be done if a listing is found to be inaccurate or in violation of any law or regulation.
            This method can only be called by admins.
            </p>
        </dd>

        <dt>POST /listing/activate
            <form method="POST" action="/listing/activate"><input type="text" name="id" value="&lt;listing_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Activates the listing with the given id.  This makes the listing live and active on the site.  If asking for funds, bidding is enabled at this point.
            Commenting also becomes enabled for the listing.  This method can only be called if the listing is in posted or frozen state.
            This method can only be called by admins.
            </p>
        </dd>

        <dt>POST /listing/send_back
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

<div id="loadmsg"></div>

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
  <!-- JavaScript at the bottom for fast page loading -->
  <script src="js/libs/prevel.min.js"></script>
  <script src="js/libs/hl-all.js"></script>
  <script src="js/modules/base.js"></script>
  <script src="js/modules/apipage.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
