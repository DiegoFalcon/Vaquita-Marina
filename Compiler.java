import java.awt.FileDialog;
import java.awt.Frame;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Compiler {

	static int lastByteRead = 0;
	static boolean isFileFinished = false;
	static byte[] _bytesInFile;
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		openFile();
		
		while (!isFileFinished) {
			String sToken = ReadTokenFromFile();
			int nTokenCode = GetTokenCode(sToken);
		}
		
		System.out.println("Tokenizer Finished");
	}
	
	public static void openFile() throws IOException{
		Frame f = new Frame();
		boolean error = false;
		FileDialog fd = new FileDialog(f, "Choose a file", FileDialog.LOAD);
		fd.setDirectory("C:\\");
		fd.setFile("*.KWBG");
		fd.setVisible(true);
		String fileName = "";
		String fileDir = "";

		try {
			fileName = fd.getFile();
			fileDir = fd.getDirectory();
			if (fileName == null) {
				System.out.println("You cancelled the choice");
				error = true;
			} else {
				System.out.println("You chose " + fileName);

			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			error = true;
		}
		if (error)
			System.exit(0);
		f.dispose();

		_bytesInFile = Files.readAllBytes(Paths.get(fileDir, fileName));
	}

	public static String ReadTokenFromFile() throws IOException {

		// 9 - Tab
		// 10 - Salto de linea
		// 32 - Espacio
		// 33 - !
		// 40 - Abrir parentesis
		// 41 - Cerrar parentesis
		// 42 - *
		// 43 - +
		// 44 - ,
		// 45 - -
		// 47 - /
		// 59 - ;
		// 60 - <
		// 61 - =
		// 62 - >
		// 91 - Abrir corchete
		// 92 - \
		// 93 - Cerrar corchete
		// 123 - Abrir llave
		// 125 - Cerrar Llave

		boolean isComplete = false;
		String tokenWord = "";

		boolean commentFound = false;
		boolean quotationFound = false;

		while (!isComplete) {

			boolean increaseByte = false;
			if (!commentFound) {
				switch (_bytesInFile[lastByteRead]) {

				// Separadores de palabra que no se convierten a token
				case 9:
				case 10:
				case 13:
				case 32:
					increaseByte = true;
					if(!quotationFound){
						if (!tokenWord.equals("")) {
							isComplete = true;
						}
					} else {
						tokenWord += (char) _bytesInFile[lastByteRead];
					}
					

					break;

				// Comentarios
				case 92:
					increaseByte = true;
					if(!quotationFound){
						commentFound = true;
					}
					break;

				// Separadores de palabra que se convierten a token
				case 33:
				case 40:
				case 41:
				case 42:
				case 43:
				case 44:
				case 45:
				case 47:
				case 59:
				case 60:
				case 61:
				case 62:
				case 91:
				case 93:
				case 123:
				case 125:
					if(!quotationFound){
						if (tokenWord.length() == 0) {
							tokenWord += (char) _bytesInFile[lastByteRead];
							increaseByte = true;
						}
						isComplete = true;
						break;
					} else {
						increaseByte = true;
					}
					

				// No separadores de palabra
				default:
					if( _bytesInFile[lastByteRead] == 34){
						quotationFound = !quotationFound;
					}
					increaseByte = true;
					tokenWord += (char) _bytesInFile[lastByteRead];
					break;
				}

				if (increaseByte) {
					lastByteRead++;
				}

				if (lastByteRead == _bytesInFile.length) {
					isFileFinished = true;
					isComplete = true;
				}

			} else {
				if(_bytesInFile[lastByteRead] == 10){
					commentFound = false;
				}
				lastByteRead++;
			}

		}

		return tokenWord;
	}

	public static int GetTokenCode(String token)
    {
        switch(token)
        {
        	
            case "+":
                return 1;
            case "-":
                return 2;
            case "/":
                return 3;
            case "*":
                return 4;
            case "%":
                return 5;
            case "=":
                return 6;
            case "++":
                return 7;
            case "--":
                return 8;
            case "+=":
                return 9;
            case "-=":
                return 10;
            case ">=":
                return 11;
            case "<=":
                return 12;
            case "==":
                return 13;
            case "!=":
                return 14;
            case ">":
                return 15;
            case "<":
                return 16;
            case "AND":
                return 17;
            case "OR":
                return 18;
            case ";":
                return 19;
            case "break":
                return 20;
            case "continue":
                return 21;
            case "\"":
                return 22;
            case "(":
                return 23;
            case ")":
                return 24;
            case "{":
                return 25;
            case "}":
                return 26;
            case "[":
                return 27;
            case "]":
                return 28;
            case ",":
                return 29;
            case "for":
                return 30;
            case "while":
                return 31;
            case "if":
                return 32;
            case "else":
                return 33;
            case "#int":
                return 34;
            case "#float":
                return 35;
            case "#double":
                return 36;
            case "#char":
                return 37;
            case "#string":
                return 38;
            case "write":
                return 39;
            case "writeln":
                return 40;
            case "read":
                return 41;
            case "charAt":
                return 42;
            default:
            	return 43; 
        }
    }

}
