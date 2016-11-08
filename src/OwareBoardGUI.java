import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Pos;
import javafx.event.*;
import javafx.scene.effect.*;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.Cursor;
import javafx.stage.Popup;
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
        setAvaliable(true);
        setImage(chessImage);
        setChessNum(initial);
    }
    public void setAvaliable(boolean avaliable)
    {
        if(avaliable)
        {
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
        }
        else
        {
            setOnMouseEntered(null);
            setOnMouseExited(null);
            setOnMouseClicked(null);
        }
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
    private Popup invalidMove = new Popup();
    private Label invalidMoveText = new Label();
    private Board oware;
    private Stage boardStage;

    public BoardPane(Board oware, Image chessImage, Stage boardStage)
    {
        super();
        this.oware = oware;
        this.boardStage = boardStage;
        //初始化棋洞，並將棋洞放入棋盤的格子中
        for(int i=0; i < houses.length; i++)
        {
            for(int j=0; j < houses[i].length; j++)
            {
                houses[i][j] = new HousesView(chessImage, 25, 5, 5, i, j, oware.getHouses(i, j));
                add(houses[i][j], j, i);
            }
        }
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-image: url('img/board.png');" + 
        "-fx-background-position: center center;" + 
        "-fx-background-repeat: no-repeat, no-repeat;");
        /*設定當選擇不合法棋洞時會跳出的提示框*/
        //使提示框能夠自動符合其內容的大小
        invalidMove.setAutoFix(true);
        //使提示框在失去焦點時會自動隱藏
        invalidMove.setAutoHide(true);
        //使提示框在按下未被處理的Esc鍵時會自動隱藏
        invalidMove.setHideOnEscape(true);
        invalidMoveText.setStyle("-fx-background-color: seashell;" + "-fx-border-color: black;" +
        "-fx-padding: 6;" + "-fx-border-radius: 5px;" + "-fx-font-size: 12px;");
        //將錯誤訊息物件放入提示框內容中
        invalidMove.getContent().add(invalidMoveText);
        //建立事件接收器，當接受到棋洞被點擊傳來的事件時，呼叫HousesMove()進行移動
        this.addEventHandler(HousesMoveEvent.HOUSES_MOVE, event -> HousesMove(event.getPosX(), event.getPosY()));
    }
    //直接調用Board的move方法，並用updateBoard()刷新棋盤
    public void HousesMove(int startSide, int startNum)
    {
        try
        {
            int moveplayer = oware.getCurrentPlayer();
            int opponent = (moveplayer==0)?1:0;
            oware.move(startSide, startNum);
            updateBoard();
            fireEvent(new UpdateTextEvent());
            if(oware.checkOver(moveplayer, true))
                fireEvent(new GameOverEvent("[玩家" + (moveplayer+1) + "] 得分棋子數已過半，遊戲結束！"));
            else if(oware.checkOver(opponent, false))
                fireEvent(new GameOverEvent("[玩家" + (opponent+1) + "] 無棋可動，遊戲結束！"));
        }
        catch(InvalidMoveException e)
        {
            //設定提示框的錯誤訊息
            invalidMoveText.textProperty().setValue(e.getMessage());
            //設定提示框出現的位置
            invalidMove.setX(boardStage.getX() + houses[startSide][startNum].getLayoutX());
            invalidMove.setY(boardStage.getY() + this.getLayoutY() + (invalidMoveText.getHeight()*startSide) + (this.getHeight()*startSide));
            //顯示提示框
            invalidMove.show(boardStage);
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
    public void setBoardAvaliable(boolean avaliable)
    {
        for(int i=0; i < houses.length; i++)
        {
            for(int j=0; j < houses[i].length; j++)
                houses[i][j].setAvaliable(avaliable);
        }
    }
}
class HandsText extends Text
{
    private int player, hands;
    private String handsShowString = "[玩家%d]得分：%d";
    private Board oware;

    public HandsText(Board oware, int player)
    {
        super();
        this.oware = oware;
        this.player = player;
        setStyle("-fx-font-size: 14pt;");
        updateHands();
    }
    public void updateHands()
    {
        hands = oware.getHands(player);
        textProperty().setValue(String.format(handsShowString, (player+1), hands));
    }
}
class GameOverEvent extends Event
{
    private String message;
    public static final EventType<GameOverEvent> GAME_OVER = new EventType<>(Event.ANY, "GAME_OVER");
    public GameOverEvent(String m)
    {
        super(GAME_OVER);
        message = m;
    }
    public String getMessage()
    {
        return message;
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
class UpdateTextEvent extends Event
{
    public static final EventType<UpdateTextEvent> UPDATE_TEXT = new EventType<>(Event.ANY, "UPDATE_TEXT");
    public UpdateTextEvent()
    {
        super(UPDATE_TEXT);
    }
}
class GameRestartEvent extends Event
{
    public static final EventType<GameRestartEvent> GAME_RESTART = new EventType<>(Event.ANY, "GAME_RESTART");
    public GameRestartEvent()
    {
        super(GAME_RESTART);
    }
}
public class OwareBoardGUI extends Application
{
    private final double margin = 37.0;
    private static Board oware;

    @Override
    public void start(Stage stage)
    {
        Image chessImage = new Image("img/chesses.png");
        BoardPane owareBoard = new BoardPane(oware, chessImage, stage);
        Text currentPlayer = new Text("現在是[玩家" + (oware.getCurrentPlayer()+1) + "]的回合");
        currentPlayer.setTextAlignment(TextAlignment.CENTER);
        currentPlayer.setStyle("-fx-font-size: 16pt;");
        HandsText playerHands[] = new HandsText[2];
        HBox handsBox[] = new HBox[2];
        for(int i=0; i<2; i++)
        {
            playerHands[i] = new HandsText(oware, i);
            handsBox[i] = new HBox(playerHands[i]);
            if(i == 0)
                handsBox[i].setAlignment(Pos.CENTER_LEFT);
            else
                handsBox[i].setAlignment(Pos.CENTER_RIGHT);
        }
        Button gameOverButton = new Button("結束本局");
        gameOverButton.setOnMouseClicked(event -> {
            oware.housesToHands();
            oware.calcWinner();
            gameOverButton.fireEvent(new GameOverEvent("[玩家" + (oware.getCurrentPlayer()+1) + "] 按下結束此局按鈕，遊戲結束！"));
        });
        Button restartButton = new Button("再來一局");
        restartButton.setOnMouseClicked(event -> {
            restartButton.fireEvent(new GameRestartEvent());
        });
        HBox buttonBox = new HBox(restartButton, gameOverButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        restartButton.setVisible(false);
        VBox gameInfo = new VBox(currentPlayer, handsBox[0], owareBoard, handsBox[1], buttonBox);
        VBox.setMargin(handsBox[0], new Insets(0.0, 0.0, 0.0, margin));
        VBox.setMargin(handsBox[1], new Insets(0.0, margin, 0.0, 0.0));
        VBox.setMargin(buttonBox, new Insets(0.0, margin, 0.0, 0.0));
        gameInfo.setAlignment(Pos.CENTER);
        gameInfo.addEventHandler(UpdateTextEvent.UPDATE_TEXT, event -> {
            playerHands[0].updateHands();
            playerHands[1].updateHands();
            currentPlayer.textProperty().setValue("現在是[玩家" + (oware.getCurrentPlayer()+1) + "]的回合");
        });
        Scene scene = new Scene(gameInfo, (600+margin*2), 343, Color.LIGHTGRAY);
        scene.addEventHandler(GameOverEvent.GAME_OVER, event -> {
            owareBoard.setBoardAvaliable(false);
            gameOverButton.setVisible(false);
            restartButton.setVisible(true);
            currentPlayer.textProperty().setValue(event.getMessage() + "\n勝利者為：" + oware.getWinnerName() + "！");
        });
        scene.addEventHandler(GameRestartEvent.GAME_RESTART, event -> {
            oware.initialize();
            owareBoard.setBoardAvaliable(true);
            owareBoard.updateBoard();
            restartButton.fireEvent(new UpdateTextEvent());
            restartButton.setVisible(false);
            gameOverButton.setVisible(true);
        });

        stage.setScene(scene);
        stage.setTitle("西非播棋");
        stage.setResizable(false);
        stage.show();
    }
    public static void setBoard(Board oware)
    {
        OwareBoardGUI.oware = oware;
    }
}
