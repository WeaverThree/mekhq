/*
 * Copyright (C) 2017-2025 The MegaMek Team. All Rights Reserved.
 *
 * This file is part of MekHQ.
 *
 * MekHQ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (GPL),
 * version 3 or (at your option) any later version,
 * as published by the Free Software Foundation.
 *
 * MekHQ is distributed in the hope that it will be useful,
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
package mekhq.gui;

import static megamek.client.ratgenerator.ForceDescriptor.RATING_5;
import static mekhq.campaign.mission.enums.MissionStatus.PARTIAL;
import static mekhq.campaign.mission.enums.MissionStatus.SUCCESS;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import megamek.client.bot.princess.BehaviorSettings;
import megamek.client.bot.princess.PrincessException;
import megamek.client.generator.ReconfigurationParameters;
import megamek.client.generator.TeamLoadOutGenerator;
import megamek.client.ui.baseComponents.MMComboBox;
import megamek.common.Entity;
import megamek.common.EntityListFile;
import megamek.common.Game;
import megamek.common.annotations.Nullable;
import megamek.common.containers.MunitionTree;
import megamek.common.event.Subscribe;
import megamek.common.options.OptionsConstants;
import megamek.common.util.sorter.NaturalOrderComparator;
import megamek.logging.MMLogger;
import megameklab.util.UnitPrintManager;
import mekhq.MekHQ;
import mekhq.campaign.autoresolve.AutoResolveMethod;
import mekhq.campaign.event.*;
import mekhq.campaign.finances.Money;
import mekhq.campaign.finances.enums.TransactionType;
import mekhq.campaign.force.CombatTeam;
import mekhq.campaign.mission.AtBContract;
import mekhq.campaign.mission.AtBDynamicScenario;
import mekhq.campaign.mission.AtBDynamicScenarioFactory;
import mekhq.campaign.mission.AtBScenario;
import mekhq.campaign.mission.BotForce;
import mekhq.campaign.mission.Contract;
import mekhq.campaign.mission.Mission;
import mekhq.campaign.mission.Scenario;
import mekhq.campaign.mission.atb.AtBScenarioFactory;
import mekhq.campaign.mission.enums.MissionStatus;
import mekhq.campaign.personnel.Person;
import mekhq.campaign.personnel.SkillType;
import mekhq.campaign.personnel.autoAwards.AutoAwardsController;
import mekhq.campaign.personnel.enums.PersonnelRole;
import mekhq.campaign.randomEvents.prisoners.PrisonerMissionEndEvent;
import mekhq.campaign.stratcon.StratconScenario;
import mekhq.campaign.unit.Unit;
import mekhq.campaign.universe.Faction;
import mekhq.campaign.universe.Factions;
import mekhq.gui.adapter.ScenarioTableMouseAdapter;
import mekhq.gui.dialog.CompleteMissionDialog;
import mekhq.gui.dialog.CustomizeAtBContractDialog;
import mekhq.gui.dialog.CustomizeMissionDialog;
import mekhq.gui.dialog.CustomizeScenarioDialog;
import mekhq.gui.dialog.MissionTypeDialog;
import mekhq.gui.dialog.NewAtBContractDialog;
import mekhq.gui.dialog.NewContractDialog;
import mekhq.gui.dialog.RetirementDefectionDialog;
import mekhq.gui.enums.MHQTabType;
import mekhq.gui.model.ScenarioTableModel;
import mekhq.gui.sorter.DateStringComparator;
import mekhq.gui.utilities.JScrollPaneWithSpeed;
import mekhq.gui.view.AtBScenarioViewPanel;
import mekhq.gui.view.LanceAssignmentView;
import mekhq.gui.view.MissionViewPanel;
import mekhq.gui.view.ScenarioViewPanel;
import mekhq.utilities.MHQInternationalization;

/**
 * Displays Mission/Contract and Scenario details.
 */
public final class BriefingTab extends CampaignGuiTab {
    private LanceAssignmentView panLanceAssignment;
    private JSplitPane splitScenario;
    private JTable scenarioTable;
    private MMComboBox<Mission> comboMission;
    private JScrollPane scrollMissionView;
    private JScrollPane scrollScenarioView;
    private JButton btnAddScenario;
    private JButton btnEditMission;
    private JButton btnCompleteMission;
    private JButton btnDeleteMission;
    private JButton btnGMGenerateScenarios;
    private JButton btnStartGame;
    private JButton btnJoinGame;
    private JButton btnLoadGame;
    private JButton btnPrintRS;
    private JButton btnGetMul;
    private JButton btnClearAssignedUnits;
    private JButton btnResolveScenario;
    private JButton btnAutoResolveScenario;

    private ScenarioTableModel scenarioModel;

    public int selectedScenario;

    private static final MMLogger logger = MMLogger.create(BriefingTab.class);

    // region Constructors
    public BriefingTab(CampaignGUI gui, String tabName) {
        super(gui, tabName);
        selectedScenario = -1;
        MekHQ.registerHandler(this);
    }
    // endregion Constructors

    @Override
    public MHQTabType tabType() {
        return MHQTabType.BRIEFING_ROOM;
    }

