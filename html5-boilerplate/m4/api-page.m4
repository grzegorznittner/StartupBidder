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

        <dt>GET /listings/discover</dt>
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
                    the city, state and country in the USA.  Sructure is:
<pre name="code" class="js">
{
    :brief_address: :active_listing_count,
    ...
}
</pre>
                </li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listings/discover" target="listings-discover">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listings-discover"></iframe>
            <p>
        </div>

        <dt>GET /listings/discover_user</dt>
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
                <li><code>categories</code> map of listing categories, same as for <var>/listings/discover</var></li>
                <li><code>top_locations</code> map of top locations, same as for <var>/listings/discover</var></li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listings/discover_user" target="listings-discover_user">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listings-discover_user"></iframe>
            <p>
        </div>

        <dt>GET /listings/monitored</dt>
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
    :start_index: :index,
    :max_results: :max,
    :num_results: :n,
    :more_results_url: :url
}
</pre>
                </li>
                <li><code>categories</code> map of listing categories, same as for <var>/listings/discover</var></li>
                <li><code>top_locations</code> map of top locations, same as for <var>/listings/discover</var></li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listings/monitored" target="listings-monitored">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listings-monitored"></iframe>
            <p>
        </div>

        <dt>GET /listings/top</dt>
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
    :start_index: :index,
    :max_results: :max,
    :num_results: :n,
    :more_results_url: :url
}
</pre>
                </li>
                <li><code>categories</code> map of listing categories, same as for <var>/listings/discover</var></li>
                <li><code>top_locations</code> map of top locations, same as for <var>/listings/discover</var></li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listings/top" target="listings-top">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listings-top"></iframe>
            <p>
        </div>
<!--
        <dt>GET /listings/valuation</dt>
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
    :start_index: :index,
    :max_results: :max,
    :num_results: :n,
    :more_results_url: :url
}
</pre>
                </li>
                <li><code>categories</code> map of listing categories, same as for <var>/listings/discover</var></li>
                <li><code>top_locations</code> map of top locations, same as for <var>/listings/discover</var></li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listings/valuation" target="listings-valuation">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listings-valuation"></iframe>
            <p>
        </div>
