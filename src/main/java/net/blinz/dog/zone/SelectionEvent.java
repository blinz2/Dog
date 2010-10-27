/*
 * Dog - A project for making highly scalable non-clustered game and simulation environments.
 * Copyright (C) 2010 BlinzProject <gtalent2@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.blinz.dog.zone;

import net.blinz.dog.util.User;

/**
 * Holds information for SelectableSprites that receive selection.
 * @author Blinz
 */
public final class SelectionEvent {

    private User user;
    private int button, clickCount;
    /**
     * The location on the Screen that
     */
    private int x, y;

    /**
     * Constructor
     * @param user the User that made the selection
     * @param button the mouse button used to make the selection
     * @param clickCount the number of clicks behind the selection
     * @param x the exact x coordinate of the click
     * @param y the exact y coordinate of the click
     */
    SelectionEvent(final User user, final int button, final int clickCount, final int x, final int y) {
        this.user = user;
        this.button = button;
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the User that made the selection.
     * @return the User that made the selection
     */
    public final User getUser() {
        return user;
    }

    /**
     * Gets the mouse button used to make the selection.
     * @return the mouse button used to make the selection
     */
    public final int getButton() {
        return button;
    }

    /**
     * Gets the number of clicks behind the selection.
     * @returnthe number of clicks behind the selection
     */
    public final int clickCount() {
        return clickCount;
    }

    /**
     * Gets the exact x coordinate of the click.
     * @return the exact x coordinate of the click
     */
    public final int getX() {
        return x;
    }

    /**
     * Gets the exact y coordinate of the click.
     * @return the exact y coordinate of the click
     */
    public final int getY() {
        return y;
    }
}
