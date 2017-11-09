package ds.tetris.fx.util

import org.junit.Assert
import org.junit.Test

class ColorsTest {

    @Test
    fun toColor() {
        val c = 0xA1B2C3.toColor()
        Assert.assertEquals((c.red * 255).toInt(), 0xA1)
        Assert.assertEquals((c.green * 255).toInt(), 0xB2)
        Assert.assertEquals((c.blue * 255).toInt(), 0xC3)
    }

}