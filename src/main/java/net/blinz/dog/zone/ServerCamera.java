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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A Camera for monitoring server Zones.
 * @author Blinz
 */
public final class ServerCamera extends BaseCamera {

    private final HashMap<BaseSprite, CameraSprite> spriteMap = new HashMap<BaseSprite, CameraSprite>();
    private Socket socket;

    /**
     * Constructor
     * @param socket the socket with which this ServerCamera will communicate with its client
     */
    public ServerCamera(final Socket socket) {
	    this.socket = socket;
    }

    @Override
    final void addSprite(CameraSprite sprite) {
        spriteMap.put(sprite.getSprite(), sprite);
    }

    @Override
    void removeOrphanedSprites(final ArrayList<CameraSprite> orphans) {
        for (int i = 0; i < orphans.size(); i++) {
            spriteMap.remove(orphans.get(i).getSprite());
        }
    }
}
