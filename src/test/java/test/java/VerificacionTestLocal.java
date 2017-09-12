package test.java;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import main.java.Authority;
import main.java.AuthorityImpl;
import main.java.CryptoRSA;
import main.java.VerificationException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
/*En esta clase estarÃ¡n implementados todos los mÃ©todos que ya han sido implementos en la clase VerificacionTest, pero 
en este caso, todas las pruebas se harÃ¡n con una base de datos local, ya que en la clase VerificacionTest los mÃ©todos
hacen uso de una base de datos remota. */
public class VerificacionTestLocal {
	
	private static Authority auth = new AuthorityImpl();
	
	private static List<String> idUtilizados = new ArrayList<String>();
	
	private static final Integer[] tokenAuxList = {234546, 134567, 183745, 123454, 345673, 123456, 
			435343, 678798, 675434, 564354, 120338, 127508, 219240, 231958, 
			264907, 301200, 301415, 318851, 328237, 333555, 366710, 376217, 382413, 
			406463, 409921, 436780, 458841, 461513, 530897, 589116, 590265, 590815, 
			593252, 656720, 746976, 830375, 865247, 869061, 885540, 907197, 909246, 
			961864, 976931, 982612};
	    
	private static Integer calculateToken(Integer votationId){
	
		Integer token = 0;
		
		checkId(votationId);
		
		String binaryInteger = Integer.toBinaryString(votationId);
		char[] numberByNumber = binaryInteger.toCharArray();
		
		int j = 0;
		for(int i=numberByNumber.length-1; 0 <= i; i--){
			String binDigit = Character.toString(numberByNumber[i]);
			Integer digit = new Integer(binDigit);
			if(digit > 0){
				token += digit*tokenAuxList[tokenAuxList.length-1-j];
				
			}
			j++;
		}
	
	return token*17;
	
	}  
	
	
	private static Connection getDatabaseConnection(){
		String USER = "root";
	    String PASS = "admin";  
	    String DB_URL = "jdbc:mysql://127.0.0.1:3306/verificacion";
	    
	    Connection conn = null;
	    
	    try{
	    	Class.forName("com.mysql.jdbc.Driver").newInstance();
	    	conn = DriverManager.getConnection(DB_URL, USER, PASS);
	    }catch (Exception e){
	    	e.printStackTrace();
	    }
	    
	    return conn;	    
	    
	}
	
	
	private static void checkId(Integer votationId) {
		assert votationId <= 999999998;
		
	}
	
	/*
	 *Cuando hacemos uso del mÃ©todo postKey obtenemos las claves publica y privada segÃºn el
	 *id de votaciÃ³n pasado.
	 */
	
	
	@Test
	public void test1PostKey1Local(){
		System.out.println("----------------PRUEBA TEST 1 POST KEY LOCAL 1-------------------------");
		String votationId;
		Integer token;
		boolean res;
		
		votationId = (new BigInteger(25, new SecureRandom())).toString();
		
		token = calculateToken(new Integer(votationId));
		
		res = auth.postKeyLocal(votationId, token);
		
		idUtilizados.add(votationId);
		
		System.out.println("ID Votacion: " + votationId);
		System.out.println("Token: " + token);
		System.out.println("Verificar que el postKey se hace correctamente: " + res);
		System.out.println("-----------------------------------------------------------------------\n\n");
		Assert.assertTrue(res == true);		
		
	}
	
	@Test(expected = VerificationException.class)
	public void test2PostKey2Local(){
		System.out.println("----------------PRUEBA TEST 2 POST KEY LOCAL 2-------------------------");
		String votationId;
		Integer token;
		boolean res;
		
		votationId = (new BigInteger(25, new SecureRandom())).toString();
		
		token = 123456789;
		System.out.println("ID Votacion: " + votationId);
		System.out.println("Token: " + token);
		System.out.println("El id votaciÃ³n no estÃ¡ icnluido entonces el metodo postKey da error.");
		System.out.println("-----------------------------------------------------------------------\n\n");
		res = auth.postKeyLocal(votationId, token);
		System.out.println(res);
				
		
	}
	
	@Test(expected = NumberFormatException.class)
	public void test3PostKey3Local(){
		System.out.println("----------------PRUEBA TEST 3 POST KEY LOCAL 3-------------------------");
		
		boolean res;
		
		String votationId =null;
		System.out.println("ID Votacion: " + votationId);
		System.out.println("Estamos pasando una votaciÃ³n nula, entonces no podemos crear el token");
		System.out.println("-----------------------------------------------------------------------\n\n");
		
		Integer token = calculateToken(new Integer(votationId));
		
		
		res = auth.postKeyLocal(votationId, token);
		
		idUtilizados.add(votationId);
		
		Assert.assertTrue(res == false);		
		
	}
	
