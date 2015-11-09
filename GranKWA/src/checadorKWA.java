import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class checadorKWA {
    private static String [] _KWA;
    static String[] _instructions=new String[75];
    static byte[] _bytesInFile;
    static String _fileName;
    public static void mainChecador(String[] args) throws Exception{
    	pasarInstruKWAalVector();
    	 fill_KWA();
    	 WriteAssemblyFile();
    }
    public static void WriteAssemblyFile() throws NumberFormatException, IOException{
    	 File texto=new File(_fileName.substring(0,_fileName.length()-4) + "Checador.ASM");
         FileWriter escribir = new FileWriter(texto,false);
         for(int i=0; i<_KWA.length; i++)
        	 escribir.write(_KWA[i]+"\n");
         escribir.close();
    }
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
	    public static void pasarInstruKWAalVector()throws Exception{
	        Scanner archivoInstrucciones= new Scanner(new FileReader("InstruccionesKWA.txt"));
	        int index=0;
	        while(archivoInstrucciones.hasNext()){
	            _instructions[index]=archivoInstrucciones.next();
	            index++;
	        }
	        archivoInstrucciones.close();
	    }
}