    /*
     * (non-Javadoc)
     *
     * @see mekhq.gui.CampaignGuiTab#initTab()
     */
    @Override
    public void initTab() {
        final ResourceBundle resourceMap = ResourceBundle.getBundle("mekhq.resources.CampaignGUI",
                MekHQ.getMHQOptions().getLocale());

        JPanel panMission = new JPanel(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        panMission.add(new JLabel(resourceMap.getString("lblMission.text")), gridBagConstraints);

        comboMission = new MMComboBox<>("comboMission");
        comboMission.addActionListener(ev -> changeMission());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.0;
        panMission.add(comboMission, gridBagConstraints);

        JPanel panMissionButtons = new JPanel(new GridLayout(2, 3));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.0;
        panMission.add(panMissionButtons, gridBagConstraints);

        JButton btnAddMission = new JButton(resourceMap.getString("btnAddMission.text"));
        btnAddMission.setToolTipText(resourceMap.getString("btnAddMission.toolTipText"));
        btnAddMission.addActionListener(ev -> addMission());
        panMissionButtons.add(btnAddMission);

        btnAddScenario = new JButton(resourceMap.getString("btnAddScenario.text"));
        btnAddScenario.setToolTipText(resourceMap.getString("btnAddScenario.toolTipText"));
        btnAddScenario.addActionListener(ev -> addScenario());
        panMissionButtons.add(btnAddScenario);

        btnEditMission = new JButton(resourceMap.getString("btnEditMission.text"));
        btnEditMission.setToolTipText(resourceMap.getString("btnEditMission.toolTipText"));
        btnEditMission.addActionListener(ev -> editMission());
        panMissionButtons.add(btnEditMission);

        btnCompleteMission = new JButton(resourceMap.getString("btnCompleteMission.text"));
        btnCompleteMission.setToolTipText(resourceMap.getString("btnCompleteMission.toolTipText"));
        btnCompleteMission.addActionListener(ev -> completeMission());
        panMissionButtons.add(btnCompleteMission);

        btnDeleteMission = new JButton(resourceMap.getString("btnDeleteMission.text"));
        btnDeleteMission.setToolTipText(resourceMap.getString("btnDeleteMission.toolTipText"));
        btnDeleteMission.setName("btnDeleteMission");
        btnDeleteMission.addActionListener(ev -> deleteMission());
        panMissionButtons.add(btnDeleteMission);

        btnGMGenerateScenarios = new JButton(resourceMap.getString("btnGMGenerateScenarios.text"));
        btnGMGenerateScenarios.setToolTipText(resourceMap.getString("btnGMGenerateScenarios.toolTipText"));
        btnGMGenerateScenarios.setName("btnGMGenerateScenarios");
        btnGMGenerateScenarios.addActionListener(ev -> gmGenerateScenarios());
        panMissionButtons.add(btnGMGenerateScenarios);

        scrollMissionView = new JScrollPaneWithSpeed();
        scrollMissionView.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollMissionView.setViewportView(null);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panMission.add(scrollMissionView, gridBagConstraints);

        scenarioModel = new ScenarioTableModel(getCampaign());
        scenarioTable = new JTable(scenarioModel);
        scenarioTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scenarioTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        TableRowSorter<ScenarioTableModel> scenarioSorter = new TableRowSorter<>(scenarioModel);
        scenarioSorter.setComparator(ScenarioTableModel.COL_NAME, new NaturalOrderComparator());
        scenarioSorter.setComparator(ScenarioTableModel.COL_DATE, new DateStringComparator());
        scenarioTable.setRowSorter(scenarioSorter);
        scenarioTable.setShowGrid(false);
        ScenarioTableMouseAdapter.connect(getCampaignGui(), scenarioTable, scenarioModel);
        for (int i = 0; i < ScenarioTableModel.N_COL; i++) {
            final TableColumn column = scenarioTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(scenarioModel.getColumnWidth(i));
            column.setCellRenderer(scenarioModel.getRenderer());
        }
        scenarioTable.setIntercellSpacing(new Dimension(0, 0));
        scenarioTable.getSelectionModel().addListSelectionListener(ev -> refreshScenarioView());

        JPanel panScenario = new JPanel(new GridBagLayout());

        JPanel panScenarioButtons = new JPanel(new GridLayout(3, 3));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.0;
        panScenario.add(panScenarioButtons, gridBagConstraints);

        btnStartGame = new JButton(resourceMap.getString("btnStartGame.text"));
        btnStartGame.setToolTipText(resourceMap.getString("btnStartGame.toolTipText"));
        btnStartGame.addActionListener(ev -> startScenario());
        btnStartGame.setEnabled(false);
        panScenarioButtons.add(btnStartGame);

        btnJoinGame = new JButton(resourceMap.getString("btnJoinGame.text"));
        btnJoinGame.setToolTipText(resourceMap.getString("btnJoinGame.toolTipText"));
        btnJoinGame.addActionListener(ev -> joinScenario());
        btnJoinGame.setEnabled(false);
        panScenarioButtons.add(btnJoinGame);

        btnLoadGame = new JButton(resourceMap.getString("btnLoadGame.text"));
        btnLoadGame.setToolTipText(resourceMap.getString("btnLoadGame.toolTipText"));
        btnLoadGame.addActionListener(ev -> loadScenario());
        btnLoadGame.setEnabled(false);
        panScenarioButtons.add(btnLoadGame);

        btnPrintRS = new JButton(resourceMap.getString("btnPrintRS.text"));
        btnPrintRS.setToolTipText(resourceMap.getString("btnPrintRS.toolTipText"));
        btnPrintRS.addActionListener(ev -> printRecordSheets());
        btnPrintRS.setEnabled(false);
        panScenarioButtons.add(btnPrintRS);

        btnGetMul = new JButton(resourceMap.getString("btnGetMul.text"));
        btnGetMul.setToolTipText(resourceMap.getString("btnGetMul.toolTipText"));
        btnGetMul.setName("btnGetMul");
        btnGetMul.addActionListener(ev -> deployListFile());
        btnGetMul.setEnabled(false);
        panScenarioButtons.add(btnGetMul);

        btnResolveScenario = new JButton(resourceMap.getString("btnResolveScenario.text"));
        btnResolveScenario.setToolTipText(resourceMap.getString("btnResolveScenario.toolTipText"));
        btnResolveScenario.addActionListener(ev -> resolveScenario());
        btnResolveScenario.setEnabled(false);
        panScenarioButtons.add(btnResolveScenario);

        btnAutoResolveScenario = new JButton(resourceMap.getString("btnAutoResolveScenario.text"));
        btnAutoResolveScenario.setToolTipText(resourceMap.getString("btnAutoResolveScenario.toolTipText"));
        btnAutoResolveScenario.addActionListener(ev -> autoResolveScenario());
        btnAutoResolveScenario.setEnabled(false);
        panScenarioButtons.add(btnAutoResolveScenario);

        btnClearAssignedUnits = new JButton(resourceMap.getString("btnClearAssignedUnits.text"));
        btnClearAssignedUnits.setToolTipText(resourceMap.getString("btnClearAssignedUnits.toolTipText"));
        btnClearAssignedUnits.addActionListener(ev -> clearAssignedUnits());
        btnClearAssignedUnits.setEnabled(false);
        panScenarioButtons.add(btnClearAssignedUnits);

        scrollScenarioView = new JScrollPaneWithSpeed();
        scrollScenarioView.setViewportView(null);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panScenario.add(scrollScenarioView, gridBagConstraints);

        /* ATB */
        panLanceAssignment = new LanceAssignmentView(getCampaign());
        JScrollPane paneLanceDeployment = new JScrollPaneWithSpeed(panLanceAssignment);
        paneLanceDeployment.setMinimumSize(new Dimension(200, 300));
        paneLanceDeployment.setPreferredSize(new Dimension(200, 300));
        paneLanceDeployment.setVisible(getCampaign().getCampaignOptions().isUseAtB());
        splitScenario = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panScenario,
                paneLanceDeployment);
        splitScenario.setOneTouchExpandable(true);
        splitScenario.setResizeWeight(1.0);

        JSplitPane splitBrief = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panMission, splitScenario);
        splitBrief.setOneTouchExpandable(true);
        splitBrief.setResizeWeight(0.5);
        splitBrief.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, ev -> refreshScenarioView());

        setLayout(new BorderLayout());
        add(splitBrief, BorderLayout.CENTER);
    }

    private void addMission() {
        MissionTypeDialog mtd = new MissionTypeDialog(getFrame(), true);
        mtd.setVisible(true);
        if (mtd.isContract()) {
            NewContractDialog ncd = getCampaignOptions().isUseAtB()
                    ? new NewAtBContractDialog(getFrame(), true, getCampaign())
                    : new NewContractDialog(getFrame(), true, getCampaign());
            ncd.setVisible(true);
            comboMission.setSelectedItem(ncd.getContract());
        }
         if (mtd.isMission()) {
            CustomizeMissionDialog cmd = new CustomizeMissionDialog(getFrame(), true, null, getCampaign());
            cmd.setVisible(true);
            comboMission.setSelectedItem(cmd.getMission());
        }
    }

    private void editMission() {
        final Mission mission = comboMission.getSelectedItem();
        if (mission == null) {
            return;
        }

        if (getCampaign().getCampaignOptions().isUseAtB() && (mission instanceof AtBContract)) {
            CustomizeAtBContractDialog cmd = new CustomizeAtBContractDialog(getFrame(), true,
                    (AtBContract) mission, getCampaign());
            cmd.setVisible(true);
            comboMission.setSelectedItem(cmd.getAtBContract());
        } else {
            CustomizeMissionDialog cmd = new CustomizeMissionDialog(getFrame(), true, mission, getCampaign());
            cmd.setVisible(true);
            comboMission.setSelectedItem(cmd.getMission());
        }
        MekHQ.triggerEvent(new MissionChangedEvent(mission));
    }

    private void completeMission() {
        ResourceBundle resources = ResourceBundle.getBundle("mekhq.resources.GUI",
                MekHQ.getMHQOptions().getLocale());

        final Mission mission = comboMission.getSelectedItem();

        if (mission == null) {
            return;
        } else if (mission.hasPendingScenarios()) {
            JOptionPane.showMessageDialog(getFrame(), "You cannot complete a mission that has pending scenarios",
                    "Pending Scenarios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        final CompleteMissionDialog cmd = new CompleteMissionDialog(getFrame());
        if (!cmd.showDialog().isConfirmed()) {
            return;
        }

        final MissionStatus status = cmd.getStatus();
        if (status.isActive()) {
            return;
        }

        PrisonerMissionEndEvent prisoners = null;
        if (mission instanceof Contract) {
            prisoners = new PrisonerMissionEndEvent(getCampaign(), (AtBContract) mission);

            if (!getCampaign().getPrisonerDefectors().isEmpty() && prisoners.handlePrisonerDefectors() == 0) { // This is the cancel choice index
                return;
            }
        }

        if (getCampaign().getCampaignOptions().isUseAtB() && (mission instanceof AtBContract)) {
            if (((AtBContract) mission).contractExtended(getCampaign())) {
                return;
            }
        }

        getCampaign().completeMission(mission, status);
        MekHQ.triggerEvent(new MissionCompletedEvent(mission));

        // apply mission xp
        int xpAward = getMissionXpAward(cmd.getStatus(), mission);

        if (xpAward > 0) {
            LocalDate today = getCampaign().getLocalDate();
            for (Person person : getCampaign().getActivePersonnel()) {
                if (person.isChild(today)) {
                    continue;
                }

                if (person.isDependent()) {
                    continue;
                }

                person.awardXP(getCampaign(), xpAward);
            }
        }

        if (mission instanceof AtBContract) {
            boolean wasOverallSuccess = cmd.getStatus() == SUCCESS || cmd.getStatus() == PARTIAL;

            if (prisoners != null) { // IDEA says we don't need the null check; I left it for insurance
                if (!getCampaign().getFriendlyPrisoners().isEmpty()) {
                    prisoners.handlePrisoners(wasOverallSuccess, true);
                }

                if (!getCampaign().getCurrentPrisoners().isEmpty()) {
                    prisoners.handlePrisoners(wasOverallSuccess, false);
                }
            }
        }

        // resolve turnover
        if ((getCampaign().getCampaignOptions().isUseRandomRetirement())
                && (getCampaign().getCampaignOptions().isUseContractCompletionRandomRetirement())) {
            RetirementDefectionDialog rdd = new RetirementDefectionDialog(getCampaignGui(), mission, true);

            if (rdd.wasAborted()) {
                /*
                 * Once the retirement rolls have been made, the outstanding payouts can be
                 * resolved
                 * without a reference to the contract and the dialog can be accessed through
                 * the menu
                 * provided they aren't still assigned to the mission in question.
                 */
                if (!getCampaign().getRetirementDefectionTracker().isOutstanding(mission.getId())) {
                    return;
                }
            } else {
                if ((getCampaign().getRetirementDefectionTracker().getRetirees(mission) != null)
                        && getCampaign().getFinances().getBalance().isGreaterOrEqualThan(rdd.totalPayout())) {
                    for (PersonnelRole role : PersonnelRole.getAdministratorRoles()) {
                        Person admin = getCampaign().findBestInRole(role, SkillType.S_ADMIN);
                        if (admin != null) {
                            admin.awardXP(getCampaign(), 1);
                            getCampaign().addReport(admin.getHyperlinkedName() + " has gained 1 XP.");
                        }
                    }
                }

                if (!getCampaign().applyRetirement(rdd.totalPayout(), rdd.getUnitAssignments())) {
                    return;
                }
            }
        }

        if (getCampaign().getCampaignOptions().isUseAtB() && (mission instanceof AtBContract)) {
            getCampaign().getContractMarket().checkForFollowup(getCampaign(), (AtBContract) mission);
        }

        // prompt autoAwards ceremony
        if (getCampaign().getCampaignOptions().isEnableAutoAwards()) {
            AutoAwardsController autoAwardsController = new AutoAwardsController();

            // for the purposes of Mission Accomplished awards, we do not count partial
            // Successes as Success
            autoAwardsController.PostMissionController(getCampaign(), mission,
                    Objects.equals(String.valueOf(cmd.getStatus()), "Success"));
        }

        final List<Mission> missions = getCampaign().getSortedMissions();
        comboMission.setSelectedItem(missions.isEmpty() ? null : missions.get(0));
    }

    /**
     * Displays a prompt asking the user if they want to ransom their prisoners or
     * defectors.
     *
     * @param prisoners    The list of prisoners to be ransomed.
     * @param resourceName The name of the resource bundle key for the prompt
     *                     message.
     * @param resources    The resource bundle containing the string resources.
     * @return true if the user selects the "Cancel" option, false otherwise.
     */
    private boolean prisonerPrompt(List<Person> prisoners, String resourceName, ResourceBundle resources) {
        Money total = Money.zero();
        total = total.plus(prisoners.stream()
                .map(person -> person.getRansomValue(getCampaign()))
                .collect(Collectors.toList()));

        int optionSelected = JOptionPane.showConfirmDialog(
                null,
                String.format(resources.getString(resourceName),
                        prisoners.size(),
                        total.toAmountAndSymbolString()),
                resources.getString("ransom.text"),
                JOptionPane.YES_NO_CANCEL_OPTION);

        switch (optionSelected) {
            case JOptionPane.YES_OPTION -> {
                getCampaign().addReport(String.format(resources.getString("ransomReport.format"),
                        prisoners.size(),
                        total.toAmountAndSymbolString()));
                getCampaign().addFunds(TransactionType.RANSOM,
                        total,
                        resources.getString("ransom.text"));
                prisoners.forEach(prisoner -> getCampaign().removePerson(prisoner, false));
            }
            case JOptionPane.NO_OPTION -> {
            }
            default -> {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates the XP award for completing a mission.
     *
     * @param missionStatus The status of the mission as a MissionStatus enum.
     * @param mission       The Mission object representing the completed mission.
     * @return The XP award for completing the mission.
     */
    private int getMissionXpAward(MissionStatus missionStatus, Mission mission) {
        return switch (missionStatus) {
            case FAILED, BREACH -> getCampaign().getCampaignOptions().getMissionXpFail();
            case SUCCESS, PARTIAL -> {
                if ((getCampaign().getCampaignOptions().isUseStratCon())
                        && (mission instanceof AtBContract)
                        && (((AtBContract) mission).getStratconCampaignState().getVictoryPoints() >= 3)) {
                    yield getCampaign().getCampaignOptions().getMissionXpOutstandingSuccess();
                } else {
                    yield getCampaign().getCampaignOptions().getMissionXpSuccess();
                }
            }
            case ACTIVE -> 0;
        };
    }

    private void deleteMission() {
        final Mission mission = comboMission.getSelectedItem();
        if (mission == null) {
            logger.error("Cannot remove null mission");
            return;
        }
        logger.debug("Attempting to Delete Mission, Mission ID: {}", mission.getId());
        if (0 != JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this mission?", "Delete mission?",
                JOptionPane.YES_NO_OPTION)) {
            return;
        }
        getCampaign().removeMission(mission);
        final List<Mission> missions = getCampaign().getSortedMissions();
        comboMission.setSelectedItem(missions.isEmpty() ? null : missions.get(0));
        MekHQ.triggerEvent(new MissionRemovedEvent(mission));
    }

    private void gmGenerateScenarios() {
        if (!getCampaign().isGM()) {
            JOptionPane.showMessageDialog(this,
                    "Only allowed for GM players",
                    "Not GM", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (0 != JOptionPane.showConfirmDialog(null, "Are you sure you want to generate a new set of scenarios?",
                "Generate scenarios?",
                JOptionPane.YES_NO_OPTION)) {
            return;
        }

        AtBScenarioFactory.createScenariosForNewWeek(getCampaign());
    }

    private void addScenario() {
        final Mission mission = comboMission.getSelectedItem();
        if (mission == null) {
            return;
        }

        CustomizeScenarioDialog csd = new CustomizeScenarioDialog(getFrame(), true, null, mission, getCampaign());
        csd.setVisible(true);
        // need to update the scenario table and refresh the scroll view
        refreshScenarioTableData();
        scrollMissionView.revalidate();
        scrollMissionView.repaint();
    }

    private void clearAssignedUnits() {
        if (0 == JOptionPane.showConfirmDialog(null,
            "Do you really want to remove all units from this scenario?",
                "Clear Units?", JOptionPane.YES_NO_OPTION)) {
            int row = scenarioTable.getSelectedRow();
            Scenario scenario = scenarioModel.getScenario(scenarioTable.convertRowIndexToModel(row));

            if (scenario == null) {
                return;
            }

            // This handles StratCon undeployment
            if (scenario instanceof AtBScenario) {
                AtBContract contract = ((AtBScenario) scenario).getContract(getCampaign());
                StratconScenario stratConScenario = ((AtBScenario) scenario).getStratconScenario(contract,
                    (AtBScenario) scenario);

                if (stratConScenario != null) {
                    stratConScenario.resetScenario(getCampaign());
                    return;
                }
            }

            // This handles Legacy AtB undeployment
            scenario.clearAllForcesAndPersonnel(getCampaign());
        }
    }

    private void printRecordSheets() {
        final int row = scenarioTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        final Scenario scenario = scenarioModel.getScenario(scenarioTable.convertRowIndexToModel(row));
        if (scenario == null) {
            return;
        }

        // First, we need to get all units assigned to the current scenario
        final List<UUID> unitIds = scenario.getForces(getCampaign()).getAllUnits(true);

        // Then, we need to convert the ids to units, and filter out any units that are
        // null and
        // any units with null entities
        final List<Unit> units = unitIds.stream()
                .map(unitId -> getCampaign().getUnit(unitId))
                .filter(unit -> (unit != null) && (unit.getEntity() != null))
                .toList();

        final List<Entity> chosen = new ArrayList<>();
        final StringBuilder undeployed = new StringBuilder();

        for (final Unit unit : units) {
            if (unit.checkDeployment() == null) {
                unit.resetPilotAndEntity();
                chosen.add(unit.getEntity());
            } else {
                undeployed.append('\n').append(unit.getName()).append(" (").append(unit.checkDeployment()).append(')');
            }
        }

        if (!undeployed.isEmpty()) {
            final Object[] options = { "Continue", "Cancel" };
            if (JOptionPane.showOptionDialog(getFrame(),
                    "The following units could not be deployed:" + undeployed,
                    "Could not deploy some units", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE, null, options, options[1]) == JOptionPane.NO_OPTION) {
                return;
            }
        }

        if (scenario instanceof AtBScenario) {
            // Also print off allied sheets
            chosen.addAll(((AtBScenario) scenario).getAlliesPlayer());
        }

        // add bot forces
        chosen.addAll(scenario.getBotForces().stream()
                .flatMap(botForce -> botForce.getFullEntityList(getCampaign()).stream())
                .toList());

        if (!chosen.isEmpty()) {
            UnitPrintManager.printAllUnits(chosen, true);
        }
    }

    private void loadScenario() {
        int row = scenarioTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        Scenario scenario = scenarioModel.getScenario(scenarioTable.convertRowIndexToModel(row));
        if (null != scenario) {
            getCampaignGui().getApplication().startHost(scenario, true, new ArrayList<>());
        }
    }

    private void startScenario() {
        startScenario(null);
    }


    /**
     * Resolve the selected scenario by proving a MUL file
     */
    private void resolveScenario() {
        Scenario scenario = getSelectedScenario();
        if (null == scenario) {
            return;
        }
        getCampaign().getApp().resolveScenario(scenario);
    }

    /**
     * Auto-resolve the selected scenario.
     * Can run both the auto resolve using princess or using the ACS engine
     */
    private void autoResolveScenario() {
        Scenario scenario = getSelectedScenario();
        if (null == scenario) {
            return;
        }
        promptAutoResolve(scenario);
    }

    private void runAbstractCombatAutoResolve(Scenario scenario) {
        List<Unit> chosen = playerUnits(scenario, new StringBuilder());
        if (chosen.isEmpty()) {
            return;
        }
        getCampaign().getApp().startAutoResolve((AtBScenario) scenario, chosen);
    }

    private void runPrincessAutoResolve() {
        startScenario(getCampaign().getAutoResolveBehaviorSettings());
    }

    private void promptAutoResolve(Scenario scenario) {
        // the options for the auto resolve method follow a predefined order, which is the same as the order in the enum
        // and it uses that to preselect the option that is currently set in the campaign options
        Object[] options = new Object[]{
            MHQInternationalization.getText("AutoResolveMethod.PRINCESS.text"),
            MHQInternationalization.getText("AutoResolveMethod.ABSTRACT_COMBAT.text"),
        };

        var preSelectedOptionIndex = getCampaignOptions().getAutoResolveMethod().ordinal();

        var selectedOption = JOptionPane.showOptionDialog(getFrame(),
            MHQInternationalization.getText("AutoResolveMethod.promptForAutoResolveMethod.text"),
            MHQInternationalization.getText("AutoResolveMethod.promptForAutoResolveMethod.title"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE, null, options, options[preSelectedOptionIndex]);

        if (selectedOption == JOptionPane.CLOSED_OPTION) {
            return;
        }

        var autoResolveMethod = AutoResolveMethod.values()[selectedOption];

        if (autoResolveMethod == AutoResolveMethod.PRINCESS) {
            runPrincessAutoResolve();
        } else if (autoResolveMethod == AutoResolveMethod.ABSTRACT_COMBAT) {
            runAbstractCombatAutoResolve(scenario);
        }
    }


    private List<Unit> playerUnits(Scenario scenario, StringBuilder undeployed) {
        Vector<UUID> uids = scenario.getForces(getCampaign()).getAllUnits(true);
        if (uids.isEmpty()) {
            return Collections.emptyList();
        }
        List<Unit> chosen = new ArrayList<>();
        for (UUID uid : uids) {
            Unit u = getCampaign().getUnit(uid);

            if ((null != u) && (null != u.getEntity())) {
                if (null == u.checkDeployment()) {
                    // Make sure the unit's entity and pilot are fully up to date!
                    u.resetPilotAndEntity();
                    chosen.add(u);
                } else {
                    undeployed.append('\n').append(u.getName()).append(" (").append(u.checkDeployment()).append(')');
                }
            }
        }
        return chosen;
    }

    private Scenario getSelectedScenario() {
        int row = scenarioTable.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return scenarioModel.getScenario(scenarioTable.convertRowIndexToModel(row));
    }

    private void startScenario(BehaviorSettings autoResolveBehaviorSettings) {
        int row = scenarioTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        Scenario scenario = scenarioModel.getScenario(scenarioTable.convertRowIndexToModel(row));
        if (scenario == null) {
            return;
        }
        Vector<UUID> uids = scenario.getForces(getCampaign()).getAllUnits(true);
        if (uids.isEmpty()) {
            return;
        }

        List<Unit> chosen = new ArrayList<>();
        StringBuilder undeployed = new StringBuilder();

        for (UUID uid : uids) {
            Unit u = getCampaign().getUnit(uid);
            if ((null != u) && (null != u.getEntity())) {
                if (null == u.checkDeployment()) {
                    // Make sure the unit's entity and pilot are fully up to date!
                    u.resetPilotAndEntity();

                    // Add and run
                    chosen.add(u);

                } else {
                    undeployed.append('\n').append(u.getName()).append(" (").append(u.checkDeployment()).append(')');
                }
            }
        }

        if (scenario instanceof AtBDynamicScenario atBDynamicScenario) {
            AtBDynamicScenarioFactory.setPlayerDeploymentTurns(atBDynamicScenario, getCampaign());
            AtBDynamicScenarioFactory.finalizeStaggeredDeploymentTurns(atBDynamicScenario, getCampaign());
            AtBDynamicScenarioFactory.setPlayerDeploymentZones(atBDynamicScenario, getCampaign());
        }

        if (!undeployed.isEmpty()) {
            Object[] options = { "Continue", "Cancel" };
            int n = JOptionPane.showOptionDialog(getFrame(),
                    "The following units could not be deployed:" + undeployed, "Could not deploy some units",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);

            if (n == 1) {
                return;
            }
        }

        // Ensure that the MegaMek year GameOption matches the campaign year
        // this is being set early on so that when setting up the autoconfig munitions
        // the correct year is used
        getCampaign().getGameOptions().getOption(OptionsConstants.ALLOWED_YEAR)
            .setValue(getCampaign().getGameYear());

        // code to support deployment of reinforcements for legacy ATB scenarios.
        if ((scenario instanceof AtBScenario atBScenario) && !(scenario instanceof AtBDynamicScenario)) {

            CombatTeam combatTeam = atBScenario.getCombatTeamById(getCampaign());
            if (combatTeam != null) {
                int assignedForceId = combatTeam.getForceId();
                int cmdrStrategy = 0;
                Person commander = getCampaign().getPerson(CombatTeam.findCommander(assignedForceId, getCampaign()));
                if ((null != commander) && (null != commander.getSkill(SkillType.S_STRATEGY))) {
                    cmdrStrategy = commander.getSkill(SkillType.S_STRATEGY).getLevel();
                }
                List<Entity> reinforcementEntities = new ArrayList<>();

                for (Unit unit : chosen) {
                    if (unit.getForceId() != assignedForceId) {
                        reinforcementEntities.add(unit.getEntity());
                    }
                }

                AtBDynamicScenarioFactory.setDeploymentTurnsForReinforcements(getCampaign().getHangar(),
                      scenario, reinforcementEntities, cmdrStrategy);
            }
        }

        if (getCampaign().getCampaignOptions().isUseAtB() && (scenario instanceof AtBScenario atBScenario)) {
            atBScenario.refresh(getCampaign());

            // Autoconfigure munitions for all non-player forces once more, using finalized
            // forces
            if (getCampaign().getCampaignOptions().isAutoConfigMunitions()) {
                autoconfigureBotMunitions(atBScenario, chosen);
            }
            configureBotAi(atBScenario);
        }

        if (scenario.getStratConScenarioType().isConvoy() && (autoResolveBehaviorSettings != null)) {
            try {
                autoResolveBehaviorSettings = autoResolveBehaviorSettings.getCopy();
                autoResolveBehaviorSettings.setIAmAPirate(true);
            } catch (PrincessException e) {
                logger.error("Failed to copy autoResolveBehaviorSettings", e);
            }
        }

        if (!chosen.isEmpty()) {
            getCampaignGui().getApplication()
                .startHost(scenario, false, chosen, autoResolveBehaviorSettings);
        }
    }

    private void configureBotAi(AtBScenario scenario) {
        Faction opFor = getEnemyFactionFromScenario(scenario);
        boolean isPirate = opFor.isRebelOrPirate();
        for (var bf : scenario.getBotForces()) {
            bf.getBehaviorSettings().setIAmAPirate(isPirate);
        }
    }

    /**
     * Get the enemy faction from the Mission from the scenario
     * @param scenario the scenario to get the enemy faction from
     * @return the enemy faction
     */
    private Faction getEnemyFactionFromScenario(Scenario scenario) {
        Mission mission = null;
        if (scenario.getMissionId() != -1) {
            mission = getCampaign().getMission(scenario.getMissionId());
        }
        if (mission == null) {
            mission = comboMission.getSelectedItem();
        }
        String opforFactionCode = "IS";
        Faction enemy;
        if (mission instanceof AtBContract atBContract) {
            enemy = atBContract.getEnemy();
            if (enemy != null) {
                return atBContract.getEnemy();
            }
            opforFactionCode = atBContract.getEnemyCode().isBlank() ? opforFactionCode : atBContract.getEnemyCode();
        }
        enemy = Factions.getInstance().getFaction(opforFactionCode);
        return enemy;
    }

    /**
     * Designed to fully kit out all non-player-controlled forces prior to battle.
     * Does not do any checks for supplies, only for availability to each faction
     * during the current timeframe.
     *
     * @param scenario
     * @param chosen
     */
    private void autoconfigureBotMunitions(AtBScenario scenario, List<Unit> chosen) {
        Game cGame = getCampaign().getGame();
        boolean groundMap = scenario.getBoardType() == AtBScenario.T_GROUND;
        boolean spaceMap = scenario.getBoardType() == AtBScenario.T_SPACE;
        ArrayList<Entity> alliedEntities = new ArrayList<>();

        ArrayList<String> allyFactionCodes = new ArrayList<>();
        ArrayList<String> opforFactionCodes = new ArrayList<>();
        String opforFactionCode = "IS";
        String allyFaction = "IS";
        int opforQuality = RATING_5;
        HashMap<Integer, ArrayList<Entity>> botTeamMappings = new HashMap<>();
        int allowedYear = cGame.getOptions().intOption(OptionsConstants.ALLOWED_YEAR);

        // This had better be an AtB contract...
        final Mission mission = comboMission.getSelectedItem();
        if (mission instanceof AtBContract atbc) {
            opforFactionCode = (atbc.getEnemyCode().isBlank()) ? opforFactionCode : atbc.getEnemyCode();
            opforQuality = atbc.getEnemyQuality();
            allyFactionCodes.add(atbc.getEmployerCode());
            allyFaction = atbc.getEmployerName(allowedYear);
        } else {
            allyFactionCodes.add(allyFaction);
        }
        Faction opforFaction = Factions.getInstance().getFaction(opforFactionCode);
        opforFactionCodes.add(opforFactionCode);
        boolean isPirate = opforFaction.isRebelOrPirate();

        // Collect player units to use as configuration fodder
        ArrayList<Entity> playerEntities = new ArrayList<>();
        for (final Unit unit : chosen) {
            playerEntities.add(unit.getEntity());
        }
        allyFactionCodes.add(getCampaign().getFaction().getShortName());

        // Split up bot forces into teams for separate handling
        for (final BotForce botForce : scenario.getBotForces()) {
            if (botForce.getName().contains(allyFaction)) {
                // Stuff with our employer's name should be with us.
                playerEntities.addAll(botForce.getFixedEntityList());
                alliedEntities.addAll(botForce.getFixedEntityList());
            } else {
                int botTeam = botForce.getTeam();
                if (!botTeamMappings.containsKey(botTeam)) {
                    botTeamMappings.put(botTeam, new ArrayList<>());
                }
                botTeamMappings.get(botTeam).addAll(botForce.getFixedEntityList());
            }
        }

        // Configure generated units with appropriate munitions (for BV calcs)
        TeamLoadOutGenerator tlg = new TeamLoadOutGenerator(cGame);

        // Reconfigure each group separately so they only consider their own
        // capabilities
        for (ArrayList<Entity> entityList : botTeamMappings.values()) {
            // bin fill ratio will be adjusted by the loadout generator based on piracy and
            // quality
            ReconfigurationParameters rp = TeamLoadOutGenerator.generateParameters(
                    cGame,
                    cGame.getOptions(),
                    entityList,
                    opforFactionCode,
                    playerEntities,
                    allyFactionCodes,
                    opforQuality,
                    ((isPirate) ? TeamLoadOutGenerator.UNSET_FILL_RATIO : 1.0f));
            rp.isPirate = isPirate;
            rp.groundMap = groundMap;
            rp.spaceEnvironment = spaceMap;
            MunitionTree mt = TeamLoadOutGenerator.generateMunitionTree(rp, entityList, "");
            tlg.reconfigureEntities(entityList, opforFactionCode, mt, rp);
        }

        // Finally, reconfigure all allies (but not player entities) as one organization
        ArrayList<Entity> allEnemyEntities = new ArrayList<>();
        botTeamMappings.values().stream().forEach(x -> allEnemyEntities.addAll(x));
        ReconfigurationParameters rp = TeamLoadOutGenerator.generateParameters(
                cGame,
                cGame.getOptions(),
                alliedEntities,
                allyFactionCodes.get(0),
                allEnemyEntities,
                opforFactionCodes,
                opforQuality,
                (getCampaign().getFaction().isPirate()) ? TeamLoadOutGenerator.UNSET_FILL_RATIO : 1.0f);
        rp.isPirate = getCampaign().getFaction().isPirate();
        rp.groundMap = groundMap;
        rp.spaceEnvironment = spaceMap;
        MunitionTree mt = TeamLoadOutGenerator.generateMunitionTree(rp, alliedEntities, "");
        tlg.reconfigureEntities(alliedEntities, allyFactionCodes.get(0), mt, rp);

    }

    private void joinScenario() {
        int row = scenarioTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        Scenario scenario = scenarioModel.getScenario(scenarioTable.convertRowIndexToModel(row));
        if (scenario == null) {
            return;
        }
        Vector<UUID> uids = scenario.getForces(getCampaign()).getAllUnits(true);
        if (uids.isEmpty()) {
            return;
        }

        List<Unit> chosen = new ArrayList<>();
        StringBuilder undeployed = new StringBuilder();

        for (UUID uid : uids) {
            Unit u = getCampaign().getUnit(uid);
            if (null != u.getEntity()) {
                if (null == u.checkDeployment()) {
                    // Make sure the unit's entity and pilot are fully up to date!
                    u.resetPilotAndEntity();

                    // Add and run
                    chosen.add(u);

                } else {
                    undeployed.append('\n').append(u.getName()).append(" (").append(u.checkDeployment()).append(')');
                }
            }
        }

        if (!undeployed.isEmpty()) {
            Object[] options = { "Continue", "Cancel" };
            int n = JOptionPane.showOptionDialog(getFrame(),
                    "The following units could not be deployed:" + undeployed, "Could not deploy some units",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);
            if (n == 1) {
                return;
            }
        }

        if (!chosen.isEmpty()) {
            getCampaignGui().getApplication().joinGame(scenario, chosen);
        }
    }

    private void deployListFile() {
        final int row = scenarioTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        final Scenario scenario = scenarioModel.getScenario(scenarioTable.convertRowIndexToModel(row));
        if (scenario == null) {
            return;
        }

        // First, we need to get all units assigned to the current scenario
        final List<UUID> unitIds = scenario.getForces(getCampaign()).getAllUnits(true);

        // Then, we need to convert the ids to units, and filter out any units that are
        // null and
        // any units with null entities
        final List<Unit> units = unitIds.stream()
                .map(unitId -> getCampaign().getUnit(unitId))
                .filter(unit -> (unit != null) && (unit.getEntity() != null))
                .toList();

        final ArrayList<Entity> chosen = new ArrayList<>();
        final StringBuilder undeployed = new StringBuilder();

        for (final Unit unit : units) {
            if (unit.checkDeployment() == null) {
                unit.resetPilotAndEntity();
                chosen.add(unit.getEntity());
            } else {
                undeployed.append('\n').append(unit.getName()).append(" (").append(unit.checkDeployment()).append(')');
            }
        }

        if (!undeployed.isEmpty()) {
            final Object[] options = { "Continue", "Cancel" };
            if (JOptionPane.showOptionDialog(getFrame(),
                    "The following units could not be deployed:" + undeployed,
                    "Could not deploy some units", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE, null, options, options[1]) == JOptionPane.NO_OPTION) {
                return;
            }
        }

        File file = determineMULFilePath(scenario, getCampaign().getName());
        if (file == null) {
            return;
        }

        try {
            // Save the player's entities to the file.
            EntityListFile.saveTo(file, chosen);
        } catch (Exception ex) {
            logger.error("", ex);
        }

        final Mission mission = comboMission.getSelectedItem();
        if ((mission instanceof AtBContract) && (scenario instanceof AtBScenario)
                && !((AtBScenario) scenario).getAlliesPlayer().isEmpty()) {
            // Export allies
            chosen.clear();
            chosen.addAll(((AtBScenario) scenario).getAlliesPlayer());
            file = determineMULFilePath(scenario, ((AtBContract) mission).getEmployer());

            int genericBattleValue = calculateGenericBattleValue(chosen);

            if (file != null) {
                try {
                    // Save the player's allied entities to the file.
                    EntityListFile.saveTo(file, chosen, genericBattleValue);
                } catch (Exception ex) {
                    logger.error("", ex);
                }
            }
        }

        // Export Bot forces
        for (final BotForce botForce : scenario.getBotForces()) {
            chosen.clear();
            chosen.addAll(botForce.getFullEntityList(getCampaign()));
            if (chosen.isEmpty()) {
                continue;
            }
            file = determineMULFilePath(scenario, botForce.getName());

            int genericBattleValue = calculateGenericBattleValue(chosen);

            if (file != null) {
                try {
                    // Save the bot force's entities to the file.
                    EntityListFile.saveTo(file, chosen, genericBattleValue);
                } catch (Exception ex) {
                    logger.error("", ex);
                }
            }
        }
    }

    /**
     * Calculates the total generic battle value of the entities chosen.
     * If the use of generic battle value option is enabled in the campaign options, the generic battle
     * value of each entity in the list is summed up and returned as the total generic battle value.
     * If the said option is disabled, the method returns 0.
     *
     * @param chosen the list of entities for which the generic battle value is to be calculated.
     * @return the total generic battle value or 0 if the generic battle value usage is turned off in
     * campaign options.
     */
    private int calculateGenericBattleValue(ArrayList<Entity> chosen) {
        int genericBattleValue = 0;
        if (getCampaign().getCampaignOptions().isUseGenericBattleValue()) {
            genericBattleValue = chosen.stream().mapToInt(Entity::getGenericBattleValue).sum();
        }
        return genericBattleValue;
    }

    private @Nullable File determineMULFilePath(final Scenario scenario, final String name) {
        final Optional<File> maybeUnitFile = FileDialogs.saveDeployUnits(getFrame(), scenario, name);
        if (maybeUnitFile.isEmpty()) {
            return null;
        }

        final File unitFile = maybeUnitFile.get();
        if (unitFile.getName().toLowerCase().endsWith(".mul")) {
            return unitFile;
        } else {
            try {
                return new File(unitFile.getCanonicalPath() + ".mul");
            } catch (Exception ignored) {
                // nothing needs to be done here
                return null;
            }
        }
    }

    public void refreshMissions() {
        comboMission.removeAllItems();
        final List<Mission> missions = getCampaign().getSortedMissions();
        for (final Mission mission : missions) {
            comboMission.addItem(mission);
        }

        if ((comboMission.getSelectedIndex() == -1) && !missions.isEmpty()) {
            comboMission.setSelectedIndex(0);
        }

        changeMission();
        if (getCampaign().getCampaignOptions().isUseAtB()) {
            refreshLanceAssignments();
        }
    }

    public void refreshScenarioView() {
        int row = scenarioTable.getSelectedRow();
        if (row < 0) {
            scrollScenarioView.setViewportView(null);
            btnStartGame.setEnabled(false);
            btnJoinGame.setEnabled(false);
            btnLoadGame.setEnabled(false);
            btnGetMul.setEnabled(false);
            btnClearAssignedUnits.setEnabled(false);
            btnResolveScenario.setEnabled(false);
            btnAutoResolveScenario.setEnabled(false);
            btnPrintRS.setEnabled(false);
            selectedScenario = -1;
            return;
        }
        Scenario scenario = scenarioModel.getScenario(scenarioTable.convertRowIndexToModel(row));
        if (scenario == null) {
            return;
        }
        selectedScenario = scenario.getId();
        if (getCampaign().getCampaignOptions().isUseAtB() && (scenario instanceof AtBScenario)) {
            scrollScenarioView.setViewportView(
                    new AtBScenarioViewPanel((AtBScenario) scenario, getCampaign(), getFrame()));
        } else {
            scrollScenarioView.setViewportView(new ScenarioViewPanel(getFrame(), getCampaign(), scenario));
        }
        // This odd code is to make sure that the scrollbar stays at the top
        // I can't just call it here, because it ends up getting reset somewhere
        // later
        SwingUtilities.invokeLater(() -> scrollScenarioView.getVerticalScrollBar().setValue(0));

        final boolean canStartGame = (
            (!getCampaign().checkLinkedScenario(scenario.getId())) && (scenario.canStartScenario(getCampaign()))
            );

        btnStartGame.setEnabled(canStartGame);
        btnJoinGame.setEnabled(canStartGame);
        btnLoadGame.setEnabled(canStartGame);
        btnGetMul.setEnabled(canStartGame);

        final boolean hasTrack = scenario.getHasTrack();
        if (hasTrack) {
            btnClearAssignedUnits.setEnabled(canStartGame && getCampaign().isGM());
        } else {
            btnClearAssignedUnits.setEnabled(canStartGame);
        }

        btnResolveScenario.setEnabled(canStartGame);
        if (scenario instanceof AtBScenario) {
            btnAutoResolveScenario.setEnabled(canStartGame);
        }
        btnPrintRS.setEnabled(canStartGame);
    }

    public void refreshLanceAssignments() {
        panLanceAssignment.refresh();
    }

    /*
     * (non-Javadoc)
     *
     * @see mekhq.gui.CampaignGuiTab#refreshAll()
     */
    @Override
    public void refreshAll() {
        refreshMissions();
        refreshScenarioTableData();
    }

    public void changeMission() {
        final Mission mission = comboMission.getSelectedItem();
        if (mission == null) {
            scrollMissionView.setViewportView(null);
            btnEditMission.setEnabled(false);
            btnCompleteMission.setEnabled(false);
            btnDeleteMission.setEnabled(false);
            btnAddScenario.setEnabled(false);
            btnGMGenerateScenarios.setEnabled(false);
        } else {
            scrollMissionView.setViewportView(new MissionViewPanel(mission, scenarioTable, getCampaignGui()));
            // This odd code is to make sure that the scrollbar stays at the top
            // I can't just call it here, because it ends up getting reset somewhere later
            SwingUtilities.invokeLater(() -> scrollMissionView.getVerticalScrollBar().setValue(0));
            btnEditMission.setEnabled(true);
            btnCompleteMission.setEnabled(mission.getStatus().isActive());
            btnDeleteMission.setEnabled(true);
            btnAddScenario.setEnabled(mission.isActiveOn(getCampaign().getLocalDate()));
            btnGMGenerateScenarios.setEnabled(mission.isActiveOn(getCampaign().getLocalDate()) && getCampaign().isGM());
        }
        refreshScenarioTableData();
    }

    public void refreshScenarioTableData() {
        final Mission mission = comboMission.getSelectedItem();
        scenarioModel.setData((mission == null) ? new ArrayList<Scenario>() : mission.getVisibleScenarios());
        selectedScenario = -1;
        scenarioTable.setPreferredScrollableViewportSize(scenarioTable.getPreferredSize());
        scenarioTable.setFillsViewportHeight(true);
    }

    private final ActionScheduler scenarioDataScheduler = new ActionScheduler(this::refreshScenarioTableData);
    private final ActionScheduler scenarioViewScheduler = new ActionScheduler(this::refreshScenarioView);
    private final ActionScheduler missionsScheduler = new ActionScheduler(this::refreshMissions);
    private final ActionScheduler lanceAssignmentScheduler = new ActionScheduler(this::refreshLanceAssignments);

    @Subscribe
    public void handle(OptionsChangedEvent ev) {
        splitScenario.getBottomComponent().setVisible(getCampaignOptions().isUseAtB());
        splitScenario.resetToPreferredSizes();
    }

    @Subscribe
    public void handle(ScenarioChangedEvent evt) {
        final Mission mission = comboMission.getSelectedItem();
        if ((evt.getScenario() != null)
                && (evt.getScenario().getMissionId() == (mission == null ? -1 : mission.getId()))) {
            scenarioTable.repaint();
            if (evt.getScenario().getId() == selectedScenario) {
                scenarioViewScheduler.schedule();
            }
            scenarioDataScheduler.schedule();
        }
    }

    @Subscribe
    public void handle(ScenarioResolvedEvent ev) {
        missionsScheduler.schedule();
    }

    @Subscribe
    public void handle(OrganizationChangedEvent ev) {
        scenarioDataScheduler.schedule();
        if (getCampaignOptions().isUseAtB()) {
            lanceAssignmentScheduler.schedule();
        }
    }

    @Subscribe
    public void handle(ScenarioNewEvent ev) {
        scenarioDataScheduler.schedule();
    }

    @Subscribe
    public void handle(ScenarioRemovedEvent ev) {
        scenarioDataScheduler.schedule();
    }

    @Subscribe
    public void handle(MissionNewEvent ev) {
        missionsScheduler.schedule();
    }

    @Subscribe
    public void handle(MissionRemovedEvent ev) {
        missionsScheduler.schedule();
    }

    @Subscribe
    public void handle(MissionCompletedEvent ev) {
        missionsScheduler.schedule();
    }

    @Subscribe
    public void handle(MissionChangedEvent evt) {
        final Mission mission = comboMission.getSelectedItem();
        if ((mission != null) && (evt.getMission().getId() == mission.getId())) {
            changeMission();
        }
    }

    @Subscribe
    public void handle(GMModeEvent ev) {
        btnGMGenerateScenarios.setEnabled(ev.isGMMode());
    }
}
