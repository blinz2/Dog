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
package net.blinz.dog.world;

import java.util.HashMap;
import java.util.Vector;
import net.blinz.core.graphics.Graphics;
import net.blinz.core.input.KeyListener;
import net.blinz.core.input.MouseListener;
import net.blinz.core.input.MouseWheelListener;
import net.blinz.core.util.Bounds;
import net.blinz.core.util.Position;
import net.blinz.dog.input.ClickEvent;
import net.blinz.dog.input.KeyEvent;
import net.blinz.dog.input.MouseEvent;
import net.blinz.dog.input.MouseWheelEvent;
import net.blinz.dog.util.User;
import net.blinz.dog.world.SelectableSprite.Selection;
import net.blinz.dog.world.UserListenerCatalog.UserListenerList;

/**
 * Camera acts as an interface between a the user, and the Zone.
 * It delivers all necessary images to the screen and allows input to travel to
 * the Zone and appropriate sprites.
 * @author Blinz
 */
public class Camera extends ZoneObject {

    /**
     * Tracks the Sprites in a given Sector that the Camera occupies.
     */
    private final class CameraSector {

        private final UnorderedList<CameraSprite> sprites = new UnorderedList<CameraSprite>();
        private Sector sector;

        /**
         * Constructor
         * @param sector the associated Sector
         */
        CameraSector(final Sector sector) {
            this.sector = sector;
        }

        /**
         * Adds sprites new to this Sector that this CameraSector represents to
         * this Camera.
         */
        private final void addNewSprites() {
            final Vector<BaseSprite> list = sector.getAddedSprites();
            for (int n = 0; n < list.size(); n++) {
                addSprite(list.get(n));
            }
        }

        /**
         * Declares all of the sprites in this CameraSector orphans.
         */
        private final void orphanSprites() {
            while (!sprites.isEmpty()) {
                orphanedSprites.put(sprites.get(0).getSprite(), sprites.get(0));
                sprites.remove(0).setSector(null);
            }
        }

        /**
         * Finds, removes, and declares the sprites that should no longer represent
         * this sector.
         */
        private final void orphanRemovedSprites() {
            final Vector<BaseSprite> list = sector.getRemovedSprites();
            for (int n = 0; n < list.size(); n++) {
                final BaseSprite sprite = list.get(n);
                for (int i = 0; i < sprites.size(); i++) {
                    if (sprites.get(i).getSprite() == sprite) {
                        orphanedSprites.put(sprites.get(i).getSprite(), sprites.get(i));
                        sprites.remove(i).setSector(null);
                        break;
                    }
                }
            }
        }

        /**
         * Adds the given sprite to this CameraSector and to the Camera if necessary.
         * @param sprite the sprite to be added
         */
        private final void addSprite(final BaseSprite sprite) {
            CameraSprite cs = null;
            //recover the sprite's orphaned representation if it exists
            cs = orphanedSprites.remove(sprite);
            spriteMap.put(sprite, cs);
            if (cs != null) {
                cs.setSector(sector);
            } else {
                cs = new CameraSprite(sprite, sector);
                //add the sprite to sprite list at the proper location
                synchronized (spriteList) {
                    if (spriteList.isEmpty()) {
                        spriteList.add(cs);
                        return;
                    }
                    for (int i = spriteList.size() - 1; i > -1; i--) {
                        if (spriteList.get(i).getLayer() <= sprite.getLayer()) {
                            spriteList.insertElementAt(cs, i + 1);
                            break;
                        }
                    }
                }
            }
            //add the sprite to the list representing its Sector
            sprites.add(cs);
        }

        /**
         * Adds the sprites already existing within the Sector.
         */
        private final void addSprites() {
            final UnorderedList<BaseSprite> list = sector.getSprites();
            for (int n = 0; n < list.size(); n++) {
                addSprite(list.get(n));
            }
        }
    }
    /**
     * Represents the bounds of this Camera during the last round.
     */
    private final Bounds oldBounds = new Bounds();
    private CameraSprite selected;
    /**
     * Used to keep track of how click inputs affect the selected sprite.
     */
    private final Vector<Position> selections = new Vector<Position>();
    private final Vector<CameraSector> sectors = new Vector<CameraSector>();
    private final HashMap<BaseSprite, CameraSprite> orphanedSprites = new HashMap<BaseSprite, CameraSprite>();
    private final HashMap<BaseSprite, CameraSprite> spriteMap = new HashMap<BaseSprite, CameraSprite>();
    private final Vector<CameraSprite> spriteList = new Vector<CameraSprite>();
    private final Bounds bounds = new Bounds();
    private Zone zone;
    private User user;
    private Scene scene = new Scene();
    private Scene swap1 = new Scene();
    private Scene swap2 = new Scene();
    private InputListener inputListener;
    private UserListenerList userListeners;

