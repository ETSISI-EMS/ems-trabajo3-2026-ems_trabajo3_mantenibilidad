package com.practica.ems.covid;
public class Principal {

	public static void main(String[] args) {
		ContactosCovid contactosCovid = new ContactosCovid();
		try {
			contactosCovid.loadDataFile("datos2.txt", false);
			System.out.println(contactosCovid.getLocalizacion().toString());
			System.out.println(contactosCovid.getPoblacion().toString());
			System.out.println(contactosCovid.getListaContactos().tamanioLista());
			System.out.println(contactosCovid.getListaContactos().getPrimerNodo());
		} catch (Exception e) {
			System.err.println("Error al cargar el archivo de datos: " + e.getMessage());
		}
		System.out.println(contactosCovid.getListaContactos());
	}
}
