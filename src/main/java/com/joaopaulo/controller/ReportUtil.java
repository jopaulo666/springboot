package com.joaopaulo.controller;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component
public class ReportUtil  implements Serializable{

	// Retona PDF em byte para download
	public byte[] gerarRelatorio (List listDados, String relatorio, ServletContext servletContext) throws JRException {
		
		// cria a lista de dados para o relatório com a lista de objetos para imprimir
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listDados);
		
		// carrega o caminho do arquivo jasper compilado
		String caminhoJasper = servletContext.getRealPath("relatorios") + File.separator + relatorio + ".jasper";
		
		// carrega o arquivo jasper passando dados
		JasperPrint impressoraJasper = JasperFillManager.fillReport(caminhoJasper, new HashMap(), dataSource);
		
		//exporta oara byte[] para fazer o dawnload do PDF
		return JasperExportManager.exportReportToPdf(impressoraJasper);
	}
}
