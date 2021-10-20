package client;


import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;


public  class ItemRenderer implements Callback<ListView<String>, ListCell<String>> {

    @Override
    public ListCell<String> call(ListView<String> param) {
        ListCell<String> cell = new ListCell<String>(){
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(null);
                setText(null);
                if(item != null){
                    HBox hBox = new HBox();
                    Text name = new Text(item);
                    hBox.getChildren().addAll( name);
                    hBox.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(hBox);
                }else{
                    setText("");
                }
            }
        };
        return cell;
    }
}
