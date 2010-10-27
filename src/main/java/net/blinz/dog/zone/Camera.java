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

import java.util.ArrayList;
import java.util.Vector;
import net.blinz.core.graphics.Graphics;
import net.blinz.core.util.Bounds;
import net.blinz.dog.util.User;
import net.blinz.dog.zone.BaseCamera;
import net.blinz.dog.zone.BaseSprite;
import net.blinz.dog.zone.CameraSprite;
import net.blinz.dog.zone.SelectableSprite.SelectionResponse;
import net.blinz.dog.zone.SelectionEvent;

/**
 * Camera acts as an interface between a the user, and the Zone.
 * It delivers all necessary images to the screen and allows input to travel to
 * the Zone and appropriate sprites.
 * @author Blinz
 */
public class Camera extends BaseCamera {

    private final Vector<SelectionEvent> selections = new Vector<SelectionEvent>();
    private final Vector<CameraSprite> spriteList = new Vector<CameraSprite>();
    private CameraSprite selected;
    private Scene scene = new Scene();
    private Scene swap1 = new Scene();
    private Scene swap2 = new Scene();

    /**
     * Constructor
     */
    public Camera() {
        this(new User());
    }

    /**
     * Constructor
     * @param user the User associated with this Camera
     */
    public Camera(final User user) {
        super(user);
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

    @Override
    protected void select(final SelectionEvent selection) {
        selections.add(selection);
    }

    @Override
    final void addSprite(final CameraSprite cs) {
        //add the sprite to sprite list at the proper location
        synchronized (spriteList) {
            if (spriteList.isEmpty()) {
                spriteList.add(cs);
                return;
            }
            for (int i = spriteList.size() - 1; i > -1; i--) {
                if (spriteList.get(i).getLayer() <= cs.getLayer()) {
                    spriteList.insertElementAt(cs, i + 1);
                    break;
                }
            }
        }
    }

    @Override
    final void removeOrphanedSprites(final ArrayList<CameraSprite> orphans) {
        //remove remaining orphans from spriteList
        for (int i = 0; i < spriteList.size(); i++) {
            if (spriteList.get(i).isOrphaned()) {
                spriteList.remove(i);
                i--;
            }
        }
    }

    /**
     * Updates the Camera.
     */
    @Override
    final void internalUpdate() {
        super.internalUpdate();
        processSpriteSelection();
        generateCurrentScene();
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
     * Processes all sprite selection from this Camera within the last round.
     */
    private final void processSpriteSelection() {
        while (!selections.isEmpty()) {

            final SelectionEvent selection = selections.remove(selections.size() - 1);
            final int x = selection.getX();
            final int y = selection.getY();
            final CameraSprite oldSelected = selected;


            CameraSprite newSelected = null;
            for (int i = spriteList.size() - 1; i > -1; i--) {
                final BaseSprite s = spriteList.get(i).getSprite();
                if (Bounds.intersects(s.getX(), s.getY(), s.getWidth(), s.getHeight(), x + getX(), y + getY(), 1, 1)) {
                    newSelected = spriteList.get(i);
                    if (newSelected.select(getUser()) == SelectionResponse.ACCEPT) {
                        selected = newSelected;
                        break;
                    } else if (newSelected.select(getUser()) == SelectionResponse.REJECT_STOP) {
                        break;
                    }
                }
            }
            if (oldSelected != null && oldSelected != newSelected) {
                oldSelected.deselect(getUser());
            }
        }
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
}
