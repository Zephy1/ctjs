package com.chattriggers.ctjs.api.render

import com.chattriggers.ctjs.api.client.Client
import com.chattriggers.ctjs.api.client.Player
import com.chattriggers.ctjs.api.message.ChatLib
import com.chattriggers.ctjs.api.vec.Vec3f
import com.mojang.blaze3d.pipeline.BlendFunction
import com.mojang.blaze3d.platform.DepthTestFunction
import com.mojang.blaze3d.platform.DestFactor
import com.mojang.blaze3d.platform.SourceFactor
import gg.essential.elementa.dsl.component1
import gg.essential.elementa.dsl.component2
import gg.essential.elementa.dsl.component3
import gg.essential.elementa.dsl.component4
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.Tessellator
import org.joml.Quaternionf
import java.awt.Color
import kotlin.math.PI
import kotlin.math.sin
import org.lwjgl.opengl.GL11

//#if MC<=12105
//$$import com.mojang.blaze3d.textures.GpuTexture
//#else
import com.mojang.blaze3d.textures.GpuTextureView
//#endif

//#if MC<=12110
//$$import com.mojang.blaze3d.systems.RenderSystem
//#else
import net.minecraft.client.render.BufferBuilder
//#endif

object RenderUtils {
    @JvmStatic
    fun getFontRenderer() = Client.getMinecraft().textRenderer

    @JvmStatic
    fun getRenderManager() = Client.getMinecraft().worldRenderer

    // WorldRenderer
    private val NEWLINE_REGEX = """\n|\r\n?""".toRegex()
    private var firstVertex = true
    private var began = false
    private val tessellator = Tessellator.getInstance()
    private val ucWorldRenderer = UGraphics.getFromTessellator()

    // GUIRenderer
    @JvmField var colorized: Long? = null

    @JvmField var vertexColor: Color? = null

    // The currently-active matrix stack
    internal lateinit var matrixStack: UMatrixStack
    private val matrixStackStack = ArrayDeque<UMatrixStack>()
    internal var matrixPushCounter = 0

    @JvmField val BLACK = getColor(0, 0, 0, 255)

    @JvmField val DARK_BLUE = getColor(0, 0, 190, 255)

    @JvmField val DARK_GREEN = getColor(0, 190, 0, 255)

    @JvmField val DARK_AQUA = getColor(0, 190, 190, 255)

    @JvmField val DARK_RED = getColor(190, 0, 0, 255)

    @JvmField val DARK_PURPLE = getColor(190, 0, 190, 255)

    @JvmField val GOLD = getColor(217, 163, 52, 255)

    @JvmField val GRAY = getColor(190, 190, 190, 255)

    @JvmField val DARK_GRAY = getColor(63, 63, 63, 255)

    @JvmField val BLUE = getColor(63, 63, 254, 255)

    @JvmField val GREEN = getColor(63, 254, 63, 255)

    @JvmField val AQUA = getColor(63, 254, 254, 255)

    @JvmField val RED = getColor(254, 63, 63, 255)

    @JvmField val LIGHT_PURPLE = getColor(254, 63, 254, 255)

    @JvmField val YELLOW = getColor(254, 254, 63, 255)

    @JvmField val WHITE = getColor(255, 255, 255, 255)

    @JvmStatic
    fun color(color: Int): Long = when (color) {
        0 -> BLACK
        1 -> DARK_BLUE
        2 -> DARK_GREEN
        3 -> DARK_AQUA
        4 -> DARK_RED
        5 -> DARK_PURPLE
        6 -> GOLD
        7 -> GRAY
        8 -> DARK_GRAY
        9 -> BLUE
        10 -> GREEN
        11 -> AQUA
        12 -> RED
        13 -> LIGHT_PURPLE
        14 -> YELLOW
        else -> WHITE
    }

    @JvmStatic
    fun colorInt(color: Long): Int = color.toInt()

