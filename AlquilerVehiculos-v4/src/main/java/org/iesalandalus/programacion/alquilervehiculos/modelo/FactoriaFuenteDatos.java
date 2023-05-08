package org.iesalandalus.programacion.alquilervehiculos.modelo;

import org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.IFuenteDatos;
import org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.ficheros.FuenteDatosFicheros;
import org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.memoria.FuenteDatosMemoria;
import org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.mysql.FuenteDatosMySQL;

public enum FactoriaFuenteDatos {

	MEMORIA{
		@Override
		public IFuenteDatos crear() {
			return new FuenteDatosMemoria();
		}
	},
	
	FICHEROS{
		@Override
		public IFuenteDatos crear() {
			return new FuenteDatosFicheros();
		}
	},
	
	MYSQL{
		@Override
		public IFuenteDatos crear() {
			return new FuenteDatosMySQL();
		}
	};
	
	abstract IFuenteDatos crear();
}
