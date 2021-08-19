/*
 * Copyright (c) 2021 - The MegaMek Team. All Rights Reserved.
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
package mekhq.gui.dialog.reportDialogs;

import mekhq.campaign.report.CargoReport;

import javax.swing.*;

public class CargoReportDialog extends AbstractReportDialog {
    //region Variable Declarations
    private final CargoReport cargoReport;
    //endregion Variable Declarations

    //region Constructors
    public CargoReportDialog(final JFrame frame, final CargoReport cargoReport) {
        super(frame, "CargoReportDialog", "CargoReportDialog.title");
        this.cargoReport = cargoReport;
        initialize();
    }
    //endregion Constructors

    //region Getters
    public CargoReport getCargoReport() {
        return cargoReport;
    }

    @Override
    protected JTextPane createTxtReport() {
        final JTextPane txtReport = new JTextPane();
        txtReport.setText(getCargoReport().getCargoDetails());
        txtReport.setName("txtReport");
        txtReport.setEditable(false);
        txtReport.setCaretPosition(0);
        return txtReport;
    }
    //endregion Getters
}
