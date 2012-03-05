package com.startupbidder.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.joda.time.DateMidnight;
import org.joda.time.Days;

import com.googlecode.objectify.Key;
import com.startupbidder.datamodel.Bid;
import com.startupbidder.datamodel.Category;
import com.startupbidder.datamodel.Comment;
import com.startupbidder.datamodel.Listing;
import com.startupbidder.datamodel.SBUser;
import com.startupbidder.datamodel.Vote;

/**
 * Generates mock data.
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 */
public class MockDataBuilder {
	private SBUser greg;
	private Listing gregsListing;
	private SBUser john;
	private Listing johnsListing;
	
	private long id = 2000L;
	
	private long id() {
		return id++;
	}

	public List<Category> createCategories() {
		List<Category> categories = new ArrayList<Category>();
		categories.add(new Category(1, "Biotech"));
		categories.add(new Category(2, "Chemical"));
		categories.add(new Category(3, "Retail"));
		categories.add(new Category(4, "Electronics"));
		categories.add(new Category(5, "Energy"));
		categories.add(new Category(6, "Environmental"));
		categories.add(new Category(7, "Financial"));
		categories.add(new Category(8, "Hardware"));
		categories.add(new Category(9, "Healthcare"));
		categories.add(new Category(10, "Industrial"));
		categories.add(new Category(11, "Internet"));
		categories.add(new Category(12, "Manufacturing"));
		categories.add(new Category(13, "Media"));
		categories.add(new Category(14, "Medical"));
		categories.add(new Category(15, "Pharma"));
		categories.add(new Category(16, "Software"));
		categories.add(new Category(17, "Telecom"));
		categories.add(new Category(18, "Other"));

		return categories;
	}
	
	/**
	 * Generates random comments for listings
	 */
	public List<Comment> generateComments(Collection<SBUser> usersList, Collection<Listing> listings) {
		List<Comment> comments = new ArrayList<Comment>();
		
		List<SBUser> users = new ArrayList<SBUser>(usersList);
		for (Listing bp : listings) {
			int commentNum = new Random().nextInt(30);
			while (--commentNum > 0) {
				Comment comment = new Comment();
				comment.id = id();
				comment.mockData = true;
				comment.listing = new Key<Listing>(Listing.class, bp.id);
				comment.user = new Key<SBUser>(SBUser.class, users.get(new Random().nextInt(users.size())).id);
				comment.commentedOn = new Date(System.currentTimeMillis() - commentNum * 18 * 60 * 60 * 1000);
				comment.comment = "Comment " + commentNum;
				
				comments.add(comment);
			}
		}
		return comments;
	}	

	/**
	 * Generates random bids for listings
	 */
	public List<Bid> generateBids(Collection<SBUser> usersList, Collection<Listing> listings) {
		List<Bid> bids = new ArrayList<Bid>();
		
		List<SBUser> users = new ArrayList<SBUser>();
		for (SBUser user : usersList) {
			if (user.investor) {
				users.add(user);
			}
		}
		
		for (Listing listing : listings) {
			int bidNum = new Random().nextInt(15);
			long bidTimeSpan = (System.currentTimeMillis() - listing.listedOn.getTime()) / (bidNum + 1);
			Random fundTypeRandom = new Random();
			while (--bidNum > 0) {
				Bid bid = new Bid();
				bid.id = id();
				bid.mockData = true;
				
				Key<SBUser> userId = new Key<SBUser>(SBUser.class, users.get(new Random().nextInt(users.size())).id);
				if (!listing.owner.equals(userId)) {
					bid.bidder = userId;
					bid.listing = new Key<Listing>(Listing.class, listing.id);
					switch (fundTypeRandom.nextInt(3)) {
						case 0: bid.fundType = Bid.FundType.COMMON;
						break;
						case 1: bid.fundType = Bid.FundType.NOTE;
						break;
						case 2: bid.fundType = Bid.FundType.PREFERRED;
						break;
					}
					bid.percentOfCompany = new Random().nextInt(50) + 10;
					bid.placed = new Date(listing.listedOn.getTime() + bidNum * bidTimeSpan);
					bid.value = new Random().nextInt(50) * 1000 + listing.suggestedValuation;
					bid.listingOwner = listing.owner;
					bid.action = Bid.Action.ACTIVATE;
					bid.actor = Bid.Actor.BIDDER;
					
					bids.add(bid);
				}
			}
		}
		
		bids.addAll(generateBidsForAdminListing(usersList));
		bids.addAll(generateBidsByAdmins(listings));
		
		return bids;
	}
	
