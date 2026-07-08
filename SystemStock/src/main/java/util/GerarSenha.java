package util;

import org.mindrot.jbcrypt.BCrypt;

public class GerarSenha {
    
    // Corrigido de 'Main' para 'main'
    public static void main(String[] args) {
        String senhapura = "123";
        
        // Gera o hash seguro usando BCrypt
        String senhaHash = BCrypt.hashpw(senhapura, BCrypt.gensalt());
        
        System.out.println(senhaHash);
    }
    
}