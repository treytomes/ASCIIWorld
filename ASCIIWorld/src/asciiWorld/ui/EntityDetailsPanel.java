package asciiWorld.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;

import asciiWorld.FontFactory;
import asciiWorld.entities.Entity;

public class EntityDetailsPanel extends GridViewPanel {

	private static final Color COLOR_TEXT_DESCRIPTION = Color.white;
	private static final Color COLOR_TEXT_DETAILS = Color.yellow;
	
	private Object _entityBinding;
	
	public EntityDetailsPanel(Object entityBinding) {
		super(8, 2);
		
		_entityBinding = entityBinding;
		
		setColumnWidth(0, 0.4f);
		setColumnWidth(1, 0.6f);
		
		try {
			UnicodeFont largeFont = FontFactory.get().getResource(30);

			int row = 0;
			addChild(new Label(largeFont, "Details for:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, row, 0);
			addChild(new Label(largeFont, new MethodBinding(_entityBinding, "getName"), COLOR_TEXT_DETAILS) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, row, 1);
			
			row++;
			addChild(new Label("Health:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, row, 0);
			addChild(new Label(new MethodBinding(this, "getHealthText", _entityBinding), COLOR_TEXT_DETAILS) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, row, 1);
			
			row++;
			addChild(new Label("Health regen rate:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, row, 0);
			addChild(new Label(new MethodBinding(_entityBinding, "getHealthRegenRate"), COLOR_TEXT_DETAILS) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, row, 1);
			
			row++;
			addChild(new Label("Weight:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, row, 0);
			addChild(new Label(new MethodBinding(this, "getWeightText", _entityBinding), COLOR_TEXT_DETAILS) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, row, 1);
			
			row++;
			addChild(new Label("Strength:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, row, 0);
			addChild(new Label(new MethodBinding(_entityBinding, "getStrength"), COLOR_TEXT_DETAILS) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, row, 1);
			
			row++;
			addChild(new Label("Agility:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, row, 0);
			addChild(new Label(new MethodBinding(_entityBinding, "getAgility"), COLOR_TEXT_DETAILS) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, row, 1);
			
			row++;
			addChild(new Label("Perception:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, row, 0);
			addChild(new Label(new MethodBinding(_entityBinding, "getPerception"), COLOR_TEXT_DETAILS) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, row, 1);
			
			row++;
			addChild(new Label("Movement speed:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, row, 0);
			addChild(new Label(new MethodBinding(_entityBinding, "getMovementSpeed"), COLOR_TEXT_DETAILS) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, row, 1);
			
			row++;
			addChild(new Label("Range of vision:", COLOR_TEXT_DESCRIPTION) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, row, 0);
			addChild(new Label(new MethodBinding(_entityBinding, "getRangeOfVision"), COLOR_TEXT_DETAILS) {{ setHorizontalContentAlignment(HorizontalAlignment.Left); }}, row, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Entity getEntity() {
		if (_entityBinding instanceof Entity) {
			return Entity.class.cast(_entityBinding);
		} else if (_entityBinding instanceof MethodBinding) {
			return Entity.class.cast(MethodBinding.class.cast(_entityBinding).getValue());
		} else {
			return null;
		}
	}
	
	public String getHealthText(Entity item) {
		return String.format("%d / %d", item.getHealth(), item.getMaxHealth());
	}
	
	public String getWeightText(Entity item) {
		return String.format("%.2f / %.2f", item.getTotalWeight(), item.getMaxWeight());
	}
}
