/*
 *  BlinzEngine - A library for large 2D world simultions and games.
 *  Copyright (C) 2009  Blinz <gtalent2@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.blinz.util;

import java.util.Vector;

/**
 *
 * @author Blinz
 */
public final class Users {

    private final static Vector<User> users = new Vector<User>();

    /**
     * Adds given User to the list of Users.
     * @param user
     */
    public final static void add(final User user) {
        users.add(user);
    }

    /**
     * 
     * @param index
     * @return the User at that given index.
     */
    public final static User get(final int index) {
        return users.get(index);
    }

    /**
     * Removes the User at the specified index.
     * @param index
     */
    public final static void remove(final int index) {
        users.remove(index);
    }

    /**
     * Returns a count of the existing users.
     * @return count of Users
     */
    public final static int userCount() {
        return users.size();
    }
}
