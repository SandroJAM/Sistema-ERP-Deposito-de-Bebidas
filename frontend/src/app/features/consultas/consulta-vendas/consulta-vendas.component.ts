import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VendaService } from '../../../core/services/venda.service';
import { ClienteDevedor, ExtratoCliente, StatusPagamentoVenda, Venda } from '../../../core/models/venda.model';
import { ModalComponent } from '../../../shared/ui/modal/modal.component';

@Component({
  selector: 'app-consulta-vendas',
  standalone: true,
  imports: [CommonModule, FormsModule, ModalComponent],
  templateUrl: './consulta-vendas.component.html',
})
export class ConsultaVendasComponent implements OnInit {
  private vendaService = inject(VendaService);

  // ----- Resumo por cliente (um registro = total devido) -----
  devedores = signal<ClienteDevedor[]>([]);
  carregandoDevedores = signal(true);
  buscaCliente = signal('');

  devedoresFiltrados = computed(() => {
    const busca = this.buscaCliente().trim().toLowerCase();
    if (!busca) {
      return this.devedores();
    }
    return this.devedores().filter((d) => d.clienteNome.toLowerCase().includes(busca));
  });

  // ----- Vendas sem cliente identificado (balcão) -----
  todasVendas = signal<Venda[]>([]);
  carregandoVendas = signal(true);

  statusBalcaoSelecionado = signal<'ABERTA' | 'FINALIZADA' | 'CANCELADA' | null>(null);
  balcaoDataDe = signal<string | null>(null);
  balcaoDataAte = signal<string | null>(null);

  vendasBalcao = computed(() => {
    const status = this.statusBalcaoSelecionado();
    const de = this.balcaoDataDe();
    const ate = this.balcaoDataAte();

    return this.todasVendas()
      .filter((v) => v.clienteId == null)
      .filter((v) => !status || v.status === status)
      .filter((v) => !de || v.dataVenda.slice(0, 10) >= de)
      .filter((v) => !ate || v.dataVenda.slice(0, 10) <= ate)
      .sort((a, b) => b.id - a.id);
  });

  // ----- Modal de histórico (a "bobina") -----
  clienteSelecionado = signal<ClienteDevedor | null>(null);
  extrato = signal<ExtratoCliente | null>(null);
  carregandoExtrato = signal(false);
  somenteDevido = signal(true);
  periodoInicio = signal<string | null>(null);
  periodoFim = signal<string | null>(null);
  statusPagamentoSelecionado = signal<StatusPagamentoVenda | null>(null);

  vendasPendentesDoCliente = computed(() => {
    const cliente = this.clienteSelecionado();
    if (!cliente) {
      return [];
    }
    return this.todasVendas().filter((v) => v.clienteId === cliente.clienteId && v.valorDevido > 0);
  });

  // ----- Form de recebimento (baixa) -----
  vendaRecebimentoId = signal<number | null>(null);
  valorRecebimento = signal<number | null>(null);
  observacaoRecebimento = signal('');
  registrandoRecebimento = signal(false);

  // ----- PDF / WhatsApp -----
  baixandoPdf = signal(false);

  ngOnInit(): void {
    this.carregarDevedores();
    this.carregarVendas();
  }

  carregarDevedores(): void {
    this.carregandoDevedores.set(true);
    this.vendaService.listarDevedores().subscribe({
      next: (devedores) => {
        this.devedores.set(devedores);
        this.carregandoDevedores.set(false);
      },
      error: () => this.carregandoDevedores.set(false),
    });
  }

  carregarVendas(): void {
    this.carregandoVendas.set(true);
    this.vendaService.listar().subscribe({
      next: (vendas) => {
        this.todasVendas.set(vendas);
        this.carregandoVendas.set(false);
      },
      error: () => this.carregandoVendas.set(false),
    });
  }

  cancelarVenda(venda: Venda): void {
    const confirmou = confirm(`Cancelar a venda #${venda.id}? O estoque dos itens será devolvido.`);
    if (!confirmou) {
      return;
    }

    this.vendaService.cancelar(venda.id).subscribe({
      next: () => this.carregarVendas(),
      error: () => alert('Não foi possível cancelar essa venda.'),
    });
  }

  limparFiltrosBalcao(): void {
    this.statusBalcaoSelecionado.set(null);
    this.balcaoDataDe.set(null);
    this.balcaoDataAte.set(null);
  }

  // ----- Histórico / bobina -----

  abrirHistorico(devedor: ClienteDevedor): void {
    this.clienteSelecionado.set(devedor);
    this.somenteDevido.set(true);
    this.periodoInicio.set(null);
    this.periodoFim.set(null);
    this.statusPagamentoSelecionado.set(null);
    this.limparFormRecebimento();
    this.carregarExtrato();
  }

