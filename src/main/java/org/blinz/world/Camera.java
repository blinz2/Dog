/*
 *  BlinzEngine - A library for large 2D world simultions and games.
 *  Copyright (C) 2009 - 2010  Blinz <gtalent2@gmail.com>
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

import java.util.ArrayList;
import org.blinz.util.User;
import org.blinz.graphics.Graphics;
import org.blinz.util.Bounds;
import org.blinz.input.MouseListener;
import java.util.Hashtable;
import java.util.Vector;
import org.blinz.input.ClickEvent;
import org.blinz.input.KeyEvent;
import org.blinz.input.KeyListener;
import org.blinz.input.MouseEvent;
import org.blinz.input.MouseWheelEvent;
import org.blinz.input.MouseWheelListener;
import org.blinz.util.Position;
import org.blinz.world.UserListenerCatalog.UserListenerList;

/**
 * Camera acts as an interface between a the user, and the Zone.
 * It delivers all necessary images to the screena and allows input to travel to
 * the Zone.
 * @author Blinz
 */
public class Camera extends ZoneObject {

    /**
     * Represents the bounds of this Camera during the last round.
     */
    private final Bounds oldBounds = new Bounds();
    private CameraSprite selected;
    /**
     * Used to keep track of how click inputs affect the selected sprite.
     */
    private final Vector<Position> selections = new Vector<Position>();
    private final Vector<Sector> sectors = new Vector<Sector>();
    private final Vector<CameraSprite> orphanedSprites = new Vector<CameraSprite>();
    private final Vector<CameraSprite> spriteList = new Vector<CameraSprite>();
    private final Hashtable<Sector, Vector<CameraSprite>> spriteTable =
	    new Hashtable<Sector, Vector<CameraSprite>>();
    private final Bounds bounds = new Bounds();
    private Zone zone;
    private User user;
    private Scene scene = new Scene();
    private Scene swap1 = new Scene();
    private Scene swap2 = new Scene();
    private InputListener inputListener;
    private UserListenerList userListeners;

    /**
     * Constructer for Camera.
     */
    public Camera() {
	this(new User());
    }

    /**
     * Creates a new Camera with the given User as this Camera's User.
     * @param user
     */
    public Camera(final User user) {
	this.user = user;
    }

    /**
     *
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
	    spriteTable.clear();
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
     * @param width
     */
    public final void setWidth(final int width) {
	bounds.width = width;
    }

    /**
     * Sets the height of this Camera to that given.
     * @param height
     */
    public final void setHeight(final int height) {
	bounds.height = height;
    }

    /**
     * Sets the x coordinate of this Camera to the given value.
     * @param x
     */
    public final void setX(final int x) {
	bounds.x = x;
    }

    /**
     * Sets the y coordinate of this Camera to the given value.
     * @param y
     */
    public final void setY(final int y) {
	bounds.y = y;
    }

    /**
     * Sets the location of this Camera.
     * @param x
     * @param y
     */
    public final void setPosition(final int x, final int y) {
	setX(x);
	setY(y);
    }

    /**
     * Moves this Camera up the specified distance.
     * @param distance
     */
    public final void moveUp(final int distance) {
	bounds.y = -distance;
    }

    /**
     * Moves this Camera down the specified distance.
     * @param distance
     */
    public final void moveDown(final int distance) {
	bounds.y += distance;
    }

    /**
     * Moves this Camera right the specified distance.
     * @param distance
     */
    public final void moveRight(final int distance) {
	bounds.x += distance;
    }

    /**
     * Moves this Camera left the specified distance.
     * @param distance
     */
    public final void moveLeft(final int distance) {
	bounds.x -= distance;
    }

    /**
     * Draws this Camera. A single given Camera should only be drawn by one
     * thread at a time, and thus by only one Screen.
     * @param graphics
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
     * Called after the Camera recieves a Zone. Does nothing, for implementing
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
		sectors.add(s);
		spriteTable.put(s, new Vector<CameraSprite>());
		final UnorderedList<BaseSprite> list = s.getSprites();
		for (int i = 0; i < list.size(); i++) {
		    addSprite(list.get(i), s);
		}
	    }
	}
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
	final int end = selections.size() - 1;
	while (!selections.isEmpty()) {
	    final int x = selections.get(end).x;
	    final int y = selections.get(end).y;
	    final CameraSprite oldSelected = selected;


	    CameraSprite newSelected = null;
	    for (int i = spriteList.size() - 1; i > -1; i--) {
		final BaseSprite s = spriteList.get(i).getSprite();
		if (Bounds.intersects(s.getX(), s.getY(), s.getWidth(), s.getHeight(), x + getX(), y + getY(), 1, 1)) {
		    if (newSelected == null || newSelected.getLayer() < spriteList.get(i).getLayer()) {
			newSelected = spriteList.get(i);
		    }
		    break;
		}
	    }

	    if (newSelected != oldSelected) {
		selected = newSelected;
		if (newSelected != null && newSelected.isSelectable()) {
		    newSelected.select(user);
		}
		if (oldSelected != null && oldSelected.isSelectable()) {
		    oldSelected.deselect(user);
		}
	    }
	}
    }

    /**
     * Lists the given sprite as an orphan and removes it from its Sector.
     * @param sprite the orphaned sprite
     * @param sector the Sector of the now orphaned sprite
     */
    private final void orphanSprite(final BaseSprite sprite, final Sector sector) {
	final Vector<CameraSprite> list = spriteTable.get(sector);
	for (int i = 0; i < list.size(); i++) {
	    if (list.get(i).getSprite() == sprite) {
		orphanedSprites.add(list.get(i));
		list.remove(i).setSector(null);
		break;
	    }
	}
    }

