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

package mekhq.campaign.parts;

import java.util.ResourceBundle;

import megamek.common.CargoBay;
import megamek.common.Mek;
import megamek.common.MiscType;
import megamek.common.Tank;
import megamek.common.WeaponType;
import mekhq.MekHQ;
import mekhq.campaign.Campaign;
import mekhq.campaign.parts.enums.RefitClass;
import mekhq.campaign.parts.enums.RefitStepType;
import mekhq.campaign.parts.equipment.AmmoBin;
import mekhq.campaign.parts.equipment.EquipmentPart;
import mekhq.campaign.parts.equipment.HeatSink;
import mekhq.campaign.parts.equipment.MissingAmmoBin;
import mekhq.campaign.parts.equipment.MissingEquipmentPart;
import mekhq.campaign.parts.equipment.MissingHeatSink;
import mekhq.campaign.unit.Unit;

/**
 * Holds data on one step of a refit process. It calculates as much as possible from the ingredients
 * given to it, but other things will need to be calculated outside of here. All values are based on
 * Campaign Operations... where possible.
 */
public class RefitStep {
    private static final ResourceBundle resources = ResourceBundle.getBundle("mekhq.resources.Parts",
            MekHQ.getMHQOptions().getLocale());
    // region Instance Variables
    private Part neededPart;
    private Part returnsPart;
    private int oldLoc;
    private int newLoc;
    private String oldLocName;
    private String newLocName;
    private String oldPartName;
    private String newPartName;
    private int oldQuantity;
    private int newQuantity;
    private RefitStepType type;
    private String notes;
    private RefitClass refitClass;
    private boolean isFixedEquipmentChange;
    private boolean isArmorDamageOnly;
    private int baseTime;

    // region Initialization
    /**
     * Creates a blank refit step. Might be useful for XML initialization.
     */
    private RefitStep() {
        baseTime = 0;
        refitClass = RefitClass.NO_CHANGE;
        type = RefitStepType.ERROR;
        neededPart = null;
        returnsPart = null;
        isFixedEquipmentChange = false;
        isArmorDamageOnly = false;
        oldLocName = "";
        newLocName = "";
        oldPartName = "";
        newPartName = "";
        notes = "";
    }

    public static RefitStep spcialOmniFixedRefit() {
        RefitStep step = new RefitStep();
        step.oldLocName = "Whole Unit";
        step.newLocName = "Whole Unit";
        step.type = RefitStepType.META;
        step.refitClass = RefitClass.CLASS_F;
        step.notes = resources.getString("RefitStepError.OmniFixedRefit.text");
        return step;
    }

    /**
     * Creates a RefitStep for a Refit operation.
     * @param oldUnit - the unit being refit FROM. Important to understand what kind of unit is involved.
     * @param oldPart - the part on the old unit
     * @param newPart - the part on the new unit
     * @throws IllegalArgumentException
     */
    RefitStep(Unit oldUnit, Part oldPart, Part newPart) throws IllegalArgumentException {
        this(oldUnit, oldPart, newPart, false);
    }

