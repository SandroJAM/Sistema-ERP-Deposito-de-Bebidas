package com.sandrojam.controlevendas.service;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import com.sandrojam.controlevendas.dto.ExtratoClienteDTO;
import com.sandrojam.controlevendas.dto.ItemLancamentoDTO;
import com.sandrojam.controlevendas.dto.LancamentoExtratoDTO;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Gera o extrato do cliente em PDF com a mesma cara de "bobina de máquina de calcular"
 * que aparece na tela: página estreita (~80mm, largura padrão de bobina/impressora térmica),
 * fonte monoespaçada, lançamentos em ordem cronológica com saldo acumulado.
 */
@Service
public class ExtratoPdfService {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yy");
    private static final Locale LOCALE_BR = Locale.forLanguageTag("pt-BR");
    private static final float LARGURA_BOBINA = 226f; // ~80mm

    private static final Color CINZA_CLARO = new Color(180, 180, 180);
    private static final Color CINZA = new Color(120, 120, 120);
    private static final Color VERDE_RECEBIMENTO = new Color(47, 125, 79);

    public byte[] gerar(ExtratoClienteDTO extrato, boolean somenteDevido) {
        boolean temEmpresa = extrato.getEmpresaNome() != null && !extrato.getEmpresaNome().isBlank();
        float alturaEstimada = (temEmpresa ? 210f : 170f) + extrato.getLancamentos().size() * 30f;
        Document documento = new Document(new Rectangle(LARGURA_BOBINA, Math.max(alturaEstimada, 260f)), 14, 14, 16, 16);

        try {
            ByteArrayOutputStream saida = new ByteArrayOutputStream();
            PdfWriter.getInstance(documento, saida);
            documento.open();

            Font fonteEmpresaNome = new Font(Font.COURIER, 9, Font.BOLD);
            Font fonteEmpresaDados = new Font(Font.COURIER, 6.5f, Font.NORMAL, CINZA);
            Font fonteTitulo = new Font(Font.COURIER, 11, Font.BOLD);
            Font fonteSubtitulo = new Font(Font.COURIER, 7, Font.NORMAL, CINZA);
            Font fonteTexto = new Font(Font.COURIER, 8, Font.NORMAL);
            Font fonteTextoRecebimento = new Font(Font.COURIER, 8, Font.NORMAL, VERDE_RECEBIMENTO);
            Font fonteSaldo = new Font(Font.COURIER, 7, Font.NORMAL, CINZA);
            Font fonteTotal = new Font(Font.COURIER, 10, Font.BOLD);
            Font fonteTracejado = new Font(Font.COURIER, 8, Font.NORMAL, CINZA_CLARO);
            Font fonteItem = new Font(Font.COURIER, 7, Font.NORMAL, CINZA);

            if (temEmpresa) {
                Paragraph nomeEmpresa = new Paragraph(extrato.getEmpresaNome(), fonteEmpresaNome);
                nomeEmpresa.setAlignment(Element.ALIGN_CENTER);
                documento.add(nomeEmpresa);

                if (extrato.getEmpresaEndereco() != null && !extrato.getEmpresaEndereco().isBlank()) {
                    Paragraph endereco = new Paragraph(extrato.getEmpresaEndereco(), fonteEmpresaDados);
                    endereco.setAlignment(Element.ALIGN_CENTER);
                    documento.add(endereco);
                }
                if (extrato.getEmpresaTelefone() != null && !extrato.getEmpresaTelefone().isBlank()) {
                    Paragraph telefone = new Paragraph(extrato.getEmpresaTelefone(), fonteEmpresaDados);
                    telefone.setAlignment(Element.ALIGN_CENTER);
                    telefone.setSpacingAfter(5f);
                    documento.add(telefone);
                }

                documento.add(linhaTracejada(fonteTracejado));
            }

            Paragraph titulo = new Paragraph(extrato.getClienteNome(), fonteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);

            Paragraph subtitulo = new Paragraph(
                    somenteDevido ? "TUDO QUE AINDA DEVE" : "HISTORICO DO PERIODO", fonteSubtitulo);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(6f);
            documento.add(subtitulo);

            documento.add(linhaTracejada(fonteTracejado));

            if (extrato.getLancamentos().isEmpty()) {
                Paragraph vazio = new Paragraph("Nenhum lançamento.", fonteSaldo);
                vazio.setAlignment(Element.ALIGN_CENTER);
                vazio.setSpacingBefore(6f);
                documento.add(vazio);
            }

            for (LancamentoExtratoDTO lancamento : extrato.getLancamentos()) {
                boolean recebimento = "RECEBIMENTO".equals(lancamento.getTipo());
                String sinal = lancamento.getValor().signum() >= 0 ? "+" : "-";
                BigDecimal valorAbsoluto = lancamento.getValor().abs();

                Paragraph linha = new Paragraph();
                linha.setFont(recebimento ? fonteTextoRecebimento : fonteTexto);
                linha.add(FORMATO_DATA.format(lancamento.getData()) + " " + lancamento.getDescricao());
                linha.add(Chunk.NEWLINE);
                linha.add(sinal + " R$ " + formatarValor(valorAbsoluto));
                linha.setSpacingAfter(1f);
                documento.add(linha);

                if (lancamento.getItens() != null) {
                    for (ItemLancamentoDTO item : lancamento.getItens()) {
                        Paragraph linhaItem = new Paragraph(
                                "  " + item.getQuantidade() + "x " + item.getProdutoNome()
                                        + " ... R$ " + formatarValor(item.getSubtotal()),
                                fonteItem);
                        linhaItem.setSpacingAfter(0.5f);
                        documento.add(linhaItem);
                    }
                }

                Paragraph saldo = new Paragraph("saldo: R$ " + formatarValor(lancamento.getSaldoAcumulado()), fonteSaldo);
                saldo.setAlignment(Element.ALIGN_RIGHT);
                saldo.setSpacingAfter(5f);
                documento.add(saldo);
            }

            documento.add(linhaTracejada(fonteTracejado));

            Paragraph total = new Paragraph(
                    "SALDO DEVEDOR: R$ " + formatarValor(extrato.getTotalDevidoGeral()), fonteTotal);
            total.setSpacingBefore(4f);
            documento.add(total);

            documento.close();
            return saida.toByteArray();
        } catch (DocumentException e) {
            throw new IllegalStateException("Não foi possível gerar o PDF do extrato.", e);
        }
    }

    private Paragraph linhaTracejada(Font fonte) {
        Paragraph linha = new Paragraph("- - - - - - - - - - - - - - - -", fonte);
        linha.setAlignment(Element.ALIGN_CENTER);
        linha.setSpacingAfter(4f);
        return linha;
    }

    private String formatarValor(BigDecimal valor) {
        return String.format(LOCALE_BR, "%,.2f", valor);
    }
}