  fecharHistorico(): void {
    this.clienteSelecionado.set(null);
    this.extrato.set(null);
  }

  verTudoQueDeve(): void {
    this.somenteDevido.set(true);
    this.periodoInicio.set(null);
    this.periodoFim.set(null);
    this.carregarExtrato();
  }

  aoMudarPeriodo(): void {
    this.somenteDevido.set(false);
    this.carregarExtrato();
  }

  aoMudarStatusPagamento(): void {
    this.carregarExtrato();
  }

  carregarExtrato(): void {
    const cliente = this.clienteSelecionado();
    if (!cliente) {
      return;
    }

    this.carregandoExtrato.set(true);
    this.vendaService
      .buscarExtratoCliente(cliente.clienteId, {
        inicio: this.periodoInicio(),
        fim: this.periodoFim(),
        somenteDevido: this.somenteDevido(),
        statusPagamento: this.statusPagamentoSelecionado(),
      })
      .subscribe({
        next: (extrato) => {
          this.extrato.set(extrato);
          this.carregandoExtrato.set(false);
        },
        error: () => this.carregandoExtrato.set(false),
      });
  }

  // ----- Recebimento (baixa) -----

  limparFormRecebimento(): void {
    this.vendaRecebimentoId.set(null);
    this.valorRecebimento.set(null);
    this.observacaoRecebimento.set('');
  }

  registrarRecebimento(): void {
    const vendaId = this.vendaRecebimentoId();
    const valor = this.valorRecebimento();
    if (!vendaId || !valor || valor <= 0) {
      return;
    }

    this.registrandoRecebimento.set(true);
    this.vendaService
      .registrarRecebimento(vendaId, { valor, observacao: this.observacaoRecebimento() || null })
      .subscribe({
        next: () => {
          this.registrandoRecebimento.set(false);
          this.limparFormRecebimento();
          this.carregarExtrato();
          this.carregarVendas();
          this.carregarDevedores();
        },
        error: (err) => {
          this.registrandoRecebimento.set(false);
          alert(err?.error?.message ?? 'Não foi possível registrar o recebimento.');
        },
      });
  }

  // ----- PDF / WhatsApp -----

  baixarPdf(): void {
    const cliente = this.clienteSelecionado();
    if (!cliente) {
      return;
    }

    this.baixandoPdf.set(true);
    this.vendaService
      .baixarExtratoPdf(cliente.clienteId, {
        inicio: this.periodoInicio(),
        fim: this.periodoFim(),
        somenteDevido: this.somenteDevido(),
        statusPagamento: this.statusPagamentoSelecionado(),
      })
      .subscribe({
        next: (blob) => {
          this.baixandoPdf.set(false);
          this.salvarArquivo(blob, cliente.clienteNome);
        },
        error: () => {
          this.baixandoPdf.set(false);
          alert('Não foi possível gerar o PDF do extrato.');
        },
      });
  }

  /**
   * Baixa o PDF e, em seguida, abre o WhatsApp do cliente com uma mensagem pronta.
   * O WhatsApp não permite anexar arquivos automaticamente por segurança — o PDF baixado
   * precisa ser anexado manualmente na conversa que abrir.
   */
  enviarPorWhatsapp(): void {
    const cliente = this.clienteSelecionado();
    if (!cliente) {
      return;
    }

    const telefone = this.formatarTelefoneWhatsapp(cliente.clienteTelefone);
    if (!telefone) {
      alert('Este cliente não tem telefone cadastrado.');
      return;
    }

    this.baixarPdf();

    const totalDevido = this.extrato()?.totalDevidoGeral ?? cliente.totalDevido;
    const mensagem =
      `Olá, ${cliente.clienteNome}! Segue o extrato da sua conta. ` +
      `Saldo devedor atual: R$ ${totalDevido.toFixed(2).replace('.', ',')}. ` +
      `O PDF acabou de ser baixado aqui — é só anexar nessa conversa.`;

    window.open(`https://wa.me/${telefone}?text=${encodeURIComponent(mensagem)}`, '_blank');
  }

  private salvarArquivo(blob: Blob, nomeCliente: string): void {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `extrato-${nomeCliente.toLowerCase().replace(/\s+/g, '-')}.pdf`;
    link.click();
    window.URL.revokeObjectURL(url);
  }

  /** Normaliza o telefone para o formato exigido pelo link wa.me (só dígitos, com DDI). */
  private formatarTelefoneWhatsapp(telefone: string | null): string | null {
    if (!telefone) {
      return null;
    }
    const digitos = telefone.replace(/\D/g, '');
    if (!digitos) {
      return null;
    }
    // Sem DDI (até 11 dígitos: DDD + número) — assume Brasil (55).
    return digitos.length <= 11 ? `55${digitos}` : digitos;
  }
}