    /**
     * Begin drawing with the world renderer
     *
     * @param renderLayer The [RenderLayer] to use
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun begin(renderLayer: RenderLayer = CTRenderLayers.CT_QUADS()) = apply {
        pushMatrix().blendFunc(
            BlendFunction(
                SourceFactor.SRC_ALPHA,
                DestFactor.ONE_MINUS_SRC_ALPHA,
                SourceFactor.ONE,
                DestFactor.ZERO
            )
        )

        colorized = null
        vertexColor = null
        firstVertex = true
        began = true
        ucWorldRenderer.beginRenderLayer(renderLayer)
    }

    /**
     * Begin drawing with the world renderer
     *
     * @param drawMode The [DrawMode] to use
     * @param vertexFormat The [VertexFormat] to use
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun begin(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
    ) = apply {
        CTRenderLayers.getRenderLayer(drawMode, vertexFormat)?.let { renderLayer ->
            begin(renderLayer)
        }
    }

    /**
     * Begin drawing with the world renderer
     *
     * @param drawMode The [DrawMode] to use
     * @param vertexFormat The [VertexFormat] to use
     * @param snippet The [RenderSnippet] to use
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun begin(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ) = apply {
        begin(PipelineBuilder.begin(drawMode, vertexFormat, snippet).layer())
    }

    /**
     * Finalizes vertices and draws the world renderer.
     */
    @JvmStatic
    fun draw() = apply {
        if (!began) return this
        began = false

        ucWorldRenderer.endVertex()
        ucWorldRenderer.drawDirect()

        resetColor()
            .disableBlend()
            .popMatrix()
    }

