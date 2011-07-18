package com.startupbidder.dao;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.startupbidder.dto.ListingDTO;
import com.startupbidder.vo.ListPropertiesVO;

public class AppEngineDatastoreDAO extends MockDatastoreDAO {
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public AppEngineDatastoreDAO() {
		super();
		
	}

	@Override
	public List<ListingDTO> getUserListings(String userId, ListPropertiesVO listingProperties) {
		// TODO Auto-generated method stub
		return super.getUserListings(userId, listingProperties);
	}

}
