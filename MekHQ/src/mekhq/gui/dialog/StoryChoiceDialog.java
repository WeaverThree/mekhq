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
package mekhq.gui.dialog;

import mekhq.campaign.storyarc.storyevent.ChoiceStoryEvent;
import mekhq.gui.utilities.MarkdownRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class StoryChoiceDialog extends StoryDialog {

    private ChoiceStoryEvent storyEvent;

    private ButtonGroup choiceGroup;

    //region Constructors
    public StoryChoiceDialog(final JFrame parent, ChoiceStoryEvent sEvent) {
        super(parent, sEvent.getTitle());
        this.storyEvent = sEvent;
        initialize();
    }
    //endregion Constructors

    //region Initialization

    @Override
    protected Container getMainPanel() {

        JPanel mainPanel = new JPanel(new BorderLayout());

        //TODO: put images here

        JTextPane txtDesc = new JTextPane();
        txtDesc.setEditable(false);
        txtDesc.setContentType("text/html");
        txtDesc.setText(MarkdownRenderer.getRenderedHtml(storyEvent.getQuestion()));
        JScrollPane scrollPane = new JScrollPane(txtDesc);
        mainPanel.add(scrollPane, BorderLayout.PAGE_START);

        //Create the radio buttons.
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.PAGE_AXIS));
        choiceGroup = new ButtonGroup();
        JRadioButton radioBtn;
        boolean firstEntry = true;
        for (Map.Entry<String, String> entry : storyEvent.getChoices().entrySet()) {
            radioBtn = new JRadioButton(entry.getValue());
            radioBtn.setActionCommand(entry.getKey());
            btnPanel.add(radioBtn);
            choiceGroup.add(radioBtn);
            if(firstEntry) {
                radioBtn.setSelected(true);
            }
            firstEntry = false;
        }

        mainPanel.add(btnPanel, BorderLayout.CENTER);

        return mainPanel;
    }
    //endregion Initialization


    @Override
    protected void setDialogSize() {
        setMinimumSize(new Dimension(400, 400));
        setPreferredSize(new Dimension(400, 400));
        setMaximumSize(new Dimension(400, 400));
    }

    public String getChoice() {
        return choiceGroup.getSelection().getActionCommand();
    }

}
