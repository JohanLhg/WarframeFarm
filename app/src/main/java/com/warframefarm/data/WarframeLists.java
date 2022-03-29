package com.warframefarm.data;

import static com.warframefarm.data.WarframeConstants.ARCHWING;
import static com.warframefarm.data.WarframeConstants.ARCH_GUN;
import static com.warframefarm.data.WarframeConstants.AXI;
import static com.warframefarm.data.WarframeConstants.BAND;
import static com.warframefarm.data.WarframeConstants.BARREL;
import static com.warframefarm.data.WarframeConstants.BLADE;
import static com.warframefarm.data.WarframeConstants.BLUEPRINT;
import static com.warframefarm.data.WarframeConstants.BOOT;
import static com.warframefarm.data.WarframeConstants.BUCKLE;
import static com.warframefarm.data.WarframeConstants.CARAPACE;
import static com.warframefarm.data.WarframeConstants.CEREBRUM;
import static com.warframefarm.data.WarframeConstants.CHAIN;
import static com.warframefarm.data.WarframeConstants.CHASSIS;
import static com.warframefarm.data.WarframeConstants.CORPUS;
import static com.warframefarm.data.WarframeConstants.CORRUPTED;
import static com.warframefarm.data.WarframeConstants.CROSSFIRE;
import static com.warframefarm.data.WarframeConstants.DISC;
import static com.warframefarm.data.WarframeConstants.GAUNTLET;
import static com.warframefarm.data.WarframeConstants.GRINEER;
import static com.warframefarm.data.WarframeConstants.GRIP;
import static com.warframefarm.data.WarframeConstants.GUARD;
import static com.warframefarm.data.WarframeConstants.HANDLE;
import static com.warframefarm.data.WarframeConstants.HARNESS;
import static com.warframefarm.data.WarframeConstants.HEAD;
import static com.warframefarm.data.WarframeConstants.HILT;
import static com.warframefarm.data.WarframeConstants.INFESTED;
import static com.warframefarm.data.WarframeConstants.LINK;
import static com.warframefarm.data.WarframeConstants.LITH;
import static com.warframefarm.data.WarframeConstants.LOWER_LIMB;
import static com.warframefarm.data.WarframeConstants.MELEE;
import static com.warframefarm.data.WarframeConstants.MESO;
import static com.warframefarm.data.WarframeConstants.NEO;
import static com.warframefarm.data.WarframeConstants.NEUROPTICS;
import static com.warframefarm.data.WarframeConstants.ORNAMENT;
import static com.warframefarm.data.WarframeConstants.OROKIN;
import static com.warframefarm.data.WarframeConstants.PET;
import static com.warframefarm.data.WarframeConstants.POUCH;
import static com.warframefarm.data.WarframeConstants.PRIMARY;
import static com.warframefarm.data.WarframeConstants.RECEIVER;
import static com.warframefarm.data.WarframeConstants.SECONDARY;
import static com.warframefarm.data.WarframeConstants.SENTIENT;
import static com.warframefarm.data.WarframeConstants.SENTINEL;
import static com.warframefarm.data.WarframeConstants.STARS;
import static com.warframefarm.data.WarframeConstants.STOCK;
import static com.warframefarm.data.WarframeConstants.STRING;
import static com.warframefarm.data.WarframeConstants.SYSTEMS;
import static com.warframefarm.data.WarframeConstants.UPPER_LIMB;
import static com.warframefarm.data.WarframeConstants.WARFRAME;
import static com.warframefarm.data.WarframeConstants.WINGS;
import static com.warframefarm.database.WarframeFarmDatabase.REWARD_COMMON;
import static com.warframefarm.database.WarframeFarmDatabase.REWARD_RARE;
import static com.warframefarm.database.WarframeFarmDatabase.REWARD_UNCOMMON;
import static com.warframefarm.database.WarframeFarmDatabase.TYPE_ARCHWING;
import static com.warframefarm.database.WarframeFarmDatabase.TYPE_EMPYREAN;

import com.warframefarm.R;

import java.util.HashMap;

