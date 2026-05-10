package com.javadu.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.zIndex
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.javadu.R
import com.javadu.data.database.entities.ModuleProgress
import com.javadu.ui.theme.JavaGreen
import kotlinx.coroutines.delay
import kotlin.math.sqrt

/*
 * Поуровневый граф модулей.
 *
 * Чёткие ноды с тенью + неоновые связи с частицами.
 * Без прозрачных аурат — только сплошные цвета.
 */

private data class GNode(
    val id: Long,
    val x: Float,
    val y: Float,
    @DrawableRes val iconRes: Int,
    val iconFallback: String,
    val title: String,
    val totalLessons: Int
)

private data class GEdge(val from: Long, val to: Long, val isBlocking: Boolean = true)

private val NODES = listOf(
    // Уровень 1: База
    GNode(2, 0.25f, 0.08f, R.drawable.logo_ic_testing, "", "Теория тестирования", 3),
    GNode(7, 0.75f, 0.08f, R.drawable.logo_ic_git,     "", "Git",     4),

    // Уровень 2: Ядро — по диагонали от теории
    GNode(1, 0.50f, 0.26f, R.drawable.logo_ic_java,    "", "Java Core", 6),

    // Уровень 3: Ветвление — докер вправо, API и UI вниз симметрично
    GNode(3, 0.25f, 0.46f, R.drawable.logo_ic_api,       "", "API тесты", 6),
    GNode(4, 0.50f, 0.46f, R.drawable.logo_ic_selenium,   "", "UI тесты",  6),
    GNode(8, 0.75f, 0.46f, R.drawable.logo_ic_docker,    "", "Docker",    4),

    // Уровень 4: Конвергенция
    GNode(5, 0.50f, 0.68f, R.drawable.logo_ic_sql,       "", "SQL",       6),

    // Уровень 5: Финал
    GNode(6, 0.50f, 0.90f, 0, "🎯", "Собеседование",     6),
)

private val EDGES = listOf(
    // Уровень 1 → 2
    GEdge(2, 7, isBlocking = true),   // Теория -> Git
    GEdge(2, 1),                      // Теория -> Java Core

    // Уровень 2 → 3
    GEdge(1, 8, isBlocking = true),   // Java Core -> Docker
    GEdge(1, 3),                      // Java Core -> API
    GEdge(1, 4),                      // Java Core -> UI

    // Уровень 3 → 4
    GEdge(3, 5),                      // API -> SQL
    GEdge(4, 5),                      // UI -> SQL

    // Уровень 4 → 5
    GEdge(5, 6),                      // SQL -> Собеседование
)

/** Статус ноды для визуала */
private enum class NodeVisual { LOCKED, AVAILABLE, IN_PROGRESS, DONE }