    /**
     * Constructor for Camera.
     */
    public Camera() {
        this(new User());
    }

    /**
     * Constructor
     * @param user the User associated with this Camera
     */
    public Camera(final User user) {
        this.user = user;
    }

    /**
     * Gets the User associated with this Camera.
     * @return the User that this Camera represents.
     */
    public final User getUser() {
        return user;
    }

    /**
     * Returns an input listener Used to direct input by the User owning this 
     * Camera to sprites.
     * @return an Mouse, MouseWheel, Key listener object
     */
    public final synchronized Object getInputListener() {
        return inputListener == null ? inputListener = new InputListener() : inputListener;
    }

    /**
     * Sets the zone of this Camera. In addition to setting the Zone it also
     * removes the old Zone.
     * @param zone
     */
    public final synchronized void setZone(final Zone zone) {
        dropZone();
        this.zone = zone;
        zone.addCamera(this);
    }

    /**
     * Drops the current zone, the Camera will have no Zone to moniter after
     * this method is called.
     */
    public final synchronized void dropZone() {
        if (zone != null) {
            zone.removeCamera(this);
            getData().userListeners.checkIn(user);
            zone = null;
            inputListener = null;
        }
    }

    /**
     * Gets the x coordinate of this Camera
     * @return the x location of this Camera
     */
    public final int getX() {
        return bounds.x;
    }

    /**
     * Gets the y coordinate of this Camera
     * @return the y location of this Camera
     */
    public final int getY() {
        return bounds.y;
    }

    /**
     * Gets the width of this Camera.
     * @return the width of this Camera
     */
    public final int getWidth() {
        return bounds.width;
    }

    /**
     * Gets the height of this Camera.
     * @return the height of this Camera
     */
    public final int getHeight() {
        return bounds.height;
    }

    /**
     * Sets the size of this Camera in the Zone.
     * @param width the new width of this Camera
     * @param height the new height of this Camera
     */
    public final void setSize(final int width, final int height) {
        setWidth(width);
        setHeight(height);
    }

    /**
     * Sets the width of this Camera to that given.
     * @param width the new width of this Camera
     */
    public final void setWidth(final int width) {
        bounds.width = width;
    }

    /**
     * Sets the height of this Camera to that given.
     * @param height the new height of this Camera
     */
    public final void setHeight(final int height) {
        bounds.height = height;
    }

    /**
     * Sets the x coordinate of this Camera to the given value.
     * @param x the new x coordinate of this Camera
     */
    public final void setX(final int x) {
        bounds.x = x;
    }

    /**
     * Sets the y coordinate of this Camera to the given value.
     * @param y the new y coordinate of this Camera
     */
    public final void setY(final int y) {
        bounds.y = y;
    }

    /**
     * Sets the location of this Camera.
     * @param x the new x coordinate of this Camera
     * @param y the new y coordinate of this Camera
     */
    public final void setPosition(final int x, final int y) {
        setX(x);
        setY(y);
    }

    /**
     * Moves this Camera up the specified distance.
     * @param distance the distance that this Camera is to move
     */
    public final void moveUp(final int distance) {
        bounds.y = -distance;
    }

    /**
     * Moves this Camera down the specified distance.
     * @param distance the distance that this Camera is to move
     */
    public final void moveDown(final int distance) {
        bounds.y += distance;
    }

    /**
     * Moves this Camera right the specified distance.
     * @param distance the distance that this Camera is to move
     */
    public final void moveRight(final int distance) {
        bounds.x += distance;
    }

    /**
     * Moves this Camera left the specified distance.
     * @param distance the distance that this Camera is to move
     */
    public final void moveLeft(final int distance) {
        bounds.x -= distance;
    }

