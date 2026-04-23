package com.practica.lista;

import com.practica.genericas.Coordenada;
import com.practica.genericas.FechaHora;


/**
 * Nodo para guardar un instante de tiempo. Además guardamos una lista con las coordeandas
 * y las personas (solo número) que en ese instante están en una coordeanda en concreto  
 *
 */
public class NodoTemporal {
	private NodoPosicion listaCoordenadas;
	private FechaHora fecha;
	private NodoTemporal siguiente;
	public NodoTemporal() {
		super();
		siguiente = null;
		listaCoordenadas=null;	
	}
	public NodoPosicion getListaCoordenadas() {
		return listaCoordenadas;
	}
	public FechaHora getFecha() {
		return fecha;
	}
	public void setFecha(FechaHora fecha) {
		this.fecha = fecha;
	}
	public NodoTemporal getSiguiente() {
		return siguiente;
	}
	public void setSiguiente(NodoTemporal siguiente) {
		this.siguiente = siguiente;
	}
	public void insertarCoordenada(Coordenada coordenada){
		NodoPosicion npActual = this.listaCoordenadas;
		NodoPosicion npAnt=null;
		boolean npEncontrado = false;
		while (npActual!=null && !npEncontrado) {
			if(npActual.getCoordenada().equals(coordenada)) {
				npEncontrado=true;
				npActual.setNumPersonas(npActual.getNumPersonas()+1);
			}else {
				npAnt = npActual;
				npActual = npActual.getSiguiente();
			}
		}
		if(!npEncontrado) {
			NodoPosicion npNuevo = new NodoPosicion(coordenada,1, null);
			if(this.listaCoordenadas==null)
				this.listaCoordenadas = npNuevo;
			else
				npAnt.setSiguiente(npNuevo);
		}
	}
}
