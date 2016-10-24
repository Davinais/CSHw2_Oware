import java.io.IOException;

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
    public static void main(String[] args)
    {
        clear();
        System.out.println("歡迎來到《西非播棋》的世界，遊戲即將開始");
        System.out.println("═════════════════════════════════════════════════");
        Board oware = new Board();
        oware.printBoard();
    }
}
