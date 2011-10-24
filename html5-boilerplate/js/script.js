/* Author: johnarleyburns@gmail.com

 */

google.load("jquery", "1.6.4");
google.load("visualization", "1", {
	packages : [ "corechart" ]
});
google.setOnLoadCallback(loadAll);

function loadAll() {
    Plugins();
    StartupBidder();
}
