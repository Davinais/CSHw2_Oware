import java.util.Arrays;

public class Board
{
    int hands[] = {0, 0};
    int houses[][] = {{4, 4, 4, 4, 4, 4},{4, 4, 4, 4, 4, 4}};
    int winner = -1;
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
    public void move(int playside, int num) throws InvalidMoveException
    {
        if(houses[playside][num] == 0)
            throw new InvalidMoveException("選擇的棋洞中沒有棋子，請重新輸入指令");
        int seeds = houses[playside][num];
        //複製另一陣列，以方便做特殊規則檢查
        int housescheck[][] = Arrays.copyOf(houses, houses.length);
        boolean besowed[][] = {{false, false, false, false, false, false},{false, false, false, false, false, false}};
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
            besowed[sideiter][numiter] = true;
        }
        //檢查是否遵守特殊規則
        int opponent = (playside==0)?1:0;
        //宣告三個檢查值，檢查對方棋盤是否被拿走數、對方棋盤空棋洞數以及玩家其他選擇數
        int beempty = 0, betaken = 0, otherchoice = 0;
        for(int index=0; index < 6; index++)
        {
            //若對手的棋洞有被播種，檢查是否可能會被拿走
            if(besowed[opponent][index])
            {
                if(housescheck[opponent][index] == 2 || housescheck[opponent][index] == 3)
                    betaken++;
            }
            //否則，檢查對手的棋洞是否為空棋洞
            else if(housescheck[opponent][index] == 0)
                beempty++;
            //檢查玩家的棋洞是否是空棋洞，若否，代表有其他選擇
            if(housescheck[playside][index] == 0)
                otherchoice++;
        }
        //若全部皆為空棋洞，則對方棋盤是否全空為真
        if(beempty == 6)
        {
            //檢查是否有其他選擇
            if(otherchoice != 0)
                throw new InvalidMoveException("除非只能選擇這個棋洞，否則必須選擇能讓對手棋盤任一棋洞至少擁有一顆棋子的棋步，請重新輸入指令");
        }
        //若不是全部皆為空棋洞，但空棋洞數目加上被拿走棋洞數目為6，則對方棋盤是否被全拿走為真
        else if(beempty+betaken == 6)
        {
            //檢查是否有其他選擇
            if(otherchoice != 0)
                throw new InvalidMoveException("除非只能選擇這個棋洞，否則不可一次獲取對方棋盤全部的棋子，請重新輸入指令");
        }
        //若通過特殊規則檢查，則將複製的棋洞物件參照回傳給原本的棋洞
        houses = housescheck;
        addToHands(playside, sideiter, numiter);
    }
    public void addToHands(int player, int endside, int endnum)
    {
        if(player == endside)
            return;
        //為保險而重賦值
        sideiter = endside;
        numiter = endnum;
        while(player != sideiter && houses[sideiter][numiter] == 2 || houses[sideiter][numiter] == 3)
        {
            hands[player] += houses[sideiter][numiter];
            houses[sideiter][numiter] = 0;
            housesIterCW();
        }
    }
    public boolean checkOver(int player, boolean moved)
    {
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
                winner = (player==0)?1:0;
                return true;
            }
        }
        else if(hands[player] > 24)
        {
            winner = player;
            return true;
        }
        return false;
    }
}
