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
package mekhq.gui.dialog.randomEvents.prisonerDialogs;

import megamek.common.annotations.Nullable;
import mekhq.campaign.Campaign;
import mekhq.campaign.personnel.Person;
import mekhq.gui.baseComponents.MHQDialogImmersive;

import java.util.List;

import static mekhq.utilities.MHQInternationalization.getFormattedTextAt;

public class PrisonerWarningDialog extends MHQDialogImmersive {
    private static final String RESOURCE_BUNDLE = "mekhq.resources.PrisonerEvents";

    public PrisonerWarningDialog(Campaign campaign, @Nullable Person speaker,
                                 @Nullable Integer executeCount, @Nullable Integer freeCount) {
        super(campaign, speaker, null, createInCharacterMessage(campaign),
            createButtons( executeCount, freeCount), createOutOfCharacterMessage(),
            null);
    }

    private static List<ButtonLabelTooltipPair> createButtons(@Nullable Integer executeCount,
                                                              @Nullable Integer freeCount) {
        ButtonLabelTooltipPair btnDoNothing = new ButtonLabelTooltipPair(
            getFormattedTextAt(RESOURCE_BUNDLE, "btnDoNothing.button"),
            getFormattedTextAt(RESOURCE_BUNDLE, "btnDoNothing.tooltip"));
        ButtonLabelTooltipPair btnFree = new ButtonLabelTooltipPair(
            getFormattedTextAt(RESOURCE_BUNDLE, "free.button", freeCount),
            getFormattedTextAt(RESOURCE_BUNDLE, "free.tooltip"));
        ButtonLabelTooltipPair btnExecute = new ButtonLabelTooltipPair(
            getFormattedTextAt(RESOURCE_BUNDLE, "execute.button", executeCount),
            getFormattedTextAt(RESOURCE_BUNDLE, "execute.tooltip"));

        return List.of(btnDoNothing, btnFree, btnExecute);
    }

    private static String createInCharacterMessage(Campaign campaign) {
        String commanderAddress = campaign.getCommanderAddress(false);
        return getFormattedTextAt(RESOURCE_BUNDLE, "warning.message", commanderAddress);
    }

    private static String createOutOfCharacterMessage() {
        return getFormattedTextAt(RESOURCE_BUNDLE, "warning.ooc");
    }
}
