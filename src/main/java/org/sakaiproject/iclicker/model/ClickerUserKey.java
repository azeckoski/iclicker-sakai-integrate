/**
 * Copyright (c) 2009 i>clicker (R) <http://www.iclicker.com/dnn/>
 *
 * This file is part of i>clicker Sakai integrate.
 *
 * i>clicker Sakai integrate is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * i>clicker Sakai integrate is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with i>clicker Sakai integrate.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.sakaiproject.iclicker.model;

import java.io.Serializable;
import java.util.Date;

/**
 * This represents a clicker user key which is assigned to instructors on demand when used with an
 * LMS which uses a single sign on system. They key is used in place of a password and is a randomly
 * generated alphanum char sequence.
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class ClickerUserKey implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * Sakai userId (internal, not EID/USERNAME)
     */
    private String userId;
    /**
     * The encoded user key
     */
    private String userKey;
    private Date dateCreated;
    private Date dateModified;

    /**
     * Default constructor
     */
    public ClickerUserKey() {
    }

    /**
     * Full constructor
     */
    public ClickerUserKey(String userKey, String userId) {
        this.userKey = userKey;
        this.userId = userId;
        this.dateCreated = new Date();
        this.dateModified = this.dateCreated;
    }

    /**
     * Special copy constructor which ensures we are not handing around the persistent object
     */
    public ClickerUserKey(ClickerUserKey cluk) {
        this.id = cluk.getId();
        this.userKey = cluk.getUserKey();
        this.userId = cluk.getUserId();
        this.dateCreated = cluk.getDateCreated();
        this.dateModified = cluk.getDateModified();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClickerUserKey other = (ClickerUserKey) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ClickerUserKey [id=" + id + ", userId=" + userId + ", userKey=" + userKey + "]";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

}
