package asciiWorld.ui;

public class ScrollableListView extends GridViewPanel {

	private ListView _listView;
	
	public ScrollableListView(ListView listView) throws Exception {
		super(1, 2);
		
		_listView = listView;
		
		StackPanel verticalScrollButtons = new StackPanel(Orientation.Vertical);
		verticalScrollButtons.addChild(new Button("^") {{
			addClickListener(new ButtonClickedEvent() {
				@Override
				public void click(Button button) {
					_listView.setTopIndex(_listView.getTopIndex() - 1);
				}
			});
		}});
		verticalScrollButtons.addChild(new Button("V") {{
			addClickListener(new ButtonClickedEvent() {
				@Override
				public void click(Button button) {
					_listView.setTopIndex(_listView.getTopIndex() + 1);
				}
			});
		}});
		
		setColumnWidth(0, 0.9f);
		setColumnWidth(1, 0.1f);
		addChild(_listView, 0, 0);
		addChild(verticalScrollButtons, 0, 1);
	}

	public ListView getListView() {
		return _listView;
	}
}