	/**
	 * Generates random bids for admin listings (john and greg)
	 */
	private List<Bid> generateBidsForAdminListing(Collection<SBUser> usersList) {
		List<Bid> bids = new ArrayList<Bid>();
		Bid.Action statuses[] = Bid.Action.values();

		Collection<Listing> listings = new ArrayList<Listing>();
		listings.add(gregsListing);
		listings.add(johnsListing);
				
		List<SBUser> users = new ArrayList<SBUser>();
		for (SBUser user : usersList) {
			if (user.investor) {
				users.add(user);
			}
		}
		users.remove(greg);
		users.remove(john);
		
		for (Listing listing : listings) {
			int bidNum = 4;
			long bidTimeSpan = (System.currentTimeMillis() - listing.listedOn.getTime()) / (bidNum + 1);
			Random fundTypeRandom = new Random();
			while (--bidNum > 0) {
				Bid bid = new Bid();
				bid.id = id();
				Key<SBUser> userId = new Key<SBUser>(SBUser.class, users.get(new Random().nextInt(users.size())).id);

				bid.mockData = true;
				
				if (!listing.owner.equals(userId)) {
					bid.bidder = userId;
					bid.listing = new Key<Listing>(Listing.class, listing.id);
					switch (fundTypeRandom.nextInt(3)) {
						case 0: bid.fundType = Bid.FundType.COMMON;
						break;
						case 1: bid.fundType = Bid.FundType.NOTE;
						break;
						case 2: bid.fundType = Bid.FundType.PREFERRED;
						break;
					}
					bid.percentOfCompany = new Random().nextInt(50) + 10;
					bid.placed = new Date(listing.listedOn.getTime() + bidNum * bidTimeSpan);
					bid.value = new Random().nextInt(50) * 1000 + listing.suggestedValuation;
					bid.listingOwner = listing.owner;
					// calculate valuation
					bid.valuation = bid.value * 100 / bid.percentOfCompany;
					bid.action = statuses[bidNum];
					
					bids.add(bid);
				}
			}
		}
		return bids;
	}
	
	/**
	 * Generates random bids for admin listings (john and greg)
	 */
	private List<Bid> generateBidsByAdmins(Collection<Listing> listingList) {
		List<Listing> listings = new ArrayList<Listing>(listingList);
		List<Bid> bids = new ArrayList<Bid>();
		Bid.Action statuses[] = Bid.Action.values();

		listings.remove(gregsListing);
		listings.remove(johnsListing);
				
		List<SBUser> users = new ArrayList<SBUser>();
		users.add(greg);
		users.add(john);
		
		Random fundTypeRandom = new Random();
		
		for (SBUser user : users) {
			int bidNum = 4;
			while (--bidNum > 0) {
				Bid bid = new Bid();
				bid.id = id();
				Key<SBUser> userId = new Key<SBUser>(SBUser.class, user.id);
				Listing listing = listings.get(new Random().nextInt(listings.size()));

				bid.mockData = true;
				
				long bidTimeSpan = (System.currentTimeMillis() - listing.listedOn.getTime()) / (bidNum + 1);
				if (!listing.owner.equals(userId)) {
					bid.bidder = userId;
					bid.listing = new Key<Listing>(Listing.class, listing.id);
					switch (fundTypeRandom.nextInt(3)) {
						case 0: bid.fundType = Bid.FundType.COMMON;
						break;
						case 1: bid.fundType = Bid.FundType.NOTE;
						break;
						case 2: bid.fundType = Bid.FundType.PREFERRED;
						break;
					}
					bid.percentOfCompany = new Random().nextInt(50) + 10;
					bid.placed = new Date(listing.listedOn.getTime() + bidNum * bidTimeSpan);
					bid.value = new Random().nextInt(50) * 1000 + listing.suggestedValuation;
					bid.listingOwner = listing.owner;
					// calculate valuation
					bid.valuation = bid.value * 100 / bid.percentOfCompany;
					bid.action = statuses[bidNum];
					
					bids.add(bid);
				}
			}
		}
		return bids;
	}
	
