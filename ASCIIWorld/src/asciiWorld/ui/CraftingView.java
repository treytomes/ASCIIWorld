package asciiWorld.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.newdawn.slick.Color;

import asciiWorld.entities.Entity;
import asciiWorld.entities.EntityComponent;
import asciiWorld.entities.EntityFactory;
import asciiWorld.entities.InventoryContainer;
import asciiWorld.entities.RecipeComponent;

public class CraftingView extends GridViewPanel {

	private InventoryContainer _playerInventoryContainer;
	
	private int _craftingLevel;
	private ListView _playerList;
	private StackPanel _outputView;
	
	public CraftingView(InventoryContainer playerInventory, int craftingLevel) {
		super(1, 2);
		
		_craftingLevel = craftingLevel;
		
		_playerInventoryContainer = playerInventory;
		
		setColumnWidth(0, 0.3f);
		setColumnWidth(1, 0.7f);
		
		try {
			// Player Inventory
			GridViewPanel playerGrid = new GridViewPanel(2, 1);
			playerGrid.setRowHeight(0, 0.1f);
			playerGrid.setRowHeight(1, 0.9f);
			playerGrid.addChild(new Label("Inventory", Color.white), 0, 0);
			_playerList = createItemList(_playerInventoryContainer);
			playerGrid.addChild(new ScrollableListView(_playerList), 1, 0);
			addChild(playerGrid, 0, 0);
			
			GridViewPanel subGrid = new GridViewPanel(4, 1);
			subGrid.setRowHeight(0, 0.1f);
			subGrid.setRowHeight(1, 0.4f);
			subGrid.setRowHeight(2, 0.1f);
			subGrid.setRowHeight(3, 0.4f);

			// Recipes
			subGrid.addChild(new Label("Recipes", Color.white), 0, 0);
			subGrid.addChild(createRecipeList(), 1, 0);
			
			// Output
			subGrid.addChild(new Label("Output", Color.white), 2, 0);
			_outputView = new StackPanel(Orientation.Vertical);
			subGrid.addChild(_outputView, 3, 0);
			
			addChild(subGrid, 0, 1);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private ListView createItemList(InventoryContainer inventory) {
		ListView itemsList = new ListView();
		itemsList.setItemsSource(inventory);
		return itemsList;
	}
	
	private ScrollableListView createRecipeList() throws Exception {
		ListView recipeList = new ListView();
		List<RecipeComponent> recipes = new ArrayList<RecipeComponent>();
		
		for (Entity item : _playerInventoryContainer) {
			for (EntityComponent component : item.getComponents()) {
				if (component instanceof RecipeComponent) {
					RecipeComponent recipe = RecipeComponent.class.cast(component);
					if (recipe.getCraftingLevel() <= _craftingLevel) {
						recipes.add(RecipeComponent.class.cast(component));
					}
				}
			}
		}
		
		recipeList.setItemsSource(recipes);
		
		recipeList.addItemSelectedListener(new ListViewItemSelectedEvent() {
			@Override
			public void itemSelected(ListView listView, Object selectedItem) {
				resetOutputViewBinding(selectedItem);
			}
		});
		
		return new ScrollableListView(recipeList);
	}
	
	private void resetOutputViewBinding(final Object selectedItem) {
		final RecipeComponent recipe = RecipeComponent.class.cast(selectedItem);
		final Map<String, Integer> ingredients = recipe.getIngredients();
		
		_outputView.clearChildren();
		try {
			_outputView.addChild(new Label("Ingredients", Color.white));
			_outputView.addChild(new GridViewPanel(ingredients.size() + 1, 3) {{
				addChild(new Label("Name", Color.white), 0, 0);
				addChild(new Label("Need", Color.white), 0, 1);
				addChild(new Label("Have", Color.white), 0, 2);
				
				int row = 1;
				for (String itemType : ingredients.keySet()) {
					addChild(new Label(EntityFactory.get().getTemplate(itemType).getName(), Color.yellow), row, 0);
					addChild(new Label(ingredients.get(itemType), Color.yellow), row, 1);
					addChild(new Label(countInstancesOfItemInPlayerInventory(itemType), Color.yellow), row, 2);
					row++;
				}
			}});
			
			if (recipe.meetsRequirements(_playerInventoryContainer)) {
				_outputView.addChild(new Button("Craft Item") {{
					addClickListener(new ButtonClickedEvent() {
						@Override
						public void click(Button button) {
							recipe.generateItem(_playerInventoryContainer);
							resetOutputViewBinding(selectedItem);
							_playerList.resetBinding();
						}
					});
				}});
			} else {
				_outputView.addChild(new Label("Requirements not met.", Color.red));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private int countInstancesOfItemInPlayerInventory(String itemType) {
		int count = 0;
		for (Entity item : _playerInventoryContainer) {
			if (item.getType().equals(itemType)) {
				count++;
			}
		}
		return count;
	}
}