public class WarframeLists {
    public static HashMap<String, Integer> PrimeTypeImage = setPrimeTypeImage(),
            PrimeImage = setPrimeImage(),
            RelicEraImage = setRelicEraImage(),
            RelicEraRadiantImage = setRelicEraRadiantImage(),
            FactionImage = setFactionImage(),
            PlanetImage = setPlanetImage(),
            PlanetSquareImage = setPlanetSquareImage(),
            PlanetTopImage = setPlanetTopImage();
    public static HashMap<Integer, Integer> RelicRarityImage = setRelicRarityImage(),
            MissionTypeImage = setMissionTypeImage();

    private static HashMap<String, Integer> setPrimeImage() {
        HashMap<String, Integer> primeTypeImage = new HashMap<>();
        //region Warframe
        primeTypeImage.put("Ash", R.drawable.prime_warframe_ash);
        primeTypeImage.put("Atlas", R.drawable.prime_warframe_atlas);
        primeTypeImage.put("Banshee", R.drawable.prime_warframe_banshee);
        primeTypeImage.put("Chroma", R.drawable.prime_warframe_chroma);
        primeTypeImage.put("Ember", R.drawable.prime_warframe_ember);
        primeTypeImage.put("Equinox", R.drawable.prime_warframe_equinox);
        primeTypeImage.put("Frost", R.drawable.prime_warframe_frost);
        primeTypeImage.put("Gara", R.drawable.prime_warframe_gara);
        primeTypeImage.put("Garuda", R.drawable.prime_warframe_garuda);
        primeTypeImage.put("Harrow", R.drawable.prime_warframe_harrow);
        primeTypeImage.put("Hydroid", R.drawable.prime_warframe_hydroid);
        primeTypeImage.put("Inaros", R.drawable.prime_warframe_inaros);
        primeTypeImage.put("Ivara", R.drawable.prime_warframe_ivara);
        primeTypeImage.put("Limbo", R.drawable.prime_warframe_limbo);
        primeTypeImage.put("Loki", R.drawable.prime_warframe_loki);
        primeTypeImage.put("Mag", R.drawable.prime_warframe_mag);
        primeTypeImage.put("Mesa", R.drawable.prime_warframe_mesa);
        primeTypeImage.put("Mirage", R.drawable.prime_warframe_mirage);
        primeTypeImage.put("Nekros", R.drawable.prime_warframe_nekros);
        primeTypeImage.put("Nezha", R.drawable.prime_warframe_nezha);
        primeTypeImage.put("Nidus", R.drawable.prime_warframe_nidus);
        primeTypeImage.put("Nova", R.drawable.prime_warframe_nova);
        primeTypeImage.put("Nyx", R.drawable.prime_warframe_nyx);
        primeTypeImage.put("Oberon", R.drawable.prime_warframe_oberon);
        primeTypeImage.put("Octavia", R.drawable.prime_warframe_octavia);
        primeTypeImage.put("Rhino", R.drawable.prime_warframe_rhino);
        primeTypeImage.put("Saryn", R.drawable.prime_warframe_saryn);
        primeTypeImage.put("Titania", R.drawable.prime_warframe_titania);
        primeTypeImage.put("Trinity", R.drawable.prime_warframe_trinity);
        primeTypeImage.put("Valkyr", R.drawable.prime_warframe_valkyr);
        primeTypeImage.put("Vauban", R.drawable.prime_warframe_vauban);
        primeTypeImage.put("Volt", R.drawable.prime_warframe_volt);
        primeTypeImage.put("Wukong", R.drawable.prime_warframe_wukong);
        primeTypeImage.put("Zephyr", R.drawable.prime_warframe_zephyr);
        //endregion

        //Archwing
        primeTypeImage.put("Odonata", R.drawable.prime_archwing_odonata);

        //Pet
        primeTypeImage.put("Kavasa", R.drawable.prime_pet_kavasa_collar);

        //region Sentinel
        primeTypeImage.put("Carrier", R.drawable.prime_sentinel_carrier);
        primeTypeImage.put("Dethcube", R.drawable.prime_sentinel_dethcube);
        primeTypeImage.put("Helios", R.drawable.prime_sentinel_helios);
        primeTypeImage.put("Wyrm", R.drawable.prime_sentinel_wyrm);
        //endregion

        //region Primary
        primeTypeImage.put("Astilla", R.drawable.prime_primary_astilla);
        primeTypeImage.put("Baza", R.drawable.prime_primary_baza);
        primeTypeImage.put("Boar", R.drawable.prime_primary_boar);
        primeTypeImage.put("Boltor", R.drawable.prime_primary_boltor);
        primeTypeImage.put("Braton", R.drawable.prime_primary_braton);
        primeTypeImage.put("Burston", R.drawable.prime_primary_burston);
        primeTypeImage.put("Cernos", R.drawable.prime_primary_cernos);
        primeTypeImage.put("Corinth", R.drawable.prime_primary_corinth);
        primeTypeImage.put("Latron", R.drawable.prime_primary_latron);
        primeTypeImage.put("Nagantaka", R.drawable.prime_primary_nagantaka);
        primeTypeImage.put("Panthera", R.drawable.prime_primary_panthera);
        primeTypeImage.put("Paris", R.drawable.prime_primary_paris);
        primeTypeImage.put("Rubico", R.drawable.prime_primary_rubico);
        primeTypeImage.put("Scourge", R.drawable.prime_primary_scourge);
        primeTypeImage.put("Soma", R.drawable.prime_primary_soma);
        primeTypeImage.put("Stradavar", R.drawable.prime_primary_stradavar);
        primeTypeImage.put("Strun", R.drawable.prime_primary_strun);
        primeTypeImage.put("Sybaris", R.drawable.prime_primary_sybaris);
        primeTypeImage.put("Tenora", R.drawable.prime_primary_tenora);
        primeTypeImage.put("Tiberon", R.drawable.prime_primary_tiberon);
        primeTypeImage.put("Tigris", R.drawable.prime_primary_tigris);
        primeTypeImage.put("Vectis", R.drawable.prime_primary_vectis);
        primeTypeImage.put("Zhuge", R.drawable.prime_primary_zhuge);
        //endregion

        //region Secondary
        primeTypeImage.put("Akbolto", R.drawable.prime_secondary_akbolto);
        primeTypeImage.put("Akbronco", R.drawable.prime_secondary_akbronco);
        primeTypeImage.put("Akjagara", R.drawable.prime_secondary_akjagara);
        primeTypeImage.put("Aklex", R.drawable.prime_secondary_aklex);
        primeTypeImage.put("Aksomati", R.drawable.prime_secondary_aksomati);
        primeTypeImage.put("Akstiletto", R.drawable.prime_secondary_akstiletto);
        primeTypeImage.put("Akvasto", R.drawable.prime_secondary_akvasto);
        primeTypeImage.put("Ballistica", R.drawable.prime_secondary_ballistica);
        primeTypeImage.put("Bronco", R.drawable.prime_secondary_bronco);
        primeTypeImage.put("Euphona", R.drawable.prime_secondary_euphona);
        primeTypeImage.put("Hikou", R.drawable.prime_secondary_hikou);
        primeTypeImage.put("Knell", R.drawable.prime_secondary_knell);
        primeTypeImage.put("Lex", R.drawable.prime_secondary_lex);
        primeTypeImage.put("Magnus", R.drawable.prime_secondary_magnus);
        primeTypeImage.put("Pandero", R.drawable.prime_secondary_pandero);
        primeTypeImage.put("Pyrana", R.drawable.prime_secondary_pyrana);
        primeTypeImage.put("Sicarus", R.drawable.prime_secondary_sicarus);
        primeTypeImage.put("Spira", R.drawable.prime_secondary_spira);
        primeTypeImage.put("Vasto", R.drawable.prime_secondary_vasto);
        primeTypeImage.put("Zakti", R.drawable.prime_secondary_zakti);
        //endregion

        //region Melee
        primeTypeImage.put("Ankyros", R.drawable.prime_melee_ankyros);
        primeTypeImage.put("Bo", R.drawable.prime_melee_bo);
        primeTypeImage.put("Dakra", R.drawable.prime_melee_dakra);
        primeTypeImage.put("Destreza", R.drawable.prime_melee_destreza);
        primeTypeImage.put("Dual Kamas", R.drawable.prime_melee_dual_kamas);
        primeTypeImage.put("Fang", R.drawable.prime_melee_fang);
        primeTypeImage.put("Fragor", R.drawable.prime_melee_fragor);
        primeTypeImage.put("Galatine", R.drawable.prime_melee_galatine);
        primeTypeImage.put("Glaive", R.drawable.prime_melee_glaive);
        primeTypeImage.put("Gram", R.drawable.prime_melee_gram);
        primeTypeImage.put("Guandao", R.drawable.prime_melee_guandao);
        primeTypeImage.put("Karyst", R.drawable.prime_melee_karyst);
        primeTypeImage.put("Kogake", R.drawable.prime_melee_kogake);
        primeTypeImage.put("Kronen", R.drawable.prime_melee_kronen);
        primeTypeImage.put("Nami Skyla", R.drawable.prime_melee_nami_skyla);
        primeTypeImage.put("Nikana", R.drawable.prime_melee_nikana);
        primeTypeImage.put("Ninkondi", R.drawable.prime_melee_ninkondi);
        primeTypeImage.put("Orthos", R.drawable.prime_melee_orthos);
        primeTypeImage.put("Pangolin", R.drawable.prime_melee_pangolin);
        primeTypeImage.put("Reaper", R.drawable.prime_melee_reaper);
        primeTypeImage.put("Redeemer", R.drawable.prime_melee_redeemer);
        primeTypeImage.put("Scindo", R.drawable.prime_melee_scindo);
        primeTypeImage.put("Silva & Aegis", R.drawable.prime_melee_silva_aegis);
        primeTypeImage.put("Tekko", R.drawable.prime_melee_tekko);
        primeTypeImage.put("Tipedo", R.drawable.prime_melee_tipedo);
        primeTypeImage.put("Venka", R.drawable.prime_melee_venka);
        primeTypeImage.put("Volnus", R.drawable.prime_melee_volnus);
        //endregion

        //region Arch-Gun
        primeTypeImage.put("Corvas", R.drawable.prime_archgun_corvas);
        //endregion
        return primeTypeImage;
    }

