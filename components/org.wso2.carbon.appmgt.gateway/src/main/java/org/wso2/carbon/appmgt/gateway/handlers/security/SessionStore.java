/*
 *
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   you may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.wso2.carbon.appmgt.gateway.handlers.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.appmgt.gateway.utils.CacheManager;
import org.wso2.carbon.appmgt.gateway.utils.GatewayUtils;
import org.wso2.carbon.appmgt.impl.AppMConstants;

import javax.cache.Cache;
import javax.cache.Caching;

/**
 * The store of user sessions.
 */
public class SessionStore {


    private static SessionStore instance;

    private static Log log = LogFactory.getLog(SessionStore.class);

    public static SessionStore getInstance() {
        if (instance == null) {
            synchronized (SessionStore.class) {
                if (instance == null) {
                    instance = new SessionStore();
                }
            }
        }
        return instance;
    }

    public Session getSession(String uuid, boolean createIfNotExists){

        Session session = null;

        if(createIfNotExists && (uuid == null || (session = getSessionCache().get(uuid)) == null)){

            session = new Session();
            getSessionCache().put(session.getUuid(), session);

            if(log.isDebugEnabled()){
                log.debug(String.format("{%s} - A session is not available for '%s'. Created a new session.",
                        GatewayUtils.getMD5Hash(session.getUuid()), uuid));
            }
        }else if(uuid != null){
            session = getSessionCache().get(uuid);
        }

        return session;
    }

    public void updateSession(Session session){
        getSessionCache().put(session.getUuid(), session);
    }

    public void removeSession(String uuid) {
        getSessionCache().remove(uuid);
    }

    private Cache<String, Session> getSessionCache() {
        return CacheManager.getInstance().getSessionCache();
    }
}