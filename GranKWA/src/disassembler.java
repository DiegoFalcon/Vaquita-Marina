//package disassembler;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class disassembler{
    private static String [] _KWA;
    private static String [][] _tags=new String[0][2];
    private static String [][] _variables=new String[0][4];
    static String[] _instructions=new String[75];
    static byte[] _bytesInFile;
    static String _fileName;
    
    public static void mainDisassembler(String[] args) throws Exception{
        // TODO code application logic here
    	pasarInstruKWAalVector();
    	 fill_KWA();
    	fillVariables();
    	fillTags();
    	TranslateToAssembly();
    }
    
    //FILL INSTRUCTIONS/VARIABLES/TAGS
    public static void fillVariables() throws FileNotFoundException{
        Scanner sc = new Scanner(new FileReader(_fileName.substring(0, _fileName.length()-3)+"vars"));
        for(int i=0;sc.hasNext();i++){
        	_variables=makeArrayBigger(_variables,4);
        	for(int c=0;c<4;c++){
        		_variables[i][c]=(""+sc.next());
        	}
        }
        sc.close();
    }
    public static void fillTags() throws FileNotFoundException{
    	Scanner sc = new Scanner(new FileReader(_fileName.substring(0, _fileName.length()-3)+"tags"));
        for(int i=0;sc.hasNext();i++){
        	_tags=makeArrayBigger(_tags,2);
        	for(int c=0;c<2;c++){
        		_tags[i][c]=(""+sc.next());
        	}
        }
        sc.close();
    }
    public static String[][] makeArrayBigger(String[][] array, int width){
    	String[][] arrayTmp=new String[array.length+1][width];
    	for(int i=0;i<array.length;i++){
    		arrayTmp[i]=array[i];
    	}
    	return arrayTmp;
    	
    }
    public static void pasarInstruKWAalVector()throws Exception{
        Scanner archivoInstrucciones= new Scanner(new FileReader("InstruccionesKWA.txt"));
        int index=0;
        while(archivoInstrucciones.hasNext()){
            _instructions[index]=archivoInstrucciones.next();
            index++;
        }
        archivoInstrucciones.close();
    }
    
    //FILL _KWA
    public static void fill_KWA() throws IOException{
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
        
    	_bytesInFile=Files.readAllBytes(Paths.get(_fileName));
    	byte[] segment=new byte[2];
    	byte[] dir=new byte[2];
    	byte[] intInBytes=new byte[4];
    	byte[] floatInBytes=new byte[4];
    	byte[] doubleInBytes=new byte[8];
    	int instructionLength;
    	int variableSize;
    	
    	segment[0]=_bytesInFile[10];
    	segment[1]=_bytesInFile[11];
    	_KWA=new String[(ByteArrayToSegment(segment))];

    	for(int i=0;i<_KWA.length;i++)
    		_KWA[i]="";
    	
    	segment[0]=_bytesInFile[12];
    	segment[1]=_bytesInFile[13];
    	
    	for(int index_bytesInFile=14,indexKWA=0;index_bytesInFile<_bytesInFile.length;index_bytesInFile++,indexKWA++){
    		instructionLength = getInstructionVariableSize(ByteToInstruction(_bytesInFile[index_bytesInFile]));
    		
    		_KWA[indexKWA]=(""+ByteToInstruction(_bytesInFile[index_bytesInFile]));
    		//System.out.println("["+indexKWA+"]="+_KWA[indexKWA]+"--"+"["+index_bytesInFile+"]="+_bytesInFile[index_bytesInFile]);
    		//WRTM PUSHKS
    		if(instructionLength==-1){
    			index_bytesInFile++;
    			indexKWA++;
    			variableSize = ByteToStringLength(_bytesInFile[index_bytesInFile]);
    			_KWA[indexKWA]=(""+variableSize+",");
    			// corregi este for para que concatene los caracteres variableSize+1 por la coma
    			 for(int c=1;c<=variableSize;c++){
    				_KWA[indexKWA]+=(""+ByteToChar(_bytesInFile[c+index_bytesInFile]));
    			}

    			 index_bytesInFile=index_bytesInFile+variableSize;
    			 indexKWA=indexKWA+variableSize;
    		}
    		
    		if(instructionLength==2){
    			index_bytesInFile++;
    			indexKWA++;
    			dir[0]=_bytesInFile[index_bytesInFile];index_bytesInFile++;
    			dir[1]=_bytesInFile[index_bytesInFile];
    			_KWA[indexKWA]=(""+ByteArrayToDir(dir));
    			indexKWA++;
    		}
    		
    		if(instructionLength>0 && instructionLength!=2){
    			index_bytesInFile++;
    			indexKWA++;
    			switch(getVariableTypeCode(_instructions[ByteToInstruction(_bytesInFile[index_bytesInFile-1])])){
        			case 0:
        				intInBytes[0]=_bytesInFile[index_bytesInFile];index_bytesInFile++;
        				intInBytes[1]=_bytesInFile[index_bytesInFile];index_bytesInFile++;
        				intInBytes[2]=_bytesInFile[index_bytesInFile];index_bytesInFile++;
        				intInBytes[3]=_bytesInFile[index_bytesInFile];
        				_KWA[indexKWA]=(""+ByteArrayToInt(intInBytes));
        				indexKWA+=3;
        				break;
        			case 1:
        				floatInBytes[0]=_bytesInFile[index_bytesInFile];index_bytesInFile++;
        				floatInBytes[1]=_bytesInFile[index_bytesInFile];index_bytesInFile++;
        				floatInBytes[2]=_bytesInFile[index_bytesInFile];index_bytesInFile++;
        				floatInBytes[3]=_bytesInFile[index_bytesInFile];
        				_KWA[indexKWA]=(""+ByteArrayToFloat(floatInBytes));
        				indexKWA+=3;
        				break;
        			case 2:
        				doubleInBytes[0]=_bytesInFile[index_bytesInFile];index_bytesInFile++;
        				doubleInBytes[1]=_bytesInFile[index_bytesInFile];index_bytesInFile++;
        				doubleInBytes[2]=_bytesInFile[index_bytesInFile];index_bytesInFile++;
        				doubleInBytes[3]=_bytesInFile[index_bytesInFile];index_bytesInFile++;
        				doubleInBytes[4]=_bytesInFile[index_bytesInFile];index_bytesInFile++;
        				doubleInBytes[5]=_bytesInFile[index_bytesInFile];index_bytesInFile++;
        				doubleInBytes[6]=_bytesInFile[index_bytesInFile];index_bytesInFile++;
        				doubleInBytes[7]=_bytesInFile[index_bytesInFile];
        				_KWA[indexKWA]=(""+ByteArrayToDouble(doubleInBytes));
        				indexKWA+=7;
        				break;		
        			case 3:
        				_KWA[indexKWA]=(""+ByteToChar(_bytesInFile[index_bytesInFile]));
        				break;
    			}
    		}
    	}
        
    }
    
    //0 -> Integer -- 1-> Float -- 2-> Double -- 3 -> char -- 4 -> String 
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
    		//SETINDEX
    		case 23:case 26:case 27:case 28:case 29:case 30:
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
    		//CMP & Arithmetic & POPINDEX
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

    //DISASSEMBLY
    public static void TranslateToAssembly(){
        //Creates the array that is going to be used for the final print. 
        String [][] printingArray= new String[_KWA.length][3];  
        //Is used to know what type of instruction is next to be used. 
        String instruction=""; 
        int rowPrintingArray=0;
        for(int i=0; i<_KWA.length;i++)
        {
            if(!_KWA[i].equals("")){
                instruction=GetTypeInstruction(_KWA[i]);
                printingArray[rowPrintingArray][0]=CheckIfTag(i);
                printingArray[rowPrintingArray][1]=_instructions[Integer.parseInt(_KWA[i])];
                if(instruction.equals("C")) //constante
                {
                    i++;
                    printingArray[rowPrintingArray][2]=_KWA[i];
                }
                if(instruction.equals("V")) //variable
                {
                    i++;
                    printingArray[rowPrintingArray][2]=GetVariable(_KWA[i]);
                }
                if(instruction.equals("E")) //etiqueta
                {
                    i++;
                    printingArray[rowPrintingArray][2]=GetTag(_KWA[i]);
                }
                if(instruction.equals("F"))
                {
                    printingArray[rowPrintingArray][2]="";
                }
                rowPrintingArray++;
            }
        }
        WriteArrayToFile(printingArray,_fileName);
    }
    
    //recibe el codigo de operacion que se quiere buscar
    //regresa el tipo que es - Constante, Variable, Funcion -
    private static String GetTypeInstruction(String instructionCode){
        //functions array:Has all the instructions that dont require a anything else after itself (ADD, Writeln)
        //variablesArray:Has all the functions that requiere a variable afterwards (PushI, PopI)
        //constantsArray:Has all the functions that requiere a constant afterwards (PushKI, PoPI)
        //tagsArray:Has the functions which requiere a tag afterwards (Jumpto)
        String [] functionsArray ={"51","52","53","54","55","56","60","61","62","63","64","17","0","25"};
        String [] variablesArray = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","18","19","20","21","22","23","26","27","28","29","30","36","37","38","39","40","41","42","43","44","45","46","47","48","49","50"};
        String [] constantsArray= {"31","32","33","34","35","24","16"};
        String [] tagsArray= {"57","58","59"};
	int longMayor=variablesArray.length;
	//longMayor is equal to the longest array for search purposes
	//Search what type of instruction is next in KWA
	for(int i = 0 ; i < longMayor ; i++ ){
            if( i < constantsArray.length ){
		if( instructionCode.equals(constantsArray[i]) )
                    return "C";
            }
            if( i < variablesArray.length ){
		if( instructionCode.equals(variablesArray[i]) )
		{
                    return "V";
		}
            }
            if( i < functionsArray.length ){
		if( instructionCode.equals(functionsArray[i]) )
                    return "F";
            }
            if(i<tagsArray.length){
                if(instructionCode.equals(tagsArray[i]))
                    return "E";
            }
	}
	return "";
    }
    private static String CheckIfTag(int iteracion){
        for(int i = 0 ; i < _tags.length ; i++){
            ///busca la dirección
            if(_tags[i][1].equals(iteracion+"")){
                //regresa el nombre de la etiqueta
		return _tags[i][0]+":"; 
            }
        }
	return "";
    }
    private static String GetTag(String dir){
        for(int i=0;i<_tags.length;i++){
            if(_tags[i][1].equals(dir))
                return _tags[i][0];
        }
        return "";
    }
    private static String GetVariable(String dirVariable){
        for( int i = 0 ; i < _variables.length ; i++ ){
            if( _variables[i][2].equals(dirVariable) )
		return _variables[i][0];
	}
	return "";
    }
    private static void WriteArrayToFile(String [][] printingArray, String fileName){
         
        try{
            File texto=new File(fileName.substring(0,fileName.length()-4) + "Des.ASM");
            FileWriter escribir = new FileWriter(texto,false);
            
            int vectorSize=0;
            while(printingArray[vectorSize][0]!=null)
                vectorSize++;
            for(int x=0; x<vectorSize;x++){
                if(printingArray[x][0].equals(""))
                    escribir.write("\t\r\n");
                else
                    escribir.write("\t\r\n"+printingArray[x][0] + "\t\r\n");
                escribir.write(printingArray[x][1]+ "\t ");
                if(printingArray[x][2].equals(""))
                    escribir.write("\t");
                else
                    escribir.write(printingArray[x][2]);
            }                  
            escribir.close();
        }
        catch(Exception e){
            System.out.println("Error");
        }
        
        
        
        int vectorSize=0;
        while(printingArray[vectorSize][0]!=null)
            vectorSize++;
        for(int x=0; x<vectorSize;x++){
            if(printingArray[x][0].equals(""))
                System.out.print("\t");
            else
                System.out.print(printingArray[x][0] + "\t");
            System.out.print(printingArray[x][1]+ "\t");
            if(printingArray[x][2].equals(""))
                System.out.println();
            else
                System.out.println(printingArray[x][2]);
            
        }
               
    }
}