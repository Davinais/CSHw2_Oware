import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class ConsoleIn
{
    public static String readLine()
    {
        BufferedReader inputObject = new BufferedReader(new InputStreamReader(System.in));
        String input = null;
        try
        {
            input = inputObject.readLine();
        }
        catch(IOException err)
        {
            System.out.println("在讀取輸入時發生問題，離開程式…");
            System.exit(0);
        }
        return input;
    }
}