    private static HashMap<String, Integer> setPrimeTypeImage() {
        HashMap<String, Integer> primeTypeImage = new HashMap<>();
        primeTypeImage.put(WARFRAME, R.drawable.prime_warframe);
        primeTypeImage.put(ARCHWING, R.drawable.prime_archwing);
        primeTypeImage.put(PET, R.drawable.prime_pet);
        primeTypeImage.put(SENTINEL, R.drawable.prime_sentinel);
        primeTypeImage.put(PRIMARY, R.drawable.prime_primary);
        primeTypeImage.put(SECONDARY, R.drawable.prime_secondary);
        primeTypeImage.put(MELEE, R.drawable.prime_melee);
        primeTypeImage.put(ARCH_GUN, R.drawable.prime_archgun);
        return primeTypeImage;
    }

    private static HashMap<String, Integer> setRelicEraImage() {
        HashMap<String, Integer> relicEraImage = new HashMap<>();
        relicEraImage.put(LITH, R.drawable.relic_lith);
        relicEraImage.put(MESO, R.drawable.relic_meso);
        relicEraImage.put(NEO, R.drawable.relic_neo);
        relicEraImage.put(AXI, R.drawable.relic_axi);
        return relicEraImage;
    }

    private static HashMap<String, Integer> setRelicEraRadiantImage() {
        HashMap<String, Integer> relicEraImage = new HashMap<>();
        relicEraImage.put(LITH, R.drawable.relic_lith_selected);
        relicEraImage.put(MESO, R.drawable.relic_meso_selected);
        relicEraImage.put(NEO, R.drawable.relic_neo_selected);
        relicEraImage.put(AXI, R.drawable.relic_axi_selected);
        return relicEraImage;
    }

