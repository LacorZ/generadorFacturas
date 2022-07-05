package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public class GeneradorMain {
	static String pathReport = null;
	static String pathResultado = null;
	static String pathExcel = null;
	static String [] nombreHojasExcel = {"ENERO","FEBRERO","MARZO","ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"};
	static String pathParametros;
	static String pathExcel1;
	static Date fechaGeneracion = new Date();

	public static void main(String[] args) {

		try {
			inicializarParametros(args);
			ArrayList<Llamada> obtenerDatosDelExcel = obtenerDatosDelExcel();
			ArrayList<String> listaCarpetasComprimir = generarFacturas(obtenerDatosDelExcel);
			comprimirFacturas(listaCarpetasComprimir);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void comprimirFacturas(ArrayList<String> listaCarpetasComprimir) throws IOException {
		for (String path : listaCarpetasComprimir) {
			pack(path, path+".zip");
		}
		
	}
	
	public static void pack(String sourceDirPath, String zipFilePath) throws IOException {
	    Path p = Files.createFile(Paths.get(zipFilePath));
	    try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
	        Path pp = Paths.get(sourceDirPath);
	        Files.walk(pp)
	          .filter(path -> !Files.isDirectory(path))
	          .forEach(path -> {
	              ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
	              try {
	                  zs.putNextEntry(zipEntry);
	                  Files.copy(path, zs);
	                  zs.closeEntry();
	            } catch (IOException e) {
	                System.err.println(e);
	            }
	          });
	    }
	}

	private static ArrayList<String> generarFacturas(ArrayList<Llamada> obtenerDatosDelExcel) throws JRException, IOException {
		ArrayList<String> listaFolders = new ArrayList<>();
		JasperReport report = JasperCompileManager.compileReport(pathReport);
		for (Llamada llamada : obtenerDatosDelExcel) {
			System.out.println(llamada);
			JasperPrint print = JasperFillManager.fillReport(report, generarParametrosPorLlamada(llamada), new JREmptyDataSource());
			String anio = llamada.getFecha_Y_Hora().subSequence(llamada.getFecha_Y_Hora().length()-4, llamada.getFecha_Y_Hora().length())+"";
			int numTrimestre =1;
		    Integer numMes = Integer.valueOf(llamada.getFecha_Y_Hora().split("/")[1]);
			switch (numMes) {
			case 1:
				numTrimestre = 1;
				break;
			case 2:
				numTrimestre = 1;			
				break;
			case 3:
				numTrimestre = 1;
				break;
			case 4:
				numTrimestre = 2;
				break;
			case 5:
				numTrimestre = 2;
				break;
			case 6:
				numTrimestre = 2;
				break;
			case 7:
				numTrimestre = 3;
				break;
			case 8:
				numTrimestre = 3;
				break;
			case 9:
				numTrimestre = 3;
				break;
			case 10:
				numTrimestre = 4;
				break;
			case 11:
				numTrimestre = 4;
				break;
			case 12:
				numTrimestre = 4;
				break;

			default:
				throw new RuntimeException("Numero de mes no esperado: "+numMes);
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			
			String trimestreFolder = pathResultado+"\\resultado\\"+sdf.format(fechaGeneracion)+"\\"+anio+"\\trimestre_"+numTrimestre;
			if(!listaFolders.contains(trimestreFolder)) {
				listaFolders.add(trimestreFolder);
			}
			String facturaPath =trimestreFolder+"\\factura_"+anio+"_"+llamada.getNumFactura()+".pdf";
			File yourFile = new File(facturaPath);
			yourFile.getParentFile().mkdirs();
			yourFile.createNewFile(); // if file already exists will do nothing 
			JasperExportManager.exportReportToPdfFile(print, facturaPath);
		}
//		JasperViewer.viewReport(print, false);
		return listaFolders;
	}

	private static ArrayList<Llamada> obtenerDatosDelExcel() {
		ArrayList<Llamada> llamadas = new ArrayList<>();
		XSSFWorkbook workbook;
		try {
			workbook = new XSSFWorkbook(new FileInputStream(pathExcel));
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				llamadas.addAll(obtenerDatosDeHojaExcel( workbook,i ))	;
			}
//			for (Llamada llamada : llamadas) {
//				System.out.println(llamada);	
//			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return llamadas;
	}

	private static ArrayList<Llamada> obtenerDatosDeHojaExcel( XSSFWorkbook workbook, int numeroHoja) {
		ArrayList<Llamada> llamadas = new ArrayList<>();
		int cols = 0; // No of columns
	    int tmp = 0;
	    XSSFCell cell;
		XSSFSheet sheet = workbook.getSheetAt(numeroHoja);
		if(sheet != null && sheet.getRow(0) != null && sheet.getRow(0).getCell(0) != null ) {
			boolean stringCellValueIsCliente = false;
			try {
				stringCellValueIsCliente = sheet.getRow(0).getCell(0).getStringCellValue().equals("CLIENTE");
			} catch (Exception e1) {
				System.err.println(e1);
			}
			if(stringCellValueIsCliente) {
				int rows = sheet.getPhysicalNumberOfRows();
				XSSFRow row;
				// This trick ensures that we get the data properly even if it doesn't start from first few rows
				for(int i = 0; i < 10 || i < rows; i++) {
					row = sheet.getRow(i);
					if(row != null) {
						tmp = sheet.getRow(i).getPhysicalNumberOfCells();
						if(tmp > cols) cols = tmp;
					}
				}
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				for(int r = 1; r < rows; r++) {
					row = sheet.getRow(r);
					if(row != null) {
						try {
							short c = 0;
							if(row.getCell(c) != null && row.getCell(c).getStringCellValue() != null) {
								String Cliente = row.getCell(c++).getStringCellValue();
								String Fecha_Y_Hora = sdf.format(row.getCell(c++).getDateCellValue());
								String Tiempo = row.getCell(c++).getNumericCellValue()+"";
								double FacturadoD = row.getCell(c++).getNumericCellValue();
								String Medio_De_Pago = row.getCell(c++).getStringCellValue();
								if(Medio_De_Pago.toUpperCase().contains("BIZUM")) {
									Medio_De_Pago = "bizum";
								} else if(Medio_De_Pago.toUpperCase().contains("TARJETA")) {
									Medio_De_Pago = "tarjeta";
								} else if(Medio_De_Pago.toUpperCase().contains("INGRE")) {
									Medio_De_Pago = "ingreso en banco";
								}
								Medio_De_Pago = "Pago mediante "+Medio_De_Pago;
								double IvaD = FacturadoD*0.21d;
								c++;
								String Tpv = row.getCell(c++).getNumericCellValue()+"";
								String Linea = null;
								String Metodo=null;
								XSSFCell cell2 = row.getCell(c++);
								if(cell2 != null ) {
									try {
										Linea = cell2.getNumericCellValue()+"";		
										Metodo = "Tarjeta de debito/credito";
									} catch (Exception e) {
										Metodo = "Bizum";
									}
								}else {
									Metodo = "Bizum";
								}
								String Liquido = row.getCell(c++).getNumericCellValue()+"";
								double BaseD = FacturadoD-IvaD;
								short s = c++;
								String NumFactura;
								try {
									NumFactura = row.getCell(s) != null ? row.getCell(s).getNumericCellValue()+"" : null;
								} catch (Exception e) {
									NumFactura = row.getCell(s) != null ? row.getCell(s).getStringCellValue() : null;
									if(NumFactura != null && NumFactura.contains("factura")) {
										NumFactura = NumFactura.split(" ")[1];
									} 
								}		
								s = c++;
								String concepto = row.getCell(s) != null ? row.getCell(s).getStringCellValue()+"" : "";
								if(StringUtils.isEmpty(concepto)) {
									concepto = "Sesión de tarot telefónica";
								} else {
									concepto = "Curso de  tarot online";
								}
								Llamada llamada = new Llamada(Cliente,
										Fecha_Y_Hora, Tiempo, FacturadoD+"",
										Medio_De_Pago, IvaD+"", Tpv, "", Liquido,NumFactura,BaseD+"",concepto);
								try {
									Integer.valueOf(NumFactura);
									if(NumFactura != null && NumFactura != "") {	                		
										llamadas.add(llamada);
									}
								}catch (Exception e) {
									// TODO: handle exception
								}
//							}
								
//	                	System.out.println(llamada);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	    return llamadas;
	}

	private static Map<String, Object> generarParametrosPorLlamada(Llamada llamada) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("Cliente", llamada.getCliente());
		parameters.put("Facturado", llamada.getFacturado());
		parameters.put("Fecha_Y_Hora", llamada.getFecha_Y_Hora());
		parameters.put("Iva", llamada.getIva());	
		parameters.put("Anio", llamada.getFecha_Y_Hora().subSequence(llamada.getFecha_Y_Hora().length()-4, llamada.getFecha_Y_Hora().length()));
		parameters.put("NumFactura", llamada.getNumFactura());
		parameters.put("Base", llamada.getBase());
		parameters.put("concepto", llamada.getConcepto());
		parameters.put("metodoPago", llamada.getMedio_De_Pago());
		
		
		return parameters;
	}

	private static void inicializarParametros(String[] args) throws URISyntaxException, IOException {
		int indiceArgumento = 0;
		String path = new File(GeneradorMain.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
		if("C:\\Users\\regular\\eclipse-workspace\\rts\\target\\classes".equalsIgnoreCase(path)) {
			path="C:\\Users\\regular\\eclipse-workspace\\rts\\distributable";
		}else {
			path = path.substring(0,path.indexOf("\\rts-"));
		}
		
//		"C:\\Repositories\\Git\\generador_facturas_tarot"
		pathResultado = incializarArgumento(args, indiceArgumento,  path);
		pathReport = pathResultado+"\\Blank_A4.jrxml";
		pathExcel1 = incializarArgumento(args, indiceArgumento, "C:\\Users\\regular\\Downloads\\excel mama mayo.xlsx");
		pathParametros = incializarArgumento(args, indiceArgumento, path+"\\parametros.txt");
		
		BufferedReader br = new BufferedReader(new FileReader(pathParametros));
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
//		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    pathExcel  = sb.toString();
		} finally {
		    br.close();
		}
		//C:\Users\regular\Downloads\excel mama mayo.xlsx
		//C:\Users\regular\Downloads\excel mama mayo.xlsx
//		System.out.println(pathExcel.equals(pathExcel1));
	}

	private static String incializarArgumento(String[] args, int indiceArgumento,  String valorPorDefecto) {		
		String variable = args.length > indiceArgumento && args[indiceArgumento] != null ? args[indiceArgumento]: valorPorDefecto;
		indiceArgumento++;
		return variable;
	}

}