@Composable
fun KnowledgeGraph(
    modifier: Modifier = Modifier,
    progressMap: Map<Long, ModuleProgress>,
    totalLessonsMap: Map<Long, Int>,
    onNodeClick: (Long) -> Unit
) {
    var w by remember { mutableFloatStateOf(0f) }
    var h by remember { mutableFloatStateOf(0f) }
    val tm = rememberTextMeasurer()
    val density = LocalDensity.current

    /* частицы по рёбрам */
    val parts = remember { List(EDGES.size) { Animatable(0f) } }
    EDGES.forEachIndexed { i, _ ->
        LaunchedEffect(i) { delay(i * 350L); parts[i].animateTo(1f, infiniteRepeatable(tween(2200, easing = LinearEasing), RepeatMode.Restart)) }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(900.dp)
            .pointerInput(Unit) {
                detectTapGestures { off ->
                    val hit = 50.dp.toPx()
                    NODES.find { n ->
                        val dx = off.x - n.x * w; val dy = off.y - n.y * h
                        sqrt(dx * dx + dy * dy) <= hit
                    }?.let { clicked ->
                        if (visual(clicked.id, progressMap, totalLessonsMap) != NodeVisual.LOCKED) {
                            onNodeClick(clicked.id)
                        }
                    }
                }
            }
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            w = size.width; h = size.height
            if (w == 0f || h == 0f) return@Canvas

            val map = NODES.associateBy { it.id }
            val nodeR = 34.dp.toPx()
            val ringW = 4.5f.dp.toPx()

            /* ═══════ РЁБРА ═══════ */
            EDGES.forEachIndexed { i, e ->
                val a = map[e.from] ?: return@forEachIndexed
                val b = map[e.to]   ?: return@forEachIndexed
                val ax = a.x * w; val ay = a.y * h
                val bx = b.x * w; val by = b.y * h

                val visA = visual(a.id, progressMap, totalLessonsMap)
                val visB = visual(b.id, progressMap, totalLessonsMap)
                val unl = visA == NodeVisual.DONE || visB == NodeVisual.DONE
                val bothDone = visA == NodeVisual.DONE && visB == NodeVisual.DONE

                val col = when {
                    bothDone -> Color(0xFF00E676)
                    unl      -> JavaGreen.copy(alpha = 0.65f)
                    else     -> Color(0xFF3D4852)
                }

                if (unl) {
                    /* тень связи */
                    drawLine(col.copy(alpha = 0.18f), Offset(ax, ay), Offset(bx, by), 10.dp.toPx(), StrokeCap.Round)
                    drawLine(col.copy(alpha = 0.30f), Offset(ax, ay), Offset(bx, by), 5.dp.toPx(), StrokeCap.Round)
                }
                /* тело */
                drawLine(col.copy(alpha = if (unl) 0.75f else 0.20f), Offset(ax, ay), Offset(bx, by),
                         if (bothDone) 3.dp.toPx() else if (unl) 2.2.dp.toPx() else 1.3.dp.toPx(), StrokeCap.Round)

                /* стрелка направления (маленький треугольник посередине) */
                if (unl) {
                    val mx = (ax + bx) / 2; val my = (ay + by) / 2
                    val ang = kotlin.math.atan2(by - ay, bx - ax)
                    val arr = 6.dp.toPx()
                    val p1 = Offset(mx + arr * kotlin.math.cos(ang), my + arr * kotlin.math.sin(ang))
                    val p2 = Offset(mx + arr * kotlin.math.cos(ang + 2.5f), my + arr * kotlin.math.sin(ang + 2.5f))
                    val p3 = Offset(mx + arr * kotlin.math.cos(ang - 2.5f), my + arr * kotlin.math.sin(ang - 2.5f))
                    drawPath(
                        androidx.compose.ui.graphics.Path().apply {
                            moveTo(p1.x, p1.y); lineTo(p2.x, p2.y); lineTo(p3.x, p3.y); close()
                        },
                        color = col.copy(alpha = 0.9f)
                    )
                }

                /* частицы */
                if (unl) {
                    val t = parts[i].value
                    for (j in 0..3) {
                        val tt = (t - j * 0.06f).coerceIn(0f, 1f)
                        val px = ax + (bx - ax) * tt
                        val py = ay + (by - ay) * tt
                        val a = (0.9f - j * 0.22f - tt * 0.2f).coerceAtLeast(0f)
                        drawCircle(JavaGreen.copy(alpha = a), (4.5f - j * 0.8f).dp.toPx(), Offset(px, py))
                    }
                    drawCircle(Color.White.copy(alpha = 0.85f), 2.dp.toPx(),
                               Offset(ax + (bx - ax) * t, ay + (by - ay) * t))
                }
            }

            /* ═══════ НОДЫ ═══════ */
            NODES.forEach { n ->
                val cx = n.x * w; val cy = n.y * h
                val vis = visual(n.id, progressMap, totalLessonsMap)
                val pct = if (n.totalLessons > 0) (progressMap[n.id]?.completedLessons ?: 0).toFloat() / n.totalLessons else 0f

                /* ─ палитра ─ */
                val (ringColor, fillColor, _, labelColor) = when (vis) {
                    NodeVisual.DONE ->
                        Quad(Color(0xFF00E676), Color(0xFF0A2918), Color(0xFFB9F6CA), Color(0xFF00E676))
                    NodeVisual.IN_PROGRESS ->
                        Quad(Color(0xFFFFA726), Color(0xFF2A1B0A), JavaGreen, Color(0xFFFFB74D))
                    NodeVisual.AVAILABLE ->
                        Quad(Color(0xFF42A5F5), Color(0xFF0D1B2A), Color(0xFF90CAF9), Color(0xFF64B5F6))
                    NodeVisual.LOCKED ->
                        Quad(Color(0xFF3D4852), Color(0xFF151B23), Color(0xFF5B6977), Color(0xFF5B6B7B))
                }

                val r = nodeR

                /* тень под нодой (сплошная, не прозрачная аура) */
                drawCircle(Color.Black.copy(alpha = 0.35f), r, Offset(cx + 2.dp.toPx(), cy + 4.dp.toPx()))

                /* кольцо прогресса (или полное для done / available / locked) */
                if (n.totalLessons > 0) {
                    if (vis == NodeVisual.IN_PROGRESS) {
                        /* трека */
                        drawArc(Color(0xFF3D4852), -90f, 360f, false,
                                topLeft = Offset(cx - r, cy - r),
                                size = Size(r * 2, r * 2),
                                style = Stroke(ringW, cap = StrokeCap.Round))
                        /* прогресс */
                        drawArc(ringColor, -90f, 360f * pct, false,
                                topLeft = Offset(cx - r, cy - r),
                                size = Size(r * 2, r * 2),
                                style = Stroke(ringW, cap = StrokeCap.Round))
                    } else {
                        /* сплошное кольцо */
                        drawCircle(ringColor, r, style = Stroke(ringW), center = Offset(cx, cy))
                    }
                } else {
                    /* модули без уроков — пунктирное фантазийное кольцо */
                    val dashCount = 24
                    val dashLen = 6f
                    val gap = 9f
                    repeat(dashCount) { d ->
                        val startA = -90f + d * (dashLen + gap)
                        drawArc(ringColor, startA, dashLen, false,
                                topLeft = Offset(cx - r, cy - r),
                                size = Size(r * 2, r * 2),
                                style = Stroke(3.dp.toPx(), cap = StrokeCap.Round))
                    }
                }

                /* заливка ноды — сплошной цвет, без прозрачности */
                drawCircle(fillColor, r - ringW / 2f, Offset(cx, cy))

                /* мини-маркер внутри ноды (% / ✓ / 🔒) теперь рисуется Compose-оверлеем поверх иконки */

                /* название под нодой — жирное, крупное */
                val ts = TextStyle(color = labelColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                val tl = tm.measure(n.title, ts)
                drawText(tm, n.title, Offset(cx - tl.size.width / 2f, cy + r + 10.dp.toPx()), ts)
            }
        }

        /* ═══════ ИКОНКИ + БАДЖИ НОД (оверлеем поверх Canvas) ═══════ */
        NODES.forEach { n ->
            if (w == 0f || h == 0f) return@forEach
            val cx = n.x * w
            val cy = n.y * h
            val vis = visual(n.id, progressMap, totalLessonsMap)

            val iconColor = when (vis) {
                NodeVisual.DONE -> Color(0xFFB9F6CA)
                NodeVisual.IN_PROGRESS -> JavaGreen
                NodeVisual.AVAILABLE -> Color(0xFF90CAF9)
                NodeVisual.LOCKED -> Color(0xFF5B6977)
            }

            val iconSize = 32.dp
            val boxSize = 48.dp
            val boxSizePx = with(density) { boxSize.toPx() }
            val topOffsetPx = with(density) { 2.dp.toPx() }
            val ox = cx - boxSizePx / 2f
            val oy = cy - boxSizePx / 2f - topOffsetPx

            val pct = if (n.totalLessons > 0) (progressMap[n.id]?.completedLessons ?: 0).toFloat() / n.totalLessons else 0f

            Box(
                modifier = Modifier
                    .offset { IntOffset(ox.toInt(), oy.toInt()) }
                    .size(boxSize)
                    .zIndex(1f)
            ) {
                /* иконка */
                if (n.iconRes != 0) {
                    Icon(
                        painter = painterResource(id = n.iconRes),
                        contentDescription = n.title,
                        modifier = Modifier.align(Alignment.Center).size(iconSize),
                        tint = Color.Unspecified
                    )
                } else {
                    Text(
                        text = n.iconFallback,
                        fontSize = 24.sp,
                        color = iconColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                /* баджи */
                when (vis) {
                    NodeVisual.LOCKED -> {
                        Text(
                            text = "\uD83D\uDD12",
                            fontSize = 18.sp,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = (-2).dp, y = 2.dp)
                        )
                    }
                    NodeVisual.DONE -> {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = (-2).dp, y = 2.dp)
                                .size(18.dp)
                                .background(Color(0xFF00E676), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "\u2713",
                                fontSize = 10.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.offset(y = (-1).dp)
                            )
                        }
                    }
                    NodeVisual.IN_PROGRESS -> {
                        val pctText = "${(pct * 100).toInt()}%"
                        Text(
                            text = pctText,
                            fontSize = 10.sp,
                            color = Color(0xFFFFA726),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 8.dp)
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

private data class Quad(val a: Color, val b: Color, val c: Color, val d: Color)

private fun visual(
    id: Long,
    pm: Map<Long, ModuleProgress>,
    tm: Map<Long, Int>
): NodeVisual {
    val total = tm[id] ?: 0
    val done = (pm[id]?.completedLessons ?: 0)

    if (done >= total) return NodeVisual.DONE
    if (done > 0) return NodeVisual.IN_PROGRESS

    // Нода ещё не начата — проверяем, разблокирована ли она
    val parentEdges = EDGES.filter { it.to == id }
    val locked = parentEdges.isNotEmpty() && parentEdges.any { edge ->
        if (!edge.isBlocking) return@any false // Неблокирующая связь не влияет на лок
        val parentTotal = tm[edge.from] ?: 0
        val parentDone = pm[edge.from]?.completedLessons ?: 0
        parentDone < parentTotal
    }
    return if (locked) NodeVisual.LOCKED else NodeVisual.AVAILABLE
}
