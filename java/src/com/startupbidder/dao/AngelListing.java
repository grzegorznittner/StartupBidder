package com.startupbidder.dao;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: minlenmay
 * Date: 4/28/12
 * Time: 12:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class AngelListing {

    public static class AngelLocation {
        public String id;
        public String name;
        // public String display_name;
        // public String tag_type;
        // public String angellist_url;
    }

    public String id;
    public String name;
    //public String angellist_url;
    public String logo_url;
    public String product_desc;
    public String high_concept;
    public String company_url;
    public String created_at;
    public String updated_at;
    public ArrayList<AngelLocation> locations;
    //public String video_url;

}
