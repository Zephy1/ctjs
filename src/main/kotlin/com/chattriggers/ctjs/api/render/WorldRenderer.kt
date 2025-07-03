package com.chattriggers.ctjs.api.render

import com.chattriggers.ctjs.api.client.Client
import com.chattriggers.ctjs.internal.utils.get
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.LightmapTextureManager
import org.joml.Matrix4f
import org.joml.Vector3f
import org.mozilla.javascript.NativeObject
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin

object WorldRenderer {
    /**
     * Renders floating lines of text in the world
     *
     * @param text the text
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param scale the text scale
     * @param renderBackground whether to draw a transparent background
     * @param centered whether to center each text line (Doesn't work with newline characters)
     * @param textShadow whether to draw a shadow behind the text
     * @param disableDepth whether to render the text through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawStringRGBA(
        text: String,
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        scale: Float = 1f,
        renderBackground: Boolean = false,
        centered: Boolean = false,
        textShadow: Boolean = true,
        disableDepth: Boolean = false,
    ) {
        drawString(text, xPosition, yPosition, zPosition, RenderUtils.getColor(red, green, blue, alpha), scale, renderBackground, centered, textShadow, disableDepth)
    }

    /**
     * Renders floating lines of text in the world
     *
     * @param text the text
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param color the color as a [Long] value in RGBA format
     * @param scale the text scale
     * @param renderBackground whether to draw a transparent background
     * @param centered whether to center each text line (Doesn't work with newline characters)
     * @param textShadow whether to draw a shadow behind the text
     * @param disableDepth whether to render the text through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawString(
        text: String,
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        scale: Float = 1f,
        renderBackground: Boolean = false,
        centered: Boolean = false,
        textShadow: Boolean = true,
        disableDepth: Boolean = false,
    ) {
        val (lines, width, height) = RenderUtils.splitText(text)
        val fontRenderer = MinecraftClient.getInstance().textRenderer
        val camera = Client.getMinecraft().gameRenderer.camera
        val cameraPos = camera.pos
        val vertexConsumers = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers

        val matrix = Matrix4f()
        val adjustedScale = (scale * 0.05).toFloat()
        val xShift = -width / 2
        val yShift = -height / 2
        var yOffset = 0
        val backgroundColorInt = if (renderBackground) {
            Color(0, 0, 0, 150).rgb
        } else {
            Color(0, 0, 0, 0).rgb
        }

        for (line in lines) {
            matrix
                .translate(
                    (xPosition - cameraPos.getX()).toFloat(),
                    (yPosition - cameraPos.getY() + yOffset * adjustedScale).toFloat(),
                    (zPosition - cameraPos.getZ()).toFloat(),
                )
                .rotate(camera.rotation)
                .scale(adjustedScale, -adjustedScale, adjustedScale)

            val centerShift = if (centered) {
                xShift + (fontRenderer.getWidth(line) / 2f)
            } else {
                0f
            }

            fontRenderer.draw(
                line,
                xShift - centerShift,
                yShift + yOffset,
                color.toInt(),
                textShadow,
                matrix,
                vertexConsumers,
                if (disableDepth) TextRenderer.TextLayerType.SEE_THROUGH else TextRenderer.TextLayerType.NORMAL,
                backgroundColorInt,
                LightmapTextureManager.MAX_LIGHT_COORDINATE,
            )

            yOffset += fontRenderer.fontHeight + 1
        }
    }

    /**
     * A variant of drawString that takes an object instead of positional parameters
     */
    @JvmStatic
    fun drawString(obj: NativeObject) {
        drawString(
            obj.get<String>("text") ?: error("Expected \"text\" property in object passed to WorldRenderer.drawString"),
            obj.get<Number>("xPosition")?.toFloat() ?: error("Expected \"xPosition\" property in object passed to WorldRenderer.drawString"),
            obj.get<Number>("yPosition")?.toFloat() ?: error("Expected \"yPosition\" property in object passed to WorldRenderer.drawString"),
            obj.get<Number>("zPosition")?.toFloat() ?: error("Expected \"zPosition\" property in object passed to WorldRenderer.drawString"),
            obj.get<Number>("color")?.toLong() ?: RenderUtils.colorized ?: RenderUtils.WHITE,
            obj.get<Number>("scale")?.toFloat() ?: 1f,
            obj.get<Boolean>("renderBackground") ?: true,
            obj.get<Boolean>("disableDepth") ?: false,
            obj.get<Boolean>("textShadow") ?: true,
            obj.get<Boolean>("disableDepth") ?: true,
        )
    }