    /**
     * Sets a new vertex in the world renderer.
     *
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun pos(x: Float, y: Float, z: Float) = apply {
        pos(x.toDouble(), y.toDouble(), z.toDouble())
    }
    @JvmStatic
    fun pos(x: Double, y: Double, z: Double) = apply {
        if (!began) begin()
        if (!firstVertex) ucWorldRenderer.endVertex()

        //#if MC<=12110
        //$$val cameraPos = Client.getMinecraft().gameRenderer.camera.pos
        //#else
        val cameraPos = Client.getMinecraft().gameRenderer.camera.cameraPos
        //#endif
        ucWorldRenderer.pos(matrixStack, x - cameraPos.x, y - cameraPos.y, z - cameraPos.z)

        firstVertex = false
        vertexColor?.let {
            color(vertexColor!!)
        }
    }

    /**
     * Sets a new list vertex to the world renderer.
     *
     * @param positions the list of (x, y, z) vertices in List<Pair<Float, Float, Float>> format
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun posList(positions: List<Triple<Float, Float, Float>>) = apply {
        for (pos in positions) {
            pos(pos.first, pos.second, pos.third)
        }
    }

    /**
     * Sets a new list vertex to the world renderer.
     *
     * @param positions the list of (x, y) vertices in List<Pair<Float, Float>> format
     * @param zPosition the z position for all vertices
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun posList(positions: List<Pair<Float, Float>>, zPosition: Float) = apply {
        for (pos in positions) {
            pos(pos.first, pos.second, zPosition)
        }
    }

    /**
     * Sets a new vertex in the world renderer in screen space.
     *
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    @JvmOverloads
    fun cameraPos(x: Float, y: Float, z: Float = 0f) = apply {
        cameraPos(x.toDouble(), y.toDouble(), z.toDouble())
    }

    @JvmStatic
    @JvmOverloads
    fun cameraPos(x: Double, y: Double, z: Double = 0.0) = apply {
        //#if MC<=12110
        //$$val cameraPos = Client.getMinecraft().gameRenderer.camera.pos
        //#else
        val cameraPos = Client.getMinecraft().gameRenderer.camera.cameraPos
        //#endif
        pos(x + cameraPos.x, y + cameraPos.y, z + cameraPos.z)
    }

    /**
     * Sets a new list vertex to the world renderer.
     *
     * @param positions the list of (x, y, z) vertices in List<Pair<Float, Float, Float>> format
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun cameraPosList(positions: List<Triple<Float, Float, Float>>) = apply {
        for (pos in positions) {
            cameraPos(pos.first, pos.second, pos.third)
        }
    }
    /**
     * Sets a new list vertex to the world renderer.
     *
     * @param positions the list of (x, y) vertices in List<Pair<Float, Float>> format
     * @param zPosition the z position for all vertices
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun cameraPosList(positions: List<Pair<Float, Float>>, zPosition: Float) = apply {
        for (pos in positions) {
            cameraPos(pos.first, pos.second, zPosition)
        }
    }

//    @JvmStatic
//    fun worldPos(x: Float, y: Float, z: Float) = apply {
//        if (!began) {
//            begin()
//        }
//        if (!firstVertex) {
//            ucWorldRenderer.endVertex()
//        }
//        ucWorldRenderer.pos(matrixStack, x.toDouble(), y.toDouble(), z.toDouble())
//        vertexColor?.let {
//            color(vertexColor!!)
//        }
//
//        firstVertex = false
//    }

    /**
     * Sets the texture location on the last defined vertex.
     *
     * @param u the u position in the texture
     * @param v the v position in the texture
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun tex(u: Float, v: Float) = apply {
        tex(u.toDouble(), v.toDouble())
    }
    @JvmStatic
    fun tex(u: Double, v: Double) = apply {
        ucWorldRenderer.tex(u, v)
    }

    /**
     * Sets the color for the last defined vertex.
     *
     * @param r the red value of the color, between 0 and 1
     * @param g the green value of the color, between 0 and 1
     * @param b the blue value of the color, between 0 and 1
     * @param a the alpha value of the color, between 0 and 1
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    @JvmOverloads
    fun color(r: Float, g: Float, b: Float, a: Float = 1f) = apply {
        ucWorldRenderer.color(r, g, b, a)
    }

    /**
     * Sets the color for the last defined vertex.
     *
     * @param r the red value of the color, between 0 and 255
     * @param g the green value of the color, between 0 and 255
     * @param b the blue value of the color, between 0 and 255
     * @param a the alpha value of the color, between 0 and 255
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    @JvmOverloads
    fun color(r: Int, g: Int, b: Int, a: Int = 255) = apply {
        ucWorldRenderer.color(r, g, b, a)
    }

    /**
     * Sets the color for the last defined vertex.
     *
     * @param color the color value, can use [getColor] to get this
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun color(color: Long) = apply {
        val (r, g, b, a) = Color(color.toInt(), true)
        color(r, g, b, a)
    }

    @JvmStatic
    fun color(color: Color) = apply {
        color(color.red, color.green, color.blue, color.alpha)
    }

    /**
     * Sets the normal of the vertex. This is mostly used with [VertexFormat.LINES]
     *
     * @param x the x position of the normal vector
     * @param y the y position of the normal vector
     * @param z the z position of the normal vector
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun normal(x: Float, y: Float, z: Float) = apply {
        ucWorldRenderer.norm(matrixStack, x, y, z)
    }
    @JvmStatic
    fun normal(x: Double, y: Double, z: Double) = apply {
        normal(x.toFloat(), y.toFloat(), z.toFloat())
    }

    /**
     * Sets the overlay location on the last defined vertex.
     *
     * @param u the u position in the overlay
     * @param v the v position in the overlay
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun overlay(u: Int, v: Int) = apply {
        ucWorldRenderer.overlay(u, v)
    }

    /**
     * Sets the light location on the last defined vertex.
     *
     * @param u the u position in the light
     * @param v the v position in the light
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun light(u: Int, v: Int) = apply {
        ucWorldRenderer.light(u, v)
    }

    /**
     * Sets the line width when rendering [DrawMode.LINES]
     *
     * @param width the width of the line
     * @return [RenderUtils] to allow for method chaining
     */
    @JvmStatic
    fun lineWidth(width: Float) = apply {
        //#if MC<=12110
        //$$RenderSystem.lineWidth(width)
        //#else
        val field = UGraphics::class.java.getDeclaredField("instance")
        field.setAccessible(true)
        val instance = field.get(ucWorldRenderer) as BufferBuilder
        instance.lineWidth(width)
        //#endif
    }

