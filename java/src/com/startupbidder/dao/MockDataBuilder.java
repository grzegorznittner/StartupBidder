package com.startupbidder.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.joda.time.DateMidnight;
import org.joda.time.Days;

import com.startupbidder.dto.BidDTO;
import com.startupbidder.dto.CommentDTO;
import com.startupbidder.dto.ListingDTO;
import com.startupbidder.dto.UserDTO;
import com.startupbidder.dto.VoteDTO;

/**
 * 
 * @author "Grzegorz Nittner" <grzegorz.nittner@gmail.com>
 *
 */
public class MockDataBuilder {
	/**
	 * Generates random comments for business plans
	 */
	public Map<String, CommentDTO> generateComments(Collection<UserDTO> usersList, Collection<ListingDTO> listings) {
		Map<String, CommentDTO> comments = new HashMap<String, CommentDTO>();
		
		List<UserDTO> users = new ArrayList<UserDTO>(usersList);
		for (ListingDTO bp : listings) {
			int commentNum = new Random().nextInt(30);
			while (--commentNum > 0) {
				CommentDTO comment = new CommentDTO();
				comment.createKey(commentNum + "_" + bp.hashCode());
				comment.setListing(bp.getIdAsString());
				comment.setUser(users.get(new Random().nextInt(users.size())).getIdAsString());
				comment.setCommentedOn(new Date(System.currentTimeMillis() - commentNum * 45 * 60 * 1000));
				comment.setComment("Comment " + commentNum);
				
				comments.put(comment.getIdAsString(), comment);
			}
		}
		return comments;
	}	

	/**
	 * Generates random bids for listings
	 */
	public Map<String, BidDTO> generateBids(Collection<UserDTO> usersList, Collection<ListingDTO> listings) {
		Map<String, BidDTO> bids = new HashMap<String, BidDTO>();
		
		List<UserDTO> users = new ArrayList<UserDTO>();
		for (UserDTO user : usersList) {
			if (user.isInvestor()) {
				users.add(user);
			}
		}
		
		for (ListingDTO listing : listings) {
			int bidNum = new Random().nextInt(15);
			long bidTimeSpan = (System.currentTimeMillis() - listing.getListedOn().getTime()) / (bidNum + 1);
			while (--bidNum > 0) {
				BidDTO bid = new BidDTO();
				bid.createKey(bidNum + "_" + listing.hashCode());
				bid.setUser(users.get(new Random().nextInt(users.size())).getIdAsString());
				bid.setListing(listing.getIdAsString());
				bid.setFundType(new Random().nextInt(2) > 0 ? BidDTO.FundType.SYNDICATE : BidDTO.FundType.SOLE_INVESTOR);
				bid.setPercentOfCompany(new Random().nextInt(50) + 10);
				bid.setPlaced(new Date(listing.getListedOn().getTime() + bidNum * bidTimeSpan));
				bid.setValue(new Random().nextInt(50) * 1000 + listing.getSuggestedValuation());
				// calculate valuation
				bid.setValuation(bid.getValue() * 100 / bid.getPercentOfCompany());
				
				bids.put(bid.getIdAsString(), bid);
			}
		}
		return bids;
	}
	
	/**
	 * Generates mock users
	 */
	public Map<String, UserDTO> createMockUsers() {
		Map<String, UserDTO> users = new HashMap<String, UserDTO>();
		String key = "";
		
		UserDTO user = new UserDTO();
		key = "deadahmed";
		user.createKey(key);
		user.setNickname("Dead");
		user.setName("Ahmed");
		user.setEmail("deadahmed@startupbidder.com");
		user.setOpenId(user.getEmail());
		user.setJoined(new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000));
		user.setStatus(UserDTO.Status.ACTIVE);
		user.setFacebook("fb_" + key);
		user.setJoined(new Date());
		user.setLastLoggedIn(new Date());
		user.setModified(new Date());
		user.setTitle("Dr");
		user.setTwitter("twit_" + key);
		user.setLinkedin("ln_" + key);
		users.put(user.getIdAsString(), user);

