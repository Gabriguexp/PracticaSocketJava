package chatserver;

import java.util.List;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;

public class ChatServer{
    private boolean run =true;
    private List<ChatServerThread> serverThreads = new ArrayList();
    private ServerSocket servicio;
    public ChatServer(int port) { // port = puerto desde el que llegan las peticiones
        try{
            servicio = new ServerSocket(port);
        }catch(IOException ex){
            System.out.println(ex.getLocalizedMessage());
        }
    }

    public void broadcast(String text){
        for(ChatServerThread client : serverThreads){
            client.send(text);
        }
        System.out.println(text);

    }

    
    public void startService(){
        Thread mainThread = new Thread(){
            public void run(){
                ChatServerThread serverThread;
                Socket servidor;
                while(run){
                    try {
                        servidor = servicio.accept();
                        serverThread  = new ChatServerThread(ChatServer.this, servidor);
                        serverThreads.add(serverThread);
                        serverThread.setId(serverThreads.indexOf(serverThread));
                        serverThread.start();
                    } catch (IOException ex) {
                        System.out.println("startService: "+ ex.getLocalizedMessage());
                    }
                }                
            }
            
        };
        mainThread.start();
                        

    }
    
    
    
    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer(5000);
        chatServer.startService();
    }

    public String listaUsuarios(){
        String s="";
        if(serverThreads.size()==1){
            return serverThreads.get(0).getNombre();
        }
        for(ChatServerThread user: serverThreads){
            s+= user.getNombre()+", ";
        }
        
        return s.substring(0, s.length()-2);
    }
    
    public void borrarUsuario(ChatServerThread serverthread){
        serverThreads.remove(serverthread);
    }

    void sendMP(String from, String usuario, String mensaje) {
        for(ChatServerThread user: serverThreads){
            if(user.getNombre().equals(usuario)){
                String s = "["+from+"]->me : "+mensaje;
                user.send(s);
            }
        }
    }
}