    @JvmStatic
    fun resetLineWidth() = apply {
        lineWidth(1f)
    }

    @JvmStatic
    fun enableCull() = apply {
        PipelineBuilder.enableCull()
    }

    @JvmStatic
    fun disableCull() = apply {
        PipelineBuilder.disableCull()
    }

    @JvmStatic
    fun enableLighting() = apply {
        UGraphics.enableLighting()
    }

    @JvmStatic
    fun disableLighting() = apply {
        UGraphics.disableLighting()
    }

    @JvmStatic
    fun enableDepth() = apply {
        PipelineBuilder.enableDepth()
    }

    @JvmStatic
    fun disableDepth() = apply {
        PipelineBuilder.disableDepth()
    }

    @JvmStatic
    fun getDepthTestFunctionFromInt(value: Int): DepthTestFunction {
        return when (value) {
//            0x200 -> !! // GL_NEVER
            0x201 -> DepthTestFunction.LESS_DEPTH_TEST // GL_LESS
            0x202 -> DepthTestFunction.EQUAL_DEPTH_TEST // GL_EQUAL
            0x203 -> DepthTestFunction.LEQUAL_DEPTH_TEST // GL_LEQUAL
            0x204 -> DepthTestFunction.GREATER_DEPTH_TEST // GL_GREATER
//            0x205 -> !! // GL_NOTEQUAL
//            0x206 -> !! // GL_GEQUAL
            0x207 -> DepthTestFunction.NO_DEPTH_TEST // GL_ALWAYS
            else -> throw IllegalArgumentException("Invalid depth test function value: $value")
        }
    }

    @JvmStatic
    fun depthFunc(function: DepthTestFunction) = apply {
        PipelineBuilder.setDepthTestFunction(function)
    }

    @JvmStatic
    fun depthFunc(depthFunc: Int) = apply {
        depthFunc(getDepthTestFunctionFromInt(depthFunc))
    }

    @JvmStatic
    fun depthMask(mask: Boolean) = apply { }

    @JvmStatic
    fun enableTexture2D() = apply { }
    @JvmStatic
    fun disableTexture2D() = apply { }

    @JvmStatic
    fun enableLineSmooth() = apply { }
    @JvmStatic
    fun disableLineSmooth() = apply { }

    @JvmStatic
    fun enableBlend() = apply {
        PipelineBuilder.enableBlend()
    }
    @JvmStatic
    fun disableBlend() = apply {
        PipelineBuilder.disableBlend()
    }

    @JvmStatic
    fun blendFunc(srcFactor: Int, dstFactor: Int) = apply {
        blendFunc(getSourceFactorFromInt(srcFactor), getDestFactorFromInt(dstFactor))
    }

    @JvmStatic
    fun blendFunc(function: BlendFunction) = apply {
        PipelineBuilder.setBlendFunction(function)
    }
    @JvmStatic
    fun blendFunc(srcFactor: SourceFactor, dstFactor: DestFactor) = apply {
        PipelineBuilder.setBlendFunction(BlendFunction(srcFactor, dstFactor))
    }

