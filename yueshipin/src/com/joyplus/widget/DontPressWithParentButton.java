package com.joyplus.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

/**
 * A special {@link Button} that does not turn into the pressed state when when
 * the parent is already pressed.
 * 
 */
public class DontPressWithParentButton extends Button {

    public DontPressWithParentButton(Context context) {
        super(context);
    }

    public DontPressWithParentButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DontPressWithParentButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setPressed(boolean pressed) {
        // Make sure the parent is a View prior casting it to View
        if (pressed && getParent() instanceof View && ((View) getParent()).isPressed()) {
            return;
        }
        super.setPressed(pressed);
    }

}
