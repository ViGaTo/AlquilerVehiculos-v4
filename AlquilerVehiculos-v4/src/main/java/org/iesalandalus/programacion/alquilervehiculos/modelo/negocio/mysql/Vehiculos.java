package org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Autobus;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Furgoneta;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Turismo;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Vehiculo;
import org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.IVehiculos;
import org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.mysql.utilidades.MySQL;

public class Vehiculos implements IVehiculos{

	private static final String TURISMO = "turismo";
	private static final String AUTOBUS = "autobus";
	private static final String FURGONETA = "furgoneta";
	
	private Connection conexion = null;
	
	private static Vehiculos instancia;
	
	private Vehiculos() {
		
	}
	
	static Vehiculos getInstancia() {
		if (instancia==null)
			instancia=new Vehiculos();

		return instancia;
	}

	@Override
	public void comenzar() throws OperationNotSupportedException {
		conexion = MySQL.establecerConexion();
	}

	@Override
	public void terminar() {
		MySQL.cerrarConexion();
	}

	@Override
	public List<Vehiculo> get() {
		List<Vehiculo> vehiculos = new ArrayList<>();
		
		Vehiculo vehiculo = null;
		
		try {
			String sentenciaStr = "select marca, modelo, tipo, cilindrada, plazas, pma, matricula from vehiculos order by modelo";
			Statement sentencia = conexion.createStatement();
			ResultSet filas = sentencia.executeQuery(sentenciaStr);
			
			while(filas.next()) {
				String marca = filas.getString(1);
				String modelo = filas.getString(2);
				String tipo = filas.getString(3);
				int cilindrada = filas.getInt(4);
				int plazas = filas.getInt(5);
				int pma = filas.getInt(6);
				String matricula = filas.getString(7);
				
				if(tipo.equalsIgnoreCase(TURISMO)) {
					vehiculo = new Turismo(marca, modelo, cilindrada, matricula);
				}else if(tipo.equalsIgnoreCase(AUTOBUS)) {
					vehiculo = new Autobus(marca, modelo, plazas, matricula);
				}else if(tipo.equalsIgnoreCase(FURGONETA)) {
					vehiculo = new Furgoneta(marca, modelo, plazas, pma, matricula);
				}
				
				vehiculos.add(vehiculo);
			}
		}catch (SQLException e) {
			throw new IllegalArgumentException("ERROR:" + e.getMessage());
		}
		
		return vehiculos;
	}

	public int getCantidad() {
		int tamano = 0;
		
		try {
			String sentenciaStr = "select count(*) from vehiculos";
			Statement sentencia = conexion.createStatement();
			ResultSet filas = sentencia.executeQuery(sentenciaStr);
			if (filas.next()) {
				tamano = filas.getInt(1);
			}
		} 
		catch (SQLException e) {
			throw new IllegalArgumentException("ERROR:" + e.getMessage());
		}
		
		return tamano;		
	}
	
	@Override
	public void insertar(Vehiculo vehiculo) throws OperationNotSupportedException {
		
		if (vehiculo == null) {
			throw new NullPointerException("ERROR: No se puede insertar un vehículo nulo.");
		}
		
		try {
			String sentenciaStr = "insert into vehiculos values (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement sentencia = conexion.prepareStatement(sentenciaStr);
			
			if(vehiculo instanceof Turismo) {
				sentencia.setString(3, vehiculo.getMarca());
				sentencia.setString(2, vehiculo.getModelo());
				sentencia.setString(4, TURISMO);
				sentencia.setInt(5, ((Turismo) vehiculo).getCilindrada());
				sentencia.setInt(6, 0);
				sentencia.setInt(7, 0);
				sentencia.setString(1, vehiculo.getMatricula());
			}else if(vehiculo instanceof Autobus) {
				sentencia.setString(3, vehiculo.getMarca());
				sentencia.setString(2, vehiculo.getModelo());
				sentencia.setString(4, AUTOBUS);
				sentencia.setInt(5, 0);
				sentencia.setInt(6, ((Autobus) vehiculo).getPlazas());
				sentencia.setInt(1, 0);
				sentencia.setString(7, vehiculo.getMatricula());
			}else if(vehiculo instanceof Furgoneta) {
				sentencia.setString(3, vehiculo.getMarca());
				sentencia.setString(2, vehiculo.getModelo());
				sentencia.setString(4, FURGONETA);
				sentencia.setInt(5, 0);
				sentencia.setInt(6, ((Furgoneta) vehiculo).getPlazas());
				sentencia.setInt(7, ((Furgoneta) vehiculo).getPma());
				sentencia.setString(1, vehiculo.getMatricula());
			}
			
			sentencia.executeUpdate();
		} 
		catch (SQLIntegrityConstraintViolationException e) {
			throw new OperationNotSupportedException("ERROR: Ya existe un vehículo igual.");
		} 
		catch (SQLException e) {
			throw new OperationNotSupportedException("ERROR:" + e.getMessage());
		}
	}

	@Override
	public Vehiculo buscar(Vehiculo vehiculo) {
		Vehiculo vehiculoEncontrado = null;
		
		if (vehiculo == null) {
			throw new NullPointerException("ERROR: No se puede buscar un vehículo nulo.");
		}
		
		try {
			String sentenciaStr = "select marca, modelo, tipo, cilindrada, plazas, pma, matricula from vehiculos where matricula=?";
			PreparedStatement sentencia = conexion.prepareStatement(sentenciaStr);
			sentencia.setString(1, vehiculo.getMatricula());
			ResultSet filas = sentencia.executeQuery();
			
			while(filas.next()) {
				String marca = filas.getString(1);
				String modelo = filas.getString(2);
				String tipo = filas.getString(3);
				int cilindrada = filas.getInt(4);
				int plazas = filas.getInt(5);
				int pma = filas.getInt(6);
				String matricula = filas.getString(7);
				
				if(tipo.equalsIgnoreCase(TURISMO)) {
					vehiculoEncontrado = new Turismo(marca, modelo, cilindrada, matricula);
				}else if(tipo.equalsIgnoreCase(AUTOBUS)) {
					vehiculoEncontrado = new Autobus(marca, modelo, plazas, matricula);
				}else if(tipo.equalsIgnoreCase(FURGONETA)) {
					vehiculoEncontrado = new Furgoneta(marca, modelo, plazas, pma, matricula);
				}
			}
		}catch (SQLException e) {
			throw new IllegalArgumentException("ERROR:" + e.getMessage());
		}
		
		return vehiculoEncontrado;
	}

	@Override
	public void borrar(Vehiculo vehiculo) throws OperationNotSupportedException {
		if (vehiculo == null) {
			throw new NullPointerException("ERROR: No se puede borrar un vehículo nulo.");
		}
		
		try {
			String sentenciaStr = "delete from vehiculos where matricula=?";
			PreparedStatement sentencia = conexion.prepareStatement(sentenciaStr);
			sentencia.setString(1, vehiculo.getMatricula());
			
			if (sentencia.executeUpdate() == 0) {
				throw new OperationNotSupportedException("ERROR: No existe ningún vehículo con los datos indicados.");	
			}
		} catch (SQLException e) {
			throw new OperationNotSupportedException("ERROR:" + e.toString());
		}
	}
}
