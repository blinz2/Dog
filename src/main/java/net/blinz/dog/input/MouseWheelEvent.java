/*
 * Dog - A project for making highly scalable non-clustered game and simulation environments.
 * Copyright (C) 2009-2010 BlinzProject <gtalent2@gmail.com>
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
package net.blinz.dog.input;

import net.blinz.dog.util.User;

/**
 * An InputEvent generated with use of the mouse wheel.
 * @author Blinz
 */
public class MouseWheelEvent extends InputEvent {

    private int ticks, cursorX, cursorY;

    /**
     * Constructor
     * @param user the user that generated this event
     * @param cursorX the x coordinate of the cursor at the time of this event
     * @param cursorY the y coordinate of the cursor at the time of this event
     * @param scrollTicks the number of ticks behind this event
     */
    public MouseWheelEvent(final User user, final int cursorX, final int cursorY, final int scrollTicks) {
        super(user);
        ticks = scrollTicks;
        this.cursorX = cursorX;
        this.cursorY = cursorY;
    }

    /**
     * Gets the number of scroll ticks in this event.
     * @return the number of scroll ticks in this event.
     */
    public final int getScrollTicks() {
        return ticks;
    }

    /**
     * Gets the x coordinate of the cursor at the time of this event
     * @return the x coordinate of the cursor at the time of this event
     */
    public final int cursorX() {
        return cursorX;
    }

    /**
     * Gets the y coordinate of the cursor at the time of this event
     * @return the y coordinate of the cursor at the time of this event
     */
    public final int cursorY() {
        return cursorY;
    }
}
