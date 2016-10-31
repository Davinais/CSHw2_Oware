import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Pos;
import javafx.event.*;
import javafx.scene.effect.*;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.Cursor;
import javafx.stage.Stage;

class HousesView extends ImageView
{
    private Rectangle2D[] clips;
    private int chessNum;

    public HousesView(Image chessImage, int chessAmount, int cellsColumns, int rowCounts, int initial)
    {
        double chessWidth = chessImage.getWidth() / cellsColumns;
        double chessHeight = chessImage.getHeight() / rowCounts;
        clips = new Rectangle2D[chessAmount];
        //建立clip物件
        for(int i=0; i < chessAmount; i++)
            clips[i] = new Rectangle2D((i%cellsColumns)*chessWidth, (i/cellsColumns)*chessHeight, chessWidth, chessHeight);
        //讓滑鼠滑進去時有陰影
        DropShadow drop = new DropShadow(12, Color.GOLD);
        //建立滑鼠滑入與滑出時的事件
        setOnMouseEntered(event -> {
            setEffect(drop);
            setCursor(Cursor.HAND);
        });
        setOnMouseExited(event -> {
            setEffect(null);
            setCursor(null);
        });
        setImage(chessImage);
        setChessNum(initial);
    }
    public void setChessNum(int chessNum)
    {
        setViewport(clips[chessNum]);
        this.chessNum = chessNum;
    }
}
class BoardPane extends GridPane
{
    private HousesView[] houses = new HousesView[12];
    private Board oware;

    public BoardPane(Board oware, Image chessImage)
    {
        super();
        this.oware = oware;
        for(int i=0; i < houses.length; i++)
        {
            houses[i] = new HousesView(chessImage, 25, 5, 5, oware.getHouses((i/6), (i%6)));
            add(houses[i], (i%6), (i/6));
        }
    }
}
public class OwareBoardGUI extends Application
{
    private static Board oware;

    @Override
    public void start(Stage stage)
    {
        AnchorPane anchorpane = new AnchorPane();
        Image chessImage = new Image("img/chesses.png");
        BoardPane owareBoard = new BoardPane(oware, chessImage);
        owareBoard.setAlignment(Pos.CENTER);
        owareBoard.setStyle("-fx-background-image: url('img/board.png');" + 
        "-fx-background-position: center center;" + 
        "-fx-background-repeat: no-repeat, no-repeat;");
        Scene scene = new Scene(owareBoard, 674, 293, Color.LIGHTGRAY);

        stage.setScene(scene);
        stage.setTitle("西非播棋");
        stage.show();
    }
    public static void setBoard(Board oware)
    {
        OwareBoardGUI.oware = oware;
    }
}
