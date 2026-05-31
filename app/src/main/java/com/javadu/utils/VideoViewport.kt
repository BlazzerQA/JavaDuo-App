package com.javadu.utils

data class VideoViewport(
    val scale: Float,
    val cropX: Float,
    val cropY: Float
)

private const val VIDEO_WIDTH = 1080f
private const val VIDEO_HEIGHT = 2200f

fun calculateViewport(
    screenWidth: Float,
    screenHeight: Float
): VideoViewport {

    val scale = maxOf(
        screenWidth / VIDEO_WIDTH,
        screenHeight / VIDEO_HEIGHT
    )

    val scaledWidth = VIDEO_WIDTH * scale
    val scaledHeight = VIDEO_HEIGHT * scale

    return VideoViewport(
        scale = scale,
        cropX = (scaledWidth - screenWidth) / 2f,
        cropY = (scaledHeight - screenHeight) / 2f
    )
}
