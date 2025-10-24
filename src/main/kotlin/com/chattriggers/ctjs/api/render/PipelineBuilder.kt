package com.chattriggers.ctjs.api.render

import com.mojang.blaze3d.pipeline.BlendFunction
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.DepthTestFunction
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderPhase
import net.minecraft.util.Identifier
import net.minecraft.util.TriState
import java.util.OptionalDouble

object PipelineBuilder {
    private val layerList = mutableMapOf<String, RenderLayer>()
    private val pipelineList = mutableMapOf<String, RenderPipeline>()
    private var cull: Boolean? = null
    private var depthTestFunction: DepthTestFunction? = null
    private var blendFunction: BlendFunction? = null
    private var lineWidth: Float? = null
    private var layering: RenderPhase.Layering? = null
    private var textureIdentifier: Identifier? = null
    private var drawMode = DrawMode.QUADS
    private var vertexFormat = VertexFormat.POSITION_COLOR
    private var snippet = RenderSnippet.POSITION_COLOR_SNIPPET
    private var location: String? = null

    @JvmStatic
    @JvmOverloads
    fun begin(
        drawMode: DrawMode = DrawMode.QUADS,
        vertexFormat: VertexFormat = VertexFormat.POSITION_COLOR,
        snippet: RenderSnippet = RenderSnippet.POSITION_COLOR_SNIPPET,
    ) = apply {
        this.drawMode = drawMode
        this.vertexFormat = vertexFormat
        this.snippet = snippet
    }

    @JvmStatic
    fun enableBlend() = apply {
        setBlendFunction(BlendFunction.TRANSLUCENT)
    }

    @JvmStatic
    fun disableBlend() = apply {
        blendFunction = null
    }

    @JvmStatic
    fun enableCull() = apply {
        cull = true
    }

    @JvmStatic
    fun disableCull() = apply {
        cull = false
    }

    @JvmStatic
    fun enableDepth() = apply {
        setDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
    }

    @JvmStatic
    fun disableDepth() = apply {
        setDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
    }

    @JvmStatic
    fun setLocation(newValue: String?) = apply {
        location = newValue
    }

    @JvmStatic
    fun setLayering(newValue: RenderPhase.Layering?) = apply {
        layering = newValue
    }

    @JvmStatic
    fun setLineWidth(newValue: Float?) = apply {
        lineWidth = newValue
    }

    @JvmStatic
    fun setTexture(newValue: Identifier?) = apply {
        textureIdentifier = newValue
    }

    @JvmStatic
    fun setDepthTestFunction(newValue: DepthTestFunction) = apply {
        depthTestFunction = newValue
    }

    @JvmStatic
    fun setBlendFunction(newValue: BlendFunction) = apply {
        blendFunction = newValue
    }

    @JvmStatic
    fun build(): RenderPipeline {
        if (pipelineList.containsKey(state())) return pipelineList[state()]!!

        val basePipeline = RenderPipeline
            .builder(snippet.mcSnippet)
            .withLocation("ctjs/custom/pipelines/${location ?: hashCode()}")
            .withVertexFormat(vertexFormat.toMC(), drawMode.toUC().mcMode)

        blendFunction?.let {
            basePipeline.withBlend(it)
        } ?: basePipeline.withoutBlend()

        cull?.let {
            basePipeline.withCull(cull!!)
        }

        depthTestFunction?.let {
            when (it) {
                DepthTestFunction.NO_DEPTH_TEST -> basePipeline.withDepthWrite(false)
                else -> basePipeline.withDepthWrite(true)
            }

            basePipeline.withDepthTestFunction(it)
        }

        val pipeline = basePipeline.build()
        pipelineList[state()] = pipeline

        return pipeline
    }

    @JvmStatic
    fun layer(): RenderLayer {
        if (layerList.containsKey(state())) return layerList[state()]!!

        val layerBuilder = RenderLayer.MultiPhaseParameters.builder()

        if (lineWidth != null) {
            layerBuilder.lineWidth(RenderPhase.LineWidth(OptionalDouble.of(lineWidth!!.toDouble())))
        }

        if (layering != null) {
            layerBuilder.layering(layering!!)
        }

        if (textureIdentifier != null) {
            //#if MC>12105
            //$$layerBuilder.texture(RenderPhase.Texture(textureIdentifier, false))
            //#else
            layerBuilder.texture(RenderPhase.Texture(textureIdentifier, TriState.FALSE, false))
            //#endif
        }

        val layer = RenderLayer.of(
            "ctjs/custom/layers/${location ?: hashCode()}",
            1536,
            build(),
            layerBuilder.build(false),
        )
        layerList[state()] = layer

        return layer
    }

    @JvmStatic
    fun state(): String {
        return (
            "PipelineBuilder[" +
                "location=$location, " +
                "cull=$cull, " +
                "depth=$depthTestFunction, " +
                "blend=$blendFunction, " +
                "layering=$layering, " +
                "lineWidth=$lineWidth, " +
                "drawMode=${drawMode.name}, " +
                "vertexFormat=${vertexFormat.name}, " +
                "snippet=${snippet.name}" +
            "]"
        )
    }
}
