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
package org.blinz.world;

import org.blinz.util.User;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import org.blinz.util.Size;
import org.blinz.util.Bounds;
import org.blinz.util.concurrency.Barrier;
import org.blinz.util.concurrency.OnePassTask;
import org.blinz.util.concurrency.Task;
import org.blinz.util.concurrency.TaskExecuter;

/**
 * Represents a realm for sprites to be processed and interact.
 * @author Blinz
 */
public abstract class Zone extends ZoneObject {

    private class Pause extends Task {

        @Override
        protected void run() {
            if (paused) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Zone.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                setMoveOn();
            }
        }
    }

    private class ManageTime extends OnePassTask {

        @Override
        protected void run() {
            cycleStartTime = System.currentTimeMillis();
            getData().zoneTime = System.currentTimeMillis() - initTime;
            getData().zoneTime -= pauseTime;
        }
    }

    private class UpdateSectors extends Task {

        @Override
        protected void run() {
            //Update sectors.
            Sector s = updateSchema.getNextSector();
            s.update();
            int x = s.getXIndex();
            for (int y = s.getYIndex(); y < getData().sectors[0].length; y++) {
                while (x < getData().sectors.length) {
                    getData().sectors[x++][y].update();
                }
                x = 0;
            }
            setMoveOn();
        }
    }

    private class PostUpdate extends Task {

        @Override
        protected void run() {
            //Post-update all the sectors.
            Sector next = updateSchema.getNextPostUpdateSector();
            while (next != null) {
                next.postUpdate();
                next = updateSchema.getNextPostUpdateSector();
            }
            setMoveOn();
        }
    }

    private class DeleteSprites extends Task {

        @Override
        protected void run() {
            //Post-update all the sectors.
            int i = updateSchema.spriteToDelete();
            while (i < getData().spritesToDelete.size()) {
                deleteSprite(getData().spritesToDelete.get(idIndex));
            }
            setMoveOn();
        }
    }

    private class UpdateZone extends OnePassTask {

        @Override
        protected void run() {
            getData().zoneCycles++;
            update();
        }
    }

    private class UpdateObservers extends Task {

        @Override
        protected void run() {
            Camera observer = updateSchema.nextCamera();
            while (observer != null) {
                observer.internalUpdate();
                observer = updateSchema.nextCamera();
            }
            setMoveOn();
        }
    }

    private class Reset extends OnePassTask {

        @Override
        protected void run() {
            updateSchema.reset();
        }
    }

    private class Sleep extends Task {

        @Override
        protected void run() {
            long sleepTime = (cycleStartTime + cycleIntervalTime) - System.currentTimeMillis();
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Zone.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private class ListTrimmer extends Thread {

        @Override
        public void run() {
            while (isRunning) {
                trimLists();

                try {
                    Thread.sleep(600000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Zone.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    private Size size;
    private final ArrayList<Camera> cameras = new ArrayList<Camera>();
    private final Hashtable<User, Vector<BaseSprite>> userListeners = new Hashtable<User, Vector<BaseSprite>>();
    private ZoneUpdateSchema updateSchema;
    private String name = "Zone";
    private long initTime;
    private long pauseTime = 0;
    /**
     * The amount of time to pass between loops.
     */
    private long cycleIntervalTime = 5;
    private long cycleStartTime;
    private Thread listTrimmer = new ListTrimmer();
    private boolean paused = false;
    private boolean isRunning = false;
    private TaskExecuter zoneProcessor;
    private static final Vector<Byte> recycledIDs = new Vector<Byte>();
    private static byte idIndex = 0;

    public Zone() {
        try {
            zoneID = generateZoneID();
        } catch (Exception ex) {
            Logger.getLogger(Zone.class.getName()).log(Level.SEVERE, null, ex);
        }

        setZoneData(zoneID, new ZoneData());
        getData().init(zoneID);
        size = getData().zoneSize;
        updateSchema = new TrivialSectorUpdateSchema(getData().sectors, zoneProcessor, cameras);
    }

    /**
     * Returns the name of this Zone. "Zone" by default. This name will also be
     * assigned to threads processing this Zone when created.
     * @return the name of this Zone, "Zone" by default
     */
    public String getName() {
        return name;
    }

    /**
     * Pauses the zone.
     * 
     * Note: This pause is not meant for short pauses, it will take at least 250
     * milliseconds before the Zone resumes execution even if resume() is called
     * immediately.
     */
    public final void pause() {
        paused = true;
    }

    /**
     * Unpauses the zone.
     */
    public final void unpause() {
        paused = false;
        pauseTime = System.currentTimeMillis() - getData().zoneTime;
    }

    /**
     *
     * @return a new Size object representing the dimensions of this zone.
     */
    public final Size getSize() {
        return new Size(size);
    }

    /**
     *
     * @return int representing the width of this zone
     */
    public final int getWidth() {
        return size.width;
    }

    /**
     *
     * @return int representing the height of this zone
     */
    public final int getHeight() {
        return size.height;
    }

    /**
     * Adds the given sprite to this sprites zone.
     * @param sprite
     */
    public final void addSprite(BaseSprite sprite) {
        getData().addSprite(sprite);
    }

    /**
     * Creates threads for processing this Zone.
     * @param threads number of threads dedicated to this Zone
     */
    public final synchronized void start(int threads) {
        start(name, threads);
    }

    /**
     * Creates threads for processing this Zone.
     * @param name the name assigned to the threads processing this Zone
     * @param threads number of threads dedicated to this Zone
     */
    public final synchronized void start(String name, int threads) {
        if (!isRunning) {
            this.name = name;
            zoneProcessor = new TaskExecuter(name, threads);
            zoneProcessor.addTask(new Pause());
            zoneProcessor.addTask(new Barrier());
            zoneProcessor.addTask(new ManageTime());
            zoneProcessor.addTask(new UpdateSectors());
            zoneProcessor.addTask(new Barrier());
            zoneProcessor.addTask(new PostUpdate());
            zoneProcessor.addTask(new DeleteSprites());
            zoneProcessor.addTask(new Barrier());
            zoneProcessor.addTask(new UpdateZone());
            zoneProcessor.addTask(new UpdateObservers());
            zoneProcessor.addTask(new Sleep());
            zoneProcessor.addTask(new Barrier());
            zoneProcessor.addTask(new Reset());
            init();
            zoneProcessor.start();
            listTrimmer.start();
        }
    }

    /**
     * Cause updating of this Zone to stop after the current cycle.
     */
    public final void stop() {
        isRunning = false;
        zoneProcessor.stop();
        zoneProcessor = null;
    }

    public synchronized void addUserListeningSprite(User user, BaseSprite sprite) {
        if (userListeners.contains(user)) {
            Vector<BaseSprite> sprites = new Vector<BaseSprite>();
            userListeners.put(user, sprites);
            sprites.add(sprite);
        } else {
            userListeners.get(user).add(sprite);
        }
    }

    /**
     * Sets the width of this Zone to the given value.
     * @param width
     */
    protected final void setWidth(int width) {
        size.setWidth(width);
        refactorSectors();
    }

    /**
     * Sets the height of this Zone to the given value.
     * @param height
     */
    protected final void setHeight(int height) {
        size.setHeight(height);
        refactorSectors();
    }

    /**
     * Sets the width and height of this Zone.
     * @param width
     * @param height
     */
    protected final void setSize(int width, int height) {
        size.setSize(width, height);
        refactorSectors();
    }

    /**
     *
     * @return a long representing the number of cycles the zone has gone through
     */
    protected final long cycles() {
        return getData().zoneCycles;
    }

    /**
     *
     * @return the amount of time the zone has been running minus the pause time
     */
    protected final long time() {
        return getData().zoneTime;
    }

    @Override
    protected final void init() {
        initTime = System.currentTimeMillis();
        initZone();
    }

    /**
     * Passes the given data to the Zone and its sprites so that an extra pointer
     * does not have to be assigned to each sprite to reduce memory consumption.
     * @param sharedData
     */
    protected final void setSharedZoneData(SharedZoneData sharedData) {
        getData().data = sharedData;
    }

    @Override
    protected void finalize() {
        for (Camera camera : cameras) {
            camera.dropZone();
        }

        setZoneData(zoneID, null);
        recycledIDs.add(zoneID);
    }

    /**
     * Abstract method for initializing the zone.
     */
    protected abstract void initZone();

    /**
     * Abstract method for updating the zone to be implemented by the developer.
     * Note: This method is not called while the Zone is paused.
     */
    protected abstract void update();

    /**
     * Adds the given Camera to this Zone, to moniter the sprites in its area.
     * @param observer
     */
    final void addCamera(Camera observer) {
        cameras.add(observer);
        getData().registerZoneObject(observer);
    }

    /**
     * Removes the given sprite to this Zone.
     * @param observer
     */
    final void removeCamera(Camera observer) {
        cameras.remove(observer);

        for (int i = 0; i < getData().sectors.length; i++) {
            for (int n = 0; n < getData().sectors[i].length; n++) {
                if (getData().sectors[i][n].intersects(observer.getX(), observer.getY(), observer.getWidth(), observer.getHeight())) {
                    getData().sectors[i][n].removeCamera(observer);
                    i = getData().sectors.length;
                    break;
                }
            }
        }
    }

    /**
     * Places Sectors intersecting with the given Bounds into the given ArrayList.
     * @param bounds
     * @param sectors
     */
    final void getIntersectingSectors(Bounds bounds, ArrayList<Sector> sectors) {
        for (int i = 0; i < getData().sectors.length; i++) {
            for (int n = 0; n < getData().sectors[i].length; n++) {
                if (getData().sectors[i][n].intersects(bounds)) {
                    sectors.add(getData().sectors[i][n]);
                }
            }
        }
    }

    /**
     * Trims excessively large lists.
     */
    final void trimLists() {
        getData().trimLists();
        cameras.trimToSize();
    }

    /**
     *
     * @param user
     * @return all sprites listening to input from the given user.
     */
    final Vector<BaseSprite> getSprites(User user) {
        return userListeners.get(user);
    }

    /**
     * Generates a distinct zone id for a new Zone.
     * @return byte
     */
    private final synchronized byte generateZoneID() throws Exception {
        if (idIndex < 127) {
            return (byte) (++idIndex);
        } else if (recycledIDs.size() != 0) {
            return recycledIDs.remove(0);
        } else {
            throw new Exception("Out of Zone IDs. Max number of active Zones is 128.");
        }

    }

    /**
     * Deletes the given sprite from this Zone.
     * @param sprite
     */
    private final void deleteSprite(BaseSprite sprite) {
        Sector tl = getData().getSectorOf(sprite.getX(), sprite.getY());
        Sector br = getData().getSectorOf(sprite.getX() + sprite.getWidth(), sprite.getY() + sprite.getHeight());
        tl.deleteIntersectingSprite(sprite);
        if (sprite instanceof UpdatingSprite) {
            tl.deleteUpdatingSprite((UpdatingSprite) sprite);
        }
        if (tl != br) {
            br.deleteIntersectingSprite(sprite);
            Sector tr = getData().getSectorOf(sprite.getX() + sprite.getWidth(), sprite.getY());
            if (tr != br && tl != tr) {
                getData().getSectorOf(sprite.getX(), sprite.getY() + sprite.getHeight()).deleteIntersectingSprite(sprite);
                tr.deleteIntersectingSprite(sprite);
            }

        }
    }

    /**
     * Refactors the Sectors to fit the current size of this Zone.
     */
    private final synchronized void refactorSectors() {
        if (size.width == 0 || size.height == 0) {
            return;
        }

        if (size.width / getData().sectorSize.width + 1 <= getData().sectors.length
                || size.height / getData().sectorSize.width + 1 <= getData().sectors[0].length) {
            return;
        }

        ArrayList<Sector> newSectors = new ArrayList<Sector>();

        Sector[][] sectors =
                new Sector[size.width / getData().sectorSize.width + 1][size.height / getData().sectorSize.width + 1];
        for (int i = 0; i < sectors.length; i++) {
            for (int n = 0; n < sectors[i].length; n++) {
                if (i >= getData().sectors.length || n >= getData().sectors[i].length) {
                    sectors[i][n] = new Sector(getData().sectorSize.width * i, getData().sectorSize.height * n);
                    newSectors.add(sectors[i][n]);
                } else {
                    sectors[i][n] = getData().sectors[i][n];
                }
            }
        }

        getData().sectors = sectors;

        for (Sector sector : newSectors) {
            getData().registerZoneObject(sector);
        }

        for (int i = 0; i
                < sectors.length; i++) {
            for (int n = 0; n
                    < sectors[i].length; n++) {
                sectors[i][n].findNeighbors();
            }

        }

        if ((zoneProcessor.getThreadCount() == 1)
                || (sectors.length == 1 && sectors[0].length == 1)) {
            if (!(updateSchema instanceof TrivialSectorUpdateSchema)) {
                updateSchema = new TrivialSectorUpdateSchema(sectors, zoneProcessor, cameras);
            }

        } else {
            updateSchema = new LargeSectorUpdateSchema(sectors, zoneProcessor, cameras);
        }
    }
}
