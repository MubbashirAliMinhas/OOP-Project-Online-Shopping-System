package com;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;

public class ProductAndCodes {
    private ToggleButton toggleButton;

    public ProductAndCodes(ToggleButton toggleButton) {
        this.toggleButton = toggleButton;
    }

    public ToggleButton getToggleButton() {
        return toggleButton;
    }

    public void setToggleButton(ToggleButton toggleButton) {
        this.toggleButton = toggleButton;
    }
}
