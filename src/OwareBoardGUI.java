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
    private int chessNum, boardSide, boardNum;

    public HousesView(Image chessImage, int chessAmount, int cellsColumns, int rowCounts, int boardSide, int boardNum, int initial)
    {
        this.boardSide = boardSide;
        this.boardNum = boardNum;
        //取得旗子圖片之寬與高
        double chessWidth = chessImage.getWidth() / cellsColumns;
        double chessHeight = chessImage.getHeight() / rowCounts;
        clips = new Rectangle2D[chessAmount];
        //建立clip物件
        for(int i=0; i < chessAmount; i++)
            clips[i] = new Rectangle2D((i%cellsColumns)*chessWidth, (i/cellsColumns)*chessHeight, chessWidth, chessHeight);
        //讓滑鼠滑進去時有陰影
        DropShadow drop = new DropShadow(12, Color.GOLD);
        //建立當點下滑鼠時會送出的事件，用以將起始位置傳送給BoardPane
        HousesMoveEvent move = new HousesMoveEvent(getBoardPosition());
        //設定滑鼠滑入與滑出時的事件
        setOnMouseEntered(event -> {
            setEffect(drop);
            setCursor(Cursor.HAND);
        });
        setOnMouseExited(event -> {
            setEffect(null);
            setCursor(null);
        });
        //設定點下滑鼠的事件
        setOnMouseClicked(event -> fireEvent(move));
        setImage(chessImage);
        setChessNum(initial);
    }
    //設定棋洞內的旗子數目
    public void setChessNum(int chessNum)
    {
        setViewport(clips[chessNum]);
        this.chessNum = chessNum;
    }
    //取得棋洞位置
    public int[] getBoardPosition()
    {
        int pos[] = {boardSide, boardNum};
        return pos;
    }
}
class BoardPane extends GridPane
{
    private HousesView[][] houses = new HousesView[2][6];
    private Board oware;

    public BoardPane(Board oware, Image chessImage)
    {
        super();
        this.oware = oware;
        //初始化棋洞，並將棋洞放入棋盤的格子中
        for(int i=0; i < houses.length; i++)
        {
            for(int j=0; j < houses[i].length; j++)
            {
                houses[i][j] = new HousesView(chessImage, 25, 5, 5, i, j, oware.getHouses(i, j));
                add(houses[i][j], j, i);
            }
        }
        //建立事件接收器，當接受到棋洞被點擊傳來的事件時，呼叫HousesMove()進行移動
        this.addEventHandler(HousesMoveEvent.HOUSES_MOVE, event -> HousesMove(event.getPosX(), event.getPosY()));
    }
    //直接調用Board的move方法，並用updateBoard()刷新棋盤
    public void HousesMove(int startSide, int startNum)
    {
        try
        {
            oware.move(startSide, startNum);
            updateBoard();
        }
        catch(InvalidMoveException e)
        {
        }
    }
    //刷新棋盤的函數
    public void updateBoard()
    {
        for(int i=0; i < houses.length; i++)
        {
            for(int j=0; j < houses[i].length; j++)
                houses[i][j].setChessNum(oware.getHouses(i, j));
        }
    }
}
class HousesMoveEvent extends Event
{
    private int[] pos;
    public static final EventType<HousesMoveEvent> HOUSES_MOVE = new EventType<>(Event.ANY, "HOUSES_MOVE");
    public HousesMoveEvent(int[] pos)
    {
        super(HOUSES_MOVE);
        this.pos = pos;
    }
    public int getPosX()
    {
        return pos[0];
    }
    public int getPosY()
    {
        return pos[1];
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
