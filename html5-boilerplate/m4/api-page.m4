`
<!doctype html>
<html lang="en">
'
include(api-head.m4)
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
                <li><code>latest_listings</code> list of the four listings most recently activated on the site</li>
                <li><code>monitored_listings</code> list of the four most recently watched listings for the logged in user, null if not logged in</li>
                <li><code>users_listings</code> list of the four listings most recently posted by the currently logged in user, null otherwise</li>
                <li><code>edited_listing</code> the logged in user&rsquo;s listing currently being edited but not yet approved, null otherwise</li>
                <li><code>categories</code> the map of all venture capital industry categories on the site with at least one active listing.  Structure is:
<pre name="code" class="brush: js">
{
    :category_name: :active_listing_count,
    ...
}
</pre>
                </li>
                <li><code>top_locations</code> the map of the twenty locations with the most active listings on the site.  Locations are grouped by
                    brief address, which corresponds roughly to the Metropolitan Stastical Area, which is the city and country in most locations and
                    the city, state and country in the USA.  Structure is:
<pre name="code" class="brush: js">
{
    :brief_address: :active_listing_count,
    ...
}
</pre>
                </li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/discover" target="listing-discover">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listing-discover"></iframe>
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
                <li><code>monitored_listings</code> list of the four listings most recently watched by the user</li>
                <li><code>edited_listing</code> the logged in user&rsquo;s listing currently being edited but not yet approved, null otherwise</li>
                <li><code>notifications</code> list of the user notifications, unread first then by date order, see Notification API for details</li>
                <li><code>admin_posted_listings</code> list of all listings submitted for review but not yet approved, null except for logged in admins</li>
                <li><code>admin_frozen_listings</code> list of all administratively frozen listings, null except for logged in admins</li>
                <li><code>categories</code> map of listing categories, same as for <var>/listing/discover</var></li>
                <li><code>top_locations</code> map of top locations, same as for <var>/listing/discover</var></li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/discover_user" target="listing-discover_user">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listing-discover_user"></iframe>
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
                <li><code>listings_props</code> list properties, call <var>more_results_url</var> in AJAX for more, structure:
<pre name="code" class="brush: js">
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
            <form method="GET" action="/listing/monitored" target="listing-monitored">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listing-monitored"></iframe>
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
                <li><code>listings_props</code> list properties, call <var>more_results_url</var> in AJAX for more, structure:
<pre name="code" class="brush: js">
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
            <form method="GET" action="/listing/top" target="listing-top">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listing-top"></iframe>
        </div>

        <dt>GET /listing/valuation</dt>
        <dd>
            <p>
            Returns the most valued active listings on startupbidder, ordered by total accepted bids descending.
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
                <li><code>listings_props</code> list properties, call <var>more_results_url</var> in AJAX for more, structure:
<pre name="code" class="brush: js">
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
            <form method="GET" action="/listing/valuation" target="listing-valuation">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listing-valuation"></iframe>
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
                <li><code>listings_props</code> list properties, call <var>more_results_url</var> in AJAX for more, structure:
<pre name="code" class="brush: js">
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
            <form method="GET" action="/listing/latest" target="listing-latest">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listing-latest"></iframe>
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
                <li><code>listings_props</code> list properties, call <var>more_results_url</var> in AJAX for more, structure:
<pre name="code" class="brush: js">
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
            <form method="GET" action="/listing/keyword" target="listing-keyword">
                <div class="formitem">
                    <label class="inputlabel" for="title">TEXT</label>
                    <span class="inputfield">
                        <input class="text inputwidetext" type="text" name="text" value="software"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>

            </form>
            <iframe name="listing-keyword"></iframe>
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
                <li><code class="apiprop">.total_raised</code> total amount of accepted bids for this listing</li>
                <li><code class="apiprop">.num_qandas</code> number of questions asked about this listing, only counts answered questions</li>
                <li><code class="apiprop">.days_ago</code> number of days that have elapsed since this listing was posted, rounded down</li>
                <li><code class="apiprop">.monitored</code> <var>true</var> if logged in user is watching this listing, <var>false</var> otherwise</li>
                <li><code class="apiprop">.business_plan_id</code> business plan download ID for this listing via the File API</li>
                <li><code class="apiprop">.presentation_id</code> presentation download ID for this listing via the File API</li>
                <li><code class="apiprop">.financials_id</code> financial statement download ID for this listing via the File API</li>
                <li><code class="apiprop">.video</code> embed url for a video about this listing from youtube, dailymotion, or vidmeo</li>
                <li><code class="apiprop">.business_plan_upload</code> one-time URL for uploading a business plan document via the File API</li>
                <li><code class="apiprop">.presentation_upload</code> one-time URL for uploading a presentation document via the File API</li>
                <li><code class="apiprop">.financials_upload</code> one-time URL for uploading a financial statement via the File API</li>
                <li><code class="apiprop">.logo_upload</code> one-time URL for uploading a company logo via the File API</li>
                <li><code class="apiprop">.answer1</code> KEY ACTIVITIES</li>
                <li><code class="apiprop">.answer2</code> KEY RESOURCES</li>
                <li><code class="apiprop">.answer3</code> KEY PARTNERS</li>
                <li><code class="apiprop">.answer4</code> VALUE PROPOSITIONS</li>
                <li><code class="apiprop">.answer5</code> CUSTOMER SEGMENTS</li>
                <li><code class="apiprop">.answer6</code> CHANNELS</li>
                <li><code class="apiprop">.answer7</code> CUSTOMER RELATIONSHIPS</li>
                <li><code class="apiprop">.answer8</code> COST STRUCTURE</li>
                <li><code class="apiprop">.answer9</code> REVENUE STREAMS</li>
                <li><code class="apiprop">.answer10</code> PROBLEM</li>
                <li><code class="apiprop">.answer11</code> SOLUTION</li>
                <li><code class="apiprop">.answer12</code> FEATURES AND BENEFITS</li>
                <li><code class="apiprop">.answer13</code> COMPANY STATUS</li>
                <li><code class="apiprop">.answer14</code> MARKET</li>
                <li><code class="apiprop">.answer15</code> CUSTOMER</li>
                <li><code class="apiprop">.answer16</code> COMPETITORS</li>
                <li><code class="apiprop">.answer17</code> COMPETITIVE COMPARISON</li>
                <li><code class="apiprop">.answer18</code> BUSINESS MODEL</li>
                <li><code class="apiprop">.answer19</code> MARKETING PLAN</li>
                <li><code class="apiprop">.answer20</code> TEAM</li>
                <li><code class="apiprop">.answer21</code> TEAM VALUES</li>
                <li><code class="apiprop">.answer22</code> CURRENT FINANCIALS</li>
                <li><code class="apiprop">.answer23</code> FINANCIAL PROJECTIONS</li>
                <li><code class="apiprop">.answer24</code> OWNERS</li>
                <li><code class="apiprop">.answer25</code> INVESTMENT</li>
                <li><code class="apiprop">.answer26</code> TIMELINE AND WRAPUP</li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/get" target="listing-get">
                <div class="formitem">
                    <label class="inputlabel" for="title">ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext listingid" type="text" name="id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="listing-get"></iframe>
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
            <form method="GET" action="/listing/logo" target="listing-logo">
                <div class="formitem">
                    <label class="inputlabel" for="title">ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext listingid" type="text" name="id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="listing-logo"></iframe>
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
<pre name="code" class="brush: js">
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
            Deletes the user&rsquo;s new listing.  This only works for a listing which has not yet been approved.  To delete a listing which has already been approved,
            call the withdraw method.  You must be the logged in listing owner in order to delete the listing.
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

        <dt>POST /listing/withdraw</dt>
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
            <form method="POST" action="/listing/withdraw" target="listing-withdraw">
                <div class="formitem">
                    <label class="inputlabel" for="title">ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext listingid" type="text" name="id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="listing-withdraw"></iframe>
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
<pre name="code" class="brush: js">
{
    :brief_address: :active_listing_count,
    ...
}
</pre>
                </li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/locations" target="listing-locations">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listing-locations"></iframe>
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
<pre name="code" class="brush: js">
[
    [ :listing_id, :latitude, :longitude ],
    ...
]
</pre>
                </li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/all-listing-locations" target="listing-all-listing-locations">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listing-all-listing-locations"></iframe>
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
<pre name="code" class="brush: js">
{
    :category_name: :display_name,
    ...
}
</pre>
                </li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/categories" target="listing-categories">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listing-categories"></iframe>
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
<pre name="code" class="brush: js">
{
    :category_name: :active_listing_count,
    ...
}
</pre>
                </li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/used_categories" target="listing-used_categories">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listing-used_categories"></iframe>
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
<pre name="code" class="brush: js">
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
            <form method="GET" action="/listing/comments" target="listing-comments">
                <div class="formitem">
                    <label class="inputlabel" for="title">ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext listingid" type="text" name="id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="listing-comments"></iframe>
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
                        <input class="text inputwidetext commentid" type="text" name="id" value="0"></input>
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
<pre name="code" class="brush: js">
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
            <form method="POST" action="/listing/post_comment" target="listing-post-comment">
                <div class="formitem">
                    <label class="inputlabel" for="title">COMMENT</label>
                    <span class="inputfield">
                        <input class="text inputwidetext commentobj" type="text" name="comment" value="{ listing_id: &rsquo;0&rsquo;, text: &rsquo;this is my comment&rsquo;}"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="listing-post-comment"></iframe>
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
            <form method="POST" action="/listing/delete_comment" target="listing-delete-comment">
                <div class="formitem">
                    <label class="inputlabel" for="title">ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext commentid" type="text" name="id" id="comment_id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="listing-delete-comment"></iframe>
        </div>

    </div>

    <div class="boxtitle">MONITOR API</div>
    <div class="boxpanel apipanel">
    <p>Watch listings and be automatically notified of bids, comments, and other changes to the listing</p>

        <dt>GET /monitor/active-for-user</dt>
        <dd>
            <p>
            Returns list of active monitors for logged in user.  Monitors are used for watching listings.
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
                <li><code>profile</code> logged in user profile as per the User API</li>
                <li><code>monitors</code> watch listing monitors for the user with structure:</li>
<pre name="code" class="brush: js">
[
    {
        monitor_id: :monitor_id,
        listing_id: :listing_id,
        type: :monitor_type,
        profile_id: :comment_poster_id,
        profile_username: :comment_poster_username,
        create_date: :create_date_yyyymmdd
        deactivate_date: :deactivate_date_yyyymmdd,
        active: :active_flag
    },
    ...
]
</pre>
                </li>
                <li><code>monitors_props</code> list properties, call <var>more_results_url</var> in AJAX for more, structure:
<pre name="code" class="brush: js">
{
    start_index: :index,
    max_results: :max,
    num_results: :n,
    more_results_url: :url
}
</pre>
                </li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/monitor/active-for-user" target="monitors-active-for-user">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="monitors-active-for-user"></iframe>
        </div>

        <dt>GET /monitors/active-for-listing/:id</dt>
        <dd>
            <p>
            Returns list of active monitors for the given listing.  This can be used to find the users which are watching a given listing.
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
                <li><code>monitors</code> list of monitors as detailed by the <var>/monitor/set</var> method with structure:</li>
<pre name="code" class="brush: js">
[
    {
        monitor_id: :monitor_id,
        listing_id: :listing_id,
        type: :monitor_type,
        profile_id: :comment_poster_id,
        profile_username: :comment_poster_username,
        create_date: :create_date_yyyymmdd
        deactivate_date: :deactivate_date_yyyymmdd,
        active: :active_flag
    },
    ...
]
</pre>
                </li>
                <li><code>monitors_props</code> list properties, call <var>more_results_url</var> in AJAX for more, structure:
<pre name="code" class="brush: js">
{
    start_index: :index,
    max_results: :max,
    num_results: :n,
    more_results_url: :url
}
</pre>
                </li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/monitor/active-for-listing" target="monitors-active-for-listing">
                <div class="formitem">
                    <label class="inputlabel" for="title">LISTING ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext listingid" type="text" name="id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="monitors-active-for-listing"></iframe>
        </div>

        <dt>POST /monitor/set
            <form method="POST" action="/monitor/set/"><input type="text" name="id" value="&lt;listing_id&gt;"></input><input type="submit" value="TEST"></input></form>
        </dt>
        <dd>
            <p>
            <br/>
            Monitor status is provided with listing objects.
            </p>
        </dd>

        <dt>POST /monitor/set/:id</dt>
        <dd>
            <p>
            Activates monitoring of the provided listing by the currently logged in user.  This allows the user to keep track of the listing.
            The <var>monitor</var> property of the <var>listing</var> property in the Listing API will then be true for this listing-user pair.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>id</code> alphanumeric listing id as returned by the listing list and search API</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>monitor_id</code> monitor id unique to the user and listing pair</li>
                <li><code>listing_id</code> listing id for the listing being watched</li>
                <li><code>type</code> always <var>null</var>, unused</li>
                <li><code>profile_id</code> profile id for the user watching this listing</li>
                <li><code>profile_username</code> username for the user watching this listing</li>
                <li><code>create_date</code> date the user started watching this listing in YYYYMMDD format</li>
                <li><code>deactivate_date</code> date the user stopped watching this listing in YYYYMMDD format</li>
                <li><code>active</code> will always be <var>true</var> after a successful set call</li>
            </ul>
            <h4>Test</h4>
            <form method="POST" action="/monitor/set" target="monitor-set">
                <div class="formitem">
                    <label class="inputlabel" for="title">LISTING ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext listingid" type="text" name="id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="monitor-set"></iframe>
        </div>

        <dt>POST /monitor/deactivate/:id</dt>
        <dd>
            <p>
            Deactivates monitoring of the provided listing by the currently logged in user.  This means the user no longer keeps track of the listing.
            The <var>monitor</var> property of the <var>listing</var> property in the Listing API will then be false for this listing-user pair.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>id</code> alphanumeric listing id as returned by the listing list and search API</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>monitor_id</code> monitor id unique to the user and listing pair</li>
                <li><code>listing_id</code> listing id for the listing being watched</li>
                <li><code>type</code> always <var>null</var>, unused</li>
                <li><code>profile_id</code> profile id for the user watching this listing</li>
                <li><code>profile_username</code> username for the user watching this listing</li>
                <li><code>create_date</code> date the user started watching this listing in YYYYMMDD format</li>
                <li><code>deactivate_date</code> date the user stopped watching this listing in YYYYMMDD format</li>
                <li><code>active</code> will always be <var>false</var> after a successful deactivate call</li>
            </ul>
            <h4>Test</h4>
            <form method="POST" action="/monitor/deactivate" target="monitor-deactivate">
                <div class="formitem">
                    <label class="inputlabel" for="title">LISTING ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext listingid" type="text" name="id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="monitor-deactivate"></iframe>
        </div>

    </div>

    <div class="boxtitle">QUESTION AND ANSWER API</div>
    <div class="boxpanel apipanel">
    <p>Find questions and answers concerning a listing and ask new ones for the owner to answer.</p>

        <dt>GET /listing/questions_answers/:id</dt>
        <dd>
            <p>
            Get the set of questions and answers for this listing.  Listing owner will see all questions,
            logged in user will see all answered questions and any questions they have asked personally,
            public users see only all answered questions.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>id</code> alphanumeric listing id</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li>list of questions for this listing, see <var>/listing/ask_owner</var> for field detials.  Structure:
<pre name="code" class="brush: js">
[
    {
    question_id: :id,
    listing_id: :id,
    user_id: :uid,
    user_nickname: :name,
    question: :question_text,
    answer: :answer_text,
    create_date: :date_yyyymmddhh24mmss,
    answer_date: :date_yyyymmddhh24mmss,
    published: :true_or_false
    },
    ...
]
</pre>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/questions_answers" target="listing-questions_answers">
                <div class="formitem">
                    <label class="inputlabel" for="title">ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext listingid" type="text" name="id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="listing-questions_answers"></iframe>
        </div>

        <dt>POST /listing/ask_owner</dt>
        <dd>
            <p>Ask owner a question concerning the listing.  You must be a logged in user in order to ask a question.</p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>message</code> ask question object for owner</li>
                <li><code class="apiprop">.listing_id</code> alphanumeric listing id as returned by the listing list and search API</li>
                <li><code class="apiprop">.text</code> text message user wishes to ask the owner</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>question_id</code> unique ID for this question</li>
                <li><code>from_user_id</code> user id who asked this question</li>
                <li><code>from_user_nickname</code> username of user who asked this question</li>
                <li><code>text_2</code> full question text</li>
                <li><code>answer</code> full question answer, <var>null</var> if question is not answered yet</li>
                <li><code>answer_date</code> date when question was answered, in YYYYMMDDHH24MMSS format, null if unanswered</li>
                <li><code>create_date</code> date when question was asked, in YYYYMMDDHH24MMSS format</li>
            </ul>
            <h4>Test</h4>
            <form method="POST" action="/listing/ask_owner" target="listing-ask_owner">
                <div class="formitem clear">
                    <label class="inputlabel" for="title">ASK</label>
                    <span class="inputfield">
                        <input class="text inputwidetext askobj" type="text" name="ask" value="{ listing_id: 0, text: &rsquo;put your question here&rsquo; }"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="listing-ask_owner"></iframe>
        </div>

        <dt>POST /listing/answer_question</dt>
        <dd>
            <p>Ask owner a question concerning the listing.  You must be a logged in user in order to ask a question.</p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>message</code> answer question by owner object</li>
                <li><code class="apiprop">.question_id</code> alphanumeric question id as returned by <var>/listing/questions_answers</var></li>
                <li><code class="apiprop">.text</code> text message of owner&rsquo;s answer to the question</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><var>same as /listing/ask_owner method</var></li>
            </ul>
            <h4>Test</h4>
            <form method="POST" action="/listing/answer_question" target="listing-answer_question">
                <div class="formitem clear">
                    <label class="inputlabel" for="title">ANSWER</label>
                    <span class="inputfield">
                        <input class="text inputwidetext answerobj" type="text" name="answer" value="{ question_id: 0, text: &rsquo;put your answer here&rsquo; }"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="listing-answer_question"></iframe>
        </div>

    </div>

    <div class="boxtitle">PRIVATE MESSAGES API</div>
    <div class="boxpanel apipanel">
    <p>Send and receive private messages between users on the site.  You must be logged in to use these methods.</p>

        <dt>GET /user/message_users</dt>
        <dd>
            <p>Get the list of users with which the currently logged in user has had a conversation.  Call this first in order to get access
            to user conversations which you can then retrieve via the <var>/user/messages</var> method</p>
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
                <li><code>users</code> list of users along with the latest conversation snippet, and whether it&rsquo;s been read.  Structure:
<pre name="code" class="brush: js">
[
    {
        from_user_id: :uid,
        from_user_nickname: :name,
        last_text: :message,
        last_date: :date_yyyymmddhh24mmss,
        counter: :number_of_messages
        read: :true_or_false
    },
    ...
]
</pre>
                <li><code>users_props</code> list properties, call <var>more_results_url</var> in AJAX for more, structure:
<pre name="code" class="brush: js">
{
    start_index: :index,
    max_results: :max,
    num_results: :n,
    more_results_url: :url
}
</pre>
                </li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/user/message_users" target="user-message_users">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="user-message_users"></iframe>
        </div>

        <dt>GET /user/messages/:id</dt>
        <dd>
            <p>Get the list of messages between the logged in user and the user with the given user <var>id</var>.</p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>id</code> alphanumeric user id</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>login_url</code> URL to use for site login action</li>
                <li><code>logout_url</code> URL to use for site logout action</li>
                <li><code>loggedin_profile</code> private user profile object, see User API for profile object details</li>
                <li><code>error_code</code> error status for this call, 0 on success</li>
                <li><code>error_msg</code> error message for this call, null on success</li>
                <li><code>other_user_profile</code> profile object for user on the other side of the conversation, with private fields hidden</li>
                <li><code>messages</code> messages between logged in user and other user, from logged in user&rsquo;s perspective,
                    ordered by date ascending.  Structure:
<pre name="code" class="brush: js">
[
    {
        direction: :sent_or_received, /* "sent" if sent by loggedin user, "received" if received */
        text: :message_text,
        create_date: :date_yyyymmddhh24mmss
    },
    ...
]
</pre>
                </li>
                <li><code>messages_props</code> list properties, call <var>more_results_url</var> in AJAX for more, structure:
<pre name="code" class="brush: js">
{
    start_index: :index,
    max_results: :max,
    num_results: :n,
    more_results_url: :url
}
</pre>
                </li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/user/messages" target="user-messages">
                <div class="formitem">
                    <label class="inputlabel" for="title">USER ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext profileid" type="text" name="id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="user-messages"></iframe>
        </div>

        <dt>POST /user/send_message</dt>
        <dd>
            <p>Post a message from the logged in user to the user identified in the message field.</p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>send</code> json object of message send with properties:</li>
                <li><code class="apiprop">.profile_id</code> alphanumeric user id</li>
                <li><code class="apiprop">.text</code> message text</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><var>json response of new message object:</var>
<pre name="code" class="brush: js">
{
    direction: :sent_or_received, /* "sent" if sent by loggedin user, "received" if received */
    text: :message_text,
    create_date: :date_yyyymmddhh24mmss
}
</pre>
                </li>
            </ul>
            <h4>Test</h4>
            <form method="POST" action="/user/send_message" target="user-send_message">
                <div class="formitem">
                    <label class="inputlabel" for="title">SEND</label>
                    <span class="inputfield">
                        <input class="text inputwidetext sendobj" type="text" name="send" value="{ profile_id: 0, text: &rsquo;put message here&rsquo; }"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="user-send_message"></iframe>
        </div>

    </div>

    <div class="boxtitle">NOTIFICATION API</div>
    <div class="boxpanel apipanel">
    <p>Get notifications for the currently logged in user, which includes listing notifications such as bids and questions and also private messages.</p>

        <dt>GET /notification/user</dt>
        <dd>
            <p>Returns the list of notifications for the logged in user.  Making this call automatically marks all notifications as read.</p>
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
                <li><code>notifications</code> list of user notifications in descending date order, structure:
<pre name="code" class="brush: js">
[
    {
        notify_type: :type, /* "notification", "bid", "comment", "ask_listing_owner" or "private_message" */
        title: :title_text,
        text_1: :notification_text,
        create_date: :date_yyyymmddhh24mmss,
        read: :true_or_false, /* whether message has been read */
        link: :url /* link for underlying content to which notification refers, may be listing url, etc. */
    }
]
</pre>
                </li>
                <li><code>notifications_props</code> list properties, call <var>more_results_url</var> in AJAX for more, structure:
<pre name="code" class="brush: js">
{
    start_index: :index,
    max_results: :max,
    num_results: :n,
    more_results_url: :url
}
</pre>
                </li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/notification/user" target="notification-user">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="notification-user"></iframe>
        </div>

     </div>

    <div class="boxtitle">FILE API</div>
    <div class="boxpanel apipanel">
    <p>Upload and download documents for a listing, downloads are public but uploads can only be by the owner for a <var>new</var> status listing.</p>

        <dt>POST :upload_url</dt>
        <dd>
            <p>
            Uploads a file for the currently logged in user&rsquo;s edited listing.  The upload url must be obtained from the <var>/listing/create</var> method
            and is the value of one of the fields: business_plan_upload, presentation_upload, financials_upload, or logo_upload. Then when the form is submitted,
            the file will be uploaded.  The file can then be retrieved via the listing file download method.  Also see the <var>/listing/update_field</var> method
            for an alternative way to get files into the system via url passing.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <p>NOTE: only one of the following parameters may be passed in a single call, and it must be of input type="file" with a <var>name</var> of:</p>
            <ul>
                <li><code>BUSINESS_PLAN</code> upload a business plan, either a Word or PDF document</li>
                <li><code>PRESENTATION</code> upload a product presentation, either a Powerpoint or PDF document</li>
                <li><code>FINANCIALS</code> upload financial statements, either an Excel or PDF document</li>
                <li><code>LOGO</code> upload a logo, must be of png, jpeg or gif format, resized and cropped to 146px by 146px</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><var>200</var> status code if upload succeeded upon form submit, otherwise an error code</li>
            </ul>
            <h4>Test</h4>
            <form method="POST" class="uploadurl" action="upload_url" target="upload_url">
                <div class="formitem">
                    <label class="inputlabel" for="title">PLAN</label>
                    <span class="inputfield">
                        <input class="text inputwidetext" type="file" name="BUSINESS_PLAN" value="Attach Business Plan"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <label class="inputlabel" for="title">PRESENTATION</label>
                    <span class="inputfield">
                        <input class="text inputwidetext" type="file" name="PRESENTATION" value="Attach Presentation"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <label class="inputlabel" for="title">FINANCIALS</label>
                    <span class="inputfield">
                        <input class="text inputwidetext" type="file" name="FINANCIALS" value="Attach Financial Statements"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <label class="inputlabel" for="title">LOGO</label>
                    <span class="inputfield">
                        <input class="text inputwidetext" type="file" name="LOGO" value="Attach Logo"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="upload_url"></iframe>
        </div>

        <dt>GET /file/download/:id</dt>
        <dd>
            <p>
            Downloads a listing file with the given <var>id</var> as returned by the <var>/listing/get</var> method.  Note that logos cannot be downloaded via this method,
            they must be downloaded via the <var>/listing/logo</var> method or via the data URI returned by <var>/listing/get</var> or in the listing list methods.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>id</code> file ID as returned by the <var>/listing/get</var> method field business_plan_id, presentation_id, or financials_id</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><var>binary file</var></li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/file/download" target="file-download">
                <div class="formitem">
                    <label class="inputlabel" for="title">FILE ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext fileid" type="text" name="id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="file-download"></iframe>
        </div>
    </div>

    <div class="boxtitle">BID API</div>
    <div class="boxpanel apipanel">
        <p>Get information on investor bids for a listing.  Some bid information is available publicly in anonymous format
           with private data removed, this is the order book used to show what bids are happening for this listing.
           Detailed bids are private and obtained in reference to the listing and investor.</p>
        <p>The client should consider two cases for private bids: first, if the logged in user is the listing owner; and second, if the
           logged in user is an actual or potential investor, not the owner.  In the first case, <var>/listing/bid_investors</var>
           should be called as there may be multiple investors the owner can view.  In the second case, <var>/listing/bids</var>
           should be called instead as the investor can only view their own bids.
           Note that all bids are private, only viewable by the bidder and listing owner.</p>

        <dt>GET /listing/order_book/:id</dt>
        <dd>
            <p>Get the public order book, the list of bid activiity for this listing, with private data removed, ordered by date ascending.
                Bids are listed in three categories: investor bids, owner asks, and accepted bids.</p>
            <p>Note that only the anonymized, public aspects of the bid are shown.
                Thus, the bidder and bid note fields are not shown, so there is no way of tying the bid to the specific bidder.
                Instead only the bid action, amount, percent, implied valuation and date are given.
                Since this information has private data removed, it may be displayed publicly even to non-logged in visitors.</p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>id</code> listing ID</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>login_url</code> URL to use for site login action</li>
                <li><code>logout_url</code> URL to use for site logout action</li>
                <li><code>loggedin_profile</code> private user profile object, see User API for profile object details</li>
                <li><code>error_code</code> error status for this call, 0 on success</li>
                <li><code>error_msg</code> error message for this call, null on success</li>
                <li><code>investor_bids</code> list of most recent investor POST, COUNTER or ACCEPT bids for this listing, ordered by valuation descending,
                    with private information removed, structure:
<pre name="code" class="brush: js">
[
    {
        amt: :amount,
        pct: :pct,
        val: :val, /* calculated as amt / (pct / 100) */
        type: :bid_type, /* INVESTOR_POST, INVESTOR_COUNTER, INVESTOR_ACCEPT */
        created_date: :date_yyyymmddhh24mmss
    },
    ...
]
</pre>
                </li>
                <li><code>owner_bids</code> list of most recent owner COUNTER or ACCEPT bids for this listing, ordered by valuation ascending,
                    with private information removed, structure:
<pre name="code" class="brush: js">
[
    {
        amt: :amount,
        pct: :pct,
        val: :val, /* calculated as amt / (pct / 100) */
        type: :bid_type, /* OWNER_ACCEPT, OWNER_COUNTER */
        created_date: :date_yyyymmddhh24mmss
    },
    ...
]
</pre>
                </li>
                <li><code>accepted_bids</code> list of most recent ACCEPT bids the investor or owner, ordered by date descending, with private information removed, structure:
<pre name="code" class="brush: js">
[
    {
        amt: :amount,
        pct: :pct,
        val: :val, /* calculated as amt / (pct / 100) */
        type: :bid_type, /* INVESTOR_ACCEPT, OWNER_ACCEPT */
        created_date: :date_yyyymmddhh24mmss
    },
    ...
]
</pre>
                </li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/order_book" target="listing-order_book">
                <div class="formitem">
                    <label class="inputlabel" for="title">ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext listingid" type="text" name="id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="listing-order_book"></iframe>
        </div>

        <dt>GET /listing/bid_users/:id</dt>
        <dd>
            <p>Get the list of investors who have bid on this listing with the latest bid information by the investor.
            Call this method first to get access to the latest bidding by investors, then call <var>/listing/bids</var> to get the full bid history.</p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>id</code> listing ID</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>login_url</code> URL to use for site login action</li>
                <li><code>logout_url</code> URL to use for site logout action</li>
                <li><code>loggedin_profile</code> private user profile object, see User API for profile object details</li>
                <li><code>error_code</code> error status for this call, 0 on success</li>
                <li><code>error_msg</code> error message for this call, null on success</li>
                <li><code>investors</code> list of investors along with the latest bid concerning them, and whether it&rsquo;s been read.  Structure:
<pre name="code" class="brush: js">
[
    {
        listing_id: :id,
        investor_id: :uid,
        investor_nickname: :name,
        last_amt: :amount,
        last_pct: :pct,
        last_val: :val, /* calculated as amt / (pct / 100) */
        last_type: :bid_type, /* INVESTOR_POST, INVESTOR_COUNTER, INVESTOR_ACCEPT, INVESTOR_REJECT, INVESTOR_WITHDRAW,
                                 OWNER_ACCEPT, OWNER_REJECT, OWNER_COUNTER, OWNER_WITHDRAW */
        last_text: :bid_note,
        last_date: :date_yyyymmddhh24mmss,
        counter: :number_of_bids_from_this_investor,
        read: :true_or_false
    },
    ...
]
</pre>
                <li><code>investors_props</code> list properties, call <var>more_results_url</var> in AJAX for more, structure:
<pre name="code" class="brush: js">
{
    start_index: :index,
    max_results: :max,
    num_results: :n,
    more_results_url: :url
}
</pre>
                </li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/bid_investors" target="listing-bid_investors">
                <div class="formitem">
                    <label class="inputlabel" for="title">ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext listingid" type="text" name="id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="listing-bid_investors"></iframe>
        </div>

        <dt>GET /listing/bids/:id/:investor_id</dt>
        <dd>
            <p>Get the list of bids for the given listing <var>id</var> and investor <var>profile_id</var>, ordered by date ascending.</p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>id</code> listing ID</li>
                <li><code>investor_id</code> <var>OPTIONAL</var> investor user profile ID who has bid on this listing, defaults to logged in profile ID for non-owners</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>login_url</code> URL to use for site login action</li>
                <li><code>logout_url</code> URL to use for site logout action</li>
                <li><code>loggedin_profile</code> private user profile object, see User API for profile object details</li>
                <li><code>error_code</code> error status for this call, 0 on success</li>
                <li><code>error_msg</code> error message for this call, null on success</li>
                <li><code>investor</code> profile object for investor, with private fields hidden</li>
                <li><code>bids</code> bids for this investor and listing, ordered by date ascending.  Structure:
<pre name="code" class="brush: js">
[
    {
        amt: :amt,
        pct: :pct,
        val: :val, /* calculated as amt / (pct / 100) */
        type: :type, /* INVESTOR_POST, INVESTOR_COUNTER, INVESTOR_ACCEPT, INVESTOR_REJECT, INVESTOR_WITHDRAW,
                        OWNER_ACCEPT, OWNER_REJECT, OWNER_COUNTER, OWNER_WITHDRAW */
        text: :note,
        created_date: :yyyymmddhh24mmss
    },
    ...
]
</pre>
                </li>
                <li><code>bids_props</code> list properties, call <var>more_results_url</var> in AJAX for more, structure:
<pre name="code" class="brush: js">
{
    start_index: :index,
    max_results: :max,
    num_results: :n,
    more_results_url: :url
}
</pre>
                </li>
                <li><code>valid_actions</code> valid bid actions for current user. Structure:
<pre name="code" class="brush: js">
"valid_actions": "owner_accept, owner_reject, owner_counter"
</pre>
                </li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/listing/bids" target="listing-bids">
                <div class="formitem">
                    <label class="inputlabel" for="title">LISTING ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext listingid" type="text" name="id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <label class="inputlabel" for="title">INVESTOR ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext profileid" type="text" name="investor_id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="listing-bids"></iframe>
        </div>

        <dt>POST /listing/make_bid</dt>
        <dd>
            <p>Post a bid from the logged in user to the listing and with the bid properties as specified in the bid field.
                Note that bids always include a bid type which represents an action of the party as to the bid, as restricted
                by the bid history.  For instance, a user without previous bid history for a listing may place a bid as an investor.
                The listing owner may then approve or reject this bid with the exact same amount and percent, or may counter with
                a different counter or percent.  This causes a bid to only be approvable exactly as offered by the counterparty.
                Note that properties <var>investor_nickname</var>, <var>val</var> and <var>created_date</var> cannot be passed, as they are
                generated automatically by the server.</p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>bid</code> bid to create as per the logged in user with properties:</li>
                <li><code>.listing_id</code> the listing this bid is for</li>
                <li><code>.investor_id</code> <var>OPTIONAL</var> the profile_id of the investor who placed the bid, only passed for listing owner making bid</li>
                <li><code>.amt</code> amount of cash being offered by the investor, e.g. 20000</li>
                <li><code>.pct</code> percentage of the company/project being demanded by the investor, e.g. 5</li>
                <li><code>.type</code>
                    one of INVESTOR_POST, INVESTOR_COUNTER, INVESTOR_ACCEPT, INVESTOR_REJECT, INVESTOR_WITHDRAW, OWNER_ACCEPT, OWNER_REJECT, OWNER_COUNTER, OWNER_WITHDRAW</li>
                <li><code>.text</code> note attached to this bid, may describe additional conditions</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>login_url</code> URL to use for site login action</li>
                <li><code>logout_url</code> URL to use for site logout action</li>
                <li><code>loggedin_profile</code> private user profile object, see User API for profile object details</li>
                <li><code>error_code</code> error status for this call, 0 on success</li>
                <li><code>error_msg</code> error message for this call, null on success</li>
                <li><code>investor</code> profile object for investor, with private fields hidden</li>
                <li><code>bids</code> created bid returned as the only item in list.  Structure:
<pre name="code" class="brush: js">
[
    {
        amt: :amt,
        pct: :pct,
        val: :val, /* calculated as amt * 100 / pct */
        type: :type, /* INVESTOR_POST, INVESTOR_COUNTER, INVESTOR_ACCEPT, INVESTOR_REJECT, INVESTOR_WITHDRAW,
                        OWNER_ACCEPT, OWNER_REJECT, OWNER_COUNTER, OWNER_WITHDRAW */
        text: :note,
        created_date: :yyyymmddhh24mmss
    },
    ...
]
</pre>
                </li>
                <li><code>bids_props</code> list properties, call <var>more_results_url</var> in AJAX for more, structure:
<pre name="code" class="brush: js">
{
    start_index: :index,
    max_results: :max,
    num_results: :n,
    more_results_url: :url
}
</pre>
                </li>
                <li><code>valid_actions</code> valid bid actions for current user. Structure:
<pre name="code" class="brush: js">
"valid_actions": "owner_accept, owner_reject, owner_counter"
</pre>
                </li>
            </ul>
            <h4>Test</h4>
            <form method="POST" action="/listing/make_bid" target="listing-make_bid">
                <div class="formitem">
                    <label class="inputlabel" for="title">BID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext bidobj" type="text" name="bid"
                            value="{ listing_id: 0, amt: 20000, pct: 5, type: 'INVESTOR_POST', text: 'bid note' }"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="listing-make_bid"></iframe>
        </div>
    </div>

    <div class="boxtitle">USER API</div>
    <div class="boxpanel apipanel">
    <p>Get information on individual users, full information is only available to the currently logged in user and to admins.</p>

        <dt>GET /user/loggedin</dt>
        <dd>
            <p>
            Return logged in user profile data.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><var>none</var></li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>profile_id</code> unique alphanumeric ID key of the user</li>
                <li><code>username</code> human readable alphanumeric username, between 5 and 30 characters</li>
                <li><code>name</code> full real name of user up to 120 characters</li>
                <li><code>email</code> email address of user,</li>
                <li><code>location</code> full physical address of user</li>
                <li><code>phone</code> numeric phone number of user including prefixed country code</li>
                <li><code>investor</code> <var>true</var> iff the user is an accredited, professional, or sophisticated investor</li>
                <li><code>notify_enabled</code> <var>true</var> iff the user has email notificaitons enabled</li>
                <li><code>edited_listing</code> the listing id of the user&rsquo;s in-progress listing, null otherwise</li>
                <li><code>edited_status</code> edited listing status, <var>new</var> if being edited, <var>posted</var> if submitted but not yet approved</li>
                <li><code>joined_date</code> date user signed up in YYYYMMDD format</li>
                <li><code>last_login</code> date of last user login in YYYYMMDD format</li>
                <li><code>modified</code> date user last modified their profile in YYYYMMDD format</li>
                <li><code>num_listings</code> total user listing count, all statuses included </li>
                <li><code>num_bids</code> total user bid count, all statuses included</li>
                <li><code>num_accepted_bids</code> total number of bids placed by this user which have been accepted by the listing owner</li>
                <li><code>num_payed_bids</code> total number of bids placed by this user which have been paid for</li>
                <li><code>num_comments</code> total number of comments placed by this user</li>
                <li><code>num_notifications</code> current number of unread notifications for this user</li>
                <li><code>status</code> <var>active</var> iff user is enabled on the system</li>
                <li><code>admin</code> <var>true</var> iff this user is a startupbidder administrator</li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/user/loggedin" target="user-loggedin">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="user-loggedin"></iframe>
        </div>

        <dt>GET /user/get/:id</dt>
        <dd>
            <p>
            Return user data for a given user id.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>id</code> alphanumeric user id</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><code>login_url</code> URL to use for site login action</li>
                <li><code>logout_url</code> URL to use for site logout action</li>
                <li><code>loggedin_profile</code> private user profile object, see User API for profile object details</li>
                <li><code>error_code</code> error status for this call, 0 on success</li>
                <li><code>error_msg</code> error message for this call, null on success</li>
                <li><code>profile</code> user object as described in method <var>/user/loggedin</var></li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/user/get" target="user-get">
                <div class="formitem">
                    <label class="inputlabel" for="title">ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext profileid" type="text" name="id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="user-get"></iframe>
        </div>

        <dt>POST /user/deactivate</dt>
        <dd>
            <p>
            Deactivates a given user.  Note that you must be logged in as this user or an admin in order to call this method.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>id</code> alphanumeric user id</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><var>same as method /user/loggedin</var></li>
            </ul>
            <h4>Test</h4>
            <form method="POST" action="/user/deactivate" target="user-deactivate">
                <div class="formitem">
                    <label class="inputlabel" for="title">USER ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext profileid" type="text" name="id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="user-deactivate"></iframe>
        </div>

        <dt>GET /user/check-user-name</dt>
        <dd>
            <p>
            Checks if the given username is currently taken.  If it is available, returns <var>true</var>.  If the name is already taken, returns <var>false</var>.
            Used to check if a username exists before the user is allowed to update their username, as all usernames (nicknames) must be unique for the site.
            </p>
        </dd>
        <div class="apidetail">
            <h4>Parameters</h4>
            <ul>
                <li><code>name</code> alphanumeric string with potential username between 5 and 30 characters</li>
            </ul>
            <h4>Response</h4>
            <ul>
                <li><var>true</var> if the passed username can be used as a new name on the system, <var>false</var> if it is already taken or otherwise invalid</li>
            </ul>
            <h4>Test</h4>
            <form method="GET" action="/user/check-user-name" target="user-check-user-name">
                <div class="formitem">
                    <label class="inputlabel" for="title">USERNAME</label>
                    <span class="inputfield">
                        <input class="text inputwidetext profileusername" type="text" name="name" value="bobinator357"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="user-check-user-name"></iframe>
        </div>

    </div>

    <div class="boxtitle">ADMIN API</div>
    <div class="boxpanel apipanel">
    <p>Perform administrative tasks on startupbidder; you must have administrative rights as a logged in user in order for these calls to work</p>

        <dt>GET /listing/posted</dt>
        <dd>
            <p>
            Returns posted status listings, that is, those which have been submitted by a user and are awaiting approval by a startupbidder admin.
            Can only be called by a startupbidder admin.
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
                <li><code>listings_props</code> list properties, call <var>more_results_url</var> in AJAX, structure:
<pre name="code" class="brush: js">
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
            <form method="GET" action="/listing/posted" target="listing-posted">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listing-top"></iframe>
        </div>

        <dt>GET /listing/frozen</dt>
        <dd>
            <p>
            Returns frozen status listings, that is, those which have been frozen by a startupbidder admin.  Can only be called by a startupbidder admin.
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
                <li><code>listings_props</code> list properties, call <var>more_results_url</var> in AJAX for more, structure:
<pre name="code" class="brush: js">
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
            <form method="GET" action="/listing/frozen" target="listing-frozen">
                <input type="submit" class="inputbutton" value="SUBMIT"></input>
            </form>
            <iframe name="listing-frozen"></iframe>
        </div>

        <dt>POST /listing/freeze/:id</dt>
        <dd>
            <p>
            Freezes the listing with the given id.  This suspends the listing pending further review, which may be done if a listing is found to be inaccurate
            or in violation of any law or regulation.  This method can only be called by admins.
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
            <form method="POST" action="/listing/freeze" target="listing-freeze">
                <div class="formitem">
                    <label class="inputlabel" for="title">ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext listingid" type="text" name="id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="listing-freeze"></iframe>
        </div>

        <dt>POST /listing/activate/:id</dt>
        <dd>
            <p>
            Activates the listing with the given id.  This makes the listing live and active on the site.  If asking for funds, bidding is enabled at this point.
            Commenting also becomes enabled for the listing.  This method can only be called if the listing is in posted or frozen state.
            This method can only be called by admins.
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
            <form method="POST" action="/listing/activate" target="listing-activate">
                <div class="formitem">
                    <label class="inputlabel" for="title">ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext listingid" type="text" name="id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="listing-activate"></iframe>
        </div>

        <dt>POST /listing/send_back/:id</dt>
        <dd>
            <p>
            Sends the listing with the given id back to the user for further modification.  This suspends the listing pending further review,
            which may be done if a listing is found to be inaccurate or in violation of any law or regulation.
            This method can only be called by admins.
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
            <form method="POST" action="/listing/send_back" target="listing-send_back">
                <div class="formitem">
                    <label class="inputlabel" for="title">ID</label>
                    <span class="inputfield">
                        <input class="text inputwidetext listingid" type="text" name="id" value="0"></input>
                    </span>
                </div>
                <div class="formitem clear">
                    <span class="inputlabel"></span>
                    <span class="inputfield">
                        <input type="submit" class="inputbutton" value="SUBMIT"></input>
                    </span>
                </div>
            </form>
            <iframe name="listing-send_back"></iframe>
        </div>

    </div>

<div id="loadmsg"></div>

</div> <!-- end container -->
</div> <!-- end main -->
</div> <!-- end wrap -->
'
include(footer.m4)
`
  <!-- JavaScript at the bottom for fast page loading -->
  <script src="http://alexgorbatchev.com/pub/sh/current/scripts/shCore.js" type="text/javascript"></script>
  <script src="http://alexgorbatchev.com/pub/sh/current/scripts/shBrushJScript.js" type="text/javascript"></script>
  <script src="js/libs/prevel.min.js"></script>
  <script src="js/modules/base.js"></script>
  <script src="js/modules/apipage.js"></script>
  <script src="js/modules/tracker.js"></script>
'
include(promptie.m4)
`
</body>
</html>
'
