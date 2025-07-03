package com.chattriggers.ctjs.api.render

import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier

object CTRenderLayers {
    @JvmStatic
    fun getRenderLayer(
        drawMode: DrawMode,
        vertexFormat: VertexFormat? = null,
    ): RenderLayer? {
        if (vertexFormat != null) {
            return getRenderLayerFunction_DrawModeVertexFormat(drawMode)?.invoke(drawMode, vertexFormat)
        }
        return getRenderLayerFunction_DrawMode(drawMode)?.invoke(drawMode)
    }

    private fun getRenderLayerFunction_DrawModeVertexFormat(drawMode: DrawMode): ((DrawMode, VertexFormat) -> RenderLayer)? {
        return when (drawMode) {
            DrawMode.LINES -> ::CT_LINES
            DrawMode.LINE_STRIP -> ::CT_LINE_STRIP
            DrawMode.TRIANGLES -> ::CT_TRIANGLES
            DrawMode.TRIANGLE_STRIP -> ::CT_TRIANGLE_STRIP
            DrawMode.QUADS -> ::CT_QUADS
            else -> null
        }
    }

    private fun getRenderLayerFunction_DrawMode(drawMode: DrawMode): ((DrawMode) -> RenderLayer)? {
        return when (drawMode) {
            DrawMode.LINES -> ::CT_LINES
            DrawMode.LINE_STRIP -> ::CT_LINE_STRIP
            DrawMode.TRIANGLES -> ::CT_TRIANGLES
            DrawMode.TRIANGLE_STRIP -> ::CT_TRIANGLE_STRIP
            DrawMode.QUADS -> ::CT_QUADS
            else -> null
        }
    }

    private fun getRenderLayerFunction(drawMode: DrawMode): (() -> RenderLayer)? {
        return when (drawMode) {
            DrawMode.LINES -> ::CT_LINES
            DrawMode.LINE_STRIP -> ::CT_LINE_STRIP
            DrawMode.TRIANGLES -> ::CT_TRIANGLES
            DrawMode.TRIANGLE_STRIP -> ::CT_TRIANGLE_STRIP
            DrawMode.QUADS -> ::CT_QUADS
            else -> null
        }
    }

    @JvmStatic
    fun CT_LINES(
        drawMode: DrawMode = DrawMode.LINES,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR_NORMAL,
        snippet: RenderSnippet = RenderSnippet.RENDERTYPE_LINES_SNIPPET,
        lineThickness: Float = 1.0f,
    ): RenderLayer {
        return CTRenderPipelines
            .CT_LINES(drawMode, vertexFormat, snippet)
            .setLineWidth(lineThickness)
            .layer()
    }

    @JvmStatic
    fun CT_LINES_ESP(
        drawMode: DrawMode = DrawMode.LINES,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR_NORMAL,
        snippet: RenderSnippet = RenderSnippet.RENDERTYPE_LINES_SNIPPET,
        lineThickness: Float = 1.0f,
    ): RenderLayer {
        return CTRenderPipelines
            .CT_LINES_ESP(drawMode, vertexFormat, snippet)
            .setLineWidth(lineThickness)
            .layer()
    }

    @JvmStatic
    fun CT_LINE_STRIP(
        drawMode: DrawMode = DrawMode.LINE_STRIP,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR_NORMAL,
        snippet: RenderSnippet = RenderSnippet.RENDERTYPE_LINES_SNIPPET,
        lineThickness: Float = 1.0f,
    ): RenderLayer {
        return CTRenderPipelines
            .CT_LINE_STRIP(drawMode, vertexFormat, snippet)
            .setLineWidth(lineThickness)
            .layer()
    }

    @JvmStatic
    fun CT_LINE_STRIP_ESP(
        drawMode: DrawMode = DrawMode.LINE_STRIP,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR_NORMAL,
        snippet: RenderSnippet = RenderSnippet.RENDERTYPE_LINES_SNIPPET,
        lineThickness: Float = 1.0f,
    ): RenderLayer {
        return CTRenderPipelines
            .CT_LINE_STRIP_ESP(drawMode, vertexFormat, snippet)
            .setLineWidth(lineThickness)
            .layer()
    }

    @JvmStatic
    fun CT_TRIANGLES(
        drawMode: DrawMode = DrawMode.TRIANGLES,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): RenderLayer {
        return CTRenderPipelines
            .CT_TRIANGLES(drawMode, vertexFormat, snippet)
            .layer()
    }

    @JvmStatic
    fun CT_TRIANGLES_ESP(
        drawMode: DrawMode = DrawMode.TRIANGLES,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): RenderLayer {
        return CTRenderPipelines
            .CT_TRIANGLES_ESP(drawMode, vertexFormat, snippet)
            .layer()
    }

    @JvmStatic
    fun CT_TRIANGLE_STRIP(
        drawMode: DrawMode = DrawMode.TRIANGLE_STRIP,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): RenderLayer {
        return CTRenderPipelines
            .CT_TRIANGLE_STRIP(drawMode, vertexFormat, snippet)
            .layer()
    }

    @JvmStatic
    fun CT_TRIANGLE_STRIP_ESP(
        drawMode: DrawMode = DrawMode.TRIANGLE_STRIP,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): RenderLayer {
        return CTRenderPipelines
            .CT_TRIANGLE_STRIP_ESP(drawMode, vertexFormat, snippet)
            .layer()
    }

    @JvmStatic
    fun CT_QUADS(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): RenderLayer {
        return CTRenderPipelines
            .CT_QUADS(drawMode, vertexFormat, snippet)
            .layer()
    }

    @JvmStatic
    fun CT_QUADS_ESP(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ): RenderLayer {
        return CTRenderPipelines
            .CT_QUADS_ESP(drawMode, vertexFormat, snippet)
            .layer()
    }

    fun CT_TEXTURED_QUADS(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_TEXTURE_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_TEX_COLOR_SNIPPET,
        textureIdentifier: Identifier,
    ): RenderLayer {
        return CTRenderPipelines
            .CT_TEXTURED_QUADS(drawMode, vertexFormat, snippet)
            .setTexture(textureIdentifier)
            .layer()
    }

    fun CT_TEXTURED_QUADS_ESP(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_TEXTURE_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_TEX_COLOR_SNIPPET,
        textureIdentifier: Identifier,
    ): RenderLayer {
        return CTRenderPipelines
            .CT_TEXTURED_QUADS(drawMode, vertexFormat, snippet)
            .setTexture(textureIdentifier)
            .layer()
    }
}
