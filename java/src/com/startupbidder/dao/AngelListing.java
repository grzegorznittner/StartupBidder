package com.startupbidder.dao;

/**
 * Created by IntelliJ IDEA.
 * User: minlenmay
 * Date: 4/28/12
 * Time: 12:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class AngelListing {
    public String id;
    public String name;
    //public String angellist_url;
    //public String logo_url;
    public String product_desc;
    public String high_concept;
    public String company_url;
    public String created_at;
    public String updated_at;
    //public String video_url;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getProduct_desc() { return product_desc; }
    public String getHigh_concept() { return high_concept; }
    public String getCompany_url() { return company_url; }
    public String getCreated_at() { return created_at; }
    public String getUpdated_at() { return updated_at; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setProduct_desc(String product_desc) { this.product_desc = product_desc; }
    public void setHigh_concept(String high_concept) { this.high_concept = high_concept; }
    public void setCompany_url(String company_url) { this.company_url = company_url; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
    public void setUpdated_at(String updated_at) { this.updated_at = updated_at; }

}
