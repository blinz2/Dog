/*
 *  BlinzEngine - A library for large 2D world simultions and games.
 *  Copyright (C) 2009-2010  Blinz <gtalent2@gmail.com>
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
package org.blinz.world;

/**
 * For organizing messages to be sent over the network by the sprites of a Sector.
 * @author Blinz
 */
final class SectorMessageManager {

    /**
     * The sprite who's messages are currently in queue.
     */
    private BaseSprite current = null;
    private String messages = "";

    final String getMessages() {
        return messages;
    }

    /**
     * Puts the given message in queue to be sent to the given sprite. The message
     * will be delivered to the corresponding sprite on the recipient machine.
     * @param sprite the sprite sending the message
     * @param message the message to be sent
     */
    final synchronized void addMessage(BaseSprite sprite, String message) {
        if (current == sprite) {
            messages += message;
        } else {
            current = sprite;
            messages += sprite.hashCode() + message;
        }
    }
}
