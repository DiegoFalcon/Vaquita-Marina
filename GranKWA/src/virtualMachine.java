import java.awt.FileDialog;
import java.awt.Frame;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
//import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class VirtualMachine {
    //Contador de la linea actual (PC)  
    static int _currentLine;
    //Direccion de la variable dentro del segmento de datos
    static int _dir;
    //Arreglo de las instrucciones (Segmento de c�digo)
    static byte[] _sc;
    //Contador para variables que son vectores
    static int _index;
    //Segmento de datos con [nombre] - [valor] - [direccion]
    static byte[] _sd;
    //Pila
    static KwaStack _stack = new KwaStack();
    //Variable predefinida para comparar con valor NULL
    static char _nullValue='\u0000';
    static String _fileName;
    
    public static void mainVirtualMachine(String[] args) throws IOException {
        _currentLine = 0;
        _dir = 0;
        _index = 0;
        GetFileName();
        GetSC();
        GetSD();
        RunVirtualMachine();
    }
    public static void RunVirtualMachine(){
        while(_currentLine <= _sc.length && ByteToInstruction(_sc[_currentLine])!=0){
            //System.out.println(ByteToInstruction(_sc[_currentLine]));

            switch(ByteToInstruction(_sc[_currentLine])){
                case 1:
                    ReadI();
                    break;
                case 2:
                    ReadD();
                    break;
                case 3:
                    ReadF();
                    break;
                case 4:
                    ReadC();
                    break;
                case 5:
                    ReadS();
                    break;
                case 6:
                    ReadVI();
                    break;
                case 7:
                    ReadVD();
                    break;
                case 8:
                    ReadVF();
                    break;
                case 9:
                    ReadVC();
                    break;
                case 10:
                    ReadVS();
                    break;
                case 11:
                    WRTI();
                    break;
                case 12:
                    WRTD();
                    break;
                case 13:
                    WRTF();
                    break;
                case 14:
                    WRTC();
                    break;
                case 15:
                    WRTS();
                    break;
                case 16:
                    WRTM();
                    break;
                case 17:
                    WRTLN();
                    break;
                case 18:
                    WRTVI();
                    break;
                case 19:
                    WRTVD();
                    break;
                case 20:
                    WRTVC();
                    break;
                case 21:
                    WRTVF();
                    break;
                case 22:
                    WRTVS();
                    break;
                case 23:
                    SETINDEX();
                    break;
                case 24:
                    SETINDEXK();
                    break;
                case 25:
                    POPINDEX();
                    break;
                case 26:
                    PUSHI();
                    break;
                case 27:
                    PUSHD();
                    break;
                case 28:
                    PUSHC();
                    break;
                case 29:
                    PUSHF();
                    break;
                case 30:
                    PUSHS();
                    break;
                case 31:
                    PUSHKI();
                    break;
                case 32:
                    PUSHKF();
                    break;
                case 33:
                    PUSHKD();
                    break;
                case 34:
                    PUSHKC();
                    break;
                case 35:
                    PUSHKS();
                    break;
                case 36:
                    PUSHVI();
                    break;
                case 37:
                    PUSHVF();
                    break;
                case 38:
                    PUSHVD();
                    break;
                case 39:
                    PUSHVC();
                    break;
                case 40:
                    PUSHVS();
                    break;
                case 41:
                    POPI();
                    break;
                case 42:
                    POPD();
                    break;
                case 43:
                    POPC();
                    break;
                case 44:
                    POPF();
                    break;
                case 45:
                    POPS();
                    break;
                case 46:
                    POPVI();
                    break;
                case 47:
                    POPVD();
                    break;
                case 48:
                    POPVC();
                    break;
                case 49:
                    POPVF();
                    break;
                case 50:
                    POPVS();
                    break;
                case 51:
                    CMPEQ();
                    break;
                case 52:
                    CMPNE();
                    break;
                case 53:
                    CMPLT();
                    break;
                case 54:
                    CMPLE();
                    break;
                case 55:
                    CMPGT();
                    break;
                case 56:
                    CMPGE();
                    break;
                case 57:
                    JMP();
                    break;
                case 58:
                    JMPT();
                    break;
                case 59:
                    JMPF();
                    break;
                case 60:
                    ADD();
                    break;
                case 61:
                    SUB();
                    break;
                case 62:
                    MUL();
                    break;
                case 63:
                    DIV();
                    break;
                case 64:
                    MOD();
                    break;
                default:
                    //error
                    _currentLine = _sc.length + 23;
                    break;
            }
        }
    }
    public static void GetFileName(){
        //OPEN FILE DIALOG
        Frame f=new Frame();
        boolean error=false;
        FileDialog fd = new FileDialog(f, "Choose a file", FileDialog.LOAD);       
        fd.setDirectory("C:\\");
        fd.setFile("*.KWA");
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
            System.out.println("You chose " + _fileName);
            
        }
        }catch(Exception e){System.out.println(e.getMessage());error=true;}
        if(error)
            System.exit(0);
        f.dispose();
        
    }
    
    //Leer Segmento de C�digo
    public static void GetSC() throws IOException{
        byte[] bytesInFile=Files.readAllBytes(Paths.get(_fileName));
        byte[] segment=new byte[2];
        segment[0]=bytesInFile[10];
        segment[1]=bytesInFile[11];
        _sc=new byte[ByteArrayToSegment(segment)];
        
        for(int i=14;i<bytesInFile.length;i++){
            _sc[i-14]=bytesInFile[i];
        }
        
    }
    public static void GetSD() throws IOException{
        byte[] bytesInFile=Files.readAllBytes(Paths.get(_fileName));
        byte[] segment=new byte[2];
        segment[0]=bytesInFile[12];
        segment[1]=bytesInFile[13];
        _sd=new byte[ByteArrayToSegment(segment)];
    }
    
    //Metodos
    public static void WRTI(){
    int x=0;
        varPrint(x);
    }
    public static void WRTD(){
    int x=1;
        varPrint(x);
    }
    public static void WRTF(){
    int x=2;
        varPrint(x);
    }
    public static void WRTC(){
    int x=3;
        varPrint(x);
    }
    public static void WRTS(){
    int x=4;
        varPrint(x);
    }
    public static void varPrint(int x){
        _currentLine++;
        // 0 = int -- 1 = double -- 2 = float -- 3 = char -- 4 = string
        int esInt=0;
         double esDouble=0;
         char esChar='0';
         float esFloat=0f;
         String esString="0";
         
       /* double a=0;
        char b='0';
        float c=0f;
        String d="0";*/
        _dir=GetDir();
        switch (x)
        {
            case 0:
                System.out.print(GetVariableValue(_dir, esInt));
                break;
            case 1:
                System.out.print(GetVariableValue(_dir, esDouble));
                break;
            case 2:
                System.out.print(GetVariableValue(_dir, esFloat));
                break;
            case 3:
                System.out.print(GetVariableValue(_dir, esChar));
                break;
            case 4:
                System.out.print(GetVariableValue(_dir, esString));
                break;
        }
        _currentLine += 2;
    }
    public static void WRTM(){
        String x="0";
        _currentLine++;
        x=GetConstantValue(x);
        System.out.print(x);
        _currentLine += x.length()+1;
    }
    public static void WRTLN(){
        _currentLine++;
        System.out.println("");
    }
    public static void WRTVI(){
        _currentLine++;
        int x=0;
        _dir = GetDir();
        System.out.print(GetVariableValue(_dir+_index*4, x));
        _currentLine += 2;
    }
    public static void WRTVD(){
        _currentLine++;
        double x=0;
        _dir = GetDir();
        System.out.print(GetVariableValue(_dir+_index*8,x));
        _currentLine += 2;
    }
    public static void WRTVF(){
        _currentLine++;
        float x=0f;
        _dir = GetDir();
        System.out.print(GetVariableValue(_dir+_index*4, x));
        _currentLine +=2 ;
    }
    public static void WRTVC(){
        _currentLine++;
        char x='0';
        _dir = GetDir();
        System.out.print(GetVariableValue(_dir+_index, x));
        _currentLine+=2;
    }
    public static void WRTVS(){
        String StringValue="0";
        _currentLine++;
        _dir = GetDir();
        StringValue=GetVariableValue(_dir+(255*_index),StringValue);
        System.out.print(StringValue);
        _currentLine += 2;
    }
    public static void ReadI() {
        Scanner scan=new Scanner(System.in);
        _currentLine++;
        int newValue = 0;
        try{
            newValue = Integer.parseInt(scan.nextLine());
            _dir=GetDir();
            }
        catch(Exception e){
                System.out.println(e.getMessage());
        }
        SetVariableValue(_dir,newValue);
        _currentLine+=2;
    }
    public static void ReadD() {
         Scanner scan=new Scanner(System.in);
         _currentLine++;
         double newValue = 0;
         try{
            newValue = Double.parseDouble(scan.nextLine());
             _dir=GetDir();
             }
         catch(Exception e){
                 System.out.println(e.getMessage());
         }
         SetVariableValue(_dir,newValue);
         _currentLine+=2;
    }
    public static void ReadF(){
         Scanner scan=new Scanner(System.in);
         _currentLine++;
         float newValue = 0;
         try{
             newValue = Float.parseFloat(scan.nextLine());
             _dir=GetDir();
             }
         catch(Exception e){
                 System.out.println(e.getMessage());
         }
         SetVariableValue(_dir,newValue);
         _currentLine+=2;
    }
    public static void ReadC() {
         Scanner scan=new Scanner(System.in);
         _currentLine++;
         char newValue = ' ';
         try{
             newValue = (scan.nextLine()).charAt(0);
             _dir=GetDir();
             }
         catch(Exception e){
                 System.out.println(e.getMessage());
         }
         SetVariableValue(_dir,newValue);
         _currentLine+=2;
    }
    public static void ReadS(){
         Scanner scan=new Scanner(System.in);
         _currentLine++;
         String newValue = "";
         try{
             newValue = scan.nextLine();
             _dir=GetDir();
             }
         catch(Exception e){
                 System.out.println(e.getMessage());
         }
         SetVariableValue(_dir,newValue);
         _currentLine+=2;
    }
    public static void ReadVI(){
        Scanner scan=new Scanner(System.in);
        int newValue = 0;
        _currentLine++;
        try{
             newValue = Integer.parseInt(scan.nextLine());
             _dir=GetDir();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        SetVariableValue(_dir+_index*4,newValue);
        _currentLine+=2;
    }
    public static void ReadVD(){
         Scanner scan=new Scanner(System.in);
         double newValue = 0;
         _currentLine++;
         try{
             newValue = Double.parseDouble(scan.nextLine());
             _dir=GetDir();
         }
         catch(Exception e){
             System.out.println(e.getMessage());
         }
         SetVariableValue(_dir+_index*8,newValue);
         _currentLine+=2;
    }
    public static void ReadVF(){
         Scanner scan=new Scanner(System.in);
         float newValue = 0;
         _currentLine++;
         try{
             newValue = Float.parseFloat(scan.nextLine());
             _dir=GetDir();
         }
         catch(Exception e){
             System.out.println(e.getMessage());
         }
         SetVariableValue(_dir+_index*4,newValue);
         _currentLine+=2;
    }
    public static void ReadVC(){
         Scanner scan=new Scanner(System.in);
         char newValue = ' ';
         _currentLine++;
         try{
             newValue = scan.nextLine().charAt(0);
             _dir=GetDir();
         }
         catch(Exception e){
             System.out.println(e.getMessage());
         }
         SetVariableValue(_dir+_index*1,newValue);
         _currentLine+=2;
    }
    public static void ReadVS(){
         Scanner scan=new Scanner(System.in);
         String newValue = "";
         _currentLine++;
         try{
             newValue = scan.nextLine();
             _dir=GetDir();
         }
         catch(Exception e){
             System.out.println(e.getMessage());
         }
         SetVariableValue(_dir+_index*255,newValue);
         _currentLine+=2;
    }
    
    public static void SETINDEX(){
        _currentLine++;
        int x=0;
        _dir=GetDir();
        _index=GetVariableValue(_dir,x);
        _currentLine+=2;
    }
    public static void SETINDEXK(){
        _currentLine++;
        int x=0;
        _index=GetConstantValue(x);
        _currentLine+=4;
    }
    public static void POPINDEX(){
        try{
            _index = _stack.POPI();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        _currentLine ++;
    }
    public static void JMP(){
        _currentLine++;
        _currentLine = GetDir();
    }
    public static void JMPF(){
        int flag;
        flag=_stack.POPI();
        _currentLine++;
        
        if(flag==0){
            _currentLine = GetDir();
        }
        else
            _currentLine+=2;
    }
    public static void JMPT(){
        int flag;
        flag=_stack.POPI();
        _currentLine++;

        if(flag==1){
            _currentLine = GetDir();
        }
        else
            _currentLine+=2;
    }
    public static void CMPEQ(){
        int type=0;
        type=_stack.getType();
        //Compara segun el tipo de dato y regresa un 1 si es verdadero y un cero si es falso
        switch (type)
        {
            case 0:
                if(_stack.POPI()==_stack.POPI())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 1:
                if(_stack.POPF()==_stack.POPF())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 2:
                if(_stack.POPD()==_stack.POPD())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 3:
                if(_stack.POPC()==_stack.POPC())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 4:
                if((_stack.POPS()).equals(_stack.POPS()))
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
        }
        _currentLine ++;
    }
    public static void CMPNE(){
       int type=0;
        type=_stack.getType();
        //Compara segun el tipo de dato y regresa un 1 si es verdadero y un cero si es falso
        switch (type)
        {
            case 0:
                if(_stack.POPI()!=_stack.POPI())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 1:
                if(_stack.POPF()!=_stack.POPF())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 2:
                if(_stack.POPD()!=_stack.POPD())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 3:
                if(_stack.POPC()!=_stack.POPC())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 4:
                if(!(_stack.POPS()).equals(_stack.POPS()))
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
        }
        _currentLine ++;
    }
    public static void CMPLT(){
        int type=0;
        type=_stack.getType();
        //Compara segun el tipo de dato y regresa un 1 si es verdadero y un cero si es falso
        switch (type)
        {
            case 0:
                if(_stack.POPI()>_stack.POPI())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 1:
                if(_stack.POPF()>_stack.POPF())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 2:
                if(_stack.POPD()>_stack.POPD())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 3:
                if(_stack.POPC()>_stack.POPC())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 4:
                if(0>((_stack.POPS().compareTo(_stack.POPS()))))
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
        }
        _currentLine ++;
    }
    public static void CMPLE(){
       int type=0;
        type=_stack.getType();
        //Compara segun el tipo de dato y regresa un 1 si es verdadero y un cero si es falso
        switch (type)
        {
            case 0:
                if(_stack.POPI() >= _stack.POPI())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 1:
                if(_stack.POPF()>=_stack.POPF())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 2:
                if(_stack.POPD()>=_stack.POPD())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 3:
                if(_stack.POPC()>=_stack.POPC())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 4:
                if(0>=((_stack.POPS().compareTo(_stack.POPS()))))
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
        }
        _currentLine ++;
    }
    public static void CMPGT(){
        int type=0, num1, num2;
        type=_stack.getType();
        //Compara segun el tipo de dato y regresa un 1 si es verdadero y un cero si es falso
        switch (type)
        {
            case 0:
                num2=_stack.POPI();
                num1=_stack.POPI();
                
                if(num1>num2)
                //if(_stack.POPI()<_stack.POPI())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 1:
                if(_stack.POPF()<_stack.POPF())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 2:
                if(_stack.POPD()<_stack.POPD())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 3:
                if(_stack.POPC()<_stack.POPC())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 4:
                if(0<((_stack.POPS().compareTo(_stack.POPS()))))
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
        }
        _currentLine ++;
    }
    public static void CMPGE(){
         int type=0;
        type=_stack.getType();
        //Compara segun el tipo de dato y regresa un 1 si es verdadero y un cero si es falso
        switch (type)
        {
            case 0:
                if(_stack.POPI()<=_stack.POPI())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 1:
                if(_stack.POPF()<=_stack.POPF())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 2:
                if(_stack.POPD()<=_stack.POPD())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 3:
                if(_stack.POPC()<=_stack.POPC())
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
                break;
            case 4:
                if(0<=((_stack.POPS().compareTo(_stack.POPS()))))
                    _stack.PUSHI(1);
                else
                    _stack.PUSHI(0);
        }
        _currentLine ++;
    }
    
    public static void PUSHI(){
        int valueInt=0;
        _currentLine++;
        _dir = GetDir();
        valueInt = GetVariableValue(_dir, valueInt);
        _stack.PUSHI(valueInt);
        _currentLine = _currentLine + 2;
    }
    public static void PUSHD(){
        Double valueDouble=0.0;
        _currentLine++;
        _dir = GetDir();
        valueDouble = GetVariableValue(_dir, valueDouble);
        _stack.PUSHD(valueDouble);
        _currentLine = _currentLine + 2;
    }
    public static void PUSHC(){
        char valueChar=' ';
        _currentLine++;
        _dir = GetDir();
        valueChar = GetVariableValue(_dir, valueChar);       
        _stack.PUSHC(valueChar);
        _currentLine = _currentLine + 2;
    }
    public static void PUSHF(){
        float valueFloat=0.0f;
        _currentLine++;
        _dir = GetDir();
        valueFloat = GetVariableValue(_dir, valueFloat);       
        _stack.PUSHF(valueFloat);
        _currentLine = _currentLine + 2;
    }
    public static void PUSHS(){
       String valueString = "";
        _currentLine++;
        _dir = GetDir();
        valueString =GetVariableValue(_dir, valueString);       
        _stack.PUSHS(valueString);
        _currentLine = _currentLine + 2;
    }
    public static void PUSHKI(){
        //Cambiar la logica de las constantes
        //Tomar el valor directamente del vector _sc
        int valueKInt=0;
        _currentLine++;
        valueKInt = GetConstantValue(valueKInt);
        _stack.PUSHI(valueKInt);
        _currentLine = _currentLine + 4;
    }
    public static void PUSHKF(){
        float valueFloat=0.0f;
        _currentLine++;
        valueFloat = GetConstantValue(valueFloat);
        _stack.PUSHF(valueFloat);
        _currentLine = _currentLine + 4;
    }
    public static void PUSHKD(){
        double valueDouble=0;
        _currentLine++;
        valueDouble = GetConstantValue(valueDouble);
        _stack.PUSHD(valueDouble);
        _currentLine = _currentLine + 8;
    }
    public static void PUSHKC(){
        char valueChar = '0';
        _currentLine++;
        valueChar = GetConstantValue(valueChar);
        _stack.PUSHC(valueChar);
        _currentLine = _currentLine + 1;
    }
    public static void PUSHKS(){
        String valueString = "0";
        _currentLine++;
        valueString = GetConstantValue(valueString);
        _stack.PUSHS(valueString);
        _currentLine = _currentLine + (valueString.length()+1);
    }   
    public static void PUSHVI(){
        int valueInt = 0;
        _currentLine++;
        valueInt = GetVariableValue(GetDir()+_index*4,valueInt);
        _stack.PUSHI(valueInt);
        _currentLine += 2;
    }
    public static void PUSHVF(){
        float valueFloat = 0;
        _currentLine++;
        valueFloat = GetVariableValue(GetDir()+_index*4,valueFloat);
        _stack.PUSHF(valueFloat);
        _currentLine += 2;
    }
    public static void PUSHVD(){        
        double valueDouble = 0;
        _currentLine++;
        valueDouble = GetVariableValue(GetDir()+_index*8,valueDouble);
        _stack.PUSHD(valueDouble);
        _currentLine += 2;
    }
    public static void PUSHVC(){
        char valueChar = ' ';
        _currentLine++;
        valueChar = GetVariableValue(GetDir()+_index,valueChar);
        _stack.PUSHC(valueChar);
        _currentLine += 2;
    }
    public static void PUSHVS(){
        String valueString = "";
        _currentLine++;
        valueString = GetVariableValue(GetDir()+_index*255,valueString);
        _stack.PUSHS(valueString);
        _currentLine += 2;
    }
    public static void POPI(){        
        int poppedVariable=0;
        try{
            poppedVariable = _stack.POPI();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        _currentLine ++;
        _dir = GetDir();
        SetVariableValue(_dir,poppedVariable);
        _currentLine += 2; 
    }
    public static void POPF(){ 
        float poppedVariable=0.0f;
        try{
            poppedVariable = _stack.POPF();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        _currentLine ++;
        _dir = GetDir();
        SetVariableValue(_dir,poppedVariable);
        _currentLine += 2; 
    }
    public static void POPD(){ 
        double poppedVariable=0.0;
        try{
            poppedVariable = _stack.POPD();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        _currentLine ++;
        _dir = GetDir();
        SetVariableValue(_dir,poppedVariable);
        _currentLine += 2;
    }
    public static void POPC(){ 
        char poppedVariable=' ';
        try{
            poppedVariable = (char)_stack.POPC();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        _currentLine ++;
        _dir = GetDir();
        SetVariableValue(_dir,poppedVariable);
        _currentLine += 2; 
    }
    public static void POPS(){ 
        String poppedVariable="";
        try{
            poppedVariable = String.valueOf((_stack.POPS()));
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        _currentLine ++;
        _dir = GetDir();
        SetVariableValue(_dir,poppedVariable);
        _currentLine += 2; 
    }
    public static void POPVI(){
        int poppedVariable = 0;
        try{
            poppedVariable = _stack.POPI();
            _currentLine++;
            SetVariableValue(GetDir()+_index*4,poppedVariable);
            _currentLine += 2;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
      }
    public static void POPVF(){ 
        float poppedVariable = 0.0f;
        try{
            poppedVariable = _stack.POPF();
            _currentLine ++;
            SetVariableValue(GetDir()+_index*4,poppedVariable);
            _currentLine += 2;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    public static void POPVD(){ 
        double poppedVariable = 0.0;
        try{
            poppedVariable = _stack.POPD();
            _currentLine ++;
            SetVariableValue(GetDir()+_index*8,poppedVariable);
            _currentLine += 2;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }     
    }
    public static void POPVC(){ 
        char poppedVariable = ' ';
        try{
            poppedVariable = _stack.POPC();
            _currentLine ++;
            SetVariableValue(GetDir()+_index, poppedVariable);
            _currentLine += 2;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    public static void POPVS(){ 
        String poppedVariable="";
        try{
            poppedVariable = _stack.POPS();
            _currentLine ++;
            SetVariableValue(GetDir()+_index*255, poppedVariable);
            _currentLine += 2;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    public static void ADD(){
        double var1, var2=0, result;
        String varString1, varString2, resultString;
        
        //Entero
        if (_stack.getType() == 0){
            var1 = _stack.POPD();
            varString1 = "";
        }
        else{
        //float
            if (_stack.getType() == 1){
                var1 = _stack.POPD();
                varString1 = "";
            }
            else{
                //double
                if (_stack.getType() == 2){
                    var1 = _stack.POPD();
                    varString1 = "";
                }
                else{
                    //String
                    if (_stack.getType() == 4){
                        varString1 = _stack.POPS();
                        var1 = 0;
                    }
                    else{
                        var1 = 0;
                        varString1 = "";
                    }
                }
            }
        }

        //Entero
        if (_stack.getType() == 0){
            var2 = _stack.POPD();
            var2 = var1 + var2;
            result = var2;
            _stack.PUSHI((int)result);
            resultString = "";
        }
        else{
        //float
            if (_stack.getType() == 1){
                var2 = _stack.POPD();
                var2 = var1 + var2;
                result = var2;
                _stack.PUSHF((float)result);
                resultString = "";
            }
            else{
                //double
                if (_stack.getType() == 2){
                    var2 = _stack.POPD();
                    var2 = var1 + var2;
                    result = var2;
                    _stack.PUSHD(result);
                    resultString = "";
                }
                else{
                    //String
                    if (_stack.getType() == 4){
                        varString2 = _stack.POPS();
                        varString2 = varString2 + varString1;
                        resultString = varString2;
                        _stack.PUSHS(resultString);
                        result = 0;
                    }
                    else{ 
                        result = 0;
                        resultString = "";
                    }
                }
            }
        }
        _currentLine ++;
    }
    public static void SUB(){
        double var1, var2, result;
        
        //Entero
        if (_stack.getType() == 0){
            var1 = _stack.POPD();
        }
        else{
        //float
            if (_stack.getType() == 1){
                var1 = _stack.POPD();
            }
            else{
                //double
                if (_stack.getType() == 2){
                    var1 = _stack.POPD();
                }
                else
                    var1 = 0;
                
            }
        }

        //Entero
        if (_stack.getType() == 0){
            var2 = _stack.POPD();
            var2 = var2 - var1;
            result = var2;
            _stack.PUSHI((int)result);
        }
        else{
        //float
            if (_stack.getType() == 1){
                var2 = _stack.POPD();
                var2 = var2 - var1;
                result = var2;
                _stack.PUSHF((float)result);
            }
            else{
                //double
                if (_stack.getType() == 2){
                    var2 = _stack.POPD();
                    var2 = var2 - var1;
                    result = var2;
                    _stack.PUSHD(result);
                }
                else{
                    result = 0;
                }
            }
        }
        _currentLine ++;
    }
    public static void MUL(){
        double var1, var2, result;
        
        //Entero
        if (_stack.getType() == 0){
            var1 = _stack.POPD();
        }
        else{
        //float
            if (_stack.getType() == 1){
                var1 = _stack.POPD();
            }
            else{
                //double
                if (_stack.getType() == 2){
                    var1 = _stack.POPD();
                }
                else
                    var1 = 0;
            }
        }

        //Entero
        if (_stack.getType() == 0){
            var2 = _stack.POPD();
            var2 = var1 * var2;
            result = var2;
            _stack.PUSHI((int)result);
        }
        else{
        //float
            if (_stack.getType() == 1){
                var2 = _stack.POPD();
                var2 = var1 * var2;
                result = var2;
                _stack.PUSHF((float)result);
            }
            else{
                //double
                if (_stack.getType() == 2){
                    var2 = _stack.POPD();
                    var2 = var1 * var2;
                    result = var2;
                    _stack.PUSHD(result);
                }
                else
                    result = 0;
            }
        }
        _currentLine ++;
    }
    public static void DIV(){
        double var1, var2, result;
        
        //Entero
        if (_stack.getType() == 0){
            var1 = _stack.POPD();
        }
        else{
        //float
            if (_stack.getType() == 1){
                var1 = _stack.POPD();
            }
            else{
                //double
                if (_stack.getType() == 2){
                    var1 = _stack.POPD();
                }
                else
                    var1 = 0;
                
            }
        }

        //Entero
        if (_stack.getType() == 0){
            var2 = _stack.POPD();
            if(var1 != 0){
                var2 = var2 / var1;
                result = var2;
                _stack.PUSHI((int)result);
            }
            else
                System.out.println("Can't divide by 0");
        }
        else{
        //float
            if (_stack.getType() == 1){
                var2 = _stack.POPD();
                if(var1 != 0){
                    var2 = var2 / var1;
                    result = var2;
                    _stack.PUSHF((float)result);
                }
                else
                    System.out.println("Can't divide by 0");
            }
            else{
                //double
                if (_stack.getType() == 2){
                    var2 = _stack.POPD();
                    if(var1 != 0){
                        var2 = var2 / var1;
                        result = var2;
                        _stack.PUSHD(result);
                    }
                    else
                        System.out.println("Can't divide by 0");
                }
                else{
                    result = 0;
                }
            }
        }
        _currentLine ++;
    }
    public static void MOD(){
        double var1, var2, result;
        
        //Entero
        if (_stack.getType() == 0){
            var1 = _stack.POPD();
        }
        else{
        //float
            if (_stack.getType() == 1){
                var1 = _stack.POPD();
            }
            else{
                //double
                if (_stack.getType() == 2){
                    var1 = _stack.POPD();
                }
                else
                    var1 = 0;
                
            }
        }

        //Entero
        if (_stack.getType() == 0){
            var2 = _stack.POPD();
            if(var1 != 0){
                var2 = var2 % var1;
                result = var2;
                _stack.PUSHI((int)result);
            }
            else
                System.out.println("Can't divide by 0");
        }
        else{
        //float
            if (_stack.getType() == 1){
                var2 = _stack.POPD();
                if(var1 != 0){
                    var2 = var2 % var1;
                    result = var2;
                    _stack.PUSHF((float)result);
                }
                else
                    System.out.println("Can't divide by 0");
            }
            else{
                //double
                if (_stack.getType() == 2){
                    var2 = _stack.POPD();
                    if(var1 != 0){
                        var2 = var2 % var1;
                        result = var2;
                        _stack.PUSHD(result);
                    }
                    else
                        System.out.println("Can't divide by 0");
                }
                else{
                    result = 0;
                }
            }
        }
        _currentLine ++;
    }
    
    //******BYTE CONVERSIONS*********
    public static int getVariableTypeCode(String instruction){
        switch(instruction.charAt(instruction.length()-1)){
            case 'I':
            case 'K':
                return 0;       
            case 'F':
                return 1;       
            case 'D':
                return 2;       
            case 'S':
            case 'M':
                return 3;       
            case 'C':
                return 4;
            default:
                return -1;
        }
    }
    public static int getInstructionVariableSize(int instructionCode){
        switch(instructionCode){
            //READs
            case 1:case 2:case 3:case 4:case 5:case 6:case 7:case 8:case 9:case 10:
            //WRTs
            case 11:case 12:case 13:case 14:case 15:
            //WRTVs
            case 18:case 19:case 20:case 21:case 22:
            //SETINDEX & POPINDEX
            case 23:case 25:case 26:case 27:case 28:case 29:case 30:
            //PUSHVs
            case 36:case 37:case 38:case 39:case 40:
            //POPs
            case 41:case 42:case 43:case 44:case 45:case 46:case 47:case 48:case 49:
            //JMPs
            case 50:case 57:case 58:case 59:
                return 2;
            //SETINDEXK PUSHKI PUSHKF
            case 24:case 31:case 32:
                return 4;
            //PUSHKD
            case 33:
                return 8;
            //PUSHKC
            case 34:
                return 1;   
            //constant string
            case 16:case 35:
                return -1;
            //CMP & Arithmetic
            default:
                return 0;
        }
    }
    public static int ByteToStringLength(byte byteStringLength){
        return byteStringLength & 0xff;
    }
    public static int ByteToInstruction(byte byteInstruction){
        return byteInstruction & 0xff;
    }
    public static int ByteArrayToInt(byte[] byteArray) {
        return ByteBuffer.wrap(byteArray).getInt();
    }
    public static float ByteArrayToFloat(byte[] byteArray){
        return ByteBuffer.wrap(byteArray).getFloat();
    }
    public static double ByteArrayToDouble(byte[] byteArray) {
        return ByteBuffer.wrap(byteArray).getDouble();
    }
    public static char ByteToChar(byte byteArray) {
        return (char)(byteArray & 0xff);
    }
    public static int ByteArrayToSegment(byte[] segment){
        return ((segment[0] & 0xff) << 8) | (segment[1] & 0xff);
    }
    public static int ByteArrayToDir(byte[] dir){
        return ((dir[0] & 0xff) << 8) | (dir[1] & 0xff);
    }
    
    public static byte StringLengthToByte(int stringLengthToConvert){
        return (byte)stringLengthToConvert;
    }
    public static byte InstructionToByte(int instructionToConvert){
        return (byte)instructionToConvert;
    }
    public static byte[] IntToByteArray(int numberToConvert){
       return ByteBuffer.allocate(4).putInt(numberToConvert).array();
    }
    public static byte[] FloatToByteArray(float numberToConvert){
       return ByteBuffer.allocate(4).putFloat(numberToConvert).array();
    } 
    public static byte[] DoubleToByteArray(double numberToConvert){
       return ByteBuffer.allocate(8).putDouble(numberToConvert).array();
    }
    public static byte CharToByte(char charToConvert){
        return (byte)charToConvert;
    }
    public static byte[] SegmentsToByteArray(int segmentSize){
        byte[] segmentIn2Bytes=new byte[2];
        segmentIn2Bytes[1]=(byte)(segmentSize & 0xFF);
        segmentIn2Bytes[0]=(byte)((segmentSize>>8) & 0xFF);
        return segmentIn2Bytes;
    }
    public static byte[] DirToByteArray(int dir){
        byte[] segmentIn2Bytes=new byte[2];
        segmentIn2Bytes[1]=(byte)(dir & 0xFF);
        segmentIn2Bytes[0]=(byte)((dir>>8) & 0xFF);
        return segmentIn2Bytes;
    }
    
    public static int GetVariableValue(int dir, int dummyValue){
        byte[] intToConvert=new byte[4];
        intToConvert[0]=_sd[dir];
        intToConvert[1]=_sd[dir+1];
        intToConvert[2]=_sd[dir+2];
        intToConvert[3]=_sd[dir+3];
        return ByteArrayToInt(intToConvert);
    }
    public static float GetVariableValue(int dir, float dummyValue){
        byte[] floatToConvert=new byte[4];
        floatToConvert[0]=_sd[dir];
        floatToConvert[1]=_sd[dir+1];
        floatToConvert[2]=_sd[dir+2];
        floatToConvert[3]=_sd[dir+3];
        return ByteArrayToFloat(floatToConvert);
    }
    public static double GetVariableValue(int dir, double dummyValue){
        byte[] doubleToConvert=new byte[8];
        doubleToConvert[0]=_sd[dir];
        doubleToConvert[1]=_sd[dir+1];
        doubleToConvert[2]=_sd[dir+2];
        doubleToConvert[3]=_sd[dir+3];
        doubleToConvert[4]=_sd[dir+4];
        doubleToConvert[5]=_sd[dir+5];
        doubleToConvert[6]=_sd[dir+6];
        doubleToConvert[7]=_sd[dir+7];
        return ByteArrayToDouble(doubleToConvert);
    }
    public static char GetVariableValue(int dir, char dummyValue){
        return ByteToChar(_sd[dir]);
    }
    public static String GetVariableValue(int dir, String dummyValue){
        String stringToReturn="";
        //int stringLength=ByteToStringLength(_sc[_currentLine]);
        for(int i=0 ; ByteToChar(_sd[dir+i])!=_nullValue && i<255; i++){
            stringToReturn+=""+(ByteToChar(_sd[dir+i]));
        }
        return stringToReturn;
    }

    public static void SetVariableValue(int dir, int value){
        byte[] intToSet=IntToByteArray(value);
        for(int i=0;i<4;i++)
            _sd[dir+i]=intToSet[i];
    }
    public static void SetVariableValue(int dir, float value){
        byte[] floatToSet=FloatToByteArray(value);
        for(int i=0;i<4;i++)
            _sd[dir+i]=floatToSet[i];
    }
    public static void SetVariableValue(int dir, double value){
        byte[] doubleToSet=DoubleToByteArray(value);
        for(int i=0;i<8;i++)
            _sd[dir+i]=doubleToSet[i];
    }
    public static void SetVariableValue(int dir, char value){
        _sd[dir]=CharToByte(value);
    }
    public static void SetVariableValue(int dir, String value){
        int stringLength=value.length();
        for(int i=0 ; i<255 ; i++){
            if(i<stringLength)
                _sd[dir+i]=CharToByte(value.charAt(i));
            else
                _sd[dir+i]=CharToByte(_nullValue);
        }
    }

    public static int GetDir(){
        byte[] dirInByte = new byte[2];
        dirInByte[0]=_sc[_currentLine];
        dirInByte[1]=_sc[_currentLine+1];
        return ByteArrayToDir(dirInByte);
    }
    
    public static int GetConstantValue(int dummyValue){
        byte[] intToConvert=new byte[4];
        for(int i=0;i<4;i++)
            intToConvert[i]=_sc[_currentLine+i];
        return ByteArrayToInt(intToConvert);
    }
    public static float GetConstantValue(float dummyValue){
        byte[] floatToConvert=new byte[4];
        for(int i=0;i<4;i++)
            floatToConvert[i]=_sc[_currentLine+i];
        return ByteArrayToFloat(floatToConvert);
    }
    public static double GetConstantValue(double dummyValue){
        byte[] doubleToConvert=new byte[8];
        for(int i=0;i<8;i++)
            doubleToConvert[i]=_sc[_currentLine+i];
        return ByteArrayToDouble(doubleToConvert);
    }
    public static char GetConstantValue(char dummyValue){
        return ByteToChar(_sc[_currentLine]);
    } 
    public static String GetConstantValue(String dummyValue){
        String stringToReturn="";
        int stringLength=ByteToStringLength(_sc[_currentLine]);
        for(int i=1 ; i<(stringLength+1) ; i++){
            stringToReturn+=""+(ByteToChar(_sc[_currentLine+i]));
        }
        return stringToReturn;
    }
}
