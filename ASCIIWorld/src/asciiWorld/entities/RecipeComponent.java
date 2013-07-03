package asciiWorld.entities;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class RecipeComponent extends EntityComponent {

	private String _recipeName;
	private String _entityType;
	private Map<String, Integer> _ingredients;
	private int _craftingLevel;
	
	public RecipeComponent(Entity owner) {
		super(owner);
		_recipeName = null;
		_entityType = null;
		_ingredients = new HashMap<String, Integer>();
		_craftingLevel = 0;
	}
	
	@Override
	public String toString() {
		return EntityFactory.get().getTemplate(_entityType).getName();
	}
	
	public String getRecipeName() {
		return _recipeName;
	}
	
	public void setRecipeName(String value) {
		if (!value.equals(_recipeName)) {
			_recipeName = value;
			loadRecipe();
		}
	}
	
	public String getEntityType() {
		return _entityType;
	}
	
	public void setEntityType(String value) {
		_entityType = value;
	}
	
	public Map<String, Integer> getIngredients() {
		return _ingredients;
	}

	public Integer getCraftingLevel() {
		return _craftingLevel;
	}
	
	public void setCraftingLevel(Integer value) {
		_craftingLevel = value;
	}
	
	public boolean meetsRequirements(InventoryContainer inventory) {
		for (String entityType : _ingredients.keySet()) {
			int count = 0;
			for (int index = 0; index < inventory.getItemCount(); index++) {
				if (inventory.getItemAt(index).getType().equals(entityType)) {
					count++;
				}
			}
			if (count < _ingredients.get(entityType)) {
				return false;
			}
		}
		return true;
	}
	
	public Entity generateItem(InventoryContainer inventory) {
		if (!meetsRequirements(inventory)) {
			return null;
		}
		
		for (String entityType : _ingredients.keySet()) {
			for (int index = 0; index < _ingredients.get(entityType); index++) {
				if (!removeItemFromInventory(inventory, entityType)) {
					System.err.println("Unable to remove item from inventory for recipe generation.");
					return null;
				}
			}
		}
		
		Entity outputItem = EntityFactory.get().getResource(_entityType);
		try {
			inventory.add(outputItem);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outputItem;
	}
	
	private boolean removeItemFromInventory(InventoryContainer inventory, String entityType) {
		Entity item = findItem(inventory, entityType);
		if (item != null) {
			try {
				inventory.remove(item);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			System.err.println("Unable to find the item in the inventory for recipe generation.");
		}
		return false;
	}
	
	private Entity findItem(InventoryContainer inventory, String entityType) {
		for (int index = 0; index < inventory.getItemCount(); index++) {
			Entity item = inventory.getItemAt(index);
			if (item.getType().equals(entityType)) {
				return item;
			}
		}
		return null;
	}
	
	private void loadRecipe() {
		String recipePath = getPathForResource(_recipeName);
		try {
			Element recipeElem = (Element)new SAXBuilder().build(new File(recipePath)).getRootElement();
			if (!recipeElem.getName().equals("Recipe")) {
				throw new Exception("This file does not describe a recipe.");
			} else {
				_entityType = recipeElem.getAttributeValue("output");
				_craftingLevel = Integer.parseInt(recipeElem.getAttributeValue("craftingLevel"));
				_ingredients.clear();
				for (Element ingredientElem : recipeElem.getChildren("Ingredient")) {
					_ingredients.put(ingredientElem.getAttributeValue("name"), Integer.parseInt(ingredientElem.getAttributeValue("amount")));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String getPathForResource(String name) {
		return String.format("resources/recipes/%s.xml", name);
	}
}
