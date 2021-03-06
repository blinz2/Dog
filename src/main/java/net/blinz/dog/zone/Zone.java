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
package net.blinz.dog.zone;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Vector;
import net.blinz.core.util.Position;
import net.blinz.core.util.Size;
import net.blinz.dog.input.ClickEvent;
import net.blinz.dog.input.KeyEvent;
import net.blinz.dog.input.MouseEvent;
import net.blinz.dog.input.MouseWheelEvent;
import net.blinz.dog.util.Barrier;
import net.blinz.dog.util.SynchronizedTask;
import net.blinz.dog.util.Task;
import net.blinz.dog.util.TaskExecuter;
import net.blinz.dog.util.TaskList;

/**
 * Represents a realm for sprites to be processed and interact.
 * @author Blinz
 */
public class Zone extends ZoneObject {

    /**
     * Task for pausing the Zone if it is paused.
     */
    private class Pause extends Task {

        @Override
        protected void run() {
            //pause if Zone is paused
            while (getData().paused()) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Zone.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Manages the time.
     */
    private class ManageTime extends SynchronizedTask {

        @Override
        protected void run() {
            cycleStartTime = System.currentTimeMillis();
            getData().zoneTime = (System.currentTimeMillis() - initTime) - pauseTime;
        }
    }

    /**
     * For updating the cycles executed and the update method implemented by
     * users of the API.
     */
    private class UpdateZone extends SynchronizedTask {

        @Override
        protected void run() {
            getData().zoneCycles++;
            update();
        }
    }

    /**
     * Adds new Cameras to this Zone.
     */
    private class AddCameras extends SynchronizedTask {

        @Override
        protected void run() {
            addCameras();
        }
    }

    /**
     * Updates the user input for sprites requesting input from specific users.
     */
    private class UserListenerUpdate extends SynchronizedTask {

        @Override
        protected void run() {
            getData().userListeners.update();
        }
    }

    /**
     * Deletes sprites marked for deletion.
     */
    private class DeleteSprites extends Task {

        @Override
        protected void run() {
            for (BaseSprite s = getData().spritesToDelete.remove(0);
                    s != null; s = getData().spritesToDelete.remove(0)) {
                removeSprite(s);
            }
        }
    }

    /**
     * Deletes sprites marked for deletion. Must come before PostUpdateSectors.
     */
    private class ResetCameraIndex extends SynchronizedTask {

        @Override
        protected void run() {
            currentCamera = 0;
        }
    }

    /**
     * Deletes sprites marked for deletion. Must come before PostUpdateSectors.
     */
    private class UpdateCameras extends Task {

        @Override
        protected void run() {
            //Update Cameras
            for (BaseCamera camera = nextCamera(); camera != null; camera = nextCamera()) {
                camera.internalUpdate();
            }
        }
    }

    /**
     * Deletes sprites marked for deletion.
     */
    private class Sleep extends Task {

        @Override
        protected void run() {
            final long sleepTime = (cycleStartTime + cycleIntervalTime) - System.currentTimeMillis();
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

        public ListTrimmer() {
            setDaemon(true);
        }

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
    /**
     * Used to track the next Camera to be updated.
     */
    private int currentCamera = 0;
    private Size size;
    private final Vector<BaseCamera> cameras = new Vector<BaseCamera>();
    private final Vector<BaseCamera> camerasToAdd = new Vector<BaseCamera>();
    private String name = "Zone";
    private long initTime;
    private long pauseTime = 0;
    /**
     * The amount of time to pass between loops.
     */
    private long cycleIntervalTime = 5;
    private long cycleStartTime;
    private Thread listTrimmer = new ListTrimmer();
    private boolean isRunning = false;
    private TaskExecuter zoneProcessor;
    private final TaskList sectorUpdate = new TaskList();
    private final TaskList sectorPostUpdate = new TaskList();
    private final TaskList updatingObjects = new TaskList();

    /**
     * Constructor
     */
    public Zone() {
        setZoneData(new ZoneData(this));
        getData().init();
        size = getData().zoneSize;
    }

    /**
     * Gets the name of this Zone. "Zone" by default. This name will also be
     * assigned to threads processing this Zone when created.
     * @return the name of this Zone, "Zone" by default
     */
    public final String getName() {
        return name;
    }

    /**
     * Pauses the Zone.
     * 
     * Note: This pause is not meant for short pauses, it will take at least 250
     * milliseconds before the Zone resumes execution even if resume() is called
     * immediately. Also pauses user input going to the sprites, but no the Zone.
     */
    public final void pause() {
        getData().pause();
    }

    /**
     * Unpauses the Zone.
     */
    public final void unpause() {
        pauseTime = System.currentTimeMillis() - getData().zoneTime;
        getData().unpause();
    }

    /**
     * Sets the amount of time between cycles of execution for this Zone. Defaults
     * to 5 milliseconds.
     * @param interval the time between cycles of execution for this Zone in milliseconds
     */
    public final void setCycleInterval(final long interval) {
        cycleIntervalTime = interval;
    }

    /**
     * Gets a new Size object representing the dimensions of this Zone.
     * @return a new Size object representing the dimensions of this Zone.
     */
    public final Size getSize() {
        return new Size(size);
    }

    /**
     * Gets an int representing the width of this Zone.
     * @return an int representing the width of this Zone
     */
    public final int getWidth() {
        return size.width;
    }

    /**
     * Gets an int representing the height of this Zone.
     * @return an int representing the height of this Zone
     */
    public final int getHeight() {
        return size.height;
    }

    /**
     * Adds the given UpdatingObject to this Zone for it to update every cycle with
     * this Zone.
     * @param updatingObject the UpdatingObject to be added
     */
    public final void addUpdatingObject(final UpdatingObject updatingObject) {
        updatingObjects.add(new Updater(updatingObject));
    }

    /**
     * Removes the given UpdatingObject to this Zone for it to update every cycle with
     * this Zone.
     * @param updatingObject the UpdatingObject to be removed
     * @return true if the given object was present, false otherwise
     */
    public final boolean removeUpdatingObject(final UpdatingObject updatingObject) {
        for (int i = 0; i < updatingObjects.size(); i++) {
            final Updater updater = (Updater) updatingObjects.get(i);
            if (updater.getUpdatingObject() == updatingObject) {
                updatingObjects.remove(updater);
                return true;
            }
        }
        return false;
    }

    /**
     * Adds the given sprite to this sprites zone.
     * @param sprite the sprite to be added to the Zone
     */
    public final void addSprite(final BaseSprite sprite) {
        getData().addSprite(sprite);
    }

    /**
     * Creates threads for processing this Zone.
     * @param threads number of threads dedicated to this Zone
     */
    public final synchronized void start(final int threads) {
        start("Zone", threads);
    }

    /**
     * Creates threads for processing this Zone.
     * @param name the name assigned to the threads processing this Zone
     * @param threads number of threads dedicated to this Zone
     */
    public final synchronized void start(final String name, final int threads) {
        if (!isRunning) {
            zoneProcessor = new TaskExecuter(name, threads);

            getData().setName(name);
            initTime = System.currentTimeMillis();
            isRunning = true;
            init();

            zoneProcessor.addTask(new Pause());
            zoneProcessor.addTask(new ManageTime());
            zoneProcessor.addTask(new UpdateZone());
            zoneProcessor.addTask(updatingObjects);
            zoneProcessor.addTask(new AddCameras());
            zoneProcessor.addTask(new UserListenerUpdate());
            zoneProcessor.addTask(new Barrier());

            zoneProcessor.addTask(sectorUpdate);
            zoneProcessor.addTask(new ResetCameraIndex());
            zoneProcessor.addTask(new Barrier());
            zoneProcessor.addTask(new UpdateCameras());
            zoneProcessor.addTask(new Barrier());
            zoneProcessor.addTask(sectorPostUpdate);

            zoneProcessor.addTask(new Barrier());
            zoneProcessor.addTask(new DeleteSprites());
            zoneProcessor.addTask(new Sleep());

            generateSectorGroups(getData().sectors);
            listTrimmer.start();
            zoneProcessor.start();
        }
    }

    /**
     * Cause updating of this Zone to stop after the current cycle.
     */
    public final void stop() {
        isRunning = false;
        zoneProcessor.stop();
    }

    /**
     * Runs the given CollidableSprite against other CollidableSprites to check
     * for collisions.
     * @param sprite
     */
    public final void checkCollisions(final CollidableSprite sprite) {
        final Sector tl = getData().getSectorOf(((BaseSprite) sprite).getX(), ((BaseSprite) sprite).getY());
        final Sector br = getData().getSectorOf(((BaseSprite) sprite).getX() + ((BaseSprite) sprite).getWidth(),
                ((BaseSprite) sprite).getY() + ((BaseSprite) sprite).getHeight());
        tl.checkCollisionsFor(sprite);

        if (tl.leftNeighbor != null) {
            tl.leftNeighbor.checkCollisionsFor(sprite);
            if (tl.topNeighbor != null) {
                tl.topNeighbor.leftNeighbor.checkCollisionsFor(sprite);
            }
        }
        if (tl.topNeighbor != null) {
            tl.topNeighbor.checkCollisionsFor(sprite);
        }

        if (tl != br) {
            if (tl.bottomNeighbor.rightNeighbor == br) {
                //check them all
                br.checkCollisionsFor(sprite);
                if (br.leftNeighbor != null) {
                    br.leftNeighbor.checkCollisionsFor(sprite);
                }
                if (tl.rightNeighbor != null) {
                    tl.rightNeighbor.checkCollisionsFor(sprite);
                    if (tl.rightNeighbor.topNeighbor != null) {
                        tl.rightNeighbor.topNeighbor.checkCollisionsFor(sprite);
                    }
                }
            } else if (br == tl.bottomNeighbor) {
                if (br.leftNeighbor != null) {
                    br.leftNeighbor.checkCollisionsFor(sprite);
                }
            } else {
                if (br.topNeighbor != null) {
                    br.topNeighbor.checkCollisionsFor(sprite);
                }
            }
        }
    }

    /**
     * Gets the maximum width for a sprite.
     * return the maximum width for a sprite
     */
    public final int maximumSpriteWidth() {
        return getData().getSectorSize();
    }

    /**
     * Gets the maximum height for a sprite.
     * return the maximum height for a sprite
     */
    public final int maximumSpriteHeight() {
        return getData().getSectorSize();
    }

    /**
     * Method empty, for implementing as needed. Called when the Zone starts.
     */
    @Override
    public void init() {
    }

    /**
     * Sets the width of this Zone to the given value.
     * @param width the new width of the Zone
     */
    protected final void setWidth(final int width) {
        size.setWidth(width);
        refactorSectors();
    }

    /**
     * Sets the height of this Zone to the given value.
     * @param height the new height of the Zone
     */
    protected final void setHeight(final int height) {
        size.setHeight(height);
        refactorSectors();
    }

    /**
     * Sets the width and height of this Zone.
     * @param width the new width of the Zone
     * @param height the new height of the Zone
     */
    protected final void setSize(final int width, final int height) {
        size.setSize(width, height);
        refactorSectors();
    }

    /**
     * Return the amount of time the zone has been running minus the pause time.
     * @return the amount of time the zone has been running minus the pause time
     */
    protected final long time() {
        return getData().zoneTime;
    }

    /**
     * Passes the given data to the Zone and its sprites so that an extra pointer
     * does not have to be assigned to each sprite to reduce memory consumption.
     * @param sharedData the object containing the data to be shared with every object in the Zone
     */
    protected final void setSharedZoneData(final Object sharedData) {
        getData().data = sharedData;
    }

    /**
     * Abstract method for updating the zone to be implemented by the developer.
     * Note: This method is not called while the Zone is paused.
     */
    protected void update() {
    }

    /**
     * A stub method for listening to clicks. Implement as needed.
     * @param event contains data about the input
     */
    protected void buttonClicked(final ClickEvent event) {
    }

    /**
     * A stub method for listening to mouse button presses. Implement as needed.
     * @param event contains data about the input
     */
    protected void buttonPressed(final MouseEvent event) {
    }

    /**
     * A stub method for listening to mouse button releases. Implement as needed.
     * @param event contains data about the input
     */
    protected void buttonReleased(final MouseEvent event) {
    }

    /**
     * A stub method for listening to the mouse wheel. Implement as needed.
     * @param event contains data about the input
     */
    protected void mouseWheelScroll(final MouseWheelEvent event) {
    }

    /**
     * A stub method for listening to the keys pressed. Implement as needed.
     * @param event contains data about the input
     */
    protected void keyPressed(final KeyEvent event) {
    }

    /**
     * A stub method for listening to the keys released. Implement as needed.
     * @param event contains data about the input
     */
    protected void keyReleased(final KeyEvent event) {
    }

    /**
     * A stub method for listening to the key typed. Implement as needed.
     * @param event contains data about the input
     */
    protected void keyTyped(final KeyEvent event) {
    }

    /**
     * Adds the given camera to this Zone, to monitor the sprites in its area.
     * @param camera
     */
    final void addCamera(final BaseCamera camera) {
        camerasToAdd.add(camera);
    }

    /**
     * Removes the given camera from this Zone.
     * @param camera the camera to remove
     */
    final void removeCamera(final BaseCamera camera) {
        cameras.remove(camera);
    }

    /**
     * Trims excessively large lists.
     */
    final void trimLists() {
        getData().trimLists();
        cameras.trimToSize();
    }

    /**
     * Adds new Cameras to this Zone.
     */
    private final void addCameras() {
        for (int i = 0; i < camerasToAdd.size(); i++) {
            cameras.add(camerasToAdd.get(i));
            getData().registerZoneObject(camerasToAdd.remove(i));
        }
    }

    /**
     * Deletes the given sprite from this Zone.
     * @param sprite the sprite to remove from the Zone
     */
    private final void removeSprite(final BaseSprite sprite) {
        final Sector tl = getData().getSectorOf(sprite.getX(), sprite.getY());
        tl.removeSprite(sprite);
        sprite.onDelete();
        sprite.dropZone(this);
    }

    /**
     * Refactors the Sectors to fit the current size of this Zone.
     */
    private final synchronized void refactorSectors() {
        if (size.width == 0 || size.height == 0) {
            return;
        }

        if (size.width / getData().getSectorSize() + 1 <= getData().sectors.length
                || size.height / getData().getSectorSize() + 1 <= getData().sectors[0].length) {
            return;
        }

        ArrayList<Sector> newSectors = new ArrayList<Sector>();

        Sector[][] sectors =
                new Sector[size.width / getData().getSectorSize() + 1][size.height / getData().getSectorSize() + 1];
        for (int i = 0; i < sectors.length; i++) {
            for (int n = 0; n < sectors[i].length; n++) {
                if (i >= getData().sectors.length || n >= getData().sectors[i].length) {
                    sectors[i][n] = new Sector(getData().sectorWidth() * i, getData().sectorHeight() * n);
                    newSectors.add(sectors[i][n]);
                } else {
                    sectors[i][n] = getData().sectors[i][n];
                }
            }
        }

        getData().sectors = sectors;

        for (int i = 0; i < newSectors.size(); i++) {
            getData().registerZoneObject(newSectors.get(i));
        }

        for (int i = 0; i
                < sectors.length; i++) {
            for (int n = 0; n < sectors[i].length; n++) {
                int ix = sectors[i][n].getX() / getData().getSectorSize();
                int iy = sectors[i][n].getY() / getData().getSectorSize();

                if (ix > 0) {
                    sectors[i][n].leftNeighbor = getData().sectors[ix - 1][iy];
                } else {
                    sectors[i][n].leftNeighbor = null;
                }
                if (iy > 0) {
                    sectors[i][n].topNeighbor = getData().sectors[ix][iy - 1];
                } else {
                    sectors[i][n].topNeighbor = null;
                }
                if (ix < getData().sectors.length - 1) {
                    sectors[i][n].rightNeighbor = getData().sectors[ix + 1][iy];
                } else {
                    sectors[i][n].rightNeighbor = null;
                }
                if (iy < getData().sectors[ix].length - 1) {
                    sectors[i][n].bottomNeighbor = getData().sectors[ix][iy + 1];
                } else {
                    sectors[i][n].bottomNeighbor = null;
                }
            }

        }
        if (zoneProcessor != null) {
            generateSectorGroups(sectors);
        }
    }

    /**
     * Used to help concurrently update the Cameras.
     * @return the next Camera to be updated
     */
    private final synchronized BaseCamera nextCamera() {
        try {
            return cameras.get(currentCamera++);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Divides the Sectors into groups for the threads to manage.
     * @param sectors the lists of Sectors
     */
    private final synchronized void generateSectorGroups(final Sector[][] sectors) {
        final int sectorsPerThread = (sectors.length * sectors[0].length) / zoneProcessor.getThreadCount();
        final Position index = new Position();

        final ArrayList<Sector> group = new ArrayList<Sector>();
        final ArrayList<UpdateSectors> updates = new ArrayList<UpdateSectors>();
        final ArrayList<PostUpdateSectors> postUpdates = new ArrayList<PostUpdateSectors>();
        int sectorCount = 0;
        while (index.x < sectors.length) {
            while (index.y < sectors[index.x].length) {
                group.add(sectors[index.x][index.y]);
                sectorCount++;
                if (group.size() == sectorsPerThread) {
                    updates.add(new UpdateSectors(group));
                    postUpdates.add(new PostUpdateSectors(group));
                    group.clear();
                }
                index.y++;
            }
            index.y = 0;
            index.x++;
        }
        if (!group.isEmpty()) {
            updates.add(new UpdateSectors(group));
            postUpdates.add(new PostUpdateSectors(group));
        }
        sectorUpdate.clear();
        for (int i = 0; i < updates.size(); i++) {
            sectorUpdate.add(updates.get(i));
        }
        sectorPostUpdate.clear();
        for (int i = 0; i < postUpdates.size(); i++) {
            sectorPostUpdate.add(postUpdates.get(i));
        }
    }
}
