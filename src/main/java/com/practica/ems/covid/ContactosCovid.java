package com.practica.ems.covid;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;

import com.practica.excecption.EmsDuplicateLocationException;
import com.practica.excecption.EmsDuplicatePersonException;
import com.practica.excecption.EmsInvalidNumberOfDataException;
import com.practica.excecption.EmsInvalidTypeException;
import com.practica.excecption.EmsLocalizationNotFoundException;
import com.practica.excecption.EmsPersonNotFoundException;
import com.practica.genericas.Constantes;
import com.practica.genericas.Coordenada;
import com.practica.genericas.Persona;
import com.practica.genericas.PosicionPersona;
import com.practica.lista.ListaContactos;

import static com.practica.genericas.FechaHora.parsearFecha;

public class ContactosCovid {
    private Poblacion poblacion;
    private Localizacion localizacion;
    private ListaContactos listaContactos;

    public ContactosCovid() {
        reset();
    }

    public Poblacion getPoblacion() {
        return poblacion;
    }

    public Localizacion getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(Localizacion localizacion) {
        this.localizacion = localizacion;
    }

    public ListaContactos getListaContactos() {
        return listaContactos;
    }

    public void loadData(String data, boolean reset) throws EmsInvalidTypeException, EmsInvalidNumberOfDataException,
            EmsDuplicatePersonException, EmsDuplicateLocationException {
        // borro información anterior
        if (reset) {
            reset();
        }
        String datas[] = dividirEntrada(data);
        anadirPersona(datas);
    }

    private void reset() {
        this.poblacion = new Poblacion();
        this.localizacion = new Localizacion();
        this.listaContactos = new ListaContactos();
    }

    private void anadirPersona(String[] datas) throws EmsInvalidTypeException, EmsInvalidNumberOfDataException, EmsDuplicatePersonException, EmsDuplicateLocationException {
        for (String linea : datas) {
            String[] datos = this.dividirLineaData(linea);
            switch (datos[0]) {
                case "PERSONA":
                    procesarPersona(datos);
                    break;
                case "LOCALIZACION":
                    procesarLocalizacion(datos);
                    break;
                default:
                    throw new EmsInvalidTypeException();
            }
        }
    }

    private void procesarPersona(String[] datos) throws EmsInvalidNumberOfDataException, EmsDuplicatePersonException {
        if (datos.length != Constantes.MAX_DATOS_PERSONA) {
            throw new EmsInvalidNumberOfDataException("El número de datos para PERSONA es menor de 8");
        }
        this.poblacion.addPersona(this.crearPersona(datos));
    }

    private void procesarLocalizacion(String[] datos) throws EmsInvalidNumberOfDataException, EmsDuplicateLocationException {
        if (datos.length != Constantes.MAX_DATOS_LOCALIZACION) {
            throw new EmsInvalidNumberOfDataException("El número de datos para LOCALIZACION es menor de 6");
        }
        PosicionPersona pp = this.crearPosicionPersona(datos);
        this.localizacion.addLocalizacion(pp);
        this.listaContactos.insertarNodoTemporal(pp);
    }

    public void loadDataFile(String fichero, boolean reset) throws Exception {
        if (reset) {
            reset();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(fichero))) {
            String data;
            while ((data = br.readLine()) != null) {
                String[] datas = dividirEntrada(data.trim());
                anadirPersona(datas);
            }
        }
    }

    public int findPersona(String documento) throws EmsPersonNotFoundException {
        int pos;
        try {
            pos = this.poblacion.findPersona(documento);
            return pos;
        } catch (EmsPersonNotFoundException e) {
            throw new EmsPersonNotFoundException();
        }
    }

    public int findLocalizacion(String documento, String fecha, String hora) throws EmsLocalizationNotFoundException {
        int pos;
        try {
            pos = localizacion.findLocalizacion(documento, fecha, hora);
            return pos;
        } catch (EmsLocalizationNotFoundException e) {
            throw new EmsLocalizationNotFoundException();
        }
    }

    public List<PosicionPersona> localizacionPersona(String documento) throws EmsPersonNotFoundException {
        List<PosicionPersona> lista = this.localizacion.getLista().stream()
                .filter(pp -> pp.getDocumento().equals(documento))
                .collect(Collectors.toList());
        if (lista.isEmpty()) {
            throw new EmsPersonNotFoundException();
        }
        return lista;
    }

    public boolean delPersona(String documento) throws EmsPersonNotFoundException {
        boolean fueEliminada = this.poblacion.getLista().removeIf(persona -> persona.getDocumento().equals(documento));
        if (!fueEliminada) {
            throw new EmsPersonNotFoundException();
        }
        return false;
    }

    private String[] dividirEntrada(String input) {
        return input.split("\\n");
    }

    private String[] dividirLineaData(String data) {
        return data.split("\\;");
    }

    private Persona crearPersona(String[] data) {
        Persona persona = new Persona();
        persona.setDocumento(data[1]);
        persona.setNombre(data[2]);
        persona.setApellidos(data[3]);
        persona.setEmail(data[4]);
        persona.setDireccion(data[5]);
        persona.setCp(data[6]);
        persona.setFechaNacimiento(parsearFecha(data[7]));
        return persona;
    }

    private PosicionPersona crearPosicionPersona(String[] data) {
        PosicionPersona posicionPersona = new PosicionPersona();
        posicionPersona.setDocumento(data[1]);
        posicionPersona.setFechaPosicion(parsearFecha(data[2], data[3]));
        float latitud = Float.parseFloat(data[4]);
        float longitud = Float.parseFloat(data[5]);
        posicionPersona.setCoordenada(new Coordenada(latitud, longitud));
        return posicionPersona;
    }
}
