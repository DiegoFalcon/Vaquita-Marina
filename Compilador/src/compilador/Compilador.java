package compilador;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Compilador {

    static String _fileName;
    static byte[] _sc;
    public static void main(String[] args) throws IOException {    
        GetFileName();
        Tokenizer();
        
    }
    
    public static Token Tokenizer() throws IOException  {
        String token;
        int currentTokenCode;
        String cuurentTokenDescription;
        String cuurentTokenInfo;
        
        token = ReadTokenFromFile();
        currentTokenCode = GetTokenCode(token);
        cuurentTokenDescription = "";
        cuurentTokenInfo = "";
        
        Token tokenTemp = new Token();
        tokenTemp.code = currentTokenCode;
        tokenTemp.description = cuurentTokenDescription;
        tokenTemp.info = cuurentTokenInfo;
        
        return tokenTemp;
    }
    public static String ReadTokenFromFile(){
        //Rebeca y Yisus
        return "";
    }
    public static int GetTokenCode(String token){
        //Gera / Itzel
        return -1;
    }
    
    public static void GetFileName(){
        //OPEN FILE DIALOG
        Frame f=new Frame();
        boolean error=false;
        FileDialog fd = new FileDialog(f, "Choose a file", FileDialog.LOAD);       
        fd.setDirectory("C:\\");
        fd.setFile("*.txt");
        fd.setVisible(true);
        _fileName="";
        
         try
        {
         _fileName = fd.getFile();
        if (_fileName == null)
        {
            System.out.println("You cancelled the choice");
            error=true;
        }
        else
        {
            //System.out.println("You chose " + _fileName);
            
        }
        }catch(Exception e){System.out.println(e.getMessage());error=true;}
        if(error)
            System.exit(0);
        f.dispose();
        
    }    
    
}
