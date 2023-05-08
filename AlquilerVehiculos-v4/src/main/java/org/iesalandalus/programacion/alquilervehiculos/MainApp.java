package org.iesalandalus.programacion.alquilervehiculos;

import javax.naming.OperationNotSupportedException;

import org.iesalandalus.programacion.alquilervehiculos.controlador.Controlador;
import org.iesalandalus.programacion.alquilervehiculos.modelo.FactoriaFuenteDatos;
import org.iesalandalus.programacion.alquilervehiculos.modelo.Modelo;
import org.iesalandalus.programacion.alquilervehiculos.modelo.ModeloCascada;
import org.iesalandalus.programacion.alquilervehiculos.vista.Vista;
import org.iesalandalus.programacion.alquilervehiculos.vista.grafica.VistaGrafica;
import org.iesalandalus.programacion.alquilervehiculos.vista.texto.VistaTexto;


public class MainApp {

	/*public static void main(String[] args) {
		Modelo modelo=new ModeloCascada(FactoriaFuenteDatos.FICHERO);
		VistaGrafica vista=new VistaGrafica();
		Controlador controlador=new Controlador(modelo,vista);
		controlador.comenzar();
	
	}*/
	
	public static void main(String[] args) {
		Modelo modelo=procesarArgumentosFuenteDatos(args);
		Vista vista=procesarArgumentosVista(args);
		Controlador controlador=new Controlador(modelo,vista);
		try {
			controlador.comenzar();
		} catch (OperationNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	private static Vista procesarArgumentosVista(String[] args) {
		Vista vista = new VistaGrafica();
		
		for (String argumento : args) {
			if (argumento.equalsIgnoreCase("-vgrafica")) 
			{
				vista = new VistaGrafica();
			} 
			else if (argumento.equalsIgnoreCase("-vtexto")) 
			{
				vista = new VistaTexto();
			}
		}
		return vista;
	}
	
	private static Modelo procesarArgumentosFuenteDatos(String[] args) {
		Modelo modelo = new ModeloCascada(FactoriaFuenteDatos.FICHEROS);
		
		for (String argumento : args) {
			if (argumento.equalsIgnoreCase("-fdficheros")) 
			{
				modelo = new ModeloCascada(FactoriaFuenteDatos.FICHEROS);
			}
			if (argumento.equalsIgnoreCase("-fdmemoria")) 
			{
				modelo = new ModeloCascada(FactoriaFuenteDatos.MEMORIA);
			}
			/*else if (argumento.equalsIgnoreCase("-fdmongodb")) 
			{
				modelo = new ModeloCascada(FactoriaFuenteDatos.MONGODB);
			} */
			else if (argumento.equalsIgnoreCase("-fdmysql")) 
			{
				modelo = new ModeloCascada(FactoriaFuenteDatos.MYSQL);
			} /*
			else if (argumento.equalsIgnoreCase("-fdmysqlxdevapi")) 
			{
				modelo = new ModeloCascada(FactoriaFuenteDatos.MYSQL_XDEVAPI);
			} */
		}
		return modelo;
	}
	
	

}
