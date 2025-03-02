/*
 * Copyright (c) 2019 Vicente Cartas Espinel (vicente.cartas at outlook.com). All rights reserved.
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

package mekhq.campaign.finances;

import java.io.IOException;
import java.util.Map;

import org.joda.money.BigMoney;
import org.joda.money.format.MoneyPrintContext;
import org.joda.money.format.MoneyPrinter;

/**
 * This is the writer used to write extra currency data based
 * on the currency code.
 *
 * @author Vicente Cartas Espinel (vicente.cartas at outlook.com)
 */
class CurrencyDataLookupWriter implements MoneyPrinter {
    private Map<String, String> currencyExtraData;

    CurrencyDataLookupWriter(Map<String, String> currencyExtraData) {
        this.currencyExtraData = currencyExtraData;
    }

    @Override
    public void print(MoneyPrintContext context, Appendable appendable, BigMoney money) throws IOException {
        appendable.append(this.currencyExtraData.get(money.getCurrencyUnit().getCode()));
    }
}
