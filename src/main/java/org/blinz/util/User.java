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
public class User {

    private int id = getIndex();
    private String name = "User " + id;
    private static int idIndex = -1;
    private static Vector<Integer> oldIDs = new Vector<Integer>();

    /**
     * Returns a String representing the name of the User.
     * @return String with the name of the User
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the User to that in the String provided.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    private static synchronized int getIndex() {
        if (oldIDs.isEmpty()) {
            return idIndex++;
        }
        oldIDs.remove(idIndex);
        return oldIDs.get(idIndex);
    }

    @Override
    protected void finalize() throws Throwable {
        oldIDs.add(id);
        super.finalize();
    }
}
