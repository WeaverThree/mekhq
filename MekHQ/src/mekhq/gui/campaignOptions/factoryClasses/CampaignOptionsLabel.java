package mekhq.gui.campaignOptions.factoryClasses;

import megamek.common.annotations.Nullable;

import javax.swing.*;

import java.util.ResourceBundle;

import static megamek.client.ui.WrapLayout.wordWrap;
import static megamek.client.ui.swing.util.FlatLafStyleBuilder.setFontScaling;
import static mekhq.gui.campaignOptions.CampaignOptionsUtilities.processWrapSize;

/**
 * This class provides a custom {@link JLabel} for campaign options.
 * The label name and tooltips are fetched from a resource bundle based on the provided name.
 */
public class CampaignOptionsLabel extends JLabel {
    private static final String RESOURCE_PACKAGE = "mekhq/resources/CampaignOptionsDialog";
    static final ResourceBundle resources = ResourceBundle.getBundle(RESOURCE_PACKAGE);

    /**
     * Creates a {@link JLabel} with the specified name.
     * <p>
     * Please note that 'name' is also used to fetch the resources associated with this label.
     * For the label text 'name' is appended by '.text'.
     * For the label tooltip 'name' is appended with '.tooltip'.
     * These values must exist in the resource bundle otherwise an error will be thrown.
     *
     * @param name             the name of the label
     */
    public CampaignOptionsLabel(String name) {
        this(name, null, false);
    }

    /**
     * Creates a {@link JLabel} with the specified name and optional customWrapSize.
     * <p>
     * Please note that 'name' is also used to fetch the resources associated with this label.
     * For the label text 'name' is appended by '.text'.
     * For the label tooltip 'name' is appended with '.tooltip'.
     * These values must exist in the resource bundle otherwise an error will be thrown.
     *
     * @param name             the name of the label
     * @param customWrapSize   the maximum number of characters (including whitespaces) on each line
     *                        of the tooltip; defaults to 100 if {@code null}
     * @param noTooltip        if {@code true} the component will be created without a tooltip.
     */
    public CampaignOptionsLabel(String name, @Nullable Integer customWrapSize, boolean noTooltip) {
        super(String.format("<html>%s</html>",
            resources.getString("lbl" + name + ".text")));

        if (!noTooltip) {
            setToolTipText(wordWrap(resources.getString("lbl" + name + ".tooltip"),
                processWrapSize(customWrapSize)));
        }

        setName("lbl" + name);

        setFontScaling(this, false, 1);
    }
}
