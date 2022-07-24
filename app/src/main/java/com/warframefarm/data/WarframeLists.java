package com.warframefarm.data;

import static com.warframefarm.data.WarframeConstants.ARCHWING;
import static com.warframefarm.data.WarframeConstants.ARCH_GUN;
import static com.warframefarm.data.WarframeConstants.ARENA;
import static com.warframefarm.data.WarframeConstants.ASSASSINATION;
import static com.warframefarm.data.WarframeConstants.ASSAULT;
import static com.warframefarm.data.WarframeConstants.AXI;
import static com.warframefarm.data.WarframeConstants.BAND;
import static com.warframefarm.data.WarframeConstants.BARREL;
import static com.warframefarm.data.WarframeConstants.BLADE;
import static com.warframefarm.data.WarframeConstants.BLUEPRINT;
import static com.warframefarm.data.WarframeConstants.BOOT;
import static com.warframefarm.data.WarframeConstants.BUCKLE;
import static com.warframefarm.data.WarframeConstants.CAPTURE;
import static com.warframefarm.data.WarframeConstants.CARAPACE;
import static com.warframefarm.data.WarframeConstants.CEREBRUM;
import static com.warframefarm.data.WarframeConstants.CHAIN;
import static com.warframefarm.data.WarframeConstants.CHASSIS;
import static com.warframefarm.data.WarframeConstants.CORPUS;
import static com.warframefarm.data.WarframeConstants.CORRUPTED;
import static com.warframefarm.data.WarframeConstants.CROSSFIRE;
import static com.warframefarm.data.WarframeConstants.DEFECTION;
import static com.warframefarm.data.WarframeConstants.DEFENSE;
import static com.warframefarm.data.WarframeConstants.DISC;
import static com.warframefarm.data.WarframeConstants.DISRUPTION;
import static com.warframefarm.data.WarframeConstants.EXCAVATION;
import static com.warframefarm.data.WarframeConstants.EXTERMINATE;
import static com.warframefarm.data.WarframeConstants.GAUNTLET;
import static com.warframefarm.data.WarframeConstants.GRINEER;
import static com.warframefarm.data.WarframeConstants.GRIP;
import static com.warframefarm.data.WarframeConstants.GUARD;
import static com.warframefarm.data.WarframeConstants.HANDLE;
import static com.warframefarm.data.WarframeConstants.HARNESS;
import static com.warframefarm.data.WarframeConstants.HEAD;
import static com.warframefarm.data.WarframeConstants.HIJACK;
import static com.warframefarm.data.WarframeConstants.HILT;
import static com.warframefarm.data.WarframeConstants.INFESTED;
import static com.warframefarm.data.WarframeConstants.INFESTED_SALVAGE;
import static com.warframefarm.data.WarframeConstants.INTERCEPTION;
import static com.warframefarm.data.WarframeConstants.LINK;
import static com.warframefarm.data.WarframeConstants.LITH;
import static com.warframefarm.data.WarframeConstants.LOWER_LIMB;
import static com.warframefarm.data.WarframeConstants.MELEE;
import static com.warframefarm.data.WarframeConstants.MESO;
import static com.warframefarm.data.WarframeConstants.MOBILE_DEFENSE;
import static com.warframefarm.data.WarframeConstants.NEO;
import static com.warframefarm.data.WarframeConstants.NEUROPTICS;
import static com.warframefarm.data.WarframeConstants.ORNAMENT;
import static com.warframefarm.data.WarframeConstants.OROKIN;
import static com.warframefarm.data.WarframeConstants.ORPHIX;
import static com.warframefarm.data.WarframeConstants.PET;
import static com.warframefarm.data.WarframeConstants.POUCH;
import static com.warframefarm.data.WarframeConstants.PRIMARY;
import static com.warframefarm.data.WarframeConstants.PURSUIT;
import static com.warframefarm.data.WarframeConstants.RATHUUM;
import static com.warframefarm.data.WarframeConstants.RECEIVER;
import static com.warframefarm.data.WarframeConstants.RESCUE;
import static com.warframefarm.data.WarframeConstants.RUSH;
import static com.warframefarm.data.WarframeConstants.SABOTAGE;
import static com.warframefarm.data.WarframeConstants.SECONDARY;
import static com.warframefarm.data.WarframeConstants.SENTIENT;
import static com.warframefarm.data.WarframeConstants.SENTINEL;
import static com.warframefarm.data.WarframeConstants.SKIRMISH;
import static com.warframefarm.data.WarframeConstants.SPY;
import static com.warframefarm.data.WarframeConstants.STARS;
import static com.warframefarm.data.WarframeConstants.STOCK;
import static com.warframefarm.data.WarframeConstants.STRING;
import static com.warframefarm.data.WarframeConstants.SURVIVAL;
import static com.warframefarm.data.WarframeConstants.SYSTEMS;
import static com.warframefarm.data.WarframeConstants.UPPER_LIMB;
import static com.warframefarm.data.WarframeConstants.VOID_ARMAGEDDON;
import static com.warframefarm.data.WarframeConstants.VOID_CASCADE;
import static com.warframefarm.data.WarframeConstants.VOID_FLOOD;
import static com.warframefarm.data.WarframeConstants.VOLATILE;
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
            RelicEraImage = setRelicEraImage(),
            RelicEraRadiantImage = setRelicEraRadiantImage(),
            FactionImage = setFactionImage(),
            MissionName = setMissionName(),
            MissionDescription = setMissionDescription(),
            MissionRotationsInfo = setMissionRotationsInfo(),
            MissionEfficiencyInfo = setMissionEfficiencyInfo();
    public static HashMap<Integer, Integer> RelicRarityImage = setRelicRarityImage(),
            MissionTypeImage = setMissionTypeImage();

    private static HashMap<String, Integer> setPrimeTypeImage() {
        HashMap<String, Integer> primeTypeImage = new HashMap<>();
        primeTypeImage.put(WARFRAME, R.drawable.type_warframe);
        primeTypeImage.put(ARCHWING, R.drawable.type_archwing);
        primeTypeImage.put(PET, R.drawable.type_pet);
        primeTypeImage.put(SENTINEL, R.drawable.type_sentinel);
        primeTypeImage.put(PRIMARY, R.drawable.type_primary);
        primeTypeImage.put(SECONDARY, R.drawable.type_secondary);
        primeTypeImage.put(MELEE, R.drawable.type_melee);
        primeTypeImage.put(ARCH_GUN, R.drawable.type_archgun);
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

    private static HashMap<String, Integer> setMissionName() {
        HashMap<String, Integer> missionName = new HashMap<>();
        missionName.put(ARENA, R.string.arena_mission);
        missionName.put(ASSASSINATION, R.string.assassination_mission);
        missionName.put(ASSAULT, R.string.assault_mission);
        missionName.put(CAPTURE, R.string.capture_mission);
        missionName.put(DEFECTION, R.string.defection_mission);
        missionName.put(DEFENSE, R.string.defense_mission);
        missionName.put(DISRUPTION, R.string.disruption_mission);
        missionName.put(EXCAVATION, R.string.excavation_mission);
        missionName.put(EXTERMINATE, R.string.exterminate_mission);
        missionName.put(HIJACK, R.string.hijack_mission);
        missionName.put(INFESTED_SALVAGE, R.string.infested_salvage_mission);
        missionName.put(INTERCEPTION, R.string.interception_mission);
        missionName.put(MOBILE_DEFENSE, R.string.mobile_defense_mission);
        missionName.put(PURSUIT, R.string.pursuit_mission);
        missionName.put(RATHUUM, R.string.rathuum_mission);
        missionName.put(RESCUE, R.string.rescue_mission);
        missionName.put(RUSH, R.string.rush_mission);
        missionName.put(SABOTAGE, R.string.sabotage_mission);
        missionName.put(SKIRMISH, R.string.skirmish_mission);
        missionName.put(SPY, R.string.spy_mission);
        missionName.put(SURVIVAL, R.string.survival_mission);
        missionName.put(VOID_ARMAGEDDON, R.string.void_armageddon_mission);
        missionName.put(VOID_CASCADE, R.string.void_cascade_mission);
        missionName.put(VOID_FLOOD, R.string.void_flood_mission);
        missionName.put(VOLATILE, R.string.volatile_mission);
        missionName.put(ORPHIX, R.string.orphix_mission);
        return missionName;
    }

    private static HashMap<String, Integer> setMissionDescription() {
        HashMap<String, Integer> missionDescription = new HashMap<>();
        missionDescription.put(ARENA, R.string.arena_description);
        missionDescription.put(ASSASSINATION, R.string.assassination_description);
        missionDescription.put(ASSAULT, R.string.assault_description);
        missionDescription.put(CAPTURE, R.string.capture_description);
        missionDescription.put(DEFECTION, R.string.defection_description);
        missionDescription.put(DEFENSE, R.string.defense_description);
        missionDescription.put(DISRUPTION, R.string.disruption_description);
        missionDescription.put(EXCAVATION, R.string.excavation_description);
        missionDescription.put(EXTERMINATE, R.string.exterminate_description);
        missionDescription.put(HIJACK, R.string.hijack_description);
        missionDescription.put(INFESTED_SALVAGE, R.string.infested_salvage_description);
        missionDescription.put(INTERCEPTION, R.string.interception_description);
        missionDescription.put(MOBILE_DEFENSE, R.string.mobile_defense_description);
        missionDescription.put(PURSUIT, R.string.pursuit_description);
        missionDescription.put(RATHUUM, R.string.rathuum_description);
        missionDescription.put(RESCUE, R.string.rescue_description);
        missionDescription.put(RUSH, R.string.rush_description);
        missionDescription.put(SABOTAGE, R.string.sabotage_description);
        missionDescription.put(SKIRMISH, R.string.skirmish_description);
        missionDescription.put(SPY, R.string.spy_description);
        missionDescription.put(SURVIVAL, R.string.survival_description);
        missionDescription.put(VOID_ARMAGEDDON, R.string.void_armageddon_description);
        missionDescription.put(VOID_CASCADE, R.string.void_cascade_description);
        missionDescription.put(VOID_FLOOD, R.string.void_flood_description);
        missionDescription.put(VOLATILE, R.string.volatile_description);
        missionDescription.put(ORPHIX, R.string.orphix_description);
        return missionDescription;
    }

    private static HashMap<String, Integer> setMissionRotationsInfo() {
        HashMap<String, Integer> missionRotationsInfo = new HashMap<>();
        missionRotationsInfo.put(DEFECTION, R.string.defection_rotations);
        missionRotationsInfo.put(DEFENSE, R.string.defense_rotations);
        missionRotationsInfo.put(DISRUPTION, R.string.disruption_rotations);
        missionRotationsInfo.put(EXCAVATION, R.string.excavation_rotations);
        missionRotationsInfo.put(INFESTED_SALVAGE, R.string.infested_salvage_rotations);
        missionRotationsInfo.put(INTERCEPTION, R.string.interception_rotations);
        missionRotationsInfo.put(RUSH, R.string.rush_rotations);
        missionRotationsInfo.put(SPY, R.string.spy_rotations);
        missionRotationsInfo.put(SURVIVAL, R.string.survival_rotations);
        missionRotationsInfo.put(ORPHIX, R.string.orphix_rotations);
        return missionRotationsInfo;
    }

    private static HashMap<String, Integer> setMissionEfficiencyInfo() {
        HashMap<String, Integer> missionRotationsInfo = new HashMap<>();
        missionRotationsInfo.put(DISRUPTION, R.string.disruption_efficiency);
        return missionRotationsInfo;
    }

    private static HashMap<Integer, Integer> setRelicRarityImage() {
        HashMap<Integer, Integer> relicRarityImage = new HashMap<>();
        relicRarityImage.put(REWARD_COMMON, R.drawable.reward_common);
        relicRarityImage.put(REWARD_UNCOMMON, R.drawable.reward_uncommon);
        relicRarityImage.put(REWARD_RARE, R.drawable.reward_rare);
        return relicRarityImage;
    }

    private static HashMap<Integer, Integer> setMissionTypeImage() {
        HashMap<Integer, Integer> missionTypeImage = new HashMap<>();
        missionTypeImage.put(TYPE_ARCHWING, R.drawable.archwing);
        missionTypeImage.put(TYPE_EMPYREAN, R.drawable.empyrean);
        return missionTypeImage;
    }

    public static int getImageComponent(String name, String primeType) {
        //Set image for the type of prime
        switch (name) {
            //Warframe / Sentinel / Archwing
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

        return R.color.transparent;
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
