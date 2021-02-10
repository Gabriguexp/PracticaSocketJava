package chatserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ChatServerThread extends Thread{
    private boolean run = true;
    private int id;
    private final Socket servidor;
    private DataInputStream flujoE;
    private DataOutputStream flujoS;
    private ChatServer server;
    private String nombre = "";
    
    
    public ChatServerThread(ChatServer server, Socket servidor){
        this.server = server;
        this.servidor = servidor;
        try{
            flujoE = new DataInputStream(servidor.getInputStream());
            flujoS = new DataOutputStream(servidor.getOutputStream());
            
        }catch(IOException ex){
            System.out.println("Constructor: "+ ex.getLocalizedMessage());
        }
    }
    
    @Override
    public void run(){
        String text;
        while(run){
            
            try{
                text = flujoE.readUTF();
                if(text.startsWith("[codigo:__002]:")){
                     borrarUsuario(this);
                     server.broadcast(this.getNombre()+" ha abandonado el chat");
                     server.broadcast("[codigo:__001]:"+listaUsuarios());
                }
               
                
                    
                    
                else if(nombre.equals("")){
                    this.setNombre(text);
                    server.broadcast(nombre + " Se ha unido al chat");
                    server.broadcast("[codigo:__001]:"+listaUsuarios());
                }else if(text.startsWith("/mp ")){
                    String[] split = text.split(" ");
                    String usuario = split[1];
                    String mensaje ="";
                    for(int i = 2; i < split.length; i++){
                        mensaje+= split[i]+" ";
                    }
                    server.sendMP(nombre, usuario, mensaje );
                    this.send("["+nombre+"]->"+usuario+" :"+mensaje );
                }else{   
                    server.broadcast(nombre+"> "+text);
                }
                
                
                //flujoS.writeUTF(id +"> "+text);
                //flujoS.flush();
            }catch(IOException ex){
                System.out.println("Run: "+ ex.getLocalizedMessage());
            }
        }
    }

    void setId(int id) {
        this.id = id;
    }
    
    public void send(String text){
        try{
            flujoS.writeUTF(text);
            flujoS.flush();
        }catch(IOException ex){
            System.out.println("send: "+ ex.getLocalizedMessage());
        }
    }

    private void setNombre(String text) {
        this.nombre = text;
    }

    public String getNombre() {
        return nombre;
    }
    
    
    
    // PRUEBAS
    public String listaUsuarios() {
        return server.listaUsuarios();
    }

    public void borrarUsuario(ChatServerThread serverthread) {
        server.borrarUsuario(serverthread);
    }

    


    
    
    
}