    /**
     * Draws this Camera. A single given Camera should only be drawn by one
     * thread at a time, and thus by only one Screen.
     * @param graphics the Graphics object with which this is to be drawn
     */
    public synchronized final void draw(final Graphics graphics) {
        Scene s = scene;
        while (!s.lock()) {
            s = scene;
        }
        s.draw(graphics);
        s.unLock();
    }

    /**
     * Called after each cycle of this Camera's Zone. Does nothing, for implementing
     * as needed.
     */
    protected void update() {
    }

    /**
     * Called after the Camera receives a Zone. Does nothing, for implementing
     * as needed.
     */
    @Override
    protected void init() {
    }

    @Override
    final void internalInit() {
        userListeners = getData().userListeners.checkOut(user);
        //add relevant Sectors
        final int x1 = sector1().leftNeighbor != null ? sector1().leftNeighbor.getX() : 0;
        final int y1 = sector1().topNeighbor != null ? sector1().topNeighbor.getY() : 0;
        final int x2 = bounds.x2();
        final int y2 = bounds.y2();
        for (int x = x1; x < x2; x += getData().sectorWidth()) {
            for (int y = y1; y < y2; y += getData().sectorHeight()) {
                final Sector s = getData().getSectorOf(x, y);
                addSector(s);
            }
        }
        oldBounds.setBounds(bounds);
        init();
    }

    /**
     * Updates the Camera.
     */
    final void internalUpdate() {
        update();
        updateSprites();
        processSpriteSelection();
        generateCurrentScene();
    }

    /**
     * Processes all sprite selection from this Camera within the last round.
     */
    private final void processSpriteSelection() {
        while (!selections.isEmpty()) {

            final Position selection = selections.remove(selections.size() - 1);
            final int x = selection.x;
            final int y = selection.y;
            final CameraSprite oldSelected = selected;


            CameraSprite newSelected = null;
            for (int i = spriteList.size() - 1; i > -1; i--) {
                final BaseSprite s = spriteList.get(i).getSprite();
                if (Bounds.intersects(s.getX(), s.getY(), s.getWidth(), s.getHeight(), x + getX(), y + getY(), 1, 1)) {
                    newSelected = spriteList.get(i);
                    if (newSelected.select(user) == Selection.ACCEPT) {
                        selected = newSelected;
                        break;
                    } else if (newSelected.select(user) == Selection.REJECT_STOP) {
                        break;
                    }
                }
            }
            if (oldSelected != null && oldSelected != newSelected) {
                oldSelected.deselect(user);
            }
        }
    }

    /**
     * Adds the given Sector to the necessary structures.
     * @param sector the Sector to be added
     */
    private final void addSector(final Sector sector) {
        final CameraSector cs = new CameraSector(sector);
        cs.addSprites();
        sectors.add(cs);
    }

    /**
     * Removes the given Sector from the necessary structures.
     * @param sectorIndex the location of the Sector in the sectors list
     */
    private final void removeSector(final int sectorIndex) {
        sectors.remove(sectorIndex).orphanSprites();
    }

    /**
     * Updates the sprites in this Camera.
     */
    private final void updateSprites() {
        //manage sprites for current sectors
        //find and declare orphaned sprites
        for (int i = 0; i < sectors.size(); i++) {
            sectors.get(i).orphanRemovedSprites();
        }

        //add new sprites
        for (int i = 0; i < sectors.size(); i++) {
            sectors.get(i).addNewSprites();
        }

        //update Sectors
        if (bounds.x != oldBounds.x || bounds.y != oldBounds.y
                || bounds.width != oldBounds.width || bounds.height != oldBounds.height) {
            //update the Sectors
            //remove old Sectors
            for (int i = 0; i < sectors.size(); i++) {
                if (!sectors.get(i).sector.withinSpriteRange(bounds)) {
                    removeSector(i);
                    i--;
                }
            }
            if (bounds.width > 0 && bounds.height > 0) {
                //add new Sectors
                final int x1 = sector1().leftNeighbor != null ? sector1().leftNeighbor.getX() : 0;
                final int y1 = sector1().topNeighbor != null ? sector1().topNeighbor.getY() : 0;
                final int x2 = sector2().getX();
                final int y2 = sector2().getY();

                for (int x = x1; x <= x2; x += getData().sectorWidth()) {
                    for (int y = y1; y <= y2; y += getData().sectorHeight()) {
                        final Sector s = getData().getSectorOf(x, y);
                        if (!s.withinSpriteRange(oldBounds)) {
                            addSector(s);
                        }
                    }
                }
            }
            //update oldBounds
            oldBounds.setBounds(bounds);
        }

        //remove remaining orphans from spriteList
        for (int i = 0; i < spriteList.size(); i++) {
            if (spriteList.get(i).getSector() == null) {
                spriteMap.remove(spriteList.remove(i).getSprite());
                i--;
            }
        }
        orphanedSprites.clear();
    }

