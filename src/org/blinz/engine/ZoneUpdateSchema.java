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
package org.blinz.engine;

import java.util.List;
import org.blinz.util.Position;
import org.blinz.util.concurrency.TaskExecuter;

/**
 * Class used to efficiently and cleanly choose fastest way to update the Sectors.
 * @author Blinz
 */
abstract class ZoneUpdateSchema {

    int xLength, yLength;
    Sector[][] sectors;
    private List<Camera> cameras;
    private Position straightInterater = new Position();
    private int cameraIndex = 0;
    private int spriteDeletionIndex = 0;

    ZoneUpdateSchema(Sector[][] sectors, TaskExecuter zoneProcessor, List<Camera> cameras) {
        this.cameras = cameras;
        this.sectors = sectors;
        this.xLength = sectors.length;
        this.yLength = sectors[0].length;
    }

    /**
     *
     * @return the index of the next sprite to delete.
     */
    final synchronized int spriteToDelete() {
        return spriteDeletionIndex++;
    }

    final synchronized Sector getNextPostUpdateSector() {
        if (straightInterater.y < yLength - 1) {
            straightInterater.y++;
        } else if (straightInterater.x < xLength - 1) {
            straightInterater.x++;
            straightInterater.y = 0;
        } else {
            return null;
        }
        return sectors[straightInterater.x][straightInterater.y];
    }

    /**
     *
     * @return the next Camera to be updated, prevents a double update
     */
    final synchronized Camera nextCamera() {
        if (cameraIndex < cameras.size()) {
            return cameras.get(cameraIndex++);
        }
        return null;
    }

    void reset() {
        straightInterater.setPosition(0, -1);
        cameraIndex = 0;
    }

    abstract Sector getNextSector();
}

/**
 * Used when the Sector update order is trivial.
 *
 * i.e. When there is only one thread or one Sector.
 * @author Blinz
 */
class TrivialSectorUpdateSchema extends ZoneUpdateSchema {

    TrivialSectorUpdateSchema(Sector[][] sectors, TaskExecuter zoneProcessor, List<Camera> cameras) {
        super(sectors, zoneProcessor, cameras);
        this.xLength = sectors.length;
        this.yLength = sectors[0].length;
    }

    @Override
    Sector getNextSector() {
        return sectors[0][0];
    }
}

/**
 * For use when Sector length is 5 or greater in either dimension.
 *
 * @author Blinz
 */
class LargeSectorUpdateSchema extends ZoneUpdateSchema {

    private final Position address = new Position();
    private int xInterval, yInterval;

    LargeSectorUpdateSchema(Sector[][] sectors, TaskExecuter zoneProcessor, List<Camera> cameras) {
        super(sectors, zoneProcessor, cameras);
        xInterval = Math.round(xLength / zoneProcessor.getThreadCount());
        yInterval = Math.round(yLength / zoneProcessor.getThreadCount());
        address.setPosition(-xInterval, -yInterval);
        this.sectors = sectors;
    }

    @Override
    synchronized Sector getNextSector() {
        address.x += xInterval;
        address.y += yInterval;

        if (address.x > sectors.length) {
            address.x -= xInterval;
        }
        if (address.y > sectors[0].length) {
            address.y -= yInterval;
        }
        return sectors[address.x][address.y];
    }

    @Override
    void reset() {
        super.reset();
        address.x = -xInterval;
        address.y = -yInterval;
    }
}
