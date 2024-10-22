/*
 * Copyright (c) 2024 - The MegaMek Team. All Rights Reserved.
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
package mekhq.campaign.universe;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mekhq.adapter.LocalDateAdapter;
import mekhq.campaign.universe.enums.HiringHallLevel;

import java.time.LocalDate;

/**
 * Class representing an "override" for the dynamic hiring hall system. Normally, hiring halls are
 * generated dynamically based on planetary system factors like tech level and HPG quality, but some
 * canonical systems should have hiring halls of certain qualities despite what the dynamic formula
 * says.
 * Overrides are stored as child elements of planetary systems in systems.xml, with a start date,
 * optional end date, and quality.
 */
@XmlRootElement(name = "hiringHall")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class HiringHallOverride {
    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    private LocalDate start = null;
    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    private LocalDate end = null;
    @XmlElement
    private HiringHallLevel level = HiringHallLevel.NONE;

    /**
     * Gets the level of the hiring hall for this override
     *
     * @return The hiring hall level as an enum
     */
    public HiringHallLevel getLevel() {
        return level;
    }

    /**
     * Sets the hiring hall level for this override
     *
     * @param level The level of hiring hall
     */
    public void setLevel(HiringHallLevel level) {
        this.level = level;
    }

    /**
     * Checks whether the hiring hall is active on a certain date. Returns true if no end date is
     * specified in the override.
     *
     * @param date The date to check whether the hiring hall is active
     * @return boolean representing whether the hiring hall is active
     */
    public boolean isActive(LocalDate date) {
        // Hall has no start date, so it's always inactive
        if (start == null) {
            return false;
        }
        // Hall has a start date and no end date, so it's always active
        if (end == null) {
            return true;
        }
        // Hall has a start date and end date, so it's only active between those dates
        return date.isAfter(start) && date.isBefore(end);
    }
}