	public List<Vote> createMockVotes(Collection<SBUser> usersList, Collection<Listing> listings) {
		List<Vote> votes = new ArrayList<Vote>();
		
		List<SBUser> users = new ArrayList<SBUser>(usersList);
		
		for (Listing listing : listings) {
			int numOfVotes = new Random().nextInt(users.size());
			long commentTimeSpan = (System.currentTimeMillis() - listing.listedOn.getTime()) / (numOfVotes + 1);
			while (numOfVotes > 0) {
				Vote vote = new Vote();
				vote.id = id();
				vote.mockData = true;
				vote.listing = new Key<Listing>(Listing.class, listing.id);
				
				Key<SBUser> userId = new Key<SBUser>(SBUser.class, users.get(numOfVotes).id);
				if (!listing.owner.equals(userId)) {
					vote.voter = userId;
					vote.user = null;
					vote.value = 1;
					vote.commentedOn = new Date(listing.listedOn.getTime() + numOfVotes * commentTimeSpan);
					votes.add(vote);
				}
				numOfVotes--;
			}
		}

		for (SBUser user : users) {
			int numOfVotes = new Random().nextInt(users.size());
			long commentTimeSpan = (System.currentTimeMillis() - user.joined.getTime()) / (numOfVotes + 1);
			while (numOfVotes > 0) {
				Vote vote = new Vote();
				vote.id = id();
				vote.mockData = true;
				vote.user = new Key<SBUser>(SBUser.class, user.id);
				
				Key<SBUser> userId = new Key<SBUser>(SBUser.class, users.get(numOfVotes).id);
				if (!user.id.equals(userId)) {
					vote.voter = userId;
					vote.listing = null;
					vote.value = 1;
					vote.commentedOn = new Date(user.joined.getTime() + numOfVotes * commentTimeSpan);
					votes.add(vote);
				}
				numOfVotes--;
			}
		}
		
		return votes;
	}

	/**
	 * Generates mock users
	 */
	public List<SBUser> createMockUsers() {
		List<SBUser> users = new ArrayList<SBUser>();
		
		SBUser user = new SBUser();
		user.id = id();
		user.mockData = true;
		user.nickname = "Dead";
		user.name = "Ahmed";
		user.email = "deadahmed@startupbidder.com";
		user.openId = user.email;
		user.joined = new Date(System.currentTimeMillis() - 22 * 24 * 60 * 60 * 1000);
		user.status = SBUser.Status.ACTIVE;
		user.lastLoggedIn = new Date();
		users.add(user);

		user = new SBUser();
		user.id = id();
		user.mockData = true;
		user.nickname = "fowler";
		user.name = "Jackob";
		user.email = "jpfowler@startupbidder.com";
		user.openId = user.email;
		user.joined = new Date(System.currentTimeMillis() - 23 * 24 * 60 * 60 * 1000);
		user.status = SBUser.Status.ACTIVE;
		user.lastLoggedIn = new Date();
		users.add(user);

		user = new SBUser();
		user.id = id();
		user.mockData = true;
		user.nickname = "Insider";
		user.name = "The";
		user.email = "insider@startupbidder.com";
		user.openId = user.email;
		user.joined = new Date(System.currentTimeMillis() - 31 * 24 * 60 * 60 * 1000);
		user.status = SBUser.Status.ACTIVE;
		users.add(user);

		user = new SBUser();
		user.id = id();
		user.mockData = true;
		user.nickname = "The Dragon";
		user.name = "Mark";
		user.email = "dragon@startupbidder.com";
		user.openId = user.email;
		user.joined = new Date(System.currentTimeMillis() - 26 * 24 * 60 * 60 * 1000);
		user.status = SBUser.Status.ACTIVE;
		user.lastLoggedIn = new Date();
		user.investor = true;
		users.add(user);

		user = new SBUser();
		user.id = id();
		user.mockData = true;
		user.nickname = "MadMax";
		user.name = "Mad";
		user.email = "madmax@startupbidder.com";
		user.openId = user.email;
		user.joined = new Date(System.currentTimeMillis() - 35 * 24 * 60 * 60 * 1000);
		user.status = SBUser.Status.ACTIVE;
		user.investor = true;
		user.lastLoggedIn = new Date();
		users.add(user);

		user = new SBUser();
		user.id = id();
		user.mockData = true;
		user.nickname = "The One";
		user.name = "Bruce";
		user.email = "bruce@startupbidder.com";
		user.openId = user.email;
		user.joined = new Date(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000);
		user.status = SBUser.Status.DEACTIVATED;
		user.investor = true;
		user.lastLoggedIn = new Date();
		users.add(user);
		
		user = new SBUser();
		user.id = id();
		user.mockData = true;
		user.admin = true;
		user.nickname = "John";
		user.name = "John A. Burns";
		user.email = "johnarleyburns@gmail.com";
		user.openId = user.email;
		user.joined = new Date(0L);
		user.status = SBUser.Status.ACTIVE;
		user.investor = true;
		user.lastLoggedIn = new Date();
		users.add(user);
		john = user;
		
		user = new SBUser();
		user.id = id();
		user.mockData = true;
		user.admin = true;
		user.nickname = "Greg";
		user.name = "Grzegorz Nittner";
		user.email = "grzegorz.nittner@gmail.com";
		user.openId = user.email;
		user.joined = new Date(0L);
		user.status = SBUser.Status.ACTIVE;
		user.investor = true;
		user.lastLoggedIn = new Date();
		users.add(user);
		greg = user;
		
		return users;
	}
	
