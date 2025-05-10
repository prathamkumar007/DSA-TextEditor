// import java.util.Scanner;

// public class Main {
//     public static void main(String[] args) {
//         Scanner sc = new Scanner(System.in);
//         TextEditor editor = new TextEditor();

//         System.out.println("=== Console Java Text Editor ===");

//         while (true) {
//             System.out.println("\nMenu:");
//             System.out.println("1. Insert line");
//             System.out.println("2. Delete line");
//             System.out.println("3. Undo");
//             System.out.println("4. Redo");
//             System.out.println("5. Show document");
//             System.out.println("6. Exit");
//             System.out.print("Enter your choice: ");

//             int choice = sc.nextInt();
//             sc.nextLine();

//             switch (choice) {
//                 case 1:
//                     System.out.print("Enter line index (0-based): ");
//                     int idx = sc.nextInt();
//                     sc.nextLine();
//                     System.out.print("Enter text: ");
//                     String line = sc.nextLine();
//                     editor.insertLine(idx, line);
//                     break;
//                 case 2:
//                     System.out.print("Enter line index (0-based): ");
//                     int delIdx = sc.nextInt();
//                     editor.deleteLine(delIdx);
//                     break;
//                 case 3:
//                     editor.undo();
//                     break;
//                 case 4:
//                     editor.redo();
//                     break;
//                 case 5:
//                     editor.display();
//                     break;
//                 case 6:
//                     System.out.println("Exiting editor. Goodbye!");
//                     return;
//                 default:
//                     System.out.println("Invalid choice. Try again.");
//             }
//         }
//     }
// }
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TextEditor editor = new TextEditor();
        editor.loadFromDatabase();

        System.out.println("=== Console Java Text Editor ===");

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Insert line");
            System.out.println("2. Delete line");
            System.out.println("3. Undo");
            System.out.println("4. Redo");
            System.out.println("5. Show document");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter line number (1-based): ");
                    int idx = sc.nextInt() - 1;  // Convert to 0-based
                    sc.nextLine();
                    System.out.print("Enter text: ");
                    String line = sc.nextLine();
                    editor.insertLine(idx, line);
                    break;
                case 2:
                    System.out.print("Enter line number (1-based): ");
                    int delIdx = sc.nextInt() - 1;  // Convert to 0-based
                    editor.deleteLine(delIdx);
                    break;
                case 3:
                    editor.undo();
                    break;
                case 4:
                    editor.redo();
                    break;
                case 5:
                    editor.display();
                    break;
                case 6:
                    System.out.println("Exiting editor. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