    private static HashMap<Integer, Integer> setRelicRarityImage() {
        HashMap<Integer, Integer> relicRarityImage = new HashMap<>();
        relicRarityImage.put(REWARD_COMMON, R.drawable.reward_common);
        relicRarityImage.put(REWARD_UNCOMMON, R.drawable.reward_uncommon);
        relicRarityImage.put(REWARD_RARE, R.drawable.reward_rare);
        return relicRarityImage;
    }

    private static HashMap<String, Integer> setFactionImage() {
        HashMap<String, Integer> factionImage = new HashMap<>();
        factionImage.put(GRINEER, R.drawable.faction_grineer);
        factionImage.put(CORPUS, R.drawable.faction_corpus);
        factionImage.put(CROSSFIRE, R.drawable.faction_crossfire);
        factionImage.put(INFESTED, R.drawable.faction_infested);
        factionImage.put(CORRUPTED, R.drawable.faction_orokin);
        factionImage.put(OROKIN, R.drawable.faction_orokin);
        factionImage.put(SENTIENT, R.drawable.faction_sentient);
        return factionImage;
    }

    private static HashMap<Integer, Integer> setMissionTypeImage() {
        HashMap<Integer, Integer> missionTypeImage = new HashMap<>();
        missionTypeImage.put(TYPE_ARCHWING, R.drawable.archwing);
        missionTypeImage.put(TYPE_EMPYREAN, R.drawable.empyrean);
        return missionTypeImage;
    }

