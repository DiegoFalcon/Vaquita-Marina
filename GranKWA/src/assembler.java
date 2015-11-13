//package assembler;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.*;
import java.util.*;
import java.nio.*;
import javax.swing.JOptionPane;

public class assembler {
	
    static String[] tokens;
    static String[] KWA;
    static String[] instructions;
    static String[][] data;
    static String[][] tags;
    static String[][] referencedTags;
    static int SC=0, SD=0;
    static byte[] bytesInFile;
    static String fileName;
    
    public static void mainAssembler () throws Exception{
        tokens=new String[0];
        KWA=new String[0];
        instructions=new String[75];
        data=new String [0][4];
        tags=new String [0][2];
        referencedTags=new String[0][2];

        pasarInstruKWAalVector();
        fillTokensArray();
        if(validate()){
            if(assemblyToKWA())
            {
                    variablesTabletoFile();
                    tagsTabletoFile();
                    ponerHeader();
                    writeAssemblyFile();
                    //KWAToFile();
            }
        }
    }
    //LLENAR TOKENS
    public static void fillTokensArray()throws Exception{
       //OPEN FILE DIALOG
        Frame f=new Frame();
        boolean error=false;
        FileDialog fd = new FileDialog(f, "Choose a file", FileDialog.LOAD);
        
        fd.setDirectory("C:\\");
        fd.setFile("*.ASM");
        fd.setVisible(true);
        
        fileName="";
        
         try
        {
         fileName = fd.getFile();
        if (fileName == null)
        {
            System.out.println("You cancelled the choice");
            error=true;
        }
        else
        {
            System.out.println("You chose " + fileName);
            
        }
        }catch(Exception e){System.out.println(e.getMessage());error=true;}
        if(error)
            System.exit(0);
        f.dispose();
        
        
        //archivo a "ensamblar"
        Scanner sc = new Scanner(new FileReader(fileName));
        //instrucciones
        Scanner instruc= new Scanner(new FileReader("InstruccionesKWA.txt"));
        //instruccion actual
        String inst="";
        String stringnumber="",stringmessage="";
        int stringintnum=0, stringsentsize=0;
        int contador=0;

        while(sc.hasNext()){
            inst=sc.next();
            if(inst.charAt(0)!=';')
            {
                //instruccion normal
                if(validarInstruccion(inst)==1){ 
                    tokens=agrandarVector(tokens,3);
                    tokens[contador]=inst;
                    contador++;
                    System.out.println(inst);
                    inst=sc.next();
                    tokens[contador]=inst;
                    contador++;
                    tokens[contador]="";
                    contador++;
                }
                else{
                    //error o constante
                    if(validarInstruccion(inst)==0){ 
                        tokens=agrandarVector(tokens,2);
                        tokens[contador]=inst;
                        contador++;
                        tokens[contador]="";
                        contador++;
                    }
                    else{
                        //instruccion corta
                        if(validarInstruccion(inst)==3){

                            tokens=agrandarVector(tokens,2);

                            tokens[contador]=inst;
                            contador++;
                            tokens[contador]="";
                            contador++;

                        }
                        else{
                            //etiqueta
                            if(validarInstruccion(inst)==2){ 
                                tokens=agrandarVector(tokens,1);
                                tokens[contador]=inst;
                                contador++;
                            }
                            else
                            {
                                if(validarInstruccion(inst)==4)
                                {
                                    tokens=agrandarVector(tokens,3);
                                    tokens[contador]=inst;
                                    contador++;
                                    inst=sc.nextLine();
                                    stringintnum=0;
                                    stringsentsize=0;
                                    stringnumber="";
                                    stringmessage="";

                                    int i=0;
                                    //sacar el tamaño enviado
                                    while(inst.charAt(++i)!=','&&i+1<inst.length())
                                    {
                                            stringnumber+=inst.charAt(i)+"";
                                    }
                                    //sacar el string enviado y su tamaño
                                    while(++i<inst.length())
                                    {
                                            stringmessage+=inst.charAt(i)+"";
                                            stringsentsize++;
                                    }
                                    try{
                                    stringintnum=Integer.parseInt(stringnumber);
                                    if(stringintnum<stringsentsize){
                                            String shorterString="";
                                            for(int x=0;x<stringintnum;x++)
                                                    shorterString+=stringmessage.charAt(x)+"";
                                            stringmessage=shorterString;
                                        }
                                    //si el tamaño enviado es mayor al atmaño del String
                                            while(stringmessage.length()<stringintnum){
                                                    stringmessage+=" ";
                                            }
                                                                                tokens[contador]=stringmessage.length()+","+stringmessage;
                                    contador++;
                                    }catch(Exception ex){
                                        tokens[contador]=stringnumber+","+stringmessage;
                                        contador++;
                                    }

                                    
                                    


                                    tokens[contador]="";
                                    contador++;
                                }
                                else
                                sc.nextLine();
                            }
                        }
                    }
                }
            }
            else
            {
                sc.nextLine();
            }
        }
        sc.close();
        instruc.close();
    }
    public static int validarInstruccion(String inst){
        int indexInstr=0;
        if(inst.charAt(inst.length()-1) == ':')
            //es etiqueta
            return 2;
        while(indexInstr < 75){
            if(instructions[indexInstr].equals(inst)){
                if(indexInstr==0||indexInstr == 25 || indexInstr == 17 || (indexInstr >= 51 && indexInstr <= 56) 
                    || (indexInstr >= 60 && indexInstr <= 64))
                    //es ADD, COMP, WRTLN (los de 1)
                    return 3; 
                else                    
                    if(indexInstr == 16 || indexInstr == 35) //es WRTM o PUSHKS (STRINGS)
                        return 4;
                    else
                        //es instruccion
                        return 1;
            }
            indexInstr++;
        }
        //no es etiqueta ni instruc
        return 0; 
    }
    public static String[] agrandarVector(String[] a,int casillasagregar){
        String[] anew=new String[a.length+casillasagregar];
        
        for(int x=0;x<a.length;x++)
            anew[x]=a[x];
        return anew;
    }
    //LLENAR VECTOR DE INSTRUCCIONES
    public static void pasarInstruKWAalVector()throws Exception{
        Scanner archivoInstrucciones= new Scanner(new FileReader("InstruccionesKWA.txt"));
        int index=0;
        while(archivoInstrucciones.hasNext()){
            instructions[index]=archivoInstrucciones.next();
            index++;
        }
        archivoInstrucciones.close();
    }
    //VALIDAR        
   public static boolean validate()
	{
		//indice del token a validar
		int index;
		//Tipo del valor revisado anteriormente
		int previous=0;
		//Tipo del valor a revisar
		int actualtype=0;
		//Tipo del valor revisado anteriormente (antes del anterior)
		int previousprevious=0; 
		
		boolean Validate;
		String part1;
		String part2;
		String[] parts;
		boolean words=true;
	
		boolean variableorconstant;
		char ch;
		
		for(index=0;index<tokens.length;index++)
		{
			
			
			//Recibo el tipo de variable 
			actualtype= getTypeToken(tokens[index],previous); // Metodo Yisus
			//El tipo es constante o Variable?
			if(actualtype == 1)
			{
				if(previous==10)
				{
					if(tokens[index].length() > 1)
					{
                                            
                                            JOptionPane.showMessageDialog(null,"El Char es muy grande: " + tokens[index],"Error",JOptionPane.ERROR_MESSAGE);
                                            
                                            return false;
					}
				}else{
					
				
				//Tiene puros numeros?
					variableorconstant=isNumeric(tokens[index]);
					if(!variableorconstant)
					{
						//tiene algun numero?
						if (tokens[index].matches(".*\\d+.*") == false)
						{
						
							boolean Symbolcheck=false;
							for (int i = 0; i < tokens[index].length(); i++) 
							{
								ch = tokens[index].charAt(i);
								if (!Character.isAlphabetic(ch) && !Character.isDigit(ch)) 
								{
									Symbolcheck=true;
									JOptionPane.showMessageDialog(null,"La variable solo acepta letras " + tokens[index],"Error",JOptionPane.ERROR_MESSAGE);
									return false;
									//error
								} 
				            
							}
							if(!Symbolcheck)
							{
								actualtype=2; //variable
							}
						
						
						}else
						{
							//Revisar que tiene coma
							if(tokens[index].contains(","))
							{
								//Separa el String en 2 partes usando la coma como pivote
								parts=tokens[index].split(",");
							
								
								part1=parts[0];
								part2=parts[1];
                                                                
                                                                if(parts.length>2)
								{
									for(int c=2 ; c<parts.length;c++)
                                                                        {
                                                                            part2 += "," + parts[c];
                                                                        }
									
								}
							
								words=isWord(part1);
							
								//Revisa si la parte 1 son letras y la 2da son numeros (vector)
								if(words && isNumeric(part2))
								{
									previousprevious=previous;
									previous=2;
									actualtype=1;
								}else
								{
									words=isWord(part2);
									//Revisa si la primera parte son numeros y la segunda son letras (DEFS)
									if(isNumeric(part1) && words && previous != 9)
									{
										previousprevious=previous;
										previous=1;
										actualtype=2;
									}
									else
									{
										// Valida el MEnsaje de WRTM
										if(isNumeric(part1))
										{
											previousprevious=previous;
											previous=1;
											actualtype=1;
										
										}else
										{
											JOptionPane.showMessageDialog(null,"No es valido para un vector o mensaje o DEFS: " + tokens[index],"Error",JOptionPane.ERROR_MESSAGE);

											
											return false;
										}
									}
								}
							
							
							}
							else{
								JOptionPane.showMessageDialog(null,"No es una variable valida: " + tokens[index],"Error",JOptionPane.ERROR_MESSAGE);

							
								return false;
							}
						
						}
					}else
					{
						actualtype=1;//constante
					
					}
				}
			}
			
			Validate=validateSequence(previousprevious,previous,actualtype); // metodo rebeks
			
			
			
			if(!Validate)
			{
				JOptionPane.showMessageDialog(null,"El Orden de las instrucciones no es valida: "+ tokens[index-1] + " "+ tokens[index],"Error",JOptionPane.ERROR_MESSAGE);

				
				return false;
				
				
			}
			else
			{
				previousprevious=previous;
				previous=actualtype;
			}
		}
		
		
                return true;
		
	}
	//Checa si el String es totalmente numerico
	public static boolean isNumeric(String str){
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	//Checa si el String es una palabra
	public static boolean isWord(String str){
		char ch;
		for (int i = 0; i < str.length(); i++) 
		{
            ch = str.charAt(i);
            if (!Character.isAlphabetic(ch)) 
            {
            	return false;
            	
            	
            	
            } 
            
        }
		
		return true;
	}
	public static int getTypeToken(String token,int previous){
	if(token.equals(""))
		return 0; //It's Space
	
	if(token.substring(token.length() - 1).equals( ":") && previous == 0)
		return 3; //It's Tag
	
	if(token.equals("ADD") || token.equals( "SUB") || token.equals( "MUL" )|| token.equals( "DIV" )|| token.equals( "MOD") || token.equals( "CMPEQ") 
			|| token.equals( "CMPNE") || token.equals( "CMPLT" )|| token.equals( "CMPLE" )|| token.equals( "CMPGT" )|| token.equals( "CMPGE")
			|| token.equals( "POPINDEX" )|| token.equals( "WRTLN" )|| token.equals( "HALT"))
		return 4; //It's Instruction 0
	 
	
	if(token.equals( "SETINDEXK") || token.equals( "PUSHKI" )|| token.equals( "PUSHKF" )|| token.equals( "PUSHKD" ))
		return 5; //It's Instruction K
	
	if(token.equals( "READI" )|| token.equals( "READD" )|| token.equals( "READF" )|| token.equals( "READC" )|| token.equals( "READS" )|| token.equals( "READVI") 
			|| token.equals( "READVD" )|| token.equals( "READVF" )|| token.equals( "READVC" )|| token.equals( "READVS" )|| token.equals( "WRTI")
			|| token.equals( "WRTD" )|| token.equals( "WRTF" )|| token.equals( "WRTC" )|| token.equals( "WRTS" )|| token.equals( "WRTVI" )
			|| token.equals( "WRTVD" )|| token.equals( "WRTVC" )|| token.equals( "WRTVF" )|| token.equals( "WRTVS" )|| token.equals( "SETINDEX" )
			|| token.equals( "PUSHI" )|| token.equals( "PUSHD") || token.equals( "PUSHC" )|| token.equals( "PUSHF" )|| token.equals( "PUSHS")
			|| token.equals( "PUSHVI" )|| token.equals( "PUSHVF" )|| token.equals( "PUSHVD" )|| token.equals( "PUSHVC" )|| token.equals( "PUSHVS")
			|| token.equals( "POPI" )|| token.equals( "POPD" )|| token.equals( "POPC" )|| token.equals( "POPF" )|| token.equals( "POPS")
			|| token.equals( "POPVI" )|| token.equals( "POPVD" )|| token.equals( "POPVC" )|| token.equals( "POPVF" )|| token.equals( "POPVS")
			|| token.equals( "JMP" )|| token.equals( "JMPT" )|| token.equals( "JMPF" )|| token.equals( "POPVF") || token.equals( "POPVS")
			|| token.equals( "DEFI" )|| token.equals( "DEFD" )|| token.equals( "DEFF" )|| token.equals( "DEFC"))
		return 6; //It's Instruction V
	
	if(token.equals( "DEFVI" )|| token.equals( "DEFVD" )|| token.equals( "DEFVF") || token.equals( "DEFVC" )|| token.equals( "DEFVS"))
		return 7; // It's Instruction VK
	
	if(token.equals( "DEFS" ))
		return 8; //It's Instruction KV
	
	if( token.equals( "PUSHKS" )|| token.equals( "WRTM"))
	{
		return 9; //It's Instruction KK
	}
	
	if(token.equals( "PUSHKC"))
	{
		return 10; // It's Instruction K(anything)
	}
	
	return 1; //Its K or V

	}
	public static boolean validateSequence(int previousprevious, int previous, int actual)
    {  
        if(actual==0)
            if(previous!=1 || previous!=2 || previous!=4)
                return true;
            else
                return false;
        
        if(actual==1)
            if(previous==5 || previous==8 || (previous==2 && previousprevious == 7) || (previous==1 && previousprevious== 9) || previous == 10)
                return true;
            else
                return false;
        
        if(actual == 2)
            if (previous == 6 || previous == 7 || (previous == 1 && previousprevious == 8) || previous == 10 )
               return true;
            else
                return false;
        
        if(actual==3)
            if(previous == 0)
                return true;
            else
                return false;
        if(actual == 4 || actual == 5 || actual == 6 || actual == 7 || actual == 8 || actual == 9 || actual == 10)
            if(previous == 0 || previous == 3)
                return true;
            else
                return false;
        else return false;
    }
	//RESOLVER
   public static boolean assemblyToKWA(){
        int tokenPos=0;  
        int tagDir=0; 
        int variableDir=0;
        int variableNeededSize=0;
        String stringSize="";
        
        while(tokenPos<tokens.length){
            //Es una etiqueta
            if(tokens[tokenPos].charAt(tokens[tokenPos].length()-1) == ':'){
                tagDir=Integer.parseInt(getTagAddress(tokens[tokenPos]));
                //No existe
                if(tagDir==-1){
                    createTag(tokens[tokenPos],SC);
                    updateReferencedTags(tokens[tokenPos],Integer.parseInt(getTagAddress(tokens[tokenPos])));
                }
                //Si existe: ERROR 
                else{
                    System.out.println("ERROR: No puede estar la misma etiqueta definida m�s de una vez.");
                    return false;
                }
                tokenPos++;
            }
            
            //Es instruccion
            variableNeededSize=getVariableSize(getInstructionCode(tokens[tokenPos]));
            if(variableNeededSize!=-3)
                addToKWA(""+getInstructionCode(tokens[tokenPos]),1);
            tokenPos++;

            //JMP 
            if(variableNeededSize!=-2 && tokens[tokenPos]!=""){
                //Si necesita variable
                if(variableNeededSize==-4){
                    tagDir=Integer.parseInt(getTagAddress(tokens[tokenPos]+":"));
                    //Existe
                    if(tagDir!=-1){
                        addToKWA(""+tagDir,2);
                    }
                    //No existe
                    else{
                        addToReferencedTags(tokens[tokenPos]+":",SC);
                        addToKWA("-1",2);
                    }
                    tokenPos++;
                }
                else{
                    //Se definió una variable
                    if(variableNeededSize==-3){
                    	if(tokens[tokenPos-1].charAt(tokens[tokenPos-1].length()-2)!='V'){
	                        createVariable(tokens[tokenPos],tokens[tokenPos-1],"0");
	                        tokenPos++;
                    	}
                    	else{
                    		for(int c=0;c<Integer.parseInt(tokens[tokenPos].substring(tokens[tokenPos].indexOf(',')+1,tokens[tokenPos].length()));c++){
                    			createVariable(tokens[tokenPos].substring(0,tokens[tokenPos].indexOf(',')),tokens[tokenPos-1],"0");
                    		}
                                tokenPos++;
                    	}
                    }
                    else{
                        //Es un string
                        if(variableNeededSize==-1){
                            for(int c=0; tokens[tokenPos].charAt(c)!=','; c++)
                                    stringSize+=""+tokens[tokenPos].charAt(c);
                            addToKWA(tokens[tokenPos],(Integer.parseInt(stringSize)+1));
                            stringSize="";
                        }
                        
                        else{
                            //Es constante
                            if(variableNeededSize!=2){
                                addToKWA(tokens[tokenPos],variableNeededSize);
                            }
                            //Es variable
                            else{
                                variableDir=Integer.parseInt(getVariableDir(tokens[tokenPos]));
                                if(variableDir==-1){
                                    System.out.println("ERROR: La variable"+ tokens[tokenPos] +" no fue declarada.");
                                    return false;
                                }
                                else
                                    addToKWA(""+variableDir,2);
                            }
                        }
                        tokenPos++;
                    }
                }
            }

            //-------Es salto de l�nea
            if(tokenPos < tokens.length && tokens[tokenPos] == "")
                tokenPos++;
        }//termina de revisar tokens

        //Quedaron etiquetas referenciadas sin declarar
        if(referencedTags.length>0){
            System.out.println("ERROR: faltan direcciones de etiquetas.");
            return false;
        }
        return true;
    }
    public static void addToKWA(String instructionCode, int instructionSize){
        String [] array_tempKWA;
        int array_KWAlength = KWA.length;
        array_tempKWA = new String[array_KWAlength];
        
        for (int index = 0; index<array_KWAlength; index++)
            array_tempKWA[index]=KWA[index];

        KWA = new String[array_KWAlength+instructionSize];
        
        for (int index = 0; index<array_KWAlength; index++)
            KWA[index]=array_tempKWA[index];

        KWA[array_KWAlength]=instructionCode;

        for (int i = 1; i<instructionSize; i++)
            KWA[array_KWAlength+i]="";
        
        SC=KWA.length;
    }
    public static void createVariable(String variableName, String instruction, String value){
        String array_tempData[][];
        int array_dataLength = data.length;
        array_tempData = new String[data.length][4];
        int variableSize = getVariableSize(instruction,value);
         if (instruction.charAt(instruction.length()-1)=='S')
            variableName=variableName.substring(variableName.indexOf(',')+1,variableName.length());
        for (int index=0; index<array_dataLength; index++){
            array_tempData[index][0]=data[index][0];
            array_tempData[index][1]=data[index][1];
            array_tempData[index][2]=data[index][2];
            array_tempData[index][3]=data[index][3];
        }
        
        //array_tempData = array_data;
        data = new String[array_dataLength+1][4];

        for (int index=0; index<array_dataLength; index++){
            data[index][0]=array_tempData[index][0];
            data[index][1]=array_tempData[index][1];
            data[index][2]=array_tempData[index][2];
            data[index][3]=array_tempData[index][3];
        }
        
        data[array_dataLength][0]=variableName;
        data[array_dataLength][1]=value;
        data[array_dataLength][2]=""+SD;
        data[array_dataLength][3]=""+getVariableTypeCode(instruction);

        //se suma el segmento de dato
        SD+=variableSize;
    }
    public static String getVariableDir(String variableName){
        int array_dataLength = data.length;
        
        for (int index=0; index<array_dataLength; index++){
            if (data[index][0].equals(variableName)){
                //regresa la direccion
                return data[index][2];
            }
        }
        return "-1";
    }
    public static void setVariableValue(String variableName,String newValue){
        int array_dataLength = data.length;
        for (int index=0; index<array_dataLength; index++){
            if (data[index][0].equals(variableName)){
                //regresa la direccion
                 data[index][2]=newValue;
            }
        }
    }
    public static int getVariableSize(String instructionName,String value){
        char lastLetter=instructionName.charAt(instructionName.length()-1);

        if(lastLetter=='I'|| lastLetter=='F')
            return 4;
        
        if(lastLetter=='D')
            return 8;
        
        if(lastLetter=='C')
            return 1;
        
        if(lastLetter=='S'){
        	return 255;
            /*String temp="";
            boolean flag=false;
            boolean error=false;
            int length=0;
            
            for(int i=0;i<value.length();i++){
                if(!flag &&value.charAt(i)!=','){
                  temp+=value.charAt(i);
                }
                
                if(value.charAt(i)==','){
                    flag=true;
                    try{
                        length=Integer.parseInt(temp);
                    }
                    catch(Exception ex){ 
                        error=true; 
                    }
                    
                    temp="";
                    i=value.length();
                }
            }
            if(!error)
                return length+1;
            else
                return -1;*/
        }
        return -1;
    }
    public static int getInstructionCode(String instructionName){
        //REGRESA -1 SI NO COINCIDE LA INSTRUCCION
        for(int i=0;i<instructions.length;i++){
            if(instructions[i].equals(instructionName))
                return i;
        }
        return -1;
    }
    public static boolean IsTag (String tagname){
        if (tagname.endsWith(":")){
            return true;
        }
        else
            return false;
    }
    public static void createTag(String tagname, int dir){
        int ArrayLength = tags.length;
        // transforma de int a string
        String direction = "" + dir; 
        // crea una matriz temporal
        String[][] tmp = new String[ArrayLength+1][2]; 

        for(int count = 0 ; count < ArrayLength; count++){
            tmp[count][0]=tags[count][0];
            tmp[count][1]=tags[count][1];
        }
        tmp[ArrayLength][0]=tagname;
        tmp[ArrayLength][1]=direction;
        // ahora la matriz global tendra lo mismo que la de tmp
        tags = tmp; 
    }
    public static void addToReferencedTags(String tagname, int dir ){
        int ArrayLength = referencedTags.length;
        // transforma de int a string
        String direction = "" + dir; 
        // crea una matriz temporal
        String[][] tmp = new String[ArrayLength+1][2]; 

        for(int count = 0 ; count < ArrayLength; count++){
                tmp[count][0]=referencedTags[count][0];
                tmp[count][1]=referencedTags[count][1];
        }
        
        tmp[ArrayLength][0]=tagname;
        tmp[ArrayLength][1]=direction;
        // ahora la matriz global tendra lo mismo que la de tmp
        referencedTags = tmp;
    }
    public static String getTagName(int dir){
        int ArrayLength = tags.length;
        String direction = "" + dir;
        
        for(int count = 0; count < ArrayLength; count++){
            if(direction == tags[count][1])
                return tags[count][0];
        }
        return "-1";
    }
    public static String getTagAddress(String tagname){
        int ArrayLength = tags.length;

        for(int count = 0; count < ArrayLength; count++){
            if(tagname.equals(tags[count][0]))
                return tags[count][1];
        }
        return "-1";
    }
    public static int getVariableSize(int instruction){
        //REGRESA -1 SI ES STRING
        //2 SI NO NECESITA VARIABLE (pura direccion)
        //-2 ERROR (HALT,JUMP,etc)
        int size=0;
        switch(instruction){
            case 0: size=-2;break;
            case 1: size=2;break;
            case 2: size=2;break;
            case 3: size=2;break;
            case 4: size=2;break;
            case 5: size=2;break;
            case 6: size=2;break;
            case 7: size=2;break;
            case 8: size=2;break;
            case 9: size=2;break;
            case 10: size=2;break;
            case 11: size=2;break;
            case 12: size=2;break;
            case 13: size=2;break;
            case 14: size=2;break;
            case 15: size=2;break;
            case 16: size=-1;break;
            case 17: size=-2;break;
            case 18: size=2;break;
            case 19: size=2;break;
            case 20: size=2;break;
            case 21: size=2;break;
            case 22: size=2;break;
            case 23: size=2;break;
            case 24: size=4;break;
            case 25: size=2;break;
            case 26: size=2;break;
            case 27: size=2;break;
            case 28: size=2;break;
            case 29: size=2;break;
            case 30: size=2;break;
            case 31: size=4;break;
            case 32: size=4;break;
            case 33: size=8;break;
            case 34: size=1;break;
            case 35: size=-1;break;
            case 36: size=2;break;
            case 37: size=2;break;
            case 38: size=2;break;
            case 39: size=2;break;
            case 40: size=2;break;
            case 41: size=2;break;
            case 42: size=2;break;
            case 43: size=2;break;
            case 44: size=2;break;
            case 45: size=2;break;
            case 46: size=2;break;
            case 47: size=2;break;
            case 48: size=2;break;
            case 49: size=2;break;
            case 50: size=2;break;
            case 51: size=-2;break;
            case 52: size=-2;break;
            case 53: size=-2;break;
            case 54: size=-2;break;
            case 55: size=-2;break;
            case 56: size=-2;break;
            case 57: size=-4;break;
            case 58: size=-4;break;
            case 59: size=-4;break;
            case 60: size=-2;break;
            case 61: size=-2;break;
            case 62: size=-2;break;
            case 63: size=-2;break;
            case 64: size=-2;break;

            default: size=-3;
        }
        return size;
    }
    public static void updateReferencedTags(String tagname, int dir){
        int notReferenced=0;
        int TagsArrayLength = referencedTags.length;
        String direction = "" + dir;
        String[][] tmp;

        for(int i=0;i<referencedTags.length;i++)
            if(!referencedTags[i][0].equals(tagname))
                notReferenced++;

        tmp=new String[notReferenced][2];
        notReferenced=0;

        for(int count = 0; count < TagsArrayLength; count++){
            if(referencedTags[count][0].equals(tagname)){
                KWA[Integer.parseInt(referencedTags[count][1])] = direction;
            }
            else{
                tmp[notReferenced][0]=referencedTags[count][0];
                tmp[notReferenced][1]=referencedTags[count][1];
                notReferenced++;
            }
        }
        referencedTags=tmp;
    }
    //ESCRITURA DE ARCHIVOS
    public static void ponerHeader(){
         //tamaño del vector
        String[] KWAWithHeader=new String[KWA.length+14]; 
        KWAWithHeader[0]="(";
        KWAWithHeader[1]="C";
        KWAWithHeader[2]=")";
        KWAWithHeader[3]="K";
        KWAWithHeader[4]="W";
        KWAWithHeader[5]="A";
        KWAWithHeader[6]="2";
        KWAWithHeader[7]="0";
        KWAWithHeader[8]="1";
        KWAWithHeader[9]="5";
        KWAWithHeader[10]=""+SC;
        KWAWithHeader[11]="";
        KWAWithHeader[12]=""+SD;
        KWAWithHeader[13]="";
        for(int i=0;i<KWA.length;i++)
        	KWAWithHeader[i+14]=KWA[i];
        KWA=KWAWithHeader;
    }
    public static void KWAToFile(){
        try{
            File texto=new File("KWAtext.txt");
            FileWriter escribir = new FileWriter(texto,false);
            //String vectorHeader[]=ponerHeader();
            
            //for(int i=0;i<vectorHeader.length;i++)
            //    escribir.write(vectorHeader[i] +"\r\n");
            
            for(int i=0;i<KWA.length;i++)
                escribir.write(KWA[i] +"\r\n");
            
            escribir.close();
        }
        catch(Exception e){
            System.out.println("Error");
        }
    }
    public static void tagsTabletoFile()
    {
        try{
            File texto=new File(fileName.substring(0, fileName.length()-3)+"tags");
            FileWriter escribir = new FileWriter(texto,false);
           
            for(int i=0;i<tags.length;i++)
            {
                escribir.write(tags[i][0].substring(0,tags[i][0].length()-1)+" "+tags[i][1]+"\r\n");
                
            }
            escribir.close();
        }
        catch(Exception e){
            System.out.println("Error");
        }
    }
    public static void variablesTabletoFile(){
        try{
            File texto=new File(fileName.substring(0, fileName.length()-3)+"vars");
            FileWriter escribir = new FileWriter(texto,false);
            
            for(int i=0;i<data.length;i++)
                escribir.write(data[i][0]+" "+data[i][1]+" "+data[i][2]+" "+data[i][3]+"\r\n");
            
            escribir.close();
        }
        catch(Exception e){
            System.out.println("Error");
        }
    }
	//ESCRITURA DE ARCHIVOS
    public static void writeAssemblyFile() throws NumberFormatException, IOException{
		BufferedOutputStream bufferedOut = new BufferedOutputStream(new FileOutputStream(fileName.substring(0, fileName.length()-3)+"KWA")); 
    	int instructionLength=0, variableSize=0;
    	String stringToSave="";
    	
    	//HEADER
    	for(int i=0;i<10;i++){
    		bufferedOut.write(CharToByte(KWA[i].charAt(0)));
    	}
    	bufferedOut.write(SegmentsToByteArray(Integer.parseInt(KWA[10])));
    	bufferedOut.write(SegmentsToByteArray(Integer.parseInt(KWA[12])));
    	
    	//SC
    	for(int i=14;i<KWA.length;i++){
    		System.out.println("["+i+"]="+KWA[i]);
    		instructionLength=getInstructionVariableSize(Integer.parseInt(KWA[i]));
			bufferedOut.write(InstructionToByte(Integer.parseInt(KWA[i])));
    		if(instructionLength==-1){
    			i++;
    			variableSize=Integer.parseInt(KWA[i].substring(0, KWA[i].indexOf(',')));
    			bufferedOut.write(StringLengthToByte(variableSize));
    			stringToSave=KWA[i].substring(KWA[i].indexOf(',')+1,KWA[i].length());
    			for(int c=0;c<variableSize;c++){
    				bufferedOut.write(CharToByte(stringToSave.charAt(c)));
    			}
    			i=i+variableSize;
    		}
    		
    		if(instructionLength==2){
    			i++;
    			bufferedOut.write(DirToByteArray(Integer.parseInt(KWA[i])));
    			i++;
    		}
    		
    		if(instructionLength>0 && instructionLength!=2){
    			i++;
    			switch(getVariableTypeCode(instructions[Integer.parseInt(KWA[i-1])])){
	    			case 0:
	    				bufferedOut.write(IntToByteArray(Integer.parseInt(KWA[i])));
	    				break;
	    			case 1:
	    				bufferedOut.write(FloatToByteArray(Float.parseFloat(KWA[i])));
	    				break;
	    			case 2:
	    				bufferedOut.write(DoubleToByteArray(Double.parseDouble(KWA[i])));
	    				break;
	    			case 3:
	    				bufferedOut.write(CharToByte(KWA[i].charAt(0)));
	    				break;
    			}
    			i=i+instructionLength-1;
    		}
    	}
    	bufferedOut.close();
    }
    public static int getVariableTypeCode(String instruction){
        //0 -> Integer -- 1-> Float -- 2-> Double -- 3 -> char -- 4 -> String 
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
    //BYTE CONVERSIONS
    public static byte StringLengthToByte(int stringLengthToConvert){
    	return (byte)stringLengthToConvert;
 	}
    public static byte InstructionToByte(int instructionToConvert){
    	return (byte)instructionToConvert;
 	}
    public static byte[] IntToByteArray(int numberToConvert){
	   return ByteBuffer.allocate(4).putInt(numberToConvert).array();
	}
    public static byte[] FloatToByteArray(float numberToConvert)
	{
 	   return ByteBuffer.allocate(4).putFloat(numberToConvert).array();
	} 
    public static byte[] DoubleToByteArray(double numberToConvert)
	{
 	   return ByteBuffer.allocate(8).putDouble(numberToConvert).array();
	}
    public static byte CharToByte(char charToConvert)
	{
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
}