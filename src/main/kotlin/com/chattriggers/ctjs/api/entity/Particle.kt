package com.chattriggers.ctjs.api.entity

import com.chattriggers.ctjs.MCParticle
import com.chattriggers.ctjs.api.CTWrapper
import com.chattriggers.ctjs.api.render.GUIRenderer
import com.chattriggers.ctjs.internal.mixins.ParticleAccessor
import com.chattriggers.ctjs.internal.utils.asMixin
import java.awt.Color

class Particle(override val mcValue: MCParticle) : CTWrapper<MCParticle> {
    private val mixed: ParticleAccessor = mcValue.asMixin()

    var x by mixed::x
    var y by mixed::y
    var z by mixed::z

    var lastX by mixed::lastX
    var lastY by mixed::lastY
    var lastZ by mixed::lastZ

    val renderX get() = lastX + (x - lastX) * GUIRenderer.partialTicks
    val renderY get() = lastY + (y - lastY) * GUIRenderer.partialTicks
    val renderZ get() = lastZ + (z - lastZ) * GUIRenderer.partialTicks

    var motionX by mixed::velocityX
    var motionY by mixed::velocityY
    var motionZ by mixed::velocityZ

    //#if MC<=12108
    //$$var red by mixed::red
    //$$var green by mixed::green
    //$$var blue by mixed::blue
    //$$var alpha by mixed::alpha
    //#endif

    var age by mixed::age
    var dead by mixed::dead

    fun scale(scale: Float) = apply {
        mcValue.scale(scale)
    }

    /**
     * Sets the color of the particle.
     * @param red the red value between 0 and 1.
     * @param green the green value between 0 and 1.
     * @param blue the blue value between 0 and 1.
     */
    @Deprecated("Deprecated since mojang does not have a similar method") // for now perhaps
    fun setColor(red: Float, green: Float, blue: Float) = apply {
        //#if MC<=12108
        //$$mcValue.setColor(red, green, blue)
        //#endif
    }

    /**
     * Sets the color of the particle.
     * @param red the red value between 0 and 1.
     * @param green the green value between 0 and 1.
     * @param blue the blue value between 0 and 1.
     * @param alpha the alpha value between 0 and 1.
     */
    @Deprecated("Deprecated since mojang does not have a similar method")
    fun setColor(red: Float, green: Float, blue: Float, alpha: Float) = apply {
        //#if MC<=12108
        //$$setColor(red, green, blue)
        //$$setAlpha(alpha)
        //#endif
    }

    @Deprecated("Deprecated since mojang does not have a similar method")
    fun setColor(color: Long) = apply {
        //#if MC<=12108
        //$$val red = (color shr 16 and 255).toFloat() / 255.0f
        //$$val blue = (color shr 8 and 255).toFloat() / 255.0f
        //$$val green = (color and 255).toFloat() / 255.0f
        //$$val alpha = (color shr 24 and 255).toFloat() / 255.0f
        //$$setColor(red, green, blue, alpha)
        //#endif
    }

    /**
     * Sets the alpha of the particle.
     * @param alpha the alpha value between 0 and 1.
     */
    @Deprecated("Deprecated since mojang does not have a similar method")
    fun setAlpha(alpha: Float) = apply {
//        mixed.alpha = alpha
    }

    /**
     * Returns the color of the Particle
     *
     * @return A [Color] with the R, G, B and A values
     */
    @Deprecated("Deprecated since mojang does not have a similar method")
    fun getColor() = {
        //#if MC<=12108
        //$$Color(red, green, blue, alpha)
        //#endif
    }

    fun setColor(color: Color) = setColor(color.rgb.toLong())

    /**
     * Sets the amount of ticks this particle will live for
     *
     * @param maxAge the particle's max age (in ticks)
     */
    fun setMaxAge(maxAge: Int) = apply {
        mcValue.maxAge = maxAge
    }

    fun remove() = apply {
        mcValue.markDead()
    }

    //#if MC<=12108
    //$$override fun toString() = "Particle(type=${mcValue.javaClass.simpleName}, pos=($x, $y, $z), color=[$red, $green, $blue, $alpha], age=$age)"
    //#else
    override fun toString() = "Particle(type=${mcValue.javaClass.simpleName}, pos=($x, $y, $z), age=$age)"
    //#endif
}
