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

class ProviderHub5x1_Cyber : BaseMultiuserWidgetProvider(R.drawable.bg_cyber_pulse, R.layout.widget_bar_pill)
class ProviderHub5x1_Gold : BaseMultiuserWidgetProvider(R.drawable.bg_liquid_gold, R.layout.widget_neon_bar)
class ProviderHub5x1_Bloom : BaseMultiuserWidgetProvider(R.drawable.bg_midnight_bloom, R.layout.widget_bar_glass)
class ProviderHub5x1_Eco : BaseMultiuserWidgetProvider(R.drawable.bg_eco_leaf, R.layout.widget_bar_pill)
class ProviderHub5x1_Stealth : BaseMultiuserWidgetProvider(R.drawable.bg_iron_stealth, R.layout.widget_neon_bar)

// --- NEW DESIGNS ---

// Extreme Hub (Special)
class ProviderExtremeHub : BaseMultiuserWidgetProvider(R.drawable.bg_liquid_magma, R.layout.widget_hub)

// Multiuser Ribbon (Special)
class ProviderMultiuserRibbon : BaseMultiuserWidgetProvider(R.drawable.bg_glass_dark, R.layout.widget_ribbon)

// Obsidian 1x1 (Standard)
class ProviderObsidian1x1 : BaseMultiuserWidgetProvider(R.drawable.bg_obsidian_glass, R.layout.widget_standard)

// Smart Expand
class ProviderSmartExpand : BaseMultiuserWidgetProvider(R.drawable.bg_quantum_glass, R.layout.widget_smart_expand)

// Wide Designs
class ProviderCharcoalWide : BaseMultiuserWidgetProvider(R.drawable.bg_charcoal, R.layout.widget_wide)
class ProviderFrostWide : BaseMultiuserWidgetProvider(R.drawable.bg_mint_frost, R.layout.widget_wide)
class ProviderMidnightWide : BaseMultiuserWidgetProvider(R.drawable.bg_sunset_glow, R.layout.widget_wide)

// Glass Designs
class ProviderFrostGlass : BaseMultiuserWidgetProvider(R.drawable.bg_frostbite, R.layout.widget_neon_bar)
class ProviderMistGlass : BaseMultiuserWidgetProvider(R.drawable.bg_glass_sexy, R.layout.widget_bar_glass)

// Colored Designs (Bar)
class ProviderDustyRose : BaseMultiuserWidgetProvider(R.drawable.bg_dusty_rose, R.layout.widget_neon_bar)
class ProviderLavender : BaseMultiuserWidgetProvider(R.drawable.bg_lavender_mist, R.layout.widget_neon_bar)
class ProviderNordicBlue : BaseMultiuserWidgetProvider(R.drawable.bg_nordic_blue, R.layout.widget_neon_bar)
class ProviderSageGreen : BaseMultiuserWidgetProvider(R.drawable.bg_sage_green, R.layout.widget_neon_bar)
class ProviderSandstone : BaseMultiuserWidgetProvider(R.drawable.bg_sandstone, R.layout.widget_neon_bar)

// Large Designs
class ProviderNordicLarge : BaseMultiuserWidgetProvider(R.drawable.bg_nordic_blue, R.layout.widget_large)
class ProviderSageLarge : BaseMultiuserWidgetProvider(R.drawable.bg_sage_green, R.layout.widget_large)

// Base Designs
class ProviderNeonBar : BaseMultiuserWidgetProvider(R.drawable.bg_obsidian_glass, R.layout.widget_neon_bar)
class ProviderNeonGrid : BaseMultiuserWidgetProvider(R.drawable.bg_obsidian_glass, R.layout.widget_neon_grid)
