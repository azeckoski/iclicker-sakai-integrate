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
 * This represents a clicker (iclicker remote) registration which is stored in the local Sakai DB,
 * this registration can be controlled by the owner or the system admin only <br/>
 * Registrations can be viewed by the instructor of the courses the owner is in <br/>
 * Anyone in the system can create a registration
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class ClickerRegistration implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String clickerId;
    /**
     * Sakai userId (internal, not EID/USERNAME)
     */
    private String ownerId;
    /**
     * [OPTIONAL] Sakai course ID
     */
    private String locationId;
    /**
     * if this is true then the registration is effectively deleted (disabled)
     * and should not be returned as part of the data feeds,
     * it should still be displayed to the owner but not to others
     */
    private boolean activated = true;
    /**
     * if this is true it means this registration came from the national system,
     * or has been synced with it
     */
    private boolean national = false;
    private Date dateCreated;
    private Date dateModified;

    // NON-PERSISTENT
    public String userDisplayName;
    @SuppressWarnings("unused")
    public String getUserDisplayName() {
        return userDisplayName;
    }

    /**
     * Default constructor
     */
    public ClickerRegistration() {
    }

    /**
     * Minimal constructor
     */
    public ClickerRegistration(String clickerId, String ownerId) {
        this(clickerId, ownerId, null);
    }

    /**
     * Full constructor
     */
    public ClickerRegistration(String clickerId, String ownerId, String locationId) {
        this.clickerId = clickerId;
        this.ownerId = ownerId;
        this.locationId = "".equals(locationId) ? null : locationId;
        this.dateCreated = new Date();
        this.dateModified = this.dateCreated;
    }

    /**
     * Special copy constructor which ensures we are not handing around the persistent object
     */
    public ClickerRegistration(ClickerRegistration cr) {
        this.id = cr.getId();
        this.clickerId = cr.getClickerId();
        this.ownerId = cr.getOwnerId();
        this.locationId = cr.getLocationId();
        this.activated = cr.isActivated();
        this.dateCreated = cr.getDateCreated();
        this.dateModified = cr.getDateModified();
    }

    /**
     * @return a unique key which can be used for maps
     */
    public String getKey() {
        return clickerId+":"+ownerId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clickerId == null) ? 0 : clickerId.hashCode());
        result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
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
        ClickerRegistration other = (ClickerRegistration) obj;
        if (id != null && other.id != null) {
            // to make hibernate happy
            return id.equals(other.id);
        } else {
            if (clickerId == null) {
                if (other.clickerId != null)
                    return false;
            } else if (!clickerId.equals(other.clickerId))
                return false;
            if (ownerId == null) {
                if (other.ownerId != null)
                    return false;
            } else if (!ownerId.equals(other.ownerId))
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.clickerId + ":uid=" + this.ownerId + ":" + this.dateModified;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClickerId() {
        return clickerId;
    }

    public void setClickerId(String clickerId) {
        this.clickerId = clickerId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = "".equals(locationId) ? null : locationId;
    }

    public boolean isNational() {
        return national;
    }

    public void setNational(boolean national) {
        this.national = national;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
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
