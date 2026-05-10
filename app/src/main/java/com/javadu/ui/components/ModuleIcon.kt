package com.javadu.ui.components

import androidx.annotation.DrawableRes
import com.javadu.R

private val MODULE_ICON_MAP = mapOf(
    "code"       to R.drawable.logo_ic_java,
    "science"    to R.drawable.logo_ic_testing,
    "cloud"      to R.drawable.logo_ic_selenium,
    "web"        to R.drawable.logo_ic_selenium,
    "storage"    to R.drawable.logo_ic_sql,
    "psychology" to 0
)

@DrawableRes
fun resolveModuleIcon(iconKey: String): Int = MODULE_ICON_MAP[iconKey] ?: 0
