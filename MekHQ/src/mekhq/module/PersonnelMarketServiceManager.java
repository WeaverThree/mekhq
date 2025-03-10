/*
 * Copyright (C) 2018-2025 The MegaMek Team. All Rights Reserved.
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
 * creating free software for the BattleTech community.
 *
 * MechWarrior, BattleMech, `Mech and AeroTech are registered trademarks
 * of The Topps Company, Inc. All Rights Reserved.
 *
 * Catalyst Game Labs and the Catalyst Game Labs logo are trademarks of
 * InMediaRes Productions, LLC.
 */
package mekhq.module;

import mekhq.module.api.PersonnelMarketMethod;

/**
 * Manager for services that provide methods for generating and removing potential recruits to and
 * from the personnel market
 *
 * @author Neoancient
 *
 */
public class PersonnelMarketServiceManager extends AbstractServiceManager<PersonnelMarketMethod> {

    private static PersonnelMarketServiceManager instance;

    private PersonnelMarketServiceManager() {
        super(PersonnelMarketMethod.class);
    }

    public static PersonnelMarketServiceManager getInstance() {
        if (null == instance) {
            instance = new PersonnelMarketServiceManager();
        }
        return instance;
    }
}
