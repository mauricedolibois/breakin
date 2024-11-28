package com.innoveworkshop.gametest.engine;

public interface Caged {
    /**
     * Checks if a GameObject has hit the left wall.
     *
     * @return True if it has hit the left wall, otherwise False.
     */
    public boolean hitLeftWall();

    /**
     * Checks if a GameObject has hit the right wall.
     *
     * @return True if it has hit the right wall, otherwise False.
     */
    public boolean hitRightWall();

    public boolean hitTopWall();

    /**
     * Checks if a GameObject is on the floor.
     *
     * @return True if it's on the floor, otherwise False.
     */
    public boolean isFloored();
}
