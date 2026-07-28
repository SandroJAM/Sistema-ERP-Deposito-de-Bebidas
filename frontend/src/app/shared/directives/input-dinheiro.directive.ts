import { Directive, ElementRef, EventEmitter, HostListener, Input, OnChanges, Output, SimpleChanges } from '@angular/core';

/**
 * Máscara de valor em reais no padrão brasileiro: o usuário digita só números
 * e as casas decimais vão sendo ajustadas da direita pra esquerda — como em
 * apps de banco (ex.: digitar "150000" vira "1.500,00").
 *
 * Uso:
 *   <input type="text" inputmode="decimal" [inputDinheiro]="valor()" (inputDinheiroChange)="valor.set($event)" />
 */
@Directive({
  selector: '[inputDinheiro]',
  standalone: true,
})
export class InputDinheiroDirective implements OnChanges {
  @Input('inputDinheiro') valor: number | null = null;
  @Output('inputDinheiroChange') valorMudou = new EventEmitter<number>();

  private formatador = new Intl.NumberFormat('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  private focado = false;

  constructor(private elementRef: ElementRef<HTMLInputElement>) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['valor'] && !this.focado) {
      this.atualizarTela(this.valor ?? 0);
    }
  }

  @HostListener('focus')
  aoFocar(): void {
    this.focado = true;
    // Seleciona tudo para que o primeiro dígito digitado já substitua o valor atual.
    this.elementRef.nativeElement.select();
  }

  @HostListener('blur')
  aoDesfocar(): void {
    this.focado = false;
    this.atualizarTela(this.valor ?? 0);
  }

  @HostListener('input', ['$event'])
  aoDigitar(event: Event): void {
    const input = event.target as HTMLInputElement;
    const apenasDigitos = input.value.replace(/\D/g, '');
    const centavos = apenasDigitos ? parseInt(apenasDigitos, 10) : 0;
    const novoValor = centavos / 100;

    input.value = this.formatador.format(novoValor);
    this.valorMudou.emit(novoValor);
  }

  private atualizarTela(valor: number): void {
    this.elementRef.nativeElement.value = this.formatador.format(valor);
  }
}
