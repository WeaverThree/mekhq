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

import megamek.client.ui.baseComponents.MMComboBox;
import megamek.common.util.EncodeControl;
import mekhq.MekHQ;
import mekhq.campaign.Campaign;
import mekhq.campaign.personnel.enums.PersonnelRole;
import mekhq.campaign.universe.Faction;
import mekhq.campaign.universe.Planet;
import mekhq.campaign.universe.PlanetarySystem;
import mekhq.campaign.universe.enums.CompanyGenerationMethod;
import mekhq.campaign.universe.enums.ForceNamingMethod;
import mekhq.campaign.universe.enums.MysteryBoxType;
import mekhq.campaign.universe.enums.PartGenerationMethod;
import mekhq.campaign.universe.generators.companyGenerators.CompanyGenerationOptions;
import mekhq.gui.FileDialogs;
import mekhq.gui.baseComponents.AbstractMHQPanel;
import mekhq.gui.baseComponents.JDisableablePanel;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CompanyGenerationOptionsPanel extends AbstractMHQPanel {
    //region Variable Declarations
    private final Campaign campaign;

    // Base Information
    private MMComboBox<CompanyGenerationMethod> comboCompanyGenerationMethod;
    private JCheckBox chkGenerateMercenaryCompanyCommandLance;
    private JSpinner spnCompanyCount;
    private JSpinner spnIndividualLanceCount;
    private JSpinner spnLancesPerCompany;
    private JSpinner spnLanceSize;
    private JSpinner spnStarLeagueYear;

    // Personnel
    private JLabel lblTotalSupportPersonnel;
    private Map<PersonnelRole, JSpinner> spnSupportPersonnelNumbers;
    private JCheckBox chkPoolAssistants;
    private JCheckBox chkGenerateCaptains;
    private JCheckBox chkAssignCompanyCommanderFlag;
    private JCheckBox chkApplyOfficerStatBonusToWorstSkill;
    private JCheckBox chkAssignBestOfficers;
    private JCheckBox chkAutomaticallyAssignRanks;
    private JCheckBox chkAssignFounderFlag;

    // Personnel Randomization
    private JCheckBox chkRandomizeOrigin;
    private JCheckBox chkRandomizeAroundCentralPlanet;
    private JCheckBox chkCentralSystemFactionSpecific;
    private MMComboBox<PlanetarySystem> comboCentralSystem;
    private MMComboBox<Planet> comboCentralPlanet;
    private JSpinner spnOriginSearchRadius;
    private JCheckBox chkExtraRandomOrigin;
    private JSpinner spnOriginDistanceScale;

    // Starting Simulation
    private JCheckBox chkRunStartingSimulation;
    private JSpinner spnSimulationDuration;
    private JCheckBox chkSimulateRandomMarriages;
    private JCheckBox chkSimulateRandomProcreation;

    // Units
    private JCheckBox chkGenerateUnitsAsAttached;
    private JCheckBox chkAssignBestRollToUnitCommander;
    private JCheckBox chkSortStarLeagueUnitsFirst;
    private JCheckBox chkGroupByWeight;
    private JCheckBox chkGroupByQuality;
    private JCheckBox chkKeepOfficerRollsSeparate;
    private JCheckBox chkAssignTechsToUnits;

    // Unit
    private MMComboBox<ForceNamingMethod> comboForceNamingMethod;
    private JCheckBox chkGenerateForceIcons;

    // Spares
    private JCheckBox chkGenerateMothballedSpareUnits;
    private JSpinner spnSparesPercentOfActiveUnits;
    private MMComboBox<PartGenerationMethod> comboPartGenerationMethod;
    private JSpinner spnStartingArmourWeight;
    private JCheckBox chkGenerateSpareAmmunition;
    private JSpinner spnNumberReloadsPerWeapon;
    private JCheckBox chkGenerateFractionalMachineGunAmmunition;

    // Contracts
    private JCheckBox chkSelectStartingContract;
    private JCheckBox chkStartCourseToContractPlanet;

    // Finances
    private JSpinner spnStartingCash;
    private JCheckBox chkRandomizeStartingCash;
    private JSpinner spnRandomStartingCashDiceCount;
    private JSpinner spnMinimumStartingFloat;
    private JCheckBox chkPayForSetup;
    private JCheckBox chkStartingLoan;
    private JCheckBox chkPayForPersonnel;
    private JCheckBox chkPayForUnits;
    private JCheckBox chkPayForParts;
    private JCheckBox chkPayForArmour;
    private JCheckBox chkPayForAmmunition;

    // Surprises
    private JCheckBox chkGenerateSurprises;
    private JCheckBox chkGenerateMysteryBoxes;
    private Map<MysteryBoxType, JCheckBox> chkGenerateMysteryBoxTypes;

    private final ResourceBundle resources = ResourceBundle.getBundle("mekhq.resources.GUI", new EncodeControl());
    //endregion Variable Declarations

    //region Constructors
    public CompanyGenerationOptionsPanel(final JFrame frame, final Campaign campaign) {
        super(frame, "CompanyGenerationOptionsPanel", new GridBagLayout());
        this.campaign = campaign;

        initialize();

        if (campaign.getCampaignOptions().getCompanyGenerationOptions() == null) {
            setOptions(MekHQ.getMekHQOptions().getDefaultCompanyGenerationMethod());
        } else {
            setOptions(campaign.getCampaignOptions().getCompanyGenerationOptions());
        }
    }
    //endregion Constructors

    //region Getters/Setters
    public Campaign getCampaign() {
        return campaign;
    }

    //region Base Information
    public MMComboBox<CompanyGenerationMethod> getComboCompanyGenerationMethod() {
        return comboCompanyGenerationMethod;
    }

    public void setComboCompanyGenerationMethod(final MMComboBox<CompanyGenerationMethod> comboCompanyGenerationMethod) {
        this.comboCompanyGenerationMethod = comboCompanyGenerationMethod;
    }

    public JCheckBox getChkGenerateMercenaryCompanyCommandLance() {
        return chkGenerateMercenaryCompanyCommandLance;
    }

    public void setChkGenerateMercenaryCompanyCommandLance(final JCheckBox chkGenerateMercenaryCompanyCommandLance) {
        this.chkGenerateMercenaryCompanyCommandLance = chkGenerateMercenaryCompanyCommandLance;
    }

    public JSpinner getSpnCompanyCount() {
        return spnCompanyCount;
    }

    public void setSpnCompanyCount(final JSpinner spnCompanyCount) {
        this.spnCompanyCount = spnCompanyCount;
    }

    public JSpinner getSpnIndividualLanceCount() {
        return spnIndividualLanceCount;
    }

    public void setSpnIndividualLanceCount(final JSpinner spnIndividualLanceCount) {
        this.spnIndividualLanceCount = spnIndividualLanceCount;
    }

    public JSpinner getSpnLancesPerCompany() {
        return spnLancesPerCompany;
    }

    public void setSpnLancesPerCompany(final JSpinner spnLancesPerCompany) {
        this.spnLancesPerCompany = spnLancesPerCompany;
    }

    public JSpinner getSpnLanceSize() {
        return spnLanceSize;
    }

    public void setSpnLanceSize(final JSpinner spnLanceSize) {
        this.spnLanceSize = spnLanceSize;
    }

    public JSpinner getSpnStarLeagueYear() {
        return spnStarLeagueYear;
    }

    public void setSpnStarLeagueYear(final JSpinner spnStarLeagueYear) {
        this.spnStarLeagueYear = spnStarLeagueYear;
    }
    //endregion Base Information

    //region Personnel
    public JLabel getLblTotalSupportPersonnel() {
        return lblTotalSupportPersonnel;
    }

    public void updateLblTotalSupportPersonnel(final int numSupportPersonnel) {
        getLblTotalSupportPersonnel().setText(String.format(
                resources.getString("lblTotalSupportPersonnel.text"), numSupportPersonnel));
    }

    public void setLblTotalSupportPersonnel(final JLabel lblTotalSupportPersonnel) {
        this.lblTotalSupportPersonnel = lblTotalSupportPersonnel;
    }

    public Map<PersonnelRole, JSpinner> getSpnSupportPersonnelNumbers() {
        return spnSupportPersonnelNumbers;
    }

    public void setSpnSupportPersonnelNumbers(final Map<PersonnelRole, JSpinner> spnSupportPersonnelNumbers) {
        this.spnSupportPersonnelNumbers = spnSupportPersonnelNumbers;
    }

    public JCheckBox getChkPoolAssistants() {
        return chkPoolAssistants;
    }

    public void setChkPoolAssistants(final JCheckBox chkPoolAssistants) {
        this.chkPoolAssistants = chkPoolAssistants;
    }

    public JCheckBox getChkGenerateCaptains() {
        return chkGenerateCaptains;
    }

    public void setChkGenerateCaptains(final JCheckBox chkGenerateCaptains) {
        this.chkGenerateCaptains = chkGenerateCaptains;
    }

    public JCheckBox getChkAssignCompanyCommanderFlag() {
        return chkAssignCompanyCommanderFlag;
    }

    public void setChkAssignCompanyCommanderFlag(final JCheckBox chkAssignCompanyCommanderFlag) {
        this.chkAssignCompanyCommanderFlag = chkAssignCompanyCommanderFlag;
    }

    public JCheckBox getChkApplyOfficerStatBonusToWorstSkill() {
        return chkApplyOfficerStatBonusToWorstSkill;
    }

    public void setChkApplyOfficerStatBonusToWorstSkill(final JCheckBox chkApplyOfficerStatBonusToWorstSkill) {
        this.chkApplyOfficerStatBonusToWorstSkill = chkApplyOfficerStatBonusToWorstSkill;
    }

    public JCheckBox getChkAssignBestOfficers() {
        return chkAssignBestOfficers;
    }

    public void setChkAssignBestOfficers(final JCheckBox chkAssignBestOfficers) {
        this.chkAssignBestOfficers = chkAssignBestOfficers;
    }

    public JCheckBox getChkAutomaticallyAssignRanks() {
        return chkAutomaticallyAssignRanks;
    }

    public void setChkAutomaticallyAssignRanks(final JCheckBox chkAutomaticallyAssignRanks) {
        this.chkAutomaticallyAssignRanks = chkAutomaticallyAssignRanks;
    }

    public JCheckBox getChkAssignFounderFlag() {
        return chkAssignFounderFlag;
    }

    public void setChkAssignFounderFlag(final JCheckBox chkAssignFounderFlag) {
        this.chkAssignFounderFlag = chkAssignFounderFlag;
    }
    //endregion Personnel

    //region Personnel Randomization
    public JCheckBox getChkRandomizeOrigin() {
        return chkRandomizeOrigin;
    }

    public void setChkRandomizeOrigin(final JCheckBox chkRandomizeOrigin) {
        this.chkRandomizeOrigin = chkRandomizeOrigin;
    }

    public JCheckBox getChkRandomizeAroundCentralPlanet() {
        return chkRandomizeAroundCentralPlanet;
    }

    public void setChkRandomizeAroundCentralPlanet(final JCheckBox chkRandomizeAroundCentralPlanet) {
        this.chkRandomizeAroundCentralPlanet = chkRandomizeAroundCentralPlanet;
    }

    public JCheckBox getChkCentralSystemFactionSpecific() {
        return chkCentralSystemFactionSpecific;
    }

    public void setChkCentralSystemFactionSpecific(final JCheckBox chkCentralSystemFactionSpecific) {
        this.chkCentralSystemFactionSpecific = chkCentralSystemFactionSpecific;
    }

    public MMComboBox<PlanetarySystem> getComboCentralSystem() {
        return comboCentralSystem;
    }

    public void setComboCentralSystem(final MMComboBox<PlanetarySystem> comboCentralSystem) {
        this.comboCentralSystem = comboCentralSystem;
    }

    private void restoreComboCentralSystem() {
        getComboCentralSystem().removeAllItems();
        final Faction faction = getChkCentralSystemFactionSpecific().isSelected()
                ? getCampaign().getFaction() : null;
        final PlanetarySystem[] planetarySystems = getCampaign().getSystems().stream()
                .filter(p -> (faction == null) || p.getFactionSet(getCampaign().getLocalDate()).contains(faction))
                .sorted(Comparator.comparing(p -> p.getName(getCampaign().getLocalDate())))
                .collect(Collectors.toList()).toArray(new PlanetarySystem[]{});
        getComboCentralSystem().setModel(new DefaultComboBoxModel<>(planetarySystems));
        restoreComboCentralPlanet();
    }

    public MMComboBox<Planet> getComboCentralPlanet() {
        return comboCentralPlanet;
    }

    public void setComboCentralPlanet(final MMComboBox<Planet> comboCentralPlanet) {
        this.comboCentralPlanet = comboCentralPlanet;
    }

    private void restoreComboCentralPlanet() {
        final PlanetarySystem centralSystem = getComboCentralSystem().getSelectedItem();
        if (centralSystem != null) {
            getComboCentralPlanet().setModel(new DefaultComboBoxModel<>(
                    centralSystem.getPlanets().toArray(new Planet[]{})));
            getComboCentralPlanet().setSelectedItem(centralSystem.getPrimaryPlanet());
        } else {
            getComboCentralPlanet().removeAllItems();
        }
    }

    public JSpinner getSpnOriginSearchRadius() {
        return spnOriginSearchRadius;
    }

    public void setSpnOriginSearchRadius(final JSpinner spnOriginSearchRadius) {
        this.spnOriginSearchRadius = spnOriginSearchRadius;
    }

    public JCheckBox getChkExtraRandomOrigin() {
        return chkExtraRandomOrigin;
    }

    public void setChkExtraRandomOrigin(final JCheckBox chkExtraRandomOrigin) {
        this.chkExtraRandomOrigin = chkExtraRandomOrigin;
    }

    public JSpinner getSpnOriginDistanceScale() {
        return spnOriginDistanceScale;
    }

    public void setSpnOriginDistanceScale(final JSpinner spnOriginDistanceScale) {
        this.spnOriginDistanceScale = spnOriginDistanceScale;
    }
    //endregion Personnel Randomization

    //region Starting Simulation
    public JCheckBox getChkRunStartingSimulation() {
        return chkRunStartingSimulation;
    }

    public void setChkRunStartingSimulation(final JCheckBox chkRunStartingSimulation) {
        this.chkRunStartingSimulation = chkRunStartingSimulation;
    }

    public JSpinner getSpnSimulationDuration() {
        return spnSimulationDuration;
    }

    public void setSpnSimulationDuration(final JSpinner spnSimulationDuration) {
        this.spnSimulationDuration = spnSimulationDuration;
    }

    public JCheckBox getChkSimulateRandomMarriages() {
        return chkSimulateRandomMarriages;
    }

    public void setChkSimulateRandomMarriages(final JCheckBox chkSimulateRandomMarriages) {
        this.chkSimulateRandomMarriages = chkSimulateRandomMarriages;
    }

    public JCheckBox getChkSimulateRandomProcreation() {
        return chkSimulateRandomProcreation;
    }

    public void setChkSimulateRandomProcreation(final JCheckBox chkSimulateRandomProcreation) {
        this.chkSimulateRandomProcreation = chkSimulateRandomProcreation;
    }
    //endregion Starting Simulation

    //region Units
    public JCheckBox getChkGenerateUnitsAsAttached() {
        return chkGenerateUnitsAsAttached;
    }

    public void setChkGenerateUnitsAsAttached(final JCheckBox chkGenerateUnitsAsAttached) {
        this.chkGenerateUnitsAsAttached = chkGenerateUnitsAsAttached;
    }

    public JCheckBox getChkAssignBestRollToUnitCommander() {
        return chkAssignBestRollToUnitCommander;
    }

    public void setChkAssignBestRollToUnitCommander(final JCheckBox chkAssignBestRollToUnitCommander) {
        this.chkAssignBestRollToUnitCommander = chkAssignBestRollToUnitCommander;
    }

    public JCheckBox getChkSortStarLeagueUnitsFirst() {
        return chkSortStarLeagueUnitsFirst;
    }

    public void setChkSortStarLeagueUnitsFirst(final JCheckBox chkSortStarLeagueUnitsFirst) {
        this.chkSortStarLeagueUnitsFirst = chkSortStarLeagueUnitsFirst;
    }

    public JCheckBox getChkGroupByWeight() {
        return chkGroupByWeight;
    }

    public void setChkGroupByWeight(final JCheckBox chkGroupByWeight) {
        this.chkGroupByWeight = chkGroupByWeight;
    }

    public JCheckBox getChkGroupByQuality() {
        return chkGroupByQuality;
    }

    public void setChkGroupByQuality(final JCheckBox chkGroupByQuality) {
        this.chkGroupByQuality = chkGroupByQuality;
    }

    public JCheckBox getChkKeepOfficerRollsSeparate() {
        return chkKeepOfficerRollsSeparate;
    }

    public void setChkKeepOfficerRollsSeparate(final JCheckBox chkKeepOfficerRollsSeparate) {
        this.chkKeepOfficerRollsSeparate = chkKeepOfficerRollsSeparate;
    }

    public JCheckBox getChkAssignTechsToUnits() {
        return chkAssignTechsToUnits;
    }

    public void setChkAssignTechsToUnits(final JCheckBox chkAssignTechsToUnits) {
        this.chkAssignTechsToUnits = chkAssignTechsToUnits;
    }
    //endregion Units

    //region Unit
    public MMComboBox<ForceNamingMethod> getComboForceNamingMethod() {
        return comboForceNamingMethod;
    }

    public void setComboForceNamingMethod(final MMComboBox<ForceNamingMethod> comboForceNamingMethod) {
        this.comboForceNamingMethod = comboForceNamingMethod;
    }

    public JCheckBox getChkGenerateForceIcons() {
        return chkGenerateForceIcons;
    }

    public void setChkGenerateForceIcons(final JCheckBox chkGenerateForceIcons) {
        this.chkGenerateForceIcons = chkGenerateForceIcons;
    }
    //endregion Unit

    //region Spares
    public JCheckBox getChkGenerateMothballedSpareUnits() {
        return chkGenerateMothballedSpareUnits;
    }

    public void setChkGenerateMothballedSpareUnits(final JCheckBox chkGenerateMothballedSpareUnits) {
        this.chkGenerateMothballedSpareUnits = chkGenerateMothballedSpareUnits;
    }

    public JSpinner getSpnSparesPercentOfActiveUnits() {
        return spnSparesPercentOfActiveUnits;
    }

    public void setSpnSparesPercentOfActiveUnits(final JSpinner spnSparesPercentOfActiveUnits) {
        this.spnSparesPercentOfActiveUnits = spnSparesPercentOfActiveUnits;
    }

    public MMComboBox<PartGenerationMethod> getComboPartGenerationMethod() {
        return comboPartGenerationMethod;
    }

    public void setComboPartGenerationMethod(final MMComboBox<PartGenerationMethod> comboPartGenerationMethod) {
        this.comboPartGenerationMethod = comboPartGenerationMethod;
    }

    public JSpinner getSpnStartingArmourWeight() {
        return spnStartingArmourWeight;
    }

    public void setSpnStartingArmourWeight(final JSpinner spnStartingArmourWeight) {
        this.spnStartingArmourWeight = spnStartingArmourWeight;
    }

    public JCheckBox getChkGenerateSpareAmmunition() {
        return chkGenerateSpareAmmunition;
    }

    public void setChkGenerateSpareAmmunition(final JCheckBox chkGenerateSpareAmmunition) {
        this.chkGenerateSpareAmmunition = chkGenerateSpareAmmunition;
    }

    public JSpinner getSpnNumberReloadsPerWeapon() {
        return spnNumberReloadsPerWeapon;
    }

    public void setSpnNumberReloadsPerWeapon(final JSpinner spnNumberReloadsPerWeapon) {
        this.spnNumberReloadsPerWeapon = spnNumberReloadsPerWeapon;
    }

    public JCheckBox getChkGenerateFractionalMachineGunAmmunition() {
        return chkGenerateFractionalMachineGunAmmunition;
    }

    public void setChkGenerateFractionalMachineGunAmmunition(final JCheckBox chkGenerateFractionalMachineGunAmmunition) {
        this.chkGenerateFractionalMachineGunAmmunition = chkGenerateFractionalMachineGunAmmunition;
    }
    //endregion Spares

    //region Contracts
    public JCheckBox getChkSelectStartingContract() {
        return chkSelectStartingContract;
    }

    public void setChkSelectStartingContract(final JCheckBox chkSelectStartingContract) {
        this.chkSelectStartingContract = chkSelectStartingContract;
    }

    public JCheckBox getChkStartCourseToContractPlanet() {
        return chkStartCourseToContractPlanet;
    }

    public void setChkStartCourseToContractPlanet(final JCheckBox chkStartCourseToContractPlanet) {
        this.chkStartCourseToContractPlanet = chkStartCourseToContractPlanet;
    }
    //endregion Contracts

    //region Finances
    public JSpinner getSpnStartingCash() {
        return spnStartingCash;
    }

    public void setSpnStartingCash(final JSpinner spnStartingCash) {
        this.spnStartingCash = spnStartingCash;
    }

    public JCheckBox getChkRandomizeStartingCash() {
        return chkRandomizeStartingCash;
    }

    public void setChkRandomizeStartingCash(final JCheckBox chkRandomizeStartingCash) {
        this.chkRandomizeStartingCash = chkRandomizeStartingCash;
    }

    public JSpinner getSpnRandomStartingCashDiceCount() {
        return spnRandomStartingCashDiceCount;
    }

    public void setSpnRandomStartingCashDiceCount(final JSpinner spnRandomStartingCashDiceCount) {
        this.spnRandomStartingCashDiceCount = spnRandomStartingCashDiceCount;
    }

    public JSpinner getSpnMinimumStartingFloat() {
        return spnMinimumStartingFloat;
    }

    public void setSpnMinimumStartingFloat(final JSpinner spnMinimumStartingFloat) {
        this.spnMinimumStartingFloat = spnMinimumStartingFloat;
    }

    public JCheckBox getChkPayForSetup() {
        return chkPayForSetup;
    }

    public void setChkPayForSetup(final JCheckBox chkPayForSetup) {
        this.chkPayForSetup = chkPayForSetup;
    }

    public JCheckBox getChkStartingLoan() {
        return chkStartingLoan;
    }

    public void setChkStartingLoan(final JCheckBox chkStartingLoan) {
        this.chkStartingLoan = chkStartingLoan;
    }

    public JCheckBox getChkPayForPersonnel() {
        return chkPayForPersonnel;
    }

    public void setChkPayForPersonnel(final JCheckBox chkPayForPersonnel) {
        this.chkPayForPersonnel = chkPayForPersonnel;
    }

    public JCheckBox getChkPayForUnits() {
        return chkPayForUnits;
    }

    public void setChkPayForUnits(final JCheckBox chkPayForUnits) {
        this.chkPayForUnits = chkPayForUnits;
    }

    public JCheckBox getChkPayForParts() {
        return chkPayForParts;
    }

    public void setChkPayForParts(final JCheckBox chkPayForParts) {
        this.chkPayForParts = chkPayForParts;
    }

    public JCheckBox getChkPayForArmour() {
        return chkPayForArmour;
    }

    public void setChkPayForArmour(final JCheckBox chkPayForArmour) {
        this.chkPayForArmour = chkPayForArmour;
    }

    public JCheckBox getChkPayForAmmunition() {
        return chkPayForAmmunition;
    }

    public void setChkPayForAmmunition(final JCheckBox chkPayForAmmunition) {
        this.chkPayForAmmunition = chkPayForAmmunition;
    }
    //endregion Finances

    //region Surprises
    public JCheckBox getChkGenerateSurprises() {
        return chkGenerateSurprises;
    }

    public void setChkGenerateSurprises(final JCheckBox chkGenerateSurprises) {
        this.chkGenerateSurprises = chkGenerateSurprises;
    }

    public JCheckBox getChkGenerateMysteryBoxes() {
        return chkGenerateMysteryBoxes;
    }

    public void setChkGenerateMysteryBoxes(final JCheckBox chkGenerateMysteryBoxes) {
        this.chkGenerateMysteryBoxes = chkGenerateMysteryBoxes;
    }

    public Map<MysteryBoxType, JCheckBox> getChkGenerateMysteryBoxTypes() {
        return chkGenerateMysteryBoxTypes;
    }

    public void setChkGenerateMysteryBoxTypes(final Map<MysteryBoxType, JCheckBox> chkGenerateMysteryBoxTypes) {
        this.chkGenerateMysteryBoxTypes = chkGenerateMysteryBoxTypes;
    }
    //endregion Surprises
    //endregion Getters/Setters

    //region Determination Methods
    public int determineMaximumSupportPersonnel() {
        return ((getChkGenerateMercenaryCompanyCommandLance().isSelected() ? 1 : 0)
                + ((int) getSpnCompanyCount().getValue() * (int) getSpnLancesPerCompany().getValue())
                + (int) getSpnIndividualLanceCount().getValue()) * (int) getSpnLanceSize().getValue();
    }
    //endregion Determination Methods

    //region Initialization
    @Override
    protected void initialize() {
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(createBaseInformationPanel(), gbc);

        gbc.gridx++;
        add(createPersonnelPanel(), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(createPersonnelRandomizationPanel(), gbc);

        gbc.gridx++;
        add(createStartingSimulationPanel(), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(createUnitsPanel(), gbc);

        gbc.gridx++;
        add(createUnitPanel(), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(createSparesPanel(), gbc);

        gbc.gridx++;
        add(createContractsPanel(), gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(createFinancesPanel(), gbc);

        gbc.gridx++;
        add(createSurprisesPanel(), gbc);
    }

    private JPanel createBaseInformationPanel() {
        // Create Panel Components
        final JLabel lblCompanyGenerationMethod = new JLabel(resources.getString("lblCompanyGenerationMethod.text"));
        lblCompanyGenerationMethod.setToolTipText(resources.getString("lblCompanyGenerationMethod.toolTipText"));
        lblCompanyGenerationMethod.setName("lblCompanyGenerationMethod");

        setComboCompanyGenerationMethod(new MMComboBox<>("comboCompanyGenerationMethod", CompanyGenerationMethod.values()));
        getComboCompanyGenerationMethod().setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(final JList<?> list, final Object value,
                                                          final int index, final boolean isSelected,
                                                          final boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CompanyGenerationMethod) {
                    list.setToolTipText(((CompanyGenerationMethod) value).getToolTipText());
                }
                return this;
            }
        });

        setChkGenerateMercenaryCompanyCommandLance(new JCheckBox(resources.getString("chkGenerateMercenaryCompanyCommandLance.text")));
        getChkGenerateMercenaryCompanyCommandLance().setToolTipText(resources.getString("chkGenerateMercenaryCompanyCommandLance.toolTipText"));
        getChkGenerateMercenaryCompanyCommandLance().setName("chkGenerateMercenaryCompanyCommandLance");

        final JLabel lblCompanyCount = new JLabel(resources.getString("lblCompanyCount.text"));
        lblCompanyCount.setToolTipText(resources.getString("lblCompanyCount.toolTipText"));
        lblCompanyCount.setName("lblCompanyCount");

        setSpnCompanyCount(new JSpinner(new SpinnerNumberModel(0, 0, 5, 1)));
        getSpnCompanyCount().setToolTipText(resources.getString("lblCompanyCount.toolTipText"));
        getSpnCompanyCount().setName("spnCompanyCount");

        final JLabel lblIndividualLanceCount = new JLabel(resources.getString("lblIndividualLanceCount.text"));
        lblIndividualLanceCount.setToolTipText(resources.getString("lblIndividualLanceCount.toolTipText"));
        lblIndividualLanceCount.setName("lblIndividualLanceCount");

        setSpnIndividualLanceCount(new JSpinner(new SpinnerNumberModel(0, 0, 2, 1)));
        getSpnIndividualLanceCount().setToolTipText(resources.getString("lblIndividualLanceCount.toolTipText"));
        getSpnIndividualLanceCount().setName("spnIndividualLanceCount");

        final JLabel lblLancesPerCompany = new JLabel(resources.getString("lblLancesPerCompany.text"));
        lblLancesPerCompany.setToolTipText(resources.getString("lblLancesPerCompany.toolTipText"));
        lblLancesPerCompany.setName("lblLancesPerCompany");

        setSpnLancesPerCompany(new JSpinner(new SpinnerNumberModel(3, 2, 6, 1)));
        getSpnLancesPerCompany().setToolTipText(resources.getString("lblLancesPerCompany.toolTipText"));
        getSpnLancesPerCompany().setName("spnLancesPerCompany");

        final JLabel lblLanceSize = new JLabel(resources.getString("lblLanceSize.text"));
        lblLanceSize.setToolTipText(resources.getString("lblLanceSize.toolTipText"));
        lblLanceSize.setName("lblLanceSize");

        setSpnLanceSize(new JSpinner(new SpinnerNumberModel(4, 3, 6, 1)));
        getSpnLanceSize().setToolTipText(resources.getString("lblLanceSize.toolTipText"));
        getSpnLanceSize().setName("spnLanceSize");

        final JLabel lblStarLeagueYear = new JLabel(resources.getString("lblStarLeagueYear.text"));
        lblStarLeagueYear.setToolTipText(resources.getString("lblStarLeagueYear.toolTipText"));
        lblStarLeagueYear.setName("lblStarLeagueYear");

        setSpnStarLeagueYear(new JSpinner(new SpinnerNumberModel(2765, 2571, 2780, 1)));
        getSpnStarLeagueYear().setToolTipText(resources.getString("lblStarLeagueYear.toolTipText"));
        getSpnStarLeagueYear().setName("spnStarLeagueYear");
        getSpnStarLeagueYear().setEditor(new JSpinner.NumberEditor(getSpnStarLeagueYear(), "#"));

        // Programmatically Assign Accessibility Labels
        lblCompanyGenerationMethod.setLabelFor(getComboCompanyGenerationMethod());
        lblCompanyCount.setLabelFor(getSpnCompanyCount());
        lblIndividualLanceCount.setLabelFor(getSpnIndividualLanceCount());
        lblLancesPerCompany.setLabelFor(getSpnLancesPerCompany());
        lblLanceSize.setLabelFor(getSpnLanceSize());
        lblStarLeagueYear.setLabelFor(getSpnStarLeagueYear());

        // Layout the UI
        final JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(resources.getString("baseInformationPanel.title")));
        panel.setName("baseInformationPanel");
        final GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblCompanyGenerationMethod)
                                .addComponent(getComboCompanyGenerationMethod(), GroupLayout.Alignment.LEADING))
                        .addComponent(getChkGenerateMercenaryCompanyCommandLance())
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblCompanyCount)
                                .addComponent(getSpnCompanyCount())
                                .addComponent(lblIndividualLanceCount)
                                .addComponent(getSpnIndividualLanceCount(), GroupLayout.Alignment.LEADING))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblLancesPerCompany)
                                .addComponent(getSpnLancesPerCompany())
                                .addComponent(lblLanceSize)
                                .addComponent(getSpnLanceSize(), GroupLayout.Alignment.LEADING))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblStarLeagueYear)
                                .addComponent(getSpnStarLeagueYear(), GroupLayout.Alignment.LEADING))
        );

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblCompanyGenerationMethod)
                                .addComponent(getComboCompanyGenerationMethod()))
                        .addComponent(getChkGenerateMercenaryCompanyCommandLance())
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblCompanyCount)
                                .addComponent(getSpnCompanyCount())
                                .addComponent(lblIndividualLanceCount)
                                .addComponent(getSpnIndividualLanceCount()))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblLancesPerCompany)
                                .addComponent(getSpnLancesPerCompany())
                                .addComponent(lblLanceSize)
                                .addComponent(getSpnLanceSize()))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblStarLeagueYear)
                                .addComponent(getSpnStarLeagueYear()))
        );

        return panel;
    }

    private JPanel createPersonnelPanel() {
        // Create Panel Components
        setLblTotalSupportPersonnel(new JLabel());
        updateLblTotalSupportPersonnel(0);
        getLblTotalSupportPersonnel().setToolTipText(resources.getString("lblTotalSupportPersonnel.toolTipText"));
        getLblTotalSupportPersonnel().setName("lblTotalSupportPersonnel");

        final JPanel supportPersonnelNumbersPanel = createSupportPersonnelNumbersPanel();

        setChkPoolAssistants(new JCheckBox(resources.getString("chkPoolAssistants.text")));
        getChkPoolAssistants().setToolTipText(resources.getString("chkPoolAssistants.toolTipText"));
        getChkPoolAssistants().setName("chkPoolAssistants");

        setChkGenerateCaptains(new JCheckBox(resources.getString("chkGenerateCaptains.text")));
        getChkGenerateCaptains().setToolTipText(resources.getString("chkGenerateCaptains.toolTipText"));
        getChkGenerateCaptains().setName("chkGenerateCaptains");

        setChkAssignCompanyCommanderFlag(new JCheckBox(resources.getString("chkAssignCompanyCommanderFlag.text")));
        getChkAssignCompanyCommanderFlag().setToolTipText(resources.getString("chkAssignCompanyCommanderFlag.toolTipText"));
        getChkAssignCompanyCommanderFlag().setName("chkAssignCompanyCommanderFlag");

        setChkApplyOfficerStatBonusToWorstSkill(new JCheckBox(resources.getString("chkApplyOfficerStatBonusToWorstSkill.text")));
        getChkApplyOfficerStatBonusToWorstSkill().setToolTipText(resources.getString("chkApplyOfficerStatBonusToWorstSkill.toolTipText"));
        getChkApplyOfficerStatBonusToWorstSkill().setName("chkApplyOfficerStatBonusToWorstSkill");

        setChkAssignBestOfficers(new JCheckBox(resources.getString("chkAssignBestOfficers.text")));
        getChkAssignBestOfficers().setToolTipText(resources.getString("chkAssignBestOfficers.toolTipText"));
        getChkAssignBestOfficers().setName("chkAssignBestOfficers");

        setChkAutomaticallyAssignRanks(new JCheckBox(resources.getString("chkAutomaticallyAssignRanks.text")));
        getChkAutomaticallyAssignRanks().setToolTipText(resources.getString("chkAutomaticallyAssignRanks.toolTipText"));
        getChkAutomaticallyAssignRanks().setName("chkAutomaticallyAssignRanks");

        setChkAssignFounderFlag(new JCheckBox(resources.getString("chkAssignFounderFlag.text")));
        getChkAssignFounderFlag().setToolTipText(resources.getString("chkAssignFounderFlag.toolTipText"));
        getChkAssignFounderFlag().setName("chkAssignFounderFlag");

        // Layout the UI
        final JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(resources.getString("personnelPanel.title")));
        panel.setName("personnelPanel");
        final GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(getLblTotalSupportPersonnel())
                        .addComponent(supportPersonnelNumbersPanel)
                        .addComponent(getChkPoolAssistants())
                        .addComponent(getChkGenerateCaptains())
                        .addComponent(getChkAssignCompanyCommanderFlag())
                        .addComponent(getChkApplyOfficerStatBonusToWorstSkill())
                        .addComponent(getChkAssignBestOfficers())
                        .addComponent(getChkAutomaticallyAssignRanks())
                        .addComponent(getChkAssignFounderFlag())
        );

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(getLblTotalSupportPersonnel())
                        .addComponent(supportPersonnelNumbersPanel)
                        .addComponent(getChkPoolAssistants())
                        .addComponent(getChkGenerateCaptains())
                        .addComponent(getChkAssignCompanyCommanderFlag())
                        .addComponent(getChkApplyOfficerStatBonusToWorstSkill())
                        .addComponent(getChkAssignBestOfficers())
                        .addComponent(getChkAutomaticallyAssignRanks())
                        .addComponent(getChkAssignFounderFlag())
        );

        return panel;
    }

    private JPanel createSupportPersonnelNumbersPanel() {
        final PersonnelRole[] personnelRoles = {
                PersonnelRole.MECH_TECH, PersonnelRole.MECHANIC, PersonnelRole.AERO_TECH,
                PersonnelRole.BA_TECH, PersonnelRole.DOCTOR, PersonnelRole.ADMINISTRATOR_COMMAND,
                PersonnelRole.ADMINISTRATOR_LOGISTICS, PersonnelRole.ADMINISTRATOR_TRANSPORT, PersonnelRole.ADMINISTRATOR_HR
        };

        // Create Panel Components
        setSpnSupportPersonnelNumbers(new HashMap<>());
        final Map<PersonnelRole, JLabel> labels = new HashMap<>();
        for (final PersonnelRole role : personnelRoles) {
            final String name = role.getName(getCampaign().getFaction().isClan());
            final String toolTipText = String.format(resources.getString("supportPersonnelNumber.toolTipText"), name);

            labels.put(role, new JLabel(name));
            labels.get(role).setToolTipText(toolTipText);
            labels.get(role).setName("lbl" + role.name());

            getSpnSupportPersonnelNumbers().put(role, new JSpinner(new SpinnerNumberModel(0, 0, 100, 1)));
            getSpnSupportPersonnelNumbers().get(role).setToolTipText(toolTipText);
            getSpnSupportPersonnelNumbers().get(role).setName("spn" + role.name());

            // Programmatically Assign Accessibility Labels
            labels.get(role).setLabelFor(getSpnSupportPersonnelNumbers().get(role));
        }

        // Layout the UI
        final JPanel panel = new JPanel(new GridLayout(0, 3));
        panel.setBorder(BorderFactory.createTitledBorder(resources.getString("supportPersonnelNumbersPanel.title")));
        panel.setName("supportPersonnelNumbersPanel");

        // This puts the label above the spinner, separated into three columns. From the
        // personnelRoles array declaration, the i tracks the line and the j tracks the
        for (int i = 0; i < (personnelRoles.length / 3.0); i++) {
            for (int j = 0; j < 3; j++) {
                panel.add(labels.get(personnelRoles[j + (3 * i)]));
            }

            for (int j = 0; j < 3; j++) {
                panel.add(getSpnSupportPersonnelNumbers().get(personnelRoles[j + (3 * i)]));
            }
        }

        return panel;
    }

    private JPanel createPersonnelRandomizationPanel() {
        // Initialize Labels Used in ActionListeners
        final JLabel lblCentralPlanet = new JLabel();
        final JLabel lblOriginSearchRadius = new JLabel();
        final JLabel lblOriginDistanceScale = new JLabel();

        // Create Panel Components
        setChkRandomizeOrigin(new JCheckBox(resources.getString("chkRandomizeOrigin.text")));
        getChkRandomizeOrigin().setToolTipText(resources.getString("chkRandomizeOrigin.toolTipText"));
        getChkRandomizeOrigin().setName("chkRandomizeOrigin");
        getChkRandomizeOrigin().addActionListener(evt -> {
            final boolean selected = getChkRandomizeOrigin().isSelected();
            getChkRandomizeAroundCentralPlanet().setEnabled(selected);
            getChkCentralSystemFactionSpecific().setEnabled(selected && getChkRandomizeAroundCentralPlanet().isSelected());
            lblCentralPlanet.setEnabled(selected && getChkRandomizeAroundCentralPlanet().isSelected());
            getComboCentralSystem().setEnabled(selected && getChkRandomizeAroundCentralPlanet().isSelected());
            getComboCentralPlanet().setEnabled(selected && getChkRandomizeAroundCentralPlanet().isSelected());
            lblOriginSearchRadius.setEnabled(selected);
            getSpnOriginSearchRadius().setEnabled(selected);
            getChkExtraRandomOrigin().setEnabled(selected);
            lblOriginDistanceScale.setEnabled(selected);
            getSpnOriginDistanceScale().setEnabled(selected);
        });

        setChkRandomizeAroundCentralPlanet(new JCheckBox(resources.getString("chkRandomizeAroundCentralPlanet.text")));
        getChkRandomizeAroundCentralPlanet().setToolTipText(resources.getString("chkRandomizeAroundCentralPlanet.toolTipText"));
        getChkRandomizeAroundCentralPlanet().setName("chkRandomizeAroundCentralPlanet");
        getChkRandomizeAroundCentralPlanet().addActionListener(evt -> {
            final boolean selected = getChkRandomizeAroundCentralPlanet().isSelected()
                    && getChkRandomizeAroundCentralPlanet().isEnabled();
            getChkCentralSystemFactionSpecific().setEnabled(selected);
            lblCentralPlanet.setEnabled(selected);
            getComboCentralSystem().setEnabled(selected);
            getComboCentralPlanet().setEnabled(selected);
        });

        setChkCentralSystemFactionSpecific(new JCheckBox(resources.getString("FactionSpecific.text")));
        getChkCentralSystemFactionSpecific().setToolTipText(resources.getString("chkCentralSystemFactionSpecific.toolTipText"));
        getChkCentralSystemFactionSpecific().setName("chkCentralSystemFactionSpecific");
        getChkCentralSystemFactionSpecific().addActionListener(evt -> {
            final PlanetarySystem system = getComboCentralSystem().getSelectedItem();
            if ((system == null)
                    || !system.getFactionSet(getCampaign().getLocalDate()).contains(getCampaign().getFaction())) {
                restoreComboCentralSystem();
            }
        });

        lblCentralPlanet.setText(resources.getString("lblCentralPlanet.text"));
        lblCentralPlanet.setToolTipText(resources.getString("lblCentralPlanet.toolTipText"));
        lblCentralPlanet.setName("lblCentralPlanet");

        setComboCentralSystem(new MMComboBox<>("comboCentralSystem"));
        getComboCentralSystem().setToolTipText(resources.getString("comboCentralSystem.toolTipText"));
        getComboCentralSystem().setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(final JList<?> list, final Object value,
                                                          final int index, final boolean isSelected,
                                                          final boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof PlanetarySystem) {
                    setText(((PlanetarySystem) value).getName(getCampaign().getLocalDate()));
                }
                return this;
            }
        });
        getComboCentralSystem().addActionListener(evt -> {
            final PlanetarySystem system = getComboCentralSystem().getSelectedItem();
            final Planet planet = getComboCentralPlanet().getSelectedItem();
            if ((system == null) || ((planet != null) && !planet.getParentSystem().equals(system))) {
                restoreComboCentralPlanet();
            }
        });

        setComboCentralPlanet(new MMComboBox<>("comboCentralPlanet"));
        getComboCentralPlanet().setToolTipText(resources.getString("lblCentralPlanet.toolTipText"));
        getComboCentralPlanet().setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(final JList<?> list, final Object value,
                                                          final int index, final boolean isSelected,
                                                          final boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Planet) {
                    setText(((Planet) value).getName(getCampaign().getLocalDate()));
                }
                return this;
            }
        });

        lblOriginSearchRadius.setText(resources.getString("lblOriginSearchRadius.text"));
        lblOriginSearchRadius.setToolTipText(resources.getString("lblOriginSearchRadius.toolTipText"));
        lblOriginSearchRadius.setName("lblOriginSearchRadius");

        setSpnOriginSearchRadius(new JSpinner(new SpinnerNumberModel(0, 0, 2000, 25)));
        getSpnOriginSearchRadius().setToolTipText(resources.getString("lblOriginSearchRadius.toolTipText"));
        getSpnOriginSearchRadius().setName("spnOriginSearchRadius");

        setChkExtraRandomOrigin(new JCheckBox(resources.getString("chkExtraRandomOrigin.text")));
        getChkExtraRandomOrigin().setToolTipText(resources.getString("chkExtraRandomOrigin.toolTipText"));
        getChkExtraRandomOrigin().setName("chkExtraRandomOrigin");

        lblOriginDistanceScale.setText(resources.getString("lblOriginDistanceScale.text"));
        lblOriginDistanceScale.setToolTipText(resources.getString("lblOriginDistanceScale.toolTipText"));
        lblOriginDistanceScale.setName("lblOriginDistanceScale");

        setSpnOriginDistanceScale(new JSpinner(new SpinnerNumberModel(0.6, 0.1, 2.0, 0.1)));
        getSpnOriginDistanceScale().setToolTipText(resources.getString("lblOriginDistanceScale.toolTipText"));
        getSpnOriginDistanceScale().setName("spnOriginDistanceScale");

        // Programmatically Assign Accessibility Labels
        lblCentralPlanet.setLabelFor(getComboCentralPlanet());
        lblOriginSearchRadius.setLabelFor(getSpnOriginSearchRadius());
        lblOriginDistanceScale.setLabelFor(getSpnOriginDistanceScale());

        // Disable Panel by Default
        getChkRandomizeOrigin().setSelected(true);
        getChkRandomizeOrigin().doClick();

        // Layout the UI
        final JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(resources.getString("personnelRandomizationPanel.title")));
        panel.setName("personnelRandomizationPanel");
        final GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(getChkRandomizeOrigin())
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(getChkRandomizeAroundCentralPlanet())
                                .addComponent(getChkCentralSystemFactionSpecific(), GroupLayout.Alignment.LEADING))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblCentralPlanet)
                                .addComponent(getComboCentralSystem())
                                .addComponent(getComboCentralPlanet(), GroupLayout.Alignment.LEADING))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblOriginSearchRadius)
                                .addComponent(getSpnOriginSearchRadius(), GroupLayout.Alignment.LEADING))
                        .addComponent(getChkExtraRandomOrigin())
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblOriginDistanceScale)
                                .addComponent(getSpnOriginDistanceScale(), GroupLayout.Alignment.LEADING))
        );

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(getChkRandomizeOrigin())
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(getChkRandomizeAroundCentralPlanet())
                                .addComponent(getChkCentralSystemFactionSpecific()))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblCentralPlanet)
                                .addComponent(getComboCentralSystem())
                                .addComponent(getComboCentralPlanet()))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblOriginSearchRadius)
                                .addComponent(getSpnOriginSearchRadius()))
                        .addComponent(getChkExtraRandomOrigin())
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblOriginDistanceScale)
                                .addComponent(getSpnOriginDistanceScale()))
        );

        return panel;
    }

    private JPanel createStartingSimulationPanel() {
        // Initialize Labels Used in ActionListeners
        final JLabel lblSimulationDuration = new JLabel();

        // Create Panel Components
        setChkRunStartingSimulation(new JCheckBox(resources.getString("chkRunStartingSimulation.text")));
        getChkRunStartingSimulation().setToolTipText(resources.getString("chkRunStartingSimulation.toolTipText"));
        getChkRunStartingSimulation().setName("chkRunStartingSimulation");
        getChkRunStartingSimulation().addActionListener(evt -> {
            final boolean selected = getChkRunStartingSimulation().isSelected();
            lblSimulationDuration.setEnabled(selected);
            getSpnSimulationDuration().setEnabled(selected);
            getChkSimulateRandomMarriages().setEnabled(selected);
            getChkSimulateRandomProcreation().setEnabled(selected);
        });

        lblSimulationDuration.setText(resources.getString("lblSimulationDuration.text"));
        lblSimulationDuration.setToolTipText(resources.getString("lblSimulationDuration.toolTipText"));
        lblSimulationDuration.setName("lblSimulationDuration");

        setSpnSimulationDuration(new JSpinner(new SpinnerNumberModel(0, 0, 25, 1)));
        getSpnSimulationDuration().setToolTipText(resources.getString("lblSimulationDuration.toolTipText"));
        getSpnSimulationDuration().setName("spnSimulationDuration");

        setChkSimulateRandomMarriages(new JCheckBox(resources.getString("chkSimulateRandomMarriages.text")));
        getChkSimulateRandomMarriages().setToolTipText(resources.getString("chkSimulateRandomMarriages.toolTipText"));
        getChkSimulateRandomMarriages().setName("chkSimulateRandomMarriages");

        setChkSimulateRandomProcreation(new JCheckBox(resources.getString("chkSimulateRandomProcreation.text")));
        getChkSimulateRandomProcreation().setToolTipText(resources.getString("chkSimulateRandomProcreation.toolTipText"));
        getChkSimulateRandomProcreation().setName("chkSimulateRandomProcreation");

        // Programmatically Assign Accessibility Labels
        lblSimulationDuration.setLabelFor(getSpnSimulationDuration());

        // Disable Panel Portions by Default
        getChkRunStartingSimulation().setSelected(true);
        getChkRunStartingSimulation().doClick();

        // Layout the UI
        final JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(resources.getString("startingSimulationPanel.title")));
        panel.setName("startingSimulationPanel");
        final GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(getChkRunStartingSimulation())
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblSimulationDuration)
                                .addComponent(getSpnSimulationDuration(), GroupLayout.Alignment.LEADING))
                        .addComponent(getChkSimulateRandomMarriages())
                        .addComponent(getChkSimulateRandomProcreation())
        );

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(getChkRunStartingSimulation())
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblSimulationDuration)
                                .addComponent(getSpnSimulationDuration()))
                        .addComponent(getChkSimulateRandomMarriages())
                        .addComponent(getChkSimulateRandomProcreation())
        );

        return panel;
    }

    private JPanel createUnitsPanel() {
        // Create Panel Components
        setChkGenerateUnitsAsAttached(new JCheckBox(resources.getString("chkGenerateUnitsAsAttached.text")));
        getChkGenerateUnitsAsAttached().setToolTipText(resources.getString("chkGenerateUnitsAsAttached.toolTipText"));
        getChkGenerateUnitsAsAttached().setName("chkGenerateUnitsAsAttached");

        setChkAssignBestRollToUnitCommander(new JCheckBox(resources.getString("chkAssignBestRollToUnitCommander.text")));
        getChkAssignBestRollToUnitCommander().setToolTipText(resources.getString("chkAssignBestRollToUnitCommander.toolTipText"));
        getChkAssignBestRollToUnitCommander().setName("chkAssignBestRollToUnitCommander");

        setChkSortStarLeagueUnitsFirst(new JCheckBox(resources.getString("chkSortStarLeagueUnitsFirst.text")));
        getChkSortStarLeagueUnitsFirst().setToolTipText(resources.getString("chkSortStarLeagueUnitsFirst.toolTipText"));
        getChkSortStarLeagueUnitsFirst().setName("chkSortStarLeagueUnitsFirst");

        setChkGroupByWeight(new JCheckBox(resources.getString("chkGroupByWeight.text")));
        getChkGroupByWeight().setToolTipText(resources.getString("chkGroupByWeight.toolTipText"));
        getChkGroupByWeight().setName("chkGroupByWeight");

        setChkGroupByQuality(new JCheckBox(resources.getString("chkGroupByQuality.text")));
        getChkGroupByQuality().setToolTipText(resources.getString("chkGroupByQuality.toolTipText"));
        getChkGroupByQuality().setName("chkGroupByQuality");

        setChkKeepOfficerRollsSeparate(new JCheckBox(resources.getString("chkKeepOfficerRollsSeparate.text")));
        getChkKeepOfficerRollsSeparate().setToolTipText(resources.getString("chkKeepOfficerRollsSeparate.toolTipText"));
        getChkKeepOfficerRollsSeparate().setName("chkKeepOfficerRollsSeparate");

        setChkAssignTechsToUnits(new JCheckBox(resources.getString("chkAssignTechsToUnits.text")));
        getChkAssignTechsToUnits().setToolTipText(resources.getString("chkAssignTechsToUnits.toolTipText"));
        getChkAssignTechsToUnits().setName("chkAssignTechsToUnits");

        // Layout the UI
        final JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(resources.getString("unitsPanel.title")));
        panel.setName("unitsPanel");
        final GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(getChkGenerateUnitsAsAttached())
                        .addComponent(getChkAssignBestRollToUnitCommander())
                        .addComponent(getChkSortStarLeagueUnitsFirst())
                        .addComponent(getChkGroupByWeight())
                        .addComponent(getChkGroupByQuality())
                        .addComponent(getChkKeepOfficerRollsSeparate())
                        .addComponent(getChkAssignTechsToUnits())
        );

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(getChkGenerateUnitsAsAttached())
                        .addComponent(getChkAssignBestRollToUnitCommander())
                        .addComponent(getChkSortStarLeagueUnitsFirst())
                        .addComponent(getChkGroupByWeight())
                        .addComponent(getChkGroupByQuality())
                        .addComponent(getChkKeepOfficerRollsSeparate())
                        .addComponent(getChkAssignTechsToUnits())
        );

        return panel;
    }

    private JPanel createUnitPanel() {
        // Create Panel Components
        final JLabel lblForceNamingMethod = new JLabel(resources.getString("lblForceNamingMethod.text"));
        lblForceNamingMethod.setToolTipText(resources.getString("lblForceNamingMethod.toolTipText"));
        lblForceNamingMethod.setName("lblForceNamingMethod");

        setComboForceNamingMethod(new MMComboBox<>("comboForceNamingMethod", ForceNamingMethod.values()));
        getComboForceNamingMethod().setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(final JList<?> list, final Object value,
                                                          final int index, final boolean isSelected,
                                                          final boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ForceNamingMethod) {
                    list.setToolTipText(((ForceNamingMethod) value).getToolTipText());
                }
                return this;
            }
        });

        setChkGenerateForceIcons(new JCheckBox(resources.getString("chkGenerateForceIcons.text")));
        getChkGenerateForceIcons().setToolTipText(resources.getString("chkGenerateForceIcons.toolTipText"));
        getChkGenerateForceIcons().setName("chkGenerateForceIcons");

        // Programmatically Assign Accessibility Labels
        lblForceNamingMethod.setLabelFor(getComboForceNamingMethod());

        // Layout the UI
        final JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(resources.getString("unitPanel.title")));
        panel.setName("unitPanel");
        final GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblForceNamingMethod)
                                .addComponent(getComboForceNamingMethod(), GroupLayout.Alignment.LEADING))
                        .addComponent(getChkGenerateForceIcons())
        );

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblForceNamingMethod)
                                .addComponent(getComboForceNamingMethod()))
                        .addComponent(getChkGenerateForceIcons())
        );
        return panel;
    }

    private JPanel createSparesPanel() {
        // Initialize Labels Used in ActionListeners
        final JLabel lblSparesPercentOfActiveUnits = new JLabel();
        final JLabel lblNumberReloadsPerWeapon = new JLabel();

        // Create Panel Components
        setChkGenerateMothballedSpareUnits(new JCheckBox(resources.getString("chkGenerateMothballedSpareUnits.text")));
        getChkGenerateMothballedSpareUnits().setToolTipText(resources.getString("chkGenerateMothballedSpareUnits.toolTipText"));
        getChkGenerateMothballedSpareUnits().setName("chkGenerateMothballedSpareUnits");
        getChkGenerateMothballedSpareUnits().addActionListener(evt -> {
            final boolean selected = getChkGenerateMothballedSpareUnits().isSelected();
            lblSparesPercentOfActiveUnits.setEnabled(selected);
            getSpnSparesPercentOfActiveUnits().setEnabled(selected);
        });

        lblSparesPercentOfActiveUnits.setText(resources.getString("lblSparesPercentOfActiveUnits.text"));
        lblSparesPercentOfActiveUnits.setToolTipText(resources.getString("lblSparesPercentOfActiveUnits.toolTipText"));
        lblSparesPercentOfActiveUnits.setName("lblSparesPercentOfActiveUnits");

        setSpnSparesPercentOfActiveUnits(new JSpinner(new SpinnerNumberModel(0, 0, 100, 1)));
        getSpnSparesPercentOfActiveUnits().setToolTipText(resources.getString("chkGenerateMothballedSpareUnits.toolTipText"));
        getSpnSparesPercentOfActiveUnits().setName("spnGenerateMothballedSpareUnits");

        final JLabel lblPartGenerationMethod = new JLabel(resources.getString("lblPartGenerationMethod.text"));
        lblPartGenerationMethod.setToolTipText(resources.getString("lblPartGenerationMethod.toolTipText"));
        lblPartGenerationMethod.setName("lblPartGenerationMethod");

        setComboPartGenerationMethod(new MMComboBox<>("comboPartGenerationMethod", PartGenerationMethod.values()));
        getComboPartGenerationMethod().setToolTipText(resources.getString("lblPartGenerationMethod.toolTipText"));
        getComboPartGenerationMethod().setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(final JList<?> list, final Object value,
                                                          final int index, final boolean isSelected,
                                                          final boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof PartGenerationMethod) {
                    list.setToolTipText(((PartGenerationMethod) value).getToolTipText());
                }
                return this;
            }
        });

        final JLabel lblStartingArmourWeight = new JLabel(resources.getString("lblStartingArmourWeight.text"));
        lblStartingArmourWeight.setToolTipText(resources.getString("lblStartingArmourWeight.toolTipText"));
        lblStartingArmourWeight.setName("lblStartingArmourWeight");

        setSpnStartingArmourWeight(new JSpinner(new SpinnerNumberModel(0, 0, 500, 1)));
        getSpnStartingArmourWeight().setToolTipText(resources.getString("lblStartingArmourWeight.toolTipText"));
        getSpnStartingArmourWeight().setName("spnStartingArmourWeight");

        setChkGenerateSpareAmmunition(new JCheckBox(resources.getString("chkGenerateSpareAmmunition.text")));
        getChkGenerateSpareAmmunition().setToolTipText(resources.getString("chkGenerateSpareAmmunition.toolTipText"));
        getChkGenerateSpareAmmunition().setName("chkGenerateSpareAmmunition");
        getChkGenerateSpareAmmunition().addActionListener(evt -> {
            final boolean selected = getChkGenerateSpareAmmunition().isSelected();
            lblNumberReloadsPerWeapon.setEnabled(selected);
            getSpnNumberReloadsPerWeapon().setEnabled(selected);
            getChkGenerateFractionalMachineGunAmmunition().setEnabled(selected);
        });

        lblNumberReloadsPerWeapon.setText(resources.getString("lblNumberReloadsPerWeapon.text"));
        lblNumberReloadsPerWeapon.setToolTipText(resources.getString("lblNumberReloadsPerWeapon.toolTipText"));
        lblNumberReloadsPerWeapon.setName("lblNumberReloadsPerWeapon");

        setSpnNumberReloadsPerWeapon(new JSpinner(new SpinnerNumberModel(0, 0, 25, 1)));
        getSpnNumberReloadsPerWeapon().setToolTipText(resources.getString("lblNumberReloadsPerWeapon.toolTipText"));
        getSpnNumberReloadsPerWeapon().setName("spnNumberReloadsPerWeapon");

        setChkGenerateFractionalMachineGunAmmunition(new JCheckBox(resources.getString("chkGenerateFractionalMachineGunAmmunition.text")));
        getChkGenerateFractionalMachineGunAmmunition().setToolTipText(resources.getString("chkGenerateFractionalMachineGunAmmunition.toolTipText"));
        getChkGenerateFractionalMachineGunAmmunition().setName("chkGenerateFractionalMachineGunAmmunition");

        // Programmatically Assign Accessibility Labels
        lblSparesPercentOfActiveUnits.setLabelFor(getSpnSparesPercentOfActiveUnits());
        lblPartGenerationMethod.setLabelFor(getComboPartGenerationMethod());
        lblStartingArmourWeight.setLabelFor(getSpnStartingArmourWeight());
        lblNumberReloadsPerWeapon.setLabelFor(getSpnNumberReloadsPerWeapon());

        // Disable Panel Portions by Default
        getChkGenerateMothballedSpareUnits().setSelected(true);
        getChkGenerateMothballedSpareUnits().doClick();
        getChkGenerateSpareAmmunition().setSelected(true);
        getChkGenerateSpareAmmunition().doClick();

        // Layout the UI
        final JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(resources.getString("sparesPanel.title")));
        panel.setName("sparesPanel");
        final GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(getChkGenerateMothballedSpareUnits())
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblSparesPercentOfActiveUnits)
                                .addComponent(getSpnSparesPercentOfActiveUnits(), GroupLayout.Alignment.LEADING))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblPartGenerationMethod)
                                .addComponent(getComboPartGenerationMethod(), GroupLayout.Alignment.LEADING))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblStartingArmourWeight)
                                .addComponent(getSpnStartingArmourWeight(), GroupLayout.Alignment.LEADING))
                        .addComponent(getChkGenerateSpareAmmunition())
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblNumberReloadsPerWeapon)
                                .addComponent(getSpnNumberReloadsPerWeapon(), GroupLayout.Alignment.LEADING))
                        .addComponent(getChkGenerateFractionalMachineGunAmmunition())
        );

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(getChkGenerateMothballedSpareUnits())
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblSparesPercentOfActiveUnits)
                                .addComponent(getSpnSparesPercentOfActiveUnits()))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblPartGenerationMethod)
                                .addComponent(getComboPartGenerationMethod()))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblStartingArmourWeight)
                                .addComponent(getSpnStartingArmourWeight()))
                        .addComponent(getChkGenerateSpareAmmunition())
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblNumberReloadsPerWeapon)
                                .addComponent(getSpnNumberReloadsPerWeapon()))
                        .addComponent(getChkGenerateFractionalMachineGunAmmunition())
        );

        return panel;
    }

    private JPanel createContractsPanel() {
        // Create Panel Components
        setChkSelectStartingContract(new JCheckBox(resources.getString("chkSelectStartingContract.text")));
        getChkSelectStartingContract().setToolTipText(resources.getString("chkSelectStartingContract.toolTipText"));
        getChkSelectStartingContract().setName("chkSelectStartingContract");
        getChkSelectStartingContract().addActionListener(evt -> {
            final boolean selected = getChkSelectStartingContract().isSelected();
            getChkStartCourseToContractPlanet().setEnabled(selected);
        });

        setChkStartCourseToContractPlanet(new JCheckBox(resources.getString("chkStartCourseToContractPlanet.text")));
        getChkStartCourseToContractPlanet().setToolTipText(resources.getString("chkStartCourseToContractPlanet.toolTipText"));
        getChkStartCourseToContractPlanet().setName("chkStartCourseToContractPlanet");

        // Disable Panel by Default
        getChkSelectStartingContract().setSelected(true);
        getChkSelectStartingContract().doClick();

        // Layout the UI
        final JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(resources.getString("contractsPanel.title")));
        panel.setName("contractsPanel");
        final GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(getChkSelectStartingContract())
                        .addComponent(getChkStartCourseToContractPlanet())
        );

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(getChkSelectStartingContract())
                        .addComponent(getChkStartCourseToContractPlanet())
        );

        return panel;
    }

    private JPanel createFinancesPanel() {
        // Initialize Labels Used in ActionListeners
        final JLabel lblRandomStartingCashDiceCount = new JLabel();

        // Create Panel Components
        final JLabel lblStartingCash = new JLabel(resources.getString("lblStartingCash.text"));
        lblStartingCash.setToolTipText(resources.getString("lblStartingCash.toolTipText"));
        lblStartingCash.setName("lblStartingCash");

        setSpnStartingCash(new JSpinner(new SpinnerNumberModel(0, 0, 200000000, 100000)));
        getSpnStartingCash().setToolTipText(resources.getString("lblStartingCash.toolTipText"));
        getSpnStartingCash().setName("spnStartingCash");

        setChkRandomizeStartingCash(new JCheckBox(resources.getString("chkRandomizeStartingCash.text")));
        getChkRandomizeStartingCash().setToolTipText(resources.getString("chkRandomizeStartingCash.toolTipText"));
        getChkRandomizeStartingCash().setName("chkRandomizeStartingCash");
        getChkRandomizeStartingCash().addActionListener(evt -> {
            final boolean selected = getChkRandomizeStartingCash().isSelected();
            lblStartingCash.setEnabled(!selected);
            getSpnStartingCash().setEnabled(!selected);
            lblRandomStartingCashDiceCount.setEnabled(selected);
            getSpnRandomStartingCashDiceCount().setEnabled(selected);
        });

        lblRandomStartingCashDiceCount.setText(resources.getString("lblRandomStartingCashDiceCount.text"));
        lblRandomStartingCashDiceCount.setToolTipText(resources.getString("lblRandomStartingCashDiceCount.toolTipText"));
        lblRandomStartingCashDiceCount.setName("lblRandomStartingCashDiceCount");

        setSpnRandomStartingCashDiceCount(new JSpinner(new SpinnerNumberModel(8, 1, 100, 1)));
        getSpnRandomStartingCashDiceCount().setToolTipText(resources.getString("lblRandomStartingCashDiceCount.toolTipText"));
        getSpnRandomStartingCashDiceCount().setName("spnRandomStartingCashDiceCount");

        final JLabel lblMinimumStartingFloat = new JLabel(resources.getString("lblMinimumStartingFloat.text"));
        lblMinimumStartingFloat.setToolTipText(resources.getString("lblMinimumStartingFloat.toolTipText"));
        lblMinimumStartingFloat.setName("lblMinimumStartingFloat");

        setSpnMinimumStartingFloat(new JSpinner(new SpinnerNumberModel(0, 0, 10000000, 100000)));
        getSpnMinimumStartingFloat().setToolTipText("lblMinimumStartingFloat.toolTipText");
        getSpnMinimumStartingFloat().setName("spnMinimumStartingFloat");

        setChkPayForSetup(new JCheckBox(resources.getString("chkPayForSetup.text")));
        getChkPayForSetup().setToolTipText(resources.getString("chkPayForSetup.toolTipText"));
        getChkPayForSetup().setName("chkPayForSetup");
        getChkPayForSetup().addActionListener(evt -> {
            final boolean selected = getChkPayForSetup().isSelected();
            getChkStartingLoan().setEnabled(selected);
            getChkPayForPersonnel().setEnabled(selected);
            getChkPayForUnits().setEnabled(selected);
            getChkPayForParts().setEnabled(selected);
            getChkPayForArmour().setEnabled(selected);
            getChkPayForAmmunition().setEnabled(selected);
        });

        setChkStartingLoan(new JCheckBox(resources.getString("chkStartingLoan.text")));
        getChkStartingLoan().setToolTipText(resources.getString("chkStartingLoan.toolTipText"));
        getChkStartingLoan().setName("chkStartingLoan");

        setChkPayForPersonnel(new JCheckBox(resources.getString("chkPayForPersonnel.text")));
        getChkPayForPersonnel().setToolTipText(resources.getString("chkPayForPersonnel.toolTipText"));
        getChkPayForPersonnel().setName("chkPayForPersonnel");

        setChkPayForUnits(new JCheckBox(resources.getString("chkPayForUnits.text")));
        getChkPayForUnits().setToolTipText(resources.getString("chkPayForUnits.toolTipText"));
        getChkPayForUnits().setName("chkPayForUnits");

        setChkPayForParts(new JCheckBox(resources.getString("chkPayForParts.text")));
        getChkPayForParts().setToolTipText(resources.getString("chkPayForParts.toolTipText"));
        getChkPayForParts().setName("chkPayForParts");

        setChkPayForArmour(new JCheckBox(resources.getString("chkPayForArmour.text")));
        getChkPayForArmour().setToolTipText(resources.getString("chkPayForArmour.toolTipText"));
        getChkPayForArmour().setName("chkPayForArmour");

        setChkPayForAmmunition(new JCheckBox(resources.getString("chkPayForAmmunition.text")));
        getChkPayForAmmunition().setToolTipText(resources.getString("chkPayForAmmunition.toolTipText"));
        getChkPayForAmmunition().setName("chkPayForAmmunition");

        // Programmatically Assign Accessibility Labels
        lblStartingCash.setLabelFor(getSpnStartingCash());
        lblRandomStartingCashDiceCount.setLabelFor(getSpnRandomStartingCashDiceCount());
        lblMinimumStartingFloat.setLabelFor(getSpnMinimumStartingFloat());

        // Disable Panel Portions by Default
        getChkRandomizeStartingCash().setSelected(true);
        getChkRandomizeStartingCash().doClick();

        // Layout the UI
        final JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(resources.getString("financesPanel.title")));
        panel.setName("financesPanel");
        final GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblStartingCash)
                                .addComponent(getSpnStartingCash(), GroupLayout.Alignment.LEADING))
                        .addComponent(getChkRandomizeStartingCash())
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblRandomStartingCashDiceCount)
                                .addComponent(getSpnRandomStartingCashDiceCount(), GroupLayout.Alignment.LEADING))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblMinimumStartingFloat)
                                .addComponent(getSpnMinimumStartingFloat(), GroupLayout.Alignment.LEADING))
                        .addComponent(getChkPayForSetup())
                        .addComponent(getChkStartingLoan())
                        .addComponent(getChkPayForPersonnel())
                        .addComponent(getChkPayForUnits())
                        .addComponent(getChkPayForParts())
                        .addComponent(getChkPayForArmour())
                        .addComponent(getChkPayForAmmunition())
        );

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblStartingCash)
                                .addComponent(getSpnStartingCash()))
                        .addComponent(getChkRandomizeStartingCash())
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblRandomStartingCashDiceCount)
                                .addComponent(getSpnRandomStartingCashDiceCount()))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(lblMinimumStartingFloat)
                                .addComponent(getSpnMinimumStartingFloat()))
                        .addComponent(getChkPayForSetup())
                        .addComponent(getChkStartingLoan())
                        .addComponent(getChkPayForPersonnel())
                        .addComponent(getChkPayForUnits())
                        .addComponent(getChkPayForParts())
                        .addComponent(getChkPayForArmour())
                        .addComponent(getChkPayForAmmunition())
        );

        return panel;
    }

    private JPanel createSurprisesPanel() {
        // Initialize Components Used in ActionListeners
        final JPanel mysteryBoxPanel = new JDisableablePanel("mysteryBoxPanel");

        // Create Panel Components
        setChkGenerateSurprises(new JCheckBox(resources.getString("chkGenerateSurprises.text")));
        getChkGenerateSurprises().setToolTipText(resources.getString("chkGenerateSurprises.toolTipText"));
        getChkGenerateSurprises().setName("chkGenerateSurprises");
        getChkGenerateSurprises().addActionListener(evt -> {
            final boolean selected = getChkGenerateSurprises().isSelected();
            getChkGenerateMysteryBoxes().setEnabled(selected);
            mysteryBoxPanel.setEnabled(selected && getChkGenerateMysteryBoxes().isSelected());
        });

        setChkGenerateMysteryBoxes(new JCheckBox(resources.getString("chkGenerateMysteryBoxes.text")));
        getChkGenerateMysteryBoxes().setToolTipText(resources.getString("chkGenerateMysteryBoxes.toolTipText"));
        getChkGenerateMysteryBoxes().setName("chkGenerateMysteryBoxes");
        getChkGenerateMysteryBoxes().addActionListener(evt -> mysteryBoxPanel.setEnabled(
                getChkGenerateMysteryBoxes().isSelected()));

        createMysteryBoxPanel(mysteryBoxPanel);

        // Disable Panel by Default
        getChkGenerateSurprises().setSelected(true);
        getChkGenerateSurprises().doClick();

        // Layout the UI
        final JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(resources.getString("surprisesPanel.title")));
        panel.setToolTipText(resources.getString("surprisesPanel.toolTipText"));
        panel.setName("surprisesPanel");
        final GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(getChkGenerateSurprises())
                        .addComponent(getChkGenerateMysteryBoxes())
                        .addComponent(mysteryBoxPanel)
        );

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(getChkGenerateSurprises())
                        .addComponent(getChkGenerateMysteryBoxes())
                        .addComponent(mysteryBoxPanel)
        );

        // TODO : Remove me and implement Surprises
        panel.setEnabled(false);
        getChkGenerateSurprises().setEnabled(false);
        getChkGenerateMysteryBoxes().setEnabled(false);
        mysteryBoxPanel.setEnabled(false);

        return panel;
    }

    private void createMysteryBoxPanel(final JPanel panel) {
        // Create Panel
        panel.setBorder(BorderFactory.createTitledBorder(resources.getString("mysteryBoxPanel.title")));
        panel.setToolTipText(resources.getString("mysteryBoxPanel.toolTipText"));
        panel.setLayout(new GridLayout(0, 1));

        // Create Panel Components
        setChkGenerateMysteryBoxTypes(new HashMap<>());
        for (final MysteryBoxType type : MysteryBoxType.values()) {
            getChkGenerateMysteryBoxTypes().put(type, new JCheckBox(type.toString()));
            getChkGenerateMysteryBoxTypes().get(type).setToolTipText(type.getToolTipText());
            getChkGenerateMysteryBoxTypes().get(type).setName("chk" + type.name());
            panel.add(getChkGenerateMysteryBoxTypes().get(type));
        }
    }
    //endregion Initialization

    //region Options
    /**
     * Sets the options for this panel to the default for the provided CompanyGenerationMethod
     * @param method the CompanyGenerationOptions to create the CompanyGenerationOptions from
     */
    public void setOptions(final CompanyGenerationMethod method) {
        setOptions(new CompanyGenerationOptions(method, getCampaign()));
    }

    /**
     * Sets the options for this panel based on the provided CompanyGenerationOptions
     * @param options the CompanyGenerationOptions to use
     */
    public void setOptions(final CompanyGenerationOptions options) {
        // Base Information
        getComboCompanyGenerationMethod().setSelectedItem(options.getMethod());
        getChkGenerateMercenaryCompanyCommandLance().setSelected(options.isGenerateMercenaryCompanyCommandLance());
        getSpnCompanyCount().setValue(options.getCompanyCount());
        getSpnIndividualLanceCount().setValue(options.getIndividualLanceCount());
        getSpnLancesPerCompany().setValue(options.getLancesPerCompany());
        getSpnLanceSize().setValue(options.getLanceSize());
        getSpnStarLeagueYear().setValue(options.getStarLeagueYear());

        // Personnel
        updateLblTotalSupportPersonnel(determineMaximumSupportPersonnel());
        for (final Map.Entry<PersonnelRole, JSpinner> entry : getSpnSupportPersonnelNumbers().entrySet()) {
            entry.getValue().setValue(options.getSupportPersonnel().getOrDefault(entry.getKey(), 0));
        }
        getChkPoolAssistants().setSelected(options.isPoolAssistants());
        getChkGenerateCaptains().setSelected(options.isGenerateCaptains());
        getChkAssignCompanyCommanderFlag().setSelected(options.isAssignCompanyCommanderFlag());
        getChkApplyOfficerStatBonusToWorstSkill().setSelected(options.isApplyOfficerStatBonusToWorstSkill());
        getChkAssignBestOfficers().setSelected(options.isAssignBestOfficers());
        getChkAutomaticallyAssignRanks().setSelected(options.isAutomaticallyAssignRanks());
        getChkAssignFounderFlag().setSelected(options.isAssignFounderFlag());

        // Personnel Randomization
        if (getChkRandomizeOrigin().isSelected() != options.isRandomizeOrigin()) {
            getChkRandomizeOrigin().doClick();
        }

        if (getChkRandomizeAroundCentralPlanet().isSelected() != options.isRandomizeAroundCentralPlanet()) {
            getChkRandomizeAroundCentralPlanet().doClick();
        }
        getChkCentralSystemFactionSpecific().setSelected(false);
        restoreComboCentralSystem();
        getComboCentralSystem().setSelectedItem(options.getCentralPlanet().getParentSystem());
        getComboCentralPlanet().setSelectedItem(options.getCentralPlanet());
        getSpnOriginSearchRadius().setValue(options.getOriginSearchRadius());
        getChkExtraRandomOrigin().setSelected(options.isExtraRandomOrigin());
        getSpnOriginDistanceScale().setValue(options.getOriginDistanceScale());

        // Starting Simulation
        if (getChkRunStartingSimulation().isSelected() != options.isRunStartingSimulation()) {
            getChkRunStartingSimulation().doClick();
        }
        getSpnSimulationDuration().setValue(options.getSimulationDuration());
        getChkSimulateRandomMarriages().setSelected(options.isSimulateRandomMarriages());
        getChkSimulateRandomProcreation().setSelected(options.isSimulateRandomProcreation());

        // Units
        getChkGenerateUnitsAsAttached().setSelected(options.isGenerateUnitsAsAttached());
        getChkAssignBestRollToUnitCommander().setSelected(options.isAssignBestRollToUnitCommander());
        getChkSortStarLeagueUnitsFirst().setSelected(options.isSortStarLeagueUnitsFirst());
        getChkGroupByWeight().setSelected(options.isGroupByWeight());
        getChkGroupByQuality().setSelected(options.isGroupByQuality());
        getChkKeepOfficerRollsSeparate().setSelected(options.isKeepOfficerRollsSeparate());
        getChkAssignTechsToUnits().setSelected(options.isAssignTechsToUnits());

        // Unit
        getComboForceNamingMethod().setSelectedItem(options.getForceNamingMethod());
        getChkGenerateForceIcons().setSelected(options.isGenerateForceIcons());

        // Spares
        if (getChkGenerateMothballedSpareUnits().isSelected() != options.isGenerateMothballedSpareUnits()) {
            getChkGenerateMothballedSpareUnits().doClick();
        }
        getSpnSparesPercentOfActiveUnits().setValue(options.getSparesPercentOfActiveUnits());
        getComboPartGenerationMethod().setSelectedItem(options.getPartGenerationMethod());
        getSpnStartingArmourWeight().setValue(options.getStartingArmourWeight());
        if (getChkGenerateSpareAmmunition().isSelected() != options.isGenerateSpareAmmunition()) {
            getChkGenerateSpareAmmunition().doClick();
        }
        getSpnNumberReloadsPerWeapon().setValue(options.getNumberReloadsPerWeapon());
        getChkGenerateFractionalMachineGunAmmunition().setSelected(options.isGenerateFractionalMachineGunAmmunition());

        // Contracts
        if (getChkSelectStartingContract().isSelected() != options.isSelectStartingContract()) {
            getChkSelectStartingContract().doClick();
        }
        getChkStartCourseToContractPlanet().setSelected(options.isStartCourseToContractPlanet());

        // Finances
        getSpnStartingCash().setValue(options.getStartingCash());
        getChkRandomizeStartingCash().setSelected(options.isRandomizeStartingCash());
        getSpnRandomStartingCashDiceCount().setValue(options.getRandomStartingCashDiceCount());
        getSpnMinimumStartingFloat().setValue(options.getMinimumStartingFloat());
        getChkPayForSetup().setSelected(options.isPayForSetup());
        getChkStartingLoan().setSelected(options.isStartingLoan());
        getChkPayForPersonnel().setSelected(options.isPayForPersonnel());
        getChkPayForUnits().setSelected(options.isPayForUnits());
        getChkPayForParts().setSelected(options.isPayForParts());
        getChkPayForArmour().setSelected(options.isPayForArmour());
        getChkPayForAmmunition().setSelected(options.isPayForAmmunition());

        // Surprises
        if (getChkGenerateSurprises().isSelected() != options.isGenerateSurprises()) {
            getChkGenerateSurprises().doClick();
        }

        if (getChkGenerateMysteryBoxes().isSelected() != options.isGenerateMysteryBoxes()) {
            getChkGenerateMysteryBoxes().doClick();
        }

        for (final Map.Entry<MysteryBoxType, JCheckBox> entry : getChkGenerateMysteryBoxTypes().entrySet()) {
            entry.getValue().setSelected(options.getGenerateMysteryBoxTypes().getOrDefault(entry.getKey(), false));
        }
    }

    /**
     * @return the CompanyGenerationOptions created from the current panel
     */
    public CompanyGenerationOptions createOptionsFromPanel() {
        final CompanyGenerationOptions options = new CompanyGenerationOptions(
                getComboCompanyGenerationMethod().getSelectedItem(), getCampaign());

        // Base Information
        options.setGenerateMercenaryCompanyCommandLance(getChkGenerateMercenaryCompanyCommandLance().isSelected());
        options.setCompanyCount((Integer) getSpnCompanyCount().getValue());
        options.setIndividualLanceCount((Integer) getSpnIndividualLanceCount().getValue());
        options.setLancesPerCompany((Integer) getSpnLancesPerCompany().getValue());
        options.setLanceSize((Integer) getSpnLanceSize().getValue());
        options.setStarLeagueYear((Integer) getSpnStarLeagueYear().getValue());

        // Personnel
        options.setSupportPersonnel(new HashMap<>());
        for (final Map.Entry<PersonnelRole, JSpinner> entry : getSpnSupportPersonnelNumbers().entrySet()) {
            final int value = (int) entry.getValue().getValue();
            if (value <= 0) {
                continue;
            }
            options.getSupportPersonnel().put(entry.getKey(), value);
        }
        options.setPoolAssistants(getChkPoolAssistants().isSelected());
        options.setGenerateCaptains(getChkGenerateCaptains().isSelected());
        options.setAssignCompanyCommanderFlag(getChkAssignCompanyCommanderFlag().isSelected());
        options.setApplyOfficerStatBonusToWorstSkill(getChkApplyOfficerStatBonusToWorstSkill().isSelected());
        options.setAssignBestOfficers(getChkAssignBestOfficers().isSelected());
        options.setAutomaticallyAssignRanks(getChkAutomaticallyAssignRanks().isSelected());
        options.setAssignFounderFlag(getChkAssignFounderFlag().isSelected());

        // Personnel Randomization
        options.setRandomizeOrigin(getChkRandomizeOrigin().isSelected());
        options.setRandomizeAroundCentralPlanet(getChkRandomizeAroundCentralPlanet().isSelected());
        options.setCentralPlanet(getComboCentralPlanet().getSelectedItem());
        options.setOriginSearchRadius((Integer) getSpnOriginSearchRadius().getValue());
        options.setExtraRandomOrigin(getChkExtraRandomOrigin().isSelected());
        options.setOriginDistanceScale((Double) getSpnOriginDistanceScale().getValue());

        // Starting Simulation
        options.setRunStartingSimulation(getChkRunStartingSimulation().isSelected());
        options.setSimulationDuration((Integer) getSpnSimulationDuration().getValue());
        options.setSimulateRandomMarriages(getChkSimulateRandomMarriages().isSelected());
        options.setSimulateRandomProcreation(getChkSimulateRandomProcreation().isSelected());

        // Units
        options.setGenerateUnitsAsAttached(getChkGenerateUnitsAsAttached().isSelected());
        options.setAssignBestRollToUnitCommander(getChkAssignBestRollToUnitCommander().isSelected());
        options.setSortStarLeagueUnitsFirst(getChkSortStarLeagueUnitsFirst().isSelected());
        options.setGroupByWeight(getChkGroupByWeight().isSelected());
        options.setGroupByQuality(getChkGroupByQuality().isSelected());
        options.setKeepOfficerRollsSeparate(getChkKeepOfficerRollsSeparate().isSelected());
        options.setAssignTechsToUnits(getChkAssignTechsToUnits().isSelected());

        // Unit
        options.setForceNamingMethod(getComboForceNamingMethod().getSelectedItem());
        options.setGenerateForceIcons(getChkGenerateForceIcons().isSelected());

        // Spares
        options.setGenerateMothballedSpareUnits(getChkGenerateMothballedSpareUnits().isSelected());
        options.setSparesPercentOfActiveUnits((Integer) getSpnSparesPercentOfActiveUnits().getValue());
        options.setPartGenerationMethod(getComboPartGenerationMethod().getSelectedItem());
        options.setStartingArmourWeight((Integer) getSpnStartingArmourWeight().getValue());
        options.setGenerateSpareAmmunition(getChkGenerateSpareAmmunition().isSelected());
        options.setNumberReloadsPerWeapon((Integer) getSpnNumberReloadsPerWeapon().getValue());
        options.setGenerateFractionalMachineGunAmmunition(getChkGenerateFractionalMachineGunAmmunition().isSelected());

        // Contracts
        options.setSelectStartingContract(getChkSelectStartingContract().isSelected());
        options.setStartCourseToContractPlanet(getChkStartCourseToContractPlanet().isSelected());

        // Finances
        options.setStartingCash((Integer) getSpnStartingCash().getValue());
        options.setRandomizeStartingCash(getChkRandomizeStartingCash().isSelected());
        options.setRandomStartingCashDiceCount((Integer) getSpnRandomStartingCashDiceCount().getValue());
        options.setMinimumStartingFloat((Integer) getSpnMinimumStartingFloat().getValue());
        options.setPayForSetup(getChkPayForSetup().isSelected());
        options.setStartingLoan(getChkStartingLoan().isSelected());
        options.setPayForPersonnel(getChkPayForPersonnel().isSelected());
        options.setPayForUnits(getChkPayForUnits().isSelected());
        options.setPayForParts(getChkPayForParts().isSelected());
        options.setPayForArmour(getChkPayForArmour().isSelected());
        options.setPayForAmmunition(getChkPayForAmmunition().isSelected());

        // Surprises
        options.setGenerateSurprises(getChkGenerateSurprises().isSelected());
        options.setGenerateMysteryBoxes(getChkGenerateMysteryBoxes().isSelected());
        options.setGenerateMysteryBoxTypes(new HashMap<>());
        for (final Map.Entry<MysteryBoxType, JCheckBox> entry : getChkGenerateMysteryBoxTypes().entrySet()) {
            options.getGenerateMysteryBoxTypes().put(entry.getKey(), entry.getValue().isSelected());
        }

        return options;
    }

    /**
     * Validates the data contained in this panel
     * @return true if the data validates successfully, otherwise false
     */
    public boolean validateOptions() {
        //region Errors
        // Minimum Generation Size Validation
        // Minimum Generation Parameter of 1 Company or Lance, the Company Command Lance Doesn't Count
        if (((int) getSpnCompanyCount().getValue() <= 0)
                && ((int) getSpnIndividualLanceCount().getValue() <= 0)) {
            JOptionPane.showMessageDialog(getFrame(),
                    resources.getString("CompanyGenerationOptionsPanel.InvalidGenerationSize.text"),
                    resources.getString("CompanyGenerationOptionsPanel.InvalidOptions.title"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Central System/Planet Validation
        if ((getComboCentralSystem().getSelectedItem() == null)
                || (getComboCentralPlanet().getSelectedItem() == null)) {
            JOptionPane.showMessageDialog(getFrame(),
                    resources.getString("CompanyGenerationOptionsPanel.InvalidCentralPlanet.text"),
                    resources.getString("CompanyGenerationOptionsPanel.InvalidOptions.title"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        //endregion Errors

        //region Warnings
        // Support Personnel Count:
        // 1) Above Recommended Maximum Support Personnel Count
        // 2) Below Half of Recommended Maximum Support Personnel Count
        final int maximumSupportPersonnelCount = determineMaximumSupportPersonnel();
        final int currentSupportPersonnelCount = getSpnSupportPersonnelNumbers().values().stream()
                .mapToInt(spinner -> (int) spinner.getValue()).sum();
        if ((maximumSupportPersonnelCount < currentSupportPersonnelCount)
                && (JOptionPane.showConfirmDialog(getFrame(),
                        resources.getString("CompanyGenerationOptionsPanel.OverMaximumSupportPersonnel.text"),
                        resources.getString("CompanyGenerationOptionsPanel.OverMaximumSupportPersonnel.title"),
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION)) {
            return false;
        } else if ((currentSupportPersonnelCount < (maximumSupportPersonnelCount / 2.0))
                && (JOptionPane.showConfirmDialog(getFrame(),
                        resources.getString("CompanyGenerationOptionsPanel.UnderHalfMaximumSupportPersonnel.text"),
                        resources.getString("CompanyGenerationOptionsPanel.UnderHalfMaximumSupportPersonnel.title"),
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION)) {
            return false;
        }
        //endregion Warnings

        // The options specified are correct, and thus can be saved
        return true;
    }
    //endregion Options

    //region File I/O
    /**
     * Imports CompanyGenerationOptions from an XML file
     */
    public void importOptionsFromXML() {
        FileDialogs.openCompanyGenerationOptions(getFrame())
                .ifPresent(file -> setOptions(CompanyGenerationOptions.parseFromXML(getCampaign(), file)));
    }

    /**
     * Exports the CompanyGenerationOptions displayed on this panel to an XML file.
     */
    public void exportOptionsToXML() {
        FileDialogs.saveCompanyGenerationOptions(getFrame())
                .ifPresent(file -> createOptionsFromPanel().writeToFile(file));
    }
    //endregion File I/O
}
