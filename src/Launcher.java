
public class Launcher{

    public static void main(String[] args){

        final int NB_THREAD = 3;
        Process p1 = new Process("0",NB_THREAD);
        Process p2 = new Process("1",NB_THREAD);
        Process p3 = new Process("2",NB_THREAD);

        ProcessToken pT = new ProcessToken(NB_THREAD);


        try{
            Thread.sleep(2000);
        }catch(Exception e){
            e.printStackTrace();
        }

        p1.stop();
        p2.stop();
        p3.stop();
    }
}