    private static HashMap<String, Integer> setPlanetImage() {
        HashMap<String, Integer> planetImage = new HashMap<>();
        planetImage.put("Mercury", R.drawable.planet_mercury);
        planetImage.put("Venus", R.drawable.planet_venus);
        planetImage.put("Earth", R.drawable.planet_earth);
        planetImage.put("Mars", R.drawable.planet_mars);
        planetImage.put("Phobos", R.drawable.planet_phobos);
        planetImage.put("Deimos", R.drawable.planet_deimos);
        planetImage.put("Ceres", R.drawable.planet_ceres);
        planetImage.put("Jupiter", R.drawable.planet_jupiter);
        planetImage.put("Europa", R.drawable.planet_europa);
        planetImage.put("Saturn", R.drawable.planet_saturn);
        planetImage.put("Uranus", R.drawable.planet_uranus);
        planetImage.put("Neptune", R.drawable.planet_neptune);
        planetImage.put("Pluto", R.drawable.planet_pluto);
        planetImage.put("Sedna", R.drawable.planet_sedna);
        planetImage.put("Eris", R.drawable.planet_eris);
        planetImage.put("Void", R.drawable.planet_void);
        planetImage.put("Lua", R.drawable.planet_lua);
        planetImage.put("Kuva Fortress", R.drawable.planet_kuva_fortress);
        planetImage.put("Veil", R.drawable.planet_veil);
        return planetImage;
    }

    private static HashMap<String, Integer> setPlanetSquareImage() {
        HashMap<String, Integer> planetImage = new HashMap<>();
        planetImage.put("Mercury", R.drawable.planet_square_mercury);
        planetImage.put("Venus", R.drawable.planet_square_venus);
        planetImage.put("Earth", R.drawable.planet_square_earth);
        planetImage.put("Mars", R.drawable.planet_square_mars);
        planetImage.put("Phobos", R.drawable.planet_square_phobos);
        planetImage.put("Deimos", R.drawable.planet_square_deimos);
        planetImage.put("Ceres", R.drawable.planet_square_ceres);
        planetImage.put("Jupiter", R.drawable.planet_square_jupiter);
        planetImage.put("Europa", R.drawable.planet_square_europa);
        planetImage.put("Saturn", R.drawable.planet_square_saturn);
        planetImage.put("Uranus", R.drawable.planet_square_uranus);
        planetImage.put("Neptune", R.drawable.planet_square_neptune);
        planetImage.put("Pluto", R.drawable.planet_square_pluto);
        planetImage.put("Sedna", R.drawable.planet_square_sedna);
        planetImage.put("Eris", R.drawable.planet_square_eris);
        planetImage.put("Void", R.drawable.planet_square_void);
        planetImage.put("Lua", R.drawable.planet_square_lua);
        planetImage.put("Kuva Fortress", R.drawable.planet_square_kuva_fortress);
        planetImage.put("Veil", R.drawable.planet_square_veil);
        return planetImage;
    }

