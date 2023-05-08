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

import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Cliente;
import org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.IClientes;
import org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.mysql.utilidades.MySQL;

public class Clientes implements IClientes{

	private static final String ER_NOMBRE = "([A-Z]{1}[a-z]+([ ][A-Z]{1}[a-z]+)*)";
	private static final String ER_TELEFONO = "([0-9]{9})";
	
	private Connection conexion = null;
	
	private static Clientes instancia;
	
	private Clientes() {
		
	}
	
	static Clientes getInstancia() {
		if (instancia==null)
			instancia=new Clientes();

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
	public List<Cliente> get() {
		List<Cliente> clientes = new ArrayList<>();
		
		try {
			String sentenciaStr = "select nombre, dni, telefono from clientes order by nombre";
			Statement sentencia = conexion.createStatement();
			ResultSet filas = sentencia.executeQuery(sentenciaStr);
			
			while(filas.next()) {
				String nombre = filas.getString(1);
				String dni = filas.getString(2);
				String telefono = filas.getString(3);
				
				Cliente cliente = new Cliente(nombre, dni, telefono);
				
				clientes.add(cliente);
			}
		}catch (SQLException e) {
			throw new IllegalArgumentException("ERROR:" + e.getMessage());
		}
		
		return clientes;
	}
	
	public int getCantidad() {
		int tamano = 0;
		
		try {
			String sentenciaStr = "select count(*) from clientes";
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
	public void insertar(Cliente cliente) throws OperationNotSupportedException {
		
		if (cliente == null) {
			throw new NullPointerException("ERROR: No se puede insertar un cliente nulo.");
		}
		
		try {
			String sentenciaStr = "insert into clientes values (?, ?, ?)";
			PreparedStatement sentencia = conexion.prepareStatement(sentenciaStr);
			sentencia.setString(2, cliente.getNombre());
			sentencia.setString(1, cliente.getDni());
			sentencia.setString(3, cliente.getTelefono());
			sentencia.executeUpdate();
		} 
		catch (SQLIntegrityConstraintViolationException e) {
			throw new OperationNotSupportedException("ERROR: Ya existe un cliente igual.");
		} 
		catch (SQLException e) {
			throw new OperationNotSupportedException("ERROR:" + e.getMessage());
		}
	}

	@Override
	public Cliente buscar(Cliente cliente) {
		Cliente clienteEncontrado = null;
		
		if (cliente == null) {
			throw new NullPointerException("ERROR: No se puede buscar un cliente nulo.");
		}
		
		try {
			String sentenciaStr = "select nombre, dni, telefono from clientes where dni = ?";
			PreparedStatement sentencia = conexion.prepareStatement(sentenciaStr);
			sentencia.setString(1, cliente.getDni());
			ResultSet filas = sentencia.executeQuery();
			if (filas.next()) {
				String nombre = filas.getString(1);
				String dni = filas.getString(2);
				String telefono = filas.getString(3);
				
				clienteEncontrado = new Cliente(nombre, dni, telefono);
			}
		}catch (SQLException e) {
			throw new IllegalArgumentException("ERROR:" + e.getMessage());
		}
		
		return clienteEncontrado;
	}

	@Override
	public void borrar(Cliente cliente) throws OperationNotSupportedException {
		if (cliente == null) {
			throw new NullPointerException("ERROR: No se puede borrar un cliente nulo.");
		}
		
		try {
			String sentenciaStr = "delete from clientes where dni = ?";
			PreparedStatement sentencia = conexion.prepareStatement(sentenciaStr);
			sentencia.setString(1, cliente.getDni());
			
			if (sentencia.executeUpdate() == 0) {
				throw new OperationNotSupportedException("ERROR: No existe ningún cliente con los datos indicados.");	
			}
		} catch (SQLException e) {
			throw new OperationNotSupportedException("ERROR:" + e.toString());
		}
	}

	@Override
	public void modificar(Cliente cliente, String nombre, String telefono) throws OperationNotSupportedException {
		if (cliente == null) {
			throw new NullPointerException("ERROR: No se puede modificar un cliente nulo.");
		}
		
		if (!nombre.matches(ER_NOMBRE)) {
			throw new IllegalArgumentException("ERROR: El nombre no tiene un formato válido.");
		}

		if (!telefono.matches(ER_TELEFONO)) {
			throw new IllegalArgumentException("ERROR: El teléfono no tiene un formato válido.");
		}
		
		try {
			String sentenciaStr = "update clientes set nombre='" + nombre + "', telefono='" + telefono + "'where dni=?";
			PreparedStatement sentencia = conexion.prepareStatement(sentenciaStr);
			sentencia.setString(1, cliente.getDni());
			
			if(sentencia.executeUpdate() == 0) {
				throw new OperationNotSupportedException("ERROR: No existe ningún cliente con los datos indicados.");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}