	@Test(expected = VerificationException.class)
	public void test4EncryptDecryptTest1Local() throws Exception{
		System.out.println("----------------PRUEBA TEST 4 ENCRYPT DECRYPT KEY LOCAL 1--------------------\n");
		
		String votationId;
		String encrypText;
		Integer token2;
		String encriptado;
		
		votationId = (new BigInteger(25, new SecureRandom())).toString();
		token2 = 156464379;
		
		idUtilizados.add(votationId);
		
		encrypText = "prueba prueba";
		
		encriptado = auth.encrypt(votationId, encrypText, token2);
		System.out.println("Encriptado:" + encriptado.toString());
		
		
		String desencriptado;
		
		desencriptado = auth.decrypt(votationId, encriptado, token2);
		System.out.println("Desencriptado" + desencriptado);
		Assert.assertTrue(encrypText.equals(desencriptado));
		System.out.println("El token no coincicide, test4EncryptDecryptTest1Local");
		auth.postKeyLocal(votationId, token2);
	}
	
	@Test
	public void test5EncryptDecryptTest2Local() throws Exception{
		
		System.out.println("----------------PRUEBA TEST 5 ENCRYPT DECRYPT KEY LOCAL 2--------------------\n");
		
		String encrypText;
		String aux;
		String encriptado;
		
		String votationId = (new BigInteger(25, new SecureRandom())).toString();
		Integer token2 = calculateToken(new Integer(votationId));		
		
		idUtilizados.add(votationId);
		
		auth.postKeyLocal(votationId, token2);
		
		encrypText = "prueba prueba";
		
	
		encriptado = auth.encryptLocal(votationId, encrypText, token2);
		
		//---------------------------------
		System.out.println(encriptado);
		String desencriptado;
		System.out.println(encriptado.toString());
		aux=encriptado.toString();
		System.out.println(aux);
		desencriptado = auth.decryptLocal(votationId, encriptado, token2);
		System.out.println(desencriptado);
		Assert.assertTrue(encrypText.equals(desencriptado));
	}
	
	@Test(expected = VerificationException.class)
	public void test6EncryptDecryptTest2Local() throws Exception{
		
		System.out.println("----------------PRUEBA TEST 6 ENCRYPT DECRYPT KEY LOCAL 2--------------------\n");
		
		String votationId;
		String encrypText;
		Integer token2;
		String encriptado;
		
		votationId = (new BigInteger(25, new SecureRandom())).toString();
		token2 = calculateToken(new Integer(votationId));		
		
		idUtilizados.add(votationId);
		
		auth.postKeyLocal(votationId, token2);
		
		encrypText = "prueba prueba";
		
		encriptado = auth.encrypt(votationId, encrypText, token2);
		
		//---------------------------------
		
		String desencriptado;
		
		Integer token3 = 111111111;
		
		desencriptado = auth.decrypt(votationId, encriptado, token3);
		System.out.println(desencriptado);
		
		
	}
	
	@Test
	public void test7EncryptDecryptTest3Local() throws Exception{
		System.out.println("----------------PRUEBA TEST 7 ENCRYPT DECRYPT KEY LOCAL 3--------------------\n");
		
		String votationId;
		String encrypText;
		Integer token2;
		String encriptado;
		
		votationId = (new BigInteger(25, new SecureRandom())).toString();
		token2 = calculateToken(new Integer(votationId));		
		
		idUtilizados.add(votationId);
		
		auth.postKeyLocal(votationId, token2);
		
		encrypText = "";
		
		encriptado = auth.encryptLocal(votationId, encrypText, token2);
		
		//---------------------------------
		
		String desencriptado;
		
		desencriptado = auth.decryptLocal(votationId, encriptado, token2);
		System.out.println(desencriptado);
	}
	