    @JvmStatic
    fun getSourceFactorFromInt(value: Int): SourceFactor {
        return when (value) {
            0 -> SourceFactor.ZERO
            1 -> SourceFactor.ONE
            768 -> SourceFactor.SRC_COLOR
            769 -> SourceFactor.ONE_MINUS_SRC_COLOR
            774 -> SourceFactor.DST_COLOR
            775 -> SourceFactor.ONE_MINUS_DST_COLOR
            32769 -> SourceFactor.CONSTANT_COLOR
            32770 -> SourceFactor.ONE_MINUS_CONSTANT_COLOR
            770 -> SourceFactor.SRC_ALPHA
            771 -> SourceFactor.ONE_MINUS_SRC_ALPHA
            772 -> SourceFactor.DST_ALPHA
            773 -> SourceFactor.ONE_MINUS_DST_ALPHA
            32771 -> SourceFactor.CONSTANT_ALPHA
            32772 -> SourceFactor.ONE_MINUS_CONSTANT_ALPHA
            776 -> SourceFactor.SRC_ALPHA_SATURATE
            else -> throw IllegalArgumentException("Invalid source factor value: $value")
        }
    }
    @JvmStatic
    fun getDestFactorFromInt(value: Int): DestFactor {
        return when (value) {
            0 -> DestFactor.ZERO
            1 -> DestFactor.ONE
            768 -> DestFactor.SRC_COLOR
            769 -> DestFactor.ONE_MINUS_SRC_COLOR
            774 -> DestFactor.DST_COLOR
            775 -> DestFactor.ONE_MINUS_DST_COLOR
            32769 -> DestFactor.CONSTANT_COLOR
            32770 -> DestFactor.ONE_MINUS_CONSTANT_COLOR
            770 -> DestFactor.SRC_ALPHA
            771 -> DestFactor.ONE_MINUS_SRC_ALPHA
            772 -> DestFactor.DST_ALPHA
            773 -> DestFactor.ONE_MINUS_DST_ALPHA
            32771 -> DestFactor.CONSTANT_ALPHA
            32772 -> DestFactor.ONE_MINUS_CONSTANT_ALPHA
//            776 -> DestFactor.SRC_ALPHA_SATURATE
            else -> throw IllegalArgumentException("Invalid source factor value: $value")
        }
    }
    @JvmStatic
    fun tryBlendFuncSeparate(
        sourceFactor: SourceFactor,
        destFactor: DestFactor,
        sourceFactorAlpha: SourceFactor,
        destFactorAlpha: DestFactor,
    ) = apply {
        blendFunc(
            BlendFunction(
                sourceFactor,
                destFactor,
                sourceFactorAlpha,
                destFactorAlpha
            )
        )
    }

    @JvmStatic
    fun tryBlendFuncSeparate(
        sourceFactor: Int,
        destFactor: Int,
        sourceFactorAlpha: Int,
        destFactorAlpha: Int,
    ) = apply {
        tryBlendFuncSeparate(
            getSourceFactorFromInt(sourceFactor),
            getDestFactorFromInt(destFactor),
            getSourceFactorFromInt(sourceFactorAlpha),
            getDestFactorFromInt(destFactorAlpha)
        )
    }

    @JvmStatic
    @JvmOverloads
    fun bindTexture(textureImage: Image, textureIndex: Int = 0) = apply {
        UGraphics.bindTexture(textureIndex, textureImage.getTexture()?.image?.imageId()?.toInt() ?: 0)
    }

    @JvmStatic
    fun deleteTexture(texture: Image) = apply {
        GL11.glDeleteTextures(texture.getTexture()?.image?.imageId()?.toInt() ?: 0)
    }

    @JvmStatic
    //#if MC<=12105
    //$$fun setShaderTexture(textureIndex: Int, texture: GpuTexture?) = apply {
    //#elseif MC<=12110
    //$$fun setShaderTexture(textureIndex: Int, texture: GpuTextureView?) = apply {
    //#else
    fun setShaderTexture(texture: GpuTextureView?) = apply {
    //#endif
    //#if MC<=12110
    //$$RenderSystem.setShaderTexture(textureIndex, texture)
    //#else
        PipelineBuilder.setTexture(texture?.texture())
    //#endif
    }

