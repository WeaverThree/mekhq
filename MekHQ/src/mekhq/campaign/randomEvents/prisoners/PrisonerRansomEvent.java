/*
 * Copyright (c) 2025 - The MegaMek Team. All Rights Reserved.
 *
 * This file is part of MekHQ.
 *
 * MekHQ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MekHQ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MekHQ. If not, see <http://www.gnu.org/licenses/>.
 */
package mekhq.campaign.randomEvents.prisoners;

import mekhq.campaign.Campaign;
import mekhq.campaign.finances.Money;
import mekhq.campaign.finances.enums.TransactionType;
import mekhq.campaign.personnel.Person;
import mekhq.gui.dialog.randomEvents.prisonerDialogs.PrisonerRansomEventDialog;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.max;
import static mekhq.campaign.personnel.enums.PersonnelStatus.ACTIVE;
import static mekhq.utilities.MHQInternationalization.getFormattedTextAt;

public class PrisonerRansomEvent {
    private static final String RESOURCE_BUNDLE = "mekhq.resources.PrisonerEvents";

    private static final int RANSOM_COST_DIVIDER = 10;
    private static final int RANSOM_COST_MULTIPLIER = 10;
    private static final int ACCEPTED = 1; // Choice for accepting ransom
    private static final double RANSOM_PERCENTAGE = 0.1; // Allow 10% of prisoners to be ransomed in any given event

    public PrisonerRansomEvent(Campaign campaign, boolean isFriendlyPOWs) {
        List<Person> prisoners = isFriendlyPOWs
            ? campaign.getFriendlyPrisoners()
            : campaign.getCurrentPrisoners();

        // Exit early if there are no prisoners
        if (prisoners.isEmpty()) {
            return;
        }

        // Sort prisoners by experience level in descending order
        if (!isFriendlyPOWs) {
            // The OpFor always requests their best personnel first
            prisoners.sort(Comparator.comparing(person -> person.getExperienceLevel(campaign, false),
                Comparator.reverseOrder()));
        } else {
            // The OpFor offers ransom POWs
            Collections.shuffle(prisoners);
        }

        int prisonerRansomCount = calculateRansomCount(prisoners.size());
        List<Person> ransomList = prisoners.subList(0, prisonerRansomCount);

        Money totalRansom = calculateTotalRansom(ransomList, campaign, isFriendlyPOWs);

        // Handle friendly POWs (player's prisoners) specifically
        if (isFriendlyPOWs && !canAffordRansom(campaign, totalRansom)) {
            return; // Exit if funds are insufficient to cover the ransom
        }

        // Launch ransom dialog to ask for the player's decision
        PrisonerRansomEventDialog eventDialog = new PrisonerRansomEventDialog(campaign, ransomList,
            totalRansom, isFriendlyPOWs);
        int choice = eventDialog.getDialogChoice();

        if (choice == ACCEPTED) {
            handleRansomOutcome(campaign, ransomList, totalRansom, isFriendlyPOWs);
        }
    }

    private int calculateRansomCount(int prisonerCount) {
        return max(1, (int) Math.ceil(prisonerCount * RANSOM_PERCENTAGE));
    }

    private Money calculateTotalRansom(List<Person> ransomList, Campaign campaign, boolean isFriendlyPOWs) {
        Money ransom = Money.zero();
        for (Person person : ransomList) {
            Money ransomValue = person.getRansomValue(campaign);
            ransom = ransom.plus(ransomValue);
        }

        return isFriendlyPOWs
            ? ransom.multipliedBy(RANSOM_COST_MULTIPLIER)
            : ransom.dividedBy(RANSOM_COST_DIVIDER);
    }

    private boolean canAffordRansom(Campaign campaign, Money ransom) {
        Money currentFunds = campaign.getFinances().getBalance();
        return !currentFunds.isLessThan(ransom);
    }

    private void handleRansomOutcome(Campaign campaign, List<Person> ransomList, Money ransom,
                                  boolean isFriendlyPOWs) {
        if (isFriendlyPOWs) {
            // Debit funds and return POWs to active duty
            campaign.getFinances().debit(TransactionType.RANSOM, campaign.getLocalDate(), ransom,
                getFormattedTextAt(RESOURCE_BUNDLE, "ransom.entry"));
            ransomList.forEach(pow -> pow.changeStatus(campaign, campaign.getLocalDate(), ACTIVE));
        } else {
            // Credit funds and remove enemy prisoners from campaign
            campaign.getFinances().credit(TransactionType.RANSOM, campaign.getLocalDate(), ransom,
                getFormattedTextAt(RESOURCE_BUNDLE, "ransom.entry"));
            ransomList.forEach(campaign::removePerson);
        }
    }
}
