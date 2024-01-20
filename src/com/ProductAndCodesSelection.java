package com;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;

public class ProductAndCodesSelection {
    private CheckBox checkBox;
    private ToggleButton toggleButton;

    public ProductAndCodesSelection(CheckBox checkBox, ToggleButton toggleButton) {
        this.checkBox = checkBox;
        this.toggleButton = toggleButton;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public ToggleButton getToggleButton() {
        return toggleButton;
    }

    public void setToggleButton(ToggleButton toggleButton) {
        this.toggleButton = toggleButton;
    }
}