    /**
     * Draws a line in the world from point (startX, startY) to (endX, endY)
     *
     * @param startX the starting X-coordinate
     * @param startY the starting Y-coordinate
     * @param startZ the starting Z-coordinate
     * @param endX the ending X-coordinate
     * @param endY the ending Y-coordinate
     * @param endZ the ending Z-coordinate
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the line through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawLineRGBA(
        startX: Float,
        startY: Float,
        startZ: Float,
        endX: Float,
        endY: Float,
        endZ: Float,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawLine(startX, startY, startZ, endX, endY, endZ, RenderUtils.getColor(red, green, blue, alpha), disableDepth, lineThickness)
    }

    /**
     * Draws a line in the world from point (startX, startY) to (endX, endY)
     *
     * @param startX the starting X-coordinate
     * @param startY the starting Y-coordinate
     * @param startZ the starting Z-coordinate
     * @param endX the ending X-coordinate
     * @param endY the ending Y-coordinate
     * @param endZ the ending Z-coordinate
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to disable depth testing
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawLine(
        startX: Float,
        startY: Float,
        startZ: Float,
        endX: Float,
        endY: Float,
        endZ: Float,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        val renderLayer = when {
            disableDepth -> CTRenderLayers.CT_LINES_ESP()
            else -> CTRenderLayers.CT_LINES()
        }

        val normalVec = Vector3f(endX - startX, endY - startY, endZ - startZ).normalize()
        RenderUtils
            .pushMatrix()
            .disableDepth()
            .lineWidth(lineThickness)
            .begin(renderLayer)
            .colorize(color)
            .pos(startX, startY, startZ).normal(normalVec.x, normalVec.y, normalVec.z)
            .pos(endX, endY, endZ).normal(normalVec.x, normalVec.y, normalVec.z)
            .draw()
            .resetLineWidth()
            .enableDepth()
            .popMatrix()
    }

    /**
     * Draws a wireframe cube in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the size of the box
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframeCubeRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, size, size, size, RenderUtils.getColor(red, green, blue, alpha), disableDepth, wireframe = true, lineThickness)
    }

    /**
     * Draws a wireframe cube in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the size of the box
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframeCube(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, size, size, size, color, disableDepth, wireframe = true, lineThickness)
    }

    /**
     * Draws a wireframe box in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param width the width of the box
     * @param height the height of the box
     * @param depth the depth of the box
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawWireframeBoxRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, RenderUtils.getColor(red, green, blue, alpha), disableDepth, wireframe = true, lineThickness)
    }

    /**
     * Draws a wireframe box in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param width the width of the box
     * @param height the height of the box
     * @param depth the depth of the box
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawWireframeBox(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, color, disableDepth, wireframe = true, lineThickness)
    }

    /**
     * Draws a solid cube in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the size of the box
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidCubeRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, size, size, size, RenderUtils.getColor(red, green, blue, alpha), disableDepth, wireframe = false, lineThickness)
    }

    /**
     * Draws a solid cube in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the size of the box
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidCube(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, size, size, size, color, disableDepth, wireframe = false, lineThickness)
    }

    /**
     * Draws a solid box in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param width the width of the box
     * @param height the height of the box
     * @param depth the depth of the box
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawSolidBoxRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, RenderUtils.getColor(red, green, blue, alpha), disableDepth, wireframe = false, lineThickness)
    }

    /**
     * Draws a solid box in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param width the width of the box
     * @param height the height of the box
     * @param depth the depth of the box
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawSolidBox(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, color, disableDepth, wireframe = false, lineThickness)
    }

    /**
     * Draws a box in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param width the width of the box
     * @param height the height of the box
     * @param depth the depth of the box
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     * @param wireframe whether to draw the box as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawBoxRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawBox(xPosition, yPosition, zPosition, width, height, depth, RenderUtils.getColor(red, green, blue, alpha), disableDepth, wireframe, lineThickness)
    }

    /**
     * Draws a box in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param width the width of the box
     * @param height the height of the box
     * @param depth the depth of the box
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     * @param wireframe whether to draw the box as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawBox(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        width: Float = 1f,
        height: Float = 1f,
        depth: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        val renderLayer = when {
            disableDepth && wireframe -> CTRenderLayers.CT_LINES_ESP()
            disableDepth && !wireframe -> CTRenderLayers.CT_TRIANGLE_STRIP_ESP()
            !disableDepth && wireframe -> CTRenderLayers.CT_LINES()
            else -> CTRenderLayers.CT_TRIANGLE_STRIP()
        }

        val hw = width / 2f
        val hh = height / 2f
        val hd = depth / 2f

        val x0 = xPosition - hw
        val x1 = xPosition + hw
        val y0 = yPosition - hh
        val y1 = yPosition + hh
        val z0 = zPosition - hd
        val z1 = zPosition + hd

        val vertexes = when {
            wireframe -> listOf(
                Vector3f(x0, y0, z0),
                Vector3f(x1, y0, z0),
                Vector3f(x1, y1, z0),
                Vector3f(x0, y1, z0),
                Vector3f(x1, y1, z0),
                Vector3f(x1, y1, z1),
                Vector3f(x0, y1, z1),
                Vector3f(x1, y1, z1),
                Vector3f(x1, y0, z1),
                Vector3f(x0, y0, z1),
                Vector3f(x0, y1, z1),
                Vector3f(x0, y1, z0),
                Vector3f(x0, y0, z0),
                Vector3f(x0, y0, z1),
                Vector3f(x1, y0, z1),
                Vector3f(x1, y0, z0),
            )
            else -> listOf(
                Vector3f(x0, y0, z0),
                Vector3f(x1, y0, z0),
                Vector3f(x0, y1, z0),
                Vector3f(x1, y1, z0),
                Vector3f(x1, y1, z1),
                Vector3f(x1, y0, z0),
                Vector3f(x1, y0, z1),
                Vector3f(x0, y0, z0),
                Vector3f(x0, y0, z1),
                Vector3f(x0, y1, z0),
                Vector3f(x0, y1, z1),
                Vector3f(x1, y1, z1),
                Vector3f(x0, y0, z1),
                Vector3f(x1, y0, z1),
            )
        }

        RenderUtils
            .pushMatrix()
            .enableBlend()
            .lineWidth(lineThickness)
            .begin(renderLayer)
            .colorize(color)

        for (i in 0 until vertexes.size - if (wireframe) 1 else 0) {
            val p1 = vertexes[i]
            RenderUtils.pos(p1.x, p1.y, p1.z)
            if (wireframe) {
                val p2 = vertexes[i + 1]
                val normal = Vector3f(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z).normalize()

                RenderUtils
                    .normal(normal.x, normal.y, normal.z)
                    .pos(p2.x, p2.y, p2.z)
                    .normal(normal.x, normal.y, normal.z)
            }
        }

        RenderUtils
            .draw()
            .disableBlend()
            .resetLineWidth()
            .popMatrix()
    }

    /**
     * Draws a solid sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the sphere
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidSphereRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 32,
        disableDepth: Boolean = false,
    ) {
        drawSphere(xPosition, yPosition, zPosition, radius, radius, radius, RenderUtils.getColor(red, green, blue, alpha), segments, disableDepth, false)
    }

    /**
     * Draws a solid sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the sphere
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidSphere(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 32,
        disableDepth: Boolean = false,
    ) {
        drawSphere(xPosition, yPosition, zPosition, radius, radius, radius, color, segments, disableDepth, false)
    }

    /**
     * Draws a solid sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the sphere
     * @param yScale the Y-scale of the sphere
     * @param zScale the Z-scale of the sphere
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSolidSphereRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 32,
        disableDepth: Boolean = false,
    ) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, RenderUtils.getColor(red, green, blue, alpha), segments, disableDepth, false)
    }

    /**
     * Draws a solid sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the sphere
     * @param yScale the Y-scale of the sphere
     * @param zScale the Z-scale of the sphere
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSolidSphere(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 32,
        disableDepth: Boolean = false,
    ) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, color, segments, disableDepth, false)
    }

    /**
     * Draws a wireframe sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the sphere
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframeSphereRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 32,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawSphere(xPosition, yPosition, zPosition, radius, radius, radius, RenderUtils.getColor(red, green, blue, alpha), segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a wireframe sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the sphere
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframeSphere(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 32,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawSphere(xPosition, yPosition, zPosition, radius, radius, radius, color, segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a wireframe sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the sphere
     * @param yScale the Y-scale of the sphere
     * @param zScale the Z-scale of the sphere
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawWireframeSphereRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 32,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, RenderUtils.getColor(red, green, blue, alpha), segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a wireframe sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the sphere
     * @param yScale the Y-scale of the sphere
     * @param zScale the Z-scale of the sphere
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawWireframeSphere(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 32,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, color, segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the sphere
     * @param yScale the Y-scale of the sphere
     * @param zScale the Z-scale of the sphere
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     * @param wireframe whether to draw the sphere as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawSphereRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 32,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawSphere(xPosition, yPosition, zPosition, xScale, yScale, zScale, RenderUtils.getColor(red, green, blue, alpha), segments, disableDepth, wireframe, lineThickness)
    }

    /**
     * Draws a sphere in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the sphere
     * @param yScale the Y-scale of the sphere
     * @param zScale the Z-scale of the sphere
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the sphere
     * @param disableDepth whether to render the box through blocks
     * @param wireframe whether to draw the sphere as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawSphere(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 32,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        val renderLayer = when {
            disableDepth && wireframe -> CTRenderLayers.CT_LINES_ESP()
            disableDepth && !wireframe -> CTRenderLayers.CT_QUADS_ESP()
            !disableDepth && wireframe -> CTRenderLayers.CT_LINES()
            else -> CTRenderLayers.CT_QUADS()
        }

        RenderUtils
            .pushMatrix()
            .enableBlend()
            .lineWidth(lineThickness)
            .begin(renderLayer)
            .colorize(color)

        for (phi in 0 until segments) {
            for (theta in 0 until (segments * 2)) {
                val x1 = (xPosition + xScale * sin(Math.PI * phi / segments) * cos(2.0 * Math.PI * theta / (segments * 2))).toFloat()
                val y1 = (yPosition + yScale * cos(Math.PI * phi / segments)).toFloat()
                val z1 = (zPosition + zScale * sin(Math.PI * phi / segments) * sin(2.0 * Math.PI * theta / (segments * 2))).toFloat()

                val x2 = (xPosition + xScale * sin(Math.PI * (phi + 1) / segments) * cos(2.0 * Math.PI * theta / (segments * 2))).toFloat()
                val y2 = (yPosition + yScale * cos(Math.PI * (phi + 1) / segments)).toFloat()
                val z2 = (zPosition + zScale * sin(Math.PI * (phi + 1) / segments) * sin(2.0 * Math.PI * theta / (segments * 2))).toFloat()

                val x3 = (xPosition + xScale * sin(Math.PI * (phi + 1) / segments) * cos(2.0 * Math.PI * (theta + 1) / (segments * 2))).toFloat()
                val y3 = (yPosition + yScale * cos(Math.PI * (phi + 1) / segments)).toFloat()
                val z3 = (zPosition + zScale * sin(Math.PI * (phi + 1) / segments) * sin(2.0 * Math.PI * (theta + 1) / (segments * 2))).toFloat()

                val x4 = (xPosition + xScale * sin(Math.PI * phi / segments) * cos(2.0 * Math.PI * (theta + 1) / (segments * 2))).toFloat()
                val y4 = (yPosition + yScale * cos(Math.PI * phi / segments)).toFloat()
                val z4 = (zPosition + zScale * sin(Math.PI * phi / segments) * sin(2.0 * Math.PI * (theta + 1) / (segments * 2))).toFloat()

                RenderUtils
                    .pos(x1, y1, z1)
                    .pos(x2, y2, z2)
                    .pos(x3, y3, z3)
                    .pos(x4, y4, z4)
            }
        }

        RenderUtils
            .draw()
            .disableBlend()
            .resetLineWidth()
            .popMatrix()
    }

    /**
     * Draws a solid cone in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cone
     * @param height the height of the cone
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cone
     * @param disableDepth whether to render the cone through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSolidConeRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, RenderUtils.getColor(red, green, blue, alpha), segments, disableDepth, false)
    }

    /**
     * Draws a solid cone in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cone
     * @param height the height of the cone
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cone
     * @param disableDepth whether to render the cone through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSolidCone(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, color, segments, disableDepth, false)
    }

    /**
     * Draws a wireframe cone in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cone
     * @param height the height of the cone
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cone
     * @param disableDepth whether to render the cone through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawWireframeConeRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, RenderUtils.getColor(red, green, blue, alpha), segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a wireframe cone in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cone
     * @param height the height of the cone
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cone
     * @param disableDepth whether to render the cone through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawWireframeCone(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, color, segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a cone in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cone
     * @param height the height of the cone
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cone
     * @param disableDepth whether to render the cone through blocks
     * @param wireframe whether to draw the cone as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawConeRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, RenderUtils.getColor(red, green, blue, alpha), segments, disableDepth, wireframe, lineThickness)
    }

    /**
     * Draws a cone in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cone
     * @param height the height of the cone
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cone
     * @param disableDepth whether to render the cone through blocks
     * @param wireframe whether to draw the cone as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawCone(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, 0f, radius, height, color, segments, disableDepth, wireframe, lineThickness)
    }

    /**
     * Draws a solid cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidCylinderRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, RenderUtils.getColor(red, green, blue, alpha), segments, disableDepth, false)
    }

    /**
     * Draws a solid cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidCylinder(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, color, segments, disableDepth, false)
    }

    /**
     * Draws a solid cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param topRadius the radius of the top of the cylinder
     * @param bottomRadius the radius of the bottom of the cylinder
     * @param height the height of the cylinder
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSolidCylinderRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, RenderUtils.getColor(red, green, blue, alpha), segments, disableDepth, false)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param topRadius the radius of the top of the cylinder
     * @param bottomRadius the radius of the bottom of the cylinder
     * @param height the height of the cylinder
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSolidCylinder(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, color, segments, disableDepth, false)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframeCylinderRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, RenderUtils.getColor(red, green, blue, alpha), segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframeCylinder(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, color, segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param topRadius the radius of the top of the cylinder
     * @param bottomRadius the radius of the bottom of the cylinder
     * @param height the height of the cylinder
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawWireframeCylinderRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, RenderUtils.getColor(red, green, blue, alpha), segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param topRadius the radius of the top of the cylinder
     * @param bottomRadius the radius of the bottom of the cylinder
     * @param height the height of the cylinder
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawWireframeCylinder(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, color, segments, disableDepth, true, lineThickness)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param wireframe whether to draw the cylinder as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleCylinderRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, RenderUtils.getColor(red, green, blue, alpha), segments, disableDepth, wireframe, lineThickness)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param wireframe whether to draw the cylinder as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleCylinder(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        radius: Float = 1f,
        height: Float = 2f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, radius, radius, height, RenderUtils.getColor(red, green, blue, alpha), segments, disableDepth, wireframe, lineThickness)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param topRadius the radius of the top of the cylinder
     * @param bottomRadius the radius of the bottom of the cylinder
     * @param height the height of the cylinder
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param wireframe whether to draw the cylinder as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawCylinderRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawCylinder(xPosition, yPosition, zPosition, topRadius, bottomRadius, height, RenderUtils.getColor(red, green, blue, alpha), segments, disableDepth, wireframe, lineThickness)
    }

    /**
     * Draws a cylinder in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param topRadius the radius of the top of the cylinder
     * @param bottomRadius the radius of the bottom of the cylinder
     * @param height the height of the cylinder
     * @param color the color as a [Long] value in RGBA format
     * @param segments the number of segments in the cylinder
     * @param disableDepth whether to render the cylinder through blocks
     * @param wireframe whether to draw the cylinder as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawCylinder(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        topRadius: Float = 1f,
        bottomRadius: Float = 1f,
        height: Float = 2f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        segments: Int = 64,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        val renderLayer = when {
            disableDepth && wireframe -> CTRenderLayers.CT_LINES_ESP()
            disableDepth && !wireframe -> CTRenderLayers.CT_QUADS_ESP()
            !disableDepth && wireframe -> CTRenderLayers.CT_LINES()
            else -> CTRenderLayers.CT_QUADS()
        }

        val angleStep = 2f * Math.PI / segments
        val topY = yPosition + height

        val bottomX = FloatArray(segments + 1)
        val bottomZ = FloatArray(segments + 1)
        val topX = FloatArray(segments + 1)
        val topZ = FloatArray(segments + 1)

        for (i in 0..segments) {
            val angle = angleStep * i
            val cosA = cos(angle).toFloat()
            val sinA = sin(angle).toFloat()

            bottomX[i] = xPosition + bottomRadius * cosA
            bottomZ[i] = zPosition + bottomRadius * sinA
            topX[i] = xPosition + topRadius * cosA
            topZ[i] = zPosition + topRadius * sinA
        }

        RenderUtils
            .pushMatrix()
            .enableBlend()
            .lineWidth(lineThickness)
            .begin(renderLayer)
            .colorize(color)

        for (i in 0 until segments) {
            val next = (i + 1) % segments

            RenderUtils.pos(bottomX[i], yPosition, bottomZ[i])
            if (wireframe) {
                RenderUtils
                    .pos(topX[i], topY, topZ[i])
                    .pos(topX[next], topY, topZ[next])
                    .pos(bottomX[i], yPosition, bottomZ[i])
                    .pos(bottomX[next], yPosition, bottomZ[next])
            } else {
                RenderUtils
                    .pos(bottomX[next], yPosition, bottomZ[next])
                    .pos(topX[next], topY, topZ[next])
                    .pos(topX[i], topY, topZ[i])
            }
        }

        for ((y, xRing, zRing) in listOf(
            Triple(yPosition, bottomX, bottomZ),
            Triple(topY, topX, topZ),
        )) {
            for (i in 0 until segments) {
                val next = (i + 1) % segments
                RenderUtils
                    .pos(xPosition, y, zPosition)
                    .pos(xRing[next], y, zRing[next])
                    .pos(xRing[i], y, zRing[i])
                    .pos(xPosition, y, zPosition)
            }
        }

        RenderUtils
            .draw()
            .disableBlend()
            .resetLineWidth()
            .popMatrix()
    }

    /**
     * Draws a solid pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the size of the pyramid
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidPyramidRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, size, size, size, RenderUtils.getColor(red, green, blue, alpha), disableDepth, false)
    }

    /**
     * Draws a solid pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the size of the pyramid
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleSolidPyramid(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, size, size, size, color, disableDepth, false)
    }

    /**
     * Draws a solid pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the pyramid
     * @param yScale the Y-scale of the pyramid
     * @param zScale the Z-scale of the pyramid
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSolidPyramidRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, RenderUtils.getColor(red, green, blue, alpha), disableDepth, false)
    }

    /**
     * Draws a solid pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the pyramid
     * @param yScale the Y-scale of the pyramid
     * @param zScale the Z-scale of the pyramid
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     */
    @JvmStatic
    @JvmOverloads
    fun drawSolidPyramid(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, color, disableDepth, false)
    }

    /**
     * Draws a wireframe pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the size of the pyramid
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframePyramidRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, size, size, size, RenderUtils.getColor(red, green, blue, alpha), disableDepth, true)
    }

    /**
     * Draws a wireframe pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param size the radius of the pyramid
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawSimpleWireframePyramid(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        size: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, size, size, size, color, disableDepth, true)
    }

    /**
     * Draws a wireframe pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the pyramid
     * @param yScale the Y-scale of the pyramid
     * @param zScale the Z-scale of the pyramid
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawWireframePyramidRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, RenderUtils.getColor(red, green, blue, alpha), disableDepth, true)
    }

    /**
     * Draws a wireframe pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the pyramid
     * @param yScale the Y-scale of the pyramid
     * @param zScale the Z-scale of the pyramid
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the box through blocks
     * @param lineThickness how thick the line should be
     */
    @JvmStatic
    @JvmOverloads
    fun drawWireframePyramid(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, color, disableDepth, true)
    }

    /**
     * Draws a pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the pyramid
     * @param yScale the Y-scale of the pyramid
     * @param zScale the Z-scale of the pyramid
     * @param red the red component of the color (0-255)
     * @param green the green component of the color (0-255)
     * @param blue the blue component of the color (0-255)
     * @param alpha the alpha component of the color (0-255)
     * @param disableDepth whether to render the box through blocks
     * @param wireframe whether to draw the pyramid as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawPyramidRGBA(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255,
        alpha: Int = 255,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        drawPyramid(xPosition, yPosition, zPosition, xScale, yScale, zScale, RenderUtils.getColor(red, green, blue, alpha), disableDepth, wireframe)
    }

    /**
     * Draws a pyramid in the world
     *
     * @param xPosition the X-coordinate
     * @param yPosition the Y-coordinate
     * @param zPosition the Z-coordinate
     * @param xScale the X-scale of the pyramid
     * @param yScale the Y-scale of the pyramid
     * @param zScale the Z-scale of the pyramid
     * @param color the color as a [Long] value in RGBA format
     * @param disableDepth whether to render the pyramid through blocks
     * @param wireframe whether to draw the pyramid as a wireframe
     * @param lineThickness how thick the line should be (wireframe only)
     */
    @JvmStatic
    @JvmOverloads
    fun drawPyramid(
        xPosition: Float,
        yPosition: Float,
        zPosition: Float,
        xScale: Float = 1f,
        yScale: Float = 1f,
        zScale: Float = 1f,
        color: Long = RenderUtils.colorized ?: RenderUtils.WHITE,
        disableDepth: Boolean = false,
        wireframe: Boolean = false,
        lineThickness: Float = 1f,
    ) {
        val renderLayer = when {
            disableDepth && wireframe -> CTRenderLayers.CT_LINES_ESP()
            disableDepth && !wireframe -> CTRenderLayers.CT_TRIANGLES_ESP()
            !disableDepth && wireframe -> CTRenderLayers.CT_LINES()
            else -> CTRenderLayers.CT_TRIANGLES()
        }

        val halfX = xScale / 2f
        val halfZ = zScale / 2f

        val x0 = xPosition - halfX
        val x1 = xPosition + halfX
        val z0 = zPosition - halfZ
        val z1 = zPosition + halfZ

        val yBase = yPosition
        val yTip = yPosition + yScale

        val tipX = xPosition
        val tipY = yTip
        val tipZ = zPosition

        fun triangle(ax: Float, ay: Float, az: Float, bx: Float, by: Float, bz: Float, cx: Float, cy: Float, cz: Float) {
            val normal = Vector3f(
                (bx - ax) * (cy - ay) - (cx - ax) * (by - ay),
                (bz - az) * (cy - ay) - (cz - az) * (by - ay),
                (cx - ax) * (by - ay) - (bx - ax) * (cy - ay),
            ).normalize()

            RenderUtils
                .pos(ax, ay, az).normal(normal.x, normal.y, normal.z)
                .pos(bx, by, bz).normal(normal.x, normal.y, normal.z)
                .pos(cx, cy, cz).normal(normal.x, normal.y, normal.z)
        }

        RenderUtils
            .pushMatrix()
            .enableBlend()
            .lineWidth(lineThickness)
            .begin(renderLayer)
            .colorize(color)

        triangle(tipX, tipY, tipZ, x0, yBase, z0, x1, yBase, z0)
        triangle(tipX, tipY, tipZ, x1, yBase, z0, x1, yBase, z1)
        triangle(tipX, tipY, tipZ, x1, yBase, z1, x0, yBase, z1)
        triangle(tipX, tipY, tipZ, x0, yBase, z1, x0, yBase, z0)

        triangle(x0, yBase, z0, x1, yBase, z0, x1, yBase, z1)
        triangle(x0, yBase, z0, x1, yBase, z1, x0, yBase, z1)

        RenderUtils
            .draw()
            .disableBlend()
            .resetLineWidth()
            .popMatrix()
    }
}
