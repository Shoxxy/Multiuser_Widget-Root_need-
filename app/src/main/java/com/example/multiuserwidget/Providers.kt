package com.example.multiuserwidget

abstract class BaseMultiuserWidgetProvider(
    val bgResId: Int,
    val layoutResId: Int
) : MultiuserWidgetProvider()

// 1. Bar Hub 4x1 (Stealth Design)
class ProviderHub4x1 : BaseMultiuserWidgetProvider(R.drawable.bg_carbon_stealth, R.layout.widget_neon_bar)

// 2. Bar Hub 5x1 (Aurora Design)
class ProviderHub5x1 : BaseMultiuserWidgetProvider(R.drawable.bg_aurora_glass, R.layout.widget_neon_bar)

// 3. Grid Hub 4x2 (Neumorph Design)
class ProviderHub4x2 : BaseMultiuserWidgetProvider(R.drawable.bg_dark_neumorph, R.layout.widget_neon_grid)

// 4. Grid Hub 5x2 (Hologram Design)
class ProviderHub5x2 : BaseMultiuserWidgetProvider(R.drawable.bg_hologram_tech, R.layout.widget_neon_grid)
