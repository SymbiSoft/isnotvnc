import java.io.IOException;

    public class InnerThread extends Thread {
    	private Communication comm=null;
    	private long sleep=500;
        private boolean run=true;

        
        InnerThread(Communication comm) {
          super();
          this.comm=comm;
          start();
        }

        public void setRun(boolean r) {
        	this.run=r;
        }
        
        public void run() {
          while (run) {
            try {
				comm.getGet();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
            
            try {
              sleep(sleep);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
          }
        }
      }