	@Test
	public void test8DeleteEntriesInDatabaseLocal(){
		Integer res = 0;
		Connection conn = null;
		Statement stmt = null;
	    
		try {	
		
			for(String id: idUtilizados){
				conn = getDatabaseConnection();
				
				stmt = conn.createStatement();

				String sql = "DELETE FROM keysvotes " +
		                "WHERE idvotation="+id;
				
				PreparedStatement preparedStatement = conn.prepareStatement(sql);
		        int r = preparedStatement.executeUpdate();
		        
		        System.out.println(res);
		        
		        res = res + r;
		        
			}
		} catch(SQLException se) {
	        se.printStackTrace();
	    } catch(Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if(stmt != null)
	                
	            	conn.close();
	            	System.out.println("close");
	        
	        } catch(SQLException se) {
	        }
	        try {
	            if(conn != null)
	                
	            	conn.close();
	            	System.out.println("close");
	            	
	        } catch(SQLException se) {
	            se.printStackTrace();
	        }
	    }
		System.out.println(res);
		Assert.assertTrue(res == 5);
	

	}
	
	
	public void test9EncryptRSALocal() throws Exception{
		  KeyPair keypair;
		  String votationId;
			
			Integer token2;
			
			
			votationId = (new BigInteger(25, new SecureRandom())).toString();
			token2 = calculateToken(new Integer(votationId));		
			
    
		   String test = "Prueba";
		   System.out.println("Length of string to encrypt: " + test.length());
		  
		  byte[] data = test.getBytes("UTF-8");
		     System.out.println("Texto a encriptar: " + test);
		     System.out.println("Texto a encriptar (array): " + data);
		     System.out.println("Tamaño del texto (array): " + data.length);
		  
		     keypair = CryptoRSA.generateKeyPair();
		     Key publicKey = keypair.getPublic();
		     Key privateKey = keypair.getPrivate();
		     
		     System.out.println("Keypair generated......");
		  
		     byte[] encrypted = CryptoRSA.encryptLocal(votationId,data, publicKey,token2);
		     System.out.println("Texto encriptado (array): " + encrypted);
		     System.out.println("Texto encriptado: " + new String (encrypted));
		     System.out.println("Tamaño del texto (array): " + encrypted.length);
		   
		  
		     
		     byte[] decrypted = CryptoRSA.decryptLocal(votationId,encrypted, privateKey,token2);
		     System.out.println("Texto desencriptado (array): " + decrypted);
		     System.out.println("Texto desencriptado: " + new String (decrypted));
		     System.out.println("Tamaño del texto (array): " + decrypted.length);
		     
		   
		  
		     System.out.println("Original-data == decrypted data : " + Arrays.equals(data, decrypted));
		     String a = new String(decrypted);
		     Assert.assertTrue(test.equals(a));
	}
	
	@Test(expected = java.lang.AssertionError.class)
	public void test10EncryptRSAFalseLocal() throws Exception{
		  KeyPair keypair1;
		  KeyPair keypair2;
		  String votationId;
			
			Integer token2;
			
			
			votationId = (new BigInteger(25, new SecureRandom())).toString();
			token2 = calculateToken(new Integer(votationId));		
			
			idUtilizados.add(votationId);
			
			auth.postKeyLocal(votationId, token2);
		   String test = "Prueba";
		   System.out.println("Length of string to encrypt: " + test.length());
		  
		  byte[] data = test.getBytes("UTF-8");
		     System.out.println("Texto a encriptar: " + test);
		     System.out.println("Texto a encriptar (array): " + data);
		     System.out.println("Tamaño del texto (array): " + data.length);
		  
		     keypair1 = CryptoRSA.generateKeyPair();
		     keypair2 = CryptoRSA.generateKeyPair();
		     Key publicKey = keypair1.getPublic();
		     Key privateKey = keypair2.getPrivate();
		     
		     System.out.println("Keypair generated......");
		  
		     byte[] encrypted = CryptoRSA.encrypt(votationId,data, publicKey,token2);
		     System.out.println("Texto encriptado (array): " + encrypted);
		     System.out.println("Texto encriptado: " + new String (encrypted));
		     System.out.println("Tamaño del texto (array): " + encrypted.length);
		     
		     byte[] decrypted = CryptoRSA.decrypt(votationId,encrypted, privateKey,token2);
		     System.out.println("Texto desencriptado (array): " + decrypted);
		     System.out.println("Texto desencriptado: " + new String (decrypted));
		     System.out.println("Tamaño del texto (array): " + decrypted.length);
		     
		     System.out.println("Original-data == decrypted data : " + Arrays.equals(data, decrypted));
		     String a = new String(decrypted);
		     Assert.assertTrue(test.equals(a));
	}
	
	@Test
	public void testDefensaLocal(){
		System.out.println("----------------PRUEBA TEST Defensa -------------------------");
		String votationId;
		Integer token;
		boolean res;
		
		votationId = (new BigInteger(25, new SecureRandom())).toString();
		
		token = calculateToken(new Integer(votationId));
		
		res = auth.defensaLocal(votationId, token);
		
		idUtilizados.add(votationId);
		
		System.out.println("ID Votacion: " + votationId);
		System.out.println("Token: " + token);
		System.out.println("Verificar que el postKey se hace correctamente: " + res);
		System.out.println("-----------------------------------------------------------------------\n\n");
		Assert.assertTrue(res == true);		
		
	}
	
	@Test
	public void testDefensaSeptiembreLocal() throws Exception {
		List<String> result;
		String textoEncriptado;
		String textoDesencriptado;
		List<String> resultFinal = new ArrayList<String>();
		
		
		result=auth.encriptarYDesencriptar("texto de prueba");
		textoEncriptado = auth.Encriptar("texto de prueba");
		textoDesencriptado = auth.Desencriptar(textoEncriptado);
		resultFinal.add("texto de prueba");
		resultFinal.add(textoEncriptado);
		resultFinal.add(textoDesencriptado);
		
		Assert.assertEquals(result.get(0), resultFinal.get(0));
		Assert.assertEquals(result.get(1), resultFinal.get(1));
		Assert.assertEquals(result.get(2), resultFinal.get(2));	
	
		
	}
	
	
}
