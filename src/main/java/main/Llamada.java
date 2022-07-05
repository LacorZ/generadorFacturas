package main;


public class Llamada {
	String Cliente;
	String Fecha_Y_Hora;
	String Tiempo;
	String Facturado;
	String Medio_De_Pago;
	String Iva;
	String Tpv;
	String Linea;
	String Liquido;
	String numFactura;
	String base;
	String concepto;

	public String getConcepto() {
		return concepto;
	}


	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}


	public Llamada(String cliente, String fecha_Y_Hora, String tiempo, String facturado, String medio_De_Pago,
			String iva, String tpv, String linea, String liquido, String numFactura, String BaseD, String concepto) {
		super();
		Cliente = cliente;
		Fecha_Y_Hora = fecha_Y_Hora;
		Tiempo = tiempo;
		Facturado = facturado;
		Medio_De_Pago = medio_De_Pago;
		Iva = iva;
		Tpv = tpv;
		Linea = linea;
		Liquido = liquido;
		this.numFactura = numFactura;
		this.base = BaseD;
	}


	@Override
	public String toString() {
		return "Llamada [Cliente=" + Cliente + ", Fecha_Y_Hora=" + Fecha_Y_Hora + ", Tiempo=" + Tiempo + ", Facturado="
				+ Facturado + ", Medio_De_Pago=" + Medio_De_Pago + ", Iva=" + Iva + ", Tpv=" + Tpv + ", Linea=" + Linea
				+ ", Liquido=" + Liquido + ", numFactura=" + numFactura + ", base=" + base + "]";
	}


	public String getBase() {
		return base;
	}


	public void setBase(String base) {
		this.base = base;
	}


	public String getCliente() {
		return Cliente;
	}

	public void setCliente(String cliente) {
		Cliente = cliente;
	}

	public String getFecha_Y_Hora() {
		return Fecha_Y_Hora;
	}

	public void setFecha_Y_Hora(String fecha_Y_Hora) {
		Fecha_Y_Hora = fecha_Y_Hora;
	}

	public String getTiempo() {
		return Tiempo;
	}

	public void setTiempo(String tiempo) {
		Tiempo = tiempo;
	}

	public String getFacturado() {
		return Facturado;
	}

	public void setFacturado(String facturado) {
		Facturado = facturado;
	}

	public String getMedio_De_Pago() {
		return Medio_De_Pago;
	}

	public void setMedio_De_Pago(String medio_De_Pago) {
		Medio_De_Pago = medio_De_Pago;
	}

	public String getIva() {
		return Iva;
	}

	public void setIva(String iva) {
		Iva = iva;
	}

	public String getTpv() {
		return Tpv;
	}

	public void setTpv(String tpv) {
		Tpv = tpv;
	}

	public String getNumFactura() {
		return numFactura;
	}

	public void setNumFactura(String numFactura) {
		this.numFactura = numFactura;
	}

	public String getLinea() {
		return Linea;
	}

	public void setLinea(String linea) {
		Linea = linea;
	}

	public String getLiquido() {
		return Liquido;
	}

	public void setLiquido(String liquido) {
		Liquido = liquido;
	}
}