    @JvmStatic
    //#if MC<=12110
    //$$fun setShaderTexture(textureIndex: Int, textureImage: Image) = apply {
    //#else
    fun setShaderTexture(textureImage: Image) = apply {
    //#endif
        val gpuTexture = textureImage.getTexture()
        gpuTexture?.let {
            //#if MC<=12105
            //$$RenderSystem.setShaderTexture(textureIndex, gpuTexture.glTexture)
            //#else
            //#if MC<=12110
            //$$RenderSystem.setShaderTexture(textureIndex, gpuTexture.glTextureView)
            //#else
            PipelineBuilder.setTexture(gpuTexture.glTextureView.texture())
            //#endif
            //#endif
        }
    }

    @JvmStatic
    @JvmOverloads
    fun pushMatrix(stack: UMatrixStack = matrixStack) = apply {
        matrixPushCounter++
        matrixStackStack.addLast(stack)
        matrixStack = stack
        stack.push()
    }

    @JvmStatic
    fun popMatrix() = apply {
        matrixPushCounter--
        matrixStackStack.removeLast()
        matrixStack.pop()
    }

    @JvmStatic
    @JvmOverloads
    fun translate(x: Float, y: Float, z: Float = 0.0F) = apply {
        matrixStack.translate(x, y, z)
    }
    @JvmStatic
    @JvmOverloads
    fun translate(x: Double, y: Double, z: Double = 0.0) = apply {
        translate(x.toFloat(), y.toFloat(), z.toFloat())
    }

    @JvmStatic
    @JvmOverloads
    fun scale(scaleX: Float, scaleY: Float = scaleX, scaleZ: Float = 1f) = apply {
        matrixStack.scale(scaleX, scaleY, scaleZ)
    }
    @JvmStatic
    @JvmOverloads
    fun scale(scaleX: Double, scaleY: Double = scaleX, scaleZ: Double = scaleX) = apply {
        scale(scaleX.toFloat(), scaleY.toFloat(), scaleZ.toFloat())
    }

    @JvmStatic
    @JvmOverloads
    fun rotate(angle: Float, x: Float = 0f, y: Float = 0f, z: Float = 1f) = apply {
        matrixStack.rotate(angle, x, y, z)
    }
    @JvmStatic
    @JvmOverloads
    fun rotate(angle: Double, x: Double = 0.0, y: Double = 0.0, z: Double = 1.0) = apply {
        rotate(angle.toFloat(), x.toFloat(), y.toFloat(), z.toFloat())
    }

    @JvmStatic
    fun multiply(quaternion: Quaternionf) = apply {
        matrixStack.multiply(quaternion)
    }

    @JvmStatic
    fun colorize(color: Long) = apply {
        val (r, g, b, a) = getColorRGBA(color)
        colorize(r, g, b, a)
    }

    @JvmStatic
    @JvmOverloads
    fun colorize(red: Float, green: Float, blue: Float, alpha: Float = 1f) = apply {
        colorize(
            (red * 255).toInt(),
            (green * 255).toInt(),
            (blue * 255).toInt(),
            (alpha * 255).toInt(),
        )
    }

    @JvmStatic
    @JvmOverloads
    @Deprecated("There is no longer an easy way to change the color globally, you need to find out each color you do.")
    fun colorize(red: Int, green: Int, blue: Int, alpha: Int = 255) = apply {
        colorized = fixAlpha(getColor(red, green, blue, alpha))
        vertexColor = Color(colorized!!.toInt(), true)

        //#if MC<=12105
        //$$RenderSystem.setShaderColor(
        //$$    vertexColor!!.red / 255f,
        //$$    vertexColor!!.green / 255f,
        //$$    vertexColor!!.blue / 255f,
        //$$    vertexColor!!.alpha / 255f,
        //$$)
        //#endif
    }

    @JvmStatic
    fun fixAlpha(color: Long): Long {
        val alpha = color ushr 24 and 255
        return if (alpha < 10) {
            (color and 0xFF_FF_FF) or 0xA_FF_FF_FF
        } else {
            color
        }
    }

    //#if MC<=12108
    //$$fun getStringWidth(text: String) = getFontRenderer().getWidth(ChatLib.addColor(text))
    //#else
    fun getStringWidth(text: String): Int {
        return Client.synchronizedTask {
            getFontRenderer().getWidth(ChatLib.addColor(text))
        }
    }
    //#endif