		user = new UserDTO();
		key = "jpfowler";
		user.createKey(key);
		user.setNickname("fowler");
		user.setName("Jackob");
		user.setEmail("jpfowler@startupbidder.com");
		user.setOpenId(user.getEmail());
		user.setJoined(new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000));
		user.setStatus(UserDTO.Status.ACTIVE);
		user.setFacebook("fb_" + key);
		user.setJoined(new Date());
		user.setLastLoggedIn(new Date());
		user.setModified(new Date());
		user.setOrganization("org_" + key);
		user.setTitle("Dr");
		user.setTwitter("twit_" + key);
		user.setLinkedin("ln_" + key);
		users.put(user.getIdAsString(), user);

		user = new UserDTO();
		key = "businessinsider";
		user.createKey(key);
		user.setNickname("Insider");
		user.setName("The");
		user.setEmail("insider@startupbidder.com");
		user.setOpenId(user.getEmail());
		user.setJoined(new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000));
		user.setStatus(UserDTO.Status.ACTIVE);
		user.setFacebook("fb_" + key);
		user.setJoined(new Date());
		user.setLastLoggedIn(new Date());
		user.setModified(new Date());
		user.setTwitter("twit_" + key);
		users.put(user.getIdAsString(), user);

		user = new UserDTO();
		key = "dragonsden";
		user.createKey(key);
		user.setNickname("The Dragon");
		user.setName("Mark");
		user.setEmail("dragon@startupbidder.com");
		user.setOpenId(user.getEmail());
		user.setJoined(new Date(System.currentTimeMillis() - 6 * 24 * 60 * 60 * 1000));
		user.setStatus(UserDTO.Status.ACTIVE);
		user.setFacebook("fb_" + key);
		user.setJoined(new Date());
		user.setLastLoggedIn(new Date());
		user.setModified(new Date());
		user.setOrganization("org_" + key);
		user.setTitle("Dr");
		user.setLinkedin("ln_" + key);
		user.setInvestor(true);
		users.put(user.getIdAsString(), user);

		user = new UserDTO();
		key = "crazyinvestor";
		user.createKey(key);
		user.setNickname("MadMax");
		user.setName("Mad");
		user.setEmail("madmax@startupbidder.com");
		user.setOpenId(user.getEmail());
		user.setJoined(new Date(System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000));
		user.setStatus(UserDTO.Status.ACTIVE);
		user.setInvestor(true);
		user.setFacebook("fb_" + key);
		user.setJoined(new Date());
		user.setLastLoggedIn(new Date());
		user.setModified(new Date());
		user.setOrganization("org_" + key);
		user.setTitle("Dr");
		user.setTwitter("twit_" + key);
		user.setLinkedin("ln_" + key);
		users.put(user.getIdAsString(), user);

		user = new UserDTO();
		key = "chinese";
		user.createKey(key);
		user.setNickname("The One");
		user.setName("Bruce");
		user.setEmail("madmax@startupbidder.com");
		user.setOpenId(user.getEmail());
		user.setJoined(new Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000));
		user.setStatus(UserDTO.Status.DEACTIVATED);
		user.setInvestor(true);
		user.setJoined(new Date());
		user.setLastLoggedIn(new Date());
		user.setModified(new Date());
		user.setOrganization("org_" + key);
		user.setTitle("Dr");
		user.setTwitter("twit_" + key);
		user.setLinkedin("ln_" + key);
		users.put(user.getIdAsString(), user);
		
		return users;
	}
	
	public Map<String, VoteDTO> createMockVotes(Collection<UserDTO> usersList, Collection<ListingDTO> listings) {
		Map<String, VoteDTO> votes = new HashMap<String, VoteDTO>();
		
		List<UserDTO> users = new ArrayList<UserDTO>(usersList);
		
		for (ListingDTO listing : listings) {
			int numOfVotes = new Random().nextInt(users.size());
			while (numOfVotes > 0) {
				VoteDTO vote = new VoteDTO();
				vote.setListing(listing.getIdAsString());
				vote.setVoter(users.get(numOfVotes).getIdAsString());
				vote.setUser(null);
				vote.setValue(1);
				vote.createKey(String.valueOf(vote.hashCode()));
				votes.put(vote.getIdAsString(), vote);
				numOfVotes--;
			}
		}
		return votes;
	}

	/**
	 * Generates mock listings
	 */
	public Map<String, ListingDTO> createMockListings(Map<String, UserDTO> users) {
		Map<String, ListingDTO> listings = new HashMap<String, ListingDTO>();
		
		List<String> userIds = new ArrayList<String>(users.keySet());
		int bpNum = 0;
	
		ListingDTO bp = new ListingDTO();
		bp.createKey("mislead");
		bp.setName("MisLead");
		bp.setOwner(userIds.get(bpNum % userIds.size()));
		bp.setSuggestedValuation(20000);
		bp.setSuggestedPercentage(25);
		bp.setSuggestedAmount(bp.getSuggestedValuation()*bp.getSuggestedPercentage()/100);
		bp.setListedOn(new Date(System.currentTimeMillis() - 45 * 60 * 60 * 1000));
		bp.setState(ListingDTO.State.CLOSED);
		DateMidnight midnight = new DateMidnight(bp.getListedOn().getTime());
		bp.setClosingOn(midnight.plus(Days.days(30)).toDate());
		bp.setSummary("Executive summary for <b>MisLead</b>");
		listings.put(bp.getIdAsString(), bp);
	
		bp = new ListingDTO();
		bp.createKey("comp_training_camp");
		bp.setName("Computer Training Camp");
		bp.setOwner(userIds.get(bpNum % userIds.size()));
		bp.setSuggestedValuation(15000);
		bp.setSuggestedPercentage(45);
		bp.setSuggestedAmount(bp.getSuggestedValuation()*bp.getSuggestedPercentage()/100);
		bp.setListedOn(new Date(System.currentTimeMillis() - 5 * 60 * 60 * 1000));
		bp.setState(ListingDTO.State.CREATED);
		midnight = new DateMidnight(bp.getListedOn().getTime());
		bp.setClosingOn(midnight.plus(Days.days(30)).toDate());
		bp.setSummary("Starting a computer training camp for children is a terrific new business " +
				"venture to set in motion. In spite of the fact that many children now receive " +
				"computer training in school, attending computer camps ensures parents and" +
				" children a better and more complete understanding of the course material. " +
				"The computer camps can be operated on a year-round basis or in the summer only. " +
				"Typically, these camps are one or two days in length and available for various " +
				"training needs, from beginner to advanced. Once again, this is the kind of children's" +
				" business that can be operated as an independent business venture or operated in" +
				" conjunction with a community program or community center.");
		listings.put(bp.getIdAsString(), bp);

		bp = new ListingDTO();
		bp.createKey("comp_upgrading_service");
		bp.setName("Computer Upgrading Service");
		bp.setOwner(userIds.get(bpNum++ % userIds.size()));
		bp.setSuggestedValuation(35000);
		bp.setSuggestedPercentage(33);
		bp.setSuggestedAmount(bp.getSuggestedValuation()*bp.getSuggestedPercentage()/100);
		bp.setListedOn(new Date(System.currentTimeMillis() - 15 * 60 * 60 * 1000));
		bp.setState(ListingDTO.State.ACTIVE);
		midnight = new DateMidnight(bp.getListedOn().getTime());
		bp.setClosingOn(midnight.plus(Days.days(30)).toDate());
		bp.setSummary("Starting a business that specializes in upgrading existing computer systems" +
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
				" operates on a mobile format.");
		listings.put(bp.getIdAsString(), bp);

		bp = new ListingDTO();
		bp.createKey("semanticsearch");
		bp.setName("Semantic Search");
		bp.setOwner(userIds.get(bpNum++ % userIds.size()));
		bp.setSuggestedValuation(40000);
		bp.setSuggestedPercentage(45);
		bp.setSuggestedAmount(bp.getSuggestedValuation()*bp.getSuggestedPercentage()/100);
		bp.setListedOn(new Date(System.currentTimeMillis() - 15 * 60 * 60 * 1000));
		bp.setState(ListingDTO.State.ACTIVE);
		midnight = new DateMidnight(bp.getListedOn().getTime());
		bp.setClosingOn(midnight.plus(Days.days(30)).toDate());
		bp.setSummary("The fact of the matter is Google, and to a much lesser extent Bing, " + 
				"own the search market. Ask Barry Diller, if you don't believe us." +
				"Yet, startups still spring up hoping to disrupt the incumbents. " +
				"Cuil flopped. Wolfram Alpha is irrelevant. Powerset, which was a semantic" + 
				" search engine was bailed out by Microsoft, which acquired it.");
		listings.put(bp.getIdAsString(), bp);

		bp = new ListingDTO();
		bp.createKey("socialrecommendations");
		bp.setName("Social recommendations");
		bp.setOwner(userIds.get(bpNum++ % userIds.size()));
		bp.setSuggestedValuation(15000);
		bp.setSuggestedPercentage(10);
		bp.setSuggestedAmount(bp.getSuggestedValuation()*bp.getSuggestedPercentage()/100);
		bp.setListedOn(new Date(System.currentTimeMillis() - 20 * 60 * 60 * 1000));
		bp.setState(ListingDTO.State.ACTIVE);
		midnight = new DateMidnight(bp.getListedOn().getTime());
		bp.setClosingOn(midnight.plus(Days.days(30)).toDate());
		bp.setSummary("It's a very tempting idea. Collect data from people about their tastes" +
				" and preferences. Then use that data to create recommendations for others. " +
				"Or, use that data to create recommendations for the people that filled in " +
				"the information. It doesn't work. The latest to try is Hunch and Get Glue." +
				"Hunch is pivoting towards non-consumer-facing white label business. " +
				"Get Glue has had some success of late, but it's hardly a breakout business.");
		listings.put(bp.getIdAsString(), bp);

		bp = new ListingDTO();
		bp.createKey("localnewssites");
		bp.setName("Local news sites");
		bp.setOwner(userIds.get(bpNum++ % userIds.size()));
		bp.setSuggestedValuation(49000);
		bp.setSuggestedPercentage(20);
		bp.setSuggestedAmount(bp.getSuggestedValuation()*bp.getSuggestedPercentage()/100);
		bp.setListedOn(new Date(System.currentTimeMillis() - 3 * 60 * 60 * 1000));
		bp.setState(ListingDTO.State.ACTIVE);
		midnight = new DateMidnight(bp.getListedOn().getTime());
		bp.setClosingOn(midnight.plus(Days.days(30)).toDate());
		bp.setSummary("Maybe Tim Armstrong, AOL, and Patch will prove it wrong, but to this point" +
				" nobody has been able to crack the local news market and make a sustainable business." +
				"In theory creating a network of local news sites that people care about is a good" +
				" idea. You build a community, there's a baked in advertising group with local " +
				"businesses, and classifieds. But, it appears to be too niche to scale into a big" +
				" business.");
		listings.put(bp.getIdAsString(), bp);

		bp = new ListingDTO();
		bp.createKey("micropayments");
		bp.setName("Micropayments");
		bp.setOwner(userIds.get(bpNum++ % userIds.size()));
		bp.setSuggestedValuation(5000);
		bp.setSuggestedPercentage(49);
		bp.setSuggestedAmount(bp.getSuggestedValuation()*bp.getSuggestedPercentage()/100);
		bp.setListedOn(new Date(System.currentTimeMillis() - 23 * 60 * 60 * 1000));
		bp.setState(ListingDTO.State.ACTIVE);
		midnight = new DateMidnight(bp.getListedOn().getTime());
		bp.setClosingOn(midnight.plus(Days.days(30)).toDate());
		bp.setSummary("Micropayments are one idea that's tossed around to solve the problem" +
				" of paying for content on the Web. If you want to read a New York Times " +
				"story it would only cost a nickel! Or on Tumblr, if you want to tip a blogger" +
				" or pay for a small design you could with ease. So far, these micropayment" +
				" plans have not worked.");
		listings.put(bp.getIdAsString(), bp);

		bp = new ListingDTO();
		bp.createKey("kill email");
		bp.setName("Kill email");
		bp.setOwner(userIds.get(bpNum++ % userIds.size()));
		bp.setSuggestedValuation(40000);
		bp.setSuggestedPercentage(50);
		bp.setSuggestedAmount(bp.getSuggestedValuation()*bp.getSuggestedPercentage()/100);
		bp.setListedOn(new Date(System.currentTimeMillis() - 6 * 24 * 60 * 60 * 1000));
		bp.setState(ListingDTO.State.ACTIVE);
		midnight = new DateMidnight(bp.getListedOn().getTime());
		bp.setClosingOn(midnight.plus(Days.days(30)).toDate());
		bp.setSummary("If any startup says it's going to eliminate email, it's destined for failure. " +
				"You can iterate on the inbox, and try to improve it, but even that's " +
				"not much of a business. The latest high profile flop in this arena is " +
				"Google Wave. It was supposed to change email forever. It was going to " +
				"displace email. Didn't happen.");
		listings.put(bp.getIdAsString(), bp);

		bp = new ListingDTO();
		bp.createKey("better company car");
		bp.setName("Better company car");
		bp.setOwner(userIds.get(bpNum++ % userIds.size()));
		bp.setSuggestedValuation(100000);
		bp.setSuggestedPercentage(15);
		bp.setSuggestedAmount(bp.getSuggestedValuation()*bp.getSuggestedPercentage()/100);
		bp.setListedOn(new Date(System.currentTimeMillis() - 8 * 60 * 60 * 1000));
		bp.setState(ListingDTO.State.WITHDRAWN);
		midnight = new DateMidnight(bp.getListedOn().getTime());
		bp.setClosingOn(midnight.plus(Days.days(30)).toDate());
		bp.setSummary("Considering how frustrated people are with car companies, you'd think " +
				"launching a new one would be perfect for a startup. So far, that's not the case. " +
				"You can point to Tesla as a success, and considering it IPO'd it's hard to argue " +
				"against it. But, Tesla has sold fewer than 2,000 cars since it was founded in 2003. " +
				"It's far from certain it will succeed. Even when its next car comes out, Nissan " +
				"could be making a luxury electric car that competes with Tesla.");
		listings.put(bp.getIdAsString(), bp);
		
		return listings;
	}

}
