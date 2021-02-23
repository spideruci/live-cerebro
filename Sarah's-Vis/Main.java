public class Main {
    public static void main(String args[]) {
        try{
            String inputJson = args[0];
            new LiveCerebro(inputJson);
        } catch(Exception writEx){
            System.out.println("Writer exception");
        }
    }
}