    /**
     * Creates a RefitStep for a Refit operation.
     * @param oldUnit - the unit being refit FROM. Important to understand what kind of unit is involved.
     * @param oldPart - the part on the old unit
     * @param newPart - the part on the new unit
     * @param untracked - is this an untracked part like engine heatsinks
     * @throws IllegalArgumentException
     */
    RefitStep(Unit oldUnit, Part oldPart, Part newPart, boolean untracked) throws IllegalArgumentException {
        this();

        if (null == oldPart && null == newPart) {
            throw new IllegalArgumentException("oldPart and newPart must not both be null");
        }

        Campaign campaign = oldUnit.getCampaign();

        // region Keeping Data

        // We don't actually keep the parts around or even any parts in some cases, so keep the
        // values required to report what's going on

        oldLoc = null == oldPart ? -1 : oldPart.getLocation();
        oldLocName = null == oldPart ? "" : oldPart.getLocationName();
        oldPartName = null == oldPart ? "" : oldPart.getName();
        newLoc = null == newPart ? -1 : newPart.getLocation();
        newLocName = null == newPart ? "" : newPart.getLocationName();
        newPartName = null == newPart ? "" : newPart.getName();

        if (null != oldPart) {
            if (oldPart instanceof Armor) {
                oldQuantity = ((Armor) oldPart).getAmount();
            } else {
                oldQuantity = oldPart.getQuantity();
            }
        } else {
            oldQuantity = 0;
        }

        if (null != newPart) {
            if (newPart instanceof Armor) {
                newQuantity = ((Armor) newPart).getAmount();
            } else {
                newQuantity = newPart.getQuantity();
            }
        } else {
            newQuantity = 0;
        }

        // region Untracked Items

        if (untracked) {
            if ((oldPart instanceof AeroHeatSink) && (newPart instanceof AeroHeatSink)) {
                AeroHeatSink oldAHS = (AeroHeatSink) oldPart;
                AeroHeatSink newAHS = (AeroHeatSink) oldPart;
                refitClass = RefitClass.CLASS_B; // Engine Heat Sinks - treating all untracked HS as this for now
                isFixedEquipmentChange = true;

                if (oldAHS.getType() == newAHS.getType()) {
                    if (oldAHS.getQuantity() == newAHS.getQuantity()) {
                        refitClass = RefitClass.NO_CHANGE;
                        type = RefitStepType.LEAVE;
                        isFixedEquipmentChange = false;
                        return;
                    } else {
                        int oldAHSQuant = oldAHS.getQuantity();
                        int newAHSQuant = newAHS.getQuantity();
                        AeroHeatSink deltaAHS = oldAHS.clone();
                        int delta = 0;
                        if (oldAHSQuant > newAHSQuant) {
                            delta = oldAHSQuant - newAHSQuant;
                            deltaAHS.setQuantity(delta);
                            type = RefitStepType.REMOVE_UNTRACKED_SINKS;
                            returnsPart = deltaAHS;
                        } else {
                            delta = newAHSQuant - oldAHSQuant;
                            deltaAHS.setQuantity(delta);
                            type = RefitStepType.ADD_UNTRACKED_SINKS;
                            neededPart = deltaAHS;
                        }
                        baseTime = delta * 20; // TODO: Class basetimes are off? - WeaverThree
                        return;
                    }
                } else {
                    // Changing HS Type
                    type = RefitStepType.CHANGE_UNTRACKED_SINKS;
                    returnsPart = oldAHS.clone();
                    neededPart = newAHS.clone();
                    baseTime = oldAHS.getQuantity() * 20;
                    baseTime += newAHS.getQuantity() * 20;
                    return;
                }
            } else if (tempIsHeatSink(oldPart) && tempIsHeatSink(newPart)) {
                EquipmentPart oldHS = (EquipmentPart) oldPart;
                EquipmentPart newHS = (EquipmentPart) newPart;
                refitClass = RefitClass.CLASS_B; // Engine Heat Sinks - treating all untracked HS as this for now
                isFixedEquipmentChange = true;

                if(oldHS.getType().equals(newHS.getType())) {
                    if(oldHS.getQuantity() == newHS.getQuantity()) {
                        refitClass = RefitClass.NO_CHANGE;
                        type = RefitStepType.LEAVE;
                        isFixedEquipmentChange = false;
                        return;
                    } else {
                        int oldHSQuant = oldHS.getQuantity();
                        int newHSQuant = newHS.getQuantity();
                        EquipmentPart deltaHS = oldHS.clone();
                        int delta = 0;
                        if (oldHSQuant > newHSQuant) {
                            delta = oldHSQuant - newHSQuant;
                            deltaHS.setQuantity(delta);
                            type = RefitStepType.REMOVE_UNTRACKED_SINKS;
                            returnsPart = deltaHS;
                        } else {
                            delta = newHSQuant - oldHSQuant;
                            deltaHS.setQuantity(delta);
                            type = RefitStepType.ADD_UNTRACKED_SINKS;
                            neededPart = deltaHS;
                        }
                        if (oldUnit.getEntity() instanceof Mek) {
                            baseTime = 90; // Meks treat engine sinks as "one location" for time
                        } else {
                            baseTime = 20 * delta; // All vehicles?
                        }
                        return;
                    }
                } else {
                    // Changing HS Type
                    type = RefitStepType.CHANGE_UNTRACKED_SINKS;
                    returnsPart = oldHS.clone();
                    neededPart = newHS.clone();
                    if (oldUnit.getEntity() instanceof Mek) {
                        baseTime = 180; // One operation to remove, one operation to install?
                    } else {
                        baseTime = 20 * oldHS.getQuantity(); // All vehicles?
                        baseTime += 20 * newHS.getQuantity();
                    }
                    return;
                }
            }

            // If we reach this point for untracked, something has gone wrong
        
            type = RefitStepType.ERROR;
            refitClass = RefitClass.PLEASE_REPAIR;
            return;
        }

        // region Armor

        if ((oldPart instanceof Armor && newPart instanceof Armor)) {
            // Refit code should have found us armors from the same location
            Armor oldArmor = (Armor) oldPart;
            Armor newArmor = (Armor) newPart;
            if ((oldLoc != newLoc) || (oldArmor.isRearMounted() != newArmor.isRearMounted())) {
                throw new IllegalArgumentException(
                        "Moving armor between locations directly is not supported. " + oldUnit);
            }
            
            // This covers every armor change except no change
            refitClass = RefitClass.CLASS_A;
            isFixedEquipmentChange = true;
            // Capital scale armor is 10 points per point allocated
            int armorMultipler = oldUnit.getEntity().isCapitalScale() ? 10 : 1;

            if (oldArmor.getType() == newArmor.getType()) {
                if(oldArmor.getAmount() == newArmor.getAmount()) {
                    refitClass = RefitClass.NO_CHANGE;
                    type = RefitStepType.LEAVE;
                    isFixedEquipmentChange = false;
                    return;
                } else {
                    int oldAmount = oldArmor.getAmount();
                    int newAmount = newArmor.getAmount();
                    Armor deltaArmor = oldArmor.clone();
                    int delta = 0;
                    if (oldAmount > newAmount) {
                        delta = oldAmount - newAmount;
                        deltaArmor.setAmount(delta * armorMultipler);
                        type = RefitStepType.REMOVE_ARMOR;
                        returnsPart = deltaArmor;
                    } else {
                        delta = newAmount - oldAmount;
                        deltaArmor.setAmount(delta * armorMultipler);
                        type = RefitStepType.ADD_ARMOR;
                        neededPart = deltaArmor;

                        if (oldArmor.getTotalAmount() == newAmount) {
                            isArmorDamageOnly = true;
                            isFixedEquipmentChange = false;
                        }
                    }
                    baseTime = deltaArmor.getBaseTimeFor(oldUnit.getEntity()) * delta;
                    return;
                }
            } else {
                // Armor types differ, remove old and add new
                type = RefitStepType.CHANGE_ARMOR_TYPE;
                returnsPart = oldArmor.clone();
                neededPart = newArmor.clone();

                ((Armor) returnsPart).setAmount(((Armor) returnsPart).getAmount() * armorMultipler);
                ((Armor) neededPart).setAmount(((Armor) neededPart).getAmount() * armorMultipler);

                baseTime = oldArmor.getBaseTimeFor(oldUnit.getEntity()) * oldArmor.getAmount();
                baseTime += newArmor.getBaseTimeFor(oldUnit.getEntity()) * newArmor.getAmount();
                return;
            }
        
        } else if (oldPart instanceof Armor) {
            refitClass = RefitClass.CLASS_A;
            type = RefitStepType.REMOVE_ARMOR;
            isFixedEquipmentChange = true;
            returnsPart = oldPart.clone();
            baseTime = ((Armor) oldPart).getBaseTimeFor(oldUnit.getEntity()) * ((Armor) oldPart).getAmount();
            return;
        } else if (newPart instanceof Armor) {
            refitClass = RefitClass.CLASS_A;
            type = RefitStepType.ADD_ARMOR;
            isFixedEquipmentChange = true;
            neededPart = newPart.clone();
            baseTime = ((Armor) newPart).getBaseTimeFor(oldUnit.getEntity()) * ((Armor) newPart).getAmount();
            return;
        

        // region Locations

        } else if (((oldPart instanceof MekLocation) || (oldPart instanceof MissingMekLocation))
                && (newPart instanceof MekLocation)) {
            boolean oldTsm;
            int oldStructure;
            if (oldPart instanceof MekLocation) {
                oldTsm = ((MekLocation) oldPart).isTsm();
                oldStructure = ((MekLocation) oldPart).getStructureType();
            } else {
                oldTsm = ((MissingMekLocation) oldPart).isTsm();
                oldStructure = ((MissingMekLocation) oldPart).getStructureType();
            }

            MekLocation newMekLocation = (MekLocation) newPart;

            if (oldTsm == newMekLocation.isTsm() && oldStructure == newMekLocation.getStructureType()) {
                refitClass = RefitClass.NO_CHANGE;
                type = RefitStepType.LEAVE;
                return;
            } else {
                refitClass = RefitClass.CLASS_F;
                type = RefitStepType.CHANGE_STRUCTURE_TYPE;
                baseTime = 0;
                if (oldTsm != newMekLocation.isTsm()) {
                    baseTime += 360;
                }
                if (oldStructure != newMekLocation.getStructureType()) {
                    baseTime += 360;
                }
                neededPart = newPart.clone();
                returnsPart = (oldPart instanceof MekLocation) ? oldPart.clone() : null; // No returning Missing Parts
                return;
            }

        } else if (((oldPart instanceof MissingRotor) || (oldPart instanceof MissingTurret))
                    && null != newPart) {
            // We'll just leave the broken parts on
            refitClass = RefitClass.NO_CHANGE;
            type = RefitStepType.LEAVE;
            baseTime = 0;
            return;

        } else if (((oldPart instanceof Turret) || (oldPart instanceof MissingTurret)) && null == newPart) {
            // FIXME: WeaverThree - Removing a Turret is changing the weight of the turret...
            refitClass = RefitClass.CLASS_D;
            type = RefitStepType.REMOVE_TURRET;
            isFixedEquipmentChange = true;
            returnsPart = oldPart instanceof Turret ? oldPart.clone() : null;
            baseTime = 160;
            return;
        } else if ((null == oldPart) && (newPart instanceof Turret)) {
            refitClass = RefitClass.CLASS_F;
            type = RefitStepType.ADD_TURRET;
            isFixedEquipmentChange = true;
            neededPart = newPart.clone();
            baseTime = 160;
            return;

        } else if ((oldPart instanceof TankLocation) && (newPart instanceof TankLocation)) {
            // There's nothing else you can change about a tank location
            refitClass = RefitClass.NO_CHANGE;
            type = RefitStepType.LEAVE;
            baseTime = 0;
            return;
        
        // region CASE

        } else if ((oldPart instanceof CASE) && (newPart instanceof CASE)) {
            // We only get here if the CASE is the same
            refitClass = RefitClass.NO_CHANGE;
            type = RefitStepType.LEAVE;
            baseTime = 0;
            return;

        } else if (oldPart instanceof CASE) {
            refitClass = RefitClass.CLASS_A;
            type = RefitStepType.REMOVE_CASE;
            if(oldUnit.getEntity() instanceof Mek) {
                baseTime = 120;
            } else if (oldUnit.getEntity() instanceof Tank) {
                baseTime = 90;
            } else { // Aero and Large Craft apparently
                baseTime = 60;
            }
            isFixedEquipmentChange = false; // We're just assuming CASE is pod mounted until the full item is implemented.
            return;

        } else if (newPart instanceof CASE) {
            refitClass = RefitClass.CLASS_D;
            type = RefitStepType.ADD_CASE;
            if(oldUnit.getEntity() instanceof Mek) {
                baseTime = 120;
            } else if (oldUnit.getEntity() instanceof Tank) {
                baseTime = 90;
            } else { // Aero and Large Craft apparently
                baseTime = 60;
            }
            isFixedEquipmentChange = false;
            return;


        // region Actuators

        } else if (((oldPart instanceof MekActuator) || (oldPart instanceof MissingMekActuator))
                && (newPart instanceof MekActuator)) { 
            // Some arm actuators can be added/removed but if we have both there's nothing to do
            refitClass = RefitClass.NO_CHANGE;
            type = RefitStepType.LEAVE;
            isFixedEquipmentChange = false;
            return;

        } else if ((oldPart instanceof MekActuator) || (oldPart instanceof MissingMekActuator)) {
            returnsPart = oldPart instanceof MekActuator ? oldPart.clone() : null;
            refitClass = RefitClass.CLASS_A;
            type = RefitStepType.REMOVE;
            baseTime = 90;

            int oldType = oldPart instanceof MekActuator ?
                ((MekActuator) oldPart).getType() : ((MissingMekActuator) oldPart).getType();
            if ((oldType == Mek.ACTUATOR_HAND) || (oldType == Mek.ACTUATOR_LOWER_ARM)) {
                isFixedEquipmentChange = false;
            } else {
                isFixedEquipmentChange = true;
            }
            return;

        } else if (newPart instanceof MekActuator) {
            neededPart = newPart.clone();
            refitClass = RefitClass.CLASS_B;
            type = RefitStepType.ADD;
            baseTime = 90;

            int newType = ((MekActuator) newPart).getType();
            if ((newType == Mek.ACTUATOR_HAND) || (newType == Mek.ACTUATOR_LOWER_ARM)) {
                isFixedEquipmentChange = false;
            } else {
                isFixedEquipmentChange = true;
            }
            return;



        // region Core Equipment
        } else if (((oldPart instanceof EnginePart) || (oldPart instanceof MissingEnginePart))
                && (newPart instanceof EnginePart)) {
            
            boolean equal;
            if (oldPart instanceof EnginePart) {
                equal = oldPart.isSamePartType(newPart);
            } else {
                equal = ((MissingEnginePart) oldPart).isAcceptableReplacement(newPart, true);
            }

            if (equal) {
                refitClass = RefitClass.NO_CHANGE;
                type = RefitStepType.LEAVE;
                baseTime = 0;
                return;
            } else {
                refitClass = RefitClass.CLASS_E; // Refit code responsible for downgrading for kit
                type = RefitStepType.CHANGE;
                baseTime = 360;
                returnsPart = (oldPart instanceof EnginePart) ? oldPart.clone() : null;
                neededPart = newPart.clone();
                return;
            }


        } else if (((oldPart instanceof MekGyro) || (oldPart instanceof MissingMekGyro))
                && (newPart instanceof MekGyro)) {
            
            boolean equal;
            if (oldPart instanceof MekGyro) {
                equal = oldPart.isSamePartType(newPart);
            } else {
                equal = ((MissingMekGyro) oldPart).isAcceptableReplacement(newPart, true);
            }

            if (equal) {
                refitClass = RefitClass.NO_CHANGE;
                type = RefitStepType.LEAVE;
                baseTime = 0;
                return;
            } else {
                refitClass = RefitClass.CLASS_D;
                type = RefitStepType.CHANGE;
                baseTime = 360;
                returnsPart = (oldPart instanceof MekGyro) ? oldPart.clone() : null;
                neededPart = newPart.clone();
                return;
            }


        } else if (((oldPart instanceof MekCockpit) || (oldPart instanceof MissingMekCockpit))
                && (newPart instanceof MekCockpit)) {
            
            boolean equal;
            if (oldPart instanceof MekCockpit) {
                equal = oldPart.isSamePartType(newPart);
            } else {
                equal = ((MissingMekCockpit) oldPart).isAcceptableReplacement(newPart, true);
            }

            if (equal) {
                refitClass = RefitClass.NO_CHANGE;
                type = RefitStepType.LEAVE;
                baseTime = 0;
                return;
            } else {
                refitClass = RefitClass.CLASS_E;
                type = RefitStepType.CHANGE;
                baseTime = 300; // FIXME: WeaverThree - From MissingMekCockpit - not in CamOps
                returnsPart = (oldPart instanceof MekCockpit) ? oldPart.clone() : null;
                neededPart = newPart.clone();
                return;
            }


        } else if (((oldPart instanceof MekSensor) || (oldPart instanceof MissingMekSensor))
                && (newPart instanceof MekSensor)) {
            
            boolean equal;
            if (oldPart instanceof MekSensor) {
                equal = oldPart.isSamePartType(newPart);
            } else {
                equal = ((MissingMekSensor) oldPart).isAcceptableReplacement(newPart, true);
            }

            if (equal) {
                refitClass = RefitClass.NO_CHANGE;
                type = RefitStepType.LEAVE;
                baseTime = 0;
                return;
            } else {
                refitClass = RefitClass.NO_CHANGE;
                type = RefitStepType.ERROR; // FIXME: This shouldn't be a thing, right?
                baseTime = 260; 
                returnsPart = (oldPart instanceof MekSensor) ? oldPart.clone() : null;
                neededPart = newPart.clone();
                return;
            }


        } else if (((oldPart instanceof MekLifeSupport) || (oldPart instanceof MissingMekLifeSupport))
                && (newPart instanceof MekLifeSupport)) {
            
            boolean equal;
            if (oldPart instanceof MekLifeSupport) {
                equal = oldPart.isSamePartType(newPart);
            } else {
                equal = ((MissingMekSensor) oldPart).isAcceptableReplacement(newPart, true);
            }

            if (equal) {
                refitClass = RefitClass.NO_CHANGE;
                type = RefitStepType.LEAVE;
                baseTime = 0;
                return;
            } else {
                refitClass = RefitClass.NO_CHANGE;
                type = RefitStepType.ERROR; // FIXME: This shouldn't be a thing, right?
                baseTime = 180; 
                returnsPart = (oldPart instanceof MekLifeSupport) ? oldPart.clone() : null;
                neededPart = newPart.clone();
                return;
            }

            
        } else if ((oldPart instanceof SpacecraftCoolingSystem) && (newPart instanceof SpacecraftCoolingSystem)) {
            SpacecraftCoolingSystem oldSCCS = (SpacecraftCoolingSystem) oldPart;
            SpacecraftCoolingSystem newSCCS = (SpacecraftCoolingSystem) newPart;

            // Override our part names and stuff 
            oldQuantity = oldSCCS.getTotalSinks();
            newQuantity = newSCCS.getTotalSinks();
            
            AeroHeatSink oldAHS = new AeroHeatSink(0, oldSCCS.getSinkType(), false, campaign);
            AeroHeatSink newAHS = new AeroHeatSink(0, newSCCS.getSinkType(), false, campaign);
            
            oldAHS.setQuantity(oldQuantity);
            newAHS.setQuantity(newQuantity);

            oldPartName = oldAHS.getName();
            newPartName = newAHS.getName();

            refitClass = RefitClass.CLASS_C; // Not going to track location stuff for heatsinks on a big unit.
            isFixedEquipmentChange = true; // I don't think these can be omni but...

            if (oldAHS.getType() == newAHS.getType()) {
                if (oldQuantity == newQuantity) {
                    refitClass = RefitClass.NO_CHANGE;
                    type = RefitStepType.LEAVE;
                    isFixedEquipmentChange = false;
                    return;
                } else {
                    int delta = 0;
                    AeroHeatSink deltaAHS = new AeroHeatSink(0, oldAHS.getType(), false, campaign);

                    if (oldQuantity > newQuantity) {
                        delta = oldQuantity - newQuantity;
                        deltaAHS.setQuantity(delta);
                        returnsPart = deltaAHS;
                        type = RefitStepType.REMOVE_SCCS_SINKS;
                    } else {
                        delta = newQuantity - oldQuantity;
                        deltaAHS.setQuantity(delta);
                        neededPart = deltaAHS;
                        type = RefitStepType.ADD_SCCS_SINKS;
                    }
                    baseTime = (int) (Math.ceil(delta / 50) * 60); // 50/hour round up.
                    return;
                }
            } else {
                // Change HS type
                type = RefitStepType.CHANGE_SCCS_SINKS;
                returnsPart = oldAHS;
                neededPart = newAHS;
                baseTime = (int) (Math.ceil((oldQuantity + newQuantity) / 50) * 60); // 50/hour round up.
                return;
            }
      
            
            
        // region Ammo Bins :<
        
        } else if (((oldPart instanceof AmmoBin) || (oldPart instanceof MissingAmmoBin))
                && (newPart instanceof AmmoBin)) {
            

            // Missing bins hold no shots
            oldQuantity = (oldPart instanceof AmmoBin) ? ((AmmoBin) oldPart).getCurrentShots() : 0;
            // FIXME: Something's wrong with at least the large craft ammo bin that causes it to
            // generate on the new unit with negative shots needed on the dummy new unit, so...
            // different function used here...
            newQuantity = ((AmmoBin) newPart).getFullShots();

            if (oldLoc == newLoc) {
                refitClass = RefitClass.NO_CHANGE;
                type = RefitStepType.LEAVE;
                isFixedEquipmentChange = false;
                return;
            }

            refitClass = RefitClass.CLASS_C;
            type = RefitStepType.MOVE_AMMO;
            isFixedEquipmentChange = !(oldPart.isOmniPodded() && newPart.isOmniPodded());
            baseTime = 240; // 120 out, 120 in
            return;

        } else if ((oldPart instanceof AmmoBin) || (oldPart instanceof MissingAmmoBin)) {

            // Missing bins hold no shots
            oldQuantity = (oldPart instanceof AmmoBin) ? ((AmmoBin) oldPart).getCurrentShots() : 0;
            returnsPart = new AmmoStorage(0, ((AmmoBin) oldPart).getType(), oldQuantity, campaign);

            refitClass = RefitClass.CLASS_A;
            type = RefitStepType.UNLOAD;
            isFixedEquipmentChange = !oldPart.isOmniPodded();
            baseTime = 120;
            return;

        } else if (newPart instanceof AmmoBin) {

            newQuantity = ((AmmoBin) newPart).getFullShots();
            neededPart = new AmmoStorage(0, ((AmmoBin) newPart).getType(), newQuantity, campaign);

            refitClass = RefitClass.CLASS_B;
            type = RefitStepType.LOAD;
            isFixedEquipmentChange = !newPart.isOmniPodded();
            baseTime = 120;
            return; 


        // region Tracked HeatSinks
        } else if ((tempIsHeatSink(oldPart) || tempIsMissingHeatSink(oldPart)) && tempIsHeatSink(newPart)) {

            if (oldLoc == newLoc) {
                refitClass = RefitClass.NO_CHANGE;
                type = RefitStepType.LEAVE;
                isFixedEquipmentChange = false;
                return;
            }
            
            refitClass = RefitClass.CLASS_C;
            type = RefitStepType.MOVE;
            isFixedEquipmentChange = !(oldPart.isOmniPodded() && newPart.isOmniPodded());
            baseTime = (oldUnit.getEntity() instanceof Mek) ? 180 : 40;
            return;

        } else if (tempIsHeatSink(oldPart) || tempIsMissingHeatSink(oldPart)) {

            returnsPart = (oldPart instanceof MissingPart) ? null : oldPart.clone();

            refitClass = RefitClass.CLASS_A;
            type = RefitStepType.REMOVE;
            isFixedEquipmentChange = !oldPart.isOmniPodded();
            baseTime = (oldUnit.getEntity() instanceof Mek) ? 90 : 20; // 20 is all other vehicles
            return;
        
        } else if (tempIsHeatSink(newPart)) {

            neededPart = newPart.clone();

            refitClass = RefitClass.CLASS_B;
            type = RefitStepType.ADD;
            isFixedEquipmentChange = !newPart.isOmniPodded();
            baseTime = (oldUnit.getEntity() instanceof Mek) ? 90 : 20; // 20 is all other vehicles
            return;


        // region Transport Bay Stuff

        } else if ((oldPart instanceof TransportBayPart) && (newPart instanceof TransportBayPart)) { 
            
            oldQuantity = (int) ((TransportBayPart) oldPart).getBay().getCapacity(); 
            newQuantity = (int) ((TransportBayPart) newPart).getBay().getCapacity();

            if (oldLoc != newLoc) {
                throw new IllegalArgumentException("Transport Bays shouldn't move location...");
            }

            refitClass = RefitClass.NO_CHANGE;
            type = RefitStepType.LEAVE;
            isFixedEquipmentChange = false;
            return;
        
        } else if (oldPart instanceof TransportBayPart) {

            oldQuantity = (int) ((TransportBayPart) oldPart).getBay().getCapacity(); 
            returnsPart = oldPart.clone();

            refitClass = RefitClass.CLASS_A;
            type = RefitStepType.REMOVE;
            isFixedEquipmentChange = true;
            // Cargo bays take a month (30 workdays), other bays are managed by cubicle
            baseTime = (((TransportBayPart) oldPart).getBay() instanceof CargoBay) ? 480 * 30 : 0;
            return;

        } else if (newPart instanceof TransportBayPart) {

            newQuantity = (int) ((TransportBayPart) newPart).getBay().getCapacity(); 
            returnsPart = newPart.clone();

            refitClass = RefitClass.CLASS_C; // FIXME: No Location = No Removal Bonus? - WeaverThree
            type = RefitStepType.ADD;
            isFixedEquipmentChange = true;
            // Cargo bays take a month (30 workdays), other bays are managed by cubicle
            baseTime = (((TransportBayPart) newPart).getBay() instanceof CargoBay) ? 480 * 30 : 0;
            return;


        } else if (((oldPart instanceof Cubicle) || (oldPart instanceof MissingCubicle))
                && (newPart instanceof Cubicle)) {

            if (oldLoc != newLoc) {
                throw new IllegalArgumentException("Cubicles shouldn't move location...");
            }

            refitClass = RefitClass.NO_CHANGE;
            type = RefitStepType.LEAVE;
            isFixedEquipmentChange = false;
            return;

        } else if ((oldPart instanceof Cubicle) || (oldPart instanceof MissingCubicle)) {

            returnsPart = (oldPart instanceof Cubicle) ? oldPart.clone() : null;

            refitClass = RefitClass.CLASS_A;
            type = RefitStepType.REMOVE;
            isFixedEquipmentChange = true;
            baseTime = 480 * 7;
            return;

        } else if (newPart instanceof Cubicle) {

            neededPart = newPart.clone();

            refitClass = RefitClass.CLASS_C;
            type = RefitStepType.ADD;
            isFixedEquipmentChange = true;
            baseTime = 480 * 7;
            return;
        

        } else if (((oldPart instanceof BayDoor) || (oldPart instanceof MissingBayDoor))
                && (newPart instanceof BayDoor)) {

            if (oldLoc != newLoc) {
                throw new IllegalArgumentException("Cubicles shouldn't move location...");
            }

            refitClass = RefitClass.NO_CHANGE;
            type = RefitStepType.LEAVE;
            isFixedEquipmentChange = false;
            return;

        } else if ((oldPart instanceof BayDoor) || (oldPart instanceof MissingBayDoor)) {

            returnsPart = (oldPart instanceof BayDoor) ? oldPart.clone() : null;

            refitClass = RefitClass.CLASS_A;
            type = RefitStepType.REMOVE;
            isFixedEquipmentChange = true;
            baseTime = 60 * 10;
            return;

        } else if (newPart instanceof BayDoor) {

            neededPart = newPart.clone();

            refitClass = RefitClass.CLASS_C;
            type = RefitStepType.ADD;
            isFixedEquipmentChange = true;
            baseTime = 60 * 10;
            return;
        



        // region Equipment

        } else if (((oldPart instanceof EquipmentPart) || (oldPart instanceof MissingEquipmentPart))
                    && (newPart instanceof EquipmentPart)) {
            
            if (oldLoc == newLoc) {
                if (((EquipmentPart) oldPart).isRearFacing() == ((EquipmentPart) newPart).isRearFacing()) {    
                    refitClass = RefitClass.NO_CHANGE;
                    type = RefitStepType.LEAVE;
                    isFixedEquipmentChange = false;
                    return;
                } else {
                    refitClass = RefitClass.CLASS_B;
                    type = RefitStepType.CHANGE_FACING;
                    isFixedEquipmentChange = !(oldPart.isOmniPodded() && newPart.isOmniPodded());
                    baseTime = 240; // 120 out, 120 in
                    return;
                }
            }

            refitClass = RefitClass.CLASS_C;
            type = RefitStepType.MOVE;
            isFixedEquipmentChange = !(oldPart.isOmniPodded() && newPart.isOmniPodded());
            baseTime = 240; // 120 out, 120 in
            return;

        } else if ((oldPart instanceof EquipmentPart) || (oldPart instanceof MissingEquipmentPart)) {

            returnsPart = (oldPart instanceof EquipmentPart) ? oldPart.clone() : null;

            refitClass = RefitClass.CLASS_A;
            type = RefitStepType.REMOVE;
            isFixedEquipmentChange = !oldPart.isOmniPodded();
            baseTime = 120;
            return;
        
        } else if (newPart instanceof EquipmentPart) {

            neededPart = newPart.clone();

            refitClass = RefitClass.CLASS_B;
            type = RefitStepType.ADD;
            isFixedEquipmentChange = !newPart.isOmniPodded();
            baseTime = 120;
            return;


        // region Everything Else
        } else if (((oldPart instanceof Part) || (newPart instanceof MissingPart))
                && (newPart instanceof Part)) {
            
            if (oldLoc == newLoc) {
                refitClass = RefitClass.NO_CHANGE;
                type = RefitStepType.LEAVE;
                isFixedEquipmentChange = false;
                return;
            }

            refitClass = RefitClass.CLASS_C;
            type = RefitStepType.MOVE;
            // The MissingPart basetimes seem to be what we need...
            if (oldPart instanceof MissingPart) {
                baseTime = ((MissingPart) oldPart).getBaseTime() * 2;
            } else {
                baseTime = oldPart.getMissingPart().getBaseTime() * 2;
            }
            isFixedEquipmentChange = !(oldPart.isOmniPodded() && newPart.isOmniPodded());
            return;
        
        } else if ((oldPart instanceof Part) || (newPart instanceof MissingPart)) {
    
            returnsPart = !(oldPart instanceof MissingPart) ? oldPart.clone() : null;

            refitClass = RefitClass.CLASS_A;
            type = RefitStepType.REMOVE;
            if (oldPart instanceof MissingPart) {
                baseTime = ((MissingPart) oldPart).getBaseTime();
            } else {
                baseTime = oldPart.getMissingPart().getBaseTime();
            }
            isFixedEquipmentChange = !oldPart.isOmniPodded();
            return;

        } else if (newPart instanceof Part) {

            neededPart = newPart.clone();

            refitClass = RefitClass.CLASS_B;
            type = RefitStepType.ADD;
            baseTime = newPart.getMissingPart().getBaseTime();
            isFixedEquipmentChange = !newPart.isOmniPodded();
            return;

        }

        // If we reach this point, something has gone wrong

        type = RefitStepType.ERROR;
        refitClass = RefitClass.PLEASE_REPAIR;
        
    }


