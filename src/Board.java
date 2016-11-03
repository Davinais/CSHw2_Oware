import java.util.Arrays;

public class Board
{
    private int hands[] = {0, 0};
    private int houses[][] = {{4, 4, 4, 4, 4, 4},{4, 4, 4, 4, 4, 4}};
    private int currentplay = 0, winner = -1;
    //為不採用Integer作迭代，在類別內初始宣告兩迭代值
    private int sideiter = 0, numiter = 0;
    //偽迭代器，逆時針迭代棋洞
    private void housesIterCCW()
    {
        if(sideiter == 0)
        {
            if(numiter == 0)
                sideiter = 1;
            else
                numiter--;
        }
        else
        {
            if(numiter == 5)
                sideiter = 0;
            else
                numiter++;
        }
    }
    //偽迭代器，順時針迭代棋洞
    private void housesIterCW()
    {
        if(sideiter == 0)
        {
            if(numiter == 5)
                sideiter = 1;
            else
                numiter++;
        }
        else
        {
            if(numiter == 0)
                sideiter = 0;
            else
                numiter--;
        }
    }
    public void printBoard()
    {
        String boardstatus =
        "┌  1-1  ┬  1-2  ┬  1-3  ┬  1-4  ┬  1-5  ┬  1-6  ┐\n" +
        "╔·······╦·······╦·······╦·······╦·······╦·······╗\n" + 
        "║       ║       ║       ║       ║       ║       ║\n" +
        "║  %2d   ║  %2d   ║  %2d   ║  %2d   ║  %2d   ║  %2d   ║\n" +
        "║       ║       ║       ║       ║       ║       ║\n" +
        "╠·······╬·······╬·······╬·······╬·······╬·······╣\n" +
        "║       ║       ║       ║       ║       ║       ║\n" +
        "║  %2d   ║  %2d   ║  %2d   ║  %2d   ║  %2d   ║  %2d   ║\n" +
        "║       ║       ║       ║       ║       ║       ║\n" +
        "╚·······╩·······╩·······╩·······╩·······╩·······╝\n" +
        "└  2-1  ┴  2-2  ┴  2-3  ┴  2-4  ┴  2-5  ┴  2-6  ┘\n";
        System.out.printf(boardstatus, houses[0][0], houses[0][1], houses[0][2], houses[0][3], houses[0][4], houses[0][5],
        houses[1][0], houses[1][1], houses[1][2], houses[1][3], houses[1][4], houses[1][5]);
    }
    public void printHands(int player, int align)
    {
        String playerHands = "[玩家" + (player+1) + "]得分：" + hands[player];
        //當align=1時，即為靠右對齊，format使用%44s是由於棋盤有49個字符
        //而得分字串扣掉5個雙字元寬字符(玩、家、得、分、：)，即為49-5=44
        //並且由於box-drawing字元在不同情況上的寬度不確定，再多使用六個box-drawing字元排版，消除影響
        if(align == 1)
            System.out.printf("%44s%n", ("────── " + playerHands));
        //當align為其他值，預設即為靠左對齊
        else
            System.out.println(playerHands + " ──────");
    }
    public void move(int playside, int num) throws InvalidMoveException
    {
        //檢查是否是移動自己的棋洞
        if(currentplay != playside)
            throw new InvalidMoveException("只能移動自己棋洞中的棋子，請重新輸入指令");
        //檢查欲移動的棋洞裡有沒有棋子
        if(houses[playside][num] == 0)
            throw new InvalidMoveException("選擇的棋洞中沒有棋子，請重新輸入指令");
        int seeds = houses[playside][num];
        //複製另一個棋洞陣列，以方便做特殊規則檢查
        int housescheck[][] = new int[houses.length][];
        for(int i=0; i < houses.length; i++)
            housescheck[i] = Arrays.copyOf(houses[i], houses[i].length);
        housescheck[playside][num] = 0;
        //初始化迭代器之數值
        sideiter = playside;
        numiter = num;
        for(;seeds > 0; seeds--)
        {
            housesIterCCW();
            if(sideiter == playside && numiter == num)
                housesIterCCW();
            housescheck[sideiter][numiter]++;
        }
        //檢查是否遵守特殊規則
        //設定一變數代表對手
        int opponent = (currentplay==0)?1:0;
        //記錄放置最後一子的棋洞
        int endside = sideiter, endnum = numiter;
        //宣告三個檢查值，檢查對方棋盤被拿走數、對方棋盤空棋洞數、玩家可選擇數以及玩家不使對方棋洞不被放棋之選擇數
        int beempty = 0, betaken = 0, playerchoices = 0, validnotbeemptychoices = 0;
        //計算對方被取走的棋洞數
        while(sideiter != playside)
        {
            if(housescheck[sideiter][numiter] == 2 || housescheck[sideiter][numiter] == 3)
                betaken++;
            housesIterCW();
        }
        for(int index=0; index < 6; index++)
        {
            //計算對方的空棋洞數
            if(housescheck[opponent][index] == 0)
                beempty++;
            //計算玩家原來可選擇的棋洞數，因此使用houses而非housescheck
            if(houses[playside][index] != 0)
            {
                playerchoices++;
                //檢查可使對方棋洞不為空的選擇數，採取該棋洞編號與內部棋子數之和是否會超過陣列索引值為判定依據
                if(playside == 0)
                {
                    if(index - houses[playside][index] < 0)
                        validnotbeemptychoices++;
                }
                else
                {
                    if(index + houses[playside][index] > 5)
                        validnotbeemptychoices++;
                }
            }
        }
        //若全部皆為空棋洞，則對方棋盤是否全空為真
        if(beempty == 6)
        {
            //檢查是否有選擇可使對方棋洞不為空
            if(validnotbeemptychoices > 0)
                throw new InvalidMoveException("除非沒有其他選擇，否則必須選擇能讓對手棋盤任一棋洞至少擁有一顆棋子的棋步，請重新輸入指令");
        }
        //若不是全部皆為空棋洞，但空棋洞數目加上被拿走棋洞數目為6，則對方棋盤是否被全拿走為真
        else if(beempty+betaken == 6)
        {
            //檢查是否有其他選擇可不拿走對方棋盤裡全部的棋子
            if(playerchoices > 1)
                throw new InvalidMoveException("除非只能選擇這個棋洞，否則不可一次獲取對方棋盤全部的棋子，請重新輸入指令");
        }
        //若通過特殊規則檢查，則將複製的棋洞物件參照回傳給原本的棋洞
        houses = housescheck;
        //計算可以加進得分區的棋子數並加入該玩家得分區
        addToHands(playside, endside, endnum);
        setCurrentPlayer(opponent);
    }
    public void addToHands(int player, int endside, int endnum)
    {
        if(player == endside)
            return;
        sideiter = endside;
        numiter = endnum;
        while(player != sideiter && (houses[sideiter][numiter] == 2 || houses[sideiter][numiter] == 3))
        {
            hands[player] += houses[sideiter][numiter];
            houses[sideiter][numiter] = 0;
            housesIterCW();
        }
    }
    public void housesToHands()
    {
        for(int i=0; i < houses.length; i++)
        {
            for(int j=0; j < houses[i].length; j++)
                hands[i] += houses[i][j];
        }
    }
    //檢查是否自動結束遊戲的函數，有一參數moved標記此回合玩家是否已移動過，並且在確定遊戲結束時會算出贏家
    public boolean checkOver(int player, boolean moved)
    {
        //若未移動過，檢查棋洞是否有棋，若都沒有棋子則回傳true
        if(!moved)
        {
            int beempty = 0;
            for(int index=0; index < 6; index++)
            {
                if(houses[player][index] == 0)
                    beempty++;
            }
            if(beempty == 6)
            {
                housesToHands();
                calcWinner();
                return true;
            }
        }
        //若已移動過，檢查玩家得分區的棋子是否過半，若是則回傳true
        else if(hands[player] > 24)
        {
            housesToHands();
            calcWinner();
            return true;
        }
        //若以上皆非，則為false
        return false;
    }
    //以比較得分區棋子數多寡的方式找出贏家，當平手時，贏家紀錄為-1
    public void calcWinner()
    {
        if(hands[0] > hands[1])
            winner = 0;
        else if(hands[1] > hands[0])
            winner = 1;
        else
            winner = -1;
    }
    public void setCurrentPlayer(int player)
    {
        currentplay = player;
    }
    public String getWinnerName()
    {
        if(winner != -1)
            return "[玩家" + (winner+1) +"]";
        else
            return "從缺，雙方平手";
    }
    public int getHouses(int playside, int num)
    {
        return houses[playside][num];
    }
    public int getHands(int player)
    {
        return hands[player];
    }
    public int getCurrentPlayer()
    {
        return currentplay;
    }
}
