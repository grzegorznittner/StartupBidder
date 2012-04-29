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
public class AngelListCache {
    @Id
    String path;
    @Unindexed
    String json;
    AngelListCache() {}
    public AngelListCache(String path, String json) {
        this.path = path;
        this.json = json;
    }
}