    /**
     * Finds the sprite currently on screen, gathers them, orders them by layer
     * and sets it to the current scene.
     */
    private final void generateCurrentScene() {
        final Scene upcoming = getScene();
        upcoming.manageSceneSprites();

        upcoming.setTranslation(getX(), getY());
        upcoming.setSize(getWidth(), getHeight());
        for (int i = 0; i < spriteList.size(); i++) {
            final CameraSprite s = spriteList.get(i);
            if (Bounds.intersects(s.getX(), s.getY(), s.getWidth(), s.getHeight(),
                    upcoming.translationX, upcoming.translationY, upcoming.width, upcoming.height)) {
                upcoming.add(s);
            }
        }

        upcoming.sortLayers();
        upcoming.unLock();
        scene = upcoming;
    }

    /**
     * Gets a Scene that is safe to write to.
     * @return a Scene that is safe to write to
     */
    private final Scene getScene() {
        Scene retval = null;
        while (retval == null) {
            if (swap1.lock()) {
                retval = swap1;
            } else if (swap2.lock()) {
                retval = swap2;
            }
        }
        retval.width = getWidth();
        retval.height = getHeight();
        return retval;
    }

    /**
     * Gets Sector 1 of this Camera, makes sure the indices are safe.
     * @return Sector of the upper left hand corner of this Camera.
     */
    private final Sector sector1() {
        return getData().getSectorOfSafe(bounds.x, bounds.y);
    }

    /**
     * Gets Sector 2 of this Camera, makes sure the indices are safe.
     * @return Sector of the lower right hand corner of this Camera.
     */
    private final Sector sector2() {
        return getData().getSectorOfSafe(bounds.x2(), bounds.y2());
    }

    /**
     * Class used to read all input for the Camera.
     */
    private final class InputListener implements MouseListener, MouseWheelListener, KeyListener {

        /**
         * Constructor
         */
        private InputListener() {
        }

        @Override
        public void buttonClick(int buttonNumber, int clickCount, int cursorX, int cursorY) {
            final ClickEvent e = new ClickEvent(user, buttonNumber,
                    cursorX + getX(), cursorY + getY(), clickCount);
            userListeners.buttonClick(e);
            selections.add(new Position(cursorX, cursorY));
        }

        @Override
        public void buttonPress(int buttonNumber, int cursorX, int cursorY) {
            final MouseEvent e = new MouseEvent(user, buttonNumber, cursorX + getX(), cursorY + getY());
            userListeners.buttonPress(e);
        }

        @Override
        public void buttonRelease(int buttonNumber, int cursorX, int cursorY) {
            final MouseEvent e = new MouseEvent(user, buttonNumber, cursorX + getX(), cursorY + getY());
            userListeners.buttonRelease(e);
        }

        @Override
        public void wheelScroll(int number, int cursorX, int cursorY) {
            final MouseWheelEvent e = new MouseWheelEvent(user, number, cursorX + getX(), cursorY + getY());
            userListeners.wheelScroll(e);
        }

        @Override
        public void keyPressed(int key) {
            final KeyEvent e = new KeyEvent(user, key);
            userListeners.keyPressed(e);
        }

        @Override
        public void keyReleased(int key) {
            final KeyEvent e = new KeyEvent(user, key);
            userListeners.keyReleased(e);
        }

        @Override
        public void keyTyped(int key) {
            final KeyEvent e = new KeyEvent(user, key);
            userListeners.keyTyped(e);
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            getData().userListeners.checkIn(user);
        }
    }
}
