import java.io.IOException;
import java.util.*;

public class GranKWA {

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        int option;
        
        do{
            System.out.println("\n ======= MENU =======");
            System.out.println("1. Assembler");
            System.out.println("2. Disassembler");
            System.out.println("3. Virtual Machine");
            System.out.println("4. Compiler");
            System.out.println("5. Checador");
            System.out.println("6. Exit");
            System.out.println("====================");
            System.out.print("Choose Option: ");
            option = Integer.parseInt(sc.next());
            
            switch (option){
            case 1: 
                assembler test1 = new assembler();
                test1.mainAssembler(args);
                break;
            case 2:
                disassembler test2 = new disassembler();
                test2.mainDisassembler(args);
                break;
            case 3:
                virtualMachine test3 = new virtualMachine();
                test3.mainVirtualMachine(args);
                break;
            case 4:
                Compiler test4 = new Compiler();
                test4.mainCompiler(args);
                break;
            case 5:
                checadorKWA test5 = new checadorKWA();
                test5.mainChecador(args);
                break;
            case 6:
                break;
            default: 
                System.out.println("Invalid Option. Please type in a number 1-5.");
                break;
            }
        } while(option>0 & option<6);
    }
    
}
