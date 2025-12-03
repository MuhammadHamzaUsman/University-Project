package com.example.shape;

import com.example.TextEditor.Interpreter.interpreter.RuntimeError;
import com.jme3.math.ColorRGBA;

public enum Colors {
    WHITE (ColorRGBA.fromRGBA255(223, 233, 245, 255)),
    GRAY (ColorRGBA.fromRGBA255(104, 117, 149, 255)),
    BLACK (ColorRGBA.fromRGBA255(16, 21, 24, 255)),
    SKIN (ColorRGBA.fromRGBA255(248, 170, 168, 255)),
    PINK (ColorRGBA.fromRGBA255(212, 104, 154, 255)),
    PURPLE (ColorRGBA.fromRGBA255(120, 45, 150, 255)),
    RED (ColorRGBA.fromRGBA255(233, 53, 98, 255)),
    ORANGE (ColorRGBA.fromRGBA255(242, 130, 92, 255)),
    YELLOW (ColorRGBA.fromRGBA255(255, 200, 110, 255)),
    LIGHT_GREEN (ColorRGBA.fromRGBA255(136, 195, 77, 255)),
    DARK_GREEN (ColorRGBA.fromRGBA255(63, 158, 90, 255)),
    DARK_BLUE (ColorRGBA.fromRGBA255(55, 52, 97, 255)),
    BLUE (ColorRGBA.fromRGBA255(72, 84, 168, 255)),
    LIGHT_BLUE (ColorRGBA.fromRGBA255(113, 152, 217, 255)),
    LIGHT_BROWN (ColorRGBA.fromRGBA255(158, 82, 82, 255)),
    DARK_BROWN (ColorRGBA.fromRGBA255(78, 37, 55, 255));

    private ColorRGBA color;

    private Colors(ColorRGBA color) {
        this.color = color;
    }

    public ColorRGBA getColor() {
        return color;
    }

    public static Colors getColor(int index) throws RuntimeError{
        if(index < 0 || index > 15){ 
            throw new RuntimeError("Interpreter Error: Unkown Color Index: " + index);
        }

        return Colors.values()[index];
    }
}
