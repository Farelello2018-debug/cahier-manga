package com.matchs.tv

/**
 * Listes publiques de chaînes en accès libre, tenues par le projet iptv-org
 * (https://github.com/iptv-org/iptv). Ce ne sont que des chaînes gratuites :
 * les chaînes payantes (Canal+, beIN, SuperSport…) demandent leur propre abonnement.
 */
object Presets {
    private const val BASE = "https://iptv-org.github.io/iptv"

    val AFRICA: List<Pair<String, String>> = listOf(
        "Toute l'Afrique" to "$BASE/regions/afr.m3u",
        "Sports (monde)" to "$BASE/categories/sports.m3u",
        "Sénégal" to "$BASE/countries/sn.m3u",
        "Côte d'Ivoire" to "$BASE/countries/ci.m3u",
        "Cameroun" to "$BASE/countries/cm.m3u",
        "RD Congo" to "$BASE/countries/cd.m3u",
        "Congo" to "$BASE/countries/cg.m3u",
        "Mali" to "$BASE/countries/ml.m3u",
        "Burkina Faso" to "$BASE/countries/bf.m3u",
        "Guinée" to "$BASE/countries/gn.m3u",
        "Bénin" to "$BASE/countries/bj.m3u",
        "Togo" to "$BASE/countries/tg.m3u",
        "Gabon" to "$BASE/countries/ga.m3u",
        "Niger" to "$BASE/countries/ne.m3u",
        "Maroc" to "$BASE/countries/ma.m3u",
        "Algérie" to "$BASE/countries/dz.m3u",
        "Tunisie" to "$BASE/countries/tn.m3u",
        "Égypte" to "$BASE/countries/eg.m3u",
        "Nigeria" to "$BASE/countries/ng.m3u",
        "Ghana" to "$BASE/countries/gh.m3u",
        "Kenya" to "$BASE/countries/ke.m3u",
        "Afrique du Sud" to "$BASE/countries/za.m3u",
    )
}
