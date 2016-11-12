import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.regex.*;

public class hw2
{
    //定義一個clear方法，作用於Windows系統的cmd下，或是其他作業系統支援ANSI控制字元的終端機
    public static void clear()
    {
        //由於.waitfor()方法可能throw例外，於是使用try-catch區塊處理
        try
        {
            final String os = System.getProperty("os.name");
            //若是Windows作業系統，假定使用cmd終端機，調用內部cls方法
            if(os.contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            //否則，輸出ANSI控制字元，分別為游標跳至左上角(\033[H)以及清除螢幕(\033[2J)，\033代表於8進位編碼下的Esc字元
            else
            {
                System.out.print("\033[H\033[2J");  
                System.out.flush();
            }
        }
        //自Java 7起的多重catch方式
        catch(IOException | InterruptedException err)
        {
            System.out.println("發生嚴重錯誤，離開程式…");
            System.exit(0);
        }
    }
    public static void commandHelp()
    {
        System.out.println("═══════════════════════════════════════");
        System.out.println("指令說明：");
        System.out.println("    move [編號] ：移動該棋洞的旗子");
        System.out.println("    game over ：結束遊戲");
        System.out.println("    gui ：顯示GUI介面，關閉GUI後將同時退出程式");
        System.out.println("    save ：儲存棋盤至oware.brd");
        System.out.println("    load ：自oware.brd讀取棋盤");
        System.out.println("    help ：顯示本說明");
        System.out.println("═══════════════════════════════════════");
    }
    public static void printGame(Board oware)
    {
        oware.printHands(0, 0);
        oware.printBoard();
        oware.printHands(1, 1);
    }
    public static void main(String[] args)
    {
        clear();
        System.out.println("歡迎來到《西非播棋》的世界，遊戲即將開始(′·ω·`)");
        Board oware = new Board();
        Pattern movecmd = Pattern.compile("move ([12])-([1-6])");
        boolean guilaunched = false;
        commandHelp();
        while(true)
        {
            int player = oware.getCurrentPlayer();
            boolean endturn = false, gameovercmd = false,needprintgame = true;
            if(oware.checkOver(player, false))
            {
                printGame(oware);
                System.out.println("[玩家" + (player+1) + "] 無棋可動，遊戲結束！");
                break;
            }
            do
            {
                if(needprintgame)
                {
                    printGame(oware);
                    needprintgame = false;
                }
                System.out.println("[玩家" + (player+1) + "] 請輸入想進行的指令");
                System.out.print(">>> ");
                String command = ConsoleIn.readLine();
                Matcher movematch = movecmd.matcher(command);
                if(movematch.matches())
                {
                    try
                    {
                        int moveside = (Integer.parseInt(movematch.group(1))-1);
                        int movenum = (Integer.parseInt(movematch.group(2))-1);
                        oware.move(moveside, movenum);
                        endturn = true;
                    }
                    catch(InvalidMoveException e)
                    {
                        System.out.println(e.getMessage());
                    }
                    catch(NumberFormatException e){/*不可能發生*/}
                }
                else
                {
                    switch(command)
                    {
                        case "game over":
                            endturn = true;
                            gameovercmd = true;
                            oware.calcWinner();
                            break;
                        case "help":
                            clear();
                            commandHelp();
                            needprintgame = true;
                            break;
                        case "gui":
                            OwareBoardGUI.setBoard(oware);
                            OwareBoardGUI.launch(OwareBoardGUI.class);
                            endturn = true;
                            guilaunched = true;
                            break;
                        case "save":
                            try
                            {
                                oware.saveBoard();
                            }
                            catch(IOException e)
                            {
                                e.printStackTrace();
                            }
                            break;
                        case "load":
                            try
                            {
                                oware.loadBoard();
                                player = oware.getCurrentPlayer();
                                needprintgame = true;
                            }
                            catch(FileNotFoundException e)
                            {
                                System.out.println(e.getMessage());
                            }
                            catch(IOException e)
                            {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            System.out.println("無此指令，請重新輸入！");
                            System.out.println("輸入help，可以查看指令列表喔！");
                    }
                }
            }while(!endturn);
            if(guilaunched)
            {
                System.out.println("已結束GUI介面，離開遊戲");
                break;
            }
            else if(oware.checkOver(player, true))
            {
                System.out.println("[玩家" + (player+1) + "] 得分棋子數已過半，遊戲結束！");
                break;
            }
            else if(gameovercmd)
            {
                System.out.println("[玩家" + (player+1) + "] 輸入遊戲結束指令，遊戲結束！");
                oware.housesToHands();
                oware.calcWinner();
                break;
            }
            else
                clear();
        }
        if(!guilaunched)
            System.out.println("勝利者為：" + oware.getWinnerName() + "！");
        System.out.println("感謝遊玩《西非播棋》，我們下次再見～");
    }
}
