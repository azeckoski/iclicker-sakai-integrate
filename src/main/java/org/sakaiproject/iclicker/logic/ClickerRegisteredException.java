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
package org.sakaiproject.iclicker.logic;

/**
 * Exception which indicates the clicker is already registered,
 * contains info about who it is registered to and who tried to 
 * register it
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class ClickerRegisteredException extends RuntimeException {

    public String ownerId;
    public String clickerId;
    public String registeredOwnerId;

    public ClickerRegisteredException(String ownerId, String clickerId,
            String registeredOwnerId) {
        super();
        this.ownerId = ownerId;
        this.clickerId = clickerId;
        this.registeredOwnerId = registeredOwnerId;
    }

}