-->
        <dt>GET /listings/closing</dt>
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
    :start_index: :index,
    :max_results: :max,
    :num_results: :n,
    :more_results_url: :url
}
</pre>
                </li>
                <li><code>categories</code> map of listing categories, same as for <var>/listings/discover</var></li>
                <li><code>top_locations</code> map of top locations, same as for <var>/listings/discover</var></li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listings/closing" target="listings-closing">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listings-closing"></iframe>
            <p>
        </div>

        <dt>GET /listings/latest</dt>
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
    :start_index: :index,
    :max_results: :max,
    :num_results: :n,
    :more_results_url: :url
}
</pre>
                </li>
                <li><code>categories</code> map of listing categories, same as for <var>/listings/discover</var></li>
                <li><code>top_locations</code> map of top locations, same as for <var>/listings/discover</var></li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listings/latest" target="listings-latest">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listings-latest"></iframe>
            <p>
        </div>
    </div>

    <div class="boxtitle">SEARCH API</div>
    <div class="boxpanel apipanel">
    <p>Search for a set of listings using keywords, location, and category matches</p>
    
        <dt>GET /listings/keyword</dt>
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
                    <var>location:name</var> - match by location <var>name</var> as per <var>/listings/locations</var>
                    </p>
                    <p>
                    <var>category:name</var> - match by category <var>name</var> as per <var>/listings/categories</var>
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
    :start_index: :index,
    :max_results: :max,
    :num_results: :n,
    :more_results_url: :url
}
</pre>
                </li>
                <li><code>categories</code> map of listing categories, same as for <var>/listings/discover</var></li>
                <li><code>top_locations</code> map of top locations, same as for <var>/listings/discover</var></li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listings/keyword" target="listings-keyword">

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
            <p>
        </div>
    </div>

    <div class="boxtitle">LISTING API</div>
    <div class="boxpanel apipanel">
    <p>Get and update information on an individual company listing</p>

        <dt>GET /listings/get/:id</dt>
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
                <li><code class="apiprop">.status</code> progresses from <var>new</var> -> <var>posted</var> -> <var>active</var>, can also be <var>withdrawn</var> or <var>frozen</var></li>
                <li><code class="apiprop">.mantra</code> one sentance summary of the business</li>
                <li><code class="apiprop">.summary</code> one paragraph summary of the company</li>
                <li><code class="apiprop">.website</code> external website URL of the company</li>
                <li><code class="apiprop">.category</code> industry category as per <var>/listings/categories</var></li>
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
                <li><code class="apiprop">.presentation_upload</code> one-time URL for uploading a presentation document via the File API<</li>
                <li><code class="apiprop">.financials_upload</code> one-time URL for uploading a financial statement via the File API<</li>
                <li><code class="apiprop">.logo_upload</code> one-time URL for uploading a company logo via the File API<</li>
                <li><code class="apiprop">.answer1</code> the fields answer1 through answer26 are used for BMC and presentation construction</li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listings/get" target="listings-get">

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
            <p>
        </div>

        <dt>GET /listings/logo/:id
            <form method="GET" action="/listings/logo"><input type="text" name="id" value="&lt;listing_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Return listing logo for a given listing id. Should be used as source for image tags if data uri cannot be used.
            </p>
        </dd>

        <dt>POST /listing/create
            <form method="POST" action="/listings/create/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Creates a new listing for the currently logged in user.  Only works for logged in users.  If the user already has a new
            listing which has not yet been approved, this existing
            listing is returned.  The happens because the user may only have one new listing at a time in the startupbidder system.
            Thus you can also call this method if you want the current in-edit listing.
            </p>
        </dd>

        <dt>POST /listing/update_field
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

        <dt>POST /listing/post
            <form method="POST" action="/listing/post/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Submits the user&rsquo;s new listing for approval by a startupbidder admin.  After approval, the current listing will become active and the user may then create a new additional listing.
            </p>
        </dd>

        <dt>POST /listing/delete
            <form method="POST" action="/listing/delete/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Deletes the user&rsquo;s new listing.  This only works for a listing which has not yet been approved.  To delete a listing which has already been approved, call the withdraw method.
            </p>
        </dd>

        <dt>POST /listing/withdraw
            <form method="POST" action="/listing/withdraw"><input type="text" name="id" value="&lt;listing_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Withdraws the listing with the given id.  You must either be the logged in owner of this listing or an admin in order to call this method.  Note that for regulatory reasons,
            listings can never be deleted once approved, they must instead be withdrawn.  Withdrawn listings are kept in the system as per the startupbidder data retention policy.
            </p>
        </dd>

    </div>

    <div class="boxtitle">LOCATIONS API</div>
    <div class="boxpanel apipanel">
    <p>Find the set of location groupings, roughly city/state/country metropolitan areas, containing startups on this site</p>

        <dt>GET /listings/locations
            <form method="GET" action="/listings/locations/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Return the most active locations for startupbidder.  The returned location names can be used as a location value for the search API.
            </p>
        </dd>
        <dt>GET /listings/all-listing-locations
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
    <p>Find information on the various venture-capital categories supported by startupbidder including a list of startups for each category</p>

        <dt>GET /listings/categories
            <form method="GET" action="/listings/categories/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Return all company categories for listings.  This is a list of the possible different categories of a company within the startupbidder system.
            </p>
        </dd>
        <dt>GET /listings/used_categories
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
    <p>Find and create comments on a particular listing; comments are always public to all.</p>

        <dt>GET /comments/listing/:id
            <form method="GET" action="/comments/listing/"><input type="text" name="id" value="&lt;listing_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Return all comments for the given listing id.
            </p>
        </dd>
        <dt>GET /comments/user/:id
            <form method="GET" action="/comments/user/"><input type="text" name="id" value="&lt;user_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Return all comments for the given user id.
            </p>
        </dd>
        <dt>GET /comments/get/:id
            <form method="GET" action="/comments/get/"><input type="text" name="id" value="&lt;comment_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Return an individual comment for the given comment id.
            </p>
        </dd>
        <dt>POST /comment/create
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
        <dt>POST /comment/delete
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

        <dt>GET /listings/posted
            <form method="GET" action="/listings/posted/"><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            Returns posted status listings, that is, those which have been submitted by a user and are awaiting approval by a startupbidder admin.  Can only be called by a startupbidder admin.
            </p>
        </dd>

        <dt>GET /listings/frozen
            <form method="GET" action="/listings/frozen/"><input type="submit" value="TEST"></input></form>
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