    @JvmStatic
    fun resetColor() = apply {
        colorize(1f, 1f, 1f, 1f)
    }

    @JvmStatic
    @JvmOverloads
    fun getColor(red: Int, green: Int, blue: Int, alpha: Int = 255): Long {
        return ((alpha.coerceIn(0, 255) shl 24) or
            (red.coerceIn(0, 255) shl 16) or
            (green.coerceIn(0, 255) shl 8) or
            blue.coerceIn(0, 255)).toLong()
    }

    @JvmStatic
    @JvmOverloads
    fun getColor(red: Float, green: Float, blue: Float, alpha: Float = 255f): Long = getColor(
        red.toInt(),
        green.toInt(),
        blue.toInt(),
        alpha.toInt(),
    )

    @JvmStatic
    @JvmOverloads
    fun getColor0_1(r: Float, g: Float, b: Float, a: Float = 1f): Long {
        val ri = (r.coerceIn(0f, 1f) * 255).toInt()
        val gi = (g.coerceIn(0f, 1f) * 255).toInt()
        val bi = (b.coerceIn(0f, 1f) * 255).toInt()
        val ai = (a.coerceIn(0f, 1f) * 255).toInt()

        val colorInt = ((ai and 0xFF) shl 24) or
                ((ri and 0xFF) shl 16) or
                ((gi and 0xFF) shl 8) or
                (bi and 0xFF)

        return colorInt.toLong() and 0xFFFFFFFFL
    }

    @JvmStatic
    fun getColorRGBA(color: Long): FloatArray {
        val intColor = color.toInt()
        val r = ((intColor shr 24) and 0xFF).toFloat() / 255f
        val g = ((intColor shr 16) and 0xFF).toFloat() / 255f
        val b = ((intColor shr 8) and 0xFF).toFloat() / 255f
        val a = (intColor and 0xFF).toFloat() / 255f

        return floatArrayOf(
            r.coerceIn(0f, 1f),
            g.coerceIn(0f, 1f),
            b.coerceIn(0f, 1f),
            a.coerceIn(0f, 1f),
        )
    }

    @JvmStatic
    fun getARGBColorFromRGBAColor(color: Long): Long {
        val (r, g, b, a) = getColorRGBA(color)
        return getColor0_1(a, r, g, b)
    }

    @JvmStatic
    @JvmOverloads
    fun getRainbowColors(step: Float, speed: Float = 1f): IntArray {
        val red = ((sin(step / speed) + 0.75) * 170).toInt()
        val green = ((sin(step / speed + 2 * PI / 3) + 0.75) * 170).toInt()
        val blue = ((sin(step / speed + 4 * PI / 3) + 0.75) * 170).toInt()
        return intArrayOf(red, green, blue)
    }

    @JvmStatic
    @JvmOverloads
    fun getRainbow(step: Float, speed: Float = 1f): Long {
        val (r, g, b) = getRainbowColors(step, speed)
        return getColor(r, g, b)
    }

    /**
     * Gets a fixed render position from x, y, and z inputs adjusted with partial ticks
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @return the Vec3f position to render at
     */
    @JvmStatic
    fun getRenderPos(x: Float, y: Float, z: Float): Vec3f {
        return Vec3f(
            x - Player.getRenderX().toFloat(),
            y - Player.getRenderY().toFloat(),
            z - Player.getRenderZ().toFloat(),
        )
    }

    internal data class TextLines(val lines: List<String>, val width: Float, val height: Float)

    @JvmStatic
    internal fun splitText(text: String): TextLines {
        val lines = ChatLib.addColor(text).split(NEWLINE_REGEX)
        return TextLines(
            lines,
            lines.maxOf { getFontRenderer().getWidth(it) }.toFloat(),
            (getFontRenderer().fontHeight * lines.size + (lines.size - 1)).toFloat(),
        )
    }
}