    // region Helpers

    
    public void omnify() {
        type = type.omnify();
        if (type.isOmniType()) {
            refitClass = RefitClass.OMNI_RECONFIG;
            if (type == RefitStepType.MOVE_AMMOPOD || type == RefitStepType.MOVE_OMNIPOD) {
                baseTime = 60;
            } else {
                baseTime = 30;
            }
        } else if (type == RefitStepType.ADD_ARMOR && isArmorDamageOnly) {

            // Want to allow omni refits on units with damaged armor.

            type = RefitStepType.LEAVE;
            refitClass = RefitClass.NO_CHANGE;
            baseTime = 0;
            neededPart = null;
            
        }
    }


    /**
     * Determine if a Part is a heat sink because not all heat sinks are of class HeatSink right now.
     * I hope the need for this function goes away in the future.
     * @param part - the part to check
     * @return is this part a heat sink
     */
    public static boolean tempIsHeatSink(Part part) {
        if (part instanceof HeatSink) {
            return true;
        } else if ((part instanceof EquipmentPart)
                && (((EquipmentPart) part).getType().hasFlag(MiscType.F_LASER_HEAT_SINK)
                    || ((EquipmentPart) part).getType().hasFlag(MiscType.F_COMPACT_HEAT_SINK)
                    || ((EquipmentPart) part).getType().hasFlag(MiscType.F_IS_DOUBLE_HEAT_SINK_PROTOTYPE))) { 
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determine if a Part is a missing heat sink because not all heat sinks are of class HeatSink
     * right now. I hope the need for this function goes away in the future.
     * @param part - the part to check
     * @return is this part a heat sink
     */
    public static boolean tempIsMissingHeatSink(Part part) {
        if (part instanceof MissingHeatSink) {
            return true;
        } else if ((part instanceof MissingEquipmentPart)
                && (((MissingEquipmentPart) part).getType().hasFlag(MiscType.F_LASER_HEAT_SINK)
                    || ((MissingEquipmentPart) part).getType().hasFlag(MiscType.F_COMPACT_HEAT_SINK)
                    || ((MissingEquipmentPart) part).getType().hasFlag(MiscType.F_IS_DOUBLE_HEAT_SINK_PROTOTYPE))) { 
            return true;
        } else {
            return false;
        }
    }

    public static boolean isEquipmentSubtype(Part part, Class targetClass) {
        return (part instanceof EquipmentPart)
                && targetClass.isInstance(((EquipmentPart) part).getType());
    }

    public static boolean isEquipmentOrMissingSubtype(Part part, Class targetClass) {
        boolean isRegular = (part instanceof EquipmentPart)
                && targetClass.isInstance(((EquipmentPart) part).getType());
        boolean isMissing = (part instanceof MissingEquipmentPart)
                && targetClass.isInstance(((MissingEquipmentPart) part).getType());
        return isRegular || isMissing;
    }

    // region Getter/Setters

    public Part getNeededPart() {
        return neededPart;
    }

    public Part getReturnsPart() {
        return returnsPart;
    }

    public int getOldLoc() {
        return oldLoc;
    }

    public int getNewLoc() {
        return newLoc;
    }

    public String getOldLocName() {
        return oldLocName;
    }

    public String getNewLocName() {
        return newLocName;
    }

    public String getOldPartName() {
        return oldPartName;
    }

    public String getNewPartName() {
        return newPartName;
    }

    public int getOldQuantity() {
        return oldQuantity;
    }

    public int getNewQuantity() {
        return newQuantity;
    }

    public RefitStepType getType() {
        return type;
    }

    public void setType(RefitStepType type) {
        this.type = type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public RefitClass getRefitClass() {
        return refitClass;
    }

    public void setRefitClass(RefitClass refitClass) {
        this.refitClass = refitClass;
    }

    public void setRefitClassToHarder(RefitClass refitClass) {
        this.refitClass = this.refitClass.keepHardest(refitClass);
    }

    public int getBaseTime() {
        return baseTime;
    }

    public boolean isFixedEquipmentChange() {
        return isFixedEquipmentChange;
    }



}