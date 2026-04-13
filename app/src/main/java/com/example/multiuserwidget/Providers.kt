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

// --- 5x1 SPECIAL DESIGNS ---

// 5. Cyber Pulse 5x1 (Pill Shape)
class ProviderHub5x1_Cyber : BaseMultiuserWidgetProvider(R.drawable.bg_cyber_pulse, R.layout.widget_bar_pill)

// 6. Liquid Gold 5x1 (Modern Shape)
class ProviderHub5x1_Gold : BaseMultiuserWidgetProvider(R.drawable.bg_liquid_gold, R.layout.widget_neon_bar)

// 7. Midnight Bloom 5x1 (Glass Shape)
class ProviderHub5x1_Bloom : BaseMultiuserWidgetProvider(R.drawable.bg_midnight_bloom, R.layout.widget_bar_glass)

// 8. Eco Leaf 5x1 (Pill Shape)
class ProviderHub5x1_Eco : BaseMultiuserWidgetProvider(R.drawable.bg_eco_leaf, R.layout.widget_bar_pill)

// 9. Iron Stealth 5x1 (Modern Shape)
class ProviderHub5x1_Stealth : BaseMultiuserWidgetProvider(R.drawable.bg_iron_stealth, R.layout.widget_neon_bar)
