package com.startupbidder.dao;

import com.googlecode.objectify.annotation.Unindexed;

import javax.persistence.Id;

/**
* Created by IntelliJ IDEA.
* User: minlenmay
* Date: 4/29/12
* Time: 1:07 PM
* To change this template use File | Settings | File Templates.
*/
public class StartuplyCache {
    @Id
    String path;
    @Unindexed
    String page;
    StartuplyCache() {}
    public StartuplyCache(String path, String page) {
        this.path = path;
        this.page = page;
    }
}
