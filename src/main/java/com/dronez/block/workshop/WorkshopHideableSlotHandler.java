package com.dronez.block.workshop;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class WorkshopHideableSlotHandler extends SlotItemHandler {
    /**
     * The x and y location this slot moves to when hidden
     */
    public static final int HIDDEN_POSITION = -200;

    /**
     * The x location of this slot when shown
     */
    private int desiredX;

    /**
     * The y location of this slot when shown
     */
    private int desiredY;

    public WorkshopHideableSlotHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        desiredX = xPosition;
        desiredY = yPosition;
    }

    /**
     * Hide this slot
     */
    public void hide() {
        xPos = HIDDEN_POSITION;
        yPos = HIDDEN_POSITION;
    }

    /**
     * Show this slot
     */
    public void show() {
        xPos = desiredX;
        yPos = desiredY;
    }
}