    private static HashMap<String, Integer> setPlanetTopImage() {
        HashMap<String, Integer> planetImage = new HashMap<>();
        planetImage.put("Mercury", R.drawable.planet_top_mercury);
        planetImage.put("Venus", R.drawable.planet_top_venus);
        planetImage.put("Earth", R.drawable.planet_top_earth);
        planetImage.put("Mars", R.drawable.planet_top_mars);
        planetImage.put("Phobos", R.drawable.planet_top_phobos);
        planetImage.put("Deimos", R.drawable.planet_top_deimos);
        planetImage.put("Ceres", R.drawable.planet_top_ceres);
        planetImage.put("Jupiter", R.drawable.planet_top_jupiter);
        planetImage.put("Europa", R.drawable.planet_top_europa);
        planetImage.put("Saturn", R.drawable.planet_top_saturn);
        planetImage.put("Uranus", R.drawable.planet_top_uranus);
        planetImage.put("Neptune", R.drawable.planet_top_neptune);
        planetImage.put("Pluto", R.drawable.planet_top_pluto);
        planetImage.put("Sedna", R.drawable.planet_top_sedna);
        planetImage.put("Eris", R.drawable.planet_top_eris);
        planetImage.put("Void", R.drawable.planet_top_void);
        planetImage.put("Lua", R.drawable.planet_top_lua);
        planetImage.put("Kuva Fortress", R.drawable.planet_top_kuva_fortress);
        planetImage.put("Veil", R.drawable.planet_top_veil);
        return planetImage;
    }

    public static int getImageComponent(String name, String prime, String primeType) {
        int image = PrimeTypeImage.get(primeType);
        //Set image for the type of prime
        switch (name) {
            //Warframe / Sentinel / Archwing
            case BLUEPRINT: return PrimeImage.get(prime);

            case NEUROPTICS:
            case CEREBRUM: return R.drawable.component_neuroptics;

            case CHASSIS:
            case CARAPACE: return R.drawable.component_chassis;

            case SYSTEMS:
                if (primeType.equals(WARFRAME) || primeType.equals(SENTINEL))
                    return R.drawable.component_systems;
                if (primeType.equals(ARCHWING))
                    return R.drawable.component_archwing_systems;

            case HARNESS: return R.drawable.component_archwing_harness;
            case WINGS: return R.drawable.component_archwing_wings;
            //WEAPONS
            case BARREL: return R.drawable.component_barrel;
            case RECEIVER: return R.drawable.component_receiver;

            case STOCK:
            case CHAIN:
            case STRING: return R.drawable.component_stock;

            case BLADE:
            case LOWER_LIMB:
            case UPPER_LIMB:
            case STARS:
            case HEAD:
            case DISC: return R.drawable.component_blade;

            case BOOT:
            case GUARD: return R.drawable.component_boot;

            case GRIP:
            case POUCH:
            case BAND: return R.drawable.component_grip;

            case HANDLE:
            case HILT:
            case GAUNTLET: return R.drawable.component_handle;

            case BUCKLE:
            case ORNAMENT:
            case LINK: return R.drawable.component_ornament;
        }

        return image;
    }

    public static boolean isComponentBP(String type, String primeType) {
        switch (type) {
            case BLUEPRINT:
            case NEUROPTICS:
            case CHASSIS:
            case HARNESS:
            case WINGS: return true;

            case SYSTEMS: return !primeType.equals(SENTINEL);

            case CEREBRUM:
            case CARAPACE:
            case BARREL:
            case RECEIVER:
            case STOCK:
            case CHAIN:
            case STRING:
            case BLADE:
            case LOWER_LIMB:
            case UPPER_LIMB:
            case STARS:
            case HEAD:
            case DISC:
            case BOOT:
            case GUARD:
            case GRIP:
            case POUCH:
            case BAND:
            case BUCKLE:
            case HANDLE:
            case HILT:
            case GAUNTLET:
            case ORNAMENT:
            case LINK: return false;
        }
        return true;
    }
}