	/**
	 * Generates mock listings
	 */
	public List<Listing> createMockListings(List<SBUser> users) {
		List<Listing> listings = new ArrayList<Listing>();
		
		List<Key<SBUser>> userIds = new ArrayList<Key<SBUser>>();
		for (SBUser user : users) {
			userIds.add(new Key<SBUser>(SBUser.class, user.id));
		}
		userIds.remove(john);
		userIds.remove(greg);
		int bpNum = 0;
	
		Listing bp = new Listing();
		bp.id = id();
		bp.mockData = true;
		bp.name = "MisLead";
		bp.owner = userIds.get(bpNum % userIds.size());
		bp.suggestedValuation = 20000;
		bp.suggestedPercentage = 25;
		bp.suggestedAmount = bp.suggestedValuation*bp.suggestedPercentage/100;
		bp.listedOn = new Date(System.currentTimeMillis() - 25 * 24 * 60 * 60 * 1000);
		bp.state = Listing.State.CLOSED;
		DateMidnight midnight = new DateMidnight(bp.listedOn.getTime());
		bp.closingOn = midnight.plus(Days.days(30)).toDate();
		bp.summary = "Executive summary for <b>MisLead</b>";
		bp.mantra = "Misleading is our mantra";
		bp.address = "Lohstr. 53, 49074 Osnabrück, Deutschland";
		listings.add(bp);
	
		bp = new Listing();
		bp.id = id();
		bp.mockData = true;
		bp.name = "Computer Training Camp";
		bp.owner = new Key<SBUser>(SBUser.class, john.id);
		bp.suggestedValuation = 15000;
		bp.suggestedPercentage = 45;
		bp.suggestedAmount = bp.suggestedValuation*bp.suggestedPercentage/100;
		bp.listedOn = new Date(System.currentTimeMillis() - 15 * 24 * 60 * 60 * 1000);
		bp.state = Listing.State.POSTED;
		midnight = new DateMidnight(bp.listedOn.getTime());
		bp.closingOn = midnight.plus(Days.days(30)).toDate();
		bp.summary = "Starting a computer training camp for children is a terrific new business " +
				"venture to set in motion. In spite of the fact that many children now receive " +
				"computer training in school, attending computer camps ensures parents and" +
				" children a better and more complete understanding of the course material. " +
				"The computer camps can be operated on a year-round basis or in the summer only. " +
				"Typically, these camps are one or two days in length and available for various " +
				"training needs, from beginner to advanced. Once again, this is the kind of children's" +
				" business that can be operated as an independent business venture or operated in" +
				" conjunction with a community program or community center.";
		bp.mantra = "We'll train even lammers";
		bp.address = "Fellenoord 310, 5611 ZD Eindhoven, Nederland";
		listings.add(bp);
		johnsListing = bp;

		bp = new Listing();
		bp.id = id();
		bp.mockData = true;
		bp.name = "Computer Upgrading Service";
		bp.owner = new Key<SBUser>(SBUser.class, greg.id);
		bp.suggestedValuation = 35000;
		bp.suggestedPercentage = 33;
		bp.suggestedAmount = bp.suggestedValuation*bp.suggestedPercentage/100;
		bp.listedOn = new Date(System.currentTimeMillis() - 15 * 24 * 60 * 60 * 1000);
		bp.state = Listing.State.ACTIVE;
		midnight = new DateMidnight(bp.listedOn.getTime());
		bp.closingOn = midnight.plus(Days.days(30)).toDate();
		bp.summary = "Starting a business that specializes in upgrading existing computer systems" +
				" with new internal and external equipment is a terrific homebased business to " +
				"initiate that has great potential to earn an outstanding income for the operator" +
				" of the business. A computer upgrading service is a very easy business to get" +
				" rolling, providing you have the skills and equipment necessary to complete upgrading" +
				" tasks, such as installing more memory into the hard drive, replacing a hard drive," +
				" or adding a new disk drive to the computer system. Ideally, to secure the most " +
				"profitable segment of the potential market, the service should specialize in " +
				"upgrading business computers as there are many reasons why a business would upgrade" +
				" a computer system as opposed to replacing the computer system. Additionally, managing" +
				" the business from a homebased location while providing clients with a mobile service" +
				" is the best way to keep operating overheads minimized and potentially increases the" +
				" size of the target market by expanding the service area, due to the fact the business" +
				" operates on a mobile format.";
		bp.mantra = "If it could be upgraded we'll do that";
		bp.address = "10 Rue de Passy, 75016 Paris, France";
		listings.add(bp);
		gregsListing = bp;

		bp = new Listing();
		bp.id = id();
		bp.mockData = true;
		bp.name = "Semantic Search";
		bp.owner = userIds.get(bpNum++ % userIds.size());
		bp.suggestedValuation = 40000;
		bp.suggestedPercentage = 45;
		bp.suggestedAmount = bp.suggestedValuation*bp.suggestedPercentage/100;
		bp.listedOn = new Date(System.currentTimeMillis() - 19 * 24 * 60 * 60 * 1000);
		bp.state = Listing.State.ACTIVE;
		midnight = new DateMidnight(bp.listedOn.getTime());
		bp.closingOn = midnight.plus(Days.days(30)).toDate();
		bp.summary = "The fact of the matter is Google, and to a much lesser extent Bing, " + 
				"own the search market. Ask Barry Diller, if you don't believe us." +
				"Yet, startups still spring up hoping to disrupt the incumbents. " +
				"Cuil flopped. Wolfram Alpha is irrelevant. Powerset, which was a semantic" + 
				" search engine was bailed out by Microsoft, which acquired it.";
		bp.mantra = "Search is our life";
		bp.address = "Calle del Pintor Cabrera, 29, 03003 Alicante, España";
		listings.add(bp);

		bp = new Listing();
		bp.id = id();
		bp.mockData = true;
		bp.name = "Social recommendations";
		bp.owner = userIds.get(bpNum++ % userIds.size());
		bp.suggestedValuation = 15000;
		bp.suggestedPercentage = 10;
		bp.suggestedAmount = bp.suggestedValuation*bp.suggestedPercentage/100;
		bp.listedOn = new Date(System.currentTimeMillis() - 20 * 24 * 60 * 60 * 1000);
		bp.state = Listing.State.ACTIVE;
		midnight = new DateMidnight(bp.listedOn.getTime());
		bp.closingOn = midnight.plus(Days.days(30)).toDate();
		bp.summary = "It's a very tempting idea. Collect data from people about their tastes" +
				" and preferences. Then use that data to create recommendations for others. " +
				"Or, use that data to create recommendations for the people that filled in " +
				"the information. It doesn't work. The latest to try is Hunch and Get Glue." +
				"Hunch is pivoting towards non-consumer-facing white label business. " +
				"Get Glue has had some success of late, but it's hardly a breakout business.";
		bp.mantra = "Don't try that at home";
		bp.address = "Via Amerigo Vespucci, 10, 80142 Napoli, Italia";
		listings.add(bp);

		bp = new Listing();
		bp.id = id();
		bp.mockData = true;
		bp.name = "Local news sites";
		bp.owner = userIds.get(bpNum++ % userIds.size());
		bp.suggestedValuation = 49000;
		bp.suggestedPercentage = 20;
		bp.suggestedAmount = bp.suggestedValuation*bp.suggestedPercentage/100;
		bp.listedOn = new Date(System.currentTimeMillis() - 13 * 24 * 60 * 60 * 1000);
		bp.state = Listing.State.ACTIVE;
		midnight = new DateMidnight(bp.listedOn.getTime());
		bp.closingOn = midnight.plus(Days.days(30)).toDate();
		bp.summary = "Maybe Tim Armstrong, AOL, and Patch will prove it wrong, but to this point" +
				" nobody has been able to crack the local news market and make a sustainable business." +
				"In theory creating a network of local news sites that people care about is a good" +
				" idea. You build a community, there's a baked in advertising group with local " +
				"businesses, and classifieds. But, it appears to be too niche to scale into a big" +
				" business.";
		bp.mantra = "Better than free newspaper";
		bp.address = "45 L Street Southwest, Washington D.C., DC 20024";
		listings.add(bp);

		bp = new Listing();
		bp.id = id();
		bp.mockData = true;
		bp.name = "Micropayments";
		bp.owner = userIds.get(bpNum++ % userIds.size());
		bp.suggestedValuation = 5000;
		bp.suggestedPercentage = 49;
		bp.suggestedAmount = bp.suggestedValuation*bp.suggestedPercentage/100;
		bp.listedOn = new Date(System.currentTimeMillis() - 23 * 24 * 60 * 60 * 1000);
		bp.state = Listing.State.ACTIVE;
		midnight = new DateMidnight(bp.listedOn.getTime());
		bp.closingOn = midnight.plus(Days.days(30)).toDate();
		bp.summary = "Micropayments are one idea that's tossed around to solve the problem" +
				" of paying for content on the Web. If you want to read a New York Times " +
				"story it would only cost a nickel! Or on Tumblr, if you want to tip a blogger" +
				" or pay for a small design you could with ease. So far, these micropayment" +
				" plans have not worked.";
		bp.mantra = "We can trasfer even a penny";
		bp.address = "671 John F Kennedy Boulevard West, Bayonne, NJ 07002";
		listings.add(bp);

		bp = new Listing();
		bp.id = id();
		bp.mockData = true;
		bp.name = "Kill email";
		bp.owner = userIds.get(bpNum++ % userIds.size());
		bp.suggestedValuation = 40000;
		bp.suggestedPercentage = 50;
		bp.suggestedAmount = bp.suggestedValuation*bp.suggestedPercentage/100;
		bp.listedOn = new Date(System.currentTimeMillis() - 6 * 24 * 60 * 60 * 1000);
		bp.state = Listing.State.ACTIVE;
		midnight = new DateMidnight(bp.listedOn.getTime());
		bp.closingOn = midnight.plus(Days.days(30)).toDate();
		bp.summary = "If any startup says it's going to eliminate email, it's destined for failure. " +
				"You can iterate on the inbox, and try to improve it, but even that's " +
				"not much of a business. The latest high profile flop in this arena is " +
				"Google Wave. It was supposed to change email forever. It was going to " +
				"displace email. Didn't happen.";
		bp.mantra = "Is it possible?";
		bp.address = "273 High Street, Perth Amboy, NJ 08861";
		listings.add(bp);

		bp = new Listing();
		bp.id = id();
		bp.mockData = true;
		bp.name = "Better company car";
		bp.owner = userIds.get(bpNum++ % userIds.size());
		bp.suggestedValuation = 100000;
		bp.suggestedPercentage = 15;
		bp.suggestedAmount = bp.suggestedValuation*bp.suggestedPercentage/100;
		bp.listedOn = new Date(System.currentTimeMillis() - 8 * 24 * 60 * 60 * 1000);
		bp.state = Listing.State.WITHDRAWN;
		midnight = new DateMidnight(bp.listedOn.getTime());
		bp.closingOn = midnight.plus(Days.days(30)).toDate();
		bp.summary = "Considering how frustrated people are with car companies, you'd think " +
				"launching a new one would be perfect for a startup. So far, that's not the case. " +
				"You can point to Tesla as a success, and considering it IPO'd it's hard to argue " +
				"against it. But, Tesla has sold fewer than 2,000 cars since it was founded in 2003. " +
				"It's far from certain it will succeed. Even when its next car comes out, Nissan " +
				"could be making a luxury electric car that competes with Tesla.";
		bp.mantra = "We always do better";
		bp.address = "1501 East 22nd Street, Los Angeles, CA 90011";
		listings.add(bp);
		
		return listings;
	}

}