    /**
     * Adds the given sprite to this Camera for the given Sector.
     * @param sprite the sprite to be added
     * @param sector the Sector of the sprite to be added
     */
    private final void addSprite(final BaseSprite sprite, final Sector sector) {
	for (int i = 0; i < orphanedSprites.size(); i++) {
	    if (orphanedSprites.get(i).getSprite() == sprite) {
		final CameraSprite cs = orphanedSprites.remove(i);
		i--;
		cs.setSector(sector);
		spriteTable.get(sector).add(cs);
		return;
	    }
	}
	final CameraSprite cs = new CameraSprite(sprite);
	cs.setSector(sector);
	spriteTable.get(sector).add(cs);
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

    /**
     * Updates the sprites in this Camera.
     */
    private final void updateSprites() {
	//manage sprites for current sectors
	//find and declare orphaned sprites
	for (int i = 0; i < sectors.size(); i++) {
	    final Sector sector = sectors.get(i);
	    final ArrayList<BaseSprite> list = sector.getRemovedSprites();
	    for (int r = 0; r < list.size(); r++) {
		orphanSprite(list.get(r), sector);
	    }
	}

	//add new sprites
	for (int i = 0; i < sectors.size(); i++) {
	    final Sector sector = sectors.get(i);
	    final ArrayList<BaseSprite> list = sectors.get(i).getAddedSprites();
	    for (int r = 0; r < list.size(); r++) {
		addSprite(list.get(r), sector);
	    }
	}

	//update the bounds
	if (bounds.x == oldBounds.x && bounds.y == oldBounds.y
		&& bounds.width == oldBounds.width && bounds.height == oldBounds.height) {
	    //update the Sectors
	    //remove old Sectors
	    for (int i = 0; i < sectors.size(); i++) {
		if (!sectors.get(i).withinSpriteRange(bounds.x, bounds.y)) {
		    final UnorderedList<BaseSprite> list = sectors.get(i).getSprites();
		    for (int n = 0; n < list.size(); n++) {
			orphanSprite(list.get(n), sectors.get(i));
		    }
		    spriteTable.remove(sectors.get(i));
		    sectors.remove(i);
		    i--;
		}
	    }

	    //add new Sectors
	    final int x1 = sector1().leftNeighbor.getX();
	    final int y1 = sector1().topNeighbor.getY();
	    final int x2 = sector2().getX();
	    final int y2 = sector2().getY();

	    for (int x = x1; x < x2; x += getData().sectorWidth()) {
		for (int y = y1; y < y2; y += getData().sectorHeight()) {
		    final Sector s = getData().getSectorOf(x, y);
		    if (!s.withinSpriteRange(oldBounds.x, oldBounds.y)) {
			sectors.add(s);
			final UnorderedList<BaseSprite> list = s.getSprites();
			for (int i = 0; i < list.size(); i++) {
			    addSprite(list.get(i), s);
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
		spriteList.remove(i);
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
	upcoming.manageContainers();

	upcoming.translation.setPosition(getX(), getY());
	upcoming.size.setSize(bounds.width, bounds.height);
	final Bounds b = new Bounds();
	b.setPosition(upcoming.translation);
	b.setSize(upcoming.size);
	for (int i = 0; i < spriteList.size(); i++) {
	    final CameraSprite s = spriteList.get(i);
	    if (b.intersects(s.getX(), s.getY(), s.getWidth(), s.getHeight())) {
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
	retval.size.setSize(getWidth(), getHeight());
	return retval;
    }

    /**
     * Returns Sector 1 of this Camera, makes sure the indices are safe.
     * @return Sector of the upper left hand corner of this Camera.
     */
    private final Sector sector1() {
	return getData().getSectorOfSafe(bounds.x, bounds.y);
    }

    /**
     * Returns Sector 2 of this Camera, makes sure the indices are safe.
     * @return Sector of the lower right hand corner of this Camera.
     */
    private final Sector sector2() {
	return getData().getSectorOfSafe(bounds.x2(), bounds.y2());
    }

    private final class InputListener implements MouseListener, MouseWheelListener, KeyListener {

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
	protected void finalize() {
	    getData().userListeners.checkIn(user);
	}
    }
}
