package mekhq.campaign.personnel.autoAwards;

import megamek.common.annotations.Nullable;
import mekhq.campaign.Campaign;
import mekhq.campaign.mission.AtBContract;
import mekhq.campaign.mission.Mission;
import mekhq.campaign.personnel.Award;
import mekhq.campaign.universe.Faction;
import mekhq.campaign.universe.PlanetarySystem;
import mekhq.campaign.universe.RandomFactionGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MiscAwards {

    /**
     * This function loops through Misc Awards, checking whether the person is eligible to receive each type of award.
     * All Misc awards need to be coded as individual functions
     *
     * @param campaign the current campaign
     * @param mission @Nullable the mission just completed (Null if no Mission was completed)
     * @param person the person to check award eligibility for
     * @param awards the awards to be processed (should only include awards where item == Kill)
     * @param missionWasSuccessful true if the completed mission was successful
     */
    public static Map<Integer, List<Object>> MiscAwardsProcessor(Campaign campaign, @Nullable Mission mission, UUID person, List<Award> awards, Boolean missionWasSuccessful) {
        List<Award> eligibleAwards = new ArrayList<>();

        for (Award award : awards) {
            switch (award.getRange().replaceAll("\\s","").toLowerCase()) {
                case "missionaccomplished":
                    if (missionWasSuccessful) {
                        if (MissionAccomplishedAward(campaign, award, person)) {
                            eligibleAwards.add(award);
                        }
                    }
                    break;
                case "houseworldnowar":
                    if (HouseWorldWar(campaign, mission, award, person, false)) {
                        eligibleAwards.add(award);
                    }
                    break;
                case "houseworldyeswar":
                    if (HouseWorldWar(campaign, mission, award, person, true)) {
                        eligibleAwards.add(award);
                    }
                    break;
                case "periphery":
                    if (Periphery(campaign, mission, award, person)) {
                        eligibleAwards.add(award);
                    }
                    break;
                default:
            }
        }

        return AutoAwardsController.prepareAwardData(person, eligibleAwards);
    }

    /**
     * This function checks whether Mission Accomplished awards can be awarded to Person
     *
     * @param campaign the current campaign
     * @param award the award to be processed
     * @param person the person to check award eligibility for
     */
    private static boolean MissionAccomplishedAward(Campaign campaign, Award award, UUID person) {
        return award.canBeAwarded(campaign.getPerson(person));
    }

    /**
     * This function checks whether House World War/No War awards can be awarded to Person
     *
     * @param campaign the current campaign
     * @param mission the Mission just completed
     * @param award the award to be processed
     * @param person the person to check award eligibility for
     * @param isYesWar true if this is a Yes War Award
     */
    private static boolean HouseWorldWar(Campaign campaign, @Nullable Mission mission, Award award, UUID person, boolean isYesWar) {
        if (award.canBeAwarded(campaign.getPerson(person))) {
            if (mission != null) {
                if (mission instanceof AtBContract) {
                    PlanetarySystem system = campaign.getSystemById(mission.getSystemId());
                    LocalDate date = campaign.getLocalDate();
                    Faction enemyFaction = ((AtBContract) mission).getEnemy();

                    for (Faction faction : system.getFactionSet(campaign.getLocalDate())) {
                        if (faction.isISMajorOrSuperPower()) {
                            boolean isAtWar = RandomFactionGenerator.getInstance().getFactionHints()
                                    .isAtWarWith(enemyFaction, faction, date);

                            if ((isAtWar) && (isYesWar)) {
                                return true;
                            } else if ((!isAtWar) && (!isYesWar)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * This method checks whether Periphery awards can be awarded to a person.
     *
     * @param campaign the current campaign
     * @param mission the mission just completed (nullable)
     * @param award the award to be processed
     * @param person the person to check award eligibility for
     * @return true if the person is eligible for the award, false otherwise
     */
    private static boolean Periphery(Campaign campaign, @Nullable Mission mission, Award award, UUID person) {
        if (award.canBeAwarded(campaign.getPerson(person))) {
            if (mission != null) {
                PlanetarySystem system = campaign.getSystemById(mission.getSystemId());

                return system.getFactionSet(campaign.getLocalDate()).stream().anyMatch(Faction::isPeriphery);

            }
        }
        return false;
    }
}
