package ch.zhaw.psit3.elfuego.tools;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IsometricMathTest {

    @Test
    public void screenToMapX() {
        assertEquals(IsometricMath.screenToMapX((float) 2560, (float) -1280), (float) 40, 0.5);
        assertEquals(IsometricMath.screenToMapX((float) 2560, (float) 0), (float) 20, 0.5);
        assertEquals(IsometricMath.screenToMapX((float) 2560, (float) 1280), (float) 0, 0.5);
        assertEquals(IsometricMath.screenToMapX((float) 0, (float) 0), (float) 0, 0.5);
        assertEquals(IsometricMath.screenToMapX((float) (5120), (float) 0), (float) 40, 0.5);
    }

    @Test
    public void screenToMapY() {
        assertEquals(IsometricMath.screenToMapY((float) 2560, (float) -1280), (float) 40, 0.5);
        assertEquals(IsometricMath.screenToMapY((float) 2560, (float) 0), (float) 20, 0.5);
        assertEquals(IsometricMath.screenToMapY((float) 2560, (float) 1280), (float) 0, 0.5);
        assertEquals(IsometricMath.screenToMapY((float) 0, (float) 0), (float) 40, 0.5);
        assertEquals(IsometricMath.screenToMapY((float) (5120 - 128), (float) 0), (float) 1, 0.5);
    }

    @Test
    public void mapToScreenX() {
        assertEquals(IsometricMath.mapToScreenX((float) 40, (float) 40), (float) 2560, 128);
        assertEquals(IsometricMath.mapToScreenX((float) 20, (float) 20), (float) 2560, 128);
        assertEquals(IsometricMath.mapToScreenX((float) 0, (float) 0), (float) 2560, 128);
        assertEquals(IsometricMath.mapToScreenX((float) 0, (float) 40), (float) 0, 128);
        assertEquals(IsometricMath.mapToScreenX((float) 40, (float) 1), (float) 5120, 128);
    }

    @Test
    public void mapToScreenY() {
        assertEquals(IsometricMath.mapToScreenY((float) 40, (float) 40), (float) -1280, 128);
        assertEquals(IsometricMath.mapToScreenY((float) 20, (float) 20), (float) 0, 128);
        assertEquals(IsometricMath.mapToScreenY((float) 0, (float) 0), (float) 1280, 128);
        assertEquals(IsometricMath.mapToScreenY((float) 0, (float) 40), (float) 0, 128);
        assertEquals(IsometricMath.mapToScreenY((float) 40, (float) 1), (float) 0, 128);
    }
}