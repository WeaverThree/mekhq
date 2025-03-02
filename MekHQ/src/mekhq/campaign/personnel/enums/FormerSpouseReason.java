/*
 * Copyright (C) 2020-2025 The MegaMek Team
 *
 * This file is part of MekHQ.
 *
 * MegaMek is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (GPL),
 * version 3 or (at your option) any later version,
 * as published by the Free Software Foundation.
 *
 * MegaMek is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * A copy of the GPL should have been included with this project;
 * if not, see <https://www.gnu.org/licenses/>.
 *
 * NOTICE: The MegaMek organization is a non-profit group of volunteers
 * creating free software for the BattleTech community. BattleMech,
 * BattleTech, and MechWarrior are trademarks of The Topps Company, Inc.
 * The MegaMek organization is not affiliated with The Topps Company, Inc.
 * or Catalyst Game Labs.
 */
package mekhq.campaign.personnel.enums;

import java.util.ResourceBundle;

import megamek.logging.MMLogger;
import mekhq.MekHQ;

public enum FormerSpouseReason {
    // region Enum Declarations
    WIDOWED("FormerSpouseReason.WIDOWED.text"),
    DIVORCE("FormerSpouseReason.DIVORCE.text");
    // endregion Enum Declarations

    // region Variable Declarations
    private final String name;
    // endregion Variable Declarations

    // region Constructors
    FormerSpouseReason(final String name) {
        final ResourceBundle resources = ResourceBundle.getBundle("mekhq.resources.Personnel",
                MekHQ.getMHQOptions().getLocale());
        this.name = resources.getString(name);
    }
    // endregion Constructors

    // region Boolean Comparison Methods
    public boolean isWidowed() {
        return this == WIDOWED;
    }

    public boolean isDivorce() {
        return this == DIVORCE;
    }
    // endregion Boolean Comparison Methods

    // region File I/O
    public static FormerSpouseReason parseFromString(final String text) {
        try {
            return valueOf(text);
        } catch (Exception ignored) {

        }

        try {
            switch (Integer.parseInt(text)) {
                case 0:
                    return WIDOWED;
                case 1:
                    return DIVORCE;
                default:
                    break;
            }
        } catch (Exception ignored) {

        }

        MMLogger.create(FormerSpouseReason.class)
                .error("Unable to parse " + text + " into a FormerSpouseReason. Returning WIDOWED.");
        return WIDOWED;
    }
    // endregion File I/O

    @Override
    public String toString() {
        return name;
    }
}
