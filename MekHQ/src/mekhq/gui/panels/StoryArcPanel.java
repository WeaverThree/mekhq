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
package mekhq.gui.panels;

import megamek.client.ui.baseComponents.MMButton;
import megamek.common.annotations.Nullable;
import mekhq.campaign.Campaign;
import mekhq.campaign.storyarcs.StoryArc;
import mekhq.gui.baseComponents.AbstractMHQPanel;

import javax.swing.*;
import java.awt.*;

/**
 * This class displays a Story Arc.
 */
public class StoryArcPanel extends AbstractMHQPanel {
    //region Variable Declarations
    private final Campaign campaign;
    private StoryArc storyArc;
    private JLabel lblTitle;
    private JTextArea txtDescription;
    //endregion Variable Declarations

    //region Constructors
    public StoryArcPanel(final JFrame frame, final @Nullable Campaign campaign,
                               final @Nullable StoryArc arc) {
        super(frame, "CampaignPresetPanel");
        this.campaign = campaign;
        setStoryArc(arc);
        initialize();
    }
    //endregion Constructors

    //region Getters/Setters
    public @Nullable Campaign getCampaign() {
        return campaign;
    }

    public @Nullable StoryArc getStoryArc() {
        return storyArc;
    }

    public void setStoryArc(final @Nullable StoryArc arc) {
        this.storyArc = arc;
    }

    public JLabel getLblTitle() {
        return lblTitle;
    }

    public void setLblTitle(final JLabel lblTitle) {
        this.lblTitle = lblTitle;
    }

    public JTextArea getTxtDescription() {
        return txtDescription;
    }

    public void setTxtDescription(final JTextArea txtDescription) {
        this.txtDescription = txtDescription;
    }
    //endregion Getters/Setters

    //region Initialization
    @Override
    protected void initialize() {
        //TODO: eventually we can use this same selector to allow users to edit story arcs
        //final boolean editStoryArc = (getCampaign() != null) && (getStoryArc() != null) && getStoryArc().isUserData();

        // Setup the Panel
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createLineBorder(Color.BLACK, 2)));
        setName("storyArcPanel");
        setLayout(new GridBagLayout());

        // Create the Constraints
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;

        // Create Components and Layout
        setLblTitle(new JLabel(""));
        getLblTitle().setName("lblTitle");
        getLblTitle().setAlignmentX(Component.CENTER_ALIGNMENT);
        add(getLblTitle(), gbc);

        /*
        if (editPreset) { // TODO : Add a way to access this
            final JButton btnEditPreset = new MMButton("btnEditPreset", resources.getString("Edit.text"),
                    resources.getString("btnEditPreset.toolTipText"), evt -> {
                final CreateCampaignPresetDialog dialog = new CreateCampaignPresetDialog(
                        getFrame(), getCampaign(), getPreset());
                if (dialog.showDialog().isConfirmed()) {
                    updateFromPreset(getPreset());
                }
            });
            gbc.gridx++;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            add(btnEditPreset, gbc);
        }
         */

        setTxtDescription(new JTextArea(""));
        getTxtDescription().setName("txtDescription");
        getTxtDescription().setMinimumSize(new Dimension(400, 120));
        getTxtDescription().setEditable(false);
        getTxtDescription().setLineWrap(true);
        getTxtDescription().setWrapStyleWord(true);
        gbc.gridx = 0;
        gbc.gridy++;
        //gbc.gridwidth = editPreset ? 2 : 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.SOUTH;
        add(getTxtDescription(), gbc);
    }
    //endregion Initialization

    protected void updateFromPreset(final StoryArc arc) {
        getLblTitle().setText(arc.getTitle());
        getTxtDescription().setText(arc.getDescription());
    }
}
